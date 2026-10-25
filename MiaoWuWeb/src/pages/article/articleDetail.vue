<template>
  <el-container>
    <el-main>
      <el-row v-loading="loading">
        <el-col :span="24">
          <div style="text-align: left;">
            <el-button
              type="text"
              icon="el-icon-back"
              style="padding-bottom: 0px; margin-bottom: 10px"
              @click="goBack"
            >{{ $t('common.back') }}
            </el-button>
          </div>
        </el-col>
        <el-col :span="24">
          <div>
            <div>
              <h3 style="margin-top: 0px;margin-bottom: 0px">{{ article.title }}</h3>
              <span class="description"> {{ $t('article.author') }} {{ author.name }}</span>
            </div>
            <el-button
              v-if="userId !== article.authorId"
              type="primary"
              style="margin-top: 10px"
              @click="handleFollow"
            >{{ $t('article.follow') }}
            </el-button>
            <div
              style="width: 100%;margin-top: 5px;display: flex;justify-content: flex-end;align-items: center"
            >
              <!--          <div style="display: inline; color: #20a0ff;margin-left: 50px;margin-right:20px;font-size: 12px;">-->
              <!--            {{article.nickname}}-->
              <!--          </div>-->
              <!--          TODO: view count-->
              <!--          <span style="color: #20a0ff;margin-right:20px;font-size: 12px;">Views {{article.pageView==null?0:article.pageView}}</span>-->
              <span
                style="color: #20a0ff;margin-right:20px;font-size: 12px;"
              >{{ new Date(article.lastUpdate).getTime() | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
              <el-tag
                v-for="(item,index) in labels"
                :key="index"
                type="success"
                size="small"
                style="margin-left: 8px"
              >{{ item.name }}
              </el-tag>
              <span style="margin:0px 50px 0px 0px" />
            </div>
          </div>
        </el-col>
        <el-col>
          <div id="content" style="text-align: left" v-html="article.source" />
        </el-col>
      </el-row>

      <h3>{{ $t('article.comments') }}</h3>
      <div v-for="(comment,index) in comments" id="comment-container" :key="index">
        <div style="text-align: left; margin-bottom: 5px">
          {{ comment.user.name }}
          <span
            style="color: #20a0ff;margin-right:20px;font-size: 12px;"
          >{{ new Date(comment.date).getTime() | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
          <el-popconfirm
            :confirm-button-text="$t('common.submit')"
            :cancel-button-text="$t('common.oops')"
            icon="el-icon-info"
            icon-color="red"
            :title="$t('article.confirmUnfollow')"
            @onConfirm="postSave(comment.user.id)"
          >
            <el-button
              v-if="userId !== comment.userId"
              type="primary"
              style="margin-top: 10px"
              @click="handleFollow"
            >{{ $t('article.unfollow') }}
            </el-button>
          </el-popconfirm>
          <el-button
            v-if="userId !== comment.user.id"
            type="primary"
            style="margin-top: 10px"
            @click="handleFollow"
          >{{ $t('article.follow') }}
          </el-button>
          <el-popconfirm
            :confirm-button-text="$t('common.submit')"
            :cancel-button-text="$t('common.oops')"
            icon="el-icon-info"
            icon-color="red"
            :title="$t('article.confirmRescue')"
            @onConfirm="postSave(comment.user.id)"
          >
            <el-button
              v-if="article.authorId !== comment.userId && article.authorId === userId"
              slot="reference"
              type="primary"
              style=""
            >{{ $t('article.submitRescue') }}
            </el-button>
          </el-popconfirm>
        </div>

        <div style="text-align: left">{{ comment.commentContent }}</div>
      </div>
      <el-input
        v-model="textarea"
        v-loading="commentLoading"
        type="textarea"
        :rows="2"
        :placeholder="$t('article.addComment')"
        style="margin-top: 10px"
      />

      <el-button type="primary" style="margin-top: 10px" @click="commitComment">{{ $t('article.submitComment') }}</el-button>
    </el-main>
    <el-footer style />
  </el-container>
</template>

<script>
import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'

export default {
  name: 'ArticleDetail',
  data() {
    return {
      articleData: {
        article: {
          title: ''
        },
        comments: {},
        labels: {},
        author: {
          name: ''
        }
      },
      loading: '',
      commentLoading: '',
      textarea: ''
    }
  },
  computed: {
    userId() {
      return this.$store.getters.id
    },
    userInfo() {
      return this.$store.state.user.userInfo
    },
    article() {
      return this.articleData.article
    },
    comments() {
      return this.articleData.comments
    },
    labels() {
      return this.articleData.labels
    },
    author() {
      return this.articleData.author
    }

    // article(){
    //   return this.article;
    // }
  },
  created() {
    this.getArticle()
  },
  mounted() {
    // objectsContains(userInfo.follows);
  },
  methods: {
    getArticle() {
      const self = this
      const url = `${settings.apiUrl}articles/${self.$route.query.articleId}`
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
            this.$message.error(this.$t('common.serverError'))
            return
          }
          if (result.code === ResultCode.SuccessCode) {
            this.articleData = result.data
          } else if (result.code === ResultCode.ServerInnerError) {
            this.$message.error(this.$t('common.serverError'))
          }
        })
    },
    goBack() {
      this.$router.go(-1)
    },
    commitComment() {
      const self = this
      self.commentLoading = true
      const url = settings.apiUrl + 'comments'
      const form = {
        userId: self.$store.getters.id,
        articleId: self.article.id,
        comment: self.textarea
      }
      request
        .request({
          url,
          method: 'post',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          data: qs.stringify(form)
        })
        .then((res, error) => {
          if (error) {
            this.commentLoading = false
            return
          }
          if (res.code === ResultCode.SuccessCode) {
            self.commentLoading = false
            self.$message.success(self.$t('article.commentSuccess'))
            self.$router.go(0)
          } else if (res.code === ResultCode.BadRequest) {
            self.commentLoading = false
            self.$message.error(res.message)
          } else if (res.code === ResultCode.ServerInnerError) {
            self.commentLoading = false
            this.$message.error(this.$t('common.serverError'))
            return
          }
        })
    },
    handleFollow() {
      const self = this
      const url = settings.apiUrl + 'follows'
      // Double check (the button should already be hidden)
      if (this.article.authorId === self.$store.getters.id) {
        this.$message.error(this.$t('article.cannotFollowSelf'))
      }
      const form = {
        userId: this.article.authorId,
        fansId: self.$store.getters.id
      }
      request
        .request({
          url,
          method: 'post',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          data: qs.stringify(form)
        })
        .then((res, error) => {
          if (error) {
            return
          }
          if (res.code === ResultCode.SuccessCode) {
            self.$message.success(self.$t('article.followSuccess'))
          } else if (res.code === ResultCode.CancelSuccessCode) {
            self.$message.success(self.$t('article.unfollowSuccess'))
          } else if (res.code === ResultCode.BadRequest) {
            self.$message.error(res.message)
          } else if (res.code === ResultCode.ServerInnerError) {
            this.$message.error(this.$t('common.serverError'))
            return
          }
        })
    },
    postSave(userId) {
      // TODO: untested
      const url = settings.apiUrl + 'saves'
      const self = this
      const form = {
        'userId': userId,
        'animalId': this.article.animalId
      }
      request.request({
        url,
        method: 'post',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        },
        data: qs.stringify(form)
      }).then((res, error) => {
        if (error) {
          this.loading = false
          return
        }
        if (res.code === ResultCode.SuccessCode) {
          this.$message.success(this.$t('article.rescueSuccess'))
          this.loading = false
        } else if (res.code === ResultCode.BadRequest) {
          self.$message.error(res.message)
          this.loading = false
          return
        } else if (res.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
    #comment-container {
        padding: 10px;
        margin-bottom: 10px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    #content {
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
    }
</style>
