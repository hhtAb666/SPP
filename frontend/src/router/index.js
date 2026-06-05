/**
 * Vue Router 路由配置文件
 */
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/admin',
    component: () => import('../layout/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: '',
        redirect: 'venues'
      },
      {
        path: 'users',
        name: 'User Management',
        component: () => import('../views/admin/user-list.vue')
      },
      {
        path: 'venues',
        name: 'Venue Management',
        component: () => import('../views/admin/venue-list.vue')
      },
      {
        path: 'schedules',
        name: 'Schedule Management',
        component: () => import('../views/admin/schedule.vue')
      },
      {
        path: 'reservations',
        name: 'Reservation Audit',
        component: () => import('../views/admin/reservations.vue')
      },
      {
        path: 'logs',
        name: 'System Logs',
        component: () => import('../views/admin/sys-log.vue')
      }
    ]
  },
  {
    path: '/user',
    component: () => import('../layout/UserLayout.vue'),
    meta: { requiresAuth: true, role: 'USER' },
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('../views/user/dashboard.vue')
      },
      {
        path: 'venues',
        name: 'VenueList',
        component: () => import('../views/user/venue-list.vue')
      },
      {
        path: 'reservations',
        name: 'MyReservations',
        component: () => import('../views/user/my-reservations.vue')
      },
      {
        path: 'health',
        name: 'HealthRecord',
        component: () => import('../views/user/health-record.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
