export const ADMIN_TOKEN_KEY = 'overduefree_admin_token'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')

type QueryValue = string | number | boolean | null | undefined
type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

interface RequestOptions {
  method?: HttpMethod
  query?: Record<string, QueryValue>
  body?: unknown
}

export interface PageResult<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
}

export interface AdminLoginResult {
  token: string
  role: string
  displayName: string
  expiresAt: string
}

export interface AdminMe {
  adminId: number
  username: string
  displayName: string
  role: string
}

export interface LeadListItem {
  rowType: string
  customerId: number
  leadId?: number
  phone: string
  surname?: string
  region?: string
  debtAmount?: number
  debtType?: string
  source?: string
  firstLoginAt?: string
  leadCreatedAt?: string
  viewedWechatQr?: boolean
  lastWechatQrViewAt?: string
  latestEventType?: string
  latestEventAt?: string
  historyCount?: number
}

export interface LeadHistoryItem {
  id: number
  source?: string
  surname?: string
  region?: string
  debtAmount?: number
  debtType?: string
  debtDescription?: string
  ageRange?: string
  jobStatus?: string
  creditStatus?: string
  monthlyIncomeRange?: string
  monthlyExpenseRange?: string
  createdAt?: string
}

export interface EventHistoryItem {
  id: number
  eventType: string
  sourcePage?: string
  refType?: string
  refId?: number
  metadataJson?: string
  createdAt?: string
}

export interface CustomerHistory {
  customer: {
    customerId: number
    phone: string
    status: string
    firstLoginAt?: string
    lastLoginAt?: string
  }
  leads: LeadHistoryItem[]
  events: EventHistoryItem[]
}

export interface AssetResource {
  id: number
  assetKey: string
  title: string
  fileUrl?: string
  originalFileName?: string
  mimeType?: string
  fileSize?: number
  status: string
  updatedAt?: string
}

export interface FileUploadResult {
  url: string
  originalFileName: string
  mimeType: string
  fileSize: number
}

export interface ArticleItem {
  id: number
  title: string
  coverUrl?: string
  summary?: string
  contentText?: string
  publishTime?: string
  sortOrder?: number
  status: string
  createdAt?: string
  updatedAt?: string
}

export interface SuccessCaseItem {
  id: number
  displayName: string
  maskedPhone?: string
  overduePlatforms?: string
  overdueAmount?: number
  handlingPlan?: string
  avatarUrl?: string
  detailText?: string
  sortOrder?: number
  status: string
  createdAt?: string
  updatedAt?: string
}

export interface AdminUserItem {
  id: number
  username: string
  displayName: string
  role: string
  status: string
  lastLoginAt?: string
  createdAt?: string
  updatedAt?: string
}

export interface HealthResult {
  overallStatus: string
  applicationStatus: string
  databaseStatus: string
  databaseProductName?: string
  message?: string
  timeZone: string
  checkedAt?: string
}

export interface ExportBlobResult {
  blob: Blob
  filename: string
}

function getToken() {
  return localStorage.getItem(ADMIN_TOKEN_KEY) || ''
}

export function saveAdminToken(token: string) {
  localStorage.setItem(ADMIN_TOKEN_KEY, token)
}

export function clearAdminToken() {
  localStorage.removeItem(ADMIN_TOKEN_KEY)
}

function buildUrl(path: string, query?: Record<string, QueryValue>) {
  const url = new URL(path, API_BASE_URL || window.location.origin)
  if (!query) {
    return resolveUrl(url)
  }
  Object.entries(query).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      return
    }
    url.searchParams.set(key, String(value))
  })
  return resolveUrl(url)
}

function resolveUrl(url: URL) {
  if (API_BASE_URL) {
    return url.toString()
  }
  return `${url.pathname}${url.search}`
}

export function resolveResourceUrl(fileUrl?: string) {
  if (!fileUrl) {
    return ''
  }
  if (fileUrl.startsWith('http')) {
    return fileUrl
  }
  if (API_BASE_URL) {
    return new URL(fileUrl, API_BASE_URL).toString()
  }
  return fileUrl
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers()
  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  let body: BodyInit | undefined
  if (options.body !== undefined) {
    headers.set('Content-Type', 'application/json')
    body = JSON.stringify(options.body)
  }

  const response = await safeFetch(buildUrl(path, options.query), {
    method: options.method || 'GET',
    headers,
    body
  })

  let result: ApiResponse<T> | null = null
  try {
    result = await response.json()
  } catch {
    result = null
  }

  if (!response.ok || !result || result.code !== 0) {
    if (result?.code === 40001 || response.status === 401) {
      clearAdminToken()
    }
    throw new Error(result?.message || `请求失败：${response.status}`)
  }

  return result.data
}

