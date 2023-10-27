import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'index',
            component: () => import('../views/Index.vue'),
            redirect: '/home',
            children: [
                {
                    path: '/home',
                    name: 'home',
                    component: () => import('../views/Home.vue')
                },
                {
                    path: '/users',
                    name: 'users',
                    component: () => import('../views/Users.vue')
                },
                {
                    path: '/tasks',
                    name: 'tasks',
                    component: () => import('../views/Tasks.vue')
                },
                {
                    path: '/records',
                    name: 'records',
                    component: () => import('../views/Records.vue')
                },
            ]
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue'),
        }
    ]
})

export default router
