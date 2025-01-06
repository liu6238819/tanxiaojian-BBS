<template>
	<view>
		<view class="clearMessage" @click="clearMsg">
			<text class="clearTxt">清空</text>
			<image class="clearIcon" src="/static/clear.png" mode="aspectFit"></image>
		</view>
		<block v-if="msgList" v-for="(item,index) in msgList" :key="index">
			<view class="comment-msg-item" @click="read(item,index)">

				<view @click="jumpToUserHome(item)" hover-class="none">
					<u-avatar class="avatar" :src="item.userAvatar"></u-avatar>
				</view>
				<view class="msg-c">
					<view class="msg-c-t">

						<text class="username" @click="jumpToUserHome(item)">{{item.userNickName}}</text>
						<text class="time">{{item.createTime | date('yyyy年mm月dd日hh时MM分')}}</text>
					</view>
					<navigator v-if="msgType === 2" :url="'/package_task/pages/bbs/user/home?userId=' + item.fromUserId"
						hover-class="none">
						<view>{{ remindWord }}</view>
						<view class="msg-c-txt">
							<text v-if="item.state === 0" class="dot"></text>
						</view>
					</navigator>

					<navigator v-if="msgType != 2" hover-class="none" :url="'/pages/post/detail?id='+item.contentId">
						<view @click="updateSchoolConfigAndUserState(item.schoolId)">{{remindWord}}
							（{{ getSchoolName(item.schoolId) }}）</view>
						<view class="msg-c-txt">
							<text v-if="item.state === 0" class="dot"></text>
						</view>
					</navigator>
				</view>
			</view>
		</block>

		<block v-if="msgList.length === 0">
			<u-empty margin-top="100" text="暂无相关消息" mode="favor"></u-empty>
		</block>
		<!-- 加载状态 -->
		<block v-else>
			<view style="margin: 30rpx 0;">
				<!-- <u-loadmore :status="loadStatus" @loadmore='getReminds()' /> -->
				<u-loadmore :status="loadStatus" @loadmore='getRemindsAllSchool()' />
			</view>
		</block>
		<!-- 弥补底部陷入 -->
		<view style="height: 52px;"></view>
	</view>
</template>

