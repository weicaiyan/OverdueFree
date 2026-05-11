<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import PageState from '../../components/PageState.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { HomeData, SuccessCaseItem } from '../../types'

const id = ref(0)
const detail = ref<SuccessCaseItem | null>(null)
const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)
const loading = ref(false)
const errorText = ref('')

onLoad((query) => {
  id.value = Number(query?.id || 0)
})

onShow(async () => {
  if (!(await requireLogin()) || !id.value) {
    return
  }
  loadDetail()
})

async function loadDetail() {
  loading.value = true
  errorText.value = ''
  try {
    const [caseDetail, home] = await Promise.all([api.caseDetail(id.value), api.home()])
    detail.value = caseDetail
    homeData.value = home
  } catch (error) {
    errorText.value = '案例详情加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function openApply() {
  api.event({
    eventType: 'CASE_APPLY',
    sourcePage: 'CASE_DETAIL',
    refType: 'SUCCESS_CASE',
    refId: id.value,
    metadata: { caseId: id.value }
  }).catch(() => undefined)
  qrVisible.value = true
}
</script>

<template>
  <view class="detail-page">
    <PageHeader title="案例详情" back />
    <PageState
      v-if="loading && !detail"
      title="正在加载详情"
      subtitle="请稍候"
      compact
    />
    <PageState
      v-else-if="errorText"
      title="详情加载失败"
      :subtitle="errorText"
      action-text="重新加载"
      @action="loadDetail"
    />
    <view v-else-if="detail" class="detail-card">
      <view class="title">{{ detail.displayName }}</view>
      <view class="meta">{{ detail.maskedPhone || '号码已脱敏' }}</view>
      <view class="item">逾期平台：{{ detail.overduePlatforms || '待补充' }}</view>
      <view class="item">逾期金额：{{ detail.overdueAmount || 0 }}元</view>
      <view class="item accent">处理方案：{{ detail.handlingPlan || '人工评估后确认' }}</view>
      <view class="content">{{ detail.detailText || '当前案例详情待补充。' }}</view>
    </view>
    <button v-if="detail" class="fixed-cta" @click="openApply">领取债务减免延期方案</button>
    <WechatQrModal :visible="qrVisible" :asset="homeData.assets.wechatQr" source-page="CASE_DETAIL" @close="qrVisible = false" />
  </view>
</template>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 0 16px 96px;
  box-sizing: border-box;
  background: #fff4ea;
}

.detail-card {
  margin-top: 18px;
  padding: 22px;
  border-radius: 14px;
  background: #ffffff;
}

.title {
  font-size: 24px;
  font-weight: 800;
}

.meta {
  margin-top: 8px;
  color: #999999;
}

.item {
  margin-top: 16px;
  font-size: 16px;
  color: #555555;
  line-height: 1.5;
}

.accent {
  color: #ff9200;
}

.content {
  margin-top: 22px;
  font-size: 16px;
  color: #333333;
  line-height: 1.8;
}

.fixed-cta {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: 26px;
  height: 58px;
  border-radius: 30px;
  color: #ffffff;
  background: #ef5a4f;
  font-size: 18px;
  font-weight: 800;
}
</style>
