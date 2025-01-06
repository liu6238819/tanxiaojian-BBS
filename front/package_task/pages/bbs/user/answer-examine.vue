<template>
	<view>
		<view style="display: flex; padding: 10rpx; flex-direction: column;">
			<view class="question" v-for="(question,index) in questionsList" :key="question._id">
				<view class="action" style="font-size: 32rpx;">
					<text class="text-bold">{{index+1}} . {{question.title}}</text>
				</view>
				<radio-group @change="radioChange" :id="index">
					<label class="" v-for="(option, index) in question.options" :key="option.key"
						style="display: flex; align-items: center; height: 55rpx;">
						<radio style="transform:scale(0.7)" class="blue radio" :value="option.key"></radio>
						<text class="" style="font-size: 28rpx;">{{option.content}}</text>
					</label>
				</radio-group>
			</view>
		</view>

		<view style="text-align: center;">
			<button v-if="userInfos.phone" class="cu-btn text-bold text-white" @click="onSubmit()"
				style="width: 30%; background-color:rgba(255, 0, 0, 0.9);">
				提 交
			</button>
			<button v-if="!userInfos.phone" class="cu-btn text-bold text-white" open-type="getPhoneNumber" @getphonenumber="exazmine"
				style="width: 30%; background-color:rgba(255, 0, 0, 0.9);">
				提 交
			</button>
		</view>
		<view class="cu-modal show" v-if="showResultPopup==1">
			<view class="cu-dialog ">
				<view class="cu-bar justify-end" v-if="correctNum==3" style="color: white; background-color: #028fab;">
					<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;">
						认证成功
					</view>
					<!-- 	<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-white text-bold"></text>
					</view> -->
				</view>
				<view class="cu-bar justify-end" v-if="correctNum<3" style="color: white; background-color: #028fab;">
					<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;">
						认证失败
					</view>
					<!-- <view class="action" @tap="hideModal">
						<text class="cuIcon-close text-white text-bold"></text>
					</view> -->
				</view>
				<view style="">
					<view style="margin-top: 30rpx;" v-if="correctNum>=3">你答对了{{correctNum}}道题，认证成功</view>
					<view style="margin-top: 30rpx;" v-if="correctNum<3">你答对了{{correctNum}}道题，认证失败</view>
					<view v-if="correctNum<3" style="margin-top: 20rpx; margin-bottom: 20rpx; justify-content: center;">
						<!-- 						<view v-if="usedCount<3" class="card"  @click="toAnswerExamnie()">
							<text>再次答题</text>
						</view> -->
						<view v-if="correctNum<3&&usedCount<3">
							<button @click="toAnswerExamnie()" class="cu-btn round bg-blue text-bold "
								style=" width: 30%; background-color: #00aa00;color: #ffffff; height: 45rpx;">再次答题</button>
						</view>
						<view v-if="usedCount>=3" style="color: red;">
							<text>答题机会已用完，请尝试人工认证</text>
						</view>
					</view>

				</view>
				<view style="text-align: center display: block; padding-top:10r; padding-bottom: 30rpx;">
					<view>
						<button @click="toFisrtPage()" class="cu-btn round bg-blue text-bold "
							style=" width: 30%; background-color: #028fab;color: #ffffff; height: 45rpx;">返回首页</button>
					</view>
				</view>

			</view>
		</view>
	</view>

</template>

