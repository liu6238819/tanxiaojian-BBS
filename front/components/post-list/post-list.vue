<template>
	<view>
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>

		<block v-if="content.contentState!=3&&content.contentState!=8" v-for="(content, index) in contentList"
			:key="content.contentId">
			<view @longpress="dealAction(content)">
				<view class="post-item" :class="{'first-item-radius': index === 0 ,'other-item-radius': index != 0}"
					style="border-radius: 0; margin-bottom: 0;">
					<view class="post-item-top-user" @click="jump(content)">
						<view style="display: flex;align-items: center;">
							<!-- <u-avatar class="avatar" :src="content.headimgUrl" level-bg-color="#8072f3"></u-avatar> -->
							<image
								style="width: 80rpx; height: 80rpx; float: left; margin-right: 15rpx;border-radius: 50%;"
								:src="content.headimgUrl" webp="true"></image>
						</view>
						<view class="center">
							<view
								style="display: flex;align-items: center; justify-content: space-between; padding-left: 5rpx;">
								<!-- <text class="official">官方</text> -->
								<view style="display: flex;align-items: center;">
									<text class="username">{{content.nickName}}</text>
									<text
										v-if="content.userIdentify==10 &&content.isSpecial != 1 && content.isSpecial != 4"
										class="cu-tag light bg-blue sm" style="margin-left: 10rpx ;">官方</text>
								</view>
								<view style="color: rgba(51,51,51,0.6); font-size: 24rpx;">
									<text v-if="content.contentState!=2"
										class="time">{{Date.parse(content.createTime)| timeFrom }}</text>
									<text v-if="content.contentState==2 " class="cu-tag lg"
										style="margin-left: 10rpx ; 
									background-color: #0081ff; color: #fff; font-weight: bold; font-size: 22rpx; border-radius: 30rpx; padding: 0upx 20rpx; height: 40rpx;">置顶</text>
								</view>
								</view>
							<view style="display: flex;">
								<!-- 话题 GPT 帖子标签 -->
								<text v-if="content.downNum==1" class="discuss-title2" @tap.stop="toDiscuss">@GPT
								</text>
								<view class="type-icon2"
									v-if="showTag&&content.contentState!=2&&content.contentState!=3&&content.plateName!='校园广场'">
									<text style="font-size: 22rpx;"
										v-if="showTag&&content.contentState!=2&&content.contentState!=3&&content.plateName!='校园广场'">#{{ content.plateName }}
									</text>
								</view>
								<view class="type-icon"
									v-if="showTag&&content.contentState!=3">
									<image style="height: 24rpx; width: 24rpx; margin-right: 5rpx;"
										src="../../static/newUI/type4.png" v-if="content.contentType==1"></image>
									<image style="height: 24rpx; width: 24rpx; margin-right: 5rpx;"
										src="../../static/newUI/type1.png" v-if="content.contentType==5"></image>
									<image style="height: 24rpx; width: 24rpx; margin-right: 5rpx;"
										src="../../static/newUI/type2.png" v-if="content.contentType==6"></image>
									<image style="height: 24rpx; width: 24rpx; margin-right: 5rpx;"
										src="../../static/newUI/type3.png" v-if="content.contentType==7"></image>
									<image style="height: 24rpx; width: 24rpx; margin-right: 5rpx;"
										src="../../static/newUI/type5.png" v-if="content.contentType==8"></image>
									<text v-if="showTag&&content.contentState!=3"
										style="font-size: 22rpx;">
										{{ content.contentTypeLabel }}
									</text>
								</view>
								<view class="type-icon2"
									v-if="content.contentState==3 ||content.contentState==8||content.contentState==11|| ((content.contentType==3 ||content.isSpecial==1) && content.contentState!=11)">
									<!-- <text v-if="content.contentState==2" style="font-size: 22rpx;">#校内置顶</text> -->
									<text v-if="content.contentState==3" style="font-size: 22rpx;">#话题置顶</text>
									<text v-if="content.contentState==8" style="font-size: 22rpx;">#官方通告</text>
									<text v-if="content.contentState==11" style="font-size: 22rpx;">#往日精选</text>
									<text
										v-if="(content.contentType==3 ||content.isSpecial==1) && content.contentState!=11"
										style="font-size: 22rpx;">#坦言</text>
								</view>
							</view>

						</view>
					</view>

					<block v-if="content.alumniOnly && content.alumniOnly==1 && currentUserState!=2 ">
						<view class="post-content" @click="jump(content)"
							style="display: flex;align-items: center;margin-bottom: 20rpx;">
							<u-icon name="lock-fill" size="30" color="#333333"></u-icon>
							<text class="post-title" style="color:#333333 ; margin-left: 5rpx; letter-spacing: 1rpx;">
								作者已设置仅对校园认证用户展示
							</text>
						</view>
					</block>
					<block v-if="!content.alumniOnly || content.alumniOnly!=1 || currentUserState==2 ">
						<view class="post-content" @click="jump(content)">
							<rich-text v-if="content.title" class="post-title" :style="{textAlign:'left'}"
								:nodes="content.title">
							</rich-text>
							<rich-text v-if="content.contentText&&content.contentType!=4&&content.isSpecial!=2"
								class="post-text" :style="{textAlign:'left'}" :nodes="content.contentText">
							</rich-text>
							<rich-text v-if="content.contentType==4 ||content.isSpecial==2" class="post-text"
								:style="{textAlign:'left'}" :nodes="'[1]. '+content.schoolInfos[0].intro">
							</rich-text>
							<block
								v-if="firstPage.show_image_in_post_list==0 &&content.showImage==0 && content.contentUrls &&content.contentUrls.length>0">
								<view @tap.stop="showPostImage(content)"
									style="font-size: 28rpx; color:#2979ff; font-weight:bold">
									<u-icon name="photo" size="36" color="#2979ff" top="2"></u-icon>
									查看{{content.contentUrls.length}}张图片
								</view>
							</block>
							<block
								v-if="(firstPage.show_image_in_post_list==1 || (firstPage.show_image_in_post_list==0 &&content.showImage==1)) && content.contentState!=2">
								<block v-if="(content.contentType == 0||content.contentType == 3 || (content.contentType > 4 && content.contentType < 9) )
								&& ( content.isSpecial== 0 || content.isSpecial== 1)">
									<!--一张图片-->
									<block v-if="content.contentUrls.length == 1 && firstPage.image_use_base64!=1">
										<image :lazy-load="true" mode="aspectFill" class="img-style-1" webp="true"
											:src="content.contentUrls[0]"
											@tap.stop="previewImageByPlatform(content, 0)">
										</image>
									</block>
									<block
										v-if="content.contentBase64 && content.contentBase64.length == 1 && firstPage.image_use_base64==1">
										<image :lazy-load="true" mode="aspectFill" class="img-style-1" webp="true"
											:src="content.contentBase64[0]"
											@tap.stop="previewImage(content.contentBase64[0], content.contentBase64)">
										</image>
									</block>
									<!--二张图片-->
									<block v-if="content.contentUrls.length == 2 && firstPage.image_use_base64!=1">
										<view class="img-style-2">
											<image :lazy-load="true" v-for="(mediaItem, index2) in content.contentUrls"
												:key="index2" @tap.stop="previewImageByPlatform(content, index2)"
												mode="aspectFill" :src="mediaItem" webp="true"></image>
										</view>
									</block>
									<block
										v-if="content.contentBase64&& content.contentBase64.length == 2 && firstPage.image_use_base64==1">
										<view class="img-style-2">
											<image :lazy-load="true"
												v-for="(mediaItem, index2) in content.contentBase64" :key="index2"
												@tap.stop="previewImage(mediaItem, content.contentBase64)"
												mode="aspectFill" :src="mediaItem" webp="true"></image>
										</view>
									</block>
									<!--三张以上图片-->
									<block
										v-if="content.contentUrls.length > 2 &&content.contentUrls.length != 4 && firstPage.image_use_base64!=1">
										<view class="img-style-3">
											<image :lazy-load="true" v-for="(mediaItem, index2) in content.contentUrls"
												:key="index2" @tap.stop="previewImageByPlatform(content, index2)"
												mode="aspectFill" :src="mediaItem" webp="true"></image>
										</view>
									</block>
									<block
										v-if="content.contentBase64 && content.contentBase64.length > 2 &&content.contentBase64.length != 4 && firstPage.image_use_base64==1">
										<view class="img-style-3">
											<image :lazy-load="true"
												v-for="(mediaItem, index2) in content.contentBase64" :key="index2"
												@tap.stop="previewImage(mediaItem, content.contentBase64)"
												mode="aspectFill" :src="mediaItem" webp="true"></image>
										</view>
									</block>
									<!--四张图片-->
									<block v-if="content.contentUrls.length == 4 && firstPage.image_use_base64!=1">
										<view class="img-style-4">
											<image :lazy-load="true" v-for="(mediaItem, index2) in content.contentUrls"
												:key="index2" @tap.stop="previewImageByPlatform(content, index2)"
												mode="aspectFill" :src="mediaItem" webp="true"></image>
										</view>
									</block>
									<block
										v-if="content.contentBase64 && content.contentBase64.length == 4 && firstPage.image_use_base64==1">
										<view class="img-style-4">
											<image :lazy-load="true"
												v-for="(mediaItem, index2) in content.contentBase64" :key="index2"
												@tap.stop="previewImage(mediaItem, content.contentBase64)"
												mode="aspectFill" :src="mediaItem" webp="true"></image>
										</view>
									</block>
								</block>
							</block>

							<!--视频-->
							<!-- #ifdef MP-WEIXIN -->
							<block
								v-if="(content.contentType === 2 ||content.isSpecial==3 ||content.isSpecial==4) && content.videoId">
								<view style="width: 100%; text-align: center;">
									<channel-video style="width: 100%;" :feed-id="content.videoId"
										finder-user-name="sphg37vWTgqo6yZ"></channel-video>
								</view>
							</block>
							<!-- #endif -->
						</view>
						<view v-if="content.contentType === 1 && content.votes" class="vote-box" @click="jump(content)">
							<!-- <view class="title">{{content.contentText}}</view> -->
							<view class="expire-time">
								截止：{{content.votes[0].deadline | date('yyyy年mm月dd日hh时MM分')}}
							</view>
							<view class="vote-item" v-for="(item2,index2) in content.votes" :key="index2">
								{{item2.optionText}}<text
									style="color: #999;margin-left: 20rpx;">({{ item2.ticketNum }}票)</text>
							</view>
						</view>
						<view class="p-footer" style="justify-content: space-between; align-items: center;">
							<view style="display: flex; justify-content: space-between; width: 45%;">

								<view v-show="content.isMark==1" class="p-item" @click="changeMark(0,content)">
									<image src="/static/mark-fill.png" style="width: 35rpx; height: 35rpx;">
									</image>
									<text class="count " style="color: #4BAAFC; font-weight: bold;">弃坑</text>
								</view>
								<view v-if="content.isMark!=1" class="p-item" @click="changeMark(1,content)">
									<image src="/static/mark.png" style="width: 35rpx; height: 35rpx;"> </image>
									<text class="count">蹲一下</text>
								</view>

							</view>
							<view class="p-item margin50"
								style="width: 60%; display: flex; justify-content: space-between; ">
								<view class="p-item" v-if="content.place&&content.place !=''&&content.place !=null "
									@click="copyContactInfo(content)">
									<image src="/static/newUI/weixin-fill.png" style="width: 37rpx; height: 37rpx;">
									</image>
									<text class="count " style="color: #4BAAFC; font-weight: bold;">联系</text>
								</view>
								<view class="p-item" v-if="!content.place||content.place ==''&&content.place ==null ">
									<image src="/static/newUI/weixin.png" style="width: 37rpx; height: 37rpx;"> </image>
									<text class="count">联系</text>
								</view>
								<view v-show="content.isLike" class="p-item" @click="changeLike(0,content)">
									<u-icon size="35" name="thumb-up-fill" color="#4BAAFC"></u-icon>
									<text v-if="content.upNum>0" class="count">{{ content.upNum }}</text>
								</view>
								<view v-show="!content.isLike" class="p-item" @click="changeLike(1,content)">
									<u-icon size="35" name="thumb-up" style="color: rgba(51, 51, 51, 0.6);"></u-icon>
									<text v-if="content.upNum>0" class="count">{{ content.upNum }}</text>
								</view>


								<view class="p-item" @click="jump(content)">
									<u-icon size="35" name="edit-pen" style="color: rgba(51, 51, 51, 0.6); "></u-icon>
									<text style="margin-left: 10rpx; color: #616161; font-size: 26rpx; ">评论
										<text v-if="content.commentNum>0" style=" margin-left: 5rpx;">(</text>
										<text v-if="content.commentNum>0" style="">{{content.commentNum}}</text>
										<text v-if="content.commentNum>0" style="">)</text>
									</text>

								</view>
							</view>

						</view>
						<view style="display: flex; align-items: center; font-size: 24rpx;
						 font-weight:bold; margin-bottom: 20rpx;" v-if="content.markCount>0">
							<u-icon name="bell-fill"></u-icon>
							<text v-if="content.markNickName ==null"
								style=" margin: 0 10rpx; letter-spacing:5rpx">{{content.markCount}}人在蹲</text>
							<view v-if="content.markNickName !=null">
								<text style=" margin: 0 10rpx;">{{content.markNickName}}</text>
								<text v-if="content.markCount!=1" style="letter-spacing:5rpx;">
									等{{content.markCount}}人在蹲</text>
								<text v-if="content.markCount==1" style="letter-spacing:5rpx">在蹲</text>
							</view>
						</view>
						<view v-if="content.hotComments && content.hotComments.length>0" class="hot-comment-part"
							@click="jump(content)">
							<view class="hot-comment" v-for="comment in content.hotComments">
								<text style="color: #999999;">{{comment.nickName}} ：</text>
								<text style="color: #666666;">{{comment.commentText}}</text>
							</view>
						</view>
					</block>
				</view>
				<view class="partition" style="height: 16rpx; background-color: #F8F8F8 ; width: 100%;"></view>
			</view>
		</block>
		<!-- 操作弹窗 -->
		<root-portal>
			<u-action-sheet :list="actionList" v-model="showAction" :closeOnClickOverlay="true"
				@close="showAction = false" :cancel-btn="true" @click="onAction"
				:safe-area-inset-bottom="true"></u-action-sheet>
		</root-portal>
		<!-- 加载状态 -->
		<block v-if="contents.length === 0&&loadStatus!=null&&loadStatus=='nomore'">
			<u-empty margin-top="100" text="暂无相关帖子" mode="favor"></u-empty>
		</block>
		<block v-else-if="loadStatus == 'loadmore'||loadStatus == 'nomore'">
			<view style="margin: 30rpx 0;">
				<u-loadmore :status="loadStatus" :load-text="loadText" @loadmore='loadMore()' />
			</view>
		</block>
		<!-- 举报modal框 -->
		<root-portal>
			<view class="cu-modal show" style="z-index:100000" v-if="modalName =='DialogModal'">
				<view class="cu-dialog modal-content">
					<view class="cu-bar bg-white lg justify-end">
						<view class="content">请完善举报信息</view>
						<view class="action" @tap="hideModal()">
							<text class="cuIcon-close text-red"></text>
						</view>
					</view>
					<view class="informContainer margin-top">
						<view style="font-weight: bold;margin-bottom: 10rpx;">请选择举报原因</view>
						<radio-group @change="radioChange" :wrap="true" style="display: flex; flex-wrap:wrap;">
							<label v-for="(item,index) in informConfig.inform_reasons" :key="index">
								<view class="complaint-item">
									<radio style="transform:scale(0.8);height: 100%;" class="radio" :value="item"
										color="#5543ef" :checked="index == 0"></radio>
									<text>{{item}}</text>
								</view>
							</label>
						</radio-group>
					</view>
					<view class="informText margin-top">
						<u-input type="textarea" class="informTextArea" :clearable="false" maxlength="-1"
							v-model="detailInformInfo" placeholder="请输入详细原因(选填)" />
					</view>
					<view class="submit-btn">
						<q-button color="#000" style="color: #fff;" @click="onSubmit">提交</q-button>
					</view>
				</view>

			</view>
		</root-portal>
		<root-portal>
			<view class="cu-modal show" v-if="modalName =='showContactInfo'">
				<view class="cu-dialog modal-content">
					<view class="cu-bar bg-white lg justify-end"
						style="min-height: 10rpx;text-align: end; padding-right: 15rpx;">
						<view class="content text-bold">查看校友联系方式</view>
						<view class="action" @tap="hideModal()">
							<text class="cuIcon-close text-red text-bold"></text>
						</view>
					</view>
					<view style="display: flex; align-items: center; 
				 background-color: #fff; padding-bottom: 20rpx;">
						<text
							style="width: 80%; font-weight: bold; text-align: left; padding-left: 30rpx;">联系方式：{{currentContent.place}}</text>
						<button class="cu-btn" style="height: 50rpx; background: rgba(75, 170, 252, 0.1);
					color: #4BAAFC; font-weight: bold;" @click="onCopy(currentContent.place)">
							复制</button>
					</view>
					<view
						style="text-align: center display: block; padding-top:10rpx ;background-color: #fff; padding-bottom: 15rpx;">
						<button @click="hideModal()" class="cu-btn bg-black"
							style=" width: 30%; height: 50rpx; ">确定</button>
					</view>
				</view>

			</view>
		</root-portal>
		<root-portal>
			<view v-if="showExaminePopup==1">
				<examine-popup v-if="currentSchoolState!=3" @returnHid='returnHide'></examine-popup>
			</view>
		</root-portal>
		<root-portal>
			<view v-if="showManageModal==1">
				<manage-modal :manageKind="manageKind" :manageUserId='manageUserId' :manageContent="manageContent"
					@returnHideManage="returnHideManage"></manage-modal>
			</view>
		</root-portal>
	</view>
	</view>
