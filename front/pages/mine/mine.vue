<template>
	<view>
		<view class="head">
			<block v-if="(userInfos.nickName)">
				<view style="display: flex;justify-content: space-between; align-items: center; margin-right: 20rpx;">
					<view class="userinfo" @click="toUcenter">
						<u-avatar :src="userInfos.headimgUrl" size="140"></u-avatar>
						<view>
							<view style="display: flex; align-items: center;">
								<view class="username">
									<text>{{ userInfos.nickName }}</text>
								</view>
								<q-icon class="asrrow-right" name="q-arrow-right" size="36rpx"></q-icon>
							</view>

							
							<u-button type="primary" size="mini" shape="circle" plain style="margin-right: 20rpx;"
								@click="onTabShowSchool2"
								v-if="changeSchoolState==1">
								<text class="text-bold cuIcon-order"
									style="transform: rotate(90deg) scaleX(-1); margin-right: 5rpx;"></text>
								{{currentSchoolName}}
							</u-button>
							<u-button type="primary" size="mini" shape="circle" plain style="margin-right: 20rpx;"
								v-if="changeSchoolState!=1">
								{{currentSchoolName}}
							</u-button>
							
						</view>
					</view>
					<view style="display: flex; flex-direction: row-reverse;">
						<u-button v-if="currentUserState==0" type="default" size="mini" shape="circle"
							@click="onhandleAuthentication" @longpress="getItem(5)" plain>
							未认证
						</u-button>
						<u-button v-if="currentUserState==1" type="warning" size="mini" shape="circle"
							@longpress="getItem(5)" plain disabled>认证中
						</u-button>
						<u-button v-if="currentUserState==2" type="success" size="mini" shape="circle"
							 plain>
							已认证
						</u-button>
						<u-button v-if="currentUserState==3" type="warning" size="mini" shape="circle" plain>
							外校游客
						</u-button>
					</view>
				</view>
			</block>
			<block v-else>
				<view v-if="isLocalUser!=1 || currentSchoolState!=3" class="btn-login">
					<u-button type="default" shape="circle" @click="toLogin" plain>登录/认证</u-button>
				</view>
			</block>
			<u-grid :col="3" :border="false" style="margin: 20rpx 0;" @click="toNav">
				<u-grid-item index="/package_task/pages/bbs/mine/fans">
					<text>{{followData.fansNum}}</text>
					<view class="grid-text">粉丝</view>
				</u-grid-item>
				<u-grid-item index="/package_task/pages/bbs/mine/follow">
					<text>{{followData.followNum}}</text>
					<view class="grid-text">关注</view>
				</u-grid-item>
				<u-grid-item @click="toExchangeCenter">
					<text>{{ scoresFront }}</text>
					<view class="grid-text">积分</view>
				</u-grid-item>

			</u-grid>
		</view>
		<!-- 我的服务 -->
		<view class="block-wrap">
			<view class="block-title">我的服务</view>
			<u-grid :col="3" :border="false">
				<u-grid-item index="/pages/my/topic" @click="getItem(1)">
					<image class="gn-icon" src="/static/m_2.png"></image>
					<view class="grid-text">我的帖子</view>
				</u-grid-item>
				<u-grid-item index="/pages/my/thumb" @click="getItem(3)">
					<image class="gn-icon" src="/static/m_4.png"></image>
					<view class="grid-text">赞的帖子</view>
				</u-grid-item>

				<u-grid-item @click="openCustomerServiceChat">
					
					<view class="gn-icon">
						<u-icon name="server-fill" size="60" color="#646464"></u-icon>
					</view>
					<view class="grid-text">联系客服</view>
				</u-grid-item>

				<u-grid-item @click="getItem(6)">
					
					<view class="gn-icon">
						<u-icon name="person-delete-fill" size="60" color="#646464"></u-icon>
					</view>
					<view class="grid-text">拉黑列表</view>
				</u-grid-item>

				<u-grid-item @click="getHelp">
					<image class="gn-icon" src="/static/m_22.png"></image>
					<view class="grid-text">关于我们</view>
				</u-grid-item>

				<u-grid-item @click="toRulePage">
					
					<view class="gn-icon">
						<u-icon name="order" size="60" color="#646464"></u-icon>
					</view>
					<view class="grid-text">社区规范</view>
				</u-grid-item>

			</u-grid>
		</view>

		<image :src="imageurl" mode="widthFix"></image>

		<!-- modal框 -->
		<view class="cu-modal show" v-if="modalName=='DialogModal'">
			<view class="cu-dialog modal-content" style="max-height: 80%;">
				<view class="cu-bar bg-white lg justify-end">
					<view class="content">{{classList[current]}}</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<!-- 我的帖子 -->
				<view v-show="current === 1">
					<post-list :contents="postList" :showTag="false" :loadStatus="loadStatus" @loadmore='getMore(1)'
						@editLike="editLikeState" @editMark="editMarkState" @editDeletePost="editDeletePost">
					</post-list>
				</view>
				<!-- 我的板块 -->
				<view v-show="current === 2">
					<topic-list :list="topicList" :loadStatus='loadStatus' @loadmore='getMore(2)'></topic-list>
				</view>
				<!-- 我赞的帖子 -->
				<view v-show="current === 3">
					<post-list :contents="postList" :showTag="false" :loadStatus="loadStatus" @loadmore='getMore(3)'
						@editLike="editLikeState" @editMark="editMarkState">
					</post-list>
				</view>
				<!-- 我的邀请码 -->
				<view v-show="current === 4">
					<view class="inviteCode">
						<view style="width: 90%;color: #8e8e8e; text-align: left;">长按复制</view>
						<view style="width: 90%;color: #ff0000;text-align: left;word-wrap: break-word"
							@longpress="onCopy">{{inviteCode}}
						</view>
					</view>
				</view>
				<!-- 面对面认证 -->
				<view v-show="current === 5">
					<view class="moadl-view">
						<input class="text-left text-df modal-input" placeholder-class="text-grey" placeholder="请输入认证码"
							v-model="offlineInfo.code" />
						<view class="modal-button">
							<button class="cu-btn bg-red" @click="onInputCode"> 确定</button>
						</view>
					</view>
				</view>
				<!-- 黑名单 -->
				<view v-show="current === 6">
					<view v-if="blockRecordList.length==0">
						<view class="msg-empty">
							<!-- <image class="img" mode="widthFix" src="/static/m_10.png"></image> -->
							<text class="txt">暂无拉黑用户</text>
						</view>
					</view>
					<view v-if="blockRecordList.length!=0">
						<view v-for="record in blockRecordList">
							<view class="block-card">
								<view style="display: block;">
									<view>昵称：{{record.targetName}}</view>
								</view>
								<button class="cu-btn bg-blue" style="height: 50rpx;" @click="editBlockRecord(record)">
									取消拉黑</button>
							</view>
						</view>
					</view>
					<block>
						<view style="margin: 30rpx 0;">
							<u-loadmore :status="blockLoadStatus" :load-text="blockLoadText"
								@loadmore='getBlockRecordList()' />
						</view>
					</block>
				</view>

			</view>
		</view>
		<!-- 显示tabBar中的红色圆点(消息提示) -->
		<q-tabbar :active="4" :count="msgCount" :markState="markState"></q-tabbar>
		<view v-if="showExaminePopup==1">
			<examine-popup v-if="currentSchoolState!=3" @returnHid='returnHide'></examine-popup>
		</view>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>

	</view>
