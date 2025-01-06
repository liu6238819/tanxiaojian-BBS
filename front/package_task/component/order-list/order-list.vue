<template>
	<view class="card-list">
		<view v-for="order in orderlist">
			<view @click="jump(order)">
				<view class="cu-list menu margin card-shadow border-radius "
					style="background-color: #ffffff; padding-bottom:20rpx; padding-top:20rpx;">
					<view class="post-item ">
						<view class="post-item-top-user">

							<view style="display: flex; ">
								<u-avatar class="avatar" :src="order.publisherHeadImgUrl" level-bg-color="#8072f3">
								</u-avatar> <!-- 头像需要改成发布人的 -->

								<view class="center">
									<view style="display: flex;align-items: center;">
										<!-- <text class="official">官方</text> -->
										<text class="username">{{order.publisherNickName}}</text>
									</view>
									<view>
										<text class="time">{{Date.parse(order.createTime)| timeFrom }}</text>
									</view>
								</view>
							</view>

							<view class="card-right">
								<view style="display: flex;align-items: center; margin-top: 5rpx; ">
									<text class="username text-bold"
										style="color: #979797">{{order.showOrderName}}</text>
								</view>
								<text v-if="order.orderState==-1" class="orderstaus-1 ">未支付</text>
								<view v-if="order.orderState==0" class="orderstaus0 ">待接取</view>
								<text v-if="order.orderState==1" class="orderstaus1">进行中</text>
								<text v-if="order.orderState==2" class="orderstaus1">接取方确认</text>
								<text v-if="order.orderState==3" class="orderstaus">接取方确认超时</text>
								<text v-if="order.orderState==4" class="orderstaus4">已完成</text>
								<text v-if="order.orderState==5" class="orderstaus">已撤销</text>
								<text v-if="order.orderState==6" class="orderstaus">申诉中</text>
								<text v-if="order.orderState==7" class="orderstaus">申诉完成</text>
								<text v-if="order.orderState==8" class="orderstaus">已下线</text>
								<text v-if="order.orderState==10" class="orderstaus4">提现中</text>
								<text v-if="order.orderState==11" class="orderstaus4">已提现</text>

							</view>


						</view>
					</view>
					<view style="display: flex; background-color: #ffffff;">
						<view style="width: 70%;">
							<view class="cu-form-group-1 white">
								<view class="content" style=" margin-left: 10rpx;">
									<!-- <text class="text-grey">需求内容：</text> -->
									<text style="width: 100%;font-size: 32rpx;">{{order.orderContent}}</text>
								</view>
							</view>

							<block v-if="order.orderKind<10">
								<view style="background-color: #ffffff; padding-left: 20rpx; margin-top: 10rpx;">
									<view class="content" style="width: 100%;" v-if="order.applicantMajor != ''">
										<text class="cuIcon-title text-green "></text>
										<text class="text-sm">专业要求：{{order.applicantMajor}}</text>
									</view>
									<view class="content" style="width: 100%;"
										v-if="order.applicantQualification != ''">
										<text class="cuIcon-title text-green"></text>
										<text class="text-sm">学历要求：{{order.applicantQualification}}</text>
									</view>
									<view class="content" v-if="order.orderRemark != ''">
										<text class="cuIcon-title text-green "></text>
										<text class="text-sm">备注：{{order.orderRemark}}</text>
									</view>
								</view>
							</block>


							<block v-if="order.orderKind>9 &&order.orderKind<20 &&order.orderKind!=11">
								<view class="text-second-info">
									<view style="display: flex; margin-bottom: 10rpx; "
										v-if="order.orderStartPlace != ''">
										<image class="icons" src="../../static/img/quicon.png"></image>
										<text class="text-sm">在哪里取物：{{order.orderStartPlace}}</text>
									</view>
									<view style="display: flex;" v-if="order.orderEndPlace != ''">
										<image class="icons" src="../../static/img/send.png"></image>
										<!-- <text class="cuIcon-title text-green"></text> -->
										<text class="text-sm">送到哪里去：{{order.orderEndPlace}}</text>
									</view>
								</view>
							</block>


							<block v-if="order.orderKind==11">
								<view class="text-second-info">
									<view style="display: flex; margin-bottom: 10rpx;"
										v-if="order.orderStartPlace != ''">
										<image class="icons" src="../../static/img/jiicon.png"></image>
										<text class="text-sm">要寄到哪里：{{order.orderStartPlace}}</text>
									</view>
									<view style="display: flex;" v-if="order.orderEndPlace != ''">
										<image class="icons" src="../../static/img/quicon.png"></image>
										<!-- <text class="cuIcon-title text-green"></text> -->
										<text class="text-sm">在哪里取件：{{order.orderEndPlace}}</text>
									</view>
								</view>
							</block>

							<block v-if="order.orderKind>19 &&order.orderKind<30 ">
								<view style="background-color: #ffffff; padding-left: 20rpx;">
									<view class="content" style="width: 100%;" v-if="order.applicantMajor != ''">
										<text class="cuIcon-title text-green "></text>
										<text class="text-sm">租赁时长：{{order.applicantMajor}}</text>
									</view>
									<view class="content" v-if="order.orderRemark != ''">
										<text class="cuIcon-title text-green "></text>
										<text class="text-sm">备注：{{order.orderRemark}}</text>
									</view>
								</view>
							</block>

							<view style="background-color: #ffffff; padding-left: 28rpx;"
								v-if="order.orderRemark != '' && order.orderKind >9 && order.orderKind <20">
								<view class="content">
									<text class="cuIcon-title text-green "></text>
									<text class="text-sm-rem">备注：{{order.orderRemark}}</text>
								</view>
							</view>
						</view>
						<view
							style="display: flex; justify-content:center; width: 25%; align-items: center; margin-left: 10rpx;">
							<text class="text-price text-red text-bold"
								style=" font-size: 40rpx;">{{order.orderCost}}</text>
						</view>
					</view>
					<view class="cu-form-group-2">
						<!-- v-if="order.orderState==0 || order.orderState==2&& place=='mypublish' || 
					userInfos.userId!=order.publisherId &&order.orderState==1&& place=='myapplied'"> -->

						<view style="display: flex;">
							<view v-if="order.userKind==0 &&order.orderState==0 && place=='mypublish'">
								<button class="cu-btn bg-grey round shadow-blur "
									@click.native.stop="cancelOrder(order)">撤销订单</button>
							</view>
							<view v-if="order.userKind==3 &&order.orderState==0 && place!='myapplied'
								&& this.userInfos.userId != order.publisherId">
								<button class="cu-btn bg-red round shadow-blur"
									@click.native.stop="applyAction(order)">接取订单</button>
							</view>
							<view v-if="order.userKind==1 &&order.orderState==1&& place=='myapplied'
							&& order.applicantState==1">
								<button class="cu-btn bg-red round shadow-blur"
									@click.native.stop="confirmOrder(order)">确认完成</button>
							</view>
							<view v-if="order.userKind==2 &&order.orderState==0">
								<button class="cu-btn bg-orange round shadow-blur" @click.native.stop="">正在沟通</button>
							</view>
							<view v-if="order.userKind==2 &&order.orderState==1&& place=='myapplied'">
								<button class="cu-btn bg-grey round shadow-blur" @click.native.stop="">未被选择</button>
							</view>
							<view v-if="order.userKind==0 &&order.orderState==2&& place=='mypublish'">
								<button class="cu-btn bg-red round shadow-blur "
									@click.native.stop="finishOrder(order)">确认完成</button>
							</view>
						</view>
						<view class="text-bold" style=" display: flex;" v-if="order.orderState == 0">
							<text class="text-red">截止剩余：</text>
							<u-count-down color="#e54d42" :show-border=false separator="zh" :timestamp="order.countdown"
								separator-color="#e54d42"></u-count-down>
						</view>
						<view class="text-bold" style=" display: flex;" v-if="order.orderState == 2">
							<text style="color:#f37b1d ;">自动确认剩余：</text>
							<u-count-down color="#f37b1d" :show-border=false separator="zh"
								:timestamp="order.confirmCountdown" separator-color="#f37b1d"></u-count-down>
						</view>
						<view class="text-bold" style=" display: flex;"
							v-if="order.orderState != 0 &&order.orderState != 2">
							<text style="color:#9c9c9c ;">截止剩余：</text>
							<u-count-down color="#9c9c9c" :show-border=false separator="zh" :timestamp="consttime"
								separator-color="#9c9c9c"></u-count-down>
						</view>
					</view>


				</view>
			</view>
		</view>


		<view class="cu-modal show" v-if="modalName=='DialogModal'">
			<view class="cu-dialog modal-content">
				<view class="cu-bar bg-white lg justify-end">
					<view class="content">请完善申诉原因 </view>
					<view class="action" @tap="hideModal()">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="informText margin-top">
					<textarea maxlength="-1" v-model="informInfo" placeholder="请输入申诉原因"></textarea>
				</view>
				<view class="submit-btn">
					<q-button @click="appealOrder()">提交</q-button>
				</view>
			</view>

		</view>
		<view class="cu-modal show" v-if="modalName=='applyModal'">
			<view class="cu-dialog modal-content">
				<view class="cu-bar bg-white lg justify-end">
					<view class="content">接取提示 </view>
					<view class="action" @tap="hideModal()">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<!-- <view class="submit-btn">
					<q-button @click="appealOrder()">确认接取</q-button>
				</view> -->
				<view class="modal-bottom">
					<view class="text-df">
						<text class="cuIcon-title text-orange "></text>
						在接取订单前，请认真阅读《接取须知》，接取订单后默认同意《接取须知》中内容。
					</view>
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="hideModal">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="applyOrder(currentOrder)">确认接取</button>
					</view>
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
	import localData from '../../../utils/data.js'
	import moment from 'moment'
	import {
		// cancelOrder,
		appealOrder,
		// appealOrder1,
	} from '../../../api/index.js'
	import orderFuns from '../../component/order-functions.js'
	export default {
		name: 'orderList',
		props: {
			orders: Array,
			place: String,
		},
		data() {
			return {
				orderInfo: {
					userName: '',
					userHeadImg: '',
					orderType: '',
				},
				actionList: [{
					text: '申诉',
					color: 'red',
					key: 'inform'
				}],
				currentOrderId: '', //记录当前操作的订单id
				currentOrder: [],
				showAction: false,
				informInfo: '',
				modalName: '', //是否展示举报模态框
				plateId: {
					inschool: '',
					unschool: '',
				}, //用户权限审核使用的plateId
				countdown: '3600', //倒计时
				confirmCountdown: "3600", //确认倒计时
				consttime: 0,
				isapplied: 0, //对于寻人订单，是否已经接取
			};
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
			orderlist() {
				const newOrders = this.orders.map(item => {
					return item

				})

				return newOrders
			}
		},
		methods: {
			jump(order) {
				let url = ''
				url = '../order/order-detail?id=' + order.orderId
				uni.navigateTo({
					url
				});
			},
			//弹出模态框
			appealAction(order) {
				if (order.orderState == 6) {
					uni.showToast({
						title: '订单申诉中',
						icon: "none"
					});
					return;
				}
				console.log(this.modalName)
				this.currentOrderId = order.orderId
				this.modalName = 'DialogModal'
				console.log('后', this.modalName)
			},
			async appealOrder() {
				let appealForm = {
					publisherId: this.userInfos.userId,
					orderId: this.currentOrderId,
					appealReason: this.informInfo,
				}
				let that = this
				console.log('传入', appealForm);
				await orderFuns.appeal(appealForm, that, 1) //1代表这个属于组件调用
				console.log('传出', that.modalName)
				setTimeout(function() {
					console.log('传出', that)
				}, 1000);
				// this.modalName = ''
			},
			confirmOrder(order) {
				let that = this
				console.log(order)
				orderFuns.confirm(order, that, 1) //1代表这个属于组件调用
			},
			finishOrder(order) {
				let that = this
				orderFuns.finish(order, that, 1) //1代表这个属于组件调用
			},
			cancelOrder(order) {
				let that = this
				// this.cancelOrder.publisherId = this.userInfos.userId;
				// this.cancelOrder.orderId = order.orderId;
				orderFuns.cancel(order, that, 1) //1代表这个属于组件调用
			},
			applyAction(order) {
				console.log(this.modalName)
				this.currentOrder = order
				this.modalName = 'applyModal'
				console.log('后', this.modalName)
			},
			applyOrder(order) {
				let that = this
				orderFuns.apply(order, this.plateId.inschool, this.plateId.unschool, that, 1) //1代表这个属于组件调用
				this.modalName = ''
			},

			hideModal() {
				this.modalName = ''
			},
			showOrderKindName(orderkind) {
				let name = localData.getOrderKindName(orderKind)
				return name
			}
		},

		async mounted() {
			// #ifdef MP-WEIXIN
			if (this.userInfos.userId == null || this.userInfos.userId == '') {
				return
			}
			// #endif

			// let catelist = [1, 4]
			// let param = {
			// 	cateId: '',
			// 	schoolId: this.currentSchoolId
			// }
			// // param.cateId = catelist[0]
			// // getPlateLists(param).then(res => {
			// // 		console.log("请求res内容", res)
			// // 		this.plateId.inschool = res[0].plateId;
			// // 		// this.plateId.unschool=res[0].plateId;
			// // 	})
			// // 	.catch(err => {
			// // 		console.log("请求err内容", err)
			// // 	})
			// // param.cateId = catelist[1]
			// // getPlateLists(param).then(res => {
			// // 		console.log("请求res内容", res)
			// // 		this.plateId.unschool = res[0].plateId;
			// // 	})
			// // 	.catch(err => {
			// // 		console.log("请求err内容", err)
			// // 	})


		}
	};
