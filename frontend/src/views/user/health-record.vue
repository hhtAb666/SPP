<!-- 用户-健康记录页面 -->
<template>
  <div class="health-sport-container">
    <el-tabs v-model="activeTab">
      
      <!-- 运动记录 Tab -->
      <el-tab-pane label="运动记录" name="sport">
        <div class="action-bar">
          <el-button type="primary" @click="showSportDialog">添加运动记录</el-button>
        </div>
        
        <!-- 图表类型选择 -->
        <div class="chart-selector">
          <el-radio-group v-model="sportChartType" @change="initSportChart">
            <el-radio-button label="duration">运动时长</el-radio-button>
            <el-radio-button label="calories">消耗卡路里</el-radio-button>
            <el-radio-button label="all">全部</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 运动图表 -->
        <div class="chart-container" ref="sportChartRef"></div>

        <!-- 运动列表 -->
        <el-table :data="sportList" style="width: 100%; margin-top: 20px">
          <el-table-column prop="recordDate" label="日期" width="150" />
          <el-table-column prop="sportType" label="类型" width="150">
            <template #default="scope">
              {{ getSportTypeText(scope.row.sportType) }}
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="时长 (分钟)" width="150" />
          <el-table-column prop="calories" label="消耗 (千卡)" width="150" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="danger" size="small" @click="handleDeleteSport(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 健康记录 Tab -->
      <el-tab-pane label="健康记录" name="health">
        <div class="action-bar">
          <el-button type="primary" @click="showHealthDialog">添加健康记录</el-button>
        </div>

        <!-- 图表类型选择 -->
        <div class="chart-selector">
          <el-radio-group v-model="healthChartType" @change="initHealthChart">
            <el-radio-button label="weight">体重</el-radio-button>
            <el-radio-button label="height">身高</el-radio-button>
            <el-radio-button label="bmi">BMI</el-radio-button>
            <el-radio-button label="all">全部</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 健康图表 -->
        <div class="chart-container" ref="healthChartRef"></div>

        <!-- 健康列表 -->
        <el-table :data="healthList" style="width: 100%; margin-top: 20px">
          <el-table-column prop="recordDate" label="日期" width="150" />
          <el-table-column prop="height" label="身高 (cm)" width="150" />
          <el-table-column prop="weight" label="体重 (kg)" width="150" />
          <el-table-column label="BMI" width="150">
            <template #default="scope">
              {{ calculateBMI(scope.row.height, scope.row.weight) }}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="danger" size="small" @click="handleDeleteHealth(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加运动记录对话框 -->
    <el-dialog v-model="sportDialogVisible" title="添加运动记录" width="30%">
      <el-form :model="sportForm" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker v-model="sportForm.recordDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="sportForm.sportType" placeholder="选择运动类型">
            <el-option label="跑步" value="Running" />
            <el-option label="游泳" value="Swimming" />
            <el-option label="骑行" value="Cycling" />
            <el-option label="健身" value="Gym" />
            <el-option label="羽毛球" value="Badminton" />
          </el-select>
        </el-form-item>
        <el-form-item label="时长">
          <el-input-number v-model="sportForm.duration" :min="1" /> 分钟
        </el-form-item>
        <el-form-item label="消耗">
          <el-input-number v-model="sportForm.calories" :min="1" /> 千卡
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSport">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加健康记录对话框 -->
    <el-dialog v-model="healthDialogVisible" title="添加健康记录" width="30%">
      <el-form :model="healthForm" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker v-model="healthForm.recordDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="身高 (cm)">
          <el-input-number v-model="healthForm.height" :min="100" :max="250" />
        </el-form-item>
        <el-form-item label="体重 (kg)">
          <el-input-number v-model="healthForm.weight" :min="30" :max="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="healthDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHealth">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { getSportRecords, addSportRecord, deleteSportRecord, getHealthRecords, addHealthRecord, deleteHealthRecord } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('sport')

