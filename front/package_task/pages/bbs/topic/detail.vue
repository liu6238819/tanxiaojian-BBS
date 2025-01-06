<template>
	<view class="wrap">
		<view style="position: absolute;">
			<u-navbar :custom-back="onBack" back-icon-color="#fff" :background="background" :border-bottom="false">
			</u-navbar>
		</view>
		<view class="head">
			<image mode="widthFix" class="bg" :src="plateInfo.backImgUrl"></image>
			<view class="head-c">
				<text class="name">{{ plateInfo.name }}</text>
				<view class="count">
					<text>{{ plateInfo.userNum }}人已关注</text>
					<text>{{ plateInfo.contentNum }}篇内容</text>
					<block v-if="plateInfo.isJoin">
						<button class="like-plate" :custom-style="btnStyle" @click="leavePlate">取消关注</button>
					</block>
					<block v-else>
						<button class="like-plate" :custom-style="btnStyle" @click="joinPlate">关注板块</button>
					</block>
				</view>
			</view>
		</view>
		<!-- 简介 -->
		<view class="member-wrap">
			<view class="member-wrap-head">
				<text class="notice-txt u-line-1">{{ plateInfo.introduction }}</text>
				<!-- <u-icon class="icon" name="arrow-right"></u-icon> -->
			</view>
		</view>
		<post-list :contents="topContents" :loadStatus="null">
		</post-list>

		<!-- 分类tab -->
		<!-- <view class="tabs-box">
			<view class="tab-left">
				<u-tabs :list="tabList" :is-scroll="false" :current="current" @change="tabsChange"></u-tabs>
			</view>
		</view> -->
		<view v-show="current == 0">
			<post-list :contents="postNews" :showTag="showTag" :loadStatus="loadStatus" @loadmore='getMore'
				@editLike="editLikeState" @editMark="editMarkState"></post-list>
		</view>
		<view v-show="current == 1">
			<post-list :contents="postHot" :showTag="showTag" :loadStatus="loadHotStatus" @loadmore='getMore'
				@editLike="editLikeState" @editMark="editMarkState">
			</post-list>
		</view>
		<!-- 底部菜单 -->
		<view class="tabbar">
			<view @click="onMenu" class="tab-item">
				<image class="icon" src="/static/menu.png"></image>
				<text>操作</text>
			</view>
			<view @click="onPlus" class="tab-item mid-button">
				<u-icon name="plus" size="50"></u-icon>
			</view>
			<button open-type="share" class="tab-item">
				<image class="icon" src="/static/hot.png"></image>
				<text>分享</text>
			</button>
			<!-- 			<navigator url="/pages/more/more" open-type="switchTab" class="tab-item" hover-class="none">
				<image class="icon" src="../../static/hot.png"></image>
				<text>分享</text>
			</navigator> -->
		</view>
		<!-- 菜单弹框 -->
		<q-popup v-model="showMenu">
			<view class="popup-head">
				<text>菜单</text>
				<u-icon @click="showMenu = false" size="40" class="close" color="#999" name="close"></u-icon>
			</view>
			<u-grid :col="4" :border="false">
				<!-- 如果是圈主或管理员 -->
				<!-- 				<block v-if="plateInfo.uid == sessionUser.uid || plateInfo.is_admin">
					<u-grid-item @click="jumpMenu('/pages/discuss/add?topicId=' + timeForm.plateId)">
						<image class="menu-icon" src="../../static/add-dis.png"></image>
						<view class="grid-text">新建话题</view>
					</u-grid-item>
					<u-grid-item @click="jumpMenu('/pages/topic/info-edit?topicId=' + timeForm.plateId)">
						<image class="menu-icon" src="../../static/topic.png"></image>
						<view class="grid-text">板块信息</view>
					</u-grid-item> -->
				<!-- 				<u-grid-item>
					<image class="menu-icon" src="../../static/notic.png"></image>
					<view class="grid-text">公告</view>
				</u-grid-item> -->
				<!-- 如果是圈主 -->
				<block v-if="plateInfo.userId == userInfos.userId">
					<!-- 					<u-grid-item @click="jumpMenu('/pages/topic/admin?id=' + timeForm.plateId)">
						<image class="menu-icon" src="../../static/admin.png"></image>
						<view class="grid-text">管理员</view>
					</u-grid-item> -->

					<u-grid-item @click="delTopicModel = true">
						<image class="menu-icon" src="/static/jiesan.png"></image>
						<view class="grid-text">解散板块</view>
					</u-grid-item>
				</block>
				</block>
				<!-- 普通成员 -->
				<block v-else>
					<u-grid-item @click="joinTopicModel = true">
						<image class="menu-icon" src="/static/jiesan.png"></image>
						<view class="grid-text">退出板块</view>
					</u-grid-item>
				</block>
			</u-grid>
		</q-popup>
		<!-- 关注板块弹窗 -->
		<u-modal v-if="!plateInfo.isJoin" v-model="joinTopicModel" :show-cancel-button="true" confirm-text="关注板块"
			:content="'是否关注【' + plateInfo.name + '】?'" @confirm="joinPlate"></u-modal>
		<u-modal v-else v-model="joinTopicModel" :show-cancel-button="true" confirm-text="取消关注"
			:content="'是否取消关注【' + plateInfo.name + '】?'" @confirm="leavePlate"></u-modal>
		<!-- 解散板块弹窗 -->
		<u-modal v-model="delTopicModel" :show-cancel-button="true" confirm-color="red" confirm-text="确认"
			:content="'解散【' + plateInfo.name + '】后，将不可恢复，是否确认解散？'" @confirm="plateDel"></u-modal>
		<!-- 发布弹窗 -->
		<u-popup v-model="showPlusPost" mode="center" border-radius="20" width="80%">
			<view class="share-type">
				<view @click="onTrigger(5)" class="type-item">
					<u-icon class="icon" size="40" name="photo" color="#1aa3ff"></u-icon>
					<text>发布分享吐槽</text>
				</view>
				<view @click="onTrigger(6)" class="type-item">
					<image class="icon" src="/static/topic.png"></image>
					<text>发布求助</text>
				</view>
				<view @click="onTrigger(7)" class="type-item">
					<image class="icon" src="/static/group.png"></image>
					<text>发布组队交友</text>
				</view>
				<view @click="onTrigger(8)" class="type-item">
					<image class="icon" src="/static/images/icon/jfdh.png"></image>
					<text>发布二手兼职</text>
				</view>
				<view @click="onTrigger(1)" class="type-item">
					<image class="icon" src="/static/h_1.png"></image>
					<text>发布投票</text>
				</view>

			</view>
		</u-popup>
		<!-- 弥补操作菜单高度 -->
		<view style="height: 200rpx;"></view>
	</view>
