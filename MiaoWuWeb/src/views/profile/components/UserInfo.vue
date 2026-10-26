<template>
  <!--TODO: filters <-->
  <el-form>
    <el-form-item :label="$t('user.nickname')">
      <el-input v-model="form.name" @input="change($event)" />
    </el-form-item>
    <el-form-item :label="$t('user.password')">
      <el-input v-model="form.password" @input="change($event)" />
    </el-form-item>
    <el-form-item :label="$t('user.idCard')">
      <el-input v-model="form.idCode" :disable="false" @input="change($event)" />
    </el-form-item>
    <el-form-item :label="$t('user.phone')">
      <el-input v-model="form.phone" @input="change($event)" />
    </el-form-item>
    <!--  <el-form-item label="Email">-->
    <!--    <el-input v-model.trim="user.email" />-->
    <!--  </el-form-item>-->
    <el-form-item>
      <el-button type="primary" @click="submit">{{ $t('profile.submitChanges') }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import request from '@/utils/request'
import settings from '@/settings'
import { ResultCode } from '@/utils/ResultCode'
import qs from 'querystring'

export default {
  name: 'UserInfo',
  props: {
    user: {
      type: Object,
      default: () => {
        return {
          name: '',
          idCode: '',
          password: '',
          phone: ''
        }
      }
    }
  },
  data() {
    return {
      form: {
      }
    }
  },
  computed: {
  },
  mounted() {

  },
  created() {
    this.form = {
      name: this.$store.getters.name,
      password: this.$store.getters.password,
      idCode: this.$store.getters.idCode,
      phone: this.$store.getters.phone
    }
  },
  methods: {
    submit() {
      // todo invalidate
      const url = settings.apiUrl + 'users'
      this.form.id = this.$store.getters.id
      request
        .request({
          url,
          method: 'PUT',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          data: qs.stringify(this.form)
        })
        .then((result, error) => {
          if (error) {
            this.loading = false
            return
          }
          if (result.code === ResultCode.SuccessCode) {
            this.$message.success(this.$t('profile.updateSuccess'))
            this.loading = false
          } else if (result.code === ResultCode.BadRequest) {
            this.$message.error(this.$t('profile.paramError'))
            this.loading = false
          }
        })
    },
    change(e) {
      this.$forceUpdate()
    }

  }
}
</script>

<style scoped>
</style>
