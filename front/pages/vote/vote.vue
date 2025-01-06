<template>
	<view>
		<!-- 投票弹窗 -->
		<q-popup v-model="showVotePopup" height="100%">
			<scroll-view scroll-y style="height: 100%;">
				<view class="vote-wrap">
					<q-field v-model="title" placeholder="添加标题"></q-field>
					<q-field v-model="item.value" v-for="(item, index) in voteOption" :key="index"
						:placeholder="'选项' + (index + 1)" right-icon="q-close" @onRightIcon="onRightIcon(index)">
					</q-field>
					<view class="option-add" @click="optionAdd">
						<q-icon name="q-add" color="#333"></q-icon>
						<text class="txt">添加选项</text>
					</view>
					<!-- 投票类型 -->
					<view class="block-title">投票类型</view>
					<view class="choose-wrap">
						<view class="item" @click="type = 1" :class="{ active: type === 1 }">单选</view>
						<view class="item" @click="type = 2" :class="{ active: type === 2 }">多选</view>
					</view>
					<!-- 投票有效期 -->
					<view class="block-title">投票有效期</view>
					<view class="choose-wrap">
						<view class="item" @click="expireTime = 1" :class="{ active: expireTime === 1 }">一天</view>
						<view class="item" @click="expireTime = 7" :class="{ active: expireTime === 7 }">7天</view>
						<view class="item" @click="expireTime = 30" :class="{ active: expireTime === 30 }">30天</view>
						<view class="item" @click="expireTime = 90" :class="{ active: expireTime === 90 }">90天</view>
					</view>
					<view v-if="title && voteOption[0].value && voteOption[1].value" @click="showVotePopup = false"
						style="margin-top: 50rpx;">
						<q-button>下一步</q-button>
					</view>
					<view v-else style="margin: 50rpx;"><button style="border-radius: 20rpx;">下一步</button></view>
				</view>
			</scroll-view>
		</q-popup>
		<!-- 帖子 -->
		<view class="container">
			<view class="title">{{ title }}</view>
			<textarea placeholder="投票补充内容..." :auto-height="true" maxlength="-1" v-model="contentText"
				class="post-txt"></textarea>
			<view class="vote-preview" @click="showVotePopup = true">
				<!-- <view class="title">{{ title }}</view> -->
				<view v-if="type === 1" class="type">单选</view>
				<view v-if="type === 2" class="type">多选</view>
				<view class="option-item" v-for="(item, index) in voteOption" :key="index">{{ item.value }}</view>
			</view>
			<!-- 选择板块 -->
			<!-- <navigator v-if="isTopic" url="/pages/post/choosePlate/choosePlate" class="choose-item">
				<image class="icon" src="/static/p_1.png"></image>
				<text class="txt">{{ plateName }}</text>
				<u-icon class="u-icon" name="arrow-right"></u-icon>
			</navigator> -->
			<!-- 可见范围 -->
			<!-- <view @click="" class="choose-item">
				<view class="action">
					<text class="txt"></text>可见范围
				</view>
				<radio-group @change="radioChange">
					<label class="margin-left-sm" v-for="(item, index) in items" :key="item.value">
						<radio style="transform:scale(0.8)" class="blue radio" :value="item.value"
							:checked="index === currentItem"></radio>
						<text class="margin-left-sm">{{item.name}}</text>
					</label>
				</radio-group>
			</view> -->
			<q-button @click="submit">发布</q-button>
		</view>
		<view v-if="showExaminePopup==1">
			<examine-popup @returnHid='returnHide'></examine-popup>
		</view>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>

	</view>
</template>

