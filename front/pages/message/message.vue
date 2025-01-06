<template>
	<view>
		<!-- :url="'/pages/chat/chat?user=' + $f.tostring(item.user_info)" -->
		<u-grid :col="3" :border="false" @click="toNav">
			<u-grid-item index="1">
				<u-badge :count="admireCount" :offset="[10, 50]"></u-badge>
				<image class="nav-icon" src="/static/images/icon/souc.png"></image>
				<view class="grid-text">赞</view>
			</u-grid-item>
			<u-grid-item index="2">
				<u-badge :count="followCount" :offset="[10, 50]"></u-badge>
				<image class="nav-icon" src="/static/images/icon/gz.png"></image>
				<view class="grid-text">新增关注</view>
			</u-grid-item>
			<u-grid-item index="3">
				<u-badge :count="replyCount" :offset="[10, 50]"></u-badge>
				<image class="nav-icon" src="/static/images/icon/pl.png"></image>
				<view class="grid-text">评论</view>
			</u-grid-item>
		</u-grid>
		<view class="msg-wrap">
			<view class="title"
				style="background-color: #fff; display: flex; justify-content:space-between;font-size: 28rpx;">
				<view class="left text-bold" style="display: flex; align-items: center;">
					帖子动态
					<view
						style=" margin-left: 10rpx;;display: flex;justify-content: center;align-items: center;padding: 3rpx 10rpx;height: 35rpx; border-radius: 17.5rpx; background-color: red; color: #fff; font-weight: 100; font-size: 26rpx;"
						v-if="markState[3]>0">{{markState[3]}}</view>
				</view>
				<view class="left text-bold" @click="editAllRead()" v-if="markState[3]>0"
					style="color: #0055ff; display: flex; align-items: center;">
					清空未读
				</view>
				<view class="left" style="color: #b8b8b8;" v-if="markState[3]==0">全部已读
				</view>
			</view>
			<block v-if="markMessageList&&markMessageList.length>0">
				<view v-for="markMessage in markMessageList">
					<view v-if="markMessage.type =='focus' && markMessage.isRead==0 && markMessage.postNum>0"
						style="background-color: rgba(255, 220, 30, 1.0);margin: 5rpx 0; padding: 20rpx 10rpx; font-size: 28rpx;"
						@click="getFocusContentPage(markMessage)">
						<view style="display: flex; align-items: center; justify-content: space-between;">
							<view class="text-bold">
								<text>您关注的用户新增 </text>
								<text style="margin: 0 5rpx;"> {{markMessage.postNum}} </text>
								<text> 条帖子</text>
							</view>
							<view style="font-size: 24rpx;">{{markMessage.lastCommentTime| date('yyyy年mm月dd日hh时')}}</view>
						</view>
						<view></view>
					</view>
					<view v-if="markMessage.type =='focus' && (markMessage.isRead==1 || markMessage.isRead==0 && markMessage.postNum==0)"
						style="background-color: #fff;margin: 5rpx 0; padding: 20rpx 10rpx; font-size: 28rpx;"
						@click="getFocusContentPage(markMessage)">
						<view style="display: flex; align-items: center; justify-content: space-between;">
							<view class="text-bold">
								<text>您关注的用户新增 </text>
								<text style="margin: 0 5rpx;"> {{markMessage.postNum}} </text>
								<text> 条帖子</text>
							</view>
							<view style="font-size: 24rpx;">{{markMessage.lastCommentTime| date('yyyy年mm月dd日hh时')}}</view>
						</view>
						<view></view>
					</view>
					<view v-if="markMessage.isRead ==1 &&markMessage.isMark==1"
						style="background-color: #fff;margin: 10rpx 0; padding: 10rpx; font-size: 28rpx;"
						@click="toContent(markMessage)">
						<view class="text-bold text-gray">
							<text>帖子新增 </text>
							<text style="margin: 0 5rpx;"> {{markMessage.commentCount}} </text>
							<text> 条评论</text>
						</view>
						<view></view>
						<view style="padding: 20rpx; margin: 10rpx 0; background-color: #f0f0f0;">
							{{markMessage.contentText.slice(0, 20)}}...
						</view>
						<view style="display: flex; justify-content: space-between;font-size: 24rpx;">
							<view @click.stop="deleteMark(markMessage)" style="color: #0055ff; font-weight: bold;">
								<u-icon name="eye-off"></u-icon>
								<text style="margin-left: 10rpx;">取消追踪</text>
							</view>
							<view style="">{{markMessage.lastCommentTime| date('yyyy年mm月dd日hh时')}}</view>
							<!-- Date.parse(markMessage.createTime)| timeFrom -->
						</view>

						<!-- <view>{{markMessage.createTime| date('yyyy年mm月dd日hh时MM分')}}</view> -->
					</view>

					<view v-if="markMessage.isRead ==0 && markMessage.isMark==1"
						style="background-color: rgba(255, 220, 30, 1.0);margin: 5rpx 0; padding: 20rpx; font-size: 28rpx;"
						@click="toContent(markMessage)">
						<view class="text-bold">
							<text>帖子新增 </text>
							<text class="text-red" style="margin: 0 5rpx;font-size: 30rpx;">
								{{markMessage.commentCount}} </text>
							<text> 条评论</text>
						</view>
						<view></view>
						<view style="padding: 10rpx 10rpx; margin: 10rpx 0; background-color: rgba(240, 240, 240,0.5);">
							{{markMessage.contentText.slice(0, 20)}}...
						</view>
						<view style="display: flex; justify-content: space-between;font-size: 24rpx;">
							<view @click.stop="deleteMark(markMessage)" style="color: #0055ff; font-weight: bold;">
								<u-icon name="eye-off"></u-icon>
								<text style="margin-left: 10rpx;">取消追踪</text>
							</view>
							<view style="">{{markMessage.lastCommentTime| date('yyyy年mm月dd日hh时')}}</view>
						</view>
						<!-- <view>{{markMessage.createTime| date('yyyy年mm月dd日hh时MM分')}}</view> -->
					</view>
				</view>
				<block v-if="loadStatus == 'loadmore'||loadStatus == 'nomore'">
					<view style="margin: 30rpx 0;">
						<u-loadmore :status="loadStatus" :load-text="loadText" @loadmore='loadMore()' />
					</view>
				</block>
			</block>
			<block v-if="markMessageList&&markMessageList.length==0">
				<view class="msg-empty">
					<image class="img" mode="widthFix" src="/static/empty.png"></image>
					<text class="txt">暂无蹲的帖子</text>
				</view>
			</block>
		</view>
		<!-- 删除私信消息弹窗 -->
		<u-action-sheet :list="sheetList" v-model="showChatSheet" @click="onSheeChat"></u-action-sheet>
		<!-- 显示tabBar中的红色圆点(消息提示) -->
		<q-tabbar :active="3" :count="msgCount" :markState="markState"></q-tabbar>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>
		<view v-if="showExaminePopup==1">
			<examine-popup v-if="currentSchoolState!=3" @returnHid='returnHide'></examine-popup>
		</view>
		<view class="cu-modal show" v-if="focusModalName=='DialogModal'">
			<view class="cu-dialog modal-content" style="max-height: 80%;">
				<view class="cu-bar bg-white lg justify-end">
					<view class="content">关注人帖子列表</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<!-- 我的帖子 -->
				<post-list :contents="focusUserContentList" :showTag="false" :loadStatus="focusLoadStatus" @loadmore='getFocusContentPage(focusLoadText)'
					@editLike="editLikeState" @editMark="editMarkState" @editDeletePost="editDeletePost">
				</post-list>
			</view>
		</view>
	</view>
