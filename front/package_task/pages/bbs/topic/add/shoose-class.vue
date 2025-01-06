<template>
	<view class="container">
		<view class="title">选择话题类目</view>
		<view class="class-wrap">
			<view class="class-item u-line-1" @click="chooseClass(item.cateId,item.cateName)"
				v-for="(item, index) in classList" :key="index">{{ item.cateName }}</view>
		</view>
	</view>
</template>

<script>
	import {
		getClassLists,
		getPlateLists
	} from '@/api/index.js'
	export default {
		data() {
			return {
				classList: []
			};
		},
		async created() {
			//this.getCatList();
			this.classList = await getClassLists();
		},
		methods: {
			// getCatList() {
			// 	this.$H.get('topic/classList').then(res => {
			// 		this.classList = res.result;
			// 	});
			// },
			chooseClass(id, name) {
				let pages = getCurrentPages(); //获取所有页面栈实例列表
				let nowPage = pages[pages.length - 1]; //当前页页面实例
				let prevPage = pages[pages.length - 2]; //上一页页面实例
				prevPage.$vm.form.cateId = id;
				prevPage.$vm.cateName = name;
				uni.navigateBack();
			}
		}
	};
</script>

<style lang="scss" scoped>
	.title {
		margin-bottom: 30rpx;
	}

	.class-wrap {
		.class-item {
			width: 30%;
			display: inline-block;
			border: 1px solid #999;
			padding: 20rpx;
			font-size: 24rpx;
			color: #999;
			text-align: center;
			margin-bottom: 20rpx;
			border-radius: 10rpx;

			&:nth-child(3n + 2) {
				margin-left: 5%;
				margin-right: 5%;
			}

			&:active {
				background-color: #333;
				color: #fff;
			}
		}
	}
</style>
