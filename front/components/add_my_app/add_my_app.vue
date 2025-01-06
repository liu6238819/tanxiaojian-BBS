<template>
	<view v-if="tipState == 1" class="tip" @click="hideTip">
		<view name="content">添加到“我的小程序”</view>
		<view name="content">下次打开更方便</view>
		<view class="tip_icon"></view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import localData from '../../utils/data.js';
	export default {
		computed: {
			...mapState('popup', ['showPageNum']),
			...mapState('config', ['firstPage']),
		},
		name: 'add_my_app',
		props: {
			top: Number,
		},
		data() {
			return {
				tipState: 0,
			}
		},
		async mounted() {
			let that = this
			this.increaseShowPageNum();
			console.log('打开页面数', this.showPageNum)
			if (this.showPageNum == 2 && this.firstPage.duration != null && this.firstPage.duration != '') {
				this.tipState = this.firstPage.my_app_tip_state
				setTimeout(function() {
					that.tipState = 0
				}, 3000)
			} else {
				this.tipState = 0
			}
		},
		watch: {
			firstPage: {
				async handler(newVal, oldVal) {
					let that = this
					console.log('watch触发')
					// #ifdef MP-WEIXIN
					if (this.firstPage.duration != null && this.firstPage.duration != '') {
						if (this.showPageNum == 2) {
							this.tipState = this.firstPage.my_app_tip_state
							setTimeout(function() {
								that.tipState = 0
							}, 3000)
						} else {
							this.tipState = 0
						}

					}
					// #endif
				},
				deep: true
			}
		},
		methods: {
			...mapMutations('popup', {
				increaseShowPageNum: 'increaseShowPageNum',

			}),
			//隐藏"我的小程序"提示
			hideTip() {
				this.tipState = 0
			},
		}
	}
</script>

<style lang="scss" scoped>
	.tip {
		background: rgba(0, 0, 0, 0.7);
		color: #fff;
		position: absolute;
		border-radius: 4px;
		font-size: 26rpx;
		padding: 5px;
		min-width: 10px;
		word-wrap: break-word;
		display: inline-block;
		white-space: nowrap;
		z-index: 1001;
		// top: 10%;
		right: 17%;
		display: block;
	}

	.tip_icon {
		border-left: 4px solid transparent;
		border-right: 4px solid transparent;
		border-bottom: 15px solid rgba(0, 0, 0, 0.7);
		top: -15px;
		// border-top: 6px solid transparent;
		// border-bottom: 6px solid transparent;
		// border-right: 6px solid rgba(255, 0, 0, 0.4);
		right: 6px;
		width: 0;
		height: 0;
		position: absolute;
		z-index: 9;
	}
</style>
