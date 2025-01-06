// // 统一管理所有请求接口
import request,{localRequest} from "./request.js";

// 用户注册

export const userRegister = (params) => request.post('/user/userRegisterApi',{},{params}) 


// 用户登录
export const userLogin = (params) => request.post('/user/userLoginApi',{},{params}) 
// 修改用户信息
export const reqEditUserInfos = (params) => request.post('/user/editUserInfosApi',{},{params})
// 微信用户登录
export const userLoginWX = (params) => request.post('/user/userLoginWX',{},{params}) 
// 校友身份认证
export const userIdentification = (params) => request.post('/user/identification',{},{params}) 
// 关注或取消关注用户
export const followUser = (params) => request.post('/user/followUser',{},{params}) 
// 通过searchId（必填）查询用户信息，并返回用户与当前用户之间的关注关系
export const getUserInfoById = (params) => request.post('/user/getUserInfoById',{},{params}) 
// 答题身份认证成功
export const answerIdentification = (params) => request.post('/user/answerIdentification',{},{params}) 



// 我的页面
// 获取我赞的帖子
export const reqLikedContents = (params) => request.get('/user/getLikedContents',{params}) 
// 获取我关注的用户、关注我的用户列表
export const reqFollowedUsers = (params) => request.get('/user/getFollowedUsers',{params}) 
// 获取用户认证状态
export const getUserStateBySchool = (params) => request.get('/user/getUserStateBySchool',{params})



// 发布帖子
export const reqPublishContent = (params) => request.post('/content/publishContentApi',{},{params}) 
// 获取首页的帖子内容列表
export const reqContentList = (params) => request.get('/content/getHomeContentsApi',{params}) 
//用户投票
export const doVote = (params) => request.post('/content/doVoteApi',{},{params})

// 发布评论
export const reqPublishComment = (params) => request.post('/comment/publishCommentApi',{},{params})

export const reqPublishCommentUseBody =(data) =>request({
		url:'/comment/publishCommentApiUseBody',
		data,
		method:'post'
	})

// 获取评论列表
export const reqHomeComments = (params) => request.get('/comment/getHomeCommentsApi',{params})
// 获取带高亮的评论列表
export const reqHomeCommentsWithHighLight = (params) => request.get('/comment/getHomeCommentsWithHighLight',{params})
// 获取子评论列表
export const reqChildrenCommentPage = (params) => request.get('/comment/getChildrenCommentPage',{params})

// 获取首页时光机或往日精选的帖子内容列表
export const reqHistoryPostList = (params) => request.get('/content/getHistoryContentsApi',{params}) 



// 点赞或取消点赞
export const reqEditLikeNum = (params) => request.post('/admire/editLikeNumApi',{},{params})
// export const reqBaseCategoryList = () => request.get('/product/getBaseCategoryList')
// export const reqSearchList = (data) => request.post('/list', data)
// export const reqGoodDetail = (goodId) => request.get(`/list/${goodId}`)
// // 直接向mock请求数据
// export const mockReqBanList = () => mockRequest.get('/banner')
// export const mockReqFloorList = () => mockRequest.get('/floor')

// 创建板块
export const createPlate = (params) => request.post('/plate/createPlate',{},{params:params})
//获取板块类目
export const getClassLists = (params) => request.get('/plate/getClassLists',{params:params});
//根据学校id,获取数据库中相应的板块
export const getPlateLists = (params) => request.get('/plate/getPlateLists',{params:params});
//根据板块id,获取板块信息
export const getPlateInfoById = (params) => request.get('/plate/getPlateInfoById',{params:params})
//用户点击关注
export const joinPlate = (params) => request.post('/plate/joinPlate',{},{params:params})
//用户取消关注
export const leavePlate = (params) => request.post('/plate/leavePlate',{},{params:params})
//用户解散板块
export const deletePlate = (params) => request.post('/plate/deletePlate',{},{params:params})
// 获取关注板块
export const getFocusedPlates = (params) => request.post('/plate/getFocusedPlates',{},{params:params})

