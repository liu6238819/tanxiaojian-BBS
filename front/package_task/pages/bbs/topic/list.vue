<template>
	<view>
		<view class="u-search-box" v-if="current == '0'">
			<u-search placeholder="输入名称搜索板块" v-model="plateKeyword" @change="getPlateList(0)" :show-action="false">
			</u-search>
		</view>
		<view class="u-search-box" v-if="current == '1'">
			<u-search placeholder="输入名称搜索帖子" v-model="contentKeyword" @search="getContentList(0)" :show-action="false">
			</u-search>
		</view>
		<scroll-view scroll-y class="right-box" v-if="current == '0'">
			<view class="page-view">
				<block v-for="(item, index) in plateList" :key="index">
					<navigator :url="'/package_task/pages/bbs/topic/detail?id=' + item.plateId">
						<view class="topic-item">
							<u-image width="100rpx" height="100rpx" shape="square" :src="item.portraitUrl">
							</u-image>
							<view class="right">
								<view>{{ item.name }}</view>
								<view class="num">
									<text>{{ item.upNum }}人已加入</text>
									<text>{{ item.contentNum }}篇内容</text>
								</view>
							</view>
						</view>
					</navigator>
				</block>
				<!-- 板块加载状态 -->
				<block v-if="loadStatus != 'none'">
					<block v-if="plateList.length > 0">
						<view style="margin: 30rpx;">
							<u-loadmore :status="loadStatus" @loadmore='getMore(1)' />
						</view>
					</block>
					<block v-else>
						<u-empty margin-top="100" text="暂无相关板块" mode="favor"></u-empty>
					</block>
				</block>
			</view>
		</scroll-view>
		<block v-for="(content, index) in contentList" :key="content.contentId" v-if="current == '1'">
			<block v-if="content.alumniOnly && content.alumniOnly==1 && currentUserState!=2 ">
				<view class="post-item" @click="tabAlumniOnlyPost(content)">
					<view class="post-title">
						<text class="discuss-title">无标题
						</text>
						<text class="" v-if="content.nickName">{{ content.nickName }}
						</text>
					</view>
					<view style="display: flex;align-items: center;margin: 20rpx 0;">
						<u-icon name="lock-fill" size="30" color="#646464"></u-icon>
						<text class="post-text" style="color:#646464 ; margin-left: 5rpx;">
							作者已设置仅对校园认证用户展示
						</text>
					</view>
				</view>
			</block>
			<block v-if="!content.alumniOnly || content.alumniOnly!=1 || currentUserState==2 ">
				<view class="post-item" @click="toPost(content.contentId)">
					<view class="post-title">
						<text class="discuss-title" v-if="content.title">{{ content.title }}
						</text>
						<text class="discuss-title" v-else>无标题
						</text>
						<text class="" v-if="content.nickName">{{ content.nickName }}
						</text>
					</view>
					<view class="post-content">
						<view class="text">
							<rich-text v-if="content.contentText" class="post-text" :nodes="content.contentText">
							</rich-text>
						</view>
						<!--只展示一张图片-->
						<view style="width: 35%;" v-if="content.contentUrl !== null && content.contentUrl != ''">
							<image class="image" mode="aspectFill" :src="content.contentUrl[0]"></image>
							<!-- 						<u-image width="150rpx" height="100rpx" shape="square" :src="content.contentUrl[0]">
							</u-image> -->
						</view>
					</view>
					<view class="p-footer">
						<view class="interactInfo">
							<view class="p-item">
								<text class="iconfont icon-pinglun"></text>
								<text class="count">{{ content.commentNum }}</text>
							</view>
							<view class="p-item">
								<u-icon name="heart"></u-icon>
								<text class="count">{{ content.upNum }}</text>
							</view>
						</view>
						<view class="timeInfo">
							<view class="center">
								<text>{{ content.createTime| date('yyyy年mm月dd日hh时MM分') }}</text>
							</view>
						</view>
					</view>
				</view>
			</block>
		</block>
		<!-- 帖子加载状态 -->
		<!-- <block v-if="loadContentStatus != 'none'"> -->
		<block v-if="current == '1'">
			<view v-if="contentList.length > 0">
				<view style="margin: 30rpx;">
					<u-loadmore :status="loadContentStatus" @loadmore='getMore(0)' />
				</view>
			</view>
			<view v-else>
				<u-empty margin-top="100" text="暂无相关帖子" mode="favor"></u-empty>
			</view>
		</block>
		<!-- 防止底部凹陷 -->
		<view style="height: 200rpx;"></view>
		<view v-if="showExaminePopup==1">
			<examine-popup @returnHid='returnHide'></examine-popup>
		</view>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>
		<view class="page-mask" v-if="sliderMask==1" >
			<view
				style="display: flex; width: 100%; justify-content:center;text-align: center; position: relative; top: 25%; justify-items: center; ">
				<view style="width: 70%;">
					<view style="margin-bottom: 10px; text-align: center; color: #fff; font-weight: bold;">
						真人检测
					</view>
					<wo-slider @slideFinish="onFinish" :round="{show: true, style: '10rpx'}">
						<template v-slot:begin>
							<view
								style="background-color: #E5673B; height: 100%; display: flex; justify-content: center; align-items: center; color: #fff;">
								<!-- <image style="height: 50rpx;width: 50rpx;" src="/static/wow.png"></image> -->
								<u-icon name="arrow-right-double" size="50"> </u-icon>
							</view>
						</template>
						<template v-slot:end>
							<view
								style="background-color: #1BA035; height: 100%; display: flex; justify-content: center; align-items: center; color: #fff; ">
								<!-- <image style="height: 50rpx;width: 50rpx;" src="/static/smile.png"></image> -->
								<u-icon name="checkmark" size="50"> </u-icon>
							</view>
						</template>
					</wo-slider>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	let page;
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	import {
		searchKeyWords
	} from "@/api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos', 'currentUserState']),
			...mapState('user', ['currentSchoolId']),
			...mapState('config', ['alumniOnly', 'privacyPopup', 'firstPage']),
			...mapState('popup', ['searchTimes']),
		},
		data() {
			return {
				current: 1, //0表示显示板块搜索结果，1表示帖子搜索结果
				topicClssList: [],
				plateList: [],
				contentList: [],
				loadStatus: "nomore",
				loadContentStatus: "nomore",
				plateKeyword: "",
				contentKeyword: "",
				form: {
					mode: 1,
					type: 2,
					condition: '',
					pageNum: 1,
					pageSize: 5,
					sortType: 'createTime'

				},
				contentForm: {
					mode: 1,
					type: 1,
					condition: '',
					pageNum: 1,
					pageSize: 5,
					sortType: 'createTime'
				},
				showExaminePopup: 0,
				sliderMask: 0,
				needShowSilde: 1,
			};
		},
		onLoad(options) {
			this.current = options.current;
			if (this.current === '0') {
				this.plateKeyword = options.keyword;
			}
			if (this.current === '1') {
				this.contentKeyword = options.keyword;
				this.getContentList(0)
			}
			if (this.current === undefined) {
				this.current = 1
			} else {
				this.contentList = [];
			}
		},
		methods: {
			...mapMutations('popup', {
				increaseSearchTimes: 'increaseSearchTimes'
			}),
			getMore(type) {
				if (type === 0) {
					// 加载更多帖子
					this.contentForm.pageNum += 1
					console.log(this.contentForm.pageNum);
					this.getContentList(1) //flag = 1
				} else if (type === 1) {
					// 加载更多板块
					this.form.pageNum += 1
					// console.log(this.contentForm.pageNum);
					this.getPlateList(1) //flag = 1
				}
				// console.log('####');
			},
			async getPlateList(flag) {
				if (this.plateKeyword == '') {
					this.plateList = []
					return
				}
				this.loadStatus = "loading";
				if (flag === 0) {
					this.plateList = [] // 直接搜索
					this.form.pageNum = 1
				}
				// this.plateList = [];
				const conditions = {
					name: this.plateKeyword,
					schoolId: this.currentSchoolId,
					searchUserId: this.userInfos.userId
				}
				this.form.condition = JSON.stringify(conditions);
				console.log('form', this.form);
				const data = await searchKeyWords(this.form);
				console.log('res data', data)
				const {
					total,
					records,
					current
				} = data.data
				if (data.code === 200) {
					if (records.length > 0) {
						if (flag === 0) {
							// 直接搜索
							this.plateList = records
							// console.log('########33')
							console.log('plateList', this.plateList)
						} else if (flag === 1) {
							// 点击加载更多
							this.plateList = this.plateList.concat(...records)
						}
						if (current * this.form.pageSize >= total) {
							this.loadStatus = "nomore"
						} else {
							this.loadStatus = "loadmore"
						}
						// console.log(data.data.records);
						// this.plateList.push(...data.data.records);
						// this.loadStatus = "nomore";
					} else {
						this.loadStatus = "nomore";
					}
				} else {
					console.log("请求失败");
				}
			},
			async getContentList(flag) {
				// flag用来判断是直接搜索还是加载更多，flag=0:直接搜索
				if (this.contentKeyword == '') {
					this.contentList = []
					return
				}
				this.loadContentStatus = "loading"
				if (flag === 0) {
					this.contentList = [] // 直接搜索
					this.contentForm.pageNum = 1
					//检测一下当前是第几次搜索
					if (this.searchTimes > 0 && this.firstPage.show_silde_mask == 1 && this.needShowSilde == 1) {
						this.sliderMask = 1
						return
					}
				}
				const conditions = {
					title: this.contentKeyword,
					schoolId: this.currentSchoolId,
					searchUserId: this.userInfos.userId
				}
				this.contentForm.condition = JSON.stringify(conditions)
				const data = await searchKeyWords(this.contentForm)
				// console.log('this.contentForm', this.contentForm);
				// console.log(data);
				if (data.code === 200) {
					const {
						total,
						records,
						current
					} = data.data
					if (records.length > 0) {
						records.forEach(item => {
							if (item.contentUrl !== null && item.contentUrl !== "") {
								item.contentUrl = item.contentUrl.split(",")
							}
						})
						// console.log('res data', data)
						if (flag === 0) {
							// 直接搜索
							this.contentList = records
							this.increaseSearchTimes()
							this.needShowSilde = 1
							// console.log('contentList', this.contentList)
						} else if (flag === 1) {
							// 点击加载更多
							this.contentList = this.contentList.concat(...records)
						}
						if (current * this.contentForm.pageSize >= total) {
							this.loadContentStatus = "nomore"
						} else {
							this.loadContentStatus = "loadmore"
						}
					} else {
						this.loadContentStatus = "nomore"
					}
				} else {
					console.log("请求失败");
				}
			},
			toTopic(id) {
				uni.navigateTo({
					url: "/package_task/pages/bbs/topic/detail?id=" + id
				})
			},
			async toPost(id) {
				if (this.alumniOnly.search_alumni_only == 1) {
					//先进行隐私授权判断
					if (this.privacyPopup.needAuthorization) {
						let needPrivacyAuthorization = await this.judgePrivacySetting()
						if (needPrivacyAuthorization == true) {
							return
						}
					} else {
						console.log("已授权，不需要调用")
					}
					let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
						.currentSchoolId,
						1)
					if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
						return
					}
					if (permissionCheckResult != 0) {
						this.showExaminePopup = 1
						return
					}
				}
				uni.navigateTo({
					url: "/pages/post/detail?id=" + id
				})
			},
			async tabAlumniOnlyPost(content) {
				if (content.alumniOnly && content.alumniOnly == 1 && this.currentUserState != 2) {
					//先进行隐私授权判断
					if (this.privacyPopup.needAuthorization) {
						let needPrivacyAuthorization = await this.judgePrivacySetting()
						if (needPrivacyAuthorization == true) {
							return
						}
					} else {
						console.log("已授权，不需要调用")
					}
					let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
						.currentSchoolId,
						1)
					if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
						return
					}
					if (permissionCheckResult != 0) {
						this.showExaminePopup = 1
						return
					}
				}
				uni.navigateTo({
					url: "/pages/post/detail?id=" + content.contentId
				})
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			onFinish() {
				let that =this
				setTimeout(function() {
					that.sliderMask = 0;
					that.needShowSilde = 0;
					that.getContentList(0)
				}, 500);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.post-item {
		background: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;

		.post-title {
			padding: 10rpx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
		}

		.post-content {
			margin-top: 20rpx;
			padding: 10rpx;
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;

			.text {
				font-size: 27rpx;
				color: #999;
			}

			.image {
				display: block;
				max-height: 200rpx;
				border-radius: 5px;
			}

		}

		.p-footer {
			display: flex;
			justify-content: space-between;
			align-items: baseline;
			padding: 10rpx;

			.interactInfo {
				display: flex;
				flex-direction: row;
				align-items: baseline;

				.p-item {
					padding-right: 10rpx;
					margin: 0 auto;
					color: #616161;
					display: flex;
					align-items: center;

					.count {
						margin-left: 10rpx;
						font-size: 28rpx;
					}
				}
			}

			.timeInfo {
				display: flex;
				align-items: center;

				.center {
					flex: 1;
					display: flex;
					flex-direction: column;
					font-size: 24rpx;
					color: #999;
				}
			}
		}


	}



	.post-text {
		display: flex;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
		white-space: pre-wrap;
		overflow: hidden;
	}

	.discuss-title {
		height: 30rpx;
		line-height: 30rpx;
		border-radius: 0rpx;
		font-size: 30rpx;
		color: #000000;
		text-align: center;
		font-weight: bold;
	}

	.u-search-box {
		padding: 30rpx 20rpx;
	}

	.swiper {
		height: calc(100vh - 80rpx);
	}

	.grid-text {
		margin-top: 20rpx;
		font-size: 12px;
	}

	.page-view {
		padding: 16rpx;
	}

	// 板块列表
	.topic-item {
		border: 1rpx;
		display: flex;
		background-color: #fff;
		padding: 20rpx;

		.right {
			margin-left: 20rpx;
			display: flex;
			flex-direction: column;

			.desc {
				font-size: 25rpx;
				color: #999;
				margin: 10rpx 0;
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

	.page-mask {
		height: 100vh;
		width: 100vw;
		top: 0;
		left: 0;
		position: fixed;
		z-index: 1001;
		background-color: rgba(0, 0, 0, 0.8);
	}
</style>