<template>
	<view>
		<image class="background-image2" :src="backImageUrl" :style="{ height: backImageHeight }">
		</image>

		<u-navbar :is-back="false" :background="navbarStyle" :border-bottom="false" :title="currentSchoolName"
			:title-bold="true" title-color="#000000" title-width="350">
			<view style="margin-left: 20rpx; display: flex; align-items: center;height: 50rpx; 
			border-radius: 25rpx; background-color: rgba(255,255,255,0.4); padding: 0 40rpx; color:#333333"
				@click="showSearch=true" v-if="currentSchoolId!=9999">
				<u-icon name="search" size="30"> </u-icon>
				<view style="color:#333333; font-size: 30rpx; margin-left: 5rpx;">
					搜索
				</view>
			</view>
			
			<view style="margin-left: 20rpx; display: flex; align-items: center;height: 50rpx;
			border-radius: 25rpx; background-color: rgba(255, 255, 255, 0.5); padding: 0 40rpx; color:#333333"
				@click="showSearch=true" v-if="currentSchoolId==9999">
				<u-icon name="search" size="30"> </u-icon>
				<view style="color:#333333; font-size: 30rpx; margin-left: 5rpx;">
					搜索
				</view>
			</view>

		</u-navbar>

		<scroll-view scroll-y="true" :style="{height: scrollViewHeight + 'px'}" @scrolltolower="onReachScrollBottum"
			refresher-enabled="true" @refresherrefresh="reloadPage" lower-threshold="1500">
			<!-- 校园热榜 -->
			<view class="topic-wrap" :style="{index: topicWrapIndex}">
				<!-- 提醒添加到我的小程序 -->
				<add-my-app top="0"></add-my-app>
				<view class="block-title" style="align-items: center;">
					<view class="text-container">
						<view class="text-bold"
							style="color: black; border-radius: 10rpx; padding: 5rpx 0;  z-index: 10; position: relative; font-size: 30rpx;">
							校园热榜
						</view>
						<view class="underline"></view>
					</view>
					<view style="font-weight: 500; font-size: 24rpx;">
						<text class=" text-black text-bold" @click="showMorePost">更多</text>
						<text class=" text-black text-bold cuIcon-right"></text>
					</view>
				</view>
				<view v-if="rankingState==1 ">
					<!-- 固定热榜 -->
					<block v-if="hotList && hotList.length<=3 ">
						<view v-for="(post,index) in hotList" @click="jumpFromHotRoTop(post)"
							style="display: flex; justify-content: space-between; align-items: center; height: 60rpx;">
							<view style="display: flex; align-items: center;">
								<view
									style="width: 40rpx; display: flex; align-items: center; justify-content: center;">
									<image style="height: 24rpx; width: 24rpx;" src="../../static/newUI/one.png"
										v-if="index==0"></image>
									<image style="height: 24rpx; width: 24rpx;" src="../../static/newUI/two.png"
										v-if="index==1"></image>
									<image style="height: 24rpx; width: 24rpx;" src="../../static/newUI/three.png"
										v-if="index==2"></image>
									<text class="text-black" style="font-weight: bold; font-size: 24rpx;"
										v-if="index>2">{{index+1}}</text>
								</view>
								<view v-if="post.title!=null &&post.title!=''" class="text-df  hot-post-text">
									{{post.title}} {{post.contentText}}
								</view>
								<view v-if="post.title==null ||post.title=='' " class="text-df  hot-post-text">
									{{post.contentText}}
								</view>
								<image style="height: 22rpx; width: 22rpx;" src="../../static/newUI/hot1.png"
									v-if="index==0"></image>
								<image style="height: 22rpx; width: 22rpx;" src="../../static/newUI/hot2.png"
									v-if="index==1"></image>
								<image style="height: 22rpx; width: 22rpx;" src="../../static/newUI/hot3.png"
									v-if="index==2"></image>
							</view>
							<view>
								<text class="text-df"
									style="color: #A3A2A8;">{{ (post.commentNum + post.upNum +post.realReadNum)*firstPage.hot_post_multiple}}</text>
							</view>

						</view>
					</block>
					<!-- 滚动热榜 -->
					<block v-if="hotList && hotList.length>3 ">
						<swiper :autoplay="true" :interval="2000" :duration="1000" :display-multiple-items="3"
							style="height: 150rpx;padding: 10rpx 0;" :circular="true" :vertical="true">
							<swiper-item v-for="(post,index) in hotList" :key="index" @click="jumpFromHotRoTop(post)"
								style="display: flex; justify-content: space-between; align-items: center; height: 60rpx;">
								<view style="display: flex; align-items: center;">
									<view
										style="width: 40rpx; display: flex; align-items: center; justify-content: center;">
										<image style="height: 24rpx; width: 24rpx;" src="../../static/newUI/one.png"
											v-if="index==0"></image>
										<image style="height: 24rpx; width: 24rpx;" src="../../static/newUI/two.png"
											v-if="index==1"></image>
										<image style="height: 24rpx; width: 24rpx;" src="../../static/newUI/three.png"
											v-if="index==2"></image>
										<text class="text-black" style="font-weight: bold; font-size: 24rpx;"
											v-if="index>2">{{index+1}}</text>
									</view>
									<view v-if="post.title!=null &&post.title!=''" class="text-df  hot-post-text">
										{{post.title}} {{post.contentText}}
									</view>
									<view v-if="post.title==null ||post.title=='' " class="text-df  hot-post-text">
										{{post.contentText}}
									</view>
									<image style="height: 22rpx; width: 22rpx;" src="../../static/newUI/hot1.png"
										v-if="index==0"></image>
									<image style="height: 22rpx; width: 22rpx;" src="../../static/newUI/hot2.png"
										v-if="index==1"></image>
									<image style="height: 22rpx; width: 22rpx;" src="../../static/newUI/hot3.png"
										v-if="index==2"></image>
								</view>
								<view>
									<text class="text-df"
										style="color: #A3A2A8;">{{ (post.commentNum + post.upNum +post.realReadNum)*firstPage.hot_post_multiple}}</text>
								</view>
							</swiper-item>
						</swiper>
					</block>

					<view v-if="topicList.length!=0 && topicState==1">
						<view class="block-title" style="margin-bottom: 10rpx;">
							<view
								style="color: white; background-color: blue;padding: 5rpx 10rpx;border-radius: 10rpx; font-size: ;">
								热议话题
							</view>
						</view>
						<view style="display: flex; flex-wrap:wrap">
							<view v-for="topic in topicList" style="margin-right: 15rpx;">
								<text v-if="topic.name" class="discuss-title"
									@tap.stop="toTopic(topic)">#{{ topic.name }}
								</text>
							</view>
						</view>

					</view>
					<!-- 置顶帖 -->
					<view style="margin-bottom: 10rpx;">
						<view class="topContent" v-if="content.contentState==3||content.contentState==8"
							v-for="(content, index1) in topContents" :key="content.contentId">
							<view class="">
								<view class="post-item-top-user" @click="jumpFromHotRoTop(content)">
									<view class="center" style="flex-direction: row; align-items: center;">
										<text v-if="content.contentState==2" class="official text-blue">【校内置顶】：</text>
										<text v-if="content.contentState==3" class="official">【话题置顶】</text>
										<text v-if="content.contentState==8" class="official text-blue">【官方通告】：</text>
										<text class="top_post_text"
											style="font-size: 26rpx; color: black; font-weight: 400;">{{content.title}}</text>
									</view>
									
								</view>
							</view>
							<rich-text @click="jumpFromHotRoTop(content)"
								v-if="content.contentText &&content.postsState==1 &&topPostsState==1" class="post-text"
								:style="{textAlign:'left'}" :nodes="content.contentText">
							</rich-text>
						</view>
					</view>

				</view>

			</view>
			<!-- 置顶广告 -->
			<view v-if="topSwiperList.length>=5" style="padding: 0rpx 20rpx;">
				<u-swiper :list="topSwiperList" :bg-color="swiperBgColor" :height="145" :autoplay="true"
					@click='onTabTopAdv0507()' mode="none"></u-swiper>
			</view>
			<view v-if="topSwiperList.length>0 && topSwiperList.length<5"
				style="display: flex; justify-content: space-between; padding: 0rpx 20rpx; height: 145rpx;">
				<block v-if="topSwiperList.length == 1">
					<image :lazy-load="true" mode="aspectFill" style="height: 145rpx; border-radius: 8rpx;width: 100%;"
						:src="topSwiperList[0].image" @click='onTabTopAdv0507(0)'></image>
				</block>
				<block v-if="topSwiperList.length == 2">
					<image :lazy-load="true" style="height: 145rpx; border-radius: 8rpx; width: 49%;"
						v-for="(topAdv, index) in topSwiperList" :key="index" mode="aspectFill" :src="topAdv.image"
						@click='onTabTopAdv0507(index)'></image>
				</block>
				<block v-if="topSwiperList.length == 3">
					<image :lazy-load="true" style="height: 145rpx; border-radius: 8rpx; width: 32%;"
						v-for="(topAdv, index) in topSwiperList" :key="index" mode="aspectFill" :src="topAdv.image"
						@click='onTabTopAdv0507(index)'></image>
				</block>
				<block v-if="topSwiperList.length == 4">
					<image :lazy-load="true" style="height: 145rpx; border-radius: 8rpx; width: 24%;"
						v-for="(topAdv, index) in topSwiperList" :key="index" mode="aspectFill" :src="topAdv.image"
						@click='onTabTopAdv0507(index)'></image>
				</block>
			</view>
			<view style="margin: 20rpx 20rpx;">

				<view style="width: 100%;">
					<view class="type-tabs" style="position: sticky; top:0; z-index: 998;">
						<u-tabs :list="contentTypeList" font-size="30" name="type_name" bg-color="#ffffff"
							:current="contentTypeIndex" @change="chooseContentTypeNew" bar-height="10" height="100"
							inactive-color="#606266" active-color="#333333" :bar-style="barStyle" gutter="20">
						</u-tabs>
					</view>
					<!-- <u-tabs :list="tabList" font-size="30" name="cate_name" bg-color="#fff" :current="current" @change="tabChange">
							</u-tabs> -->
					<!-- 最新 -->
					<view v-if="current === 0">
						<post-list :contents="homeContentList" :loadStatus="loadStatus" :needJump=1 @loadmore='getMore'>
						</post-list>
					</view>
				</view>
			</view>
		</scroll-view>


		<view class="reload-page" @click="reloadPage">
			<!-- <text class="cuIcon-pullup" style="font-size: 60rpx;"></text> -->
			<u-icon name="reload" size="40"> </u-icon>
		</view>

		<view class="timeMachineIcon" @click="onTapTimeMachine">
			<image style="width: 40rpx;height: 40rpx;" src="../../static/calendar.png"></image>
		</view>

		<!-- <text class="cuIcon-pullup reload-page" style="font-size: 60rpx;"></text> -->

		<u-popup v-model="showSearch" mode="center" border-radius="20" width="96%" :zoom="true">
			<view style="width: 100%; text-align: center; font-weight: bold; font-size: 32rpx;margin-top: 20rpx;">帖子搜索
			</view>
			<view class="u-search-box">
				<u-search placeholder="输入帖子关键词" v-model="contentKeyword" @search="toSearch" :action-style="searchStyle"
					@custom="toSearch"></u-search>
			</view>
		</u-popup>



		<!-- 返回顶部 -->
		<!-- <q-back-top></q-back-top> -->
		<u-popup v-model="showSchool" mode="center" border-radius="20" height="40%" width="80%" :zoom="true">
			<view class="u-search-box">
				<u-search placeholder="输入名称搜索学校" v-model="keyword" @change="getSchool" :show-action="false"></u-search>
			</view>
			<scroll-view class="share-type" @click="chooseSchool(index)" v-for="(school, index) in schoolList">
				<view class="type-item">
					<u-icon class="icon" size="40" name="map" color="#1aa3ff"></u-icon>
					<text>{{school.schoolName}}</text>
				</view>
			</scroll-view>
			<!-- 加载状态 -->
			<block v-if="schoolList.length === 0 && loadSchoolStatus == 'nomore'">
				<u-empty text="暂无相关学校" mode="favor"></u-empty>
			</block>
		</u-popup>
		<!-- 显示tabBar中的红色圆点(消息提示) -->
		<q-tabbar :active="0" :count="msgCount" :markState="markState"></q-tabbar>
		<!-- 发布按钮 -->
		<!-- <image @click="onTab" class="add-icon" src="/static/tabbar/add.png"></image> -->
		<!-- 发布弹窗 -->
		<q-popup v-model="showPopup">
			<view class="handle-wrap">
				<!-- 动态和投票 -->
				<view @click="handleJump(item)" class="item" v-for="(item,index) in popup" :key="index">
					<image mode="widthFix" class="icon" :src="item.icon"></image>
					<text class="txt">{{item.text}}</text>
				</view>
			</view>
			<view class="handle-close">
				<q-icon @click="onClose" size="50rpx" name="q-Close"></q-icon>
			</view>
		</q-popup>

		<!-- 更多热榜弹窗 -->
		<u-popup v-model="showMoreHotPost" mode="center" closeable="true" border-radius="20" height="60%" width="80%"
			:zoom="true">
			<view style="position: relative; height: 90%;">
				<view style="padding-top: 70rpx;">
					<view style="display: flex; justify-content: space-around;">
						<view v-if="moreHotListIndex==1 ||moreHotListIndex==2"
							style="font-weight: bold; color: white; background-color: grey; padding: 5rpx 20rpx;border-radius: 10rpx;"
							@click="changeMoreHotList(3)">今日榜
						</view>
						<view v-if="moreHotListIndex==3"
							style="font-weight: bold; color: white; background-color: #2979ff; padding: 5rpx 20rpx;border-radius: 10rpx;"
							@click="changeMoreHotList(3)">今日榜
						</view>
						<view v-if="moreHotListIndex==2 ||moreHotListIndex==3"
							style="font-weight: bold; color: white; background-color: grey; padding: 5rpx 20rpx;border-radius: 10rpx;"
							@click="changeMoreHotList(1)">三日榜
						</view>
						<view v-if="moreHotListIndex==1"
							style="font-weight: bold; color: white; background-color: #2979ff; padding: 5rpx 20rpx;border-radius: 10rpx;"
							@click="changeMoreHotList(1)">三日榜
						</view>
						<view v-if="moreHotListIndex==1 ||moreHotListIndex==3"
							style="font-weight: bold; color: white; background-color: grey; padding: 5rpx 20rpx;border-radius: 10rpx;"
							@click="changeMoreHotList(2)">周榜
						</view>
						<view v-if="moreHotListIndex==2"
							style="font-weight: bold; color: white; background-color: #2979ff; padding: 5rpx 20rpx;border-radius: 10rpx;"
							@click="changeMoreHotList(2)">周榜
						</view>
					</view>
				</view>
				<scroll-view style=" margin-left: 10rpx; margin-right: 10rpx; width: 96%;;
				 padding: 10rpx 15rpx; margin-top: 30rpx;  height: 80%; background-color: #ffffff; " scroll-y="true">
					<view v-for="(post,index) in moreHotList" @click="jumpFromHotRoTop(post)" style="display: flex; justify-content: space-between;
							 align-items: center; height: 50rpx; margin-bottom: 10rpx;">

						<view v-if="post.title!=null &&post.title!=''" class="text-df  more-hot-post-text">
							<text class="text-black">{{index+1}}.</text>
							{{post.title}}
						</view>
						<view v-if="post.title==null ||post.title=='' " class="text-df  more-hot-post-text">
							<text class="text-black">{{index+1}}.</text>
							{{post.contentText}}
						</view>
						<view>
							<text class=" text-red  cuIcon-hotfill"></text>
							<!-- 							<text
								class="text-df text-bold text-red">热度：{{post.commentNum*5 + post.upNum*5 +post.realReadNum*3 }}</text> -->
							<text
								class="text-df text-bold text-red">热度：{{ (post.commentNum + post.upNum +post.realReadNum)*firstPage.hot_post_multiple}}</text>

						</view>

					</view>
				</scroll-view>
				<view
					style="font-weight: bold; color: #2979ff; width: 100%; text-align: center; absolute: fixed; bottom: 0; "
					@click="toDailyHot">查看更多每日热帖 >></view>
			</view>


		</u-popup>
		<!-- 弥补底部陷入 -->
		<!-- <view style="height: 52px;" class="blank-fill"></view> -->
		<!-- 首日登录弹窗 -->
		<daily-login-popup :pageShowDaliyPopup="showDailyPopup"></daily-login-popup>
		<u-select title="请选择日期" v-model="timeMachine" mode="mutil-column-auto" :list="timeMachineList"
			@confirm="confirmTimeMachine">
		</u-select>

		<view class="page-mask" v-if="showAdNum==1 &&swiperList.length>0">
			<view style="display: flex; flex-direction:column; width: 100%; justify-content:center;
			text-align: center; position: relative; top: 25%; ">
				<view style="padding: 0;">
					<u-swiper :list="swiperList" :effect3d="true" :bg-color="swiperBgColor" :img-mode="heightFix"
						:height="700" :autoplay="false" @click='onTabAdv()' mode="none" effect3d-previous-margin="100">
					</u-swiper>
				</view>
				<view v-if="swiperList.length>1" style=" color: #dedede; font-weight: bold; margin-top:10rpx">滑 动 查 看 更
					多
				</view>
				<view style="text-align: center display: block; margin-top:20rpx" v-if="adMaskClose==1"
					@click="hideAdMask">
					<u-icon name="close-circle" size="80" color="#dedede"></u-icon>
					<!-- <u-checkbox @change="checkboxChange" v-model="popupChecked">1日内不再提醒</u-checkbox> -->
				</view>

			</view>
		</view>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>
		<view v-if="!currentSchoolId && showIndexMask==1" style="position: fixed; z-index: 10000000;top:0; height: 100vh; width: 100vW; background-color: #fff;
		display: flex; justify-content: center; align-items: center; flex-direction: column;">
			<view>您还没有选择学校</view>
			<view style="color: blue;" @click="toChooseSchoolPage">点击选择</view>
		</view>
		<view v-if="showExaminePopup==1">
			<examine-popup v-if="currentSchoolState!=3" @returnHid='returnHide'></examine-popup>
		</view>
	</view>

