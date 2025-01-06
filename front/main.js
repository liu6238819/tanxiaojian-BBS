import App from './App'
import store from './store/index.js'
// import moment from 'moment'
import dayjs from 'dayjs'


// Qui公共JS
import Qui from './uni_modules/q-view/js_sdk/index.js';
Vue.prototype.$q = Qui;

import examinePopup from '@/components/examine-popup/examine-popup.vue';
Vue.component('examinePopup', examinePopup)

new Vue({
	el: '#app',
	render: h => h(App)
});


import uView from "uview-ui";
Vue.use(uView);



//引入axios相关接口
import {
	reqEditUserInfos,
	getAccessTokenWX,
	code2SessionWX,
	userLoginWX,
	redeemScore,
	getOneBbsConfig,
	getOneBbsConfigWithoutSchoolId,
	addBlockRecord,
	judgeHaveBeBlocked,
	code2SessionWXlocal,
	userLoginWXlocal,
} from '@/api/index.js'
import localData from "./utils/data.js"
Vue.prototype.localData = localData;
import WXBizDataCrypt from '@/utils/WXBizDataCrypt.js'
import request, {
	setBaseUrl,
	localRequest
} from './api/request.js';
//引入图片处理的js
import {
	pathToBase64,
	base64ToPath
} from 'image-tools'

import uploadImageOSS from './js_sdk/yushijie-ossutil/ossutil/uploadFile.js'
import uploadVideoOSS from './js_sdk/yushijie-ossutil/ossutil/uploadVideo.js'

Vue.prototype.loginWX = async () => {
	return new Promise(async (resolve, reject) => {
		console.log('微信登录')
		uni.login({
			provider: 'weixin',
			success: loginRes => {
				let code = loginRes.code;
				// console.log('code',code);
				code2SessionWX({
					"code": code,
					"APPID": localData.appId
				}).then(async sessionData => {
					// 网络请求成功，获取到openId
					if (sessionData.code === 200) {
						// let tokenWX = await getAccessTokenWX({
						// 	"APPID": localData.appId
						// })
						// console.log('tokenWX', tokenWX);
						// console.log('sessionData', sessionData);
						// 更新vuex 中的sessionKey
						let userKey = {}
						// userKey.tokenWX = tokenWX.data.access_token
						userKey.sessionKey = sessionData.data.session_key
						userKey.openId = sessionData.data.openid
						userKey.sessionData = sessionData
						//存入store
						store.commit('user/saveUserKey',
							userKey)
						// 使用openId进行微信登录
						const data = await userLoginWX({
							'openId': userKey.openId
						})
						// 获取用户信息，缓存到本地
						const userInfos = data.data
						if (userInfos.introduction == null) {
							userInfos.introduction = ''
						}
						uni.setStorageSync('userInfos', userInfos)
						store.commit('user/getUserInfos', userInfos)
						resolve('success')
					} else {
						uni.showModal({
							title: "登录失败"
						})
						resolve('fail')
					}
				})
			}
		})

	})
}
//设置初始头像昵称
Vue.prototype.userInfoInit = async () => {
	return new Promise(async (resolve, reject) => {
		// 将从微信获取的用户信息写入后台,通过更新本地缓存和vuex中的用户信息
		const userInfos = uni.getStorageSync('userInfos')
		if (userInfos.headimgUrl) {
			console.log('已有头像！')
			resolve('success')
			return
		}
		//设置初始值
		let newUserInfos = {
			nickName: localData.newNickName + userInfos.userId.slice(0, 5),
			headimgUrl: localData.newHeadimgUrl
		}
		console.log('初始信息', newUserInfos)
		//上传服务器
		const editUserInfos =
			await reqEditUserInfos({
				userId: userInfos.userId,
				...newUserInfos
			})
		if (editUserInfos.code ===
			200) {
			// 后台更新用户数据成功，更新veux和本地缓存中的用户信息
			uni.setStorageSync(
				'userInfos', {
					...userInfos,
					...newUserInfos
				})
			store.commit(
				'user/editUserInfos',
				newUserInfos)
			resolve('success')
		} else {
			resolve('fail')
		}

	})
}

