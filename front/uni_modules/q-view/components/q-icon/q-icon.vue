<template>
	<view>
		<text @click="click" :style="{'color':color,'font-size':size}" class="iconfont" :class="name"></text>
	</view>
</template>

<script>
	/**
	 * m-field icon 图标
	 * @property {String} name 图标名称
	 * @property {String} color 图标颜色
	 * @property {String} size 图标大小，默认40
	 */
export default {
	name:"q-icon",
	props:{
		name:String,
		color:{
			type:String,
			default:'#333'
		},
		size:{
			type:String,
			default:'40rpx'
		},
	},
	data() {
		return {};
	},
	methods:{
		click(){
			this.$emit("click")
		}
	}
};
</script>

<style lang="scss">
@import 'iconfont.css';
</style>
