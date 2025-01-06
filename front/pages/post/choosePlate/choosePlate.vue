<template>
	<view>
		<view class="u-search-box">
			<u-search placeholder="输入名称搜索话题" v-model="plateKeyword" @change="getPlateList(0)" :show-action="false">
			</u-search>
		</view>
		<view class="topic-item" @click="chooseTopic(item.plateId, item.name)" v-for="(item, index) in plateList"
			:key="index">
			<u-image class="cover-img" width="120rpx" height="120rpx" border-radius="10rpx" :src="item.portraitUrl">
			</u-image>
			<view class="center">
				<view>{{ item.name.substring(0, 10) }}</view>
				<view class="desc">{{ item.introduction | replace }}</view>
			</view>
			<view class="count-box">
				<text>{{ item.contentNum }}圈友</text>
				<text>{{ item.contentNum }}动态</text>
			</view>
		</view>
		<!-- 加载状态 -->
		<block v-if="plateList.length === 0 && loadStatus == 'nomore'">
			<u-empty text="暂无相关话题" mode="favor"></u-empty>
		</block>
	</view>
</template>

<script>
	import {
		getClassLists,
		getPlateLists,
		searchKeyWords
	} from '@/api/index.js'
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	export default {

		data() {
			return {
				plateList: [],
				loadStatus: 'loadmore',
				page: 1,
				plateForm: {
					mode: 1,
					type: 2,
					condition: '',
					pageNum: 1,
					pageSize: 5,

				},
				plateKeyword: "",
				loadStatus: "nomore",
			};
		},
		computed: {
			...mapState('user', ['currentSchoolId']),
		},
		onLoad(options) {
			this.getTopicList();
		},
		filters: {
			replace(str) {
				str = str.replace(/\n/g, '');
				return str.substring(0, 40);
			}
		},
		onReachBottom() {
			this.page++;
			this.getTopicList();
		},
		methods: {
			async getTopicList() {
				let form = {
					schoolId: this.currentSchoolId,
					cateId: 0,
				};
				this.plateList = await getPlateLists(form);
				console.log(this.plateList)
			},
			chooseTopic(id, name) {
				let pages = getCurrentPages(); //获取所有页面栈实例列表
				let nowPage = pages[pages.length - 1]; //当前页页面实例
				let prevPage = pages[pages.length - 2]; //上一页页面实例
				console.log(id);
				console.log(name);
				prevPage.$vm.form.plateId = id;
				prevPage.$vm.plateName = name;
				uni.navigateBack();
			},
			async getPlateList(flag) {
				if (this.plateKeyword == '') {
					this.plateList = []
					return
				}
				this.loadStatus = "loading";
				if (flag === 0) {
					this.plateList = [] // 直接搜索
					this.plateForm.pageNum = 1
				}
				// this.plateList = [];
				const conditions = {
					name: this.plateKeyword,
					schoolId:this.currentSchoolId
				}
				this.plateForm.condition = JSON.stringify(conditions);
				const data = await searchKeyWords(this.plateForm);
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
						if (current * this.plateForm.pageSize >= total) {
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
			}
		}
	};
</script>

<style lang="scss" scoped>
	.u-search-box {
		padding: 30rpx 20rpx;
	}

	.topic-item {
		display: flex;
		align-items: center;
		background-color: #fff;
		padding: 30rpx;
		border-bottom: 1px solid #f5f5f5;

		.cover-img {
			margin-right: 20rpx;
		}

		.center {
			flex: 1;

			.desc {
				font-size: 20rpx;
				color: #999;
			}
		}

		.count-box {
			display: flex;
			flex-direction: column;
			font-size: 20rpx;
			color: #999;
			margin-left: 20rpx;
		}
	}
</style>
