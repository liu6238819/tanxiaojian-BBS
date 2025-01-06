<template>
	<view>
		<!-- 通过canvas压缩图片 -->
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<view class="c-wrap">
			<u-form ref="uForm" label-width="auto">
				<u-form-item label="头像">
					<u-avatar @click="onAvatar" mode="square" slot="right" :src="userInfos.headimgUrl" size="100">
					</u-avatar>
				</u-form-item>
				<u-form-item label="昵称" right-icon="arrow-right">
					<u-input @click="jump(userInfos.nickName,'nickName')" :placeholder="userInfos.nickName"
						:disabled="true" input-align="right" />
				</u-form-item>

				<u-form-item label="个性签名" right-icon="arrow-right">
					<u-input @click="jump(userInfos.introduction,'introduction')" :placeholder="userInfos.introduction"
						:disabled="true" input-align="right" />
				</u-form-item>
			</u-form>
		</view>
		<view class="f-fixed">
			<!-- <q-button @click="outlogin">退出登录</q-button> -->
			<button @click="onTabLogOut" class="cu-btn "
				style=" width: 25%; height: 50rpx; text-align: center; background-color: #aaaaaa; color: #ffffff;">注销账号</button>
		</view>
		<view class="cu-modal show" v-if="showLogOut">
			<view class="cu-dialog ">
				<view class="cu-bar justify-end" style="color: white; background-color: #028fab;">
					<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;">
						账号注销
					</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-white text-bold"></text>
					</view>
				</view>
				<view class="modal-bottom " style="padding: 20rpx;background-color: #ffffff;">
					<view style="margin-bottom: 10rpx; text-align: left; font-size: 30rpx; color: #000000;
					   width: 100%; font-weight: bold;">账号注销须知：</view>
					<view style="text-align: left;">
						<view style="font-size: 28rpx;">1.注销将重置该账号，所有历史发帖、评论等个人数据将
						<text style="color: red;">永久删除，不可恢复；</text>
						</view>
						<view style="font-size: 28rpx;">2.该账号对应主体的违规封禁，不会因为注销而解除；</view>
						<view style="font-size: 28rpx; color: red;">3.您当前的微信号在成功注销的 180 天内，无法再次注销账号。</view>
					</view>
				</view>
				<view
					style="text-align: center ; display: block; padding-top:10rpx; padding-bottom: 30rpx;  background: #fff;">
					<view style="font-size: 24rpx; color: red; text-align: left; padding: 10rpx 20rpx;" >*请仔细阅读、谨慎操作，注销操作不可逆</view>
					<view style="display: flex; justify-content: space-around;">
						<button @click="hideModal" class="cu-btn round bg-blue text-bold "
							style=" width: 30%; background-color: #028fab;color: #ffffff; height: 45rpx;">取消</button>
						<button @click="subimtLogOut" class="cu-btn round bg-blue text-bold "
							style=" width: 30%; background-color: #aaaaaa;color: #ffffff; height: 45rpx;">注销账号</button>
					</view>
				</view>
			</view>
		</view>
		<!-- 性别选择 -->
		<!-- <q-select v-model="showGender" :list="gender" @confirm="saveGender"></q-select> -->
	</view>
</template>