Vue.prototype.userAuthorize = async () => {
	return new Promise(async (resolve, reject) => {
		console.log('微信授权')
		uni.showModal({
			title: '提示',
			content: '请授权用户信息',
			success: async res => {
				if (res.confirm) {
					wx.getSetting({
						success(res) {
							if (res.authSetting['scope.userInfo']) {
								wx.getUserProfile({
									desc: '用于完善用户资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
									success: async res => {
										console.log(res)
										// console.log('res ###', res);
										if (!res
											.encryptedData ||
											!
											res.iv)
											return false;
										const {
											nickName,
											city,
											avatarUrl
										} = res.userInfo
										const
											newUserInfos = {
												nickName,
												city,
												headimgUrl: avatarUrl,
											}
										// 将从微信获取的用户信息写入后台,通过更新本地缓存和vuex中的用户信息
										const
											userInfos =
											uni
											.getStorageSync(
												'userInfos'
											)
										const
											editUserInfos =
											await reqEditUserInfos({
												userId: userInfos
													.userId,
												...
												newUserInfos
											})
										if (editUserInfos
											.code ===
											200) {
											// 后台更新用户数据成功，更新veux和本地缓存中的用户信息
											uni.setStorageSync(
												'userInfos', {
													...
													userInfos,
													...
													newUserInfos
												})
											store
												.commit(
													'user/editUserInfos',
													newUserInfos
												)

										}
										uni.showToast({
											title: '用户授权成功'
										})
										resolve(
											'success'
										)
									},
									fail(res) {
										resolve('fail')
										console.log(
											'获取用户微信信息失败',
											res)
									}
								})
							} else {
								resolve('fail')
								console.log('未授权!')
							}
						}
					})
				} else if (res.cancel) {
					resolve('fail')
					//console.log('用户点击取消');
				}
			},
		});


	})
}

// 审核：弹出授权框，获取用户手机号，并让用户填写学校等信息
Vue.prototype.userExamine = async (e, sessionData) => {
	// e：获取手机号点击按钮的点击事件
	const {
		userId
	} = uni.getStorageSync('userInfos')
	console.log("sessionData", sessionData);
	if (sessionData.code === 200) {
		const {
			session_key,
		} = sessionData.data
		console.log("sessionKey", session_key);
		if (e.detail.errMsg == 'getPhoneNumber:fail user deny') {
			// 用户拒绝授权使用手机号
			uni.showToast({
				title: '拒绝授权将影响使用',
				icon: "none"
			});
		} else {
			let phone = null
			let pc = new WXBizDataCrypt(localData.appId,
				session_key); //appid 和sessionKey
			console.log("e", e);
			phone = pc.decryptData(e.detail
				.encryptedData, e.detail.iv);
			console.log('phone', phone);
			const {
				phoneNumber
			} = phone
			// console.log("phoneNumber", phoneNumber);
			//手机号写入后台，敏感信息前端无需保存
			const editUserInfos = await reqEditUserInfos({
				userId,
				phone: phoneNumber
			})
			if (editUserInfos.code === 200) {
				//  进入认证页面
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/user-examine'
				});
			}
		}
	}


}
//权限审核方法
Vue.prototype.permissionCheck = async (userId, schoolId, plateId) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: localData.baseUrl + '/commonAPIs/permissionCheck',
			data: {
				"userId": userId,
				"schoolId": schoolId,
				"plateId": plateId
			},
			method: 'POST',
			header: {
				'content-type': 'application/x-www-form-urlencoded'
			},
			success: async res => {
				console.log('权限审核结果：')
				console.log(res.data)
				let authorizeResult = null
				if (res.data.code == 0) {
					resolve(res.data.code)
					return
				} else {
					uni.showToast({
						icon: 'none',
						title: res.data.message
					})
					// if (res.data.code == 100) { //先授权昵称，再跳转至认证页面
					// 	authorizeResult = await Vue.prototype.userAuthorize()
					// 	if (authorizeResult == 'success') {
					// 		uni.navigateTo({
					// 			url: '/pages/user/user-examine'
					// 		});
					// 	} else {
					// 		resolve(res.data.code);
					// 	}
					// 	return
					// }
					if (res.data.code == 100 || res.data.code ==
						200) { //跳转至认证页面
						uni.hideToast()
						uni.showModal({
							title: '提示',
							content: '该校新用户或审核未通过，需要进行校友身份认证。在认证过程中请认真阅读隐私说明。',
							success: async res => {
								if (res.confirm) {
									//authorizeResult = await Vue.prototype.userAuthorize();
									authorizeResult = await Vue.prototype
										.userInfoInit();
									if (authorizeResult ==
										'success') {
										uni.navigateTo({
											url: '/package_task/pages/bbs/user/user-examine'
										});
									} else {
										resolve('initFail');
									}
								} else if (res.cancel) {
									resolve('userCancel');
									//console.log('用户点击取消');
								}
							},
						});
						return
					}
					if (res.data.code == 300 || res.data.code == 600) { //仅进行toast提示
						resolve(res.data.code);
						return
					}
					if (res.data.code == 700) { //只进行授权
						//authorizeResult = await Vue.prototype.userAuthorize()
						authorizeResult = await Vue.prototype.userInfoInit();
						if (authorizeResult == 'success') {
							resolve(0)
						} else {
							resolve(res.data.code);
						}
						return
					}
					if (res.data.code == 500) {
						console.log("预警用户")
						uni.hideToast()
						//用户是预警用户，修改store中currentUserType
						store.commit('user/setCurrentUserType', 1)
						resolve(0);
						return
					}
				}

				resolve(res.data.code);

			},
			fail(res) {
				console.log('权限审核接口请求失败！')
				reject(400);
			}
		})
	})
}

