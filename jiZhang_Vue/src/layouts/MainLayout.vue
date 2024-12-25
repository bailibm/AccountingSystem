<template>
  <div class="main-layout">
    <SideMenu 
      v-model:collapsed="isCollapsed" 
      class="side-menu"
    />
    
    <div class="main-content" :class="{ expanded: isCollapsed }">
      <div class="header">
        <div class="header-left">
          <el-button 
            v-if="isMobile"
            link
            class="mobile-menu-btn"
            @click="toggleMobileMenu"
          >
            <i class="fas fa-bars"></i>
          </el-button>
          
          <el-button 
            v-else
            link
            class="collapse-btn"
            @click="toggleCollapse"
          >
            <i :class="isCollapsed ? 'fas fa-indent' : 'fas fa-outdent'"></i>
          </el-button>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <i class="fas fa-user-circle"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item>
                  <i class="fas fa-cog"></i> 设置
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <i class="fas fa-sign-out-alt"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { UserFilled } from '@element-plus/icons-vue'
import SideMenu from '@/components/SideMenu.vue'

const route = useRoute()
const isCollapsed = ref(false)
const isMobile = ref(false)

const currentPageTitle = computed(() => {
  return route.meta.title || '记账管理系统'
})

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
  if (isMobile.value) {
    isCollapsed.value = true
  }
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const toggleMobileMenu = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleQuickAdd = () => {
  console.log('Quick add clicked')
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.side-menu {
  position: fixed;
  height: 100vh;
  z-index: 1000;
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1) 0s;
}

.main-content {
  flex: 1;
  margin-left: 240px;
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1) 0s;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-content.expanded {
  margin-left: 64px;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  height: 64px;
  padding: 0 32px;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.collapse-btn,
.mobile-menu-btn {
  font-size: 18px;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.collapse-btn:hover,
.mobile-menu-btn:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.04);
}

.username {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.85);
}

.content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  max-width: 1800px;
  margin: 0 auto;
  width: 100%;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式布局 */
@media screen and (max-width: 1600px) {
  .content {
    padding: 24px;
  }
}

@media screen and (max-width: 768px) {
  .main-content {
    margin-left: 0 !important;
  }

  .header {
    padding: 0 16px;
  }

  .content {
    padding: 16px;
  }

  .username {
    display: none;
  }
}
</style> 