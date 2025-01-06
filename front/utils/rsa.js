import WxmpRsa from 'wxmp-rsa'

// 实例化rsa
const rsa = new WxmpRsa()


// 定义公钥
const publicKey = `
  -----BEGIN PUBLIC KEY-----
  -----END PUBLIC KEY-----
`
const userInfoPublicKey = `
-----BEGIN PUBLIC KEY-----
-----END PUBLIC KEY-----
`


//加密
function getRsaCode(str) {
	const rsa = new WxmpRsa()
	rsa.setPublicKey(publicKey);
	const data = rsa.encryptLong(str)
	return data
}
//用户隐私信息加密
function encryptUserInfo(str) {
	const rsa = new WxmpRsa()
	rsa.setPublicKey(userInfoPublicKey);
	const data = rsa.encryptLong(str)
	return data
}




export default {
	getRsaCode,
	encryptUserInfo
};
