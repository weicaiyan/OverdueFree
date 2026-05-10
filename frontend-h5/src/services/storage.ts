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
