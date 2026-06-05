<!-- 管理员-场馆列表页面 -->
<template>
  <div class="venue-list">
    
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="场馆名称">
          <el-input v-model="searchForm.name" placeholder="输入名称搜索" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="选择类型" clearable>
            <el-option label="羽毛球" value="Badminton" />
            <el-option label="篮球" value="Basketball" />
            <el-option label="足球" value="Football" />
            <el-option label="网球" value="Tennis" />
            <el-option label="健身房" value="Gym" />
            <el-option label="游泳" value="Swimming" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleAdd">添加场馆</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    
    <el-card class="table-card">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="type" label="类型" width="120">
            <template #default="scope">
                {{ getTypeText(scope.row.type) }}
            </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '正常' : '维护中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开放时间" width="200">
          <template #default="scope">
            <div v-if="scope.row.openDays">
              {{ formatOpenDays(scope.row.openDays) }}<br/>
              {{ scope.row.openStartTime }}:00 - {{ scope.row.openEndTime }}:00
            </div>
            <div v-else>默认规则</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleSchedule(scope.row)">排班详情</el-button>
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row.id)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      
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

    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加场馆' : '编辑场馆'"
      width="50%"
    >
      <el-form :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="名称">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
                <el-option label="羽毛球" value="Badminton" />
                <el-option label="篮球" value="Basketball" />
                <el-option label="足球" value="Football" />
                <el-option label="网球" value="Tennis" />
                <el-option label="健身房" value="Gym" />
                <el-option label="游泳" value="Swimming" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="位置">
              <el-input v-model="form.location" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">维护中</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">排班规则配置 (修改将重新生成未来排班)</el-divider>
        
        <el-form-item label="开放日期">
          <el-checkbox-group v-model="form.openDaysList">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="7">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="开放时间">
              <el-time-select
                v-model="form.startTimeStr"
                start="06:00"
                step="01:00"
                end="23:00"
                placeholder="开始时间"
                style="width: 140px; margin-right: 10px;"
              />
              <span>至</span>
              <el-time-select
                v-model="form.endTimeStr"
                start="06:00"
                step="01:00"
                end="23:00"
                placeholder="结束时间"
                style="width: 140px; margin-left: 10px;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认容量">
              <el-input-number v-model="form.defaultMaxPeople" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    
    <el-drawer v-model="scheduleDrawerVisible" title="排班管理" size="50%">
        <Schedule :venue-id="currentVenueId" v-if="scheduleDrawerVisible" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getVenueList, addVenue, updateVenue, deleteVenue } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { useTable } from '@/hooks/useTable'
import Schedule from './schedule.vue'


const {
  loading,
  tableData,
  total,
  currentPage,
  pageSize,
  searchForm,
  fetchData,
  handleSearch,
  resetSearch,
  handleSizeChange,
  handleCurrentChange,
  handleDelete: execDelete
} = useTable(getVenueList, deleteVenue, {
  name: '',
  type: ''
})

const dialogVisible = ref(false)
const dialogType = ref('add')
const form = reactive({
  id: '',
  name: '',
  type: '',
  location: '',
  status: 1,
  openDaysList: ['1','2','3','4','5','6','7'],
  startTimeStr: '09:00',
  endTimeStr: '22:00',
  defaultMaxPeople: 20,
  openDays: '',
  openStartTime: 9,
  openEndTime: 22
})

const scheduleDrawerVisible = ref(false)
const currentVenueId = ref(null)

const handleAdd = () => {
  dialogType.value = 'add'
  
  Object.assign(form, {
    id: '',
    name: '',
    type: '',
    location: '',
    status: 1,
    openDaysList: ['1','2','3','4','5','6','7'],
    startTimeStr: '09:00',
    endTimeStr: '22:00',
    defaultMaxPeople: 20
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(form, row)
  
  
  if (row.openDays) {
    form.openDaysList = row.openDays.split(',')
  } else {
    form.openDaysList = ['1','2','3','4','5','6','7']
  }
  
  if (row.openStartTime !== undefined) {
    form.startTimeStr = (row.openStartTime < 10 ? '0' : '') + row.openStartTime + ':00'
  } else {
    form.startTimeStr = '09:00'
  }
  
  if (row.openEndTime !== undefined) {
    form.endTimeStr = (row.openEndTime < 10 ? '0' : '') + row.openEndTime + ':00'
  } else {
    form.endTimeStr = '22:00'
  }
  
  if (!row.defaultMaxPeople) {
    form.defaultMaxPeople = 20
  }
  
  dialogVisible.value = true
}

const handleDelete = (id) => {
  execDelete(id)
}

const handleSchedule = (row) => {
    currentVenueId.value = row.id
    scheduleDrawerVisible.value = true
}

const submitForm = async () => {
  try {
    
    form.openDays = form.openDaysList.join(',')
    form.openStartTime = parseInt(form.startTimeStr.split(':')[0])
    form.openEndTime = parseInt(form.endTimeStr.split(':')[0])
    
    if (form.openStartTime >= form.openEndTime) {
        ElMessage.error('结束时间必须晚于开始时间')
        return
    }

    if (dialogType.value === 'add') {
      await addVenue(form)
      ElMessage.success('添加成功，已自动生成排班')
    } else {
      await updateVenue(form)
      ElMessage.success('更新成功，未来排班已重新生成')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
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

const formatOpenDays = (daysStr) => {
    if (!daysStr) return '每天'
    const days = daysStr.split(',')
    if (days.length === 7) return '每天'
    const map = {'1':'周一','2':'周二','3':'周三','4':'周四','5':'周五','6':'周六','7':'周日'}
    return days.map(d => map[d]).join('、')
}
</script>

<style scoped>
.venue-list {
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