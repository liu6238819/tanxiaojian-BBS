// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
	env: cloud.DYNAMIC_CURRENT_ENV,
	// traceUser: true
})

const db = cloud.database()
const $ = db.command.aggregate
const _ = db.command

// 云函数入口函数
exports.main = async (event, context) => {
	//数据库选择初始化
	let examineQuestions = '';
	let userAnswerRecord = '';
	if (event.usingDatabase == 0) {
		examineQuestions = 'examine_questions_test';
		userAnswerRecord = 'user_answer_record_test';
		config = 'function_config_test';
	} else {
		examineQuestions = 'examine_questions';
		userAnswerRecord = 'user_answer_record';
		config = 'function_config';
	}
	console.log("数据库", examineQuestions, userAnswerRecord)

	//初始获取openCount
	if (event.functionName == 'getQuestions') {
		console.log("进入获取题目")
		let questionList = []; //问题列表
		let answerList = []; //答案列表
		let tempList = []; //临时问题列表
		await db.collection(examineQuestions).where({
				school_id: event.schoolId,
				state: 1
			})
			.get().then(res => {
				if (res.data.length > 0) {
					tempList = res.data
				}
			})
		//打乱顺序
		for (var i = tempList.length - 1; i > 0; i--) {
			let index = Math.floor(Math.random() * i); //交换的索引
			[tempList[i], tempList[index]] = [tempList[index], tempList[i]]
		}
		console.log("打乱后数组", tempList)
		for (var i = 0; i < 3; i++) {
			questionList.push(tempList[i])
			answerList.push(tempList[i].answer)
		}
		return {
			questionList,
			answerList
		}
	}
	
	if (event.functionName == 'isHaveChance') {
		console.log("判断用户是否还有机会")
		let userAnswerCount =0
		await db.collection(userAnswerRecord).where({
				school_id: event.schoolId,
				user_id:event.userId,
			})
			.get().then(res => {
				if (res.data.length > 0) {
					userAnswerCount = res.data[0].answer_count
				}else{
					userAnswerCount =0
				}
			})
		return userAnswerCount
	}

	if (event.functionName == 'useAnswerChance') {
		console.log("用户提交答案")
		let answerRecord=''
		let code =0
		let userAnswerCount =0
		await db.collection(userAnswerRecord).where({
				school_id: event.schoolId,
				user_id:event.userId,
			})
			.get().then(res => {
				if (res.data.length > 0) {
					answerRecord = res.data[0]
					code=1
				}else{
					
				}
			})
		if (code==0) {
			await db.collection(userAnswerRecord).add({
				data: {
					user_id: event.userId,
					school_id: event.schoolId,
					answer_count: 1,
					create_time: new Date(),
					update_time: new Date(),
				},
			}).then(res => {
				console.log('新增：新增一个用户认证记录')
				userAnswerCount =1
			})
		}
		else{
			userAnswerCount =answerRecord.answer_count+1
			await db.collection(userAnswerRecord).doc(answerRecord._id).update({
				data: {
					answer_count: userAnswerCount,
					update_time: new Date(),
				},
			}).then(res => {
				console.log("修改：用户认证次数+1", res.errMsg)
			
			})
		}
		return userAnswerCount
	}

}
