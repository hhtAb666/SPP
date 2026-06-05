<!-- 用户-我的预约页面 -->
<template>
  <div class="my-reservations">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的预约记录</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="venueName" label="场馆" width="180" />
        <el-table-column prop="date" label="日期" width="150" />
        <el-table-column prop="timeSlot" label="时间" width="150">
          <template #default="scope">
            {{ formatTimeSlot(scope.row.timeSlot) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预约时间" width="200">
            <template #default="scope">
                {{ formatDateTime(scope.row.createTime) }}
            </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 0"
              size="small"
              type="primary"
              @click="handleEdit(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              size="small"
              type="danger"
              @click="handleCancel(scope.row.id)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          layout="prev, pager, next"
          :total="total"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    
    <el-dialog v-model="editDialogVisible" title="修改预约" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="原预约">
          <div>{{ currentEditRow?.venueName }} - {{ currentEditRow?.date }} {{ formatTimeSlot(currentEditRow?.timeSlot) }}</div>
        </el-form-item>
        <el-form-item label="新日期">
          <el-date-picker
            v-model="editForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            @change="fetchSchedule"
          />
        </el-form-item>
        <el-form-item label="新时间段">
          <el-select v-model="editForm.scheduleId" placeholder="选择时间段" :loading="scheduleLoading">
             <el-option
                v-for="item in scheduleList"
                :key="item.id"
                :label="formatTimeSlot(item.timeSlot) + ' (余' + (item.maxPeople - item.currentPeople) + ')'"
                :value="item.id"
                :disabled="item.currentPeople >= item.maxPeople"
             />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEdit">确定修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyReservations, cancelReservation, updateReservation, getVenueSchedule } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)


const editDialogVisible = ref(false)
const currentEditRow = ref(null)
const scheduleLoading = ref(false)
const scheduleList = ref([])
const editForm = ref({
  date: '',
  scheduleId: ''
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getMyReservations({
      page: currentPage.value,
      size: pageSize.value,
      userId: userStore.userInfo.id
    })
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleCancel = (id) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await cancelReservation(id)
    ElMessage.success('取消成功')
    fetchList()
  })
}

const handleEdit = (row) => {
  currentEditRow.value = row
  editForm.value = {
    date: '',
    scheduleId: ''
  }
  scheduleList.value = []
  editDialogVisible.value = true
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const fetchSchedule = async () => {
  if (!editForm.value.date || !currentEditRow.value) return
  scheduleLoading.value = true
  try {
    
    
    
    const res = await getVenueSchedule(currentEditRow.value.venueId, editForm.value.date)
    scheduleList.value = res
  } finally {
    scheduleLoading.value = false
  }
}

const confirmEdit = async () => {
  if (!editForm.value.scheduleId) {
    ElMessage.warning('请选择新的时间段')
    return
  }
  try {
    await updateReservation({
      id: currentEditRow.value.id,
      newScheduleId: editForm.value.scheduleId
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchList()
  } catch (error) {
    console.error(error)
  }
}

const formatDateTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ')
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

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.my-reservations {
  padding: 20px 0;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
