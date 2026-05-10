<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AssetImage from '../../components/AssetImage.vue'
import BottomTabs from '../../components/BottomTabs.vue'
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

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  loadHome()
})

async function loadHome() {
  try {
    homeData.value = await api.home()
  } catch (error) {
    uni.showToast({ title: '首页加载失败', icon: 'none' })
  }
}

function openVideo() {
  const video = homeData.value.assets.homeVideo
  if (!video?.fileUrl) {
    uni.showToast({ title: '视频待配置', icon: 'none' })
    return
  }
  videoVisible.value = true
}

function go(url: string) {
  uni.navigateTo({ url })
}
</script>

<template>
  <view class="home-page">
    <view class="page-title">逾期上岸</view>

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
      <button class="small-entry" @click="go('/pages/ai-chat/index')">
        <AssetImage
          :asset="homeData.assets.aiConsultBanner"
          title="AI债务咨询师"
          subtitle="一键 Chat"
          tone="blue"
        />
      </button>
      <button class="small-entry" @click="go('/pages/calculator/index')">
        <AssetImage
          :asset="homeData.assets.loanCalculatorBanner"
          title="来算算您的网贷"
          subtitle="真实利率估算"
          tone="blue"
        />
      </button>
    </view>

    <button class="wide-entry" @click="go('/pages/plan-form/index')">
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
      <view v-for="(step, index) in homeData.serviceSteps" :key="step.title" class="step">
        <view class="step-number">{{ index + 1 }}</view>
        <view class="step-title">{{ step.title }}</view>
        <view class="step-desc">{{ step.description }}</view>
      </view>
    </view>

    <button class="fixed-cta" @click="qrVisible = true">领取债务减免延期方案</button>
    <view v-if="videoVisible" class="video-mask" @click="videoVisible = false">
      <view class="video-panel" @click.stop>
        <video
          class="video-player"
          controls
          autoplay
          :src="resolveFileUrl(homeData.assets.homeVideo?.fileUrl)"
        />
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
  padding: 72px 16px 156px;
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
  letter-spacing: 2px;
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
}

.step-desc {
  margin-top: 8px;
  font-size: 11px;
  line-height: 1.4;
  color: #858585;
}

.fixed-cta {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: 88px;
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

.close-video {
  width: 120px;
  height: 44px;
  margin: 18px auto 0;
  border-radius: 22px;
  color: #ffffff;
  background: #f75a50;
}
</style>
