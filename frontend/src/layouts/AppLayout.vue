<template>
  <v-app>
    <v-app-bar v-if="windowWidth <= 1024" app>
      <v-app-bar-nav-icon @click="drawer = !drawer" variant="flat"></v-app-bar-nav-icon>
      <v-toolbar-title>Instalite</v-toolbar-title>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer" class="sidebar" app>
      <v-list-item title="Instalite"></v-list-item>
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
  </v-app>
</template>

<script>
import { jwtDecode } from 'jwt-decode';

export default {
  data: () => ({
    drawer: true,
    windowWidth: window.innerWidth,
    menuItems: [
      { title: 'Home', icon: 'mdi-home', to: '/' },
      { title: 'Public Images', icon: 'mdi-account-group-outline', to: '/public-images' },
      { title: 'Private Images', icon: 'mdi-account', to: '/private-images', roles: ['PRIVILEGED_USER', 'ADMINISTRATOR'] },
      { title: 'Upload Image', icon: 'mdi-upload', to: '/upload-image', roles: ['ADMINISTRATOR'] },
    ]
  }),
  mounted() {
    window.addEventListener('resize', this.onResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize);
  },
  computed: {
    filteredMenuItems() {
      const userRole = this.getUserRole();

      if (!userRole || userRole === 'USER') {
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
      localStorage.removeItem('user-token');
      this.$router.push({ name: 'Home' });
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
