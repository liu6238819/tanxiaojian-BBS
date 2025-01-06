<template>
	<view>

		<u-navbar title="帖子详情" :background="navbarStyle" :border-bottom="false" :title-bold="true"
			:custom-back="comeback" title-color="#000000">
		</u-navbar>
		<!-- 通过canvas压缩图片 -->
		<canvas
			:style="{ 'width': cWidth + 'px', 'height': cHeight + 'px', 'position': 'absolute', 'z-index': -1, 'left': '-10000rpx', 'top': '-10000rpx' }"
			canvas-id="canvas"></canvas>
		<block
			v-if="currentContent.contentState==7||(currentContent.contentState==6&&currentContent.userId!=userInfos.userId)||(currentContent.contentState==12&&currentContent.userId!=userInfos.userId)">
			<image class="background-image" :src="backImageUrl"></image>
			<view class="msg-empty">
				<image class="img" mode="widthFix" src="/static/m_10.png"></image>
				<text class="txt">帖子已经被删除了...</text>
			</view>
		</block>
		<block v-else-if="currentContent.alumniOnly && currentContent.alumniOnly==1 && currentUserState!=2 ">
			<image class="background-image" :src="backImageUrl"></image>
			<view class="msg-empty">
				<image class="img" mode="widthFix" src="/static/m_10.png"></image>
				<view style="display: flex; align-items: center; margin-bottom: 20rpx;">
					<!-- <u-icon name="lock-fill" size="40" color="#999"></u-icon> -->
					<view class="txt">作者已设置仅对校园认证用户展示</view>
				</view>
				<button @click="onAlumniOnlyExamine" class="cu-btn round check-examine-button ">现 在 去 认 证
				</button>
			</view>
			<examine-popup v-if="alumniOnlyExamine==1" @returnHid='returnIndex'></examine-popup>
		</block>
		<block v-else>
			<add-my-app></add-my-app>
			<view v-if="msgCount[3]>0" class="notice-line" @click="toMessage()">
				<u-notice-bar mode="vertical" bg-color="#ffaa00" color="#000000" font-size="28rpx" :list="remindTxt">
				</u-notice-bar>
			</view>
			<view class="info-box">
				<image v-if="currentSchoolId !=9999" class="background-image" :src="backImageUrl"></image>
				<image v-if="currentSchoolId ==9999" class="background-image" :src="backImageUrl" mode="top"></image>
				<view class="user-item">
					<image @click="jumpToUserHome" :src="currentContent.headimgUrl" webp="true"></image>
					<view class="user-item-user">
						<!-- <text >{{ currentContent.nickName}}</text> -->
						<view style="display: flex; align-items: center;">
							<text class="user-name"
								style="font-size: 30rpx;color: #000000;font-weight: bold;">{{ currentContent.nickName }}</text>
							<!-- <text v-if="currentContent.userState==2" style="color: blue;">{{currentContent.nickName}}</text> -->
							<text
								v-if="currentContent.userIdentify==10 &&currentContent.isSpecial !=1 && currentContent.isSpecial !=4"
								class="cu-tag light bg-blue sm" style="margin-left: 10rpx ;">官方</text>
							<text
								v-if="currentContent.userState==2 &&(currentContent.userIdentify!=10 ||currentContent.isSpecial ==1 || currentContent.isSpecial ==4)"
								class="cu-tag light bg-green sm" style="margin-left: 10rpx ;">已认证</text>
							<!-- <text v-if="currentContent.userState==0" class="cu-tag light bg-gray sm"
							style="margin-left: 10rpx ;">未认证</text> -->
						</view>
						<view
							v-if="currentContent.introduction && currentContent.isSpecial !=1 && currentContent.isSpecial !=4"
							class="cxplain" style="font-size: 26rpx;">{{ currentContent.introduction }}</view>
					</view>
					<u-button
						v-if="currentContent.contentType!=3 && currentContent.isSpecial !=4 && currentContent.isSpecial!=1 &&isFollow"
						size="mini" style="float:right;font-size: 14px; margin-top: 15rpx; margin-right: 15rpx;"
						shape="circle" plain @click="ontabNoFollow()" :custom-style="btnStyle2" :hair-line="false">
						已关注</u-button>
					<u-button
						v-if="currentContent.contentType!=3 && currentContent.isSpecial !=4 && currentContent.isSpecial!=1 &&!isFollow"
						shape="circle" size="mini" style="float:right;font-size: 14px; margin-top: 15rpx; margin-right: 15rpx;" plain
						@click="changeFollow()" :custom-style="btnStyle" :hair-line="false">
						关注</u-button>
					<u-icon class="arrow-down" style="float:right;font-size: 14px;" size="40" name="more-dot-fill"
						@tap.stop="contentAction(content)"></u-icon>

				</view>
				<view class="icon" style="margin-bottom: 20rpx;">
					<text style="font-size: 24rpx;">{{ currentContent.createTime | date('yyyy年mm月dd日hh时MM分') }}</text>
				</view>
				<block v-if="(currentContent.contentType == 0|| currentContent.contentType == 1 || currentContent.contentType == 3 || (currentContent.contentType > 4 && currentContent.contentType < 9) )
			&& ( currentContent.isSpecial== 0 || currentContent.isSpecial== 1) ">
					<view class="hr"></view>
					<view class="post-title" v-if="currentContent.title!=null &&currentContent.title!=''">
						{{ currentContent.title }}
					</view>
					<rich-text class="post-text" @longpress="copyAndPaste(0,this,currentContent.contentText)"
						:nodes="currentContent.contentText"></rich-text>
					<!-- 图片 -->
					<!-- 使用contenturls展示 -->
					<block v-if="!(currentContent.contentBase64 && currentContent.contentBase64.length>0)">
						<!--一张图片-->
						<block v-if="currentContent.contentUrls && currentContent.contentUrls.length == 1">
							<image class="img-style-1" mode="aspectFill" webp="true"
								:src="currentContent.contentUrls[0]"
								@tap.stop="previewImageByPlatform(currentContent, 0)">
							</image>

						</block>
						<!--二张图片-->
						<block v-if="currentContent.contentUrls && currentContent.contentUrls.length == 2">
							<view class="img-style-2">
								<image v-for="(mediaItem, index2) in currentContent.contentUrls" :key="index2"
									@tap.stop="previewImageByPlatform(currentContent, index2)" mode="aspectFill"
									:src="mediaItem" webp="true"></image>
							</view>
						</block>
						<!--三张以上图片-->
						<block
							v-if="currentContent.contentUrls && currentContent.contentUrls.length > 2 &&currentContent.contentUrls.length != 4">
							<view class="img-style-3">
								<image v-for="(mediaItem, index2) in currentContent.contentUrls" :key="index2"
									@tap.stop="previewImageByPlatform(currentContent, index2)" mode="aspectFill"
									:src="mediaItem" webp="true"></image>
							</view>
						</block>
						<!--四张图片-->
						<block v-if="currentContent.contentUrls && currentContent.contentUrls.length == 4">
							<view class="img-style-8">
								<image v-for="(mediaItem, index2) in currentContent.contentUrls" :key="index2"
									@tap.stop="previewImageByPlatform(currentContent, index2)" mode="aspectFill"
									:src="mediaItem" webp="true"></image>
							</view>
						</block>
					</block>
					<block v-if="currentContent.contentBase64 && currentContent.contentBase64.length>0">
						<!--一张图片-->
						<block v-if="currentContent.contentBase64.length == 1">
							<image class="img-style-1" @tap.stop="previewImage" mode="aspectFill"
								:data-current="currentContent.contentBase64[0]" webp="true"
								:data-image="currentContent.contentBase64" :src="currentContent.contentBase64[0]">
							</image>
						</block>
						<!--二张图片-->
						<block v-if="currentContent.contentBase64.length == 2">
							<view class="img-style-2">
								<image v-for="(mediaItem, index2) in currentContent.contentBase64" :key="index2"
									@tap.stop="previewImage" mode="aspectFill" :data-current="mediaItem"
									:data-image="currentContent.contentBase64" :src="mediaItem" webp="true">
								</image>
							</view>
						</block>
						<!--三张以上图片-->
						<block
							v-if="currentContent.contentBase64.length > 2 &&currentContent.contentBase64.length != 4">
							<view class="img-style-3">
								<image v-for="(mediaItem, index2) in currentContent.contentBase64" :key="index2"
									@tap.stop="previewImage" mode="aspectFill" :data-current="mediaItem"
									:data-image="currentContent.contentBase64" :src="mediaItem" webp="true">
								</image>
							</view>
						</block>
						<!--四张图片-->
						<block v-if="currentContent.contentBase64.length == 4">
							<view class="img-style-8">
								<image v-for="(mediaItem, index2) in currentContent.contentBase64" :key="index2"
									@tap.stop="previewImage" mode="aspectFill" :data-current="mediaItem"
									:data-image="currentContent.contentBase64" :src="mediaItem" webp="true">
								</image>
							</view>
						</block>
					</block>

				</block>

				<block
					v-if="currentContent.contentType == 2 ||currentContent.isSpecial== 3 ||currentContent.isSpecial== 4">
					<view class="hr"></view>
					<view class="post-title">{{ currentContent.title }}</view>
					<rich-text class="post-text" @longpress="copyAndPaste(0,this,currentContent.contentText)"
						:nodes="currentContent.contentText"></rich-text>
					<!-- #ifdef MP-WEIXIN -->
					<block
						v-if="(currentContent.contentType==2 ||currentContent.isSpecial== 3 ||currentContent.isSpecial== 4) && videoId ">
						<view style="width: 100%; text-align: center;">
							<channel-video style="width: 100%" :feed-id="videoId" finder-user-name="sphg37vWTgqo6yZ">
							</channel-video>
						</view>
					</block>
					<!-- #endif -->
				</block>


				<!-- 投票 -->
				<view v-if="currentContent.contentType === 1&&currentContent.votes" class="vote-box">
					<!-- <view class="title">{{ currentContent.title }}</view> -->
					<view class="expire-time" v-if="currentContent.votes[0].isMultiple === 1">单选</view>
					<view class="expire-time" v-if="currentContent.votes[0].isMultiple === 2">多选</view>
					<!-- 是否投票失效 -->
					<view class="expire-box" v-if="isVoteExpire">投票过期了</view>
					<view v-else class="expire-time">
						截止：{{ currentContent.votes[0].deadline | date('yyyy年mm月dd日hh时MM分') }}
					</view>
					<view class="vote-item" @click="castVote(index2, currentContent.votes[0].isMultiple)"
						:class="{ active: item2.checked }" v-for="(item2, index2) in currentContent.votes"
						:key="index2">
						<text v-if="isVote || isVoteExpire">{{ item2.optionText }}<text
								style="color: #999;margin-left: 20rpx;">({{ item2.ticketNum }}票)</text></text>
						<text v-else>{{ item2.optionText }}</text>
					</view>
					<q-button v-if="!currentContent.doVote&&isVoteExpire === false&&isVote === false"
						@click="voteSubmit">
						投票
					</q-button>
				</view>
				<!-- 信息聚合 -->
				<block v-if="currentContent.contentType == 4 || currentContent.isSpecial== 2">
					<view class="post-title">{{ currentContent.title }}</view>
					<!-- 信息 -->
					<view v-for="(Item, index) in currentContent.schoolInfos">
						<rich-text class="post-text" @longpress="copyAndPaste(0,this,Item.intro)"
							:nodes="'['+(index+1)+']. '+Item.intro">
						</rich-text>
						<!--一张图片-->
						<block v-if="Item.imgUrls.length == 1">
							<image class="img-style-1" @tap.stop="previewImage" mode="aspectFill"
								:data-current="Item.imgUrls[0]" :data-image="Item.imgUrls" :src="Item.imgUrls[0]">
							</image>
						</block>
						<view class="schoolInfo-text" @longpress="copyAndPaste(0,this,Item.linkman)">
							<u-button type="primary" size="mini" shape="circle" plain disabled>
								联系方式
							</u-button>
							<view style="padding-right: 10rpx;"></view>
							<text v-if="currentUserState==2">{{ Item.linkman }}</text>
							<text style="width: 65%;"
								v-if="currentUserState!=2">尚未认证，为保护校友隐私，有意向请在评论区留言，将私信您联系方式。</text>
							<text style="color: #0055ff;text-decoration:underline" v-if="currentUserState!=2"
								@click="exazmine">点此认证</text>
						</view>
					</view>
					<rich-text class="post-text" style="font-size: 24rpx; color: #999;"
						:nodes="currentContent.contentText">
					</rich-text>

				</block>

				<!-- 圈子信息 -->
				<navigator v-if="plateInfo.name!='校园广场'" class="topic-info"
					:url="'/package_task/pages/bbs/topic/detail?id=' + plateId">
					<image mode="aspectFill" class="cover" :src="plateInfo.porttairUrl" webp="true"></image>
					<view class="center">
						<view class="desc">{{ plateInfo.name.substring(0, 15) }}</view>
						<!-- 						<view class="count-txt">{{ plateInfo.userNum}}人加入话题 |
							{{ plateInfo.contentNum }}篇内容
						</view> -->
						<view class="count-txt">
							共{{ plateInfo.contentNum }}篇内容
						</view>
					</view>
					<view class="right">
						<text>去看看</text>
						<u-icon name="arrow-right"></u-icon>
					</view>
				</navigator>

				<!--点赞、分享、评论-->
				<view class="p-footer">
					<view style="display: flex; justify-content: space-between; width: 45%;">
						<view class="p-item" style="color: #4BAAFC;" @click="ontabShare()">
							<u-icon name="zhuanfa" size="35"></u-icon>
							<text style="margin-left: 10rpx; font-weight: bold;">分享</text>
						</view>
					</view>
					<view style="display: flex; justify-content: space-between; width: 60%;">

						<view v-if="currentContent.isMark==1" class="p-item" @click="changeMark(0,currentContent)">
							<image src="/static/newUI/mark-fill.png" style="width: 35rpx; height: 35rpx;">
							</image>
							<text class="count "
								style="color: #4BAAFC; font-weight: bold; margin-left: 10rpx;">弃坑</text>
						</view>

						<view v-if="currentContent.isMark==0" class="p-item" @click="changeMark(1,currentContent)">
							<!-- <u-icon name="eye"></u-icon> -->
							<image src="/static/newUI/mark.png" style="width: 35rpx; height: 35rpx;"> </image>
							<text class="count" style="margin-left: 10rpx;">蹲</text>
						</view>

						<view class="p-item"
							v-if="currentContent.place&&currentContent.place !=''&&currentContent.place !=null "
							@click="copyContactInfo()" style="color: #4BAAFC;">
							<!-- <u-icon name="weixin-fill" size="35"></u-icon> -->
							<image src="/static/newUI/weixin-fill.png" style="width: 37rpx; height: 37rpx;">
							</image>
							<text style="margin-left: 10rpx; font-weight: bold;">联系</text>
						</view>

						<view class="p-item"
							v-if="!currentContent.place||currentContent.place ==''&&currentContent.place ==null ">
							<!-- <u-icon name="weixin-fill" color="rgba(51, 51, 51, 0.6);" size="35"></u-icon> -->
							<image src="/static/newUI/weixin.png" style="width: 37rpx; height: 37rpx;"> </image>
							<text style="margin-left: 10rpx">联系</text>
						</view>

						<view @click="changeLike(1)" style="display: flex; align-items: center;">
							<block v-if="currentContent.isLike">
								<view class="p-item">
									<u-icon name="thumb-up-fill" color="#4BAAFC" size="35"></u-icon>
									<text style="margin-left: 10rpx;">{{ currentContent.upNum}}</text>
								</view>
							</block>
							<block v-else>
								<view class="p-item">
									<u-icon name="thumb-up" color="rgba(51, 51, 51, 0.6);" size="35"></u-icon>
									<text style="margin-left: 10rpx;">{{ currentContent.upNum }}</text>
								</view>
								<view v-if="tipState == 1 " class="tip" @click.stop="hideTip">
									<view name="content">留下个赞</view>
									<view name="content">鼓励一下作者！</view>
									<view class="tip_icon"></view>
								</view>
							</block>
						</view>

					</view>

				</view>
				<view style="display: flex; align-items: center; font-size: 22rpx;
				 font-weight:bold; margin-bottom: 20rpx;" v-if="currentContent.markCount>0">
					<!-- <image src="../../static/mark.png" style="width: 16px; height: 16px;"> </image> -->
					<u-icon name="bell-fill"></u-icon>
					<text v-if="currentContent.markNickName ==null"
						style=" margin: 0 10rpx; letter-spacing:5rpx">{{currentContent.markCount}}人在蹲</text>
					<view v-if="currentContent.markNickName !=null">
						<text style=" margin: 0 10rpx;">{{currentContent.markNickName}}</text>
						<text v-if="currentContent.markCount!=1" style="letter-spacing:5rpx;">
							等{{currentContent.markCount}}人在蹲</text>
						<text v-if="currentContent.markCount==1" style="letter-spacing:5rpx">在蹲</text>
					</view>
				</view>
			</view>
			<view class="partition" style="height: 20rpx; background-color: #F9FAFC ; width: 100%;"></view>
			<!-- 进入群聊 -->
			<view class="join-group2" v-if="AdvShowState==true">
				<view class="center">
					<view class="desc" style="font-weight: bold; font-size: 24rpx;">校园资讯群</view>
					<view class="count-txt" style="color: #000000;">链接更多优质校友，不错过每件新鲜事~</view>
				</view>
				<view>
					<view @click="onTabJoinGroup0909" class="cu-btn round bg-black" style="color: #fff; font-size: 20rpx; height: 40rpx; padding: 0 15rpx 0 25rpx;
					 display: flex; align-items: center; justify-content: space-between; width: 100%;">
						<view>加入社群</view>
						<view>
							<u-icon name="arrow-right"></u-icon>
						</view>
					</view>
				</view>
			</view>
			<view v-if="AdvShowState==true" class="partition"
				style="height: 20rpx; background-color: #F9FAFC ; width: 100%;"></view>
			<!-- 评论相关内容 -->
			<view class="comment-box"
				v-if="currentContent.contentState != 9||(currentContent.contentState == 9&&currentUserState==2)">
				<view style="display: flex; align-items: center; justify-content: space-between; margin-top: 20rpx;">
					<view v-if="currentContent.noComment==1"
						style="font-size: 32rpx; font-weight: bold; color: #333333;">评论
						<text style="margin-left:10rpx;">0</text>
					</view>
					<view v-else style="font-size: 32rpx; font-weight: bold; color: #333333;">评论
						<text style="margin-left:10rpx;">{{ currentContent.commentNum }}</text>
					</view>
					<!-- 评论排序方式 -->
					<view style="width: 300rpx;">
						<u-subsection :list="requestTypeList" :font-size="26" :height="60" :current="curRequestType"
							@change="requestTypeChange" inactive-color="#333333" active-color="#333333" bold="false"
							bg-color="#F3F5F8">
						</u-subsection>
					</view>
				</view>

				<view v-if="comments.length==0||currentContent.noComment==1"
					style="text-align: center; font-size: 32rpx; color: #999; margin-top: 40rpx;">
					暂无评论，别让贴主孤单太久...
				</view>
				<view v-if="currentContent.noComment!=1" :id="'comment-' + item.commentId"
					style="margin-bottom: 40rpx; border-bottom: 1rpx solid #ECECEC;" v-for="(item, index1) in comments"
					:key="index1">
					<view class="comment-item" @longpress="deleteAction(item)"
						:class="{'highlight': item.commentId ==highLightCommentId}">
						<image @click="jumpToCommenterHome(item)" class="avatar" :src="item.headimgUrl" webp="true">
						</image>
						<view class="c-content"
							@click="showReplyModelForMain(1,item.commentId,item.nickName,item.userId)">
							<view class="user">
								<view style="display: flex; align-items: center;">
									<text style="font-size: 32rpx;">{{ item.nickName }}</text>
									<!-- <text v-if="item.userState==2" style="color: blue;" >{{item.nickName}}</text> -->
									<!-- <text v-if="item.userState==2" class="cu-tag light bg-green sm"
									style="margin-left: 10rpx ;">已认证</text> -->
									<text
										v-if="item.userIdentify==10 &&currentContent.isSpecial !=1 && currentContent.isSpecial !=4"
										class="cu-tag light bg-blue sm" style="margin-left: 10rpx ;">官方</text>
									<text
										v-if="item.userState==2 &&(currentContent.userIdentify!=10 ||currentContent.isSpecial ==1 || currentContent.isSpecial ==4)"
										class="cu-tag light bg-green sm" style="margin-left: 10rpx ;">已认证</text>
									<!-- 								<text v-if="item.userState==0" class="cu-tag light bg-gray sm"
									style="margin-left: 10rpx ;">未认证</text> -->
								</view>
								<view class="thumbs" @click.stop="changeLike(2,item.commentId,index1)">
									<block v-if="item.viewUserisLike">
										<view style="display: flex; align-items: center;">
											<!-- <text class="num">{{ item.upNum }}</text> -->
											<u-icon class="icon" size="35" name="heart-fill" color="#e62e00"></u-icon>
											<text class="num"
												style="margin-left: 5rpx; font-size: 28rpx;">{{ item.upNum }}</text>
										</view>
									</block>
									<block v-else>
										<view style="display: flex; align-items: center;">
											<!-- <text class="num">{{ item.upNum }}</text> -->
											<u-icon class="icon" size="35" name="heart-fill" color="#bfbfbf"></u-icon>
											<text class="num"
												style="margin-left: 5rpx; font-size: 28rpx;">{{ item.upNum }}</text>
										</view>
									</block>
								</view>

							</view>
							<text class="c-txt">{{ item.commentText }}</text>
							<view class="img-style-5">
								<image v-for="(mediaItem, index2) in item.commentUrls" :key="index2"
									@tap.stop="previewImage" mode="aspectFill" :data-current="mediaItem"
									:data-image="item.commentUrls" :src="mediaItem" webp="true">
								</image>
							</view>
							<view
								style="display: flex; justify-content: space-between; margin-top: 10rpx; align-items: center;">
								<view class="time">{{ item.createTime |  date('yyyy年mm月dd日hh时MM分')  }}</view>
							</view>
						</view>
					</view>
					<view v-if="item.children.length > 0 && item.childrenShowState==1"
						:id="'comment-' + item2.commentId" v-for="(item2, index2) in item.children"
						:key="item2.commentId" class="sub-comment-item" @longpress="deleteAction(item2)"
						:class="{'highlight': item2.commentId ==highLightCommentId}">
						<view class="user">
							<u-avatar @click="jumpToCommenterHome(item2)" class="avatar" :size="40"
								:src="item2.headimgUrl">
							</u-avatar>
							<view class="u-head">
								<view style="display: flex; align-items: center;" @click="jumpToCommenterHome(item2)">
									<text style="font-size: 32rpx;">{{ item2.nickName }}</text>
									<!-- <text v-if="item2.userState==2" style="color: blue;">{{item2.nickName}}</text> -->
									<text
										v-if="item2.userIdentify==10 &&currentContent.isSpecial !=1 && currentContent.isSpecial !=4"
										class="cu-tag light bg-blue sm" style="margin-left: 10rpx ;">官方</text>
									<text
										v-if="item2.userState==2 &&(currentContent.userIdentify!=10 ||currentContent.isSpecial ==1 || currentContent.isSpecial ==4)"
										class="cu-tag light bg-green sm" style="margin-left: 10rpx ;">已认证</text>
								</view>
								<view class="thumbs" @click.stop="changeLike(2,item2.commentId,index1,index2)">
									<block v-if="item2.viewUserisLike">
										<view style="display: flex; align-items: center;">
											<!-- <text class="num">{{ item2.upNum }}</text> -->
											<u-icon class="icon" size="35" name="heart-fill" color="#e62e00"></u-icon>
											<text class="num"
												style="margin-left: 5rpx; font-size: 28rpx;">{{ item2.upNum }}</text>
										</view>
									</block>
									<block v-else>
										<view style="display: flex; align-items: center;">
											<!-- <text class="num">{{ item2.upNum }}</text> -->
											<u-icon class="icon" size="35" name="heart-fill" color="#bfbfbf"></u-icon>
											<text class="num"
												style="margin-left: 5rpx; font-size: 28rpx;">{{ item2.upNum }}</text>
										</view>
									</block>
								</view>

							</view>
						</view>
						<view class="reply"
							@click.stop="showReplyModelForComment(2,item.commentId, item2.nickName,item2.userId)">
							<view class="c-txt">
								<text>回复</text>
								<text :style="{color:'#3a8de5'}">{{item2.replyNickName||item.nickName}}:</text>
								<text>{{ item2.commentText }}</text>
							</view>
							<view class="img-style-5">
								<image v-for="(mediaItem, index2) in item2.commentUrls" :key="index2"
									@tap.stop="previewImage" mode="aspectFill" :data-current="mediaItem"
									:data-image="item2.commentUrls" :src="mediaItem" webp="true">
								</image>
							</view>
							<view
								style="display: flex; justify-content: space-between; margin-top: 10rpx; align-items: center;">
								<view class="time">{{ item2.createTime |  date('yyyy年mm月dd日hh时MM分')  }}</view>
							</view>

						</view>
					</view>
					<view class="reply_text" v-if="item.haveMoreChildren==1"
						@click="getMoreChildrenComment(item,index1)">展开更多回复</view>
					<view class="reply_text"
						v-if="item.haveMoreChildren==0&&item.children.length>2&&item.childrenShowState==1"
						@click="changeChildrenCommentState(item,2)">收起回复</view>
					<view class="reply_text" v-if="item.haveMoreChildren==0 &&item.childrenShowState==2"
						@click="changeChildrenCommentState(item,1)">展开{{item.children.length}}条回复</view>
				</view>
				<!-- 加载状态,点击加载更多 -->
				<block v-if="comments.length > 0 && currentContent.noComment!=1">
					<view style="margin: 30rpx 0;">
						<!-- <u-loadmore :status="loadStatus" :loadingIcon="circle" @loadmore="getMore" /> -->
						<u-loadmore :status="loadStatus" @loadmore="getMore" />
					</view>
				</block>
			</view>
			<view class="comment-box" v-if="currentContent.contentState == 9&&currentUserState!=2">
				<text style="font-weight: bold;">本贴话题较为敏感，</text>
				<text style="font-weight: bold; color: #2979ff;text-decoration:underline" v-if="currentUserState!=2"
					@click="exazmine">校友认证后可查看全部评论</text>
			</view>
			<!-- <ad v-if="AdvShowState==true && AdvShowPosition=='bottom'" unit-id="adunit-d2b94de015b14d1a"></ad> -->
			<view style="padding: 0 20rpx;">
				<u-swiper :list="swiperList" :bg-color="swiperBgColor" :height="200" :autoplay="true"
					@click='onTabAdv()'></u-swiper>
			</view>
			<!-- </scroll-view> -->
			<view style="height: 120rpx;"></view>

			<!-- 评论输入框 -->
			<view class="comment-tool" v-if="currentContent.noComment !=1">
				<view class="pre-box" v-if="tempContentUrls">
					<view class="img-style-4" v-for="(mediaItem, index2) in tempContentUrls" :key="index2">
						<u-icon class="iconfont icon-guanbi1" name="close-circle-fill" size="50"
							@click="deleteImage(index2)"></u-icon>
						<image @tap.stop="previewImage" mode="aspectFill" :data-current="mediaItem"
							:data-image="tempContentUrls" :src="mediaItem" style="height: 150rpx;" webp="true">
						</image>
					</view>
				</view>
				<view
					style="display: flex; align-items: center; height: auto; border-radius: 50rpx; border: 5rpx solid #000000; justify-content: space-between; padding:10rpx 10rpx 10rpx 30rpx;">
					<view style="display: flex; align-items: center;width: 80%;">
						<u-upload :size-type="['compressed']" :custom-btn="true" ref="uUpload" name="Image"
							:max-count='1' @on-choose-complete='chooseImg' @on-list-change='changeImg'
							:auto-upload="false" :max-size='10485760' :show-upload-list="false" :header="header">
							<view slot="addBtn" class="slot-btn" hover-class="slot-btn__hover" hover-stay-time="150"
								@click="judgePrivacy">
								<u-icon name="photo" size="60" color="#000" top="2"></u-icon>
							</view>
						</u-upload>
						<u-input class="input-style" height="60" type="textarea" :clearable="false"
							:placeholder="placeholder" :focus="focus" v-model="form.commentText" :auto-height="true"
							maxlength="500" placeholder-style="color: #999999;" />
					</view>
					<button class="cu-btn round bg-black "
						style="color: #fff; font-size: 30rpx; height: 80rpx; width: 150rpx;"
						@click="publishComment()">发布</button>
				</view>
			</view>
			<!-- 不可评论输入框  -->
			<view class="comment-tool" v-if="currentContent.noComment ==1">
				<view
					style="display: flex; align-items: center; height: auto; border-radius: 50rpx; border: 5rpx solid #000000; justify-content: space-between; padding:10rpx 10rpx 10rpx 30rpx;">
					<u-input class="input-style" height="60" type="textarea" :clearable="false" placeholder="本帖不可评论..."
						:focus="focus" v-model="form.commentText" :auto-height="true" maxlength="500"
						placeholder-style="color: #999999;" disabled />
					<button class="cu-btn round bg-black "
						style="color: #fff; font-size: 30rpx; height: 80rpx; width: 150rpx;"
						@click="publishComment()">发布</button>
				</view>
			</view>

		</block>

		<u-action-sheet :list="deleteActionList" v-model="showDeleteAction" :closeOnClickOverlay="true"
			@close="showDeleteAction = false" :cancel-btn="true" @click="onDeleteAction" :safe-area-inset-bottom="true">
		</u-action-sheet>

		<view class="share-part">
			<!-- <image class="detailAdd" src="../../static/newUI/add-2.png" @click="onPublish"></image> -->

			<view class="add-page" @click="onPublish">
				<u-icon name="edit-pen" size="40"> </u-icon>
			</view>

			<view class="home-page" @click="toFisrtPage">
				<u-icon name="home" size="40"> </u-icon>
			</view>
		</view>
		<view v-show="codeShow">
			<view class="qrcode">
				<image style="width: 180rpx;" :src="pageCodeurl" mode="widthFix"></image>
			</view>
		</view>
		<!-- 		<text v-show="textShow" class="text-sm text-black text-bold shareText">长按二维码进入页面</text> -->

		<q-popup v-model="showShare" height="240rpx">
			<view class="share-wrap" @click="showShare = false">
				<button open-type="share" class="share-item u-reset-button">
					<image src="/static/wechat.png"></image>
					<text>微信好友</text>
				</button>
				<!-- 				<view class="share-item" @click="shareCanvas">
					<image src="/static/img.png"></image>
					<text>分享海报</text>
				</view> -->
			</view>
		</q-popup>
		<!-- 发布弹窗 -->
		<q-popup v-model="showPopup">
			<view class="handle-wrap">
				<!-- 动态和投票 -->
				<view @click="handleJump(item)" class="item" v-for="(item,index) in popup" :key="index">
					<image mode="widthFix" class="icon" :src="item.icon"></image>
					<text class="txt">{{item.text}}</text>
				</view>
			</view>
			<view class="handle-close">
				<q-icon @click="onClose" size="50rpx" name="q-Close"></q-icon>
			</view>
		</q-popup>

		<!-- 加入群聊弹窗 -->
		<view class="cu-modal show" v-if="modalName=='groupChat'">
			<view class="cu-dialog" style="max-height: 80%; overflow: auto;">
				<view class="modal-bottom">
					<view style="display: block; width: 100%;">
						<view class="img-style-6">
							<u-icon class="iconfont icon-guanbi1" name="close-circle-fill" size="50" @click="hideModal">
							</u-icon>
							<image :src="popupImage" mode="widthFix" style="width: 100%" :show-menu-by-longpress="true"
								webp="true">
							</image>
						</view>
						<view style="padding-left: 10rpx;padding-right: 10rpx;" class="text-red text-bold">
							{{popupText}}
						</view>
						<view style="text-align: center display: block; margin-top:10rpx">
							<view>
								<button @click="hideModal" class="cu-btn round bg-blue" style=" width: 50%;">关闭</button>
							</view>
							<u-checkbox @change="checkboxChange" v-model="popupChecked">1日内不再提醒</u-checkbox>
						</view>

					</view>
				</view>
			</view>
		</view>
		<!-- 发帖跳转的蒙版 -->
		<view class="page-mask" v-if="shareMaskState==1">
			<view style="width: 100%;">
				<image class="mask-img2" mode="widthFix" src="/static/m_17.png">
				</image>
				<text class="mask-text text-bold text-white">右上角可分享或转发本贴</text>
			</view>
			<view class="add-modal">
				<u-icon class="iconfont icon-guanbi1" name="close-circle-fill" size="50" @click="hidemask1">
				</u-icon>
				<image src="/static/success.png" mode="widthFix" style="width: 80rpx; margin-bottom:15prx">
				</image>
				<view class="text-xl text-bold" style="margin-top:15rpx; margin-bottom:15prx;"> 动态发布成功</view>
				<view class="text-df text-gray" style="margin-top:15rpx; margin-bottom:15prx; ">赶快邀请好友来围观吧！</view>
				<button @click="returnFirstPage" class="cu-btn round bg-blue text-bold"
					style=" width: 85%; margin-top:20rpx;">回到首页</button>
			</view>
		</view>
		<!-- 邀请进入群聊的蒙版 -->
		<view class="page-mask" v-if="shareMaskState==2">
			<view style="display: block; width: 100%; text-align: center; position: relative; top: 15%;">
				<view class="mask-img3">
					<u-icon class="iconfont icon-guanbi1" name="close-circle-fill" size="50" @click="hidemask2">
					</u-icon>
					<image :src="popupImage" mode="widthFix" style="width: 100%; border-radius: 10rpx;"
						:show-menu-by-longpress="true" webp="true">
					</image>
				</view>

				<view style="padding-left: 10rpx;padding-right: 10rpx;" class="text-white text-bold">
					{{popupText}}
				</view>
				<view style="text-align: center display: block; margin-top:10rpx">
					<view>
						<button @click="hidemask2" class="cu-btn round bg-red" style=" width: 50%;">关闭</button>
					</view>
					<u-checkbox v-if="handleJoinGroup==0" @change="checkboxChange" v-model="popupChecked">1日内不再提醒
					</u-checkbox>
				</view>

			</view>
		</view>
		<!-- 发布弹窗 -->
		<u-popup v-model="showPlusPost" mode="center" border-radius="20" width="80%">
			<view class="share-type">
				<view @click="onTrigger(5)" class="type-item">
					<u-icon class="icon" size="40" name="photo" color="#1aa3ff"></u-icon>
					<text>发布分享吐槽</text>
				</view>
				<view @click="onTrigger(6)" class="type-item">
					<image class="icon" src="/static/topic.png"></image>
					<text>发布求助</text>
				</view>
				<view @click="onTrigger(7)" class="type-item">
					<image class="icon" src="/static/group.png"></image>
					<text>发布组队扩列</text>
				</view>
				<view @click="onTrigger(8)" class="type-item">
					<image class="icon" src="/static/images/icon/jfdh.png"></image>
					<text>发布二手兼职</text>
				</view>
				<view @click="onTrigger(1)" class="type-item">
					<image class="icon" src="/static/h_1.png"></image>
					<text>发布投票</text>
				</view>

			</view>
		</u-popup>
		<view>
			<daily-login-popup :pageShowDaliyPopup="showDailyPopup"></daily-login-popup>
		</view>
		<view class="cu-modal show" v-if="modalName =='showContactInfo'">
			<view class="cu-dialog modal-content">
				<view class="cu-bar bg-white lg justify-end"
					style="min-height: 10rpx;text-align: end; padding-right: 15rpx;">
					<view class="content">查看校友联系方式</view>
					<view class="action" @tap="hideModal()">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view style="display: flex; align-items: center;
				 background-color: #fff; padding-bottom: 20rpx;">
					<text
						style="width: 80%; font-weight: bold; text-align: left; padding-left: 30rpx;">联系方式：{{currentContent.place}}</text>
					<button class="cu-btn bg-blue" style="height: 50rpx;" @click="onCopy(currentContent.place)">
						复制</button>
				</view>
				<view
					style="text-align: center display: block; padding-top:10rpx ;background-color: #fff; padding-bottom: 15rpx;">
					<button @click="hideModal()" class="cu-btn bg-red" style=" width: 30%; height: 50rpx; ">确定</button>
				</view>
			</view>

		</view>

		<view v-if="showExaminePopup==1">
			<!-- <examine-popup @returnHid='returnHide'></examine-popup> -->
			<examine-popup v-if="currentSchoolState!=3" @returnHid='returnHide'></examine-popup>
		</view>

		<!-- 隐私协议弹窗 -->
		<view v-if="firstPage.rule_page!= '/pages/post/detail?id='+currentContent.contentId">
			<privacy-popup></privacy-popup>
		</view>

		<!-- 举报modal框 -->
		<view class="cu-modal show" v-if="modalName =='informModal' ||modalName =='informContentModal'">
			<view class="cu-dialog">
				<view class="cu-bar bg-white lg justify-end">
					<view class="content">请完善举报信息</view>
					<view class="action" @tap="hideModal()">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="informContainer margin-top">
					<view style="font-weight: bold;margin-bottom: 10rpx;">请选择举报原因</view>
					<radio-group @change="informRadioChange" :wrap="true" style="display: flex; flex-wrap:wrap;">
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
					<view style="font-weight: bold;margin-bottom: 10rpx;">详细原因</view>
					<!-- <textarea class="informTextArea" maxlength="-1" v-model="detailInformInfo"
						placeholder="请输入详细原因(选填)"></textarea> -->
					<u-input type="textarea" class="informTextArea" :clearable="false" maxlength="-1"
						v-model="detailInformInfo" placeholder="请输入详细原因(选填)" />
				</view>
				<view class="submit-btn" v-if="modalName =='informModal'">
					<q-button style="color: #FFF;" color="#000" @click="onSubmitInform">提交</q-button>
				</view>
				<view class="submit-btn" v-if="modalName =='informContentModal'">
					<q-button style="color: #FFF;" color="#000" @click="onSubmitInformContent">提交</q-button>
				</view>
			</view>

		</view>
		<!-- 帖子右上角三点弹窗 -->
		<u-action-sheet :list="contentActionList" v-model="showCotnentAction" :closeOnClickOverlay="true"
			@close="showCotnentAction = false" :cancel-btn="true" @click="onContentAction"
			:safe-area-inset-bottom="true">
		</u-action-sheet>
		<view v-if="showManageModal==1">
			<manage-modal :manageKind="manageKind" :manageUserId='manageUserId' :manageContent="manageContent"
				:manageComment="manageComment" @returnHideManage="returnHideManage"></manage-modal>
		</view>

		<view class="share-mask" v-if="showShareMask" @click="onClickShareCancel"></view>
		<image class="timeline-image " v-if="showTimeLineImage" src="/static/newUI/timeLine.png" mode="widthFix">
		</image>
		<view class="menu" v-if="showShareAction">
			<!-- 我想实现点击转发给朋友，所以这里用button里边的share,很方便js不用写什么，只需要控制显示/隐藏 -->
			<button class="menu-item" style="padding: 10rpx;" @click="onClickSharePart(0)"
				open-type="share">转发给朋友</button>
			<button class="menu-item" style="padding: 10rpx;" @click="onClickSharePart(1)">转发到朋友圈</button>
			<!-- <view class="menu-item" @click="onClickSharePart(1)">转发到朋友圈</view> -->
			<view class="menu-item cancel" @click="onClickShareCancel">取消</view>
		</view>
	</view>
