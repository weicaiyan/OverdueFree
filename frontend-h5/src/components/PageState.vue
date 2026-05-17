<script setup lang="ts">
import { computed } from 'vue'

const emit = defineEmits<{
  (event: 'action'): void
}>()

const props = defineProps<{
  title: string
  subtitle?: string
  actionText?: string
  compact?: boolean
  variant?: 'default' | 'loading'
}>()

const loading = computed(() => props.variant === 'loading')
</script>

<template>
  <view class="page-state" :class="{ compact }">
    <view class="state-mark" :class="{ loading }">{{ loading ? '' : '!' }}</view>
    <view class="state-title">{{ title }}</view>
    <view v-if="subtitle" class="state-subtitle">{{ subtitle }}</view>
    <button v-if="actionText" class="state-button" @click="emit('action')">{{ actionText }}</button>
  </view>
</template>

<style scoped>
.page-state {
  padding: 44px 18px;
  text-align: center;
}

.page-state.compact {
  padding: 28px 18px;
}

.state-mark {
  width: 48px;
  height: 48px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 14px;
  border-radius: 50%;
  color: #f75a50;
  background: #fff0ee;
  font-size: 24px;
  font-weight: 900;
}

.state-mark.loading {
  border: 4px solid #ffe0dc;
  border-top-color: #f75a50;
  color: transparent;
  background: #ffffff;
  animation: state-spin 0.9s linear infinite;
}

@keyframes state-spin {
  to {
    transform: rotate(360deg);
  }
}

.state-title {
  color: #252525;
  font-size: 17px;
  font-weight: 800;
}

.state-subtitle {
  max-width: 260px;
  margin: 8px auto 0;
  color: #8b8b8b;
  font-size: 14px;
  line-height: 1.5;
}

.state-button {
  width: 132px;
  height: 42px;
  margin: 18px auto 0;
  border-radius: 22px;
  color: #ffffff;
  background: #f75a50;
  font-size: 15px;
  font-weight: 700;
}
</style>
