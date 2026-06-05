<!-- 管理员-预约审核页面 -->
<template>
  <div class="reservation-audit">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 200px">
            <el-option label="已预约" :value="0" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="venueName" label="场馆" width="150" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="150">
          <template #default="scope">
            {{ formatTimeSlot(scope.row.timeSlot) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 0"
              size="small"
              type="primary"
              @click="openUpdateDialog(scope.row)"
            >修改</el-button>
            <el-button
              v-if="scope.row.status === 0"
              size="small"
              type="warning"
              @click="handleAudit(scope.row.id, 2)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 修改预约对话框 -->
    <el-dialog
      v-model="updateDialogVisible"
      title="修改预约时间"
      width="500px"
    >
      <el-form :model="updateForm" label-width="100px">
        <el-form-item label="当前场馆">
          <span>{{ currentReservation?.venueName }}</span>
        </el-form-item>
        <el-form-item label="选择日期">
          <el-date-picker
            v-model="updateForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="选择时间段">
          <el-select v-model="updateForm.scheduleId" placeholder="请选择时间段" no-data-text="该日期暂无可用排班">
            <el-option
              v-for="item in availableSchedules"
              :key="item.id"
              :label="formatScheduleLabel(item)"
              :value="item.id"
              :disabled="item.currentPeople >= item.maxPeople"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="updateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateSubmit" :loading="updateLoading">
            确认修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllReservations, auditReservation, adminUpdateReservation, getScheduleList } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchForm = reactive({
  status: ''
})

// 修改相关状态
const updateDialogVisible = ref(false)
const updateLoading = ref(false)
const currentReservation = ref(null)
const availableSchedules = ref([])
const updateForm = reactive({
  date: '',
  scheduleId: ''
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getAllReservations({
      page: currentPage.value,
      size: pageSize.value,
      status: searchForm.status
    })
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchList()
}

// 打开修改对话框
const openUpdateDialog = (row) => {
  currentReservation.value = row
  updateForm.date = row.date // 默认选中当前预约的日期
  updateForm.scheduleId = '' // 重置选中的时间段
  availableSchedules.value = []
  updateDialogVisible.value = true
  
  // 加载该日期的排班
  fetchSchedules(row.venueId, row.date)
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁止选择今天之前的日期
}

const handleDateChange = (val) => {
  updateForm.scheduleId = ''
  if (val && currentReservation.value) {
    fetchSchedules(currentReservation.value.venueId, val)
  }
}

const fetchSchedules = async (venueId, date) => {
  try {
    const res = await getScheduleList({ venueId, date })
    availableSchedules.value = res
  } catch (error) {
    console.error(error)
    ElMessage.error('获取排班失败')
  }
}

const formatScheduleLabel = (item) => {
  return `${item.timeSlot}:00 - ${item.timeSlot + 1}:00 (剩余: ${item.maxPeople - item.currentPeople})`
}

const handleUpdateSubmit = async () => {
  if (!updateForm.scheduleId) {
    ElMessage.warning('请选择新的时间段')
    return
  }
  
  updateLoading.value = true
  try {
    await adminUpdateReservation(currentReservation.value.id, updateForm.scheduleId)
    ElMessage.success('修改成功')
    updateDialogVisible.value = false
    fetchList()
  } finally {
    updateLoading.value = false
  }
}

const handleAudit = (id, status) => {
  const actionText = '取消'
  ElMessageBox.confirm(`确定要${actionText}该预约吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await auditReservation(id, status)
    ElMessage.success('操作成功')
    fetchList()
  })
}

const formatTimeSlot = (slot) => {
  return `${slot}:00 - ${slot + 1}:00`
}

const getStatusType = (status) => {
  const map = {
    0: 'success', 
    2: 'info'
  }
  return map[status]
}

const getStatusText = (status) => {
  const map = {
    0: '已预约',
    2: '已取消'
  }
  return map[status]
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.reservation-audit {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