</template>

<script>
	import postList from '@/components/post-list/post-list.vue';
	import userList from '@/components/user-list/user-list.vue';
	import {
		getPlateInfoById,
		joinPlate,
		leavePlate,
		deletePlate,
		reqContentList
	} from '@/api/index.js'
	import {
		mapState,
		mapMutations
	} from 'vuex'

	export default {
		components: {
			postList,
			userList
		},
		data() {
			return {
				showPlusPost: false,
				showCanvas: false,
				showShare: false,
				joinTopicModel: false,
				delTopicModel: false,
				showMenu: false,
				current: 0,
				tabList: [{
						name: '最新'
					},
					{
						name: '最热'
					}
				],
				btnStyle: {
					marginRight: 0,
					width: '150rpx',
					fontSize: '12px',
					height: '60rpx',
					lineHeight: '60rpx',
					backgroundColor: '#333',
					color: '#fff'
				},
				shareBtnStyle: {
					backgroundColor: '#333'
				},
				plateInfo: {
					plateId: '',
					name: '',
					introduction: '',
					porttairUrl: '',
					contentNum: 0,
					backImgUrl: '',
					member_list: [],
					discuss_list: [],
					description: '',
					userInfo: {
						username: ''
					},
					isJoin: false,
					userJoinNumber: 0
				},
				postHot: [],
				postNews: [],
				topContents: [],
				loadStatus: 'loadmore',
				loadHotStatus: 'loadmore',
				background: {
					backgroundColor: 'unset'
				},
				sessionUser: {
					uid: 123
				},
				timeForm: {
					pageNum: 0,
					pageSize: 5,
					userId: '',
					plateId: '',
					requestType: 'time',
					schoolId: '',
				},
				hotForm: {
					pageNum: 0,
					pageSize: 5,
					userId: '',
					plateId: '',
					requestType: 'hot',
					schoolId: '',
				},
				topForm: {
					pageNum: 1,
					pageSize: 5,
					plateId: '',
					userId: '',
					requestType: 'top',
					schoolId: ''
				},
				showTag: true //是否展示板块名称标签
			};
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
		},
		onPullDownRefresh() {
			//重置内容
			this.timeForm.pageNum = 0
			this.hotForm.pageNum = 0
			this.loadStatus = 'loadmore'
			this.loadHotStatus = 'loadmore'
			this.postNews = []
			this.postHot = []
			this.topContents = []
			// 获取置顶帖
			this.getTopContents();
			//获取最新内容
			this.getNew()
			//获取最热内容
			this.getHot()
			//交互优化
			uni.stopPullDownRefresh();
			uni.showToast({
				icon: 'none',
				title: '刷新成功！'
			})
		},
		onLoad(options) {
			this.timeForm.plateId = options.id;
			this.timeForm.userId = this.userInfos.userId;
			this.timeForm.schoolId = this.currentSchoolId;
			this.hotForm.plateId = options.id;
			this.hotForm.userId = this.userInfos.userId;
			this.hotForm.schoolId = this.currentSchoolId;
			this.topForm.plateId = options.id;
			this.topForm.userId = this.userInfos.userId;
			this.topForm.schoolId = this.currentSchoolId;
			this.getPlateById();
		},
		async mounted() {
			// 获取置顶帖
			this.getTopContents();
			//请求最新的帖子列表
			this.timeForm.pageNum = this.timeForm.pageNum + 1;
			const data = await reqContentList(this.timeForm);
			if (data.code === 200) {
				if (data.data.length > 0) {
					const stepData = data.data.sort(function(item1, item2) {
						return item2.createTime > item1.createTime
					})
					this.postNews.push(...stepData)
				} else if (data.data.length === 0) {
					this.loadStatus = 'nomore'
				}
			} else {
				console.log('获取最新帖子列表失败');
			}

			//请求最热的帖子列表
			this.hotForm.pageNum = this.hotForm.pageNum + 1;
			const data1 = await reqContentList(this.hotForm);
			if (data1.code === 200) {
				if (data1.data.length > 0) {
					const stepData1 = data1.data.sort(function(item1, item2) {
						return item2.upNum + item2.commentNum > item1.upNum + item1.commentNum
					})
					this.postHot.push(...stepData1)
				} else if (data1.data.length === 0) {
					this.loadHotStatus = 'nomore'
				}
			} else {
				console.log('获取帖子列表失败');
			}
		},


		methods: {
			async getMore() {
				if (this.current === 0) {
					//请求最新的帖子列表
					this.getNew()
				} else {
					//请求最热的帖子列表
					this.getHot()
				}
			},
			async getNew() { //请求最新的帖子列表
				this.timeForm.pageNum = this.timeForm.pageNum + 1;
				if (this.postNews.length > 0) {
					const time = new Date(this.postNews[this.postNews.length - 1].createTime); // 获取帖子列表最后一个帖子的创建时间
					const year = time.getFullYear(); // 年份
					const month = time.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
					const day = time.getDate(); // 日期
					const hour = time.getHours(); // 小时
					const minute = time.getMinutes(); // 分钟
					const second = time.getSeconds(); // 秒钟
					this.timeForm.queryTime = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' +
					second;
					console.log(time)
				}
				const data = await reqContentList(this.timeForm);
				if (data.code === 200) {
					if (data.data.length > 0) {
						this.loadStatus = 'loadmore'
						const stepData = data.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						this.postNews.push(...stepData)
					} else if (data.data.length === 0) {
						this.loadStatus = 'nomore'
					}
				} else {
					console.log('获取最新帖子列表失败');
				}
			},
			async getHot() { //请求最热的帖子列表
				this.hotForm.pageNum = this.hotForm.pageNum + 1;
				const data1 = await reqContentList(this.hotForm);
				if (data1.code === 200) {
					if (data1.data.length > 0) {
						const stepData1 = data1.data.sort(function(item1, item2) {
							return item2.upNum + item2.commentNum > item1.upNum + item1.commentNum
						})
						this.postHot.push(...stepData1)
					} else if (data1.data.length === 0) {
						this.loadHotStatus = 'nomore'
					}
				} else {
					console.log('获取帖子列表失败');
				}

			},
			async getTopContents() { //获取置顶帖
				const data = await reqContentList(this.topForm);
				if (data.code === 200 && data.data) {
					if (data.data.length > 0) {
						const stepData = data.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						this.topContents.push(...stepData)
					}
					console.log("置顶列表请求成功");
				} else {
					console.log("置顶列表请求失败");

				}
			},
			async getPlateById() {
				const form = {
					plateId: this.timeForm.plateId,
					userId: this.userInfos.userId
				};
				const data = await getPlateInfoById(form);
				this.plateInfo.plateId = data.plateId;
				this.plateInfo.userId = data.userId;
				this.plateInfo.name = data.name;
				this.plateInfo.introduction = data.introduction;
				this.plateInfo.porttairUrl = data.portraitUrl;
				this.plateInfo.backImgUrl = data.backImgUrl;
				this.plateInfo.contentNum = data.contentNum;
				this.plateInfo.isJoin = data.join;
				this.plateInfo.userNum = data.userNum;
			},
			async joinPlate() {
				console.log('join');
				if (this.userInfos.userId !== '') {
					if (this.timeForm.plateId !== undefined) {
						const form = {
							userId: this.userInfos.userId,
							plateId: this.timeForm.plateId
						};
						uni.showLoading({
							title: '关注中'
						})
						const data = await joinPlate(form);
						if (data.code === 200) {
							uni.hideLoading()
							this.plateInfo.isJoin = true;
						} else {
							uni.showToast({
								title: '关注失败',
								icon: "none"
							});
						}
					} else {
						uni.showToast({
							title: '板块不存在！',
							icon: "none"
						});
					}
				} else {
					uni.showToast({
						title: '请登录后关注！'
					})
				}

			},
			async leavePlate() {
				console.log('leave');
				if (this.userInfos.userId !== '') {
					const form = {
						userId: this.userInfos.userId,
						plateId: this.timeForm.plateId
					};
					uni.showLoading({
						title: '取消中'
					})
					const data = await leavePlate(form);
					if (data.code === 200) {
						uni.hideLoading()
						this.plateInfo.isJoin = false;
					} else {
						uni.showToast({
							title: '请重试',
							icon: "none"
						});
					}
				} else {
					uni.showToast({
						title: '请登录后操作！'
					})
				}
			},
			onBack() {
				let pages = getCurrentPages();
				console.log(pages);
				if (pages.length > 1) {
					if (pages[pages.length - 2].route === '/package_task/pages/bbs/topic/add/add') {
						uni.switchTab({
							url: '/pages/index/index'
						});
					} else {
						uni.navigateBack();
					}

				} else {
					uni.switchTab({
						url: '/pages/index/index'
					});
				}
			},
			onMenu() {
				//this.joinTopicModel = true;
				if (this.plateInfo.isJoin) {
					this.showMenu = true;
				} else {
					this.joinTopicModel = true;
				}
			},
			onPlus() {
				// this.showPlusPost = true;
				uni.navigateTo({
					url: '/pages/post/add?plateId=' + this.timeForm.plateId + '&name=' + this.plateInfo.name
				});

			},
			tabsChange(index) {
				this.current = index;
			},
			onTrigger(type) {
				this.showPlusPost = false;
				if (type == 1) {
					uni.navigateTo({
						url: '/pages/vote/vote?plateId=' + this.timeForm.plateId + '&name=' + this.plateInfo.name
					});
				} else {
					uni.navigateTo({
						url: '/pages/post/add?plateId=' + this.timeForm.plateId + '&name=' + this.plateInfo.name +
							'&contentType=' + type
					});
				}
			},
			async plateDel() {
				console.log('delete');
				if (this.userInfos.userId !== '') {
					const form = {
						userId: this.userInfos.userId,
						plateId: this.timeForm.plateId
					};
					uni.showLoading({
						title: '解散中'
					})
					const data = await deletePlate(form);
					if (data.code === 200) {
						uni.hideLoading()
						this.plateInfo.isJoin = false;
						uni.reLaunch({
							url: '/pages/index/index'
						});
					} else {
						uni.showToast({
							title: data.message,
							icon: "none"
						});
					}
				} else {
					uni.showToast({
						title: '请登录后操作！'
					})
				}

			},
			onShareTimeline() {
				let imgURL = this.plateInfo.backImgUrl;
				return {
					title: this.plateInfo.name,
					imageUrl: imgURL,
					query: 'id=' + this.plateInfo.plateId
				};
			},
			onShareAppMessage(res) {
				console.log(this.plateInfo)
				if (res.from === 'button') {
					// 来自页面内分享按钮
					console.log(res.target);
				}
				let imgURL = this.plateInfo.backImgUrl;
				return {
					title: this.plateInfo.name,
					// path: '/package_task/pages/bbs/topic/detail?id=' + this.plateInfo.plateId,
					path: '/pages/index/index?page=topicDetail&currentSchoolId=' + this.currentSchoolId + '&plateId=' +
						this.plateInfo.plateId,
					imageUrl: imgURL
				};
			},
			editLikeState(value) {
				console.log("触发")
				for (var i = 0; i < this.postNews.length; i++) {
					if (this.postNews[i].contentId == value.contentId) {
						this.postNews[i].isLike = value.isLike
						if (value.isLike == 1) {
							this.postNews[i].upNum += 1
						} else if (value.isLike == 0) {
							this.postNews[i].upNum -= 1
						}
					}
				}
			},
			editMarkState(value) {
				console.log("触发")
				for (var i = 0; i < this.postNews.length; i++) {
					if (this.postNews[i].contentId == value.contentId) {
						this.postNews[i].isMark = value.isMark
						if (value.isMark == 1) {
							this.postNews[i].markCount += 1
						} else {
							this.postNews[i].markCount -= 1
						}
						if (value.delNickName) {
							this.postNews[i].markNickName = null
						}
					}
				}
			}
		},
		async onReachBottom() {
			if (this.loadStatus == 'loadmore') {
				this.loadStatus = 'loading'
				await this.getMore()
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
	.like-plate {
		padding: 0;
		margin-right: 0;
		width: 64px;
		font-size: 12px;
		height: 25px;
		line-height: 25px;
		background-color: #333;
		color: #fff;
		overflow: visible;
	}

	.wrap {
		height: calc(100vh);
	}

	.mg-left-auth {
		margin-left: auto;
	}

	.mg-left-20 {
		margin-left: 20rpx;
	}

	.tabs-box {
		background-color: #FFFFFF;

		.tab-left {
			width: 30%;
		}
	}

	/*  */
	.notice-txt {
		color: #999;
		font-size: 12px;
	}

	.grid-text {
		font-size: 12px;
		color: #616161;
		margin-bottom: 30rpx;
	}

	.head {
		position: relative;
		height: 380rpx;
	}

	.head-c {
		position: absolute;
		top: 200rpx;
		left: 0;
		width: 100%;
		display: flex;
		flex-direction: column;
		color: #fff;
		padding: 30rpx;
	}

	.head-c .count {
		font-size: 10px;
		display: flex;
		align-items: center;
	}

	.head-c .count text {
		margin: 0 5rpx;
		color: #e6e6e6;
	}

	.head-c .name {
		font-size: 20px;
		font-weight: bold;
	}

	.margin-left {
		margin-left: auto;
		margin-right: 20rpx;
	}

	.head .bg {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		max-height: 380rpx;
	}

	/* 管理员 */
	.member-wrap {
		padding: 30rpx;
		background-color: #fff;
		border-bottom: 1px solid #eee;
	}

	.member-wrap .avatar {
		margin-bottom: 10rpx;
	}

	.member-wrap .member-wrap-head {
		display: flex;

		.user-num {
			margin-left: auto;
			color: #999;
		}

		.icon {
			margin-left: auto;
			color: #999;
		}
	}

	// 置顶
	.post-top-box {
		background-color: #fff;
		padding: 20rpx;

		.post-item {
			display: flex;
			align-items: center;
			padding: 20rpx;
			font-size: 28rpx;

			&:last-child {
				margin-bottom: 0;
			}

			.tag {
				background-color: #333;
				color: #fff;
				padding: 0 10rpx;
				border-radius: 10rpx;
				font-size: 20rpx;
				height: 40rpx;
				line-height: 40rpx;
				margin-right: 20rpx;
			}
		}
	}

	// 圈话题
	.dis-wrap {
		display: flex;
		margin-top: 20rpx;

		.d-item {
			flex-grow: 0;
			flex-shrink: 0;
			width: 330rpx;
			background-color: #F5F5F5;
			border-radius: 10rpx;
			padding: 20rpx;
			font-size: 28rpx;
			font-weight: 600;
			margin-right: 20rpx;
		}
	}

	// 菜单
	.tabbar {
		position: fixed;
		bottom: 50rpx;
		width: 70%;
		margin-left: 15%;
		margin-right: 15%;
		background-color: #fff;
		display: flex;
		padding: 10rpx;
		box-shadow: 0 0 10rpx #e6e6e6;
		font-size: 24rpx;
		border-radius: 100rpx;
		z-index: 9999;

		.tab-item {
			font-size: 24rpx;
			background-color: #fff;

			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			margin: 0 auto;

			.icon {
				margin-top: 10rpx;
				width: 40rpx;
				height: 40rpx;
			}
		}

		.tab-item::after {
			border: 0rpx;
		}

		.mid-button {
			background-color: #333;
			width: 90rpx;
			height: 90rpx;
			border-radius: 50%;
			color: #fff;
		}
	}

	// 菜单弹窗
	.popup-head {
		text-align: center;
		font-size: 24rpx;
		position: relative;
		padding: 30rpx;
		border-bottom: 1px solid #F5F5F5;
		margin-bottom: 30rpx;

		.close {
			position: absolute;
			right: 30rpx;
			top: 30rpx;
		}
	}

	.menu-icon {
		width: 60rpx;
		height: 60rpx;
		margin-bottom: 10rpx;
	}

	// 分享类型弹窗
	.share-type {
		padding: 50rpx 30rpx;

		.type-item {
			background-color: #F5F5F5;
			padding: 20rpx;
			display: flex;
			justify-content: center;
			align-items: center;

			.icon {
				width: 40rpx;
				height: 40rpx;
				margin-right: 20rpx;
			}

			&:nth-child(2) {
				margin: 20rpx 0;
			}

			&:nth-child(3) {
				margin: 20rpx 0;
			}

			&:nth-child(4) {
				margin: 20rpx 0;
			}
		}
	}

	//海报弹窗
	.canvas-box {
		height: 500px;
		position: relative;

		.footer {
			position: absolute;
			bottom: 20rpx;
			left: 20rpx;
			right: 20rpx;
		}
	}
</style>