</script>

<style lang="scss">
	.card-list {
		margin-top: 20rpx;
		margin-bottom: 30rpx;
	}

	.post-item {
		background: #ffffff;
		width: 100%;
		// background: #c7bdbd;

		padding-left: 30rpx;
		// margin-bottom: 5rpx;
	}

	.post-item-top-user {
		display: flex;
		align-items: center;
		width: 100%;
		justify-content: space-between;

		.avatar {
			width: 85rpx;
			height: 85rpx;
			border-radius: 50%;
			margin-right: 20rpx;
		}


		.orderstaus {
			display: inline-block;
			font-size: 27rpx;
			color: #ffffff;
			background-color: rgba(0, 0, 0, 0.6);

			border-style: solid;
			border-width: 1px;
			//border-color: #7f7f7f;
			padding: 5rpx 10rpx;
			border-radius: 10rpx;
			// margin-right: 10rpx;
		}

		.orderstaus-1 {
			display: inline-block;
			font-size: 27rpx;
			color: #e54d42;
			background-color: #fadbd9;
			// color: #ffffff;
			// background-color: #e54d42;

			border-style: solid;
			border-width: 1px;
			border-color: #ffffff;
			padding: 5rpx 10rpx;
			border-radius: 10rpx;
			// margin-right: 10rpx;
		}

		.orderstaus0 {
			display: inline-block;
			font-size: 27rpx;
			color: #f37b1d;
			background-color: #fde6d2;

			// color: #ffffff;
			// background-color: #fbbd08;

			border-style: solid;
			border-width: 1px;
			border-color: #ffffff;
			padding: 5rpx 10rpx;
			border-radius: 10rpx;
			// margin-right: 10rpx;
		}

		.orderstaus1 {
			display: inline-block;
			font-size: 27rpx;
			color: #0081ff;
			background-color: #cce6ff;
			// color: #ffffff;
			// background-color: #1cbbb4;

			border-style: solid;
			border-width: 1px;
			border-color: #ffffff;
			padding: 5rpx 10rpx;
			border-radius: 10rpx;
			// margin-right: 10rpx;
		}

		.orderstaus4 {
			display: inline-block;
			font-size: 27rpx;
			color: #39b54a;
			background-color: #d7f0dbff;
			// color: #ffffff;
			// background-color: #8dc63f;

			border-style: solid;
			border-width: 1px;
			border-color: #ffffff;
			padding: 5rpx 10rpx;
			border-radius: 10rpx;
			// margin-right: 10rpx;
		}

		.center {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: 24rpx;
			color: #999;

			.username {
				font-size: 32rpx;
				font-weight: 600;
				color: #616161;
			}

		}

		.right {
			height: 85rpx;

			.arrow-down {
				color: #999;
			}
		}
	}

	/* ==================
         模态窗口
 ==================== */

	.cu-modal {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		z-index: 1110;
		opacity: 0;
		outline: 0;
		text-align: center;
		-ms-transform: scale(1.185);
		transform: scale(1.185);
		backface-visibility: hidden;
		perspective: 2000upx;
		background: rgba(0, 0, 0, 0.6);
		transition: all 0.3s ease-in-out 0s;
		pointer-events: none;
	}

	.cu-modal::before {
		content: "\200B";
		display: inline-block;
		height: 100%;
		vertical-align: middle;
	}

	.cu-modal.show {
		opacity: 1;
		transition-duration: 0.3s;
		-ms-transform: scale(1);
		transform: scale(1);
		overflow-x: hidden;
		overflow-y: auto;
		pointer-events: auto;
	}

	.cu-dialog {
		position: relative;
		display: inline-block;
		vertical-align: middle;
		margin-left: auto;
		margin-right: auto;
		width: 680upx;
		max-width: 100%;
		background-color: #f8f8f8;
		border-radius: 10upx;
		overflow: hidden;
	}

	.cu-modal.bottom-modal::before {
		vertical-align: bottom;
	}

	.cu-modal.bottom-modal .cu-dialog {
		width: 100%;
		border-radius: 0;
	}

	.cu-modal.bottom-modal {
		margin-bottom: -1000upx;
	}

	.cu-modal.bottom-modal.show {
		margin-bottom: 0;
	}

	.cu-modal.drawer-modal {
		transform: scale(1);
		display: flex;
	}

	.cu-modal.drawer-modal .cu-dialog {
		height: 100%;
		min-width: 200upx;
		border-radius: 0;
		margin: initial;
		transition-duration: 0.3s;
	}

	.cu-modal.drawer-modal.justify-start .cu-dialog {
		transform: translateX(-100%);
	}

	.cu-modal.drawer-modal.justify-end .cu-dialog {
		transform: translateX(100%);
	}

	.cu-modal.drawer-modal.show .cu-dialog {
		transform: translateX(0%);
	}

	.cu-modal .cu-dialog>.cu-bar:first-child .action {
		min-width: 100rpx;
		margin-right: 0;
		min-height: 100rpx;
	}

	.border-radius {
		// border-top-left-radius: 20rpx;
		// border-top-right-radius: 20rpx;
		border-radius: 20rpx;
	}

	.border-top-radius {
		border-top-left-radius: 20rpx;
		border-top-right-radius: 20rpx;
		// border-radius: 20rpx;
	}


	.border-bottom-radius {
		border-bottom-left-radius: 20rpx;
		border-bottom-right-radius: 20rpx;
	}

	.bottom-group {
		display: flex;
		margin-right: 35rpx;
	}

	.icons {
		height: 1.3em !important;
		width: 1.3em !important;
		margin-right: 15rpx;
	}

	.text-sm-rem {
		font-size: 24upx;
		margin-left: 20rpx;
	}

	.text-second-info {
		margin-top: 10rpx;
		background-color: #ffffff;
		padding-left: 25rpx;
		display: block;
	}

	.card-shadow {
		box-shadow: 0px 2px 8px 0px rgba(68, 68, 68, 0.15);
	}

	.card-right {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		width: 33%;
	}

	.right-tag {
		margin-bottom: 5rpx;
		margin-top: 10rpx;

	}

	.right-tag-text {
		height: 70rpx;
		width: 150rpx;
		font-size: 30rpx;
	}

	.cu-form-group-1 {
		background-color: #ffffff;
		padding-right: 20rpx;
		padding-left: 30rpx;
		margin-top: 5rpx;
		display: flex;
		align-items: center;
		// min-height: 100upx;
		justify-content: space-between;
	}

	.cu-form-group-2 {
		background-color: #ffffff;
		padding: 1upx 30upx;
		display: flex;
		align-items: center;
		// min-height: 100upx;
		flex-direction: row-reverse;
		justify-content: space-between;
	}
</style>
