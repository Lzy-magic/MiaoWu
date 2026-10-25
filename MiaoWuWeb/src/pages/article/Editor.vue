<template>
  <el-container v-loading="loading" class="post-article">
    <el-header class="header">
      <el-select
        v-show="isSelectAnimalShow"
        v-model="article.animalId"
        :placeholder="$t('article.selectAnimal')"
        style="width: 200px;"
      >
        <el-option v-for="item in foundAnimals" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-input
        v-model="article.title"
        :placeholder="$t('article.titlePlaceholder')"
        style="width: 400px;margin-left: 10px"
      />
      <el-tag
        v-for="tag in article.dynamicTags"
        v-show="isTagShow"
        :key="tag"
        closable
        :disable-transitions="false"
        style="margin-left: 10px"
        @close="handleClose(tag)"
      >{{ tag }}
      </el-tag>
      <el-input
        v-if="tagInputVisible"
        ref="saveTagInput"
        v-model="tagValue"
        class="input-new-tag"
        size="small"
        @keyup.enter.native="handleInputConfirm"
        @blur="handleInputConfirm"
      />
      <el-button
        v-else
        v-show="isTagShow"
        class="button-new-tag"
        type="primary"
        size="small"
        @click="showInput"
      >+Tag
      </el-button>
    </el-header>
    <el-main class="main">
      <div id="editor">
        <mavon-editor
          ref="md"
          v-model="article.mdContent"
          style="height: 100%;width: 100%;"
          @imgAdd="imgAdd"
          @imgDel="imgDel"
        />
      </div>
      <div style="display: flex;align-items: center;margin-top: 15px;justify-content: flex-end">
        <el-button v-if="from!=undefined" @click="cancelEdit">{{ $t('article.discardChanges') }}</el-button>
        <template v-if="from==undefined || from=='draft'">
          <!--          <el-button @click="submit(0)">Save to drafts</el-button>-->
          <el-button type="primary" @click="submit(1)">{{ $t('article.publish') }}</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="submit(1)">{{ $t('article.saveChanges') }}</el-button>
        </template>
      </div>
    </el-main>
  </el-container>
</template>
<script>
// import {uploadFileRequest} from '../utils/api'
// Local Registration
import { mavonEditor } from 'mavon-editor'
import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'
import 'mavon-editor/dist/css/index.css'
// import {isNotNullORBlank} from '../utils/utils'

// The markdown-it parser instance is available via mavonEditor.markdownIt

