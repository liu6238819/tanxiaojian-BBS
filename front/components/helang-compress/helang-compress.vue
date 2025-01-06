<template>
	<view class="compress">
		<canvas :style="{ width: canvasSize.width,height: canvasSize.height}" canvas-id="myCanvas"></canvas>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				pic:'',
				canvasSize: {
					width: 0,
					height: 0
				}
			}
		},
		methods: {
			// 压缩
			compress(params) {
				return new Promise(async (resolve, reject) => {
					// 等待图片信息
					let info = await this.getImageInfo(params.src).then(info=>info).catch(err=>err);
					
					if(!info){
						reject('获取图片信息异常');
						return;
					}
					
					// 设置最大 & 最小 尺寸
					const maxSize = params.maxSize || 1080;
					const minSize = params.minSize || 640;
					
					// 当前图片尺寸
					let {width,height} = info;
					
					// 非 H5 平台进行最小尺寸校验
					// #ifndef H5
					if(width <= minSize && height <= minSize){
						resolve(params.src);
						return;
					}
					// #endif
					
					// 最大尺寸计算
					if (width > maxSize || height > maxSize) {
						if (width > height) {
							height = Math.floor(height / (width / maxSize));
							width = maxSize;
						} else {
							width = Math.floor(width / (height / maxSize));
							height = maxSize;
						}
					}

					// 设置画布尺寸
					this.$set(this,"canvasSize",{
						width: `${width}rpx`,
						height: `${height}rpx`
					});
					
					// Vue.nextTick 回调在 App 有异常，则使用 setTimeout 等待DOM更新
					setTimeout(() => {
						const ctx = uni.createCanvasContext('myCanvas', this);
						ctx.clearRect(0,0,width, height)
						ctx.drawImage(info.path, 0, 0, uni.upx2px(width), uni.upx2px(height));
						ctx.draw(false, () => {
							uni.canvasToTempFilePath({
								x: 0,
								y: 0,
								width: uni.upx2px(width),
								height: uni.upx2px(height),
								destWidth: width,
								destHeight: height,
								canvasId: 'myCanvas',
								fileType: params.fileType || 'png',
								quality: params.quality || 0.9,
								success: (res) => {
									// 在H5平台下，tempFilePath 为 base64
									resolve(res.tempFilePath);
								},
								fail:(err)=>{
									reject(null);
								}
							},this);
						});
					}, 300);
				});
			},
			// 获取图片信息
			getImageInfo(src){
				return new Promise((resolve, reject)=>{
					uni.getImageInfo({
						src,
						success: (info)=> {
							resolve(info);
						},
						fail: () => {
							reject(null);
						}
					});
				});
			},
			// 批量压缩
			batchCompress(params){
				// index:进度，done:成功，fail:失败
				let [index,done,fail] = [0,0,0];
				// 压缩完成的路径集合
				let paths = [];
				// 批量压缩方法
				let batch = ()=>{
					return new Promise((resolve, reject)=>{
						// 开始
						let start = async ()=>{
							params.progress && params.progress({
								done,
								fail,
								count:params.batchSrc.length
							});
							// 等待图片压缩方法返回
							let path = await next();
							if(path){
								done++;
								paths.push(path);
							}else{
								fail++;
							}
							
							index++;
							// 压缩完成
							if(index >= params.batchSrc.length){
								resolve(true);
							}else{
								start();
							}
						}
						start();
					});
				}
				// 依次调用压缩方法
				let next = ()=>{
					return this.compress({
						src:params.batchSrc[index],
						maxSize:params.maxSize,
						fileType:params.fileType,
						quality:params.quality,
						minSize:params.minSize
					})
				}
				
				// 全部压缩完成后调用
				return new Promise(async (resolve, reject)=>{
					// 批量压缩方法回调
					let res = await batch();
					if(res){
						resolve(paths);
					}else{
						reject(null);
					}
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.compress{
		position: fixed;
		width: 12px;
		height: 12px;
		overflow: hidden;
		top: -99999px;
		left: 0;
	}
</style>
