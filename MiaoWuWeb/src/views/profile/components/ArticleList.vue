<template>
  <div class="article-contains">
    <div v-if="JSON.stringify(articles) === '{}'">
      <span>{{ $t('article.noArticles') }}</span>
    </div>
    <div v-for="(article,index) in articles" :key="index" class="articles">
      <div class="post">
        <div class="user-block">
          <img
            class="img-circle"
            :src="'https://wpimg.wallstcn.com/57ed425a-c71e-4201-9428-68760c0537c4.jpg'+avatarPrefix"
          >
          <!--          <span class="username text-muted">{{article.title}}</span>-->
          <el-link type="primary" style="left:10px; position:relative" @click="toArticleDetail(article.id)">{{ article.title }}</el-link>
          <span class="description">{{ article.authorName }}     {{ new Date(article.lastUpdate).getTime() | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
          <el-button
            v-if="article.authorId === userId"
            slot="reference"
            type="primary"
            style="right: 50px; position: absolute"
            @click="updateArticle(article.id)"
          >{{ $t('article.edit') }}</el-button>
          <el-popconfirm
            :confirm-button-text="$t('article.delete')"
            :cancel-button-text="$t('common.oops')"
            icon="el-icon-info"
            icon-color="red"
            :title="$t('article.confirmDelete')"
            @onConfirm="delArticle(article.id)"
          >
            <el-button
              v-if="article.authorId === userId"
              slot="reference"
              type="danger"
              icon="el-icon-delete"
              circle
              style="right: 0px; position: absolute"
            />
          </el-popconfirm>
        </div>
        <p>
          {{ article.content }}
        </p>
        <ul class="list-inline">
          <li>
            <span class="link-black text-sm">
              {{ article.thumbUp }}
              <img src="@/static/like.jpg" alt="" style="height:10px" @click="handleLike(article)">
            </span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>

import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'

export default {
  name: 'ArticleList',
  props: {
    articles: {
      type: Array,
      default: () => {
        return {
          thumbUp: 0
        }
      }
    }
  },
  data() {
    return {
      avatarPrefix: '',
      carouselPrefix: ''
    }
  },
  computed: {
    userId() {
      return this.$store.getters.id
    }
  },
  mounted() {
  },
  methods: {
    handleLike(article) {
      const self = this
      const url = settings.apiUrl + 'thumbs'
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
          return
        }
        if (result.code === ResultCode.SuccessCode) {
          self.$set(article, 'thumbUp', article.thumbUp + 1)
          // article.thumbUp++;
          this.$message.success(this.$t('article.likeSuccess'))
        } else if (result.code === ResultCode.CancelSuccessCode) {
          self.$set(article, 'thumbUp', article.thumbUp - 1)
          this.$message.success(this.$t('article.unlikeSuccess'))
          // article.thumbUp--;
        } else if (result.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
          this.loading = false
        }
      })
    },
    delArticle(articleId) {
      const url = settings.apiUrl + 'articles'
      const form = {
        'id': articleId
      }
      request.request({
        url,
        // TODO: the page does not respond
        method: 'delete',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        },
        data: qs.stringify(form)
      }).then((result, error) => {
        if (error) {
          return
        }
        if (result.code === ResultCode.SuccessCode) {
          this.$message.success(this.$t('article.deleteSuccess'))
        } else if (result.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
        }
      })
    },
    toArticleDetail(articleId) {
      this.$router.push({ path: '/article/articleDetail', query: { articleId: articleId }})
    },
    updateArticle(id) {
      // Load the article info in the editor
      this.$router.push({ path: '/article/editor', query: { articleId: id }})
    }
  }
}
</script>

<style lang="scss" scoped>
  .user-block {
    .username,
    .description {
      display: block;
      margin-left: 50px;
      padding: 2px 0;
    }

    .username {
      font-size: 16px;
      color: #000;
    }

    :after {
      clear: both;
    }

    .img-circle {
      border-radius: 50%;
      width: 40px;
      height: 40px;
      float: left;
    }

    span {
      font-weight: 500;
      font-size: 12px;
    }
  }

  .post {
    font-size: 14px;
    border-bottom: 1px solid #d2d6de;
    margin-bottom: 15px;
    padding-bottom: 15px;
    color: #666;

    .image {
      width: 100%;
      height: 100%;

    }

    .user-images {
      padding-top: 20px;
    }
  }

  .list-inline {
    padding-left: 0;
    margin-left: -5px;
    list-style: none;

    li {
      display: inline-block;
      padding-right: 5px;
      padding-left: 5px;
      font-size: 13px;
    }

    .link-black {

      &:hover,
      &:focus {
        color: #999;
      }
    }
  }

  .box-center {
    margin: 0 auto;
    display: table;
  }

  .text-muted {
    color: #777;
  }
</style>

