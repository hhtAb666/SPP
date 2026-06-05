<!-- 用户-场馆预定页面 -->
<template>
  <div class="venue-booking">
    
    <div class="filter-section">
      <el-radio-group v-model="activeType" size="large" @change="handleTypeChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="Badminton">羽毛球</el-radio-button>
        <el-radio-button label="Basketball">篮球</el-radio-button>
        <el-radio-button label="Football">足球</el-radio-button>
        <el-radio-button label="Tennis">网球</el-radio-button>
        <el-radio-button label="Gym">健身房</el-radio-button>
        <el-radio-button label="Swimming">游泳</el-radio-button>
      </el-radio-group>
    </div>

    
    <div class="venue-grid" v-loading="loading">
      <el-empty v-if="venueList.length === 0" description="暂无场馆" />
      <el-card
        v-for="venue in venueList"
        :key="venue.id"
        class="venue-card"
        :body-style="{ padding: '0px' }"
      >
        <div class="venue-image">
          
          <div class="placeholder-img">{{ getTypeText(venue.type) }}</div>
        </div>
        <div class="venue-info">
          <h3>{{ venue.name }}</h3>
          <p class="location"><el-icon><Location /></el-icon> {{ venue.location }}</p>
          <div class="tags">
            <el-tag size="small" :type="venue.status === 1 ? 'success' : 'info'">
              {{ venue.status === 1 ? '开放中' : '维护中' }}
            </el-tag>
            <el-tag size="small" type="warning" style="margin-left: 5px">{{ getTypeText(venue.type) }}</el-tag>
          </div>
          <div class="actions">
            <el-button
              type="primary"
              :disabled="venue.status !== 1"
              @click="openBooking(venue)"
            >
              立即预约
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    
    <el-dialog
      v-model="dialogVisible"
      :title="'预约 - ' + currentVenue.name"
      width="600px"
    >
      <div class="booking-content">
        <div class="date-picker">
          <span>选择日期: </span>
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            value-format="YYYY-MM-DD"
            @change="fetchSchedule"
          />
        </div>

        <div class="time-slots" v-loading="scheduleLoading">
          <p v-if="!selectedDate" class="tip">请先选择日期</p>
          <el-empty v-else-if="scheduleList.length === 0" description="暂无可用时段" />
          <div v-else class="slots-grid">
            <div
              v-for="slot in scheduleList"
              :key="slot.id"
              class="slot-item"
              :class="{
                'disabled': slot.currentPeople >= slot.maxPeople || slot.status === 0,
                'selected': selectedSlot?.id === slot.id
              }"
              @click="selectSlot(slot)"
            >
              <div class="time">{{ formatTime(slot.timeSlot) }}</div>
              <div class="status">
                {{ slot.currentPeople }}/{{ slot.maxPeople }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :disabled="!selectedSlot" @click="confirmBooking">
            确认预约
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVenueList, getVenueSchedule, createReservation } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const venueList = ref([])
const activeType = ref('')

const dialogVisible = ref(false)
const currentVenue = ref({})
const selectedDate = ref('')
const scheduleLoading = ref(false)
const scheduleList = ref([])
const selectedSlot = ref(null)

const fetchVenues = async () => {
  loading.value = true
  try {
    const res = await getVenueList({
      page: 1,
      size: 100,
      type: activeType.value
    })
    venueList.value = res.records
  } finally {
    loading.value = false
  }
}

const handleTypeChange = () => {
  fetchVenues()
}

const openBooking = (venue) => {
  currentVenue.value = venue
  selectedDate.value = ''
  scheduleList.value = []
  selectedSlot.value = null
  dialogVisible.value = true
}

const disabledDate = (time) => {
  
  return time.getTime() < Date.now() - 8.64e7
}

const fetchSchedule = async () => {
  if (!selectedDate.value || !currentVenue.value?.id) {
    return
  }
  scheduleLoading.value = true
  selectedSlot.value = null
  try {
    const res = await getVenueSchedule(currentVenue.value.id, selectedDate.value)
    scheduleList.value = res
  } finally {
    scheduleLoading.value = false
  }
}

const selectSlot = (slot) => {
  if (slot.currentPeople >= slot.maxPeople || slot.status === 0) return
  selectedSlot.value = slot
}

const confirmBooking = () => {
  if (!selectedSlot.value) return
  
  ElMessageBox.confirm(
    `确定要预约 ${selectedDate.value} ${formatTime(selectedSlot.value.timeSlot)} 的 ${currentVenue.value.name} 吗？`,
    '预约确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'primary'
    }
  ).then(async () => {
    try {
      
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      
      await createReservation({
        userId: userInfo.id,
        venueId: currentVenue.value.id,
        scheduleId: selectedSlot.value.id
      })
      ElMessage.success('预约成功！')
      dialogVisible.value = false
      
      fetchSchedule()
    } catch (error) {
      console.error(error)
    }
  })
}

const formatTime = (slot) => {
  return `${slot}:00-${slot + 1}:00`
}

const getTypeText = (type) => {
  const map = {
    'Badminton': '羽毛球',
    'Basketball': '篮球',
    'Football': '足球',
    'Tennis': '网球',
    'Gym': '健身房',
    'Swimming': '游泳'
  }
  return map[type] || type
}

onMounted(() => {
  fetchVenues()
})
</script>

<style scoped>
.venue-booking {
  padding: 20px 0;
}
.filter-section {
  margin-bottom: 30px;
  text-align: center;
}
.venue-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.venue-card {
  transition: transform 0.3s;
}
.venue-card:hover {
  transform: translateY(-5px);
}
.venue-image {
  height: 160px;
  background-color: #eef1f6;
  display: flex;
  align-items: center;
  justify-content: center;
}
.placeholder-img {
  font-size: 24px;
  color: #909399;
  font-weight: bold;
}
.venue-info {
  padding: 15px;
}
.venue-info h3 {
  margin: 0 0 10px;
  font-size: 18px;
}
.location {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.tags {
  margin-bottom: 15px;
}
.actions {
  text-align: right;
}

.booking-content {
  padding: 10px;
}
.date-picker {
  margin-bottom: 20px;
}
.tip {
  color: #909399;
  text-align: center;
  padding: 20px;
}
.slots-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.slot-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}
.slot-item:hover:not(.disabled) {
  border-color: #409EFF;
  color: #409EFF;
}
.slot-item.selected {
  background-color: #409EFF;
  color: white;
  border-color: #409EFF;
}
.slot-item.disabled {
  background-color: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
}
.time {
  font-weight: bold;
  margin-bottom: 5px;
}
.status {
  font-size: 12px;
}
</style>
