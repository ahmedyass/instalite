<template>
  <v-app>
    <v-container fill-height>
      <v-row justify="center" align="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="mx-auto pa-12 pb-8" rounded="lg">
            <!-- Username Field -->
            <v-text-field v-model="loginDto.username" :rules="usernameRules" label="Username"
                          prepend-inner-icon="mdi-account-outline" variant="outlined" dense></v-text-field>

            <!-- Password Field -->
            <v-text-field v-model="loginDto.password" :rules="passwordRules"
                          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'" :type="visible ? 'text' : 'password'"
                          label="Enter your password" prepend-inner-icon="mdi-lock-outline" variant="outlined" dense
                          @click:append-inner="visible = !visible"></v-text-field>

            <!-- Login Button -->
            <v-btn block class="mb-8" color="blue" size="large" variant="tonal" @click="login" :disabled="!isValid"
                   :loading="loading">
              Log In
            </v-btn>

            <!-- Error Message -->
            <v-card-text v-if="errorMessage" class="text-center text-red">
              {{ errorMessage }}
            </v-card-text>

            <!-- Sign Up Link -->
            <v-card-text class="text-center">
              <router-link class="text-blue text-decoration-none" to="/auth/register">
                Sign up now <v-icon icon="mdi-chevron-right"></v-icon>
              </router-link>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-app>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      loginDto: {
        username: '',
        password: ''
      },
      visible: false,
      loading: false,
      errorMessage: '',
      usernameRules: [
        v => !!v || 'Username is required',
      ],
      passwordRules: [
        v => !!v || 'Password is required',
      ],
    };
  },
  computed: {
    isValid() {
      return this.loginDto.username && this.loginDto.password;
    }
  },
  methods: {
    login() {
      if (!this.isValid) return;
      this.loading = true;
      this.errorMessage = '';

      axios.post('http://localhost:8080/api/v1/users/login', this.loginDto)
        .then(response => {
          this.loading = false;
          const token = response.data.jwt;
          localStorage.setItem('user-token', token);
          this.$router.push({ name: 'Home' });
        })
        .catch(error => {
          this.loading = false;
          this.errorMessage = error.response.data.message || 'Login failed';
        });
    }
  }
};
</script>
