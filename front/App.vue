<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		userLoginWX,
		code2SessionWX,
		getAccessTokenWX,
		getReminds,
		getUserStateBySchool,
		getOneBbsConfig,
		getMarkMessageList_1,
		reqEditUserInfos,
		addUserLoginRecord,
		searchFocusContentRecords
	} from '@/api/index.js'
	import localData from './utils/data.js'
	import {
		localRequest
	} from "@/api/request.js";
	export default {
		globalData: {
			launched: false,
			currentSchoolList: [],
			launchChangeSchoolEnd: true,
			isMaintenance: false, // 默认关闭维护模式
		},
		async onLaunch() {
			// #ifdef MP-WEIXIN
			this.globalData.launched = true;
			this.globalData.launchChangeSchoolEnd = false;
			//清理之前缓存到本地的图片信息
			this.clearCacheImages()
			//确定是否有默认学校ID
			//单校区启动
			if (this.currentSchoolId != null) {
				uni.setStorageSync('schoolId', this.currentSchoolId * 1);
				// 启动小程序时通过openId获取用户信息
				await this.firstGetUserInfo()
				//进行隐私授权判断
				await this.judgePrivacySetting()
				await this.getSchoolBasicConfig()
				await this.getCloudConfig()
			}
			//多校区启动
			else {
				//启动参数
				let launchOptions = uni.getLaunchOptionsSync()
				//如果本地缓存和页面参数中均不存在shcoolId，跳转选择学校页面
				if (!uni.getStorageSync('schoolId') && !launchOptions.query.currentSchoolId) {
					// 启动小程序时通过openId获取用户信息
					await this.firstGetUserInfo()
					//进行隐私授权判断
					await this.judgePrivacySetting()
					this.globalData.launchChangeSchoolEnd = true;
					return
				}

				// 启动小程序时通过openId获取用户信息
				await this.firstGetUserInfo()
				//进行隐私授权判断
				await this.judgePrivacySetting()
				//获取配置信息，当stroeage中学校id不存在是时不请求
				this.getCurrentSchoolId(uni.getStorageSync('schoolId'));
				await this.getSchoolBasicConfig()
				console.log("基础配置", this.basicPlateId)
				await this.getCloudConfig()
				this.globalData.launchChangeSchoolEnd = true;
			}
			// #endif
		},
		onLoad() {},
		async onShow() {
			console.log('App Show')
			// #ifdef MP-WEIXIN
			// 启动小程序的时候获取通知公告(其他用户的点赞或者评论)
			this.getReminds()
			this.getMarkMessageList()
			//如果storage中的schoolId为String类型，改为number类型
			let schoolIdInStorage = uni.getStorageSync('schoolId')
			if (typeof schoolIdInStorage === 'string') {
				let numberValue = schoolIdInStorage * 1;
				// 存储转换后的数字类型到本地存储
				uni.setStorageSync('schoolId', numberValue);
			}
			if (this.globalData.launched) {
				this.globalData.launched = false
				// console.log("onlaunch触发")
			} else {
				let launchOptions = uni.getLaunchOptionsSync()
				this.insertUserLoginRecord(launchOptions)
			}
			// #endif


		},
		onHide: function() {
			console.log('App Hide')

		},
		computed: {
			...mapState('user', ['userInfos', 'userKey', 'currentSchoolId', 'currentUserState', 'basicPlateId',
				'currentSchoolState', 'shouldWatchCurrenSchoolId'
			]),
			...mapState('remind', ['admireCount', 'followCount', 'replyCount']),
			...mapState('config', ['Adv', 'alumniOnly', 'firstPage', 'addPost', 'userFirstLogin']),
			...mapState('remindWSS', ['webSocketTask']),
		},
		methods: {
			...mapMutations('user', {
				getUserInfos: 'getUserInfos',
				saveUserKey: 'saveUserKey',
				setCurrentUserState: 'setCurrentUserState',
				getCurrentSchoolId: 'getCurrentSchoolId',
				setCurrentUserType: 'setCurrentUserType',
				setBannedTime: 'setBannedTime',
				setCurrentSchoolState: 'setCurrentSchoolState'
			}),
			...mapMutations('remind', {
				setRemindCount: 'setRemindCount',
				setAdmireCount: 'setAdmireCount',
				setFollowCount: 'setFollowCount',
				setReplyCount: 'setReplyCount',
			}),
			...mapMutations('config', {
				setFirstPage: 'setFirstPage',
				setAdv: 'setAdv',
				setAlumniOnly: 'setAlumniOnly',
				setAddPost: 'setAddPost',
				setUserFirstLogin: 'setUserFirstLogin',
				setJudgeShowPopup: 'setJudgeShowPopup',
				setInformConfig: 'setInformConfig'

			}),
			...mapMutations('remindWSS', {
				webSocketTaskInit: 'webSocketTaskInit',
				setMarkMessageList: 'setMarkMessageList'
			}),
			async getUserStateBySchool(userId) {
				const params = {
					userId: userId,
					schoolId: this.currentSchoolId
				}
				if (params.userId == '' || params.userId == null) {
					console.log("userId缺失")
					return
				}
				if (params.schoolId == '' || params.schoolId == null) {
					console.log("schoolId缺失")
					return
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					//this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
					this.setCurrentUserType(stateData.data.userType)
					if (stateData.data.userType == 2 && stateData.data.bannedTime) {
						this.setBannedTime(stateData.data.bannedTime)
					}
					// console.log("用户状态", this.currentUserState,stateData.data)
				}
			},
			async getReminds() {
				const params = {
					userId: uni.getStorageSync('userInfos').userId,
					queryType: 'all',
					schoolId: this.currentSchoolId,
					pageNum: 1,
					pageSize: 5
				}
				const remindsData = await getReminds(params)
				if (!remindsData) {
					console.log('请求通知失败！');
					this.judgeMaintenanceState()
					return
				}
				if (remindsData.code === 200) {
					// 获取通知数量
					this.setRemindCount(remindsData.data.admireCount + remindsData.data.followCount + remindsData.data
						.replyCount)
					this.setAdmireCount(remindsData.data.admireCount)
					this.setFollowCount(remindsData.data.followCount)
					this.setReplyCount(remindsData.data.replyCount)
					console.log('通知数：' + this.msgCount)


				} else {
					console.log('请求通知失败！');
					this.judgeMaintenanceState()
				}
			},
			async firstGetUserInfo() {
				uni.login({
					provider: 'weixin',
					success: loginRes => {
						let code = loginRes.code;
						console.log('loginRes', code);
						code2SessionWX({
							"code": code,
							"APPID": localData.appId
						}).then(async sessionData => {
							// 网络请求成功，获取到openId
							if (sessionData.code === 200) {
								this.userKey.sessionKey = sessionData.data.session_key
								this.userKey.openId = sessionData.data.openid
								this.userKey.sessionData = sessionData
								console.log(this.userKey)
								console.log('微信登录');

								// 每次启动从服务器拉取最新用户数据
								if (this.userKey.sessionData.code === 200) {
									// 请求用户信息
									const data = await userLoginWX({
										'openId': this.userKey.openId,


									})
									if (data.code === 200) {
										let userInfo = {
											...data.data
										}
										if (userInfo.introduction == null) {
											userInfo.introduction = ''
										}
										userInfo.unionId =
											this.userKey.sessionData.data.unionid
										this.getUserInfos(userInfo)
										uni.setStorageSync('userInfos', userInfo)
										console.log(this.userInfos)
										//获取认证状态
										this.getUserStateBySchool(this.userInfos.userId)
										//用户登录
										let launchOptions = uni.getLaunchOptionsSync()
										this.insertUserLoginRecord(launchOptions)
									}
								} else {
									console.log(this.userKey.sessionData.code);
									uni.showModal({
										title: "服务器错误"
									})
									console.log('获取opendid失败');
								}
							} else {
								uni.showModal({
									title: "微信登录失败"
								})
							}
						})
					}
				})

			},
			async getCloudConfig() {
				let that = this
				let UserId = '';
				if (uni.getStorageSync('userInfos').userId != null &&
					uni.getStorageSync('userInfos').userId != '') {
					UserId = uni.getStorageSync('userInfos').userId
				}
				console.log('启动项', this.userInfos.userId)
				//获取启动项配置
				let param = {
					schoolId: this.currentSchoolId,
					configType: 'functionConfig'
				}
				let data = await getOneBbsConfig(param)
				if (data.code == 200) {
					let configList = JSON.parse(data.data.configJson).data
					console.log("启动时总配置项（数据库）", configList)
					if (configList.length > 0) {
						for (var i = 0; i < configList.length; i++) {
							//广告位置
							if (configList[i].function_name == 'Adv') {
								let AdvConfig = configList[i]
								this.setAdv(AdvConfig);
							}
							//首页
							else if (configList[i].function_name == 'firstPage') {
								let firstPageConfig = configList[i]
								this.setFirstPage(firstPageConfig);
							}
							//校友认证（发帖评论）
							else if (configList[i].function_name == 'alumniOnly') {
								let alumniOnlyConfig = configList[i]
								this.setAlumniOnly(alumniOnlyConfig);

							}
							//新建话题
							else if (configList[i].function_name == 'addTopic') {
								let addTopicConfig = configList[i]
								this.setAddPost(addTopicConfig);
							}
							//匿名
							else if (configList[i].function_name == 'chatGPT') {
								let anonymousConfig = configList[i]
								this.setAddPost(anonymousConfig);
							}
							//显示弹窗
							else if (configList[i].function_name == 'judgeShowPopup') {
								let showPopupConfig = configList[i]
								this.setJudgeShowPopup(showPopupConfig)
							}
							//举报配置
							else if (configList[i].function_name == 'informConfig') {
								let informConfig = configList[i]
								this.setInformConfig(informConfig)
							} else {

							}
						}
					}
				} else {
					console.log("启动项配置获取失败");
				}

			},
			//按页获取mark记录列表
			async getMarkMessageList() {
				let that = this
				const params = {
					userId: uni.getStorageSync('userInfos').userId,
					pageNum: -1,
					pageSize: 10
				}
				const data = await getMarkMessageList_1(params)
				if (data.code === 200) {
					console.log(data)
					this.setMarkMessageList({
						markMessageList: data.data.markMessageList,
						noReadCount: data.data.noReadCount
					})
				}
			},
			async getFocusContentRecord() {
				let that = this
				const params = {
					userId: uni.getStorageSync('userInfos').userId,
					schoolId:this.currentSchoolId
				}
				const data = await searchFocusContentRecords(params)
				if (data.code === 200) {
					console.log(data)
				}
			},
			//清理上次缓存的图片信息
			clearCacheImages() {
				const fsm = wx.getFileSystemManager();
				fsm.readdir({ // 获取文件列表
					dirPath: wx.env.USER_DATA_PATH,
					success(res) {
						// console.log("目标目录文件", res)
						let advImageCount = res.files.filter(file => file.includes("advImage")).length;
						let avatarCount = res.files.filter(file => file.includes("non-anonymousAvatar")).length;
						let postImageCount = res.files.filter(file => file.includes("postImage")).length;
						console.log('缓存广告图片数：', advImageCount, '非匿名头像：', avatarCount, "帖子图片数量：", postImageCount)
						res.files.forEach((el) => {
							let name = (wx.env.USER_DATA_PATH + el).replace(/usr/g, "usr/");
							//图片缓存清除
							if ((advImageCount >= 10 && name.includes("advImage")) || (avatarCount >= 30 &&
									name.includes("non-anonymousAvatar")) || (postImageCount >= 30 && name
									.includes("postImage"))) {
								// console.log(name)
								//删除时要注意文件名一定要和存的时候一样,不然会报没有unlink无文件权限问题
								fsm.unlink({
									filePath: name,
									fail(e) {
										console.log('readdir文件删除失败：', e)
									},
									success(succ) {
										// console.log('readdir文件删除成功：', succ)
									}
								})
							}

							// name.includes("homeContent")||
						})
					}
				})
			},
			//替换用户过大的头像
			replaceOversizedAvatars() {
				let that = this
				//判断用户头像是否过大，过大压缩
				//是否替换过
				if (uni.getStorageSync('replaceAvatars') == 1) {
					console.log("已经替换过")
					return
				}
				//未替换过
				else {
					//获取网络图片
					uni.getImageInfo({
						src: that.userInfos.headimgUrl,
						success: (res) => {
							console.log("uniapp", res)
							// 获取头像大小
							const fs = wx.getFileSystemManager();
							fs.getFileInfo({
								filePath: res.path,
								success: async function(info) {
									console.log('文件大小', info.size) //204800
									//头像大于200k
									if (info.size > 204800) {
										//微信官方压缩方法(二次压缩)
										const compressResult = await localData
											.compressionIamgeWXTwice(this, res.path, 200);
										// console.log(compressResult)
										// 将头像上传到阿里云oss，返回新图像的url
										let headimgUrl = await that.uploadImageOSS(compressResult,
											'user/headImg/', that.currentSchoolId)
										headimgUrl = headimgUrl.replace(
											'keming-bbs.oss-cn-shanghai.aliyuncs.com',
											'image.tanxiaojian.zone')
										// 用新的图像url发送更新头像的请求
										const data = await reqEditUserInfos({
											userId: that.userInfos.userId,
											headimgUrl: headimgUrl
										})
										if (data.code === 200) {
											console.log("修改成功")
											//在storage中做标识
											uni.setStorageSync('replaceAvatars', 1 * 1)
										} else {
											console.log('获取信息失败');
										}
									}
									//头像小于200k
									else {
										//在storage中做标识
										uni.setStorageSync('replaceAvatars', 1 * 1)
									}
								},
								fail(err) {
									console.log('获取失败')
								}
							})

						},
						fail: (err) => {
							//获取图片信息失败，返回图片网络地址
							console.log("网络图片信息获取失败", err)
						}
					})
				}
			},
			//新增用户登录记录
			async insertUserLoginRecord(launchOptions) {
				//拼合链接
				let queryString = ''
				let LoginPath = ''
				if (Object.keys(launchOptions.query).length != 0) {
					// 将query对象转换为查询字符串
					queryString = Object.keys(launchOptions.query).map(key => `${key}=${launchOptions.query[key]}`)
						.join('&');
					LoginPath = launchOptions.path + '?' + queryString;
				} else {
					LoginPath = launchOptions.path
				}
				// console.log('启动参数',LoginPath,this.userInfos.userId,this.currentSchoolId)
				let form = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId,
					loginPath: LoginPath
				}
				if (form.userId == '' || form.userId == null) {
					console.log("userId缺失")
					return
				}
				if (form.schoolId == '' || form.schoolId == null) {
					console.log("schoolId缺失")
					return
				}
				const data = await addUserLoginRecord(form)
				if (data.code === 200) {
					console.log("新增登录记录成功", data)
				} else {
					console.log('新增登录记录失败');
				}
			},
			toMaintenancePage(){
				uni.redirectTo({
					url:'/package_task/pages/bbs/more/maintenance'
				})
			},
			async judgeMaintenanceState(){
				let param = {
					schoolId: this.currentSchoolId,
					configType: 'basicConfig'
				}
				if (!this.currentSchoolId || this.currentSchoolId == 0) {
					uni.redirectTo({
						url: '/package_task/pages/bbs/more/maintenance'
					})
					return
				}
				let data = await getOneBbsConfig(param)
				if (!data) {
					uni.redirectTo({
						url: '/package_task/pages/bbs/more/maintenance'
					})
				}
				if (data.code == 200) {
					let configList = JSON.parse(data.data.configJson).data
					for (var i = 0; i < configList.length; i++) {
						// console.log(configList[i].schoolId*1)
						if (configList[i].schoolId * 1 == this.currentSchoolId) {
							//维护状态判断
							console.log(configList[i])
							// 如果系统维护状态为关闭，直接返回 false
							if (configList[i].maintenanceStatus == 0) {
				
							} else if (configList[i].maintenanceStatus == 1) {
								const currentTime = new Date().toLocaleTimeString('en-GB', { hour12: false }); // HH:mm:ss
								const current = new Date(`1970-01-01T${currentTime}`);
								const start = new Date(`1970-01-01T${configList[i].maintenanceStart}`);
								const end = new Date(`1970-01-01T${configList[i].maintenanceEnd}`);
								if (current >= start && current <= end) {
									uni.redirectTo({
										url: '/package_task/pages/bbs/more/maintenance'
									})
								}
							}
						}
					}
				
				} else {
					console.log("基础配置获取失败");
					uni.redirectTo({
						url: '/package_task/pages/bbs/more/maintenance'
					})
				}
			}
		},
		watch: {
			currentSchoolId: {
				async handler(newVal, oldVal) {
					if (this.shouldWatchCurrenSchoolId && this.globalData.launchChangeSchoolEnd) {
						let school = {}
						for (let i = 0; i < this.globalData.currentSchoolList.length; i++) {
							if (this.globalData.currentSchoolList[i].schoolId == newVal) {
								school = this.globalData.currentSchoolList[i]
							}
						}
						if ( (school.state !=="" && school.state !==null ) && (school.state === 3 || school.state!==this.currentSchoolState) ) {
							if ((school.state == 0 || school.state == 6) && !school.baseUrl) {
								for (let i = 0; i < this.schoolList.length; i++) {
									if (this.schoolList[i].state == 0 && this.schoolList[i].baseUrl) {
										school.baseUrl = this.schoolList[i].baseUrl
										school.appId = this.schoolList[i].appId
										break;
									}
								}
							}
							this.changeBaseUrlBySchoolId(school)
							this.setCurrentSchoolState(school.state)
							await this.firstGetUserInfo()
						}
						else{
						}
					}
				}
			}
		}
	}
