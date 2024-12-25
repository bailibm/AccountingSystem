import request from '@/utils/request'

export const recordApi = {
    // 获取记录列表
    async getRecords(params = {}) {
        try {
            const response = await request({
                url: '/records',
                method: 'get',
                params: {
                    page: params.page || 1,
                    size: params.size || 10,
                    type: params.type || null,
                    categoryId: params.categoryId || null,
                    startDate: params.startDate ? new Date(params.startDate).toISOString() : null,
                    endDate: params.endDate ? new Date(params.endDate).toISOString() : null
                }
            })
            return response
        } catch (error) {
            console.error('Failed to get records:', error)
            throw error
        }
    },

    // 获取统计数据
    async getStatistics(params = {}) {
        try {
            const response = await request({
                url: '/records/statistics',
                method: 'get',
                params: {
                    year: params.year || new Date().getFullYear(),
                    month: params.month || new Date().getMonth() + 1
                }
            })
            return response
        } catch (error) {
            console.error('Failed to get statistics:', error)
            throw error
        }
    },

    // 创建记录
    async createRecord(data) {
        try {
            const response = await request({
                url: '/records',
                method: 'post',
                data: {
                    type: data.type,
                    amount: data.amount,
                    categoryId: data.categoryId,
                    date: data.date ? new Date(data.date).toISOString() : new Date().toISOString(),
                    description: data.description || ''
                }
            })
            return response
        } catch (error) {
            console.error('Failed to create record:', error)
            throw error
        }
    },

    // 更新记录
    async updateRecord(id, data) {
        try {
            const response = await request({
                url: `/records/${id}`,
                method: 'put',
                data: {
                    type: data.type,
                    amount: data.amount,
                    categoryId: data.categoryId,
                    date: data.date ? new Date(data.date).toISOString() : new Date().toISOString(),
                    description: data.description || ''
                }
            })
            return response
        } catch (error) {
            console.error('Failed to update record:', error)
            throw error
        }
    },

    // 删除记录
    async deleteRecord(id) {
        try {
            const response = await request({
                url: `/records/${id}`,
                method: 'delete'
            })
            return response
        } catch (error) {
            console.error('Failed to delete record:', error)
            throw error
        }
    }
}