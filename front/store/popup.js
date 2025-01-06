import Vue from 'vue'

export default {
	namespaced: true,
	state: {
		showDetailPageNum: 0,
		showCourseCommentPageNum: 0,
		showPageNum: 0,
		showDaliyPopupNum: 0,
		showAdNum: 0,
		showIndexMask: 0,
		showSecondHandPageNum: 0,
		searchTimes:0,
		focusSearchTimes:0,
		
	},
	actions: {},
	mutations: {
		setShowDetailPageNum(state) {
			state.showDetailPageNum = 0
		},
		increaseShowDetailPageNum(state) {
			state.showDetailPageNum++
		},
		setShowCourseCommentPageNum(state) {
			state.showCourseCommentPageNum = 0
		},
		increaseShowCourseCommentPageNum(state) {
			state.showCourseCommentPageNum++
		},
		setShowPageNum(state) {
			state.showPageNum = 0
		},
		increaseShowPageNum(state) {
			state.showPageNum++
		},
		increaseshowDaliyPopupNum(state) {
			state.showDaliyPopupNum++
		},
		setShowAdNum(state) {
			state.showAdNum = 0
		},
		increaseShowAdNum(state) {
			state.showAdNum++
		},
		setShowIndexMask(state, value) {
			state.showIndexMask = value
		},
		increaseShowSecondHandPageNum(state) {
			// console.log("进入闲置交易页面")
			state.showSecondHandPageNum++
		},
		increaseSearchTimes(state) {
			state.searchTimes++
		},
		increaseFocusSearchTimes(state) {
			state.focusSearchTimes++
		},
	}
}
