// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
	env: cloud.DYNAMIC_CURRENT_ENV,
	// traceUser: true
})

exports.main = async (event, context) => {
  try {
    const result = await cloud.openapi.wxacode.getUnlimited({
        "page": event.page,
        "scene":  event.scene,
        "checkPath": true,
        "envVersion": 'release'
      })
    return result
  } catch (err) {
    return err
  }
}