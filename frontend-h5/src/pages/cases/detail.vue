<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { HomeData, SuccessCaseItem } from '../../types'

const id = ref(0)
const detail = ref<SuccessCaseItem | null>(null)
const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)

onLoad((query) => {
  id.value = Number(query?.id || 0)
})

onShow(async () => {
  if (!(await requireLogin()) || !id.value) {
    return
  }
  const [caseDetail, home] = await Promise.all([api.caseDetail(id.value), api.home()])
  detail.value = caseDetail
  homeData.value = home
})
</script>

<template>
  <view class="detail-page">
    <PageHeader title="案例详情" back />
    <view v-if="detail" class="detail-card">
      <view class="title">{{ detail.displayName }}</view>
      <view class="meta">{{ detail.maskedPhone || '号码已脱敏' }}</view>
      <view class="item">逾期平台：{{ detail.overduePlatforms || '待补充' }}</view>
      <view class="item">逾期金额：{{ detail.overdueAmount || 0 }}元</view>
      <view class="item accent">处理方案：{{ detail.handlingPlan || '人工评估后确认' }}</view>
      <view class="content">{{ detail.detailText || '当前案例详情待补充。' }}</view>
    </view>
    <button class="fixed-cta" @click="qrVisible = true">领取债务减免延期方案</button>
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