//非BBS权限审核方法
Vue.prototype.permissionCheckOutBBS = async (userId, schoolId, schoolFlag) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: localData.baseUrl + '/commonAPIs/permissionCheckOutBBS',
			data: {
				"userId": userId,
				"schoolId": schoolId,
				"schoolFlag": schoolFlag
			},
			method: 'POST',
			header: {
				'content-type': 'application/x-www-form-urlencoded'
			},
			success: async res => {
				console.log('权限审核结果：')
				console.log(res.data)
				let authorizeResult = null
				if (res.data.code == 0) {
					resolve(res.data.code)
					return
				} else {
					uni.showToast({
						icon: 'none',
						title: res.data.message
					})
					if (res.data.code == 100 || res.data.code == 200) { //先授权昵称，再跳转至认证页面
						uni.hideToast()
						uni.showModal({
							title: '提示',
							content: '该校新用户或审核未通过，需要进行校友身份认证。在认证过程中请认真阅读隐私说明。',
							success: async res => {
								if (res.confirm) {
									//authorizeResult = await Vue.prototype.userAuthorize();
									authorizeResult = await Vue.prototype
										.userInfoInit();
									if (authorizeResult == 'success') {
										uni.navigateTo({
											url: '/package_task/pages/bbs/user/user-examine'
										});
									} else {
										resolve('initFail');
									}
								} else if (res.cancel) {
									resolve('userCancel');
									//console.log('用户点击取消');
								}
							},
						});
					}
					if (res.data.code == 300 || res.data.code == 500 || res.data
						.code ==
						600) { //仅进行toast提示
						resolve(res.data.code);
						return
					}
					if (res.data.code == 700) { //只进行授权
						//authorizeResult = await Vue.prototype.userAuthorize()
						authorizeResult = await Vue.prototype.userInfoInit();
						if (authorizeResult == 'success') {
							resolve(0)
						} else {
							resolve(res.data.code);
						}
						return
					}
				}

				resolve(res.data.code);

			},
			fail(res) {
				console.log('权限审核接口请求失败！')
				reject(400);
			}
		})
	})
}

