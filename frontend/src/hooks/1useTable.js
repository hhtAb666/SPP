/**
 * 通用表格逻辑 Hook
 */
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useTable(apiFunc, deleteFunc) {
  const loading = ref(false)
  const tableData = ref([])
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const searchForm = ref({})

  const fetchList = async () => {
    loading.value = true
    try {
      const res = await apiFunc({
        page: currentPage.value,
        size: pageSize.value,
        ...searchForm.value
      })

      if (res && res.records) {
        tableData.value = res.records
        total.value = res.total
      } else {

        tableData.value = res
        total.value = res.length
      }
    } finally {
      loading.value = false
    }
  }

  const handleSearch = () => {
    currentPage.value = 1
    fetchList()
  }

  const resetSearch = () => {

    for (const key in searchForm.value) {
      searchForm.value[key] = ''
    }
    handleSearch()
  }

  const handleDelete = (id, deleteApi) => {
    ElMessageBox.confirm('确定要删除吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {



      const api = deleteApi || deleteFunc
      if (!api) {
        console.error('Delete function is not defined')
        return
      }
      await api(id)
      ElMessage.success('删除成功')
      fetchList()
    })
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

  return {
    loading,
    tableData,
    total,
    currentPage,
    pageSize,
    searchForm,
    fetchList,
    handleSearch,
    resetSearch,
    handleDelete,
    handleSizeChange,
    handleCurrentChange
  }
}