// 运动记录相关
const sportList = ref([])
const sportChartRef = ref(null)
let sportChart = null
const sportChartType = ref('duration') // 默认显示时长
const sportDialogVisible = ref(false)
const sportForm = reactive({
  recordDate: '',
  sportType: '',
  duration: 30,
  calories: 200
})

// 健康记录相关
const healthList = ref([])
const healthChartRef = ref(null)
let healthChart = null
const healthChartType = ref('weight') // 默认显示体重
const healthDialogVisible = ref(false)
const healthForm = reactive({
  recordDate: '',
  height: 170,
  weight: 60
})

// 初始化运动图表
const initSportChart = () => {
  if (!sportChartRef.value) return
  if (sportChart) sportChart.dispose()
  
  sportChart = echarts.init(sportChartRef.value)
  
  const sortedData = [...sportList.value].sort((a, b) => new Date(a.recordDate) - new Date(b.recordDate))
  
  let option = {}

  if (sportChartType.value === 'all') {
    option = {
      title: { text: '运动综合趋势' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['运动时长', '消耗卡路里'] },
      xAxis: { type: 'category', data: sortedData.map(item => item.recordDate) },
      yAxis: [
        { type: 'value', name: '分钟', position: 'left' },
        { type: 'value', name: '千卡', position: 'right' }
      ],
      series: [
        {
          name: '运动时长',
          data: sortedData.map(item => item.duration),
          type: 'line',
          smooth: true,
          yAxisIndex: 0,
          itemStyle: { color: '#409EFF' },
          areaStyle: { color: '#409EFF', opacity: 0.2 }
        },
        {
          name: '消耗卡路里',
          data: sortedData.map(item => item.calories),
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          itemStyle: { color: '#E6A23C' },
          areaStyle: { color: '#E6A23C', opacity: 0.2 }
        }
      ]
    }
  } else {
    let titleText = ''
    let yAxisName = ''
    let seriesData = []

    if (sportChartType.value === 'duration') {
      titleText = '运动时长趋势'
      yAxisName = '分钟'
      seriesData = sortedData.map(item => item.duration)
    } else {
      titleText = '消耗卡路里趋势'
      yAxisName = '千卡'
      seriesData = sortedData.map(item => item.calories)
    }

    option = {
      title: { text: titleText },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: sortedData.map(item => item.recordDate) },
      yAxis: { type: 'value', name: yAxisName },
      series: [{
        data: seriesData,
        type: 'line',
        smooth: true,
        areaStyle: {},
        itemStyle: { color: sportChartType.value === 'duration' ? '#409EFF' : '#E6A23C' },
        areaStyle: { color: sportChartType.value === 'duration' ? '#409EFF' : '#E6A23C', opacity: 0.2 }
      }]
    }
  }
  sportChart.setOption(option, true)
}