//0407新审核逻辑
Vue.prototype.permissionCheckNew = async (userId, schoolId, schoolFlag) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: localData.baseUrl + '/commonAPIs/permissionCheckOutBBS',
			data: {
				"userId": userId,
				"schoolId": schoolId,
				"schoolFlag": schoolFlag
			},
			method: 'POST',
			header: {
				'content-type': 'application/x-www-form-urlencoded'
			},
			success: async res => {
				console.log('权限审核结果：')
				console.log(res.data)
				let authorizeResult = null
				if (res.data.code == 0) {
					resolve(res.data.code)
					return
				} else {
					uni.showToast({
						icon: 'none',
						title: res.data.message
					})
					if (res.data.code == 100 || res.data.code == 200) { //先授权昵称，再跳转至认证页面
						uni.hideToast()
						authorizeResult = await Vue.prototype
							.userInfoInit();
						if (authorizeResult == 'success') {
							resolve(res.data.code);

						} else {
							resolve('initFail');
						}
					}
					if (res.data.code == 300 || res.data.code == 500 || res.data
						.code ==
						600) { //仅进行toast提示
						//禁言由toast改为model框提示
						uni.hideToast()
						let title = "认证提示";
						if (res.data.code == 600) {
							title = title;
						}
						uni.showModal({
							title: '禁言提示',
							content: res.data.message,
							// cancelText:'申诉',
							showCancel: false,
							success: async res => {
								if (res.confirm) {} else if (res.cancel) {}
							},
						})
						resolve(res.data.code);
						return
					}
					if (res.data.code == 700) { //只进行授权
						//authorizeResult = await Vue.prototype.userAuthorize()
						authorizeResult = await Vue.prototype.userInfoInit();
						if (authorizeResult == 'success') {
							resolve(0)
						} else {
							resolve(res.data.code);
						}
						return
					}
					//用户在其他学校有认证通过的记录
					if (res.data.code == 800) {
						uni.hideToast()
						uni.showModal({
							content: res.data.message,
							// confirmText:'联系客服',
							showCancel: false,
							// cancelText:'取消',
							success: async res => {
								if (res.confirm) {
									// uni.navigateTo({
									// 	url: '/package_task/pages/group-code/index?codeKind=' + 2
									// })
								} else if (res.cancel) {
									// console.log('用户点击取消');
								}
							},
						});
						return
					}

					if (res.data.code == 400) {
						uni.hideToast()
					}
				}

				resolve(res.data.code);

			},
			fail(res) {
				console.log('权限审核接口请求失败！')
				reject(400);
			}
		})
	})
}

Vue.prototype.uploadImageOSS = function(filePath, catalogue, schoolId) {
	return filePath;
	// console.log(filePath + ' ' + catalogue);
	// return new Promise((resolve, reject) => {
	// 	uploadImageOSS(filePath, catalogue, schoolId,
	// 		result => {
	// 			console.log(result);
	// 			resolve(result);
	// 		},
	// 		fail => {
	// 			console.log(fail);
	// 			reject(fail);
	// 		}
	// 	)
	// })
}

Vue.prototype.uploadVideoOSS = function(filePath, catalogue, suffix) {
	// console.log(filePath + ' ' + catalogue);
	return new Promise((resolve, reject) => {
		uploadVideoOSS(filePath, catalogue, suffix,
			result => {
				console.log(result);
				resolve(result);
			},
			fail => {
				console.log(fail);
				reject(fail);
			}
		)
	})
}

Vue.prototype.ugcTxtCheck = function(access_token, openid, txtContent, scene) {
	return 0;

}
Vue.prototype.ugcImgCheck = function(access_token, openid, imgUrl, type, scene) {
	return 0
}
Vue.prototype.urlToBase64 = function(url) {

	return new Promise((resolve, reject) => {
		pathToBase64(url)
			.then(base64 => {
				// console.log(base64);
				resolve(base64);
			})
			.catch(error => {
				reject(error);
			});
	})
}

Vue.prototype.base64ToUrl = function(base64) {
	return new Promise((resolve, reject) => {
		base64ToPath(base64)
			.then(url => {
				console.log(url);
				resolve(url);
			})
			.catch(error => {
				reject(error);
			});
	})
}

Vue.prototype.compressImgbycanvas = async function(that, image) {
	const result = await localData.compressionIamge(that, image);
	console.log(result)
}



Vue.prototype.base64ToBlob = function(base64Data) {
		var byteString;
		if (base64Data.split(',')[0].indexOf('base64') >= 0)
			byteString = atob(base64Data.split(',')[1]); //base64 解码
		else {
			byteString = unescape(base64Data.split(',')[1]);
		}
		var mimeString = base64Data.split(',')[0].split(':')[1].split(';')[0]; //mime类型 -- image/png

		// var arrayBuffer = new ArrayBuffer(byteString.length); //创建缓冲数组
		// var ia = new Uint8Array(arrayBuffer);//创建视图
		var ia = new Uint8Array(byteString.length); //创建视图
		for (var i = 0; i < byteString.length; i++) {
			ia[i] = byteString.charCodeAt(i);
		}
		var blob = new Blob([ia], {
			type: mimeString
		});
		return blob;


	},
	Vue.prototype.copyAndPaste = function(type, that, data) {
		// 参数说明：
		// type:0 复制；1 粘贴
		// that:上下文
		// data:复制的内容
		return new Promise((resolve, reject) => {
			if (type == 0) { //复制
				let context = that
				console.log('长按复制');
				uni.setClipboardData({
					data: data,
					success: function(res) {
						console.log('success');
						resolve(res.data);
					}
				});
			} else { //粘贴
				let context = that
				console.log('长按粘贴');
				uni.getClipboardData({
					success: function(res) {
						resolve(res.data);
					}
				});
			}
		})
	}

