import { createWebHistory, createRouter } from 'vue-router';
import HelloWorld from './components/HelloWorld.vue';
import LoginForm from './components/auth/LoginForm.vue';
import LogoutForm from './components/auth/LogoutForm.vue';
import RegisterForm from './components/auth/RegisterForm.vue';

const routes = [
  { path: '/', component: HelloWorld },
  { path: '/login', component: LoginForm },
  { path: '/logout', component: LogoutForm },
  { path: '/register', component: RegisterForm },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