</template>

<script>
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		editContentState,
		informContent,
		reqEditLikeNum,
		getUserScore,
		getActionNumToday,
		editMark_1,
		informContentMoreInfo,
		ifAlreadyInformThisTarget,
		addOperationRecord,
		informContentMoreInfo0522
	} from "../../api/index.js"
	import localData from '../../utils/data.js';
	export default {
		name: 'post-list',
		props: {
			contents: Array,
			loadStatus: String,
			handle: {
				default: false,
				type: Boolean
			},
			uid: Number,
			admin: {
				default: false,
				type: Boolean
			},
			showTag: {
				default: true,
				type: Boolean
			},
			needJump: Number,
		},
		data() {
			return {
				modalName: '', //是否展示举报模态框
				informList: [
					"辱骂攻击",
					"色情低俗",
					"不良广告",
					"政治相关",
					"泄露他人隐私",
				],
				informInfo: '',
				detailInformInfo: '',
				showAction: false,
				actionList: [],
				sessionUid: '',
				currentContentId: '', //记录当前操作的帖子id
				currentContent: '', //记录当前操作的帖子信息
				isAlumnus: 1, //判断是否为校友
				// 显示的文字
				loadText: {
					loadmore: '点击加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				chatGPTState: 0,
				nowScore: 0,
				actionToday: '',
				actionTodayForm: {
					userId: '',
				},
				showExaminePopup: 0,
				tempInformUrls: [],
				header: {
					token: uni.getStorageSync('token')
				},
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
				modifiedContents: this.contents, // 存储props中的值
				//用户、帖子管理
				showManageModal: 0,
				manageKind: 0,
				manageUserId: '',
				manageContent: '',
			};
		},
		watch: {
			currentContent() {
				if (this.currentContent.userId == this.userInfos.userId) {
					if (this.currentContent.contentState != 6) {
						this.actionList = [{
								text: '设为私密',
								key: 'private'
							},
							{
								text: '删除',
								color: 'red',
								key: "delete"
							}
						]
					} else {
						this.actionList = [{
								text: '设为公开',
								key: 'public'
							},
							{
								text: '删除',
								color: 'red',
								key: "delete"
							}
						]
					}

				} else {
					if (this.currentUserType == 3) {
						this.actionList = [{
								text: '举报',
								color: 'red',
								key: 'inform'
							},
							{
								text: '拉黑TA',
								color: 'black',
								key: 'block'
							},
							{
								text: '帖子管理',
								color: 'black',
								key: 'contentManage'
							}
						]
					} else if (this.currentContent.userIdentify != 10 && this.currentContent.isSpecial!=1 && this.currentContent.isSpecial!=4) {
						this.actionList = [{
								text: '举报',
								color: 'red',
								key: 'inform'
							},
							{
								text: '拉黑TA',
								color: 'black',
								key: 'block'
							}
						]
					} else {
						this.actionList = [{
							text: '举报',
							color: 'red',
							key: 'inform'
						}]
					}
				}
			},

		},
		computed: {
			...mapState('user', ['userInfos', 'currentUserState', 'currentUserType', 'currentSchoolState', 'isLocalUser']),
			...mapState('user', ['currentSchoolId']),
			...mapState('config', ['privacyPopup', 'firstPage', 'informConfig']),
			contentList() {
				const newContents = this.contents.map(item => {
					//图片处理
					if (item.contentUrl !== null && item.contentUrl !== undefined && item.contentUrl !== '' && !
						item.contentUrls) {
						item['contentUrls'] = item.contentUrl.split(',');
						if ((item.contentType == 2 || item.isSpecial == 3 || item.isSpecial == 4) && item
							.contentUrls.length > 1) {
							item.videoId = item.contentUrls[item.contentUrls.length - 1]
						}
					}
					//已经对帖子图片处理过，什么都不做
					else if (item.contentUrl !== null && item.contentUrl !== undefined && item.contentUrl !== '' &&
						item.contentUrls) {

					} else {
						item['contentUrls'] = [];
					}
					// 帖子类型标签处理
					//帖子类型标签
					for (let k = 0; k < localData.contentTypeList.length; k++) {
						if (item.contentType <= 5 && localData.contentTypeList[k].type == 5) {
							item.contentTypeLabel = localData.contentTypeList[k].name
							break;
						} else if (item.contentType == localData.contentTypeList[k].type) {
							item.contentTypeLabel = localData.contentTypeList[k].name
							break;
						} else {
							continue;
						}
					}
					return item
				})
				// console.log(newContents)
				return newContents
			}
		},
		mounted() {
			// this.aboutfunctionConfig("chatGPT")
		},
		methods: {
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
				setAdmireState: 'setAdmireState',
				setMarkState: 'setMarkState',
				setShowImageState: 'setShowImageState'

			}),
			...mapMutations('remindWSS', {
				addPostToMarkMessageList: 'addPostToMarkMessageList',
				delPostInMarkMessageList: 'delPostInMarkMessageList',
			}),
			async changeLike(isLike, content) {
				if (this.currentSchoolState == 3 && this.isLocalUser == 1) {
					console.log(this.isLocalUser, this.currentSchoolState)
					uni.showToast({
						icon: 'none',
						title: '请认证后查看'
					})
					this.showExaminePopup = 1
					return
				}
				uni.showLoading({
					title: '点赞中...',
					mask: true,
					//duration: 1000
				})
				//查询是否被拉黑
				if (isLike == 1) {
					let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, content.userId)
					if (blockedState == 1) {
						uni.showToast({
							title: '您已被该用户拉黑',
							icon: 'none'
						})
						return
					}
				}
				if (this.userInfos.userId !== '') {
					const params = {
						isContent: 1, //帖子
						isLike: isLike, //1点赞 0取消点赞
						targetId: content.contentId, //34,//'65310f343fdb431d866f991d9ade98fb',
						userId: this.userInfos.userId //'6c109fe4bcdb4b718d94a687ce8a1be6'
					}
					const data = await reqEditLikeNum(params)
					setTimeout(function() {
						uni.hideLoading()
					}, 500)
					if (data.code === 200) {
						console.log("点赞", data.data)
						this.$emit('editLike', {
							"isLike": isLike,
							"contentId": content.contentId,
						});
						console.log("中间")
						this.setAdmireState({
							'contentId': content.contentId,
							'isLike': isLike
						})
						if (isLike == 1) {
							// content.isLike = 1
							// content.upNum += 1 //前端更新点赞数和是否已点赞
							if (data.data.admireType == 1) {
								await this.showScoreTosat(1);
							}
						} else {
							// content.isLike = 0
							// content.upNum -= 1 //前端更新点赞数和是否已点赞
						}
					} else {
						console.log('点赞失败');
					}
				} else {
					uni.showToast({
						title: '请登录后点赞！'
					})
				}
			},
			previewImage(url, urls) {
				console.log(url)
				uni.previewImage({
					current: url, // 当前显示图片的http链接
					urls: urls // 需要预览的图片http链接列表
				});
			},
			previewImageByPlatform(content, index) {
				// console.log(uni.getSystemInfoSync().platform,index)
				let url = content.contentUrls[index];
				let urls = content.contentUrls;
				if (uni.getSystemInfoSync().platform == "windows" || uni.getSystemInfoSync().platform == "mac") {
					url = content.contentTempUrls[index];
					urls = content.contentTempUrls;
				}
				uni.previewImage({
					current: url, // 当前显示图片的http链接
					urls: urls // 需要预览的图片http链接列表
				});
			},
			async jump(content) {
				if (content.alumniOnly && content.alumniOnly == 1 && this.currentUserState != 2) {
					//先进行隐私授权判断
					// console.log(this.privacyPopup.needAuthorization)
					if (this.privacyPopup.needAuthorization) {
						let needPrivacyAuthorization = await this.judgePrivacySetting()
						// console.log(needPrivacyAuthorization)
						if (needPrivacyAuthorization == true) {
							return
						}
					} else {
						console.log("已授权，不需要调用")
					}
					let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
						.currentSchoolId,
						1)
					if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
						return
					}
					if (permissionCheckResult != 0) {
						this.showExaminePopup = 1
						return
					}
				}
				console.log(content.contentText);
				this.getContentDetail(content) //修改了store中的数据
				let url = ''
				// if (content.contentType == 1) {
				// 	url = '/pages/post/detail?id=' + content.contentId
				// } else {
				// 	url = '/pages/post/detail'
				// }
				url = '/pages/post/detail?id=' + content.contentId
				uni.navigateTo({
					url
				});
			},
			loadMore() {
				if (this.loadStatus == 'loadmore') this.$emit('loadmore');
			},
			hideModal() {
				this.modalName = ''
			},
			async onSubmit() {
				if (this.userInfos.userId == '' || this.userInfos.userId == null) {
					uni.showToast({
						title: '当前登录用户状态异常！',
						icon: 'none'
					})
					return
				}
				const oosImgUrl = []
				const {
					tempInformUrls
				} = this
				console.log(tempInformUrls);
				for (let i = 0, length = tempInformUrls.length; i < length; i++) {
					uni.showLoading({
						mask: true,
						title: '上传第' + (i + 1) + '/' + length + '张...'
					});
					//微信官方压缩方法(二次压缩)
					const compressResult = await localData.compressionIamgeWXTwice(this, tempInformUrls[i], 1000);
					console.log(compressResult)
					//上传
					let imgUrl = await this.uploadImageOSS(compressResult, 'content/contentImg/', this
						.currentSchoolId)
					//上传图片
					imgUrl = imgUrl.replace('keming-bbs.oss-cn-shanghai.aliyuncs.com',
						'image.tanxiaojian.zone')
					oosImgUrl.push(imgUrl)
					console.log(oosImgUrl);
				}
				//目标帖子内容
				let contentText = ''
				if (this.currentContent.title) {
					contentText = (this.currentContent.title + this.currentContent.contentText).slice(0, 100)
				} else {
					contentText = this.currentContent.contentText.slice(0, 100)
				}
				let informForm = {
					userId: this.userInfos.userId,
					informedUserId: this.currentContent.userId,
					contentId: this.currentContentId,
					targetId: this.currentContentId,
					targetText: contentText,
					informInfo: this.informInfo + " " + this.detailInformInfo,
					informUrl: oosImgUrl.join(','),
					isContent: 1,
					schoolId: this.currentSchoolId
				}
				let res = await informContentMoreInfo0522(informForm)
				if (res.code == 200) {
					uni.showToast({
						title: "举报成功，管理员将进行核实",
						icon: "none"
					})
					setTimeout(() => {
						this.modalName = ''
					}, 500)
				}
			},
			radioChange(e) {
				this.informInfo = e.target.value;
			},
			dealAction(content) {
				console.log('当前帖子：' + content.contentId)
				this.currentContentId = content.contentId
				this.currentContent = content
				this.showAction = true;
			},
			async onAction(index) {
				console.log(`点击内容为：${this.actionList[index].key}`)
				let key = this.actionList[index].key;
				let title = ''
				let changeState = 0
				let changeForm = {
					userId: this.userInfos.userId,
					contentId: this.currentContentId,
					contentState: 0
				}
				if (key == 'inform') {
					//先进行隐私授权判断
					if (this.privacyPopup.needAuthorization) {
						let needPrivacyAuthorization = await this.judgePrivacySetting()
						// console.log(needPrivacyAuthorization)
						if (needPrivacyAuthorization == true) {
							return
						}
					} else {
						console.log("已授权，不需要调用")
					}
					let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
						.currentSchoolId,
						1)
					if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
						return
					}
					if (permissionCheckResult != 0) {
						this.showExaminePopup = 1
						return
					}
					let params = {
						userId: this.userInfos.userId,
						schoolId: this.currentSchoolId,
						targetId: this.currentContentId
					}
					console.log(params)
					const data = await ifAlreadyInformThisTarget(params)
					if (data.code == 200) {
						if (data.data == 1) {
							uni.showToast({
								title: "您已举报过该帖子，无需重复举报",
								icon: "none"
							})
							return
						}
					}
					this.informInfo = this.informConfig.inform_reasons[0]
					this.detailInformInfo = ''
					this.modalName = 'DialogModal'
					return
				}
				if (key == 'block') {
					let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
						.currentSchoolId,
						1)
					if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
						return
					}
					if (permissionCheckResult != 0) {
						this.showExaminePopup = 1
						return
					}
					this.addBlockRecordByUser(this.userInfos.userId, this.currentContent.userId);
					return
				}
				if (key == 'private') {
					title = '确认设为私密吗？'
					changeForm.contentState = 6
				} else if (key == 'public') {
					title = '确认设为公开吗？'
					changeForm.contentState = 0
				} else if (key == 'delete') {
					title = '确认删除吗？'
					changeForm.contentState = 7
				}
				//帖子管理
				else if (key == 'contentManage') {
					this.showManageModal = 1
					this.manageKind = 1 //帖子管理
					this.manageUserId = this.currentContent.userId
					this.manageContent = this.currentContent
					return
				}
				let that = this
				uni.showModal({
					title: title,
					success: async function(res) {
						if (res.confirm) {
							//console.log('用户点击确定');
							const data = await editContentState(changeForm);
							if (data.code === 200) {
								uni.showToast({
									title: '操作成功！',
									icon: 'none'
								})
								that.createOperationRecord(2, changeForm)
								//删除后跳转
								if (that.needJump == 1) {
									setTimeout(function() {
										uni.reLaunch({
											url: '../../pages/index/index'
										})
									}, 500)
								}
								//删除后不跳转,过滤被删除的帖子
								else if (key == 'delete') {
									that.$emit('editDeletePost', {
										"contentId": that.currentContentId,
									});
								}

							} else {
								uni.showToast({
									title: '操作失败，服务器异常！',
									icon: 'none'
								})
							}
						} else if (res.cancel) {
							//console.log('用户点击取消');
						}
					}
				})
			},
			//获取当前积分
			async getNowscore() {
				this.actionTodayForm.userId = this.userInfos.userId;
				const data = await getUserScore(this.actionTodayForm);
				if (data.code == 200) {
					console.log(data)
					this.nowScore = data.data.score
				}
			},
			//获取今日交互量
			async getActionToday() {
				this.actionTodayForm.userId = this.userInfos.userId;
				const data = await getActionNumToday(this.actionTodayForm);
				if (data.code == 200) {
					console.log(data)
					this.actionToday = data.data
				}
			},
			//展示积分增加提示框
			async showScoreTosat(index) {

				await this.getNowscore();
				await this.getActionToday();
				if (index == 1 && this.actionToday.AdmireNum <= 3) {
					uni.showToast({
						icon: 'none',
						title: '积分+5，现有积分' + this.nowScore,
						duration: 3000
					})
				} else if (index == 2 && this.actionToday.commentNum <= 3) {
					uni.showToast({
						icon: 'none',
						title: '积分+15，现有积分' + this.nowScore,
						duration: 3000
					})
				} else {

				}

			},
			//获取用户预留的联系方式（旧）
			async copyContactInfo(content) {
				uni.showLoading({
					title: '获取联系方式中...',
					mask: true,
				})
				//先进行隐私授权判断
				// console.log(this.privacyPopup.needAuthorization)
				if (this.privacyPopup.needAuthorization) {
					let needPrivacyAuthorization = await this.judgePrivacySetting()
					// console.log(needPrivacyAuthorization)
					if (needPrivacyAuthorization == true) {
						uni.hideLoading()
						return
					}
				} else {
					console.log("已授权，不需要调用")
				}
				let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId, this
					.currentSchoolId,
					1)
				if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
					uni.hideLoading()
					return
				}
				if (permissionCheckResult != 0) {
					uni.hideLoading()
					this.showExaminePopup = 1
					return
				}
				//查询是否被拉黑
				let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, content.userId)
				if (blockedState == 1) {
					uni.hideLoading()
					uni.showToast({
						title: '您已被该用户拉黑',
						icon: 'none'
					})
					return
				}
				this.currentContent = content
				uni.hideLoading()
				this.modalName = 'showContactInfo'

			},
			//复制
			onCopy(data) {
				uni.setClipboardData({
					data: data,
					success: function() {
						console.log('success');
					}
				});
			},
			//关闭认证modal
			returnHide(val) {
				this.showExaminePopup = val
				console.log(val)
			},
			returnHideManage(val) {
				this.showManageModal = false;
			},
			//用户点击马住和弃坑
			async changeMark(isMark, content) {
				if (this.currentSchoolState == 3 && this.isLocalUser == 1) {
					console.log(this.isLocalUser, this.currentSchoolState)
					uni.showToast({
						icon: 'none',
						title: '请认证后查看'
					})
					this.showExaminePopup = 1
					return
				}
				if (this.userInfos.userId !== '') {
					uni.showLoading({
						title: '操作中...',
						mask: true
					})
					if (isMark == 1) {
						//查询是否被拉黑
						let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, content.userId)
						if (blockedState == 1) {
							uni.showToast({
								title: '您已被该用户拉黑',
								icon: 'none'
							})
							return
						}
					}
					//马住时触发订阅消息
					if (isMark == 1) {
						localData.requestMessage([7, 9, 10])
					}
					const params = {
						isMark: isMark, //1马住 0弃坑
						contentId: content.contentId,
						userId: this.userInfos.userId
					}
					const data = await editMark_1(params)
					console.log(data)
					if (data.code === 200) {
						uni.hideLoading()
						console.log("马住", data.data)
						let delNickName = 0;
						if (content.markNickName == this.userInfos.nickName) {
							delNickName = 1
						}
						this.setMarkState({
							'contentId': content.contentId,
							'isMark': isMark,
							"delNickName": delNickName,
						})
						this.$emit('editMark', {
							"isMark": isMark,
							"contentId": content.contentId,
							"delNickName": delNickName,
						});
						//更改store中的mark,messagList
						if (isMark == 1) {
							console.log("添加", content)
							this.addPostToMarkMessageList(content)
						}
						if (isMark == 0) {
							console.log("删除", content)
							this.delPostInMarkMessageList(content)
						}

					} else {
						uni.hideLoading()
						console.log('马住失败');
					}
				} else {
					uni.showToast({
						title: '请登录后操作！'
					})
				}
			},
			showPostImage(content) {
				// content.showImage=1
				this.setShowImageState(content)
			},
			chooseImg(lists) {
				this.tempInformUrls = lists.map(item => item.url)
			},
			changeImg(lists) {
				this.tempInformUrls = lists.map(item => item.url)
			},
			async createOperationRecord(targetKind, changeForm) {
				console.log("触发")
				let operatinForm = {
					schoolId: this.currentSchoolId,
					recordState: 1,
					operator: this.userInfos.userId,
					targetKind: targetKind,
					targetId: this.currentContent.contentId,
					changeInfo: null,
					note: null,
				}
				//操作对象为帖子
				operatinForm.changeInfo = {
					data: {
						oldForm: {
							contentState: 0
						},
						newForm: {
							contentState: changeForm.contentState
						}
					}
				};
				//添加备注
				if (changeForm.contentState == 6) {
					operatinForm.note = "用户改帖子为私密"
				}
				if (changeForm.contentState == 7) {
					operatinForm.note = "用户改帖子为删除"
				}
				if (changeForm.contentState == 0) {
					operatinForm.note = "用户改帖子为公开"
					operatinForm.changeInfo.data.oldForm.contentState = 6
				}
				operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				console.log(operatinForm)
				const data = await addOperationRecord(operatinForm)
				if (data.code == 200) {
					console.log(data.data)
				}

			},
		}
	};
