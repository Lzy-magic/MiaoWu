<template>
  <div class="register-container">
    <el-form
      ref="registerForm"
      :model="registerForm"
      :rules="rules"
      class="register-form"
      autocomplete="on"
      label-position="left"
    >

      <div class="title-container">
        <h3 class="title">
          <el-button type="primary" style="position: absolute; left: 0" @click="back">{{ $t('common.back') }}</el-button>
          {{ $t('user.register') }}
        </h3>
      </div>

      <el-form-item prop="phone">
        <span class="svg-container">
          <i class="el-icon-phone" />
        </span>
        <el-input
          ref="phone"
          v-model="registerForm.phone"
          :placeholder="$t('user.phone')"
          name="phone"
          type="text"
          tabindex="1"
          maxlength="11"
        />
      </el-form-item>

      <el-tooltip v-model="capsTooltip" content="Caps lock is On" placement="right" manual>
        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password"
            v-model="registerForm.password"
            :type="passwordType"
            :placeholder="$t('user.password')"
            name="password"
            tabindex="2"
            maxlength="16"
            @keyup.native="checkCapslock"
            @blur="capsTooltip = false"
            @keyup.enter.native="handleRegister"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
          </span>
        </el-form-item>
      </el-tooltip>

      <el-tooltip v-model="capsTooltip" content="Caps lock is On" placement="right" manual>
        <el-form-item prop="cpwd">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="cpwd"
            v-model="cpwd"
            :type="passwordType"
            :placeholder="$t('user.confirmPassword')"
            name="cpwd"
            tabindex="3"
            maxlength="16"
            @keyup.native="checkCapslock"
            @blur="capsTooltip = false"
            @keyup.enter.native="handleRegister"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
          </span>
        </el-form-item>
      </el-tooltip>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="name"
          v-model="registerForm.name"
          :placeholder="$t('user.nickname')"
          name="name"
          type="text"
          tabindex="4"
          maxlength="12"
        />
      </el-form-item>

      <el-form-item prop="idCode">
        <span class="svg-container">
          <i class="el-icon-bank-card" />
        </span>
        <el-input
          ref="idCode"
          v-model="registerForm.idCode"
          :placeholder="$t('user.idCard')"
          name="idCode"
          type="text"
          maxlength="18"
          tabindex="5"
        />
      </el-form-item>

      <el-button
        :loading="loading"
        type="primary"
        style="width:100%;margin-bottom:30px;"
        @click.native.prevent="handleRegister"
      >{{ $t('user.register') }}
      </el-button>

    </el-form>

  </div>
</template>

<script>
import settings from '../../settings'
import request from '@/utils/request'
import qs from 'querystring'
import { ResultCode } from '@/utils/ResultCode'

