<script setup lang="ts">
import { computed, ref } from 'vue'
import { onReachBottom, onShow } from '@dcloudio/uni-app'
import BottomTabs from '../../components/BottomTabs.vue'
import PageHeader from '../../components/PageHeader.vue'
import PageState from '../../components/PageState.vue'
import WechatQrModal from '../../components/WechatQrModal.vue'
import { api, resolveFileUrl } from '../../services/api'
import { requireLogin } from '../../services/auth'
import { safeNavigateTo } from '../../services/navigation'
import type { ArticleItem, HomeData } from '../../types'
import { formatDate } from '../../utils/format'

const articles = ref<ArticleItem[]>([])
const homeData = ref<HomeData>({ assets: {}, serviceSteps: [] })
const qrVisible = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const errorText = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const failedCoverUrls = ref<string[]>([])

const hasMore = computed(() => articles.value.length < total.value)

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  loadArticles(true)
})

onReachBottom(() => {
  loadArticles(false)
})

async function loadArticles(reset = true) {
  if (reset && loading.value) {
    return
  }
  if (!reset && (loading.value || loadingMore.value || !hasMore.value)) {
    return
  }
  const nextPage = reset ? 1 : page.value + 1
  if (reset) {
    loading.value = true
    errorText.value = ''
  } else {
    loadingMore.value = true
  }
  try {
    const articlePage = await api.articles(nextPage, pageSize)
    if (reset) {
      failedCoverUrls.value = []
    }
    articles.value = reset ? articlePage.list : articles.value.concat(articlePage.list)
    total.value = articlePage.total
    page.value = articlePage.page
    if (reset) {
      api.home()
        .then((home) => {
          homeData.value = home
        })
        .catch(() => undefined)
    }
  } catch (error) {
    if (reset) {
      errorText.value = '资讯加载失败，请检查后端服务或稍后重试'
    } else {
      uni.showToast({ title: '加载更多失败', icon: 'none' })
    }
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

function goDetail(id: number) {
  safeNavigateTo(`/pages/articles/detail?id=${id}`)
}

function openArticleCta() {
  api.event({
    eventType: 'ARTICLE_CTA',
    sourcePage: 'ARTICLES',
    metadata: { source: 'fixedCta' }
  }).catch(() => undefined)
  qrVisible.value = true
}

function coverFailed(url?: string) {
  return !!url && failedCoverUrls.value.includes(url)
}

function markCoverFailed(url?: string) {
  if (url && !failedCoverUrls.value.includes(url)) {
    failedCoverUrls.value = failedCoverUrls.value.concat(url)
  }
}
</script>

<template>
  <view class="article-page">
    <PageHeader title="教程&资讯" />
    <PageState
      v-if="loading && !articles.length"
      title="正在加载资讯"
      subtitle="请稍候"
      variant="loading"
      compact
    />
    <PageState
      v-else-if="errorText"
      title="资讯加载失败"
      :subtitle="errorText"
      action-text="重新加载"
      @action="loadArticles"
    />
    <PageState
      v-else-if="!articles.length"
      title="暂无教程资讯"
      subtitle="后台发布资讯后会展示在这里"
      compact
    />
    <view v-else class="list">
      <button v-for="item in articles" :key="item.id" class="article-card" @click="goDetail(item.id)">
        <image
          v-if="item.coverUrl && !coverFailed(item.coverUrl)"
          class="cover"
          mode="aspectFill"
          :src="resolveFileUrl(item.coverUrl)"
          @error="markCoverFailed(item.coverUrl)"
        />
        <view v-else class="cover placeholder">资讯</view>
        <view class="article-main">
          <view class="title">{{ item.title }}</view>
          <view class="time">更新时间：{{ formatDate(item.publishTime) }}</view>
        </view>
        <view class="arrow">›</view>
      </button>
      <button v-if="hasMore" class="load-more" :class="{ disabled: loadingMore }" :disabled="loadingMore" @click="loadArticles(false)">
        {{ loadingMore ? '加载中...' : '加载更多资讯' }}
      </button>
      <view v-else class="list-end">已展示全部资讯</view>
    </view>
    <button class="fixed-cta" @click="openArticleCta">领取债务减免延期方案</button>
    <WechatQrModal
      :visible="qrVisible"
      :asset="homeData.assets.wechatQr"
      source-page="ARTICLES"
      @close="qrVisible = false"
    />
    <BottomTabs active="articles" />
  </view>
</template>

<style scoped>
.article-page {
  min-height: 100vh;
  padding: 0 16px calc(156px + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #fff4ea;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 28px;
}

.article-card {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 118px;
  padding: 16px;
  border-radius: 14px;
  background: #ffffff;
  text-align: left;
}

.cover {
  width: 116px;
  height: 82px;
  border-radius: 10px;
  margin-right: 14px;
}

.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: #3d7bdc;
}

.article-main {
  flex: 1;
  min-width: 0;
}

.title {
  display: -webkit-box;
  overflow: hidden;
  font-size: 18px;
  line-height: 1.4;
  color: #333333;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.time {
  margin-top: 10px;
  font-size: 14px;
  color: #999999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  margin-left: 8px;
  color: #999999;
  font-size: 34px;
}

.load-more {
  height: 44px;
  border-radius: 22px;
  color: #f75a50;
  background: #ffffff;
  font-size: 15px;
  font-weight: 700;
}

.load-more.disabled {
  opacity: 0.7;
}

.list-end {
  text-align: center;
  color: #9a9a9a;
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
</style>