</template>

<script>
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	import {
		getAccessTokenWX,
		code2SessionWX,
		reqEditUserInfos,
		userLoginWX,
		searchKeyWords,
		reqLikedContents,
		reqFollowedUsers,
		getUserStateBySchool,
		getPlateLists,
		offlineIdentification,
		getUserScore,
		reqContentList,
		getBlockRecordList,
		editBlockRecord,
		getOneBbsConfig,
		editLocalUserInfos,
	} from '@/api/index.js'
	import postList from '../../components/post-list/post-list.vue';
	import topicList from '../../components/topic-list/topic-list.vue';
	import localData from '../../utils/data.js';
	import WXBizDataCrypt from '@/utils/WXBizDataCrypt.js'
	import rsa from '@/utils/rsa.js';
	export default {
		components: {
			postList,
			topicList,
		},
		data() {
			return {
				modalName: null,
				classList: {
					1: '我的帖子',
					2: '我的话题',
					3: '我赞的帖子',
					4: '我的邀请码',
					5: '面对面认证',
					6: '我的拉黑列表'
				}, // '我赞的帖子'],
				current: 0,
				loadStatus: "loading",
				postList: [],
				topicList: [],
				form: {
					type: 1, //(1：帖子，2：板块，3：评论，4：用户，5：学校)
					mode: 1, //mode(查询模式 1：单条件，2：多条件)
					condition: {}, //(查询条件的键值对)
					pageNum: 1,
					pageSize: 5,
					sortType: 'createTime'
					// sortType: null //排序方式默认按照创建时间排序
				},
				followData: {
					fansNum: 0,
					followNum: 0,
					contentNum: 0
				},
				isLoginedOut: false,
				isloading: true, //默认为true
				code: '',
				hasLogin: false,
				tokenWX: '',
				openid: '',
				inviteCode: '微信登录授权后生成',
				userState: 0,
				plateId: {
					inschool: ''
				},
				offlineInfo: { //线下认证信息
					code: '',
					schoolId: 0,
					userId: '',
				},
				//认证相关
				showExaminePopup: 0,
				cardCur: 0, //当前选中下标
				//黑名单相关
				blockRecordList: [],
				blockParams: {
					userId: '',
					pageNum: 0,
					pageSize: 5
				},
				blockLoadStatus: "loadmore",
				blockLoadText: {
					loadmore: '点击加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				blindBoxState: 0,
				sevenDayState: 0,
				currentSchoolName: '',
				changeSchoolState: localData.changeSchoolState,
			}
		},
		async mounted() {
			console.log(localData.changeSchoolState)
			// 获取用户在当前学校下的认证状态
			//this.getUserStateBySchool()
			// 获取关注我的用户列表
			this.getFollowedUser(0)
			// 获取我关注的用户列表
			this.getFollowedUser(1)
			//获取用户资格判断的板块号
			let param = {
				cateId: 1,
				schoolId: this.currentSchoolId
			}
			this.currentSchoolName = uni.getStorageSync("currentSchoolName")
		},
		async onShow() {
			this.getUserStateBySchool()
			this.getFollowedUser(1)
			this.getActivityConfig()
		},
		computed: {
			...mapState('user', ['userInfos', 'userKey', 'scoresFront', 'currentUserState', 'currentUserType',
				'currentSchoolState', 'isLocalUser', 'localUserInfos'
			]),
			...mapState('remind', ['msgCount']),
			...mapState('remindWSS', ['markState']),
			...mapState('user', ['currentSchoolId']),
			...mapState('config', ['privacyPopup', 'judgeShowPopup']),
		},
		methods: {
			...mapMutations('user', {
				setCurrentUserState: 'setCurrentUserState',
				setScoresFront: 'setScoresFront',
			}),
			//跳转抽奖页面
			async toLuckDraw() {
				//先进行隐私授权判断
				// console.log(this.privacyPopup.needAuthorization)
				if (this.privacyPopup.needAuthorization) {
					let needPrivacyAuthorization = await this.judgePrivacySetting()
					// console.log(needPrivacyAuthorization)
					if (needPrivacyAuthorization == true) {
						return
					}
				} else {
					console.log("已授权，不需要调用")
				}
				let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
					.currentSchoolId,
					1)
				if (permissionCheckResult != 0) {
					this.showExaminePopup = 1
					return
				}
				uni.navigateTo({
					url: '../../package_task/pages/luck-draw/luck-draw'
				})
			},
			//面对面认证
			async onInputCode() {
				if (this.offlineInfo.code == '') {
					uni.showToast({
						title: '请输入认证码',
						icon: "none"
					})
					return
				}
				this.offlineInfo.schoolId = this.currentSchoolId
				this.offlineInfo.userId = this.userInfos.userId
				console.log("传入", this.offlineInfo)
				let res = await offlineIdentification(this.offlineInfo)
				let that = this
				if (res.code == 200) {
					uni.showToast({
						title: res.data,
						icon: "none"
					})
					this.setCurrentUserState(2)
					//this.userState = 2
					setTimeout(function() {
						that.modalName = ''
					}, 500);
				} else {
					uni.showToast({
						title: res.message,
						icon: "none"
					})

				}
			},
			// 获取用户在当前学校下的状态
			async getUserStateBySchool() {
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					//this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
				}
			},
			// 获取我关注或者关注我的用户信息
			async getFollowedUser(type) {
				const params = {
					userId: uni.getStorageSync('userInfos').userId,
					type,
					pageNum: 1,
					pageSize: 5
				}
				const followUserData = await reqFollowedUsers(params)
				if (followUserData.code === 200) {
					const {
						total,
						contentNum,
						records
					} = followUserData.data
					this.followData.contentNum = contentNum
					if (type === 0) {
						this.followData.fansNum = total
						this.setFollowData({
							fansList: records,
							fansTotalNum: total
						})
					} else if (type === 1) {
						this.followData.followNum = total
						this.setFollowData({
							followList: records,
							followTotalNum: total
						})
					}
					// console.log('vuex',this.$store);
					console.log(this.followData);
					// console.log('### followUserData', followUserData);
				}
			},
			toNav(url) {
				uni.navigateTo({
					url
				});
			},
			hideModal() {
				this.modalName = null
				//黑名单页数重置
				this.blockRecordList = []
				this.blockParams.pageNum = 0
			},
			getItem(type) {
				console.log('getItem')
				this.current = type
				this.modalName = 'DialogModal'
				if (type === 1) {
					// 帖子
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
					this.getSearchList(1, 1)
				} else if (type === 2) {
					// 板块
					this.getSearchList(1, 2)
				} else if (type === 3) {
					// 我赞的帖子
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
					this.getLikedContents(1)
				} else if (type === 4) {
					console.log('生成邀请码')
					// 生成邀请码
					if (this.userInfos.nickName && this.userInfos.headimgUrl) {
						this.inviteCode = localData.createInviteCode(this.userInfos.userId)
					} else {
						console.log('未认证用户')
					}
				} else if (type === 5) {

				} else if (type == 6) {
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
					this.getBlockRecordList()
				}
			},
			getMore(type) {
				let pageNum = this.form.pageNum + 1
				if (type === 3) {
					this.getLikedContents(pageNum)
				} else {
					this.getSearchList(pageNum, type)
				}
			},
			// 查询我的板块、我的帖子
			async getSearchList(pageNum = 1, type = 1) {
				this.loadStatus = "loadmore";
				this.form.type = type // 1：查询对应userId下的用户发的帖子
				this.form.pageNum = pageNum
				this.form.condition.userId = uni.getStorageSync('userInfos').userId
				this.form.condition.searchUserId = uni.getStorageSync('userInfos').userId
				this.form.condition.schoolId = this.currentSchoolId
				const searchData = await searchKeyWords(this.form)
				if (searchData.code === 200) {
					const {
						total,
						records,
						current
					} = searchData.data
					if (type === 1) { // 获取用户帖子列表
						for (var i = 0; i < records.length; i++) { //赋予头像昵称
							records[i].headimgUrl = this.userInfos.headimgUrl
							records[i].nickName = this.userInfos.nickName
						}
						if (pageNum === 1) {
							this.postList = records
						} else {
							// 加载更多
							this.postList = this.postList.concat(records);
						}
					} else if (type === 2) {
						// 获取用户板块列表
						if (pageNum === 1) {
							this.topicList = records
							console.log('topicList', this.topicList);
						} else {
							this.topicList = this.topicList.concat(records);
						}
					}
					if (current * this.form.pageSize >= total || !records) {
						this.loadStatus = "nomore";
					} else {
						this.loadStatus = "loadmore"
					}
				}
				console.log('searchData', searchData);
			},
			// 查询我赞的帖子
			async getLikedContents(pageNum = 1) {
				this.form.pageNum = pageNum
				const params = {
					userId: this.userInfos.userId,
					pageNum,
					pageSize: this.form.pageSize,
					schoolId: this.currentSchoolId,
				}
				// console.log('params',params);
				const likeContentsData = await reqLikedContents(params)
				if (likeContentsData.code === 200) {
					const {
						total,
						records,
						current
					} = likeContentsData.data
					if (pageNum === 1) {
						this.postList = records
					} else {
						// 加载更多
						this.postList = this.postList.concat(records);
					}
					if (current * this.form.pageSize >= total || !records) {
						this.loadStatus = "nomore";
					} else {
						this.loadStatus = "loadmore"
					}
					// console.log('likeContentsData',likeContentsData);
				}
				// console.log('postList',this.postList);
			},
			...mapMutations('user', {
				getUserInfos: 'getUserInfos',
				editUserInfos: 'editUserInfos',
				setFollowData: 'setFollowData',
				setIsLocalUser: 'setIsLocalUser'
			}),
			async exazmine(e) {
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/user-examine'
				});

			},
			async authorize() {
				const authorize = await this.userAuthorize()
				console.log('authorize', authorize);
			},
			async toLogin() {
				//#ifdef H5
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/login'
				});
				//#endif
				// #ifdef MP-WEIXIN
				console.log('登录/认证开始');
				if (this.currentSchoolState == 3 && this.isLocalUser == 1) {

				}
				//const authorizeResult = await this.userAuthorize()
				const authorizeResult = await this.userInfoInit()
				if (authorizeResult == 'success') {
					this.loginWX()
				}
				// #endif
			},
			toUcenter() {
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/edit-info/edit'
				});
			},
			getHelp() {
				uni.navigateTo({
					url: '../../package_task/pages/group-code/index?codeKind=' + 2
				})
				// uni.showModal({
				// 	content: '开通校园/建议反馈/商务合作，请联系qq：2095678031'
				// })
			},
			onCopy() {
				let that = this
				console.log('长按复制');
				uni.setClipboardData({
					data: that.inviteCode,
					success: function() {
						console.log('success');
					}
				});
			},
			//向后台更新信息
			updateUserInfo() {
				let _this = this;
				uni.request({
					url: 'url', //服务器端地址
					data: {
						customerId: _this.customerId, //自定义id
						nickName: _this.nickName, //昵称
						headUrl: _this.avatarUrl //头像
					},
					method: 'POST',
					header: {
						'content-type': 'application/json'
					},
					success: (res) => {
						if (res.data.state == "success") {
							uni.reLaunch({ //信息更新成功后跳转到小程序首页
								url: '/pages/index/index'
							});
						}
					}

				});
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			editLikeState(value) {
				console.log("触发")
				for (var i = 0; i < this.postList.length; i++) {
					if (this.postList[i].contentId == value.contentId) {
						this.postList[i].isLike = value.isLike
						if (value.isLike == 1) {
							this.postList[i].upNum += 1
						} else if (value.isLike == 0) {
							this.postList[i].upNum -= 1
						}
					}
				}
			},
			editMarkState(value) {
				console.log("触发")
				for (var i = 0; i < this.postList.length; i++) {
					if (this.postList[i].contentId == value.contentId) {
						this.postList[i].isMark = value.isMark

					}
				}
			},
			editDeletePost(value) {
				// console.log("删除帖子",value)
				this.postList = this.postList.filter(item => item.contentId !== value.contentId);
			},
			async onhandleAuthentication() {
				//先进行隐私授权判断
				// console.log(this.privacyPopup.needAuthorization)
				if (this.privacyPopup.needAuthorization) {
					let needPrivacyAuthorization = await this.judgePrivacySetting()
					// console.log(needPrivacyAuthorization)
					if (needPrivacyAuthorization == true) {
						return
					}
				} else {
					console.log("已授权，不需要调用")
				}
				this.showExaminePopup = 1

			},
			//获取黑名单
			async getBlockRecordList() {
				this.blockParams.userId = this.userInfos.userId;
				this.blockParams.pageNum += 1;
				console.log(this.blockParams)
				const data = await getBlockRecordList(this.blockParams)
				if (data.code === 200) {
					console.log("拉黑列表", data)
					this.blockRecordList.push(...data.data)
					if (data.data.length == 0) {
						this.blockLoadStatus = 'nomore'
					}
				} else {
					console.log('拉黑列表获取失败');
				}
			},
			//移除黑名单
			async editBlockRecord(blockRecord) {
				const params = {
					id: blockRecord.id,
					targetState: 0,
				}
				const data = await editBlockRecord(params)
				if (data.code === 200) {
					console.log("移出拉黑列表", data)
					this.blockParams.pageNum = 0
					this.blockRecordList = []
					this.getBlockRecordList()
				} else {
					console.log('移出拉黑列表失败');
				}
			},
			//打开客服页面
			openCustomerServiceChat() {
				let that = this
				console.log(this.judgeShowPopup.extInfo)
				//默认客服链接
				let extInfoUrl = 'https://work.weixin.qq.com/'
				if (this.judgeShowPopup.extInfo && this.judgeShowPopup.extInfo != '') {
					extInfoUrl = this.judgeShowPopup.extInfo
				}
				wx.openCustomerServiceChat({
					extInfo: {
						url: extInfoUrl
					},
					corpId: 'ww45debfa9e67919a9',
					success(res) {
						console.log(res)
					},
					fail(err) {
						console.log(err)
					}
				})

			},
			//前往规则页面
			toRulePage() {
				uni.navigateTo({
					url: '../../package_task/pages/rule-page/rule-page?index=1'
				})
			},
			//获取活动配置
			async getActivityConfig() {
				let param = {
					schoolId: this.currentSchoolId,
					configType: 'activityConfig'
				}
				let data = await getOneBbsConfig(param)
				if (data.code == 200) {
					let configList = JSON.parse(data.data.configJson).data
					// console.log("活动配置项配置项（数据库）", configList)
					for (var i = 0; i < configList.length; i++) {
						if (configList[i].function_name == 'blindBox') {
							console.log(configList[i])
							this.blindBoxState = configList[i].activityState
						}
						if (configList[i].function_name == 'sevenDayCouple') {
							console.log(configList[i])
							this.sevenDayState = configList[i].activityState
						}
					}
				}
			},
			//onTabshowSchool
			onTabShowSchool() {
				if (this.currentSchoolId == 2 || this.currentSchoolId == 1) {
					return
				} else {
					// this.showSchool=true
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/chang-school'
					})
				}
			},
			onTabShowSchool2() {
				uni.navigateTo({
					url: '/package_task/pages/bbs/more/chang-school'
				})
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
			}
		}
	};
