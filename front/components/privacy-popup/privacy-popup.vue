<template>
	<view class="privacy" v-if="privacyPopup.showPrivacyPopup">
		<view class="content">
			<view class="title">社区规范与隐私保护</view>
			<view class="des">
				应微信官方最新要求，在使用社区类小程序之前，请先仔细阅读
				<text class="link" @click="openPrivacyContract">{{ privacyPopup.privacyContractName }}</text>
				和
				<text class="link" @click="toRulePage">《社区规范》</text>
				。点击“同意”则表示知悉上述文件内容并接受，拒绝则小程序部分功能将无法正常使用。
			</view>
			<view class="btns">
				<button class="item reject" @click="exitMiniProgram">拒绝</button>
				<button id="agree-btn" class="item agree" open-type="agreePrivacyAuthorization"
					@agreeprivacyauthorization="handleAgreePrivacyAuthorization">同意</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	export default {
		name: 'privacy-popup',
		props: {
			privacyContractName: String,
		},
		data() {
			return {
				// privacyContractName: '',
			};
		},
		computed: {
			...mapState('config', ['firstPage', 'privacyPopup']),
		},
		created() {

		},

		methods: {
			...mapMutations('config', {
				setPrivacyContractName: 'setPrivacyContractName',
				setShowPrivacyPopup: 'setShowPrivacyPopup',
				setNeedAuthorization: 'setNeedAuthorization'

			}),
			// 同意隐私协议
			handleAgreePrivacyAuthorization() {
				const that = this;
				wx.requirePrivacyAuthorize({
					success: res => {
						console.log(res)
						uni.showToast({
							mask: true,
							icon: 'none',
							title: '操作成功！已可正常使用小程序'
						})
						that.setShowPrivacyPopup(false)
						that.setNeedAuthorization(false)
						// that.hideModal(1)
					}
				});
			},
			// 拒绝隐私协议
			exitMiniProgram() {
				const that = this;
				that.setShowPrivacyPopup(false);
				uni.showToast({
					title: '小程序部分功能暂时无法使用，后续可重新选择',
					icon: 'none',
					duration: 2500
				});
			},
			// 跳转协议页面  
			// 点击高亮的名字会自动跳转页面 微信封装好的不用操作
			openPrivacyContract() {
				wx.openPrivacyContract({
					fail: () => {
						uni.showToast({
							title: '网络错误',
							icon: 'error'
						});
					}
				});
			},
			//关闭弹窗
			hideModal(index) {
				this.$emit("returnHid", index) //传递的值
			},
			//前往规则页面
			toRulePage(){
				uni.navigateTo({
					url: '../../package_task/pages/rule-page/rule-page?index=1'
				})
			},
		}
	};
</script>

<style lang="scss" scoped>
	.privacy {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 9999999;
		display: flex;
		align-items: center;
		justify-content: center;

		.content {
			width: 85vw;
			padding: 50rpx;
			box-sizing: border-box;
			background: #fff;
			border-radius: 16rpx;

			.title {
				text-align: center;
				color: #333;
				font-weight: bold;
				font-size: 34rpx;
			}

			.des {
				font-size: 26rpx;
				color: #666;
				margin-top: 40rpx;
				text-align: justify;
				line-height: 1.6;

				.link {
					color: #07c160;
					text-decoration: underline;
				}
			}

			.btns {
				margin-top: 60rpx;
				display: flex;
				justify-content: space-between;

				.item {
					justify-content: space-between;
					width: 150rpx;
					height: 60rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					border-radius: 16rpx;
					box-sizing: border-box;
					border: none;
				}

				.reject {
					background: #f4f4f5;
					color: #909399;
				}

				.agree {
					background: #07c160;
					color: #fff;
				}
			}
		}
	}
</style>