</template>

<script>
	//import dailyLoginPopup from '../../components/daily_login_popup/daily_login_popup.vue';
	import addMyApp from '../../components/add_my_app/add_my_app.vue';
	import {
		mapState,
		mapMutations
	} from 'vuex'
	import {
		reqPublishComment,
		reqHomeComments,
		reqEditLikeNum,
		doVote,
		getPlateInfoById,
		getContentById,
		getUserInfoById,
		followUser,
		getUserStateBySchool,
		isFirstRead,
		getUserScore,
		getActionNumToday,
		editCommentState,
		editMark_1,
		editRemindByContentId,
		editMarkRead_1,
		getAdvList,
		reqChildrenCommentPage,
		isFirstReadNew,
		needShowPopup,
		addNoRemindRecord,
		informContentMoreInfo,
		editContentState,
		ifAlreadyInformThisTarget,
		addOperationRecord,
		getStateOfCheckBeforePublish,
		getAdvList0507,
		informContentMoreInfo0522,
		getOneBbsConfig,
		reqHomeCommentsWithHighLight,
		reqPublishCommentUseBody,
		updateFocusSearchTime
	} from "../../api/index.js"
	import localData from '../../utils/data.js';
	import tabbar from '@/utils/tabbar.js';
	import wxml2canvas from "wxml2canvas";
	export default {
		components: {
			//dailyLoginPopup,
			addMyApp,
		},
		data() {
			return {
				remindTxt: [
					'收到新的通知，点击前往查看。',
				],
				contentId: '',
				focus: false,
				loadStatus: 'loadmore',
				commentUserId: '', //发表评论的用户的id
				commentType: 0, //用于表示是对帖子发表评论还是对评论进行回复，0表示对帖子评论，1表示对主评论回复,2表示对子评论进行回复
				placeholder: '说点什么（200字以内）...',
				comments: [], //评论列表
				pageNum: 1, // 获取评论列表用于展示的参数
				pageNumNew: 0,
				pageSize: 10,
				form: { //发表评论需要的参数
					userId: '', //发帖用户的id
					commentState: 0, //0:正常，1：折叠展示，2：需要审核，3：已下架
					commentText: '', //评论的内容
					commentUrl: '', //评论的图片
					contentId: "", //帖子的ID
					isMain: 1, //1：主评论；0：评论的评论
					isReply: 0, //是否是子评论对子评论的评论1：是；0：否
					replyCommentId: "", //子评论所在主评论的id
					replyNickName: "", //子评论回复的子评论对象名称,{userId,nickName}
					upNum: 0, //评论的点赞数
				},
				isFollow: 0,
				//评论请求类型相关
				requestType: 'hot',
				requestTypeList: [{
						name: '最热',
						value: 'hot'
					},
					{
						name: '最新',
						value: 'timeDesc'
					},
					{
						name: '最早',
						value: 'timeAsc'
					}

				],
				curRequestType: 0,
				postDetail: localData.postDetail,
				plateId: '',
				plateInfo: {
					name: '校园广场',
					introduction: '',
					porttairUrl: '',
					contentNum: 0,
					backImgUrl: '',
					member_list: [],
					discuss_list: [],
					description: '',
					isJoin: false,
					userJoinNumber: 0
				},
				showShare: false,
				showCanvas: false,
				shareBtnStyle: {
					backgroundColor: '#333'
				},
				btnStyle: {
					// color: "#000",
					// backgroundColor: '#efd234',
					color: "#fff",
					backgroundColor: '#0081ff',
					height:"40rpx",
					padding:"0 20rpx"
				},
				btnStyle2: {
					color: "#b0b0b0",
					backgroundColor: '#f7f7f7',
					height:"40rpx",
					padding:"0 20rpx"
				},
				page: 1,
				isVoteExpire: false,
				isVote: false,
				pageCodeurl: '', //本页面二维码
				codeShow: false,
				textShow: false,
				AdvShowState: false,
				AdvShowPosition: '',
				isAlumnus: 1, //判断是否认证
				showPopup: false,

				videoId: '', //视频号
				testType: 2, //测试用
				cWidth: 750, // canvas最大宽
				cHeight: 1500, // canvas最大高，
				tempContentUrls: [],
				header: {
					token: uni.getStorageSync('token')
				},
				//弹窗相关
				popupUrl: '',
				popupImage: '',
				popupText: '',
				modalName: "",
				havingRecord: 0,
				noNotice: 0,
				noNoticeDays: 1,
				popupChecked: false,
				//分享蒙层相关
				shareMaskState: 0,
				//投稿相关
				showPlusPost: false,
				tipState: 0,
				//互动提示相关
				nowScore: 0,
				actionToday: '',
				actionTodayForm: {
					userId: '',
				},
				//删除弹窗相关
				showDeleteAction: false,
				deleteActionList: [{
					text: '删除',
					color: 'red',
					key: "delete"
				}],
				currentComment: '',
				//认证相关
				showExaminePopup: 0,
				showDailyPopup: 1,
				swiperList: [],
				enterSource: '',
				//点击加入群聊
				handleJoinGroup: 0,
				//举报相关
				tempInformUrls: [],
				informInfo: '',
				detailInformInfo: '',
				//右上角三点
				showCotnentAction: false,
				contentActionList: [],
				checkBeforePublish: 0,
				//是否需要重新请求
				needRequest: false,
				alumniOnlyExamine: 0,
				haveGetUserState: 0,
				//用户、评论管理
				showManageModal: 0,
				manageKind: 0,
				manageUserId: '',
				manageContent: '',
				manageComment: '',
				navbarStyle: {
					// backgroundColor: 'transparent',
					backgroundImage: 'url("/static/newUI/detail-back.jpg")',
					backgroundSize: '100%',
					backgroundRepeat: 'no-repeat',
					// backgroundPosition: 'top center',
				},
				backImageUrl: '',
				showShareAction: false,
				showShareMask: false,
				showTimeLineImage: false,
				backImageHeight: '600rpx',
				scrollViewHeight: 1000,
				highLightCommentId: '',
				wxQRCode: "",
				currentSchoolName: '',
				posterWide:0,
				posterHeight:0,
			};
		},
		computed: {
			...mapState('content', ['currentContent']),
			...mapState('user', ['userInfos']),
			...mapState('user', ['userKey']),
			...mapState('user', ['currentUserState', 'currentUserType', 'currentSchoolState', 'isLocalUser']),
			...mapState('user', ['scoresFront']),
			...mapState('remind', ['msgCount', 'admireCount', 'replyCount']),
			...mapState('remindWSS', ['markMessageList', 'markState']),
			...mapState('user', ['currentSchoolId']),
			...mapState('popup', ['showDetailPageNum']),
			...mapState('config', ['Adv', 'firstPage', 'privacyPopup', 'judgeShowPopup', 'informConfig', 'alumniOnly']),
		},
		async created() {
			console.log(this.userKey)
			console.log("导航栏图片转base64")
			this.backImageUrl = '/static/newUI/detail-back.jpg'
			if (localData.UIColor == 'blue') {
				this.backImageUrl = '/static/newUI/detail-back-blue.jpg'
			}
			if (this.currentSchoolId == 9999 || uni.getStorageSync('schoolId') == 9999) {
				this.backImageUrl = '/static/NXB/NXB-bg.jpg'
			}
			let imageBase64 = await this.urlToBase64(this.backImageUrl)
			this.navbarStyle.backgroundImage = `url(${imageBase64})`
			// console.log(this.navbarStyle.backgroundImage)
			this.currentSchoolName = uni.getStorageSync("currentSchoolName")
		},
		async mounted() {
			let that = this
			// 判断该用户是否被关注
			// 监听键盘高度变化
			uni.onKeyboardHeightChange(res => {
			})
			if (!this.contentId || (this.currentContent.contentId == this.contentId)) {
				console.log('无需请求帖子详情', this.currentContent);
				this.userIsFollowed()
			} else {
				console.log('重新请求帖子详情');
				this.needRequest = true
				console.log(this.userInfos);
				await this.getAndRefreshContent()
			}

			if (this.currentContent.plateId) {
				this.plateId = this.currentContent.plateId;
			}
			console.log(this.currentContent.doVote);
			if (this.currentContent.doVote) {
				this.isVote = true;
			};
			if (this.currentContent.votes !== null) {
				let now = new Date();
				let deadline = new Date(this.currentContent.votes[0].deadline);
				if (deadline.getTime() <= now.getTime()) {
					this.isVoteExpire = true;
				}
			}

			this.form.userId = this.userInfos.userId;
			//如果是订阅消息跳转的
			let data = {};
			if (this.enterSource == "subscribeMessage" && this.highLightCommentId) {
				data = await reqHomeCommentsWithHighLight({
					pageNum: this.pageNum,
					pageSize: this.pageSize,
					userId: this.form.userId,
					contentId: this.currentContent.contentId,
					requestType: this.requestType,
					commentId: this.highLightCommentId
				})
			} else {
				data = await reqHomeComments({
					pageNum: this.pageNum,
					pageSize: this.pageSize,
					userId: this.form.userId,
					contentId: this.currentContent.contentId,
					requestType: this.requestType
				})
			}
			if (data.code === 200) {
				this.handleImg(data);
				for (var i = 0; i < data.data.length; i++) {
					//将头像制为空，等待本地缓存图片地址返回
					data.data[i].headimgTempUrl = data.data[i].headimgUrl.toString()
					data.data[i].headimgUrl = ''
					//处理子评论头像
					if (data.data[i].children && data.data[i].children.length > 0) {
						for (var j = 0; j < data.data[i].children.length; j++) {
							data.data[i].children[j].headimgTempUrl = data.data[i].children[j].headimgUrl.toString()
							data.data[i].children[j].headimgUrl = ''
						}
					}
				}
				this.comments = data.data
				console.log('评论列表', this.comments);
				//评论头像缓存处理
				for (var i = 0; i < this.comments.length; i++) {
					this.updateCommentHeadUrls(this.comments[i])
				}
				if (this.enterSource == "subscribeMessage" && this.highLightCommentId) {
					this.scrollToComment()
				}
			} else {
				console.log('评论列表获取失败');
			}
			await this.getPlateById();
			await this.isFirstRead();
			if (this.currentUserState) {
				this.haveGetUserState = 1
			}
			// else{
			// this.getUserStateBySchool()
			// }
			setTimeout(function() {
				that.tipState = 0
			}, 2000)
			//处理与该帖子相关的通知信息的已读状态
			console.log("相关参数", this.enterSource, this.msgCount[3], this.userInfos.userId, this.currentContent
				.contentId)
			if (this.enterSource == 'subscribeMessage' && this.msgCount[3] > 0) {
				// console.log("处理站内消息")
				const remindReadResult = await editRemindByContentId({
					userId: this.userInfos.userId,
					contentId: this.currentContent.contentId
				})
				if (remindReadResult.code == 200) {
					// console.log("处理站内消息ok")
					let rsData = remindReadResult.data
					console.log(remindReadResult.data)
					this.setReplyCount(this.replyCount - rsData.reduceCommentCount)
					this.setAdmireCount(this.admireCount - rsData.reduceAdmireCount)
					this.setRemindCount(this.msgCount[3] - rsData.reduceCommentCount - rsData.reduceAdmireCount)
				}
			}
			if (this.enterSource == 'focusMessage') {
				// console.log("处理站内消息")
				const focusParams = {
					userId: this.userInfos.userId,
					schoolId: this.currentSchoolId
				}
				updateFocusSearchTime(focusParams)
			}
			//处理与该帖子相关的mark信息的已读状态
			if (this.markState[3] > 0) {
				if (this.markMessageList && this.markMessageList.length != 0) {
					for (let i = 0; i < this.markMessageList.length; i++) {
						// console.log("chu chu chu chu fa ")
						if (this.markMessageList[i].contentId == this.currentContent.contentId && this
							.markMessageList[i]
							.isRead == 0) {
							// console.log("chu fa ")
							this.editMarkReadState(this.currentContent.contentId)
						}
					}
				}
			}
			// this.getSmallAdvList()
			this.getSmallAdvList0507(3)
		},
		async onShow() {
			if (this.currentContent.contentId == this.contentId) { //一致则不重新请求
				return
			}
			if (!this.currentContent.contentId) { //store中无信息，不重新请求
				return
			}
			console.log('重新请求帖子详情');
			await this.getAndRefreshContent()
		},
		onReady() {
			let that = this
			// setTimeout(function() {
			// 	that.changeBackImageHeight()
			// }, 500);
			that.changeBackImageHeight()
			this.calculateScrollViewHeight();
		},
		async onLoad(options) {
			this.contentId = options.id;
			if (options.commentId) {
				this.highLightCommentId = options.commentId
			}
			//普通二维码进入
			if (options.q) {
				let qrCode = decodeURIComponent(options.q)
				this.contentId = qrCode.substring(43)
			}
			//小程序码进入
			if (options.scene) {
				this.contentId = options.scene
			}
			//发帖后进入
			if (options.from == 'add') {
				console.log('触发蒙层')
				this.shareMaskState = 1
			}
			console.log('获取帖子id：', this.contentId)
			//#ifdef MP-WEIXIN
			wx.showShareMenu({
				withShareTicket: true,
				menus: ['shareAppMessage', 'shareTimeline']
			});
			//#endif
			// this.aboutfunctionConfig('Adv')
			if (this.Adv.Adv_show_state == 1) {
				this.AdvShowState = true
				this.AdvShowPosition = this.Adv.Adv_show_position
			}
			this.increaseShowDetailPageNum();
			//浏览两个帖子，判断一天内是否点击过不再提醒，（并在由发帖跳转时不弹窗，防止混乱）
			if (this.showDetailPageNum == 2 && options.from != 'add' && this.judgeShowPopup.notice_group_config
				.state != 0) {
				// this.onJudgeShowPopup();
				this.isNeedShowPopup();
			}
			// else{
			// 	console.log("弹窗判断云函数未调用",this.showDetailPageNum,options.from,this.judgeShowPopup.notice_group_config.state)
			// }
			console.log("访问页面数", this.showDetailPageNum, this.noNotice)
			//订阅消息进入,设置进入来源
			if (options.source) {
				// console.log("参数咋回事",options)
				this.enterSource = options.source
			}
		},
		methods: {
			...mapMutations('user', {
				setScoresFront: 'setScoresFront',
				setCurrentUserState: 'setCurrentUserState',
				getCurrentSchoolId: 'getCurrentSchoolId',
			}),
			...mapMutations('popup', {
				increaseShowDetailPageNum: 'increaseShowDetailPageNum',
				setShowDetailPageNum: 'setShowDetailPageNum',
			}),
			...mapMutations('remind', {
				setRemindCount: 'setRemindCount',
				setAdmireCount: 'setAdmireCount',
				setReplyCount: 'setReplyCount',
			}),
			//获取帖子详情并重置页面
			async getAndRefreshContent() {
				const data1 = await getContentById({
					contentId: this.contentId,
					userId: uni.getStorageSync('userInfos').userId,
				})
				console.log(this.userInfos.userId);
				console.log(this.contentId)
				if (data1.code === 200) {
					//将头像和帖子图片制为空，等待本地缓存图片地址返回
					//帖子头像处理
					data1.data.headimgTempUrl = data1.data.headimgUrl.toString() //临时头像
					data1.data.headimgUrl = ''
					//帖子图片处理
					if (data1.data.contentUrl !== null && data1.data.contentUrl !== '') {
						// data1.data['contentUrls'] = data1.data.contentUrl.split(',');
						data1.data.contentTempUrls = data1.data.contentUrl.split(',');
						data1.data.contentUrls = [];
					} else {
						data1.data['contentUrls'] = [];
					}
					if (data1.data.contentType == 4 || data1.data.isSpecial == 2) {
						let infos = data1.data.schoolInfos
						for (var i = 0; i < infos.length; i++) {
							if (infos[i].imgUrls) {
								infos[i].imgUrls = infos[i].imgUrls.split(',');
							} else {
								infos[i].imgUrls = []
							}
						}
					}
					//视频贴不需要缓存处理
					if ((data1.data.contentType == 2 || data1.data.isSpecial == 3 || data1.data.isSpecial == 4) &&
						data1.data.contentTempUrls.length > 1) {
						this.videoId = data1.data.contentTempUrls[data1.data.contentTempUrls.length - 1]
						console.log("视频号", this.videoId)
					}
					console.log('帖子详情');
					this.getContentDetail(data1.data) //修改了store中的数据
					if (data1.data.schoolId != this.currentSchoolId) {
						this.getCurrentSchoolId(data1.data.schoolId)
						let numberValue = data1.data.schoolId * 1;
						// 存储转换后的数字类型到本地存储
						uni.setStorageSync('schoolId', numberValue);
					}
					console.log(this.currentContent);
					//帖子头像和图片缓存处理
					this.updatePostcontentUrls(this.currentContent)
					//请求发帖用户详情
					this.userIsFollowed()
					//获取当前认证状态
					this.getUserStateBySchool()

				} else {
					console.log(data1.message);
				}
			},
			//跳转消息页面
			toMessage() {
				uni.switchTab({
					url: '../message/message',
					fail(e) {
						console.log(e)
					}
				});
			},
			async getUserStateBySchool() {
				const params = {
					userId: this.userInfos.userId,
					schoolId: this.currentContent.schoolId
				}
				//如果两个参数中有一个参数为空，返回
				if (!params.userId || !params.schoolId) {
					return
				}
				const stateData = await getUserStateBySchool(params)
				if (stateData && stateData.code === 200) {
					//this.userState = stateData.data.userState
					this.setCurrentUserState(stateData.data.userState)
					this.haveGetUserState = 1
				}
			},
			async exazmine(e) {
				// #ifdef MP-WEIXIN
				console.log('登录/认证开始');
				//const authorizeResult = await this.userAuthorize()
				//无头像昵称，初始化为默认值
				if (this.userInfos.headimgUrl == '' || this.userInfos.headimgUrl == null) {
					uni.hideLoading();
					let headResult = await this.userInfoInit();
					if (headResult != 'success') {
						uni.showToast({
							title: '初始化失败，请重试',
							icon: "none"
						});
						return
					}
				}
				uni.navigateTo({
					url: '/package_task/pages/bbs/user/user-examine'
				});
				// #endif
			},
			async onPaste() {
				let txt = await this.copyAndPaste(1, this, '')
				console.log(txt)
				this.form.commentText = txt
			},
			async userIsFollowed() {
				//如果用户名不存在，不去做请求
				if (!this.userInfos.userId || !this.currentContent.userId) {
					return
				}
				const params = {
					userId: this.userInfos.userId,
					searchId: this.currentContent.userId,
					schoolId: this.currentSchoolId,
				}
				const isFollowData = await getUserInfoById(params)
				console.log('isFollowData', isFollowData);
				if (isFollowData.code === 200) {
					const {
						isFollowed
					} = isFollowData.data.userInfo
					if (isFollowed === 0) {
						// 用户未关注
						this.isFollow = isFollowed
					} else if (isFollowed === 1) {
						// 用户已关注
						this.isFollow = isFollowed
					}
				}
			},
			ontabNoFollow() {
				let that = this
				uni.showModal({
					title: '提示',
					content: "确定要取消关注吗？",
					success: function(res) {
						if (res.confirm) {
							// console.log('用户点击确定');
							that.changeFollow()
						} else if (res.cancel) {
							// console.log('用户点击取消');
						}
					}
				})
			},
			async changeFollow() {
				let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId,
					this
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
				const isFollow = (!this.isFollow) * 1
				//拉黑判断
				if (isFollow == 1) {
					let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, this.currentContent.userId)
					if (blockedState == 1) {
						uni.showToast({
							title: '您已被该用户拉黑',
							icon: 'none'
						})
						return
					}
				}
				let followtitle = "关注中..."
				if (isFollow == 0) {
					followtitle = "取消关注中..."
				}
				uni.showLoading({
					title: followtitle,
					mask: true,
				})
				const params = {
					userId: this.userInfos.userId,
					targetId: this.currentContent.userId,
					isFollow //0:取关，1：关注
				}
				// console.log('params',params);
				const followData = await followUser(params)
				if (followData.code === 200) {
					this.isFollow = isFollow
					uni.hideLoading()
				} else if (followData.code === 201) {
					uni.hideLoading()
					uni.showToast({
						icon: 'none',
						title: '自己不能关注自己哦'
					})
				} else {
					uni.hideLoading()
					uni.showToast({
						icon: 'none',
						title: '接口请求失败，服务器异常！'
					})
				}
				// console.log('followData', followData);
			},
			...mapMutations('content', {
				editUpNumAndIsLike: 'editUpNumAndIsLike'
			}),
			...mapMutations('content', {
				getContentDetail: 'getContentDetail',
				setAdmireState: 'setAdmireState',
				setMarkState: 'setMarkState',
			}),
			...mapMutations('remindWSS', {
				addPostToMarkMessageList: 'addPostToMarkMessageList',
				delPostInMarkMessageList: 'delPostInMarkMessageList',
				changeMarkMessageReadState: 'changeMarkMessageReadState',
			}),
			...mapMutations('content', {
				setContentDoVote: 'setContentDoVote',
				setCurrentContentState: 'setCurrentContentState'
			}),
			async jumpToUserHome() {
				if ((this.currentContent.contentType == 3 || this.currentContent.isSpecial == 1 || this
						.currentContent.isSpecial == 4) && this.currentUserType != 3) return
				//查询是否被拉黑
				let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, this.currentContent.userId)
				if (blockedState == 1) {
					uni.showToast({
						title: '已被当前用户拉黑，无法查看主页！',
						icon: 'none'
					})
					return
				}
				console.log('gotouserHome');
				let url = `/package_task/pages/bbs/user/home?userId=${this.currentContent.userId}`
				uni.navigateTo({
					url
				});
			},
			async jumpToCommenterHome(commenter) {
				if ((this.currentContent.contentType == 3 || this.currentContent.isSpecial == 1 || this
						.currentContent.isSpecial == 4) && this.currentUserType != 3) return
				let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, commenter.userId)
				if (blockedState == 1) {
					uni.showToast({
						title: '已被当前用户拉黑，无法查看主页！',
						icon: 'none'
					})
					return
				}
				console.log('gotouserHome');
				let url = `/package_task/pages/bbs/user/home?userId=${commenter.userId}`
				uni.navigateTo({
					url
				});
			},
			async getPlateById() {
				const form = {
					plateId: this.plateId
				};
				const data = await getPlateInfoById(form);
				console.log(data)
				this.plateInfo.name = data.name;
				this.plateInfo.introduction = data.introduction;
				this.plateInfo.porttairUrl = data.portraitUrl;
				this.plateInfo.backImgUrl = data.backImgUrl;
				this.plateInfo.contentNum = data.contentNum;
				this.plateInfo.isJoin = data.join;
				this.plateInfo.userJoinNumber = data.userJoinNumber;
				this.plateInfo.userNum = data.userNum;
			},
			async getMore() {
				this.pageNum += 1
				const data = await reqHomeComments({
					pageNum: this.pageNum,
					pageSize: this.pageSize,
					userId: this.form.userId,
					contentId: this.currentContent.contentId,
					requestType: this.requestType
				})
				if (data.code === 200) {
					this.handleImg(data);

					console.log(data.data.length);
					if (data.data.length > 0) {
						let existingCommentIds = new Set(this.comments.map(comment => comment.commentId));
						let filteredData = data.data.filter(comment =>
							!existingCommentIds.has(comment.commentId)
						);
						//将头像制为空，等待本地缓存图片地址返回
						for (var i = 0; i < filteredData.length; i++) {
							filteredData[i].headimgTempUrl = filteredData[i].headimgUrl.toString()
							filteredData[i].headimgUrl = ''
							//处理子评论头像
							if (filteredData[i].children && filteredData[i].children.length > 0) {
								for (var j = 0; j < filteredData[i].children.length; j++) {
									filteredData[i].children[j].headimgTempUrl = filteredData[i].children[j].headimgUrl
										.toString()
									filteredData[i].children[j].headimgUrl = ''
								}
							}
						}
						console.log('no null');
						this.loadStatus = 'loadmore'
						this.comments.push(...filteredData)
						for (var i = 0; i < filteredData.length; i++) {
							this.updateCommentHeadUrls(filteredData[i])
						}
					} else {
						console.log('nomore');
						this.loadStatus = 'nomore'
					}
					// console.log('data', data.data);
					//评论头像缓存处理
				} else {
					console.log(data);
					console.log('首页获取帖子列表失败');
				}
			},
			async changeLike(flag, commentId, index1, index2) {
				if (this.currentSchoolState == 3 && this.isLocalUser == 1) {
					console.log(this.isLocalUser, this.currentSchoolState)
					uni.showToast({
						icon: 'none',
						title: '请认证后使用'
					})
					this.showExaminePopup = 1
					return
				}
				uni.showLoading({
					title: '点赞中...',
					mask: true,
				})
				if (this.userInfos.userId !== '') {
					const params = {
						isContent: 1, //帖子
						isLike: 1, //1点赞 0取消点赞
						targetId: "", //34,//'65310f343fdb431d866f991d9ade98fb',
						userId: this.userInfos.userId //'6c109fe4bcdb4b718d94a687ce8a1be6'
					}
					if (flag === 1) { //对帖子的点赞
						params.isContent = 1
						params.targetId = this.currentContent.contentId //帖子的ID
						if (this.currentContent.isLike) { //1 已点赞
							params.isLike = 0
						} else {
							params.isLike = 1
						}
						//判断是否被拉黑
						if (params.isLike == 1) {
							let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, this.currentContent
								.userId)
							if (blockedState == 1) {
								uni.showToast({
									title: '您已被该用户拉黑',
									icon: 'none'
								})
								return
							}
						}
					} else { //对评论的点赞flag =2
						params.isContent = 0
						const isLike = index2 !== undefined ? this.comments[index1].children[
								index2].viewUserisLike :
							this
							.comments[
								index1].viewUserisLike
						if (isLike) { //1 已点赞;0未点赞
							params.isLike = 0
						} else {
							params.isLike = 1
						}
						params.targetId = commentId //评论的ID
						//判断是否被拉黑
						if (params.isLike == 1) {
							let searchUserId = this.comments[index1].userId
							//子评论
							if (index2) {
								searchUserId = this.comments[index1].children[index2].userId
							}
							let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, searchUserId)
							if (blockedState == 1) {
								uni.showToast({
									title: '您已被该用户拉黑',
									icon: 'none'
								})
								return
							}
						}
					}
					const data = await reqEditLikeNum(params)
					setTimeout(function() {
						uni.hideLoading()
					}, 500)
					if (flag === 1) { //对帖子的点赞，更新点赞的状态和数量
						if (data.code === 200) {
							this.setAdmireState({
								'contentId': this.currentContent.contentId,
								'isLike': params.isLike
							})
							//触发上一个页面的修改mark状态
							let pages = getCurrentPages();
							if (pages.length >= 2) { //防止超出数组域
								let prevPage = pages[pages.length - 2];
								prevPage.$vm.editLikeState({
									"isLike": params.isLike,
									"contentId": this.currentContent.contentId,
								})
							}
							if (params.isLike) {
								this.editUpNumAndIsLike([1,
									true
								]) //更新vuex中currentContent的点赞数和是否已点赞

								if (data.data.admireType == 1) {
									await this.showScoreTosat(1);
								}
							} else {
								this.editUpNumAndIsLike([-1, false])

							}
							// console.log('点赞成功');
						} else {
							console.log('点赞失败');
						}
					} else { //对评论的点赞
						if (data.code === 200) {
							if (params.isLike) {
								if (index2 === undefined) {
									this.comments[index1].viewUserisLike =
										true
									this.comments[index1].upNum += 1
								} else {
									this.comments[index1].children[index2].viewUserisLike =
										true
									this.comments[index1].children[index2].upNum += 1
								}
								if (data.data.admireType == 1) {
									await this.showScoreTosat(1);
								}
							} else {
								if (index2 === undefined) {
									this.comments[index1].viewUserisLike = false
									this.comments[index1].upNum -= 1
								} else {
									this.comments[index1].children[index2].viewUserisLike = false
									this.comments[index1].children[index2].upNum -= 1
								}

							}
						} else {
							console.log('点赞失败');
						}
					}
				} else {
					uni.showToast({
						title: '请登录后点赞！'
					})
				}

			},
			showReplyModelForMain(commentType, commentId, nickName, replyUserId) {
				this.focus = false
				this.focus = true //发表评论的input框获取焦点，唤起键盘
				this.commentType = commentType
				this.form.replyCommentId = commentId
				this.placeholder = `回复：${nickName}`
				this.form.replyNickName = nickName
				this.form.replyUserId = replyUserId
				// console.log(commentType, commentId);
				// console.log('main');
			},
			showReplyModelForComment(commentType, commentId, replyNickName, replyUserId) {
				this.focus = false
				this.focus = true //发表评论的input框获取焦点，唤起键盘
				//console.log('deianji', commentType, commentId, replyNickName, replyUserId)
				this.commentType = commentType
				this.form.replyCommentId = commentId
				this.form.replyNickName = replyNickName
				this.form.replyUserId = replyUserId
				this.placeholder = `回复：${replyNickName}`
				// console.log('nickName &&&&', this.form);
				// console.log(commentType, commentId);
			},
			async publishComment() {
				if (!this.form.commentText && this.tempContentUrls.length == 0) {
					uni.showToast({
						title: '请输入评论内容',
						icon: "none",
					});
					return
				}
				//订阅小程序通知，不可放在回调函数中
				localData.requestMessage([7, 9, 10])
				uni.showLoading({
					title: '发布中...',
					mask: true
				})
				//获取校友认证配置信息
				// const alumniInfo = await localData.aboutAlumniOnly('alumniOnly', this
				// 	.currentSchoolId)
				let alumniInfo = this.alumniOnly.state
				let isAlumniOnly = alumniInfo
				console.log("jieguo", isAlumniOnly)
				//当isAlumniOnly==0或contentState==10时，跳过权限判断
				if (isAlumniOnly == 0 || this.currentContent.contentState == 10) {
					console.log("跳过认证")
				} else {
					//先进行隐私授权判断
					// console.log(this.privacyPopup.needAuthorization)
					if (this.privacyPopup.needAuthorization) {
						let needPrivacyAuthorization = await this.judgePrivacySetting()
						// console.log(needPrivacyAuthorization)
						if (needPrivacyAuthorization == true) {
							uni.hideLoading();
							return
						}
					} else {
						console.log("已授权，不需要调用")
					}
					let permissionCheckResult = await this.permissionCheckNew(this.userInfos
						.userId, this
						.currentSchoolId,
						1)
					if (permissionCheckResult == 600 || permissionCheckResult == 300) { //禁言提示 600 审核中提示 300
						return
					}
					if (permissionCheckResult != 0) {
						uni.hideLoading()
						this.showExaminePopup = 1
						return
					}
					//判断是否被拉黑
					//发帖人
					let searchUserId = this.currentContent.userId
					let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, searchUserId)
					if (blockedState == 1) {
						uni.showToast({
							title: '被发帖人拉黑，无法互动！',
							icon: 'none'
						})
						return
					}
					if (this.form.replyUserId != '' && this.form.replyUserId != null) {
						searchUserId = this.form.replyUserId
						blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, searchUserId)
						if (blockedState == 1) {
							uni.showToast({
								title: '被评论用户拉黑，无法互动！',
								icon: 'none'
							})
							return
						}
					}

				}			
				//无头像昵称，初始化为默认值
				if (this.userInfos.headimgUrl == '' || this.userInfos.headimgUrl == null) {
					uni.hideLoading();
					let headResult = await this.userInfoInit();
					if (headResult != 'success') {
						// 用户拒绝授权使用昵称、头像
						uni.showToast({
							title: '初始化失败，请重试',
							icon: "none"
						});
						return
					}
				}
				//获取【先审后发】配置状态
				let queryForm = {
					schoolId: this.currentSchoolId
				}
				const data = await getStateOfCheckBeforePublish(queryForm);
				if (data.code == 200) {
					console.log(data)
					this.checkBeforePublish = data.data
				}
				//用户当前为预警用户，帖子发布类型为冻结
				if (this.currentUserType == 1 || this.checkBeforePublish == 1) {
					this.form.commentState = 6
				} else {
					this.form.commentState = 0
				}
				//发布评论
				if (!this.tempContentUrls) {
					this.submitComment()
				} else {
					this.submitPicComment()
				}
			}, //publishComment
			// 输入框失去焦点时
			onBlur() {
				this.placeholder = '说点什么...';
				this.focus = false;
				//this.form.commentText = "";
				this.commentType = 0;
				this.form.isMain = 1
				this.form.isReply = 0
				this.form.replyCommentId = ""
				this.form.replyNickName = ""
			},

			async submitComment() {
				this.form.userId = this.userInfos.userId
				this.form.contentId = this.currentContent.contentId
				// this.form.commentState = 0
				if (this.commentType === 0) { //0表示对帖子的评论
					this.form.isMain = 1
					this.form.isReply = 0
					this.form.replyCommentId = ""
					this.form.replyNickName = ""
					console.log('对帖子评论', this.form);
				} else if (this.commentType === 1) { //对主评论的评论
					this.form.isMain = 0
					this.form.isReply = 0
					// this.form.replyCommentId = "" // showReplyModel中已做修改
					//this.form.replyNickName = ""
					// this.commentType = 0
					console.log("对主评论的评论", this.form);
				} else if (this.commentType === 2) { //对主评论下子评论的评论
					this.form.isMain = 0
					this.form.isReply = 1
					// this.form.replyCommentId = "" // showReplyModel中已做修改
					// this.form.replyNickName = ""
					console.log("对主评论下子评论的评论", this.form);
				}
				//保持临时变量，用于发送小程序通知
				let tempCommentType = this.commentType
				let tempCommentText = this.form.commentText

				//重置为对主贴评论
				const placeHolder = '说点什么...'
				this.focus = false
				if (this.placeholder !== placeHolder) {
					// 修改placeholder的状态,同时修改发送评论的状态，此时发送的评论为对帖子的评论
					this.placeholder = placeHolder
					this.commentType = 0
				}

				// 发布评论,发布评论之前对评论内容进行审核
				//#ifdef MP-WEIXIN
				//console.log(this.userKey.tokenWX)
				let checkStr = this.form.commentText;
				let textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId,
					checkStr, 2);
				if (textCheck == 0) {
					uni.showLoading({
						title: '发布中...',
						mask: true
					})
					const data = await reqPublishCommentUseBody(this.form)
					// const data = await reqPublishComment(this.form)
					console.log(data);
					if (data.code === 200) {
						uni.hideLoading()
						// 在评论发布成功后修改评论发布框的状态
						this.placeholder = '说点什么...'
						this.form.commentText = ""
						uni.showToast({
							title: '评论发布成功！',
							icon: "none"
						});
						//await this.showScoreTosat(2)
						//以下为发送小程序通知代码块
						//获得replyuser的openId
						const replyUserForm = {
							userId: this.userInfos.userId,
							searchId: '',
							schoolId: this.currentSchoolId,
						}
						let replyUserOpenId = ''
						if (tempCommentType == 0) {
							replyUserForm.searchId = this.currentContent.userId
						} else {
							replyUserForm.searchId = this.form.replyUserId
						}
						const tempData = await getUserInfoById(replyUserForm)
						// console.log('信息',tempData);
						if (tempData.code == 200) {
							replyUserOpenId = tempData.data.userInfo.openId
						}
						console.log('评论发布成功');
					} else {
						console.log('评论发布失败');
					}
				} else {
					uni.hideLoading()
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
					//人工审核分支？
				}
				//#endif
				uni.hideLoading()
				// 再请求评论列表
				//console.log('pageNum', this.pageNum);
				const commentData = await reqHomeComments({
					pageNum: 1,
					pageSize: this.pageSize * this.pageNum,
					userId: this.form.userId,
					contentId: this.currentContent.contentId,
					requestType: this.requestType
				})
				if (commentData.code === 200) {
					this.handleImg(commentData)
					// this.comments = commentData.data
					//将头像制为空，等待本地缓存图片地址返回
					for (var i = 0; i < commentData.data.length; i++) {
						commentData.data[i].headimgTempUrl = commentData.data[i].headimgUrl.toString()
						commentData.data[i].headimgUrl = ''
						//处理子评论头像
						if (commentData.data[i].children && commentData.data[i].children.length > 0) {
							for (var j = 0; j < commentData.data[i].children.length; j++) {
								commentData.data[i].children[j].headimgTempUrl = commentData.data[i].children[j]
									.headimgUrl.toString()
								commentData.data[i].children[j].headimgUrl = ''
							}
						}
					}
					this.comments = commentData.data
					console.log('评论列表获取成功', this.comments);
					//评论头像缓存处理
					for (var i = 0; i < this.comments.length; i++) {
						this.updateCommentHeadUrls(this.comments[i])
					}



				} else {
					console.log('评论列表获取失败');
				}
			},
			castVote(index, type) {
				if (!this.isVoteExpire && !this.isVote) {
					if (type === 1) {
						this.currentContent.votes.forEach(item => {
							this.$set(item, 'checked', false);
						});
					}
					let checked = this.currentContent.votes[index].checked;
					console.log(checked)
					if (checked) {
						this.$set(this.currentContent.votes[index], 'checked', false);
					} else {
						this.$set(this.currentContent.votes[index], 'checked', true);
					}
				}
			},
			async submitPicComment() {
				//上传图片
				const oosImgUrl = []
				const {
					tempContentUrls
				} = this
				console.log(tempContentUrls);
				for (let i = 0, length = tempContentUrls.length; i < length; i++) {
					uni.showLoading({
						mask: true,
						title: '审核第' + (i + 1) + '/' + length + '张...'
					});
					//原始图片压缩
					// const compressResult = await localData.compressionIamge(this, tempContentUrls[
					// 	i]);
					//微信官方压缩方法(二次压缩)
					const compressResult = await localData.compressionIamgeWXTwice(this, tempContentUrls[i], 1000);
					console.log(compressResult)
					let imgUrl = await this.uploadImageOSS(compressResult, 'content/contentImg/',
						this
						.currentSchoolId)
					//对图片进行审核
					let imgCheck = await this.ugcImgCheck(this.userKey.tokenWX, this.userKey
						.openId, imgUrl, 2, 1);
					if (imgCheck !== 87014) {
						imgUrl = imgUrl.replace('keming-bbs.oss-cn-shanghai.aliyuncs.com',
							'image.tanxiaojian.zone')
						oosImgUrl.push(imgUrl)
					} else {
						this.clearImages()
						uni.hideLoading()
						uni.showToast({
							mask: true,
							icon: 'none',
							title: '图片可能潜在风险！'
						});
						return
					}

				}
				this.form.commentUrl = oosImgUrl.join(',');
				uni.hideLoading();
				this.form.userId = this.userInfos.userId
				this.form.contentId = this.currentContent.contentId
				// this.form.commentState = 0
				if (this.commentType === 0) { //0表示对帖子的评论
					this.form.isMain = 1
					this.form.isReply = 0
					this.form.replyCommentId = ""
					this.form.replyNickName = ""
					console.log('对帖子评论', this.form);
				} else if (this.commentType === 1) { //对主评论的评论
					this.form.isMain = 0
					this.form.isReply = 0
					// this.form.replyCommentId = "" // showReplyModel中已做修改
					//this.form.replyNickName = ""
					// this.commentType = 0
					console.log("对主评论的评论", this.form);
				} else if (this.commentType === 2) { //对主评论下子评论的评论
					this.form.isMain = 0
					this.form.isReply = 1
					// this.form.replyCommentId = "" // showReplyModel中已做修改
					// this.form.replyNickName = ""
					console.log("对主评论下子评论的评论", this.form);
				}
				//保持临时变量，用于发送小程序通知
				let tempCommentType = this.commentType
				let tempCommentText = this.form.commentText

				//重置为对主贴评论
				const placeHolder = '说点什么...'
				this.focus = false
				if (this.placeholder !== placeHolder) {
					// 修改placeholder的状态,同时修改发送评论的状态，此时发送的评论为对帖子的评论
					this.placeholder = placeHolder
					this.commentType = 0
				}

				// 发布评论,发布评论之前对评论内容进行审核
				//#ifdef MP-WEIXIN
				//console.log(this.userKey.tokenWX)
				let textCheck = 0
				if (this.form.commentText) {
					console.log('Check');
					let checkStr = this.form.commentText;
					textCheck = await this.ugcTxtCheck(this.userKey.tokenWX, this.userKey.openId,
						checkStr, 2);
					console.log('Check', textCheck);
				}
				console.log('textCheck', textCheck);
				if (textCheck == 0) {
					uni.showLoading({
						title: '发布中...',
						mask: true
					})
					const data = await reqPublishCommentUseBody(this.form)
					// const data = await reqPublishComment(this.form)
					console.log(data);
					if (data.code === 200) {
						uni.showToast({
							title: '发布成功！',
							icon: "none"
						});
						// 在评论发布成功后修改评论发布框的状态
						this.clearImages()
						this.form.commentUrl = ""
						this.placeholder = '说点什么...'
						this.form.commentText = ""
						//await this.showScoreTosat(2)
						//以下为发送小程序通知代码块
						//获得replyuser的openId
						const replyUserForm = {
							userId: this.userInfos.userId,
							searchId: '',
							schoolId: this.currentSchoolId,
						}
						let replyUserOpenId = ''
						if (tempCommentType == 0) {
							replyUserForm.searchId = this.currentContent.userId
						} else {
							replyUserForm.searchId = this.form.replyUserId
						}
						const tempData = await getUserInfoById(replyUserForm)
						// console.log('信息',tempData);
						if (tempData.code == 200) {
							replyUserOpenId = tempData.data.userInfo.openId
						}
						console.log('评论发布成功');
					} else {
						console.log('评论发布失败');
					}
				} else {
					uni.hideLoading()
					uni.showToast({
						title: '文本存在问题',
						icon: "none"
					});
					return
					//人工审核分支？
				}

				//#endif
				uni.hideLoading()
				// 再请求主评论列表
				if (this.form.isMain == 1) {
					const commentData = await reqHomeComments({
						pageNum: 1,
						pageSize: this.pageSize * this.pageNum,
						userId: this.form.userId,
						contentId: this.currentContent.contentId,
						requestType: this.requestType
					})
					if (commentData.code === 200) {
						this.handleImg(commentData);
						// this.comments = commentData.data
						//将头像制为空，等待本地缓存图片地址返回
						for (var i = 0; i < commentData.data.length; i++) {
							commentData.data[i].headimgTempUrl = commentData.data[i].headimgUrl.toString()
							commentData.data[i].headimgUrl = ''
							//处理子评论头像
							if (commentData.data[i].children && commentData.data[i].children.length > 0) {
								for (var j = 0; j < commentData.data[i].children.length; j++) {
									commentData.data[i].children[j].headimgTempUrl = commentData.data[i].children[j]
										.headimgUrl.toString()
									commentData.data[i].children[j].headimgUrl = ''
								}
							}
						}
						this.comments = commentData.data
						console.log('评论列表获取成功', this.comments);
						//评论头像缓存处理
						for (var i = 0; i < this.comments.length; i++) {
							this.updateCommentHeadUrls(this.comments[i])
						}
					} else {
						console.log('评论列表获取失败');
					}
				}
				//请求对应子评论列表
				else {
					let item = '';
					let index = 0
					for (var i = 0; i < this.comments.length; i++) {
						if (this.comments[i].commentId == this.form.replyCommentId) {
							item = this.comments[i];
							index = i
							break
						}
					}
					this.getMoreChildrenComment(item, index)
				}
			},
			voteSubmit() {
				let voteResult = [];
				let now = new Date();
				this.currentContent.votes.forEach(item => {
					if (item.checked) {
						item.ticketNum++;
						const temp = {
							contentId: this.currentContent.contentId,
							userId: this.userInfos.userId,
							voteId: item.voteId,
						}
						voteResult.push(temp);
					}
				});
				var params = JSON.stringify(voteResult)
				console.log(params);
				let that = this
				uni.request({
					url: localData.baseUrl + '/content/doVoteApi',
					data: params,
					method: 'POST',
					success(e) {
						console.log(e);
						uni.showToast({
							title: '投票成功！',
						});
						that.isVote = true;
						//改变store中dovote的状态
						that.setContentDoVote(that.currentContent)
						that.currentContent.votes.forEach(item => {
							that.$set(item, 'checked', false);
						});
					},
					fail(e) {
						console.log(e);
					}
				})
			},
			previewImage(e) {
				uni.previewImage({
					current: e.currentTarget.dataset.current, // 当前显示图片的http链接
					urls: e.currentTarget.dataset.image // 需要预览的图片http链接列表
				});
			},
			previewImageByPlatform(content, index) {
				console.log(uni.getSystemInfoSync().platform, content, index)
				let url = content.contentUrls[index];
				let urls = content.contentUrls;
				if ((uni.getSystemInfoSync().platform == "windows" || uni.getSystemInfoSync().platform == "mac") && content
					.contentTempUrls) {
					url = content.contentTempUrls[index];
					urls = content.contentTempUrls;
				}
				uni.previewImage({
					current: url, // 当前显示图片的http链接
					urls: urls // 需要预览的图片http链接列表
				});
			},
			onPublish() {
				// this.showPlusPost = true
				uni.navigateTo({
					url: '/pages/post/add'
				})
			},
			onTrigger(type) {
				this.showPlusPost = false;
				if (type == 1) {
					uni.navigateTo({
						url: '/pages/vote/vote?'
					});
				} else {
					uni.navigateTo({
						url: '/pages/post/add?contentType=' + type
					});
				}
			},
			//跳转群活码页面
			toGroupCode(codeKind) {
				//console.log('触发')
				uni.navigateTo({
					url: '../../package_task/pages/group-code/index?codeKind=' + codeKind
				})
			},
			async toFisrtPage() {
				uni.switchTab({
					url: '/pages/index/index'
				});
				// if (this.currentUserState == 2) {
				// 	localData.requestMessage([7, 8])
				// }
			},
			toMine() {
				uni.switchTab({
					url: '/pages/mine/mine'
				});
			},
			//调用云函数获取页面小程序码
			async getWXPageCode() {
				this.codeShow = true
				this.textShow = true
				let that = this
				// wx.cloud.init({
				// 	env: localData.envId,
				// 	traceUser: true,
				// })
				// wx.cloud.callFunction({
				await localData.cloud_shared.init()
				await localData.cloud_shared.callFunction({
					name: 'getWxacode', //云函数的名称
					data: {
						page: "pages/post/detail",
						scene: that.contentId
					},
					complete: res => {
						//console.log(res)
						this.pageCodeurl = 'data:image/png;base64,' + uni
							.arrayBufferToBase64(res.result
								.buffer);
						console.log(this.contentId);
					}

				})
			},
			//调佣云函数-功能配置-广告(已停用)
			aboutfunctionConfig(functionName) {
				let that = this
				wx.cloud.init({
					env: localData.envId,
					traceUser: true,
				})
				wx.cloud.callFunction({
					name: 'functionConfigSQL', //云函数的名称
					data: {
						functionName: functionName, //调用哪个数据库函数
						schoolId: this.currentSchoolId, //前
						AdvInfo: '',
						usingDatabase: localData.usingDatabase
					},
					complete: res => {
						let nowdata = res.result
						console.log(nowdata)
						if (nowdata.AdvInfo.Adv_show_state == 1) {
							that.AdvShowState = true
							that.AdvShowPosition = nowdata.AdvInfo.Adv_show_position
						}
					}
				})
			},
			clearImages() {
				this.tempContentUrls = []
				this.$refs.uUpload.clear();
			},
			chooseImg(lists) {
				this.tempContentUrls = lists.map(item => item.url)
			},
			changeImg(lists) {
				this.tempContentUrls = lists.map(item => item.url)
				console.log(lists)
				console.log(this.tempContentUrls)
			},
			async deleteImage(index) {
				console.log(index)
				await this.$refs.uUpload.lists.splice(index, 1)
				// this.tempContentUrls = lists.map(item => item.url)
				console.log('组件', this.$refs.uUpload.lists)
				console.log('本地', this.tempContentUrls)
			},
			//评论图片处理
			handleImg(commentData) {
				for (var i = 0; i < commentData.data.length; i++) {
					if (commentData.data[i].commentUrl !== null && commentData.data[i].commentUrl !==
						'') {
						commentData.data[i].commentUrls = commentData.data[i].commentUrl.split(',');
					} else {
						commentData.data[i].commentUrls = [];
					}
					if (commentData.data[i].children && commentData.data[i].children.length > 0) {
						for (var j = 0; j < commentData.data[i].children.length; j++) {
							if (commentData.data[i].children[j].commentUrl !== null && commentData
								.data[i]
								.children[j].commentUrl !== '') {
								commentData.data[i].children[j].commentUrls = commentData.data[i]
									.children[j]
									.commentUrl.split(',');
							} else {
								commentData.data[i].children[j].commentUrls = [];
							}
						}
					}
				}
			},
			//发布帖子相关方法
			onTab() {
				if (this.userInfos.userId) {
					this.showPopup = true;
				} else {
					uni.showToast({
						title: '请登录后发帖'
					})
				}
			},
			onClose() {
				this.showPopup = false;
			},
			handleJump(e) {
				uni.navigateTo({
					url: e.url
				})

				this.showPopup = false;
			},
			//判断是否需要弹资讯群窗
			async isNeedShowPopup() {
				let that = this
				const queryForm = {
					schoolId: this.currentSchoolId,
					unionId: this.userInfos.unionId
				}
				const data = await needShowPopup(queryForm)
				if (data.code === 200) {
					console.log("弹窗结果", data)
					//返回结果为true,弹窗
					if (data.data) {
						that.popupUrl = that.judgeShowPopup.notice_group_config.url;
						that.popupImage = that.judgeShowPopup.notice_group_config.image;
						that.popupText = that.judgeShowPopup.notice_group_config.text;
						console.log("需要弹窗", that.judgeShowPopup.notice_group_config.image, that.judgeShowPopup
							.notice_group_config.text)
						that.shareMaskState = 2;
					} else {
						console.log("不需要弹窗", data.data)
					}
				}


			},
			//用户点击不再提醒后，关闭弹窗时
			async onTabNoRemindAgain() {
				let that = this
				const queryForm = {
					schoolId: this.currentSchoolId,
					userId: this.userInfos.userId,
					unionId: this.userInfos.unionId,
					nickName: this.userInfos.nickName,
				}
				const data = await addNoRemindRecord(queryForm)
				if (data.code == 200) {
					console.log(data)
				}
			},
			//判断该用户是否在群聊里（云函数版）
			async onJudgeShowPopup() {
				let that = this
				// wx.cloud.init({
				// 	env: localData.envId,
				// 	traceUser: true,
				// })
				// wx.cloud.callFunction({
				await localData.cloud_shared.init()
				await localData.cloud_shared.callFunction({
					name: 'popupSQL', //云函数的名称
					data: {
						functionName: "judgeShowPopup", //调用哪个数据库函数
						usingDatabase: localData.usingDatabase,
						groupKind: 'notice',
						schoolId: this.currentSchoolId,
						unionId: this.userInfos.unionId,
					},
					success: res => {
						let code = res.result.code
						// that.popupConfig = res.result.configInfo
						that.popupUrl = res.result.configInfo.notice_group_config.url;
						that.popupImage = res.result.configInfo.notice_group_config
							.image;
						that.popupText = res.result.configInfo.notice_group_config
							.text;
						console.log(res.result, code, that.noNoticeDays)
						// 判断是否在群聊里
						if (code == 0) {
							console.log('通知群弹窗关闭')
						} else if (code == 10) {
							console.log('已在群')
						} else if (code == 20) {
							if (res.result.hasNoNoticeRecord == 1) {
								this.havingRecord = 1
							}
							this.shareMaskState = 2;
							// this.modalName = 'groupChat';
							console.log('弹窗')
						} else if (code == 30) {
							console.log('参加打卡活动')
						} else if (code == 40) {
							console.log('24小时内点击过不再提醒')
						}
					},
					fail: res => {
						that.setShowDetailPageNum();
					}
				})
			},
			//关闭加入群聊弹窗
			hideModal() {
				this.modalName = '';
				// if (this.popupChecked == true) {
				// 	this.noRemindAgain();
				// }
			},
			//不再提醒按钮(云函数版)
			async noRemindAgain() {
				if (this.havingRecord == 0) {
					// wx.cloud.init({
					// 	env: localData.envId,
					// 	traceUser: true,
					// })
					// wx.cloud.callFunction({
					await localData.cloud_shared.init()
					await localData.cloud_shared.callFunction({
						name: 'popupSQL', //云函数的名称
						data: {
							functionName: "add", //调用哪个数据库函数
							unionId: this.userInfos.unionId,
							userId: this.userInfos.userId,
							nickName: this.userInfos.nickName, //前
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log(nowdata)
						}
					})
				} else {
					// wx.cloud.init({
					// 	env: localData.envId,
					// 	traceUser: true,
					// })
					// wx.cloud.callFunction({
					await localData.cloud_shared.init()
					await localData.cloud_shared.callFunction({
						name: 'popupSQL', //云函数的名称
						data: {
							functionName: "updateTime", //调用哪个数据库函数
							unionId: this.userInfos.unionId,
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log(nowdata)
						}
					})
				}
			},
			checkboxChange(e) {
				console.log(e)
			},
			//关闭发帖分享蒙层
			hidemask1() {
				this.shareMaskState = 0;
			},
			//关闭加群蒙层
			hidemask2() {
				this.shareMaskState = 0;
				if (this.popupChecked == true) {
					// this.noRemindAgain();
					this.onTabNoRemindAgain();
				}
			},
			returnFirstPage() {
				uni.reLaunch({
					url: "../index/index",
				})
			},
			//判断是否是第一次阅读
			async isFirstRead() {
				let form = {
					userId: this.userInfos.userId,
					contentId: this.currentContent.contentId,
				}
				if (form.userId == '' || form.userId == null) {
					console.log("userId缺失")
					return
				}
				if (form.contentId == '' || form.contentId == null) {
					console.log("contentId缺失")
					return

				}
				const data = await isFirstReadNew(form)
				if (data.code === 200) {
					console.log("第一次阅读", data)
					if (data.data) {
						console.log(data.data.contentState)
						this.setCurrentContentState(data.data.contentState)
					}
				} else {
					console.log('非第一次阅读');
				}
			},
			//隐藏点赞提示
			hideTip() {
				this.tipState = 0
			},
			//获取积分
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
						title: '评论成功，积分+15,现有积分' + this.nowScore,
						duration: 3000
					})
				} else {

				}

			},
			//长按删除
			deleteAction(comment) {
				// this.copyAndPaste(0,this,comment.commentText)
				// console.log('当前帖子：' + content.contentId)
				this.currentComment = comment
				if (this.userInfos.userId == comment.userId) {
					this.deleteActionList = [{
							text: '删除',
							color: 'red',
							key: "delete"
						},
						{
							text: '复制',
							color: 'black',
							key: "copy"
						}
					]
					this.showDeleteAction = true;
				} else {
					if (this.currentUserType == 3) {
						this.deleteActionList = [{
								text: '复制',
								color: 'black',
								key: "copy"
							}, {
								text: '举报',
								color: 'red',
								key: "inform"
							},
							{
								text: '拉黑TA',
								color: 'black',
								key: "block"
							},
							{
								text: '评论管理',
								color: 'black',
								key: 'commentManage'
							}
						]
					} else if (comment.userIdentify != 10 && this.currentContent.isSpecial != 1 && this.currentContent
						.isSpecial != 4) {
						this.deleteActionList = [{
								text: '复制',
								color: 'black',
								key: "copy"
							}, {
								text: '举报',
								color: 'red',
								key: "inform"
							},
							{
								text: '拉黑TA',
								color: 'black',
								key: "block"
							}
						]
					} else {
						this.deleteActionList = [{
							text: '复制',
							color: 'black',
							key: "copy"
						}, {
							text: '举报',
							color: 'red',
							key: "inform"
						}]
					}
					this.showDeleteAction = true;
				}
			},
			//点击删除按键
			async onDeleteAction(index) {
				let that = this
				console.log(`点击内容为：${this.deleteActionList[index].key}`)
				let key = this.deleteActionList[index].key;
				let title = ''
				let changeForm = {
					userId: this.userInfos.userId,
					commentId: this.currentComment.commentId,
					commentState: 0,
				}
				if (key == 'delete') {
					title = '确认删除吗？'
					changeForm.commentState = 4
					uni.showModal({
						title: title,
						success: async function(res) {
							if (res.confirm) {
								//console.log('用户点击确定');
								const data = await editCommentState(changeForm);
								that.createOperationRecord(3, changeForm)
								if (data.code === 200) {
									//重新请求评论列表
									const data = await reqHomeComments({
										pageNum: 1,
										pageSize: that.pageSize,
										userId: that.form.userId,
										contentId: that.currentContent.contentId,
										requestType: that.requestType
									})
									if (data.code === 200) {
										that.handleImg(data);
										for (var i = 0; i < data.data.length; i++) {
											//将头像制为空，等待本地缓存图片地址返回
											data.data[i].headimgTempUrl = data.data[i].headimgUrl
												.toString()
											data.data[i].headimgUrl = ''
											//处理子评论头像
											if (data.data[i].children && data.data[i].children.length >
												0) {
												for (var j = 0; j < data.data[i].children.length; j++) {
													data.data[i].children[j].headimgTempUrl = data.data[i]
														.children[j].headimgUrl.toString()
													data.data[i].children[j].headimgUrl = ''
												}
											}
										}
										that.comments = data.data
										console.log('评论列表', that.comments);
										//评论头像缓存处理
										for (var i = 0; i < that.comments.length; i++) {
											that.updateCommentHeadUrls(that.comments[i])
										}
									} else {
										console.log('评论列表获取失败');
									}
									uni.showToast({
										title: '操作成功！',
										icon: 'none'
									})
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
				}
				if (key == 'copy') {
					console.log('复制', this.currentComment);
					this.copyAndPaste(0, this, this.currentComment.commentText)
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
						targetId: this.currentComment.commentId,
					}
					console.log(params)
					const data = await ifAlreadyInformThisTarget(params)
					if (data.code == 200) {
						if (data.data == 1) {
							uni.showToast({
								title: "您已举报过该评论，无需重复举报",
								icon: "none"
							})
							return
						}
					}
					console.log('举报', this.currentComment);
					this.informInfo = this.informConfig.inform_reasons[0]
					this.detailInformInfo = ''
					this.modalName = 'informModal'
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
					this.addBlockRecordByUser(this.userInfos.userId, this.currentComment.userId);
					return
				}
				if (key == 'commentManage') {
					this.showManageModal = 1
					this.manageKind = 2 //评论管理
					this.manageUserId = this.currentComment.userId
					this.manageComment = this.currentComment
					return
				}

			},
			//创建操作记录
			async createOperationRecord(targetKind, changeForm) {
				console.log("触发")
				let operatinForm = {
					schoolId: this.currentSchoolId,
					recordState: 1,
					operator: this.userInfos.userId,
					targetKind: targetKind,
					targetId: this.currentComment.commentId,
					changeInfo: null,
					note: null,
				}
				//操作对象为评论
				operatinForm.changeInfo = {
					data: {
						oldForm: {
							contentid: this.currentContent.contentId,
							commentState: 0
						},
						newForm: {
							contentid: this.currentContent.contentId,
							commentState: changeForm.commentState
						}
					}
				};
				//添加备注
				if (changeForm.commentState == 4) {
					operatinForm.note = "用户改评论为下架"
				}
				operatinForm.changeInfo = JSON.stringify(operatinForm.changeInfo)
				console.log(operatinForm)
				const data = await addOperationRecord(operatinForm)
				if (data.code == 200) {
					console.log(data.data)
				}

			},
			//获取用户预留的联系方式
			async copyContactInfo(content) {
				// let permissionCheckResult = await this.permissionCheckOutBBS(this.userInfos.userId, this
				// 	.currentSchoolId,
				// 	1)
				// if (permissionCheckResult != 0) {
				// 	return
				// }
				uni.showLoading({
					title: '获取联系方式中...',
					mask: true,
				})
				//先进行隐私授权判断
				if (this.privacyPopup.needAuthorization) {
					let needPrivacyAuthorization = await this.judgePrivacySetting()
					// console.log(needPrivacyAuthorization)
					if (needPrivacyAuthorization == true) {
						uni.hideLoading();
						return
					}
				} else {
					console.log("已授权，不需要调用")
				}
				let permissionCheckResult = await this.permissionCheckNew(this.userInfos.userId,
					this
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
				let blockedState = await this.judgeBeBlockByUser(this.userInfos.userId, this.currentContent.userId)
				if (blockedState == 1) {
					uni.hideLoading()
					uni.showToast({
						title: '您已被该用户拉黑',
						icon: 'none'
					})
					return
				}
				// this.currentContent = content
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
			returnIndex() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			},
			//用户点击马住和弃坑
			async changeMark(isMark, content) {
				if (this.currentSchoolState == 3 && this.isLocalUser == 1) {
					console.log(this.isLocalUser, this.currentSchoolState)
					uni.showToast({
						icon: 'none',
						title: '请认证后使用'
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
						if (data.data.isMark == 1) {
							console.log("触发马住")
							localData.requestMessage([7, 9, 10])
							content.isMark = 1
							this.addPostToMarkMessageList(content)
							this.currentContent.markCount = this.currentContent.markCount + 1
							// if (this.currentContent.markCount==1) {
							// 	console.log("呀哈哈")
							// 	let that=this
							// 	setTimeout(function() {
							// 		that.changeBackImageHeight()
							// 	}, 2000);
							// }
						} else if (data.data.isMark == 0) {
							console.log("触发弃坑")
							content.isMark = 0
							this.delPostInMarkMessageList(content)
							this.currentContent.markCount = this.currentContent.markCount - 1
						}
						this.setMarkState({
							'contentId': content.contentId,
							'isMark': isMark
						})
						//触发上一个页面的修改mark状态
						let pages = getCurrentPages();
						let prevPage = pages[pages.length - 2];
						prevPage.$vm.editMarkState({
							"isMark": isMark,
							"contentId": content.contentId,
						})

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
			//改变马住消息已读状态
			async editMarkReadState(contentId) {
				const params = {
					contentId: contentId,
					userId: this.userInfos.userId
				}
				const data = await editMarkRead_1(params)
				console.log(data)
				if (data.code === 200) {
					console.log("马住已读", data.data)
					if (data.data.isRead == 1) {
						console.log("触发已读")
						this.changeMarkMessageReadState(contentId)
					}
				} else {
					console.log('触发已读失败');
				}
			},
			//广告相关
			async getSmallAdvList() {
				let form = {
					schoolId: this.currentSchoolId,
					advPosition: "postDetail"

				}
				const data = await getAdvList(form);
				console.log("触发广告获取", data)
				if (data.code === 200) {
					if (data.data.length > 0) {
						for (var i = 0; i < data.data.length; i++) {
							// data.data[i].image = data.data[i].imageSmall
							data.data[i].image = ''
						}
						this.swiperList = data.data
					} else {

					}
					console.log("广告列表", this.swiperList);
					//广告图片缓存处理
					for (var i = 0; i < this.swiperList.length; i++) {
						this.updateAdvImageUrls(this.swiperList[i])
					}
					//缓存处理结束
				} else {
					console.log("广告列表请求失败");
				}

			},
			//改版后广告
			async getSmallAdvList0507(position) {
				let form = {
					schoolId: this.currentSchoolId,
					advPosition: position
				}
				const data = await getAdvList0507(form);
				console.log("触发广告获取", data)
				if (data.code === 200) {
					// console.log("广告列表", data);
					if (data.data && data.data.length > 0) {
						//图片处理,寻找该位置对应图片
						for (let i = 0; i < data.data.length; i++) {
							if (data.data[i].imageUrls) {
								let currentAdvImage = data.data[i].imageUrls.split(',');
								let keyWord = 'position_' + position
								const imageUrl = currentAdvImage.find(item => item.includes(keyWord));
								if (imageUrl) {
									data.data[i].image = imageUrl
									// console.log(i,data.data[i].image)
								}
							}
						}
						this.swiperList = data.data
						//广告图片缓存处理
						for (let i = 0; i < this.swiperList.length; i++) {
							this.updateAdvImageUrls(this.swiperList[i])
						}
						//缓存处理结束
					}

				} else {
					console.log("广告列表请求失败");
				}

			},
			//点击广告
			onTabAdv(index) {
				console.log(index, this.swiperList[index].id)
				let that = this
				//1：跳转广告展示页；2：跳转广告展示页（含报名）
				if (this.swiperList[index].kind == 1 || this.swiperList[index].kind == 2) {
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/adv?id=' + this.swiperList[index].id
					})
				}
				//跳转其他小程序 3
				if (this.swiperList[index].kind == 3) {
					let form = {
						id: this.swiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					// #ifdef MP-WEIXIN
					wx.navigateToMiniProgram({
						appId: that.swiperList[index].miniAppId,
						path: that.swiperList[index].miniAppPage,
						envVersion: 'release',
						success(res) {
							// 打开成功
							console.log('函数执行')
						}
					})
					// #endif
				}
				//跳转本小程序 4
				if (this.swiperList[index].kind == 4) {
					let form = {
						id: this.swiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					console.log(that.swiperList[index].miniAppPage)
					uni.navigateTo({
						url: that.swiperList[index].miniAppPage
					})
				}
				//跳转网页 5
				if (this.swiperList[index].kind == 5) {
					let form = {
						id: this.swiperList[index].id,
						userId: this.userInfos.userId,
					}
					getOneAdvById(form);
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/web-view?url=' + this.swiperList[index].miniAppPage
					})
				}
				//跳转多业务广告页面
				if (this.swiperList[index].kind == 6) {
					this.setShowAdNum()
					uni.navigateTo({
						url: '/package_task/pages/bbs/more/multi-business-adv?id=' + this.swiperList[index].id
					})
				}
			},
			//点击加入社群
			async onTabJoinGroup() {
				let that = this
				await localData.cloud_shared.init()
				return new Promise((resolve, reject) => {
					localData.cloud_shared.callFunction({
						name: 'functionConfigSQL', //云函数的名称
						data: {
							functionName: 'joinGroupChat', //调用哪个数据库函数
							schoolId: this.currentSchoolId, //前
							joinGroupInfo: {},
							usingDatabase: localData.usingDatabase
						},
						complete: res => {
							let nowdata = res.result
							console.log("进入群聊配置", nowdata)
							if (nowdata) {
								that.popupUrl = nowdata.joinGroupInfo.notice_group_config.url;
								that.popupImage = nowdata.joinGroupInfo.notice_group_config.image;
								that.popupText = nowdata.joinGroupInfo.notice_group_config.text;
								that.handleJoinGroup = 1
								that.shareMaskState = 2;
								resolve(nowdata)

							}
						}
					})
				})
			},
			async onTabJoinGroup0909() {
				let that = this
				console.log("hhh ")
				//获取启动项配置
				let param = {
					schoolId: this.currentSchoolId,
					configType: 'QRCodeConfig'
				}
				let data = await getOneBbsConfig(param)
				let QRCodeId = ''
				if (data.code == 200) {
					let configList = JSON.parse(data.data.configJson).data
					console.log(configList)
					if (configList && configList.length > 0) {
						QRCodeId = configList[0].QRCodeId
					}

				}
				uni.navigateTo({
					url: '/package_task/pages/group-code/group-code?schoolId=' + this.currentSchoolId +
						'&QRCodeId=' + QRCodeId
				})

			},
			//请求改变（评论排列方式改变）
			async requestTypeChange(index) {
				this.curRequestType = index;
				this.requestType = this.requestTypeList[index].value
				const commentData = await reqHomeComments({
					pageNum: 1,
					pageSize: this.pageSize * this.pageNum,
					userId: this.form.userId,
					contentId: this.currentContent.contentId,
					requestType: this.requestType
				})
				if (commentData.code === 200) {
					this.handleImg(commentData)
					// this.comments = commentData.data
					//将头像制为空，等待本地缓存图片地址返回
					for (var i = 0; i < commentData.data.length; i++) {
						commentData.data[i].headimgTempUrl = commentData.data[i].headimgUrl.toString()
						commentData.data[i].headimgUrl = ''
						//处理子评论头像
						if (commentData.data[i].children && commentData.data[i].children.length > 0) {
							for (var j = 0; j < commentData.data[i].children.length; j++) {
								commentData.data[i].children[j].headimgTempUrl = commentData.data[i].children[j]
									.headimgUrl.toString()
								commentData.data[i].children[j].headimgUrl = ''
							}
						}
					}
					this.comments = commentData.data
					console.log('评论列表获取成功', this.comments);
					//评论头像缓存处理
					for (var i = 0; i < this.comments.length; i++) {
						this.updateCommentHeadUrls(this.comments[i])
					}
				} else {
					console.log('评论列表获取失败');
				}
			},
			//获取更多子评论
			async getMoreChildrenComment(item, index) {
				const childrenComment = {
					pageNum: 0,
					pageSize: 5,
					userId: this.userInfos.userId,
					contentId: this.currentContent.contentId,
					commentId: item.commentId,
					queryTime: ''
				}
				let time = ''
				let currentChildren = this.comments[index].children
				if (this.comments[index].children.length > 0) {
					time = new Date(currentChildren[currentChildren.length - 1].createTime);
				} else {
					time = new Date(this.comments[index].createTime)
				}
				const year = time.getFullYear(); // 年份
				const month = time.getMonth() + 1; // 月份（注意要加1，返回的月份范围是0-11）
				const day = time.getDate(); // 日期
				const hour = time.getHours(); // 小时
				const minute = time.getMinutes(); // 分钟
				const second = time.getSeconds(); // 秒钟
				childrenComment.queryTime = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
				console.log(currentChildren, time, childrenComment.queryTime)
				const data = await reqChildrenCommentPage(childrenComment)
				if (data.code == 200) {
					this.handleImg(data)
					for (var i = 0; i < data.data.length; i++) {
						//将头像制为空，等待本地缓存图片地址返回
						data.data[i].headimgTempUrl = data.data[i].headimgUrl.toString()
						data.data[i].headimgUrl = ''
					}
					this.comments[index].children.push(...data.data)
					this.comments[index].childrenShowState = 1
					if (data.data.length < 5) {
						this.comments[index].haveMoreChildren = 0
					} else {

					}
				}
				console.log(data)
				//评论头像缓存处理
				for (var i = 0; i < data.data.length; i++) {
					this.updateCommentHeadUrls(data.data[i])
				}
			},
			//改变子评论显示状态
			changeChildrenCommentState(item, value) {
				item.childrenShowState = value
			},
			async judgePrivacy() {
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
			},
			//举报相关
			chooseInformImg(lists) {
				this.tempInformUrls = lists.map(item => item.url)
			},
			changeInformImg(lists) {
				this.tempInformUrls = lists.map(item => item.url)
			},
			async onSubmitInform() {
				if (this.userInfos.userId == '' || this.userInfos.userId == null) {
					uni.showToast({
						title: '当前登录用户状态异常！',
						icon: 'none'
					})
					return
				}
				// if (this.informInfo == '') {
				// 	uni.showToast({
				// 		title: '请输入举报原因！',
				// 		icon: 'none'
				// 	})
				// 	return
				// }
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
					//原始图片压缩
					// const compressResult = await localData.compressionIamge(this, tempInformUrls[i]);
					//console.log(compressResult)
					//const compressResult = await localData.compressionIamgeWX(this, tempContentUrls[i]);
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
				let commentText = this.currentComment.commentText.slice(0, 100)
				let informForm = {
					userId: this.userInfos.userId,
					informedUserId: this.currentComment.userId,
					contentId: this.currentComment.contentId,
					targetId: this.currentComment.commentId,
					targetText: commentText,
					informInfo: this.informInfo + " " + this.detailInformInfo,
					informUrl: oosImgUrl.join(','),
					isContent: 0,
					schoolId: this.currentSchoolId
				}
				let res = await informContentMoreInfo(informForm)
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
			informRadioChange(e) {
				this.informInfo = e.target.value;
			},
			//点击帖子右上角三个点
			contentAction() {
				console.log('当前帖子：', this.currentContent)
				if (this.currentContent.userId == this.userInfos.userId) {
					if (this.currentContent.contentState != 6) {
						this.contentActionList = [{
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
						this.contentActionList = [{
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
						this.contentActionList = [{
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
					} else if (this.currentContent.userIdentify != 10 && this.currentContent.isSpecial != 1 && this
						.currentContent.isSpecial != 4) {
						this.contentActionList = [{
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
						this.contentActionList = [{
							text: '举报',
							color: 'red',
							key: 'inform'
						}]
					}

				}
				this.showCotnentAction = true;
			},
			//点击选项
			async onContentAction(index) {
				console.log(`点击内容为：${this.contentActionList[index].key}`)
				let key = this.contentActionList[index].key;
				let title = ''
				let changeState = 0
				let changeForm = {
					userId: this.userInfos.userId,
					contentId: this.currentContent.contentId,
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
						targetId: this.currentContent.contentId
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
					this.modalName = 'informContentModal'
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
					if (this.isFollow == 1) {
						this.changeFollow()
					}
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
								that.createContentOperationRecord(2, changeForm)
								setTimeout(function() {
									uni.reLaunch({
										url: '../../pages/index/index'
									})
								}, 500)

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
			ontabShare() {
				this.showShareAction = true;
				this.showShareMask = true;
			},
			onClickSharePart(index) {
				if (index == 0) {
					console.log('toFriend')
					this.showShareMask = false
					this.showShareAction = false
				}
				if (index == 1) {
					console.log('toTimeLine')
					this.showTimeLineImage = true
					this.showShareAction = false
				}
			},
			onClickShareCancel() {
				this.showTimeLineImage = false;
				this.showShareAction = false;
				this.showShareMask = false
			},
			async createContentOperationRecord(targetKind, changeForm) {
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
			async onSubmitInformContent() {
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
					//原始图片压缩
					// const compressResult = await localData.compressionIamge(this, tempInformUrls[i]);
					//console.log(compressResult)
					//const compressResult = await localData.compressionIamgeWX(this, tempContentUrls[i]);
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
					contentId: this.currentContent.contentId,
					targetId: this.currentContent.contentId,
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
			//缓存帖子中的图片（头像和帖子图片），并更新store中的图片url
			async updatePostcontentUrls(post) {
				//处理头像
				//本地默认头像不需要缓存
				if (post.headimgTempUrl.includes("/static/headImg")) {
					console.log("本地默认头像不需要缓存")
					post.headimgUrl = post.headimgTempUrl
				}
				//匿名头像缓存
				else if (post.isSpecial == 1 || post.isSpecial == 4) {
					post.headimgUrl = await this.getImageLocalUrl('anonymousAvatar', post.headimgTempUrl)
				}
				//普通头像缓存
				else {
					post.headimgUrl = await this.getImageLocalUrl('non-anonymousAvatar', post.headimgTempUrl)
				}
				//处理帖子图片
				if (post.contentTempUrls && post.contentTempUrls.length > 0) {
					for (var i = 0; i < post.contentTempUrls.length; i++) {
						let imageLocalurl = await this.getImageLocalUrl('postImage', post.contentTempUrls[i])
						post.contentUrls.push(imageLocalurl)
					}
				}

			},
			//缓存广告中的图片，并返回本页面广告中的图片本地url
			async updateAdvImageUrls(adv) {
				//将网络图片缓存到本地，并返回本地地址
				// if (adv.imageSmall) {
				// 	adv.image = await this.getImageLocalUrl('advImage', adv.imageSmall)
				// }
				if (adv.image) {
					adv.image = await this.getImageLocalUrl('advImage', adv.image)
				}
				// console.log("第个广告",i,data.data[i])
				//缓存结束
			},
			//缓存评论中的头像，并返回本地头像url
			async updateCommentHeadUrls(comment) {
				// console.log(this.currentContent)
				//处理主评论头像
				//本地默认头像不需要缓存
				if (comment.headimgTempUrl.includes("/static/headImg")) {
					console.log("本地默认头像不需要缓存")
					comment.headimgUrl = comment.headimgTempUrl
				}
				//匿名头像缓存
				else if (this.currentContent.isSpecial == 1 || this.currentContent.isSpecial == 4) {
					comment.headimgUrl = await this.getImageLocalUrl('anonymousAvatar', comment.headimgTempUrl)
				}
				//普通头像缓存
				else {
					comment.headimgUrl = await this.getImageLocalUrl('non-anonymousAvatar', comment.headimgTempUrl)
				}
				//处理子评论头像
				if (comment.children && comment.children.length > 0) {
					for (var i = 0; i < comment.children.length; i++) {
						if (comment.children[i].headimgTempUrl.includes("/static/headImg")) {
							console.log("本地默认头像不需要缓存")
							comment.children[i].headimgUrl = comment.children[i].headimgTempUrl
						}
						//匿名头像缓存
						else if (this.currentContent.isSpecial == 1 || this.currentContent.isSpecial == 4) {
							comment.children[i].headimgUrl = await this.getImageLocalUrl('anonymousAvatar', comment
								.children[i].headimgTempUrl)
						}
						//普通头像缓存
						else {
							comment.children[i].headimgUrl = await this.getImageLocalUrl('non-anonymousAvatar', comment
								.children[i].headimgTempUrl)
						}
					}
				}

			},
			onAlumniOnlyExamine() {
				if (this.currentUserState == 1) {
					uni.showModal({
						title: "认证提示",
						content: '身份审核中，请耐心等待或联系客服',
						showCancel: false,
						success: async function(res) {
							if (res.confirm) {
								console.log('用户点击确定');
								uni.switchTab({
									url: '/pages/index/index'
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					})
				}
				if (this.currentUserState == 3) {
					uni.showModal({
						title: "认证提示",
						content: '您已在其他学校认证，请联系客服核实校友身份',
						showCancel: false,
						success: async function(res) {
							if (res.confirm) {
								console.log('用户点击确定');
								uni.switchTab({
									url: '/pages/index/index'
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					})
				}
				if (this.currentUserState == 0) {
					this.alumniOnlyExamine = 1
				}
			},
			returnHideManage(val) {
				this.showManageModal = false;
			},
			goBack() {
				// 使用 uni.navigateBack 返回上一页
				uni.navigateBack({
					delta: 1
				});
			},
			comeback() {
				let pages = getCurrentPages();
				if (pages.length > 1) {
					// 如果有上一页，就返回上一页
					this.goBack();
				} else {
					// 如果没有上一页，则返回首页
					uni.reLaunch({
						url: '/pages/index/index'
					});
				}
			},
			changeBackImageHeight() {
				let that = this
				// that.haveChangeBackImageHeight = 1
				const query = uni.createSelectorQuery().in(this); //这样写就只会选择本页面组件的类名box的
				query.selectAll('.partition').boundingClientRect(
					data => { //回调函数，data中存储的是这些元素节点（每个节点的信息存为一个对象）的位置信息
						// 获取状态栏的高度
						const systemInfo = uni.getSystemInfoSync();
						const statusBarHeight = systemInfo.statusBarHeight;

						let scale = uni.upx2px(100) / 100;
						let imageHeight = (80 + 66 + data[0].top) / scale
						let imageHeight2 = (data[0].bottom) / scale
						that.backImageHeight = imageHeight2 + "rpx"
						// console.log("onready触发,哈哈哈", data, imageHeight, that.backImageHeight)
					}).exec();
			},
			calculateScrollViewHeight() {
				const systemInfo = uni.getSystemInfoSync();
				const screenHeight = systemInfo.screenHeight;
				this.scrollViewHeight = screenHeight - systemInfo.statusBarHeight - uni.upx2px(120);
				console.log("scrollView高度", this.scrollViewHeight)
			},
			scrollToComment() {
				// console.log("滚动")
				this.$nextTick(() => {
					console.log("滚动1", this.highLightCommentId)
					uni.createSelectorQuery().in(this).select("#comment-" + this.highLightCommentId)
						.boundingClientRect((res) => {
							// console.log("滚动1", res);
							if (res) {
								const windowHeight = uni.getSystemInfoSync().windowHeight;
								const scrollTop = res.top - (windowHeight / 2) + (res.height / 2);
								uni.pageScrollTo({
									scrollTop: scrollTop,
								})
							}
						}).exec()
				})
			},
			drawMyCanvas() {
				const that = this
				const query = uni.createSelectorQuery().in(this);
				query.select('#my-canvas').fields({ // 选择需要生成canvas的范围
					size: true,
					scrollOffset: true
				}, data => {
					console.log(data, 'data');
					that.posterWide = data.width;
					that.posterHeight = data.height;
					// that.setData({
					// 	width,
					// 	height
					// })
					setTimeout(() => {
						that.startDraw()
					}, 1500);
				}).exec()
			},
			startDraw() {
				let that = this
				// 创建wxml2canvas对象
				let drawMyImage = new wxml2canvas({
					element: 'canvas', // canvas的id,
					obj: that, // 传入当前组件的this
					width: that.posterWide * 2,
					height: that.posterHeight * 2,
					// background: '#fff', // 生成图片的背景色
					logging: false, //日志
					scrollY: 0,
					scrollX: 0,
					useCORS: true, //跨域
					allowTaint: true,
					progress(percent) { // 进度
						// console.log(percent);
					},
					finish(url) { // 生成的图片
						console.log(url);
						wx.hideLoading()
						that.savePoster(url)
					},
					error(res) { // 失败原因
						console.log(res);
						wx.hideLoading()
					}
				}, this);
				let data = {
					// 获取wxml数据
					list: [{
						type: 'wxml',
						class: '.my_canvas .my_draw_canvas', // my_canvas要绘制的wxml元素根类名， **my_draw_canvas**单个元素的类名（所有要绘制的单个元素都要添加该类名）
						limit: '.my_canvas', // 要绘制的wxml元素根类名
						x: 0,
						y: 0
					}]
				}
				// 绘制canvas
				drawMyImage.draw(data, this);
			},
			// 保存
			savePoster(url) {
				const that = this
				wx.saveImageToPhotosAlbum({
					filePath: url,
					success: function() {
						wx.showToast({
							title: '已保存至相册',
							icon: 'none',
							duration: 1500
						});
					},
					fail(err) {
						if (err.errMsg === "saveImageToPhotosAlbum:fail:auth denied" || err.errMsg ===
							"saveImageToPhotosAlbum:fail auth deny" || err.errMsg ===
							"saveImageToPhotosAlbum:fail authorize no response") {
							wx.showModal({
								title: '提示',
								content: '需要您授权保存相册',
								showCancel: false,
								success: modalSuccess => {
									wx.openSetting({
										success(settingdata) {
											if (settingdata.authSetting[
													'scope.writePhotosAlbum']) {
												wx.saveImageToPhotosAlbum({
													filePath: that.data.imgUrl,
													success: function() {
														wx.showToast({
															title: '保存成功',
															icon: 'success',
															duration: 2000
														})
													},
												})
											} else {
												wx.showToast({
													title: '授权失败，请稍后重新获取',
													icon: 'none',
													duration: 1500
												});
											}
										}
									})
								}
							})
						}
					}
				})
			},
		},
		onShareTimeline() {
			this.showShareMask = false;
			this.showTimeLineImage = false;
			let imgURL;
			if (this.currentContent.contentUrls.length > 0) {
				imgURL = this.currentContent.contentUrls[0];
			}
			return {
				title: this.currentContent.title,
				imageUrl: imgURL,
				// query: 'id=' + this.currentContent.contentId
				path: '/pages/index/index?contentId=' + this.currentContent.contentId + "&currentSchoolId=" + this
					.currentContent.schoolId,
			};
		},
		onShareAppMessage(res) {
			console.log("触发")
			if (res.from === 'button') {
				// 来自页面内分享按钮
				console.log(res.target);
			}
			let imgURL;
			if (this.currentContent.contentUrls.length > 0) {
				imgURL = this.currentContent.contentUrls[0];
			}
			//分享加分相关
			this.fisrtShareMiniApp(this.userInfos.userId, this.currentSchoolId)
			//path: '/pages/post/detail?id=' + this.currentContent.contentId + "&currentSchoolId=" + this.currentSchoolId,
			return {
				title: this.currentContent.title,
				path: '/pages/index/index?contentId=' + this.currentContent.contentId + "&currentSchoolId=" + this
					.currentContent.schoolId,
				imageUrl: imgURL
			};
		},
		watch: { //监听userid变化，防止阅读人数统计不准确
			userInfos: {
				async handler(newVal, oldVal) {
					// #ifdef MP-WEIXIN
					if (this.userInfos != null && this.userInfos != '') {
						await this.isFirstRead()
						this.getUserStateBySchool()
					}
					// #endif

				},
				//deep: true
			}
		},
		async onReachBottom() {
			if (this.loadStatus == 'loadmore') {
				this.loadStatus = 'loading'
				await this.getMore()
			}
		}




	};
</script>

<style lang="scss" scoped>
	.page-content {
		position: relative;
		min-height: 100vh;
		// background-color: #F5F5F5;
		overflow: hidden;
	}

	.page-background {
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		// background-image: url('/static/newUI/index-back.jpg');
		background-image: url('/static/newUI/detail-back-blue.jpg');
		background-repeat: no-repeat;
		background-size: 100% 100%;
	}


	.background-image {
		width: 100%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 100%;
		z-index: -1;
		background-size: cover;
		background-repeat: no-repeat;
		// background-position: center center;
	}


	.notice-line {
		// padding: 0 26rpx;
		// margin-top: 26rpx;
		z-index: 1000;
		background-color: transparent;

		/deep/ .uicon-volume-fill {
			color: #000000 !important;
		}
	}

	.schoolInfo-text {
		display: flex;
		align-items: center;
		//justify-content: center;
		color: #ff0000;
		font-size: 24rpx;
		//background-color: #616161;
		padding-bottom: 10rpx;
		padding-top: 10rpx;

	}

	.post-title {
		margin-bottom: 10rpx;
		font-weight: bold;
		font-size: 32rpx;
	}

	.info-box {
		padding: 20rpx;
		padding-bottom: 5rpx;
		// background-color: #ffffff;
		background-color: transparent;
		position: relative;
	}

	.icon text {
		font-size: 12px;
		color: #999;
		margin-right: 20rpx;
	}

	/*关注*/

	.user-item {
		display: flex;

		.user-item-user {
			flex: 1;

			.cxplain {
				font-size: 12px;
				color: #999;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 1;
				overflow: hidden;
			}
		}

		image {
			width: 80rpx;
			height: 80rpx;
			float: left;
			margin-right: 10rpx;
			border-radius: 50%;
		}
	}

	/*底部操作*/
	.p-footer {
		margin: 30rpx 0;
		// margin-top: 20rpx;
		// margin-bottom: 20rpx;
		display: flex;
		justify-content: space-between;
		font-size: 26rpx;
		color: #616161;

		.p-item {
			// color: rgba(51, 51, 51, 0.6);
			color: #616161;
			display: flex;
			align-items: center;

			.iconfont {
				font-size: 40rpx;
			}

			&:nth-child(2) {
				// margin: 0 auto;
			}

			.iconfont {
				margin-right: 10rpx;
			}
		}
	}

	/*评论列表*/
	.comment-box {
		// background-color: #ffffff;
		//margin-top: 20rpx;
		padding: 20rpx;
		padding-top: 5rpx;
	}

	.comment-box>.title {
		margin-bottom: 20rpx;
	}

	.comment-item {
		display: flex;
		padding: 20rpx 10rpx 20rpx 0;

		&:active {
			background-color: #F5F5F5;
		}

		.c-content {
			display: flex;
			flex-direction: column;
			flex: 1;

			.time {
				color: #999;
				font-size: 24rpx;
			}

			.user {
				display: flex;

				.thumbs {
					margin-left: auto;
					display: flex;
					align-items: center;
					color: #bfbfbf;

					.num {
						margin-right: 10rpx;
					}
				}
			}
		}

		.avatar {
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
			border: 1px solid #dd524d;
			margin-right: 20rpx;
		}
	}

	.highlight {
		background-color: transparent;
		animation: fade-out 2s ease-in-out;
	}

	@keyframes fade-out {
		0% {
			background-color: #ffc869;
		}

		100% {
			background-color: transparent;
		}
	}

	.sub-comment-item {
		margin-left: 100rpx;
		padding: 20rpx;

		&:active {
			background-color: #F5F5F5;
		}

		.user {
			display: flex;
			align-items: center;

			.name {
				color: #616161;
			}

			.avatar {
				margin-right: 10rpx;
			}

			.u-head {
				flex: 1;
				display: flex;

				.thumbs {
					margin-left: auto;
					display: flex;
					align-items: center;
					color: #bfbfbf;

					.num {
						margin-right: 10rpx;
					}
				}
			}
		}

		.reply {
			.time {
				// margin-left: auto;
				font-size: 22rpx;
				color: #999;
			}

			.name {
				display: inline-block;
				color: #2b85e4;
				font-weight: 600;
			}
		}
	}

	/* 评论tool */
	.comment-tool {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		//width: 100%;
		background-color: #fff;
		padding: 20rpx;
		padding-bottom: 30rpx;
		display: block;
		z-index: 999;
		box-shadow: 0rpx -4rpx 8rpx 0rpx rgba(153, 153, 153, 0.1);
		// height: 150rpx;
	}

	// .comment-tool textarea {
	// 	background-color: #f5f5f5;
	// 	padding-top: 10rpx;
	// 	padding-left: 10rpx;
	// 	//padding-bottom: 0rpx;
	// 	border-radius: 10rpx;
	// 	min-height: 80rpx;
	// 	max-height: 200rpx;
	// }

	.input-style {
		width: 100%;
		background-color: #ffffff;
		padding-top: 10rpx;
		padding-left: 10rpx;
		//padding-bottom: 0rpx;
		border-radius: 10rpx;
		color: #999999;
		// min-height: 20rpx;
		//height: 60rpx;
		// min-height: 60rpx;
		// max-height: 300rpx;
	}

	.comment-tool .u-btn {
		margin-left: 10rpx;
	}


	/*文章图片*/
	.img-style-1 {
		display: block;
		width: 100%;
		max-height: 600rpx;
		border-radius: 5px;
	}

	.img-style-2 {
		display: flex;
	}

	.img-style-2 image {
		margin: 5rpx;
		border-radius: 5px;
		width: 100%;
		height: 294rpx;
	}

	.img-style-3 {
		display: flex;
		flex-wrap: wrap;
	}

	.img-style-3 image {
		width: 31.3%;
		height: 200rpx;
		margin: 1%;
		border-radius: 5px;
	}

	.img-style-8 {
		display: flex;
		flex-wrap: wrap;
	}

	.img-style-8 image {
		width: 48%;
		height: 200rpx;
		margin: 1%;
		border-radius: 5px;
	}

	.pre-box {
		display: flex;
	}

	.img-style-4 {
		position: relative;
		width: 150rpx;
		height: 150rpx;
		margin: 1%;
		border-radius: 5px;

		.iconfont {
			color: #5a5a5a;
			position: absolute;
			top: -10upx;
			right: -10upx;
			z-index: 1;
			background-color: #fff;
			border-radius: 50%;
		}
	}

	.img-style-5 {
		display: flex;
		flex-wrap: wrap;
	}

	.img-style-5 image {
		width: 175rpx;
		height: 175rpx;
		margin: 1%;
		border-radius: 5px;
	}

	.img-style-6 {
		position: relative;
		width: 100%;

		.iconfont {
			color: #5a5a5a;
			position: absolute;
			top: 20upx;
			right: 20upx;
			z-index: 1;
			background-color: #fff;
			border-radius: 50%;
		}
	}

	// 圈子信息
	.topic-info {
		display: flex;
		align-items: center;
		font-size: 24rpx;
		background-color: #f5f5f5;
		margin: 20rpx 0;

		.cover {
			width: 100rpx;
			height: 100rpx;
			margin-right: 20rpx;
		}

		.center {
			flex: 1;
			font-size: 24rpx;

			.count-txt {
				color: #999;
			}
		}

		.right {
			margin-left: 20rpx;
			color: #f29100;
		}
	}

	// 加入群聊
	.join-group {
		display: flex;
		align-items: center;
		font-size: 24rpx;
		background-color: #f5f5f5;
		margin-top: 10rpx;
		padding: 10rpx 20rpx;

		.center {
			flex: 1;

			.count-txt {
				color: #999;
				//font-weight: bold;
			}
		}

		.right {
			margin-left: 20rpx;
			color: #f29100;
			font-weight: bold;
		}
	}

	// 加入群聊
	.join-group2 {
		display: flex;
		align-items: center;
		font-size: 24rpx;
		background-color: #fff;
		margin-top: 10rpx;
		padding: 10rpx 20rpx;

		.center {
			flex: 1;

			.count-txt {
				color: #999;
				//font-weight: bold;
			}
		}

		.right {
			margin-left: 20rpx;
			color: #f29100;
			font-weight: bold;
		}
	}

	// 分享弹窗
	.share-wrap {
		display: flex;
		padding: 30rpx;
		width: 60%;
		margin: 0 auto;

		.share-item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;

			&:nth-child(1) {
				margin-right: auto;
			}

			image {
				width: 100rpx;
				height: 100rpx;
			}

			text {
				font-size: 24rpx;
				margin-top: 20rpx;
			}
		}
	}

	//海报弹窗
	.canvas-box {
		height: 500px;
		position: relative;

		.footer {
			position: absolute;
			bottom: 20rpx;
			left: 20rpx;
			right: 20rpx;
		}
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

		.active {
			background-color: #333;
			color: #fff;
		}
	}

	.expire-box {
		background-color: #999;
		color: #fff;
		font-size: 24rpx;
		display: inline-block;
		padding: 0 20rpx;
		border-radius: 10rpx;
		margin-bottom: 20rpx;
	}

	video {
		width: 100%;
	}

	.post-text {
		white-space: pre-wrap;
		word-break: break-all;
		word-wrap: break-word;
		margin-bottom: 10rpx;
	}

	.c-txt {
		white-space: pre-wrap;
		word-break: break-all;
		word-wrap: break-word;
		font-size: 32rpx;
	}

	.shareAdd {
		background-color: rgba(0, 0, 0, 0.5);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 20%;
		right: 5%;
		width: 180rpx;
	}

	.shareIndex {
		background-color: rgba(0, 0, 0, 0.5);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 13%;
		right: 5%;
		width: 180rpx;
	}

	.shareGroup {
		background-color: rgba(0, 0, 0, 0.5);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 6%;
		right: 5%;
	}

	.sharePerson {
		background-color: rgba(0, 0, 0, 0.5);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 13%;
		right: 5%;
		width: 180rpx;
	}

	.sharePage {
		background-color: rgba(0, 0, 0, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: 0%;
		right: 5%;
	}

	.shareText {
		position: fixed;
		z-index: 1000;
		top: 11%;
		right: 0;
	}

	.qrcode {
		display: flex;
		flex-direction: column;
		width: 200rpx;
		height: 180rpx;
		position: fixed;
		z-index: 1000;
		top: 0;
		right: 0;
		align-items: center;
		background-color: #fff;
	}

	.comment-img-btn {
		margin-left: 10rpx;
		font-size: 60upx;
		margin-right: 10rpx;
	}

	.slot-btn {
		// width: 329rpx;
		// height: 140rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		background: #fff;
		margin-left: 10rpx;
		margin-right: 10rpx;
		border-radius: 10rpx;
	}

	.slot-btn__hover {
		background-color: rgb(235, 236, 238);
	}

	.modal-top {
		height: 60rpx;
		display: flex;
		justify-content: flex-end;
		padding-right: 20rpx;
		margin-bottom: 30rpx;
		background-color: #FFFFFF;
	}

	.service-container {
		// **容器样式想怎么写怎么写。当position为relative时，子容器的绝对定位基于父容器**
		position: relative;
	}

	.service-cell {
		width: 100%;
		position: absolute;
		top: 0;
		left: 0;
		z-index: 2;
		opacity: 0;
	}

	.page-mask {
		height: 100vh;
		width: 100vw;
		top: 0;
		left: 0;
		position: fixed;
		z-index: 1001;
		background-color: rgba(0, 0, 0, 0.8);
	}

	.mask-img {
		top: 25%;
		left: 15%;
		position: relative;
		width: 70%;

		.iconfont {
			color: #5a5a5a;
			position: absolute;
			top: 10upx;
			right: 10upx;
			z-index: 1;
			background-color: #fff;
			border-radius: 50%;
		}
	}

	.mask-img2 {
		position: absolute;
		top: 1%;
		right: 23%;
		width: 20%;
	}

	.mask-text {
		position: absolute;
		top: 17%;
		left: 35%;
		//margin-left: -25%;
		//right: 50%;
	}

	.mask-img3 {
		padding: 20rpx;
		border-radius: 16rpx;
		box-shadow: 0px 2px 8px 0px rgba(68, 68, 68, 0.15);
		left: 10%;
		position: relative;
		width: 80%;

		.iconfont {
			color: #5a5a5a;
			position: absolute;
			top: 20upx;
			right: 20upx;
			z-index: 1;
			background-color: #fff;
			border-radius: 50%;
		}
	}

	.add-modal {
		top: 30%;
		left: 10%;
		position: relative;
		width: 80%;
		text-align: center;
		background-color: #FFFFFF;
		padding: 30rpx;
		border-radius: 16rpx;

		.iconfont {
			color: #5a5a5a;
			position: absolute;
			top: 10upx;
			right: 10upx;
			z-index: 1;
			background-color: #fff;
			border-radius: 50%;
		}
	}

	// 分享类型弹窗
	.share-type {
		padding: 50rpx 30rpx;

		.type-item {
			background-color: #F5F5F5;
			padding: 20rpx;
			display: flex;
			justify-content: center;
			align-items: center;

			.icon {
				width: 40rpx;
				height: 40rpx;
				margin-right: 20rpx;
			}

			&:nth-child(2) {
				margin: 20rpx 0;
			}

			&:nth-child(3) {
				margin: 20rpx 0;
			}

			&:nth-child(4) {
				margin: 20rpx 0;
			}
		}
	}

	.tip {
		//background: rgba(255, 0, 0, 0.7);
		background: rgba(0, 0, 0, 0.7);
		color: #fff;
		position: absolute;
		border-radius: 4px;
		font-size: 26rpx;
		padding: 5px;
		min-width: 10px;
		word-wrap: break-word;
		display: inline-block;
		white-space: nowrap;
		z-index: 9;
		left: 61%;
		display: block;
	}

	.tip_icon {

		border-top: 6px solid transparent;
		border-bottom: 6px solid transparent;
		//border-right: 6px solid rgba(255, 0, 0, 0.4);
		border-right: 6px solid rgba(0, 0, 0, 0.7);
		left: -6px;
		top: 20px;
		width: 0;
		height: 0;
		position: absolute;
		z-index: 9;
	}

	.msg-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		min-height: 800rpx;

		.img {
			width: 200rpx;
			margin-top: 100rpx;
		}

		.txt {
			color: #999;
			font-size: 40rpx;
			margin-top: 20rpx;
		}
	}

	.reply_text {
		margin-left: 100rpx;
		padding: 5rpx 20rpx;
		font-size: 26rpx;
		color: #3a8de5;
		font-weight: bold;
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

	.check-examine-button {
		//margin-bottom: 20rpx;
		width: 50%;
		font-size: 35rpx;
		font-weight: bold;
		height: 70rpx;
		background-color: #028fab;
		color: #ffffff;
	}

	.detailAdd {
		position: fixed;
		z-index: 1000;
		bottom: 15%;
		right: 2.5%;
		width: 125rpx;
		height: 125rpx;
	}

	.add-page {
		// background-color: rgba(0, 0, 0, 0.4);
		background-color: rgba(24, 24, 24, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: calc(10% + 200rpx);
		right: 0;
		height: 80rpx;
		width: 80rpx;
		border-radius: 40rpx 0 0 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding-left: 5rpx;
	}

	.home-page {
		background-color: rgba(24, 24, 24, 0.6);
		color: #fff;
		position: fixed;
		z-index: 1000;
		bottom: calc(10% + 100rpx);
		right: 0;
		height: 80rpx;
		width: 80rpx;
		border-radius: 40rpx 0 0 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding-left: 5rpx;
	}

	.share-mask {
		height: 100vh;
		width: 100vw;
		top: 0;
		left: 0;
		position: fixed;
		z-index: 999;
		background-color: rgba(0, 0, 0, 0.8);
	}

	.timeline-image {
		width: 90vw;
		top: 150rpx;
		left: 5vw;
		position: fixed;
		z-index: 1001;
	}

	.menu {
		position: fixed;
		bottom: 0%;
		left: 50%;
		transform: translateX(-50%);
		background-color: #fff;
		border-radius: 32rpx 32rpx 0 0;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
		z-index: 1000;
		width: 100%;
	}

	.menu-item {
		padding: 20rpx;
		text-align: center;
		font-size: 32rpx;
		color: #333;
	}

	.menu-item-button {
		padding: 10rpx;
	}


	.menu-item:first-child {
		border-top-left-radius: 32rpx;
		border-top-right-radius: 32rpx;
		// padding: 10rpx;
		// width: 100%;
		// margin: 0;
		// background-color: #FFFFFF;
		// font-size: 32rpx;
		// font-weight: 500;
	}

	.menu-item:last-child {
		border-bottom-left-radius: 16rpx;
		border-bottom-right-radius: 16rpx;
	}

	.menu-item:not(:last-child) {
		border-bottom: 1px solid #eee;
	}

	.cancel {
		color: #333;
		border-top: 16rpx solid #eee;
	}

	.title-clamp {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
		-webkit-line-clamp: 1;
		/* 标题最多 2 行 */
		line-height: 32rpx;
		/* 设置行高 */
		/* margin-bottom: 5px; */
		color: #000;
	}

	.title-clamp2 {
		display: -webkit-box;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
		-webkit-line-clamp: 2;
		/* 标题最多 2 行 */
		line-height: 32rpx;
		/* 设置行高 */
		/* margin-bottom: 5px; */
		color: #000;
	}
</style>