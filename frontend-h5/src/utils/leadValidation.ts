const MAX_DEBT_AMOUNT = 9999999999.99

export interface LeadRequiredFields {
  surname: string
  region: string
  debtAmount: string
}

export function parseDebtAmount(value: string) {
  return Number(value.trim())
}

export function validateSurname(value: string) {
  const text = value.trim()
  if (!text) {
    return '请输入称呼'
  }
  if (text.length > 50) {
    return '称呼最多50个字'
  }
  return ''
}

export function validateRegion(value: string) {
  const text = value.trim()
  if (!text) {
    return '请输入地区'
  }
  if (text.length > 100) {
    return '地区最多100个字'
  }
  return ''
}

export function validateDebtAmount(value: string) {
  const text = value.trim()
  if (!/^\d+(\.\d{1,2})?$/.test(text)) {
    return '债务金额最多保留2位小数'
  }
  const amount = parseDebtAmount(text)
  if (!Number.isFinite(amount) || amount <= 0) {
    return '请输入债务金额'
  }
  if (amount > MAX_DEBT_AMOUNT) {
    return '债务金额过大，请重新填写'
  }
  return ''
}

export function validateLeadRequiredFields(fields: LeadRequiredFields) {
  return validateSurname(fields.surname) || validateRegion(fields.region) || validateDebtAmount(fields.debtAmount)
}
