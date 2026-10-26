// Simplified Chinese messages. Keep the same keys as ./en.js.
export default {
  common: {
    appTitle: '喵呜（流浪动物救助平台）',
    serverError: '服务器出错喵~',
    error: '出错啦~',
    back: '返回',
    submit: '提交',
    oops: '手滑了',
    search: '搜索',
    pleaseSelect: '请选择',
    loading: '拼命加载中',
    loadingWait: '请给我点时间！',
    dropFile: '将文件拖到此处，或',
    clickUpload: '点击上传',
    dragOr: '拖拽或',
    uploadImage: '上传图片',
    onlyOneImage: '只能一个图',
    required: '{field}为必传项',
    success: '成功',
    saveSuccess: '保存成功',
    operationSuccess: '操作成功',
    exportFailed: '导出失败'
  },
  route: {
    home: '主界面',
    profile: '个人中心',
    articleCenter: '文章中心',
    articleList: '文章列表',
    articleEditor: '编辑文章',
    articleDetail: '浏览文章',
    animalCenter: '动物中心',
    animalEditor: '发现动物',
    animalRescue: '提交救助动物'
  },
  navbar: {
    profile: '个人中心',
    logOut: '退出登录',
    search: '搜索',
    size: '全局尺寸'
  },
  sizeSelect: {
    default: '默认',
    medium: '中',
    small: '小',
    mini: '迷你',
    switchSuccess: '切换尺寸成功'
  },
  langSelect: {
    name: '简体中文',
    switchSuccess: '切换语言成功'
  },
  tagsView: {
    refresh: '刷新',
    close: '关闭',
    closeOthers: '关闭其它',
    closeAll: '关闭所有'
  },
  time: {
    justNow: '刚刚',
    minutesAgo: '{n}分钟前 | {n}分钟前',
    hoursAgo: '{n}小时前 | {n}小时前',
    oneDayAgo: '1天前',
    monthDayTime: '{month}月{day}日{hour}时{minute}分',
    weekday0: '日',
    weekday1: '一',
    weekday2: '二',
    weekday3: '三',
    weekday4: '四',
    weekday5: '五',
    weekday6: '六'
  },
  home: {
    articleList: '文章列表',
    userCenter: '用户中心',
    findAnimal: '发现动物',
    welcome: '欢迎来到喵呜'
  },
  article: {
    notLoggedIn: '尚未登录',
    clipSuccess: '收藏文章成功喵~',
    unclipSuccess: '删除收藏成功喵~',
    searchSuccess: '搜索成功~',
    selectAnimal: '请选择要申请救助的动物',
    titlePlaceholder: '请输入标题...',
    discardChanges: '放弃修改',
    publish: '发表文章',
    saveChanges: '保存修改',
    noPermission: '没有权限修改哦',
    publishSuccess: '发布文章成功啦喵',
    updateSuccess: '修改文章成功啦喵',
    noAnimal: '还没有选择动物哦~',
    noTitle: '还没有写标题哦~',
    noContent: '还没有写内容哦~',
    noAnimalFound: '还没有发现动物哦~',
    author: '作者:',
    comments: '评论区',
    follow: '关注',
    unfollow: '取消关注',
    confirmUnfollow: '确定取消关注吗？',
    confirmRescue: '确定提交救助动物吗？',
    submitRescue: '提交救助动物',
    addComment: '添加评论',
    submitComment: '提交评论',
    commentSuccess: '发表评论成功喵~',
    cannotFollowSelf: '不能关注自己~',
    followSuccess: '关注成功~',
    unfollowSuccess: '取消关注成功~',
    rescueSuccess: '提交救助成功啦喵',
    noArticles: '没有相关文章信息哦~',
    edit: '修改文章',
    delete: '删除',
    confirmDelete: '确定删除文章吗？',
    likeSuccess: '点赞成功喵~',
    unlikeSuccess: '取消点赞成功喵~',
    deleteSuccess: '删除文章成功喵~'
  },
  animal: {
    title: '发现动物',
    name: '动物昵称',
    age: '年龄',
    gender: '性别',
    selectGender: '请选择动物性别',
    male: '雄性',
    female: '雌性',
    birthday: '生日',
    selectBirthday: '选择生日',
    publish: '发布',
    createSuccess: '发现动物成功啦喵',
    selectAnimal: '请选择要提交申请的动物',
    selectUser: '请选择要救助的用户'
  },
  user: {
    register: '注册',
    phone: '电话号码',
    password: '密码',
    confirmPassword: '再次输入密码',
    nickname: '昵称',
    idCard: '身份证',
    invalidPhone: '电话号码不合法',
    idCardLength: '身份证长度在 15 到 18 个字符',
    passwordRule: '密码为至少6位，至多16位的包含数字和字母的组合',
    nicknameLength: '用户名长度在 1 到 12 个字符',
    passwordMismatch: '两次密码不一致',
    today: '今天',
    yesterday: '昨天',
    weekAgo: '一周前',
    incomplete: '请完善信息后再注册喵~',
    registerSuccess: '注册成功喵~',
    phoneTaken: '该电话号码已经被注册喵~',
    invalidInfo: '注册信息不合法喵~'
  },
  login: {
    title: '流浪动物救助平台',
    logIn: '登录',
    browse: '随便看看',
    devMode: '开发者模式',
    success: '登录成功啦喵~',
    wrongCredentials: '账号或密码错误~',
    serverError: '服务器出错啦~',
    empty: '账号或密码不能为空~'
  },
  profile: {
    aboutMe: '关于我',
    noInfo: '没有相关信息哦~',
    myInfo: '我的信息',
    clipArticles: '收藏文章列表',
    myArticles: '我发布的文章',
    followArticles: '我关注的人的动态',
    follows: '我关注的人',
    fans: '我的粉丝',
    submitChanges: '提交修改',
    updateSuccess: '更新信息成功啦喵',
    paramError: '参数错误'
  },
  errorPage: {
    gifSource: 'gif来源',
    page: '页面',
    noPermission: '你没有权限去该页面',
    contactLeader: '如有不满请联系你领导',
    orYouCan: '或者你可以去:',
    home: '回首页',
    browse: '随便看看',
    showImage: '点我看图',
    dialogTitle: '随便看'
  },
  example: {
    invalidSourceUrl: '外链url填写不正确',
    publishSuccess: '发布文章成功',
    titleContentRequired: '请填写必要的标题和内容'
  },
  demo: {
    selectPlaceholder: '请选择'
  },
  documentation: {
    articleSeries: '系列文章'
  },
  imageCropper: {
    hint: '点击，或拖动图片至此处',
    loading: '正在上传……',
    noSupported: '浏览器不支持该功能，请使用IE10以上或其他现在浏览器！',
    success: '上传成功',
    fail: '图片上传失败',
    preview: '头像预览',
    btn: {
      off: '取消',
      close: '关闭',
      back: '上一步',
      save: '保存'
    },
    error: {
      onlyImg: '仅限图片格式',
      outOfSize: '单文件大小不能超过 ',
      lowestPx: '图片最低像素为（宽*高）：'
    }
  }
}
