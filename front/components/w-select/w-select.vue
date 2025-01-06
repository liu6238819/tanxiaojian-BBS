<template>
	<view class="w-select" id="wSelect" :style="{
      '--select-wrap-width': width,
      '--select-wrap-height': height,
      '--select-bg-color': bgColor
    }">
		<view :class="isShow ? 'select-wrap-active' : ''" class="select-wrap" @click="changeShow">
			<view v-if="multiple" class="select-content">
				<view class="select-content-item-default" v-if="multiSelectList.length === 0 && !filterable">
					{{ placeholder }}
				</view>
				<view class="select-content-item" v-if="multiSelectList.length > 0">
					{{ multiSelectList[0][valueName] }}
				</view>
				<view class="select-content-item" v-if="multiSelectList.length > 1">
					{{ multiLength }}
				</view>
			</view>
			<input v-if="!multiple || filterable" type="text" @input="inputChange" @blur="blurChange"
				:placeholder="multiple ? multiSelectList.length === 0 ? placeholder : '' : placeholder"
				:disabled="!filterable" :style="!filterable ? 'pointer-events: none' : ''" :value="inputData">
			<!-- #ifdef VUE2 -->
			<!-- 			<view @click.stop="refreshValue" class="close-icon"
				v-if="showClose && (multiple ? value.length > 0 : value)">
				<image :src="refreshUrl" mode="" />
			</view>
			<view v-if="value.length <= 0 || !showClose" :class="isShow ? 'w-select-arrow-up' : ''"
				class="w-select-arrow " /> -->
			<view @click.stop="refreshValue" class="close-icon" v-if="showClose && (multiple ? value : value)">
				<image :src="refreshUrl" mode="" />
			</view>
			<view v-if="!value || !showClose" :class="isShow ? 'w-select-arrow-up' : ''" class="w-select-arrow" />
			<!-- #endif -->
			<!-- #ifdef VUE3 -->
			<view @click.stop="refreshValue" class="close-icon"
				v-if="showClose && (multiple ? modelValue.length > 0 : modelValue)">
				<image :src="refreshUrl" mode="" />
			</view>
			<view v-if=" modelValue.length <= 0 || !showClose" :class="isShow ? 'w-select-arrow-up' : ''"
				class="w-select-arrow " />
			<!-- #endif -->

			<scroll-view scroll-y v-show="optionsShow" :class="[
          isShow
            ? showPosition === 'bottom'
              ? 'animation-bottom-in'
              : 'animation-top-in'
            : showPosition === 'bottom'
              ? 'animation-bottom-out'
              : 'animation-top-out',
          showPosition === 'bottom'
            ? 'position-bottom'
            : 'position-top'
        ]" class="select-options">
				<!-- #ifdef VUE2 -->
				<view @click.stop="handleClickItem(item)" :class="
            multiple &&
              multiSelectList.find(
                res => res[keyName] === item[keyName]
              )
              ? 'item-active'
              : value === item[keyName]
                ? 'item-active'
                : ''
          " v-for="item in filterList" :key="item[keyName]" class="select-option-item">
					{{ item[valueName] }}
				</view>
				<!-- #endif -->
				<!-- #ifdef VUE3 -->
				<view @click.stop="handleClickItem(item)" :class="
            multiple &&
              multiSelectList.find(
                res => res[keyName] === item[keyName]
              )
              ? 'item-active'
              : modelValue === item[keyName]
                ? 'item-active'
                : ''
          " v-for="item in filterList" :key="item[keyName]" class="select-option-item">
					{{ item[valueName] }}
				</view>
				<!-- #endif -->

				<view class="options-no-data" v-if="filterList.length < 1">
					无匹配数据~
				</view>
			</scroll-view>
		</view>
		<view v-if="isShow" @click="closeContentSelect" class="contentMask" />
	</view>
</template>

