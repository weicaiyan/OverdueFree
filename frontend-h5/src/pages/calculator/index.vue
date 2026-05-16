<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'

const principal = ref('10000')
const periods = ref('12')
const upfrontFeePercent = ref('0')
const monthlyFeePercent = ref('0.70')
const calculated = ref(false)

watch([principal, periods, upfrontFeePercent, monthlyFeePercent], () => {
  calculated.value = false
})

function estimateMonthlyIrr(netReceived: number, monthlyPayment: number, months: number) {
  if (monthlyPayment <= 0 || netReceived <= 0 || months <= 0) {
    return 0
  }
  let low = 0
  let high = 1
  for (let i = 0; i < 80; i += 1) {
    const mid = (low + high) / 2
    let presentValue = 0
    for (let period = 1; period <= months; period += 1) {
      presentValue += monthlyPayment / Math.pow(1 + mid, period)
    }
    if (presentValue > netReceived) {
      low = mid
    } else {
      high = mid
    }
  }
  return (low + high) / 2
}

function formatMoney(value: number) {
  return value.toFixed(2)
}

const validationMessage = computed(() => {
  const amount = Number(principal.value)
  const months = Number(periods.value)
  const upfrontRate = Number(upfrontFeePercent.value)
  const monthlyRate = Number(monthlyFeePercent.value)
  if (!Number.isFinite(amount) || amount <= 0) {
    return '请填写正确分期金额'
  }
  if (!Number.isInteger(months) || months <= 0 || months > 120) {
    return '分期期数请输入1-120之间的整数'
  }
  if (!Number.isFinite(upfrontRate) || upfrontRate < 0 || upfrontRate >= 100) {
    return '一次性手续费需小于100%'
  }
  if (!Number.isFinite(monthlyRate) || monthlyRate < 0 || monthlyRate >= 100) {
    return '每期手续费需小于100%'
  }
  return ''
})

const result = computed(() => {
  if (validationMessage.value) {
    return null
  }
  const amount = Number(principal.value)
  const months = Number(periods.value)
  const upfrontRate = Number(upfrontFeePercent.value) / 100
  const monthlyRate = Number(monthlyFeePercent.value) / 100
  if (!Number.isFinite(amount) || !Number.isFinite(months) || !Number.isFinite(upfrontRate) || !Number.isFinite(monthlyRate)) {
    return null
  }
  if (amount <= 0 || months <= 0 || upfrontRate < 0 || monthlyRate < 0) {
    return null
  }
  const upfrontFee = amount * upfrontRate
  const netReceived = amount - upfrontFee
  if (netReceived <= 0) {
    return null
  }
  const principalPerPeriod = amount / months
  const monthlyFee = amount * monthlyRate
  const monthlyPayment = principalPerPeriod + monthlyFee
  const totalFee = upfrontFee + monthlyFee * months
  const nominalAnnualized = monthlyRate * 12 * 100
  const monthlyIrr = estimateMonthlyIrr(netReceived, monthlyPayment, months)
  const actualAnnualized = (Math.pow(1 + monthlyIrr, 12) - 1) * 100
  return {
    upfrontFee: formatMoney(upfrontFee),
    netReceived: formatMoney(netReceived),
    principalPerPeriod: formatMoney(principalPerPeriod),
    monthlyFee: formatMoney(monthlyFee),
    monthlyPayment: formatMoney(monthlyPayment),
    totalFee: formatMoney(totalFee),
    nominalAnnualized: nominalAnnualized.toFixed(2),
    actualAnnualized: actualAnnualized.toFixed(2)
  }
})

const canCalculate = computed(() => !validationMessage.value && !!result.value)

onShow(async () => {
  await requireLogin()
})

async function calculate() {
  if (validationMessage.value || !result.value) {
    uni.showToast({ title: validationMessage.value || '请填写正确金额和期数', icon: 'none' })
    return
  }
  calculated.value = true
  api.event({
    eventType: 'USE_CALCULATOR',
    sourcePage: 'CALCULATOR',
    metadata: { used: true }
  }).catch(() => undefined)
}

function clear() {
  principal.value = ''
  periods.value = ''
  upfrontFeePercent.value = ''
  monthlyFeePercent.value = ''
  calculated.value = false
}
</script>

