export interface AssetResource {
  id?: number
  assetKey?: string
  title?: string
  fileUrl?: string
  originalFileName?: string
  mimeType?: string
  fileSize?: number
  status?: string
  updatedAt?: string
}

export interface HomeData {
  assets: Record<string, AssetResource | null>
  serviceSteps: Array<{
    title: string
    description: string
  }>
}

export interface PageResult<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
}

export interface ArticleItem {
  id: number
  title: string
  coverUrl?: string
  summary?: string
  contentText?: string
  publishTime?: string
  sortOrder?: number
  status?: string
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
  status?: string
}

export interface LeadPayload {
  source: string
  surname: string
  region: string
  debtAmount: number
  debtType: string
  debtDescription?: string
  ageRange?: string
  jobStatus?: string
  creditStatus?: string
  monthlyIncomeRange?: string
  monthlyExpenseRange?: string
}
