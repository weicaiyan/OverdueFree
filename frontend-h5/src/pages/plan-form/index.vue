<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { HomeData, LeadPayload } from '../../types'

const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)
const submitting = ref(false)
const submitted = ref(false)
const form = ref({
  surname: '',
  region: '',
  debtAmount: '',
  debtType: 'ONLINE_LOAN',
  debtDescription: '',
  ageRange: '26-35岁',
  jobStatus: '稳定工作',
  creditStatus: '一般',
  monthlyIncomeRange: '5000-8000',
  monthlyExpenseRange: '2000-3000'
})

const groups = [
  { key: 'ageRange', title: '年龄', options: ['18-25岁', '26-35岁', '36-45岁', '45以上'] },
  { key: 'jobStatus', title: '工作情况', options: ['稳定工作', '无业', '临时工', '灵活就业', '退休'] },
  { key: 'creditStatus', title: '征信情况', options: ['良好', '一般', '较差'] },
  { key: 'monthlyIncomeRange', title: '月收入', options: ['小于2000', '2000-5000', '5000-8000', '8000以上'] },
  { key: 'monthlyExpenseRange', title: '月开销', options: ['小于1000', '1000-2000', '2000-3000', '3000以上'] }
] as const

const debtTypes = [
  { label: '网贷', value: 'ONLINE_LOAN' },
  { label: '信用卡', value: 'CREDIT_CARD' },
  { label: '信用贷', value: 'CREDIT_LOAN' },
  { label: '车贷', value: 'CAR_LOAN' },
  { label: '其他', value: 'OTHER' }
]

const submitText = computed(() => {
  if (submitting.value) {
    return '提交中...'
  }
  return submitted.value ? '查看顾问二维码' : '获取规划方案'
})

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  try {
    homeData.value = await api.home()
  } catch (error) {
    uni.showToast({ title: '顾问二维码加载失败', icon: 'none' })
  }
})

function choose(key: string, value: string) {
  ;(form.value as Record<string, string>)[key] = value
}

function selected(key: string, value: string) {
  return (form.value as Record<string, string>)[key] === value
}

async function submit() {
  if (submitted.value) {
    qrVisible.value = true
    return
  }
  if (submitting.value) {
    return
  }
  if (!form.value.surname.trim() || !form.value.region.trim() || Number(form.value.debtAmount) <= 0) {
    uni.showToast({ title: '请填写必填信息', icon: 'none' })
    return
  }
  submitting.value = true
  const payload: LeadPayload = {
    source: 'PLAN_ASSESSMENT',
    surname: form.value.surname.trim(),
    region: form.value.region.trim(),
    debtAmount: Number(form.value.debtAmount),
    debtType: form.value.debtType,
    debtDescription: form.value.debtDescription.trim(),
    ageRange: form.value.ageRange,
    jobStatus: form.value.jobStatus,
    creditStatus: form.value.creditStatus,
    monthlyIncomeRange: form.value.monthlyIncomeRange,
    monthlyExpenseRange: form.value.monthlyExpenseRange
  }
  try {
    await api.submitLead(payload)
    submitted.value = true
    uni.showToast({ title: '已收到，人工将结合情况评估', icon: 'none' })
    qrVisible.value = true
  } catch (error) {
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <view class="plan-page">
    <PageHeader title="帮您规划和优化债务" back />
    <view class="panel">
      <view class="notice">* 以下基本信息请如实填写，填写越详细评估越准确</view>
      <view class="field-title">怎么称呼您 *</view>
      <input v-model="form.surname" class="field" placeholder="例如：张先生" />
      <view class="field-title">所在地区 *</view>
      <input v-model="form.region" class="field" placeholder="例如：重庆" />
      <view class="field-title">债务金额 *</view>
      <input v-model="form.debtAmount" class="field" type="digit" placeholder="例如：50000" />

      <view class="field-title">债务类型 *</view>
      <view class="chips">
        <button
          v-for="item in debtTypes"
          :key="item.value"
          class="chip"
          :class="{ active: form.debtType === item.value }"
          @click="form.debtType = item.value"
        >
          {{ item.label }}
        </button>
      </view>

      <view v-for="group in groups" :key="group.key" class="group">
        <view class="field-title">{{ group.title }}</view>
        <view class="chips">
          <button
            v-for="option in group.options"
            :key="option"
            class="chip"
            :class="{ active: selected(group.key, option) }"
            @click="choose(group.key, option)"
          >
            {{ option }}
          </button>
        </view>
      </view>

      <view class="field-title">补充描述</view>
      <textarea v-model="form.debtDescription" class="textarea" placeholder="可简单说明逾期平台、收入和当前压力" />
      <view v-if="submitted" class="success-card">
        信息已收到，人工顾问将结合您的情况做初步评估。您可以继续查看顾问二维码。
      </view>
    </view>
    <button class="fixed-cta" :class="{ submitted }" @click="submit">{{ submitText }}</button>
    <WechatQrModal :visible="qrVisible" :asset="homeData.assets.wechatQr" source-page="PLAN_FORM" @close="qrVisible = false" />
  </view>
</template>

<style scoped>
.plan-page {
  min-height: 100vh;
  padding-bottom: 96px;
  box-sizing: border-box;
  background: #efefef;
}

.panel {
  margin-top: 18px;
  padding: 26px 18px 120px;
  border-radius: 16px 16px 0 0;
  background: #ffffff;
}

.notice {
  margin-bottom: 22px;
  color: #ff5a42;
  font-size: 14px;
  line-height: 1.5;
}

.field-title {
  margin: 18px 0 10px;
  font-size: 18px;
  font-weight: 800;
}

.field,
.textarea {
  width: 100%;
  padding: 0 14px;
  border-radius: 10px;
  background: #f6f6f6;
  font-size: 16px;
  box-sizing: border-box;
}

.field {
  height: 46px;
}

.textarea {
  height: 96px;
  padding-top: 12px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.chip {
  min-width: 74px;
  height: 42px;
  padding: 0 16px;
  border-radius: 10px;
  color: #333333;
  background: #f5f5f5;
  font-size: 16px;
}

.chip.active {
  color: #ffffff;
  background: #f75a50;
}

.group {
  margin-top: 4px;
}

.fixed-cta {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: 24px;
  z-index: 25;
  height: 58px;
  border-radius: 30px;
  color: #ffffff;
  background: #ef5a4f;
  font-size: 18px;
  font-weight: 800;
}

.fixed-cta.submitted {
  background: #45b854;
}

.success-card {
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 12px;
  color: #b83b32;
  background: #fff0ee;
  font-size: 14px;
  line-height: 1.6;
}
</style>
