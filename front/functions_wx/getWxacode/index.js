// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({
	env: cloud.DYNAMIC_CURRENT_ENV,
	// traceUser: true
})

exports.main = async (event, context) => {
  try {
    const result = 0;
    return result
  } catch (err) {
    return err
  }
}