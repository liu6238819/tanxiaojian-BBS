//该文件用于创建Vuex中最为核心的store
import Vue from 'vue'
//引入Vuex
import Vuex from 'vuex'

import user from './user'
import content from './content'
import remind from  './remind.js'
import popup from  './popup.js'
import config from './config.js'
import remindWSS from  './remindWSS.js'

//应用Vuex插件
Vue.use(Vuex)

//创建并暴露store
export default new Vuex.Store({
	modules:{
		user,
		content,
		remind,
		popup,
		config,
		remindWSS,
	}
})