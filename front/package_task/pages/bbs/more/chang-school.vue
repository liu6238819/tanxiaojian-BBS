<template>
	<view>
		<u-navbar
			style="background: #FFFFFF;box-shadow: 0px 2px 24px 0px rgba(156, 156, 156, 0.23);border-radius: 0px 0px 30px 30px;"
			title="选择学校" :borderBottom="false">
		</u-navbar>
		<view style="background-color: #FFFFFF; padding: 5rpx 30rpx 30rpx 30rpx;">
			<u-search placeholder="搜索我日常浏览的学校" v-model="searchText" @search="searchSchoolList" :show-action="false">
			</u-search>
		</view>
		<!-- 当前选择 -->
		<block v-if="!showSearchResult">
			<view v-if="currentSchool.schoolId">
				<view style="padding:20rpx 10rpx 10rpx 30rpx; color: gray; font-size: 24rpx; font-weight: bold;">当前选择
				</view>
				<view style="padding:10rpx 10rpx 0 10rpx;" @click="selectSchool(currentSchool)">
					<view class="cu-list">
						<view class="cu-item2 ">
							<!-- <view class="cu-avatar round lg" style="margin-right: 30rpx;"></view> -->
							<view style="display: flex;align-items: center; margin-right: 25rpx;">
								<u-avatar class="avatar" :src="currentSchool.schoolAddress"
									level-bg-color="#8072f3"></u-avatar>
							</view>
							<view class="">
								<view class="text-black text-bold">
									<!-- <text v-if="currentSchool.anotherName" class="text-cut">{{currentSchool.anotherName}}</text> -->
									<text class="text-cut">{{currentSchool.schoolName}}</text>
								</view>

							</view>
						</view>
					</view>
				</view>
			</view>
			<!-- 已开通院校 -->
			<view style="padding:20rpx 10rpx 10rpx 30rpx; color: gray; font-size: 24rpx; font-weight: bold;">已开通院校
			</view>
			<view style="padding:10rpx 10rpx 0 10rpx;" v-for="(school,index) in schoolList" :key="index"
				:id="'school-'+school.schoolId" @click="selectSchool(school)"
				v-if="school.schoolId!=currentSchool.schoolId">
				<view class="cu-list">
					<view class="cu-item2 ">
						<!-- <view class="cu-avatar round lg" style="margin-right: 30rpx;"></view> -->
						<view style="display: flex;align-items: center; margin-right: 25rpx;">
							<u-avatar class="avatar" :src="school.schoolAddress" level-bg-color="#8072f3"></u-avatar>
						</view>
						<view class="">
							<view class="text-black text-bold">
								<!-- <text v-if="school.anotherName" class="text-cut">{{school.anotherName}}</text> -->
								<text class="text-cut">{{school.schoolName}}</text>
							</view>

						</view>
					</view>
				</view>
			</view>
		</block>
		<block v-if="showSearchResult">
			<view class="padding-top-sm padding-lr-xs" v-for="(school,index) in searchSchools" :key="index"
				:id="'school-'+school.schoolId" @click="selectSchool(school)">
				<view class="cu-list">
					<view class="cu-item2 ">
						<!-- <view class="cu-avatar round lg" style="margin-right: 30rpx;"></view> -->
						<view style="display: flex;align-items: center; margin-right: 25rpx;">
							<u-avatar class="avatar" :src="school.schoolAddress" level-bg-color="#8072f3"></u-avatar>
						</view>
						<view class="">
							<view class="text-black text-bold">
								<!-- <text v-if="school.anotherName" class="text-cut">{{school.anotherName}}</text> -->
								<text class="text-cut">{{school.schoolName}}</text>
							</view>

						</view>
					</view>
				</view>
			</view>
		</block>
	</view>
</template>

