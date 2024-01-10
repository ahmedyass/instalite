<template>
  <v-container>
    <v-row justify="center" align="center">
      <v-col cols="12" sm="8" md="6">
        <v-card class="pa-5" elevation="2">
          <div class="text-h5 mb-3">Registration Successful!</div>
          <div class="mb-3">
            Thank you for registering. You will be redirected to the login page shortly.
          </div>
          <v-btn to="/auth/login" variant="flat" text>Go to Login Page</v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  mounted() {
    setTimeout(() => {
      this.$router.push({ name: 'UserLogin' });
    }, 10000); // 10 seconds
  }
};
</script>