// 初始化健康图表
const initHealthChart = () => {
  if (!healthChartRef.value) return
  if (healthChart) healthChart.dispose()
  
  healthChart = echarts.init(healthChartRef.value)
  
  const sortedData = [...healthList.value].sort((a, b) => new Date(a.recordDate) - new Date(b.recordDate))
  
  let option = {}

  if (healthChartType.value === 'all') {
    option = {
      title: { text: '健康综合趋势' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['体重', '身高', 'BMI'] },
      grid: { right: '20%' }, // 为右侧第二个Y轴留出空间
      xAxis: { type: 'category', data: sortedData.map(item => item.recordDate) },
      yAxis: [
        { type: 'value', name: '体重(kg)', position: 'left', min: 'dataMin' },
        { type: 'value', name: '身高(cm)', position: 'right', offset: 0, min: 'dataMin' },
        { type: 'value', name: 'BMI', position: 'right', offset: 60, min: 'dataMin' }
      ],
      series: [
        {
          name: '体重',
          data: sortedData.map(item => item.weight),
          type: 'line',
          smooth: true,
          yAxisIndex: 0,
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '身高',
          data: sortedData.map(item => item.height),
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          itemStyle: { color: '#E6A23C' }
        },
        {
          name: 'BMI',
          data: sortedData.map(item => {
             if (!item.height || !item.weight) return 0
             return (item.weight / ((item.height / 100) * (item.height / 100))).toFixed(1)
          }),
          type: 'line',
          smooth: true,
          yAxisIndex: 2,
          itemStyle: { color: '#F56C6C' }
        }
      ]
    }
  } else {
    let titleText = ''
    let yAxisName = ''
    let seriesData = []
    let color = '#67C23A'

    if (healthChartType.value === 'weight') {
      titleText = '体重趋势'
      yAxisName = 'kg'
      seriesData = sortedData.map(item => item.weight)
      color = '#409EFF'
    } else if (healthChartType.value === 'height') {
      titleText = '身高趋势'
      yAxisName = 'cm'
      seriesData = sortedData.map(item => item.height)
      color = '#E6A23C'
    } else {
      titleText = 'BMI 趋势'
      yAxisName = 'BMI'
      seriesData = sortedData.map(item => {
        if (!item.height || !item.weight) return 0
        return (item.weight / ((item.height / 100) * (item.height / 100))).toFixed(1)
      })
      color = '#F56C6C'
    }

    option = {
      title: { text: titleText },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: sortedData.map(item => item.recordDate) },
      yAxis: { type: 'value', name: yAxisName, min: 'dataMin' },
      series: [{
        data: seriesData,
        type: 'line',
        smooth: true,
        itemStyle: { color: color }
      }]
    }
  }
  healthChart.setOption(option, true)
}

const fetchSportData = async () => {
  const res = await getSportRecords({ userId: userStore.userInfo.id })
  sportList.value = res.records 
  nextTick(() => initSportChart())
}

const fetchHealthData = async () => {
  const res = await getHealthRecords({ userId: userStore.userInfo.id })
  healthList.value = res.records 
  nextTick(() => initHealthChart())
}

const showSportDialog = () => {
  sportForm.recordDate = new Date().toISOString().split('T')[0]
  sportDialogVisible.value = true
}

const submitSport = async () => {
  await addSportRecord({ ...sportForm, userId: userStore.userInfo.id })
  ElMessage.success('添加成功')
  sportDialogVisible.value = false
  fetchSportData()
}

const handleDeleteSport = async (id) => {
  await deleteSportRecord(id)
  ElMessage.success('删除成功')
  fetchSportData()
}

const showHealthDialog = () => {
  healthForm.recordDate = new Date().toISOString().split('T')[0]
  healthDialogVisible.value = true
}

const submitHealth = async () => {
  await addHealthRecord({ ...healthForm, userId: userStore.userInfo.id })
  ElMessage.success('添加成功')
  healthDialogVisible.value = false
  fetchHealthData()
}

const handleDeleteHealth = async (id) => {
  await deleteHealthRecord(id)
  ElMessage.success('删除成功')
  fetchHealthData()
}

const calculateBMI = (h, w) => {
  if (!h || !w) return '-'
  const bmi = w / ((h / 100) * (h / 100))
  return bmi.toFixed(1)
}


watch(activeTab, (val) => {
  nextTick(() => {
    if (val === 'sport') initSportChart()
    if (val === 'health') initHealthChart()
  })
})

const getSportTypeText = (type) => {
  const map = {
    'Running': '跑步',
    'Swimming': '游泳',
    'Cycling': '骑行',
    'Gym': '健身',
    'Badminton': '羽毛球'
  }
  return map[type] || type
}

onMounted(() => {
  fetchSportData()
  fetchHealthData()
  window.addEventListener('resize', () => {
    sportChart && sportChart.resize()
    healthChart && healthChart.resize()
  })
})
</script>

<style scoped>
.health-sport-container {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  min-height: 500px;
}
.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-selector {
  margin-bottom: 15px;
  text-align: center;
}
.chart-container {
  width: 100%;
  height: 350px;
}
</style>