//分享时触发，是否要加积分
Vue.prototype.fisrtShareMiniApp = async function(UserId, SchoolId) {
	let that = this
	await localData.cloud_shared.init()
	return new Promise((resolve, reject) => {
		// wx.cloud.init({
		// 	env: localData.envId,
		// 	traceUser: true,
		// })
		// wx.cloud.callFunction({
		localData.cloud_shared.callFunction({
			name: 'userLoginSQL', //云函数的名称
			data: {
				functionName: 'firstShare', //调用哪个数据库函数
				schoolId: SchoolId, //前
				userId: UserId,
				usingDatabase: localData.usingDatabase,
			},
			async complete(res) {
				let isFirstShare = res.result
				if (isFirstShare == 1) {
					let applyForm = {
						userId: that.userInfos.userId,
						changeNum: -15
					}
					let result = await redeemScore(applyForm)
					if (result.code == 200) {
						console.log("分享加分成功")
					} else {
						console.log("分享加分失败")
					}
					resolve(isFirstShare)
				} else {
					console.log("当天已经分享过")
					resolve(isFirstShare)
				}
				// console.log("帖子Id", nowPostId)
			}
		})
	})
}


Vue.prototype.sendCommentRemind = async function(currentContent, commentText, replyUserOpenId) {
	//订阅信息推送
	// let time = moment().add(0, 'h').format('YYYY年MM月DD日 HH:mm:ss')
	let time = dayjs().add(0, 'h').format('YYYY年MM月DD日 HH:mm:ss')
	let content = ''
	let Comment = ''

	if (currentContent.title) {
		if (currentContent.title.length > 10) {
			content = currentContent.title.slice(0, 10) + '...'
		} else {
			content = currentContent.title
		}
	} else {
		if (currentContent.contentText.length > 10) {
			content = currentContent.contentText.slice(0, 10) + '...'
		} else {
			content = currentContent.contentText
		}
	}
	if (commentText.length > 15) {
		Comment = commentText.slice(0, 15) + '...'

	} else {
		Comment = commentText
	}
	//获取对应模板ID
	let TemplateId = '';
	let TemplateId9 = '';
	let TemplateId10 = '';
	for (var i = 0; i < localData.messageTemplateList.length; i++) {
		if (localData.messageTemplateList[i].index == 7) {
			TemplateId = localData.messageTemplateList[i].id
		}
		if (localData.messageTemplateList[i].index == 9) {
			TemplateId9 = localData.messageTemplateList[i].id
		}
		if (localData.messageTemplateList[i].index == 10) {
			TemplateId10 = localData.messageTemplateList[i].id
		}
	}
	// wx.cloud.init({
	// 	env: localData.envId,
	// 	traceUser: true,
	// })
	// wx.cloud.callFunction({
	await localData.cloud_shared.init()
	await localData.cloud_shared.callFunction({
		name: 'sendRemind', //云函数的名称
		data: {
			userId: replyUserOpenId,
			content: content,
			nowTime: time,
			comment: Comment,
			templateId: TemplateId,
			templateId9: TemplateId9,
			templateId10: TemplateId10,
			messageKind: 7, //评论通知
			APPID: localData.appId,
			contentId: currentContent.contentId
		},
		success() {
			console.log('云函数调用成功')
		},
		fail() {
			console.log('云函数调用失败')
		},
	})
}



//判断用户是否需要隐私授权弹窗
Vue.prototype.judgePrivacySetting = async function() {
	let that = this
	return new Promise((resolve, reject) => {
		wx.getPrivacySetting({
			success: res => {
				console.log(
					res
				) // 返回结果为: res = { needAuthorization: true/false, privacyContractName: '《xxx隐私保护指引》' }
				if (res.needAuthorization) {
					// 设置需要弹出隐私协议的全局变量
					store.commit(
						'config/setShowPrivacyPopup',
						res.needAuthorization
					)
					//设置隐私协议名称的全局变量
					store.commit(
						'config/setPrivacyContractName',
						res.privacyContractName
					)

				} else {
					// 用户已经同意过隐私协议，所以不需要再弹出隐私协议，也能调用隐私接口
					//设置是否已经授权的全局变量
					store.commit(
						'config/setNeedAuthorization',
						res.needAuthorization
					)
				}
				resolve(res.needAuthorization)
			},
			fail: () => {},
			complete: () => {}
		})
	})
}

