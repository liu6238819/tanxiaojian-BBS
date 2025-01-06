<template>
	<view>
		<!-- 时光机 -->
		<u-sticky bgColor="#fff" z-index="998">
			<view>
				<view style="background-color: #F5F5F5;height: 5rpx;"></view>
				<view @click="onTapTimeMachine"
					style="display: flex;align-items: center;justify-content: center;background-color: #ffffff; height: 60rpx;">
					<image src="../../static/ufo.png" style="height: 40rpx; width: 40rpx;"></image>
					<view style="font-size: 26rpx; color: #4f64b1;">
						搭乘时光机，带你回到过去
						<text class="lg text-gray cuIcon-right" style="color: #4f64b1;"></text>
					</view>
				</view>
			</view>
			
		</u-sticky>
		<block v-if="contentsByDay.length > 0">
			<view v-for="oneDay in contentsByDay">
				<view style="display: flex; align-items: center;">
					<view style="padding: 15rpx;font-weight: bold;font-size: 24prx;">{{oneDay.date | date('yyyy-mm-dd')}}</view>
<!-- 					<view class="line" style="width: 75%;"></view> -->
				</view>
				<view v-for="content in oneDay.list">
					<view class="post-item" @click="jump(content)">
						<!-- 顶部 -->
						<view style="font-size: 24rpx; color: #4b4b4b;">
							<text style="margin-right: 20rpx;"> {{content.commentNum}}评论</text>
							<text style="margin-right: 20rpx;"> {{content.upNum}}点赞</text>
							<text> {{content.commentNum*5 + content.upNum*5 +content.realReadNum*3 }}热度</text>
						</view>
						<!-- 中部 -->
						<view style="display: flex; justify-content: space-between; align-content: center; margin: 10rpx 0;">
							<view class="content-text" v-if= "content.contentUrls.length==0">
								<view v-if="content.title!=null &&content.title!=''">{{content.title}}</view>
								<view>{{content.contentText}}</view>
							</view>
							<view class="content-text" v-if= "content.contentUrls.length>0 &&content.contentType != 2 
							&& content.isSpecial != 3 && content.isSpecial != 4" style="width: 65%; height: 100rpx;">
								<text v-if="content.title!=null &&content.title!=''">{{content.title}}</text>
								<text>{{content.contentText}}</text>
							</view>
							<image v-if= "content.contentUrls.length>0 &&content.contentType != 2 
							&& content.isSpecial != 3 && content.isSpecial != 4" mode="aspectFill"
							style="width: 180rpx ; height: 120rpx;" :src="content.contentUrls[0]"></image>
						</view>
						<!-- 底部 -->
						<view style="display: flex; justify-content: space-between; align-content: center;">
<!-- 							<view style="background-color: #c1cfdb; color: #4f64b1; height: 45rpx;
							 font-size: 24rpx; padding: 5rpx 15rpx; border-radius: 5rpx;">#{{content.plateName}}</view> -->
							 <view class="" style="background-color: #f0f0f5; color: #68c778; height: 45rpx;
							  font-size: 24rpx; padding: 5rpx 15rpx; border-radius: 5rpx;">#{{content.contentTypeLabel}}</view>
							<view style="display: flex; justify-content: space-between; align-content: center;">
								<view>
									<image style="height: 40rpx ; width: 40rpx; border-radius: 50%;" :src="content.headimgUrl"></image>
								</view>
								<view style="color: #4b4b4b; font-size: 24rpx;margin-left: 10rpx;">
									<text class="time">{{Date.parse(content.createTime)| timeFrom }}</text>
								</view>
							</view>
						</view>
					</view>
			
				</view>
			</view>
		</block>

		<block v-if="contentsByDay.length === 0&&loadStatus!=null&&loadStatus=='nomore'">
			<u-empty margin-top="100" text="暂无相关帖子" mode="favor"></u-empty>
		</block>
		<block v-else-if="loadStatus == 'loadmore'||loadStatus == 'nomore'">
			<view style="margin: 30rpx 0;">
				<u-loadmore :status="loadStatus" :load-text="loadText" @loadmore='loadMore()' />
			</view>
		</block>
		<!-- 显示tabBar中的红色圆点(消息提示) -->
		<q-tabbar :active="1" :count="msgCount" :markState="markState"></q-tabbar>
		<u-select v-model="timeMachine" mode="mutil-column-auto" :list="timeMachineList" @confirm="confirmTimeMachine">
		</u-select>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>
	</view>
</template>

