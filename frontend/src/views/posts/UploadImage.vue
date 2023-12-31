<template>
  <v-container>
    <v-col cols="9" offset="1">
      <h1>Upload image</h1>
      <v-divider class="border-opacity-50 pa-2"></v-divider>
      <v-form @submit.prevent="uploadImage">
        <v-file-input v-model="imageFile" label="Choose an Image" accept="image/*"
                      prepend-icon="mdi-camera" variant="outlined" color="primary" @change="handleFileChange" />

        <v-text-field v-model="title" label="Title*" variant="outlined" color="primary" required />

        <v-textarea v-model="description" label="Description" variant="outlined" color="primary" rows="2" />

        <v-switch v-model="isPublic" label="Public" color="success"></v-switch>

        <v-btn type="submit" size="large"  prepend-icon="mdi-tray-arrow-up" variant="flat" block>Upload</v-btn>
      </v-form>
    </v-col>
  </v-container>
  <v-snackbar
    v-model="snackbar"
    :color="snackbarColor"
    top
    right
  >
    {{ snackbarText }}
    <v-btn color="white" text @click="snackbar = false">Close</v-btn>
  </v-snackbar>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      imageFile: null,
      title: '',
      description: '',
      isPublic: false,
      snackbar: false,
      snackbarText: '',
      snackbarColor: '',
    };
  },
  methods: {
    handleFileChange(event) {
      if (event.target.files.length > 0) {
        this.imageFile = event.target.files[0];
      }
    },
    uploadImage() {

      //const token = localStorage.getItem('user-token');

      if (!this.imageFile) {
        this.showSnackbar('Please select a file to upload.', 'red');
        return;
      }

      const formData = new FormData();
      formData.append('file', this.imageFile);
      formData.append('title', this.title);
      formData.append('description', this.description);
      formData.append('isPublic', this.isPublic);

      for (let [key, value] of formData.entries()) {
        console.log(key, value);
      }

      axios({
        method: 'post',
        url: 'http://localhost:8080/api/v1/images',
        data: formData,
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('user-token')}`
        }
      })
        .then(response => {
          this.showSnackbar('Image uploaded successfully', 'green');
          console.log('Image uploaded successfully', response);
          // Handle successful response
        })
        .catch(error => {
          this.showSnackbar('Error uploading image', 'red');
          console.error('Error uploading image', error);
          // Handle error
        });
    },
    showSnackbar(message, color) {
      this.snackbarText = message;
      this.snackbarColor = color;
      this.snackbar = true;
    }
  }
}
</script>
