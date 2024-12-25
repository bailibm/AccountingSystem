<template>
  <div class="category-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="fas fa-tags"></i>
          分类管理
        </h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <i class="fas fa-plus"></i> 新增分类
        </el-button>
      </div>
    </div>

    <!-- 分类类型切换 -->
    <div class="type-switch">
      <el-radio-group v-model="currentType" size="large">
        <el-radio-button value="INCOME">
          <i class="fas fa-arrow-up"></i> 收入分类
        </el-radio-button>
        <el-radio-button value="EXPENSE">
          <i class="fas fa-arrow-down"></i> 支出分类
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 分类列表 -->
    <el-card class="category-table">
      <el-table 
        :data="filteredCategories" 
        v-loading="loading"
        border
        stripe
      >
        <el-table-column prop="name" label="分类名称" width="180">
          <template #default="{ row }">
            <div class="category-name">
              <el-tag 
                :type="row.type === 'INCOME' ? 'success' : 'danger'"
                :effect="row.isSystem ? 'dark' : 'plain'"
              >
                {{ row.name }}
              </el-tag>
              <el-tag 
                v-if="row.isSystem" 
                size="small" 
                type="info" 
                effect="plain"
              >系统</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '暂无描述' }}
          </template>
        </el-table-column>

        <el-table-column prop="isEnabled" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled ? 'success' : 'info'" effect="plain">
              {{ row.isEnabled ? '启用' : '��用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-tooltip
                content="编辑分类"
                placement="top"
                :hide-after="1000"
              >
                <el-button 
                  type="primary" 
                  size="small"
                  @click="handleEdit(row)"
                  :disabled="row.isSystem"
                >
                  <i class="fas fa-edit"></i>
                </el-button>
              </el-tooltip>

              <el-tooltip
                :content="row.isEnabled ? '禁用分类' : '启用分类'"
                placement="top"
                :hide-after="1000"
              >
                <el-button 
                  :type="row.isEnabled ? 'warning' : 'success'" 
                  size="small"
                  @click="handleToggleStatus(row)"
                  :disabled="row.isSystem"
                >
                  <i :class="row.isEnabled ? 'fas fa-ban' : 'fas fa-check'"></i>
                </el-button>
              </el-tooltip>

              <el-tooltip
                content="删除分类"
                placement="top"
                :hide-after="1000"
              >
                <el-popconfirm
                  :title="getDeleteConfirmMessage(row)"
                  @confirm="handleDelete(row)"
                  :disabled="row.isSystem"
                >
                  <template #reference>
                    <el-button 
                      type="danger" 
                      size="small"
                      :disabled="row.isSystem"
                    >
                      <i class="fas fa-trash"></i>
                    </el-button>
                  </template>
                </el-popconfirm>
              </el-tooltip>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      :title="form.id ? '编辑分类' : '新增分类'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input 
            v-model="form.name"
            maxlength="50"
            show-word-limit
            placeholder="请输入分类名称"
          />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button value="INCOME">收入</el-radio-button>
            <el-radio-button value="EXPENSE">支出</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请输入描述信息"
          />
        </el-form-item>

        <el-form-item label="状态" prop="isEnabled">
          <el-switch
            v-model="form.isEnabled"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ form.id ? '更新' : '保存' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { categoryApi } from '@/api/category'

// 状态定义
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const currentType = ref('INCOME')
const categories = ref([])
const formRef = ref(null)

// 表单数据
const form = ref({
  name: '',
  type: 'INCOME',
  description: '',
  isEnabled: true,
  isSystem: false
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择类型', trigger: 'change' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
}

// 根据当前类型过滤分类列表
const filteredCategories = computed(() => {
  return categories.value.filter(c => c.type === currentType.value)
})

// 加载分类数据
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await categoryApi.getCategories({
      type: currentType.value,
      enabled: null  // 获取所有状态的分类
    })
    if (response.code === 200) {
      categories.value = response.data || []
      console.log('Loaded categories:', categories.value)  // 添加日志
    } else {
      throw new Error(response.message || '加载分类失败')
    }
  } catch (error) {
    console.error('Load categories error:', error)
    ElMessage.error(error.message || '加载分类失败')
    categories.value = []
  } finally {
    loading.value = false
  }
}

// 监听类型变化
watch(() => currentType.value, () => {
  loadCategories()
})

// 初始化
onMounted(() => {
  loadCategories()
})

// 处理新增
const handleAdd = () => {
  form.value = {
    name: '',
    type: currentType.value,
    description: '',
    isEnabled: true,
    isSystem: false
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  if (row.isSystem) {
    ElMessage.warning('系统预设分类不允许修改')
    return
  }
  form.value = { ...row }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  if (row.isSystem) {
    ElMessage.warning('系统预设分类不允许删除')
    return
  }

  try {
    const response = await categoryApi.deleteCategory(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await loadCategories()
    } else {
      throw new Error(response.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete error:', error)
    ElMessage.error(error.message || '删除失败')
  }
}

// 处理状态切换
const handleToggleStatus = async (row) => {
  if (row.isSystem) {
    ElMessage.warning('系统预设分类不允许修改状态')
    return
  }

  try {
    const response = await categoryApi.updateCategoryStatus(row.id)
    if (response.code === 200) {
      ElMessage.success(row.isEnabled ? '已禁用' : '已启用')
      await loadCategories()
    } else {
      throw new Error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('Status toggle error:', error)
    ElMessage.error(error.message || '操作失败')
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    let response
    if (form.value.id) {
      response = await categoryApi.updateCategory(form.value.id, form.value)
    } else {
      response = await categoryApi.createCategory(form.value)
    }
    
    if (response.code === 200) {
      ElMessage.success(form.value.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      await loadCategories()
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('Submit error:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取删除确认消息
const getDeleteConfirmMessage = (row) => {
  return row.isSystem 
    ? '系统预设分类不允许删除'
    : '确定要删除该分类吗？'
}
</script>

<style scoped>
.category-list {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.type-switch {
  margin-bottom: 24px;
}

.category-table {
  background: white;
  border-radius: 4px;
}

.category-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .category-list {
    padding: 16px;
  }
  
  .el-table {
    margin: -16px;
    width: calc(100% + 32px);
  }
}

.el-button-group {
  display: flex;
  gap: 4px;
}

.el-button-group .el-button {
  margin-left: 0 !important;
}

.system-tag {
  margin-left: 4px;
  transform: scale(0.8);
}
</style> 