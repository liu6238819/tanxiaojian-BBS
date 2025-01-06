export default {
	toast(title,icon = 'none') {
		uni.showToast({
			title: title,
			icon:icon,
			duration: 1500
		});
	}
}
