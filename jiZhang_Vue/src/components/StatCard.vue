<template>
  <el-card class="stat-card" :body-style="{ padding: '24px' }">
    <div class="card-content">
      <div class="icon-bg" :style="{ background: getGradient() }">
        <i :class="getIcon()" class="stat-icon"></i>
      </div>
      <div class="info">
        <div class="stat-title">{{ title }}</div>
        <div class="stat-amount" :style="{ color: getColor() }">
          ¥ {{ displayAmount }}
        </div>
        <div class="stat-footer">
          <div class="trend" v-if="trend">
            <i :class="getTrendIcon()" :style="{ color: getTrendColor() }"></i>
            <span :style="{ color: getTrendColor() }">{{ trend }}%</span>
            <span class="trend-label">较上月</span>
          </div>
          <div class="chart-mini" v-if="showChart">
            <div class="chart-bar" 
              v-for="(item, index) in animatedChartData" 
              :key="index"
              :style="{ 
                height: `${item}%`,
                background: getColor(0.2 + (index / chartData.length) * 0.8)
              }"
            >
              <div class="chart-tooltip">¥ {{ formatAmount(getBarValue(index)) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  amount: {
    type: Number,
    required: true
  },
  type: {
    type: String,
    default: 'balance'
  },
  trend: {
    type: Number,
    default: null
  },
  showChart: {
    type: Boolean,
    default: true
  }
})

// 动画相关状态
const displayAmount = ref('0.00')
const animatedChartData = ref([0, 0, 0, 0, 0, 0, 0])

// 模拟图表数据
const chartData = [30, 50, 20, 60, 40, 70, 45]

// 获取柱状图实际值
const getBarValue = (index) => {
  return (props.amount / 100) * chartData[index] / Math.max(...chartData)
}

// 监听金额变化并执行动画
watch(() => props.amount, (newVal) => {
  gsap.to(displayAmount, {
    duration: 1,
    value: newVal,
    onUpdate: () => {
      displayAmount.value = formatAmount(displayAmount.value)
    }
  })
})

onMounted(() => {
  // 初始金额动画
  gsap.to(displayAmount, {
    duration: 1,
    value: props.amount,
    onUpdate: () => {
      displayAmount.value = formatAmount(displayAmount.value)
    }
  })

  // 柱状图动画
  chartData.forEach((value, index) => {
    gsap.to(animatedChartData.value, {
      duration: 1,
      delay: index * 0.1,
      [index]: value,
      ease: "elastic.out(1, 0.5)"
    })
  })
})

const getIcon = () => {
  switch (props.type) {
    case 'income':
      return 'fas fa-arrow-trend-up'
    case 'expense':
      return 'fas fa-arrow-trend-down'
    default:
      return 'fas fa-wallet'
  }
}

const getColor = (opacity = 1) => {
  const colors = {
    income: `rgba(82, 196, 26, ${opacity})`,
    expense: `rgba(245, 34, 45, ${opacity})`,
    balance: `rgba(24, 144, 255, ${opacity})`
  }
  return colors[props.type] || colors.balance
}

const getGradient = () => {
  const color = getColor(0.15)
  return `linear-gradient(135deg, ${color} 0%, rgba(255,255,255,0.5) 100%)`
}

const getTrendIcon = () => {
  return props.trend > 0 ? 'fas fa-caret-up' : 'fas fa-caret-down'
}

const getTrendColor = () => {
  return props.trend > 0 ? '#52c41a' : '#f5222d'
}

const formatAmount = (amount) => {
  return Number(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}
</script>

<style scoped>
.stat-card {
  height: 100%;
  transition: all 0.3s;
  border-radius: 12px;
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  border: none;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-content {
  display: flex;
  gap: 20px;
}

.icon-bg {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.stat-card:hover .icon-bg {
  transform: scale(1.1);
}

.stat-icon {
  font-size: 24px;
  color: currentColor;
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-title {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
  margin-bottom: 8px;
}

.stat-amount {
  font-size: 28px;
  font-weight: 600;
  line-height: 1.2;
  margin-bottom: 12px;
  font-family: "DIN Alternate", "Arial", sans-serif;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.trend-label {
  color: rgba(0, 0, 0, 0.45);
  margin-left: 4px;
}

.chart-mini {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 32px;
}

.chart-bar {
  width: 6px;
  border-radius: 3px;
  transition: all 0.3s;
  position: relative;
  cursor: pointer;
}

.chart-bar:hover {
  transform: scaleY(1.1);
}

.chart-tooltip {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%) scale(0);
  background: rgba(0, 0, 0, 0.75);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  transition: transform 0.3s;
  transform-origin: bottom center;
}

.chart-bar:hover .chart-tooltip {
  transform: translateX(-50%) scale(1);
}

.chart-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 4px solid transparent;
  border-top-color: rgba(0, 0, 0, 0.75);
}

@media screen and (max-width: 768px) {
  .stat-amount {
    font-size: 24px;
  }
  
  .icon-bg {
    width: 48px;
    height: 48px;
  }
  
  .stat-icon {
    font-size: 20px;
  }
}
</style> 