<template>
  <div class="app-container">
    <div v-if="user">
      <el-row :gutter="20">

        <el-col :span="6" :xs="24">
          <user-card :user="user" />
        </el-col>

        <el-col :span="18" :xs="24">
          <el-card>
            <el-tabs v-model="activeTab">
              <el-tab-pane :label="$t('profile.myInfo')" name="userInfo">
                <userInfo :user-info="this.$store.state.userInfo" />
              </el-tab-pane>
              <el-tab-pane :label="$t('profile.clipArticles')" name="clipArticles">
                <ArticleList :articles="clipArticles" />
              </el-tab-pane>
              <el-tab-pane :label="$t('profile.myArticles')" name="myArticles">
                <ArticleList :articles="myArticles" />
              </el-tab-pane>
              <el-tab-pane :label="$t('profile.followArticles')" name="followArticles">
                <ArticleList :articles="followArticles" />
              </el-tab-pane>
              <el-tab-pane :label="$t('profile.follows')" name="follows">
                <UserList :follows="follows" />
              </el-tab-pane>
              <el-tab-pane :label="$t('profile.fans')" name="fans">
                <UserList :fans="fans" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import UserCard from './components/UserCard'
import UserInfo from './components/UserInfo'
import ArticleList from './components/ArticleList'
import request from '@/utils/request'
import settings from '../../settings'
import { ResultCode } from '@/utils/ResultCode'
import UserList from './components/UserList'

export default {
  name: 'Profile',
  components: { UserCard, UserInfo, ArticleList, UserList },
  data() {
    return {
      user: {},
      activeTab: 'userInfo',
      myArticles: [],
      clipArticles: [],
      followArticles: [],
      follows: [],
      fans: []
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles'
    ])
  },
  mounted() {
    this.getUser()
    this.getClipArticle()
    this.getMyArticles()
    this.getFollowArticles()
    this.getFollows()
    this.getFans()
    // this.getUserInfo();
  },
  methods: {
    getUser() {
      this.user = {
        // name: this.$store.state.userInfo.name,
        name: this.name,
        role: this.roles.join(' | '),
        email: 'admin@test.com',
        avatar: this.avatar
      }
    },
    getClipArticle() {
      if (settings.isDebug) {
        this.$store.state.clipArticles = {
          0: {
            title: 'British Shorthair',
            content: 'Looking for a home'
          }
        }
        return
      }
      const self = this
      const url = settings.apiUrl + 'clips/' + self.$store.getters.id
      request.request({
        url,
        method: 'get',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        }
      }).then((result, error) => {
        if (error) {
          this.loading = false
          return
        }
        if (result.code === ResultCode.SuccessCode) {
          self.clipArticles = result.data
        } else if (result.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
          this.loading = false
        }
      })
    },
    getMyArticles() {
      const self = this
      const url = settings.apiUrl + 'articles/authorId=' + self.$store.getters.id
      request.request({
        url,
        method: 'get',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        }
      }).then((result, error) => {
        if (error) {
          this.loading = false
          return
        }
        if (result.code === ResultCode.SuccessCode) {
          self.myArticles = result.data
        } else if (result.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
          this.loading = false
        }
      })
    },
    getFollowArticles() {
      const self = this
      const url = settings.apiUrl + 'articles/fansId=' + self.$store.getters.id
      request.request({
        url,
        method: 'get',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        }
      }).then((result, error) => {
        if (error) {
          this.loading = false
          return
        }
        if (result.code === ResultCode.SuccessCode) {
          self.followArticles = result.data
        } else if (result.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
          this.loading = false
        }
      })
    },
    getFollows() {
      request.get(settings.apiUrl + 'follows/' + this.$store.state.user.id).then(result => {
        this.follows = result.data
      })
    },
    getFans() {
      request.get(settings.apiUrl + 'follows/fans/' + this.$store.state.user.id).then(result => {
        this.fans = result.data
      })
    }

  }
}
</script>
