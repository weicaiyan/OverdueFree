<script setup lang="ts">
import { ref } from 'vue'
import { api } from '../../services/api'
import { isPhone, saveLoginToken } from '../../services/auth'

const phone = ref('')
const code = ref('')
const mockCode = ref('')
const loading = ref(false)
const codeLoading = ref(false)

async function sendCode() {
  if (codeLoading.value) {
    return
  }
  if (!isPhone(phone.value)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  codeLoading.value = true
  try {
    const result = await api.sendCode(phone.value)
    mockCode.value = result.mockCode
    code.value = result.mockCode
    uni.showToast({ title: '验证码已生成', icon: 'none' })
  } catch (error) {
    uni.showToast({ title: '验证码获取失败', icon: 'none' })
  } finally {
    codeLoading.value = false
  }
}

async function login() {
  if (!isPhone(phone.value)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  if (!/^\d{6}$/.test(code.value)) {
    uni.showToast({ title: '请输入6位验证码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const result = await api.login(phone.value, code.value)
    saveLoginToken(result.token)
    uni.reLaunch({ url: '/pages/home/index' })
  } catch (error) {
    uni.showToast({ title: '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <view class="login-page">
    <view class="brand">逾期上岸</view>
    <view class="login-card">
      <view class="card-title">手机号登录</view>
      <view class="field-label">联系手机</view>
      <input v-model="phone" class="field" type="number" maxlength="11" placeholder="请输入手机号" />
      <view class="field-label">验证码</view>
      <view class="code-row">
        <input v-model="code" class="field code-field" type="number" maxlength="6" placeholder="请输入验证码" />
        <button class="code-button" :disabled="codeLoading" @click="sendCode">
          {{ codeLoading ? '生成中' : '获取验证码' }}
        </button>
      </view>
      <view v-if="mockCode" class="mock-code">本次验证码：{{ mockCode }}</view>
      <button class="login-button" :disabled="loading" @click="login">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </view>
  </view>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 96px 24px 40px;
  box-sizing: border-box;
  background: #fff4ea;
}

.brand {
  margin-bottom: 36px;
  text-align: center;
  font-size: 34px;
  font-weight: 900;
  color: #191919;
}

.login-card {
  padding: 28px 22px 24px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 12px 30px rgba(75, 44, 28, 0.1);
}

.card-title {
  margin-bottom: 26px;
  font-size: 22px;
  font-weight: 800;
}

.field-label {
  margin: 18px 0 8px;
  font-size: 14px;
  color: #555555;
}

.field {
  width: 100%;
  height: 48px;
  padding: 0 14px;
  border-radius: 10px;
  background: #f6f6f6;
  font-size: 16px;
}

.code-row {
  display: flex;
  gap: 10px;
}

.code-field {
  flex: 1;
}

.code-button {
  width: 116px;
  height: 48px;
  border-radius: 10px;
  color: #ffffff;
  background: #f75a50;
  font-size: 14px;
  font-weight: 700;
}

.mock-code {
  margin-top: 14px;
  color: #f75a50;
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 52px;
  margin-top: 28px;
  border-radius: 28px;
  color: #ffffff;
  background: #f75a50;
  font-size: 17px;
  font-weight: 800;
}
</style>
