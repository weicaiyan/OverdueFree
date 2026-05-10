<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'

const principal = ref('10000')
const periods = ref('12')
const upfrontFeePercent = ref('0')
const monthlyFeePercent = ref('0.70')
const calculated = ref(false)

const result = computed(() => {
  const amount = Number(principal.value)
  const months = Number(periods.value)
  const upfrontRate = Number(upfrontFeePercent.value) / 100
  const monthlyRate = Number(monthlyFeePercent.value) / 100
  if (amount <= 0 || months <= 0) {
    return null
  }
  const upfrontFee = amount * upfrontRate
  const monthlyFee = amount * monthlyRate
  const totalFee = upfrontFee + monthlyFee * months
  const annualized = (totalFee / amount / (months / 12)) * 100
  return {
    monthlyFee: monthlyFee.toFixed(2),
    totalFee: totalFee.toFixed(2),
    annualized: annualized.toFixed(2)
  }
})

onShow(async () => {
  await requireLogin()
})

async function calculate() {
  if (!result.value) {
    uni.showToast({ title: '请填写正确金额和期数', icon: 'none' })
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
        <input v-model="periods" class="field" type="number" />
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
      <view class="formula">注：每期手续费（利率）=(年利率/12)</view>
      <view class="actions">
        <button class="primary" @click="calculate">计算</button>
        <button class="secondary" @click="clear">清除</button>
      </view>
    </view>

    <view v-if="calculated && result" class="result-card">
      <view class="result-title">估算结果</view>
      <view class="result-line">每期手续费约：{{ result.monthlyFee }} 元</view>
      <view class="result-line">总手续费约：{{ result.totalFee }} 元</view>
      <view class="result-line">折算年化约：{{ result.annualized }}%</view>
      <view class="tip">以上计算结果仅供参考。</view>
    </view>

    <view class="intro">
      <view class="intro-title">简介：</view>
      <view class="intro-text">
        很多人不知道网贷的实际利率，本工具可帮助你估算手续费折算后的年化水平，便于对还款压力有更清楚的认识。
      </view>
      <view class="bottom-tip">提示：以上计算结果仅供参考</view>
    </view>
  </view>
</template>

<style scoped>
.calculator-page {
  min-height: 100vh;
  padding-bottom: 36px;
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

.tip,
.bottom-tip {
  margin-top: 18px;
  text-align: center;
  color: #777777;
  font-size: 14px;
}
</style>