</script>

<style>
	page {
		background-color: #f5f5f5;
	}
</style>
<style lang="scss" scoped>
	.modal-content {
		//top: -26px;
		//height: 80%;
		overflow: auto;
		text-align: left;
	}

	.item {
		padding-bottom: 100rpx;
	}

	.head {
		padding: 20rpx;
		background-color: #fff;

		.sub-txt {
			font-size: 24rpx;
			color: #616161;
			display: block;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 1;
			overflow: hidden;
		}

		margin-bottom: 20rpx;
	}

	.userinfo {
		display: flex;
		align-items: center;
		padding: 20rpx;
	}

	.userinfo .username {
		display: flex;
		flex-direction: column;
	}

	.grid-text {
		color: #999;
		font-size: 12px;
		margin-bottom: 20rpx;
	}

	.userinfo u-avatar {
		margin-right: 20rpx;
	}

	.userinfo .arrow-right {
		margin-left: auto;
	}

	.inviteCode {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		align-items: center;
	}

	.btn-login {
		margin: 40rpx 0;
	}

	.gn-icon {
		width: 60rpx;
		height: 60rpx;
		margin-bottom: 20rpx;
	}

	/*服务按钮*/
	.btn-wrap {
		display: flex;
		margin-top: 30rpx;
	}

	.btn-wrap .btn-contact {
		background-color: #fff;
		margin-left: 15rpx;
		margin-right: 15rpx;
		padding: 20rpx;
		line-height: unset;
		font-size: 12px;
		color: #999;
	}

	.btn-wrap .btn-contact:active {
		background-color: #f5f5f5;
	}

	.btn-wrap .btn-contact .txt {
		margin-top: 20rpx;
	}

	.btn-wrap .btn-contact::after {
		border: unset;
		position: unset;
	}

	.icon-size {
		font-size: 50rpx;
	}

	.block-wrap {
		background-color: #fff;
		border-radius: 20rpx;
		margin: 20rpx;
		overflow: hidden;

		.block-title {
			background-color: #fff;
			padding: 20rpx;
		}
	}

	.modal-input {
		background-color: #f0f0f0;
		width: 70%;
		padding-left: 15rpx;
		margin-top: 5rpx;
	}

	.modal-button {
		width: 30%;
		display: flex;
		justify-content: flex-end;
		margin-right: 20rpx;
	}

	.moadl-view {
		display: flex;
		margin-left: 15rpx;
		margin-right: 15rpx;
		margin-top: 30rpx;
		margin-bottom: 30rpx;
	}

	.block-card {
		padding: 20rpx;
		border-radius: 16rpx;
		background: #FFFFFF;
		box-shadow: 0px 2px 8px 0px rgba(68, 68, 68, 0.15);
		margin-top: 20rpx;
		margin-bottom: 24rpx;
		margin-left: 24rpx;
		margin-right: 24rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

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
			font-size: 40rpx;
			margin-top: 20rpx;
		}
	}
</style>