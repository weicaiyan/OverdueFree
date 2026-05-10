<script setup lang="ts">
defineProps<{
  title: string
  back?: boolean
}>()

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
    return
  }
  uni.reLaunch({ url: '/pages/home/index' })
}
</script>

<template>
  <view class="page-header">
    <button v-if="back" class="back-button" @click="goBack">‹</button>
    <view class="header-title">{{ title }}</view>
    <view class="header-spacer" />
  </view>
</template>

<style scoped>
.page-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  padding: 28px 18px 0;
  box-sizing: border-box;
}

.header-title {
  font-size: 20px;
  font-weight: 700;
  color: #111111;
}

.back-button {
  position: absolute;
  left: 18px;
  bottom: 6px;
  width: 36px;
  height: 36px;
  font-size: 42px;
  line-height: 28px;
  color: #111111;
}

.header-spacer {
  width: 36px;
}
</style>
