<template>
	<view class="u-wrap">
		<view class="u-search-box">
			<u-search placeholder="输入名称搜索话题" v-model="keyword" @search="search" :show-action="false"></u-search>
		</view>
		<view class="u-menu-wrap">
			<scroll-view scroll-y scroll-with-animation class="u-tab-view menu-scroll-view" :scroll-top="scrollTop">
				<view v-for="(item, index) in classList" :key="index" class="u-tab-item"
					:class="[current == index ? 'u-tab-item-active' : '']" :data-current="index"
					@tap.stop="swichMenu(index)">
					<text class="u-line-1">{{ item.cateName }}</text>
				</view>
			</scroll-view>
			<scroll-view scroll-y class="right-box">
				<view class="page-view">
					<block v-for="(item, index) in plateList" :key="index">
						<navigator :url="'/package_task/pages/bbs/topic/detail?id=' + item.plateId">
							<view class="topic-item">
								<u-image width="100rpx" height="100rpx" shape="circle" :src="item.portraitUrl">
								</u-image>
								<view class="right">
									<view>{{ item.name }}</view>
									<view class="desc">{{ item.introduction}}</view>
									<view class="num">
										<text>{{ item.userNum }}人已加入</text>
										<text>{{ item.contentNum }}篇内容</text>
									</view>
								</view>
							</view>
						</navigator>
					</block>
					<!-- 加载状态 -->
					<block v-if="loadStatus != 'none'">
						<block v-if="plateList.length > 0">
							<view style="margin: 30rpx;">
								<u-loadmore :status="loadStatus" />
							</view>
						</block>
						<block v-else>
							<u-empty margin-top="100" text="暂无相关话题" mode="favor"></u-empty>
						</block>
					</block>
				</view>
			</scroll-view>
		</view>
		<!-- 显示tabBar中的红色圆点(消息提示) -->
		<q-tabbar :active="1" :count="msgCount"></q-tabbar>
	</view>
</template>

<script>
	import localData from '@/utils/data.js';
	import {
		getClassLists,
		getPlateLists
	} from '@/api/index.js'
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	export default {
		data() {
			return {
				scrollTop: 0, //tab标题的滚动条位置
				current: 0, // 预设当前项的值
				menuHeight: 0, // 左边菜单的高度
				menuItemHeight: 0, // 左边菜单item的高度
				classList: '',
				classId: 1, //刚点开时类目id为1
				plateList: '',
				loadStatus: 'nomore',
				keyword: '',
				page: 1
			};
		},
		computed: {
			...mapState('user', ['currentSchoolId']),
			...mapState('remind', ['msgCount'])
		},
		onShow() {
			this.getPlateList();
			this.getClassList();
		},
		methods: {
			search() {
				uni.navigateTo({
					url: '/package_task/pages/bbs/topic/list?keyword=' + this.keyword + '&current=' + 0
				});
				this.keyword = '';
			},
			async getClassList() {
				this.classList = await getClassLists();
			},
			// 点击左边的栏目切换
			async swichMenu(index) {

				if (index == this.current) return;
				this.current = index;
				// 如果为0，意味着尚未初始化
				if (this.menuHeight == 0 || this.menuItemHeight == 0) {
					await this.getElRect('menu-scroll-view', 'menuHeight');
					await this.getElRect('u-tab-item', 'menuItemHeight');
				}
				// 将菜单菜单活动item垂直居中
				this.scrollTop = index * this.menuItemHeight + this.menuItemHeight / 2 - this.menuHeight / 2;

				this.classId = this.classList[index].cateId;
				this.plateList = [];
				this.page = 1;
				this.getPlateList();
				if (this.classId == 3) {
					uni.showToast({
						icon: 'none',
						title: '自建专区仅展示关注人数大于10的板块，人数不足的板块可通过搜索关键字进入。',
						duration: 2500
					})
				}
				if (this.classId == 4) {
					uni.showToast({
						icon: 'none',
						title: '在本专区下的板块发言，无需进行校友身份认证',
						duration: 1500
					})
				}
			},

			//根据类目id获取对应板块列表
			async getPlateList() {
				let form = {
					cateId: this.classId,
					schoolId: this.currentSchoolId
				}
				console.log(form);
				this.plateList = await getPlateLists(form);
			},

			// 获取一个目标元素的高度
			getElRect(elClass, dataVal) {
				new Promise((resolve, reject) => {
					const query = uni.createSelectorQuery().in(this);
					query
						.select('.' + elClass)
						.fields({
								size: true
							},
							res => {
								// 如果节点尚未生成，res值为null，循环调用执行
								if (!res) {
									setTimeout(() => {
										this.getElRect(elClass);
									}, 10);
									return;
								}
								this[dataVal] = res.height;
							}
						)
						.exec();
				});
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

<style lang="scss" scoped>
	.u-wrap {
		height: calc(100vh);
		/* #ifdef H5 */
		height: calc(100vh - var(--window-top));
		/* #endif */
		display: flex;
		flex-direction: column;
	}

	.u-search-box {
		padding: 20rpx 30rpx;
	}

	.u-menu-wrap {
		flex: 1;
		display: flex;
		overflow: hidden;
	}

	.u-tab-view {
		width: 200rpx;
		height: 100%;
		border-right: 1px solid #F5F5F5;
		padding-left: 20rpx;
	}

	.u-tab-item {
		height: 110rpx;
		box-sizing: border-box;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 26rpx;
		color: #444;
		font-weight: 400;
		line-height: 1;
	}

	.u-tab-item-active {
		position: relative;
		color: $themes-color;
		font-size: 30rpx;
		font-weight: 600;
		background: #fff;
	}

	.u-tab-item-active::before {
		content: "";
		position: absolute;
		border-left: 4px solid $themes-color;
		height: 32rpx;
		left: 0;
		top: 39rpx;
	}

	.u-tab-view {
		height: 100%;
	}

	.page-view {
		padding: 16rpx;
	}


	// 板块列表
	.topic-item {
		display: flex;
		background-color: #fff;
		padding: 20rpx;

		.right {
			margin-left: 20rpx;
			display: flex;
			flex-direction: column;

			.desc {
				font-size: 22rpx;
				color: #999;
				margin: 10rpx 0;
				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				-webkit-line-clamp: 1;
				-webkit-box-orient: vertical;
			}
		}

		.num {
			display: flex;
			margin-top: auto;
		}

		image {
			width: 100rpx;
			height: 100rpx;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}

		.btn-gz {
			margin-left: auto;
			margin-right: 20rpx;
		}

		text {
			font-size: 10px;
			color: #999;
			margin: 0 10rpx;
		}
	}
</style>
