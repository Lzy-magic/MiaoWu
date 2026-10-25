<template>
  <el-dropdown trigger="click" class="international" @command="handleSetLanguage">
    <div>
      <svg-icon class-name="international-icon" icon-class="language" />
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item v-for="item of languages" :key="item.value" :disabled="language===item.value" :command="item.value">
        {{ item.label }}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
import getPageTitle from '@/utils/get-page-title'

export default {
  computed: {
    language() {
      return this.$store.getters.language
    },
    languages() {
      // Each language is labelled in its own language so users can always find theirs
      return Object.keys(this.$i18n.messages).map(value => ({
        value,
        label: this.$t('langSelect.name', value)
      }))
    }
  },
  methods: {
    handleSetLanguage(lang) {
      this.$i18n.locale = lang
      this.$store.dispatch('app/setLanguage', lang)
      document.documentElement.lang = lang
      document.title = getPageTitle(this.$route.meta && this.$route.meta.title)
      this.$message({
        message: this.$t('langSelect.switchSuccess'),
        type: 'success'
      })
    }
  }
}
</script>
