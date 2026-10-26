import i18n from '@/lang'
import { generateTitle } from '@/utils/i18n'

export default function getPageTitle(pageTitle) {
  const title = i18n.t('common.appTitle')
  if (pageTitle) {
    return `${generateTitle(pageTitle)} - ${title}`
  }
  return `${title}`
}
