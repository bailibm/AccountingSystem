<template>
  <div class="record-list">
    <!-- 统计卡片 -->
    <div class="statistics-cards">
      <div class="stat-card" @click="handleCardClick('income')">
        <div class="stat-content income">
          <div class="stat-icon">
            <i class="fas fa-coins"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">本月收入</div>
            <div class="stat-value">{{ formatAmount(statistics.monthlyIncome) }}</div>
            <div class="stat-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: `${progressStats.income.progress}%` }"></div>
              </div>
              <div class="progress-text">
                <i class="fas fa-arrow-up"></i>
                较上月增长 {{ progressStats.income.change }}%
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="stat-card" @click="handleCardClick('expense')">
        <div class="stat-content expense">
          <div class="stat-icon">
            <i class="fas fa-shopping-bag"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">本月支出</div>
            <div class="stat-value">{{ formatAmount(statistics.monthlyExpense) }}</div>
            <div class="stat-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: `${progressStats.expense.progress}%` }"></div>
              </div>
              <div class="progress-text">
                <i class="fas fa-arrow-down"></i>
                较上月降低 {{ progressStats.expense.change }}%
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="stat-card" @click="handleCardClick('balance')">
        <div class="stat-content balance">
          <div class="stat-icon">
            <i class="fas fa-piggy-bank"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">本月结余</div>
            <div class="stat-value">
              {{ formatAmount(Math.abs(statistics.monthlyIncome - statistics.monthlyExpense)) }}
            </div>
            <div class="stat-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: `${progressStats.balance.progress}%` }"></div>
              </div>
              <div class="progress-text">
                <i class="fas fa-percentage"></i>
                结余率 {{ progressStats.balance.value }}%
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 记录列表 -->
    <el-card class="record-table">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>记账记录</h3>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="handleAdd">
              <i class="fas fa-plus"></i> 新增记录
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="类型">
            <el-select v-model="searchForm.type" placeholder="全部" clearable>
              <el-option label="收入" value="INCOME" />
              <el-option label="支出" value="EXPENSE" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-select 
              v-model="searchForm.categoryId" 
              placeholder="全部" 
              clearable
              :disabled="!searchForm.type"
            >
              <el-option
                v-for="category in filteredCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DDTHH:mm:ss.SSSZ"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <i class="fas fa-search"></i> 搜索
            </el-button>
            <el-button @click="handleReset">
              <i class="fas fa-redo"></i> 重置
            </el-button>
            <el-button 
              type="success" 
              @click="exportData" 
              :loading="loading"
            >
              <i class="fas fa-file-excel"></i> 
              {{ loading ? '导出中...' : '导出Excel' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="records" 
        v-loading="loading" 
        border 
        stripe
        :default-sort="{ prop: 'date', order: 'descending' }"
        height="calc(100vh - 450px)"
      >
        <el-table-column prop="date" label="日期" width="180" sortable>
          <template #default="{ row }">
            <el-tooltip 
              :content="formatDate(row.date, true)" 
              placement="top"
              effect="dark"
            >
              <span class="date-text">
                <i class="far fa-calendar-alt"></i>
                {{ formatDate(row.date) }}
              </span>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="100" sortable>
          <template #default="{ row }">
            <el-tag 
              :type="row.type === 'INCOME' ? 'success' : 'danger'"
              :effect="row.categoryId ? 'light' : 'plain'"
              size="small"
            >
              <i :class="row.type === 'INCOME' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
              {{ row.type === 'INCOME' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="amount" label="金额" width="150" sortable>
          <template #default="{ row }">
            <span :class="['amount', row.type.toLowerCase()]">
              <i class="fas fa-yen-sign"></i>
              {{ formatAmount(row.amount) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="{ row }">
            <el-tooltip 
              :content="row.description"
              placement="top"
              :disabled="!row.description"
            >
              <el-tag 
                :type="row.type === 'INCOME' ? 'success' : 'danger'"
                effect="plain"
                size="small"
              >
                <i class="fas fa-tag"></i>
                {{ row.categoryName || '未分类' }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="description" :title="row.description">
              <i class="fas fa-comment-alt"></i>
              {{ row.description || '暂无描述' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建间" width="180" sortable>
          <template #default="{ row }">
            <el-tooltip 
              :content="formatDate(row.createdAt, true)" 
              placement="top"
              effect="dark"
            >
              <span class="created-time">
                <i class="far fa-clock"></i>
                {{ formatDate(row.createdAt) }}
              </span>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="button-group">
              <el-tooltip content="编辑" placement="top" :hide-after="1000">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="handleEdit(row)"
                >
                  <i class="fas fa-edit"></i>
                </el-button>
              </el-tooltip>
              
              <el-popconfirm
                :title="`确定要删除「${formatDate(row.date)} ${row.type === 'INCOME' ? '收入' : '支出'} ${formatAmount(row.amount)}」这条记录吗？`"
                @confirm="handleDelete(row)"
                confirm-button-text="确定"
                cancel-button-text="取消"
                icon="Warning"
                icon-color="red"
              >
                <template #reference>
                  <div class="delete-button-wrapper">
                    <el-tooltip content="删除" placement="top" :hide-after="1000">
                      <el-button type="danger" size="small">
                        <i class="fas fa-trash"></i>
                      </el-button>
                    </el-tooltip>
                  </div>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      :title="form.id ? '编辑记录' : '新增记录'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button value="INCOME">收入</el-radio-button>
            <el-radio-button value="EXPENSE">支出</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :precision="2"
            :step="100"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="form.categoryId"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="datetime"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss.SSSZ"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
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

    <!-- 分类统计 -->
    <el-card v-if="statistics.categoryStats?.length" class="statistics-card">
      <template #header>
        <div class="card-header">
          <span>分类统计</span>
        </div>
      </template>
      <el-table :data="statistics.categoryStats" border stripe>
        <el-table-column prop="categoryName" label="分类名称" min-width="120">
          <template #default="{ row }">
            <el-tag 
              :type="row.categoryType === 'INCOME' ? 'success' : 'danger'"
              effect="plain"
            >
              {{ row.categoryName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="记录数" width="100" align="right"/>
        <el-table-column prop="totalAmount" label="金额" min-width="120" align="right">
          <template #default="{ row }">
            <span :class="['amount', row.categoryType.toLowerCase()]">
              {{ formatAmount(row.totalAmount) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as ExcelJS from 'exceljs'
import StatCard from '@/components/StatCard.vue'
import { recordApi } from '@/api/record'
import { categoryApi } from '@/api/category'

// 状态定义
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const records = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const formRef = ref(null)

// 统计数据
const statistics = ref({
  monthlyIncome: 0,
  monthlyExpense: 0,
  categoryStats: []
})

// 搜索表单
const searchForm = ref({
  type: '',
  categoryId: '',
  dateRange: []
})

// 编辑表单
const form = ref({
  type: 'EXPENSE',
  amount: 0,
  categoryId: '',
  date: new Date().toISOString(),
  description: ''
})

// 表单验证规则
const rules = {
  type: [{ required: true, message: '请选择类型' }],
  amount: [{ required: true, message: '请输入金额' }],
  categoryId: [{ required: true, message: '请选择类' }],
  date: [{ required: true, message: '请选择日期' }]
}

// 计算属性
const categoryOptions = computed(() => {
  return categories.value.filter(c => c.type === form.value.type && c.isEnabled)
})

const filteredCategories = computed(() => {
  return categories.value.filter(c => c.type === searchForm.value.type && c.isEnabled)
})

// 监听类型变化，重置分类择
watch(() => form.value.type, () => {
  form.value.categoryId = ''
})

watch(() => searchForm.value.type, () => {
  searchForm.value.categoryId = ''
})

// 添加加载状态提示
const loadingText = computed(() => {
  return loading.value ? '正在加载数据...' : '暂无记录';
})

// 优化错误处理
const handleError = (error, message) => {
  console.error(message, error);
  ElMessage.error({
    message: error.message || message,
    duration: 5000,
    showClose: true
  });
}

// 优化加载数据方法
const loadData = async () => {
    loading.value = true
    try {
        // 加载分类数据
        const categoriesRes = await categoryApi.getCategories({})
        if (categoriesRes.code === 200) {
            categories.value = categoriesRes.data || []
        }

        // 加载记录和统计数据
        const [recordsRes, statsRes] = await Promise.all([
            recordApi.getRecords({
                page: page.value,
                size: pageSize.value,
                type: searchForm.value.type || null,
                categoryId: searchForm.value.categoryId || null,
                startDate: searchForm.value.dateRange?.[0] || null,
                endDate: searchForm.value.dateRange?.[1] || null
            }),
            recordApi.getStatistics({
                year: new Date().getFullYear(),
                month: new Date().getMonth() + 1
            })
        ])

        console.log('Records response:', recordsRes)
        console.log('Statistics response:', statsRes)

        // 处理记录数据
        if (recordsRes.code === 200 && recordsRes.data) {
            records.value = recordsRes.data.records || []
            total.value = recordsRes.data.total || 0
            console.log('Processed records:', records.value)  // 添加日志
        }

        // 处理统计数据
        if (statsRes.code === 200 && statsRes.data) {
            statistics.value = {
                monthlyIncome: Number(statsRes.data.monthlyIncome) || 0,
                monthlyExpense: Number(statsRes.data.monthlyExpense) || 0,
                categoryStats: statsRes.data.categoryStats || []
            }
            console.log('Processed statistics:', statistics.value)  // 添加日志
        }
    } catch (error) {
        handleError(error, '加载数据失败')
    } finally {
        loading.value = false
    }
}

// 格式化日期
const formatDate = (date, showSeconds = false) => {
  if (!date) return '未知时间';
  try {
    const options = {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    };
    
    if (showSeconds) {
      options.second = '2-digit';
    }
    
    return new Date(date).toLocaleString('zh-CN', options);
  } catch (error) {
    console.error('Date format error:', error);
    return '日期格式错误';
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '¥0.00'
  try {
    return new Intl.NumberFormat('zh-CN', {
      style: 'currency',
      currency: 'CNY',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount).replace('CN¥', '¥')
  } catch (error) {
    console.error('Amount format error:', error)
    return '¥0.00'
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

// 处理新增
const handleAdd = () => {
  form.value = {
    type: 'EXPENSE',
    amount: 0,
    categoryId: '',
    date: new Date().toISOString(),
    description: ''
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    const response = await recordApi.deleteRecord(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      // 如果当前页只有一条数据，且不是第一页，则跳转到上一页
      if (records.value.length === 1 && page.value > 1) {
        page.value--
      }
      await loadData()
    } else {
      throw new Error(response.message || '删除失败')
    }
  } catch (error) {
    handleError(error, '删除失败')
  }
}

// 处理搜索
const handleSearch = () => {
  page.value = 1
  loadData()
}

// 处理重置
const handleReset = () => {
  searchForm.value = {
    type: '',
    categoryId: '',
    dateRange: []
  }
  handleSearch()
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  page.value = val
  loadData()
}

// 优化表单提交方法
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const response = form.value.id
      ? await recordApi.updateRecord(form.value.id, form.value)
      : await recordApi.createRecord(form.value)
    
    if (response.code === 200) {
      ElMessage.success({
        message: form.value.id ? '更新成功' : '创建成功',
        duration: 2000
      })
      dialogVisible.value = false
      await loadData()
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    handleError(error, '保存失败')
  } finally {
    submitting.value = false
  }
}

// 完善导出数据功能
const exportData = async () => {
  try {
    loading.value = true;
    ElMessage.info('正在准备导出数据...');

    // 获取所有数据（不分页）
    const response = await recordApi.getRecords({
      type: searchForm.value.type || null,
      categoryId: searchForm.value.categoryId || null,
      startDate: searchForm.value.dateRange?.[0] || null,
      endDate: searchForm.value.dateRange?.[1] || null,
      page: 1,
      size: 10000 // 获取足够多的数据
    });

    if (response.code !== 200 || !response.data?.records) {
      throw new Error('获取导出数据失败');
    }

    const exportRecords = response.data.records;

    // 准备Excel数据
    const headers = [
      { header: '日期', key: 'date', width: 20 },
      { header: '类型', key: 'type', width: 10 },
      { header: '金额', key: 'amount', width: 15 },
      { header: '分类', key: 'category', width: 15 },
      { header: '描述', key: 'description', width: 30 },
      { header: '创建时间', key: 'createdAt', width: 20 }
    ];

    const rows = exportRecords.map(record => ({
      date: formatDate(record.date),
      type: record.type === 'INCOME' ? '收入' : '支出',
      amount: Number(record.amount),
      category: getCategoryName(record.categoryId),
      description: record.description || '',
      createdAt: formatDate(record.createdAt)
    }));

    // 创建工作簿
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('记账记录');

    // 设置表头
    worksheet.columns = headers;
    
    // 添加数据行
    worksheet.addRows(rows);

    // 设置样式
    worksheet.getRow(1).font = { bold: true };
    worksheet.getRow(1).fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: 'FFE0E0E0' }
    };

    // 设置金额列格式
    const amountColumn = worksheet.getColumn('amount');
    amountColumn.numFmt = '¥#,##0.00';
    amountColumn.alignment = { horizontal: 'right' };

    // 设置日期列格式
    const dateColumn = worksheet.getColumn('date');
    dateColumn.alignment = { horizontal: 'center' };

    const createdAtColumn = worksheet.getColumn('createdAt');
    createdAtColumn.alignment = { horizontal: 'center' };

    // 添加汇总行
    const totalRow = worksheet.addRow({
      date: '合计',
      amount: rows.reduce((sum, row) => sum + (row.type === '收入' ? row.amount : -row.amount), 0)
    });
    totalRow.font = { bold: true };

    // 自动调整列宽
    worksheet.columns.forEach(column => {
      column.width = Math.max(
        column.width || 10,
        ...rows.map(row => String(row[column.key] || '').length)
      );
    });

    // 生成文件名
    const fileName = `记账记录_${formatDate(new Date())}.xlsx`;

    // 导出文件
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    });
    
    // 创建下载链接
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);

    ElMessage.success('导出成功！');
  } catch (error) {
    handleError(error, '导出失败');
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  loadData()
})

// 获取分类描述
const getCategoryDescription = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category?.description
}

// 监听搜索条件变化
watch([() => searchForm.value.type, () => searchForm.value.categoryId, () => searchForm.value.dateRange], () => {
  page.value = 1  // 重置页码
  loadData()
})

// 监听分页变化
watch([() => page.value, () => pageSize.value], () => {
  loadData()
})

// 添加卡片点击效果处理函数
const handleCardClick = (type) => {
  const card = document.querySelector(`.stat-card.${type}`);
  if (card) {
    card.classList.add('clicked');
    setTimeout(() => {
      card.classList.remove('clicked');
    }, 200);
  }
}

// 添加计算属性
const progressStats = computed(() => {
  const lastMonthIncome = statistics.value.lastMonthIncome || 0
  const lastMonthExpense = statistics.value.lastMonthExpense || 0
  const currentIncome = statistics.value.monthlyIncome || 0
  const currentExpense = statistics.value.monthlyExpense || 0

  // 计算同比变化率
  const incomeChange = lastMonthIncome ? ((currentIncome - lastMonthIncome) / lastMonthIncome * 100).toFixed(1) : 0
  const expenseChange = lastMonthExpense ? ((currentExpense - lastMonthExpense) / lastMonthExpense * 100).toFixed(1) : 0
  
  // 计算进度条百分比（相对于过去12个月的最大值）
  const maxIncome = Math.max(currentIncome, lastMonthIncome)
  const maxExpense = Math.max(currentExpense, lastMonthExpense)
  
  const incomeProgress = maxIncome ? (currentIncome / maxIncome * 100).toFixed(1) : 0
  const expenseProgress = maxExpense ? (currentExpense / maxExpense * 100).toFixed(1) : 0
  const balanceProgress = currentIncome ? ((currentIncome - currentExpense) / currentIncome * 100).toFixed(1) : 0

  return {
    income: {
      progress: incomeProgress,
      change: incomeChange,
      trend: incomeChange >= 0 ? 'up' : 'down'
    },
    expense: {
      progress: expenseProgress,
      change: expenseChange,
      trend: expenseChange >= 0 ? 'up' : 'down'
    },
    balance: {
      progress: Math.max(0, Math.min(100, balanceProgress)),
      value: balanceProgress
    }
  }
})
</script>

<style scoped>
.record-list {
  padding: 24px;
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding: 0.5rem;
}

.stat-card {
  position: relative;
  perspective: 1500px;
  cursor: pointer;
  transform-style: preserve-3d;
}

.date-text,
.created-time,
.description {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.date-text i,
.created-time i,
.description i {
  font-size: 14px;
  opacity: 0.7;
}

.amount {
  font-weight: 600;
  font-family: 'Roboto Mono', monospace;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.amount i {
  font-size: 12px;
  opacity: 0.8;
}

.amount.income {
  color: var(--el-color-success);
}

.amount.expense {
  color: var(--el-color-danger);
}

.description {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.created-time {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.el-table {
  --el-table-border-color: var(--el-border-color-lighter);
  --el-table-header-bg-color: var(--el-fill-color-light);
  --el-table-row-hover-bg-color: var(--el-fill-color-lighter);
}

.el-table :deep(th) {
  font-weight: 600;
  background-color: var(--el-fill-color-light);
}

.el-table :deep(.el-table__empty-block) {
  min-height: 200px;
}

.el-table :deep(.el-table__empty-text) {
  line-height: 1.5;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.el-button-group {
  display: inline-flex;
  gap: 4px;
}

.el-button-group .el-button {
  margin-left: 0 !important;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .record-list {
    padding: 16px;
  }
  
  .statistics-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .search-bar :deep(.el-form-item) {
    margin-bottom: 16px;
  }
  
  .el-table {
    font-size: 13px;
  }
  
  .el-table :deep(th),
  .el-table :deep(td) {
    padding: 8px;
  }
  
  .amount {
    font-size: 13px;
  }
  
  .description,
  .created-time {
    font-size: 12px;
  }
}

.category-stats {
  margin-top: 24px;
}

.amount {
  font-family: 'Roboto Mono', monospace;
  font-weight: 600;
}

.amount.income {
  color: var(--el-color-success);
}

.amount.expense {
  color: var(--el-color-danger);
}

.description {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.date-text,
.created-time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.date-text i,
.created-time i {
  opacity: 0.7;
  font-size: 14px;
}

/* 修改 .stat-content 的样式 */
.stat-content {
  position: relative;
  padding: 2rem;
  border-radius: 20px;
  background: var(--el-bg-color);
  transform-style: preserve-3d;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
}

.income .stat-content {
  background: linear-gradient(145deg, #ffffff, #f0f7f0);
  border-left: 4px solid #4CAF50;
}

.expense .stat-content {
  background: linear-gradient(145deg, #ffffff, #fff1f1);
  border-left: 4px solid #FF5252;
}

.balance .stat-content {
  background: linear-gradient(145deg, #ffffff, #f0f7ff);
  border-left: 4px solid #2196F3;
}

/* 暗色主题适配 */
:root[data-theme='dark'] .stat-content {
  background: var(--el-bg-color-overlay);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

:root[data-theme='dark'] .income .stat-content {
  background: linear-gradient(145deg, #1a1a1a, #132713);
}

:root[data-theme='dark'] .expense .stat-content {
  background: linear-gradient(145deg, #1a1a1a, #271313);
}

:root[data-theme='dark'] .balance .stat-content {
  background: linear-gradient(145deg, #1a1a1a, #132027);
}

/* 修改悬浮效果 */
.stat-card:hover .stat-content {
  transform: translateY(-10px) rotateX(5deg) rotateY(5deg);
  box-shadow: 
    20px 20px 60px rgba(0, 0, 0, 0.05),
    -20px -20px 60px rgba(255, 255, 255, 0.8);
}

.income .stat-content:hover {
  background: linear-gradient(145deg, #ffffff, #e8f5e8);
}

.expense .stat-content:hover {
  background: linear-gradient(145deg, #ffffff, #ffe8e8);
}

.balance .stat-content:hover {
  background: linear-gradient(145deg, #ffffff, #e8f4ff);
}

/* 暗色主题悬浮效果 */
:root[data-theme='dark'] .income .stat-content:hover {
  background: linear-gradient(145deg, #1a1a1a, #1b321b);
}

:root[data-theme='dark'] .expense .stat-content:hover {
  background: linear-gradient(145deg, #1a1a1a, #321b1b);
}

:root[data-theme='dark'] .balance .stat-content:hover {
  background: linear-gradient(145deg, #1a1a1a, #1b2832);
}

/* 增强图标效果 */
.stat-icon {
  position: absolute;
  top: 2rem;
  right: 2rem;
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  transform: translateZ(30px);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: linear-gradient(135deg, rgba(255,255,255,0.1), rgba(255,255,255,0.05));
  backdrop-filter: blur(10px);
}

.stat-card:hover .stat-icon {
  transform: translateZ(50px) scale(1.1) rotate(10deg);
}

.income .stat-icon {
  background: linear-gradient(135deg, #4CAF50, #81C784);
  color: white;
  box-shadow: 0 8px 16px rgba(76, 175, 80, 0.2);
}

.expense .stat-icon {
  background: linear-gradient(135deg, #FF5252, #FF8A80);
  color: white;
  box-shadow: 0 8px 16px rgba(255, 82, 82, 0.2);
}

.balance .stat-icon {
  background: linear-gradient(135deg, #2196F3, #64B5F6);
  color: white;
  box-shadow: 0 8px 16px rgba(33, 150, 243, 0.2);
}

/* 修改进度条动画 */
.progress-bar {
  height: 8px;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

/* 进度条光效 */
.progress-fill::before,
.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.progress-fill::before {
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.4) 50%,
    transparent 100%
  );
  animation: shimmer 2s infinite;
  transform: translateX(-100%);
}

.progress-fill::after {
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.2) 20%,
    rgba(255, 255, 255, 0.4) 50%,
    rgba(255, 255, 255, 0.2) 80%,
    transparent 100%
  );
  animation: shine 3s infinite;
}

/* 数值变化动画 */
.stat-value {
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  transform-origin: left center;
}

.stat-card:hover .stat-value {
  transform: scale(1.1);
  color: var(--el-color-primary);
}

/* 涟漪效果 */
@keyframes ripple {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 0.4;
  }
  100% {
    transform: translate(-50%, -50%) scale(4);
    opacity: 0;
  }
}

/* 进度条动画 */
@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes shine {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 添加点击涟漪效果 */
.stat-card::before {
  content: '';
  position: absolute;
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  opacity: 0;
  pointer-events: none;
  transform: translate(-50%, -50%) scale(0);
}

.stat-card.clicked::before {
  animation: ripple 0.8s ease-out;
}

/* 卡片内容浮动效果 */
.stat-info {
  transform: translateZ(20px);
  transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.stat-card:hover .stat-info {
  transform: translateZ(40px);
}

/* 趋势文字动画 */
.trend-value {
  display: inline-block;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.stat-card:hover .trend-value {
  transform: scale(1.1);
}

/* 暗色主题适配 */
:root[data-theme='dark'] .stat-content {
  box-shadow: 
    20px 20px 60px rgba(0, 0, 0, 0.3),
    -20px -20px 60px rgba(255, 255, 255, 0.05);
}

/* 响应式动画调整 */
@media (prefers-reduced-motion: reduce) {
  .stat-card,
  .stat-content,
  .stat-icon,
  .progress-fill,
  .stat-value,
  .trend-value {
    transition: none;
    animation: none;
  }
}

.stat-header {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 1rem;
  font-size: 1.5rem;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.title-group {
  flex: 1;
}

.stat-title {
  display: block;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.stat-subtitle {
  display: block;
  font-size: 0.85rem;
  color: var(--el-text-color-secondary);
  margin-top: 0.25rem;
}

.stat-body {
  position: relative;
  padding-top: 0.5rem;
}

.stat-amount {
  font-size: 2rem;
  font-weight: 600;
  font-family: 'Roboto Mono', monospace;
  color: var(--el-text-color-primary);
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stat-amount i {
  font-size: 1.5rem;
  opacity: 0.8;
}

.stat-progress {
  padding: 0.5rem 0;
}

.progress-bar {
  height: 8px;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.75rem;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
  font-size: 0.85rem;
  color: var(--el-text-color-secondary);
}

.trend-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: var(--el-text-color-secondary);
}

.trend-value {
  font-weight: 600;
  font-family: 'Roboto Mono', monospace;
}

.positive {
  color: var(--el-color-success);
}

.negative {
  color: var(--el-color-danger);
}

/* 颜色主题 */
.income .icon-wrapper {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.expense .icon-wrapper {
  background: rgba(244, 67, 54, 0.1);
  color: #FF5252;
}

.balance .icon-wrapper {
  background: rgba(33, 150, 243, 0.1);
  color: #2196F3;
}

/* 暗色主题适配 */
:root[data-theme='dark'] .progress-bar {
  background: rgba(255, 255, 255, 0.1);
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .stat-subtitle {
    display: none;
  }

  .icon-wrapper {
    width: 40px;
    height: 40px;
    font-size: 1.25rem;
  }

  .stat-amount {
    font-size: 1.75rem;
  }

  .stat-amount i {
    font-size: 1.25rem;
  }
}

/* 添加点击颜色效果 */
.stat-card.clicked .stat-content.income {
  background: linear-gradient(145deg, #e8f5e8, #ffffff);
  border-left: 4px solid #81C784;
}

.stat-card.clicked .stat-content.expense {
  background: linear-gradient(145deg, #ffe8e8, #ffffff);
  border-left: 4px solid #FF8A80;
}

.stat-card.clicked .stat-content.balance {
  background: linear-gradient(145deg, #e8f4ff, #ffffff);
  border-left: 4px solid #64B5F6;
}

/* 暗色主题点击效果 */
:root[data-theme='dark'] .stat-card.clicked .stat-content.income {
  background: linear-gradient(145deg, #132713, #1a1a1a);
  border-left: 4px solid #2E7D32;
}

:root[data-theme='dark'] .stat-card.clicked .stat-content.expense {
  background: linear-gradient(145deg, #271313, #1a1a1a);
  border-left: 4px solid #C62828;
}

:root[data-theme='dark'] .stat-card.clicked .stat-content.balance {
  background: linear-gradient(145deg, #132027, #1a1a1a);
  border-left: 4px solid #1565C0;
}

/* 点击时图标效果 */
.stat-card.clicked .stat-icon {
  transform: scale(0.9);
  opacity: 0.8;
  transition: all 0.2s ease;
}

/* 点击时进度条效果 */
.stat-card.clicked .progress-fill {
  opacity: 0.8;
  transition: all 0.2s ease;
}

/* 点击涟漪效果 */
.stat-card::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, rgba(255,255,255,0.8) 0%, transparent 60%);
  transform: translate(-50%, -50%) scale(0);
  opacity: 0;
  pointer-events: none;
  transition: all 0.3s ease;
}

.stat-card.clicked::after {
  animation: clickRipple 0.6s ease-out;
}

@keyframes clickRipple {
  0% {
    transform: translate(-50%, -50%) scale(0.3);
    opacity: 0.4;
  }
  100% {
    transform: translate(-50%, -50%) scale(2);
    opacity: 0;
  }
}

/* 添加按钮组样式 */
.button-group {
  display: flex;
  gap: 8px;
}

.delete-button-wrapper {
  display: inline-block;
}
</style> 