<script>
	import localData from '@/utils/data.js';
	import {
		mapState,
	} from 'vuex'
	import {
		answerIdentification,
		reqEditUserInfos,
		haveIdentifiedInOtherSchool
	} from "@/api/index.js"
	import WXBizDataCrypt from '@/utils/WXBizDataCrypt.js'
	import rsa from '@/utils/rsa.js';
	export default {
		computed: {
			...mapState('user', ['userInfos','userKey',]),
			...mapState('user', ['currentSchoolId']),
			...mapState('user', ['scoresFront', 'currentUserState']),

		},
		data() {
			return {
				questionsList: [],
				standardAnswer: [],
				userAnswser: ["-1", "-1", "-1"],
				showResultPopup: 0,
				correctNum: 0,
				usedCount: 0,
			}
		},
		async onLoad() {
			await this.getQuestions()
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
			async getQuestions() {
				let that = this
				await localData.cloud_shared.init()
				return new Promise((resolve, reject) => {
					// wx.cloud.init({
					// 	env: localData.envId,
					// 	traceUser: true,
					// })
					// wx.cloud.callFunction({
					localData.cloud_shared.callFunction({
						name: 'answerExamineSQL', //云函数的名称
						data: {
							functionName: 'getQuestions', //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							usingDatabase: localData.usingDatabase,

						},
						async complete(res) {
							let nowdata = res.result
							console.log("问题列表", res.result)
							that.questionsList = res.result.questionList;
							that.standardAnswer = res.result.answerList;
							resolve(nowdata)
						}
					})
				})
			},
			radioChange: function(e) {
				console.log(e.currentTarget.id, e.detail.value)
				this.userAnswser[e.currentTarget.id] = e.detail.value
				console.log(this.userAnswser)
			},
			async onSubmit() {
				let code = this.userAnswser.indexOf("-1")
				this.correctNum = 0
				if (code != -1) {
					uni.showToast({
						title: '还有问题没有回答',
						icon: "none"
					});
					return
				}
				uni.showLoading({
					title: '认证中'
				})
				for (let i = 0; i < this.standardAnswer.length; i++) {
					if (this.standardAnswer[i] == this.userAnswser[i]) {
						this.correctNum = this.correctNum + 1
					} else {
						continue
					}
				}
				if (this.correctNum == 3) {
					//认证成功
					let form = {
						schoolId: this.currentSchoolId,
						userId: this.userInfos.userId,
					}
					const data = await answerIdentification(form)
					console.log(data)
					//判断用户是否存在其他学校的认证记录
					let queryForm = {
						userId: this.userInfos.userId,
						schoolId: this.currentSchoolId
					}
					const haveOtherRecord = await haveIdentifiedInOtherSchool(queryForm)
					uni.hideLoading()
					//用户存在其他学校的认证记录
					if (haveOtherRecord.code==200 && haveOtherRecord.data==1) {
						console.log("haveOtherRecord",haveOtherRecord)
						uni.showModal({
							content: haveOtherRecord.message,
							confirmText:'联系客服',
							showCancel:true,
							cancelText:'返回首页',
							success: async res => {
								if (res.confirm) {
									uni.redirectTo({
										url: '/package_task/pages/group-code/index?codeKind=' + 2
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
						return
					}
				}
				uni.hideLoading()
				this.usedCount = await this.useAnswerChance()
				this.showResultPopup = 1
				console.log("提交")
			},
			async useAnswerChance() {
				let that = this
				await localData.cloud_shared.init()
				return new Promise((resolve, reject) => {
					localData.cloud_shared.callFunction({
						name: 'answerExamineSQL', //云函数的名称
						data: {
							functionName: 'useAnswerChance', //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							userId: this.userInfos.userId,
							usingDatabase: localData.usingDatabase,

						},
						async complete(res) {
							let nowdata = res.result
							console.log("使用一次机会", res.result)
							resolve(nowdata)
						}
					})
				})
			},
			hideModal() {
				this.showPopup = 0
			},
			toManualExazmine() {
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/user-examine'
				});
			},
			async toAnswerExamnie() {
				this.userAnswser = ["-1", "-1", "-1"],
					this.questionsList = [];
				this.standardAnswer = [];
				this.correctNum = 0;
				await this.getQuestions()
				this.showResultPopup = 0
			},
			async toFisrtPage() {
				uni.switchTab({
					url: '/pages/index/index'
				});
				// if (this.currentUserState == 2) {
				// 	localData.requestMessage([7, 8])
				// }
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
			
						// 1.3 将手机号写入后台，更新本地缓存和vuex
						const editUserInfos = await reqEditUserInfos({
							userId,
							phone: phoneNumber
						})
						if (editUserInfos.code === 200) {
							// 手机号写入后台成功,提交申请
							console.log("成功")
							this.onSubmit()
						}
					}
				}
			},
		} //methods
	}
</script>

<style lang="scss" scoped>
	.question {
		display: flex;
		flex-direction: column;
		width: 100%;
		margin-bottom: 45rpx;
		padding: 0 25rpx;
		//border: #7d7d7d 2rpx solid;
	}

	.card {
		width: auto;
		// height : auto
		font-weight: bold;
		margin-left: 200rpx;
		margin-right: 200rpx;
		// background-color: #00aaff;
		color: #fff;
		background-image: linear-gradient(90deg, #2998ff, #67c5ff);
		padding-left: 20rpx;
		padding-right: 20rpx;
		border-radius: 10rpx;
	}
</style>