export default {
  components: {
    mavonEditor
  },
  data() {
    return {
      foundAnimals: [],
      tagInputVisible: false,
      tagValue: '',
      loading: false,
      from: '',
      article: {
        id: '-1',
        dynamicTags: [],
        title: '',
        mdContent: '',
        cid: ''
      },
      isSelectAnimalShow: true,
      isTagShow: true,
      isUpdateMode: false
    }
  },
  mounted: function() {
    this.getFoundAnimals()
    var from = this.$route.query.from
    this.from = from
    this.getArticle()
  },
  methods: {
    getArticle() {
      const self = this
      if (self.$route.query.articleId) {
        request
          .get(
            settings.apiUrl +
                            'articles/' +
                            self.$route.query.articleId
          )
          .then(res => {
            if (res.data.authorId !== self.$store.getters.id) {
              self.$message.error(self.$t('article.noPermission'))
              self.$router.go(-1)
              return
            }
            self.article = res.data
            self.article.mdContent = res.data.content
            self.isSelectAnimalShow = false
            self.isTagShow = false
            self.isUpdateMode = true
          })
      }
    },
    cancelEdit() {
      this.$router.go(-1)
    },
    imgAdd(pos, $file) {
      // TODO: delete the uploaded image if this fails
      var _this = this
      // Step 1: upload the image to the server.
      var formdata = new FormData()
      formdata.append('image', $file)
      const config = {
        method: 'PUT',
        url: settings.apiUrl + 'imgs',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        data: formdata
      }
      request.request(config).then(resp => {
        if (resp.code === ResultCode.SuccessCode) {
          const markdown = _this.$refs.md
          markdown.$img2Url(pos, resp.data)
        }
      })
    },
    imgDel(pos) {
    },
    handleClose(tag) {
      this.article.dynamicTags.splice(this.article.dynamicTags.indexOf(tag), 1)
    },
    showInput() {
      this.tagInputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      const tagValue = this.tagValue
      if (tagValue) {
        this.article.dynamicTags.push(tagValue)
      }
      this.tagInputVisible = false
      this.tagValue = ''
    },
    back() {
      this.$router.go(-1)
    },
    submit() {
      const self = this
      self.loading = true
      if (self.isUpdateMode) {
        self.updateArticle()
        return
      }
      self.submitArticle().then((res) => {
        const articleId = res
        if (self.article.dynamicTags) {
          self.submitTags(articleId).then((result) => {
            if (result) { this.$message.success(this.$t('article.publishSuccess')) }
          }).catch(e => {
            this.delArticle(articleId)
          })
        }
      })
      self.loading = false
    },
    updateArticle() {
      const self = this
      const form = {
        id: self.article.id,
        title: self.article.title,
        content: self.article.mdContent,
        source: self.$refs.md.d_render,
        authorId: self.$store.getters.id,
        animalId: self.article.animalId,
        type: 1
      }
      const config = {
        method: 'POST',
        url: settings.apiUrl + 'articles',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        data: qs.stringify(form)
      }
      request.request(config).then(res => {
        if (res.code === ResultCode.SuccessCode) {
          self.$message.success(self.$t('article.updateSuccess'))
          self.$router.go(-1)
        } else if (res.code === ResultCode.BadRequest) {
          self.$message.success(self.$t('common.error'))
          self.$router.go(-1)
        }
        self.loading = false
      })
    },
    submitArticle() {
      return new Promise((result, reject) => {
        const self = this

        if (!self.article.animalId) {
          self.$message.error(self.$t('article.noAnimal'))
          return
        }
        if (!self.article.title) {
          self.$message.error(self.$t('article.noTitle'))
          return
        }
        if (!self.article.mdContent) {
          self.$message.error(self.$t('article.noContent'))
          return
        }
        const url = settings.apiUrl + 'articles'
        const form = {
          title: self.article.title,
          content: self.article.mdContent,
          source: self.$refs.md.d_render,
          authorId: self.$store.getters.id,
          animalId: self.article.animalId,
          type: 1
        }
        request
          .request({
            url,
            method: 'put',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            },
            data: qs.stringify(form)
          })
          .then((res, error) => {
            if (error) {
              this.loading = false
              reject()
            }
            if (res.code === ResultCode.SuccessCode) {
              result(res.data)
            } else if (res.code === ResultCode.BadRequest) {
              self.$message.error(res.message)
            } else if (res.code === ResultCode.ServerInnerError) {
              this.$message.error(this.$t('common.serverError'))
              reject()
            }
          })
      })
    },
    submitTags(articleId) {
      const url = settings.apiUrl + 'labels'
      const self = this
      const form = {
        articleId: articleId,
        labelNames: self.article.dynamicTags
      }

      return new Promise((resolve, reject) => {
        reject()
        if (self.article.dynamicTags.length === 0) {
          resolve()
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
              reject()
            }
            if (res.code === ResultCode.SuccessCode) {
              resolve()
            } else if (res.code === ResultCode.BadRequest) {
              self.$message.error(res.message)
              reject()
            } else if (res.code === ResultCode.ServerInnerError) {
              self.$message.error(self.$t('common.serverError'))
              reject()
            }
          })
      })
    },
    delArticle(id) {
      const config = {
        method: 'DELETE',
        url: settings.apiUrl + 'articles/' + id,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
      request.request(config).then(resp => {
        if (resp.code === ResultCode.SuccessCode) {
          // nothing else to do on success
        }
      })
    },
    getFoundAnimals() {
      const url = settings.apiUrl + 'animals?userId=' + this.$store.getters.id
      const self = this
      request
        .request({
          url,
          method: 'get',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        })
        .then((res, error) => {
          if (error) {
            this.loading = false
            return
          }
          if (res.code === ResultCode.SuccessCode) {
            this.foundAnimals = res.data
            this.loading = false
          } else if (res.code === ResultCode.BadRequest) {
            self.$message.error(self.$t('article.noAnimalFound'))
            // self.$message.error(res.message);
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
<style>
    .post-article > .main > #editor {
        width: 100%;
        height: 450px;
        text-align: left;
    }

    .post-article > .header {
        background-color: #ececec;
        margin-top: 10px;
        padding-left: 5px;
        display: flex;
        justify-content: flex-start;
    }

    .post-article > .main {
        /*justify-content: flex-start;*/
        display: flex;
        flex-direction: column;
        padding-left: 5px;
        background-color: #ececec;
        padding-top: 0px;
    }

    .post-article > .header > .el-tag + .el-tag {
        margin-left: 10px;
    }

    .post-article > .header > .button-new-tag {
        margin-left: 10px;
        height: 32px;
        line-height: 30px;
        padding-top: 0;
        padding-bottom: 0;
    }

    .post-article > .header > .input-new-tag {
        width: 90px;
        margin-left: 10px;
        vertical-align: bottom;
    }

    .post-article {
    }
</style>
