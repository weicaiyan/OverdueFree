<script setup lang="ts">
import { ref, watch } from 'vue'
import type { AssetResource } from '../types'
import { resolveFileUrl } from '../services/api'

const props = defineProps<{
  asset?: AssetResource | null
  title: string
  subtitle?: string
  tone?: 'red' | 'blue' | 'teal'
}>()

const imageFailed = ref(false)

watch(
  () => props.asset?.fileUrl,
  () => {
    imageFailed.value = false
  }
)
</script>

<template>
  <view class="asset-box" :class="tone || 'blue'">
    <image
      v-if="asset?.fileUrl && !imageFailed"
      class="asset-image"
      mode="aspectFill"
      :src="resolveFileUrl(asset.fileUrl)"
      @error="imageFailed = true"
    />
    <view v-else class="asset-placeholder">
      <view class="placeholder-title">{{ title }}</view>
      <view v-if="subtitle" class="placeholder-subtitle">{{ subtitle }}</view>
    </view>
  </view>
</template>

<style scoped>
.asset-box {
  position: relative;
  overflow: hidden;
  min-height: 100%;
  background: #2c8fc8;
}

.asset-box.red {
  background: linear-gradient(135deg, #9e1d19, #d64b38);
}

.asset-box.blue {
  background: linear-gradient(135deg, #2366d7, #73c3f2);
}

.asset-box.teal {
  background: linear-gradient(135deg, #378db6, #5ec8c2);
}

.asset-image {
  width: 100%;
  height: 100%;
}

.asset-placeholder {
  min-height: 100%;
  padding: 18px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
}

.placeholder-title {
  font-size: 24px;
  font-weight: 800;
}

.placeholder-subtitle {
  margin-top: 8px;
  font-size: 14px;
  opacity: 0.92;
}
</style>
