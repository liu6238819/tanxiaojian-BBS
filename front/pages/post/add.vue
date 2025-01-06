<template>
	<view class="container">
		<!-- 通过canvas压缩图片 -->
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>

		<input v-model="form.title" class="title-input" placeholder="请输入标题(选填)" />
		<u-input type="textarea" height="300" placeholder="这一刻的想法..." :clearable="false" :auto-height="true"
			maxlength="1500" v-model="form.contentText" class="post-txt" />
		<block v-if="(similarContentList && similarContentList.length>0) && similarContentShow">

			<view style="border: 5rpx solid #0081ff;padding-bottom: 10rpx;border-radius: 10rpx; font-size: 28rpx; ">
				<view
					style="display: flex; justify-content: space-between; align-items: center; padding: 10rpx 10rpx 0rpx 10rpx;">
					<view class="text-bold text-blue">可能相关的帖子</view>
					<u-icon class="iconfont" name="close-circle" size="50" @click="similarContentShow=false"
						color="#0081ff">
					</u-icon>
				</view>
				<view v-for="(content, index) in similarContentList" :key="index" class="similar-post-text"
					@click="jump(content)" v-if="content.contentMainText">
					<text style="font-weight: bold;">{{index+1}}.</text>
					{{content.contentMainText}}
				</view>
			</view>

		</block>

		<u-tabs :list="tabList" font-size="28" name="cate_name" bg-color="#fff" :current="current" @change="tabChange"
			height="35" :bold="false" gutter="20">
		</u-tabs>
		<!-- 上传图片 -->
		<block v-if="form.contentType!=2 &&form.isSpecial!=3 &&form.isSpecial!=4">
			<view @click="judgePrivacy">
				<u-upload ref="uUpload" :size-type="['compressed']" name="Image" :max-count="9" :header="header"
					@on-choose-complete='chooseImg' @on-list-change='changeImg' :auto-upload="false"
					:max-size='15485760'>
				</u-upload>
			</view>
		</block>
		<block v-if="form.contentType==2 ||form.isSpecial==3||form.isSpecial==4">
			<view>
				<text v-if="videoFileName" class="text-blue">{{videoFileName}}</text>
				<text v-if="videoFileName" style="margin-left: 10rpx;" class=" text-gray cuIcon-delete"
					@click="deleteVideo"></text>
				<button @click="addVideo" class="cu-btn block bg-blue lg  ">
					<text class="cuIcon-upload"></text> {{showVideoName}}</button>
			</view>
		</block>
		<!-- 选择帖子类型 -->
		<view v-if="previousPage!='secondHand'" @click="" class="choose-item1" style="padding-bottom: 0;">
			<view style="display: flex; align-items: center;justify-content: space-between;">
				<view class="action">
					<text style="font-size: 28rpx">选择帖子类型（必填）</text>
				</view>
			</view>
			<view style="margin: 10rpx 0; display: flex;  flex-wrap:wrap;justify-content: space-between;"
				v-if="previousPage!='secondHand'">
				<view v-for="(contentType, index) in contentTypeList" :key="index" style=""
					@click="chooseContentTypeNew(index)">
					<view
						style="height: 50rpx; border-radius: 25rpx; border: 2rpx solid #000000; background-color: #fff; margin-bottom: 15rpx; padding: 5rpx 30rpx; color: #000000; font-size: 24rpx;align-items: center;"
						v-if="contentTypeIndex!=index&&contentType.name!='闲置交易'">
						#{{contentType.name}}
					</view>
					<view
						style="height: 50rpx; border-radius: 25rpx; ;margin-bottom: 15rpx;  padding: 5rpx 30rpx;font-size: 24rpx;color: #fff; background-color: #55aaff; align-items: center; font-weight: bold;"
						v-if="contentTypeIndex==index&&contentType.name!='闲置交易'">
						#{{contentType.name}}
					</view>
					<view
						style="height: 50rpx; border: 2rpx solid #ff0000; background-color: #ffffff; border-radius: 25rpx; margin-bottom: 15rpx; padding: 5rpx 30rpx; color: #ff0000; font-size: 24rpx;align-items: center;"
						v-if="contentTypeIndex!=index&&contentType.name=='闲置交易'">
						#{{contentType.name}}
					</view>
					<view
						style="height: 50rpx; border-radius: 25rpx; margin-bottom: 15rpx;  padding: 5rpx 30rpx;font-size: 24rpx;color: #fff; background-color: #ff0000; align-items: center; font-weight: bold;"
						v-if="contentTypeIndex==index&&contentType.name=='闲置交易'">
						#{{contentType.name}}
					</view>

				</view>
			</view>
			<view style="margin: 10rpx 0; display: flex;  flex-wrap:wrap;justify-content: left;"
				v-if="previousPage=='secondHand'">
				<view v-for="(contentType, index) in contentTypeList" :key="index" style=""
					@click="chooseContentTypeNew(index)">
					<view
						style="height: 50rpx; border-radius: 25rpx; ;margin-bottom: 15rpx;  padding: 5rpx 30rpx;font-size: 24rpx;color: #fff; background-color: #55aaff; align-items: center; font-weight: bold;"
						v-if="contentTypeIndex==index">
						#{{contentType.name}}
					</view>
				</view>
			</view>
		</view>
		<!-- 匿名发帖 -->
		<view @click="" class="choose-item1" v-if="(isClockIn==0||isClockIn==2)&&anonymousState==1">
			<view style="display: flex; align-items: center;justify-content: space-between;">
				<view class="action">
					<!-- <u-icon name="question-circle-fill" size="32" @click=""color="#575757">
					</u-icon> -->
					<text style="font-size: 28rpx">随机马甲</text>
					<text v-if="addPost.anonymous_need_score!=0">（消耗{{addPost.anonymous_need_score}}积分）</text>
					<u-icon v-if="addPost.night_chat_state==1" name="question-circle-fill" size="32"
						@click="showAnonymousRule" color="#575757">
					</u-icon>
				</view>
				<u-switch @change="isSpecialChange" v-model="isSpecialSwitch" active-value="1" inactive-value="0"
					inactive-color="#F3F5F8" size="40"></u-switch>
			</view>
			<view style="font-size: 30rpx ; color: #caab78; font-weight:bold;" v-if="addPost.anonymous_need_score!=0">
				现有积分: {{nowScore}}
			</view>
		</view>

		<view @click="" class="choose-item1" style="padding-bottom: 20rpx;">
			<view style="display: flex; align-items: center;justify-content: space-between;">
				<view class="action">
					<text style="font-size: 28rpx">内容仅认证校友可见</text>
				</view>
				<u-switch @change="alumniOnlyChange" v-model="alumniOnlySwitch" active-value="1" inactive-value="0"
					inactive-color="#F3F5F8" size="40"></u-switch>
			</view>
		</view>

		<view class="choose-item1" v-if="isClockIn==0 ||isClockIn==2">
			<view style="display: flex; align-items: center;justify-content: space-between;">
				<view class="action">
					<text style="font-size: 28rpx">留下联系方式（仅认证校友可见）</text>
				</view>
				<u-switch v-model="showContactInfo" inactive-color="#F3F5F8" size="40"></u-switch>
			</view>
			<view v-if="showContactInfo==true" style="margin-top: 10rpx;">
				<!-- 				<view style="color:#979797; font-size: 26rpx;">
					仅通过认证的校友有权获取您的联系方式
				</view> -->
				<input v-model="contactInfo"
					style="background-color: #efefef; padding: 10rpx; border-radius: 10rpx ;min-height: 70rpx; font-size: 26rpx; "
					placeholder="请填写您的联系方式" />

			</view>
		</view>
		<view @click="" class="choose-item" v-if="inClockInPlate==0 && chatGPTDisable==false">
			<view class="action">
				<text class="">@GPT</text>
				<text v-if="chatGPTDisable">(暂不可用)</text>
			</view>
			<radio-group @change="radioChangeGPT">
				<label class="margin-left-sm" v-for="(item, index) in GPTItems" :key="item.value">
					<radio style="transform:scale(0.8)" class="blue radio" :value="item.value"
						:checked="index === currentGPTItem" :disabled="chatGPTDisable"></radio>
					<text class="margin-left-sm">{{item.name}}</text>
				</label>
			</radio-group>
		</view>

		<view class="text-blue text-bold" style="margin-top: 20rpx; font-size: 28rpx;"
			v-if="changeTopic==0 && isClockIn==0" @click="onAddTopic">
			#添加话题
		</view>
		<view class="text-blue text-bold" v-if="changeTopic!=0">
			#{{currentTopic}}
			<text style="margin-left: 10rpx;" class=" text-gray cuIcon-delete" @click="deleteTopic"></text>
		</view>
		<view style="display: flex; justify-content: left; flex-wrap: wrap; flex-direction: row; font-size: 26rpx; "
			v-if="addTopic==1">
			<view v-for="topic in topicList" v-if="topic.name">
				<text class="discuss-title" @tap="onChangeTopic(topic)">#{{ topic.name }}
				</text>
			</view>
			<view style="color: #55aaff; margin: 0 10rpx; font-size: 26rpx;" @click="onAddNewTopic">+新建话题</view>
		</view>
		
		<view class="submit-btn">
			<view style="width: 100%;margin-bottom: 20rpx; display: flex; align-items: center; justify-content: center;" v-if="form.contentType==8 && firstPage.AI_search_state==1">
			    <view style="color: #e79c06; font-weight: bold;" @click="AISelectContent(1)">👉AI匹配买/卖家（限时免费中）</view>
			</view>
			<q-button @click="onTabSumbit" shape="circle" color="#000000">发布帖子</q-button>
			<!-- <q-button v-if="form1.type == 2" @click="videoSubmit" shape="circle">发布</q-button> -->
		</view>

		<!-- 底部天黑天亮倒计时 -->
		<view style="height: 60rpx;"></view>
		<view class="tabbar-count-down" v-if="addPost.night_chat_state==1">
			<view style="font-weight: bold;display: flex;flex-direction: row; align-items: center;"
				@click="showAnonymousRule">
				<u-count-down color="#0081ff" separator="zh" :timestamp="countDownStamp" separator-color="#0081ff"
					bg-color="#a6dcff"></u-count-down>
				<view style="font-size: 30rpx;color: #0081ff;">{{countDownText}}</view>
			</view>
		</view>

		<view class="cu-modal show" v-if="modalName=='addNewTopic'">
			<view class="cu-dialog">
				<view class="modal-top">
					<view class="modal-top-text">
						<view>新建话题</view>
					</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="modal-bottom">
					<view style="text-align: center; margin-top:30rpx">
						<input class="text-left text-sm modal-input" placeholder-class="text-grey"
							placeholder="请输入要创建的话题" v-model="newTopicForm.name" />
						<button class="cu-btn bg-grey "
							style="height: 50rpx; width: 200rpx; margin-top: 20px ; margin-right:40rpx;"
							@click="hideModal">
							取消</button>
						<button class="cu-btn bg-red "
							style="height: 50rpx; width: 200rpx; margin-top: 20px; margin-left:40rpx;"
							@click="onSubmitNewTopic">
							确定</button>
					</view>
				</view>
			</view>
		</view>
		<view v-if="showExaminePopup==1">
			<examine-popup @returnHid='returnHide'></examine-popup>
		</view>
		<!-- 隐私协议弹窗 -->
		<privacy-popup></privacy-popup>

	</view>
