import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export function useCategoryList() {
    const categories = ref([])
    const loading = ref(false)

    const api = axios.create({
        baseURL: 'http://localhost:8081/api'
    })

    // 加载分类列表
    const loadCategories = async() => {
        loading.value = true
        try {
            const { data } = await api.get('/categories')
            categories.value = data
            console.log('Categories loaded:', data)
        } catch (error) {
            console.error('Failed to load categories:', error)
            ElMessage.error('获取分类列表失败')
        } finally {
            loading.value = false
        }
    }

    // 初始化
    onMounted(() => {
        loadCategories()
    })

    return {
        categories,
        loading,
        loadCategories
    }
}