import request from '@/utils/request'

export const categoryApi = {
    // 获取分类列表
    async getCategories(params = {}) {
        try {
            const response = await request({
                url: '/categories',
                method: 'get',
                params: {
                    type: params.type || null,
                    enabled: params.enabled || null
                }
            })
            return response
        } catch (error) {
            console.error('Failed to get categories:', error)
            throw error
        }
    },

    // 创建分类
    async createCategory(data) {
        try {
            const response = await request({
                url: '/categories',
                method: 'post',
                data: {
                    name: data.name,
                    type: data.type,
                    description: data.description || ''
                }
            })
            return response
        } catch (error) {
            console.error('Failed to create category:', error)
            throw error
        }
    },

    // 更新分类
    async updateCategory(id, data) {
        try {
            const response = await request({
                url: `/categories/${id}`,
                method: 'put',
                data: {
                    name: data.name,
                    type: data.type,
                    description: data.description,
                    isEnabled: data.isEnabled
                }
            })
            return response
        } catch (error) {
            console.error('Failed to update category:', error)
            throw error
        }
    },

    // 更新分类状态
    async updateCategoryStatus(id, isEnabled) {
        try {
            const response = await request({
                url: `/categories/${id}/status`,
                method: 'put',
                params: { isEnabled }
            })
            return response
        } catch (error) {
            console.error('Failed to update category status:', error)
            throw error
        }
    },

    // 删除分类
    async deleteCategory(id) {
        try {
            const response = await request({
                url: `/categories/${id}`,
                method: 'delete'
            })
            return response
        } catch (error) {
            console.error('Failed to delete category:', error)
            throw error
        }
    }
}