</template>

<script>
	import {
		mapState,
		mapActions,
		mapMutations,
	} from 'vuex'
	import {
		reqPublishContent,
		getPlateLists,
		createPlate,
		getUserScore,
		getActionNumToday,
		redeemScore,
		getSimilarContent,
		getScoreAndAnonymousState,
		getStateOfCheckBeforePublish,
		getAISelectContent,
		getContentById,
	} from '../../api/index.js'
	import localData from '../../utils/data.js';
	import timeFormat from '../../uview-ui/libs/function/timeFormat.js';
	export default {
		async onLoad(options) {
			//localdata帖子类型值赋予
			this.contentTypeList = []
			for (var i = 0; i < localData.contentTypeList.length; i++) {
				if (localData.contentTypeList[i].type) {
					this.contentTypeList.push(localData.contentTypeList[i])
				}
			}
			//取出Storage的内容
			this.contactInfo = uni.getStorageSync('contactInfomation')
			if (uni.getStorageSync('postTitle')) {
				this.form.title = uni.getStorageSync('postTitle')
			}
			if (uni.getStorageSync("postContentText")) {
				this.form.contentText = uni.getStorageSync("postContentText")
			}
			this.form.plateId = this.basicPlateId;
			//如果传入参数中有板块信息
			if (options.contentType) { //url中参数会转为字符串，注意v-if用==
				this.form.contentType = options.contentType;
				//带帖子类型就赋一下值
				for (var i = 0; i < this.contentTypeList.length; i++) {
					if (this.contentTypeList[i].type == options.contentType) {
						this.contentTypeIndex = i
					}
				}
			}

			if (options.name) {
				this.plateName = options.name;
				this.currentTopic = options.name;
				this.changeTopic = 1;
			}
			if (options.plateId) {
				this.form.plateId = options.plateId;
			}
			if (options.clockIn && options.clockIn == 1) {
				this.isClockIn = options.clockIn
				this.form.plateId = options.plateId
				this.isAnonymous = 1
				this.isSpecialSwitch = true
				this.form.isSpecial = 1
				this.form.visibleRange = 1
			}
			if (options.clockIn && options.clockIn == 2) {
				this.isClockIn = options.clockIn
				this.form.plateId = options.plateId
				this.form.visibleRange = 1
			}
			//上一个页面标识
			if (options.previousPage) {
				this.previousPage = options.previousPage
			}
			this.form.userId = this.userInfos.userId
			this.form.schoolId = this.currentSchoolId
			this.topicList = this.addPost.top_list
			this.initialTopicId = this.addPost.initial_topic
			this.newTopicForm.portraitUrl = this.addPost.new_topic_portrait_url
			this.newTopicForm.backImgUrl = this.addPost.new_topic_back_img_url
			if (this.addPost.GPT_state == 1) {
				this.chatGPTDisable = false
			}
			// this.anonymousState = this.addPost.anonymous_state
			if (this.addPost.show_add_video_tag == 0) {
				this.tabList = [{
					name: '图片'
				}, ]
			} else {
				this.tabList = [{
						name: '图片'
					},
					{
						name: '视频'
					},
				]
			}
			await this.getNowscore()


		},
		// onBackPress() {
		// 	//在ios系统左滑右滑、androd系统的手机返回按钮均无效
		// 	console.log("页面回退")
		// },
		onUnload() {
			console.log("页面回退")
			let that = this
			if (this.isSubmit == 0 && (this.form.title != '' || this.form.contentText != '') && this.haveAISearch == 0) {
				uni.showModal({
					// title: '提示',
					content: '你有尚未发布的内容，是否保存',
					confirmText: '保存',
					success: function(res) {
						if (res.confirm) {
							console.log("确认")
							if (that.form.title != '') {
								uni.setStorageSync("postTitle", that.form.title);
							}
							if (that.form.contentText != '') {
								uni.setStorageSync("postContentText", that.form.contentText);
							}
						} else if (res.cancel) {
							console.log("取消")
							uni.setStorageSync("postTitle", '');
							uni.setStorageSync("postContentText", '');
						}
					}
				})
			}

		},
		data() {
			return {
				tempContentUrls: [],
				items: [{
						value: '0',
						name: '否',
						checked: 'true'
					},
					{
						value: '1',
						name: '是',

					},

				],
				GPTItems: [{
						value: '0',
						name: '否',
						checked: 'true'
					},
					{
						value: '1',
						name: '是',

					},

				],
				currentGPTItem: 0,
				chatGPTDisable: true,
				anonymousState: 0,
				currentItem: 0,
				form: {
					type: 0, //帖子类型
					userId: '',
					title: '',
					contentText: '',
					plateId: '',
					contentUrl: '',
					visibleRange: 0,
					contentType: 0,
					isSpecial: 0,
					downNum: 0,
					place: '',
					alumniOnly: 0,
				},
				plateName: '校园广场',
				header: {
					token: uni.getStorageSync('token')
				},
				isTopic: true,
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
				showVideoName: '选择视频', //按钮显示文字
				videoFileName: '',
				videoInfo: '',
				inClockInPlate: 0, //是否在打卡活动板块内
				contentId: '25c7d327725043c5b841e2ed218ac993',
				//是否来打卡
				isClockIn: 0,
				//标签列表
				tabList: [{
						name: '图片'
					},
					{
						name: '视频'
					},
				],
				current: 0,
				topicList: [],
				addTopic: 0,
				changeTopic: 0,
				currentTopic: '',
				initialTopicId: '',
				//匿名和视频的标识符
				isVideo: 0,
				isAnonymous: 0,
				//新建话题模态框
				modalName: '',
				newTopicForm: {
					cateId: '3',
					schoolId: 1,
					name: '',
					introduction: '',
					portraitUrl: '',
					backImgUrl: '',
					plateId: 'onCreate'
				},
				nowScore: 0,
				actionTodayForm: {
					userId: '',
				},
				//联系方式相关
				isSpecialSwitch: false,
				alumniOnlySwitch: false,
				showContactInfo: false,
				contactInfo: '',
				//认证相关
				showExaminePopup: 0,
				//回退相关
				isSubmit: 0,
				//相似帖子搜索相关
				searchNum: 0,
				similarContentList: [],
				similarContentShow: true,
				similarCommentContentList: [],
				similarAIContentList: [],
				//帖子类型选择
				contentTypeList: [{
						type: 5,
						name: "分享吐槽"
					},
					{
						type: 6,
						name: "求助答疑"
					},
					{
						type: 7,
						name: "组队扩列"
					},
					{
						type: 8,
						name: "二手兼职"
					},
					{
						type: 1,
						name: "#投票"
					}
				],
				contentTypeIndex: -1,
				countDownText: '',
				checkBeforePublish: 0,
				previousPage: '',
				currentAlumniOnly: 0,
				haveAISearch: 0,
				searchRecordId: ''

			};
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
			...mapState('user', ['currentSchoolId', 'basicPlateId', 'scoresFront', 'test', 'currentUserType']),
			...mapState('config', ['addPost', 'privacyPopup', 'alumniOnly', 'firstPage']),
			...mapState('content', ['currentContent']),
			countDownStamp() {
				let countDown = 0
				let nowTimeStamp = new Date().getTime()
				let targetHour = 0;
				let targetDate = new Date();
				if (new Date().getHours() >= 21) {
					// 如果现在时间晚于或等于21点，设置目标时间为次日8点
					targetHour = 8;
					targetDate.setDate(targetDate.getDate() + 1); // 将日期加1，即表示次日
					targetDate.setHours(targetHour, 0, 0, 0);
					this.countDownText = "后天亮"
				} else if (new Date().getHours() <= 8) {
					// 如果现在时间为0点到8点，设置目标时间为当日8点
					targetHour = 8;
					targetDate.setHours(targetHour, 0, 0, 0);
					this.countDownText = "后天亮"
				} else {
					// 如果现在时间为8-21点，设置目标时间为今天的21点
					targetHour = 21;
					targetDate.setHours(targetHour, 0, 0, 0);
					this.countDownText = "后进入夜聊"
				}
				// 计算目标时间点的时间戳
				let targetTimeStamp = targetDate.getTime();
				// 计算时间差（以秒为单位）
				countDown = Math.floor((targetTimeStamp - nowTimeStamp) / 1000);
				return countDown
			}
		},
		methods: {

			...mapMutations('user', {
				setScoresFront: 'setScoresFront'
			}),
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
				setHomeContentImage: 'setHomeContentImage',
			}),
			switchChange: function(e) {
				console.log('当前switch携带值为', e.detail.value)
				if (e.detail.value) {
					this.form.contentType = 3
				} else {
					this.form.contentType = 0
				}
			},
			clearImages() {
				this.tempContentUrls = []
				this.$refs.uUpload.clear();
			},
			async onPaste() {
				let txt = await this.copyAndPaste(1, this, '')
				console.log(txt)
				this.form.contentText = txt
			},
			chooseImg(lists) {
				this.tempContentUrls = lists.map(item => item.url)
			},
			changeImg(lists) {
				this.tempContentUrls = lists.map(item => item.url)
			},
			radioChange: function(e) {
				for (let i = 0; i < this.items.length; i++) {
					if (this.items[i].value === e.detail.value) {
						this.currentItem = i;
						break;
					}
				}
				this.form.visibleRange = Number(this.items[this.currentItem].value)
				console.log(this.form.visibleRange)
			},
			onTabSumbit() {
				let that = this
				//AI匹配
				if (this.haveAISearch == 0 && this.firstPage.AI_search_state == 1 && this.form.contentType == 8) {
					uni.showModal({
						content: 'AI匹配买/卖家限时免费中，是否体验？',
						confirmText: '体验一下',
						cancelText: '直接发布',
						success: function(res) {
							if (res.confirm) {
								that.AISelectContent(2)
							} else if (res.cancel) {
								that.submit()
							}
						}
					})
				} else {
					this.submit()
				}
			},
			async submit() {
				if (this.form.contentText == '') {
					uni.showToast({
						title: '输入帖子内容！',
						icon: "none"
					});
					return
				}
				if (this.form.plateId == '') {
					uni.showToast({
						title: '请选择发布板块！',
						icon: "none"
					});
					return
				}
				if (this.form.contentType == 2 && !this.videoInfo) {
					uni.showToast({
						title: '请选择视频！',
						icon: "none"
					});
					return
				}
				if ((this.form.isSpecial == 1 || this.form.isSpecial == 4) && this.nowScore < 50) {
					uni.showToast({
						title: '积分不足！',
						icon: "none"
					});
					return
				}
				if (this.showContactInfo == true && (this.contactInfo == null || !this.contactInfo || this
						.contactInfo == '')) {
					uni.showToast({
						title: '请填写联系方式！',
						icon: "none"
					});
					return
				}
				if (this.form.contentType != 5 && this.form.contentType != 6 && this.form.contentType != 7 && this.form
					.contentType != 8) {
					uni.showToast({
						title: '请选择帖子类型！',
						icon: "none"
					});
					return
				}
				//订阅小程序通知，不可放在回调函数中
				localData.requestMessage([7, 9, 10])
				uni.showLoading({
					title: '发布中...',
					mask: true
				})
				//获取校友认证配置信息
				// const alumniInfo = await localData.aboutAlumniOnly('alumniOnly', this.currentSchoolId)
				let alumniInfo = this.alumniOnly.state
				let isAlumniOnly = alumniInfo
				console.log("jieguo", isAlumniOnly)
				//当isAlumniOnly==1时，进行权限判断
				if (isAlumniOnly != 0) {
					//先进行隐私授权判断
					// console.log(this.privacyPopup.needAuthorization)
					if (this.privacyPopup.needAuthorization) {
						let needPrivacyAuthorization = await this.judgePrivacySetting()
						// console.log(needPrivacyAuthorization)
						if (needPrivacyAuthorization == true) {
							uni.hideLoading();
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
						uni.showToast({
							title: '请先完成校友认证！',
							icon: "none"
						});
						uni.hideLoading();
						this.showExaminePopup = 1
						return
					}

				}

				//无头像昵称，初始化为默认值
				if (this.userInfos.headimgUrl == '' || this.userInfos.headimgUrl == null) {
					let headResult = await this.userInfoInit();
					if (headResult != 'success') {
						// 用户拒绝授权使用昵称、头像
						uni.showToast({
							title: '初始化失败，请重试',
							icon: "none"
						});
						uni.hideLoading();
						return
					}
				}
				//用户选择携带联系方式
				if (this.showContactInfo == true) {
					this.form.place = this.contactInfo
					uni.setStorageSync("contactInfomation", this.contactInfo);
				}
				//获取【先审后发】配置状态
				let queryForm = {
					schoolId: this.currentSchoolId
				}
				const data = await getStateOfCheckBeforePublish(queryForm);
				if (data.code == 200) {
					console.log(data)
					this.checkBeforePublish = data.data
				}
				//用户当前为预警用户，帖子发布类型为冻结
				if (this.currentUserType == 1 || this.checkBeforePublish == 1) {
					this.form.contentState = 12
				}
				//发布帖子
				if (this.form.contentType == 2) {
					this.publishVideo()
				} else if (this.form.contentType > 4) {
					this.publishNewPost()
				} else {
					this.publishContent()
				}

			}, //submit
			async publishContent() {
				//#ifdef MP-WEIXIN
				//审核结果
				let checkResult;
				let checkStr = this.form.title + this.form.contentText;
				console.log(checkStr)
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, checkStr, 1);
				if (textCheck != 0) {
					//审核未通过的处理方法
					this.form.title = ''
					this.form.contentText = ''
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
				}
				checkResult |= textCheck;

				const oosImgUrl = []
				const {
					tempContentUrls
				} = this
				console.log(tempContentUrls);
				for (let i = 0, length = tempContentUrls.length; i < length; i++) {
					uni.showLoading({
						mask: true,
						title: '审核第' + (i + 1) + '/' + length + '张...'
					});
					//原始图片压缩
					// const compressResult = await localData.compressionIamge(this, tempContentUrls[i]);
					//console.log(compressResult)
					//const compressResult = await localData.compressionIamgeWX(this, tempContentUrls[i]);
					//微信官方压缩方法(二次压缩)
					const compressResult = await localData.compressionIamgeWXTwice(this, tempContentUrls[i], 1000);
					console.log(compressResult)
					const imgUrl = await this.uploadImageOSS(compressResult, 'content/contentImg/', this
						.currentSchoolId)
					//对图片进行审核
					let imgCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey.openId, imgUrl, 2, 1);
					if (imgCheck !== 87014) {
						checkResult |= 0;
						oosImgUrl.push(imgUrl)
					} else {
						this.clearImages()
						uni.hideLoading()
						uni.showToast({
							mask: true,
							icon: 'none',
							title: '图片可能潜在风险！'
						});
						return
					}
				}
				uni.hideLoading();
				//#endif
				if (checkResult === 0) {
					this.form.contentUrl = oosImgUrl.join(',');
					// 发送请求
					console.log(this.form)
					let str = JSON.stringify(this.form)
					let that = this //异步方法中this的作用域
					uni.request({
						url: localData.baseUrl + '/content/publishContentApi',
						data: str,
						method: 'POST',
						success(data) {
							console.log("返回", data)
							if (data.data.code === 200) {
								// that.setScoresFront(that.scoresFront + 10)
								// console.log('新积分：' + that.scoresFront)
								that.contentId = data.data.data.contentId
								uni.showToast({
									title: '帖子发布成功!',
									icon: "none"
								});
								setTimeout(function() {
									//如果隐藏首页闲置交易，且发布的闲置交易，跳转到闲置交易界面
									// console.log('发帖结束后', that.firstPage, that.form)
									if (that.firstPage.hide_second_hand_tab == 1 && that.form
										.contentType == 8) {
										uni.reLaunch({
											url: "/package_task/pages/second-hand-item/list",
										})
									}
									//跳转贴子页面
									else {
										uni.reLaunch({
											url: "detail?id=" + that.contentId + "&from=add",
										})
									}
								}, 1000)

							} else {
								uni.showToast({
									title: data.data.message,
									icon: "none"
								});
							}
						}
					})
				} else {
					//审核未通过的处理方法
					uni.showToast({
						title: '文字内容存在敏感信息，请检查！',
						icon: "none"
					});
					return
				}
			},
			//选择视频
			async addVideo() {
				let that = this
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
				uni.chooseVideo({
					sourceType: ['camera', 'album'],
					compressed: true,
					success: async function(res) {
						console.log(res);
						//大小及时长限制
						if (res.size > (500 * 1024 * 1024)) {
							uni.showToast({
								title: '视频不可大于500MB',
								icon: "none"
							});
							return
						}
						if (res.duration > 180) {
							uni.showToast({
								title: '请选择3分钟内的视频',
								icon: "none"
							});
							return
						}
						that.videoInfo = res
						var temp = res.tempFilePath.split('.')
						let suffix = temp[temp.length - 1]
						// that.showVideoName = timeFormat(Number(new Date()), "hh时MM分ss秒") + "选择"
						that.videoFileName = new Date().getTime() + "." + suffix
						that.showVideoName = "已上传，重新选择"
					}
				});
			},
			async publishVideo() {
				//#ifdef MP-WEIXIN
				//审核结果
				let checkResult;
				let checkStr = this.form.title + this.form.contentText;
				console.log(checkStr)
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, checkStr, 1);
				if (textCheck != 0) {
					//审核未通过的处理方法
					this.form.title = ''
					this.form.contentText = ''
					uni.hideLoading();
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
				}
				checkResult |= textCheck;
				//#endif
				//上传视频
				let videoUrl = '';
				uni.showLoading({
					mask: true,
					title: '视频上传中...'
				});
				console.log(this.videoInfo)
				//获取后缀
				var temp = this.videoInfo.tempFilePath.split('.')
				let suffix = temp[temp.length - 1]
				videoUrl = await this.uploadVideoOSS(this.videoInfo.tempFilePath, 'video/', suffix)
				this.form.contentUrl = videoUrl
				this.form.contentState = 7
				uni.hideLoading();
				if (checkResult === 0) {
					// 发送请求
					console.log(this.form)
					let str = JSON.stringify(this.form)
					let that = this //异步方法中this的作用域
					uni.request({
						url: localData.baseUrl + '/content/publishContentApi',
						data: str,
						method: 'POST',
						success(data) {
							console.log("data", data)
							if (data.data.code === 200) {
								// that.setScoresFront(that.scoresFront + 10)
								// console.log('新积分：' + that.scoresFront)
								// console.log("帖子发布成功")
								that.contentId = data.data.data.contentId
								uni.showToast({
									title: '视频需进行审核，请耐心等待',
									icon: "none"
								});
								setTimeout(function() {
									uni.reLaunch({
										url: "detail?id=" + that.contentId + "&from=add",
									})
									// uni.reLaunch({
									// 	url: "../index/index",
									// })
								}, 1000)

							} else {
								uni.showToast({
									title: data.data.message,
									icon: "none"
								});
							}
						}
					})
				} else {
					//审核未通过的处理方法
					uni.showToast({
						title: '文字内容存在敏感信息，请检查！',
						icon: "none"
					});
					return
				}
			},
			async publishNewPost() {
				//#ifdef MP-WEIXIN
				//审核结果
				let checkResult;
				let checkStr = this.form.title + this.form.contentText;
				console.log(checkStr)
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, checkStr, 1);
				if (textCheck != 0) {
					//审核未通过的处理方法
					this.form.title = ''
					this.form.contentText = ''
					uni.hideLoading();
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
				}
				checkResult |= textCheck;
				//视频相关的帖子
				if ((this.form.isSpecial == 3 || this.form.isSpecial == 4) && !this.videoInfo) {
					console.log('未选择视频')
				} else if ((this.form.isSpecial == 3 || this.form.isSpecial == 4) && this.videoInfo) {
					let videoUrl = '';
					uni.showLoading({
						mask: true,
						title: '视频上传中...'
					});
					console.log(this.videoInfo)
					//获取后缀
					var temp = this.videoInfo.tempFilePath.split('.')
					let suffix = temp[temp.length - 1]
					videoUrl = await this.uploadVideoOSS(this.videoInfo.tempFilePath, 'video/', suffix)
					this.form.contentUrl = videoUrl
					this.form.contentState = 7
					uni.hideLoading();
				}
				//图片相关的帖子
				else {
					const oosImgUrl = []
					const {
						tempContentUrls
					} = this
					console.log(tempContentUrls);
					for (let i = 0, length = tempContentUrls.length; i < length; i++) {
						uni.showLoading({
							mask: true,
							title: '审核第' + (i + 1) + '/' + length + '张...'
						});
						//原始图片压缩
						// const compressResult = await localData.compressionIamge(this, tempContentUrls[i]);
						//console.log(compressResult)
						//const compressResult = await localData.compressionIamgeWX(this, tempContentUrls[i]);
						//微信官方压缩方法(二次压缩)
						const compressResult = await localData.compressionIamgeWXTwice(this, tempContentUrls[i], 1000);
						console.log(compressResult)
						let imgUrl = await this.uploadImageOSS(compressResult, 'content/contentImg/', this
							.currentSchoolId)
						//对图片进行审核
						let imgCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey.openId, imgUrl, 2, 1);
						if (imgCheck !== 87014) {
							checkResult |= 0;
							console.log('替换前:' + imgUrl)
							imgUrl = imgUrl.replace('keming-bbs.oss-cn-shanghai.aliyuncs.com',
								'image.tanxiaojian.zone')
							oosImgUrl.push(imgUrl)
						} else {
							this.clearImages()
							uni.hideLoading()
							uni.showToast({
								mask: true,
								icon: 'none',
								title: '图片可能潜在风险！'
							});
							return
						}
					}
					uni.hideLoading();
					this.form.contentUrl = oosImgUrl.join(',');
				}
				//#endif
				uni.showLoading({
					title: '发布中...',
					mask: true
				})
				if (checkResult === 0) {
					// 发送请求
					//检测一下plateId
					if (!this.form.plateId) {
						this.form.plateId = this.basicPlateId;
						if (!this.basicPlateId) {
							this.form.plateId = this.initialTopicId
						}
					}
					console.log(this.form)
					let str = JSON.stringify(this.form)
					let that = this //异步方法中this的作用域
					uni.request({
						url: localData.baseUrl + '/content/publishContentApi',
						data: str,
						method: 'POST',
						success(data) {
							console.log("返回", data)
							if (data.data.code === 200) {
								that.isSubmit = 1;
								//匿名贴扣除积分
								if (that.form.isSpecial == 1 || that.form.isSpecial == 4) {
									let applyForm = {
										userId: that.userInfos.userId,
										changeNum: that.addPost.anonymous_need_score * 1
									}
									let result = redeemScore(applyForm)
								}
								that.contentId = data.data.data.contentId
								if ((that.form.isSpecial == 3 || that.form.isSpecial == 4) && that.videoInfo) {
									uni.hideLoading()
									uni.showToast({
										title: '视频需进行审核，请耐心等待',
										icon: "none"
									});
									setTimeout(function() {
										uni.reLaunch({
											url: "../index/index",
										})
									}, 1000)
								} else {
									uni.hideLoading()
									uni.showToast({
										title: '帖子发布成功，积分+25!',
										icon: "none"
									});
									setTimeout(function() {
										uni.reLaunch({
											url: "detail?id=" + that.contentId + "&from=add",
										})
									}, 1000)
								}

							} else {
								uni.hideLoading()
								uni.showToast({
									title: data.data.message,
									icon: "none"
								});
							}
						}
					})
				} else {
					//审核未通过的处理方法
					uni.hideLoading()
					uni.showToast({
						title: '文字内容存在敏感信息，请检查！',
						icon: "none"
					});
					return
				}

			},
			//调佣云函数-功能配置-打卡（已停用）
			aboutfunctionConfig(functionName) {
				let that = this
				wx.cloud.init({
					env: localData.envId,
					traceUser: true,
				})
				wx.cloud.callFunction({
					name: 'functionConfigSQL', //云函数的名称
					data: {
						functionName: functionName, //调用哪个数据库函数
						schoolId: this.currentSchoolId, //前
						clockInInfo: '',
						usingDatabase: localData.usingDatabase
					},
					complete: res => {
						let nowdata = res.result
						console.log("配置项", nowdata.clockInInfo.plate_id, that.form.plateId)
						if (nowdata) {
							if (that.form.plateId == nowdata.clockInInfo.plate_id) {
								that.form.visibleRange = 1,
									that.inClockInPlate = 1
							}
						}
						uni.hideLoading();
					}
				})
			},
			//图片视频切换
			tabChange(index) {
				this.current = index;
				if (index == 0 && this.isAnonymous == 0) {
					this.isVideo = 0
					this.form.isSpecial = 0
				} else if (index == 0 && this.isAnonymous == 1) {
					this.isVideo = 0
					this.form.isSpecial = 1
				} else if (index == 1 && this.isAnonymous == 0) {
					this.isVideo = 1
					this.form.isSpecial = 3 //非匿名视频
					uni.showToast({
						title: '上传视频前会进行压缩，预计2分钟内完成',
						icon: 'none',
						duration: 2500
					})
				} else if (index == 1 && this.isAnonymous == 1) {
					this.isVideo = 1
					this.form.isSpecial = 4 //匿名视频
					uni.showToast({
						title: '上传视频前会进行压缩，预计2分钟内完成',
						icon: 'none',
						duration: 2500
					})
				}
				console.log('帖子属性', this.form.isSpecial)

			},
			//是否匿名选项
			radioChangeNew: function(e) {
				for (let i = 0; i < this.items.length; i++) {
					if (this.items[i].value === e.detail.value) {
						this.currentItem = i;
						break;
					}
				}
				if (this.currentItem == 0 && this.isVideo == 0) {
					this.isAnonymous = 0
					this.form.isSpecial = 0

				} else if (this.currentItem == 0 && this.isVideo == 1) {
					this.isAnonymous = 0
					this.form.isSpecial = 3

				} else if (this.currentItem == 1 && this.isVideo == 0) {
					this.isAnonymous = 1
					this.form.isSpecial = 1
					uni.showToast({
						title: '请遵守社区规范，发布违规内容会受到处罚哦~',
						icon: "none"
					});
				} else if (this.currentItem == 1 && this.isVideo == 1) {
					this.isAnonymous = 1
					this.form.isSpecial = 4
					uni.showToast({
						title: '请遵守社区规范，发布违规内容会受到处罚哦~',
						icon: "none"
					});
				}
				console.log('帖子属性', this.form.isSpecial)

			},
			//匿名滑块
			isSpecialChange(value) {
				if (value == 0 && this.isVideo == 0) {
					this.isAnonymous = 0
					this.form.isSpecial = 0

				} else if (value == 0 && this.isVideo == 1) {
					this.isAnonymous = 0
					this.form.isSpecial = 3

				} else if (value == 1 && this.isVideo == 0) {
					this.isAnonymous = 1
					this.form.isSpecial = 1
					uni.showToast({
						title: '请遵守社区规范，发布违规内容会受到处罚哦~',
						icon: "none"
					});
				} else if (value == 1 && this.isVideo == 1) {
					this.isAnonymous = 1
					this.form.isSpecial = 4
					uni.showToast({
						title: '请遵守社区规范，发布违规内容会受到处罚哦~',
						icon: "none"
					});
				}
				console.log(value, this.form.isSpecial)
			},
			alumniOnlyChange(value) {
				this.form.alumniOnly = value * 1;
				console.log(value, this.form.alumniOnly)
			},
			//是否提问GPT选项
			radioChangeGPT: function(e) {
				for (let i = 0; i < this.items.length; i++) {
					if (this.items[i].value === e.detail.value) {
						this.currentGPTItem = i;
						break;
					}
				}
				if (this.currentGPTItem == 0) {
					this.form.downNum = 0
				} else if (this.currentGPTItem == 1) {
					this.form.downNum = 1
				} else {

				}
				console.log(this.form.downNum)
			},
			radioChangeAlumniOnly(e) {
				for (let i = 0; i < this.items.length; i++) {
					if (this.items[i].value === e.detail.value) {
						this.currentAlumniOnly = i;
						break;
					}
				}
				if (this.currentAlumniOnly == 0) {
					this.form.alumniOnly = 0
				} else if (this.currentAlumniOnly == 1) {
					this.form.alumniOnly = 1
				}
			},
			//调佣云函数-功能配置-添加话题（已停用）
			async aboutfunctionConfig2(functionName) {
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
							addTopicInfo: '',
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("配置项", nowdata, that.topicList)
							if (nowdata) {
								that.topicList = nowdata.addTopicInfo.top_list
								that.initialTopicId = nowdata.addTopicInfo.initial_topic
								that.newTopicForm.portraitUrl = nowdata.addTopicInfo
									.new_topic_portrait_url
								that.newTopicForm.backImgUrl = nowdata.addTopicInfo
									.new_topic_back_img_url
								resolve(nowdata)
							}
						}
					})
				})
			},
			//点击添加话题
			onAddTopic() {
				this.addTopic = 1
			},
			//切换话题
			onChangeTopic(topic) {
				this.changeTopic = 1
				this.currentTopic = topic.name
				this.form.plateId = topic.plateId
			},
			//删除话题
			deleteTopic() {
				this.changeTopic = 0
				this.form.plateId = this.initialTopicId
				// console.log(this.form.plateId)
			},
			//新建话题
			onAddNewTopic() {
				this.modalName = 'addNewTopic'
				this.newTopicForm.name = ''
			},
			hideModal() {
				this.modalName = ''
			},
			//新建话题提交
			async onSubmitNewTopic() {
				this.newTopicForm.userId = this.userInfos.userId;
				this.newTopicForm.schoolId = this.currentSchoolId
				// let permissionCheckResult = await this.permissionCheck(this.newTopicForm.userId, this.newTopicForm
				// 	.schoolId, this.newTopicForm
				// 	.plateId)
				// if (permissionCheckResult != 0) {
				// 	return
				// }
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
				if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
					return
				}
				if (permissionCheckResult != 0) {
					this.showExaminePopup = 1
					return
				}
				if (!this.newTopicForm.name) {
					uni.showToast({
						title: '请填写话题名称',
						icon: "none"
					});
					return;
				}
				for (var i = 0; i < this.topicList.length; i++) {
					if (this.topicList[i].name == this.newTopicForm.name) {
						uni.showToast({
							title: '存在同名话题',
							icon: "none"
						});
						this.modalName = ''
						return
					}
				}
				if (this.newTopicForm.name == '校园广场') {
					console.log("同名触发")
					uni.showToast({
						title: '存在同名话题',
						icon: "none"
					});
					let tempTopic = {
						name: '校园广场',
						plateId: this.basicPlateId
					}
					this.topicList.push(tempTopic)
					this.modalName = ''
					return
				}
				this.newTopicForm.introduction = this.newTopicForm.name
				uni.showLoading({
					mask: true,
					title: '正在审核中'
				});

				//#ifdef MP-WEIXIN

				let checkStr = this.form.name + this.form.introduction;
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, checkStr, 1);
				uni.showToast({
					mask: true,
					title: '审核结束'
				});
				console.log(textCheck);
				if (textCheck != 0) {
					//审核未通过的处理方法
					this.newTopicForm.name = ''
					this.newTopicForm.introduction = ''
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
				}

				this.newTopicForm.plateId = null //创建时重置主键字段,否则无法新建记录！
				const data = await createPlate(this.newTopicForm);
				if (data.code === 200) {
					uni.showToast({
						title: '创建话题成功',
						icon: "none"
					});
					//将新建项加入可选话题
					let tempTopic = {
						name: this.newTopicForm.name,
						plateId: data.data
					}
					this.topicList.push(tempTopic)
					//话题自动切换
					this.changeTopic = 1
					this.currentTopic = this.newTopicForm.name
					this.form.plateId = data.data
					//关模态框
					this.modalName = ''
					this.newTopicForm.plateId = 'onCreate'
				} else {
					this.newTopicForm.plateId = 'onCreate'
					uni.showToast({
						title: data.message,
						icon: "none"
					});
					//关模态框
					this.modalName = ''
					return
				}
				//#endif
			},
			deleteVideo() {
				this.videoFileName = '';
				this.videoInfo = '';
				this.showVideoName = '选择视频'
			},
			//调佣云函数-功能配置-@GPT（已停用）
			async aboutfunctionConfig3(functionName) {
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
							chatGPTInfo: '',
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("配置项", nowdata)
							if (nowdata) {
								if (nowdata.chatGPTInfo.GPT_state == 1) {
									that.chatGPTDisable = false
								}
								that.anonymousState = nowdata.chatGPTInfo.anonymous_state
							}
						}
					})
				})
			},
			//获取当前积分和匿名状态(24.1.28改动)
			async getNowscore() {
				// this.actionTodayForm.userId = this.userInfos.userId;
				// const data = await getUserScore(this.actionTodayForm);
				// if (data.code == 200) {
				// 	console.log(data)
				// 	this.nowScore = data.data.score
				// }
				let scoreForm = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const data = await getScoreAndAnonymousState(scoreForm);
				if (data.code == 200) {
					console.log("匿名状态", data)
					this.nowScore = data.data.score
					this.anonymousState = data.data.anonymousState
				}
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			async judgePrivacy() {
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
			},
			//获取相似帖子
			async getSimilarContentList() {
				let result = '';
				let qureyForm = {
					text: '',
					schoolId: this.currentSchoolId
				}
				qureyForm.text = this.form.contentText + this.form.title
				console.log(qureyForm.text)
				result = await getSimilarContent(qureyForm)
				if (result.code == 200) {
					for (var i = 0; i < result.data.length; i++) {
						if (result.data[i].title) {
							result.data[i].contentMainText = result.data[i].title + " " + result.data[i].contentText
						} else {
							result.data[i].contentMainText = result.data[i].contentText
						}
					}
					this.similarContentList = result.data
					this.similarContentShow = true
				}
				return this.similarContentList

			},

			//获取相似帖子
			async AISelectContent(SearchWay) {
				uni.showLoading({
					mask: true,
					title: '正在匹配中'
				})
				let result = '';
				let qureyForm = {
					text: '',
					schoolId: this.currentSchoolId,
					userId: this.userInfos.userId,
					searchWay: SearchWay
				}
				qureyForm.text = this.form.title + this.form.contentText
				console.log(qureyForm.text)
				if (!qureyForm.text) {
					uni.hideLoading()
					uni.showToast({
						title: "输入帖子内容！",
						icon: "none"
					})
					return
				}
				result = await getAISelectContent(qureyForm)
				if (result.code == 200) {
					this.haveAISearch = 1
					this.searchRecordId = result.data.searchRecordId
					let contentList = result.data.contentList
					for (var i = 0; i < contentList.length; i++) {
						if (contentList[i]) {
							if (contentList[i].title) {
								contentList[i].contentMainText = contentList[i].title + " " + contentList[i]
									.contentText
							} else {
								contentList[i].contentMainText = contentList[i].contentText
							}
						}
					}
					this.similarAIContentList = contentList
					let commentContentList = result.data.commentContentList
					console.log(commentContentList)
					for (var i = 0; i < commentContentList.length; i++) {
						commentContentList[i] = JSON.parse(commentContentList[i])
						console.log(commentContentList[i])
						if (commentContentList[i].title) {
							commentContentList[i].contentMainText = commentContentList[i].title + " " +
								commentContentList[i].contentText
						} else {
							commentContentList[i].contentMainText = commentContentList[i].contentText
						}
					}
					this.similarCommentContentList = commentContentList
				}
				uni.hideLoading()
				//如果没有结果
				if (this.similarAIContentList.length == 0 && this.similarCommentContentList.length == 0) {
					uni.showToast({
						title: "未搜索到匹配内容！",
						icon: "none"
					})
					this.submit()
				} else {
					this.modalName = "showAISearchRecord"
				}

			},
			//选择帖子类型
			async chooseContentTypeNew(index) {
				if (this.contentTypeIndex === index) {
					return;
				}
				this.contentTypeIndex = index;
				this.form.contentType = this.contentTypeList[index].type
				// console.log(this.form.contentType)
				if (this.contentTypeList[index].type == 1) {
					this.contentTypeIndex = -1;
					this.form.contentType = 0;
					uni.navigateTo({
						url: '/pages/vote/vote'
					})
					return;
				}
				//触发搜索
				if (this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) {
					await this.getSimilarContentList()
				}
			},
			async jump(content) {
				console.log(content.contentText);
				console.log('重新请求帖子详情');
				const data1 = await getContentById({
					contentId: content.contentId,
					userId: this.userInfos.userId
				})
				if (data1.code === 200) {
					//将头像和帖子图片制为空，等待本地缓存图片地址返回
					//帖子头像处理
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
				let url = ''
				url = '/pages/post/detail?id=' + content.contentId
				uni.navigateTo({
					url
				});
			},
			//匿名规则展示
			showAnonymousRule() {
				let appName="谈校间"
				if(this.currentSchoolId==2){
					appName="校果儿"
				}
				uni.showModal({
					title: "夜聊模式",
					content: "每天21:00 - 次日8:00，"+ appName +"将进入夜聊模式，期间可使用随机身份发帖。所有帖子将在夜聊结束时自动销毁。",
					showCancel: false,
					success: async res => {
						if (res.confirm) {} else if (res.cancel) {

						}
					},
				})
			},
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


		}, //methods
		watch: {
			form: {
				async handler(newVal, oldVal) {
					// console.log("触发")
					let similarContents = '';
					//小于5，搜索次数为0
					if ((this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) &&
						this.form.contentText.length + this
						.form.title.length < 5 && this.searchNum != 0) {
						console.log("置0")
						this.searchNum = 0
					}
					if ((this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) &&
						this.form.contentText.length + this
						.form.title.length >= 5 && this.searchNum == 0) {
						console.log("第一次")
						this.searchNum += 1
						similarContents = await this.getSimilarContentList()
						console.log(similarContents)
					}
					//大于5小于10 ，搜索次数为1
					if ((this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) &&
						this.form.contentText.length + this
						.form.title.length > 5 && this.form.contentText.length + this.form.title.length < 10 && this
						.searchNum != 1) {
						console.log("置1")
						this.searchNum = 1
					}
					if ((this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) &&
						this.form.contentText.length + this
						.form.title.length >= 10 && this.searchNum == 1) {
						console.log("第二次")
						this.searchNum += 1
						similarContents = await this.getSimilarContentList()
						console.log(similarContents)
					}
					//大于10小于15 ，搜索次数为2
					if ((this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) &&
						this.form.contentText.length + this
						.form.title.length > 10 && this.form.contentText.length + this.form.title.length < 15 && this
						.searchNum != 2) {
						console.log("置2")
						this.searchNum = 2
					}
					if ((this.form.contentType == 6 || this.form.contentType == 7 || this.form.contentType == 8) &&
						this.form.contentText.length + this
						.form.title.length >= 15 && this.searchNum == 2) {
						console.log("第三次")
						this.searchNum += 1
						similarContents = await this.getSimilarContentList()
						console.log(similarContents)
					}


				},
				deep: true,
			}
		}
	};
