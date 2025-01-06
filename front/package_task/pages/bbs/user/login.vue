<template>
	<view class="container">
		<view class="head">登录后更精彩</view>
		<u-form :model="form" ref="uForm">
			<u-form-item>
				<u-input v-model="form.phone" placeholder="请输入手机号" />
			</u-form-item>
			<u-form-item>
				<u-input v-model="form.password" placeholder="请输入密码" />
			</u-form-item>
		</u-form>
		<view class="remember-psw">
			<checkbox-group @change="remberPsw">
				<label class="label">
					<checkbox :checked="isRemmberPsw" style="transform:scale(0.6);" />
					<text>记住账号密码</text>
				</label>
			</checkbox-group>
			<navigator url="./register" open-type="navigate">注册账户</navigator>
		</view>
		<view>
			<u-button type="success" shape="circle" @click="toLogin">登录</u-button>
		</view>
	</view>
</template>

<script>
	import {
		mapMutations
	} from 'vuex'
	import {
		userLogin
	} from '@/api/index.js'

	export default {
		data() {
			return {
				isRemmberPsw: false,
				form: {
					phone: "",
					password: "",
				}
			};
		},
		methods: {
			...mapMutations('user', {
				getUserInfos: 'getUserInfos'
			}),
			remberPsw() {
				this.isRemmberPsw = !this.isRemmberPsw
			},
			async toLogin() {
				// console.log(this.isRemmberPsw);
				const {
					phone,
					password
				} = this.form
				if (phone === "") {
					uni.showToast({
						title: '请输入手机号',
						icon: "none"
					});
					return
				} else if (password === "") {
					uni.showToast({
						title: '请输入密码',
						icon: "none"
					});
					return
				} else {
					// 传递数据给actions，让actions去发送请求
					// this.getUserInfos(this.form)
					// console.log('userInfos',this.userInfos);		
					const data = await userLogin(this.form)
					if (data.code === 200) {
						uni.showToast({
							title: '登录成功',
							icon: "none"
						});
						// 将用户信息存储到全局中(vuex中)
						this.getUserInfos(data.data)
						// console.log('getUserInfos',this.getUserInfos)
						// this.getUserInfos(this.form)
						if (this.isRemmberPsw) {
							// 记住密码，将用户信息缓存到本地
							console.log('data', data);
							uni.setStorageSync("userInfos", data.data)
						}else{
							// 将数据全局存储到vuex中
							this.getUserInfos(data.data)
						}
						setTimeout(function() {
							uni.reLaunch({
								url: '../mine/mine',
							})
						}, 1000)
					} else {
						uni.showToast({
							title: '账号或者密码错误，请重新登录',
							icon: "none"
						});
						this.form.phone = ""
						this.form.password = ""
					}
				}

			},
			async login() {

				uni.showLoading({
					mask: true,
					title: '登录中'
				});

				let that = this;

				let userInfo = await this.getUserProfile();
				let loginCode = await this.getLoginCode();
				console.log('loginCode', loginCode);
				that.$H.post('user/miniWxLogin', {
					code: loginCode,
					username: userInfo.nickName,
					avatar: userInfo.avatarUrl,
					province: userInfo.province,
					city: userInfo.city,
					gender: userInfo.gender
				}).then(res2 => {
					if (res2.code === 200) {
						uni.setStorageSync("hasLogin", true);
						uni.setStorageSync("token", res2.result.token);
						uni.navigateBack();
						that.getUserInfo();

						uni.hideLoading();
					}
				})
			},
			getUserInfo() {
				this.$H.get("user/userInfo").then(res => {
					uni.setStorageSync("userInfo", res.result)
				})
			},
			getLoginCode() {
				uni.login({
					provider: 'weixin',
					success: function(loginRes) {
						console.log('weixing code', loginRes.code)
						resolve(loginRes.code);
					}
				});

				return new Promise((resolve, reject) => {
					uni.login({
						provider: 'weixin',
						success: function(loginRes) {
							console.log('weixing code', loginRes.code)
							resolve(loginRes.code);
						}
					});
				});
			},
			getUserProfile() {
				return new Promise((resolve, reject) => {
					wx.getUserProfile({
						lang: 'zh_CN',
						desc: '用于完善会员资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
						success: res => {
							console.log('res.userInfo', res.userInfo);
							resolve(res.userInfo);
						}
					});
				});
			}

		}
	}
</script>

<style lang="scss">
	.container {
		padding: 20rpx 50rpx;
	}

	.head {
		font-size: 40rpx;
		font-weight: 600;
		margin-bottom: 50rpx;
	}

	.remember-psw {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 25rpx;
		margin-top: 25rpx;

	}

	.remember-psw label {
		display: flex;
		align-items: center;
	}

	.remember-psw>text {
		padding: 0 8rpx;
	}
</style>
