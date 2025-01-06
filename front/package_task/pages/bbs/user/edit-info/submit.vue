<template>
	<view class="container">
		<block v-if="type == 'tag_str'">
			<view class="f-wrap">
				<view class="title">已选择标签（点击可删除）</view>
				<view @click="onTagDel(index)" v-for="(item,index) in value" :key="index" class="tag">{{item}}</view>
			</view>
			<view class="f-wrap">
				<view class="title">自定义标签</view>
				<view class="tag-add" @click="showAddTag = true">
					<u-icon color="#999" size="50" name="plus"></u-icon>
				</view>
				<!-- 添加弹窗 -->
				<u-popup v-model="showAddTag" mode="center" border-radius="20" width="80%">
					<view class="popup-wrap">
						<view class="title">自定义标签</view>
						<input v-model="addValue" class="tag-input" type="text" placeholder="最长4个字" />
					</view>
					<view class="btn-wrap">
						<view @click="onCancel" class="btn-cancel">取消</view>
						<view @click="onConfirm" class="btn-confirm">保存</view>
					</view>
				</u-popup>
			</view>
			<view class="f-wrap">
				<view class="title">标签</view>
				<view @click="onTagAdd(item)" v-for="(item,index) in tagList" :key="index" class="tag">{{item}}</view>
			</view>
		</block>
		<!-- 修改用户信息 -->
		<block v-else>
			<input v-if="type=='nickName'" v-model="value" class="input" type="text" />
			<textarea v-if="type=='introduction'" v-model="value" class="input" :auto-height="true" maxlength="1500"
				style="min-height: 500rpx;" />
		</block>
		<view style="margin-top: 50rpx;">
			<q-button @click="submit">保存</q-button>
		</view>
	</view>
</template>

<script>
	import {
		mapActions,
		mapState
	} from 'vuex'
	import {
		addOperationRecord
	} from "@/api/index.js"
	export default {
		data() {
			return {
				type: "",
				value: "",

				showAddTag: false,
				tagList: ["圈子达人", "旅行", "热爱生活", "摩羯座", "摄影", "读书", "运动", "看电影", "听歌"],
				addValue: "",
				oldUserInfo: {},
			}
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
		},
		onLoad(options) {
			this.type = options.type;
			console.log('options', options);
			if (options.value) {
				// this.value = JSON.parse(options.value);
				this.value = decodeURIComponent(options.value);
			}
			this.oldUserInfo = JSON.parse(JSON.stringify(this.userInfos))
		},
		methods: {
			...mapActions('user', {
				editUserInfos: 'editUserInfos'
			}),
			async submit() {
				console.log(this.value, this.userInfos.userId);
				console.log({
					userId: this.userInfos.userId,
					[this.type]: this.value
				});
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId, this.value, 1);
				if (textCheck != 0) {
					//审核未通过的处理方法
					//this.value = ''
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
				}
				// 传递给vuex在actions中发送修改用户信息的请求
				this.editUserInfos({
					userId: this.userInfos.userId,
					[this.type]: this.value
				})
				this.createOperationRecord(1, {
					userId: this.userInfos.userId,
					[this.type]: this.value
				})
				uni.showToast({
					title: '修改成功',
					icon: "none"
				});
				setTimeout(function() {
					uni.navigateBack()
				}, 500)

			},
			onCancel() {
				this.showAddTag = false;
				this.addValue = "";
			},
			onConfirm() {
				if (!this.addValue) {
					this.$u.toast('自定义标签不能为空');
					return;
				}

				if (this.addValue.length > 4) {
					this.$u.toast('不能超过4个字');
					return;
				}

				let str = this.value.toString()
				if (str.includes(this.addValue)) {
					this.$u.toast('已存在相同标签');
					return;
				}

				this.value.push(this.addValue)
				this.showAddTag = false;
				this.addValue = "";
			},

			onTagDel(index) {
				this.value.splice(index, 1)
			},
			onTagAdd(name) {

				let str = this.value.toString()
				if (str.includes(name)) {
					this.$u.toast('已存在相同标签');
					return;
				}

				this.value.push(name)
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
				let oldInfo = ''
				for (let key in this.oldUserInfo) {
					let value = this.oldUserInfo[key];
					// 在这里对属性进行处理
					if (key == this.type) {
						oldInfo = value
						break
					}
				}
				//操作对象为用户
				operatinForm.changeInfo = {
					data: {
						oldForm: {
							[this.type]: oldInfo
						},
						newForm: {
							[this.type]: this.value
						}
					}
				};
				//添加备注
				if (this.type == 'introduction') {
					operatinForm.note = "用户修改个人简介"
				}
				if (this.type == 'nickName') {
					operatinForm.note = "用户修改昵称"
				}
				operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				console.log(operatinForm)
				const data = await addOperationRecord(operatinForm)
				if (data.code == 200) {
					console.log(data.data)
				}

			},
		}
	}
</script>

<style>
	page {
		background-color: #F5F5F5;
	}

	.popup-wrap {
		padding: 50rpx 30rpx;
	}

	.popup-wrap .tag-input {
		background-color: #F5F5F5;
		margin-bottom: 30rpx;
		padding: 20rpx;
	}

	.popup-wrap>.title {
		text-align: center;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	.btn-wrap {
		display: flex;
	}

	.btn-wrap .btn-cancel {
		width: 50%;
		padding: 20rpx 0;
		text-align: center;
		background-color: #F5F5F5;
	}

	.btn-wrap .btn-confirm {
		background-color: #8687fd;
		width: 50%;
		padding: 20rpx 0;
		text-align: center;
		color: #FFFFFF;
	}

	.input {
		border-bottom: 1px solid #e2e2e2;
		/* padding: 10rpx; */
	}

	.tag-add {
		padding: 20rpx;
		background-color: #F5F5F5;
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	/* 标签 */

	.tag {
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		display: inline-block;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		background-color: #ffebe5;
	}

	.tag:nth-child(2n) {
		background-color: #ecf9f2;
	}

	.tag:nth-child(3n) {
		background-color: #fff7e5;
	}

	.tag:nth-child(5n) {
		background-color: #b3e0ff;
	}
</style>