</template>

<script>
	import postList from '../../components/post-list/post-list.vue';
	//import dailyLoginPopup from '../../components/daily_login_popup/daily_login_popup.vue';
	import addMyApp from '../../components/add_my_app/add_my_app.vue';
	import tabbar from '@/utils/tabbar.js';
	import localData from '../../utils/data.js';
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	import {
		reqContentList,
		getSchoolLists,
		searchKeyWords,
		getFocusedPlates,
		getUserStateBySchool,
		getContentById,
		getUserScore,
		getActionNumToday,
		reqHistoryPostList,
		getAdvList,
		getOneAdvById,
		getOneBbsConfig,
		getImageBase64ByContents,
		getHomeContentsImage,
		getAdvListWithImageBase64,
		getAdvList0507
	} from "../../api/index.js"

	export default {
		components: {
			postList,
			//dailyLoginPopup,
			addMyApp,

		},
		filters: {
			toFixedTwo(value) {
				return parseFloat(value).toFixed(2); // 转换为浮点数并保留两位小数
			}
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['schoolList']),
			...mapState('user', ['currentSchoolId']),
			...mapState('user', ['scoresFront', 'currentUserState', 'currentSchoolState', 'isLocalUser']),
			...mapState('remind', ['msgCount']),
			...mapState('remindWSS', ['markState']),
			...mapState('config', ['firstPage', 'needRelaunch', 'privacyPopup']),
			...mapState('content', ['homeContentList', 'currentContent', 'homeTopContentList']),
			...mapState('popup', ['showAdNum', 'showIndexMask', 'searchTimes']),
		},
		onPullDownRefresh() {
			uni.reLaunch({
				url: "../index/index"
			});
		},
		data() {
			return {
				keyword: '',
				contentKeyword: '',
				showSchool: false,
				showSearch: false,
				contents: [],
				searchContents: [],
				followPlatePost: [],
				topContents: [],
				timeForm: {
					pageNum: 0,
					pageSize: 15,
					plateId: '',
					userId: '',
					requestType: 'time',
					schoolId: '',
					contentType: '',
					queryTime: '',
					hideSecondHand: 0,
				},
				followForm: { //实际为最热列表
					pageNum: 0,
					pageSize: 5,
					plateId: '',
					userId: '',
					requestType: 'hot',
					schoolId: ''
				},
				topForm: {
					pageNum: 1,
					pageSize: 5,
					plateId: '',
					userId: '',
					requestType: 'top',
					schoolId: ''
				},
				schoolForm: {
					mode: 1,
					type: 5,
					condition: '',
					pageNum: 1,
					pageSize: 5,
				},

				loadStatus: 'loadmore',
				loadFollowStatus: 'loadmore',
				loadSchoolStatus: 'loadmore',
				loadContentStatus: 'loadmore',
				tabList: [{
						name: '最新'
					},
					{
						name: '最热'
					},
					// {
					// 	name: '关注'
					// }
				],
				current: 0,
				joinTopicList: [],
				currentSchoolName: '',
				popup: tabbar.popup,
				showPopup: false,
				showMoreHotPost: false,
				//侧边栏相关
				contentTypeList: [],
				contentTypeIndex: 0,
				rightScrollViewHeight: 300,
				topicWrapIndex: -1,
				topicState: 0,
				topPostsState: 0,
				rankingState: 0,
				//热榜相关
				hotList: [],
				moreHotList: [],
				moreHotListIndex: 1,
				hotPostState: 0,
				hotForm: { //实际为最热列表
					pageNum: 1,
					pageSize: 10,
					plateId: '',
					userId: '',
					requestType: 'hot',
					schoolId: '',
					duration: '',
				},
				moreHotForm: { //实际为最热列表
					pageNum: 0,
					pageSize: 5,
					plateId: '',
					userId: '',
					requestType: 'hot',
					schoolId: '',
					duration: '',
				},
				moreHotLoadText: {
					loadmore: '加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				moreHotLoadStatus: "loadmore",
				topicList: [],
				leftTabState: 1,
				leftTabTop: 30,
				tabTop: 0,
				noticeTop: 0,
				//首次登录弹窗相关
				tipTop: 30,
				showDailyPopup: 0,
				//自动生成短链相关
				urlLinkList: [],
				//总榜时间段
				durationAll: 0,
				//时光机相关
				timeMachineState: 0,
				timeMachine: false,
				timeMachineList: [],
				timeMachineForm: {
					pageNum: 0,
					pageSize: 5,
					userId: '',
					requestType: 'timeMachine',
					schoolId: '',
					contentType: '',
					duration: 0,
				},
				//开屏广告
				swiperList: [], //数据源
				swiperBgColor: '', //背景
				adMask: 0,
				adMaskClose: 0,
				jumpImmediately: 0,
				topSwiperList: [],
				imageForm: {
					pageNum: 0,
					pageSize: 5,
					plateId: '',
					schoolId: '',
					contentType: '',
					queryTime: '',
				},
				updateConfig: false,
				showExaminePopup: 0,
				barStyle: {
					backgroundColor: '#A05CFF',
				},
				searchStyle: {
					background: '#000000',
					width: '105rpx',
					height: '60rpx',
					borderRadius: '30rpx',
					fontSize: '28rpx',
					color: '#FFFFFF',
					lineHeight: '60rpx',
					textAlign: 'center'
				},
				navbarStyle: {
					// backgroundColor: 'transparent',
					backgroundImage: '',
					backgroundSize: '100% 100vh',
					backgroundRepeat: 'no-repeat',
					// backgroundPosition: 'center center',
				},
				backImageHeight: '600rpx',
				currentMiniAppName: '',
				backImageUrl: '',
				alreadyGetAdv: 0,
				alreadyGetHotPost: 0,
				alreadyGetTopPost: 0,
				UIColor: localData.UIColor,
				scrollViewHeight: 1000,
				backgroundStyle: {
					backgroundImage: '',
					backgroundSize: 'cover',
					backgroundRepeat: 'no-repeat',
				},
				sliderMask: 0,
				timeFormQueryTime: null,
			};
		},
		methods: {
			...mapMutations('user', {
				getSchoolList: 'getSchoolList'
			}),
			...mapMutations('user', {
				getCurrentSchoolId: 'getCurrentSchoolId'
			}),
			...mapMutations('user', {
				setScoresFront: 'setScoresFront',
				setCurrentUserState: 'setCurrentUserState',
				setCurrentUserType: 'setCurrentUserType',
				setBannedTime: 'setBannedTime'
			}),
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
				setHomeContentList: 'setHomeContentList',
				addHomeContentList: 'addHomeContentList',
				//1205之后
				addHomeContentText: 'addHomeContentText',
				addImageBase64List: 'addImageBase64List',
				clearImageBase64List: 'clearImageBase64List',
				setHomeContentImage: 'setHomeContentImage',
				//置顶帖相关
				setHomeTopContentList: 'setHomeTopContentList',
				unshiftHomeContentList: 'unshiftHomeContentList',
				addHomeContentAndFilter: 'addHomeContentAndFilter'

			}),
			...mapMutations('popup', {
				setShowAdNum: 'setShowAdNum',
				increaseShowAdNum: 'increaseShowAdNum',
				increaseSearchTimes: 'increaseSearchTimes'
			}),
			...mapMutations('config', {
				setNeedRelaunch: 'setNeedRelaunch',

			}),
			dealAnonymous(contents) { //处理匿名贴
				if (contents == null || contents.length == 0) return
				for (let i = 0; i < contents.length; i++) {
					if (contents[i].contentType == 3) {
						console.log(contents[i])
						//根据用户ID和帖子ID，生成该帖子下的唯一用户昵称
						let result = '隐士#';
						let code = ''
						for (var j = 0; j < 7; j++) {
							if (j % 2 == 0) {
								code += contents[i].contentId[j]
							} else {
								code += contents[i].userId[j]
							}
						}
						result = result + code
						contents[i].nickName = result
						//根据用户ID和帖子ID，生成该帖子下的唯一用户头像
						let num = code.replace(/[^\d]/g, '') //提取所有数字
						console.log(num)
						num = parseInt(num) % 50
						num = num + ''
						contents[i].headimgUrl = 'https://keming-bbs.oss-cn-shanghai.aliyuncs.com/user/anonymous/' + num +
							'.png'
					}
				}

			},
			async getMore() {
				if (this.current === 0) { //最新tab
					if (this.timeMachineState == 1) {
						await this.getTimeMachinePosts()
					} else {
						this.getNew()
						if (this.firstPage.image_use_base64 == 1) {
							this.getNewImgBase64()
						}
					}
				} else { //最热tab，关注已无意义
					this.getHot()
				}
			},
			async getNew() { //请求最新的帖子列表
				// console.log("帖子请求开始",new Date().getTime())
				this.timeForm.pageNum = this.timeForm.pageNum + 1;
				console.log('最新请求参数：')
				console.log(this.timeForm)
				if (!this.timeForm.schoolId) {
					console.log("没有schoolId")
					return
				}
				if (this.homeContentList.length > 0) {
					const time = new Date(this.homeContentList[this.homeContentList.length - 1]
						.createTime); // 获取帖子列表最后一个帖子的创建时间
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
				if (this.firstPage.hide_second_hand_tab == 1) {
					this.timeForm.hideSecondHand = this.firstPage.hide_second_hand_tab
				}
				if (this.timeForm.queryTime != this.timeFormQueryTime) {
					this.timeFormQueryTime = this.timeForm.queryTime
				} else {
					console.log("请求时间一致")
					return
				}
				const data = await reqContentList(this.timeForm);
				if (data.code === 200) {
					if (data.data.length > 0) {
						const stepData = data.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						//将头像和帖子图片制为空，等待本地缓存图片地址返回
						for (var i = 0; i < stepData.length; i++) {
							//头像处理
							stepData[i].headimgTempUrl = stepData[i].headimgUrl.toString() //临时头像
							stepData[i].headimgUrl = ''
							//帖子图片处理
							if (stepData[i].contentUrl !== null && stepData[i].contentUrl !== undefined && stepData[i]
								.contentUrl !== '') { //帖子有图
								//将帖子图片临时储存，contentUrls置为空数组
								stepData[i].contentTempUrls = stepData[i].contentUrl.split(',');
								stepData[i].contentUrls = [];
								if ((stepData[i].contentType == 2 || stepData[i].isSpecial == 3 || stepData[i]
										.isSpecial == 4) && stepData[i]
									.contentUrls.length > 1) {
									stepData[i].videoId = stepData[i].contentUrls[stepData[i].contentUrls.length - 1]
								}
							} else {
								stepData[i].contentUrls = []; //帖子无图
							}
						}
						this.addHomeContentList(stepData)

						//结束
						this.loadStatus = 'loadmore'
						//帖子头像和图片缓存处理
						for (var i = 0; i < stepData.length; i++) {
							let onePost = JSON.parse(JSON.stringify(stepData[i]))
							this.updatePostcontentUrls(onePost)
						}
						// console.log("缓存图片结束",new Date().getTime())
					} else {
						this.loadStatus = 'nomore'
					}
					//console.log("首页列表", this.homeContentList);
				} else {
					console.log("首页列表请求失败");
				}
				// console.log("整个方法",new Date().getTime())
			},
			async clearAndGetNew() { //清空帖子列表并请求最新的帖子列表

				this.timeForm.pageNum = this.timeForm.pageNum + 1;
				// //在查询第一页时，传入首页查询时间
				// if (this.timeForm.pageNum == 1) {
				const now = new Date(); // 获取当前时间
				const year = now.getFullYear(); // 年份
				const month = now.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
				const day = now.getDate(); // 日期
				const hour = now.getHours(); // 小时
				const minute = now.getMinutes(); // 分钟
				const second = now.getSeconds(); // 秒钟
				this.timeForm.queryTime = year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' +
					second;
				// }
				//首页隐藏闲置交易
				if (this.firstPage.hide_second_hand_tab == 1) {
					this.timeForm.hideSecondHand = this.firstPage.hide_second_hand_tab
				}
				console.log('最新请求参数：', this.homeContentList)
				if (!this.timeForm.schoolId || !this.timeForm.userId) {
					console.log("没有schoolId或用户id")
					return
				}
				console.log(this.timeForm)
				const data = await reqContentList(this.timeForm);
				if (data && data.code === 200) {
					if (data.data.length > 0) {
						const stepData = data.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						//将头像和帖子图片制为空，等待本地缓存图片地址返回
						for (var i = 0; i < stepData.length; i++) {
							//头像处理
							stepData[i].headimgTempUrl = stepData[i].headimgUrl.toString() //临时头像
							stepData[i].headimgUrl = ''
							//帖子图片处理
							if (stepData[i].contentUrl !== null && stepData[i].contentUrl !== undefined && stepData[i]
								.contentUrl !== '') { //帖子有图
								//将帖子图片临时储存，contentUrls置为空数组
								stepData[i].contentTempUrls = stepData[i].contentUrl.split(',');
								stepData[i].contentUrls = [];
								//视频贴不需要缓存处理
								if ((stepData[i].contentType == 2 || stepData[i].isSpecial == 3 || stepData[i]
										.isSpecial == 4) && stepData[i]
									.contentTempUrls.length > 1) {
									stepData[i].videoId = stepData[i].contentTempUrls[stepData[i].contentTempUrls
										.length - 1]
								}
							} else {
								stepData[i].contentUrls = []; //帖子无图
							}
						}
						this.setHomeContentList([])
						this.addHomeContentList(stepData)

						//1205改动，结束
						this.loadStatus = 'loadmore'
						//帖子头像和图片缓存处理
						for (var i = 0; i < stepData.length; i++) {
							let onePost = JSON.parse(JSON.stringify(stepData[i]))
							this.updatePostcontentUrls(onePost)
						}
						//放置置顶帖
						// console.log("呀哈哈哈哈哈",this.homeTopContentList)
						if (this.contentTypeIndex == 0) {
							this.unshiftHomeContentList(this.homeTopContentList)
						} else {
							this.unshiftHomeContentList([])
						}
					} else {
						this.loadStatus = 'nomore'
					}
					console.log("首页列表", this.homeContentList);
					if (this.timeForm.pageNum == 1 && this.showAdNum != 1) {
						uni.showToast({
							title: '刷新成功',
							icon: 'none'
						})
					}
				} else {
					this.setHomeContentList([])
					uni.showToast({
						title: '帖子请求失败，请检查网络或者重新进入小程序',
						icon: 'none'
					})
					console.log("首页列表请求失败");
				}

			},
			async getHot() { //请求最热的帖子列表
				this.followForm.pageNum = this.followForm.pageNum + 1;
				const data1 = await reqContentList(this.followForm);
				if (data1.code === 200) {
					if (data1.data.length > 0) {
						const stepData1 = data1.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						//this.dealAnonymous(stepData1)
						this.followPlatePost.push(...stepData1)
					} else {
						this.loadFollowStatus = 'nomore'
					}
				} else {
					console.log("最热列表请求失败");
					console.log(data1);
				}

			},
			async getTopContents() { //获取置顶帖this
				this.alreadyGetTopPost = 0
				let that = this
				this.topForm.schoolId = this.currentSchoolId
				const data = await reqContentList(this.topForm);
				if (data.code === 200 && data.data) {
					this.setHomeTopContentList([]);
					if (data.data.length > 0) {
						for (var i = 0; i < data.data.length; i++) {
							if (this.topPostsState == 1) {
								data.data[i].postsState = 1
							} else {
								data.data[i].postsState = 0
							}
						}
						this.topContents = []
						this.topContents.push(...data.data)
						//将校内置顶置于store中
						let homeTopContent = []
						for (var i = 0; i < this.topContents.length; i++) {
							if (this.topContents[i].contentState == 2) {
								homeTopContent.push(this.topContents[i])
							}
						}
						if (homeTopContent && homeTopContent.length > 0) {
							this.setHomeTopContentList(homeTopContent);
							this.unshiftHomeContentList(homeTopContent);
						}
					}
					console.log("置顶列表请求成功");
					//判断是否渲染完毕
					this.alreadyGetTopPost = 1
					if (this.alreadyGetAdv == 1 && this.alreadyGetHotPost == 1 && this.alreadyGetTopPost == 1) {
					}
				} else {
					console.log("置顶列表请求失败");

				}
			},
			tabChange(index) {
				this.current = index;
			},
			async chooseSchool_old(index) {
				this.current = 0;
				// this.contents = [];
				this.setHomeContentList([])
				this.followPlatePost = [];
				this.topContents = []
				this.loadStatus = 'loadmore';
				this.loadFollowStatus = 'loadmore';
				//修改store与本vue里的变量
				this.getCurrentSchoolId(this.schoolList[index].schoolId);
				this.currentSchoolName = this.schoolList[index].schoolName;

				//修改storage里的变量
				uni.setStorageSync("currentSchoolId", this.currentSchoolId);
				uni.setStorageSync("currentSchoolName", this.currentSchoolName);
				//获取关注板块
				let joinTopicForm = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				this.joinTopicList = await getFocusedPlates(joinTopicForm);
				// 获取置顶帖
				this.topForm.schoolId = this.currentSchoolId;
				await this.getTopContents();
				// 获取最新tab内容
				this.timeForm.schoolId = this.currentSchoolId;
				this.timeForm.pageNum = 0; //切换校区后从第一页开始请求
				this.getNew()

				//获取最热tab内容
				this.followForm.schoolId = this.currentSchoolId;
				this.followForm.pageNum = 0;
				this.getHot()

				this.showSchool = false;
			},
			//选择学校（2024.1.3更新）
			async chooseSchool(index) {
				this.current = 0;
				// this.contents = [];
				//热榜、首页和置顶帖置空
				this.setHomeContentList([])
				this.topContents = []
				this.hotList = []
				this.loadStatus = 'loadmore';

				//修改store与本vue里的变量
				this.getCurrentSchoolId(this.schoolList[index].schoolId);
				if (this.schoolList[index].anotherName) {
					this.currentSchoolName = this.schoolList[index].anotherName;
				} else {
					this.currentSchoolName = this.schoolList[index].schoolName;
				}
				//修改storage里的变量
				uni.setStorageSync("schoolId", this.currentSchoolId);
				uni.setStorageSync("currentSchoolName", this.currentSchoolName);

				//按学校id更新启动项配置
				await this.getSchoolFunctionConfig()
				//按学校id更新基础配置
				await this.getSchoolBasicConfig()
				this.showSchool = false;
			},
			//搜索学校对应的方法
			async getSchool() {
				const conditions = {
					schoolName: this.keyword
				}
				this.schoolForm.condition = JSON.stringify(conditions);
				const data = await searchKeyWords(this.schoolForm);
				if (data.code === 200) {
					if (data.data.records.length > 0) {
						console.log(data.data.records);
						this.getSchoolList(data.data.records);
					} else {
						this.getSchoolList(data.data.records);
						this.loadSchoolStatus = "nomore";
					}
				} else {
					console.log("请求失败");
				}
			},
			async getUserStateBySchool() {
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const stateData = await getUserStateBySchool(params)
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
			toSearch() {
				let c = 1;
				this.showSearch = false;
				uni.navigateTo({
					url: '/package_task/pages/bbs/topic/list?keyword=' + this.contentKeyword + '&current=' + c
				});
				this.contentKeyword = ''
			},
			//发布帖子相关方法
			onTab() {
				if (this.userInfos.userId) {
					this.showPopup = true;
				} else {
					uni.showToast({
						title: '请登录后发帖'
					})
				}
			},
			onClose() {
				this.showPopup = false;
			},
			handleJump(e) {
				uni.navigateTo({
					url: e.url
				})

				this.showPopup = false;
			},
			//废弃
			async chooseContentType(index, title) {
				if (this.contentTypeIndex === index) {
					return;
				}
				this.loadStatus = 'loadmore';
				this.contentTypeIndex = index;
				// this.setHomeContentList([])
				if (this.timeMachineState == 1) {
					this.timeMachineForm.pageNum = 0
					this.timeMachineForm.contentType = title.type
					let scrollTop = this.leftTabTop
					await this.clearAndGetTimeMachinePosts()
				} else {
					this.timeForm.pageNum = 0;
					this.timeForm.contentType = title.type
					// this.contents = []
					let scrollTop = this.leftTabTop
					await this.clearAndGetNew()
				}
				setTimeout(() => {
					uni.pageScrollTo({
						scrollTop: scrollTop,
						duration: 0,
					})
				}, 50)
			},
			async chooseContentTypeNew(index) {
				if (this.contentTypeIndex === index) {
					return;
				}
				this.loadStatus = 'loadmore';
				// this.setHomeContentList([])
				this.contentTypeIndex = index;
				let scrollTop = this.leftTabTop
				if (this.timeMachineState == 1) {
					this.timeMachineForm.pageNum = 0
					this.timeMachineForm.contentType = this.contentTypeList[index].type
					await this.clearAndGetTimeMachinePosts()
				} else {
					this.timeForm.pageNum = 0;
					this.timeForm.contentType = this.contentTypeList[index].type
					// this.contents = []
					this.clearAndGetNew()
					if (this.firstPage.image_use_base64 == 1) {
						this.clearAndGetNewImgBase64()
					}

				}
				setTimeout(() => {
					uni.pageScrollTo({
						scrollTop: scrollTop,
						duration: 0,
					})
				}, 50)
			},
			//调佣云函数-功能配置-课程评价(不再使用)
			async aboutfunctionConfig(functionName) {
				let that = this
				return new Promise((resolve, reject) => {
					wx.cloud.init({
						env: localData.envId,
						traceUser: true,
					})
					wx.cloud.callFunction({
						name: 'functionConfigSQL', //云函数的名称
						data: {
							functionName: functionName, //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							firstPageInfo: '',
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("配置项", nowdata)
							if (nowdata) {
								that.topicState = nowdata.firstPageInfo.topic_state;
								that.topPostsState = nowdata.firstPageInfo.top_posts_state;
								that.rankingState = nowdata.firstPageInfo.ranking_state;
								that.hotForm.duration = nowdata.firstPageInfo.duration;
								that.topicList = nowdata.firstPageInfo.topic_list;
								that.hotPostState = nowdata.firstPageInfo.hot_post_state;
								resolve(nowdata)
							}
						}
					})
				})
			},
			async getHotList() {
				this.alreadyGetHotPost = 0
				console.log("热榜请求参数", this.hotForm);
				const data = await reqContentList(this.hotForm);
				if (data.code === 200 && data.data) {
					if (data.data.length > 0) {
						this.hotList = []
						this.hotList.push(...data.data)
					}
					//判断是否渲染完毕
					this.alreadyGetHotPost = 1
					if (this.alreadyGetAdv == 1 && this.alreadyGetHotPost == 1 && this.alreadyGetTopPost == 1) {
						// this.changeBackImageHeight()
						// console.log("热榜");
					}
					console.log("热榜请求成功", this.hotList);
				} else {
					console.log("热榜列表请求失败");

				}
			},
			//获取更多热榜
			async getMoreHotList() {
				//今日榜不获取更多
				if (this.moreHotListIndex == 3) {
					return
				}
				this.moreHotForm.schoolId = this.currentSchoolId
				this.moreHotForm.pageNum = this.moreHotForm.pageNum + 1
				console.log("更多热榜请求参数", this.moreHotForm);
				const data = await reqContentList(this.moreHotForm);
				if (data.code === 200 && data.data) {
					if (data.data.length > 0) {
						this.moreHotList.push(...data.data)
						this.moreHotLoadStatus = 'loadmore'
					} else {
						this.moreHotLoadStatus = 'nomore'
					}
					console.log("更多热榜请求成功", this.moreHotList);
				} else {
					console.log("更多热榜列表请求失败");

				}
			},
			//三日榜和总榜切换
			async changeMoreHotList(index) {
				this.moreHotForm.pageNum = 0
				//今日榜
				if (index == 3) {
					this.moreHotListIndex = index
					this.moreHotList = this.hotList
					return
				}
				//三日榜
				else if (index == 1) {
					this.moreHotForm.duration = 3
				}
				//总榜
				else if (index == 2) {
					this.moreHotForm.duration = this.durationAll
				}
				this.moreHotListIndex = index
				this.moreHotList = []
				await this.getMoreHotList();
				await this.getMoreHotList();
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
			async jumpFromHotRoTop(content) {
				console.log('重新请求帖子详情');
				const data1 = await getContentById({
					contentId: content.contentId,
					userId: this.userInfos.userId
				})
				if (data1.code === 200) {
					data1.data.headimgTempUrl = data1.data.headimgUrl.toString() //临时头像
					data1.data.headimgUrl = ''
					//帖子图片处理
					if (data1.data.contentUrl !== null && data1.data.contentUrl !== '') {
						// data1.data['contentUrls'] = data1.data.contentUrl.split(',');
						data1.data.contentTempUrls = data1.data.contentUrl.split(',');
						data1.data.contentUrls = [];
					} else {
						data1.data['contentUrls'] = [];
					}
					if (data1.data.contentType == 4 || data1.data.isSpecial == 2) {
						let infos = data1.data.schoolInfos
						for (var i = 0; i < infos.length; i++) {
							if (infos[i].imgUrls) {
								infos[i].imgUrls = infos[i].imgUrls.split(',');
							} else {
								infos[i].imgUrls = []
							}
						}
					}
					// console.log('帖子详情');
					this.getContentDetail(data1.data) //修改了store中的数据
					//帖子头像和图片缓存处理
					this.updatePostcontentUrls(this.currentContent)
				} else {
					console.log(data1.message);
					return
				}
				// this.getContentDetail(content) //修改了store中的数据
				let url = ''
				url = '/pages/post/detail?id=' + content.contentId
				uni.navigateTo({
					url
				});
			},
			onTapUnfold(index, content) {
				this.topPostsState = 1
				if (index == 1 && this.topicState == 1) {
					this.topicState = 0
				} else if (index == 1 && this.topicState == 0) {
					this.topicState = 1
				} else if (index == 2 && content.postsState == 0) {
					content.postsState = 1
				} else if (index == 2 && content.postsState == 1) {
					content.postsState = 0
				} else if (index == 3 && this.rankingState == 1) {
					this.rankingState = 0
				} else if (index == 3 && this.rankingState == 0) {
					this.rankingState = 1
				}
			},
			toTopic(topic) {
				uni.navigateTo({
					url: '/package_task/pages/bbs/topic/detail?id=' + topic.plateId
				})
			},
			onLeftTabClose() {
				this.leftTabState = 0
			},
			onLeftTabShow() {
				this.leftTabState = 1
			},
			//从热帖库获取一个帖子
			async aboutHotPostSQL(functionName) {
				let that = this
				await localData.cloud_shared.init()
				return new Promise((resolve, reject) => {
					let hotPost = ''
					localData.cloud_shared.callFunction({
						name: 'hotPostSQL', //云函数的名称
						data: {
							functionName: functionName, //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							userId: this.userInfos.userId,
							usingDatabase: localData.usingDatabase,

						},
						async complete(res) {
							let nowPostId = res.result
							// console.log("帖子Id", nowPostId)
							let form = {
								contentId: nowPostId,
								userId: that.userInfos.userId
							}
							if (nowPostId) {
								let data = await getContentById(form)
								if (data.code == 200) {
									hotPost = data.data
									hotPost.contentState = 11
								}
								console.log('热帖', hotPost)
								resolve(hotPost)
							} else {
								resolve(hotPost)
							}
						}
					})
				})
			},
			//点击更多，获取更多往日热帖
			showMorePost() {
				this.showMoreHotPost = true
				this.moreHotListIndex = 3
				this.moreHotList = this.hotList
				// this.moreHotForm.duration = 3
				// this.getMoreHotList()
			},
			//刷新首页
			reloadPage() {
				uni.reLaunch({
					url: "../index/index"
				});
			},
			//无需要逻辑
			editLikeState(value) {
				console.log("触发")
			},
			//无需要逻辑
			editMarkState(value) {
				console.log("触发")
			},
			async getTimeMachinePosts() { //请求最新的帖子列表
				this.timeMachineForm.pageNum = this.timeMachineForm.pageNum + 1;
				console.log('最新请求参数：')
				console.log(this.timeMachineForm)
				const data = await reqHistoryPostList(this.timeMachineForm);
				if (data.code === 200) {
					if (data.data.length > 0) {
						const stepData = data.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						this.addHomeContentList(stepData)
						this.loadStatus = 'loadmore'
					} else {
						this.loadStatus = 'nomore'
					}
					console.log("首页列表", this.homeContentList);
				} else {
					console.log("首页列表请求失败");
				}

			},
			async clearAndGetTimeMachinePosts() { //清空帖子列表并请求时光机列表
				this.timeMachineForm.pageNum = this.timeMachineForm.pageNum + 1;
				this.timeMachineForm.userId = this.userInfos.userId
				console.log('最新请求参数：')
				console.log(this.timeMachineForm)
				const data = await reqHistoryPostList(this.timeMachineForm);
				if (data.code === 200) {
					if (data.data.length > 0) {
						const stepData = data.data.sort(function(item1, item2) {
							return item2.createTime > item1.createTime
						})
						this.setHomeContentList([])
						this.addHomeContentList(stepData)
						this.loadStatus = 'loadmore'
					} else {
						this.loadStatus = 'nomore'
					}
					console.log("首页列表", this.homeContentList);
				} else {
					console.log("首页列表请求失败");
				}

			},
			//点击时光机
			async onTapTimeMachine() {
				this.timeMachineList = [];
				//获取时光机配置
				let param = {
					schoolId: this.currentSchoolId,
					configType: 'dailySelection'
				}
				let data = await getOneBbsConfig(param)
				let months = 0
				if (data.code == 200) {
					let congfig = JSON.parse(data.data.configJson)
					months = congfig.timeMachineDuration
				} else {
					console.log("时光机配置获取失败");
				}
				console.log(months)
				this.buildTimeMachineList(months);
				this.timeMachine = true
			},
			async confirmTimeMachine(e) {
				console.log(e);
				this.timeMachineState = 1;
				// this.setHomeContentList([])
				this.timeMachineForm.pageNum = 0
				const date = new Date();
				const year = date.getFullYear();
				const month = date.getMonth() + 1;
				const day = date.getDate();
				const hours = date.getHours();
				//近期
				if (e[0].value == 100) {
					this.timeMachineForm.duration = -(hours - e[1].value + 1)
					this.setHomeContentList([])
					await this.clearAndGetTimeMachinePosts()
				} else if (e[0].value > 0) {
					let selectDate = new Date(year + '/' + e[0].value + '/' + e[1].value); //转换为yyyy-MM-dd格式
					let nowDate = new Date(new Date(new Date().toLocaleDateString()).getTime());
					this.timeMachineForm.duration = Math.abs(nowDate - selectDate) / 1000 / 60 / 60 - 24
					// console.log("参数",selectDate, nowDate,this.timeMachineForm.duration)
					this.setHomeContentList([])
					await this.clearAndGetTimeMachinePosts()
				} else {
					let selectDate = new Date((year - 1) + '/' + (12 + e[0].value) + '/' + e[1]
						.value); //转换为yyyy-MM-dd格式
					let nowDate = new Date(new Date(new Date().toLocaleDateString()).getTime());
					this.timeMachineForm.duration = Math.abs(nowDate - selectDate) / 1000 / 60 / 60 - 24
					//console.log(selectDate, nowDate)
					this.setHomeContentList([])
					await this.clearAndGetTimeMachinePosts()
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
				for (let i = 0; i < (months * 1 + 1); i++) {
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
			//广告相关
			async getIndexAdvList() {
				let form = {
					schoolId: this.currentSchoolId,
					advPosition: "index"
				}
				const data = await getAdvList(form);
				this.swiperList = [];
				this.topSwiperList = [];
				console.log("触发广告获取", data)
				if (data.code === 200) {
					console.log("广告列表", data);
					if (data.data && data.data.length > 0) {
						for (var i = 0; i < data.data.length; i++) {
							data.data[i].image = data.data[i].imageIndex
							data.data[i].image = ''
							//将置顶广告单独置入一个数组
							if (data.data[i].kind == 5) {
								this.topSwiperList.push(data.data[i])


							} else {
								this.swiperList.push(data.data[i])

							}
						}

					} else {

					}
					console.log("广告列表", this.swiperList);
					//广告图片缓存处理
					//置顶广告
					for (var i = 0; i < this.topSwiperList.length; i++) {
						this.updateAdvImageUrls(this.topSwiperList[i])
					}
					//弹窗广告
					for (var i = 0; i < this.swiperList.length; i++) {
						this.updateAdvImageUrls(this.swiperList[i])
					}
					//缓存处理结束
				} else {
					console.log("广告列表请求失败");
				}

			},
			//获取base64图片格式的广告列表
			async getIndexAdvListWithImageBase64() {
				let form = {
					schoolId: this.currentSchoolId,
					advPosition: "index"
				}
				const data = await getAdvListWithImageBase64(form);
				this.swiperList = [];
				this.topSwiperList = [];
				// console.log("触发广告获取", data)
				if (data.code === 200) {
					if (data.data && data.data.length > 0) {
						for (var i = 0; i < data.data.length; i++) {
							//如果返回是base64格式，base64tourl
							if (!(data.data[i].imageIndex.substr(0, 10).includes("http"))) { //前十个字符串不能含http
								data.data[i].image = await this.base64src(data.data[i].imageIndex, "adv_" + data.data[
										i]
									.id + "_index");
							}
							//返回的不是base64格式，直接赋值
							else {
								data.data[i].image = data.data[i].imageIndex
							}
							//将置顶广告单独置入一个数组
							if (data.data[i].kind == 5) {
								this.topSwiperList.push(data.data[i])
							} else {
								this.swiperList.push(data.data[i])
							}
						}
					} else {

					}
					console.log("广告列表", this.swiperList, this.topSwiperList);
				} else {
					console.log("广告列表请求失败");
				}

			},
			hideAdMask() {
				// this.adMask = 0
				this.increaseShowAdNum()
			},
			//点击广告
			onTabAdv(index) {
				console.log(index, this.swiperList[index].id)
				let that = this
				//1：跳转广告展示页；2：跳转广告展示页（含报名）
				if (this.swiperList[index].kind == 1 || this.swiperList[index].kind == 2) {
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/adv?id=' + this.swiperList[index].id
					})
				}
				//跳转其他小程序 3
				if (this.swiperList[index].kind == 3) {
					let form = {
						id: this.swiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					// #ifdef MP-WEIXIN
					wx.navigateToMiniProgram({
						appId: that.swiperList[index].miniAppId,
						path: that.swiperList[index].miniAppPage,
						envVersion: 'release',
						success(res) {
							// 打开成功
							console.log('函数执行')
						}
					})
					// #endif
				}
				//跳转本小程序 4
				if (this.swiperList[index].kind == 4) {
					let form = {
						id: this.swiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					console.log(that.swiperList[index].miniAppPage)
					this.setShowAdNum()
					uni.navigateTo({
						url: that.swiperList[index].miniAppPage
					})
				}
				//跳转网页 5
				if (this.swiperList[index].kind == 5) {
					let form = {
						id: this.swiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form); //增加广告阅读次数
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/web-view?url=' + this.swiperList[index].miniAppPage
					})
				}
				//跳转多业务广告页面
				if (this.swiperList[index].kind == 6) {
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/multi-business-adv?id=' + this.swiperList[index].id
					})
				}
			},
			//点击置顶广告
			onTabTopAdv(index) {
				let that = this
				if (this.topSwiperList[index].kind == 5) {
					let form = {
						id: this.topSwiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form); //增加广告阅读次数
					console.log(that.topSwiperList[index].miniAppPage)
					uni.navigateTo({
						url: that.topSwiperList[index].miniAppPage
					})
				}
			},
			//点击置顶广告0507
			onTabTopAdv0507(index) {
				console.log(index, this.topSwiperList[index].id)
				let that = this
				//1：跳转广告展示页；2：跳转广告展示页（含报名）
				if (this.topSwiperList[index].kind == 1 || this.topSwiperList[index].kind == 2) {
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/adv?id=' + this.topSwiperList[index].id
					})
				}
				//跳转其他小程序 3
				if (this.topSwiperList[index].kind == 3) {
					let form = {
						id: this.topSwiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					// #ifdef MP-WEIXIN
					wx.navigateToMiniProgram({
						appId: that.topSwiperList[index].miniAppId,
						path: that.topSwiperList[index].miniAppPage,
						envVersion: 'release',
						success(res) {
							// 打开成功
							console.log('函数执行')
						}
					})
					// #endif
				}
				//跳转本小程序 4
				if (this.topSwiperList[index].kind == 4) {
					let form = {
						id: this.topSwiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					console.log(that.topSwiperList[index].miniAppPage)
					this.setShowAdNum()
					uni.navigateTo({
						url: that.topSwiperList[index].miniAppPage
					})
				}
				//跳转网页 5
				if (this.topSwiperList[index].kind == 5) {
					let form = {
						id: this.topSwiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form); //增加广告阅读次数
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/web-view?url=' + this.topSwiperList[index].miniAppPage
					})
				}
				if (this.topSwiperList[index].kind == 6) {
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/multi-business-adv?id=' + this.topSwiperList[index].id
					})
				}
			},
			//点底部加号
			toAddPost() {
				uni.navigateTo({
					url: '/pages/post/add'
				})
			},

			//图片分离的请求模式中图片的请求（第一页）
			async clearAndGetNewImgBase64() { //清空帖子列表并请求最新的帖子列表
				this.imageForm.pageNum = this.imageForm.pageNum + 1;
				//在查询第一页时，传入首页查询时间
				if (this.imageForm.pageNum == 1) {
					const now = new Date(); // 获取当前时间
					const year = now.getFullYear(); // 年份
					const month = now.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
					const day = now.getDate(); // 日期
					const hour = now.getHours(); // 小时
					const minute = now.getMinutes(); // 分钟
					const second = now.getSeconds(); // 秒钟
					this.imageForm.queryTime = year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' +
						second;
				}
				//图片请求的其它项与帖子列表请求保持一致
				this.imageForm.plateId = this.timeForm.plateId
				this.imageForm.schoolId = this.timeForm.schoolId
				this.imageForm.contentType = this.timeForm.contentType
				// console.log('最新请求参数：', this.imageForm)
				const data = await getHomeContentsImage(this.imageForm);
				if (data && data.code === 200) {
					if (data.data.length > 0) {
						console.log(data)
						//按contentId分组
						let stepData = [] //分组结果数组
						for (var i = 0; i < data.data.length; i++) { //遍历后端返回的base64数组
							//base64转url，当返回的是url时不做处理
							if (data.data[i].base64 != "" && !(data.data[i].base64.substr(0, 10).includes(
									"http"))) { //前十个字符串不能含http
								data.data[i].imgSrc = await this.base64src(data.data[i].base64, "homeContent_" + data
									.data[i]
									.contentId + "_" + i);
							}
							let haveContent = 0 //分组结果数组中是否已存在对应帖子id
							//1.分组结果数组中有对应帖子id
							for (var j = 0; j < stepData.length; j++) { //遍历分组结果数组
								if (data.data[i].contentId == stepData[j].contentId) {
									//接口返回base64且转为缓存url
									if (data.data[i].imgSrc) {
										stepData[j].contentBase64.push(data.data[i].imgSrc)
									}
									//接口返回是url
									else {
										stepData[j].contentBase64.push(data.data[i].base64)
									}
									// stepData[j].contentBase64.push(data.data[i].base64)
									haveContent = 1
									break;
								} else {
									continue
								}
							}
							//2.分组结果数组中五无对应帖子id，有图
							if (haveContent == 0 && data.data[i].base64 != "") {
								data.data[i].contentBase64 = []
								//接口返回base64且转为缓存url
								if (data.data[i].imgSrc) {
									data.data[i].contentBase64.push(data.data[i].imgSrc)
								}
								//接口返回是url
								else {
									data.data[i].contentBase64.push(data.data[i].base64)
								}
								let toStoreData = {
									contentId: data.data[i].contentId,
									contentBase64: data.data[i].contentBase64
								}
								stepData.push(toStoreData)
							}
							//3.分组结果数组中五无对应帖子id，无图
							else if (haveContent == 0 && data.data[i].base64 == "") {
								data.data[i].contentBase64 = []
								let toStoreData = {
									contentId: data.data[i].contentId,
									contentBase64: data.data[i].contentBase64
								}
								stepData.push(toStoreData)
							}
						}
						// console.log(stepData)
						this.clearImageBase64List() //首次请求清理store中旧的临时数组
						this.addImageBase64List(stepData)
					}
				} else {
					uni.showToast({
						title: '图片请求异常',
						icon: 'none'
					})
				}

			},
			//图片分离的请求模式中图片的请求（第二页及以后）
			async getNewImgBase64() {
				this.imageForm.pageNum = this.imageForm.pageNum + 1;
				if (this.homeContentList.length > 0) {
					const time = new Date(this.homeContentList[this.homeContentList.length - 1]
						.createTime); // 获取帖子列表最后一个帖子的创建时间
					const year = time.getFullYear(); // 年份
					const month = time.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
					const day = time.getDate(); // 日期
					const hour = time.getHours(); // 小时
					const minute = time.getMinutes(); // 分钟
					const second = time.getSeconds(); // 秒钟
					this.imageForm.queryTime = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' +
						second;
				}
				//图片请求的其它项与帖子列表请求保持一致
				this.imageForm.plateId = this.timeForm.plateId
				this.imageForm.schoolId = this.timeForm.schoolId
				this.imageForm.contentType = this.timeForm.contentType
				// console.log('最新请求参数：', this.imageForm)
				const data = await getHomeContentsImage(this.imageForm);
				if (data && data.code === 200) {
					if (data.data.length > 0) {
						//按contentId分组
						let stepData = [] //分组结果数组
						for (var i = 0; i < data.data.length; i++) { //遍历后端返回的base64数组
							//base64转url
							if (data.data[i].base64 != "" && !(data.data[i].base64.substr(0, 10).includes(
									"http"))) { //前十个字符串不能含http
								data.data[i].imgSrc = await this.base64src(data.data[i].base64, "homeContent_" + data
									.data[i]
									.contentId + "_" + i);
							}
							let haveContent = 0 //分组结果数组中是否已存在对应帖子id
							//1.分组结果数组中有对应帖子id
							for (var j = 0; j < stepData.length; j++) { //遍历分组结果数组
								if (data.data[i].contentId == stepData[j].contentId) {
									//接口返回base64且转为缓存url
									if (data.data[i].imgSrc) {
										stepData[j].contentBase64.push(data.data[i].imgSrc)
									}
									//接口返回是url
									else {
										stepData[j].contentBase64.push(data.data[i].base64)
									}
									// stepData[j].base64 = stepData[j].base64 + ',' + data.data[i].base64
									haveContent = 1
									break;
								} else {
									continue
								}
							}
							//2.分组结果数组中五无对应帖子id，有图
							if (haveContent == 0 && data.data[i].base64 != "") {
								data.data[i].contentBase64 = []
								//接口返回base64且转为缓存url
								if (data.data[i].imgSrc) {
									data.data[i].contentBase64.push(data.data[i].imgSrc)
								}
								//接口返回是url
								else {
									data.data[i].contentBase64.push(data.data[i].base64)
								}
								let toStoreData = {
									contentId: data.data[i].contentId,
									contentBase64: data.data[i].contentBase64
								}
								stepData.push(toStoreData)
							}
							//3分组结果数组中五无对应帖子id，无图
							else if (haveContent == 0 && data.data[i].base64 == "") {
								data.data[i].contentBase64 = []
								let toStoreData = {
									contentId: data.data[i].contentId,
									contentBase64: data.data[i].contentBase64
								}
								stepData.push(toStoreData)
							}
						}
						// console.log(stepData)
						this.addImageBase64List(stepData)
					}
				} else {
					uni.showToast({
						title: '图片请求异常',
						icon: 'none'
					})
				}

			},
			//缓存帖子中的图片（头像和帖子图片），并更新store中的图片url
			async updatePostcontentUrls(post) {
				//处理头像
				//本地默认头像不需要缓存
				if (post.headimgTempUrl.includes("/static/headImg")) {
					console.log("本地默认头像不需要缓存")
					post.headimgUrl = post.headimgTempUrl
				}
				//匿名头像缓存
				else if (post.isSpecial == 1 || post.isSpecial == 4) {
					post.headimgUrl = await this.getImageLocalUrl('anonymousAvatar', post.headimgTempUrl)
				}
				//普通头像缓存
				else {
					post.headimgUrl = await this.getImageLocalUrl('non-anonymousAvatar', post.headimgTempUrl)
				}
				//处理帖子图片
				if (post.contentTempUrls && post.contentTempUrls.length > 0) {
					for (var i = 0; i < post.contentTempUrls.length; i++) {
						let imageLocalurl = await this.getImageLocalUrl('postImage', post.contentTempUrls[i])
						post.contentUrls.push(imageLocalurl)
					}
				}
				// console.log("帖子(缓存图片后)",post,this.homeContentList)
				this.setHomeContentImage(post)
			},
			//缓存广告中的图片，并返回本页面广告中的图片本地url
			async updateAdvImageUrls(adv) {
				//将网络图片缓存到本地，并返回本地地址
				// adv.image = await this.getImageLocalUrl('advImage', adv.imageIndex)
				if (adv.image) {
					adv.image = await this.getImageLocalUrl('advImage', adv.image)
				}
				// console.log("第个广告",i,data.data[i])
				//缓存结束
			},
			toChooseSchoolPage() {
				uni.navigateTo({
					url: '/package_task/pages/bbs/more/chang-school'
				})
			},
			toDailyHot() {
				uni.navigateTo({
					url: '/pages/post/history-post'
				})
			},
			//改版后广告
			async getIndexAdvList0507(position) {
				this.alreadyGetAdv = 0
				let form = {
					schoolId: this.currentSchoolId,
					advPosition: position
				}
				this.swiperList = [];
				this.topSwiperList = [];
				const data = await getAdvList0507(form);
				// console.log("触发广告获取", data)
				if (data.code === 200) {
					// console.log("广告列表", data);
					if (data.data && data.data.length > 0) {
						//图片处理,寻找该位置对应图片
						for (let i = 0; i < data.data.length; i++) {
							if (data.data[i].imageUrls) {
								let currentAdvImage = data.data[i].imageUrls.split(',');
								let keyWord = 'position_' + position
								const imageUrl = currentAdvImage.find(item => item.includes(keyWord));
								if (imageUrl) {
									data.data[i].image = imageUrl
									// console.log(i,data.data[i].image)
								}
							}
						}
						//开屏广告处理
						if (position == 1) {
							this.swiperList.push(...data.data)
							//广告图片缓存处理
							//开屏广告
							for (let i = 0; i < this.swiperList.length; i++) {
								this.updateAdvImageUrls(this.swiperList[i])
							}
							//缓存处理结束
						}
						//置顶广告处理
						if (position == 2) {
							this.topSwiperList.push(...data.data)
							//广告图片缓存处理
							//置顶广告
							for (var i = 0; i < this.topSwiperList.length; i++) {
								this.updateAdvImageUrls(this.topSwiperList[i])
							}
							//缓存处理结束

						}
					}
					//判断是否渲染完毕
					this.alreadyGetAdv = 1
					if (this.alreadyGetAdv == 1 && this.alreadyGetHotPost == 1 && this.alreadyGetTopPost == 1) {
						// this.changeBackImageHeight()
						// console.log("广告");
					}

				} else {
					console.log("广告列表请求失败");
				}

			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
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
			changeNavBarBack(value) {
				console.log("吸顶", value, this.tabTop)
				return
				if (value == 0) {
					this.navbarStyle = {
						backgroundImage: 'url("../../static/newUI/index-back.jpg")',
						backgroundSize: '100%',
						backgroundRepeat: 'no-repeat',
					}
				}
				if (value == 1) {
					this.navbarStyle = {
						backgroundColor: '#FFFFFF',
					}
				}

			},
			changeBackImageHeight() {
				let that = this
				// that.haveChangeBackImageHeight = 1
				// const query = uni.createSelectorQuery().in(this); //这样写就只会选择本页面组件的类名box的
				// query.selectAll('.type-tabs').boundingClientRect(
				// 	data => { //回调函数，data中存储的是这些元素节点（每个节点的信息存为一个对象）的位置信息
				// 		// 获取状态栏的高度
				// 		const systemInfo = uni.getSystemInfoSync();
				// 		const statusBarHeight = systemInfo.statusBarHeight;

				// 		let scale = uni.upx2px(100) / 100;
				// 		let imageHeight = (80 + 66 + data[0].top) / scale
				// 		let imageHeight2 = (data[0].top) / scale
				// 		that.backImageHeight = imageHeight2 + "rpx"
				// 		// console.log("onready触发,哈哈哈", data, imageHeight, that.backImageHeight)
				// 	}).exec();
				const systemInfo = uni.getSystemInfoSync();
				const screenHeight = systemInfo.screenHeight;
				console.log(systemInfo)
				const safeBottomHeight = systemInfo.safeAreaInsets.bottom
				let heightpx = screenHeight - 20
				this.backImageHeight = heightpx + "px"
			},
			calculateScrollViewHeight() {
				const systemInfo = uni.getSystemInfoSync();
				const screenHeight = systemInfo.screenHeight;
				const safeBottomHeight = systemInfo.safeAreaInsets.bottom
				this.scrollViewHeight = screenHeight - systemInfo.statusBarHeight - 44 - 52;
				console.log("scrollView高度", this.scrollViewHeight)
			},
			async onReachScrollBottum() {
				// console.log("触发一次")
				if (this.loadStatus == 'loadmore') {
					// this.loadStatus = 'loading'
					await this.getMore()
				}
			}



		},
		async onLoad(options) {
			let that = this
			//用户首次登陆且路径中不含currentSchoolId
			if (!uni.getStorageSync('schoolId') && !options.currentSchoolId) {
				console.log("jieguo", uni.getStorageSync('schoolId'))
				uni.navigateTo({
					url: '/package_task/pages/bbs/more/chang-school'
				})
			}
			console.log("参数", options)
			if (options.contentId) {
				this.showDailyPopup = 0;
				this.jumpImmediately = 1;
				if(options.source && options.commentId){
					uni.navigateTo({
						url: '/pages/post/detail?id=' + options.contentId + '&source=' + options.source + '&commentId=' + options.commentId
					})
				}
				else if (options.source) {
					uni.navigateTo({
						url: '/pages/post/detail?id=' + options.contentId + '&source=' + options.source
					})
				} else {
					uni.navigateTo({
						url: '/pages/post/detail?id=' + options.contentId
					})
				}
			} else {
				this.showDailyPopup = 1;
			}
			//来自盲盒页分享
			if (options.page && options.page == 'blindBox') {
				this.jumpImmediately = 1;
				uni.navigateTo({
					url: '/package_task/pages/blind-box/blind-box',
				})
			}
			//来自学习资料页分享
			if (options.page && options.page == 'communityWelfare') {
				this.jumpImmediately = 1;
				uni.navigateTo({
					url: '/package_task/pages/community_welfare/index',
				})
			}
			//来自课程评价页分享
			if (options.page && options.page == 'courseSelection') {
				this.jumpImmediately = 1;
				uni.navigateTo({
					url: '/package_task/pages/course-selection/course-list',
				})
			}
			//来自课程评价页分享
			if (options.page && options.page == 'topicDetail') {
				this.jumpImmediately = 1;
				uni.navigateTo({
					url: '/package_task/pages/bbs/topic/detail?id=' + options.plateId,
				})
			}
			//来自多业务广告
			if (options.advId && options.advKind && options.advKind == 'multiBusiness') {
				this.jumpImmediately = 1;
				uni.navigateTo({
					url: '/package_task/pages/bbs/more/multi-business-adv?id=' + options.advId,
				})
			}
			//赋一个初始值
			this.contentTypeList = localData.contentTypeList
			//如果携带的学校ID和缓存中的学校ID不一致
			if (options.currentSchoolId && options.currentSchoolId * 1 != uni.getStorageSync('schoolId')) {
				console.log("重新加载配置项等内容")
				//修改store与本vue里的变量
				this.getCurrentSchoolId(options.currentSchoolId * 1);
				//修改storage里的变量
				uni.setStorageSync("schoolId", this.currentSchoolId * 1);
				//按学校id更新启动项配置
				await this.getSchoolFunctionConfig()
				//按学校id更新基础配置
				await this.getSchoolBasicConfig()
				//更新用户在该学校的状态
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					//this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
					this.setCurrentUserType(stateData.data.userType)
					if (stateData.data.userType == 2 && stateData.data.bannedTime) {
						this.setBannedTime(stateData.data.bannedTime)
					} else {
						this.setBannedTime(null)
					}
				}
			}
			//来自七日页分享
			if (options.activity && options.activity == 'sevenDay') {
				this.jumpImmediately = 1;
				uni.navigateTo({
					url: '/package_task/pages/seven-day-couple/seven-day-couple',
				})
			}
			//将storage中的学校名称赋值给首页
			if (uni.getStorageSync("currentSchoolName")) {
				this.currentSchoolName = uni.getStorageSync("currentSchoolName")
			}
			// //localdata帖子类型值赋予
			this.contentTypeList = localData.contentTypeList
			//隐藏闲置交易tab
			if (this.firstPage.hide_second_hand_tab == 1) {
				this.contentTypeList = this.contentTypeList.filter(item => item.type !== 8);
			}
			// #ifdef MP-WEIXIN
			wx.getSystemInfo({
				success: (res) => {
					let statusBar = 0
					// @ts-ignore
					const custom = wx.getMenuButtonBoundingClientRect()
					let scale = uni.upx2px(100) / 100;
					// statusBar = res.screenHeight-res.windowHeight-res.safeArea.top
					// that.tabTop = (custom.bottom + custom.top - res.statusBarHeight) / scale
					//that.noticeTop = (44 + res.statusBarHeight) / scale
					that.tabTop = (44 + res.statusBarHeight) / scale
					let tempTop = ((custom.top - res.statusBarHeight) * 2 + custom.height + res
						.statusBarHeight) / scale
					// console.log('4.9高度相关', res, custom, that.tabTop, tempTop)
				}
			})
			// #endif
			//this.swiperBgColor = "rgba(0, 0, 0, 0.0)";

		},
		async created() {
			//如果无userid，则说明初始化未完成，不进行任何操作
			// #ifdef MP-WEIXIN
			if (this.firstPage.duration == null || this.firstPage.duration == '') {
				return
			}
			// #endif
			console.log("首页云函数", this.firstPage)
			this.topicState = this.firstPage.topic_state;
			this.topPostsState = this.firstPage.top_posts_state;
			this.rankingState = this.firstPage.ranking_state;
			this.hotForm.duration = this.firstPage.duration;
			this.topicList = this.firstPage.topic_list;
			this.hotPostState = this.firstPage.hot_post_state;
			this.durationAll = this.firstPage.duration_all;
			//话题和置顶帖是否展开
			// await this.aboutfunctionConfig('firstPage')
			this.hotForm.userId = this.userInfos.userId;
			this.hotForm.schoolId = this.currentSchoolId;
			this.timeForm.userId = this.userInfos.userId;
			this.timeForm.schoolId = this.currentSchoolId;
			//获取热榜
			this.getHotList()
			// 发送请求，获取帖子的列表
			// this.setHomeContentList([])
			// await this.getNew();
			//获取初始更多热榜
			// this.moreHotForm.duration = 3
			// await this.getMoreHotList()
			// await this.getMoreHotList()
			//获取广告列表
			if (this.firstPage.image_use_base64 == 1) {
				this.getIndexAdvListWithImageBase64();
			} else {
				// this.getIndexAdvList();
				//开屏广告
				this.getIndexAdvList0507(1);
				//置顶广告
				this.getIndexAdvList0507(2);
			}

		},
		onShow() {
			if (!this.currentSchoolId) {
				uni.hideTabBar();
			}
			console.log('this.msgCount', this.msgCount);
			uni.$emit('onShow', 1);
			if (this.jumpImmediately == 1 && this.showAdNum == 0) {
				console.log("立即跳转")
			} else {
				this.increaseShowAdNum()
				console.log("页面数+1", this.showAdNum)
			}
			// if (this.showAdNum == 1) {
			// 	this.adMask = 1
			// } else {
			// 	this.adMask = 0
			// }
			console.log("广告蒙版显示状态", this.showAdNum)
			this.jumpImmediately = 0;
			if (this.needRelaunch) {
				this.setNeedRelaunch(false)
				uni.reLaunch({
					url: "../index/index"
				});
			}
		},
		watch: { //监听userid变化，不为空访客加载
			userInfos: {
				async handler(newVal, oldVal) {
					// #ifdef MP-WEIXIN
					if (this.userInfos != null && this.userInfos != '') {
						console.log('加载完成')
						this.setScoresFront(this.userInfos.scores)
						//获取school列表，并保持至store
						const schoolLists = await getSchoolLists({
							'appId': localData.appId
						});
						if (schoolLists.code === 200) {
							console.log('学校列表', schoolLists, this.currentSchoolId)
							this.getSchoolList(schoolLists.data);
							this.getCurrentSchoolId(this.currentSchoolId);
							//根据schoolId展示schoolName，并存储到storage
							for (var i = 0; i < this.schoolList.length; i++) {
								if (this.schoolList[i].schoolId == this.currentSchoolId) {
									// this.currentSchoolName = this.schoolList[i].schoolName;
									if (this.schoolList[i].anotherName) {
										this.currentSchoolName = this.schoolList[i].anotherName;
									} else {
										this.currentSchoolName = this.schoolList[i].schoolName;
									}
								}
							}
							// this.currentSchoolName = this.schoolList[0].schoolName;
							uni.setStorageSync("currentSchoolName", this.currentSchoolName);
						}
						this.topForm.userId = this.userInfos.userId;
						this.topForm.schoolId = this.currentSchoolId;
						this.timeForm.userId = this.userInfos.userId;
						this.timeForm.schoolId = this.currentSchoolId;
						this.timeForm.contentType = ''
						this.followForm.userId = this.userInfos.userId;
						this.followForm.schoolId = this.currentSchoolId;
						this.hotForm.userId = this.userInfos.userId;
						this.hotForm.schoolId = this.currentSchoolId;
						this.timeMachineForm.schoolId = this.currentSchoolId;


						// 获取关注板块
						let joinTopicForm = {
							userId: this.userInfos.userId,
							schoolId: this.currentSchoolId
						}
						//this.joinTopicList = getFocusedPlates(joinTopicForm);
						// 获取置顶帖
						this.getTopContents();
						// 发送请求，获取帖子的列表
						// this.setHomeContentList([])
						this.clearAndGetNew()
						if (this.firstPage.image_use_base64 == 1) {
							this.clearAndGetNewImgBase64()
						}
					}
					// #endif

				},
				//deep: true
			},
			firstPage: {
				async handler(newVal, oldVal) {
					console.log('首页watch触发', this.firstPage)

					// this.updateConfig = true
					// #ifdef MP-WEIXIN
					if (this.firstPage.duration != null && this.firstPage.duration != '') {
						this.topicState = this.firstPage.topic_state;
						this.topPostsState = this.firstPage.top_posts_state;
						this.rankingState = this.firstPage.ranking_state;
						this.hotForm.duration = this.firstPage.duration;
						this.topicList = this.firstPage.topic_list;
						this.hotPostState = this.firstPage.hot_post_state;
						this.durationAll = this.firstPage.duration_all;
						//话题和置顶帖是否展开
						// await this.aboutfunctionConfig('firstPage')
						this.hotForm.userId = this.userInfos.userId;
						this.hotForm.schoolId = this.currentSchoolId;
						this.topForm.userId = this.userInfos.userId;
						this.topForm.schoolId = this.currentSchoolId;
						this.timeForm.userId = this.userInfos.userId;
						this.timeForm.schoolId = this.currentSchoolId;
						this.timeForm.contentType = ''
						this.timeMachineForm.schoolId = this.currentSchoolId;
						//获取热榜
						this.getHotList()
						// 重新获取置顶帖
						this.getTopContents();
						// 发送请求，重新获取帖子的列表
						this.clearAndGetNew()
						//获取广告列表
						if (this.firstPage.image_use_base64 == 1) {
							this.getIndexAdvListWithImageBase64();
						} else {
							// this.getIndexAdvList();
							//开屏广告
							this.getIndexAdvList0507(1);
							//置顶广告
							this.getIndexAdvList0507(2);
						}
						//localdata帖子类型值赋予
						console.log("更新前")
						this.contentTypeList = localData.contentTypeList
						// uni.reLaunch({
						// 	url: "../index/index"
						// });
						//隐藏闲置交易tab
						if (this.firstPage.hide_second_hand_tab == 1) {
							this.contentTypeList = this.contentTypeList.filter(item => item.type !== 8);
						}
					}
					// #endif
				},
				deep: true
			},
			showAdNum: {
				async handler(newVal, oldVal) {
					let that = this
					if (this.showAdNum == 1) {
						setTimeout(() => {
							that.adMaskClose = 1
						}, 1500)
					} else {

					}
				},

			}

		},
		async mounted() {
			//如果无userid，则说明初始化未完成，不进行任何操作
			// #ifdef MP-WEIXIN
			if (this.userInfos.userId == null || this.userInfos.userId == '') {
				return
			}
			// #endif
			//获取school列表，并保持至store
			const schoolLists = await getSchoolLists({
				'appId': localData.appId
			});
			if (schoolLists.code === 200) {
				this.getSchoolList(schoolLists.data);
				this.getCurrentSchoolId(this.currentSchoolId);
				//根据schoolId展示schoolName，并存储到storage
				// this.currentSchoolName = this.schoolList[0].schoolName;
				for (var i = 0; i < this.schoolList.length; i++) {
					if (this.schoolList[i].schoolId == this.currentSchoolId) {
						// this.currentSchoolName = this.schoolList[i].schoolName;
						if (this.schoolList[i].anotherName) {
							this.currentSchoolName = this.schoolList[i].anotherName;
						} else {
							this.currentSchoolName = this.schoolList[i].schoolName;
						}
					}
				}
				uni.setStorageSync("currentSchoolName", this.currentSchoolName);
			}


			this.topForm.userId = this.userInfos.userId;
			this.topForm.schoolId = this.currentSchoolId;
			this.timeForm.userId = this.userInfos.userId;
			this.timeForm.schoolId = this.currentSchoolId;
			this.followForm.userId = this.userInfos.userId;
			this.followForm.schoolId = this.currentSchoolId;
			this.hotForm.userId = this.userInfos.userId;
			this.hotForm.schoolId = this.currentSchoolId;
			this.timeMachineForm.schoolId = this.currentSchoolId;

			//获取当前认证状态
			//this.getUserStateBySchool()

			// 获取关注板块
			let joinTopicForm = {
				userId: this.userInfos.userId,
				schoolId: this.currentSchoolId
			}
			//this.joinTopicList = await getFocusedPlates(joinTopicForm);
			// 获取置顶帖
			this.getTopContents();
			// 发送请求，获取帖子的列表
			// this.setHomeContentList([])
			this.clearAndGetNew();
			if (this.firstPage.image_use_base64 == 1) {
				this.clearAndGetNewImgBase64()
			}
		},
		onShareTimeline() {
			let schoolName = this.currentSchoolName
			return {
				title: schoolName,
				query: "currentSchoolId=" + this.currentSchoolId
				// path: '/pages/index/index?currentSchoolId=' + this.currentSchoolId
			};
		},
		onShareAppMessage(res) {
			// console.log("触发")
			this.fisrtShareMiniApp(this.userInfos.userId, this.currentSchoolId)
			let schoolName = this.currentSchoolName
			return {
				title: schoolName,
				path: '/pages/index/index?currentSchoolId=' + this.currentSchoolId
			};
		},
		async onReady() {
			let windowHeight = 0
			let that = this
			this.calculateScrollViewHeight();
			uni.getSystemInfo({
				success: (res) => {
					// 注意这里获得的高度宽度都是px，需要转换rpx
					console.log(res.screenHeight); // 屏幕高度，包含导航栏
					windowHeight = res.windowHeight
					
					console.log(res.windowHeight); // 可使用窗口高度，不包含导航栏
				},
			})
			const query1 = uni.createSelectorQuery().in(this); //这样写就只会选择本页面组件的类名box的
			// console.log("页面信息：" + query1);
			query1.selectAll('.topic-wrap').boundingClientRect(data => { //回调函数，data中存储的是这些元素节点（每个节点的信息存为一个对象）的位置信息
				
				this.leftTabTop = data[0].top
				let scale = uni.upx2px(100) / 100;
			}).exec();
			query1.selectAll('.block-title').boundingClientRect(data => { //回调函数，data中存储的是这些元素节点（每个节点的信息存为一个对象）的位置信息
				// this.rightScrollViewHeight = windowHeight - data[0].top - 52
				this.tipTop = data[0].top
			}).exec();
			// console.log("onready触发", this.tipTop)

			//导航栏图片转base64
			// this.backImageUrl = '/static/newUI/index-back.jpg'
			this.backImageUrl = '/static/newUI/index-back2.jpg'
			if (localData.UIColor == 'blue') {
				// this.backImageUrl = '/static/newUI/index-back-blue.jpg'
				this.backImageUrl = '/static/newUI/index-back-blue2.jpg'
			}
			if (this.currentSchoolId==9999 || uni.getStorageSync('schoolId')==9999) {
				this.backImageUrl = '/static/NXB/NXB-bg.jpg'
			}
			let imageBase64 = await this.urlToBase64(this.backImageUrl)
			this.navbarStyle.backgroundImage = `url(${imageBase64})`
			this.backgroundStyle.backgroundImage = `url(${imageBase64})`
			//修改背景图片高度
			setTimeout(() => {
				this.changeBackImageHeight()
			}, 500);
			// console.log("导航栏图片转base64")
			// console.log(this.navbarStyle.backgroundImage)

			let today = new Date().getDate()
			let yesterday = new Date().setDate(today - 1)
			console.log(new Date(yesterday))
			//#ifdef MP-WEIXIN
			// 使用微信小程序的API获取当前小程序的名称
			let currentApp = uni.getAccountInfoSync().miniProgram
			//#endif

		},
		// async onReachBottom() {
		// 	if (this.loadStatus == 'loadmore') {
		// 		this.loadStatus = 'loading'
		// 		await this.getMore()
		// 	}
		// }
	};