<script>
	import tabbar from '@/utils/tabbar.js';
	import localData from '../../utils/data.js';
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		getUserStateBySchool,
		reqHistoryPostList,
		getOneBbsConfig
	} from "../../api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
			...mapState('remind', ['msgCount']),
			...mapState('remindWSS', ['markState']),
			...mapState('config', ['firstPage']),
		},
		data() {
			return {
				contents: [],
				loadStatus: 'loadmore',
				popup: tabbar.popup,
				historyHotForm: {
					pageNum: 0,
					pageSize: 10,
					userId: '',
					requestType: 'historyHot',
					schoolId: '',
					duration: 0,
				},
				loadText: {
					loadmore: '点击加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				contentTypeList: [],
				//时光机相关
				timeMachineState: 0,
				timeMachine: false,
				timeMachineList: [],
				contentsByDay: [],


			}
		},
		async onLoad(options) {
			this.historyHotForm.schoolId = this.currentSchoolId
			this.contentTypeList =localData.contentTypeList
			await this.getTimeMachinePosts();
		},
		methods: {
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
			
			}),
			async getTimeMachinePosts() { //请求最新的帖子列表
				this.historyHotForm.pageNum = this.historyHotForm.pageNum + 1;
				console.log('最新请求参数：')
				console.log(this.historyHotForm)
				const data = await reqHistoryPostList(this.historyHotForm);
				if (data.code === 200) {
					if (data.data.length > 0) {
						this.contents.push(...data.data)
						//按时间给帖子分组
						for (let i = 0; i < data.data.length; i++) {
							let item =data.data[i]
							if (item.contentUrl !== null && item.contentUrl !== undefined && item.contentUrl !== '') {
								item['contentUrls'] = item.contentUrl.split(',');
								if ((item.contentType == 2 || item.isSpecial == 3 || item.isSpecial == 4) && item
									.contentUrls.length > 1) {
									item.videoId = item.contentUrls[item.contentUrls.length - 1]
								}
							} else {
								item['contentUrls'] = [];
							}
							//帖子类型标签
							for (let k = 0; k < localData.contentTypeList.length; k++) {
								if (item.contentType<=5 &&localData.contentTypeList[k].type==5) {
									item.contentTypeLabel = localData.contentTypeList[k].name
									break;
								}
								else if(item.contentType ==localData.contentTypeList[k].type){
									item.contentTypeLabel = localData.contentTypeList[k].name
									break;
								}
								else{
									continue;
								}
							}
							// let tempContentDay = new Date(item.createTime).toLocaleDateString();
							// let tempDate = tempContentDay.split("/");
							// let contentDay = tempDate[0] + '-' + tempDate[1] + '-' + tempDate[2];
							let contentDay = new Date(item.createTime).toLocaleDateString();
							console.log('帖子时间', contentDay)
							//首次判断
							if (this.contentsByDay.length == 0) {
								let contentListObject = {
									date: contentDay,
									list: []
								}
								contentListObject.list.push(item)
								this.contentsByDay.push(contentListObject)
								console.log("首次触发")
								continue;
							}
							//非首次判断
							else {
								for (let j = 0; j < this.contentsByDay.length; j++) {
									//已存在当天的热帖
									if (this.contentsByDay[j].date == contentDay) {
										this.contentsByDay[j].list.push(item)
										break;
									}
									//一直判断到最后一个
									else if (j < this.contentsByDay.length - 1) {
										continue;
									}
									//没有就新建一个结构
									else {
										let contentListObject2 = {
											date: contentDay,
											list: []
										}
										contentListObject2.list.push(item)
										this.contentsByDay.push(contentListObject2)
										break;
									}
								}
							}
						}
						//console.log("按日期分组的帖子", this.contentsByDay)
						this.loadStatus = 'loadmore'
					} else {
						this.loadStatus = 'nomore'
					}
					//console.log("历史热帖列表", this.contents);
				} else {
					console.log("历史热帖列表请求失败");
				}

			},
			//点击时光机
			async onTapTimeMachine() {
				this.timeMachineList = [];
				//获取时光机配置
				let param={
					schoolId:this.currentSchoolId,
					configType:'dailySelection'
				}
				let data = await getOneBbsConfig(param)
				let months = 0
				if (data.code==200) {
					let congfig = JSON.parse(data.data.configJson)
					months = congfig.timeMachineDuration
				}
				else{
					console.log("时光机配置获取失败");
				}
				this.buildTimeMachineList(months);
				this.timeMachine = true
			},
			async confirmTimeMachine(e) {
				console.log(e);
				this.timeMachineState = 1;
				this.contentsByDay = [];
				this.historyHotForm.pageNum = 0
				const date = new Date();
				const year = date.getFullYear();
				const month = date.getMonth() + 1;
				const day = date.getDate();
				const hours = date.getHours();
				//近期
				if (e[0].value == 100) {
					this.historyHotForm.duration = -(hours - e[1].value + 1)
					await this.getTimeMachinePosts()
				} else if (e[0].value > 0) {
					let selectDate = new Date(year + '/' + e[0].value + '/' + e[1].value); //转换为yyyy-MM-dd格式
					let nowDate = new Date(new Date(new Date().toLocaleDateString()).getTime());
					this.historyHotForm.duration = Math.abs(nowDate - selectDate) / 1000 / 60 / 60 - 24
					console.log(selectDate, nowDate,this.historyHotForm.duration)
					await this.getTimeMachinePosts()
				} else {
					let selectDate = new Date((year - 1) + '/' + (12 + e[0].value) + '/' + e[1]
					.value); //转换为yyyy-MM-dd格式
					let nowDate = new Date(new Date(new Date().toLocaleDateString()).getTime());
					this.historyHotForm.duration = Math.abs(nowDate - selectDate) / 1000 / 60 / 60 - 24
					console.log(selectDate, nowDate,this.historyHotForm.duration)
					await this.getTimeMachinePosts()
				}
			},
			//回到现在
			async returnNow() {
				uni.reLaunch({
					url: '/pages/index/index'
				})
			},
			//构造时光机list
			buildTimeMachineList(months) {
				const date = new Date();
				const year = date.getFullYear();
				const month = date.getMonth() + 1;
				const day = date.getDate();
				const hours = date.getHours();
				console.log("当前时间", year, month, day, hours)
				let timeObject1 = {
					value: 100,
					label: "近期",
					children: []
				}
				//24小时之内的
				for (let k = 0; k < 24; k++) {
					let childrenObject = {
						value: k + 1,
						label: k + 1 + "小时前",
					}
					timeObject1.children.push(childrenObject)
				}
				this.timeMachineList.push(timeObject1)
				//近一年内的
				for (let i = 0; i < (months*1+1); i++) {
					let year_month = 0
					let timeObject2 = {
						value: '',
						label: '',
						children: []
					}
					//今年月份
					if (month - i > 0) {
						year_month = month - i
						timeObject2.value = year_month
						timeObject2.label = year_month + "月"
					}
					//去年月份
					else {
						year_month = 12 + (month - i)
						timeObject2.value = month - i
						timeObject2.label = year - 1 + "年" + year_month + "月"
					}
					let Month_days = 0;
					//确定每月天数
					if (i == 0) {
						Month_days = day - 1
					} else if (year_month == 1 || year_month == 3 || year_month == 5 || year_month == 7 || year_month ==
						8 || year_month == 10 || year_month == 12) {
						Month_days = 31
					} else if (year_month == 2 && ((year % 4 == 0 && year % 100 != 0) || (year % 4 == 0 && year %
							400 ==
							0))) {
						Month_days = 29
					} else if (year_month == 2) {
						Month_days = 28
					} else {
						Month_days = 30
					}
					for (let j = 0; j < Month_days; j++) {
						let childrenObject = {
							value: j + 1,
							label: j + 1 + "日",
						}
						timeObject2.children.push(childrenObject)
					}
					console.log(Month_days, timeObject2)
					this.timeMachineList.push(timeObject2)

				}
				console.log(this.timeMachineList)
			},
			jump(content) {
				console.log(content.contentText);
				this.getContentDetail(content) //修改了store中的数据
				let url = ''
				url = '/pages/post/detail?id=' + content.contentId
				uni.navigateTo({
					url
				});
			},
			async getMore() {
				await this.getTimeMachinePosts()
			},
		},
		async onReachBottom() {
			if (this.loadStatus == 'loadmore') {
				this.loadStatus = 'loading'
				await this.getMore()
			}
		}

	}
</script>

<style>
	page {
		background-color: #F5F5F5;
	}
</style>
<style lang="scss" scoped>
	.line {
		border-bottom: 1px solid #bababa;
	}

	.post-item {
		background: #ffffff;
		padding-left: 20rpx;
		padding-right: 20rpx;
		padding-top: 10rpx;
		padding-bottom: 5rpx;
		margin-bottom: 10rpx;
	}
	
	.content-text{
		font-size: 30rpx;
		font-weight: bold;
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		white-space: pre-wrap;
		overflow: hidden;
		word-wrap: break-all;
		word-break: break-word;
	}
	.tag-style {
		height: 30rpx;
		padding: 10rpx;
		line-height: 30rpx;
		border-radius: 10rpx;
		font-size: 24rpx;
		background: #f0f0f5;
		color: #68c778;
		text-align: center;
		margin-right: 10rpx;
	}
</style>
