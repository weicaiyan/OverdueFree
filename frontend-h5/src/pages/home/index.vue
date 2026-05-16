<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AssetImage from '../../components/AssetImage.vue'
import BottomTabs from '../../components/BottomTabs.vue'
import PageState from '../../components/PageState.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api, resolveFileUrl } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { HomeData } from '../../types'

const homeData = ref<HomeData>({
  assets: {},
  serviceSteps: []
})
const qrVisible = ref(false)
const videoVisible = ref(false)
const videoError = ref(false)
const loading = ref(false)
const loaded = ref(false)
const errorText = ref('')

const fallbackSteps = [
  { title: '提交信息', description: '信息加密处理，请放心提交' },
  { title: '法律咨询', description: '人工顾问结合情况初步沟通' },
  { title: '接受委托', description: '确认方案后再进入后续流程' },
  { title: '急速处理', description: '结合材料推进沟通处理' }
]

const serviceSteps = computed(() => {
  return homeData.value.serviceSteps.length > 0 ? homeData.value.serviceSteps : fallbackSteps
})

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  loadHome()
})

async function loadHome() {
  loading.value = true
  errorText.value = ''
  try {
    homeData.value = await api.home()
    loaded.value = true
  } catch (error) {
    errorText.value = '首页加载失败，请检查后端服务或稍后重试'
  } finally {
    loading.value = false
  }
}

function openVideo() {
  const video = homeData.value.assets.homeVideo
  if (!video?.fileUrl) {
    uni.showToast({ title: '视频待配置', icon: 'none' })
    return
  }
  videoError.value = false
  videoVisible.value = true
}

function trackHomeEntry(entry: string) {
  api.event({
    eventType: 'CLICK_HOME_ENTRY',
    sourcePage: 'HOME',
    metadata: { entry }
  }).catch(() => undefined)
}

function go(url: string, entry: string) {
  trackHomeEntry(entry)
  uni.navigateTo({ url })
}
</script>

<template>
  <view class="home-page">
    <view class="page-title">逾期上岸</view>

    <PageState
      v-if="loading && !loaded"
      title="正在加载首页"
      subtitle="请稍候"
      compact
    />
    <PageState
      v-else-if="errorText"
      title="首页加载失败"
      :subtitle="errorText"
      action-text="重新加载"
      @action="loadHome"
    />
    <template v-else>
      <button class="video-card" @click="openVideo">
        <AssetImage
          :asset="homeData.assets.homeVideoCover || homeData.assets.homeVideo"
          title="失信人员宣传片"
          subtitle="立即观看"
          tone="red"
        />
        <view class="play-button">▶</view>
      </button>

      <view class="entry-grid">
        <button class="small-entry" @click="go('/pages/ai-chat/index', 'AI_CHAT')">
          <AssetImage
            :asset="homeData.assets.aiConsultBanner"
            title="AI债务咨询师"
            subtitle="一键 Chat"
            tone="blue"
          />
        </button>
        <button class="small-entry" @click="go('/pages/calculator/index', 'CALCULATOR')">
          <AssetImage
            :asset="homeData.assets.loanCalculatorBanner"
            title="来算算您的网贷"
            subtitle="真实利率估算"
            tone="blue"
          />
        </button>
      </view>

      <button class="wide-entry" @click="go('/pages/plan-form/index', 'PLAN_FORM')">
        <AssetImage
          :asset="homeData.assets.debtPlanBanner"
          title="规划优化债务"
          subtitle="动动手指，马上试试"
          tone="teal"
        />
      </button>

      <view class="section-title">服务流程</view>
      <view class="section-subtitle">Service Steps</view>
      <view class="steps">
        <view v-for="(step, index) in serviceSteps" :key="step.title" class="step">
          <view class="step-number">{{ index + 1 }}</view>
          <view class="step-title">{{ step.title }}</view>
          <view class="step-desc">{{ step.description }}</view>
        </view>
      </view>

      <button class="fixed-cta" @click="qrVisible = true">领取债务减免延期方案</button>
    </template>
    <view v-if="videoVisible" class="video-mask" @click="videoVisible = false">
      <view class="video-panel" @click.stop>
        <video
          v-if="!videoError"
          class="video-player"
          controls
          autoplay
          :src="resolveFileUrl(homeData.assets.homeVideo?.fileUrl)"
          @error="videoError = true"
        />
        <view v-else class="video-error">视频暂时无法播放，请稍后再试</view>
        <button class="close-video" @click="videoVisible = false">关闭</button>
      </view>
    </view>
    <WechatQrModal
      :visible="qrVisible"
      :asset="homeData.assets.wechatQr"
      source-page="HOME"
      @close="qrVisible = false"
    />
    <BottomTabs active="home" />
  </view>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: calc(72px + env(safe-area-inset-top)) 16px calc(156px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #fff4ea;
}

.page-title {
  margin: 0 0 26px;
  font-size: 28px;
  font-weight: 800;
  color: #111111;
}

.video-card,
.wide-entry,
.small-entry {
  overflow: hidden;
  width: 100%;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 8px 18px rgba(70, 48, 35, 0.12);
}

.video-card {
  position: relative;
  height: 232px;
}

.play-button {
  position: absolute;
  left: 50%;
  top: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 78px;
  height: 78px;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  color: rgba(159, 40, 30, 0.88);
  background: rgba(255, 255, 255, 0.72);
  font-size: 34px;
}

.entry-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-top: 20px;
}

.small-entry {
  height: 112px;
}

.wide-entry {
  height: 138px;
  margin-top: 20px;
}

.section-title {
  margin-top: 22px;
  text-align: center;
  font-size: 28px;
  font-weight: 900;
  color: #222222;
}

.section-subtitle {
  margin-top: 2px;
  text-align: center;
  font-size: 13px;
  letter-spacing: 0;
  color: #777777;
}

.steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-top: 22px;
}

.step {
  text-align: center;
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  border-radius: 50%;
  color: #ffffff;
  background: #4c7ff0;
  font-size: 22px;
  font-weight: 800;
}

.step:nth-child(2) .step-number {
  background: #f7c946;
}

.step:nth-child(3) .step-number {
  background: #5bc6b9;
}

.step:nth-child(4) .step-number {
  background: #f78a31;
}

.step-title {
  font-size: 13px;
  font-weight: 800;
  color: #333333;
  line-height: 1.25;
  word-break: keep-all;
}

.step-desc {
  margin-top: 8px;
  font-size: 11px;
  line-height: 1.4;
  color: #858585;
  word-break: break-word;
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
  box-shadow: 0 8px 18px rgba(239, 90, 79, 0.24);
}

.video-mask {
  position: fixed;
  inset: 0;
  z-index: 60;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
  background: rgba(0, 0, 0, 0.68);
  box-sizing: border-box;
}

.video-panel {
  width: 100%;
}

.video-player {
  width: 100%;
  height: 220px;
  border-radius: 12px;
  overflow: hidden;
  background: #000000;
}

.video-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 220px;
  border-radius: 12px;
  color: #ffffff;
  background: #202932;
  font-size: 15px;
}

.close-video {
  width: 120px;
  height: 44px;
  margin: 18px auto 0;
  border-radius: 22px;
  color: #ffffff;
  background: #f75a50;
}
</style>
