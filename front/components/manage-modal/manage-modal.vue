<template>
	<view class="cu-modal show">
		<view class="cu-dialog " style="height: 80vh; overflow: auto;">
			<view class="cu-bar justify-end" style="color: white; background-color: #028fab;">
				<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;" v-if="manageKind==1">
					帖子管理
				</view>
				<view class="content text-white text-bold" style="width: 100%;font-size: 36rpx;" v-if="manageKind==2">
					评论管理
				</view>
				<view class="action" @tap="hideModal">
					<text class="cuIcon-close text-white text-bold"></text>
				</view>
			</view>
			<!-- 帖子管理 -->
			<view class="modal-bottom ">
				<block v-if="manageKind==1">
					<view style="font-weight: bold; margin: 20rpx 0; display: flex; align-items: center; justify-content: center;">
						<view>修改帖子状态</view>
					</view>

					<u-form :model="contentForm">
						<view class="select-item">
							<view style="font-size: 28rpx;"> 贴子状态： </view>
							<w-select style="margin-left: 20rpx;" v-model="contentForm.contentState"
								:list="contentStateOptions" valueName="label" keyName="value" width="100%"
								height="50rpx">
							</w-select>
						</view>
						<u-form-item label="阅读人数:" label-width="200">
							<u-input v-model="contentForm.realReadNum" placeholder="请输入阅读人数" type="number"
								border="true" />
						</u-form-item>
						<u-form-item
							v-if="contentForm.contentState!=contentOldForm.contentState || contentForm.visibleRange !=contentOldForm.visibleRange || contentForm.noComment !=contentOldForm.noComment || contentForm.alumniOnly !=contentOldForm.alumniOnly"
							label="操作记录备注:" label-width="200">
							<u-input v-model="operatinContentForm.note" placeholder="请输入操作记录备注" border="true" />
						</u-form-item>
					</u-form>
				</block>

				<block v-if="manageKind==2">
					<view style="font-weight: bold; margin: 20rpx 0;"> 修改评论状态</view>
					<u-form :model="contentForm">
						<view class="select-item">
							<view style="font-size: 28rpx;"> 评论状态： </view>
							<w-select style="margin-left: 20rpx;" v-model="commentForm.commentState"
								:list="commentStateOptions" valueName="label" keyName="value" width="100%"
								height="50rpx">
							</w-select>
						</view>
						<u-form-item
							v-if="contentForm.contentState!=contentOldForm.contentState || contentForm.visibleRange !=contentOldForm.visibleRange || contentForm.noComment !=contentOldForm.noComment || contentForm.alumniOnly !=contentOldForm.alumniOnly"
							label="操作记录备注:" label-width="200">
							<u-input v-model="operatinContentForm.note" placeholder="请输入操作记录备注" border="true" />
						</u-form-item>
					</u-form>
				</block>

				<view style="font-weight: bold; margin: 20rpx 0;"> 修改用户状态</view>
				<u-form :model="userForm">
					<view class="select-item">
						<view style="font-size: 28rpx;"> 用户类型： </view>
						<w-select style="margin-left: 20rpx;" v-model="userForm.userType" :list="userTypeOptions"
							valueName="label" keyName="value" width="100%" height="50rpx">
						</w-select>
					</view>
					<u-form-item v-if="userForm.userType!=userOldForm.userType && userForm.userType==2" label="禁言时长:"
						label-width="200">
						<u-input v-model="operatinUserForm.bannedTime" type="number" placeholder="请输入禁言时间"
							border="true" />
					</u-form-item>
					<u-form-item
						v-if="userForm.userType!=userOldForm.userType || userForm.userState!=userOldForm.userState"
						label="操作记录备注:" label-width="200">
						<u-input v-model="operatinUserForm.note" placeholder="请输入操作记录备注" border="true" />
					</u-form-item>
				</u-form>

				<view style="margin: 30rpx 0;">
					<u-button type="primary" size="default" @click="onSubmit()">提交</u-button>
				</view>

				<view style="font-weight: bold; margin: 10rpx 0;"> 禁言记录</view>
				<u-table v-if="userBannedList && userBannedList.length>0">
					<u-tr>
						<u-th>备注</u-th>
						<u-th>禁言开始时间</u-th>
						<u-th>禁言结束时间</u-th>
					</u-tr>
					<view v-for="(item,index) in userBannedList" :key="index">
						<u-tr>
							<u-td width="30%">{{item.note}}</u-td>
							<u-td width="35%">{{formatDateTime(item.operateTime)}}</u-td>
							<u-td width="35%">{{formatDateTime(item.bannedTime)}}</u-td>
						</u-tr>
					</view>
				</u-table>


			</view>
			<!-- 帖子管理 -->

		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		getUserStateBySchool,
		manageUserInfosApi,
		addOperationRecord,
		manageContentInfo,
		manageCommentState,
		selectBannedList,
		addOperationRecord0619,
		getContentById,
		manageGetCommentById,
	} from '@/api/index.js'
	export default {
		name: 'manage-modal',
		props: {
			manageKind: String,
			manageUserId: String, //该帖子的发帖用户ID
			manageContent: Object, //该帖子
			manageComment: Object, //该评论
		},
		data() {
			return {
				// privacyContractName: '',
				//用户
				userForm: {
					userId: null,
					schoolId: null,
					userType: null,
					userState: null
				},
				userTypeOptions: [{
						value: 0,
						label: '普通用户'
					},
					{
						value: 1,
						label: '预警用户'
					},
					{
						value: 2,
						label: '禁言用户'
					}
				],
				userStateOptions: [{
						value: 0,
						label: '访客'
					},
					{
						value: 1,
						label: '审核中'
					},
					{
						value: 2,
						label: '审核通过'
					}
				],
				userOldForm: {},
				operatinUserForm: {
					schoolId: null,
					recordState: 1,
					operator: null,
					targetKind: null,
					targetId: null,
					changeInfo: null,
					note: null,
					bannedTime: null,
				},
				//帖子
				contentForm: {
					contentState: null,
					visibleRange: null,
					alumniOnly: null,
					noComment: null
				},
				contentStateOptions: [{
						value: 0,
						label: '正常'
					},
					{
						value: 6,
						label: '私密'
					},
					{
						value: 7,
						label: '待删除'
					},
					{
						value: 8,
						label: '官方通告'
					},
					{
						value: 12,
						label: '冻结'
					},
					{
						value: 13,
						label: '审核违规'
					}
				],
				visibleRangeOptions: [{
						value: 0,
						label: '校内'
					},
					{
						value: 1,
						label: '板块内'
					},
					{
						value: 2,
						label: '校内外'
					}
				],
				trueOrFalseOptions: [{
						value: 0,
						label: '否'
					},
					{
						value: 1,
						label: '是'
					}
				],
				contentOldForm: {},
				operatinContentForm: {
					schoolId: null,
					recordState: 1,
					operator: null,
					targetKind: null,
					targetId: null,
					changeInfo: null,
					note: null,
				},
				//评论
				commentForm: {
					commentState: null,
				},
				commentStateOptions: [{
						value: 0,
						label: '正常'
					},
					{
						value: 4,
						label: '已下架'
					},
					{
						value: 6,
						label: '冻结'
					}
				],
				commentOldForm: {},
				operatinCommentForm: {
					schoolId: null,
					recordState: 1,
					operator: null,
					targetKind: null,
					targetId: null,
					changeInfo: null,
					note: null,
				},
				userBannedList: []
			};
		},
		computed: {
			...mapState('user', ['currentSchoolId', 'userInfos']),
		},
		created() {
			// console.log('管理子组件', this.manageKind, this.manageUserId, this.manageContent, this.manageComment);
			this.getUserState(this.manageUserId)
			this.getUserBannedList()
			if (this.manageKind == 1) {
				// this.contentForm = this.manageContent
				// this.contentOldForm = JSON.parse(JSON.stringify(this.manageContent));
				this.getContentInfo()
			}
			if (this.manageKind == 2) {
				// this.commentForm = this.manageComment
				// this.commentOldForm = JSON.parse(JSON.stringify(this.manageComment));
				this.getCommentInfo()
			}
		},
		methods: {
			confirmUserType(e) {
				// this.userForm.userType = e[0].value
				console.log('类型', this.userForm.userType, this.userOldForm.userType)
			},
			confirmUserState(e) {
				// this.userForm.userState = e[0].value
				console.log('状态', this.userForm.userState, this.userOldForm.userState)
			},
			//关闭弹窗
			hideModal(index) {
				this.$emit("returnHideManage", index) //传递的值
			},
			async getUserState(userId) {
				const params = {
					userId: userId,
					schoolId: this.currentSchoolId
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					console.log("用户状态", stateData.data)
					this.userForm.userState = stateData.data.userState
					// const selectedStateOption = this.userStateOptions.find(option => option.value === stateData.data
					// 	.userState);
					// this.userStateLabel = selectedStateOption ? selectedStateOption.label : '';
					this.userForm.userType = stateData.data.userType
					// const selectedTypeOption = this.userTypeOptions.find(option => option.value === stateData.data
					// 	.userType);
					// this.userTypeLabel = selectedTypeOption ? selectedTypeOption.label : '';
					//将此时的form复制一份，存起来
					this.userOldForm = JSON.parse(JSON.stringify(stateData.data));
					console.log(this.userOldForm)
					this.operatinUserForm.note = null;

				}
			},
			async getContentInfo() {
				const params = {
					userId: this.manageContent.userId,
					contentId: this.manageContent.contentId
				}
				const stateData = await getContentById(params)
				if (stateData && stateData.code === 200) {
					console.log("帖子状态", stateData.data)
					this.contentForm = stateData.data
					//将此时的form复制一份，存起来
					this.contentOldForm = JSON.parse(JSON.stringify(stateData.data));
					this.operatinContentForm.note = null;
				}
			},
			async getCommentInfo() {
				const params = {
					commentId: this.manageComment.commentId
				}
				const stateData = await manageGetCommentById(params)
				if (stateData && stateData.code === 200) {
					console.log("评论状态", stateData.data)
					this.commentForm = stateData.data
					//将此时的form复制一份，存起来
					this.commentOldForm = JSON.parse(JSON.stringify(stateData.data));
					this.operatinCommentForm.note = null;
				}
			},
			onSubmit() {
				//帖子端口进入
				if (this.manageKind == 1) {
					this.updateUserInfo()
					this.updateContentInfo()
				}
				if (this.manageKind == 2) {
					this.updateUserInfo()
					this.updateCommentInfo()
				}
			},
			async updateUserInfo() {
				this.userForm.userId = this.manageUserId
				this.userForm.schoolId = this.currentSchoolId
				console.log(this.userForm)
				const data = await manageUserInfosApi(this.userForm)
				if (data.code = 200) {
					uni.showToast({
						icon: 'none',
						title: '用户信息修改成功'
					})
					this.createOperationRecord(1, this.operatinUserForm)
				}
			},
			async updateContentInfo() {
				const data = await manageContentInfo(this.contentForm)
				if (data.code = 200) {
					uni.showToast({
						icon: 'none',
						title: '帖子信息修改成功'
					})
					this.createOperationRecord(2, this.operatinContentForm)
				}
			},
			async updateCommentInfo() {
				let changeForm = {
					userId: this.manageComment.userId,
					commentId: this.manageComment.commentId,
					commentState: this.commentForm.commentState,
				}
				const data = await manageCommentState(changeForm);
				if (data.code = 200) {
					uni.showToast({
						icon: 'none',
						title: '评论信息修改成功'
					})
					this.createOperationRecord(3, this.operatinCommentForm)
				}
			},
			async createOperationRecord(targetKind, operatinForm) {
				console.log("触发")
				operatinForm.schoolId = this.currentSchoolId;
				operatinForm.operator = this.userInfos.userId
				operatinForm.targetKind = targetKind;
				operatinForm.changeInfo = {
					data: {
						oldForm: {},
						newForm: {}
					}
				};
				//操作对象为用户
				if (targetKind == 1) {
					// console.log("哈哈哈",this.operatinUserForm)
					operatinForm.targetId = this.manageUserId
					let haveChange = 0
					if (this.userForm.userType != this.userOldForm.userType) {
						haveChange = 1
						operatinForm.changeInfo.data.oldForm["userType"] = this.userOldForm["userType"]
						operatinForm.changeInfo.data.newForm["userType"] = this.userForm["userType"]
					}
					if (this.userForm.userState != this.userOldForm.userState) {
						haveChange = 1
						operatinForm.changeInfo.data.oldForm["userState"] = this.userOldForm["userState"]
						operatinForm.changeInfo.data.newForm["userState"] = this.userForm["userState"]
					}
					if (haveChange == 0) {
						console.log("无变化")
						this.hideModal()
						return
					}
					if (operatinForm.bannedTime) {
						operatinForm.note = operatinForm.note + " 禁言" + operatinForm.bannedTime * 1 +
							"天"
						const bannedtime = new Date(new Date().getTime() + operatinForm.bannedTime * 24 * 60 *
							60 * 1000)
						operatinForm.bannedTime = this.formatDateTime(bannedtime)
						console.log(operatinForm.bannedTime)
					}
					if (!operatinForm.bannedTime && this.userForm.userType == 2) {
						operatinForm.bannedTime = null
						operatinForm.note = operatinForm.note + " 永久禁言"
					}
					operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				}
				//操作对象为帖子
				if (targetKind == 2) {
					// console.log("哈哈哈",this.operatinUserForm)
					operatinForm.targetId = this.manageContent.contentId;
					let haveChange = this.isFormChanged(operatinForm, this.contentForm, this.contentOldForm)
					if (haveChange == 0) {
						console.log("无变化")
						this.hideModal()
						return
					}
					if (operatinForm.note) {
						operatinForm.note = "GM修改帖子状态 " + operatinForm.note
					} else {
						operatinForm.note = "GM修改帖子状态"
					}
					operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				}
				//操作对象为评论
				if (targetKind == 3) {
					operatinForm.targetId = this.manageComment.commentId;
					let haveChange = this.isFormChanged(operatinForm, this.commentForm, this.commentOldForm)
					if (haveChange == 0) {
						console.log("无变化")
						this.hideModal()
						return
					}
					if (operatinForm.note) {
						operatinForm.note = "GM修改评论状态 " + operatinForm.note
					} else {
						operatinForm.note = "GM修改评论状态"
					}
					operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				}
				console.log(operatinForm)
				const data = await addOperationRecord0619(operatinForm)
				if (data.code == 200) {
					console.log(data.data)
					this.hideModal()
				}

			},
			formatDateTime(date) {
				if (date == null) {
					return '';
				}
				//date是传入的时间
				let d = new Date(date);
				let month = (d.getMonth() + 1) < 10 ? '0' + (d.getMonth() + 1) : (d.getMonth() + 1);
				let day = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
				let hours = d.getHours() < 10 ? '0' + d.getHours() : d.getHours();
				let min = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
				let sec = d.getSeconds() < 10 ? '0' + d.getSeconds() : d.getSeconds();
				let times = d.getFullYear() + '-' + month + '-' + day + ' ' + hours + ':' + min + ":" + sec;
				return times;
			},
			//判断属性值是否发生了变化
			isFormChanged(operatinForm, newInfo, oldInfo) {
				//获取变更了属性值
				let haveChange = 0;
				for (let key in newInfo) {
					let value = newInfo[key];
					// 在这里对属性进行处理
					if (typeof newInfo[key] != 'object' && newInfo[key] != oldInfo[key]) {
						haveChange = 1
						operatinForm.changeInfo.data.oldForm[key] = oldInfo[key]
						operatinForm.changeInfo.data.newForm[key] = newInfo[key]
					}
					if (operatinForm.targetKind == 3 && key == 'contentId') {
						operatinForm.changeInfo.data.oldForm[key] = oldInfo[key]
						operatinForm.changeInfo.data.newForm[key] = newInfo[key]
					}
				}
				return haveChange;
			},
			async getUserBannedList() {
				const params = {
					targetId: this.manageUserId,
					schoolId: this.currentSchoolId
				}
				const data = await selectBannedList(params)
				if (data && data.code === 200) {
					console.log(data.data)
					this.userBannedList = data.data
				}
			},
		}
	};
</script>

<style lang="scss" scoped>
	.modal-bottom {
		width: 100%;
		display: flex;
		flex-direction: column;
		// margin-left: 15rpx;
		padding-bottom: 20rpx;
		padding-left: 40rpx;
		padding-right: 40rpx;
		background-color: #FFFFFF;
	}

	.select-item {
		display: flex;
		align-items: center;
		margin: 10rpx 0;
		justify-content: space-between;
	}
</style>