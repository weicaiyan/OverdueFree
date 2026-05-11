<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BottomTabs from '../../components/BottomTabs.vue'
import PageHeader from '../../components/PageHeader.vue'
import PageState from '../../components/PageState.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { HomeData, SuccessCaseItem } from '../../types'

const cases = ref<SuccessCaseItem[]>([])
const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const errorText = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)

const hasMore = computed(() => cases.value.length < total.value)

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  loadData(true)
})

async function loadData(reset = true) {
  if (!reset && (loading.value || loadingMore.value || !hasMore.value)) {
    return
  }
  const nextPage = reset ? 1 : page.value + 1
  if (reset) {
    loading.value = true
    errorText.value = ''
  } else {
    loadingMore.value = true
  }
  try {
    if (reset) {
      const [casePage, home] = await Promise.all([api.cases(nextPage, pageSize), api.home()])
      cases.value = casePage.list
      homeData.value = home
      total.value = casePage.total
      page.value = casePage.page
    } else {
      const casePage = await api.cases(nextPage, pageSize)
      cases.value = cases.value.concat(casePage.list)
      total.value = casePage.total
      page.value = casePage.page
    }
  } catch (error) {
    if (reset) {
      errorText.value = '案例加载失败，请检查后端服务或稍后重试'
    } else {
      uni.showToast({ title: '加载更多失败', icon: 'none' })
    }
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/cases/detail?id=${id}` })
}

function openApply(id?: number) {
  api.event({
    eventType: 'CASE_APPLY',
    sourcePage: 'CASES',
    refType: id ? 'SUCCESS_CASE' : undefined,
    refId: id || null,
    metadata: id ? { caseId: id } : { source: 'fixedCta' }
  }).catch(() => undefined)
  qrVisible.value = true
}
</script>

<template>
  <view class="case-page">
    <PageHeader title="成功案例" />
    <PageState
      v-if="loading && !cases.length"
      title="正在加载案例"
      subtitle="请稍候"
      compact
    />
    <PageState
      v-else-if="errorText"
      title="案例加载失败"
      :subtitle="errorText"
      action-text="重新加载"
      @action="loadData"
    />
    <PageState
      v-else-if="!cases.length"
      title="暂无成功案例"
      subtitle="后台发布案例后会展示在这里"
      compact
    />
    <view v-else class="list">
      <view v-for="item in cases" :key="item.id" class="case-card">
        <view class="card-head">
          <view class="avatar">人</view>
          <view class="person">
            <view class="name">{{ item.displayName }}</view>
            <view class="phone">{{ item.maskedPhone || '号码已脱敏' }}</view>
          </view>
          <button class="apply-button" @click="openApply(item.id)">我也申请</button>
        </view>
        <view class="line" />
        <view class="info-row">逾期平台：{{ item.overduePlatforms || '待补充' }}</view>
        <view class="info-row">逾期金额：<text class="amount">{{ item.overdueAmount || 0 }}</text>元</view>
        <view class="info-row">处理方案：<text class="plan">{{ item.handlingPlan || '人工评估后确认' }}</text></view>
        <button class="detail-link" @click="goDetail(item.id)">查看详情</button>
      </view>
      <button v-if="hasMore" class="load-more" :class="{ disabled: loadingMore }" @click="loadData(false)">
        {{ loadingMore ? '加载中...' : '加载更多案例' }}
      </button>
      <view v-else class="list-end">已展示全部案例</view>
    </view>
    <button class="fixed-cta" @click="openApply()">领取债务减免延期方案</button>
    <WechatQrModal
      :visible="qrVisible"
      :asset="homeData.assets.wechatQr"
      source-page="CASES"
      @close="qrVisible = false"
    />
    <BottomTabs active="cases" />
  </view>
</template>

<style scoped>
.case-page {
  min-height: 100vh;
  padding: 0 14px calc(156px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #f4f4f4;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 18px;
}

.case-card {
  padding: 20px;
  border-radius: 14px;
  background: #ffffff;
}

.card-head {
  display: flex;
  align-items: center;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  color: #988aa9;
  background: #eadbff;
  font-size: 18px;
}

.person {
  flex: 1;
  min-width: 0;
  margin-left: 12px;
}

.name {
  font-size: 18px;
  color: #999999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.phone {
  margin-top: 6px;
  font-size: 16px;
  color: #a7a7a7;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.apply-button {
  width: 94px;
  height: 46px;
  border-radius: 24px;
  color: #ffffff;
  background: #f75a50;
  font-size: 15px;
}

.line {
  height: 1px;
  margin: 18px 0;
  background: #eeeeee;
}

.info-row {
  margin-top: 8px;
  font-size: 16px;
  color: #999999;
  line-height: 1.5;
  word-break: break-word;
}

.amount {
  color: #111111;
}

.plan {
  color: #ff9200;
}

.detail-link {
  margin-top: 12px;
  color: #f75a50;
  font-size: 14px;
  text-align: left;
}

.load-more {
  height: 44px;
  border-radius: 22px;
  color: #f75a50;
  background: #ffffff;
  font-size: 15px;
  font-weight: 700;
}

.load-more.disabled {
  opacity: 0.7;
}

.list-end {
  text-align: center;
  color: #9a9a9a;
  font-size: 13px;
}

.fixed-cta {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: calc(88px + env(safe-area-inset-bottom));
  z-index: 25;
  height: 58px;
  border-radius: 30px;
  color: #ffffff;
  background: #ef5a4f;
  font-size: 18px;
  font-weight: 800;
}
</style>