<script>
	import localData from '@/utils/data.js';
	import {
		localRequest
	} from "@/api/request.js";
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		getSchoolLists,
		searchKeyWords,
		getUserStateBySchool,
		getUserScore,
		getOneBbsConfig,
		getOneBbsConfigWithoutSchoolId,
		addUserLoginRecord,
		searchSchoolLists,
		getUserStateWhenChangeSchool
	} from "@/api/index.js"
	export default {
		data() {
			return {
				typeList: [{
					name: "学校圈子"
				}],
				schoolList: [],
				tabCur: 0,
				// mainCur: 0,
				verticalNavTop: 0,
				load: true,
				scrollViewTop: 0,
				currentSchool: {},
				searchSchools: [],
				searchText: "",
				showSearchResult: false,
				fromPage: ''
			};
		},
		computed: {
			...mapState('user', ['userInfos', 'userKey', 'currentSchoolId', 'currentUserState', 'currentSchoolState',
				'shouldWatchCurrenSchoolId'
			]),
			...mapState('config', ['Adv', 'alumniOnly', 'firstPage', 'addPost', 'userFirstLogin', 'needRelaunch']),
		},
		async onLoad(options) {
			console.log("options", options, options.fromPage)
			if (options.fromPage && options.fromPage == "sevenDay") {
				this.fromPage = options.fromPage
				console.log("options", this.fromPage)
			}
			if (options.fromPage && options.fromPage == "blindBox") {
				this.fromPage = options.fromPage
				console.log("options", this.fromPage)
			}
			uni.showLoading({
				title: '加载中...',
				mask: true
			});
			let params = {
				'appId': localData.appId
			}
			const schoolLists = await localRequest.get('/content/getSchoolList', {
				params: params,
			})
			if (schoolLists.code === 200) {
				console.log('学校列表', schoolLists)
				this.schoolList = schoolLists.data
				//保存学校列表到store
				this.getSchoolList(schoolLists.data);
				for (var i = 0; i < this.schoolList.length; i++) {
					//别名判断
					if (this.schoolList[i].anotherName && this.schoolList[i].anotherName == uni.getStorageSync(
							"currentSchoolName")) {
						this.currentSchool = this.schoolList[i]
					} else if (this.schoolList[i].schoolName == uni.getStorageSync("currentSchoolName")) {
						this.currentSchool = this.schoolList[i]
					}
					//校果儿特殊处理
					if (localData.loacalAppId == "wxf162aa13e351a338" && this.schoolList[i].state == 0 && this
						.schoolList[i].schoolId != 2) {
						this.schoolList[i].state = 2
					}
					if (localData.loacalAppId == "wxf162aa13e351a338" && (this.schoolList[i].schoolId == 16
					 || this.schoolList[i].schoolId == 2)) {
						this.schoolList[i].state = 0
					}
				}
				// 找到 schoolId 为 9999 的学校在数组中的位置
				const index = this.schoolList.findIndex(school => school.schoolId === 9999);
				// 如果找到该学校
				if (index !== -1) {
					// 移除该学校
					const specialSchool = this.schoolList.splice(index, 1)[0];
					// 将其插入到数组的第一个位置
					this.schoolList.unshift(specialSchool);
				}
				console.log(this.currentSchool)
			}
		},
		onReady() {
			uni.hideLoading()
			// 在需要获取状态栏高度的地方调用
			let systemInfo = uni.getSystemInfoSync();
			let statusBarHeight = systemInfo.statusBarHeight;
			console.log(statusBarHeight); // 输出状态栏的高度
			this.scrollViewTop = statusBarHeight + 48
			// uni.hideHomeButton()
		},
		onShow(options) {
			wx.hideHomeButton();
		},
		onUnload() {
			this.setShowIndexMask(1);
		},
		methods: {
			...mapMutations('user', {
				getSchoolList: 'getSchoolList',
				getCurrentSchoolId: 'getCurrentSchoolId',
				setCurrentUserState: 'setCurrentUserState',
				setCurrentUserType: 'setCurrentUserType',
				setBannedTime: 'setBannedTime',
				setCurrentSchoolState: 'setCurrentSchoolState',
				setShouldWatchCurrenSchoolId: 'setShouldWatchCurrenSchoolId'
			}),
			...mapMutations('config', {
				setFirstPage: 'setFirstPage',
				setAdv: 'setAdv',
				setAlumniOnly: 'setAlumniOnly',
				setAddPost: 'setAddPost',
				setUserFirstLogin: 'setUserFirstLogin',
				setJudgeShowPopup: 'setJudgeShowPopup',
				setInformConfig: 'setInformConfig',
				setNeedRelaunch: 'setNeedRelaunch'

			}),
			...mapMutations('popup', {
				setShowIndexMask: 'setShowIndexMask',
			}),
			//选择学校
			async selectSchool(school) {
				//手动切换学校不触发学校Id改变的监听
				this.setShouldWatchCurrenSchoolId(false)
				// console.log(school)
				if (school.state == 2) {
					console.log("跳转小程序")
					// #ifdef MP-WEIXIN
					if (school.appId) {
						wx.navigateToMiniProgram({
							appId: school.appId,
							path: '/pages/index/index?currentSchoolId=' + school.schoolId,
							envVersion: 'release',
							success(res) {
								// 打开成功
								console.log('函数执行')
							}
						})
					}
					// #endif
					//手动切换学校不触发学校Id改变的监听
					this.setShouldWatchCurrenSchoolId(true)
					return
				}
				if (school.state != 0 || school.state != this.currentSchoolState) {
					//如果state=0的学校不存在baseUrl，在其他baseUrl的学校中找一个
					if ((school.state == 0 || school.state == 6) && !school.baseUrl) {
						for (let i = 0; i < this.schoolList.length; i++) {
							if (this.schoolList[i].state == 0 && this.schoolList[i].baseUrl) {
								school.baseUrl = this.schoolList[i].baseUrl
								school.appId = this.schoolList[i].appId
								// console.log("找到了")
								break;
							}
						}
					}
					// console.log('哈哈')
					this.changeBaseUrlBySchoolId(school)
					await this.loginWX()
				}

				//修改schoolState
				this.setCurrentSchoolState(school.state)
				// uni.setStorageSync('currentSchoolState',school.state)
				//修改store中的值
				this.getCurrentSchoolId(school.schoolId);
				//保存用户选择的值到storeage
				uni.setStorageSync("schoolId", school.schoolId);
				if (school.anotherName) {
					uni.setStorageSync("currentSchoolName", school.anotherName);
				} else {
					uni.setStorageSync("currentSchoolName", school.schoolName);
				}
				//按学校id获取学校配置
				// await this.getSchoolConfig()
				await this.getSchoolFunctionConfig()
				//不按学校id获取基础配置
				// await this.getSchoolBasicConfigWithoutSchoolId()
				await this.getSchoolBasicConfig()
				//需要重载首页
				this.setNeedRelaunch(true)
				//新用户登录记录
				this.insertUserLoginRecord()
				//更新用户状态
				this.onGetUserStateBySchool()

				//手动切换学校不触发学校Id改变的监听
				this.setShouldWatchCurrenSchoolId(true)
				// console.log("hhhh,在这", this.fromPage)
				if (this.fromPage == "sevenDay") {
					// console.log("hhhh,在这", this.fromPage)
					uni.redirectTo({
						url: '/package_task/pages/seven-day-couple/seven-day-couple?fromPage=changeSchool'
					})
					return
				}
				if (this.fromPage == "blindBox") {
					// console.log("hhhh,在这", this.fromPage)
					uni.redirectTo({
						url: '/package_task/pages/blind-box/blind-box?fromPage=changeSchool'
					})
					return
				}
				

				//跳转首页
				uni.switchTab({
					url: '/pages/index/index'
				});
			},
			//按学校id获取学校配置
			async getSchoolConfig() {
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
			//不按学校id获取基础配置
			async getSchoolBasicConfigWithoutSchoolId() {
				let that = this
				let UserId = '';
				if (uni.getStorageSync('userInfos').userId != null &&
					uni.getStorageSync('userInfos').userId != '') {
					UserId = uni.getStorageSync('userInfos').userId
				}
				console.log('基础配置', this.userInfos.userId)
				//获取基础配置
				let param = {
					configType: 'basicConfig'
				}
				let data = await getOneBbsConfigWithoutSchoolId(param)
				if (data.code == 200) {
					let configList = JSON.parse(data.data.configJson).data
					console.log("基础配置配置项（数据库）", configList)
					for (var i = 0; i < configList.length; i++) {
						// console.log(configList[i].schoolId*1)
						if (configList[i].schoolId * 1 == this.currentSchoolId) {
							//接口地址
							// localData.baseUrl = configList[i].baseUrl
							// localData.webSocketUrl = configList[i].webSocketUrl
							//app资源相关
							localData.appId = configList[i].appId
							localData.envId = configList[i].envId
							localData.resourceAppid = configList[i].resourceAppid
							localData.resourceEnv = configList[i].resourceEnv
							//小程序默认项
							localData.newNickName = configList[i].newNickName
							localData.newHeadimgUrl = configList[i].newHeadimgUrl
							localData.contentTypeList = configList[i].contentTypeList
							//订阅模板相关
							localData.templateId4 = configList[i].templateId4
							localData.templateId7 = configList[i].templateId7
							localData.templateId9 = configList[i].templateId9
							localData.templateId10 = configList[i].templateId10
							console.log(localData.newNickName, localData.newHeadimgUrl, localData.contentTypeList)
						}
					}

				} else {
					console.log("基础配置获取失败");
				}
			},
			//新增用户登录记录
			async insertUserLoginRecord() {
				//拼合链接
				let LoginPath = 'pages/index/index'
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
			async searchSchoolList() {
				const schoolLists = await searchSchoolLists({
					'appId': localData.appId,
					'searchText': this.searchText
				});
				if (schoolLists.code === 200) {
					console.log('学校列表', schoolLists)
					this.searchSchools = schoolLists.data
					if (this.searchText == "") {
						this.showSearchResult = false
					} else {
						this.showSearchResult = true
					}
				}
			},
			async onGetUserStateBySchool() {
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				// const stateData = await getUserStateBySchool(params)
				//0819更新方法
				const stateData = await getUserStateWhenChangeSchool(params)
				if (stateData && stateData.code === 200) {
					this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
					this.setCurrentUserType(stateData.data.userType)
					if (stateData.data.userType == 2 && stateData.data.bannedTime) {
						this.setBannedTime(stateData.data.bannedTime)
					} else {
						this.setBannedTime(null)
					}
				}
			},
		},
	}
</script>
<style>
	page {
		background-color: #F5F5F5;
	}
</style>
<style lang="scss" scoped>
	::v-deep .navbar {
		display: none;
	}

	.fixed {
		position: fixed;
		z-index: 99;
	}

	.VerticalNav.nav {
		width: 200upx;
		white-space: initial;
	}

	.VerticalNav.nav .cu-item {
		width: 100%;
		text-align: center;
		background-color: #fff;
		margin: 0;
		border: none;
		height: 100rpx;
		position: relative;
	}

	.VerticalNav.nav .cu-item.cur {
		background-color: #f1f1f1;
	}

	.VerticalNav.nav .cu-item.cur::after {
		content: "";
		width: 8upx;
		height: 30upx;
		border-radius: 10upx 0 0 10upx;
		position: absolute;
		background-color: currentColor;
		top: 0;
		right: 0upx;
		bottom: 0;
		margin: auto;
	}

	.VerticalBox {
		display: flex;
		height: 100vh;
	}

	.VerticalMain {
		background-color: #f1f1f1;
		flex: 1;
	}

	.cu-item2 {
		position: relative;
		display: flex;
		padding-right: 10rpx;
		height: 140rpx;
		background-color: #ffffff;
		// justify-content: flex-end;
		padding-left: 20rpx;
		align-items: center;
	}
</style>