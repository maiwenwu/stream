module.exports = {
	// All options for webpack-dev-server are supported
	// https://webpack.js.org/configuration/dev-server/
	devServer: {
	  open: true,
      
	  host: '127.0.0.1',
      
	  port: 3000,
      
	  https: false,
      
	  hotOnly: false,
      
	  proxy: null,
      
	  before: app => {
	  }
	},
	
      };
      