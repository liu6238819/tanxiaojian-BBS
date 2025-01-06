<template>
	<view>
		<user-list :list="followList" :loadStatus="loadStatus" @loadmore='getMore'></user-list>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex'
	import {reqFollowedUsers} from '@/api/index.js'
	
	import userList from '@/components/user-list/user-list.vue';
	export default {
		components: {
			userList,
			page: 1
		},
		data() {
			return {
				followList: [],
				followTotalNum: 0,
				form: {
					userId: null,
					type:1,//获取我关注的用户
					pageNum: 1,
					pageSize: 5
				},
				loadStatus: "loadmore"
			};
		},
		computed: {
			...mapState('user', ['followData','userInfos'])
		},
		onLoad() {
			// 获取vuex中存储的第一页的数据
			const {
				followList,
				followTotalNum
			} = this.followData
			this.followList = followList
			for (var i = 0; i < this.followList.length; i++) {
				this.followList[i].isFollow =1
			}
			this.followTotalNum = followTotalNum
			this.form.userId = uni.getStorageSync('userInfos').userId
			if (followTotalNum > followList.length) {
				this.loadStatus = "loadmore"
			} else {
				this.loadStatus = 'nomore'
			}
			console.log('onload', this.followList, this.followTotalNum);
		},
		onReachBottom() {
			console.log('onReachBottom');
			this.getMore()
		},
		methods: {
			getMore() {
				this.form.pageNum = this.form.pageNum + 1
				this.getFollowList()
				console.log('getMore');
			},
			async getFollowList() {
				const followData = await reqFollowedUsers(this.form)
				if(followData.code ===200){
					const {records} = followData.data
					this.followList = this.followList.concat(records)
					if(this.followTotalNum >= this.followList.length){
						this.loadStatus = 'nomore'
					}
					console.log('this.followList',this.followList);
				}
				console.log('followData',followData);
			}
		}
	}
</script>

<style lang="scss">

</style>
