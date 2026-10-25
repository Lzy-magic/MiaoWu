<template>
  <el-row>
    <el-col>
      <el-input v-model="input" :placeholder="$t('common.search')" style="width:300px" />
      <el-button type="primary" icon="el-icon-search" @click="search(input)">{{ $t('common.search') }}</el-button>
    </el-col>
    <el-col v-for="(article, index) in articles" :key="index" :span="8" :offset="2">
      <el-card :body-style="{ padding: '0px' }" style="">
        <img
          src="@/static/defaultCat.jpg"
          class="image"
          @click.native="toArticleDetail(article.id)"
        >
        <div style="padding: 14px;">
          <el-link type="primary" @click="toArticleDetail(article.id)">{{ article.title }}</el-link>
          <a @click="clipArticle(article)">
            <i :class="article.clip !== null ?'el-icon-star-on':'el-icon-star-off'" />
          </a>
          <div class="bottom clearfix" style="font-size: 11px">
            <time class="time">{{ new Date(article.lastUpdate).getTime() | parseTime('{y}-{m}-{d} {h}:{i}') }}</time>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'

export default {
  name: 'List',
  data() {
    return {
      articles: [],
      restaurants: [],
      state: '',
      timeout: null
    }
  },
  created() {
    if (this.$store.getters.id) {
      request.get(settings.apiUrl + 'articles/with=clips&userId=' + this.$store.getters.id).then((result) => {
        this.articles = result.data
      })
    } else { // guest
      request.get(settings.apiUrl + 'articles/findAll').then(result => {
        this.articles = result.data
      })
    }
  },
  methods: {
    toPublishArticle() {
      // Go to the editor page
      this.$router.push({ path: '/article/editor' })
    },
    loadAll() {

    },
    querySearchAsync(queryString, cb) {
      var restaurants = this.restaurants
      var results = queryString ? restaurants.filter(this.createStateFilter(queryString)) : restaurants

      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        cb(results)
      }, 3000 * Math.random())
    },
    createStateFilter(queryString) {
      return (state) => {
        return (state.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },
    handleSelect(item) {
      console.log(item)
    },
    handleLike() {

    },
    toArticleDetail(articleId) {
      this.$router.push({ path: 'articleDetail', query: { articleId: articleId }})
    },
    clipArticle(article) {
      if (article.clip === null) {
        const self = this
        const url = settings.apiUrl + 'clips'
        if (!self.$store.state.user) {
          self.$message.error(self.$t('article.notLoggedIn'))
        }
        const form = {
          'userId': self.$store.getters.id,
          'articleId': article.id
        }
        request.request({
          url,
          method: 'post',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'

          },
          data: qs.stringify(form)
        }).then((result, error) => {
          if (error) {
            this.$message.error(this.$t('common.serverError'))
          } else if (result.code === ResultCode.SuccessCode) {
            article.clip = {}
            this.$message.success(this.$t('article.clipSuccess'))
          } else if (result.code === ResultCode.ServerInnerError) {
            this.$message.error(this.$t('common.serverError'))
          }
        })
      } else {
        article.clip = false
        const self = this
        const url = settings.apiUrl + 'clips'
        const form = {
          'userId': self.$store.getters.id,
          'articleId': article.id
        }
        request.request({
          url,
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'

          },
          data: qs.stringify(form)
        }).then((result, error) => {
          if (error) {
            this.$message.error(this.$t('common.serverError'))
          } else if (result.code === ResultCode.SuccessCode) {
            this.$set(article, 'clip', null)
            this.$message.success(this.$t('article.unclipSuccess'))
          } else if (result.code === ResultCode.ServerInnerError) {
            this.$message.error(this.$t('common.serverError'))
          }
        })
      }
    },
    search(value) {
      const url = settings.apiUrl + 'articles' + 'key=title' + 'value=' + value
      request.request({
        url,
        method: 'get',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        }
      }).then((result, error) => {
        if (error) {
          this.$message.error(this.$t('common.serverError'))
        } else if (result.code === ResultCode.SuccessCode) {
          this.$message.success(this.$t('article.searchSuccess'))
        } else if (result.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
        }
      })
    },
    mounted() {
    }
  }
}
</script>

<style scoped>
.image {
  height: 200px;
}
</style>
