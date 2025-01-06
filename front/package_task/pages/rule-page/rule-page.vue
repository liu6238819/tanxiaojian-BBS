<template>
	<view>
		<view v-if="type==1">
			<rich-text class="post-text" :nodes="content"></rich-text>
		</view>
		<view v-if="type==2">
			<web-view :src="url"></web-view>
		</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations,
	} from 'vuex'
	import {
		getOneBbsConfig,
	} from '@/api/index.js'
	export default {

		data() {
			return {
				url: '', // 设置要显示的网页地址
				textPageList: [],
				textpageConfig: '',
				content: '',
				type: 0,
				title:'',
			}
		},
		computed: {
			...mapState('user', ['currentSchoolId']),
		},
		async onLoad(options) {
			let that = this
			console.log(options)
			await this.getTextPageList(options.index)
			uni.setNavigationBarTitle({
				title: this.title
			});
		},
		methods: {
			async getTextPageList(index) {
				//获取文字页配置
				let param = {
					schoolId: this.currentSchoolId,
					configType: 'textPageList'
				}
				console.log(this.currentSchoolId)
				let data = await getOneBbsConfig(param)
				if (data.code == 200) {
					this.textPageList = JSON.parse(data.data.configJson).data
					console.log("文字页项", this.textPageList)
					if (this.textPageList.length > 0) {
						for (let i = 0; i < this.textPageList.length; i++) {
							if (this.textPageList[i].index == index) {
								this.content = this.textPageList[i].content
								this.type = this.textPageList[i].type
								this.url = this.textPageList[i].url
								this.title = this.textPageList[i].contentName
								break
							}
						}

					}
				} else {
					console.log("启动项配置获取失败");
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.post-text {
		white-space: pre-wrap;
		word-break: break-all;
		word-wrap: break-word;
	}
</style>
