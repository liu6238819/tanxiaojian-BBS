// 云函数入口文件
const cloud = require('wx-server-sdk')
var request = require('request-promise');
cloud.init({
	env: cloud.DYNAMIC_CURRENT_ENV,
	// traceUser: true
})

const db = cloud.database()
const $ = db.command.aggregate
const _ = db.command


// 云函数入口函数
exports.main = async (event, context) => {
	console.log(event)
	let popupRecords = ''
	let config = ''
	let memberListRecord = ''
	let payedClockIn = ''
	//数据库选择初始化
	if (event.usingDatabase == 0) {
		popupRecords = 'popup_records_test';
		config = 'function_config_test';
		memberListRecord = 'member_list_test';
		payedClockIn = 'payed_clock_in_test';

	} else {
		popupRecords = 'popup_records';
		config = 'function_config';
		memberListRecord = 'member_list';
		payedClockIn = 'payed_clock_in';

	}
	console.log("数据库", popupRecords)
	//初始获取不再提醒弹窗记录
	if (event.functionName == 'getRecord') {
		console.log("进入初始记录")
		let record = {}
		let code = 0
		console.log("jinru", event.unionId)
		await db.collection(popupRecords).where({
				unionId: event.unionId,
			})
			.get().then(res => {
				if (res.data.length > 0) {
					record = res.data[0]
				} else {
					code = 10
				}
			})
		return {
			record: record,
			code: code
		}

	}
	if (event.functionName == 'add') {
		//新增一条记录
		console.log("进入增加记录,状态", event)
		await db.collection(popupRecords).add({
			data: {
				user_id: event.userId,
				unionId: event.unionId,
				nickName: event.nickName,
				create_time: new Date(),
				update_time: new Date(),
			},
		}).then(res => {
			console.log('不在提醒弹窗记录创建成功')
		})
		return event
	}
	//修改更新时间
	if (event.functionName == 'updateTime') {
		console.log("进入更新时间修改")
		await db.collection(popupRecords).where({
				unionId: event.unionId,
			})
			.update({
				data: {
					update_time: new Date(),
				},
			}).then(res => {
				console.log("更新时间修改成功", res.errMsg)

			})
		return event

	}
	//更新云数据库的群成员列表
	if (event.functionName == 'updateMemberList') {
		//获取数据库配置表信息
		console.log("更新云数据库的群成员列表", event)
		let configInfo = {};
		let groupList = [];
		await db.collection(config).where({
				school_id: event.schoolId,
				function_name: 'judgeShowPopup'
			})
			.get().then(res => {
				if (res.data.length > 0) {
					configInfo = res.data[0]
				}
			})
		console.log("第一步", configInfo)
		//根据前端传来的标志，组成groupList
		if (event.groupKind == "all") {
			groupList.push(...configInfo.notice_group_config.groupList)
		} else if (event.groupKind == "notice") {
			groupList.push(...configInfo.notice_group_config.groupList)
		}
		//获取access_token
		let data = await getAccessToken()
		let accessToken = JSON.parse(data).access_token
		let memberList = []
		console.log("第二步", accessToken)
		//获取成员列表
		for (var i = 0; i < groupList.length; i++) {
			let tempList = await getMemberList(groupList[i], accessToken)
			// console.log(tempList)
			memberList.push(...tempList)
		}
		console.log("第三步", memberList.length)
		//更新成员列表
		let message = '';
		await db.collection(memberListRecord).where({
				kind: event.groupKind,
				school_id:event.schoolId,
			})
			.update({
				data: {
					member_list: memberList,
					update_time: new Date()
				},
			}).then(res => {
				console.log("更新列表成功", res.errMsg)
				message = res.errMsg

			})
		return {
			message
		}
	}
	// 判断是否在群聊里
	if (event.functionName == 'judgeShowPopup') {
		let configInfo = {};
		let code = 0;
		let hasNoNoticeRecord = 0;
		await db.collection(config).where({
				school_id: event.schoolId,
				function_name: event.functionName
			})
			.get().then(res => {
				if (res.data.length > 0) {
					configInfo = res.data[0]
				}
			})
		console.log("第一步", configInfo)
		//判断配置中弹窗是否开启
		if (event.groupKind == "all" && configInfo.all_group_state == 0) {
			return {
				configInfo: configInfo,
				code: code,
				hasNoNoticeRecord: hasNoNoticeRecord,
			}
		}
		if (event.groupKind == "notice" && configInfo.notice_group_config.state == 0) {
			return {
				configInfo: configInfo,
				code: code,
				hasNoNoticeRecord: hasNoNoticeRecord,
			}
		}

		//是否参加了打卡活动
		await db.collection(payedClockIn).where({
				unionId: event.unionId,
			})
			.get().then(res => {
				if (res.data.length > 0) {
					code = 30 //参与了打卡活动
					console.log("第二步",res.data)
				} else {
					console.log("第二步：参与打卡活动")
				}
			})
		if(code==30){
			return {
				configInfo: configInfo,
				code: code,
				hasNoNoticeRecord: hasNoNoticeRecord,
			}
		}
		//是否在一天内点击过不再提醒按钮
		await db.collection(popupRecords).where({
				unionId: event.unionId,
			})
			.get().then(res => {
				if (res.data.length > 0) {
					hasNoNoticeRecord =1
					let record = res.data[0]
					console.log("第三步",record)
					let updateTime = new Date(record.update_time);
					let updateTimestemp = updateTime.getTime()
					let nowTime = new Date().getTime()
					let timeDifference = nowTime - updateTimestemp
					let noNoticeTime = 86400000 * 1
					if (timeDifference < noNoticeTime) {
						code = 40 //有点记录，24小时内点过
					} else {
					}
				} else {
					hasNoNoticeRecord =0
				}
			})
		if(code==40){
			return {
				configInfo: configInfo,
				code: code,
				hasNoNoticeRecord: hasNoNoticeRecord,
			}
		}
		let memberList = []
		await db.collection(memberListRecord).where({
				kind: event.groupKind,
				school_id:event.schoolId
			})
			.get().then(res => {
				if (res.data.length > 0) {
					memberList = res.data[0].member_list
				}
			})
		console.log("第四步", memberList.length)
		for (let i = 0; i < memberList.length; i++) {
			console.log(event.unionId, memberList[i].unionid)
			if (event.unionId == memberList[i].unionid) {
				code = 10 //已在群
				break;
			} else if (i == memberList.length - 1) {
				code = 20 //未在群，需要弹窗
			} else {
				continue;
			}
		}
		return {
			configInfo: configInfo,
			code: code,
			hasNoNoticeRecord: hasNoNoticeRecord,
		}

	}
	//获取access_token
	async function getAccessToken() {
		let formData = {
			corpid: 'ww45debfa9e67919a9',
			corpsecret: 'HV8c5mIX2TN3_yGNDiWkoJLkDpqqNNBvY_VUDasv00g'
		}
		return new Promise((resolve, reject) => {
			request({
				url: 'https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=' + formData
					.corpid + '&corpsecret=' + formData.corpsecret,
				method: "GET",
			}, function(error, response, body) {
				// console.log(response)
				// console.log(error)
				// console.log(body)
				resolve(body)
				if (response.statusCode == 200 && body.code == 200) {} else {
					reject()
				}
			})
		})
	}
	//获取单个群成员列表
	async function getMemberList(groupChatId, accessToken) {
		let formData = {
			"chat_id": groupChatId
		}
		return new Promise((resolve, reject) => {
			request({
				url: 'https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get?access_token=' +
					accessToken,
				method: "POST",
				json: true,
				body: formData,
				headers: {
					"content-type": "application/json",
				},
			}, function(error, response, body) {
				// console.log(response)
				//console.log(error)
				console.log("单个群成功", body.group_chat.member_list.length)
				resolve(body.group_chat.member_list)
				if (response.statusCode == 200 && body.code == 200) {} else {
					reject()
				}
			})
		})
	}

}