<template>
  <view class="calculator-page">
    <PageHeader title="真实网贷利率计算器" back />
    <view class="calc-panel">
      <view class="row">
        <text class="label">分期金额</text>
        <input v-model="principal" class="field" type="digit" />
        <text class="unit">元</text>
      </view>
      <view class="row">
        <text class="label">分期期数</text>
        <input v-model="periods" class="field" type="number" maxlength="3" />
        <text class="unit">期</text>
      </view>
      <view class="row">
        <text class="label">一次性手续费</text>
        <input v-model="upfrontFeePercent" class="field" type="digit" />
        <text class="unit">%</text>
      </view>
      <view class="row">
        <text class="label">每期手续费（利率）</text>
        <input v-model="monthlyFeePercent" class="field" type="digit" />
        <text class="unit">%</text>
      </view>
      <view class="formula">注：按到账金额和每期还款现金流估算实际年化</view>
      <view class="actions">
        <button class="primary" :class="{ disabled: !canCalculate }" :disabled="!canCalculate" @click="calculate">计算</button>
        <button class="secondary" @click="clear">清除</button>
      </view>
      <view v-if="validationMessage" class="validation-tip">{{ validationMessage }}</view>
    </view>

    <view v-if="calculated && result" class="result-card">
      <view class="result-title">估算结果</view>
      <view class="result-line">实际到账约：{{ result.netReceived }} 元</view>
      <view class="result-line">每期本金约：{{ result.principalPerPeriod }} 元</view>
      <view class="result-line">每期手续费约：{{ result.monthlyFee }} 元</view>
      <view class="result-line">每期还款约：{{ result.monthlyPayment }} 元</view>
      <view class="result-line">总手续费约：{{ result.totalFee }} 元</view>
      <view class="result-line">名义年化参考：{{ result.nominalAnnualized }}%</view>
      <view class="result-line highlight">现金流估算年化约：{{ result.actualAnnualized }}%</view>
      <view class="tip">以上计算结果仅供参考，实际费用以合同和平台规则为准。</view>
    </view>

    <view class="intro">
      <view class="intro-title">简介：</view>
      <view class="intro-text">
        很多人不知道网贷的实际利率，本工具可帮助你估算手续费折算后的年化水平，便于对还款压力有更清楚的认识。
      </view>
      <view class="bottom-tip">提示：本工具只做本地估算，不保存贷款计算明细</view>
    </view>
  </view>
</template>

<style scoped>
.calculator-page {
  min-height: 100vh;
  padding-bottom: calc(36px + env(safe-area-inset-bottom));
  background: #202932;
}

.calc-panel {
  margin-top: 12px;
  padding: 24px 18px;
  background: #f8f8f8;
  border-top: 4px solid #2a323b;
}

.row {
  display: grid;
  grid-template-columns: 126px 1fr 28px;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.label {
  text-align: right;
  color: #666666;
  font-size: 16px;
}

.field {
  height: 44px;
  padding: 0 12px;
  border: 1px solid #d0d0d0;
  background: #ffffff;
  text-align: right;
  font-size: 18px;
}

.unit {
  color: #666666;
}

.formula {
  margin: 16px 0;
  text-align: center;
  color: #ff1f1f;
  font-size: 16px;
  line-height: 1.5;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.primary,
.secondary {
  width: 76px;
  height: 44px;
  color: #ffffff;
  background: #0075c9;
  font-size: 16px;
}

.secondary {
  color: #333333;
  background: #eeeeee;
  border: 1px solid #cccccc;
}

.primary.disabled {
  opacity: 0.58;
}

.validation-tip {
  margin-top: 14px;
  text-align: center;
  color: #f75a50;
  font-size: 14px;
  line-height: 1.4;
}

.result-card,
.intro {
  padding: 22px;
  background: #ffffff;
  border-top: 1px solid #dddddd;
}

.result-title,
.intro-title {
  font-size: 22px;
  font-weight: 800;
  color: #666666;
}

.result-line,
.intro-text {
  margin-top: 14px;
  color: #666666;
  font-size: 16px;
  line-height: 1.8;
}

.result-line.highlight {
  color: #f75a50;
  font-size: 18px;
  font-weight: 800;
}

.tip,
.bottom-tip {
  margin-top: 18px;
  text-align: center;
  color: #777777;
  font-size: 14px;
  line-height: 1.5;
}
</style>
