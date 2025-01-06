<template>
	<view class="cu-modal show" v-if="modalName=='userLogin'">
		<view class="cu-dialog ">
			<view class="cu-bar justify-end" style="color: white; background-color: #00abab;">
				<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;">
					{{currentMesaage}}
					<text v-if="currentkey==3">{{continuousLoginDays}}天!</text>
				</view>
				<view class="action" @tap="hideModal">
					<text class="cuIcon-close text-white text-bold"></text>
				</view>
			</view>
			<view class="modal-bottom ">
				<view class="" style="display: flex; align-items: center;
				 justify-content: space-between; padding-top: 20rpx; margin-bottom: 25rpx;">
					<view>
						今日获得积分（{{actionToday.contentNum*25 + 20 + actionToday.commentNum*15 +
						actionToday.AdmireNum*5 + isShared*15}}/120）
					</view>
					<button class="cu-btn line-blue " style="height: 50rpx;" @click="toExchangeCenter">前往积分兑换</button>
				</view>
				<view class="text-df ">
					<view v-if="userInfos.headimgUrl!=null" class="modal-content">
						<view><text class="lg text-gray cuIcon-squarecheck" style="margin-right: 10rpx;"></text>登录论坛：
							<text class="text-orange text-bold modal-content-right" style="margin-left: 121rpx;">+20
							</text>分
						</view>
						<view class=" modal-content-right">1/1</view>
					</view>
					<view v-if="userInfos.headimgUrl==null" class="modal-content">
						<view><text class="lg text-gray cuIcon-squarecheck" style="margin-right: 10rpx;"></text>登录论坛：
							<text class="text-orange text-bold" style="margin-left: 121rpx;">+20</text>分
						</view>
						<view class="modal-content-right">0/1</view>
					</view>
				</view>
				<view class="text-df modal-content ">
					<view><text class="lg text-gray cuIcon-text" style="margin-right: 10rpx;"></text>发帖：
						<text class="text-orange text-bold modal-content-right" style="margin-left: 173rpx;">+25
						</text>分
					</view>
					<view class=" modal-content-right">{{actionToday.contentNum}}/1</view>
				</view>
				<view class="text-df modal-content">
					<view><text class="lg text-gray cuIcon-community" style="margin-right: 10rpx;"></text>评论：
						<text class="text-orange text-bold modal-content-right" style="margin-left: 173rpx;">+15
						</text>分
					</view>
					<view class=" modal-content-right">{{actionToday.commentNum}}/3</view>
				</view>
				<view class="text-df modal-content">
					<view><text class="lg text-gray cuIcon-like" style="margin-right: 10rpx;"></text> 点赞：
						<text class="text-orange text-bold modal-content-right" style="margin-left: 173rpx;">+5
						</text>分
					</view>
					<view class=" modal-content-right">{{actionToday.AdmireNum}}/3</view>
				</view>
				<view class="text-df modal-content ">
					<view><text class="lg text-gray cuIcon-share" style="margin-right: 10rpx;"></text>分享帖子：
						<text class="text-orange text-bold modal-content-right" style="margin-left: 121rpx;">+15
						</text>分
					</view>
					<view class=" modal-content-right">{{isShared}}/1</view>
				</view>
			</view>
			<view style="text-align: center display: block; padding-top:10r; padding-bottom: 10rpx;  background: #fff;">
				<view>
					<button @click="hideModal" class="cu-btn round bg-blue text-bold "
						style=" width: 30%; background-color: #00abab;color: #ffffff; height: 45rpx;">关闭</button>
				</view>
				<u-checkbox @change="checkboxChange" v-model="noNoticeChecked" label-size="20">3日内不再提醒
				</u-checkbox>
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
		getUserScore,
		getActionNumToday,
	} from "../../api/index.js"
	import localData from '../../utils/data.js';
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
			...mapState('popup', ['showDaliyPopupNum']),
			...mapState('config', ['firstPage', 'userFirstLogin']),
		},
		name: 'daily_login_popup',
		props: {
			pageShowDaliyPopup: Number,
		},
		data() {
			return {
				nowScore: 0,
				actionToday: '',
				isShared: 0,
				modalName: '',
				noNoticeChecked: false,
				actionTodayForm: {
					userId: '',
				},
				welcomeMessage: [],
				currentMesaage: '',
				currentkey: 0,
				continuousLoginDays: 0,
				userFisrtLoginDuration: 100,
				userFisrtLoginScore: 10000,
				userFisrtLoginState: 0
			}
		},
		async mounted() {
			console.log("页面是否弹窗（页面传参）", this.pageShowDaliyPopup)
			//未请求结束或页面不需要弹窗，不挂载
			if (this.firstPage.user_first_login_state == null ||
				this.firstPage.user_first_login_state == '' || this.pageShowDaliyPopup != 1) {
				return
			}
			//请求结束，开始挂载
			this.increaseshowDaliyPopupNum()
			console.log('每日弹窗开始挂载次数', this.showDaliyPopupNum)
			//获取组件是否展示的配置（人为设置）
			this.userFisrtLoginState = this.firstPage.user_first_login_state
			this.userFisrtLoginDuration = this.firstPage.user_fisrt_login_duration;
			this.userFisrtLoginScore = this.firstPage.user_first_login_score;
			this.welcomeMessage = this.firstPage.welcome_message
			console.log('组件展示状态（配置）', this.userFisrtLoginState)
			//获取组件需不需要展示（逻辑判断）
			if (this.userFisrtLoginState == 1 && this.showDaliyPopupNum == 1) {
				this.isShowPopup()
			}
		},
		watch: { //监听userid变化，不为空访客加载
			userInfos: {
				async handler(newVal, oldVal) {
					console.log('watch:userInfos触发')
					// #ifdef MP-WEIXIN
					if (this.userInfos != null && this.userInfos != '') {
						await this.getActionToday()
					}
					// #endif
				},
				deep: true
			},
			firstPage: {
				async handler(newVal, oldVal) {
					console.log('watch:firstPage触发')
					// #ifdef MP-WEIXIN
					console.log("页面是否弹窗（页面传参）", this.pageShowDaliyPopup)
					if (this.firstPage.user_first_login_state != null &&
						this.firstPage.user_first_login_state != '' && this.pageShowDaliyPopup == 1) {
						//请求结束，开始挂载
						this.increaseshowDaliyPopupNum()
						console.log('每日弹窗开始挂载次数', this.showDaliyPopupNum)
						//获取组件是否展示的配置（人为设置）
						this.userFisrtLoginState = this.firstPage.user_first_login_state
						this.userFisrtLoginDuration = this.firstPage.user_fisrt_login_duration;
						this.userFisrtLoginScore = this.firstPage.user_first_login_score;
						this.welcomeMessage = this.firstPage.welcome_message
						//获取组件需不需要展示（逻辑判断）
						if (this.userFisrtLoginState == 1 && this.showDaliyPopupNum == 1) {
							this.isShowPopup()
						}
					}
					// #endif
				},
				deep: true
			}

		},
		methods: {
			...mapMutations('popup', {
				increaseshowDaliyPopupNum: 'increaseshowDaliyPopupNum',

			}),
			//获取当前积分
			async getNowscore() {
				this.actionTodayForm.userId = this.userInfos.userId;
				const data = await getUserScore(this.actionTodayForm);
				if (data.code == 200) {
					console.log(data)
					this.nowScore = data.data.score
				}
			},
			//获取今日交互量
			async getActionToday() {
				this.actionTodayForm.userId = this.userInfos.userId;
				const data = await getActionNumToday(this.actionTodayForm);
				if (data.code == 200) {
					console.log(data)
					this.actionToday = data.data
					//将超出部分抹去
					if (this.actionToday.contentNum > 1) {
						this.actionToday.contentNum = 1
					}
					if (this.actionToday.commentNum > 3) {
						this.actionToday.commentNum = 3
					}
					if (this.actionToday.AdmireNum > 3) {
						this.actionToday.AdmireNum = 3
					}
				}
			},
			hideModal() {
				this.modalName = ''
				if (this.noNoticeChecked == true) {
					this.noRemindAgain();
				}
			},
			async isShowPopup() {
				let that = this
				await this.getActionToday();
				let code = this.userFirstLogin.code
				this.continuousLoginDays = this.userFirstLogin.continuousLoginDays
				this.isShared = this.userFirstLogin.isShared
				console.log('组件显示结果', code)
				if (code == 0 || code == null || code == '') {

				} else {
					for (var i = 0; i < that.welcomeMessage.length; i++) {
						if (that.welcomeMessage[i].key == code) {
							that.currentMesaage = that.welcomeMessage[i].content
							that.currentkey = code
							console.log(that.currentMesaage)
							break;
						} else {
							continue;
						}
					}
					that.modalName = 'userLogin'
					console.log('组件显示', that.modalName)
				}
			},
			toExchangeCenter() {
				uni.navigateTo({
					url: '../../package_task/pages/exchange-center/center'
				})
			},
			//不再提醒选择框
			checkboxChange(e) {
				console.log(e)
			},
			//不再提醒按钮
			async noRemindAgain() {
				let that = this
				await localData.cloud_shared.init()
				return new Promise((resolve, reject) => {
					// wx.cloud.init({
					// 	env: localData.envId,
					// 	traceUser: true,
					// })
					// wx.cloud.callFunction({
					localData.cloud_shared.callFunction({
						name: 'userLoginSQL', //云函数的名称
						data: {
							functionName: 'noRemindAgain', //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							userId: this.userInfos.userId,
							usingDatabase: localData.usingDatabase,
						},
						async complete(res) {
							let data = res.result
							resolve(data)
						}
					})
				})
			},

		} //methods

	}
</script>

<style lang="scss" scoped>
	.modal-bottom {
		display: block;
		// margin-left: 15rpx;
		padding-bottom: 20rpx;
		text-align: left;
		padding-left: 45rpx;
		padding-right: 50rpx;
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
</style>