//base64转url,并缓存在微信本地
Vue.prototype.base64src = async function(base64data, FILE_BASE_NAME) {
	return new Promise((resolve, reject) => {
		// console.log("图片名称",FILE_BASE_NAME)
		const fsm = wx.getFileSystemManager();
		const [, format, bodyData] = /data:image\/(\w+);base64,(.*)/.exec(base64data) || [];
		if (!format) {
			reject(new Error('ERROR_BASE64SRC_PARSE'));
		}
		//设置图片地址
		const filePath = `${wx.env.USER_DATA_PATH}/${FILE_BASE_NAME}.${format}`;
		const buffer = wx.base64ToArrayBuffer(bodyData);
		// console.log(filePath,buffer)
		//微信缓存图片，并返回缓存地址
		fsm.writeFile({
			filePath,
			data: buffer,
			encoding: 'binary',
			success() {
				// console.log(filePath + "?flg=" + new Date().getTime())
				// resolve(filePath + "?flg=" + new Date().getTime())
				resolve(filePath)
			},
			fail() {
				reject(new Error('ERROR_BASE64SRC_WRITE'));
			},
		});
	})
}

//获取图片信息,并缓存在微信本地，返回本地连接
Vue.prototype.getImageLocalUrl = async function(imageType, imageUrl) {
	return new Promise((resolve, reject) => {
		//构建本地存储名称
		let imageUrlArr = imageUrl.split("/")
		let localUrl = imageType + "_" + imageUrlArr[imageUrlArr.length - 1] + ".png"
		//判断本地缓存有无该图片
		const fs = wx.getFileSystemManager();
		fs.readdir({ // 获取文件列表
			dirPath: wx.env.USER_DATA_PATH,
			success(res) {
				// console.log("目标目录文件", res)
				let targetCount = res.files.filter(file => file.includes(localUrl)).length;
				// console.log("目标文件数", targetCount)
				if (targetCount > 0) {
					// 文件存在
					// console.log("文件存在", res)
					resolve(`${wx.env.USER_DATA_PATH}/` + localUrl)
				} else {
					// 文件不存在，获取图片并缓存到到本地
					uni.getImageInfo({
						src: imageUrl,
						success: (res) => {
							// console.log("uniapp",res.path)
							//成功获取到图片，将图片缓存到本地
							fs.copyFile({
								srcPath: res.path,
								destPath: `${wx.env.USER_DATA_PATH}/` +
									localUrl,
								success(copyRes) {
									//成功缓存图片，返回本地缓存地址
									// console.log("文件缓存成功", copyRes)
									resolve(`${wx.env.USER_DATA_PATH}/` +
										localUrl)
								},
								fail(copyRes) {
									//图片缓存失败，返回图片网络地址
									console.log("图片缓存失败", copyRes, imageUrl)
									resolve(imageUrl)
								}
							})

						},
						fail: (err) => {
							//获取图片信息失败，返回图片网络地址
							console.log("网络图片信息获取失败", err, imageUrl)
							resolve(imageUrl)
						}
					})
				}

			},
			fail(res) {
				//获取本地图片信息失败，返回图片网络地址
				console.log("本地图片信息获取失败", res, imageUrl)
				resolve(imageUrl)
			}
		})
	})
}

