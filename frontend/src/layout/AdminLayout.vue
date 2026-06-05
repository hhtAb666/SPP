<!-- 管理员后台布局组件 -->
<template>
  <div class="admin-layout">
    <el-container>
      <el-header>
        <div class="header-inner">
          <div class="logo">体育场馆管理系统</div>
          <div class="menu-wrapper">
            <el-menu
              :default-active="activeMenu"
              mode="horizontal"
              router
              background-color="#fff"
              text-color="#303133"
              active-text-color="#409EFF"
            >
              <el-menu-item index="/admin/users">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/venues">
                <el-icon><Location /></el-icon>
                <span>场馆管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/reservations">
                <el-icon><Tickets /></el-icon>
                <span>预约审核</span>
              </el-menu-item>
              <el-menu-item index="/admin/logs">
                <el-icon><Document /></el-icon>
                <span>系统日志</span>
              </el-menu-item>
            </el-menu>
          </div>
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main>
        <div class="main-content">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}
.el-header {
  padding: 0;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: fixed;
  width: 100%;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-right: 40px;
}
.menu-wrapper {
  flex: 1;
}
.el-menu {
  border-bottom: none !important;
}
.user-info {
  margin-left: 20px;
}
.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #606266;
}
.el-main {
  padding-top: 80px;
}
.main-content {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
