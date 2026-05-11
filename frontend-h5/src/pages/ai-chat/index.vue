<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'
import { getStorageObject, removeStorageItem, setStorageObject } from '../../services/storage'
import type { HomeData, LeadPayload } from '../../types'

const AI_CHAT_DRAFT_KEY = 'overduefree_ai_chat_draft'

interface ChatDraft {
  step: number
  form: {
    surname: string
    region: string
    debtAmount: string
    debtType: string
    debtDescription: string
  }
}

function defaultForm() {
  return {
    surname: '',
    region: '',
    debtAmount: '',
    debtType: 'ONLINE_LOAN',
    debtDescription: ''
  }
}

const step = ref(0)
const submitting = ref(false)
const submitted = ref(false)
const qrVisible = ref(false)
const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const form = ref(defaultForm())

const debtTypes = [
  { label: '网贷', value: 'ONLINE_LOAN' },
  { label: '信用卡', value: 'CREDIT_CARD' },
  { label: '信用贷', value: 'CREDIT_LOAN' },
  { label: '车贷', value: 'CAR_LOAN' },
  { label: '其他', value: 'OTHER' }
]

const question = computed(() => {
  return ['怎么称呼您？', '您所在地区是哪里？', '大概逾期金额是多少？', '主要债务类型？', '还有什么想补充？'][step.value]
})

watch(
  () => ({ step: step.value, form: form.value }),
  (draft) => {
    if (!submitted.value) {
      setStorageObject(AI_CHAT_DRAFT_KEY, draft)
    }
  },
  { deep: true }
)

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  restoreDraft()
  try {
    homeData.value = await api.home()
  } catch (error) {
    uni.showToast({ title: '顾问二维码加载失败', icon: 'none' })
  }
})

function restoreDraft() {
  if (submitted.value) {
    return
  }
  const draft = getStorageObject<ChatDraft | null>(AI_CHAT_DRAFT_KEY, null)
  if (!draft) {
    return
  }
  step.value = Math.min(Math.max(Number(draft.step) || 0, 0), 4)
  form.value = {
    ...defaultForm(),
    ...draft.form
  }
}

function next() {
  if (step.value === 0 && !form.value.surname.trim()) {
    uni.showToast({ title: '请输入称呼', icon: 'none' })
    return
  }
  if (step.value === 1 && !form.value.region.trim()) {
    uni.showToast({ title: '请输入地区', icon: 'none' })
    return
  }
  if (step.value === 2 && Number(form.value.debtAmount) <= 0) {
    uni.showToast({ title: '请输入债务金额', icon: 'none' })
    return
  }
  if (step.value < 4) {
    step.value += 1
  }
}

function prev() {
  if (step.value > 0) {
    step.value -= 1
  }
}

async function submit() {
  if (submitted.value) {
    qrVisible.value = true
    return
  }
  if (submitting.value) {
    return
  }
  submitting.value = true
  const payload: LeadPayload = {
    source: 'AI_CHAT',
    surname: form.value.surname.trim(),
    region: form.value.region.trim(),
    debtAmount: Number(form.value.debtAmount),
    debtType: form.value.debtType,
    debtDescription: form.value.debtDescription.trim()
  }
  try {
    await api.submitLead(payload)
    submitted.value = true
    removeStorageItem(AI_CHAT_DRAFT_KEY)
    uni.showToast({ title: '提交成功', icon: 'none' })
    qrVisible.value = true
  } catch (error) {
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  step.value = 0
  submitted.value = false
  qrVisible.value = false
  form.value = {
    ...defaultForm()
  }
  removeStorageItem(AI_CHAT_DRAFT_KEY)
}
</script>

<template>
  <view class="chat-page">
    <PageHeader title="AI逾期债务规划师" back />
    <view class="chat-space">
      <view class="bubble bot">您好，我会先了解几个基础情况，再帮您转给人工顾问评估。</view>
      <view v-if="step > 0" class="bubble user">{{ form.surname }}</view>
      <view v-if="step > 1" class="bubble user">{{ form.region }}</view>
      <view v-if="step > 2" class="bubble user">{{ form.debtAmount }}元</view>
      <view v-if="step > 3" class="bubble user">{{ debtTypes.find((item) => item.value === form.debtType)?.label }}</view>
      <view v-if="submitted && form.debtDescription" class="bubble user">{{ form.debtDescription }}</view>
      <view class="bubble bot">{{ question }}</view>
      <view v-if="submitted" class="bubble bot success-bubble">信息已收到，人工顾问会结合您的情况做初步评估。</view>
    </view>

    <view v-if="!submitted" class="input-panel">
      <input v-if="step === 0" v-model="form.surname" class="field" placeholder="例如：张先生" />
      <input v-if="step === 1" v-model="form.region" class="field" placeholder="例如：重庆" />
      <input v-if="step === 2" v-model="form.debtAmount" class="field" type="digit" placeholder="例如：50000" />
      <view v-if="step === 3" class="chips">
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
      <textarea v-if="step === 4" v-model="form.debtDescription" class="textarea" placeholder="可简单说明逾期平台和当前情况" />
      <view class="step-actions">
        <button v-if="step > 0" class="secondary-button" @click="prev">上一步</button>
        <button v-if="step < 4" class="send-button" :class="{ compact: step > 0 }" @click="next">下一步</button>
        <button v-else class="send-button" :class="{ compact: step > 0, disabled: submitting }" @click="submit">
          {{ submitting ? '提交中...' : '提交' }}
        </button>
      </view>
    </view>
    <view v-else class="input-panel success-panel">
      <button class="send-button" @click="qrVisible = true">查看顾问二维码</button>
      <button class="ghost-button" @click="resetForm">重新填写</button>
    </view>
    <WechatQrModal :visible="qrVisible" :asset="homeData.assets.wechatQr" source-page="AI_CHAT" @close="qrVisible = false" />
  </view>
</template>

<style scoped>
.chat-page {
  min-height: 100vh;
  padding-bottom: 218px;
  box-sizing: border-box;
  background: #fff8ff;
}

.chat-space {
  padding: 42px 20px 24px;
}

.bubble {
  max-width: 78%;
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: 16px;
  font-size: 16px;
  line-height: 1.5;
}

.bot {
  color: #333333;
  background: #ffffff;
}

.user {
  margin-left: auto;
  color: #ffffff;
  background: #4d8dff;
}

.success-bubble {
  border: 1px solid rgba(247, 90, 80, 0.18);
  color: #b83b32;
  background: #fff0ee;
}

.input-panel {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 14px 16px calc(22px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #fff8ff;
  border-top: 1px solid #e8e0e8;
}

.success-panel {
  display: grid;
  gap: 10px;
}

.field,
.textarea {
  width: 100%;
  min-height: 46px;
  padding: 12px;
  box-sizing: border-box;
  border-radius: 10px;
  background: #ffffff;
}

.textarea {
  height: 92px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chip {
  min-width: 72px;
  height: 38px;
  padding: 0 14px;
  border-radius: 20px;
  background: #f0f0f0;
  color: #333333;
}

.chip.active {
  color: #ffffff;
  background: #f75a50;
}

.step-actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.send-button,
.secondary-button {
  width: 100%;
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 800;
}

.send-button {
  color: #ffffff;
  background: #f75a50;
}

.send-button.compact,
.secondary-button {
  flex: 1;
}

.secondary-button {
  color: #f75a50;
  background: #ffffff;
}

.send-button.disabled {
  opacity: 0.72;
}

.ghost-button {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  color: #f75a50;
  background: #ffffff;
  font-size: 15px;
  font-weight: 700;
}
</style>
