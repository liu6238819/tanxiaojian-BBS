<template>
	<view>
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<u-sticky bgColor="#fff" z-index="998">
			<view class="input-view" style="background-color: #fff; padding:10rpx 20rpx 20rpx 20rpx; height: 90rpx;">
				<u-search placeholder="搜索闲置交易" v-model="contentKeyword" @search="toSearch" :show-action="false">
				</u-search>
			</view>
		</u-sticky>

		<u-waterfall v-model="contentList" ref="uWaterfall">
			<template v-slot:left="{leftList}">
				<view class="demo-warter-left" v-for="(item, index) in leftList" :key="index" @click="jump(item)">
					<!-- 警告：微信小程序中需要hx2.8.11版本才支持在template中结合其他组件，比如下方的lazy-load组件 -->
					<!-- 					<u-lazy-load v-if="item.contentTempUrls" threshold="-450" border-radius="10"
						:image="item.contentTempUrls[0]" :index="index"></u-lazy-load> -->
					<view style="display: flex; justify-content: center;">
						<image v-if="item.contentUrls &&item.contentUrls[0]" :lazy-load="true" mode="aspectFill"
							style="height: 300rpx;" :src="item.contentUrls[0]" webp="true">
						</image>
					</view>
					<view v-if="!item.contentUrls || !item.contentUrls[0]" class="text-container">
						<rich-text v-if="item.title && item.title!=null" class="content-text"
							:nodes="item.title + '<br />' + item.contentText"></rich-text>
						<rich-text v-if="!item.title || item.title==null" class="content-text"
							:nodes="item.contentText"></rich-text>
					</view>
					<view class="demo-title">
						<text v-if="item.title &&item.title!=null">{{item.title}}，</text>
						<text>{{item.contentText}}</text>
					</view>
					<view style="display: flex; align-items: center; justify-content: space-between;">
						<view style="display: flex; align-items: center;">
							<view class="avatar">
								<u-avatar size="40" :src="item.headimgUrl" level-bg-color="#8072f3"></u-avatar>
							</view>
							<view class="demo-shop">
								{{item.nickName}}
							</view>
						</view>
						<view v-if="item.commentNum>0" class="demo-shop">
							{{item.commentNum}}评论
						</view>
					</view>

				</view>
			</template>
			<template v-slot:right="{rightList}">
				<view class="demo-warter-right" v-for="(item, index) in rightList" :key="index" @click="jump(item)">
					<!-- 					<u-lazy-load v-if="item.contentTempUrls" threshold="-450" border-radius="10"
						:image="item.contentTempUrls[0]" :index="index"></u-lazy-load> -->
					<view style="display: flex; justify-content: center;">
						<image v-if="item.contentUrls &&item.contentUrls[0]" :lazy-load="true" mode="aspectFill"
							style="height: 300rpx;" :src="item.contentUrls[0]" webp="true">
						</image>
					</view>
					<view v-if="!item.contentUrls || !item.contentUrls[0]" style="width: 100%; height: 300rpx; "
						class="text-container">
						<rich-text v-if="item.title && item.title!=null" class="content-text"
							:nodes="item.title + '<br />' + item.contentText"></rich-text>
						<rich-text v-if="!item.title || item.title==null" class="content-text"
							:nodes="item.contentText"></rich-text>
					</view>
					<view class="demo-title">
						<text v-if="item.title &&item.title!=null">{{item.title}}，</text>
						<text>{{item.contentText}}</text>
					</view>
					<view style="display: flex; align-items: center; justify-content: space-between;">
						<view style="display: flex; align-items: center;">
							<view class="avatar">
								<u-avatar size="40" :src="item.headimgUrl" level-bg-color="#8072f3"></u-avatar>
							</view>
							<view class="demo-shop">
								{{item.nickName}}
							</view>
						</view>
						<view v-if="item.commentNum>0" class="demo-shop">
							{{item.commentNum}}评论
						</view>
					</view>
				</view>
			</template>
		</u-waterfall>
		<u-loadmore bg-color="rgb(240, 240, 240)" :status="loadStatus" @loadmore="getMore"></u-loadmore>
		<view v-if="firstPage.page_path!='/package_task/pages/second-hand-item/list'" class="bottom-box">
			<view class="add-box">
				<view class="mid-button" @click="toPublishPost">
					<u-icon name="plus" size="50" ></u-icon>
				</view>
				
			</view>
		</view>
		<!-- 显示tabBar中的红色圆点(消息提示) -->
		<view v-if="firstPage.page_path=='/package_task/pages/second-hand-item/list'">
			<q-tabbar :active="1" :count="msgCount" :markState="markState"></q-tabbar>
		</view>
		<view class="reload-page" @click="reloadPage">
			<!-- <text class="cuIcon-pullup" style="font-size: 60rpx;"></text> -->
			<u-icon name="reload" size="50"> </u-icon>
		</view>
		<view class="to-home" @click="toFisrtPage">
			<u-icon name="home" size="50"> </u-icon>
		</view>
		<!-- 认证弹窗 -->
		<view v-if="showExaminePopup==1">
			<examine-popup @returnHid='returnHide'></examine-popup>
		</view>
	</view>
