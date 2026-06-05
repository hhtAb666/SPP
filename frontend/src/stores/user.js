/**
 * Pinia 用户状态管理 Store
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const token = ref(localStorage.getItem('token') || '')

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }
  
  function clearUserData() {
    userInfo.value = {}
    token.value = ''
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  async function login(loginForm) {
    const res = await loginApi(loginForm)
    
    
    if (res && res.token) {
      setToken(res.token)
      if (res.userInfo) {
        setUserInfo(res.userInfo)
      }
      return res.userInfo
    } else {
      throw new Error('Token not found in response')
    }
  }

  function logout() {
    clearUserData()
  }

  return { 
    userInfo, 
    token, 
    setUserInfo, 
    setToken, 
    login, 
    logout,
    clearUserData
  }
})