//按学校id获取学校配置
Vue.prototype.getSchoolFunctionConfig = async function() {
	let that = this
	let UserId = '';
	if (uni.getStorageSync('userInfos').userId != null &&
		uni.getStorageSync('userInfos').userId != '') {
		UserId = uni.getStorageSync('userInfos').userId
	}
	console.log('启动项', this.userInfos.userId)
	//获取启动项配置
	let param = {
		schoolId: this.currentSchoolId,
		configType: 'functionConfig'
	}
	let data = await getOneBbsConfig(param)
	if (data.code == 200) {
		let configList = JSON.parse(data.data.configJson).data
		console.log("启动时总配置项（数据库）", configList)
		if (configList.length > 0) {
			for (var i = 0; i < configList.length; i++) {
				//广告位置
				if (configList[i].function_name == 'Adv') {
					let AdvConfig = configList[i]
					// this.setAdv(AdvConfig);
					store.commit('config/setAdv', AdvConfig)
				}
				//首页
				else if (configList[i].function_name == 'firstPage') {
					let firstPageConfig = configList[i]
					// this.setFirstPage(firstPageConfig);
					store.commit('config/setFirstPage', firstPageConfig)
				}
				//校友认证（发帖评论）
				else if (configList[i].function_name == 'alumniOnly') {
					let alumniOnlyConfig = configList[i]
					// this.setAlumniOnly(alumniOnlyConfig);
					store.commit('config/setAlumniOnly', alumniOnlyConfig)

				}
				//新建话题
				else if (configList[i].function_name == 'addTopic') {
					let addTopicConfig = configList[i]
					// this.setAddPost(addTopicConfig);
					store.commit('config/setAddPost', addTopicConfig)
				}
				//匿名
				else if (configList[i].function_name == 'chatGPT') {
					let anonymousConfig = configList[i]
					// this.setAddPost(anonymousConfig);
					store.commit('config/setAddPost', anonymousConfig)
				}
				//显示弹窗
				else if (configList[i].function_name == 'judgeShowPopup') {
					let showPopupConfig = configList[i]
					// this.setJudgeShowPopup(showPopupConfig)
					store.commit('config/setJudgeShowPopup', showPopupConfig)
				}
				//举报配置
				else if (configList[i].function_name == 'informConfig') {
					let informConfig = configList[i]
					// this.setInformConfig(informConfig)
					store.commit('config/setInformConfig', informConfig)
				} else {

				}
			}
		}
	} else {
		console.log("启动项配置获取失败");
	}
}

//按学校id获取基础配置
Vue.prototype.getSchoolBasicConfig = async function() {
	let that = this
	let UserId = '';
	if (uni.getStorageSync('userInfos').userId != null &&
		uni.getStorageSync('userInfos').userId != '') {
		UserId = uni.getStorageSync('userInfos').userId
	}
	// console.log('基础配置', this.userInfos.userId)
	//获取基础配置
	// let param = {
	// 	configType: 'basicConfig'
	// }
	// let data = await getOneBbsConfigWithoutSchoolId(param)
	let param = {
		schoolId: this.currentSchoolId,
		configType: 'basicConfig'
	}
	if (!this.currentSchoolId || this.currentSchoolId == 0) {
		return
	}
	let data = await getOneBbsConfig(param)
	if (data.code == 200) {
		let configList = JSON.parse(data.data.configJson).data
		console.log("基础配置配置项（数据库）", configList)
		for (var i = 0; i < configList.length; i++) {
			// console.log(configList[i].schoolId*1)
			if (configList[i].schoolId * 1 == this.currentSchoolId) {
				//接口地址
				// localData.baseUrl = configList[i].baseUrl
				// localData.webSocketUrl = configList[i].webSocketUrl
				//app资源相关
				localData.appId = configList[i].appId
				localData.envId = configList[i].envId
				localData.resourceAppid = configList[i].resourceAppid
				localData.resourceEnv = configList[i].resourceEnv
				//小程序默认项
				localData.newNickName = configList[i].newNickName
				localData.newHeadimgUrl = configList[i].newHeadimgUrl
				localData.contentTypeList = configList[i].contentTypeList
				//订阅模板相关
				localData.templateId4 = configList[i].templateId4
				localData.templateId7 = configList[i].templateId7
				localData.templateId9 = configList[i].templateId9
				localData.templateId10 = configList[i].templateId10
				localData.changeSchoolState = configList[i].changeSchoolState
				console.log(localData.newNickName, localData.newHeadimgUrl, localData.contentTypeList)
				//默认版块
				store.commit('user/setBasicPlateId', configList[i].basicPlateId)
				//维护状态判断
				
				// 如果系统维护状态为关闭，直接返回 false
				if (configList[i].maintenanceStatus == 0) {

				} else if (configList[i].maintenanceStatus == 1) {
					const currentTime = new Date().toLocaleTimeString('en-GB', { hour12: false }); // HH:mm:ss
					const current = new Date(`1970-01-01T${currentTime}`);
					const start = new Date(`1970-01-01T${configList[i].maintenanceStart}`);
					const end = new Date(`1970-01-01T${configList[i].maintenanceEnd}`);
					console.log("维护",currentTime,current,start,end)
					if (current >= start && current <= end) {
						uni.redirectTo({
							url: '/package_task/pages/bbs/more/maintenance'
						})
					}
				}
			}
		}

	} else {
		console.log("基础配置获取失败");
	}
}

