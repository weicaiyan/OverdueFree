import { api } from './api'
import { goLoginWithRedirect } from './navigation'
import { clearCustomerToken, clearLoginRedirect, getCustomerToken, setCustomerToken } from './storage'

export function isPhone(phone: string) {
  return /^1[3-9]\d{9}$/.test(phone)
}

export async function requireLogin() {
  const token = getCustomerToken()
  if (!token) {
    goLoginWithRedirect()
    return false
  }
  try {
    const me = await api.me({ authRedirect: false })
    if (!me.loggedIn) {
      clearCustomerToken()
      goLoginWithRedirect()
      return false
    }
    return true
  } catch (error) {
    clearCustomerToken()
    goLoginWithRedirect()
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
  clearLoginRedirect()
  uni.reLaunch({ url: '/pages/login/index' })
}
