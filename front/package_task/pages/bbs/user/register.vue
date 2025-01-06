<template>
	<view class="container">
		<u-form :model="form" ref="uForm">
			<u-form-item>
				<u-input v-model="form.phone" placeholder="请输入手机号" />
			</u-form-item>
			<u-form-item>
				<u-input v-model="form.password" placeholder="请输入密码" />
			</u-form-item>
			<u-form-item>
				<u-input v-model="passWordConfirm" placeholder="密码确认" />
			</u-form-item>
			<u-form-item>
				<u-input v-model="form.nickName" placeholder="请输入昵称" />
			</u-form-item>
			<u-form-item>
				<u-input v-model="form.introduction" placeholder="一句话介绍自己" />
			</u-form-item>
			<u-form-item>
				<u-input v-model="form.school" placeholder="请输入学校名称" />
			</u-form-item>
<!-- 			<u-form-item>
				<u-input v-model="form.stuNum" placeholder="请输入学号" />
			</u-form-item> -->
			<!-- <u-form-item>
				<view class="sex" @change="radioChange">
					<view>性别</view>
					<radio-group @change="radioChange">
						<label class="">
							<radio style="transform:scale(0.6);" :value="0" /> 男
						</label>
						<label>
							<radio style="transform:scale(0.6);" :value="1" />女
						</label>
					</radio-group>
				</view>
			</u-form-item>
			<u-form-item>
				<u-input v-model="passWordConfirm" placeholder="设置个性化签名" />
			</u-form-item> -->
			<!-- 上传头像图片 -->
			<view class="avatar-text">
				<view>请上传头像</view>
				<view>
					{{imgList.length}}/1
				</view>
			</view>
			<view class="cu-form-group avatar">
				<view class="grid col-4 grid-square flex-sub">
					<view class="bg-img" v-for="(item,index) in imgList" :key="index" @tap="ViewImage"
						:data-url="imgList[index]">
						<image :src="imgList[index]" mode="aspectFill"></image>
						<view class="cu-tag bg-red" @tap.stop="DelImg" :data-index="index">
							<text class='cuIcon-close'></text>
						</view>
					</view>
					<view class="solids" @tap="ChooseImage" v-if="imgList.length<1">
						<text class='cuIcon-cameraadd'></text>
					</view>
				</view>
			</view>
		</u-form>
		<view>
			<u-button type="success" shape="circle" @click="submitForm">注册</u-button>
		</view>
	</view>
</template>

<script>
	import {
		userRegister
	} from '@/api/index.js'
	import {
		mapState,
		mapActions
	} from 'vuex'

	export default {
		data() {
			return {
				imgList: [],
				passWordConfirm: "",
				form: {
					phone: "",
					password: "",
					nickName: "",
					introduction: "",
					school: "",
					stuNum: "",
					headimgUrl: ""
				}
			}
		},
		computed: {
			...mapState('user', ['userKey'])
		},
		methods: {
			async submitForm() {
				console.log(this.userKey)
				let imgUrl = 'https://campus-bbs.oss-cn-beijing.aliyuncs.com/user/headImg/164242131276681.png'
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, '哈哈哈', 2)
				console.log(this.imgList[0])
				let mediaCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey.openId, this.imgList[0],
					2, 2)
				console.log(textCheck)
				console.log(mediaCheck)
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
				if (mediaCheck != 0) {
					uni.showToast({
						title: '您的内容存在敏感信息，请检查！',
						icon: "none"
					});
					return
				}


				const {
					phone,
					password,
					nickName,
					school,
					stuNum
				} = this.form
				if (!phone || !password || !nickName || !school || !stuNum) {
					uni.showToast({
						title: '存在未填写信息',
						icon: "none"
					});
					return
				} else if (password !== this.passWordConfirm) {
					uni.showToast({
						title: '两次输入密码不一致',
						icon: "none"
					});
					return
				} else {
					// 上传头像到阿里云oss
					// this.form.headimgUrl = await this.uploadImageOSS(this.imgList[0], 'userCertificate/');
					this.form.headimgUrl = await this.uploadImageOSS(this.imgList[0], 'user/headImg/',this.currentSchoolId);
					// console.log('imgurl ***',this.form.headimgUrl);
					// console.log('imgurl ***',this.imgList); "http://tmp/teoNfo0YAE8k75df7a9c709d71e4e03747b7af1f5a10.jpg"

					if (!this.form.headimgUrl) {
						uni.showToast({
							title: '上传图片失败，请重新上传',
							icon: "none"
						});
					}
					// 注册用户信息
					const data = await userRegister(this.form)
					if (data.code === 200) {
						uni.showToast({
							title: '注册成功',
							icon: "none"
						});
						setTimeout(function() {
							uni.reLaunch({
								url: './login',
							})
						}, 1000)
					} else {
						uni.showToast({
							title: '注册失败，请重新注册',
							icon: "none"
						});
					}
				}
			},
			radioChange(event) {
				this.form.sex = event.detail.value
			},
			ChooseImage() {
				uni.chooseImage({
					count: 1, //默认9
					sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album'], //从相册选择
					success: (res) => {
						// console.log(res)
						if (this.imgList.length != 0) {
							this.imgList = this.imgList.concat(res.tempFilePaths)
						} else {
							this.imgList = res.tempFilePaths
						}

						// setTimeout(()=>{
						// 	console.log("前端的url地址：",this.imgList[0].path)
						// },1000)
					}
				});
			},
			ViewImage(e) {
				uni.previewImage({
					urls: this.imgList,
					current: e.currentTarget.dataset.url
				});
			},
			DelImg(e) {
				uni.showModal({
					title: '删除图片',
					content: '确定删除该图片？',
					cancelText: '取消',
					confirmText: '确定',
					success: res => {
						if (res.confirm) {
							this.imgList.splice(e.currentTarget.dataset.index, 1)
						}
					}
				})
			},


		},
		mounted() {
			console.log(this.userKey)
		}
	}
</script>

<style lang="scss" scoped>
	.sex {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
	}

	.avatar {
		padding: 0;
	}

	.avatar-text {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}

	.container {
		padding: 20rpx 50rpx;
	}

	.marin-top-100 {
		margin-top: 100rpx;
	}

	.head {
		font-size: 40rpx;
		font-weight: 600;
		margin-bottom: 50rpx;
	}
</style>