</script>

<style>
	page {
		/* background-color: #F5F5F5; */
		/* z-index: -2; */
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}
</style>
<style lang="scss" scoped>
	.page-background {
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		// background-image: url('/static/newUI/index-back.jpg');
		background-image: url('/static/newUI/index-back-blue.jpg');
		background-repeat: no-repeat;
		background-size: 100% 100%;
	}

	.background-image {
		width: 100%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		// height: 200rpx;
		z-index: -1;
		background-size: cover;
		background-repeat: no-repeat;
		background-position: top center;
	}

	.background-image2 {
		width: 100%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 600rpx;
		z-index: -1;
		background-size: cover;
		background-repeat: no-repeat;
		background-position: top center;
	}

	.navbar-image {
		width: 100%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		z-index: -1;
		background-size: '100%';
		background-repeat: 'no-repeat';
	}

	.nav-bar-container {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		// height: 100%;
		z-index: 999;
		background-image: url('/static/newUI/index-back.jpg');
		background-size: '100%';
		background-repeat: 'no-repeat';
	}


	//导航栏
	$nav-height: 30px;

	.add-icon {
		width: 50px;
		height: 50px;
		position: fixed;
		z-index: 1000;
		bottom: 10%;
		right: 5%;
	}


	.box-bg {
		background-color: #F5F5F5;
		padding: 5px 0;
	}

	.schoolNav {
		/* #ifndef APP-PLUS-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		align-items: center;
		justify-content: flex-start;
		margin: 0 20rpx 0 30rpx;

		.text {
			font-size: 30rpx;
			//font-weight: bold;
			overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
			max-width: 270rpx;
			font-weight: bold;
		}
	}

	.input-view {
		/* #ifndef APP-PLUS-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		max-width: 280rpx;
		flex: 1;
		// background-color: #ffffff;
		background-color: transparent;
		height: $nav-height;
		border-radius: 15px;
		// padding: 0 5rpx;
		flex-wrap: nowrap;
		margin: 7px 0;
		line-height: $nav-height;
	}

	.input-uni-icon {
		line-height: $nav-height;
	}

	.nav-bar-input {
		height: $nav-height;
		line-height: $nav-height;
		width: 150rpx;
		/* #ifdef APP-PLUS-NVUE */
		/* #endif */
		padding: 0 5px;
		font-size: 12px;
		background-color: #f8f8f8;
	}

	.navbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		background-color: #FFFFFF;
	}

	.schoolName {
		font-size: 35rpx;
		font-weight: bold;
		margin-left: 30rpx;
	}

	.navIcons {
		display: flex;
		align-items: center;
		margin-right: 30rpx;
	}

	.nav-icon {
		width: 40rpx;
		height: 40rpx;
		margin-right: 10rpx;
	}

	.nav-icon2 {
		width: 40rpx;
		height: 40rpx;
		//margin: 20rpx;
	}

	.swiper-body {
		height: calc(100vh - var(--status-bar-height) - 43px);
	}

	.body-scroll-view {
		width: 100%;
		height: 100%;
	}

	.tab-box {
		width: 80%;
	}

	.block-title {
		// background-color: #fff;
		font-weight: bold;
		padding-top: 10rpx;
		color: #000000;
		display: flex;
		font-size: 30rpx;
		justify-content: space-between;

		.right {
			margin-left: auto;
			color: #999;
			font-size: 24rpx;
		}
	}

	// 顶部板块
	.topic-wrap {
		padding: 0 20rpx;
		padding-bottom: 10rpx;
		margin: 20rpx 20rpx;
		// border: 2px solid #000000;
		// background-image: linear-gradient(180deg, #ffb9b9, #ffffff);
		// background-image: linear-gradient(180deg, #bbe4ff, #f8ffff);
		box-shadow: 0rpx 8rpx 27rpx 0rpx rgba(0, 0, 0, 0.05);
		border-radius: 23rpx 23rpx 0 0;
		border: 4rpx solid #FFFFFF;
		background: rgba(255, 255, 255, 0.6);
	}

	.grid-topic {
		position: relative;
		margin-bottom: 20rpx;


		.name {
			font-size: 24rpx;
			text-align: center;
		}

		.user {
			position: absolute;
			left: 0;
			top: 0;
			font-size: 20rpx;
			display: block;
			background-color: $themes-color;
			padding: 5rpx;
			border-radius: 0 0 10rpx 0;
		}
	}

	.u-search-box {
		padding: 20rpx 30rpx;
		margin-bottom: 10rpx;
	}

	.share-type {
		padding: 10rpx 30rpx;

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
		}
	}

	// 发布弹出框
	.handle-wrap {
		display: flex;
		padding: 50rpx 0;

		.item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			flex: 1;
			padding: 20rpx 0;
			border-radius: 20rpx;

			.icon {
				width: 50px;
				height: 50px;
			}

			.txt {
				font-size: 28rpx;
			}
		}
	}

	.handle-close {
		display: flex;
		justify-content: center;
		margin-bottom: 50rpx;
	}

	.left-wrapper {
		position: sticky;
		height: 60vh;
		width: 130rpx;
		flex: 0 0 130rpx;
		background-color: #fff;

		.left-content {

			.title-content {
				width: 100%;
				height: 100rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				font-size: 25rpx;
				//font-weight: bold;
				//border-bottom: 1px solid #dedede;
				cursor: pointer;

				&:last-child {
					border-bottom: 0;
				}

				// &:first-child {
				// 	//border-color: #000000;
				// 	border-top:1rpx solid #ffffff;
				// }

				&.onSelected {
					background-color: #dddddd;
					position: relative;
					color: #000000;
					font-weight: bold;

					&::before {
						content: '';
						position: absolute;
						left: 0;
						top: 0;
						width: 5rpx;
						height: 100%;
						background: #000000;
						//background: linear-gradient(124deg, #feb436 0%, #fb7c22 100%);
					}
				}
			}
		}
	}

	// .hot-post-text {
	// 	display: -webkit-box;
	// 	/*弹性伸缩盒子模型显示*/
	// 	-webkit-box-orient: vertical;
	// 	/*排列方式*/
	// 	-webkit-line-clamp: 1;
	// 	/*显示文本行数(这里控制多少行隐藏)*/
	// 	overflow: hidden;
	// 	/*溢出隐藏*/
	// 	width: 75%;
	// 	// height: 36rpx;
	// 	font-weight: bold;
	// 	word-break: break-word;
	// 	white-space: nowrap;

	// }



	.hot-post-text {
		// display: flex;
		// justify-content: flex-start;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		// max-width: 450rpx;
		max-width: 60vw;
		font-size: 28rpx;
		font-weight: 550;

	}

	.more-hot-post-text {
		display: -webkit-box;
		/*弹性伸缩盒子模型显示*/
		-webkit-box-orient: vertical;
		/*排列方式*/
		-webkit-line-clamp: 1;
		/*显示文本行数(这里控制多少行隐藏)*/
		overflow: hidden;
		/*溢出隐藏*/
		width: 60%;
		height: 36rpx;
		font-weight: bold;
		word-break: break-word;
	}

	.topContent {
		padding-top: 10rpx;
		border: 1rpx #000;
		//border-bottom: 1px solid #eee;
		//margin-bottom: 20rpx;	

	}

	.post-item-top-user {
		display: flex;
		align-items: center;
		justify-content: space-around;

		.avatar {
			width: 85rpx;
			height: 85rpx;
			border-radius: 50%;
			margin-right: 20rpx;
		}

		.official {
			display: inline-block;
			font-size: 24rpx;
			color: blue;
			font-weight: bold;
			//background-color: blue;
			//padding: 5rpx 10rpx;
			//border-radius: 10rpx;
			//margin-right: 10rpx;
		}

		.official.red {
			//color: #ff0000;
			background-color: #c40000;
		}

		.anonymous {
			display: inline-block;
			font-size: 27rpx;
			color: #ffffff;
			background-color: rgba(0, 0, 0, 0.6);

			border-style: solid;
			border-width: 1px;
			//border-color: #7f7f7f;
			padding: 5rpx 10rpx;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}

		.center {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: 24rpx;
			color: #999;

			.username {
				font-size: 32rpx;
				font-weight: 600;
				color: #616161;
			}

			.username1 {
				font-size: 32rpx;
				font-weight: 600;
				// color: #ffd764;
				// color: #edd443;
				// color: #f7ab43;
				color: blue;
			}

		}

		.right {
			height: 85rpx;

			.arrow-down {
				color: #999;
			}
		}
	}

	.discuss-title {
		//font-weight: 600;
		font-size: 28rpx;
		padding: 10rpx;
		border-radius: 20rpx;
		background: #d4f5ca;
		color: #005500;
		text-align: center;
	}

	.post-text {
		font-size: 22rpx;
		// font-weight: bold;
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
		white-space: pre-wrap;
		overflow: hidden;


	}

	.top_post_text {
		width: 70%;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 1;
		white-space: pre-wrap;
		overflow: hidden;
	}

	.modal-bottom {
		display: block;
		// margin-left: 15rpx;
		padding-bottom: 20rpx;
		text-align: left;
		padding-left: 45rpx;
		padding-right: 50rpx;
		background-color: #FFFFFF;
	}

	.modal-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
		// margin-left: 10rpx;
	}

	.modal-content-right {
		margin-right: 5rpx;
	}

	.search-button {
		// background-color: rgba(0, 0, 0, 0.4);
		background-color: rgba(24, 24, 24, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: calc(10% + 200rpx);
		right: 0;
		height: 80rpx;
		width: 80rpx;
		// border-radius: 50%;
		border-radius: 40rpx 0 0 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding-left: 5rpx;
	}

	.reload-page {
		// background-color: rgba(0, 0, 0, 0.4);
		background-color: rgba(24, 24, 24, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: calc(10% + 100rpx);
		right: 0;
		height: 80rpx;
		width: 80rpx;
		border-radius: 40rpx 0 0 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding-left: 5rpx;
	}

	.timeMachineIcon {
		// background-color: rgba(0, 0, 0, 0.4);
		background-color: rgba(24, 24, 24, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 10%;
		right: 0;
		height: 80rpx;
		width: 80rpx;
		border-radius: 40rpx 0 0 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding-left: 5rpx;
	}

	.notice-line {
		height: 60rpx;
		background-color: transparent;
		padding: 0 26rpx;
		position: fixed;
	}

	.returnNow {
		background: rgba(63, 130, 211, 1.0);
		color: #fff;
		position: fixed;
		// border-radius: 4px;
		font-size: 26rpx;
		font-weight: bold;
		padding: 10rpx;
		width: 170rpx;
		height: 70rpx;
		z-index: 1001;
		top: 25%;
		right: 0;
		display: flex;
		align-items: center;

	}

	.returnNow_icon {
		// border-left: 4px solid transparent;
		// border-right: 4px solid transparent;
		// border-bottom: 15px solid rgba(0, 0, 0, 0.7);
		border-right: 25rpx solid rgba(63, 130, 211, 1.0);
		border-top: 35rpx solid transparent;
		border-bottom: 35rpx solid transparent;
		top: 0;
		right: 170rpx;
		width: 0;
		height: 0;
		position: absolute;
		z-index: 9;
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

	.text-container {
		position: relative;
		display: inline-block;
	}

	.underline {
		position: absolute;
		/* 绝对定位，相对于.text-container */
		left: 0;
		right: 0;
		top: 35rpx;
		/* 放置于文字底部 */
		height: 10rpx;
		/* 下划线厚度 */
		background: linear-gradient(90deg, #E9F30C 0%, #50FD35 100%);
		z-index: 1;
		/* 设置渐变色 */
		/* 确保下划线层级低于文字 */
	}


	::v-deep .u-scroll-box .u-tab-item {
		margin-right: 30rpx;
	}

	// ::v-deep .u-tabs__wrapper__nav__item {
	// 	flex: 1 1 0% !important;
	// }
</style>