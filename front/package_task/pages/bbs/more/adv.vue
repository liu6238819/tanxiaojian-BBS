<template>
	<view>
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<block v-if="advInfo.kind==1">
			<image :src="advInfo.imageDetail" style="width: 100%;" :style="{height: imageHeight+'px'}"
				show-menu-by-longpress="true"></image>
		</block>
		<block v-if="advInfo.kind==2">
			<!-- 图片 -->
			<block v-if="advInfo.imageUrls && advInfo.imageUrls.length>1">
				<image :src="advInfo.imageUrls[0]" style="width: 100%; vertical-align:bottom"
					:style="{height: imageHeight2+'px'}" show-menu-by-longpress="true"></image>
			</block>
			<!-- 表格 -->
			<view style="width: 100%;" :style="{backgroundImage: `url(${advInfo.formJson.bgImage})`}">
				<view style="padding: 40rpx;">
					<view style="width: 100%; text-align: center;">
						<view class='cu-tag round bg-gradual-orange text-bold' style="margin-bottom: 10rpx; height: 80rpx;
						 width: 50%; font-size: 36rpx;">{{advInfo.formJson.title}}</view>
					</view>
					<view style="border-radius: 20rpx;padding: 20rpx; background-color: #fff;">
						<view v-for="item in signInfo">
							<view v-if="item.kind==1" class="cu-form-group">
								<!-- v-if="line.kind=0"> -->
								<view class="title">{{item.title}}</view>
								<input :placeholder="item.placeholder" name="input" v-model="item.value"></input>
							</view>
							<view v-if="item.kind==2" class="cu-form-group">
								<!-- v-if="line.kind==1" -->
								<view class="title">{{item.title}}</view>
								<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
									<view class="picker">
										{{item.index>-1?item.value:item.placeholder}}
									</view>
								</picker>
							</view>
							<view v-if="item.kind==3" class="cu-form-group">
								<!-- v-if="line.kind=0"> -->
								<view class="cuIcon-title text-red"></view>
								<view class="title">{{item.title}}</view>
								<input :placeholder="item.placeholder" name="input" v-model="item.value"></input>
							</view>
							<view v-if="item.kind==4" class="cu-form-group">
								<!-- v-if="line.kind==1" -->
								<view class="cuIcon-title text-red"></view>
								<view class="title">{{item.title}}</view>
								<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
									<view class="picker">
										{{item.index>-1?item.value:item.placeholder}}
									</view>
								</picker>
							</view>
							<view class="cu-form-group" v-if="item.kind==5">
								<!-- 可见范围 -->
								<view class="choose-item">
									<view class="action">
										<text class="title">{{item.placeholder}}</text>
									</view>
									<!-- 上传图片 -->
									<u-upload ref="uUpload" :size-type="['compressed']" name="Image" :max-count="9"
										:header="header" @on-choose-complete='(lists) => {chooseImg(lists, item)}'
										@on-list-change='(lists) => {changeImg(lists, item)}' :auto-upload="false"
										:max-size='10485760' :file-list="item.fileList">
									</u-upload>
								</view>
							</view>
						</view>
						<view style="display: flex;align-items: center;justify-content: space-around; background-color: #fff;
						padding: 20rpx;">
							<button class="cu-btn lg text-bold text-white" @click="advSignUpSumbit"
								v-if="isExistSignUp==0" style="width: 80%; background-color:#588aff;">
								提交
							</button>
							<button class="cu-btn lg text-bold text-white" @click="advSignUpSumbit"
								v-if="isExistSignUp==1" style="width: 80%; background-color:#588aff;">
								修改信息
							</button>
						</view>
					</view>
				</view>
			</view>
			<!-- 图片2 -->
			<block v-if="advInfo.imageUrls && advInfo.imageUrls.length>1">
				<image :src="advInfo.imageUrls[1]" style="width: 100%; vertical-align:bottom"
					:style="{height: imageHeight3+'px'}" show-menu-by-longpress="true"></image>
			</block>
			<block v-if="advInfo.imageUrls && advInfo.imageUrls.length==1">
				<image :src="advInfo.imageUrls[0]" style="width: 100%; vertical-align:bottom"
					:style="{height: imageHeight3+'px'}" show-menu-by-longpress="true"></image>
			</block>
			<!-- 表格 -->
			<view style="width: 100%;" :style="{backgroundImage: `url(${advInfo.formJson.bgImage})`}">
				<view style="padding: 40rpx; border-radius: 10rpx;">
					<view style="width: 100%; text-align: center;">
						<view class='cu-tag round bg-gradual-orange text-bold' style="margin-bottom: 10rpx; height: 80rpx;
						 width: 50%; font-size: 36rpx;">{{advInfo.formJson.title}}</view>
					</view>
					<view style="border-radius: 20rpx;padding: 20rpx; background-color: #fff;">
						<view v-for="item in signInfo">
							<view v-if="item.kind==1" class="cu-form-group">
								<!-- v-if="line.kind=0"> -->
								<view class="title">{{item.title}}</view>
								<input :placeholder="item.placeholder" name="input" v-model="item.value"></input>
							</view>
							<view v-if="item.kind==2" class="cu-form-group">
								<!-- v-if="line.kind==1" -->
								<view class="title">{{item.title}}</view>
								<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
									<view class="picker">
										{{item.index>-1?item.value:item.placeholder}}
									</view>
								</picker>
							</view>
							<view v-if="item.kind==3" class="cu-form-group">
								<!-- v-if="line.kind=0"> -->
								<view class="cuIcon-title text-red"></view>
								<view class="title">{{item.title}}</view>
								<input :placeholder="item.placeholder" name="input" v-model="item.value"></input>
							</view>
							<view v-if="item.kind==4" class="cu-form-group">
								<!-- v-if="line.kind==1" -->
								<view class="cuIcon-title text-red"></view>
								<view class="title">{{item.title}}</view>
								<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
									<view class="picker">
										{{item.index>-1?item.value:item.placeholder}}
									</view>
								</picker>
							</view>
							<view class="cu-form-group" v-if="item.kind==5">
								<!-- 可见范围 -->
								<view class="choose-item">
									<view class="action">
										<text class="title">{{item.placeholder}}</text>
									</view>
									<!-- 上传图片 -->
									<u-upload ref="uUpload2" :size-type="['compressed']" name="Image" :max-count="9"
										:header="header" @on-choose-complete='(lists) => {chooseImg(lists, item)}'
										@on-list-change='(lists) => {changeImg(lists, item)}' :auto-upload="false"
										:max-size='10485760' :file-list="item.fileList2">
									</u-upload>
								</view>
							</view>
						</view>
						<view style="display: flex;align-items: center;justify-content: space-around; background-color: #fff;
						padding: 20rpx;">
							<button class="cu-btn lg text-bold text-white" @click="advSignUpSumbit"
								v-if="isExistSignUp==0" style="width: 80%; background-color:#588aff;">
								提交
							</button>
							<button class="cu-btn lg text-bold text-white" @click="advSignUpSumbit"
								v-if="isExistSignUp==1" style="width: 80%; background-color:#588aff;">
								修改信息
							</button>
						</view>
					</view>
				</view>
			</view>
		</block>
		<view class="share-part">
			<button class="cu-btn shareIndex  " @click="toFisrtPage"> 首页 </button>
		
			<button class="cu-btn  sharePerson " @click="toGroupCode(2)"> 客服 </button>
		</view>
	</view>
