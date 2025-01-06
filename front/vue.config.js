const path = require('path')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const Webpack = require('webpack')
//const CompressionWebpackPlugin = require('compression-webpack-plugin')


module.exports = {
	configureWebpack: {
		plugins: [
			new CopyWebpackPlugin([{
				from: path.join(__dirname, 'functions_wx'),
				to: path.join(__dirname, 'unpackage/dist', process.env.NODE_ENV === 'production' ?
					'build' : 'dev', process.env.UNI_PLATFORM, 'functions_wx')
			}]),
			new Webpack.ContextReplacementPlugin(
				/moment[\\\/]locale$/,
				/^\.\/(zh-cn)$/
			)
			// new CompressionWebpackPlugin({
			// 	filename: '[path][base].gz',
			// 	algorithm: 'gzip',
			// 	test: /\.js$/,
			// 	threshold: 10240,
			// 	minRatio: 0.8,
			// 	exclude: /node_modules/,
			// })
		]
	}
}
