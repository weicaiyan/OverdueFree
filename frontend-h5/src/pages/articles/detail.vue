<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import PageHeader from '../../components/PageHeader.vue'
import PageState from '../../components/PageState.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api, resolveFileUrl } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { ArticleItem, HomeData } from '../../types'

const id = ref(0)
const detail = ref<ArticleItem | null>(null)
const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)
const loading = ref(false)
const errorText = ref('')

onLoad((query) => {
  id.value = Number(query?.id || 0)
})

onShow(async () => {
  if (!(await requireLogin()) || !id.value) {
    return
  }
  loadDetail()
})

function backToList() {
  uni.reLaunch({ url: '/pages/articles/index' })
}

async function loadDetail() {
  loading.value = true
  errorText.value = ''
  try {
    const [article, home] = await Promise.all([api.articleDetail(id.value), api.home()])
    detail.value = article
    homeData.value = home
  } catch (error) {
    errorText.value = '资讯详情加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function openArticleCta() {
  api.event({
    eventType: 'ARTICLE_CTA',
    sourcePage: 'ARTICLE_DETAIL',
    refType: 'ARTICLE',
    refId: id.value,
    metadata: { articleId: id.value }
  }).catch(() => undefined)
  qrVisible.value = true
}
</script>

<template>
  <view class="detail-page">
    <PageHeader title="资讯详情" back />
    <PageState
      v-if="!id"
      title="资讯不存在"
      subtitle="缺少资讯编号，请返回列表重新选择"
      action-text="返回资讯列表"
      @action="backToList"
    />
    <PageState
      v-else-if="loading && !detail"
      title="正在加载详情"
      subtitle="请稍候"
      compact
    />
    <PageState
      v-else-if="errorText"
      title="详情加载失败"
      :subtitle="errorText"
      action-text="重新加载"
      @action="loadDetail"
    />
    <view v-else-if="detail" class="article">
      <view class="title">{{ detail.title }}</view>
      <view class="time">{{ detail.publishTime || '' }}</view>
      <image v-if="detail.coverUrl" class="cover" mode="aspectFill" :src="resolveFileUrl(detail.coverUrl)" />
      <view class="summary">{{ detail.summary }}</view>
      <view class="content">{{ detail.contentText || detail.summary || '内容待补充。' }}</view>
    </view>
    <button v-if="detail" class="fixed-cta" @click="openArticleCta">领取债务减免延期方案</button>
    <WechatQrModal :visible="qrVisible" :asset="homeData.assets.wechatQr" source-page="ARTICLE_DETAIL" @close="qrVisible = false" />
  </view>
</template>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 0 16px calc(116px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #fff4ea;
}

.article {
  margin-top: 18px;
  padding: 22px;
  border-radius: 14px;
  background: #ffffff;
}

.title {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.4;
  word-break: break-word;
}

.time {
  margin-top: 10px;
  font-size: 13px;
  color: #999999;
}

.cover {
  width: 100%;
  height: 180px;
  margin-top: 18px;
  border-radius: 12px;
}

.summary {
  margin-top: 18px;
  color: #777777;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.content {
  margin-top: 18px;
  color: #333333;
  font-size: 16px;
  line-height: 1.9;
  white-space: pre-wrap;
  word-break: break-word;
}

.fixed-cta {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: calc(26px + env(safe-area-inset-bottom));
  height: 58px;
  border-radius: 30px;
  color: #ffffff;
  background: #ef5a4f;
  font-size: 18px;
  font-weight: 800;
}
</style>
