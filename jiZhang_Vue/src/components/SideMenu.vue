<template>
  <div class="side-menu" :class="{ collapsed: isCollapsed }">
    <div class="logo-container">
      <i class="fas fa-wallet logo-icon"></i>
      <h1 class="title" v-show="!isCollapsed">记账管理系统</h1>
    </div>
    
    <el-menu
      :default-active="activeMenu"
      class="menu-container"
      :collapse="isCollapsed"
      :router="true"
      @select="handleSelect"
      unique-opened
      :collapse-transition="false"
    >
      <el-menu-item index="/record">
        <i class="fas fa-file-invoice-dollar"></i>
        <template #title>记账管理</template>
      </el-menu-item>

      <el-menu-item index="/categories">
        <i class="fas fa-tags"></i>
        <template #title>分类管理</template>
      </el-menu-item>

      <el-sub-menu index="/analysis">
        <template #title>
          <i class="fas fa-chart-line"></i>
          <span>统计分析</span>
        </template>
        <el-menu-item index="/analysis/overview">
          <i class="fas fa-chart-pie"></i>
          <span>总览分析</span>
        </el-menu-item>
        <el-menu-item index="/analysis/trend">
          <i class="fas fa-chart-area"></i>
          <span>趋势分析</span>
        </el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/settings">
        <i class="fas fa-cog"></i>
        <template #title>系统设置</template>
      </el-menu-item>
    </el-menu>

    <div class="menu-footer">
      <el-tooltip
        :content="isCollapsed ? '展开菜单' : '收起菜单'"
        placement="right"
      >
        <el-button 
          link
          class="collapse-btn"
          @click="toggleCollapse"
        >
          <i :class="isCollapsed ? 'fas fa-indent' : 'fas fa-outdent'"></i>
        </el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:collapsed'])

const route = useRoute()
const router = useRouter()

const isCollapsed = computed({
  get: () => props.collapsed,
  set: (value) => emit('update:collapsed', value)
})

const activeMenu = computed(() => route.path)

const handleSelect = (index) => {
  router.push(index)
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.side-menu {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #001529;
  transition: all 0.3s;
  position: relative;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  width: 240px;
}

.logo-container {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  height: 64px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 24px;
  color: #1890ff;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  color: white;
  font-size: 18px;
  margin: 0;
  white-space: nowrap;
  opacity: 1;
  transition: opacity 0.3s;
}

.menu-container {
  flex: 1;
  border: none;
  background: transparent;
}

/* 自定义 element-plus 菜单样式 */
:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  color: rgba(255, 255, 255, 0.65);
  margin: 4px 8px;
  border-radius: 4px;
}

:deep(.el-menu-item i),
:deep(.el-sub-menu__title i) {
  color: rgba(255, 255, 255, 0.65);
  font-size: 16px;
  margin-right: 10px;
  transition: transform 0.3s;
}

:deep(.el-menu-item.is-active) {
  background: #1890ff !important;
  color: white;
}

:deep(.el-menu-item.is-active i) {
  color: white;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: white;
}

:deep(.el-menu-item:hover i),
:deep(.el-sub-menu__title:hover i) {
  color: white;
  transform: scale(1.1);
}

/* 子菜单样式 */
:deep(.el-menu--inline) {
  background: rgba(0, 0, 0, 0.15) !important;
}

:deep(.el-menu--inline .el-menu-item) {
  padding-left: 48px !important;
  height: 40px !important;
  line-height: 40px !important;
}

.menu-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: center;
}

.collapse-btn {
  color: rgba(255, 255, 255, 0.65);
  font-size: 16px;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

/* 折叠状态样式 */
.side-menu.collapsed {
  width: 64px;
}

.side-menu.collapsed .title {
  opacity: 0;
  width: 0;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .side-menu {
    position: fixed;
    z-index: 1000;
    transform: translateX(0);
  }

  .side-menu.collapsed {
    transform: translateX(-100%);
  }

  .menu-footer {
    display: none;
  }
}
</style> 