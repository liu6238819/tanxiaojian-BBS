export default {
	list: [{
			"pagePath": "/pages/index/index",
			"iconPath": "/static/newUI/home.png",
			"selectedIconPath": "/static/newUI/home-fill.png",
			// "selectedIconPath": "/static/newUI/home-blue.png",
			"text": "主页"
		},
		{
			"pagePath": "/pages/post/history-post",
			"iconPath": "/static/newUI/tab2.png",
			"selectedIconPath": "/static/newUI/tab2-fill.png",
			// "selectedIconPath": "/static/newUI/tab2-blue.png",
			"text": "历史热帖"
		},
		{
			"pagePath": "/pages/post/add",
			iconPath: '/static/newUI/add.png',
			selectedIconPath: '/static/newUI/add.png',
			midButton: true,
			isCustom: true
		},
		{
			"pagePath": "/pages/message/message",
			"iconPath": "/static/newUI/message.png",
			"selectedIconPath": "/static/newUI/message-fill.png",
			// "selectedIconPath": "/static/newUI/message-blue.png",
			"text": "消息"
		},
		{
			"pagePath": "/pages/mine/mine",
			"iconPath": "/static/newUI/mine.png",
			"selectedIconPath": "/static/newUI/mine-fill.png",
			// "selectedIconPath": "/static/newUI/mine-blue.png",
			"text": "我的"
		},
	],
	popup: [{
			icon: '/static/h_2.png',
			text: '分享吐槽',
			url: '/pages/post/add?contentType=5'
		},
		{
			icon: '/static/topic.png',
			text: '求助',
			url: '/pages/post/add?contentType=6'
		},
		{
			icon: '/static/group.png',
			text: '组队扩列',
			url: '/pages/post/add?contentType=7'
		},
		{
			icon: '/static/images/icon/jfdh.png',
			text: '二手兼职',
			url: '/pages/post/add?contentType=8'
		},
		{
			icon: '/static/h_1.png',
			text: '投票',
			url: '/pages/vote/vote'
		}
	],
	popup2: [{
			icon: '/static/h_2.png',
			text: '帖子',
			url: '/pages/post/add?contentType=0'
		},
		{
			icon: '/static/h_3.png',
			text: '视频',
			url: '/pages/post/add?contentType=2'
		},
		{
			icon: '/static/topic.png',
			text: '坦言',
			url: '/pages/post/add?contentType=3'
		},
		{
			icon: '/static/h_1.png',
			text: '投票',
			url: '/pages/vote/vote'
		}

	]
}
