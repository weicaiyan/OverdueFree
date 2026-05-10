<script setup lang="ts">
</script>

<template>
  <view class="app-shell">
    <slot />
  </view>
</template>

<style>
page {
  min-height: 100%;
  background: #fff5ee;
  color: #202124;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}

.app-shell {
  min-height: 100vh;
}

button {
  margin: 0;
  padding: 0;
  border: 0;
  line-height: 1;
  background: transparent;
}

button::after {
  border: 0;
}

input,
textarea {
  box-sizing: border-box;
}
</style>