<script>
	import {
		mapState,
		mapActions
	} from 'vuex'
	import {
		reqPublishContent
	} from '@/api/index.js'
	import localData from '../../utils/data.js'
	export default {
		onLoad(options) {
			if (options.name) {
				this.plateName = options.name;
			}
			if (options.plateId) {
				this.form.plateId = options.plateId;
			}
		},
		data() {
			return {
				title: '',
				showVotePopup: true,
				voteOption: [{
						value: ''
					},
					{
						value: ''
					}
				],
				type: 1,
				expireTime: 1,
				contentText: '',
				form: {
					plateId: '',
				},
				plateName: '选择板块*',
				disName: '选择话题',
				isTopic: true,
				items: [{
						value: '0',
						name: '全部',
						checked: 'true'
					},
					{
						value: '1',
						name: '仅板块内',

					},

				],
				currentItem: 0,
				//认证相关
				showExaminePopup: 0,

			};
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
			...mapState('user', ['currentSchoolId','basicPlateId']),
			...mapState('config', ['privacyPopup']),
		},
		methods: {
			optionAdd() {
				this.voteOption.push({
					value: ''
				});
			},
			onRightIcon(index) {
				this.voteOption.splice(index, 1);
			},
			radioChange: function(e) {
				for (let i = 0; i < this.items.length; i++) {
					if (this.items[i].value === e.detail.value) {
						this.currentItem = i;
						break;
					}
				}
				this.form.visibleRange = Number(this.items[this.currentItem].value)
				console.log(this.form.visibleRange)
			},
			async submit() {
				// let permissionCheckResult = await this.permissionCheck(this.userInfos.userId, this.currentSchoolId,
				// 	this.form
				// 	.plateId)
				// if (permissionCheckResult != 0) {
				// 	return
				// }
				//先进行隐私授权判断
				console.log(this.privacyPopup.needAuthorization)
				if (this.privacyPopup.needAuthorization) {
					let needPrivacyAuthorization = await this.judgePrivacySetting()
					// console.log(needPrivacyAuthorization)
					if (needPrivacyAuthorization == true) {
						return
					}
				}
				else{
					console.log("已授权，不需要调用")
				}
				let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
					.currentSchoolId,
					1)
				if (permissionCheckResult == 600 || permissionCheckResult==300) { //禁言提示 600 审核中提示 300
					return
				}
				if (permissionCheckResult != 0) {
					this.showExaminePopup = 1
					return
				}

				//#ifdef MP-WEIXIN
				uni.showLoading({
					mask: true,
					title: '正在审核中'
				});
				//审核结果
				let checkResult;
				let checkStr = this.title + this.contentText;

				let votes = [];
				const form = {
					userId: this.userInfos.userId,
					contentText: this.contentText,
					// plateId: this.form.plateId,
					 plateId: this.basicPlateId,
					title: this.title,
					contentType: 1,
					schoolId: this.currentSchoolId
				}
				console.log(form)
				var options = this.voteOption;
				var now = new Date();
				for (var i = 0; i < options.length; i++) {
					checkStr += options[i].value;
					var temp = {};
					temp.isMultiple = this.type;
					temp.optionText = options[i].value;
					if (i == 0) {
						now.setDate(now.getDate() + this.expireTime);
					}
					temp.deadline = now;
					votes.push(temp);
				}
				form.votes = votes;

				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, checkStr, 1);
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
				checkResult |= textCheck.errcode;
				uni.showToast({
					mask: true,
					title: '审核结束'
				});
				//#endif

				if (checkResult === 0) {
					//审核通过
					var str = JSON.stringify(form);
					console.log(str);
					uni.request({
						url: localData.baseUrl + '/content/publishContentApi',
						data: str,
						method: 'POST',
						success(data) {
							if (data.data.code === 200) {
								uni.showToast({
									title: '发布成功',
									icon: "none"
								});
								setTimeout(function() {
									uni.reLaunch({
										url: "../index/index",
									})
								}, 1000)
							} else {
								uni.showToast({
									title: '发布失败',
									icon: "none"
								});
							}
						}
					}) //uni.request
				} else {
					//审核未通过
					uni.showToast({
						title: '您的内容存在敏感信息，请检查！',
						icon: "none"
					});
					return
					form.contentType = 2
					var str = JSON.stringify(form);
					console.log(str);
					uni.request({
						url: localData.baseUrl + '/content/publishContentApi',
						data: str,
						method: 'POST',
						success(data) {
							if (data.data.code === 200) {
								uni.showToast({
									title: '内容需要审核',
									icon: "none"
								});
								setTimeout(function() {
									uni.reLaunch({
										url: "../index/index",
									})
								}, 1000)
							} else {
								uni.showToast({
									title: '发布失败',
									icon: "none"
								});
							}
						}
					}) //uni.request
				}


			}, //submit
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},

		} //methods
	};
</script>

<style lang="scss" scoped>
	.vote-wrap {
		.block-title {
			margin-bottom: 20rpx;
			margin-left: 20rpx;
		}

		.option-add {
			display: flex;
			align-items: center;
			justify-content: center;
			border: 1px solid #333;
			background-color: #f5f5f5;
			margin: 30rpx;
			padding: 20rpx;
			border-radius: 20rpx;

			.txt {
				margin-left: 10rpx;
			}
		}

		.choose-wrap {
			display: flex;
			margin: 20rpx;

			.item {
				padding: 20rpx 40rpx;
				font-size: 24rpx;
				margin-right: 20rpx;
				background-color: #f5f5f5;
				border-radius: 10rpx;
			}

			.active {
				background-color: #333;
				color: #fff;
			}
		}
	}

	.post-txt {
		width: 100%;
		padding: 20rpx 0;
		min-height: 300rpx;
	}

	.vote-preview {
		background-color: #f5f5f5;
		padding: 30rpx;
		border-radius: 20rpx;

		.type {
			font-size: 24rpx;
			color: #999;
			margin: 20rpx 0;
		}

		.option-item {
			background-color: #fff;
			border: 1px solid #999;
			border-radius: 10rpx;
			text-align: center;
			margin-bottom: 20rpx;
		}
	}

	.choose-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border-bottom: 1px solid #f5f5f5;

		&:last-child {
			border: 0;
		}

		.txt {
			margin-left: 20rpx;
		}

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
