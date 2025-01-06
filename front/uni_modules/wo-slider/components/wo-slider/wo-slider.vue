<template>
	<view>
			<view class="skidway" id="skidway"
			 :style="{ 
				 borderRadius: round.show ? round.style : '',
				 height: height + 'rpx',
				 border: border.show ? border.style : ''}">
				<view :style="{ width:moveWidth + sliderWidth + 'px', borderRadius: round.show ? round.style : '' }" class="end-status skidway-style">
					<slot name="isFinished">
						<view style="height: 100%;background-color: #65B58A; color: #fff; display: flex; justify-content: center;align-items: center;">
								<!-- 滑动成功 -->
						</view>
					</slot>
				</view>
				<view class="skidway-style" :style="{borderRadius: round.show ? round.style : ''}">
					<slot name="init">
						<view style="background-color: #DEE1E6; color: #000; height: 100%; display: flex; justify-content: center;align-items: center;">右滑验证</view>
					</slot>
				</view>
				<view
				 :style="{
					 left:moveWidth +'px',
					 borderRadius: round.show ? round.style : '',
					 width: sliderSize + 'rpx',
					 height: sliderSize + 'rpx'}"
				 @touchstart="slideStart"
				 @touchmove="slideMove"
				 @touchend="slideEnd"
				 id="slider"
				 class="slider">
					<view v-if="!finishFlag" style="height: 100%; width: 100%;">
						<slot name="begin">
							<view style="background-color: #E5673B; height: 100%; display: flex; justify-content: center; align-items: center; color: #fff;">
								开始
							</view>
						</slot>
					</view>
					<view v-else style="height: 100%; width: 100%;">
						<slot name="end">
							<view style="background-color: #1BA035; height: 100%; display: flex; justify-content: center; align-items: center; color: #fff;">
								结束
							</view>
						</slot>
					</view>
				</view>
			</view>
		</view>
</template>

<script>
	export default {
		props: {
			height: {
				type: Number,
				default: 80,
			},
			sliderSize: {
				type: Number,
				default: 80
			},
			round: {
				type: Object,
				default: function () {
					return { show: false, style: '1rpx solid #C8C9CC' }
				}
			},
			border: {
				type: Object,
				default: function () {
					return { show: false, style: '1rpx solid #C8C9CC' }
				}
			}
		},
		data() {
			return {
				initX: 0,  // 触发屏幕时的初始位置
				moveWidth: 0,  // 滑动的距离
				skidwayWidth: 0,  // 滑道长度
				sliderWidth: 0, // 滑块长度
				finishFlag: false, // 滑动是否完成
				isMove:true // 是否可滑动
			}
		},
		mounted() {
			const elemt = uni.createSelectorQuery().in(this)
			elemt.select('#skidway').boundingClientRect((data) => {
				this.skidwayWidth = data.width;
			}).exec()
			elemt.select('#slider').boundingClientRect((data) => {
				this.sliderWidth = data.width;
			}).exec()
			console.log('触发', this.skidwayWidth, this.sliderWidth);
		},
		methods: {
			slideStart(e) {
				this.initX = e.touches[0].clientX;  // 距离屏幕左侧的距离
				this.isMove = true;  // 允许滑动
			},
			slideReset() {
				this.finishFlag = false;
				this.moveWidth = 0;
			},
			slideFinish() {
				this.isMove = false;
				this.finishFlag = true;
				this.$emit('slideFinish', { status: 'success', resetFunc: this.slideReset });
			},
			slideMove(e) {
				if(!this.isMove) return;
				if (this.moveWidth >= (this.skidwayWidth - this.sliderWidth)) {
					this.moveWidth = this.skidwayWidth - this.sliderWidth
					this.slideFinish()
				} else {
					let width = e.touches[0].clientX - this.initX;
					if (width<=0) {  // 如果滑动的位置到初始位置的距离小于0，则回到原位
						width = 0
					}
					this.moveWidth = width;
				}
			},
			slideEnd(e) {  // 触摸结束判断滑动是否完成，未完成则回到原位
				if(!this.finishFlag){
					this.slideReset();
				}
			}
		},
	}
</script>

<style lang="scss" scoped>
	.skidway {	
		display: flex;
		align-items: center;
		position: relative;
		.slider {
			display: flex;
			align-items: center;
			justify-content: center;
			position: relative;
			overflow: auto;
			z-index: 2;
		}
		.skidway-style {
			width: 100%;
			height: 100%;
			overflow: auto;
			position: absolute;
			left: 0;
			top: 0;
		}
		.end-status {
			z-index: 1;
		}
	}
</style>
