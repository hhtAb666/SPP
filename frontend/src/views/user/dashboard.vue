<!-- 用户-个人仪表盘页面 -->
<template>
  <div class="user-dashboard">
    <div class="welcome-section">
      <h2>欢迎回来, {{ userStore.userInfo.name }}!</h2>
    </div>

    
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <div class="stat-icon" style="background: #409EFF">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="value">{{ totalDuration }} min</div>
            <div class="label">总运动时长</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <div class="stat-icon" style="background: #F56C6C">
            <el-icon><Cpu /></el-icon>
          </div>
          <div class="stat-info">
            <div class="value">{{ totalCalories }} kcal</div>
            <div class="label">总消耗热量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <div class="stat-icon" style="background: #E6A23C">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="value">{{ reservationCount }}</div>
            <div class="label">当前预约</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <div class="stat-icon" style="background: #67C23A">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="value">{{ latestWeight }} kg</div>
            <div class="label">最新体重</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>运动类型分布</template>
          <div ref="pieChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>最近预约</template>
          <el-table :data="recentReservations" style="width: 100%" height="300">
            <el-table-column prop="venueName" label="场馆" />
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.status === 0 ? 'success' : 'info'">
                  {{ scope.row.status === 0 ? '已预约' : '已取消' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { getSportRecords, getHealthRecords, getMyReservations } from '@/api/user'
import * as echarts from 'echarts'

const userStore = useUserStore()
const totalDuration = ref(0)
const totalCalories = ref(0)
const reservationCount = ref(0)
const latestWeight = ref('-')
const recentReservations = ref([])
const pieChartRef = ref(null)

const initData = async () => {
  const userId = userStore.userInfo.id
  
  
  const sportRes = await getSportRecords({ userId, page: 1, size: 100 })
  const sports = sportRes.records
  totalDuration.value = sports.reduce((sum, item) => sum + item.duration, 0)
  totalCalories.value = sports.reduce((sum, item) => sum + item.calories, 0)
  
  
  renderPieChart(sports)

  
  const resRes = await getMyReservations({ userId, page: 1, size: 5, status: 1 })
  recentReservations.value = resRes.records
  reservationCount.value = resRes.total

  
  const healthRes = await getHealthRecords({ userId, page: 1, size: 1 })
  if (healthRes.records.length > 0) {
    latestWeight.value = healthRes.records[0].weight
  }
}

const renderPieChart = (sports) => {
  if (!pieChartRef.value) return
  const chart = echarts.init(pieChartRef.value)
  
  
  const typeMap = {}
  sports.forEach(item => {
    typeMap[item.sportType] = (typeMap[item.sportType] || 0) + 1
  })
  
  const data = Object.keys(typeMap).map(key => ({
    name: key,
    value: typeMap[key]
  }))

  const option = {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [
      {
        name: '运动类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        data: data.length ? data : [{ name: '暂无数据', value: 0 }]
      }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.user-dashboard {
  padding: 20px 0;
}
.welcome-section {
  margin-bottom: 30px;
}
.welcome-section h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}
.welcome-section p {
  margin: 5px 0 0;
  color: #909399;
}
.stat-cards {
  margin-bottom: 30px;
}
.stat-item {
  display: flex;
  align-items: center;
  padding: 10px;
}
.stat-item :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 10px;
  width: 100%;
}
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
  color: white;
  font-size: 24px;
}
.stat-info .value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}
.stat-info .label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
