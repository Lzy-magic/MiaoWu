<template>
  <div class="container">
    <el-button type="text" @click="back">{{ $t('common.back') }}</el-button>
    {{ $t('animal.title') }}
    <el-form ref="form" :model="form" label-width="80px">
      <el-form-item :label="$t('animal.name')">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item :label="$t('animal.age')">
        <el-input v-model="form.age" />
      </el-form-item>
      <el-form-item :label="$t('animal.gender')">
        <el-select v-model="form.gender" :placeholder="$t('animal.selectGender')">
          <el-option :label="$t('animal.male')" value="male" />
          <el-option :label="$t('animal.female')" value="female" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('animal.birthday')">
        <el-col :span="11">
          <el-date-picker v-model="form.date" type="date" :placeholder="$t('animal.selectBirthday')" style="width: 100%;" />
        </el-col>
        <el-col class="line" :span="2">-</el-col>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ $t('animal.publish') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'
export default {
  name: 'Editor',
  data() {
    return {
      form: {}
    }
  },
  methods: {
    back() {
      this.$router.go(-1)
    },
    onSubmit() {
      const self = this
      self.loading = true
      const url = settings.apiUrl + 'animals/add'
      const form = {
        'name': self.form.name,
        'gender': self.form.gender,
        'age': self.form.age,
        'date': self.form.date,
        'userId': self.$store.getters.id
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
          self.$message.success(self.$t('animal.createSuccess'))
          self.loading = false
        } else if (res.code === ResultCode.BadRequest) {
          self.$message.error(res.message)
          self.loading = false
        } else if (res.code === ResultCode.ServerInnerError) {
          this.$message.error(this.$t('common.serverError'))
          self.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