//获取小程序token
export const getAccessTokenWX = (params) => request.get('/commonAPIs/getAccessTokenWX',{params:params})
//获取小程序用户openID和session
export const code2SessionWX = (params) => request.get('/commonAPIs/code2SessionWX',{params:params})

//获取学校列表
export const getSchoolLists = (params) => request.get('/content/getSchoolList',{params:params})

//搜索学校列表
export const searchSchoolLists = (params) => request.get('/content/searchSchoolList',{params:params})

//根据关键字通用搜索
export const searchKeyWords = (params) => request.get('/commonAPIs/searchSimplified',{params:params})

//根据帖子id获取帖子内容
export const getContentById = (params) => request.get('/content/getContentById',{params:params})

//根据帖子id 用户id判断是否首次阅读
export const isFirstRead = (params) => request.post('/content/isFirstRead',{},{params:params})

//根据帖子id 用户id判断是否首次阅读
export const isFirstReadNew = (params) => request.post('/content/isFirstReadNew',{},{params:params})

// 消息页面
// 获取其他用户对帖子或评论的点赞评论公告
export const getReminds = (params) => request.get('/commonAPIs/getReminds',{params:params})
export const readRemind = (params) => request.get('/commonAPIs/readRemind',{params:params})
// 清空公告
export const clearRemind = (params) => request.get('/commonAPIs/clearRemind',{params:params})
// 修改帖子状态
export const editContentState = (params) => request.post('/contentManage/editContentInfo',{},{params}) 

// 修改评论状态
export const editCommentState = (params) => request.post('/commentManage/deleteComment',{},{params}) 

// 举报操作
export const informContent = (params) => request.post('/inform/informContent',{},{params}) 

// 举报操作(11.25新)
export const informContentMoreInfo = (params) => request.post('/inform/informContentMoreInfo',{},{params}) 

//举报操作，使用请求体
export const informContentMoreInfo0522 = (data) => request.post('/inform/informContentMoreInfo0522', data);

//任务平台页面
//申诉订单
// export const appealOrder = (params) => request.post('/order/appealOrder',{},{params}) 
export const appealOrder =(data) =>request({
		url:'/order/appealOrder',
		data,
		method:'post'
	})

//接取订单
export const applyOrder =(data) =>request({
		url:'/order/applyOrder',
		data,
		method:'post'
	})
//取消订单
export const cancelOrder =(data) =>request({
		url:'/order/cancelOrder',
		data,
		method:'post'
	})
//确认订单
export const confirmOrder =(data) =>request({
		url:'/order/confirmOrder',
		data,
		method:'post'
	})
//创建订单
export const createOrder =(data) =>request({
		url:'/order/createOrder',
		data,
		method:'post'
	})
//结束订单
export const finishOrder =(data) =>request({
		url:'/order/finishOrder',
		data,
		method:'post'
	})
//获取订单详情
export const getOrderDetail = (params) => request.get('/order/getOrderDetail',{params:params})
//获取订单列表
export const getOrderList = (params) => request.get('/order/getOrderList',{params:params})
//获取用户接取的订单列表
export const getAppliedOrderList = (params) => request.get('/order/getAppliedOrderList',{params:params})
//选择接取人
// export const selectApplicant = (params) => request.post('/order/selectApplicant',{},{params}) 
export const selectApplicant =(data) =>request({
		url:'/order/selectApplicant',
		data,
		method:'post'
	})
//获取收益排行旁
export const getProfitRanking = (params) => request.get('/order/getProfitRanking',{params:params})
//获取实时的用户收益统计
export const getUserProfitRealTime = (params) => request.get('/order/getUserProfitRealTime',{params:params})
//申请提现
export const applyCashOut =(data) =>request({
		url:'/order/applyCashOut',
		data,
		method:'post'
	})
//支付成功
export const payedorder =(data) =>request({
		url:'/order/payedOrder',
		data,
		method:'post'
	})
	
//报名相关
//增加报名信息
export const addSignUpInfo =(data) =>request({
		url:'/signUp/addSignUpInfo',
		data,
		method:'post'
	})
	
//修改报名信息
export const updateUserSignUpInfo =(data) =>request({
		url:'/signUp/updateSignUpInfo',
		data,
		method:'post'
	})
