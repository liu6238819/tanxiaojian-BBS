<template>
	<view class="cu-modal show">
		<view class="cu-dialog ">
			<view class="cu-bar justify-end" style="color: white; background-color: #028fab;">
				<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;">
					校友认证
				</view>
				<view class="action" @tap="hideModal">
					<text class="cuIcon-close text-white text-bold"></text>
				</view>
			</view>
			<view class="modal-bottom ">
				<view style="background-color: rgba(175, 175, 175, 0.3);margin-bottom: 20rpx; text-align: left; font-size: 24rpx; color: #4a4a4a;
				 margin-top: 10rpx; width: 100%;" class="">为了营造安全、纯粹的校友社区氛围，使用本功能需要先进行校友身份认证</view>
				<view style="margin-bottom: 10rpx; text-align: left; font-size: 24rpx; color: #000000;
				   width: 100%;" class="">请选择认证方式：</view>
				<view class="card" v-if="alumniOnly.answer_examine_state==1">
					<view style="display: flex; justify-content: space-between; align-items: center;">
						<view style="font-size: 30rpx; font-weight: bold;">答题认证</view>
						<!-- <button open-type="getPhoneNumber" @getphonenumber="exazmine" class="cu-btn examine-button ">前往
						</button> -->
						<button @click="toAnswerExamnie" class="cu-btn examine-button ">前往
						</button>
					</view>
					<view style="font-size: 24rpx;">回答3条与本校有关问题，答对即可验证成功</view>
				</view>
				<view class="card">
					<view style="display: flex; justify-content: space-between; align-items: center;">
						<view style="font-size: 30rpx; font-weight: bold;">人工认证</view>
						<button class="cu-btn  examine-button" @click="toManualExazmine()">前往
						</button>
					</view>
					<view style="font-size: 24rpx;">上传本校学生身份证明照片，如学生证、校园卡等</view>
				</view>
			</view>
			<view style="text-align: center display: block; padding-top:10r; padding-bottom: 30rpx;  background: #fff;">
				<view>
					<button @click="hideModal" class="cu-btn round bg-blue text-bold "
						style=" width: 30%; background-color: #028fab;color: #ffffff; height: 45rpx;">以后再说</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		reqEditUserInfos,
		getUserStateBySchool
	} from "../../api/index.js"
	import localData from '../../utils/data.js';
	import WXBizDataCrypt from '@/utils/WXBizDataCrypt.js'
	import rsa from '@/utils/rsa.js';
	export default {
		computed: {
			...mapState('user', ['userInfos', 'userKey', 'currentSchoolId','currentSchoolState']),
			...mapState('config', ['alumniOnly'])
		},
		name: 'examine-pupop',
		data() {
			return {
				showPopup: 0,
			}
		},
		methods: {
			...mapMutations('user', {
				setCurrentUserState: 'setCurrentUserState',
			}),
			hideModal() {
				this.$emit("returnHid", 0) //传递的值
				this.showPopup = 0
			},
			async toManualExazmine() {
				this.$emit("returnHid", 0) //传递的值
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/user-examine'
				});

			},
			async toAnswerExamnie() {
				let useCount = await this.isHaveChance();
				if (useCount >= 3) {
					uni.showToast({
						title: '答题认证机会已经用完',
						icon: "none"
					});
					return
				}
				this.$emit("returnHid", 0) //传递的值
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/answer-examine'
				});

			},
			async isHaveChance() {
				let that = this
				await localData.cloud_shared.init()
				return new Promise((resolve, reject) => {
					// 	wx.cloud.init({
					// 		env: localData.envId,
					// 		traceUser: true,
					// 	})
					// 	wx.cloud.callFunction({
					localData.cloud_shared.callFunction({
						name: 'answerExamineSQL', //云函数的名称
						data: {
							functionName: 'isHaveChance', //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							userId: this.userInfos.userId,
							usingDatabase: localData.usingDatabase,

						},
						async complete(res) {
							let nowdata = res.result
							console.log("使用了几次机会", res.result)
							resolve(nowdata)
						}
					})
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
						// 1.3 将手机号写入后台，更新本地缓存和vuex
						const editUserInfos = await reqEditUserInfos({
							userId,
							phone: phoneNumber
						})
						if (editUserInfos.code === 200) {

							// 手机号写入后台成功,提交申请
							this.toAnswerExamnie()
						}
					}
				}
			},

			//非BBS权限审核方法
			async permissionCheckNew(userId, schoolId, schoolFlag) {
				let that = this
				return new Promise((resolve, reject) => {
					uni.request({
						url: localData.baseUrl + '/commonAPIs/permissionCheckOutBBS',
						data: {
							"userId": userId,
							"schoolId": schoolId,
							"schoolFlag": schoolFlag
						},
						method: 'POST',
						header: {
							'content-type': 'application/x-www-form-urlencoded'
						},
						success: async res => {
							console.log('权限审核结果：')
							console.log(res.data)
							let authorizeResult = null
							if (res.data.code == 0) {
								resolve(res.data.code)
								return
							} else {
								uni.showToast({
									icon: 'none',
									title: res.data.message
								})
								if (res.data.code == 100 || res.data.code ==
									200) { //先授权昵称，再跳转至认证页面
									uni.hideToast()
									authorizeResult = await that.userInfoInit();
									that.showPopup = 1
								}
								if (res.data.code == 300 || res.data.code == 500 || res.data
									.code ==
									600) { //仅进行toast提示
									resolve(res.data.code);
									return
								}
								if (res.data.code == 700) { //只进行授权
									//authorizeResult = await Vue.prototype.userAuthorize()
									authorizeResult = await that.userInfoInit();
									if (authorizeResult == 'success') {
										resolve(0)
									} else {
										resolve(res.data.code);
									}
									return
								}
							}

							resolve(res.data.code);

						},
						fail(res) {
							console.log('权限审核接口请求失败！')
							reject(400);
						}
					})
				})
			},
			async toFisrtPage() {
				uni.switchTab({
					url: '/pages/index/index'
				});
				// if (this.currentUserState == 2) {
				// 	localData.requestMessage([7, 8])
				// }
			},
		}
	}
</script>

<style lang="scss" scoped>
	.modal-bottom {
		width: 100%;
		display: flex;
		flex-direction: column;
		// margin-left: 15rpx;
		padding-bottom: 20rpx;
		padding-left: 40rpx;
		padding-right: 40rpx;
		background-color: #FFFFFF;
	}

	.modal-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
		// margin-left: 10rpx;
	}

	.modal-content-right {
		margin-right: 5rpx;
	}

	.card {
		background-image: linear-gradient(90deg, #bbe4ff, #e7ffff);
		padding-left: 20rpx;
		padding-right: 20rpx;
		padding-top: 10rpx;
		padding-bottom: 5rpx;
		margin-bottom: 30rpx;
		border-radius: 10rpx;
		text-align: left;
	}

	.examine-button {
		//margin-bottom: 20rpx;
		font-size: 26rpx;
		font-weight: bold;
		height: 40rpx;
		background-color: #028fab;
		color: #ffffff;
	}
</style>