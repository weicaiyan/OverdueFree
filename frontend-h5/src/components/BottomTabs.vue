<script setup lang="ts">
const props = defineProps<{
  active: 'cases' | 'home' | 'articles' | 'profile'
}>()

const tabs = [
  { key: 'cases', label: '成功案例', icon: '✓', url: '/pages/cases/index' },
  { key: 'home', label: '主页', icon: '⌂', url: '/pages/home/index' },
  { key: 'articles', label: '资讯', icon: '▤', url: '/pages/articles/index' },
  { key: 'profile', label: '个人中心', icon: '⚙', url: '/pages/profile/index' }
] as const

function go(url: string) {
  uni.reLaunch({ url })
}
</script>

<template>
  <view class="tabbar">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      class="tab-item"
      :class="{ active: props.active === tab.key }"
      @click="go(tab.url)"
    >
      <view class="tab-icon">{{ tab.icon }}</view>
      <view class="tab-label">{{ tab.label }}</view>
    </button>
  </view>
</template>

<style scoped>
.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  height: 78px;
  padding: 8px 8px 14px;
  box-sizing: border-box;
  background: rgba(255, 247, 250, 0.96);
  border-top: 1px solid rgba(0, 0, 0, 0.04);
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9a9a9a;
}

.tab-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 26px;
  margin-bottom: 4px;
  border-radius: 8px;
  font-size: 19px;
  font-weight: 700;
}

.tab-label {
  font-size: 12px;
}

.tab-item.active {
  color: #151515;
}

.tab-item.active .tab-icon {
  color: #ffffff;
  background: #f75a50;
}
</style>
