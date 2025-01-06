import axios from "axios";
import localData from "../utils/data.js"
import rsa from '@/utils/rsa.js';
import user from '@/store/user.js'


// axios适配uni-app
axios.defaults.adapter = function(config) {
	return new Promise((resolve, reject) => {
		var settle = require('axios/lib/core/settle');
		var buildURL = require('axios/lib/helpers/buildURL');
		uni.request({
			method: config.method.toUpperCase(),
			url: config.baseURL + buildURL(config.url, config.params, config.paramsSerializer),
			header: config.headers,
			data: config.data,
			dataType: config.dataType,
			responseType: config.responseType,
			sslVerify: config.sslVerify,
			complete: function complete(response) {
				response = {
					data: response.data,
					status: response.statusCode,
					errMsg: response.errMsg,
					header: response.header,
					config: config
				};
				settle(resolve, reject, response);
				if (response.status==400) {
					uni.showModal({
						title: "提示",
						content: "设备异常，请检查手机时间或联系客服",
						showCancel: false,
					})
				}
				else if ((response.status + '').slice(0, 1) != "2" && (response.status + '').slice(0,
						1) != "1" && (response.status + '').slice(0, 1) != "3") {
					uni.showToast({
						title: '返回结果异常，若影响正常使用请向客服反馈~',
						icon: "none"
					})
				}
			}
		})
	})
}


const request = axios.create({
	baseURL: localData.baseUrl,
	timeout: 10000, //请求发送10秒后还是没有响应就算是失败了
})

request.interceptors.request.use(
	(config) => {

		// 要加密的原始信息
		const time = new Date().getTime();
		const userId = uni.getStorageSync('userInfos').userId;
		//生成一个32位的随机数
		let random = '';
		const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
		const charactersLength = characters.length;
		for (let i = 0; i < 16; i++) {
			random += characters.charAt(Math.floor(Math.random() * charactersLength));
		}
		//console.log('前端拦截器', time, random, userId)
		const originalData = time + "," + userId + "," + random;
		// const originalData = time + "," + userId + "," + random + "," + localData.loacalAppId;
		// 使用加密算法对信息进行加密
		const encryptedData = rsa.getRsaCode(JSON.stringify(originalData));
		// 将加密后的信息写入请求头
		config.headers['X-Encrypted-Data'] = encryptedData;
		return config
	},
	(error) => {
		console.log(error)
		return Promise.reject(error)
	})

request.interceptors.response.use(res => {
		// console.log('res',res);
		return res.data
	},
	err => console.log(err))

export default request

// 设置动态修改baseURL的方法
export const setBaseUrl = (newBaseUrl) => {
	request.defaults.baseURL = newBaseUrl;
};

const localRequest = axios.create({
	baseURL: localData.localBaseUrl,
	timeout: 10000, //请求发送10秒后还是没有响应就算是失败了
})

localRequest.interceptors.request.use(
	(config) => {

		// 要加密的原始信息
		const time = new Date().getTime();
		const userId = uni.getStorageSync('userInfos').userId;
		//生成一个32位的随机数
		let random = '';
		const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
		const charactersLength = characters.length;
		for (let i = 0; i < 16; i++) {
			random += characters.charAt(Math.floor(Math.random() * charactersLength));
		}
		//console.log('前端拦截器', time, random, userId)
		const originalData = time + "," + userId + "," + random;
		// const originalData = time + "," + userId + "," + random + "," + localData.loacalAppId;
		// 使用加密算法对信息进行加密
		const encryptedData = rsa.getRsaCode(JSON.stringify(originalData));
		// 将加密后的信息写入请求头
		config.headers['X-Encrypted-Data'] = encryptedData;
		return config
	},
	(error) => {
		console.log(error)
		return Promise.reject(error)
	})

localRequest.interceptors.response.use(res => {
		// console.log('res',res);
		return res.data
	},
	err => console.log(err))

// 导出本地的axios实例
export {
	localRequest
};