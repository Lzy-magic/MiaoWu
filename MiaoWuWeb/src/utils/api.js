import axios from 'axios'
import settings from '@/settings'

const base = settings.baseURL

export const uploadFileRequest = (url, params) => {
  return axios({
    method: 'put',
    url: `${base}${url}`,
    data: params,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