<script>
	export default {
		props: {
			width: {
				type: String,
				default: '200px'
			},
			height: {
				type: String,
				default: '30px'
			},
			bgColor: {
				type: String,
				default: '#fff'
			},
			// 是否多选
			multiple: {
				type: Boolean,
				default: false
			},
			// 是否可搜索
			filterable: {
				type: Boolean,
				default: false
			},
			// 是否显示关闭按钮
			showClose: {
				type: Boolean,
				default: false
			},
			// 渲染列表
			list: {
				type: Array,
				default: () => []
			},
			// #ifdef VUE3
			// 双向绑定的值
			modelValue: {
				type: [Array, String, Number],
				default: ''
			},
			// #endif
			// #ifdef VUE2
			// 双向绑定的值
			value: {
				type: [Array, String, Number],
				default: ''
			},
			// #endif
			// 默认显示的内容
			placeholder: {
				type: String,
				default: '请选择'
			},
			// 显示的内容
			valueName: {
				type: String,
				default: 'label'
			},
			// 绑定的内容
			keyName: {
				type: String,
				default: 'value'
			}
		},
		// #ifdef VUE3
		emits: ['update:modelValue', 'change'],
		// #endif
		watch: {
			list: {
				immediate: true,
				deep: true,
				handler(news) {
					this.filterList = news
					const findItem = news.find(item => {
						let isItem = ''
						// #ifdef VUE3
						if (item[this.keyName] === this.modelValue) {
							isItem = true
						} else {
							isItem = false
						}
						// #endif

						// #ifdef VUE2
						if (item[this.keyName] === this.value) {
							isItem = true
						} else {
							isItem = false
						}
						// #endif
						return isItem
					})
					if (findItem) {
						this.inputData = findItem[this.valueName]
					}
				}
			},
			// #ifdef VUE2
			value: {
				handler(newValue) {
					if (this.multiple) {
						this.multiSelectList = newValue || []
					} else {
						const findItem = this.list.find(it => it[this.keyName] === newValue)
						if (findItem) {
							this.inputData = findItem[this.valueName]
						}
					}
				}
			},
			// #endif
			// #ifdef VUE3
			modelValue: {
				handler(newValue) {
					if (this.multiple) {
						this.multiSelectList = newValue || []
					} else {
						const findItem = this.list.find(it => it[this.keyName] === newValue)
						if (findItem) {
							this.inputData = findItem[this.valueName]
						}
					}
				}
			}
			// #endif
		},
		computed: {
			multiLength() {
				const length = this.multiSelectList.length - 1
				return '+' + length
			},
			bottomDistance() {
				return (
					this.windowHeight - this.distanceTop - this.curHeight
				) // 当前元素距离可视屏幕底部的距离
			}
		},
		data() {
			return {
				inputData: '',
				multiSelectList: [],
				isShow: false,
				optionsShow: false,
				windowHeight: null,
				curHeight: null,
				distanceTop: null,
				showPosition: 'bottom',
				filterList: [],
				refreshUrl: 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c3ZnIHdpZHRoPSIyNCIgaGVpZ2h0PSIyNCIgdmlld0JveD0iMCAwIDQ4IDQ4IiBmaWxsPSJub25lIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxyZWN0IHdpZHRoPSI0OCIgaGVpZ2h0PSI0OCIgZmlsbD0id2hpdGUiIGZpbGwtb3BhY2l0eT0iMC4wMSIvPjxwYXRoIGQ9Ik0yNCA0NEMzNS4wNDU3IDQ0IDQ0IDM1LjA0NTcgNDQgMjRDNDQgMTIuOTU0MyAzNS4wNDU3IDQgMjQgNEMxMi45NTQzIDQgNCAxMi45NTQzIDQgMjRDNCAzNS4wNDU3IDEyLjk1NDMgNDQgMjQgNDRaIiBmaWxsPSJub25lIiBzdHJva2U9IiM3YzZlNmUiIHN0cm9rZS13aWR0aD0iNCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIvPjxwYXRoIGQ9Ik0yOS42NTY5IDE4LjM0MzFMMTguMzQzMiAyOS42NTY4IiBzdHJva2U9IiM3YzZlNmUiIHN0cm9rZS13aWR0aD0iNCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIi8+PHBhdGggZD0iTTE4LjM0MzIgMTguMzQzMUwyOS42NTY5IDI5LjY1NjgiIHN0cm9rZT0iIzdjNmU2ZSIgc3Ryb2tlLXdpZHRoPSI0IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4='
			}
		},
		mounted() {
			// console.log("哈哈哈哈", this.multiSelectList, this.value, this.modelValue, this.filterList)
			this.$nextTick(() => {
				const res = uni.getSystemInfoSync()
				this.windowHeight = res.windowHeight // 当前设备屏幕高度
				uni
					.createSelectorQuery()
					.in(this)
					.select('#wSelect')
					.boundingClientRect(data => {
						this.distanceTop = data.top // 当前元素距离顶部的距离
						this.curHeight = data.height
					})
					.exec()
			})
		},
		methods: {
			showPositon() {
				this.showPosition = 'bottom'
				if (this.bottomDistance < this.windowHeight / 3) {
					this.showPosition = 'top'
				}
			},
			changeShow() {
				this.isShow = !this.isShow
				if (this.isShow === false) {
					this.filterList = this.list
					setTimeout(() => {
						this.optionsShow = false
					}, 200)
				} else {
					this.showPositon()
					this.optionsShow = this.isShow
				}
			},
			closeContentSelect() {
				this.isShow = false
				setTimeout(() => {
					this.optionsShow = false
				}, 200)
			},
			setValue(value = '') {
				// #ifdef VUE3
				this.$emit('update:modelValue', value)
				// #endif

				// #ifdef VUE2
				this.$emit('input', value)
				// #endif
			},
			inputChange(e) {
				const value = e.detail.value
				if (this.multiple && this.filterable) {
					this.inputData = value
				} else {
					this.setValue(value)
					this.inputData = value
				}

				this.filterList = this.list.filter(item =>
					item[this.valueName].includes(value)
				)
			},
			blurChange(e) {
				const value = e.detail.value

				if (this.multiple && this.filterable && value) {
					const curValue = {
						[this.keyName]: value,
						[this.valueName]: value
					}
					this.multiSelect(curValue)
				}
			},
			refreshValue() {
				this.setValue('')
				this.inputData = ''
				this.$emit('change', '')
				this.filterList = this.list
				if (this.multiple) {
					this.multiSelectList = []
				}
			},
			handleClickItem(e) {
				if (this.multiple) {
					this.multiSelect(e)
				} else {
					this.setValue(e[this.keyName])
					this.inputData = e[this.valueName]
					this.$emit('change', e)
					this.changeShow()
				}
			},
			multiSelect(item) {
				const index = this.multiSelectList.findIndex(
					res => res[this.valueName] === item[this.valueName]
				)
				if (index > -1) {
					this.multiSelectList.splice(index, 1)
				} else {
					this.multiSelectList.push(item)
				}
				this.inputData = ''
				this.filterList = this.list
				this.setValue(this.multiSelectList)
				this.$emit('change', item)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.w-select {
		--select-wrap-width: 200px;
		--select-wrap-height: 30px;
		--select-border-radius: 4px;
		--select-border: 1px solid #dcdfe6;
		--select-active-border: 1px solid #409eff;
		--select-options-max-height: 150px;
		// --select-option-item-font-size: 14px;
		// --select-input-font-size: 14px;
		--select-option-item-font-size: 28rpx;
		--select-input-font-size: 28rpx;
		--no-data-default-color: #999999;
		--select-options-box-shadow: 0px 0px 12px rgb(0 0 0 / 12%);
		--select-bg-color: #ffffff;

		.select-wrap {
			position: relative;
			display: flex;
			justify-content: space-between;
			align-items: center;
			width: var(--select-wrap-width);
			height: var(--select-wrap-height);
			border: var(--select-border);
			border-radius: var(--select-border-radius);
			background-color: var(--select-bg-color);
			transition: all 0.2s;

			input {
				padding: 0 2px;
				width: 100%;
				min-width: 0;
				height: 100%;
				font-size: var(--select-input-font-size);
				flex: 1;
			}

			.select-content {
				display: flex;
				align-items: center;
				font-size: var(--select-option-item-font-size);

				.select-content-item {
					margin-left: 5px;
					padding: 2px 6px;
					border-radius: var(--select-border-radius);
					color: #aa93b1;
					background-color: #f4f4f5;
				}

				.select-content-item-default {
					margin-left: 5px;
					color: var(--no-data-default-color);
				}
			}

			.close-icon {
				position: absolute;
				top: 50%;
				right: 7px;
				z-index: 1000;
				width: 15px;
				height: 15px;
				transform: translateY(-50%);

				image {
					width: 100%;
					height: 100%;
				}
			}

			.position-bottom {
				top: calc(var(--select-wrap-height) + 10px);
			}

			.position-top {
				bottom: calc(var(--select-wrap-height) + 10px);
			}

			.select-options {
				position: absolute;
				right: 0;
				left: 0;
				z-index: 999;
				overflow: scroll;
				padding: 10px;
				max-height: var(--select-options-max-height);
				border-radius: var(--select-border-radius);
				background-color: var(--select-bg-color);
				box-shadow: var(--select-options-box-shadow);

				.select-option-item {
					margin-bottom: 5px;
					padding: 5px;
					font-size: var(--select-option-item-font-size);
					transition: background-color 0.2s;
				}

				.item-active {
					font-weight: 700;
					color: #409eff;
					background-color: #f5f7fa;
				}

				.options-no-data {
					font-size: var(--select-option-item-font-size);
					text-align: center;
					color: var(--no-data-default-color);
				}
			}

			.w-select-arrow {
				display: inline-block;
				margin: 3px 10px 0;
				width: 8px;
				height: 8px;
				border-top: 1px solid transparent;
				border-right: 1px solid transparent;
				border-bottom: 1px solid #999999;
				border-left: 1px solid #999999;
				transition: all 0.3s;
				transform: translateY(-50%) rotate(-45deg);
			}

			.w-select-arrow-up {
				transform: rotate(-225deg);
			}
		}

		.select-wrap-active {
			border: var(--select-active-border);
		}

		.animation-bottom-in {
			animation-name: bottom-in;
			animation-duration: 0.4s;
			animation-timing-function: ease-out;
			animation-fill-mode: both;
		}

		.animation-bottom-out {
			animation-name: bottom-out;
			animation-duration: 0.2s;
			animation-timing-function: ease-out;
			animation-fill-mode: both;
		}

		.animation-top-in {
			animation-name: top-in;
			animation-duration: 0.4s;
			animation-timing-function: ease-out;
			animation-fill-mode: both;
		}

		.animation-top-out {
			animation-name: top-out;
			animation-duration: 0.2s;
			animation-timing-function: ease-out;
			animation-fill-mode: both;
		}

		@keyframes bottom-in {
			0% {
				opacity: 0;
				transform: translateY(-15%);
			}

			100% {
				opacity: 1;
				transform: translateY(0);
			}
		}

		@keyframes bottom-out {
			0% {
				opacity: 1;
				transform: translateY(0);
			}

			100% {
				opacity: 0;
				transform: translateY(-20%);
			}
		}

		@keyframes top-in {
			0% {
				opacity: 0;
				transform: translateY(15%);
			}

			100% {
				opacity: 1;
				transform: translateY(0);
			}
		}

		@keyframes top-out {
			0% {
				opacity: 1;
				transform: translateY(0);
			}

			100% {
				opacity: 0;
				transform: translateY(20%);
			}
		}

		.contentMask {
			position: fixed;
			top: 0;
			right: 0;
			bottom: 0;
			left: 0;
			z-index: 998;
			width: 100%;
			height: 100%;
		}
	}
</style>
