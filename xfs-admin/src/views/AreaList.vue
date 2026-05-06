<template>
  <el-card shadow="never">
    <el-table :data="tableData" border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="景区名称" width="180" />
      <el-table-column prop="level" label="景区等级" width="100" align="center">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="所属县市" width="150" />
      <el-table-column prop="tel" label="咨询电话" width="150" />
      <el-table-column prop="intro" label="景区介绍" show-overflow-tooltip />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)

// 调用后端接口获取数据
const fetchAreaList = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/area/list')
    if (res.data.code === 200) {
      tableData.value = res.data.data
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取景区列表失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时执行
onMounted(() => {
  fetchAreaList()
})
</script>