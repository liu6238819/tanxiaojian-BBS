<template>
	<view>
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<block>
			<!-- 表格 -->
			<view style="width: 100%;">
				<view style="padding: 20rpx;">
					<view style="width: 100%;">
						<view class='text-bold' style="margin-bottom: 10rpx;font-size: 32rpx;"
							v-if="advInfo.formJson.bussinessText">
							{{advInfo.formJson.bussinessText}}
							<text style="color: red;">（可多选）</text>
						</view>
					</view>
					<view style="display: flex; flex-wrap: wrap; width: 100%;color: #113d73;">
						<view v-for="(item,index) in businessList" :key="index" style="width: 50%; padding: 20rpx;"
							v-if="item.business_name">
							<!-- 非选中 -->
							<view v-if="item.check!=1" @click="itemChange(item,1)"
								style="padding: 5rpx; border-radius: 16rpx;">
								<view style="display: flex;  align-items: center;">
									<text class="lg cuIcon-square" style="font-size: 40rpx;margin-right:10rpx ;"></text>
									<view style="font-weight: bold; font-size: 32rpx;">
										{{item.business_name}}
									</view>
								</view>
								<block v-if="item.child_list && item.child_list.length>0">
									<view style="display: flex; flex-wrap: wrap; font-size: 26rpx;">
										<view v-for="(childItem,index) in item.child_list " :key="index"
											style="margin-right: 5rpx;">
											{{childItem.business_name}}
										</view>
									</view>
								</block>
							</view>


							<!-- 选中 -->
							<view v-if="item.check==1" @click="itemChange(item,0)"
								style="padding: 20rpx; border-radius: 16rpx; background: #113d73; color:#FFFFFF ;">
								<view style="display: flex; align-items: center;">
									<text class="lg cuIcon-squarecheck"
										style="font-size: 40rpx;margin-right:10rpx ;"></text>
									<view style="font-weight: bold; font-size: 32rpx;">
										{{item.business_name}}
									</view>
								</view>
								<block v-if="item.child_list && item.child_list.length>0">
									<view style="display: flex; flex-wrap: wrap; font-size: 26rpx;">
										已选：
										<view v-if="childItem.check==1" v-for="(childItem,index) in item.child_list "
											:key="index" style="margin-right: 5rpx;">
											{{childItem.business_name}}
										</view>
									</view>
								</block>
							</view>
						</view>
					</view>
				</view>
				<view style="padding: 20rpx;">
					<view style="width: 100%;">
						<!-- 						<view class='text-bold' style="margin-bottom: 10rpx;font-size: 32rpx;">请填写手机号，机构负责人会尽快与你联络
						</view> -->
						<view class='text-bold' style="margin-bottom: 10rpx;font-size: 32rpx;"
							v-if="advInfo.formJson.formText">{{advInfo.formJson.formText}}
						</view>
					</view>
					<view class='text-bold' style="margin-bottom: 10rpx; color: red; font-size: 24rpx;">
						你的信息会严格受法律保护，仅被勾选的业务负责人会知晓。请勿恶意填写他人信息或无效信息，违者帐号将被处以封禁。</view>
					<view style="border-radius: 20rpx; background-color: #fff;">
						<view v-for="item in signInfo">
							<view v-if="item.kind==1" class="cu-form-group" style="padding: 1upx 0;">
								<!-- v-if="line.kind=0"> -->
								<view class="title" style="font-weight: bold;">{{item.title}}</view>
								<input :placeholder="item.placeholder" name="input" v-model="item.value"></input>
							</view>
							<view v-if="item.kind==2" class="cu-form-group" style="padding: 1upx 0;">
								<!-- v-if="line.kind==1" -->
								<view class="title" style="font-weight: bold;">{{item.title}}</view>
								<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
									<view class="picker">
										{{item.index>-1?item.value:item.placeholder}}
									</view>
								</picker>
							</view>
							<view v-if="item.kind==3" class="cu-form-group" style="padding: 1upx 0;">
								<!-- v-if="line.kind=0"> -->
								<view class="cuIcon-title text-red"></view>
								<view class="title" style="font-weight: bold;">{{item.title}}</view>
								<input :placeholder="item.placeholder" name="input" v-model="item.value"></input>
							</view>
							<view v-if="item.kind==4" class="cu-form-group" style="padding: 1upx 0;">
								<!-- v-if="line.kind==1" -->
								<view class="cuIcon-title text-red"></view>
								<view class="title" style="font-weight: bold;">{{item.title}}</view>
								<picker @change="PickerChange($event,item)" :value="item.index" :range="item.options">
									<view class="picker">
										{{item.index>-1?item.value:item.placeholder}}
									</view>
								</picker>
							</view>
							<view class="cu-form-group" v-if="item.kind==5" style="padding: 1upx 0;">
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
								v-if="isExistSignUp==0" style="width: 80%;  background-color: #113d73;">
								提交
							</button>
						</view>
					</view>
				</view>
			</view>
		</block>
		<view class="cu-modal show" v-if="modalName">
			<view class="cu-dialog" style="background-color: #fff;">
				<view class="modal-top">
					<view class="modal-top-text">
						<view v-if="currentBusiness.business_name">{{currentBusiness.business_name}}</view>
					</view>
				</view>
				<view class="modal-bottom">
					<view style="padding: 20rpx;">
						<view style="width: 100%;">
							<view style="margin-bottom: 10rpx;font-size: 24rpx; color: gray;" v-if="advInfo.formJson.detailText">
								<text > {{advInfo.formJson.detailText}}</text>
								<text style="color: red;">（可多选）</text>
							</view>
						</view>
						<view style="display: flex; flex-wrap: wrap; margin: 20rpx 0;">
							<view v-for="(childItem,index) in currentBusiness.child_list " :key="index"
								style="margin-right: 25rpx;" v-if="childItem.business_name">
								<button v-if="childItem.check!=1" class="cu-btn line-blue"
									style="border-color: #113d73; color: #113d73;"
									@click="childItemChange(childItem,1)">{{childItem.business_name}}</button>
								<button v-if="childItem.check==1" class="cu-btn bg-blue"
									style="background-color: #113d73;"
									@click="childItemChange(childItem,0)">{{childItem.business_name}}</button>
								<!-- {{childItem.business_name}} -->
							</view>
						</view>
						<view style="text-align: center; margin-top: 10rpx; position: relative;" 
							v-if="currentBusiness.child_list.some(item => item.image_url)">
							<!-- <view style="font-size: 30rpx; font-weight: bold; color:#113d73; margin-bottom: 20rpx; height: 50rpx;" v-if="currentBusiness.child_list.some(item => item.image_title)">
								<text v-if="currentImageTitle">{{currentImageTitle}}</text>
							</view> -->
							<view v-if="currentImageTitle" style="display: flex;align-items: center;justify-content: center; position: absolute; background-color: rgba(0, 0, 0, 0.5); 
							 width: 100%; z-index: 1000; font-size: 30rpx; font-weight: bold; color:#fff; height:70rpx">
								<text>{{currentImageTitle}}</text>
							</view>
							<u-swiper :list="currentBusiness.child_list" name="image_url" :height="600" :autoplay="true"
								:interval="swiperDuration" @change="swiperChange" @click="preSwiperImgae"></u-swiper>
						</view>
						<view style="text-align: center;">
							<button class="cu-btn line-blue "
								style="height: 50rpx; width: 200rpx; margin-top: 35rpx ; margin-right:40rpx; border-color: #113d73; color: #113d73;"
								@click="hideModal">
								取消</button>
							<button class="cu-btn bg-red "
								style="height: 50rpx; width: 200rpx; margin-top: 35rpx; margin-left:40rpx; background-color: #113d73;"
								@click="childItemSumbit">
								确定</button>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="share-part">
			<button class="cu-btn shareIndex  " @click="toFisrtPage"> 首页 </button>
			<button class="cu-btn  sharePerson " @click="openCustomerServiceChat"> 客服 </button>

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
		createMultiBusinessAdvSignUp
	} from "@/api/index.js"
	export default {
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['currentSchoolId']),
			...mapState('config', ['firstPage', 'judgeShowPopup']),
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
				businessList: [],
				currentBusiness: {},
				modalName: false,
				currentImageTitle: '',
				swiperDuration: 1000,
			}
		},
		async onLoad(options) {
			let that = this
			console.log(options)
			this.advId = options.id;
			//获取屏幕信息
			// this.WindowInfo = uni.getWindowInfo();
			await this.getOneAdv()
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
					data.data.formJson = JSON.parse(data.data.formJson)
					//处理报名表中的图片信息
					for (var i = 0; i < data.data.formJson.template.length; i++) {
						if (data.data.formJson.template[i].kind == 5) {
							data.data.formJson.template[i].fileList = []
							data.data.formJson.template[i].fileList2 = []
						}
					}
					this.signInfo = data.data.formJson.template
					this.businessList = JSON.parse(data.data.advInfoJson)
					this.advInfo = data.data
					if (this.advInfo.formJson.swiperDuration) {
						this.swiperDuration = this.advInfo.formJson.swiperDuration
					}
					console.log("触发广告获取", this.advInfo, this.signInfo, this.businessList)
				} else {
					console.log("广告请求失败");
				}

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
				//检测有无选择业务
				if (!this.businessList.some(item => item.check==1)) {
					uni.showToast({
						title: "请至少选择一个业务类型",
						icon: "none"
					})
					// console.log(this.businessList.some(item => item.check==1))
					return
				}
				//首先判断是否提交过
				let infoForm = {
					advId: this.advInfo.id,
					userId: this.userInfos.userId,
				}
				let data = await getAdvSignUp(infoForm);
				console.log(data)
				if (data.code == 200 && data.data) {
					uni.showToast({
						title: '您已参与过本活动，如有新的需要，可联系客服~',
						icon: "none",
						duration: 3000,
					})
					return
				}
				//没有提交过，提交
				let that = this
				let sumbitInfo = { //提交的报名信息
					advId: this.advInfo.id,
					userId: this.userInfos.userId,
					infoJson: {
						data: [],
					},
					advJson: {}

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
				sumbitInfo.advJson = this.businessList
				console.log(sumbitInfo)
				// let res = await addSignUpInfo(this.sumbitInfo)
				let res = await createMultiBusinessAdvSignUp(sumbitInfo)
				if (res.code == 200) {
					console.log(res)
					uni.showToast({
						title: '提交成功',
						icon: "none",
					})
					setTimeout(function() {
						// uni.redirectTo({
						// 	url: '/pages/more/adv?id=' + that.advInfo.id
						// });
						uni.switchTab({
							url: '/pages/index/index'
						});
					}, 500);

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
			},
			//一级业务选择
			itemChange(item, value) {
				if (value == 1 && item.child_list.length == 0) {
					this.$set(item, 'check', 1);
					console.log(item)
				}
				if (value == 1 && item.child_list.length > 0) {
					this.currentBusiness = item
					this.modalName = true
					this.currentImageTitle = item.child_list[0].image_title
					// this.$set(item, 'check', 1);
					console.log(item)
				}
				if (value == 0) {
					this.$set(item, 'check', 0);
					if (item.child_list && item.child_list.length > 0) {
						item.child_list.forEach(childItem => {
							this.$set(childItem, 'check', 0);
						});
					}
				}
			},
			//二级业务选择
			childItemChange(childItem, value) {
				if (value == 1) {
					this.$set(childItem, 'check', 1);
					console.log(childItem)
				}
				if (value == 0) {
					this.$set(childItem, 'check', 0);
				}
			},
			//二级业务确认
			childItemSumbit() {
				if (!this.currentBusiness.child_list.some(item => item.check==1)) {
					console.log(this.currentBusiness.child_list.some(item => item.check==1))
					uni.showToast({
						title: "请选择一项或多项",
						icon: "none"
					})
					return
				}
				this.$set(this.currentBusiness, 'check', 1);
				this.modalName = false;
			},
			//隐藏modal
			hideModal() {
				this.modalName = false;
			},
			//客服连接
			openCustomerServiceChat() {
				let that = this
				console.log(this.judgeShowPopup.extInfo)
				//默认客服链接
				let extInfoUrl = 'https://work.weixin.qq.com/kfid/kfcfe330dda5782a841'
				if (this.judgeShowPopup.extInfo && this.judgeShowPopup.extInfo != '') {
					extInfoUrl = this.judgeShowPopup.extInfo
				}
				wx.openCustomerServiceChat({
					extInfo: {
						url: extInfoUrl
					},
					corpId: 'ww45debfa9e67919a9',
					success(res) {
						console.log(res)
					},
					fail(err) {
						console.log(err)
					}
				})

			},
			swiperChange(e) {
				this.currentImageTitle = this.currentBusiness.child_list[e].image_title
				// console.log(this.currentImageTitle, !this.currentImageTitle)
			},
			preSwiperImgae(e) {
				uni.previewImage({
					current: this.currentBusiness.child_list[e].image_url, // 当前显示图片的http链接
					urls: [this.currentBusiness.child_list[e].image_url] // 需要预览的图片http链接列表
				});
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
		onShareTimeline() {
			let imgURL;
			const imageUrlArray = this.advInfo.imageUrls.split(',');
			if (imageUrlArray.length > 0) {
				imgURL = imageUrlArray[0];
			}
			return {
				title: this.advInfo.title,
				// query: 'id=' + this.currentContent.contentId
				query: "id="+ this.advInfo.id + "&currentSchoolId=" + this.advInfo.schoolId + "&advKind=multiBusiness",
				path: '/pages/index/index',
				imageUrl: imgURL
			};
		},
		onShareAppMessage(res) {
			console.log("触发")
			let imgURL;
			const imageUrlArray = this.advInfo.imageUrls.split(',');
			if (imageUrlArray.length > 0) {
				imgURL = imageUrlArray[0];
			}
			return {
				title: this.advInfo.title,
				path: '/pages/index/index?advId=' + this.advInfo.id + "&currentSchoolId=" + this.advInfo.schoolId + "&advKind=multiBusiness",
				imageUrl: imgURL
			};
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

	.modal-top {
		height: 60rpx;
		display: flex;
		// justify-content: flex-end;
		// padding-right: 20rpx;
		margin-bottom: 30rpx;
		background-color: #FFFFFF;
	}

	.modal-top-text {
		margin-top: 30rpx;
		display: flex;
		justify-content: center;
		width: 100%;
		font-size: 34rpx;
		font-weight: bold;
	}

	.modal-bottom {
		display: block;
		// margin-left: 15rpx;
		padding-bottom: 20rpx;
		text-align: left;
		background-color: #FFFFFF;
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
</style>