<script>
	import {
		readRemind,
		getReminds,
		clearRemind,
		getRemindsDistinguishSchoolId,
		getUserStateBySchool
	} from '@/api/index.js'
	import {
		mapState,
		mapMutations
	} from 'vuex'

	import localData from '@/utils/data.js';
	export default {
		data() {
			return {
				loadStatus: 'loadmore',
				msgType: null,
				msgList: [],
				remindWord: '',
				queryType: 'all',
				pageNum: 0,
				pageSize: 5,
				schooolMsgList: []
			};
		},
		computed: {
			...mapState('remind', ['msgCount', 'reminds', 'remindBox', 'admireCount', 'followCount', 'replyCount']),
			...mapState('user', ['userInfos', 'currentSchoolId']),
			...mapState('user', ['schoolList']),
			...mapState('config', ['needRelaunch']),
			getSchoolName() {
				return (schoolId) => {
					const school = this.schoolList.find(element => element.schoolId === schoolId);
					// console.log(school.schoolName)
					if (school) {
						if (school.anotherName && school.anotherName!='') {
							return school.anotherName
						}
						else{
							return school.schoolName
						}
					}
					else{
						return '论坛';
					}
					// return school ? school.schoolName : '论坛';
				}
			}

		},
		onLoad(options) {
			console.log('options', options)
			this.msgType = options.type * 1
			this.msgList = []
			this.schooolMsgList = JSON.parse(JSON.stringify(this.schoolList))
			console.log(this.msgList)
			this.clearMsgOnLoad()
		},
		onShow() {
			console.log('onshow');
			console.log(this.msgList);
			// 每次进入时重置列表
			if (this.msgType == 3) {
				this.queryType = 'reply'
				//所有回复的消息
				this.remindWord = "评论了您，快去看看吧！"
			} else if (this.msgType == 1) {
				this.queryType = 'admire'
				//所有点赞的消息
				this.remindWord = "赞了您"
			} else if (this.msgType == 2) {
				this.queryType = 'follow'
				//筛选所有关注的消息
				this.remindWord = "关注了您，快去回关吧！"
			}
			// this.getReminds()
			this.getRemindsAllSchool()
		},
		methods: {
			...mapMutations('remind', ['reduceMsgCount']),
			...mapMutations('remind', {
				setReminds: 'setReminds',
				setRemindCount: 'setRemindCount',
				setAdmireCount: 'setAdmireCount',
				setFollowCount: 'setFollowCount',
				setReplyCount: 'setReplyCount',
			}),
			...mapMutations('user', {
				getCurrentSchoolId: 'getCurrentSchoolId',
				setCurrentUserState: 'setCurrentUserState'
			}),
			...mapMutations('config', {
				setNeedRelaunch: 'setNeedRelaunch',

			}),
			jumpToUserHome(user) {
				if (user.userNickName == '一位游客' || user.userNickName == '一位隐士') return
				let url = '/package_task/pages/bbs/user/home?userId=' + user.fromUserId
				uni.navigateTo({
					url
				});
			},
			async read(remind, index) {
				if (remind.state == 1) return
				console.log(remind)
				const params = {
					remindId: remind.id,
					userId: this.userInfos.userId
				}
				// console.log('params', params);
				const readData = await readRemind(params)
				if (readData.code === 200) {
					// 消息已读
					this.msgList[index].state = 1 //将对应消息的状态改为已读
					//修改store中的消息数量
					if (this.queryType == 'reply') {
						this.setReplyCount(this.replyCount - 1)
					} else if (this.queryType == 'admire') {
						this.setAdmireCount(this.admireCount - 1)
					} else if (this.queryType == 'follow') {
						this.setFollowCount(this.followCount - 1)
					}
					this.reduceMsgCount() //vuex中的消息总数量-1
				}

			},
			async clearMsg() {
				uni.showLoading({
					title:"消息清空中..."
				})
				let params = {
					type: this.msgType,
					userId: this.userInfos.userId
				}
				let readData = await clearRemind(params)
				if (readData.code === 200) {
					// 重新请求消息列表
					for (var i = 0; i < this.msgList.length; i++) {
						let remind = this.msgList[i]
						if (remind.state == 0) {
							remind.state = 1 //将当前页面中消息的状态改为已读
						}

					}
					//修改store中的消息数量
					if (this.queryType == 'reply') {
						//vuex中的消息总数量
						this.setRemindCount(this.msgCount[3]-this.replyCount)
						//回复
						this.setReplyCount(0)
					} else if (this.queryType == 'admire') {
						//vuex中的消息总数量
						this.setRemindCount(this.msgCount[3]-this.admireCount)
						//点赞
						this.setAdmireCount(0)
					} else if (this.queryType == 'follow') {
						//vuex中的消息总数量
						this.setRemindCount(this.msgCount[3]-this.followCount)
						//关注
						this.setFollowCount(0)
					}	
				}
				uni.hideLoading()

			},
			async clearMsgOnLoad() {
				let params = {
					type: this.msgType,
					userId: this.userInfos.userId
				}
				let readData = await clearRemind(params)
				if (readData.code === 200) {
					// 重新请求消息列表
					//修改store中的消息数量
					if (this.queryType == 'reply') {
						//vuex中的消息总数量
						this.setRemindCount(this.msgCount[3]-this.replyCount)
						//回复
						this.setReplyCount(0)
					} else if (this.queryType == 'admire') {
						//vuex中的消息总数量
						this.setRemindCount(this.msgCount[3]-this.admireCount)
						//点赞
						this.setAdmireCount(0)
					} else if (this.queryType == 'follow') {
						//vuex中的消息总数量
						this.setRemindCount(this.msgCount[3]-this.followCount)
						//关注
						this.setFollowCount(0)
					}	
				}
			},
			async getReminds() {
				this.pageNum = this.pageNum + 1
				const params = {
					userId: uni.getStorageSync('userInfos').userId,
					queryType: this.queryType,
					schoolId: this.currentSchoolId,
					pageNum: this.pageNum,
					pageSize: this.pageSize
				}
				const remindsData = await getReminds(params)
				if (remindsData.code === 200) {
					console.log(this.msgList);
					//修改加载状态
					if (remindsData.data.data.length == 0) {
						this.loadStatus = 'nomore'
					}
					//加载新内容
					this.msgList.push(...remindsData.data.data)


				} else {
					uni.showToast({
						icon: 'none',
						title: '服务器请求异常'
					})
				}
			},
			async getRemindsAllSchool() {
				this.pageNum = this.pageNum + 1
				const params = {
					userId: uni.getStorageSync('userInfos').userId,
					queryType: this.queryType,
					pageNum: this.pageNum,
					pageSize: this.pageSize
				}
				const remindsData = await getRemindsDistinguishSchoolId(params)
				if (remindsData.code === 200) {
					console.log(this.msgList);
					//修改加载状态
					if (remindsData.data.data.length == 0) {
						this.loadStatus = 'nomore'
					}
					//加载新内容
					this.msgList.push(...remindsData.data.data)
					//按学校分组
					let remindListData = remindsData.data.data
					remindListData.forEach(item => {
						for (var i = 0; i < this.schooolMsgList.length; i++) {
							if (this.schooolMsgList[i].schoolId == item.schoolId && this.schooolMsgList[i]
								.msgList) {
								this.schooolMsgList[i].msgList.push(item)
							} else if (this.schooolMsgList[i].schoolId == item.schoolId && !this
								.schooolMsgList[i].msgList) {
								this.schooolMsgList[i].msgList = []
								this.schooolMsgList[i].msgList.push(item)
							}
						}
					})
					console.log("学校列表", this.schoolList, this.schooolMsgList)


				} else {
					uni.showToast({
						icon: 'none',
						title: '服务器请求异常'
					})
				}
			},
			async updateSchoolConfigAndUserState(schoolId) {
				if (schoolId * 1 != uni.getStorageSync('schoolId')) {
					//修改store与本vue里的变量
					this.getCurrentSchoolId(schoolId);
					//修改storage里的变量
					uni.setStorageSync("schoolId", this.currentSchoolId);
					//按学校id更新启动项配置
					await this.getSchoolFunctionConfig()
					//按学校id更新基础配置
					await this.getSchoolBasicConfig()
					//需要重载首页
					this.setNeedRelaunch(true)
				}
				//更新用户在该学校的状态
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					//this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.clearMessage {
		display: flex;
		background-color: #f5f5f5;
		align-items: center;
		justify-content: flex-end;
		height: 70rpx;

		.clearTxt {
			//margin-left:auto;
			padding-right: 15rpx;
		}

		.clearIcon {
			height: 50rpx;
			width: 50rpx;
			margin-right: 30rpx;
			//margin-left:auto;
		}
	}

	.comment-msg-item {
		display: flex;
		background-color: #fff;
		border-bottom: 1px solid #f5f5f5;
		padding: 20rpx;
	}

	.comment-msg-item .msg-c {
		width: 100%;
	}

	.comment-msg-item .avatar {
		margin-right: 10rpx;
	}

	.comment-msg-item .post-c {
		background-color: #eee;
		margin-top: 20rpx;
		padding: 20rpx;
		font-size: 12px;
		line-height: 1.8;
		border-radius: 10rpx;
	}

	.comment-msg-item .post-c .post-c-txt {
		text-overflow: ellipsis;
		display: -webkit-box;
		word-break: break-all;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 5;
		overflow: hidden;
	}

	.comment-msg-item .msg-c {
		display: flex;
		flex-direction: column;
	}

	.comment-msg-item .msg-c .msg-c-t {
		display: flex;
		align-items: center;
	}

	.comment-msg-item .msg-c .msg-c-t .time {
		font-size: 28rpx;
		color: #999;
		margin-left: auto;
		margin-bottom: 20rpx;
	}

	.comment-msg-item .msg-c .username {
		margin-bottom: 20rpx;
		color: #2B85E4;
	}

	.comment-msg-item .msg-c .msg-c-txt {
		display: flex;
		align-items: center;
	}

	.comment-msg-item .msg-c .msg-c-txt .dot {
		width: 10rpx;
		height: 10rpx;
		background-color: #FA3534;
		border-radius: 50%;
		display: block;
		margin-left: 20rpx;
	}

	/*标记已读*/
	.read-tips {
		padding: 20rpx;
		background-color: #f5f5f5;
		display: flex;
	}

	.read-tips text {
		margin-left: auto;
	}
</style>