</template>

<script>
	import localData from '../../../utils/data.js';
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	import {
		reqContentList,
		getSchoolLists,
		searchKeyWords,
		getUserStateBySchool,
		getContentById,
		getUserScore,
		getOneBbsConfig,
		getHomeContentsImage,
	} from "../../../api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['schoolList']),
			...mapState('user', ['currentSchoolId']),
			...mapState('user', ['scoresFront', 'currentUserState']),
			...mapState('content', ['homeContentList', 'currentContent']),
			...mapState('config', ['firstPage', 'privacyPopup', 'alumniOnly']),
			...mapState('remind', ['msgCount']),
			...mapState('remindWSS', ['markState']),
		},
		onPullDownRefresh() {
			uni.redirectTo({
				url: "/package_task/pages/second-hand-item/list"
			});
		},
		data() {
			return {
				contentList: [],
				timeForm: {
					pageNum: 0,
					pageSize: 10,
					plateId: '',
					userId: '',
					requestType: 'time',
					schoolId: '',
					contentType: '',
					queryTime: '',
				},
				loadStatus: 'loadmore',
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
				coverUrl: [],
				contentKeyword: '',
				//认证相关
				showExaminePopup: 0,
			}
		},
		methods: {
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
			}),
			...mapMutations('popup', {
				increaseShowSecondHandPageNum: 'increaseShowSecondHandPageNum',
			}),
			async clearAndGetNew() { //清空帖子列表并请求最新的帖子列表
				this.timeForm.pageNum = this.timeForm.pageNum + 1;
				// //在查询第一页时，传入首页查询时间
				if (this.timeForm.pageNum == 1) {
					const now = new Date(); // 获取当前时间
					const year = now.getFullYear(); // 年份
					const month = now.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
					const day = now.getDate(); // 日期
					const hour = now.getHours(); // 小时
					const minute = now.getMinutes(); // 分钟
					const second = now.getSeconds(); // 秒钟
					this.timeForm.queryTime = year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' +
						second;
				}
				console.log(this.timeForm)
				const data = await reqContentList(this.timeForm);
				if (data && data.code === 200) {
					if (data.data.length > 0) {
						const stepData = data.data
						this.contentList = []
						//将头像和帖子图片制为空，等待本地缓存图片地址返回
						for (var i = 0; i < stepData.length; i++) {
							//头像处理
							stepData[i].headimgUrl = stepData[i].headimgUrl.toString() //临时头像
							// stepData[i].headimgUrl = ''
							//帖子图片处理
							if (stepData[i].contentUrl !== null && stepData[i].contentUrl !== undefined && stepData[i]
								.contentUrl !== '') { //帖子有图
								//将帖子图片分割在contentUrls数组
								stepData[i].contentUrls = stepData[i].contentUrl.split(',');
								//视频贴处理
								if ((stepData[i].contentType == 2 || stepData[i].isSpecial == 3 || stepData[i]
										.isSpecial == 4) && stepData[i]
									.contentUrls.length > 1) {
									stepData[i].videoId = stepData[i].contentUrls[stepData[i].contentUrls
										.length - 1]
								}
							} else {
								stepData[i].contentUrls = []; //帖子无图
							}
						}
						this.contentList.push(...stepData)
						this.loadStatus = 'loadmore'
					} else {
						this.loadStatus = 'nomore'
					}
					console.log("首页列表", this.contentList);
					if (this.timeForm.pageNum == 1 && this.showAdNum != 1) {
						uni.showToast({
							title: '刷新成功',
							icon: 'none'
						})
					}
				} else {
					uni.showToast({
						title: '帖子请求失败，请检查网络或者重新进入小程序',
						icon: 'none'
					})
					console.log("首页列表请求失败");
				}

			},
			async getNew() { //请求最新的帖子列表
				this.timeForm.pageNum = this.timeForm.pageNum + 1;
				console.log('最新请求参数：')
				console.log(this.timeForm)
				if (this.contentList.length > 0) {
					const time = new Date(this.contentList[this.contentList.length - 1]
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
				const data = await reqContentList(this.timeForm);
				if (data.code === 200) {
					if (data.data.length > 0) {
						const stepData = data.data
						//将头像和帖子图片制为空，等待本地缓存图片地址返回
						for (var i = 0; i < stepData.length; i++) {
							//头像处理
							stepData[i].headimgUrl = stepData[i].headimgUrl.toString() //临时头像
							// stepData[i].headimgUrl = ''
							//帖子图片处理
							if (stepData[i].contentUrl !== null && stepData[i].contentUrl !== undefined && stepData[i]
								.contentUrl !== '') { //帖子有图
								//将帖子图片分割在contentUrls数组
								stepData[i].contentUrls = stepData[i].contentUrl.split(',');
								//视频贴处理
								if ((stepData[i].contentType == 2 || stepData[i].isSpecial == 3 || stepData[i]
										.isSpecial == 4) && stepData[i]
									.contentUrls.length > 1) {
									stepData[i].videoId = stepData[i].contentUrls[stepData[i].contentUrls
										.length - 1]
								}
							} else {
								stepData[i].contentUrls = []; //帖子无图
							}
						}
						this.contentList.push(...stepData)
						this.loadStatus = 'loadmore'
					} else {
						this.loadStatus = 'nomore'
					}
					//console.log("首页列表", this.homeContentList);
				} else {
					console.log("首页列表请求失败");
				}

			},
			//跳转帖子
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
				this.getNew()
			},
			async textToImgUrl(text) {
				return new Promise((resolve, reject) => {
					let imageUrl = ""
					// 获取设备信息
					let res = uni.getSystemInfoSync();
					// 获取窗口宽度
					let windowWidth = res.windowWidth;
					const ctx = uni.createCanvasContext('canvas', this);
					// 设置画布尺寸
					this.cWidth = 500;
					this.cHeight = 500;
					ctx.clearRect(0, 0, this.cWidth, this.cHeight); // 清空画布

					// 设置字体样式
					let fontSize = 16;
					ctx.setFontSize(fontSize);
					ctx.setFillStyle('#000');
					ctx.setTextAlign('center')

					// 绘制文本
					const text2 = 'Hello, UniApp!';

					const textLines = this.wrapText(ctx, text, 300);
					console.log('文字宽度', textLines)
					// 计算文本在画布中的位置，使文本在图片中上下居中
					let y = (this.cHeight - (fontSize * textLines.length)) / 2 + fontSize;

					// 绘制文本
					for (let i = 0; i < textLines.length; i++) {
						const x = this.cWidth - 100 - ctx.measureText(textLines[i]).width / 2;
						ctx.fillText(textLines[i], x, y);
						y += fontSize;
					}
					let that = this
					// 绘制完成后将Canvas转换为临时图片文件
					ctx.draw(true, () => {
						uni.canvasToTempFilePath({
							canvasId: 'canvas',
							success: res => {
								// 可以在这里保存生成的图片
								uni.getImageInfo({
									src: res.tempFilePath,
									success: info => {
										that.coverUrl[0] = res.tempFilePath
										console.log(res.tempFilePath, info
											.path);
										resolve(info.path)
									}
								});
							},
							fail: error => {
								console.log(error);
							}
						}, this);
					});
				})
			},
			wrapText(ctx, text, maxWidth) {
				// const words = text.split(' ');
				const words = text.match(/[\u4e00-\u9fa5\d\w\s.,!?:;'"]/ig);
				const lines = [];
				let currentLine = '';

				for (let i = 0; i < words.length; i++) {
					const word = words[i];
					const width = ctx.measureText(currentLine + '' + word).width;

					if (width < maxWidth) {
						currentLine += (currentLine == '' ? '' : ' ') + word;
					} else {
						lines.push(currentLine);
						currentLine = word;
					}
				}

				lines.push(currentLine);
				return lines;
			},
			previewImage(url, urls) {
				uni.previewImage({
					current: url, // 当前显示图片的http链接
					urls: urls // 需要预览的图片http链接列表
				});
			},
			toSearch() {
				if (!this.contentKeyword) {
					uni.showToast({
						title: '请输入检索内容',
						icon: 'none'
					})
					return
				}
				let c = 1;
				uni.navigateTo({
					url: '/package_task/pages/second-hand-item/search?keyword=' + this.contentKeyword +
						'&current=' + c
				});
				this.contentKeyword = ''
			},
			async toPublishPost() {
				//userId直接返回
				if (!this.userInfos.userId) {
					uni.showToast({
						title: '请登录后发帖'
					})
					return;
				}
				//获取校友认证配置信息,从store配置项中取
				// let alumniInfo = await localData.aboutAlumniOnly('alumniOnly', this.currentSchoolId)
				let alumniInfo = this.alumniOnly.state
				let isAlumniOnly = alumniInfo
				console.log("身份认证开关", isAlumniOnly)
				//当isAlumniOnly==1时，进行权限判断
				if (isAlumniOnly == 1) {
					//先进行隐私授权判断
					console.log(this.privacyPopup.needAuthorization)
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

				}
				uni.navigateTo({
					url: '/pages/post/add?contentType=' + 8 + '&previousPage=secondHand'
				})
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			//刷新页面
			reloadPage() {
				// uni.reLaunch({
				// 	url: "/package_task/pages/second-hand-item/list"
				// });
				uni.redirectTo({
					url: "/package_task/pages/second-hand-item/list"
				});
			},
			//回到首页
			toFisrtPage() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			},


		}, //mothed结束
		async created() {
			this.increaseShowSecondHandPageNum()
			this.timeForm.userId = this.userInfos.userId;
			this.timeForm.schoolId = this.currentSchoolId;
			this.timeForm.contentType = 8
			this.clearAndGetNew()
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
	.demo-warter {
		border-radius: 8px;
		margin: 5px 5px 5px 5px;
		background-color: #FFFFFF;
		padding: 8px;
		position: relative;
	}
	
	.demo-warter-left {
		border-radius: 8px;
		margin: 5px 2.5px 5px 5px;
		background-color: #FFFFFF;
		padding: 8px;
		position: relative;
	}
	
	.demo-warter-right {
		border-radius: 8px;
		margin: 5px 5px 5px 2.5px;
		background-color: #FFFFFF;
		padding: 8px;
		position: relative;
	}

	.u-close {
		position: absolute;
		top: 32rpx;
		right: 32rpx;
	}

	.demo-image {
		width: 100%;
		border-radius: 4px;
		height: 10rpx;
	}

	.demo-title {
		font-size: 30rpx;
		margin-top: 5px;
		margin-bottom: 5px;
		color: $u-main-color;
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		white-space: pre-wrap;
		overflow: hidden;
		word-break: break-all;
		word-wrap: break-word;
		font-weight: bold;
		font-size: 26rpx;
	}

	.demo-tag {
		display: flex;
		margin-top: 5px;
	}

	.demo-tag-owner {
		background-color: $u-type-error;
		color: #FFFFFF;
		display: flex;
		align-items: center;
		padding: 4rpx 14rpx;
		border-radius: 50rpx;
		font-size: 20rpx;
		line-height: 1;
	}

	.demo-tag-text {
		border: 1px solid $u-type-primary;
		color: $u-type-primary;
		margin-left: 10px;
		border-radius: 50rpx;
		line-height: 1;
		padding: 4rpx 14rpx;
		display: flex;
		align-items: center;
		border-radius: 50rpx;
		font-size: 20rpx;
	}

	.demo-price {
		font-size: 30rpx;
		color: $u-type-error;
		margin-top: 5px;
	}

	.demo-shop {
		font-size: 22rpx;
		color: $u-tips-color;
		// margin-top: 5px;
	}

	.warter-text {
		width: 100%;
		height: 400rpx;
		padding: 60rpx 40rpx;
		text-align: center;
		background-image: url("../../static/text_bg.png");
		background-size: cover;
		background-repeat: no-repeat;
		background-position: center center;
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 5;
		white-space: pre-wrap;
		overflow: hidden;
		word-break: break-all;
		word-wrap: break-word;
	}

	.text-container {
		width: 100%;
		height: 300rpx;
		padding: 60rpx 40rpx;
		text-align: center;
		background-image: url("../../static/text_bg.png");
		background-size: cover;
		background-repeat: no-repeat;
		background-position: center center;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.content-text {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 5;
		white-space: pre-wrap;
		overflow: hidden;
		word-break: break-all;
		word-wrap: break-word;
		font-size: 26rpx;
	}

	.avatar {
		display: flex;
		justify-content: center;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		margin-right: 10rpx;
	}

	.bottom-box {
		width: 100vW;
		display: flex;
		justify-content: center;
	}

	.add-box {
		position: fixed;
		bottom: 0;
		width: 120rpx;
		height: 120rpx;
		background: rgba(255, 255, 255, 0.5);
		border-radius: 60rpx 60rpx 0 0;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	
	.mid-button {
		background-color: #333;
		width: 90rpx;
		height: 90rpx;
		border-radius: 50%;
		color: #fff;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.add-icon {
		width: 100rpx;
		height: 100rpx;
	}
	
	.reload-page {
		background-color: rgba(0, 0, 0, 0.4);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: calc(10% + 100rpx);
		right: 5%;
		height: 80rpx;
		width: 80rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.to-home {
		background-color: rgba(0, 0, 0, 0.4);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 10%;
		right: 5%;
		height: 80rpx;
		width: 80rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
</style>
