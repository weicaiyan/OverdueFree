<template>
  <el-container class="admin-shell">
    <el-aside width="220px" class="admin-aside">
      <div class="admin-logo">逾期上岸后台</div>
      <el-menu default-active="leads">
        <el-menu-item index="leads">线索管理</el-menu-item>
        <el-menu-item index="assets">素材管理</el-menu-item>
        <el-menu-item index="articles">资讯管理</el-menu-item>
        <el-menu-item index="cases">成功案例</el-menu-item>
        <el-menu-item index="admins">管理员管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <el-card>
        <template #header>工程骨架</template>
        <p>后台 Web 骨架已启动，后续功能分支实现登录、线索、导出和内容管理。</p>
      </el-card>
    </el-main>
  </el-container>
</template>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background: #f5f7fa;
}

.admin-aside {
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
}

.admin-logo {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-weight: 700;
}
</style>