//获取活动列表
export const getActivityList = (params) => request.get('/signUp/getActivityList',{params:params})
//获取报名信息
export const getSignUpInfo = (params) => request.get('/signUp/getSignUpInfo',{params:params})
//修改积分
// export const changeUserScore =(data) =>request({
// 		url:'/score/changeUserScore',
// 		data,
// 		method:'post'
// 	})
export const changeUserScore = (params) => request.post('/score/changeUserScore',{},{params}) 
//获取活动已购买数量
export const getSignUpNum = (params) => request.get('/signUp/getSignUpNum',{params:params})

//面对面认证
export const offlineIdentification = (params) => request.get('/commonAPIs/offlineIdentification',{params:params})

//积分相关
//获取今日交互行为数量
export const getActionNumToday = (params) => request.get('/score/getActionNumToday',{params:params})
//获取当前积分
export const getUserScore = (params) => request.get('/score/getUserScore',{params:params})
//兑换物品
export const redeemScore =(data) =>request({
		url:'/score/redeemScore',
		data,
		method:'post'
	})

//报名活动拥有者相关
export const getActivityOwnerList = (params) => request.get('/activityOwner/getActivityOwnerList',{params:params})

// 马住或取消马住
export const editMark = (params) => request.post('/remindWSS/editMark',{},{params})
// 马住消息已读
export const editMarkRead = (params) => request.post('/remindWSS/editMarkReadState',{},{params})

// 马住或取消马住
export const editMark_1 = (params) => request.post('/remindWSS/editMark_1',{},{params})
// 马住消息已读
export const editMarkRead_1 = (params) => request.post('/remindWSS/editMarkReadState_1',{},{params})

//获取mark列表
export const getMarkMessageList_1 = (params) => request.get('/remindWSS/getMarkMessageList_1',{params}) 

//对贴子相关的通知的已读状态进行编辑
export const editRemindByContentId = (params) => request.post('/remindWSS/editRemindsByContentId',{},{params})

//获取广告列表
export const getAdvList = (params) => request.get('/advertisement/getAdvList',{params}) 
//获取广告
export const getOneAdvById = (params) => request.get('/advertisement/getOneAdvById',{params}) 
// 创建广告报名
export const createAdvSignUp =(data) =>request({
		url:'/advertisement/createAdvSignUp',
		data,
		method:'post'
	})
//获取报名信息
export const getAdvSignUp = (params) => request.get('/advertisement/getAdvSignUp',{params:params})

//获取论坛配置项
export const getOneBbsConfig = (params) => request.get('/commonAPIs/getOneBbsConfig',{params:params})

//搜索相似帖子搜索
export const getSimilarContent = (params) => request.get('/commonAPIs/NLPContentText',{params:params})

//判断弹窗弹出
export const needShowPopup = (params) => request.get('/commonAPIs/needShowPopup',{params:params})

//新增不再提醒记录
export const addNoRemindRecord = (params) => request.get('/commonAPIs/addNoRemindRecord',{params:params})


//根据帖子id获取图片base64
export const getImageBase64ByContents = (params) => request.get('/content/getImageBase64ByContent',{params:params})

//获取首页帖子图片base64
export const getHomeContentsImage = (params) => request.get('/content/getHomeContentsImage',{params:params})

//获取广告列表(图片为base64)
export const getAdvListWithImageBase64 = (params) => request.get('/advertisement/getAdvListWithImageBase64',{params}) 

//判断是否举报过该帖子
export const ifAlreadyInformThisTarget = (params) => request.get('/inform/ifAlreadyInformThisTarget',{params:params}) 

//判获取论坛配置项，不带schoolId
export const getOneBbsConfigWithoutSchoolId = (params) => request.get('/commonAPIs/getOneBbsConfigWithoutSchoolId',{params:params})


// 获取其他用户对帖子或评论的点赞评论公告
export const getRemindsDistinguishSchoolId = (params) => request.get('/commonAPIs/getRemindsDistinguishSchoolId',{params:params})