export default {
  // TODO: validate password confirmation and the user name
  name: 'Register',
  data() {
    var self = this
    const phoneRegex = new RegExp('((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$')
    // evaluated lazily so the message follows the current language
    const isPhoneMsg = () => self.$t('user.invalidPhone')
    const isPhone = (rule, value, callback) => {
      self.validator(rule, value, callback, isPhoneMsg(), phoneRegex)
    }
    const idCodeRegex = new RegExp('^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$')
    // evaluated lazily so the message follows the current language
    const isIdCodeMsg = () => self.$t('user.idCardLength')
    const isIdCode = (rule, value, callback) => {
      self.validator(rule, value, callback, isIdCodeMsg(), idCodeRegex)
    }
    // First-generation IDs have 15 digits, second-generation IDs have 18
    const pwdRegex = new RegExp('^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$')
    // evaluated lazily so the message follows the current language
    const isPwdCodeMsg = () => self.$t('user.passwordRule')
    const isPwd = (rule, value, callback) => {
      self.validator(rule, value, callback, isPwdCodeMsg(), pwdRegex)
    }
    const userNameRegex = new RegExp('^[a-zA-z\u2E80-\u9FFF]{0,12}$')
    // evaluated lazily so the message follows the current language
    const isUserNameMsg = () => self.$t('user.nicknameLength')
    const isUserName = (rule, value, callback) => {
      self.validator(rule, value, callback, userNameRegex, isUserNameMsg())
    }
    // evaluated lazily so the message follows the current language
    const isSamePwdMsg = () => self.$t('user.passwordMismatch')
    const isSamePwd = (rule, value, callback) => {
      if (self.cpwd !== self.registerForm.password) {
        callback(new Error(isSamePwdMsg()))
      }
    }
    return {
      registerForm: {},
      cpwd: '',
      rules: {
        phone: [{ required: true, trigger: 'blur' },
          { validator: isPhone }
        ],
        password: [{ required: true, trigger: 'blur' },
          { validator: isPwd }
        ],
        cpwd: [{ trigger: 'blur' },
          { validator: isSamePwd }
        ],
        idCode: [{ required: true, trigger: 'blur' },
          { validator: isIdCode }
        ],
        userName: [{ required: true, trigger: 'blur' },
          { validator: isUserName }
        ]
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
        shortcuts: [{
          text: this.$t('user.today'),
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: this.$t('user.yesterday'),
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24)
            picker.$emit('pick', date)
          }
        }, {
          text: this.$t('user.weekAgo'),
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', date)
          }
        }]
      },
      // TODO: confirm password
      capsTooltip: false,
      passwordType: 'password',
      loading: false
      // defaultDate: new Date().setFullYear(1997,9,9)
    }
  },
  methods: {
    back() {
      this.$router.go(-1)
    },
    validator(rule, value, callback, message, regex) {
      if (!regex.test(value)) {
        return callback(new Error(message))
      } else {
        callback()
      }
    },
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    getInfoFromIdCode(idCode) {
      // Extract the date of birth
      var birthday = idCode.substring(6, 10) + '-' + idCode.substring(10, 12) + '-' + idCode.substring(12, 14)
      // birthday = parseStrToDate(birthday);
      this.registerForm.birthday = birthday

      // Extract the gender
      if (parseInt(idCode.substr(16, 1)) % 2 === 1) {
        this.registerForm.gender = 'man'
      } else {
        this.registerForm.gender = 'woman'
      }
    },
    handleRegister() {
      this.loading = true
      this.$store.dispatch('user/register', this.registerForm)
      // test
      // this.registerForm = {
      //   name: 'Tiny',
      //   password: 'icelzyREMOVED_PASSWORD',
      //   phone: '18300000000',
      //   idCode: '450881199708081173',
      // }
      if (Object.keys(this.registerForm).length !== 4 && this.cpwd !== '') {
        this.$message.error(this.$t('user.incomplete'))
        this.loading = false
        return
      }
      this.getInfoFromIdCode(this.registerForm.idCode)
      this.$refs.registerForm.validate(valid => {
        valid = true
        if (valid) {
          const url = settings.apiUrl + 'users'
          request.request({
            url,
            method: 'post',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'

            },
            data: qs.stringify(this.registerForm)
          }).then((result, error) => {
            if (error) {
              this.loading = false
              return
            }
            if (result.code === ResultCode.SuccessCode) {
              this.loading = false
              this.$message({ message: this.$t('user.registerSuccess'), type: 'success' })
              this.loginForm.username = this.registerForm.username
              this.loginForm.password = this.registerForm.password
              this.$router.push({ path: this.redirect || '/', query: this.otherQuery })
            } else if (result.code === ResultCode.ServerInnerError) {
              this.$message.error(this.$t('common.serverError'))
              this.loading = false
            } else if (result.code === ResultCode.DATA_ALREADY_EXISTEDINT) {
              this.$message.error(this.$t('user.phoneTaken'))
              this.loading = false
            }
          })
        } else {
          this.$message.error(this.$t('user.invalidInfo'))
          this.loading = false
        }
      })
      this.registerForm = {}
    }
  }
}
</script>
<style lang="scss">
  /* Fix the mismatched input background and caret color */
  /* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

  $bg: #283443;
  $light_gray: #fff;
  $cursor: #fff;

  @supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
    .register-container .el-input input {
      color: $cursor;
    }
  }

  /* reset element-ui css */
  .register-container {
    .el-input {
      display: inline-block;
      height: 47px;
      width: 85%;

      input {
        background: transparent;
        border: 0px;
        -webkit-appearance: none;
        border-radius: 0px;
        padding: 12px 5px 12px 15px;
        color: $light_gray;
        height: 47px;
        caret-color: $cursor;

        &:-webkit-autofill {
          box-shadow: 0 0 0px 1000px $bg inset !important;
          -webkit-text-fill-color: $cursor !important;
        }
      }
    }

    .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
    }
  }
</style>

<style lang="scss" scoped>
  $bg: #2d3a4b;
  $dark_gray: #889aa4;
  $light_gray: #eee;

  .register-container {
    min-height: 100%;
    width: 100%;
    background-color: $bg;
    overflow: hidden;

    .register-form {
      position: relative;
      width: 520px;
      max-width: 100%;
      padding: 160px 35px 0;
      margin: 0 auto;
      overflow: hidden;
    }

    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 10px;

      span {
        &:first-of-type {
          margin-right: 16px;
        }
      }
    }

    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
    }

    .title-container {
      position: relative;

      .title {
        font-size: 26px;
        color: $light_gray;
        margin: 0px auto 40px auto;
        text-align: center;
        font-weight: bold;
      }
    }

    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
      user-select: none;
    }

    .thirdparty-button {
      position: absolute;
      right: 0;
      bottom: 6px;
    }

    @media only screen and (max-width: 470px) {
      .thirdparty-button {
        display: none;
      }
    }

    .is-selected {
      color: #1989FA;
    }
  }

</style>
