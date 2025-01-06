<template>
	<view>
		<!-- 弥补底部陷入 -->
		<view v-if="active==0" class="tabbar-height"></view>
		<view v-if="active!=0" class="tabbar-height" style="background-color: #f5f5f5;"></view>
		<view class="tabbar-count-down" v-if="currentUserType==2&&countDownStamp">
			<view style="font-weight: bold;display: flex;flex-direction: row;align-items: center;">
				<view style="height: 100%;display: flex;align-items: center;font-size: 24rpx;color: #000000;">禁言剩余时间：
				</view>
				<u-count-down separator-size="24" font-size="30" color="#000000" separator="zh"
					:timestamp="countDownStamp" separator-color="#000000" bg-color="#ffc208"></u-count-down>
			</view>
		</view>
		<view class="tabbar-wrap">
			<block v-for="(item, index) in list" :key="index">
				<view @click="onTab(item, index)" v-show="active !== index" class="tab-item">
					<image class="icon" :class="{ 'mid-button': item.midButton }" :src="item.iconPath"></image>
					<text class="txt" :style="{ color: color }">{{ item.text }}</text>
					<!-- <text v-if="count[index] > 0" class="number">{{count[index] | maxNum}}</text> -->
					<!-- 闲置交易标签 -->
					<text
						v-if="index==1 && firstPage.page_path=='/package_task/pages/second-hand-item/list' && showSecondHandPageNum==0"
						class="new-goods-text">上新</text>
					<text v-if="count[index] > 0 || markState[index] > 0" class="number"></text>
				</view>
				<view @click="onTab(item, index)" v-show="active === index" class="tab-item">
					<image class="icon" :class="{ 'mid-button': item.midButton }" :src="item.selectedIconPath"></image>
					<text class="txt" :style="{ color: selectedColor }">{{ item.text }}</text>
					<!-- <text v-if="count[index] > 0" class="number">{{count[index] | maxNum}}</text> -->
					<text v-if="count[index] > 0 || markState[index] > 0" class="number"></text>
				</view>
			</block>
		</view>
		<!-- 中间按钮弹窗 -->
		<q-popup v-model="showPopup">
			<view class="handle-wrap">
				<!-- 动态和投票 -->
				<view @click="handleJump(item)" class="item" v-for="(item,index) in popup" :key="index">
					<image mode="widthFix" class="icon" :src="item.icon"></image>
					<text class="txt">{{item.text}}</text>
				</view>
			</view>
			<view class="handle-close">
				<q-icon @click="onClose" size="50rpx" name="q-Close"></q-icon>
			</view>
		</q-popup>
		<!-- 认证弹窗 -->
<!-- 		<view v-if="showExaminePopup==1">
			<examine-popup @returnHid='returnHide'></examine-popup>
		</view> -->
		<view v-if="showExaminePopup==1">
			<examine-popup v-if="currentSchoolState!=3 " @returnHid='returnHide'></examine-popup>
		</view>
	</view>
</template>

