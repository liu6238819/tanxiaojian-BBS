<template>
	<view>
		<block v-for="(item,index) in list" :key="index">
			<navigator :url="'/package_task/pages/bbs/topic/detail?id='+item.plateId">
				<view class="topic-item">
					<u-image class="cover-img" width="120rpx" height="120rpx" border-radius="10rpx" :src="item.backImgUrl"></u-image>
					<view class="center" :style="{textAlign:'left'}">
						<view>{{item.name}}</view>
						<view class="desc">{{item.introduction|replace}}</view>
					</view>
					<view class="count-box">
						<text>{{ item.upNum }}人已加入</text>
						<text>{{ item.contentNum }}篇内容</text>
					</view>
				</view>
			</navigator>
		</block>
		<!-- 加载状态 -->
		<block v-if="loadStatus != 'none'">
			<block v-if="list.length > 0">
				<view style="margin: 30rpx;">
					<u-loadmore :status="loadStatus" @loadmore='loadMore()' bgColor="#f5f5f5" />
				</view>
			</block>
			<block v-else>
				<u-empty margin-top="100" text="暂无相关话题" mode="favor"></u-empty>
			</block>
		</block>
	</view>
</template>

<script>
	export default {
		props: {
			list: Array,
			loadStatus: String
		},
		data() {
			return {

			};
		},
		methods:{
			loadMore(){
				if(this.loadStatus == 'loadmore') this.$emit('loadmore');
			}
		},
		filters: {
			replace(str) {
				str = str.replace(/\n/g, '');
				return str.substring(0,40);
			}
		},
	}
</script>

<style lang="scss" scoped>
	.topic-item {
		display: flex;
		align-items: center;
		background-color: #fff;
		padding: 30rpx;
		border-bottom: 1px solid #f5f5f5;
		.cover-img{
			margin-right: 20rpx;
		}
		.center{
			flex: 1;
			.desc{
				font-size: 20rpx;
				color: #999;
			}
		}
		.count-box{
			display: flex;
			flex-direction: column;
			font-size: 20rpx;
			color: #999;
			margin-left: 20rpx;
		}
	}
</style>
