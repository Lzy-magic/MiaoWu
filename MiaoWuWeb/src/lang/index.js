import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import elementEnLocale from 'element-ui/lib/locale/lang/en'
import elementZhLocale from 'element-ui/lib/locale/lang/zh-CN'
import enLocale from './en'
import zhLocale from './zh-CN'

Vue.use(VueI18n)

export const LANGUAGE_KEY = 'language'
export const DEFAULT_LANGUAGE = 'en'

// Every locale merges the app messages with the matching Element UI locale.
// To add a language: create ./<code>.js with the same keys as ./en.js,
// import it here with the Element UI locale and register it below.
const messages = {
  en: {
    ...enLocale,
    ...elementEnLocale
  },
  'zh-CN': {
    ...zhLocale,
    ...elementZhLocale
  }
}

export const supportedLanguages = Object.keys(messages)

// Map an arbitrary language tag (e.g. "zh", "zh-cn", "en-US") onto a supported locale.
export function normalizeLanguage(lang) {
  if (!lang) return null
  const lower = String(lang).toLowerCase()
  const exact = supportedLanguages.find(l => l.toLowerCase() === lower)
  if (exact) return exact
  const base = lower.split('-')[0]
  return supportedLanguages.find(l => l.toLowerCase().split('-')[0] === base) || null
}

function readSaved() {
  let saved = null
  try {
    saved = window.localStorage && window.localStorage.getItem(LANGUAGE_KEY)
  } catch (e) {
    saved = null
  }
  return saved || Cookies.get(LANGUAGE_KEY)
}

// Saved choice (localStorage / cookie) first, then the browser language, then English.
export function getLanguage() {
  const saved = normalizeLanguage(readSaved())
  if (saved) return saved
  const nav = typeof navigator !== 'undefined' ? navigator : {}
  const candidates = [].concat(nav.languages || [], nav.language || [], nav.userLanguage || [])
  for (const c of candidates) {
    const lang = normalizeLanguage(c)
    if (lang) return lang
  }
  return DEFAULT_LANGUAGE
}

export function saveLanguage(lang) {
  Cookies.set(LANGUAGE_KEY, lang)
  try {
    window.localStorage && window.localStorage.setItem(LANGUAGE_KEY, lang)
  } catch (e) {
    // localStorage may be unavailable (private mode); the cookie is enough
  }
}

const i18n = new VueI18n({
  locale: getLanguage(),
  fallbackLocale: DEFAULT_LANGUAGE,
  messages
})

if (typeof document !== 'undefined') {
  document.documentElement.lang = i18n.locale
}

export default i18n
