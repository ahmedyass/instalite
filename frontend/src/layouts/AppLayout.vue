<template>
  <v-app-bar v-if="windowWidth <= 1024" app>
    <v-app-bar-nav-icon @click="drawer = !drawer" variant="flat"></v-app-bar-nav-icon>
    <v-toolbar-title>Instalite</v-toolbar-title>
  </v-app-bar>
  <v-navigation-drawer v-model="drawer" class="sidebar" app>
    <v-list-item>
      <v-img :width="200"
             aspect-ratio="16/9"
             src="@/assets/logo.png"
      ></v-img>
    </v-list-item>
    <v-divider></v-divider>
    <v-list density="compact" nav>
      <v-list-item v-for="item in filteredMenuItems" :key="item.title" :to="item.to" router
                   :prepend-icon="item.icon" rounded="lg">
        {{ item.title }}
      </v-list-item>
    </v-list>
    <template v-slot:append>
      <div class="pa-2">
        <v-btn @click="buttonAction" block :prepend-icon="buttonIcon" rounded="xl" variant="tonal">
          {{ buttonText }}
        </v-btn>
      </div>
    </template>
  </v-navigation-drawer>
  <v-main>
    <router-view></router-view>
  </v-main>
</template>

<script>
import { jwtDecode } from 'jwt-decode';
import axios from "axios";
import {watch} from "vue";

export default {
  data: () => ({
    drawer: true,
    windowWidth: window.innerWidth,
    menuItems: [
      { title: 'Home', icon: 'mdi-home', to: '/' },
      { title: 'Public Images', icon: 'mdi-image-multiple-outline', to: '/public-images' },
      { title: 'Private Images', icon: 'mdi-image-lock-outline', to: '/private-images', roles: ['PRIVILEGED_USER', 'ADMINISTRATOR'] },
      { title: 'Upload Image', icon: 'mdi-upload', to: '/upload-image', roles: ['ADMINISTRATOR'] },
      { title: 'Users', icon: 'mdi-account-group-outline', to: '/users', roles: ['ADMINISTRATOR'] },
      { title: 'UserProfile', icon: 'mdi-account-lock-outline', to: '/user-profile', roles: ['USER', 'PRIVILEGED_USER', 'ADMINISTRATOR']},
    ]
  }),
  mounted() {
    window.addEventListener('resize', this.onResize);
    watch(this.isAuthenticated, (newVal) => {
      this.updateMenuItems();
    });
    this.updateMenuItems();
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize);
  },
  computed: {
    filteredMenuItems() {
      const userRole = this.getUserRole();

      if (!userRole) {
        return this.menuItems.filter(item => !item.roles);
      }

      return this.menuItems.filter(item => {
        if (item.roles) {
          return item.roles.includes(userRole);
        }
        return true;
      });
    },
    buttonText() {
      return this.isAuthenticated() ? 'Logout' : 'Login';
    },
    buttonIcon() {
      return this.isAuthenticated() ? 'mdi-logout' : 'mdi-login';
    }
  },
  methods: {
    onResize() {
      this.windowWidth = window.innerWidth;
    },
    updateMenuItems() {
      const userRole = this.getUserRole();
      if (!userRole) {
        return this.menuItems.filter(item => !item.roles);
      }
      return this.menuItems.filter(item => {
        if (item.roles) {
          return item.roles.includes(userRole);
        }
        return true;
      });
    },
    isAuthenticated() {
      return !!localStorage.getItem('user-token');
    },
    buttonAction() {
      if (this.isAuthenticated()) {
        this.logout();
      } else {
        this.login();
      }
    },
    login() {
      this.$router.push({ name: 'UserLogin' });
    },
    logout() {
      const token = localStorage.getItem('user-token');
      if (!token) {
        this.$router.push({ name: 'Home' });
        return;
      }

      axios.post('http://localhost:8080/api/v1/logout', {}, {
        headers: { 'Authorization': `Bearer ${token}` }
      }).then(() => {
        localStorage.removeItem('user-token');
        this.$router.push({ name: 'Home' });
      }).catch((error) => {
        console.error('Logout error:', error);
        this.$router.push({ name: 'Home' });
      });
    },
    getUserRole() {
      try {
        const token = localStorage.getItem('user-token');
        if (!token) return null;
        const decoded = jwtDecode(token);
        return decoded.role;
      } catch (error) {
        return null;
      }
    }
  }
};
</script>


<style>
.sidebar {
  background-color: #EFEFEF;
  border-right: 1px solid #C4C4C4;
}
</style>
