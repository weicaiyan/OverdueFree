<script setup lang="ts">
import { watch } from 'vue'
import type { AssetResource } from '../types'
import { api, resolveFileUrl } from '../services/api'

const props = defineProps<{
  visible: boolean
  asset?: AssetResource | null
  sourcePage: string
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()

watch(
  () => props.visible,
  (visible) => {
    if (!visible) {
      return
    }
    api.event({
      eventType: 'VIEW_WECHAT_QR',
      sourcePage: props.sourcePage,
      refType: 'ASSET',
      refId: props.asset?.id || null,
      metadata: { sourcePage: props.sourcePage }
    }).catch(() => undefined)
  }
)
</script>

<template>
  <view v-if="visible" class="modal-mask" @click="emit('close')">
    <view class="modal-panel" @click.stop>
      <view class="modal-title">扫码添加顾问</view>
      <view class="modal-subtitle">获取免费初评回访</view>
      <image v-if="asset?.fileUrl" class="qr-image" mode="aspectFit" :src="resolveFileUrl(asset.fileUrl)" />
      <view v-else class="qr-placeholder">顾问二维码待配置</view>
      <button class="modal-button" @click="emit('close')">知道了</button>
    </view>
  </view>
</template>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(0, 0, 0, 0.55);
  box-sizing: border-box;
}

.modal-panel {
  width: 100%;
  max-width: 320px;
  padding: 28px 24px 22px;
  box-sizing: border-box;
  border-radius: 20px;
  background: #fff7fb;
  text-align: center;
}

.modal-title {
  font-size: 24px;
  font-weight: 800;
  color: #222222;
}

.modal-subtitle {
  margin-top: 10px;
  font-size: 15px;
  color: #575757;
}

.qr-image,
.qr-placeholder {
  width: 190px;
  height: 190px;
  margin: 22px auto 18px;
  border-radius: 12px;
  background: #ffffff;
}

.qr-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
  box-sizing: border-box;
  color: #999999;
  border: 1px dashed #d8d8d8;
}

.modal-button {
  width: 168px;
  height: 46px;
  margin: 0 auto;
  border-radius: 24px;
  color: #ffffff;
  background: #45b854;
  font-size: 16px;
  font-weight: 700;
}
</style>
