<template>
  <v-app>
    <v-container fill-height>
      <v-row justify="center" align="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="mx-auto pa-12 pb-8" rounded="lg">
            <!-- Username Field -->
            <v-text-field v-model="registrationDto.username" :rules="usernameRules" label="Username"
                          prepend-inner-icon="mdi-account-outline" variant="outlined" dense></v-text-field>

            <!-- Email Field -->
            <v-text-field v-model="registrationDto.email" :rules="emailRules" label="Email"
                          prepend-inner-icon="mdi-email-outline" variant="outlined" dense></v-text-field>

            <!-- Password Field -->
            <v-text-field v-model="registrationDto.password" :rules="passwordRules"
                          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'" :type="visible ? 'text' : 'password'"
                          label="Enter your password" prepend-inner-icon="mdi-lock-outline" variant="outlined" dense
                          @click:append-inner="visible = !visible"></v-text-field>

            <!-- Register Button -->
            <v-btn block class="mb-8" color="blue" size="large" variant="tonal" @click="register" :disabled="!isValid"
                   :loading="loading">
              Register
            </v-btn>

            <!-- Error Message -->
            <v-card-text v-if="errorMessage" class="text-center text-red">
              {{ errorMessage }}
            </v-card-text>

            <!-- Login Link -->
            <v-card-text class="text-center">
              <router-link class="text-blue text-decoration-none" to="/auth/login">
                Already have an account? Log in <v-icon icon="mdi-chevron-right"></v-icon>
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
      registrationDto: {
        username: '',
        email: '',
        password: ''
      },
      visible: false,
      loading: false,
      errorMessage: '',
      usernameRules: [v => !!v || 'Username is required'],
      emailRules: [v => !!v || 'Email is required', v => /.+@.+\..+/.test(v) || 'Email must be valid'],
      passwordRules: [v => !!v || 'Password is required'],
    };
  },
  computed: {
    isValid() {
      return this.registrationDto.username && this.registrationDto.email && this.registrationDto.password;
    }
  },
  methods: {
    register() {
      if (!this.isValid) return;
      this.loading = true;
      this.errorMessage = '';

      axios.post('http://localhost:8080/api/v1/users/register', this.registrationDto)
        .then(response => {
          this.loading = false;
          // Handle registration success
          // Redirect to login page or auto-login the user
          this.$router.push({ name: 'RegistrationSuccess' });
        })
        .catch(error => {
          this.loading = false;
          this.errorMessage = error.response.data.message || 'Registration failed';
        });
    }
  }
};
</script>
