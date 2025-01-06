<template>
	<view class="u-skeleton">
		<view style="position: absolute;">
			<u-navbar :custom-back="onBack" back-icon-color="#fff" :background="background" :border-bottom="false">
			</u-navbar>
		</view>
		<image class="head-bg u-skeleton-rect" src="https://image.tanxiaojian.zone/user/user_back.png">
		</image>
		<view class="con-c">
			<view class="user-info">
				<u-avatar class="avatar" :src="otherUserInfos.headimgUrl" level-bg-color="#8072f3" size="130" @click="imagePreview(otherUserInfos.headimgUrl)">
				</u-avatar>
				<view class="user-c">
					<!-- <view class="username u-skeleton-fillet">{{otherUserInfos.nickName}}</view> -->
					<view style="display: flex; align-items: center;">
						<text class="username u-skeleton-fillet">{{ otherUserInfos.nickName }}</text>
						<text v-if="otherUserInfos.userState==2" class="cu-tag light bg-green sm"
							style="margin-left: 10rpx ; margin-top: 70rpx;">已认证</text>
						<text v-if="otherUserInfos.userState!=2" class="cu-tag light bg-gray sm"
							style="margin-left: 10rpx ; margin-top: 70rpx;">未认证</text>
					</view>
					<view class="num-box u-skeleton-fillet">
						<text>{{otherUserInfos.focusCount}} <text class="txt">关注</text></text>
						<text>{{otherUserInfos.fansCount}} <text class="txt">粉丝</text></text>
						<u-button v-show="userId!==userInfos.userId && isblock==0 && otherUserInfos.isFollowed!==1" @click="changeFollow" :custom-style="btnStyle"
						 shape="circle" size="mini" >
							<u-icon name="plus"></u-icon>
							<text style="margin: 0 5rpx;">关注</text>
						</u-button>
						<u-button v-show="userId!==userInfos.userId && isblock==0 && otherUserInfos.isFollowed===1" @click="ontabNoFollow" :custom-style="btnStyle2"
						 shape="circle" size="mini" >
							<text style="margin: 0 5rpx;">已关注</text>
						</u-button>
						<u-button v-show="userId!==userInfos.userId && otherUserInfos.userIdentify!=10 && isblock==0"
							@click="addBlockRecord(userInfos.userId,userId)" :custom-style="btnStyle3" 
							shape="circle" size="mini" style="margin-left:45rpx;" >
							<u-icon name="man-delete"></u-icon>
							<text style="margin: 0 5rpx;">拉黑</text>
						</u-button>
						<u-button v-show="userId!==userInfos.userId && otherUserInfos.userIdentify!=10 && isblock==1"
							@click="editBlockRecordByuser(userInfos.userId,userId,0)" :custom-style="btnStyle4"
							shape="circle" size="mini" style="margin-left:45rpx;" >
							<text style="margin: 0 5rpx;">取消拉黑</text>
						</u-button>
						
					</view>
					<text class="desc u-skeleton-fillet">{{otherUserInfos.intro}}</text>
				</view>
			</view>
			<!-- tab -->
			<block v-if="isblock==0">
				<view class="tab-box u-skeleton-fillet">
					<u-tabs bg-color="#f5f5f5" inactive-color="#999" name="tab_name" :list="tabs" active-color="#616161"
						:is-scroll="false" c :current="current" @change="tabChange"></u-tabs>
				</view>
				<!-- 主页 -->
				<view v-show="current === 0">
					<!-- 基本信息 -->
					<view class="f-wrap u-skeleton-fillet">
						<view class="title">基本信息</view>
						<view class="info-c">
							<!-- 						<text>昵称：{{otherUserInfos.nickName}}</text> -->
							<text>简介：{{otherUserInfos.introduction}}</text>
							<text v-show="otherUserInfos.city">城市：{{otherUserInfos.city}}</text>
						</view>
					</view>
					<!-- 				创建的圈子
					<view v-if="userInfo.create_topic_list.length > 0" class="f-wrap u-skeleton-fillet">
						<view class="title">创建的圈子</view>
						<topic-list :list="userInfo.create_topic_list" loadStatus="none"></topic-list>
					</view> -->
				</view>
				<!-- 帖子 -->
				<view v-show="current === 1">
					<post-list :contents="postList" :loadStatus="loadStatus" :showTag="false" @loadmore='getMore(1)'
					@editLike="editLikeState" @editMark="editMarkState">
					</post-list>
				</view>
				<!-- 帖子（管理） -->
				<view v-show="current === 2">
					<post-list :contents="manageContentList" :loadStatus="allContentLoadStatus" :showTag="false" @loadmore='getUserAllContent()'
						@editLike="editLikeState" @editMark="editMarkState">
					</post-list>
				</view>
				<!-- 加入的圈子 -->
				<view v-show="current === 3">
					<topic-list :list="topicList" :loadStatus='loadStatus' @loadmore='getMore(2)'></topic-list>
				</view>
			</block>
			<block v-if="isblock==1">
				<view class="msg-empty">
					<!-- <image class="img" mode="widthFix" src="/static/m_10.png"></image> -->
					<text class="txt">已屏蔽该用户内容</text>
				</view>
			</block>

		</view>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>
		<view v-if="showExaminePopup==1">
			<!-- <examine-popup @returnHid='returnHide'></examine-popup> -->
			<examine-popup v-if="currentSchoolState!=3" @returnHid='returnHide'></examine-popup>
		</view>
		<!--骨架屏-->
		<u-skeleton :loading="loading" :animation="true" bgColor="#FFF"></u-skeleton>
	</view>
