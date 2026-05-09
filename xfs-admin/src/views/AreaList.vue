<template>
  <div class="modern-page-container">
    <div class="white-card table-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <h2 class="page-title">景区资源管理</h2>
          <p class="page-subtitle">管理雪峰山大景区及其内部核心景点信息</p>
        </div>
        <div class="header-right">
          <el-button type="primary" class="modern-btn" :icon="Plus" @click="openAreaDialog()">新增大景区</el-button>
          <el-button class="modern-btn secondary" :icon="Refresh" @click="fetchAreaList" :loading="loading">刷新列表</el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" class="modern-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="景区名称" width="180">
          <template #default="scope">
            <span class="area-name-text">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="100" align="center">
          <template #default="scope">
            <div class="level-badge" :class="scope.row.level ? scope.row.level.toLowerCase() : ''">{{ scope.row.level }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地理位置" width="150" />
        <el-table-column prop="tel" label="咨询电话" width="150" />
        <el-table-column label="操作" min-width="250" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="openAreaDialog(scope.row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button type="warning" link @click="manageSpots(scope.row)">景点管理</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定删除该景区吗？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDeleteArea(scope.row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 1. 新增/编辑大景区 弹窗 -->
    <el-dialog v-model="areaDialogVisible" :title="areaForm.id ? '编辑景区' : '新增大景区'" width="600px" border-radius="20px">
      <el-form ref="areaFormRef" :model="areaForm" :rules="areaRules" label-width="100px" class="modern-form">
        <el-form-item label="景区名称" prop="name">
          <el-input v-model="areaForm.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="景区等级" prop="level">
          <el-select v-model="areaForm.level" placeholder="请选择">
            <el-option label="5A" value="AAAAA" />
            <el-option label="4A" value="AAAA" />
            <el-option label="3A" value="AAA" />
          </el-select>
        </el-form-item>
        <el-form-item label="地理位置" prop="address">
          <el-input v-model="areaForm.address" placeholder="例如：怀化市溆浦县" />
        </el-form-item>
        <el-form-item label="咨询电话">
          <el-input v-model="areaForm.tel" />
        </el-form-item>
        <el-form-item label="景区简介" prop="intro">
          <el-input v-model="areaForm.intro" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item label="封面图URL">
          <el-input v-model="areaForm.coverImg" placeholder="请输入图片在线地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="areaDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveArea" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <!-- 2. 子景点管理 抽屉 -->
    <el-drawer v-model="spotDrawerVisible" :title="`【${currentArea.name}】景点详情管理`" size="750px">
      <div class="spot-manager-content">
        <div class="action-bar">
          <el-button type="primary" size="small" @click="openSpotDialog()">添加子景点</el-button>
        </div>
        
        <el-table :data="spotList" v-loading="spotLoading" class="modern-table">
          <el-table-column prop="name" label="景点名" width="120" />
          <el-table-column prop="price" label="价格" width="80">
            <template #default="scope">¥{{ scope.row.price }}</template>
          </el-table-column>
          <el-table-column prop="openTime" label="开放时间" width="120" />
          <el-table-column prop="intro" label="介绍" show-overflow-tooltip />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="openSpotDialog(scope.row)">编辑</el-button>
              <el-popconfirm title="确定删除该景点？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDeleteSpot(scope.row.id)">
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>

    <!-- 3. 新增/编辑子景点 弹窗 -->
    <el-dialog v-model="spotDialogVisible" title="子景点编辑" width="550px" append-to-body>
      <el-form ref="spotFormRef" :model="spotForm" :rules="spotRules" label-width="100px" class="modern-form">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="spotForm.name" placeholder="请输入子景点名称" />
        </el-form-item>
        <el-form-item label="门票价格" prop="price">
          <el-input-number v-model="spotForm.price" :precision="2" :step="1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开放时间" prop="openTime">
          <el-input v-model="spotForm.openTime" placeholder="如：08:00 - 18:00" />
        </el-form-item>
        <el-form-item label="景点简介" prop="intro">
          <el-input v-model="spotForm.intro" type="textarea" rows="3" placeholder="简要描述景点的特色" />
        </el-form-item>
        <el-form-item label="景点图片URL">
          <el-input v-model="spotForm.spotImg" placeholder="请输入图片在线地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="spotDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSpot" class="modern-btn">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

// --- 大景区相关 ---
const tableData = ref([])
const loading = ref(false)
const areaDialogVisible = ref(false)
const submitting = ref(false)
const areaFormRef = ref(null)
const spotFormRef = ref(null)
const areaForm = ref({ id: null, name: '', level: '', address: '', tel: '', intro: '', coverImg: '' })
const areaRules = {
  name: [{ required: true, message: '请输入景区名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择景区等级', trigger: 'change' }],
  address: [{ required: true, message: '请输入地理位置', trigger: 'blur' }],
  intro: [{ required: true, message: '请输入景区简介', trigger: 'blur' }]
}
const spotRules = {
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  price: [{ required: true, message: '请设置门票价格', trigger: 'change' }],
  openTime: [{ required: true, message: '请输入开放时间', trigger: 'blur' }],
  intro: [{ required: true, message: '请输入景点简介', trigger: 'blur' }]
}

const fetchAreaList = async () => {
  loading.value = true
  try {
    const res = await request.get('/area/list')
    tableData.value = res.data
  } catch (e) { ElMessage.error('获取列表失败') }
  finally { loading.value = false }
}

const openAreaDialog = (row = null) => {
  if (row) areaForm.value = { ...row }
  else areaForm.value = { id: null, name: '', level: 'AAAA', address: '', tel: '', intro: '', coverImg: '' }
  areaDialogVisible.value = true
}

const handleSaveArea = async () => {
  if (!areaFormRef.value) return
  try {
    await areaFormRef.value.validate()
  } catch { return }
  submitting.value = true
  try {
    await request.post('/area/save', areaForm.value)
    ElMessage.success('保存成功')
    areaDialogVisible.value = false
    fetchAreaList()
  } catch (e) { ElMessage.error('保存失败') }
  finally { submitting.value = false }
}

const handleDeleteArea = async (id) => {
  try {
    await request.delete(`/area/${id}`)
    ElMessage.success('删除成功')
    fetchAreaList()
  } catch (e) { ElMessage.error('删除失败') }
}

// --- 子景点相关 ---
const spotDrawerVisible = ref(false)
const spotDialogVisible = ref(false)
const spotLoading = ref(false)
const currentArea = ref({})
const spotList = ref([])
const spotForm = ref({ id: null, areaId: null, name: '', intro: '', spotImg: '', price: 0, openTime: '' })

const manageSpots = async (area) => {
  currentArea.value = area
  spotDrawerVisible.value = true
  fetchSpotList()
}

const fetchSpotList = async () => {
  spotLoading.value = true
  try {
    const res = await request.get(`/area/${currentArea.value.id}/spots`)
    spotList.value = res.data
  } catch (e) { ElMessage.error('获取景点列表失败') }
  finally { spotLoading.value = false }
}

const openSpotDialog = (row = null) => {
  if (row) spotForm.value = { ...row }
  else spotForm.value = { id: null, areaId: currentArea.value.id, name: '', intro: '', spotImg: '', price: 0, openTime: '08:00-17:30' }
  spotDialogVisible.value = true
}

const handleSaveSpot = async () => {
  if (!spotFormRef.value) return
  try {
    await spotFormRef.value.validate()
  } catch { return }
  try {
    await request.post('/area/spot/save', spotForm.value)
    ElMessage.success('保存成功')
    spotDialogVisible.value = false
    fetchSpotList()
  } catch (e) { ElMessage.error('保存失败') }
}

const handleDeleteSpot = async (id) => {
  try {
    await request.delete(`/area/spot/${id}`)
    ElMessage.success('已删除')
    fetchSpotList()
  } catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => {
  fetchAreaList()
})
</script>

<style scoped>
.modern-page-container { padding: 0 4px; }
.white-card {
  background: #ffffff; border-radius: 20px; padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03); border: 1px solid rgba(0,0,0,0.02);
}
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.page-title { font-size: 22px; font-weight: 800; color: #1e293b; margin: 0; }
.page-subtitle { font-size: 14px; color: #94a3b8; margin: 4px 0 0 0; }
.modern-btn { border-radius: 10px; font-weight: 600; }
.modern-btn.secondary { background: #f1f5f9; border: none; color: #64748b; }

.level-badge {
  display: inline-block; padding: 4px 12px; border-radius: 8px;
  font-size: 12px; font-weight: 700;
}
.level-badge.aaaaa { background: #ecfdf5; color: #10b981; }
.level-badge.aaaa { background: #eff6ff; color: #3b82f6; }
.level-badge.aaa { background: #fff7ed; color: #ea580c; }

.area-name-text { font-weight: 600; color: #1e293b; }
.action-bar { margin-bottom: 16px; text-align: right; }
.spot-manager-content { padding: 20px; }
</style>
