<template>
	<view>
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<u-sticky bgColor="#fff" z-index="998">
			<view class="input-view" style="background-color: #fff; padding:10rpx 20rpx 20rpx 20rpx; height: 90rpx;">
				<u-search placeholder="搜索闲置交易" v-model="timeForm.keywords" @search="clearAndGetNew()"
					:show-action="false">
				</u-search>
			</view>
		</u-sticky>

		<u-waterfall v-model="contentList" ref="uWaterfall">
			<template v-slot:left="{leftList}">
				<view class="demo-warter" v-for="(item, index) in leftList" :key="index" @click="jump(item)">
					<!-- 警告：微信小程序中需要hx2.8.11版本才支持在template中结合其他组件，比如下方的lazy-load组件 -->
					<!-- 					<u-lazy-load v-if="item.contentTempUrls" threshold="-450" border-radius="10"
						:image="item.contentTempUrls[0]" :index="index"></u-lazy-load> -->
					<view style="display: flex; justify-content: center;">
						<image v-if="item.contentUrls &&item.contentUrls[0]" :lazy-load="true" mode="aspectFill"
							style="height: 400rpx;" :src="item.contentUrls[0]" webp="true"
							@tap.stop="previewImage(item.contentUrls[0], item.contentUrls)">
						</image>
					</view>
					<view v-if="!item.contentUrls || !item.contentUrls[0]" class="text-container">
						<rich-text v-if="item.title && item.title!=null" class="content-text"
							:nodes="item.title + '<br />' + item.contentText"></rich-text>
						<rich-text v-if="!item.title || item.title==null" class="content-text"
							:nodes="item.contentText"></rich-text>
					</view>
					<view class="demo-title">
						{{item.title}} + {{item.contentText}}
					</view>
					<view style="display: flex; align-items: center;">
						<view class="avatar">
							<u-avatar size="40" :src="item.headimgUrl" level-bg-color="#8072f3"></u-avatar>
						</view>
						<view class="demo-shop">
							{{item.nickName}}
						</view>
					</view>

				</view>
			</template>
			<template v-slot:right="{rightList}">
				<view class="demo-warter" v-for="(item, index) in rightList" :key="index" @click="jump(item)">
					<!-- 					<u-lazy-load v-if="item.contentTempUrls" threshold="-450" border-radius="10"
						:image="item.contentTempUrls[0]" :index="index"></u-lazy-load> -->
					<view style="display: flex; justify-content: center;">
						<image v-if="item.contentUrls &&item.contentUrls[0]" :lazy-load="true" mode="aspectFill"
							style="height: 400rpx;" :src="item.contentUrls[0]" webp="true"
							@tap.stop="previewImage(item.contentUrls[0], item.contentUrls)">
						</image>
					</view>
					<view v-if="!item.contentUrls || !item.contentUrls[0]" style="width: 100%; height: 400rpx; "
						class="text-container">
						<rich-text v-if="item.title && item.title!=null" class="content-text"
							:nodes="item.title + '<br />' + item.contentText"></rich-text>
						<rich-text v-if="!item.title || item.title==null" class="content-text"
							:nodes="item.contentText"></rich-text>
					</view>
					<view class="demo-title">
						{{item.title}} + {{item.contentText}}
					</view>
					<view style="display: flex; align-items: center;">
						<view class="avatar">
							<u-avatar size="40" :src="item.headimgUrl" level-bg-color="#8072f3"></u-avatar>
						</view>
						<view class="demo-shop">
							{{item.nickName}}
						</view>
					</view>
				</view>
			</template>
		</u-waterfall>
		<block v-if="contentList.length==0">
			<u-empty margin-top="100" text="暂无相关帖子" mode="favor"></u-empty>
		</block>
		<u-loadmore v-if="contentList.length>0" bg-color="rgb(240, 240, 240)" :status="loadStatus" @loadmore="getMore">
		</u-loadmore>
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
		getUserStateBySchool,
		getContentById,
		searchContentsByContentType,

	} from "../../../api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['schoolList']),
			...mapState('user', ['currentSchoolId']),
			...mapState('user', ['scoresFront', 'currentUserState']),
			...mapState('remind', ['msgCount']),
			...mapState('remindWSS', ['markState']),
			...mapState('config', ['firstPage']),
			...mapState('content', ['homeContentList', 'currentContent']),
			...mapState('popup', ['showAdNum']),
		},
		data() {
			return {
				contentList: [],
				timeForm: {
					pageNum: 0,
					pageSize: 5,
					userId: '',
					schoolId: '',
					contentType: '',
					queryTime: '',
					keywords: ''
				},
				loadStatus: 'loadmore',
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
				coverUrl: [],
				contentKeyword: '',
			}
		},
		// async created() {
		// 	this.timeForm.userId = this.userInfos.userId;
		// 	this.timeForm.schoolId = this.currentSchoolId;
		// 	this.timeForm.contentType = 8
		// 	this.clearAndGetNew()
		// },
		onLoad(options) {
			console.log(options)
			this.timeForm.userId = this.userInfos.userId;
			this.timeForm.schoolId = this.currentSchoolId;
			this.timeForm.contentType = 8
			this.timeForm.keywords = options.keyword
			this.clearAndGetNew()
		},
		methods: {
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
			}),
			async clearAndGetNew() { //清空帖子列表并请求最新的帖子列表
				if (!this.timeForm.keywords) {
					uni.showToast({
						title: '请输入检索内容',
						icon: 'none'
					})
					return
				}
				this.contentList = []
				this.clear()
				//传入首页查询时间
				const now = new Date(); // 获取当前时间
				const year = now.getFullYear(); // 年份
				const month = now.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
				const day = now.getDate(); // 日期
				const hour = now.getHours(); // 小时
				const minute = now.getMinutes(); // 分钟
				const second = now.getSeconds(); // 秒钟
				this.timeForm.queryTime = year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' +
					second;
				console.log(this.timeForm)
				const data = await searchContentsByContentType(this.timeForm);
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
				const data = await searchContentsByContentType(this.timeForm);
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
				let c = 1;
				uni.navigateTo({
					url: '/package_task/pages/second-hand-item/search?keyword=' + this.contentKeyword +
						'&current=' + c
				});
				this.contentKeyword = ''
			},
			clear() {
				this.$refs.uWaterfall.clear();
			}

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
		margin: 5px;
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
		height: 400rpx;
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
	}

	.avatar {
		display: flex;
		justify-content: center;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		margin-right: 20rpx;
	}
</style>
