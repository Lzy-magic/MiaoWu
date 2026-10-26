import request from '@/utils/request'

export function getToken() {
  return request({
    url: '/qiniu/upload/token', // placeholder URL, replace with your own
    method: 'get'
  })
}
