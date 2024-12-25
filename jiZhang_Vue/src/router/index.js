import { createRouter, createWebHistory } from 'vue-router'
import RecordList from '../views/record/RecordList.vue'
import CategoryList from '../views/category/CategoryList.vue'

const routes = [{
        path: '/',
        redirect: '/records'
    },
    {
        path: '/categories',
        name: 'Categories',
        component: CategoryList,
        meta: { title: '分类管理' }
    },
    {
        path: '/records',
        name: 'Records',
        component: RecordList,
        meta: { title: '记账记录' }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/records'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title || '记账管理系统'
    next()
})

export default router