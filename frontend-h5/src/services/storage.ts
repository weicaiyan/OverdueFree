const CUSTOMER_TOKEN_KEY = 'overduefree_customer_token'

export function getCustomerToken() {
  return uni.getStorageSync(CUSTOMER_TOKEN_KEY) as string
}

export function setCustomerToken(token: string) {
  uni.setStorageSync(CUSTOMER_TOKEN_KEY, token)
}

export function clearCustomerToken() {
  uni.removeStorageSync(CUSTOMER_TOKEN_KEY)
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
