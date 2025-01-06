<template>
	<view class="page">
		<view class="margin border-radius">
			<view v-for="item in mySignUpinfo">
				<view v-if="item.kind==1" class="cu-form-group">
					<!-- v-if="line.kind=0"> -->
					<view class="title">{{item.title}}</view>
					<input placeholder="请输入内容" name="input" v-model="item.value"></input>
				</view>
				<view v-if="item.kind==2" class="cu-form-group">
					<!-- v-if="line.kind==1" -->
					<view class="title">{{item.title}}</view>
					<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
						<view class="picker">
							{{item.index>-1?item.value:'请选择'}}
						</view>
					</picker>
				</view>
				<view v-if="item.kind==3" class="cu-form-group">
					<!-- v-if="line.kind=0"> -->
					<view class="cuIcon-title text-red"></view>
					<view class="title">{{item.title}}</view>
					<input placeholder="请输入内容(必填)" name="input" v-model="item.value"></input>
				</view>
				<view v-if="item.kind==4" class="cu-form-group">
					<!-- v-if="line.kind==1" -->
					<view class="cuIcon-title text-red"></view>
					<view class="title">{{item.title}}</view>
					<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
						<view class="picker">
							{{item.index>-1?item.value:'请选择（必填）'}}
						</view>
					</picker>
				</view>
			</view>
			<view class="cu-form-group">
				<!-- 可见范围 -->
				<view class="choose-item" v-if="nowScore>=200&&deposit>1">
					<view class="action">
						<text class="title">使用200积分立减1元</text>
					</view>
					<radio-group @change="radioChange">
						<label class="margin-left" v-for="(item, index) in selectScore" :key="item.value">
							<radio style="transform:scale(0.8)" class="blue radio" :value="item.value"
								:checked="item.checked"></radio>
							<text class="margin-left-sm ">{{item.name}}</text>
						</label>
					</radio-group>
				</view>
				<!-- 				<view v-if="nowScore<200">
					<text class="title">当前积分不足200</text>
				</view> -->
			</view>
			<view class="cu-tabbar-height"></view>
			<view style="position: fixed;width: 90%;bottom: 10rpx;left: 5%;" class="flex">
				<view class="money_box">
					支付金额：
					<text v-if="deposit!=0" class="text-white money text-xl">￥{{realVaule}}</text>
					<text v-if="deposit==0" class="text-white money text-xl">无</text>
				</view>
				<button v-if="isExistSignUp==0" class="cu-btn btn_bg" @click="signUpSumbit">确认提交</button>
				<button v-if="isExistSignUp==1" class="cu-btn btn_bg" @click="signUpSumbit">确认修改</button>
			</view>

		</view>

	</view>

</template>

