import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api'
})

export const userApi = {
    getUsers() {
        return api.get('/users')
    },

    getUser(id) {
        return api.get(`/users/${id}`)
    },

    createUser(user) {
        return api.post('/users', user)
    },

    updateUser(id, user) {
        return api.put(`/users/${id}`, user)
    },

    deleteUser(id) {
        return api.delete(`/users/${id}`)
    }
}