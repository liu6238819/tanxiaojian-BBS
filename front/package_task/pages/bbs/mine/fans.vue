<template>
	<view>
		<user-list :list="fansList" :loadStatus="loadStatus" @loadmore='getMore'></user-list>
	</view>
</template>

<script>
	import {reqFollowedUsers} from '@/api/index.js'
	import {mapState} from 'vuex'
	import userList from '@/components/user-list/user-list.vue';
	export default {
		components: {
			userList,
		},
		data() {
			return {
				form: {
					userId: null,
					type:0,//关注我的用户
					pageNum: 1,
					pageSize: 5
				},
				fansList: [],
				fansTotalNum:0,
				loadStatus: "loadmore"
			};
		},
		onLoad() {
			// 获取vuex中存储的第一页的数据
			const {
				fansList,
				fansTotalNum
			} = this.followData
			this.fansList = fansList
			this.fansTotalNum = fansTotalNum
			this.form.userId = uni.getStorageSync('userInfos').userId
			if (fansTotalNum > fansList.length) {
				this.loadStatus = "loadmore"
			} else {
				this.loadStatus = 'nomore'
			}
			console.log('onload', this.fansList, this.fansTotalNum);
		},
		computed:{
			...mapState('user',['followData'])
		},
		onReachBottom() {
			this.page++;
			this.getUserList();
		},
		methods: {
			getMore() {
				this.form.pageNum = this.form.pageNum + 1
				this.getFansList()
				console.log('getMore');
			},
			async getFansList() {
				const followData = await reqFollowedUsers(this.form)
				if(followData.code ===200){
					const {records} = followData.data
					this.fansList = this.fansList.concat(records)
					if(this.fansTotalNum >= this.fansList.length){
						this.loadStatus = 'nomore'
					}
					console.log('this.followList',this.fansList);
				}
				console.log('followData',followData);
			}
		}
	}
</script>

<style lang="scss">
	
</style>
