import { clearLoginRedirect, getLoginRedirect, setLoginRedirect } from './storage'

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

export function goLoginWithRedirect() {
  const redirectUrl = getCurrentPageUrl()
  if (redirectUrl) {
    setLoginRedirect(redirectUrl)
  }
  uni.reLaunch({ url: '/pages/login/index' })
}

export function redirectAfterLogin(defaultUrl = '/pages/home/index') {
  const redirectUrl = getLoginRedirect()
  clearLoginRedirect()
  uni.reLaunch({ url: redirectUrl || defaultUrl })
}
