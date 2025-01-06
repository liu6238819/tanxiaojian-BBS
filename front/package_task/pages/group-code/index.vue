<template>
	<view class="content">
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		<image :show-menu-by-longpress="true" :src="qrCode" mode="widthFix" :data-current="qrCode"
			:data-image="qrCodeList">
		</image>
		<!-- <image :src="`${pageCodeurl}`" mode="widthFix"></image> -->
		<view class="text-area">
			<!-- <text class="title">目前已邀请{{invitedNum}}人</text> -->
			<!-- <text class="title">截图识别二维码</text> -->
		</view>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import localData from '../../../utils/data.js';
	export default {
		data() {
			return {
				title: '',
				accessToken: '',
				configId: '', //]addJoinWay获取
				qrCode: '', //二维码url
				qrCodeList: [], //二维码列表 预览用
				unionId: '',
				memberList: [],
				invitedNum: 0,
				miniAppAccessToken: '',
				pageUrl: '',
				pageCodeurl: '',
				pageId: '',
				pageCodeLink: '',
				codeKind: 0, //传参二维码种类
			}
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
			...mapState('user', ['currentSchoolId']),
		},
		onLoad(options) {
			this.miniAppAccessToken = this.userKey.tokenWX
			this.unionId = this.userInfos.unionId
			this.codeKind = options.codeKind * 1
			this.getPageUrl()
			if (this.codeKind == 3) {
				this.blinBoxConfig('blindBox');
			} else if (this.codeKind == 4) {
				this.groupShoppingConfig('groupShopping')
			} else if (this.codeKind == 5) {
				this.clockInConfig('clockIn')
			}else if (this.codeKind == 6) {
				this.title = options.groupIntro
				this.qrCode = options.groupQrCode
				this.qrCodeList.push(this.qrCode)
			} else {
				this.aboutfunctionConfig('QRCode')
			}
			console.log(this.unionId)

		},
		methods: {
			getAccessToken() {
				uni.request({
					url: 'https://qyapi.weixin.qq.com/cgi-bin/gettoken',
					method: "GET",
					data: {
						corpid: 'ww45debfa9e67919a9',
						corpsecret: 'MaOnJQxPKlvStf4a9DPhE2yZ6zDSvAQRYaDhJFwkn90',
					},
					success: (res) => {
						this.accessToken = res.data.access_token
						console.log(this.accessToken);
						this.getInvitedNum()
						// this.addJoinWay()
					}
				})
			},
			//创建一个进入形式
			addJoinWay() {
				uni.request({
					url: 'https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/add_join_way?access_token=' +
						this.accessToken,
					method: "POST",
					data: {
						"scene": 2,
						"remark": "test",
						"auto_create_room": 1,
						"room_base_name": "南师大万能群",
						"room_base_id": 5,
						// "chat_id_list": [
						// 	"wrHwwpFQAApAjtQJ1xQb2BIyUJcafmqA",
						// 	"wrHwwpFQAAuYe8c6KFMf2qDLsnQpkF_g"
						// ],
						"chat_id_list": [
							"wrHwwpFQAAO3mYQVLfv4hhk42ranKrVA",
						],
						"state": this.unionId
					},
					success: (res) => {
						console.log(res);
						this.configId = res.data.config_id
						this.getJoinWay()
					}
				})
			},
			//获取进入形式
			getJoinWay() {
				uni.request({
					url: 'https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get_join_way?access_token=' +
						this.accessToken,
					method: "POST",
					data: {
						"config_id": this.configId
					},
					success: (res) => {
						this.qrCode = res.data.join_way.qr_code
						console.log(this.qrCode);
					}
				})
			},
			//获取邀请人数
			getInvitedNum() {
				uni.request({
					url: 'https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get?access_token=' +
						this.accessToken,
					method: "POST",
					data: {
						"chat_id": "wrHwwpFQAAO3mYQVLfv4hhk42ranKrVA",
						"need_name": 1
					},
					success: (res) => {
						console.log(res)
						this.memberList = res.data.group_chat.member_list
						for (var i = 0; i < this.memberList.length; i++) {
							if (this.memberList[i].state && this.memberList[i].state == this.userInfos
								.unionId) {
								this.invitedNum = this.invitedNum + 1
							}
						}
					}
				})
			},
			getPageUrl() {
				const pages = getCurrentPages();
				console.log(pages)
				const currentPage = pages[pages.length - 2];
				// console.log(currentPage)
				this.pageId = currentPage.options.id;
				this.pageUrl = currentPage.route;
				this.pageCodeLink = 'https://tanxiaojian.zone/content/detail?id=' + currentPage.options.id;
				console.log(this.pageUrl)
				// this.getWXPageCode()
			},
			//调用云函数获取页面小程序码
			async getWXPageCode() {
				let that = this
				// wx.cloud.init({
				// 	env: localData.envId,
				// 	traceUser: true,
				// })
				// wx.cloud.callFunction({
				await localData.cloud_shared.init()
				await localData.cloud_shared.callFunction({
					name: 'getWxacode', //云函数的名称
					data: {
						page: that.pageUrl,
						scene: that.pageId
					},
					complete: res => {
						console.log(res)
						this.pageCodeurl = 'data:image/png;base64,' + uni.arrayBufferToBase64(res.result
							.buffer);
						console.log(this.pageCodeurl);
					}

				})
			},
			//调佣云函数-功能配置-二维码
			async aboutfunctionConfig(functionName) {
				let that = this
				// wx.cloud.init({
				// 	env: localData.envId,
				// 	traceUser: true,
				// })
				// wx.cloud.callFunction({
				await localData.cloud_shared.init()
				await localData.cloud_shared.callFunction({
					name: 'functionConfigSQL', //云函数的名称
					data: {
						functionName: functionName, //调用哪个数据库函数
						schoolId: this.currentSchoolId, //前
						codeKind: this.codeKind,
						codeInfo: '',
						usingDatabase: localData.usingDatabase
					},
					complete: res => {
						let nowdata = res.result
						console.log(nowdata)
						this.title = nowdata.codeInfo.title
						this.qrCode = nowdata.codeInfo.codeUrl
						this.qrCodeList.push(this.qrCode)
					}
				})
			},
			previewImage(e) {
				uni.previewImage({
					current: e.currentTarget.dataset.current, // 当前显示图片的http链接
					urls: e.currentTarget.dataset.image // 需要预览的图片http链接列表
				});
			},
			//调佣云函数-功能配置-盲盒
			async blinBoxConfig(functionName) {
				let that = this
				// wx.cloud.init({
				// 	env: localData.envId,
				// 	traceUser: true,
				// })
				// wx.cloud.callFunction({
				await localData.cloud_shared.init()
				await localData.cloud_shared.callFunction({
					name: 'functionConfigSQL', //云函数的名称
					data: {
						functionName: functionName, //调用哪个数据库函数
						schoolId: this.currentSchoolId, //前
						blindBoxInfo: '',
						usingDatabase: localData.usingDatabase
					},
					complete: res => {
						let nowdata = res.result
						console.log(nowdata)
						if (nowdata) {
							this.title = nowdata.blindBoxInfo.groupIntro
							this.qrCode = nowdata.blindBoxInfo.groupQrCode
							this.qrCodeList.push(this.qrCode)
						}
					}
				})
			},
			//调佣云函数-功能配置-团购 (不再使用)
			groupShoppingConfig(functionName) {
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
							groupShoppingInfo: '',
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("配置项", nowdata)
							if (nowdata) {
								this.title = nowdata.groupShoppingInfo.groupIntro
								this.qrCode = nowdata.groupShoppingInfo.groupQrCode
								this.qrCodeList.push(this.qrCode)
							}
							uni.hideLoading();
							resolve(nowdata)
						}
					})
				})
			},
			//调佣云函数-功能配置-打开
			clockInConfig(functionName) {
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
							clockInInfo: '',
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("配置项", nowdata)
							if (nowdata) {
								this.title = nowdata.clockInInfo.group_intro
								this.qrCode = nowdata.clockInInfo.group_qrCode
								this.qrCodeList.push(this.qrCode)
							}
							uni.hideLoading();
							resolve(nowdata)
						}
					})
				})
			},
		},
	}
</script>

<style lang="scss" scoped>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
		width: 80%;
	}

	.title {
		font-size: 36rpx;
		color: #ff0000;
		font-weight: bold;
		text-align: center;
	}
</style>
