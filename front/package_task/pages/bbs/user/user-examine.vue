<template>
	<view class="container">
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<u-form :model="form" ref="uForm">
			<u-form-item>
				<view class="item-school">
					<view>当前认证院校：</view>
					<view>{{schoolName}}</view>
				</view>
			</u-form-item>
			<u-form-item>
				<view class="item-sex">
					<view>性别：</view>
					<picker @change="PickerChange" :value="sexIndex" :range="sexPicker">
						<view class="picker">
							{{sexIndex>-1?sexPicker[sexIndex]:'请选择'}}
						</view>
					</picker>
				</view>
			</u-form-item>
			<!-- 			<u-form-item>
				<u-input placeholder="请输入证件号" v-model="form.stuNum" />
			</u-form-item> -->
			<u-form-item>
				<u-input placeholder="请输入邮箱(选填)" v-model="form.email" />
			</u-form-item>
			<u-form-item>
				<u-input placeholder="请输入邀请码(选填,长按以粘贴内容)" v-model="form.inviteCode" @longpress="onCopy" />
			</u-form-item>

			<!-- 上传头像图片 -->
			<view class="avatar-text">
				<view>请上传校友身份证明</view>
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
			<u-button v-if="!userInfos.phone" open-type="getPhoneNumber" @getphonenumber="exazmine" type="success"
				shape="circle" @click="sendMessage">提交</u-button>
			<u-button v-if="userInfos.phone" type="success" shape="circle" @click="onSubmit">提交</u-button>
			<br></br>
			<view style="padding-top: 10rpx; font-size: 24rpx;color: #757575;font-weight: bold;">隐私说明</view>
			<view style="font-size: 24rpx;color: #757575;text-indent:2em">
				您好！作为一款针对中国高校用户的论坛社区类产品，根据2017年网信办《互联网论坛社区服务管理规定》的要求，我们必须要求所有用户后台实名，即授权我们获取用户手机号来保证用户提供真实身份信息。您的相关信息仅用于后台实名认证，并受到相关法律法规的保护。除法律法规的要求外，我们不会泄露给任何第三方、发布营销广告或有其他使用用途。
			</view>
			<view style="font-size: 24rpx;color: #757575;text-indent:2em">
				作为一款半私密性社区，所有用户需要统一的身份认同。根据 2004
				年教育部、共青团中央《关于进一步加强高等学校校园网络管理工作的意见》的指示，和各大高校管理的要求，所有用户使用本产品必须提交证明其校园学生/教职工等身份的证明。您的相关信息仅用作身份证明，并受到相关法律法规的保护。除法律法规的要求外，我们不会数字化采集其中任何敏感信息、泄露给任何第三方或有其他使用用途。
			</view>
			<view style="font-size: 24rpx;color: #757575;text-indent:2em">点击提交，即表示您同意并接受上述隐私说明。</view>

		</view>
	</view>
</template>