<script>
	/**
	 * m-field tabbar 底部菜单
	 * @description 用于自定义底部菜单。
	 * @tutorial https://ui.ymeoo.cn
	 * @property {Array} list 菜单数据列表
	 * @property {String} color 文字颜色
	 * @property {Array} count 提示数字角标
	 * @property {String} selectedColor 菜单选中时文字的颜色
	 * @event {Function} click 菜单项点击事件
	 */
	import tabbar from '@/utils/tabbar.js';
	import {
		mapState,
		mapActions,
		mapMutations,
		mapGetters
	} from 'vuex';
	import {
		bannedTimeEnd,
	} from '@/api/index.js'
	import localData from '@/utils/data.js';
	export default {
		name: 'q-tabbar',
		props: {
			//tab 上的文字默认颜色
			color: {
				type: String,
				default: '#7A7E83'
			},
			//tab 上的文字选中时的颜色
			selectedColor: {
				type: String,
				default: '#515151'
			},
			active: {
				type: Number,
				default: 0
			},
			count: {
				type: Array,
				default: []
			},
			markState: {
				type: Array,
				default: []
			}

		},
		beforeCreate() {
			uni.hideTabBar();
		},
		filters: {
			maxNum(num) {

				if (num > 99) {
					return '99+'
				} else {
					return num
				}
			}
		},
		data() {
			return {
				list: tabbar.list,
				popup: tabbar.popup,
				showPopup: false,
				user: {},
				pagePath: '',
				text: '',
				//认证相关
				showExaminePopup: 0,
				currentPageIndex: 0,
			};
		},
		async created() {
			// await this.aboutfunctionConfig('firstPage')
			this.pagePath = this.firstPage.page_path;
			this.text = this.firstPage.page_text;
			this.list[1].pagePath = this.pagePath;
			this.list[1].text = this.text;
			//根据配置修改标签样式
			if (localData.UIColor=='blue') {
				this.list[0].selectedIconPath = '/static/newUI/home-blue.png'
				this.list[1].selectedIconPath = '/static/newUI/tab2-blue.png'
				this.list[3].selectedIconPath = '/static/newUI/message-blue.png'
				this.list[4].selectedIconPath = '/static/newUI/mine-blue.png'
			}
			// if (this.currentSchoolId==9999 || uni.getStorageSync('schoolId')==9999) {
			// 	this.list[0].selectedIconPath = '/static/NXB/home-purple.png'
			// 	this.list[1].selectedIconPath = '/static/NXB/tab2-purple.png'
			// 	this.list[3].selectedIconPath = '/static/NXB/message-purple.png'
			// 	this.list[4].selectedIconPath = '/static/NXB/mine-purple.png'
			// }
			console.log('修改配置后', this.list[1].pagePath, this.list[1].text)
		},
		computed: {
			userInfos() {
				return this.$store.getters['user/getUser'];
			},
			currentSchoolId() {
				console.log(this.$store.getters['user/getCurrentSchoolId'])
				return this.$store.getters['user/getCurrentSchoolId'];
			},
			...mapState('config', ['firstPage', 'privacyPopup', 'alumniOnly']),
			...mapState('user', ['currentUserType', 'bannedTime','currentSchoolState','isLocalUser']),
			...mapState('popup', ['showSecondHandPageNum']),
			countDownStamp() {
				let countDown = 0
				console.log('被禁言的时间', this.bannedTime)
				if (this.bannedTime) {
					let nowTimeStamp = new Date().getTime()
					let bannedTime = new Date(this.bannedTime).getTime()
					countDown = bannedTime / 1000 - nowTimeStamp / 1000
					if (this.currentUserType == 2 && countDown <= 0) {
						this.onBannedTimeEnd()
					}
				} else {
					countDown = null
				}
				return countDown
			}
		},
		watch: {
			firstPage: {
				async handler(newVal, oldVal) {
					console.log('watch触发')
					// #ifdef MP-WEIXIN
					if (this.firstPage.duration != null && this.firstPage.duration != '') {
						this.pagePath = this.firstPage.page_path;
						this.text = this.firstPage.page_text;
						this.list[1].pagePath = this.pagePath;
						this.list[1].text = this.text;
					}
					// #endif
				},
				deep: true
			}
		},
		methods: {
			...mapMutations('user', {
				setCurrentUserType: 'setCurrentUserType',
			}),
			async onTab(e, index) {
				let url = e.pagePath
				// if (index == 2) {
				// 	url = '/package_task/pages/center/index'
				// 	uni.navigateTo({
				// 		url,
				// 		fail(e) {
				// 			console.log(e)
				// 		}
				// 	});
				// 	return
				// }
				if (index == 1) {
					url = this.pagePath
					uni.navigateTo({
						url,
						fail(e) {
							console.log(e)
						}
					});
					return
				}
				if (!e.isCustom) {
					//console.log(url)
					uni.switchTab({
						url,
						fail(e) {
							console.log(e)
						}
					});
				}
				if (e.midButton) {
					//userId直接返回
					if (!this.userInfos.userId) {
						uni.showToast({
							title: '请登录后发帖'
						})
						return;
					}
					//获取校友认证配置信息,从store配置项中取
					// let alumniInfo = await localData.aboutAlumniOnly('alumniOnly', this.currentSchoolId)
					let alumniInfo = this.alumniOnly.state
					let isAlumniOnly = alumniInfo
					console.log("身份认证开关", isAlumniOnly)
					//当isAlumniOnly==1时，进行权限判断
					if (isAlumniOnly == 1) {

						//先进行隐私授权判断
						console.log(this.privacyPopup.needAuthorization)
						if (this.privacyPopup.needAuthorization) {
							let needPrivacyAuthorization = await this.judgePrivacySetting()
							// console.log(needPrivacyAuthorization)
							if (needPrivacyAuthorization == true) {
								return
							}
						} else {
							console.log("已授权，不需要调用")
						}
						let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
							.currentSchoolId,
							1)
						console.log(permissionCheckResult)
						if (permissionCheckResult == 600 || permissionCheckResult==300) { //禁言提示 600 审核中提示 300
							return
						}
						if (permissionCheckResult != 0) {
							this.showExaminePopup = 1
							return
						}

					}
					// this.showPopup = true;
					// console.log("输出", index)
					if (this.active == 1 && this.firstPage.page_path == '/package_task/pages/second-hand-item/list') {
						uni.navigateTo({
							url: '/pages/post/add?contentType=' + 8 + '&previousPage=secondHand'
						})
					} else {
						this.toAddPost()
					}
				}
				this.$emit('click', e);
				
			},
			onClose() {
				this.showPopup = false;
			},
			handleJump(e) {
				uni.navigateTo({
					url: e.url
				})

				this.showPopup = false;
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			//调佣云函数-功能配置-课程评价（已停用）
			async aboutfunctionConfig(functionName) {
				let that = this
				return new Promise((resolve, reject) => {
					wx.cloud.init({
						env: localData.envId,
						traceUser: true,
					})
					wx.cloud.callFunction({
						name: 'functionConfigSQL', //云函数的名称
						data: {
							functionName: functionName, //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							firstPageInfo: '',
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("组件中配置项", nowdata)
							if (nowdata) {
								that.pagePath = nowdata.firstPageInfo.page_path;
								that.text = nowdata.firstPageInfo.page_text;
								resolve(nowdata)
							}
						}
					})
				})
			},
			toAddPost() {
				uni.navigateTo({
					url: '/pages/post/add'
				})
			},
			async onBannedTimeEnd() {
				let form = {
					schoolId: this.currentSchoolId,
					userId: this.userInfos.userId,
				}
				const data = await bannedTimeEnd(form)
				if (data.code == 200) {
					console.log(data)
					this.setCurrentUserType(0)
				}

			},
		}
	};
