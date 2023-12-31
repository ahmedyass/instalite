<template>
  <v-container>
    <v-card>
      <v-card-title>User Profile</v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          <v-text-field
            label="Username"
            v-model="user.username"
            :rules="[rules.required]"
            required
          ></v-text-field>
          <v-text-field
            label="Email"
            v-model="user.email"
            :rules="[rules.required, rules.email]"
            required
          ></v-text-field>
          <v-text-field
            label="New Password"
            v-model="user.password"
            :rules="[rules.required]"
            type="password"
            required
          ></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="updateProfile">Update Profile</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>

  <!-- Snackbar -->
  <v-snackbar v-model="snackbar.show" :color="snackbar.color">
    {{ snackbar.text }}
    <v-btn color="white" text @click="snackbar.show = false">Close</v-btn>
  </v-snackbar>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { ref } from 'vue';

export default {
  data() {
    return {
      valid: false,
      user: {
        username: '',
        email: '',
        password: ''
      },
      rules: {
        required: value => !!value || 'Required.',
        email: value => /.+@.+\..+/.test(value) || 'E-mail must be valid.',
      },
      snackbar: {
        show: false,
        text: '',
        color: ''
      }
    };
  },
  created() {
    this.fetchUserProfile();
  },
  methods: {
    fetchUserProfile() {
      const token = localStorage.getItem('user-token');
      if (!token) return null;
      const decoded = jwtDecode(token);
      const username = decoded.sub;
      axios.get(`http://localhost:8080/api/v1/users/${username}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
        .then(response => {
          this.user = response.data;
        })
        .catch(error => {
          console.error('Error fetching user profile:', error);
          this.snackbar = { show: true, text: 'Failed to fetch user profile', color: 'error' };
        });
    },
    updateProfile() {
      if (this.$refs.form.validate()) {
        const token = localStorage.getItem('user-token');
        if (!token) return null;
        axios.put(`http://localhost:8080/api/v1/users/${this.user.id}`, this.user, {
          headers: { 'Authorization': `Bearer ${token}` }
        })
          .then(() => {
            console.log('Profile updated successfully');
            this.snackbar = { show: true, text: 'Profile updated successfully', color: 'success' };
          })
          .catch(error => {
            console.error('Error updating profile:', error);
            this.snackbar = { show: true, text: 'Failed to update profile', color: 'error' };
          });
      }
    }
  }
};
</script>