<script>
	import WXBizDataCrypt from '@/utils/WXBizDataCrypt.js'
	import store from '@/store/index.js'
	import localData from '@/utils/data.js'
	import {
		userIdentification,
		reqEditUserInfos,
		haveIdentifiedInOtherSchool
	} from '@/api/index.js'
	import {
		mapState
	} from 'vuex'
	import rsa from '@/utils/rsa.js';
	export default {
		data() {
			return {
				sexIndex: 0,
				sexPicker: ['请选择', '男', '女'],
				imgList: [],
				schoolName: '',
				form: {
					userId: null,
					schoolId: null,
					idCardUrl: null,
					email: '',
					stuNum: '',
					inviteCode: '',
					sex: 0,
				},
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
			}
		},
		computed: {
			...mapState('user', ['userInfos', 'userKey', 'currentSchoolId'])
		},
		async mounted() {
			this.schoolName = uni.getStorageSync('currentSchoolName')
			this.form.schoolId = this.currentSchoolId
			this.form.userId = this.userInfos.userId
			// #ifdef MP-WEIXIN
			//重新获取登录信息，以防止过期
			let loginResult = await this.loginWX()
			if (loginResult != 'success') {
				uni.showToast({
					icon: 'none',
					title: '信息获取异常，请反馈客服'
				})
				return
			}
			// #endif

		},
		methods: {
			async submitForm() {
				if (this.sexIndex == 0) {
					uni.showToast({
						title: '请选择性别！',
						icon: 'none'
					})
					return
				}
				this.form.sex = this.sexIndex
				if (this.imgList[0]) {
					uni.showLoading({
						title: '图片上传中'
					})
					const {
						userId
					} = uni.getStorageSync('userInfos')
					let tempIdCardUrl = ''
					//原始图片压缩
					const compressResult = await localData.compressionIamge(this, this.imgList[0]);
					//微信官方压缩方法(二次压缩)
					// const compressResult = await localData.compressionIamgeWXTwice(this, this.imgList[0], 1000);
					console.log(compressResult)
					try {
						tempIdCardUrl = await this.uploadImageOSS(compressResult, 'user/idCard/', this
							.currentSchoolId);
						console.log('上传成功，URL:', tempIdCardUrl);
					} catch (error) {
						console.error('上传失败:', error);
						uni.hideLoading()
						uni.showToast({
							icon: 'none',
							title: '图片过大，请重新上传',
						})
						return
					}
					this.form.idCardUrl = JSON.stringify(tempIdCardUrl)
					console.log('params', this.form);
					try {
						const editUserInfos = await userIdentification(this.form)
						if (editUserInfos.code === 200) {
							//判断用户是否存在其他学校的认证记录
							let queryForm = {
								userId: this.userInfos.userId,
								schoolId: this.currentSchoolId
							}
							const haveOtherRecord = await haveIdentifiedInOtherSchool(queryForm)
							uni.hideLoading()
							//用户存在其他学校的认证记录
							if (haveOtherRecord.code == 200 && haveOtherRecord.data == 1) {
								console.log("haveOtherRecord", haveOtherRecord)
								uni.showModal({
									content: haveOtherRecord.message,
									confirmText: '联系客服',
									showCancel: true,
									cancelText: '返回首页',
									success: async res => {
										if (res.confirm) {
											uni.redirectTo({
												url: '/package_task/pages/group-code/index?codeKind=' +
													2
											})
										} else if (res.cancel) {
											setTimeout(() => {
												uni.switchTab({
													url: '/pages/index/index'
												});
											}, 1000)
											// console.log('用户点击取消');
										}
									},
								});
							}
							//用户没有其他学校的认证记录
							else {
								uni.showToast({
									icon: 'none',
									title: '申请认证成功，请耐心等待，若审核时间过长可联系客服',
									duration: 500,
									complete: () => {
										setTimeout(() => {
											uni.switchTab({
												url: '/pages/index/index'
											});
										}, 1000)

									}
								})
							}
						} else if (editUserInfos.code === 201) {
							uni.hideLoading()
							uni.showToast({
								title: editUserInfos.message,
								icon: 'none'
							})
						} else {
							uni.hideLoading()
							uni.showToast({
								title: '提交失败，服务器请求异常',
								icon: 'none'
							})
						}
					} catch (error) {
						console.error('上传失败:', error);
						uni.hideLoading()
						uni.showToast({
							icon: 'none',
							title: '提交失败，服务器请求异常，请检查图片大小',
						})
						return
					}

				} else {
					uni.showToast({
						title: '请上传校友身份证明！',
						icon: 'none'
					})
				}
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
			async exazmine(e) {
				const {
					userId
				} = uni.getStorageSync('userInfos')
				let sessionData = this.userKey.sessionData
				console.log("sessionData", sessionData);
				if (sessionData.code === 200) {
					const {
						session_key,
					} = sessionData.data
					console.log("sessionKey", session_key);
					if (e.detail.errMsg == 'getPhoneNumber:fail user deny') {
						// 用户拒绝授权使用手机号
						// uni.showToast({
						// 	title: '拒绝授权将影响使用',
						// 	icon: "none"
						// });
						return
					} else {

						let phone = null
						let pc = new WXBizDataCrypt(localData.appId,
							session_key); //appId 和sessionKey
						phone = pc.decryptData(e.detail
							.encryptedData, e.detail.iv);
						console.log('phone', phone);
						const {
							phoneNumber
						} = phone
						const editUserInfos = await reqEditUserInfos({
							userId,
							phone: phoneNumber
						})
						if (editUserInfos.code === 200) {

							// 手机号写入后台成功,提交申请
							this.submitForm()
						}
					}
				}
			},
			PickerChange(e) {
				this.sexIndex = e.detail.value
			},
			onCopy() {
				let that = this
				console.log('长按粘贴');
				uni.getClipboardData({
					success: function(res) {
						console.log(res.data);
						that.form.inviteCode = res.data
					}
				});
			},
			sendMessage() {
				console.log('上面')
				localData.requestMessage([4, 7])
				console.log('下面')
			},
			onSubmit() {
				localData.requestMessage([4, 7])
				this.submitForm()
			}
		}
	}
</script>

<style lang="scss" scoped>
	.item-school {
		display: flex;
		justify-content: space-between;
		width: 100%;
	}

	.item-sex {
		background-color: #ffffff;
		//padding: 1upx 30upx;
		display: flex;
		align-items: center;
		width: 100%;
		//min-height: 100upx;
		justify-content: space-between;
	}

	.item-sex picker {
		flex: 1;
		padding-right: 40upx;
		overflow: hidden;
		position: relative;
	}

	.item-sex picker .picker {
		line-height: 100upx;
		font-size: 28upx;
		text-overflow: ellipsis;
		white-space: nowrap;
		overflow: hidden;
		width: 100%;
		text-align: right;
	}

	.item-sex picker::after {
		font-family: cuIcon;
		display: block;
		content: "\e6a3";
		position: absolute;
		font-size: 34upx;
		color: #8799a3;
		line-height: 100upx;
		width: 60upx;
		text-align: center;
		top: 0;
		bottom: 0;
		right: -20upx;
		margin: auto;
	}

	.avatar {
		padding: 0;
	}

	.avatar-text {
		font-size: 28rpx;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}
</style>