</script>

<style lang="scss" scoped>
	.tabbar-wrap {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		z-index: 99999;
		background-color: #fff;
		//height: 52px;
		display: flex;
		align-items: center;
		border-top: 1px solid #f5f5f5;
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
		// position: fixed;
		// bottom: 0;
		// width: 100%;
		// z-index: 99999;
		// background-color: #fff;
		// height: 52px;
		// display: flex;
		// align-items: center;

		.tab-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			flex: 1;
			height: 100%;
			border-radius: 8px;
			position: relative;

			.icon {
				width: 30px;
				height: 30px;
			}

			.txt {
				font-size: 10px;
			}

			.number {
				background-color: #FA3534;
				color: #fff;
				border-radius: 50%;
				position: absolute;
				right: 45rpx;
				top: 5rpx;
				display: inline-block;
				width: 20rpx;
				height: 20rpx;
				font-size: 20rpx;
				display: flex;
				justify-content: center;
				align-items: center;
			}

			.new-goods-text {
				background-color: #FA3534;
				color: #fff;
				border-radius: 10rpx;
				position: absolute;
				right: 0rpx;
				top: 5rpx;
				display: inline-block;
				width: 60rpx;
				height: 30rpx;
				font-size: 20rpx;
				display: flex;
				justify-content: center;
				align-items: center;
			}

			.mid-button {
				width: 43px;
				height: 43px;
			}
		}

		.tab-item[hidden] {
			display: none !important;
		}
	}

	// 中间按钮弹出框
	.handle-wrap {
		display: flex;
		padding: 50rpx 0;

		.item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			flex: 1;
			padding: 20rpx 0;
			border-radius: 20rpx;

			.icon {
				width: 100rpx;
				margin-bottom: 20rpx;
			}

			.txt {
				font-size: 28rpx;
			}
		}
	}

	.handle-close {
		display: flex;
		justify-content: center;
		margin-bottom: 50rpx;
	}

	.tabbar-height {
		height: calc(52px + env(safe-area-inset-bottom));
		background-color: #fff;
	}

	.tabbar-count-down {
		//padding: 10rpx 16rpx;
		position: fixed;
		bottom: calc(80rpx + env(safe-area-inset-bottom));
		left: 0;
		right: 0;
		z-index: 99;
		background-color: rgba(255, 194, 8, 1);
		display: flex;
		justify-content: center;
		align-items: center;
		border-top: 1px solid #f5f5f5;
	}
</style>
