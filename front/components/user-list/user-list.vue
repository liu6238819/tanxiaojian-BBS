<template>
	<view>
		<block v-for="(item, index) in userList" :key="index">
			<navigator :url="'/package_task/pages/bbs/user/home?userId=' + item.userId" class="member-item">
				<u-avatar class="avatar" :src="item.headimgUrl"></u-avatar>
				<view class="user">
					<text class="name">{{ item.nickName }}</text>
					<text class="intro">{{item.introduction}}</text>
					<!-- 					<view v-if="item.gender">
						<text v-if="item.gender == '男'" class="iconfont icon-nan"></text>
						<text v-if="item.gender == '女'" class="iconfont icon-nv"></text>
					</view>
					<text>{{item.city}}</text> -->
				</view>
				<!-- <u-button @click="follow(index, item.uid)" v-if="0 === 0" class="btn-gz" type="default" size="mini">关注
				</u-button>
				<u-button @click="cancelFollow(index, item.uid)" v-if="1 === 1" class="btn-gz" type="default"
					size="mini" plain>互相关注</u-button> -->
				<u-button @click="ontabNoFollow(item)" v-if="2 === 2 && item.isFollow && item.isFollow==1" class="btn-gz" type="default"
					size="mini" plain>已关注</u-button>
			</navigator>
		</block>
		<!-- 加载状态 -->
		<block v-if="loadStatus != 'none'">
			<block v-if="list.length === 0 && loadStatus == 'nomore'">
				<u-empty margin-top="100" text="暂无用户" mode="favor"></u-empty>
			</block>
			<block v-else>
				<u-loadmore margin-bottom="50" margin-top="50" :status="loadStatus" @loadmore='loadMore()' />
			</block>
		</block>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		followUser,
	} from "../../api/index.js"
	export default {
		props: {
			list: Array,
			loadStatus: String
		},
		data() {
			return {
				userList: []
			};
		},
		computed: {
			...mapState('user', ['followData','userInfos']),
		},
		watch: {
			list() {
				this.userList = this.list;
			}
		},
		methods: {
			...mapMutations('user', {
				setFollowData: 'setFollowData'
			}),
			loadMore() {
				if (this.loadStatus == 'loadmore') this.$emit('loadmore');
			},
			follow(index, uid) {
				this.$H
					.post('user/addFollow', {
						id: uid
					})
					.then(res => {
						if (res.code == 200) {
							this.userList[index].has_follow = 1;
						}
					});
			},
			cancelFollow(index, uid) {
				this.$H
					.post('user/cancelFollow', {
						id: uid
					})
					.then(res => {
						if (res.code === 200) {
							this.userList[index].has_follow = 0;
						}
					});
			},
			//用户点击取消关注
			ontabNoFollow(item){
				console.log("参数",item, this.userInfos.userId,this.followData)
				let that=this
				uni.showModal({
					title: '提示',
					content: "确定要取消关注吗？",
					success: function(res) {
						if (res.confirm) {
							// console.log('用户点击确定');
							that.cancelFollow(item)
						} else if (res.cancel) {
							// console.log('用户点击取消');
						}
					}
				})
			},
			//取消关注
			async cancelFollow(item) {
				//获取当前的关注信息
				let nowFollowList = this.followData.followList
				let nowFollowTotalNum =this.followData.followTotalNum
				console.log(nowFollowList,nowFollowTotalNum)
				//前端传入参数
				const isFollow = (!item.isFollow) * 1
				const params = {
					userId: this.userInfos.userId,
					targetId: item.userId,
					isFollow //0:取关，1：关注
				}
				// console.log('params',params);
				const followData = await followUser(params)
				if (followData.code === 200) {
					//改变store中的关注信息
					nowFollowTotalNum =nowFollowTotalNum-1
					let delIndex = 0
					for (var i = 0; i < nowFollowList.length; i++) {
						if(nowFollowList[i].userId ==item.userId) {
							delIndex =i;
							break;
						}
					}
					nowFollowList.splice(delIndex,1)
					this.setFollowData({
						followList: nowFollowList,
						followTotalNum: nowFollowTotalNum
					})
				} else if (followData.code === 201) {
					uni.showToast({
						icon: 'none',
						title: '自己不能关注自己哦'
					})
				} else {
					uni.showToast({
						icon: 'none',
						title: '接口请求失败，服务器异常！'
					})
				}
				// console.log('followData', followData);
			},
		}
	};
</script>

<style lang="scss" scoped>
	.member-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border-bottom: 1px solid #f5f5f5;
		background-color: #ffffff;

		&:last-child {
			border-bottom: 0;
		}

	}

	.member-item .icon-nv {
		color: #ff4d94;
	}

	.member-item .icon-nan {
		color: #0091ff;
	}

	.member-item .avatar {
		margin-right: 20rpx;
	}

	.member-item .user .name {
		margin-right: 20rpx;
		color: #2B85E4;

	}

	.member-item .user .intro {
		margin-right: 20rpx;
		font-size: 28rpx;
		color: #999;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;

	}

	.member-item .user .name {
		margin-right: 20rpx;

	}

	.member-item .user .iconfont {
		font-size: 12px;
	}

	.member-item .btn-gz {
		margin-left: auto;
	}
</style>
