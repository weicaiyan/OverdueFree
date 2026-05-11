import type { ArticleItem, HomeData, LeadPayload, PageResult, SuccessCaseItem } from '../types'
import { clearCustomerToken, getCustomerToken } from './storage'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export function resolveFileUrl(fileUrl?: string) {
  if (!fileUrl) {
    return ''
  }
  if (/^https?:\/\//.test(fileUrl)) {
    return fileUrl
  }
  return `${API_BASE_URL}${fileUrl.startsWith('/') ? fileUrl : `/${fileUrl}`}`
}

export function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof Error && error.message) {
    return error.message
  }
  return fallback
}

export function request<T>(url: string, method: HttpMethod = 'GET', data?: object): Promise<T> {
  const token = getCustomerToken()
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${API_BASE_URL}${url}`,
      method,
      data: data as UniApp.RequestOptions['data'],
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {})
      },
      success: (res) => {
        const body = typeof res.data === 'object' && res.data !== null ? (res.data as ApiResponse<T>) : null
        if (body && body.code === 0) {
          resolve(body.data)
          return
        }
        if (body && body.code === 40001) {
          clearCustomerToken()
          uni.reLaunch({ url: '/pages/login/index' })
        }
        if (body?.message) {
          reject(new Error(body.message))
          return
        }
        if (res.statusCode >= 500) {
          reject(new Error('系统繁忙，请稍后重试'))
          return
        }
        if (res.statusCode >= 400) {
          reject(new Error('请求参数不正确'))
          return
        }
        reject(new Error('请求失败，请稍后重试'))
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '网络异常'))
      }
    })
  })
}

export const api = {
  sendCode(phone: string) {
    return request<{ mockCode: string }>('/api/app/auth/send-code', 'POST', { phone })
  },
  login(phone: string, code: string) {
    return request<{ token: string; expiresAt: string }>('/api/app/auth/login', 'POST', { phone, code })
  },
  logout() {
    return request<void>('/api/app/auth/logout', 'POST')
  },
  me() {
    return request<{ loggedIn: boolean }>('/api/app/me')
  },
  home() {
    return request<HomeData>('/api/app/home')
  },
  articles(page = 1, pageSize = 10) {
    return request<PageResult<ArticleItem>>(`/api/app/articles?page=${page}&pageSize=${pageSize}`)
  },
  articleDetail(id: number) {
    return request<ArticleItem>(`/api/app/articles/${id}`)
  },
  cases(page = 1, pageSize = 10) {
    return request<PageResult<SuccessCaseItem>>(`/api/app/cases?page=${page}&pageSize=${pageSize}`)
  },
  caseDetail(id: number) {
    return request<SuccessCaseItem>(`/api/app/cases/${id}`)
  },
  submitLead(payload: LeadPayload) {
    return request<{ leadId: number }>('/api/app/leads', 'POST', payload)
  },
  event(payload: {
    eventType: string
    sourcePage?: string
    refType?: string
    refId?: number | null
    metadata?: Record<string, unknown>
  }) {
    return request<void>('/api/app/events', 'POST', payload)
  }
}
