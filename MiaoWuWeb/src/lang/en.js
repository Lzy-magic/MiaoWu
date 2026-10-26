// English messages (default and fallback locale). Every key added here must also be added to the other locale files.
export default {
  common: {
    appTitle: 'MiaoWu (Stray Animal Rescue Platform)',
    serverError: 'Server error, meow~',
    error: 'Something went wrong~',
    back: 'Back',
    submit: 'Submit',
    oops: 'Oops, never mind',
    search: 'Search',
    pleaseSelect: 'Please select',
    loading: 'Loading...',
    loadingWait: 'Give me a moment!',
    dropFile: 'Drop a file here, or ',
    clickUpload: 'click to upload',
    dragOr: 'Drag, or ',
    uploadImage: 'Upload image',
    onlyOneImage: 'Only one image is allowed',
    required: '{field} is required',
    success: 'Success',
    saveSuccess: 'Saved successfully',
    operationSuccess: 'Operation succeeded',
    exportFailed: 'Export failed'
  },
  route: {
    home: 'Home',
    profile: 'Profile',
    articleCenter: 'Articles',
    articleList: 'Article List',
    articleEditor: 'Edit Article',
    articleDetail: 'Read Article',
    animalCenter: 'Animals',
    animalEditor: 'Report an Animal',
    animalRescue: 'Submit a Rescue'
  },
  navbar: {
    profile: 'Profile',
    logOut: 'Log out',
    search: 'Search',
    size: 'Global Size'
  },
  sizeSelect: {
    default: 'Default',
    medium: 'Medium',
    small: 'Small',
    mini: 'Mini',
    switchSuccess: 'Switched size successfully'
  },
  langSelect: {
    name: 'English',
    switchSuccess: 'Language switched'
  },
  tagsView: {
    refresh: 'Refresh',
    close: 'Close',
    closeOthers: 'Close Others',
    closeAll: 'Close All'
  },
  time: {
    justNow: 'just now',
    minutesAgo: '{n} minute ago | {n} minutes ago',
    hoursAgo: '{n} hour ago | {n} hours ago',
    oneDayAgo: '1 day ago',
    monthDayTime: '{month}/{day} {hour}:{mm}',
    weekday0: 'Sun',
    weekday1: 'Mon',
    weekday2: 'Tue',
    weekday3: 'Wed',
    weekday4: 'Thu',
    weekday5: 'Fri',
    weekday6: 'Sat'
  },
  home: {
    articleList: 'Article List',
    userCenter: 'User Center',
    findAnimal: 'Report an Animal',
    welcome: 'Welcome to MiaoWu'
  },
  article: {
    notLoggedIn: 'You are not logged in yet',
    clipSuccess: 'Article added to favorites, meow~',
    unclipSuccess: 'Article removed from favorites, meow~',
    searchSuccess: 'Search completed~',
    selectAnimal: 'Select the animal that needs rescue',
    titlePlaceholder: 'Enter a title...',
    discardChanges: 'Discard changes',
    publish: 'Publish article',
    saveChanges: 'Save changes',
    noPermission: 'You do not have permission to edit this article',
    publishSuccess: 'Article published, meow',
    updateSuccess: 'Article updated, meow',
    noAnimal: 'You have not selected an animal yet~',
    noTitle: 'You have not written a title yet~',
    noContent: 'You have not written any content yet~',
    noAnimalFound: 'No animals have been reported yet~',
    author: 'Author:',
    comments: 'Comments',
    follow: 'Follow',
    unfollow: 'Unfollow',
    confirmUnfollow: 'Are you sure you want to unfollow?',
    confirmRescue: 'Are you sure you want to submit this rescue?',
    submitRescue: 'Submit rescue',
    addComment: 'Add a comment',
    submitComment: 'Post comment',
    commentSuccess: 'Comment posted, meow~',
    cannotFollowSelf: 'You cannot follow yourself~',
    followSuccess: 'Followed~',
    unfollowSuccess: 'Unfollowed~',
    rescueSuccess: 'Rescue submitted, meow',
    noArticles: 'No articles here yet~',
    edit: 'Edit article',
    delete: 'Delete',
    confirmDelete: 'Are you sure you want to delete this article?',
    likeSuccess: 'Liked, meow~',
    unlikeSuccess: 'Like removed, meow~',
    deleteSuccess: 'Article deleted, meow~'
  },
  animal: {
    title: 'Report an Animal',
    name: 'Name',
    age: 'Age',
    gender: 'Gender',
    selectGender: 'Select the gender',
    male: 'Male',
    female: 'Female',
    birthday: 'Birthday',
    selectBirthday: 'Pick a date',
    publish: 'Publish',
    createSuccess: 'Animal reported, meow',
    selectAnimal: 'Select an animal for the application',
    selectUser: 'Select the user who will adopt it'
  },
  user: {
    register: 'Register',
    phone: 'Phone number',
    password: 'Password',
    confirmPassword: 'Confirm password',
    nickname: 'Nickname',
    idCard: 'ID card number',
    invalidPhone: 'Invalid phone number',
    idCardLength: 'The ID card number must be 15 to 18 characters long',
    passwordRule: 'The password must be 6 to 16 characters and contain both letters and digits',
    nicknameLength: 'The nickname must be 1 to 12 characters long',
    passwordMismatch: 'The two passwords do not match',
    today: 'Today',
    yesterday: 'Yesterday',
    weekAgo: 'A week ago',
    incomplete: 'Please fill in all fields before registering, meow~',
    registerSuccess: 'Registered successfully, meow~',
    phoneTaken: 'This phone number is already registered, meow~',
    invalidInfo: 'The registration information is invalid, meow~'
  },
  login: {
    title: 'Stray Animal Rescue Platform',
    logIn: 'Log in',
    browse: 'Just browse',
    devMode: 'Developer mode',
    success: 'Logged in, meow~',
    wrongCredentials: 'Wrong account or password~',
    serverError: 'Server error~',
    empty: 'Account and password cannot be empty~'
  },
  profile: {
    aboutMe: 'About me',
    noInfo: 'Nothing here yet~',
    myInfo: 'My info',
    clipArticles: 'Saved articles',
    myArticles: 'My articles',
    followArticles: 'Following feed',
    follows: 'Following',
    fans: 'Followers',
    submitChanges: 'Save changes',
    updateSuccess: 'Profile updated, meow',
    paramError: 'Invalid parameters'
  },
  errorPage: {
    gifSource: 'GIF from',
    page: 'page',
    noPermission: 'You do not have permission to access this page',
    contactLeader: 'If you think this is a mistake, please contact your administrator',
    orYouCan: 'Or you can go to:',
    home: 'Back to home',
    browse: 'Look around',
    showImage: 'Click to see the picture',
    dialogTitle: 'Have a look'
  },
  example: {
    invalidSourceUrl: 'The external URL is invalid',
    publishSuccess: 'Article published',
    titleContentRequired: 'Please fill in the required title and content'
  },
  demo: {
    selectPlaceholder: 'Select'
  },
  documentation: {
    articleSeries: 'Article series'
  },
  imageCropper: {
    hint: 'Click or drag an image here',
    loading: 'Uploading...',
    noSupported: 'Your browser is not supported, please use IE10+ or another modern browser!',
    success: 'Upload succeeded',
    fail: 'Image upload failed',
    preview: 'Preview',
    btn: {
      off: 'Cancel',
      close: 'Close',
      back: 'Back',
      save: 'Save'
    },
    error: {
      onlyImg: 'Images only',
      outOfSize: 'Image exceeds the size limit: ',
      lowestPx: 'Image is too small. Minimum size (width*height): '
    }
  }
}