</script>

<style lang="scss" scoped>
	.title-input {
		padding-bottom: 20rpx;
		/* 或者你需要的具体数值 */
		margin-top: 20rpx;
		height: 60rpx;
		border-bottom: 1rpx solid #F5F5F5;
		font-size: 32rpx;
		// margin: 20rpx 0;
		// padding: 20rpx 0;
		// width: 294rpx;
	}

	.post-txt {
		width: 100%;
		padding: 20rpx 0;
		min-height: 300rpx;
		font-size: 28rpx;
	}

	.upload-wrap {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		width: 180rpx;
		height: 180rpx;
		background-color: #F5F5F5;
		margin-top: 30rpx;
		border-radius: 10rpx;

		.icon {
			width: 50rpx;
			height: 50rpx;
		}

		text {
			font-size: 24rpx;
		}
	}

	.upload-video {
		width: 180rpx;
		height: 180rpx;
		margin-top: 30rpx;
	}

	.choose-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border-bottom: 1px solid #F5F5F5;
		justify-content: space-between;

		&:last-child {
			border: 0;
		}

		.txt {
			margin-left: 20rpx;
		}

		.icon {
			width: 40rpx;
			height: 40rpx;
		}

		.u-icon {
			margin-left: auto;
			color: #999;
		}

		.add-icon {
			margin-left: 0;
		}
	}

	.choose-item1 {
		display: block;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1px solid #F5F5F5;
		justify-content: space-between;

		&:last-child {
			border: 0;
		}

		.txt {
			margin-left: 20rpx;
		}

		.icon {
			width: 40rpx;
			height: 40rpx;
		}

		.u-icon {
			margin-left: auto;
			color: #999;
		}

		.add-icon {
			margin-left: 0;
		}
	}

	.submit-btn {
		margin-top: 50rpx;
		color: #fff;
		font-size: 30rpx;
	}

	.discuss-title {
		height: 30rpx;
		padding: 10rpx 20rpx 10rpx 0rpx;
		line-height: 30rpx;
		border-radius: 20rpx;
		// font-size: 12px;
		// background: #f0f0f5;
		color: #55aaff;
		text-align: center;
		margin-right: 20rpx;
	}

	.modal-top {
		height: 60rpx;
		display: flex;
		justify-content: flex-end;
		padding-right: 20rpx;
		margin-bottom: 30rpx;
		background-color: #FFFFFF;
	}

	.modal-top-text {
		display: flex;
		justify-content: center;
		width: 90%;
		font-size: 34rpx;
		font-weight: bold;
	}

	.modal-bottom {
		display: block;
		// margin-left: 15rpx;
		padding-bottom: 20rpx;
		text-align: left;
	}

	.modal-button {
		width: 30%;
		display: block;
		text-align: center;
		// margin-right: 40rpx;
	}

	.modal-input {
		background-color: #fff;
		width: 95%;
		margin: 0 15rpx;
		padding-left: 20rpx;
	}

	.similar-post-text {
		display: -webkit-box;
		/*弹性伸缩盒子模型显示*/
		-webkit-box-orient: vertical;
		/*排列方式*/
		-webkit-line-clamp: 1;
		/*显示文本行数(这里控制多少行隐藏)*/
		overflow: hidden;
		/*溢出隐藏*/
		// width: 70%;
		height: 50rpx;
		// font-weight: bold;
		word-break: break-word;
		padding: 10rpx;
		font-size: 28rpx;
		//padding-bottom: 10rpx;
		//text-decoration: underline;
		// &:last-child {
		// 	padding-bottom: 20rpx;
		// }
	}

	.AI-res-model-content {
		display: flex;
		flex-direction: column;
		background: #ffffff;
		//background: #c7bdbd;
		//padding: 20rpx;
		padding-left: 20rpx;
		padding-right: 20rpx;
		padding-top: 10rpx;
		padding-bottom: 5rpx;
		margin-bottom: 16rpx;
	}

	.tabbar-count-down {
		//padding: 10rpx 36rpx;
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		z-index: 99;
		background-color: #a6dcff; // ffc208
		display: flex;
		justify-content: center;
		align-items: center;
		border-top: 1px solid #f5f5f5;
	}
</style>