</template>

<script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>

<script>
	import {
		mapState,
		mapActions,
		mapMutations,
	} from 'vuex'
	import {
		getReminds,
		editMark_1,
		editMarkRead_1,
		getMarkMessageList_1,
		searchFocusContentRecords,
		getFocusContentList,
		updateFocusSearchTime
	} from '@/api/index.js'
	import localData from '@/utils/data.js'
	export default {
		onLoad() {
			// #ifdef MP-WEIXIN
			//this.buildWebSocketWX();
			// #endif
			// #ifdef H5
			this.buildWebSocket();
			// #endif

		},
		onShow() {
			this.markListForm.pageNum = 0
			this.pageMarkMessageList = []
			this.getMarkMessageList()
		},
		onHide() {
			// this.webSocket.close();
		},
		async mounted() {

		},
		data() {
			return {
				webSocket: null,
				showSheet: false,
				showChatSheet: false,
				sheetList: [{
					text: '删除',
					color: 'red'
				}],
				hasChatList: false, //是否有私信
				chatType: '', //当前选中对话框的类型
				chaterId: '', //当前选中对话框的用户id
				loadText: {
					loadmore: '点击加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				loadStatus: 'loadmore',
				pageSize: 10,
				markListForm: {
					userId: '',
					pageNum: 0,
					pageSize: 5
				},
				pageMarkMessageList: [], //本页面储存的markList
				showExaminePopup: 0,
				focusContentRecord: {},
				focusContentListForm: {
					userId: '',
					schoolId: null,
					queryTime: null,
					pageSize: 10
				},
				focusUserContentList: [],
				focusModalName:null,
				focusLoadText: {
					loadmore: '点击加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				focusLoadStatus: 'loadmore',
			};
		},
		computed: {
			...mapState('user', ['userInfos', 'currentSchoolState', 'isLocalUser', 'currentSchoolId']),
			...mapState('remind', ['admireCount', 'followCount', 'replyCount', 'msgCount']),
			...mapState('remindWSS', ['webSocketTask', 'markMessageList', 'markState', 'pageNum']),
			...mapState('popup', ['focusSearchTimes']),
		},
		methods: {
			...mapMutations('remindWSS', {
				webSocketTaskInit: 'webSocketTaskInit',
				addPageNum: 'addPageNum',
				addPostToMarkMessageList: 'addPostToMarkMessageList',
				delPostInMarkMessageList: 'delPostInMarkMessageList',
				changeMarkMessageReadState: 'changeMarkMessageReadState',
				setMarkMessageList: 'setMarkMessageList',
				addFocusPostToMarkMessageList: 'addFocusPostToMarkMessageList'
			}),
			...mapMutations('content', {
				setMarkState: 'setMarkState',
			}),
			...mapMutations('popup', {
				increaseFocusSearchTimes: 'increaseFocusSearchTimes',
			}),
			toNav(index) {
				if (this.currentSchoolState == 3 && this.isLocalUser == 1) {
					console.log(this.isLocalUser, this.currentSchoolState)
					this.modalName = null
					uni.showToast({
						icon: 'none',
						title: '请认证后查看'
					})
					this.showExaminePopup = 1
					return
				}
				uni.navigateTo({
					url: '/package_task/pages/bbs/message/list?type=' + index
				});
			},
			tostring(value) {
				return JSON.stringify(value);
			},
			onPressChat(userInfo) {
				console.log(userInfo)
				this.showChatSheet = true;
				this.chatType = userInfo.returnType;
				this.chaterId = userInfo.userId;
				this.orderId = userInfo.orderId;
			},
			onSheeChat() {
				let that = this
				uni.showModal({
					title: '将清空所有对话记录，确认删除该对话吗？',
					success() {
						console.log(that.chatType)
						console.log(that.chaterId)
						console.log(that.userInfos.userId)
						// #ifdef MP-WEIXIN
						let request = {
							"requestType": "deleteChat",
							"chaterId": that.chaterId,
							"chatType": that.chatType,
							"orderId": that.orderId
						}
						that.webSocketTask.send({
							data: JSON.stringify(request)
						});
						// #endif
					}
				})
			},
			clearChat(item, index) {
				console.log('对话目标：' + item.userInfo.userId)
				//当前页面的该对话框置0
				this.chatList[index].unReadNum = 0;

			},
			//h5写法
			buildWebSocket() {
				let that = this;
				//var url = 'ws://120.79.93.123:8080/chat/';
				var url = this.localData.webSocketUrl;
				that.webSocket = new WebSocket(url + this.userInfos.userId);

				that.webSocket.onopen = function(event) {
					console.log('Websocket连接创建。。。');
					console.log(that.webSocket.readyState)
					if (that.webSocket.readyState == 1 && that.chatList.length == 0) {
						const request = {
							"requestType": "messageList",
							"userId": that.userInfos.userId
						}
						that.webSocket.send(JSON.stringify(request));
					}
				}

				that.webSocket.onmessage = function(event) {
					const data = JSON.parse(event.data);
					console.log(data);
					let MsgList = [];
					if (data.code == 200 && data.data.length > 0) {
						if (data.data[0].returnType == 'messageList') {
							for (var i = 0; i < data.data.length; i++) {
								const chatMsg = {
									unReadNum: data.data[i].unReadNum,
									userInfo: {
										userId: data.data[i].userId,
										nickName: data.data[i].nickName,
										headimgUrl: data.data[i].headimgUrl
									},
									msg: {
										content: data.data[i].messageContent,
										createTime: data.data[i].createTime
									}
								};
								console.log(chatMsg)
								MsgList.push(chatMsg);
							}
						}
					}
					that.chatList = MsgList;
				}

				that.webSocket.onclose = function(e) {
					console.log('Websocket连接关闭。。。');
					console.log(e);
				}

				that.webSocket.onerror = function() {
					console.log('WebSocket连接异常');
				}
			},
			//跳转帖子
			toContent(markMessage) {

				let url = '/pages/post/detail?id=' + markMessage.contentId
				uni.navigateTo({
					url
				});
				// markMessage.isRead = 1;
				// this.editMarkReadState(markMessage.contentId)
			},
			//全部已读
			editAllRead() {
				for (var i = 0; i < this.markMessageList.length; i++) {
					this.markMessageList[i].isRead == 1
				}
				this.editMarkReadState("ALL")
			},
			// //改变马住消息已读状态
			async editMarkReadState(contentId) {
				const params = {
					contentId: contentId,
					userId: this.userInfos.userId
				}
				const data = await editMarkRead_1(params)
				console.log(data)
				if (data.code === 200) {
					console.log("马住已读", data.data)
					if (data.data.isRead == 1) {
						console.log("触发已读")
						this.changeMarkMessageReadState(contentId)
					}
				} else {
					console.log('触发已读失败');
				}
			},
			//取消追踪
			async deleteMark(markMessage) {
				let that = this
				const params = {
					isMark: 0, //1马住 0弃坑
					contentId: markMessage.contentId,
					userId: this.userInfos.userId
				}
				const data = await editMark_1(params)
				console.log(data)
				if (data.code === 200) {
					console.log("马住", data.data)
					this.setMarkState({
						'contentId': markMessage.contentId,
						'isMark': data.data.isMark
					})
					if (data.data.isMark == 0) {
						console.log("触发弃坑")
						this.delPostInMarkMessageList(markMessage)
					}
				} else {
					console.log('弃坑失败');
				}
			},
			loadMore() {
				if (this.loadStatus == 'loadmore') {
					this.addPageNum()
					if (this.webSocketTask.readyState == 1) {
						const request = {
							"requestType": "getMarkMessageList",
							"userId": uni.getStorageSync('userInfos').userId,
							"pageNum": this.pageNum,
							"pageSize": this.pageSize,
						}
						console.log(JSON.stringify(request));
						this.webSocketTask.send({
							data: JSON.stringify(request)
						});
					} else {
						console.log('打印连接状态', this.webSocketTask.readyState);
					}
				}
			},
			//按页获取mark记录列表
			async getMarkMessageList() {
				let that = this
				this.markListForm.userId = this.userInfos.userId
				this.markListForm.pageNum = this.markListForm.pageNum + 1
				console.log("触发", this.markListForm)
				const data = await getMarkMessageList_1(this.markListForm)
				if (data.code === 200) {
					console.log(data)
					this.pageMarkMessageList.push(...data.data.markMessageList)
					this.setMarkMessageList({
						markMessageList: this.pageMarkMessageList,
						noReadCount: data.data.noReadCount
					})
					if (data.data.markMessageList && data.data.markMessageList.length > 0) {
						this.loadStatus = 'loadmore'
					} else {
						this.loadStatus = 'nomore'
					}
					if (this.markListForm.pageNum == 1) {
						this.getFocusContentRecord()
					}
				}
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			loadMore() {
				this.getMarkMessageList()
			},
			async getFocusContentRecord() {
				let that = this
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const data = await searchFocusContentRecords(params)
				if (data.code === 200) {
					console.log(data)
					this.focusContentRecord = data.data;
					// console.log("触发新增吗", data.data.latestContentTime)
					if (data.data.latestContentTime) {
						// console.log("触发新增", data.data)
						this.addFocusPostToMarkMessageList(data.data)
						// this.increaseFocusSearchTimes()
					}
				}
			},
			async getFocusContentPage(item) {
				let that = this
				this.focusContentListForm.schoolId = this.currentSchoolId;
				this.focusContentListForm.userId = this.userInfos.userId;
				let now = new Date(); // 获取当前时间
				if (this.focusUserContentList && this.focusUserContentList.length>0) {
					now = new Date(this.focusUserContentList[this.focusUserContentList.length - 1].createTime)
				}
				const year = now.getFullYear(); // 年份
				const month = now.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
				const day = now.getDate(); // 日期
				const hour = now.getHours(); // 小时
				const minute = now.getMinutes(); // 分钟
				const second = now.getSeconds(); // 秒钟
				this.focusContentListForm.queryTime = year + '/' + month + '/' + day + ' ' + hour + ':' + minute +
					':' +
					second;
				const data = await getFocusContentList(this.focusContentListForm)
				if (data.code === 200) {
					console.log(data)
					item.isRead=1
					if (data.data.length > 0) {
						console.log("modal框")
						this.focusUserContentList.push(...data.data)
						this.focusModalName="DialogModal"
						this.focusLoadStatus='loadmore'
					}else {
						this.focusLoadStatus = 'nomore'
					}
					
				}
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				updateFocusSearchTime(params)
			},
			editLikeState(value) {
				console.log("触发")
				for (var i = 0; i < this.focusUserContentList.length; i++) {
					if (this.focusUserContentList[i].contentId == value.contentId) {
						this.focusUserContentList[i].isLike = value.isLike
						if (value.isLike == 1) {
							this.focusUserContentList[i].upNum += 1
						} else if (value.isLike == 0) {
							this.focusUserContentList[i].upNum -= 1
						}
					}
				}
			},
			editMarkState(value) {
				console.log("触发")
				for (var i = 0; i < this.focusUserContentList.length; i++) {
					if (this.focusUserContentList[i].contentId == value.contentId) {
						this.focusUserContentList[i].isMark = value.isMark
			
					}
				}
			},
			editDeletePost(value) {
				// console.log("删除帖子",value)
				this.focusUserContentList = this.focusUserContentList.filter(item => item.contentId !== value.contentId);
			},
			hideModal() {
				this.focusModalName = null
				this.focusUserContentList = []
			},
			onShareTimeline() {
				return {
					title: '谈校间',
				};
			},
			onShareAppMessage(res) {
				console.log("触发")
				return {
					title: '谈校间',
					path: '/pages/index/index'
				};
			},
		},
		onReachBottom() {
			console.log('当前页面', this.pageNum)
			if (this.loadStatus == 'loadmore') {
				this.loadMore()
			}
		},

	};
</script>

<style>
	page {
		background-color: #F5F5F5;
	}
</style>
<style lang="scss" scoped>
	.nav-icon {
		width: 90rpx;
		height: 90rpx;
		margin-bottom: 10rpx;
	}

	.msg-wrap {
		// background-color: #FFFFFF;
		margin-top: 20rpx;

		.title {
			// margin: 20rpx;
			padding: 20rpx;
		}

		.msg-item {
			padding: 20rpx;
			display: flex;

			.avatar-box {
				position: relative;

				.avatar {
					margin-right: 20rpx;

				}
			}

			.right {
				display: flex;
				flex-direction: column;
				flex: 1;

				.desc {
					font-size: 24rpx;
					color: #999;
					display: flex;

					.time {
						white-space: nowrap;
						margin-left: auto;
					}
				}
			}
		}
	}

	// 
	.msg-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		min-height: 800rpx;

		.img {
			width: 200rpx;
			margin-top: 100rpx;
		}

		.txt {
			color: #999;
			font-size: 24rpx;
			margin-top: 20rpx;
		}
	}
	
	.modal-content {
		//top: -26px;
		//height: 80%;
		overflow: auto;
		text-align: left;
	}
</style>