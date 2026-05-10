import { api } from './api'
import { clearCustomerToken, getCustomerToken, setCustomerToken } from './storage'

export function isPhone(phone: string) {
  return /^1[3-9]\d{9}$/.test(phone)
}

export async function requireLogin() {
  const token = getCustomerToken()
  if (!token) {
    uni.reLaunch({ url: '/pages/login/index' })
    return false
  }
  try {
    await api.me()
    return true
  } catch (error) {
    clearCustomerToken()
    return false
  }
}

export function saveLoginToken(token: string) {
  setCustomerToken(token)
}

export async function logout() {
  try {
    await api.logout()
  } catch (error) {
    // 退出登录以本地清理为准，后端失败时仍回到登录页。
  }
  clearCustomerToken()
  uni.reLaunch({ url: '/pages/login/index' })
}
