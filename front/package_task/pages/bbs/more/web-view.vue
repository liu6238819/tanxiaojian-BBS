<template>
	<view>
	    <web-view :src="url"></web-view>
	  </view>
</template>

<script>
	export default {
	  data() {
	    return {
	      url: ''  // 设置要显示的网页地址
	    }
	  },
	  onLoad(options) {
	  	console.log(options)
		this.url=options.url
	  }
	}

</script>

<style>
</style>