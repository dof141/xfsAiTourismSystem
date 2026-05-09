<template>
  <div class="modern-dashboard">
    <!-- 1. 顶部指标：渐变玻璃卡片 -->
    <div class="metrics-grid">
      <div v-for="(card, i) in cards" :key="i" class="metric-card" :style="{ '--color': card.color }">
        <div class="card-icon">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div class="card-info">
          <div class="label">{{ card.label }}</div>
          <div class="value-row">
            <span class="value">{{ card.value }}</span>
            <span class="unit">{{ card.unit }}</span>
          </div>
        </div>
        <!-- 背景装饰 -->
        <div class="card-bg-circle"></div>
      </div>
    </div>

    <!-- 2. 中间图表：极简白卡片布局 -->
    <div class="charts-section">
      <!-- 左侧：趋势大图 -->
      <div class="white-card main-chart">
        <div class="card-header">
          <span class="title">📈 游客预约增长趋势</span>
          <el-radio-group v-model="timeRange" size="small">
            <el-radio-button label="7D">7天</el-radio-button>
            <el-radio-button label="30D">30天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="trendChartRef" class="chart-box"></div>
      </div>

      <!-- 右侧：热度小图 -->
      <div class="white-card side-chart">
        <div class="card-header">
          <span class="title">📍 景区热度分布</span>
        </div>
        <div ref="heatChartRef" class="chart-box"></div>
      </div>
    </div>

    <!-- 3. 底部：系统说明 -->
    <div class="white-card system-status">
      <div class="card-header">
        <span class="title">🛡️ 系统技术架构</span>
      </div>
      <div class="status-list">
        <div class="status-item">
          <span class="dot online"></span>
          <span class="name">AI 导览引擎</span>
          <span class="state">基于大语言模型</span>
        </div>
        <div class="status-item">
          <span class="dot online"></span>
          <span class="name">Redis 票务库存</span>
          <span class="state">原子扣减防超卖</span>
        </div>
        <div class="status-item">
          <span class="dot online"></span>
          <span class="name">二维码核销</span>
          <span class="state">ZXing 动态生成</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { User, Tickets, Connection, PieChart } from '@element-plus/icons-vue'

const timeRange = ref('7D')
const cards = ref([
  { label: '今日预约', value: '0', unit: '人', icon: User, color: '#6366f1' },
  { label: '累计接待', value: '0', unit: '人', icon: Tickets, color: '#0ea5e9' },
  { label: '在线景区', value: '0', unit: '个', icon: Connection, color: '#10b981' },
  { label: '核销率', value: '0', unit: '%', icon: PieChart, color: '#f59e0b' }
])

const trendChartRef = ref(null)
const heatChartRef = ref(null)
let trendChart = null
let heatChart = null

const fetchCardData = async () => {
  try {
    const res = await request.get('/stats/cards')
    const d = res.data
    cards.value[0].value = d.todayReserve
    cards.value[1].value = d.totalVisitors
    cards.value[2].value = d.areaCount
    cards.value[3].value = d.verifyRate.replace('%', '')
  } catch (e) { ElMessage.error('获取统计数据失败') }
}

const initCharts = async () => {
  // 1. 极简趋势图
  trendChart = echarts.init(trendChartRef.value)
  const trendRes = await request.get('/stats/trend')
  trendChart.setOption({
    grid: { top: '10%', left: '3%', right: '3%', bottom: '3%', containLabel: true },
    tooltip: { trigger: 'axis', backgroundColor: '#fff', textStyle: { color: '#333' } },
    xAxis: {
      type: 'category',
      data: trendRes.data.dates,
      axisLine: { show: false },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }
    },
    series: [{
      data: trendRes.data.values,
      type: 'line',
      smooth: true,
      showSymbol: false,
      lineStyle: { width: 4, color: '#6366f1' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(99, 102, 241, 0.2)' },
          { offset: 1, color: 'rgba(99, 102, 241, 0)' }
        ])
      }
    }]
  })

  // 2. 现代圆环图
  heatChart = echarts.init(heatChartRef.value)
  const heatRes = await request.get('/stats/areaHeat')
  heatChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0', icon: 'circle', itemWidth: 8 },
    series: [{
      type: 'pie',
      radius: ['55%', '80%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 4 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: heatRes.data
    }]
  })
}

onMounted(() => {
  fetchCardData()
  initCharts()
  window.addEventListener('resize', () => {
    trendChart?.resize()
    heatChart?.resize()
  })
})
</script>

<style scoped>
.modern-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 24px;
}

/* 指标卡片网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.metric-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0,0,0,0.02);
}

.card-icon {
  width: 56px;
  height: 56px;
  background-color: color-mix(in srgb, var(--color), white 90%);
  color: var(--color);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}

.card-info .label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 4px;
}

.card-info .value {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
}

.card-info .unit {
  font-size: 14px;
  color: #94a3b8;
  margin-left: 4px;
}

.card-bg-circle {
  position: absolute;
  right: -20px;
  top: -20px;
  width: 100px;
  height: 100px;
  background-color: var(--color);
  opacity: 0.03;
  border-radius: 50%;
}

/* 通用白色卡片样式 */
.white-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0,0,0,0.02);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header .title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

/* 图表布局 */
.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-box {
  height: 350px;
  width: 100%;
}

/* 底部状态 */
.status-list {
  display: flex;
  gap: 40px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.online {
  background-color: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.status-item .name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.status-item .state {
  font-size: 13px;
  color: #94a3b8;
}
</style>
