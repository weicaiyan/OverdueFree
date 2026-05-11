<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BottomTabs from '../../components/BottomTabs.vue'
import PageHeader from '../../components/PageHeader.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api } from '../../services/api'
import { logout, requireLogin } from '../../services/auth'
import type { HomeData } from '../../types'

const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)

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
</script>

<template>
  <view class="profile-page">
    <PageHeader title="设置" />
    <view class="menu">
      <view class="menu-item">
        <view class="menu-icon">i</view>
        <view class="menu-text">关于我们</view>
      </view>
      <view class="menu-item">
        <view class="menu-icon">□</view>
        <view class="menu-text">使用条款</view>
      </view>
      <view class="menu-item">
        <view class="menu-icon">▤</view>
        <view class="menu-text">隐私协议</view>
      </view>
      <button class="menu-item danger" @click="logout">
        <view class="menu-icon">→</view>
        <view class="menu-text">退出登录</view>
      </button>
      <view class="menu-item">
        <view class="menu-icon">✉</view>
        <view class="menu-text">联系我们</view>
        <view class="menu-extra">演示版待配置</view>
      </view>
    </view>
    <view class="record">演示版暂未配置备案信息</view>
    <button class="fixed-cta" @click="qrVisible = true">领取债务减免延期方案</button>
    <WechatQrModal :visible="qrVisible" :asset="homeData.assets.wechatQr" source-page="PROFILE" @close="qrVisible = false" />
    <BottomTabs active="profile" />
  </view>
</template>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding-bottom: 156px;
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
  text-align: right;
  color: #a5a5a5;
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
  bottom: 88px;
  z-index: 25;
  height: 58px;
  border-radius: 30px;
  color: #ffffff;
  background: #ef5a4f;
  font-size: 18px;
  font-weight: 800;
}
</style>