<script>
	import {
		mapState,
	} from 'vuex'
	import localData from '../../../utils/data.js'
	import {
		getSignUpInfo,
		addSignUpInfo,
		getUserScore,
		redeemScore,
		changeUserScore
	} from "../../../api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
		},
		data() {
			return {
				index: -1,
				infoForm: {
					activityId: '',
					userId: '',
				},
				deposit: 0,
				mySignUpinfo: [],
				isExistSignUp: 0, //是否填写过报名信息
				sumbitInfo: { //提交的报名信息
					activityId: '',
					infoJson: {
						data: [],
					},
					userId: '',
				},
				payinfo: {
					paySignUpId: '', //支付订单ID
					payBody: '支付报名订金', //支付订单类型，
					attach: '', //商家数据表
					payFee: 0,
				},
				//临时活动
				selectScore: [ //是否选择用积分兑换一块钱
					{
						name: '是',
						value: "1",
						checked: 'flase'
					},
					{
						name: '否',
						value: "0",
						checked: 'true'

					}
				],
				currentItem: 1,
				Coupon: 0,
				nowScore: 0,
				realVaule: 0,
			}
		},
		onLoad(option) {
			this.infoForm.activityId = option.id
			this.sumbitInfo.activityId = option.id
			this.deposit = option.deposit
			this.realVaule = option.deposit
			this.isExistSignUp = option.isExist
			console.log(option)
			this.getMySignUpInfo()
			this.getNowscore()
		},
		methods: {
			PickerChange(e, item) {
				item.index = e.detail.value
				item.value = item.options[item.index]
			},
			//获取填写的报名信息
			async getMySignUpInfo() {
				this.infoForm.userId = this.userInfos.userId;
				const data = await getSignUpInfo(this.infoForm);
				if (data.code == 200 && data.data ) {
					console.log('中间', data)
					let temp = []
					const stepData = data.data.info.data.sort(function(item1, item2) {
						return item1.order - item2.order
					})
					temp.push(...stepData) //过渡数据
					this.mySignUpinfo = temp.map(item => {
						if (!item.index) {
							item.index = -1;
						}
						return item
					})
					// console.log(this.mySignUpinfo)
				}
				else{
					this.infoForm.userId = 'offical'
					const officalData = await getSignUpInfo(this.infoForm);
					if(officalData.code==200){
						console.log('官方', officalData)
						let temp = []
						const stepData = officalData.data.info.data.sort(function(item1, item2) {
							return item1.order - item2.order
						})
						temp.push(...stepData) //过渡数据
						this.mySignUpinfo = temp.map(item => {
							if (!item.index) {
								item.index = -1;
							}
							return item
						})
					}
				}
			},
			//报名确认modal框
			showConfirmModal() {
				let arr = this.mySignUpinfo
				for (var i = 0; i < arr.length; i++) {
					if (arr[i].kind == 3 || arr[i].kind == 4) {
						if (arr[i].value == '') {
							uni.showToast({
								title: '存在未填项',
								icon: "none",
								duration: 3000,
							})
							return
						}
					}
				}
				let showcontent = '无订金活动可以重复提交报名信息，点击‘提交’报名信息'
				if (this.deposit != 0) {
					showcontent = '含订金活动无法重复提交报名信息，请确认报名信息后，再点击‘提交’报名信息'
				}
				let that = this
				uni.showModal({
					title: '报名提示',
					confirmText: '提交',
					content: showcontent,
					success: function(res) {
						if (res.confirm) {
							// console.log('用户点击确定');
							that.signUpSumbit()
						} else if (res.cancel) {
							// console.log('用户点击取消');
						}
					}
				});
			},
			//报名提交
			async signUpSumbit() {
				let arr = this.mySignUpinfo
				for (var i = 0; i < arr.length; i++) {
					if (arr[i].kind == 3 || arr[i].kind == 4) {
						if (arr[i].value == '') {
							uni.showToast({
								title: '存在未填项',
								icon: "none",
								duration: 3000,
							})
							return
						}
					}
				}
				this.sumbitInfo.userId = this.userInfos.userId
				this.sumbitInfo.infoJson.data = this.mySignUpinfo
				// console.log(this.sumbitInfo)
				let res = await addSignUpInfo(this.sumbitInfo)
				if (res.code == 200) {
					// console.log(res)
					if (this.deposit != 0) {
						if (this.isExistSignUp == 0) {
							this.payinfo.payFee = this.realVaule
							this.payinfo.paySignUpId = 'signUp_' + new Date().getTime() + '_' + Math.floor(Math
							.random() * 100)
							this.payinfo.attach = 'signUp_' + res.data.id
							this.payWX(this.payinfo)
						} else {
							uni.showToast({
								title: '提交成功',
								icon: "none"
							})
							//完成后的跳转逻辑
							setTimeout(function() {
								uni.reLaunch({
									url: "../../../pages/index/index",
								})
							}, 1000)
						}
					} else {
						if (this.isExistSignUp == 1) {
							uni.showToast({
								title: '提交成功',
								icon: "none"
							})
						} else {
							uni.showToast({
								title: res.message,
								icon: "none"
							})
						}
						//完成后的跳转逻辑
						setTimeout(function() {
							uni.reLaunch({
								url: "../../../pages/index/index",
							})
						}, 1000)
					}
				} else {
					uni.showToast({
						title: res.message,
						icon: "none"
					})

				}


			},
			//增加一条报名信息
			async addOneSignUp() {
				this.sumbitInfo.userId = this.userInfos.userId
				this.sumbitInfo.infoJson.data = this.mySignUpinfo
				// console.log(this.sumbitInfo)
				let res = await addSignUpInfo(this.sumbitInfo)
				if (res.code == 200) {
					uni.showToast({
						title: res.data,
						icon: "none"
					})
				} else {
					uni.showToast({
						title: res.message,
						icon: "none"
					})

				}
			},
			//微信支付示例
			async payWX(payInfo) {
				// #ifdef MP-WEIXIN
				let that = this;
				wx.showLoading({
					title: '正在支付',
				})
				//支付完成后的积分扣除逻辑
				let applyForm = {
					userId: that.userInfos.userId,
					changeNum: 200
				}
				if (that.Coupon == 1) {
					let result = await redeemScore(applyForm)
					if (result.code == 200) {
						console.log(result)
					} else {
						uni.showToast({
							title: result.message,
							icon: "none"
						})
						return
					}
				}
				// wx.cloud.init({
				// 	env: localData.envId,
				// 	traceUser: true,
				// })
				// wx.cloud.callFunction({
				await localData.cloud_shared.init()
				await localData.cloud_shared.callFunction({
					name: 'paySignUp', //云函数的名称SignUp
					data: { //替换为实际订单参数
						body: payInfo.payBody,
						outTradeNo: payInfo.paySignUpId,
						// outTradeNo: 'dsafgdsdf'+Math.floor(Math.random()*1000+1), //用记录号来做订单号，因为记录号也是唯一的。
						totalFee: parseInt(parseFloat(payInfo.payFee) * 100), //单位为分，100为1元
						userId: this.userInfos.userId,
						envId: localData.envId,
						attach: payInfo.attach

					},
					success: res => {
						console.log('支付返回结果', res)
						// this.addOneSignUp()
						const payment = res.result.payment
						//console.log(payment)
						wx.hideLoading();
						// console.log(res.result.resultCode)
						if (res.result.resultCode == 'FAIL') {
							console.log('支付失败')

							uni.showToast({
								icon: 'none',
								title: '支付失败，请刷新页面重试'
							})
							return
						}
						wx.requestPayment({
							...payment, //...这三点是 ES6的展开运算符，进行解构赋值
							success(res) {
								//这里success回调函数只有用户点击了“完成”或者返回键才会被触发
								//所以不要在这里写改变订单为已支付的业务逻辑
								//万一用户支付完成，但不点击"完成"或者返回键，那会造成数据不一致性的问题
								console.log('支付成功', res)
								wx.showToast({
									title: '支付成功',
									icon: 'success',
									duration: 2000
								})
								//点击完成后的跳转逻辑
								setTimeout(function() {
									uni.reLaunch({
										url: "../../../pages/index/index",
									})
								}, 1000)
							},
							fail(err) {
								console.error('支付失败', err)
								// 返还积分
								let changeForm = {
									userId: that.userInfos.userId,
									changeNum: 200,
									source: "payFailed",
									changeType: 6,
								}
								if (that.Coupon == 1) {
									let tempRes = changeUserScore(changeForm)
								}
							},
						})
					},
					fail() {
						//为了节省数据库的空间，支付失败的订单可以删除
						//db.collection('publish').doc(id).remove()
					},
				})
				// #endif
			},
			getHelp() {
				uni.showModal({
					content: '报名已提交，含订金类活动无法重复提交报名信息，若需更改信息请联系客服qq：2095678031'
				})
			},
			//选择是否用积分兑换优惠劵
			radioChange: function(e) {
				for (let i = 0; i < this.selectScore.length; i++) {
					if (this.selectScore[i].value === e.detail.value) {
						this.currentItem = i;

						break;
					}
				}
				if (this.currentItem == 0 && this.nowScore < 200) {
					this.currentItem = 1
					this.selectScore[0].checked = 'false'
					this.selectScore[1].checked = 'true'
					uni.showToast({
						title: '积分不足200，无法兑换优惠劵',
						icon: "none"
					});

					this.Coupon = (this.selectScore[this.currentItem].value * 1.0).toFixed(2)
					this.realVaule = (this.deposit - this.Coupon).toFixed(2)
				}
				this.Coupon = (this.selectScore[this.currentItem].value * 1.0).toFixed(2)
				this.realVaule = (this.deposit - this.Coupon).toFixed(2)
				// console.log(this.Coupon)
				// console.log(this.deposit)
				// console.log(this.realVaule)
			},
			//获取当前积分
			async getNowscore() {
				let getInfo = {
					userId: this.userInfos.userId,
				}
				const data = await getUserScore(getInfo);
				if (data.code == 200) {
					console.log(data)
					this.nowScore = data.data.score
				}
			},
		}
	}
