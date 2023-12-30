import { createRouter, createWebHistory } from 'vue-router';
import { jwtDecode } from "jwt-decode";

import AppLayout from '@/layouts/AppLayout.vue';
import AuthLayout from '@/layouts/AuthLayout.vue';

import Home from '@/views/Home.vue';
import PublicImages from '@/views/posts/PublicImages.vue';
import PrivateImages from '@/views/posts/PrivateImages.vue';
import UploadImage from '@/views/posts/UploadImage.vue';
import Login from '@/views/auth/Login.vue';
import Register from '@/views/auth/Register.vue';
import RegistrationSuccess from '@/views/auth/RegistrationSuccess.vue';

function getUserRole() {
  const token = localStorage.getItem('user-token');
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    console.log('Decoded Token:', decoded);
    return decoded.role;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}

function isAuthenticated() {
  return !!localStorage.getItem('user-token');
}

const routes = [
  {
    path: '/',
    component: AppLayout,
    children: [
      { path: '', component: Home, name: 'Home' },
      { path: 'public-images', component: PublicImages, name: 'PublicImages' },
      {
        path: 'private-images',
        component: PrivateImages,
        name: 'PrivateImages',
        meta: { requiresAuth: true, roles: ['PRIVILEGED_USER', 'ADMINISTRATOR'] }
      },
      {
        path: 'upload-image',
        component: UploadImage,
        name: 'UploadImage',
        meta: { requiresAuth: true, roles: ['ADMINISTRATOR'] }
      },
    ]
  },
  {
    path: '/auth',
    component: AuthLayout,
    children: [
      {
        path: 'registration-success',
        component: RegistrationSuccess,
        name: 'RegistrationSuccess'
      },
      {
        path: 'login',
        component: Login,
        name: 'UserLogin',
        beforeEnter: (to, from, next) => {
          if (isAuthenticated()) {
            next({ name: 'Home' });
          } else {
            next();
          }
        }
      },
      {
        path: 'register',
        component: Register,
        name: 'UserRegister',
        beforeEnter: (to, from, next) => {
          if (isAuthenticated()) {
            next({ name: 'Home' });
          } else {
            next();
          }
        }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const userRole = getUserRole();
  const allowedRoles = to.meta.roles;

  console.log('UserRole:', userRole, 'AllowedRoles:', allowedRoles)

  if (requiresAuth && !userRole) {
    next({ name: 'UserLogin' });
  } else if (requiresAuth && allowedRoles && !allowedRoles.includes(userRole)) {
    next({ path: '/' });
  } else {
    next();
  }
});

export default router;
