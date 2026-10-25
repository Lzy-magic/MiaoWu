<template>
  <div>
    <el-card :body-style="{ padding: '0px' }" style>
      <div style="padding: 14px;">
        <el-link type="primary" @click="toArticleListPage">{{ $t('home.articleList') }}</el-link>
      </div>
    </el-card>
    <el-card :body-style="{ padding: '0px' }" style>
      <div style="padding: 14px;">
        <el-link type="primary" @click="toUserCenterPage">{{ $t('home.userCenter') }}</el-link>
      </div>
    </el-card>
    <el-card :body-style="{ padding: '0px' }" style>
      <div style="padding: 14px;">
        <el-link type="primary" @click="toFindAnimalPage">{{ $t('home.findAnimal') }}</el-link>
      </div>
    </el-card>
    <h5>{{ $t('home.welcome') }}</h5>
  </div>
</template>

<script>
import request from '@/utils/request'
import settings from '../../settings'
import { ResultCode } from '@/utils/ResultCode'

export default {
  name: 'Index',
  data() {
    return {
      articles: [],
      restaurants: [],
      state: '',
      timeout: null
    }
  },
  created() {
    // this.login();
  },
  mounted() {
  },
  methods: {
    login() {
      const self = this
      const url =
                    settings.apiUrl +
                    'users/info' +
                    '?token=' +
                    window.localStorage.getItem('token')
      request
        .request({
          url,
          method: 'get',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        })
        .then((result, error) => {
          if (error) {
            return
          }
          if (result.code === ResultCode.SuccessCode) {
            self.$store.state.userInfo = result.data
          } else if (result.code === ResultCode.ServerInnerError) {
            this.$message.error(this.$t('common.serverError'))
          }
        })
    },
    toArticleListPage() {
      this.$router.push({ path: '/article/list' })
    },
    toUserCenterPage() {
      this.$router.push({ path: '/profile/index' })
    },
    toFindAnimalPage() {
      this.$router.push({ path: '/animal/editor' })
    }
  }
}
</script>

<style scoped>
    .image {
        height: 200px;
    }
</style>
