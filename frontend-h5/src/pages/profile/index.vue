<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BottomTabs from '../../components/BottomTabs.vue'
import PageHeader from '../../components/PageHeader.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { logout, requireLogin } from '../../services/auth'
import type { HomeData } from '../../types'

type InfoType = 'about' | 'terms' | 'privacy' | 'contact'

const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)
const infoVisible = ref(false)
const infoTitle = ref('')
const infoContent = ref('')

const infoMap: Record<InfoType, { title: string; content: string }> = {
  about: {
    title: '关于我们',
    content:
      '逾期上岸是本地演示版债务逾期咨询 H5，用于展示手机号登录、债务情况初评、顾问二维码承接和后台线索管理流程。正式上线前，运营主体、服务范围、联系方式等信息需要由业务方确认后配置。'
  },
  terms: {
    title: '使用条款',
    content:
      '当前为演示版本。页面展示的测算、案例和初评结果仅作为沟通参考，不构成承诺或法律意见。正式服务以双方确认的服务协议、委托材料和人工顾问评估结果为准。'
  },
  privacy: {
    title: '隐私协议',
    content:
      '本演示系统会记录手机号、提交的债务初评信息以及查看顾问二维码等行为，用于本地演示线索管理流程。正式上线前需补充完整隐私政策，并明确数据收集、使用、保存和删除规则。'
  },
  contact: {
    title: '联系我们',
    content: '演示版暂未配置正式联系方式。可点击底部按钮查看顾问二维码占位信息。'
  }
}

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  try {
    homeData.value = await api.home()
  } catch (error) {
    // 个人中心的核心动作是退出登录，首页素材失败不阻塞页面。
  }
})

function openInfo(type: InfoType) {
  infoTitle.value = infoMap[type].title
  infoContent.value = infoMap[type].content
  infoVisible.value = true
}

function confirmLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确认退出当前手机号登录状态？',
    confirmText: '退出',
    confirmColor: '#f75a50',
    success: (result) => {
      if (result.confirm) {
        logout()
      }
    }
  })
}
</script>

<template>
  <view class="profile-page">
    <PageHeader title="设置" />
    <view class="menu">
      <button class="menu-item" @click="openInfo('about')">
        <view class="menu-icon">i</view>
        <view class="menu-text">关于我们</view>
      </button>
      <button class="menu-item" @click="openInfo('terms')">
        <view class="menu-icon">□</view>
        <view class="menu-text">使用条款</view>
      </button>
      <button class="menu-item" @click="openInfo('privacy')">
        <view class="menu-icon">▤</view>
        <view class="menu-text">隐私协议</view>
      </button>
      <button class="menu-item danger" @click="confirmLogout">
        <view class="menu-icon">→</view>
        <view class="menu-text">退出登录</view>
      </button>
      <button class="menu-item" @click="openInfo('contact')">
        <view class="menu-icon">✉</view>
        <view class="menu-text">联系我们</view>
        <view class="menu-extra">演示版待配置</view>
      </button>
    </view>
    <view class="record">演示版暂未配置备案信息</view>
    <button class="fixed-cta" @click="qrVisible = true">领取债务减免延期方案</button>
    <view v-if="infoVisible" class="info-mask" @click="infoVisible = false">
      <view class="info-panel" @click.stop>
        <view class="info-title">{{ infoTitle }}</view>
        <view class="info-content">{{ infoContent }}</view>
        <button class="info-button" @click="infoVisible = false">知道了</button>
      </view>
    </view>
    <WechatQrModal :visible="qrVisible" :asset="homeData.assets.wechatQr" source-page="PROFILE" @close="qrVisible = false" />
    <BottomTabs active="profile" />
  </view>
</template>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding-bottom: calc(156px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #eeeeee;
}

.menu {
  margin-top: 22px;
  background: #ffffff;
}

.menu-item {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 82px;
  margin: 0;
  padding: 0 26px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  border-radius: 0;
  line-height: 1;
  text-align: left;
  background: #ffffff;
}

.menu-item::after {
  border: 0;
}

.menu-icon {
  width: 34px;
  color: #777777;
  font-size: 22px;
  font-weight: 700;
}

.menu-text {
  margin-left: 18px;
  font-size: 17px;
  color: #333333;
}

.menu-extra {
  flex: 1;
  min-width: 0;
  text-align: right;
  color: #a5a5a5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.danger .menu-icon,
.danger .menu-text {
  color: #f75a50;
}

.record {
  margin-top: 64px;
  text-align: center;
  color: #888888;
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

.info-mask {
  position: fixed;
  inset: 0;
  z-index: 60;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: calc(24px + env(safe-area-inset-top)) 24px calc(24px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: rgba(0, 0, 0, 0.55);
}

.info-panel {
  width: 100%;
  max-width: 336px;
  max-height: 82vh;
  overflow-y: auto;
  padding: 24px 22px 20px;
  box-sizing: border-box;
  border-radius: 18px;
  background: #ffffff;
}

.info-title {
  text-align: center;
  color: #222222;
  font-size: 22px;
  font-weight: 800;
}

.info-content {
  margin-top: 16px;
  color: #555555;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.info-button {
  width: 148px;
  height: 44px;
  margin: 22px auto 0;
  border-radius: 22px;
  color: #ffffff;
  background: #f75a50;
  font-size: 16px;
  font-weight: 800;
}
</style>
