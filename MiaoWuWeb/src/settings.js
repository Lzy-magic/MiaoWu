module.exports = {
  title: 'MiaoWu (Stray Animal Rescue Platform)', // the translated title lives in src/lang (common.appTitle)

  /**
   * @type {boolean} true | false
   * @description Whether show the settings right-panel
   */
  showSettings: true,

  /**
   * @type {boolean} true | false
   * @description Whether need tagsView
   */
  tagsView: true,

  /**
   * @type {boolean} true | false
   * @description Whether fix the header
   */
  fixedHeader: false,

  /**
   * @type {boolean} true | false
   * @description Whether show the logo in sidebar
   */
  sidebarLogo: false,

  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: 'production',

  /**
   *  debug mode, develop without request server
   */
  isDebug: false,

  /**
   *
   */
  // Prefix for MiaoWu backend calls. Empty by default so requests go through the
  // axios baseURL (VUE_APP_BASE_API) and, in development, the vue.config.js proxy.
  // Set VUE_APP_API_URL (e.g. http://localhost:8088/) to call the backend directly.
  apiUrl: process.env.VUE_APP_API_URL || ''

  // apiUrl: ''
}
