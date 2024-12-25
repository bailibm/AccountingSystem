import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export function useRecordList() {
    const records = ref([])
    const loading = ref(false)

    const api = axios.create({
        baseURL: 'http://localhost:8081/api'
    })

    // 加载记录列表
    const loadRecords = async() => {
        loading.value = true
        try {
            const { data } = await api.get('/records')
            records.value = data
            console.log('Records loaded:', data)
        } catch (error) {
            console.error('Failed to load records:', error)
            ElMessage.error('获取记录失败')
        } finally {
            loading.value = false
        }
    }

    // 添加测试数据
    const addTestData = async() => {
        try {
            await api.post('/records/test-data')
            ElMessage.success('添加测试数据成功')
            loadRecords()
        } catch (error) {
            console.error('Failed to add test data:', error)
            ElMessage.error('添加测试数据失败')
        }
    }

    // 初始化
    onMounted(() => {
        loadRecords()
    })

    return {
        records,
        loading,
        loadRecords,
        addTestData
    }
}