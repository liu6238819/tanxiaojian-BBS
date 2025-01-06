import {
	reqEditUserInfos,
	reqpublishContent,
	reqContentList,
	userLogin
} from "../api/index.js"

export default {
	namespaced: true,
	state: {
		currentContent: {}, //当前需要展示详情的帖子对象
		homeContentList: [], //首页帖子列表
		imageBase64List: [], //临时图片base64
		homeTopContentList: []
	},
	actions: {},
	mutations: {
		getContentDetail(state, value) {
			state.currentContent = value
		},
		editUpNumAndIsLike(state, value) {
			// console.log('mutations',value);
			state.currentContent.upNum += value[0]
			state.currentContent.isLike = value[1]
		},
		setHomeContentList(state, value) {
			state.homeContentList = value
		},
		addHomeContentList(state, value) {
			state.homeContentList.push(...value)
		},
		setAdmireState(state, value) {
			for (var i = 0; i < state.homeContentList.length; i++) {
				if (state.homeContentList[i].contentId == value.contentId) {
					state.homeContentList[i].isLike = value.isLike
					if (value.isLike == 1) {
						state.homeContentList[i].upNum += 1
					} else {
						state.homeContentList[i].upNum -= 1
					}
				}
			}
		},
		setMarkState(state, value) {
			for (var i = 0; i < state.homeContentList.length; i++) {
				if (state.homeContentList[i].contentId == value.contentId) {
					state.homeContentList[i].isMark = value.isMark
					if (value.isMark == 1) {
						state.homeContentList[i].markCount += 1
					} else {
						state.homeContentList[i].markCount -= 1
					}
					if (value.delNickName) {
						state.homeContentList[i].markNickName = null
					}
				}
			}
		},
		setShowImageState(state, value) {
			for (var i = 0; i < state.homeContentList.length; i++) {
				if (state.homeContentList[i].contentId == value.contentId) {
					state.homeContentList[i].showImage = 1
				}
			}
		},

		//先放除图片外的其他信息，再放图片
		addHomeContentText(state, value) {
			state.homeContentList.push(...value)
			//第二步，检测图片信息与帖子对应关系，，有，放置图片
			let standard = 0
			for (var i = 0; i < state.imageBase64List.length; i++) {
				for (var j = state.homeContentList.length - 1; j >= 0; j--) {
					if (state.homeContentList[j].contentId == state.imageBase64List[i].contentId) {
						state.homeContentList[j].contentBase64 = state.imageBase64List[i].contentBase64
						standard += 1
					}
				}
			}
			//填完后，清空
			console.log('填了几项（+字）',standard)
			if (standard == state.imageBase64List.length) {
				state.imageBase64List = []
			}

		},
		//暂存图片位置
		addImageBase64List(state, value) {
			state.imageBase64List.push(...value)
			let standard = 0
			//检测图片信息与帖子对应关系，有，放置图片
			for (var i = 0; i < state.imageBase64List.length; i++) {
				for (var j = state.homeContentList.length - 1; j >= 0; j--) {
					if (state.homeContentList[j].contentId == state.imageBase64List[i].contentId) {
						state.homeContentList[j].contentBase64 = state.imageBase64List[i].contentBase64
						standard += 1
					}
				}
			}
			//填完后，刷新homeContentList
			// console.log('填了几项(+图)',standard)
			let tempList = state.homeContentList
			state.homeContentList = []
			state.homeContentList.push(...tempList)
			// if (standard == state.imageBase64List.length) {
			// 	state.imageBase64List=[]
			// }
		},
		//清空图片位置
		clearImageBase64List(state) {
			state.imageBase64List = []
		},
		//更新帖子的发帖人头像和图片
		setHomeContentImage(state, value) {
			for (var i = 0; i < state.homeContentList.length; i++) {
				if (state.homeContentList[i].contentId == value.contentId) {
					state.homeContentList[i].headimgUrl = value.headimgUrl
					state.homeContentList[i].contentUrls = value.contentUrls
				}
			}
		},
		//投票成功后改变DoVote和对应相ticketNum的值
		setContentDoVote(state, value) {
			for (var i = 0; i < state.homeContentList.length; i++) {
				if (state.homeContentList[i].contentId == value.contentId) {
					state.homeContentList[i].doVote = true
					for (var j = 0; j < state.homeContentList[i].votes.length; j++) {
						console.log(value.votes[j], j)
						if (value.votes[j].checked) {
							// state.homeContentList[i].votes[j].checked=true
							state.homeContentList[i].votes[j].ticketNum += 1
						}
					}
				}
			}
		},
		setCurrentContentState(state, value) {
			state.currentContent.contentState = value
		},
		setHomeTopContentList(state, value) {
			state.homeTopContentList = value
		},
		unshiftHomeContentList(state, value) {
			if (value.length > 0) {
				for (let i = 0; i < value.length; i++) {
					let exists = state.homeContentList.some(item => item.contentId === value[i].contentId);
					if (!exists) {
						state.homeContentList.unshift(value[i]);
					}
				}
			} else {
				// 删除 state.homeContentList 中 contentState 为 2 的元素
				state.homeContentList = state.homeContentList.filter(item => item.contentState !== 2);
			}
			// state.homeContentList.unshift(...value);
		},
		addHomeContentAndFilter(state, value){
			console.log("开始",new Date().getTime())
			const contentIdsToCheck = state.homeContentList.slice(-5).map(item => item.contentId);
			const itemsToAdd = value.filter(item => !contentIdsToCheck.includes(item.contentId));
			state.homeContentList.push(...itemsToAdd);
			console.log("结束",new Date().getTime())
		}



	}
}