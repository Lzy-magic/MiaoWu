import i18n from '@/lang'

// Translate a route meta title. Titles are stored as keys under "route.*";
// titles without a matching key are returned unchanged.
export function generateTitle(title) {
  if (!title) return title
  const key = 'route.' + title
  return i18n.te(key) ? i18n.t(key) : title
}
