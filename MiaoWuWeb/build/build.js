// build.js is the webpack build entry, run through a package.json script: `npm run build` runs `node build/build.js`
'use strict'
// Check node and npm versions using ./check-versions.js
require('./check-versions')()

// process.env holds the environment; NODE_ENV tells development from production
process.env.NODE_ENV = 'production'

// ora provides the loading spinner
const ora = require('ora')
// rimraf wraps `rm -rf` to delete files and folders, empty or not
const rm = require('rimraf')
// node path module
const path = require('path')
// chalk colors console text
const chalk = require('chalk')
// webpack
const webpack = require('webpack')
// config/index.js
const config = require('../config')
// webpack.prod.conf
const webpackConfig = require('./webpack.prod.conf')
// loading spinner
const spinner = ora('building for production...')
// start the spinner
spinner.start()

// remove the static folder
// path.join joins path segments; an invalid segment throws
rm(path.join(config.build.assetsRoot,config.build.assetsSubDirectory),err => {
    if(err) throw err
    // run webpack
    webpack(webpackConfig,(err,stats) => {
        // stop the spinner
        spinner.stop()
        if(err) throw err
        // process.stdout.write writes to stdout, like console.log
        process.stdout.write(stats.toString({
            // colored console output
            colors:true,
            // whether to include built-in module info
            modules:false,
            children:false,
            // allow less output
            chunks:false,
            // do not add built-in module info to chunk info
            chunkModules:false

        }) + '\n\n')

        if(stats.hasErrors()){
            console.log(chalk.red(' Build complete.\n'))
            // build failed
            process.exit(1)
        }
        // exit after compiling
        console.log(chalk.cyan(' Build failed with errors.\n'))
        console.log(chalk.yellow(
            ' Tip:built files are meant to be served over an HTTP server.\n' +
            ' Opening index.html over file:// won\'t work.\n'
        ))
    })
})

