module.exports = {
  // Set all other paths relative this this one. Important when cosmos.config
  // isn't placed in the project root
  rootPath: './',

  // hot module replacement
  hot: true,

  // Reuse existing webpack config
  webpackConfigPath: './webpack.config.js',

  // Specify where should webpack watch for fixture files (defaults to rootPath)
  watchDirs: ['src'],

  //fileMatch:[
  // mmm, with typescript, fixture name resolution is wired...
  // so, export an array of fixtures from a single file.
  // __fixture__ is just directory not for matching

  //'**/__fixture?(s)__/**/*.{js,jsx,ts,tsx}',
  // '**/?(*.)fixture?(s).{js,jsx,ts,tsx}'
  //],

  // Customize dev server
  hostname: 'localhost',
  port: 8989
};
