<template>
	<view>
		<view @click="backToTop" class="back-top">
			<q-icon name="q-direction-up" color="#fff"></q-icon>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			};
		},
		methods: {
			backToTop() {
				uni.pageScrollTo({
					scrollTop: 0,
					duration: this.duration
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
.back-top{
	position: fixed;
	right: 20rpx;
	bottom: 180rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background-color: $themes-color;
	box-shadow: 0 0 20rpx #999;
	display: flex;
	justify-content: center;
	align-items: center;
}
</style>
