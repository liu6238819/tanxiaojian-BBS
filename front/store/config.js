import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);

export default {
	namespaced: true,
	state: {
		//详情页广告配置
		Adv: {
			Adv_show_position: null,
			Adv_show_state: null
		},
		//评论发帖时候需要校友认证
		alumniOnly: {
			state: null,
			answer_examine_state:null,
			search_alumni_only:null,
		},
		//课程评价
		courseSelection: {

		},
		//主页相关配置
		firstPage: {
			duration: null,
			duration_all:null,
			hot_post_state: null,
			my_app_tip_state: null,
			// page_path: null,
			// page_text: null,
			page_path: '/package_task/pages/bbs/mine/mine',
			page_text: '搜索',
			ranking_state: null,
			top_posts_state: null,
			topic_list: [],
			topic_state: null,
			user_first_login_score: null,
			user_fisrt_login_duration: null,
			user_first_login_state: null,
			welcome_message: [],
			rule_page:null,
			show_image_in_post_list:null,
			image_use_base64:null,
			hide_second_hand_tab:0,
			hot_post_multiple:5,
			show_silde_mask:0,
			AI_search_state:0,
		},
		//发布帖子页面
		addPost: {
			initial_topic: null,
			new_topic_back_img_url: null,
			new_topic_portrait_url: null,
			top_list: [],
			GPT_state: null,
			anonymous_state: null,
			anonymous_need_score:null,
			show_add_video_tag:null,
			night_chat_state:null,
			check_before_publish:null,
		},
		//用户首次登录
		userFirstLogin: {
			code: null,
			continuousLoginDays: null,
			isShared: null
		},
		//隐私协议弹窗
		privacyPopup:{
			privacyContractName:null,
			showPrivacyPopup:false,
			needAuthorization:true,
		},
		//进群弹窗配置项
		judgeShowPopup:{
			all_group_state:null,
			notice_group_config:null,
			extInfo:null
		},
		//举报置项
		informConfig:{
			informed_limit:null,
			inform_reasons:null
		},
		needRelaunch:false,
		
	},
	actions: {},
	mutations: {
		setFirstPage(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.firstPage.hasOwnProperty(item)) {
					state.firstPage[item] = value[item]
				}
			})

			console.log('vuex mutations', state.firstPage);
		},
		setAdv(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.Adv.hasOwnProperty(item)) {
					state.Adv[item] = value[item]
				}
			})

			console.log('vuex mutations', state.Adv);
		},
		setAlumniOnly(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.alumniOnly.hasOwnProperty(item)) {
					state.alumniOnly[item] = value[item]
				}
			})

			console.log('vuex mutations', state.alumniOnly);
		},
		setAddPost(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.addPost.hasOwnProperty(item)) {
					state.addPost[item] = value[item]
				}
			})

			console.log('fa bu tie zi', state.addPost);
		},
		setUserFirstLogin(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.userFirstLogin.hasOwnProperty(item)) {
					state.userFirstLogin[item] = value[item]
				}
			})
		
			console.log('vuex mutations', state.userFirstLogin);
		},
		setPrivacyContractName(state, value) {
			state.privacyPopup.privacyContractName = value
		},
		setShowPrivacyPopup(state, value) {
			state.privacyPopup.showPrivacyPopup = value
		},
		setNeedAuthorization(state, value) {
			state.privacyPopup.needAuthorization = value
		},
		setJudgeShowPopup(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.judgeShowPopup.hasOwnProperty(item)) {
					state.judgeShowPopup[item] = value[item]
				}
			})
		
			console.log('vuex mutations', state.judgeShowPopup);
		},
		setInformConfig(state, value) {
			Object.keys(value).forEach((item, index) => {
				if (state.informConfig.hasOwnProperty(item)) {
					state.informConfig[item] = value[item]
				}
			})
		
			console.log('举报配置', state.informConfig);
		},
		setNeedRelaunch(state, value) {
			state.needRelaunch = value
		},
	}
}
