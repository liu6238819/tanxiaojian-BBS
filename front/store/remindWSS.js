import Vue from 'vue'
export default {
	namespaced: true,
	state: {
		webSocketTask: null,
		markMessageList: [],
		loadStatus: 'loadmore',
		pageNum: 1,
		markState: [0, 0, 0, 0, 0], //显示每个tabBar下的未读消息条数(红色的圆点)
		markNoReadCount: 0,
	},
	actions: {},
	mutations: {
		webSocketTaskInit(state, webSocketUrl) {
			let reconNum = 0
			let heartbeatTimer = null; //心跳检测定时器
			let reconnectTimer = null; // 断线重连定时器
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
							"requestType": "getMarkMessageList",
							"userId": uni.getStorageSync('userInfos').userId,
							"pageNum": 1,
							"pageSize": 10,
						}
						console.log(JSON.stringify(request));
						state.webSocketTask.send({
							data: JSON.stringify(request)
						});
						//先关闭旧的心跳检测，再开启新的
						heartbeatTimer && clearInterval(heartbeatTimer)
						reconnectTimer && clearInterval(reconnectTimer)
						const heartRequest = {
							"requestType": "heartBeat",
						}
						heartbeatTimer = setInterval(() => {
							//监听心跳消息是否发送成功，即链接是否正常
							return new Promise((resolve, reject) => {
								state.webSocketTask.send({
									data: JSON.stringify(heartRequest),
									success() {
										console.log('心跳消息发送成功')
										resolve(true)
									},
									fail(error) {
										console.log('心跳消息发送失败')
										//表示连接断开，重连
										heartbeatTimer && clearInterval(heartbeatTimer)
										reconnectTimer && clearInterval(reconnectTimer)
										// 5秒重连一次
										reconnectTimer = setTimeout(() => {
											initWebSocket(state, webSocketUrl);
										}, 5 * 1000)
										reject(error)
									}
								})
							})

						}, 30 * 1000)

					} else {
						console.log('打印连接状态', state.webSocketTask.readyState);
					}
				})

				state.webSocketTask.onMessage(function(event) {
					console.log('消息处理');
					const data = JSON.parse(event.data);
					console.log('返回数据', data)
					if (data.code == 200) {
						if (data.returnType == 'MarkMessageList') {
							//先复原pageNum
							state.pageNum = 1
							state.markMessageList = data.data
							console.log("马住消息列表", state.markMessageList)
							if (data.data.length < 10) {
								state.loadStatus = 'nomore'
							} else if (data.data.length == 10) {
								state.loadStatus = 'loadmore'
							}
							let value = 0
							for (let i = 0; i < state.markMessageList.length; i++) {
								if (state.markMessageList[i].isRead == 0) {
									value = 1;
									break;
								} else if (i < state.markMessageList.length - 1) {
									continue;
								} else {
									value = 0
								}
							}
							if (value < 0) value = 0
							Vue.set(state.markState, 3, value)
						}
						if (data.returnType == 'moreMarkMessageList') {
							state.markMessageList.push(...data.data)
							console.log("马住消息列表", state.markMessageList)
							if (data.data.length < 10) {
								state.loadStatus = 'nomore'
							} else if (data.data.length == 10) {
								state.loadStatus = 'loadmore'
							}
							let value = 0
							for (let i = 0; i < state.markMessageList.length; i++) {
								if (state.markMessageList[i].isRead == 0) {
									value = 1;
									break;
								} else if (i < state.markMessageList.length - 1) {
									continue;
								} else {
									value = 0
								}
							}
							if (value < 0) value = 0
							Vue.set(state.markState, 3, value)
						}
					}
				})

				state.webSocketTask.onClose(function() {
					//先关闭旧定时重连
					// heartbeatTimer && clearInterval(heartbeatTimer)
					reconnectTimer && clearInterval(reconnectTimer)
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
						console.log('连接异常，请检查网络环境或重新进入小程序')
					}
				})

				state.webSocketTask.onError(function() {
				})
			}


		},
		addPageNum(state) {
			state.pageNum = state.pageNum + 1
			//state.chatList = value
		},
		addFocusPostToMarkMessageList(state, value) {
			if (!state.markMessageList) {
				return
			}
			//没有mark列表中
			let markMessage = {}
			markMessage.type = "focus";
			markMessage.postNum = value.contentNum
			markMessage.isRead = 0
			markMessage.lastCommentTime = value.latestContentTime;
			console.log("新增", markMessage)
			state.markMessageList.unshift(markMessage)
			// 按 lastCommentTime 排序（从最近到最远）
			state.markMessageList.sort((a, b) => {
			    return new Date(b.lastCommentTime) - new Date(a.lastCommentTime);
			});
		},
		addPostToMarkMessageList(state, value) {
			if (!state.markMessageList) {
				return
			}
			let inTable = 0
			for (var i = 0; i < state.markMessageList.length; i++) {
				//已在mark列表中
				if (state.markMessageList[i].contentId == value.contentId) {
					state.markMessageList[i].isMark = 1;
					inTable = 1
					// console.log("在列表", state.markMessageList[i])
					break;
				}
			}
			//没有mark列表中
			if (inTable == -0) {
				let markMessage = {}
				markMessage.isRead = 1;
				markMessage.isMark = 1;
				markMessage.commentCount = 0;
				markMessage.contentId = value.contentId;
				markMessage.contentImage = value.contentUrl;
				markMessage.contentText = value.title + " " + value.contentText;
				markMessage.lastCommentTime = new Date(value.createTime).getTime();
				// console.log("新增", markMessage)
				state.markMessageList.unshift(markMessage)
			}
		},
		delPostInMarkMessageList(state, value) {
			if (!state.markMessageList) {
				return
			}
			for (var i = 0; i < state.markMessageList.length; i++) {
				if (state.markMessageList[i].contentId == value.contentId) {
					state.markMessageList[i].isMark = 0;
					break;
				}
			}
		},
		changeMarkMessageReadState(state, value) {
			//全部已读
			if (value == "ALL") {
				for (var i = 0; i < state.markMessageList.length; i++) {
					state.markMessageList[i].isRead = 1;

				}
				state.markNoReadCount = 0
				Vue.set(state.markState, 3, state.markNoReadCount)
			}
			//单个已读
			else {
				for (var i = 0; i < state.markMessageList.length; i++) {
					if (state.markMessageList[i].contentId == value) {
						state.markMessageList[i].isRead = 1;
						//同步更改未读数
						state.markNoReadCount = state.markNoReadCount - 1
						Vue.set(state.markState, 3, state.markNoReadCount)
						break;
					}
				}
			}
		},
		setMarkMessageList(state, value) {
			state.markMessageList = value.markMessageList
			state.markNoReadCount = value.noReadCount
			console.log("马住消息列表", state.markMessageList)
			Vue.set(state.markState, 3, state.markNoReadCount)

		}
	},
	getters: {

	},
}