</script>

<style lang="scss" scoped>
	.page {
		background-color: #f0f0f0;
		height: 100vh;
		overflow: auto;
	}

	.border-radius {
		border-radius: 20rpx;
	}

	.money_box {
		background-color: #232122;
		border-top-left-radius: 50rpx;
		border-bottom-left-radius: 50rpx;
		/* border-radius: 40rpx; */
		width: 65%;
		height: 90rpx;
		line-height: 90rpx;
		color: white;
		padding-left: 30rpx;
	}

	.money {
		font-size: 40rpx;
	}

	.btn_bg {
		background-color: #FF4C4D !important;
		color: white;
		height: 90rpx;
		line-height: 90rpx;
		width: 35%;
		border-top-right-radius: 50rpx !important;
		border-bottom-right-radius: 50rpx !important;
		border-radius: 0;
		font-size: 30rpx;
		/* padding: 20rpx 120rpx !important; */
	}

	.choose-item {
		display: flex;
		align-items: center;
		// padding: 20rpx;
		border-bottom: 1px solid #F5F5F5;
		justify-content: space-between;

		&:last-child {
			border: 0;
		}

		// .txt {
		// 	margin-left: 20rpx;
		// }

		.icon {
			width: 40rpx;
			height: 40rpx;
		}

		.u-icon {
			margin-left: auto;
			color: #999;
		}

		.add-icon {
			margin-left: 0;
		}
	}
</style>