//用户拉黑操作
Vue.prototype.addBlockRecordByUser = async function(userId, targetId) {
	console.log("进入")
	let that = this
	const params = {
		userId: userId,
		targetId: targetId,
	}
	uni.showModal({
		title: '确认拉黑该用户吗？',
		content: '拉黑后，该用户将无法与您互动以及查看您的主页，对方发布的内容也将被屏蔽。',
		success: async function(res) {
			if (res.confirm) {
				console.log('用户点击确定');
				const data = await addBlockRecord(params)
				if (data.code === 200) {
					uni.showToast({
						title: '拉黑成功！刷新后，将屏蔽该用户发布内容。',
						icon: 'none',
						duration: 3000
					})
				} else {
					uni.showToast({
						title: '操作失败，服务器异常！',
						icon: 'none'
					})
				}
			} else if (res.cancel) {
				console.log('用户点击取消');
			}
		}
	})
}

//查询是否被查询用户拉黑
Vue.prototype.judgeBeBlockByUser = async function(userId, searchUserId) {
	console.log("进入", userId, searchUserId)
	let that = this
	const params = {
		userId: userId,
		searchUserId: searchUserId,
	}
	const data = await judgeHaveBeBlocked(params)
	// setTimeout(function() {
	// 	uni.hideLoading()
	// }, 500)
	if (data.code === 200) {
		console.log("是否被拉黑", data)
	} else {
		console.log('查询失败');
	}
	return data.data
}

//根据学校状态和学校id,修改baseUrl
Vue.prototype.changeBaseUrlBySchoolId = async function(school) {
	// console.log(school)
	if (school.baseUrl && school.appId) {
		localData.baseUrl = school.baseUrl
		localData.appId = school.appId
		setBaseUrl(school.baseUrl)
	}
	// console.log(localData.baseUrl, localData.webSocketUrl)
}

Vue.prototype.localLoginWX = async () => {
	return new Promise(async (resolve, reject) => {
		console.log('微信登录')
		uni.login({
			provider: 'weixin',
			success: loginRes => {
				let code = loginRes.code;
				// console.log('code',code);
				code2SessionWXlocal({
					"code": code,
					"APPID": localData.loacalAppId
				}).then(async sessionData => {
					// 网络请求成功，获取到openId
					if (sessionData.code === 200) {
						let userKey = {}
						userKey.sessionKey = sessionData.data.session_key
						userKey.openId = sessionData.data.openid
						userKey.sessionData = sessionData
						//存入store
						store.commit('user/saveUserKey', userKey)
						// 使用openId进行微信登录
						const data = await userLoginWXlocal({
							'openId': userKey.openId
						})
						// 获取用户信息，缓存到本地
						const userInfos = data.data.user
						if (userInfos.introduction == null) {
							userInfos.introduction = ''
						}
						uni.setStorageSync('userInfos', userInfos)
						store.commit('user/getUserInfos', userInfos)
						store.commit('user/setLocalUserInfos', userInfos)
						if (data.data.havePhone == 1) {
							resolve(1)
						} else {
							resolve(0)
						}
					} else {
						uni.showModal({
							title: "登录失败"
						})
						resolve('fail')
					}
				})
			}
		})

	})
}

Vue.prototype.getLocalSchoolist = async function() {
	return new Promise(async (resolve, reject) => {
		// console.log(userInfos)
		let params = {
			'appId': localData.appId
		}
		const schoolLists = await localRequest.get('/content/getSchoolList', {
			params: params,
		})
		if (schoolLists.code === 200) {
			console.log('学校列表', schoolLists)
			resolve(schoolLists.data)
			//保存学校列表到store
		}

	})
}


Vue.config.productionTip = false

App.mpType = 'app'

Vue.filter('dateFormat', function(time) {
	// return moment(time).format('YYYY-MM-DD ')
	return dayjs(time).format('YYYY-MM-DD ')
})




// #ifndef VUE3
import Vue from 'vue'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App,
	store
})
app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
export function createApp() {
	const app = createSSRApp(App)
	return {
		app
	}
}
// #endif