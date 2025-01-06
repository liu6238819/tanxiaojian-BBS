<template>
	<view class="maintenance-container">
		<text class="message">系统维护中，请稍后再试。</text>
	</view>
</template>

<script>
	export default {
		data() {
			return {};
		}
	};
</script>

<style>
	.maintenance-container {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 100vh;
		background-color: #f2f2f2;
	}

	.message {
		font-size: 18px;
		color: #333;
		text-align: center;
	}
</style>