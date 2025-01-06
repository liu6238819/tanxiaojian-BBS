import Vue from 'vue'
export default {
	namespaced: true,
	state: {
		webSocketTask: null,
		reminds: [], //存储其他用户点赞或者评论的消息通知
		chatList: [], //存储私信对话列表，数组元素结构如下
		chatDetailList: [], //存储当前对话详情
		msgCount: [0, 0, 0, 0, 0], //显示每个tabBar下的未读消息条数(红色的圆点)
		chatCount: 0, //未读私信的总数，需要单独计数
		admireCount: 0, //未读点赞的总数，需要单独计数
		followCount: 0, //未读关注的总数，需要单独计数
		replyCount: 0, //未读回复的总数，需要单独计数
	},
	actions: {},
	mutations: {
		webSocketTaskInit(state, webSocketUrl) {
			let reconNum = 0
			initWebSocket(state, webSocketUrl);

			function initWebSocket(state, webSocketUrl) {
				var url = webSocketUrl + uni.getStorageSync('userInfos').userId;
				console.log('webSocketUrl:' + url)
				if (state.webSocketTask) {
					console.log('已建立链接，close中')
					state.webSocketTask = null
				}
				state.webSocketTask = wx.connectSocket({
					url: url,
					header: {
						'content-type': 'application/json'
					},
				})
				console.log('web:', state.webSocketTask)

				state.webSocketTask.onOpen(function(event) {
					if (state.webSocketTask.readyState == 1) {
						console.log('Websocket连接创建。。。');
						const request = {
							"requestType": "messageList",
							"userId": uni.getStorageSync('userInfos').userId
						}
						console.log(JSON.stringify(request));
						state.webSocketTask.send({
							data: JSON.stringify(request)
						});
					} else {
						console.log('打印连接状态', state.webSocketTask.readyState);
					}
				})

				state.webSocketTask.onMessage(function(event) {
					console.log('消息处理');
					const data = JSON.parse(event.data);
					let msgList = [];
					let newChatCount = 0;
					if (data.code == 200) {
						if (data.returnType == 'messageList') {
							for (var i = 0; i < data.data.length; i++) {
								newChatCount += data.data[i].unReadNum;
								const chatMsg = {
									//使用userid+发言时间作为前端v-for渲染时的唯一key
									keyForFront: data.data[i].userId + data.data[i].createTime,
									unReadNum: data.data[i].unReadNum,
									userInfo: {
										userId: data.data[i].userId,
										nickName: data.data[i].nickName,
										headimgUrl: data.data[i].headimgUrl,
										returnType: data.data[i].returnType,
										orderId: data.data[i].orderId,
										orderKind: data.data[i].orderKind,
										isOpen: data.data[i].isOpen,
										userState: data.data[i].userState
									},
									msg: {
										content: data.data[i].messageContent,
										createTime: data.data[i].createTime
									}
								};
								msgList.push(chatMsg);
							}
							Vue.set(state, 'chatList', msgList)
							console.log(msgList)
							//追加通知数量，减去旧私信数后，再加上最新的私信数
							let value = state.msgCount[3] - state.chatCount + newChatCount
							if (value < 0) value = 0
							Vue.set(state.msgCount, 3, value)
							//更新私信数
							state.chatCount = newChatCount
							console.log('总通知数：' + state.msgCount)
						}
						if (data.returnType == 'chatDetails' || data.returnType == 'taskChatDetails') {
							let temp = []
							temp = data.data
							Vue.set(state, 'chatDetailList', temp)
						}
					}
					if (data.returnType == 'newMessage') {
						let temp = [{
							senderId: data.senderId,
							messageContent: data.messageContent,
							createTime: data.createTime
						}]

						Vue.set(state, 'chatDetailList', temp)

					}


				})

				state.webSocketTask.onClose(function() {
					console.log('Websocket连接关闭，自动重连中...');
					if (reconNum <= 3) {
						reconNum++
						console.log("尝试重连次数：", reconNum);
						//每5尝试一次重连
						setTimeout(function() {
							initWebSocket(state, webSocketUrl);
						}, 3000)

					} else if (reconNum == 4) {
						reconNum++
						uni.showToast({
							icon: 'none',
							title: '连接异常，请检查网络环境或重新进入小程序'
						})
					}
				})

				state.webSocketTask.onError(function() {
					console.log('WebSocket连接异常，自动重连中...');
					if (reconNum <= 3) {
						reconNum++
						console.log("尝试重连次数：", reconNum);
						//每5尝试一次重连
						setTimeout(function() {
							initWebSocket(state, webSocketUrl);
						}, 3000)

					} else if (reconNum == 4) {
						reconNum++
						uni.showToast({
							icon: 'none',
							title: '连接异常，请检查网络环境或重新进入小程序'
						})
					}
				})
			}


		},
		setReminds(state, value) {
			if (value < 0) value = 0
			state.reminds = value
		},
		setChatList(state, value) {
			Vue.set(state, 'chatList', value)
			//state.chatList = value
		},
		setChatDetailList(state, value) {
			Vue.set(state, 'chatDetailList', value)
			//state.chatList = value
		},
		setRemindCount(state, value) {
			if (value < 0) value = 0
			Vue.set(state.msgCount, 3, value) //数组/复杂对象用set
			//state.msgCount[3] = value
		},
		setChatCount(state, value) {
			if (value < 0) value = 0
			state.chatCount = value
		},
		setAdmireCount(state, value) {
			if (value < 0) value = 0
			state.admireCount = value
		},
		setFollowCount(state, value) {
			if (value < 0) value = 0
			state.followCount = value
		},
		setReplyCount(state, value) {
			if (value < 0) value = 0
			state.replyCount = value
		},
		reduceMsgCount(state) {
			let value = state.msgCount[3] - 1
			if (value < 0) value = 0
			Vue.set(state.msgCount, 3, value)
		},
		clearChatCount(state, value) { //对话框点击后消息清空
			let count = state.msgCount[3] - value
			Vue.set(state.msgCount, 3, count)
		},

	},
	getters: {

	},
}
