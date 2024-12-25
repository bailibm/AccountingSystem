import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json'
    }
})

request.interceptors.request.use(
    config => {
        return config
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        console.error('请求错误:', error)
        ElMessage.error(error.message || '请求失败')
        return Promise.reject(error)
    }
)

export default request