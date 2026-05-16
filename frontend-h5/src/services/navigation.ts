import { clearLoginRedirect, getLoginRedirect, setLoginRedirect } from './storage'

const NAVIGATION_LOCK_MS = 650

let navigationLocked = false
let unlockTimer: ReturnType<typeof setTimeout> | null = null

function releaseNavigation() {
  navigationLocked = false
  if (unlockTimer) {
    clearTimeout(unlockTimer)
    unlockTimer = null
  }
}

function lockNavigation() {
  if (navigationLocked) {
    return false
  }
  navigationLocked = true
  if (unlockTimer) {
    clearTimeout(unlockTimer)
  }
  unlockTimer = setTimeout(() => {
    releaseNavigation()
  }, NAVIGATION_LOCK_MS)
  return true
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

function normalizePageUrl(url: string) {
  if (!url.startsWith('/pages/') || url.startsWith('/pages/login/index')) {
    return ''
  }
  return url
}

export function goLoginWithRedirect() {
  const redirectUrl = getCurrentPageUrl()
  if (redirectUrl) {
    setLoginRedirect(redirectUrl)
  }
  uni.reLaunch({ url: '/pages/login/index' })
}

export function redirectAfterLogin(defaultUrl = '/pages/home/index') {
  const redirectUrl = normalizePageUrl(getLoginRedirect())
  clearLoginRedirect()
  uni.reLaunch({ url: redirectUrl || defaultUrl })
}

export function safeReLaunch(url: string) {
  if (!lockNavigation()) {
    return
  }
  uni.reLaunch({
    url,
    fail: () => {
      releaseNavigation()
    }
  })
}

export function safeNavigateTo(url: string) {
  if (!lockNavigation()) {
    return
  }
  uni.navigateTo({
    url,
    fail: () => {
      releaseNavigation()
    }
  })
}

export function safeNavigateBack(fallbackUrl = '/pages/home/index') {
  if (!lockNavigation()) {
    return
  }
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({
      fail: () => {
        releaseNavigation()
        safeReLaunch(fallbackUrl)
      }
    })
    return
  }
  uni.reLaunch({
    url: fallbackUrl,
    fail: () => {
      releaseNavigation()
    }
  })
}