<script>
	import {
		mapState,
		mapActions,
		mapMutations
	} from 'vuex'
	import {
		addOperationRecord,
		userLogOut
	} from "@/api/index.js"
	import localData from '@/utils/data.js';
	export default {
		data() {
			return {
				imgList: [],
				headimgUrl: "",
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
				oldHeadImgUrl: "",
				showLogOut: false,
			}
		},
		computed: {
			...mapState('user', ['userInfos', 'currentSchoolId']),
			...mapState('user', ['userKey']),
		},
		onShow() {
			//将旧版头像地址储存
			this.oldHeadImgUrl = JSON.parse(JSON.stringify(this.userInfos)).headimgUrl;
			// console.log("头像",this.oldHeadImgUrl)
		},
		methods: {
			...mapMutations('user', {
				clearUserInfos: 'clearUserInfos'
			}),
			...mapActions('user', {
				editUserInfos: 'editUserInfos'
			}),
			jump(value, type) {
				//submit?value="hq"&type=nickName
				console.log("submit?value=" + JSON.stringify(value) + "&type=" + type);
				// uni.navigateTo({
				// 	url: "submit?value=" + JSON.stringify(value) + "&type=" + type
				// })
				value = encodeURIComponent(value);
				uni.navigateTo({
					url: "submit?value=" + value + "&type=" + type
				})
			},
			outlogin() {
				// 清空本地缓存 
				uni.removeStorage({
					key: 'userInfos'
				})
				// 清空vuex中的用户信息
				this.clearUserInfos()
				// 标识用户已经退出登录了
				uni.setStorageSync('isLoginedOutData', {
					isLoginedOut: true
				})
				uni.reLaunch({
					url: '/pages/index/index',
				})
			},
			onAvatar() {
				this.imgList = []
				uni.chooseImage({
					count: 1, //默认9
					sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album'], //从相册选择
					success: async (res) => {
						if (this.imgList.length != 0) {
							this.imgList = this.imgList.concat(res.tempFilePaths)
						} else {
							this.imgList = res.tempFilePaths
						}
						uni.showLoading({
							mask: true,
							title: "上传头像中"
						})
						//原始图片压缩
						// const compressResult = await localData.compressionIamge(this, this.imgList[0]);
						//微信官方压缩方法(二次压缩)
						const compressResult = await localData.compressionIamgeWXTwice(this, this.imgList[0],
							200);
						console.log(compressResult)
						// 将头像上传到阿里云oss，返回新图像的url
						this.headimgUrl = await this.uploadImageOSS(compressResult,
							'user/headImg/', this.currentSchoolId)
						this.headimgUrl = this.headimgUrl.replace('keming-bbs.oss-cn-shanghai.aliyuncs.com',
							'image.tanxiaojian.zone')
						//对图片进行审核
						let imgCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey.openId,
							this.headimgUrl, 2, 1);
						if (imgCheck !== 87014) {
							// 用新的图像url到vuex中发送更新头像的请求
							this.editUserInfos({
								userId: this.userInfos.userId,
								headimgUrl: this.headimgUrl
							})
							this.createOperationRecord(1, {
								userId: this.userInfos.userId,
								headimgUrl: this.headimgUrl
							})
						} else {
							this.imgList = []
							uni.hideLoading()
							uni.showToast({
								mask: true,
								icon: 'none',
								title: '图片可能潜在风险！'
							});
						}
						uni.hideLoading()
					}
				})
			},
			async createOperationRecord(targetKind, changeForm) {
				console.log("触发")
				let operatinForm = {
					schoolId: this.currentSchoolId,
					recordState: 1,
					operator: this.userInfos.userId,
					targetKind: targetKind,
					targetId: this.userInfos.userId,
					changeInfo: null,
					note: null,
				}
				//操作对象为用户
				operatinForm.changeInfo = {
					data: {
						oldForm: {
							headimgUrl: this.oldHeadImgUrl
						},
						newForm: {
							headimgUrl: changeForm.headimgUrl
						}
					}
				};
				//添加备注
				operatinForm.note = "用户修改头像"
				operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				console.log(operatinForm)
				const data = await addOperationRecord(operatinForm)
				if (data.code == 200) {
					console.log(data.data)
				}

			},
			//用户注销账号
			onTabLogOut() {
				this.showLogOut = true
			},
			hideModal() {
				this.showLogOut = false
			},
			async subimtLogOut() {
				uni.showLoading({
					title: "处理中...",
					mask: true,
				})
				let form = {
					userId: this.userInfos.userId,
					headImgUrl: localData.newHeadimgUrl,
					nickName: localData.newNickName,
					schoolId: this.currentSchoolId
				}
				const data = await userLogOut(form)
				if (data.code = 200) {
					if (data.data == 0) {
						uni.hideLoading()
						uni.showToast({
							icon: 'none',
							title: '账号注销失败，请联系客服'
						})
					}
					if (data.data == 2) {
						uni.hideLoading()
						uni.showToast({
							icon: 'none',
							title: '180天内注销过账号，无法频繁注销',
							duration: 1500
						})
					}
					if (data.data == 1) {
						uni.hideLoading()
						uni.showToast({
							icon: 'none',
							title: '账号注销成功',
							duration: 1500
						})
						let loginResult = await this.loginWX()
						uni.switchTab({
							url:'/pages/mine/mine'
						})
					}
					this.showLogOut = false
				}
			},

		}
	}
</script>
<style scoped>
	.c-wrap {
		padding: 20rpx;
		background-color: #FFFFFF;
	}

	.bind-mobile {
		display: flex;
	}

	/* 标签 */
	.tag-box {}

	.tag-box .tag {
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		display: inline-block;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		background-color: #ffebe5;
	}

	.tag-box .tag:nth-child(2n) {
		background-color: #ecf9f2;
	}

	.tag-box .tag:nth-child(3n) {
		background-color: #fff7e5;
	}

	.tag-box .tag:nth-child(5n) {
		background-color: #b3e0ff;
	}
	
	.f-fixed {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		padding: 20rpx 20rpx 10rpx 20rpx;
		z-index: 999;
		background-color: #fff;
		text-align: center;
	}
</style>