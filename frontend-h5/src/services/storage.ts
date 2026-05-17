const CUSTOMER_TOKEN_KEY = 'overduefree_customer_token'
const LOGIN_REDIRECT_KEY = 'overduefree_login_redirect'
const CUSTOMER_DRAFT_KEYS = [
  'overduefree_ai_chat_draft',
  'overduefree_plan_form_draft'
]

function getStorageString(key: string) {
  const value = uni.getStorageSync(key)
  return typeof value === 'string' ? value : ''
}

export function getCustomerToken() {
  return getStorageString(CUSTOMER_TOKEN_KEY)
}

export function setCustomerToken(token: string) {
  uni.setStorageSync(CUSTOMER_TOKEN_KEY, token)
}

export function clearCustomerToken() {
  uni.removeStorageSync(CUSTOMER_TOKEN_KEY)
}

export function getLoginRedirect() {
  return getStorageString(LOGIN_REDIRECT_KEY)
}

export function setLoginRedirect(url: string) {
  uni.setStorageSync(LOGIN_REDIRECT_KEY, url)
}

export function clearLoginRedirect() {
  uni.removeStorageSync(LOGIN_REDIRECT_KEY)
}

export function clearCustomerDrafts() {
  CUSTOMER_DRAFT_KEYS.forEach((key) => {
    uni.removeStorageSync(key)
  })
}

export function getStorageObject<T>(key: string, fallback: T): T {
  const value = uni.getStorageSync(key)
  if (!value) {
    return fallback
  }
  if (typeof value !== 'string') {
    return value as T
  }
  try {
    return JSON.parse(value) as T
  } catch (error) {
    return fallback
  }
}

export function setStorageObject<T>(key: string, value: T) {
  uni.setStorageSync(key, JSON.stringify(value))
}

export function removeStorageItem(key: string) {
  uni.removeStorageSync(key)
}