</template>

<script>
	import {
		searchKeyWords,
		getUserInfoById,
		followUser,
		addBlockRecord,
		editBlockRecordByUserId,
		reqAllContentList
	} from '@/api/index.js'
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import postList from '@/components/post-list/post-list.vue';
	import topicList from '@/components/topic-list/topic-list.vue';
	export default {
		components: {
			postList,
			topicList
		},
		data() {
			return {
				form: {
					type: 1, //(1：帖子，2：板块，3：评论，4：用户，5：学校)
					mode: 1, //mode(查询模式 1：单条件，2：多条件)
					condition: {}, //(查询条件的键值对)
					pageNum: 1,
					pageSize: 10,
					sortType: 'createTime' //排序方式默认按照创建时间排序
				},
				userId: null,
				isFollow: 0,
				otherUserInfos: {},
				tabs: [{
						tab_name: '主页'
					}, {
						tab_name: '帖子'
					}
					// ,{
					// 	tab_name: '参与话题'
					// }
				],
				current: 0,
				btnStyle: {
					color: "#fff",
					backgroundColor: '#efd234',
					height:"40rpx",
					padding:"0 15rpx"
				},
				btnStyle2: {
					border: '1px solid #000',
					color: "#000",
					height:"40rpx",
					padding:"0 15rpx"
				},
				btnStyle3: {
					color: "#fff",
					backgroundColor: '#000',
					height:"40rpx",
					padding:"0 15rpx"
				},
				btnStyle4: {
					color: "#000",
					backgroundColor: '#f0f0f0',
					height:"40rpx",
					padding:"0 15rpx"
				},
				loading: false,
				loadStatus: "loading",
				postList: [],
				topicList: [],
				background: {
					backgroundColor: 'unset'
				},
				isAlumnus: 1, //判断是否已认证
				isblock: 0,
				manageContentForm: {
					pageNum: 0,
					pageSize: 10,
					userId: '',
					schoolId: '',
				},
				manageContentList: [],
				allContentLoadStatus: "loading",
				showExaminePopup: 0,
			};
		},
		computed: {
			...mapState('user', ['userInfos', 'webSocketLinker']),
			...mapState('user', ['currentSchoolId','currentUserType']),
		},
		onLoad(options) {
			this.userId = options.userId
			// console.log('userId',this.userId);
			// 获取用户信息(已经返回了用户是否相互关注)
			this.getOtherUserInfos();
			if (this.currentUserType==3) {
				let manageTab ={
					tab_name: '历史发帖'
				};
				this.tabs.push(manageTab)
			}
		},
		methods: {
			...mapMutations('user', {
				setWebSocketLinker: 'setWebSocketLinker'
			}),
			async changeFollow() {
				let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId,
					this
					.currentSchoolId,
					1)
				if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
					uni.hideLoading()
					return
				}
				if (permissionCheckResult != 0) {
					uni.hideLoading()
					this.showExaminePopup = 1
					return
				}
				const isFollow = (!this.otherUserInfos.isFollowed) * 1
				if (isFollow==1) {
					let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, this.userId)
					if (blockedState == 1) {
						uni.showToast({
							title: '您已被该用户拉黑',
							icon: 'none'
						})
						return
					}
				}
				let followtitle = "关注中..."
				if (isFollow==0) {
					followtitle = "取消关注中..."
				}
				uni.showLoading({
					title: followtitle,
					mask: true,
				})
				const params = {
					userId: this.userInfos.userId,
					targetId: this.userId,
					isFollow //0:取关，1：关注
				}
				// console.log('params',params);
				const followData = await followUser(params)
				if (followData.code === 200) {
					this.otherUserInfos.isFollowed = isFollow
					uni.hideLoading()
				} else if (followData.code === 201) {
					uni.hideLoading()
					uni.showToast({
						icon: 'none',
						title: '自己不能关注自己哦'
					})
				} else {
					uni.hideLoading()
					uni.showToast({
						icon: 'none',
						title: '接口请求失败，服务器异常！'
					})
				}
				// console.log('followData', followData);
			},
			async getOtherUserInfos() {
				const params = {
					userId: this.userInfos.userId,
					searchId: this.userId, //需要返回信息的用户id
					schoolId: this.currentSchoolId,
				}
				const OtherUserInfosData = await getUserInfoById(params)
				// console.log('params',params);
				// console.log('OtherUserInfosData',OtherUserInfosData);
				if (OtherUserInfosData.code === 200) {
					this.otherUserInfos = OtherUserInfosData.data.userInfo
					console.log(this.otherUserInfos);
				}
			},
			tabChange(index) {
				this.current = index;
				if (index === 1) {
					this.getSearchList(); //获取给用户的帖子列表
				}
				if (index === 2) {
					this.manageContentForm.pageNum=0
					this.getUserAllContent(); //获取给用户的帖子列表
				}
				if (index === 3) {
					this.getSearchList(1, 2); //获取该用户的板块列表
				}
			},
			getMore(type) {
				let pageNum = this.form.pageNum + 1
				this.getSearchList(pageNum, type)
			},
			async getSearchList(pageNum = 1, type = 1) {
				this.loadStatus = "loadmore";
				this.form.type = type // 1：查询对应userId下的用户发的帖子
				this.form.pageNum = pageNum
				this.form.condition.userId = this.userId
				this.form.condition.searchUserId = this.userInfos.userId
				this.form.condition.schoolId = this.currentSchoolId
				console.log('this.form', this.form);
				const searchData = await searchKeyWords(this.form)
				if (searchData.code === 200) {
					const {
						total,
						records,
						current
					} = searchData.data
					if (type === 1) {
						// 获取用户帖子列表
						for (var i = 0; i < records.length; i++) { //赋予头像昵称
							records[i].headimgUrl = this.otherUserInfos.headimgUrl
							records[i].nickName = this.otherUserInfos.nickName
						}
						if (pageNum === 1) {
							this.postList = records
						} else {
							// 加载更多
							this.postList = this.postList.concat(records);
						}
						this.postList = this.postList.filter(item => {
							return (item.contentType != 3 && item.isSpecial != 1 && item.isSpecial != 4)
						})
					} else if (type === 2) {
						// 获取用户板块列表
						if (pageNum === 1) {
							this.topicList = records
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
			onBack() {
				uni.navigateBack();
			},
			toChat() {
				let chater = {
					userId: this.otherUserInfos.userId,
					nickName: this.otherUserInfos.nickName,
					headimgUrl: this.otherUserInfos.headimgUrl
				}
				this.setWebSocketLinker(this.otherUserInfos.userId)
				uni.navigateTo({
					url: '/package_task/pages/bbs/message/chat?chater=' + JSON.stringify(chater)
				})
			},
			editLikeState(value){
				console.log("触发")
				for (var i = 0; i < this.postList.length; i++) {
					if (this.postList[i].contentId == value.contentId) {
						this.postList[i].isLike = value.isLike
						if (value.isLike==1) {
							this.postList[i].upNum +=1
						}
						else if(value.isLike==0){
							this.postList[i].upNum -=1
						}
					}
				}
			},
			editMarkState(value){
				console.log("触发")
				for (var i = 0; i < this.postList.length; i++) {
					if (this.postList[i].contentId == value.contentId) {
						this.postList[i].isMark = value.isMark
						
					}
				}
			},
			addBlockRecord(userId, targetId) {
				let that = this
				const params = {
					userId: userId,
					targetId: targetId,
				}
				uni.showModal({
					title: '确认拉黑该用户吗？',
					content: '拉黑后，该用户将无法与您互动以及查看您的主页，对方发布的内容也将被屏蔽。',
					success: async function(res) {
						if (res.confirm) {
							console.log('用户点击确定');
							const data = await addBlockRecord(params)
							if (data.code === 200) {
								that.isblock = 1
								uni.showToast({
									title: '拉黑成功！',
									icon: 'none',
									duration: 3000
								})
								if (that.otherUserInfos.isFollowed==1) {
									that.changeFollow()
								}
							} else {
								uni.showToast({
									title: '操作失败，服务器异常！',
									icon: 'none'
								})
							}
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				})
			},
			//移除黑名单
			async editBlockRecordByuser(userId, targetId, targetState) {
				const params = {
					userId: userId,
					targetId: targetId,
					targetState: targetState,
				}
				const data = await editBlockRecordByUserId(params)
				if (data.code === 200) {
					// console.log("移出拉黑列表", data)
					if (targetState == 0) {
						this.isblock = 0
					} else {
						this.isblock = 1
					}
				} else {
					console.log('移出拉黑列表失败');
				}
			},
			async getUserAllContent(){
				this.allContentLoadStatus = "loadmore";
				console.log('hhh',this.userId)
				this.manageContentForm.userId = this.userId;
				this.manageContentForm.schoolId = this.currentSchoolId;
				this.manageContentForm.pageNum +=1;
				if (this.manageContentForm.pageNum==1) {
					this.manageContentList=[]
				}
				const data = await reqAllContentList(this.manageContentForm)
				if (data.code==200) {
					console.log(data.data)
					this.manageContentList.push(...data.data)
					if (data.data.length==0) {
						this.allContentLoadStatus = "nomore";
					}
				}
			},
			imagePreview(url){
				console.log(url)
				uni.previewImage({
					current: url, // 当前显示图片的http链接
					urls: [url] // 需要预览的图片http链接列表
				});
			},
			ontabNoFollow() {
				let that = this
				uni.showModal({
					title: '提示',
					content: "确定要取消关注吗？",
					success: function(res) {
						if (res.confirm) {
							// console.log('用户点击确定');
							that.changeFollow()
						} else if (res.cancel) {
							// console.log('用户点击取消');
						}
					}
				})
			},
		}
	}
</script>
<style>
	page {
		background-color: #f5f5f5;
	}
</style>
<style lang="scss" scoped>
	.f-wrap {
		margin-bottom: 30rpx;
	}

	.head-bg {
		width: 100%;
		height: 500rpx;
	}

	.con-c {
		padding: 30rpx;
		position: relative;
		top: -200rpx;
		width: 100%;
	}

	.user-info {
		display: flex;
		flex-direction: column;
		align-items: center;
		position: relative;
		height: 500rpx;
	}

	.avatar {
		width: 130rpx;
		height: 130rpx;
		border-radius: 50%;
		border: 2px solid #FFFFFF;
		z-index: 999;
	}

	.user-info .user-c {
		background-color: #FFFFFF;
		border-radius: 30rpx;
		padding: 30rpx;
		position: absolute;
		top: 70rpx;
		width: 100%;
		box-shadow: 5rpx 10rpx 20rpx #e6e6e6;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.user-info .user-c .username {
		font-size: 40rpx;
		font-weight: bold;
		margin-top: 70rpx;
	}

	.user-info .user-c .username1 {
		font-size: 40rpx;
		font-weight: bold;
		margin-top: 70rpx;
		color: blue;
	}

	.user-info .user-c .num-box {
		font-size: 24rpx;
		margin: 20rpx 0;

	}

	.user-info .user-c .num-box .txt {
		color: #999;
		margin-left: 5rpx;
	}

	.user-info .user-c .num-box text {
		margin-right: 30rpx;
	}

	.user-info .user-c .desc {
		font-size: 22rpx;
		color: #999;
	}

	.user-info .user-c .btn-box {
		margin-top: 20rpx;
	}

	.user-info .user-c .btn-box .btn {
		margin-right: 20rpx;
	}

	.tab-box {
		margin-top: 30rpx;
		margin-bottom: 30rpx;
	}

	.info-c {
		display: flex;
		flex-direction: column;
	}

	.info-c>text {
		margin-bottom: 20rpx;
		color: #999;
	}

	.info-c .level-box {
		margin-bottom: 20rpx;
		display: flex;
		align-items: center;
		color: #999;

		.level {
			font-size: 20rpx;
			color: #fff;
			padding: 5rpx 10rpx;
			background-color: $themes-color;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}
	}

	/* 标签 */
	.tag-box {}

	.tag-box .tag {
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		display: inline-block;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		background-color: #ffebe5;
	}

	.tag-box .tag:nth-child(2n) {
		background-color: #ecf9f2;
	}

	.tag-box .tag:nth-child(3n) {
		background-color: #fff7e5;
	}

	.tag-box .tag:nth-child(5n) {
		background-color: #b3e0ff;
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
