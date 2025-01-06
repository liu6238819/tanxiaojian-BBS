import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);
import {
	reqEditUserInfos,
	userLogin
} from "../api/index.js"

export default {
	namespaced: true,
	state: {
		currentSchoolId: null,
		currentSchoolState: 0, //学校状态
		basicPlateId: null,
		userInfos: {
			userId: '',
			phone: null,
			password: '',
			school: '',
			stuNum: null,
			city: null,
			email: null,
			idCardUrl: null,
			nickName: null,
			headimgUrl: null,
			plateNum: null,
			scores: 0,
			openId: null,
			unionId: null,
			userType: null,
			userState: null,
			lastIp: null,
			introduction: null,
			createTime: null,
			updateTime: null
		},
		localUserInfos: { //本地用户信息
			userId: '',
			phone: null,
			password: '',
			school: '',
			stuNum: null,
			city: null,
			email: null,
			idCardUrl: null,
			nickName: null,
			headimgUrl: null,
			plateNum: null,
			scores: 0,
			openId: null,
			unionId: null,
			userType: null,
			userState: null,
			lastIp: null,
			introduction: null,
			createTime: null,
			updateTime: null
		},
		isLocalUser: 1, //当前用户是否是本地用户
		userKey: {
			openId: '',
			sessionKey: '',
			tokenWX: '',
			sessionData: ''

		},
		schoolList: [],
		// mine页面，关注我、我关注的用户
		followData: {
			fansList: [],
			fansTotalNum: 0,
			followList: [],
			followTotalNum: 0
		},
		//前端渲染用的scores
		scoresFront: 0,
		//标识用户认证状态
		currentUserState: 0,
		//标识用户禁言状态
		currentUserType: 0,
		//禁言时间
		bannedTime: null,
		//websocket连接对象，小程序中只能建立一个连接
		webSocketLinker: '',
		shouldWatchCurrenSchoolId: true,
	},
	actions: {
		async editUserInfos(context, value) {
			// 发送修改用户信息得请求
			const data = await reqEditUserInfos(value)
			if (data.code === 200) {
				context.commit('editUserInfos', value) //将用户修改得个人信息传递给vuex
			} else {
				console.log('获取信息失败');
			}
		}

	},
	mutations: {
		getUserInfos(state, value) {
			state.userInfos = value
		},
		setLocalUserInfos(state, value) {
			state.localUserInfos = value
		},
		editUserInfos(state, value) { //value 为修改得用户信息得新值
			Object.keys(value).forEach((item, index) => {
				if (state.userInfos.hasOwnProperty(item)) {
					state.userInfos[item] = value[item]
				}
			})
			//  修改完vuex中全局的userInfos也应该修改缓存中的userInfos
			const userInfos = uni.getStorageSync('userInfos')
			if (userInfos) {
				uni.setStorageSync("userInfos", state.userInfos)
			}
			// console.log('vuex mutations', state.userInfos);
		},
		clearUserInfos(state, value = {}) {
			state.userInfos = value
		},
		saveUserKey(state, value) {
			// console.log('userkey',value)
			// console.log('state',state)
			const {
				openId,
				sessionKey,
				tokenWX,
				sessionData
			} = value
			state.userKey.openId = openId
			state.userKey.sessionKey = sessionKey
			state.userKey.sessionData = sessionData
			state.userKey.tokenWX = tokenWX
		},
		getSchoolList(state, value) {
			state.schoolList = value
		},
		getCurrentSchoolId(state, value) {
			state.currentSchoolId = value
		},
		setFollowData(state, value) {
			// console.log('### vuex value',value);
			Object.keys(value).forEach(item => {
				if (state.followData.hasOwnProperty(item)) {
					state.followData[item] = value[item]
				}
			})
		},
		setScoresFront(state, value) {
			state.scoresFront = value
		},
		setCurrentUserState(state, value) {
			state.currentUserState = value
		},
		setWebSocketLinker(state, value) {
			state.webSocketLinker = value
		},
		setBasicPlateId(state, value) {
			state.basicPlateId = value
		},
		setCurrentUserType(state, value) {
			state.currentUserType = value
		},
		setBannedTime(state, value) {
			state.bannedTime = value
		},
		setCurrentSchoolState(state, value) {
			state.currentSchoolState = value
		},
		setIsLocalUser(state, value) {
			state.isLocalUser = value
		},
		setShouldWatchCurrenSchoolId(state, value) {
			state.shouldWatchCurrenSchoolId = value;
		}
	},
	getters: {
		getUser: (state) => {
			let user = state.userInfos
			return user
		},
		getCurrentSchoolId: (state) => {
			let currentSchoolId = state.currentSchoolId
			return currentSchoolId
		},
	},

}