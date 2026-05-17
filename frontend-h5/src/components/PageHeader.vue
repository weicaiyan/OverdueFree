<script setup lang="ts">
import { safeNavigateBack } from '../services/navigation'

const props = withDefaults(defineProps<{
  title: string
  back?: boolean
  surface?: 'transparent' | 'light'
}>(), {
  surface: 'transparent'
})

function goBack() {
  safeNavigateBack()
}
</script>

<template>
  <view class="page-header" :class="`surface-${props.surface}`">
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
  height: calc(64px + env(safe-area-inset-top));
  padding: calc(28px + env(safe-area-inset-top)) 18px 0;
  box-sizing: border-box;
}

.surface-light {
  background: #f6f6f6;
  border-bottom: 1px solid #e8e8e8;
}

.header-title {
  max-width: calc(100vw - 112px);
  overflow: hidden;
  font-size: 20px;
  font-weight: 700;
  color: #111111;
  text-overflow: ellipsis;
  white-space: nowrap;
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
