<template>
	<view>
		<view @click="onMask" v-show="value" class="mask"></view>
		<view v-show="value" :style="{ height: height  }" class="popup-wrap"><slot></slot></view>
	</view>
</template>

<script>
/**
 * q-popup 弹窗
 * @description 下往上滑弹窗
 * @property {String} height 弹出高度
 * @event {Function} confirm 点击确定按钮，返回当前选择的值
 * @example <u-select v-model="show" :list="list"></u-select>
 */
export default {
	name: 'q-popup',
	props: {
		value: {
			type: Boolean,
			default: false
		},
		height: {
			type: String,
			default: 'auto'
		}
	},
	data() {
		return {};
	},
	methods: {
		onMask() {
			let show = this.value;
			if (show) {
				show = false;
			} else {
				show = true;
			}

			this.$emit('input', show);
		}
	}
};
</script>

<style lang="scss" scoped>
.mask {
	position: fixed;
	top: 0;
	bottom: 0;
	left: 0;
	right: 0;
	z-index: 99;
	background-color: rgba(0, 0, 0, 0.3);
}
.popup-wrap {
	width: 100%;
	position: fixed;
	bottom: 0;
	background-color: #ffffff;
	z-index: 999999;
	animation-name: mymove;
	animation-duration: 0.3s;
	border-radius: 20rpx 20rpx 0 0;
}

@keyframes mymove {
	from {
		bottom: -50vh;
	}
	to {
		bottom: 0;
	}
}
</style>
