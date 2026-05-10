<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BottomTabs from '../../components/BottomTabs.vue'
import PageHeader from '../../components/PageHeader.vue'
import { api, resolveFileUrl } from '../../services/api'
import { requireLogin } from '../../services/auth'
import type { ArticleItem } from '../../types'

const articles = ref<ArticleItem[]>([])

onShow(async () => {
  if (!(await requireLogin())) {
    return
  }
  try {
    articles.value = (await api.articles(1, 20)).list
  } catch (error) {
    uni.showToast({ title: '资讯加载失败', icon: 'none' })
  }
})

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/articles/detail?id=${id}` })
}
</script>

<template>
  <view class="article-page">
    <PageHeader title="教程&资讯" />
    <view class="list">
      <button v-for="item in articles" :key="item.id" class="article-card" @click="goDetail(item.id)">
        <image v-if="item.coverUrl" class="cover" mode="aspectFill" :src="resolveFileUrl(item.coverUrl)" />
        <view v-else class="cover placeholder">资讯</view>
        <view class="article-main">
          <view class="title">{{ item.title }}</view>
          <view class="time">更新时间:{{ item.publishTime || '待发布' }}</view>
        </view>
        <view class="arrow">›</view>
      </button>
    </view>
    <BottomTabs active="articles" />
  </view>
</template>

<style scoped>
.article-page {
  min-height: 100vh;
  padding: 0 16px 94px;
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
  font-size: 18px;
  line-height: 1.4;
  color: #333333;
}

.time {
  margin-top: 10px;
  font-size: 14px;
  color: #999999;
}

.arrow {
  margin-left: 8px;
  color: #999999;
  font-size: 34px;
}
</style>
