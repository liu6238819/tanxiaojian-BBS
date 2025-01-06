<template>
	<view>
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<!-- 				<q-field label="板块类目" :required="true" :disabled="true" type="select" :isClick="true" @click="jumpClass"
			:placeholder="cateName"></q-field>
			只支持在指定类目下建立自定义板块，其他为官方设置 -->
		<q-field label="话题类目" :required="true" :disabled="true" :placeholder="cateName"></q-field>
		<q-field label="话题名称" v-model="form.name" :required="true" placeholder="请填写板块名称"></q-field>
		<q-field label="话题介绍" @longpress="onPaste()" type="textarea" v-model="form.introduction" :required="true"
			placeholder="请填写话题介绍">
		</q-field>
		<view class="upload-wrap">
			<u-form-item :required="true" label="话题头像" label-position="top">
				<u-upload ref="headUpload" name="Image" maxCount="1" @on-remove="removeHeadImg"
					@on-choose-complete="upLoadHeadImg" :auto-upload="false"></u-upload>
			</u-form-item>
			<u-form-item :required="true" label="话题背景" label-position="top">
				<u-upload ref="backUpload" name="Image" maxCount="1" @on-remove="removeBackImg"
					@on-choose-complete="upLoadBackImg" :auto-upload="false"></u-upload>
			</u-form-item>
		</view>
		<!-- 提交按钮 -->
		<q-button shape="circle" @click="submit">提交</q-button>
	</view>
</template>

<script>
	import {
		createPlate
	} from '@/api/index.js'
	import {
		mapState,
		mapActions
	} from 'vuex'
	import localData from '@/utils/data.js'
	export default {
		data() {
			return {
				cateList: [],
				cateName: '自建专区',
				form: {
					cateId: '3',
					schoolId: 1,
					name: '',
					introduction: '',
					headImage: '',
					backImgUrl: '',
					plateId: 'onCreate'
				},
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高
			};
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
			...mapState('user', ['currentSchoolId'])
		},
		onLoad() {},
		methods: {
			async onPaste() {
				let txt = await this.copyAndPaste(1, this, '')
				console.log(txt)
				this.form.introduction = txt
			},
			jumpClass() {
				uni.navigateTo({
					url: 'shoose-class'
				})
			},
			removeHeadImg() {
				this.form.portraitUrl = '';
			},
			upLoadHeadImg(lists) {
				this.form.portraitUrl = lists[0].url;
				console.log(this.form.portraitUrl);
			},
			removeBackImg() {
				this.form.backImgUrl = '';
			},
			upLoadBackImg(lists) {
				console.log(lists);
				this.form.backImgUrl = lists[0].url;
				console.log(this.form.backImgUrl);
			},
			async submit() {
				this.form.userId = this.userInfos.userId;
				let permissionCheckResult = await this.permissionCheck(this.form.userId, this.form.schoolId, this.form
					.plateId)
				if (permissionCheckResult != 0) {
					return
				}

				this.form.schoolId = this.currentSchoolId
				if (!this.form.cateId) {
					uni.showToast({
						title: '请选择话题类目'
					});
					return;
				}
				if (!this.form.name) {
					uni.showToast({
						title: '请填写话题名称'
					});
					return;
				}
				if (!this.form.introduction) {
					uni.showToast({
						title: '请填写话题介绍'
					});
					return;
				}
				if (!this.form.portraitUrl) {
					uni.showToast({
						title: '请上传头像'
					});
					return;
				}
				if (!this.form.backImgUrl) {
					uni.showToast({
						title: '请上传背景图片'
					});
					return;
				}

				uni.showLoading({
					mask: true,
					title: '正在审核中'
				});

				//#ifdef MP-WEIXIN

				let checkStr = this.form.name + this.form.introduction;
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, checkStr, 1);
				const portraitCompress = await localData.compressionIamge(this, this.form.portraitUrl);
				console.log(portraitCompress)
				const backImgCompress = await localData.compressionIamge(this, this.form.backImgUrl);
				console.log(backImgCompress)

				const portraitUrlOSS = await this.uploadImageOSS(portraitCompress, 'plate/portraitImg/', this
					.currentSchoolId)
				this.form.portraitUrl = portraitUrlOSS
				let portraitUrlCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey.openId,
					portraitUrlOSS, 2, 1);
				const backImgUrlOSS = await this.uploadImageOSS(backImgCompress, 'plate/backImg/', this
					.currentSchoolId)
				this.form.backImgUrl = backImgUrlOSS
				let backImgUrlCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey.openId,
					backImgUrlOSS, 2, 1);

				uni.showToast({
					mask: true,
					title: '审核结束'
				});
				console.log(textCheck);
				if (textCheck != 0) {
					//审核未通过的处理方法
					this.form.title = ''
					this.form.contentText = ''
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
				}
				if (portraitUrlCheck === 0 && backImgUrlCheck === 0) {
					this.form.plateId = null //创建时重置主键字段,否则无法新建记录！
					const data = await createPlate(this.form);
					if (data.code === 200) {
						uni.showToast({
							title: '创建话题成功',
							icon: "none"
						});
						uni.navigateTo({
							url: '/package_task/pages/bbs/topic/detail?id=' + data.data
						})
					} else {
						this.form.plateId = 'onCreate'
						this.removeHeadImg()
						this.removeBackImg()
						this.$refs.headUpload.clear();
						this.$refs.backUpload.clear();
						uni.showToast({
							title: data.message,
							icon: "none"
						});

						return
					}
				} else {
					uni.showToast({
						title: '您的内容存在敏感信息或接口请求异常！',
						icon: "none"
					});
					return
					console.log("板块审核中")
					const data = await createPlate(this.form);
					this.form.introduction = 'sensitiveINFO'
					if (data.code === 200) {
						uni.showToast({
							title: '话题信息需要审核',
							icon: "none"
						});
						uni.navigateTo({
							url: '/package_task/pages/bbs/topic/detail?id=' + data.data
						})
					}
				}

				//#endif

			}

		}
	};
</script>

<style>
	page {
		background-color: #fff;
	}

	.upload-wrap {
		padding: 30rpx;
	}
</style>
