export function formatAmount(value?: number | string | null) {
  const amount = Number(value ?? 0)
  if (!Number.isFinite(amount)) {
    return '0'
  }
  const fractionDigits = Number.isInteger(amount) ? 0 : 2
  return amount
    .toFixed(fractionDigits)
    .replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

export function formatAmountText(value?: number | string | null, unit = '元') {
  if (value === undefined || value === null || value === '') {
    return '待补充'
  }
  return `${formatAmount(value)}${unit}`
}

export function formatDate(value?: string | null) {
  if (!value) {
    return '待发布'
  }
  return value.length > 10 ? value.slice(0, 10) : value
}