</template>

<script>
	import localData from '@/utils/data.js';
	import {
		mapState,
		mapMutations,
	} from 'vuex'
	import {
		getOneAdvById,
		getSignUpInfo,
		addSignUpInfo,
		createAdvSignUp,
		getAdvSignUp,
	} from "@/api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
			...mapState('config', ['firstPage']),
		},
		data() {
			return {
				advId: '',
				advInfo: '',
				imageHeight: 0,
				WindowInfo: '',
				imageHeight2: 0,
				imageHeight3: 0,
				mySignUpinfo: [],
				infoForm: {
					activityId: '',
					userId: '',
					batchNumber: '',
				},
				isExistSignUp: 0,
				sumbitInfo: { //提交的报名信息
					activityId: '',
					infoJson: {
						data: [],
					},
					userId: '',
					batchNumber: '',
				},
				//标单图片
				tempContentUrls: [],
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高
				fileList: [],
				signInfo: [],
			}
		},
		async onLoad(options) {
			let that = this
			console.log(options)
			this.advId = options.id;
			//获取屏幕信息
			this.WindowInfo = uni.getWindowInfo();
			await this.getOneAdv()
			if (this.advInfo.kind == 2) {
				await this.getMyAdvSignUp()
			}
			//设置标题
			uni.setNavigationBarTitle({
				title: that.advInfo.title
			});


		},
		methods: {
			//获取广告相关
			async getOneAdv() {
				let form = {
					id: this.advId,
					userId: this.userInfos.userId,
				}
				if (form.userId == '' || form.userId == null) {
					console.log("userId缺失")
					return
				}
				const data = await getOneAdvById(form);
				if (data.code === 200) {
					if (data.data.kind == 1) {
						//将网络图片缓存到本地，并返回本地地址
						data.data.imageDetail = await this.getImageLocalUrl('advImage', data.data.imageDetail)
						// console.log("广告信息",data.data)
						//缓存结束
						this.imageHeight = await this.setImageHeight(data.data.imageDetail)
					}
					if (data.data.kind == 2) {
						//两张图片转列表
						if (data.data.imageDetail !== null && data.data.imageDetail !== undefined && data.data
							.imageDetail !== '') {
							data.data.imageUrls = data.data.imageDetail.split(',');
							//将网络图片缓存到本地，并返回本地地址
							for (var i = 0; i < data.data.imageUrls.length; i++) {
								data.data.imageUrls[i] = await this.getImageLocalUrl('advImage', data.data.imageUrls[i])
								// console.log("广告信息",data.data)
							}
							//缓存结束
						} else {
							data.data.imageUrls = [];
						}
						//分别设置两张图片高度
						console.log(data.data)
						if (data.data.imageUrls.length > 1) {
							this.imageHeight2 = await this.setImageHeight(data.data.imageUrls[0])
							this.imageHeight3 = await this.setImageHeight(data.data.imageUrls[1])
						} else if (data.data.imageUrls.length == 1) {
							this.imageHeight3 = await this.setImageHeight(data.data.imageUrls[0])
						}
						data.data.formJson = JSON.parse(data.data.formJson)
						//将网络图片(form背景图片)缓存到本地，并返回本地地址
						data.data.formJson.bgImage = await this.getImageLocalUrl('advImage', data.data.formJson.bgImage)
						//缓存结束
						//处理报名表中的图片信息
						for (var i = 0; i < data.data.formJson.template.length; i++) {
							if (data.data.formJson.template[i].kind == 5) {
								data.data.formJson.template[i].fileList = []
								data.data.formJson.template[i].fileList2 = []
							}
						}
						this.signInfo = data.data.formJson.template
					}
					this.advInfo = data.data
					console.log("触发广告获取", this.advInfo, this.signInfo)
				} else {
					console.log("广告请求失败");
				}

			},
			//设置图片高度
			async setImageHeight(imageUrl) {
				let height = 0;
				let that = this;
				return new Promise((resolve, reject) => {
					uni.getImageInfo({
						src: imageUrl,
						success: function(image) {
							console.log(image.width);
							console.log(image.height);
							height = that.WindowInfo.screenWidth / image.width * image.height
							console.log(height)
							resolve(height)
						}
					});
				})
			},
			PickerChange(e, item) {
				item.index = e.detail.value
				item.value = item.options[item.index]
			},
			chooseImg(lists, currentItem) {
				currentItem.tempContentUrls = lists.map(item => item.url)
				console.log("选图", lists, "当前项", currentItem)

			},
			changeImg(lists, currentItem) {
				console.log("变化", lists)
				if (lists.length > 0) {
					currentItem.tempContentUrls = lists.map(item => item.url)
					console.log("有图片", currentItem.tempContentUrls, currentItem.fileList)
				} else {
					currentItem.tempContentUrls = []
					// currentItem.fileList = []
					console.log("没有图片", currentItem.tempContentUrls, currentItem.fileList)
				}
			},
			//
			async advSignUpSumbit() {
				let that = this
				let sumbitInfo = { //提交的报名信息
					advId: this.advInfo.id,
					userId: this.userInfos.userId,
					infoJson: {
						data: [],
					},

				}
				//图片上传
				for (var i = 0; i < this.signInfo.length; i++) {
					console.log("这时的图片", this.signInfo[i].tempContentUrls, this.signInfo[i].fileList);
					if (this.signInfo[i].kind == 5 && this.signInfo[i].tempContentUrls && this.signInfo[i]
						.tempContentUrls.length > 0) {
						const {
							signInfo
						} = this
						signInfo[i].value = ''
						console.log(signInfo[i].tempContentUrls);
						for (var j = 0; j < signInfo[i].tempContentUrls.length; j++) {
							let imgUrl = ''
							if (signInfo[i].tempContentUrls[j].substr(0, 5) == "https") {
								imgUrl = signInfo[i].tempContentUrls[j]
								console.log("直接写入", imgUrl)
							} else {
								const compressResult = await localData.compressionIamge(this, signInfo[i]
									.tempContentUrls[j]);
								console.log(compressResult)
								imgUrl = await this.uploadImageOSS(compressResult, 'content/contentImg/', this
									.currentSchoolId)
								console.log("上传成功", imgUrl)
							}
							if (this.signInfo[i].value == '' || this.signInfo[i].value == null) {
								console.log("第一次写入", this.signInfo[i].value)
								this.signInfo[i].value = imgUrl
							} else {
								console.log("继续写入", imgUrl)
								this.signInfo[i].value = this.signInfo[i].value + ',' + imgUrl
							}
						}
					} else {

					}
				}
				let arr = this.signInfo
				for (var i = 0; i < arr.length; i++) {
					if (arr[i].kind == 3 || arr[i].kind == 4) {
						if (arr[i].value == '') {
							uni.showToast({
								title: '存在未填项',
								icon: "none",
								duration: 3000,
							})
							return
						}
					}
				}
				sumbitInfo.infoJson.data = this.signInfo
				console.log(sumbitInfo)
				// let res = await addSignUpInfo(this.sumbitInfo)
				let res = await createAdvSignUp(sumbitInfo)
				if (res.code == 200) {
					console.log(res)
					uni.showToast({
						title: '提交成功',
						icon: "none",
					})
					setTimeout(function() {
						uni.redirectTo({
							url: '/package_task/pages/bbs/more/adv?id=' + that.advInfo.id
						});
					}, 1500);

				} else {
					uni.showToast({
						title: res.message,
						icon: "none"
					})
				}
			},
			toGroupCode(codeKind) {
				//console.log('触发')
				uni.navigateTo({
					url: '/package_task/pages/group-code/index?codeKind=' + codeKind
				})
			},
			//跳转首页
			async toFisrtPage() {
				uni.switchTab({
					url: '/pages/index/index'
				});
				// if (this.currentUserState == 2) {
				// 	localData.requestMessage([7, 8])
				// }
			},
			async getMyAdvSignUp() {
				let infoForm = {
					advId: this.advInfo.id,
					userId: this.userInfos.userId,
				}
				let data = await getAdvSignUp(infoForm);
				console.log('报名信息', data)
				if (data.code == 200) {
					if (data.data == null) {
						this.isExistSignUp = 0
					} else {
						this.isExistSignUp = 1
						//处理报名表中的图片信息
						for (var i = 0; i < data.data.info.data.length; i++) {
							if (data.data.info.data[i].kind == 5 && data.data.info.data[i].value != '') {
								data.data.info.data[i].fileList = []
								data.data.info.data[i].fileList2 = []
								let arr = data.data.info.data[i].value.split(",");
								for (var j = 0; j < arr.length; j++) {
									if (arr[j] == 'undefined') {
										continue;
									}
									let tempData = {
										url: ''
									}
									tempData.url = arr[j]
									data.data.info.data[i].fileList.push(tempData)
									data.data.info.data[i].fileList2.push(tempData)
								}
							}
						}
						let temp = []
						const stepData = data.data.info.data.sort(function(item1, item2) {
							return item1.order - item2.order
						})
						temp.push(...stepData) //过渡数据
						this.signInfo = temp.map(item => {
							if (!item.index) {
								item.index = -1;
							}
							return item
						})
					}
				}

			}
		},
		watch: { //监听userid变化，防止阅读人数统计不准确
			userInfos: {
				async handler(newVal, oldVal) {
					// #ifdef MP-WEIXIN
					if (this.userInfos != null && this.userInfos != '') {
						await this.getOneAdv()
					}
					// #endif

				},
				//deep: true
			}
		},

	}
</script>

<style lang="scss" scoped>
	.container {
		height: 100vh;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.shareIndex {
		background-color: rgba(0, 0, 0, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 20%;
		right: 5%;
		width: 150rpx;
	}
	
	.sharePerson {
		background-color: rgba(0, 0, 0, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 13%;
		right: 5%;
		width: 150rpx;
	}
</style>