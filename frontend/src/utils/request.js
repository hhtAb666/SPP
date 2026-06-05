/**
 * Axios 请求封装工具
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'


const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})


service.interceptors.request.use(
  config => {
    
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['satoken'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)


service.interceptors.response.use(
  response => {
    const res = response.data
    
    
    if (response.request.responseType ===  'blob' || response.request.responseType ===  'arraybuffer') {
      return res
    }

    if (res.code === 200) {
      return res.data
    } else {
      ElMessage.error(res.msg || '操作失败')
      
      
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  error => {
    console.error('Err:', error)
    const msg = error.response?.data?.msg || error.message || '请求失败'
    ElMessage.error(msg)
    
    if (error.response?.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
    }
    
    return Promise.reject(error)
  }
)

export default service