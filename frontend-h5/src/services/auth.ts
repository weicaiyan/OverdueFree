import { api } from './api'
import { clearCustomerToken, getCustomerToken, setCustomerToken, setLoginRedirect } from './storage'

export function isPhone(phone: string) {
  return /^1[3-9]\d{9}$/.test(phone)
}

function getCurrentPageUrl() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as
    | {
        route?: string
        options?: Record<string, string | number | boolean | undefined | null>
      }
    | undefined
  if (!currentPage?.route || currentPage.route === 'pages/login/index') {
    return ''
  }
  const query = Object.entries(currentPage.options || {})
    .filter(([, value]) => value !== undefined && value !== null && value !== '')
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
    .join('&')
  return `/${currentPage.route}${query ? `?${query}` : ''}`
}

function goLogin() {
  const redirectUrl = getCurrentPageUrl()
  if (redirectUrl) {
    setLoginRedirect(redirectUrl)
  }
  uni.reLaunch({ url: '/pages/login/index' })
}

export async function requireLogin() {
  const token = getCustomerToken()
  if (!token) {
    goLogin()
    return false
  }
  try {
    const me = await api.me()
    if (!me.loggedIn) {
      clearCustomerToken()
      goLogin()
      return false
    }
    return true
  } catch (error) {
    clearCustomerToken()
    goLogin()
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
