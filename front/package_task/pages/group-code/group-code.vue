<template>
	<view class="content">
		<image :show-menu-by-longpress="true" :src="qrCode" mode="widthFix" :data-current="qrCode"
			:data-image="qrCodeList">
		</image>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import localData from '../../../utils/data.js';
	import {
		getOneBbsConfig,
		getUserStateBySchool
	} from '@/api/index.js'
	export default {
		data() {
			return {
				qrCode: '', //显示二维码url
				qrCodeList: [], //显示二维码列表 预览用
				groupCodeList:[],
				currentQRCodeId:'',
			}
		},
		computed: {
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
			...mapState('user', ['currentSchoolId']),
		},
		async onLoad(options) {
			console.log(options)
			this.currentQRCodeId=options.QRCodeId
			this.getQRCodeConfig(options.schoolId)
			//如果携带的学校ID和缓存中的学校ID不一致
			if (options.schoolId || options.schoolId * 1 != uni.getStorageSync('schoolId')) {
				console.log("重新加载配置项等内容")
				//修改store与本vue里的变量
				this.getCurrentSchoolId(options.schoolId * 1);
				//修改storage里的变量
				uni.setStorageSync("schoolId", this.currentSchoolId * 1);
				//按学校id更新启动项配置
				await this.getSchoolFunctionConfig()
				//按学校id更新基础配置
				await this.getSchoolBasicConfig()
				//更新用户在该学校的状态
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					//this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
				}
			}
			
		},
		methods: {
			...mapMutations('user', {
				getCurrentSchoolId: 'getCurrentSchoolId'
			}),
			...mapMutations('user', {
				setCurrentUserState: 'setCurrentUserState'
			}),
			async getQRCodeConfig(schoolId) {
				let that = this
				//获取启动项配置
				let param = {
					schoolId: schoolId,
					configType: 'QRCodeConfig'
				}
				let data = await getOneBbsConfig(param)
				if (data.code == 200) {
					let configList = JSON.parse(data.data.configJson).data
					console.log("二维码配置项", configList)
					if (configList.length > 0) {
						for (let i = 0; i < configList.length; i++) {
							if (configList[i].QRCodeId==this.currentQRCodeId) {
								this.groupCodeList=configList[i].groupCodeList
							} 
						}
						if (this.groupCodeList.length>0) {
							let randomInt = Math.floor(Math.random() * this.groupCodeList.length);
							this.qrCode=this.groupCodeList[randomInt]
							this.qrCodeList.push(this.groupCodeList[randomInt])
						}
						
					}
				} else {
					console.log("二维码配置获取失败");
				}			
			},
			previewImage(e) {
				uni.previewImage({
					current: e.currentTarget.dataset.current, // 当前显示图片的http链接
					urls: e.currentTarget.dataset.image // 需要预览的图片http链接列表
				});
			},

		},
	}
</script>

<style lang="scss" scoped>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-top: 50rpx;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
		width: 80%;
	}

	.title {
		font-size: 36rpx;
		color: #ff0000;
		font-weight: bold;
		text-align: center;
	}
</style>
