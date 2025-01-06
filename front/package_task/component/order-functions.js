import Vue from "vue"
import moment from 'moment'
import localData from '../../utils/data.js'
import {
	cancelOrder,
	appealOrder,
	applyOrder,
	confirmOrder,
	createOrder,
	finishOrder,
	selectApplicant,
	getOrderDetail,
} from '../../api/index.js'

var vm = new Vue()

const add = function(a) {
	return 1 + a
	console.log('内部', a)
}
//更改前端订单状态
const changeState = function(isCopmonent, order, that, stateNum) {
	if (isCopmonent == 1) {
		for (var i = 0; i < that.orders.length; i++) {
			if (order.orderId == that.orders[i].orderId) {
				 // that.orders[i].orderState= stateNum
				let confirmCountdown=86400
				that.$emit("callback", that.orders[i].orderId, stateNum,confirmCountdown)
			}
		}
	} else {
		order.orderState = stateNum
		if(stateNum==2){
			that.confirmCountdown=86400
		}
	}
}
const changeUserKind = function(isCopmonent, order, that) {
	console.log('触发')
	if (isCopmonent == 1) {
		for (var i = 0; i < that.orders.length; i++) {
			if (order.orderId == that.orders[i].orderId) {
				 // that.orders[i].orderState= stateNum
				that.$emit("callback", that.orders[i].orderId, 2,-100)
			}
		}
	} else {
		that.userKind=2
	}
}

const apply = async function(order, inschoolPlateId, unschoolPlateId, that, isCopmonent) {
	let applyForm = {
		applicantId: that.userInfos.userId,
		orderId: order.orderId
	}

	let permissionCheckResult = await vm.permissionCheckOutBBS(applyForm.applicantId,
		that.currentSchoolId, 1)
	if (permissionCheckResult != 0) {
		return
	}
	let res = await applyOrder(applyForm)
	if (res.code == 200) {
		// order.orderState = 1
		if (order.orderKind > 9) {
			changeState(isCopmonent, order, that, 1)
		}
		else{
			console.log('触发')
			changeUserKind(isCopmonent, order, that)
		}
		console.log(order)
		uni.showToast({
			title: '订单接取成功',
			icon: "none"
		});
		localData.requestMessage([1,2,5])
		// #ifdef MP-WEIXIN
		vm.sendRemind(order)
		// #endif
		setTimeout(function() {}, 1000)
	} else {
		uni.showToast({
			title: res.message,
			icon: "none"
		})
		setTimeout(function() {
			uni.reLaunch({
				url: "../center/index",
			})
		}, 1000)

	}
}

const appeal = async function(appealForm, that, isCopmonent) {
	if (appealForm.publisherId == '' || appealForm.publisherId == null) {
		uni.showToast({
			title: '当前登录用户状态异常！',
			icon: 'none'
		})
		return
	}
	if (appealForm.appealReason == '') {
		uni.showToast({
			title: '请输入申诉原因！',
			icon: 'none'
		})
		return
	}
	let res = await appealOrder(appealForm)
	if (res.code == 200) {
		that.modalName = ''

		if (isCopmonent == 1) {
			for (var i = 0; i < that.orders.length; i++) {
				if (appealForm.orderId == that.orders[i].orderId) {
					// that.orders[i].orderState=6
					that.$emit("callback", that.orders[i].orderId, 6)
				}
			}
		} else {
			that.orderDetail.orderState = 6
		}

		uni.showToast({
			title: "申诉成功，管理员将进行核实",
			icon: "none"
		})
		setTimeout(() => {

		}, 500)
	} else {
		uni.showToast({
			title: "申诉失败",
			icon: "none"
		})
	}


}
const cancel = async function(order, that, isCopmonent) {
	let cancelForm = {
		publisherId: that.userInfos.userId,
		orderId: order.orderId
	}
	let res = await cancelOrder(cancelForm)
	if (res.code == 200) {
		// order.orderState = 5
		changeState(isCopmonent, order, that, 5)
		console.log(order)
		uni.showToast({
			title: '订单撤销成功',
			icon: "none"
		});
		setTimeout(function() {}, 1000)
	} else {
		uni.showToast({
			title: res.message,
			icon: "none"
		})

	}
}

const confirm = async function(order, that, isCopmonent) {
	let confirmForm = {
		publisherId: that.userInfos.userId,
		orderId: order.orderId
	}
	let res = await confirmOrder(confirmForm)
	if (res.code == 200) {
		// order.orderState = 2
		changeState(isCopmonent, order, that, 2)
		console.log(order)
		uni.showToast({
			title: '订单确认成功',
			icon: "none"
		});
		// #ifdef MP-WEIXIN
		vm.sendFinishRemind(order, 0)
		// #endif
		setTimeout(function() {}, 1000)
	} else {
		uni.showToast({
			title: res.message,
			icon: "none"
		})

	}
}

const finish = async function(order, that, isCopmonent) {
	//订单确认接口用
	let finishForm = {
		publisherId: that.userInfos.userId,
		orderId: order.orderId
	}
	//获取接取者openid用
	let orderForm= {
		orderId: order.orderId,
		userId: that.userInfos.userId,
	}
	//给接取者发送信息用
	let sendForm={
		orderKind:order.orderKind,
		openId:'',
	}
	let res = await finishOrder(finishForm)
	if (res.code == 200) {
		// order.orderState = 4
		changeState(isCopmonent, order, that, 4)
		console.log(order)
		uni.showToast({
			title: '订单确认成功',
			icon: "none"
		});
		let data= await getOrderDetail(orderForm)
			if (data.code === 200) {
				console.log('总返回',data)
				for (var i = 0; i < data.data.applicantRecord.length; i++) {
					if(data.data.applicantRecord[i].order_applicant.applicantState==1){
						sendForm.openId=data.data.applicantRecord[i].applicantInfo.openId
						// #ifdef MP-WEIXIN
						vm.sendFinishRemind(sendForm,1)
						// #endif
					}
					else{
						
					}
				}
		}
		setTimeout(function() {}, 1000)
	} else {
		uni.showToast({
			title: res.message,
			icon: "none"
		})

	}
}
const message = function() {
	wx.requestSubscribeMessage({
		tmplIds: [
			'qKU3vN8oOr8sYte2RgDAtpXJJicnIL6R0d9O9_Hvi24'
		],
		success(res) {
			console.log('发送消息许可调用成功')
		}
	})
	console.log(1)
}
//声明函数，可以被引用
module.exports = {
	add,
	appeal,
	cancel,
	confirm,
	finish,
	apply
}
