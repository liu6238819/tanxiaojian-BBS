//pro订阅消息模板
const templateId4='mCZ4_nAhvf0JLz6-t9lWeFb2iFWTzPg_WcS1V0Pda7Y' //认证模板ID
const templateId7='cmHgsiYL4mD88oojFPph2zaj7x46mU0Gjkfkj7admso' //评论模板ID
const templateId9='YaIxrQ_o6T6U0L7OkB5vo8uXov1VCUxddFtw6hTRXcI' //资讯更新模板ID
const templateId10='7O-zj0duYbitNdpYT6RXzUd8cByoFbFDjjzLMQ0XM7g' //内容更新模板ID

export default {	
	//pro
	// baseUrl: 'https://tanxiaojian.zone/BBSapi-NUFE/',
	// webSocketUrl: 'wss://tanxiaojian.zone/BBSapi-NUFE/remindWSS/',
	envId: 'bbs-nufe-4g4wwvwz301b3695',
	appId:'wx53ca9ca3a3bc4f65',
	newNickName:'圈友_',  //新用户昵称
	newHeadimgUrl:'/static/headImg/ngc.png', //新用户头像
	cloud_shared: new wx.cloud.Cloud({
		// 资源方 AppID
		resourceAppid: 'wxf162aa13e351a338',
		// 资源方环境 ID
		resourceEnv: 'bbs-nufe-4g4wwvwz301b3695',
	}),
	UIColor:'blue',
	// localBaseUrl: 'https://tanxiaojian.zone/BBSapi-NUFE/',
	// localWebSocketUrl: 'wss://tanxiaojian.zone/BBSapi-NUFE/remindWSS/',
	loacalAppId:'wx53ca9ca3a3bc4f65',


	//本地后台服务
	baseUrl: 'http://localhost:8085', //  localhost
	webSocketUrl: 'ws://localhost:8085/remindWSS/',
	localBaseUrl: 'http://localhost:8085',
	localWebSocketUrl: 'ws://localhost:8085/remindWSS/',

	
	//使用数据库：0测试数据库 ； 1正式数据库
	usingDatabase: 1,
	
	//切换学校按钮
	changeSchoolState:0,


	//订单种类图
	orderKindList: {
		searchManList: [{
				value: 0,
				label: '咨询答疑'
			},
			{
				value: 1,
				label: '游戏陪玩'
			},
			{
				value: 2,
				label: '其他寻人'
			},
		],
		runErrandsList: [{
				value: 10,
				label: '代取物品'
			},
			{
				value: 11,
				label: '代寄快递'
			},
			{
				value: 12,
				label: '代买物品'
			},
			{
				value: 13,
				label: '代办事情'
			},
			{
				value: 14,
				label: '其他跑腿'
			},
		],
		rentList: [{
				value: 20,
				label: '实物租赁'
			},
			{
				value: 21,
				label: '账号租赁'
			},
			{
				value: 22,
				label: '其他租赁'
			}
		],
		otherList: [{
			value: 100,
			label: '万能帮'
		}]
	},
	//订阅消息模板列表
	messageTemplateList: [{
			index: 1, //订单状态变更通知
			id: '-qXhicNBGxP1TdCxPXRdjkJMqoKwhPQ1GQ-_pKfwirc'
		},
		{
			index: 2, //订单状态提醒
			id: 'qKU3vN8oOr8sYte2RgDAtpXJJicnIL6R0d9O9_Hvi24'
		},
		{
			index: 3, //提现结果通知
			id: 'tUjOxIstCqkygcX-J8AtvM3DYzFRqFkDVuME_YTa9Jk'
		},
		{
			index: 4, //认证结果通知
			id: templateId4
			// id: 'puN-Dtnv-z8HroX3P6x47n1gOnk4AHzIxaDjwBGmuXQ'
		},
		{
			index: 5, //寻人选择通知
			id: '9NY3ogiBc6oYWPo7gNrSst7YQPiZho6LumT4Lq39a2w'
		},
		{
			index: 6, //活动通知
			id: '_ZC7c6foV8ujBiJ4YlOIyBlPd6lLeHBwGhwfGzKzLiY'
		},
		{
			index: 7, //评论通知
			id: templateId7
			// id: '686HoQfgbXxWJmsA4GleSKUydD_ipuBGZt6UKyH15rM'
		},
		{
			index: 8, //聊天通知
			id: 'MnpqAhLRnormOZDiMB5eQ40Rgq38tYrpCThXwqruChw'
		},
		{
			index: 9, //资讯更新通知
			id: templateId9
			// id: '5hVWMWf7q3cCrc9N5_zQPksFR09KupnCwm6a0HRn9og'
		},
		{
			index: 10, //内容更新通知
			id: templateId10
			// id: '_kzu6xFZh52bNX7EyWBPjIpuocsOF9H3NLmxkQ5W9A8'
		},
	],
	//帖子类型列表
	contentTypeList: [{
			type: '',
			name: "全部"
		},
		{
			type: 5,
			name: "分享吐槽"
		},
		{
			type: 6,
			name: "求助答疑"
		},
		{
			type: 7,
			name: "组队扩列"
		},
		{
			type: 8,
			name: "二手兼职"
		},
		{
			type: 1,
			name: "投票"
		}
	],
	//请求发送订阅消息
	requestMessage(IdArr) {
		console.log('参数', IdArr)
		let tempIds = []
		for (var i = 0; i < IdArr.length; i++) {
			for (var j = 0; j < this.messageTemplateList.length; j++) {
				if (this.messageTemplateList[j].index == IdArr[i]) {
					tempIds.push(this.messageTemplateList[j].id)
				}
			}
		}
		console.log('进入', tempIds)
		wx.requestSubscribeMessage({
			tmplIds: tempIds,
			success(res) {
				console.log('发送消息许可调用成功')
			},
			fail(res) {
				console.log(res)
			},
			complete(res) {
				console.log(res)
			}
		})
	},
	//根据orderKind显示订单种类
	getOrderKindName(orderKind) {
		let arr = []
		let name = ''
		if (orderKind == 100) {
			name = '万能帮'
		} else {
			if (orderKind > -1 && orderKind < 10) {
				arr = this.orderKindList.searchManList
			} else if (orderKind > 9 && orderKind < 20) {
				arr = this.orderKindList.runErrandsList
			} else {
				arr = this.orderKindList.rentList
			}
			for (var i = 0; i < arr.length; i++) {
				if (orderKind == arr[i].value) {
					name = arr[i].label
				}
			}
		}
		return name
	},
	//根据userId生成邀请码
	createInviteCode(userId) {
		let rds = this.randomString()
		let code = rds + userId
		code = code.slice(0, 15) + '_' + code.slice(15)
		return code;
	},
	//解码转换成userId
	deInviteCode(code) {
		let decode = code.replace('_', '')
		decode = decode.slice(6)
		return decode;
	},
	randomString() {
		let length = 6
		var str = '0123456789abcdefghijklmnopqrstuvwxyz';
		var result = '';
		for (var i = length; i > 0; --i)
			result += str[Math.floor(Math.random() * str.length)];
		return result;
	},

	compressionIamge(that, image) { // 等比例压缩图片 可指定图片宽高 兼容性：微信小程序端正常，其他端未测试
		console.log('图片压缩中', that)
		let result = null;
		const promise = new Promise((resolve, reject) => {
			uni.getImageInfo({
				src: image,
				success: async res => {
					let originHeight = res.height;
					let originWidth = res.width;
					let maxWidth = 730; // 最大宽
					let maxHeight = 130000; // 最大高
					let targetWidth = originWidth
					let targetHeight = originHeight;
					let needCompress = await this.getFileInfo(image)
					console.log('需要压缩？' + needCompress);
					if (originWidth > maxWidth || originHeight > maxHeight || needCompress) {
						if (originWidth / originHeight > maxWidth / maxHeight) {
							targetWidth = maxWidth;
							targetHeight = Math.round(maxWidth * (originHeight / originWidth));
						} else {
							targetHeight = maxHeight;
							targetWidth = Math.round(maxHeight * (originWidth / originHeight));
						}
					} else {
						//直接返回原图地址
						console.log('无需压缩！');
						resolve(image);
					}
					that.cWidth = targetWidth;
					that.cHeight = targetHeight

					if (targetWidth == targetHeight) {
						targetWidth = targetWidth + 20

					}


					let ctx = uni.createCanvasContext('canvas', that);
					ctx.clearRect(0, 0, targetWidth, targetHeight);
					ctx.drawImage(image, 0, 0, targetWidth, targetHeight);
					ctx.draw(false, setTimeout(() => {
						uni.canvasToTempFilePath({
							canvasId: 'canvas',
							quality: 0.8,
							fileType: 'jpg',
							success: (res) => {
								console.log('压缩结果：');
								console.log('宽度：' + targetWidth + '高度：' +
									targetHeight);
								console.log(res)
								resolve(res.tempFilePath);
							},
							fail: (err) => {
								console.log(err);
								uni.showToast({
									mask: true,
									icon: 'none',
									title: '图片压缩失败，请重试'
								});
							},
							complete: () => {}
						}, that);
					}, 1000));
				}
			})
		});
		return promise.then(res => result = res);
	},

	compressionIamgeWX(that, image) { // 微信官方接口
		console.log('图片压缩中-微信方法')
		let result = null;
		// #ifdef MP-WEIXIN
		const promise = new Promise((resolve, reject) => {
			wx.compressImage({
				src: image, // 图片路径
				quality: 50, // 压缩质量
				fail: function() {
					console.log('压缩失败')

				},
				success: async function(res) {
					console.log('压缩成功')
					resolve(res.tempFilePath);
				}
			});
		});
		return promise.then(res => result = res);
		//#endif
		return image
	},
	
	//24.3.22 使用微信压缩方法，第一次，质量0.8，限宽730，若达不到大小要求在此基础上再压缩一次，质量0.8
	compressionIamgeWXTwice(that, image, size) { // 微信官方接口
		console.log('图片压缩中-微信方法')
		let result = null;
		// #ifdef MP-WEIXIN
		const promise = new Promise((resolve, reject) => {
			wx.compressImage({
				src: image, // 图片路径
				quality: 80, // 压缩质量
				compressedWidth: 730,
				fail: function() {
					console.log('压缩失败')
	
				},
				success: async function(res) {
					console.log('压缩成功', res)
					//获取压缩后图片大小
					const fs = wx.getFileSystemManager();
					fs.getFileInfo({
						filePath: res.tempFilePath,
						success(info) {
							console.log('文件大小', info.size)
							//如果压缩后图片大于指定大小，再压缩一次
							if (info.size >= size * 1024) {
								wx.compressImage({
									src: res.tempFilePath, // 图片路径
									quality: 80, // 压缩质量
									fail: function() {
										console.log('压缩失败')
									},
									success: async function(twiceRes) {
										console.log('二次压缩成功')
										resolve(twiceRes.tempFilePath);
									}
								});
							}
							//返回图片地址
							else {
								console.log('一次压缩成功')
								resolve(res.tempFilePath);
							}
						},
						fail(err) {
							console.log('压缩失败')
						}
					})
	
				}
			});
		});
		return promise.then(res => result = res);
		//#endif
		return image
	},
	
	getFileInfo(image_path) {
		return new Promise((resolve, reject) => {
			uni.getFileInfo({
				filePath: image_path,
				success: (res) => {
					console.log('图片大小：' + res.size)
					if (res.size > Number(384000)) {
						// console.log(res.size)
						resolve(1)
					} else {
						// console.log(res.size)
						resolve(0)
						// console.log(image_path)
					}
				},
				fail: (err) => {
					console.log(err)
				}
			})
		})
	},
	//调用云函数-功能配置-是否需要校友认证
	async aboutAlumniOnly(FunctionName, SchoolId) {
		//return 1;
		let that = this
		await this.cloud_shared.init()
		return new Promise((resolve, reject) => {
		// wx.cloud.init({
		// 	env: this.envId,
		// 	traceUser: true,
		// })
		// wx.cloud.callFunction({
		this.cloud_shared.callFunction({
			name: 'functionConfigSQL', //云函数的名称
			data: {
				functionName: FunctionName, //调用哪个数据库函数
				schoolId:SchoolId,
				alumniInfo:'',
				usingDatabase:this.usingDatabase
			},
			success: res => {
				let nowdata = res.result.alumniInfo.state
				console.log(res)
				resolve(nowdata)
			},
			fail:res =>{
				resolve(1)
			}
		})

		})

	},
}