</script>

<style>
	/*每个页面公共css */
	@import "colorui/main.css";
	@import "colorui/icon.css";
</style>

<style lang="scss">
	@import "uview-ui/index.scss";
	@import "static/css/iconfont.css";

	page {
		font-size: 32rpx;
		line-height: 1.7;
	}

	.container {
		padding: 20rpx;
		overflow: hidden;
	}

	// 发布按钮
	.plus-box {
		background-color: $themes-color;
		width: 110rpx;
		height: 110rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		position: fixed;
		bottom: 60rpx;
		right: 30rpx;
		z-index: 999;
		box-shadow: 0 0 10rpx #333;
	}

	.plus-box:active {
		background-color: #999;
	}

	.f-wrap {
		padding: 20rpx;
		border-radius: 10rpx;
		box-shadow: 5rpx 5rpx 20rpx #e6e6e6;
		background-color: #FFFFFF;
		margin-bottom: 20rpx;
	}

	.f-wrap>.title {
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	.f-fixed {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		padding: 20rpx 20rpx 50rpx 20rpx;
		z-index: 999;
		background-color: #fff;
	}

	/* 隐藏滚动条 */
	::-webkit-scrollbar {
		display: none;
		width: 0 !important;
		height: 0 !important;
		-webkit-appearance: none;
		background: transparent;
	}
</style>