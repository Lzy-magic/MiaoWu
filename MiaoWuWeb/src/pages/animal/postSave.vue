<template>
  <div class="container">
    <el-select v-model="form.animalId" :placeholder="$t('animal.selectAnimal')" style="width: 200px;">
      <el-option
        v-for="item in foundAnimals"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
    <el-select v-model="form.userId" :placeholder="$t('animal.selectUser')" style="width: 200px;">
      <el-option
        v-for="item in followUsers"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>

    <el-popconfirm
      :confirm-button-text="$t('common.submit')"
      :cancel-button-text="$t('common.oops')"
      icon="el-icon-info"
      icon-color="red"
      :title="$t('article.confirmRescue')"
      @onConfirm="postSave()"
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
</template>

<script>
import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'
export default {
  name: 'PostSave',
  data() {
    return {
      form: {},
      foundAnimals: [],
      followUsers: []
    }
  },
  mounted() {
    this.getFoundAnimals()
    this.getFollowUsers()
  },
  methods: {
    getFoundAnimals() {
      const url = settings.apiUrl + 'animals/userId=' + this.$store.getters.id
      const self = this
      request.request({
        url,
        method: 'get',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        }
      }).then((res, error) => {
        if (error) {
          this.loading = false
          return
        }
        if (res.code === ResultCode.SuccessCode) {
          this.foundAnimals = res.data
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
    },
    getFollowUsers() {
      const url = settings.apiUrl + 'users/getAllFollowByUserId?userId=' + this.$store.getters.id
      const self = this
      request.request({
        url,
        method: 'get',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'

        }
      }).then((res, error) => {
        if (error) {
          this.loading = false
          return
        }
        if (res.code === ResultCode.SuccessCode) {
          this.followUsers = res.data
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
    },
    postSave() {
      const url = settings.apiUrl + 'saves'
      const self = this
      const form = {
        'userId': self.form.userId,
        'animalId': self.form.animalId
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

</style>
