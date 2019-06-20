const path = require('path');
const merge = require('webpack-merge');
const webpackCommonConfig = require('./webpack.common.js');

// the display name of the war
const app = 'platform-branding';

// add the server path to your server location path
const exoServerPath = "/home/exo/dev/TC/5.3/2/plfentrial-5.3.x-company-branding-20190425.143351-8/platform-5.3.x-company-branding-SNAPSHOT-trial";

let config = merge(webpackCommonConfig, {
  output: {
    path: path.resolve(`${exoServerPath}/webapps/${app}/`),
    filename: 'js/[name].bundle.js'
  },
  devtool: 'cheap-module-eval-source-map'
});

module.exports = config;
