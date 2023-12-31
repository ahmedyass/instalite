<template>
  <div class="text-center">
    <v-menu open-on-hover>
      <template v-slot:activator="{ on, props }">
        <v-btn icon v-bind="props" v-on="on" variant="flat">
          <v-icon>mdi-dots-vertical</v-icon>
        </v-btn>
      </template>

      <v-list>
        <v-list-item v-if="getUserRole() === 'ADMINISTRATOR'" @click="showEditModal = true" rounded="lg">
          <template v-slot:prepend>
            <v-icon>mdi-pencil-outline</v-icon>
          </template>
          <v-list-item-title> Edit</v-list-item-title>
        </v-list-item>
        <v-list-item v-if="getUserRole() === 'ADMINISTRATOR'" @click="confirmDelete = true" rounded="lg">
          <template v-slot:prepend>
            <v-icon>mdi-delete-outline</v-icon>
          </template>
          <v-list-item-title>Delete</v-list-item-title>
        </v-list-item>
        <v-list-item @click="downloadImage" rounded="lg">
          <template v-slot:prepend>
            <v-icon>mdi-tray-arrow-down</v-icon>
          </template>
          <v-list-item-title>Download</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>

    <!-- Edit Modal -->
    <v-dialog v-model="showEditModal">
      <v-card>
        <v-card-title>Edit Image</v-card-title>
        <v-card-text>
          <v-text-field v-model="editedImage.title" label="Title" variant="outlined" color="primary" required />
          <v-textarea v-model="editedImage.description" label="Description" variant="outlined" color="primary" rows="2" />
          <v-switch v-model="editedImage.isPublic" label="Public" color="success" />
        </v-card-text>
        <v-card-actions>
          <v-btn @click="showEditModal = false">Cancel</v-btn>
          <v-btn @click="updateImage">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Confirmation Dialog -->
    <v-dialog v-model="confirmDelete">
      <v-card>
        <v-card-title>Confirm Deletion</v-card-title>
        <v-card-text>Are you sure you want to delete this image?</v-card-text>
        <v-card-actions>
          <v-btn @click="confirmDelete = false">Cancel</v-btn>
          <v-btn @click="deleteImage">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar for Notifications -->
    <v-snackbar v-model="snackbar" :color="snackbarColor">
      {{ snackbarText }}
      <v-btn color="white" text @click="snackbar = false">Close</v-btn>
    </v-snackbar>
  </div>
</template>

<script>
import axios from 'axios';
import { ref } from 'vue';
import {jwtDecode} from "jwt-decode";

export default {
  props: {
    imageId: Number,
    initialImage: Object
  },
  methods: {
    getUserRole() {
      const token = localStorage.getItem('user-token');
      if (!token) return null;
      try {
        const decoded = jwtDecode(token);
        return decoded.role;
      } catch (error) {
        return null;
      }
    },
  },
  setup(props) {
    const showEditModal = ref(false);
    const confirmDelete = ref(false);
    const editedImage = ref({ ...props.initialImage });
    const snackbar = ref(false);
    const snackbarText = ref('');
    const snackbarColor = ref('');
    const token = localStorage.getItem('user-token');

    const updateImage = () => {
      axios.put(`http://localhost:8080/api/v1/images/${props.imageId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
        .then(() => {
          showEditModal.value = false;
          showSnackbar('Image updated successfully', 'success');
        })
        .catch(error => {
          console.error('Error updating image', error);
          showSnackbar('Failed to update image', 'error');
        });
    };

    const deleteImage = () => {
      axios.delete(`http://localhost:8080/api/v1/images/${props.imageId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
        .then(() => {
          confirmDelete.value = false;
          showSnackbar('Image deleted successfully', 'success');
        })
        .catch(error => {
          console.error('Error deleting image', error);
          showSnackbar('Failed to delete image', 'error');
        });
    };

    const downloadImage = () => {
      axios.get(`http://localhost:8080/api/v1/private/images/${props.imageId}`)
        .then(response => {
          const url = response.data.url;
          window.open(url, '_blank');
        })
        .catch(error => console.error('Error fetching image URL', error));
    };

    const showSnackbar = (message, color) => {
      snackbarText.value = message;
      snackbarColor.value = color;
      snackbar.value = true;
    };

    return { showEditModal, confirmDelete, editedImage, updateImage, deleteImage, downloadImage, snackbar, snackbarText, snackbarColor, showSnackbar };
  }
};
</script>