</script>

<style lang="scss" scoped>
	.topContent {
		background: #ffffff;
		padding-top: 10rpx;
		padding-left: 20rpx;
		padding-right: 20rpx;
		border: 1rpx #000;
		//border-bottom: 1px solid #eee;
		//margin-bottom: 20rpx;	
	}

	.topContent:last-of-type {
		padding-bottom: 10rpx;
		border-bottom: 1px solid #eee;
		//border-bottom: 0px solid #eee;
	}

	.first-item-radius {
		border-radius: 0 0 23rpx 23rpx;
	}

	.other-item-radius {
		border-radius: 23rpx;
	}


	.post-item {
		background: #ffffff;
		//background: #c7bdbd;
		//padding: 20rpx;
		padding-left: 20rpx;
		padding-right: 20rpx;
		padding-top: 10rpx;
		padding-bottom: 5rpx;
		margin-bottom: 16rpx;

		.post-item-title {
			font-size: 24rpx;
			color: #999;
		}

		.post-content {
			margin-top: 20rpx;

			.img-style-1 {
				display: block;
				width: 100%;
				max-height: 300rpx;
				border-radius: 5px;
				overflow: hidden;
			}

			.img-style-2 {
				display: flex;

				image {
					margin: 5rpx;
					border-radius: 5px;
					width: 100%;
					height: 200rpx;
				}
			}

			.img-style-3 {
				display: flex;
				flex-wrap: wrap;

				image {
					width: 31.3%;
					height: 200rpx;
					margin: 1%;
					border-radius: 5px;
				}
			}

			.img-style-4 {
				display: flex;
				flex-wrap: wrap;

				image {
					margin: 1%;
					border-radius: 5px;
					width: 48%;
					height: 200rpx;
				}
			}
		}

		.address {
			display: flex;
			font-size: 20rpx;
			background-color: #F5F5F5;
			border-radius: 20rpx;
			display: inline-block;
			padding: 5rpx 20rpx;
			margin: 20rpx 0;

			.icon {
				margin-right: 5rpx;
			}
		}
	}

	.post-item-top-user {
		display: flex;
		align-items: center;

		.avatar {
			width: 75rpx;
			height: 75rpx;
			// border-radius: 50%;
			margin-right: 30rpx;
		}

		.official {
			// display: inline-block;
			height: 30rpx;
			font-size: 26rpx;
			color: #ffffff;
			font-weight: bold;
			background-color: blue;
			padding: 10rpx;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}

		.official.red {
			//color: #ff0000;
			background-color: red;
		}

		.official.pick {
			// display: inline-block;
			height: 30rpx;
			font-size: 24rpx;
			color: #ff0000;
			font-weight: bold;
			background-color: #ffffff;
			padding: 10rpx;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}


		.anonymous {
			// display: inline-block;
			height: 30rpx;
			font-size: 24rpx;
			color: #ffffff;
			background-color: rgba(0, 0, 0, 0.6);

			// border-style: solid;
			// border-width: 1px;
			//border-color: #7f7f7f;
			padding: 10rpx;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}

		.center {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: 24rpx;
			color: #999;

			.username {
				font-size: 28rpx;
				font-weight: 600;
				// color: #666666;
				color: #000000;
			}

			.username1 {
				font-size: 32rpx;
				font-weight: 600;
				// color: #ffd764;
				// color: #edd443;
				// color: #f7ab43;
				color: blue;
			}

		}

		.right {
			height: 85rpx;

			.arrow-down {
				color: #999;
			}
		}
	}

	.post-text {
		font-size: 30rpx;
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
		white-space: pre-wrap;
		overflow: hidden;
		word-break: break-all;
		word-wrap: break-word;
		// color: #494B4A;
		color: #000000;

	}

	.post-title {
		font-size: 30rpx;
		font-weight: bold;
		// color: #333333;
		color: #000000;
	}

	.discuss-title {
		height: 30rpx;
		padding: 10rpx;
		line-height: 30rpx;
		border-radius: 10rpx;
		font-size: 24rpx;
		background: #f0f0f5;
		color: #68c778;
		text-align: center;
		margin-right: 10rpx;
	}

	.discuss-title2 {
		height: 30rpx;
		//padding: 10rpx;
		margin-right: 10rpx;
		line-height: 30rpx;
		border-radius: 10rpx;
		font-size: 24rpx;
		//background: #f0f0f5;
		color: #767676;
		//font-weight: bold;
		text-align: center;
	}

	.p-footer {
		display: flex;
		margin: 0 10rpx;
		margin-top: 20rpx;
		margin-bottom: 20rpx;

		.p-item {
			// margin: 0 auto;
			// color: rgba(51, 51, 51, 0.6);
			color: #616161;
			display: flex;
			align-items: center;

			.count {
				margin-left: 10rpx;
				font-size: 26rpx;
			}
		}

		.p-item[hidden] {
			display: none !important;
		}
	}

	.comment-wrap {
		font-size: 28rpx;
		padding-top: 20rpx;
		border-top: 1px solid #F5F5F5;

		.item {
			.name {
				color: #000;
				font-weight: 600;
			}
		}
	}

	.video-wrap {
		display: flex;
		justify-content: center;
		align-items: center;
		position: relative;
		width: 100%;
		height: 500rpx;

		.icon {
			width: 100rpx;
			height: 100rpx;
			z-index: 999;
		}

		image {
			position: absolute;
		}
	}

	//举报
	.informContainer {
		background-color: #FFFFFF;
		text-align: left;
		display: flex;
		// align-items: center;
		// justify-content: left;
		flex-direction: column;
		padding: 1upx 30upx;

	}

	.radio {
		display: block;
		margin-right: 15rpx;
	}

	.complaint-item {
		padding: 15rpx 0;
		// margin: 0 30rpx;
		//border-bottom: 1px solid #eee;
		display: flex;
		width: 40vw;
	}

	.informText {
		background-color: #ffffff;
		text-align: left;
		padding: 1upx 30upx;
		display: flex;
		// align-items: center;
		min-height: 100upx;
		justify-content: space-between;
		flex-direction: column;
	}

	.informTextArea {
		width: 100%;
		height: 200rpx;
		margin-bottom: 10rpx;
		word-break: break-all;
		word-wrap: break-word;
	}

	// 投票
	.vote-box {
		background-color: #F5F5F5;
		border-radius: 20rpx;
		padding: 20rpx;
		margin: 20rpx;

		.title {
			font-weight: bold;
		}

		.expire-time {
			font-size: 24rpx;
			margin: 20rpx 0;
		}

		.vote-item {
			font-size: 24rpx;
			font-weight: bold;
			padding: 20rpx;
			border-radius: 20rpx;
			border: 1px solid #999;
			text-align: center;
			margin-bottom: 20rpx;

			&:last-child {
				margin-bottom: 0;
			}
		}
	}

	.hot-comment-part {
		background-color: #F9FAFC;
		font-size: 26rpx;
		border-radius: 10rpx;
		margin-bottom: 10rpx;
		padding: 10rpx;
	}

	.hot-comment {
		// height: 52rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		/*排列方式*/
		-webkit-line-clamp: 1;
		/*显示文本行数(这里控制多少行隐藏)*/
		overflow: hidden;

	}

	.type-icon {
		margin-right: 10rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 5rpx 0;
		height: 40rpx;
		width: 150rpx;
		border-radius: 20rpx;
		background: rgba(75, 170, 252, 0.1);
		color: #4BAAFC;
		font-size: 24rpx;
	}

	.type-icon2 {
		margin-right: 10rpx;
		display: inline-block;
		padding: 5rpx 15rpx;
		height: 40rpx;
		line-height: 32rpx;
		width: auto;
		border-radius: 20rpx;
		background: rgba(75, 170, 252, 0.1);
		color: #4BAAFC;
		font-size: 24rpx;
		vertical-align: middle;

	}

	.type-image {
		display: inline-block;
		vertical-align: middle;
	}
</style>