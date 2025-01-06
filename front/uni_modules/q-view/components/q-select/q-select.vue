<template>
	<view>
		<q-popup v-model="show">
			<view class="popup-wrap">
				<!-- 普通选择器 -->
				<block v-if="mode == 'selector'">
					<picker-view class="picker-view" :value="selectValue" @change="onChange">
						<picker-view-column>
							<view class="item" v-for="(item, index) in list" :key="index">{{ item.lable }}</view>
						</picker-view-column>
					</picker-view>
				</block>
				<!-- 日期选择器 -->
				<block v-if="mode == 'date'">
					<picker-view v-if="visible" :indicator-style="indicatorStyle" :value="dateValue" @change="dateChange" class="picker-view">
						<picker-view-column>
							<view class="item" v-for="(item, index) in years" :key="index">{{ item }}年</view>
						</picker-view-column>
						<picker-view-column>
							<view class="item" v-for="(item, index) in months" :key="index">{{ item }}月</view>
						</picker-view-column>
						<picker-view-column>
							<view class="item" v-for="(item, index) in days" :key="index">{{ item }}日</view>
						</picker-view-column>
					</picker-view>
				</block>
				<view class="btn-wrap">
					<view @click="onCancel" class="btn">取消</view>
					<view @click="onConfirm" class="btn">确认</view>
				</view>
			</view>
		</q-popup>
	</view>
</template>

<script>
/**
 * select 列选择器
 * @description 此选择器用于单列，多列
 * @property {Array} list 选择列表数据
 * @property {String} mode 选择器模式
 * @event {Function} confirm 点击确定按钮，返回当前选择的值
 * @example <u-select v-model="show" :list="list"></u-select>
 */
export default {
	name: 'q-select',
	props: {
		mode: {
			type: String,
			default: 'selector'
		},
		list: {
			type: Array,
			default: []
		},
		value: {
			type: Boolean,
			default: false
		}
	},
	data() {
		const date = new Date();
		const years = [];
		const year = date.getFullYear();
		const months = [];
		const month = date.getMonth() + 1;
		const days = [];
		const day = date.getDate();
		for (let i = 1990; i <= date.getFullYear(); i++) {
			years.push(i);
		}
		for (let i = 1; i <= 12; i++) {
			months.push(i);
		}
		for (let i = 1; i <= 31; i++) {
			days.push(i);
		}

		const selectDate = years[0] + '-' + months[0] + '-' + days[0];
		return {
			show: false,
			selectValue: [0],
			title: 'picker-view',
			years,
			year,
			months,
			month,
			days,
			day,
			dateValue: [0, 0, 0],
			visible: true,
			indicatorStyle: `height: 50px;`,
			selectDate
		};
	},
	watch: {
		value(n) {
			this.show = n;
		},
		show(n) {
			this.$emit('input', n);
		}
	},
	methods: {
		onCancel() {
			this.show = false;
			this.$emit('cancel', '');
		},
		onConfirm() {
			this.show = false;
			if (this.mode == 'selector') {
				this.$emit('confirm', this.selectValue);
				this.$emit('input', this.selectValue);
			}

			if (this.mode == 'date') {
				this.$emit('confirm', this.selectDate);
				this.$emit('input', this.selectDate);
			}
		},
		//当滚动选择结束时候触发事件
		onChange(e) {
			this.selectValue = e.detail.value;
			this.$emit('change', e.detail.value);
		},
		dateChange(e) {
			const val = e.detail.value;
			this.year = this.years[val[0]];
			this.month = this.months[val[1]];
			this.day = this.days[val[2]];

			this.selectDate = this.year + '-' + this.month + '-' + this.day;
		}
	}
};
</script>

<style lang="scss" scoped>
// picker-view
.picker-view {
	width: 750rpx;
	height: 300rpx;
	margin-top: 20rpx;
}
.item {
	height: 50px;
	align-items: center;
	justify-content: center;
	text-align: center;
}
.popup-wrap {
	position: relative;
	height: 50vh;
	.btn-wrap {
		position: absolute;
		bottom: 30rpx;
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		.btn {
			padding: 20rpx 100rpx;
			border-radius: 10rpx;
			&:nth-child(1) {
				margin-right: 30rpx;
				background-color: #f5f5f5;
				color: #19be6b;
			}

			&:nth-child(2) {
				background-color: #19be6b;
				margin-right: 30rpx;
				color: #fff;
			}
		}
	}
}
</style>