async function exportBlob(path: string, payload: unknown): Promise<ExportBlobResult> {
  const headers = new Headers()
  headers.set('Content-Type', 'application/json')
  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await safeFetch(buildUrl(path), {
    method: 'POST',
    headers,
    body: JSON.stringify(payload)
  })

  if (!response.ok) {
    let message = `导出失败：${response.status}`
    try {
      const result = await response.json()
      message = result.message || message
    } catch {
      // Keep the HTTP fallback message for non-JSON responses.
    }
    throw new Error(message)
  }

  const disposition = response.headers.get('Content-Disposition') || ''
  const match = disposition.match(/filename\*=UTF-8''([^;]+)/)
  const filename = match ? decodeURIComponent(match[1]) : '线索导出.xlsx'
  return {
    blob: await response.blob(),
    filename
  }
}

async function uploadFile(file: File, category: 'IMAGE' | 'VIDEO') {
  const formData = new FormData()
  formData.append('file', file)

  const headers = new Headers()
  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await safeFetch(buildUrl('/api/admin/files/upload', { category }), {
    method: 'POST',
    headers,
    body: formData
  })
  const result = await response.json() as ApiResponse<FileUploadResult>
  if (!response.ok || result.code !== 0) {
    throw new Error(result.message || `上传失败：${response.status}`)
  }
  return result.data
}

async function safeFetch(input: RequestInfo | URL, init?: RequestInit) {
  try {
    return await fetch(input, init)
  } catch {
    throw new Error('无法连接后端服务，请确认后端已启动')
  }
}

export const adminApi = {
  health: () => request<HealthResult>('/api/health'),
  login: (payload: { username: string; password: string }) =>
    request<AdminLoginResult>('/api/admin/auth/login', { method: 'POST', body: payload }),
  logout: () => request<void>('/api/admin/auth/logout', { method: 'POST' }),
  me: () => request<AdminMe>('/api/admin/me'),
  leads: (query: Record<string, QueryValue>) =>
    request<PageResult<LeadListItem>>('/api/admin/leads', { query }),
  leadHistory: (customerId: number) =>
    request<CustomerHistory>(`/api/admin/customers/${customerId}/history`),
  exportLeads: (payload: unknown) => exportBlob('/api/admin/leads/export', payload),
  assets: () => request<AssetResource[]>('/api/admin/assets'),
  uploadFile,
  updateAsset: (assetKey: string, payload: Partial<AssetResource>) =>
    request<AssetResource>(`/api/admin/assets/${assetKey}`, { method: 'PUT', body: payload }),
  articles: (query: Record<string, QueryValue>) =>
    request<PageResult<ArticleItem>>('/api/admin/articles', { query }),
  createArticle: (payload: Partial<ArticleItem>) =>
    request<ArticleItem>('/api/admin/articles', { method: 'POST', body: payload }),
  updateArticle: (id: number, payload: Partial<ArticleItem>) =>
    request<ArticleItem>(`/api/admin/articles/${id}`, { method: 'PUT', body: payload }),
  deleteArticle: (id: number) => request<void>(`/api/admin/articles/${id}`, { method: 'DELETE' }),
  publishArticle: (id: number) => request<ArticleItem>(`/api/admin/articles/${id}/publish`, { method: 'PATCH' }),
  offlineArticle: (id: number) => request<ArticleItem>(`/api/admin/articles/${id}/offline`, { method: 'PATCH' }),
  cases: (query: Record<string, QueryValue>) =>
    request<PageResult<SuccessCaseItem>>('/api/admin/cases', { query }),
  createCase: (payload: Partial<SuccessCaseItem>) =>
    request<SuccessCaseItem>('/api/admin/cases', { method: 'POST', body: payload }),
  updateCase: (id: number, payload: Partial<SuccessCaseItem>) =>
    request<SuccessCaseItem>(`/api/admin/cases/${id}`, { method: 'PUT', body: payload }),
  deleteCase: (id: number) => request<void>(`/api/admin/cases/${id}`, { method: 'DELETE' }),
  publishCase: (id: number) => request<SuccessCaseItem>(`/api/admin/cases/${id}/publish`, { method: 'PATCH' }),
  offlineCase: (id: number) => request<SuccessCaseItem>(`/api/admin/cases/${id}/offline`, { method: 'PATCH' }),
  adminUsers: () => request<AdminUserItem[]>('/api/admin/users'),
  createAdminUser: (payload: { username: string; password: string; displayName: string; role: string }) =>
    request<AdminUserItem>('/api/admin/users', { method: 'POST', body: payload }),
  updateAdminUser: (id: number, payload: { displayName: string; role: string; status: string }) =>
    request<AdminUserItem>(`/api/admin/users/${id}`, { method: 'PUT', body: payload }),
  resetAdminPassword: (id: number, password: string) =>
    request<void>(`/api/admin/users/${id}/password`, { method: 'POST', body: { password } })
}