// 解除禁言状态
export const bannedTimeEnd = (params) => request.get('/OperationRecord/isReachBannedTime',{params:params})

// 新增操作记录
export const addOperationRecord = (params) => request.post('/OperationRecord/addOperationRecord',{},{params})

// 新增操作记录0619,使用请求体
export const addOperationRecord0619 = (data) => request.post('/OperationRecord/addOperationRecord0619', data);

 //获取当前积分和匿名发帖状态
export const getScoreAndAnonymousState = (params) => request.get('/score/getUserScoreAndAnonymousState',{params:params})

//获取【先审后发】配置状态
export const getStateOfCheckBeforePublish = (params) => request.get('/commonAPIs/getConfigOfCheckBeforePublish',{params:params})

//按帖子类型搜索帖子
export const searchContentsByContentType = (params) => request.get('/content/searchContentsByContentType',{params:params})

//获取用户是否存在其他学校已认证记录
export const haveIdentifiedInOtherSchool = (params) => request.get('/user/haveIdentifiedInOtherSchool',{params:params})

//新增用户登录记录
export const addUserLoginRecord = (params) => request.post('/user/insertUserLoginRecord',{},{params:params})

//获取广告列表
export const getAdvList0507 = (params) => request.get('/advertisement/getAdvList0507',{params}) 

// 拉黑操作
export const addBlockRecord = (params) => request.post('/blockRecord/addBlockRecord',{},{params}) 

// 修改拉黑状态
export const editBlockRecord = (params) => request.post('/blockRecord/editBlockRecord',{},{params}) 

// 获取拉黑记录
export const getBlockRecordList = (params) => request.get('/blockRecord/getBlockRecordList',{params}) 

// 判断是否被查询用户拉黑
export const judgeHaveBeBlocked = (params) => request.post('/blockRecord/judgeHaveBeBlocked',{},{params}) 

// 修改拉黑状态
export const editBlockRecordByUserId = (params) => request.post('/blockRecord/editBlockRecordByUserId',{},{params}) 

// 管理员修改用户信息
export const manageUserInfosApi = (params) => request.post('/user/manageUserInfosApi',{},{params})

// 管理员修改用户信息
export const manageContentInfo = (data) => request.post('/contentManage/manageContentInfo', data);

//管理员获取评论信息
export const manageGetCommentById = (params) => request.get('/commentManage/manageGetCommentById',{params}) 

// 修改评论状态
export const manageCommentState = (params) => request.post('/commentManage/manageComment',{},{params}) 

// 获取某用户所有帖子列表
export const reqAllContentList = (params) => request.get('/content/getAllHomeContentsApi',{params}) 

// 获取禁言记录列表
export const selectBannedList = (params) => request.get('/OperationRecord/selectBannedList',{params:params})

// 创建广告报名
export const createMultiBusinessAdvSignUp =(data) =>request({
		url:'/advertisement/createMultiBusinessAdvSignUp',
		data,
		method:'post'
	})
	
// 用户注销账号
export const userLogOut = (params) => request.post('/user/userLogOut',{},{params})


// 在变更学校时获取用户在对应学校下的状态
export const getUserStateWhenChangeSchool = (params) => request.get('/user/getUserStateWhenChangeSchool',{params})



//跨库登录认证相关
//（本地方法）
//获取小程序用户openID和session
export const code2SessionWXlocal = (params) => localRequest.get('/commonAPIs/code2SessionWX',{params:params})
// 微信用户登录
export const userLoginWXlocal = (params) => localRequest.post('/login/userLocalLoginWX',{},{params}) 
// 修改用户信息
export const editLocalUserInfos = (params) => localRequest.post('/user/editUserInfosApi',{},{params})


//搜索相似帖子搜索
export const getAISelectContent = (params) => request.get('/AISearch/AISelectContent',{params:params})


export const searchFocusContentRecords = (params) => request.get('/remindWSS/searchFocusContentRecords',{params}) 

export const getFocusContentList = (params) => request.get('/remindWSS/getFocusContentList',{params}) 

export const updateFocusSearchTime = (params) => request.get('/remindWSS/updateFocusSearchTime',{params}) 



