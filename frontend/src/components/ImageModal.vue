<template>
  <v-dialog v-model="dialog" max-width="800px">
    <v-card>
      <!-- Image Title -->
      <v-card-title>{{ image.title }}</v-card-title>
      <v-divider></v-divider>

      <!-- Image Display -->
      <v-img :src="image.url" aspect-ratio="1.7"></v-img>

      <!-- Image Description -->
      <v-card-text>{{ image.description }}</v-card-text>

      <!-- Comments List -->
      <v-card-text class="comments-container">
        <div v-for="comment in comments" :key="comment.id" class="my-2">
          <v-card class="comment-card" variant="tonal">
            <!-- Comment Content -->
            <v-card-title class="d-flex justify-space-between">
              {{ comment.text }}
              <v-spacer></v-spacer>
              <!-- Comment Action Menu -->
              <v-menu offset-y>
                <template v-slot:activator="{ on, props }">
                  <v-btn icon v-bind="props" v-on="on" variant="flat">
                    <v-icon>mdi-dots-horizontal</v-icon>
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item @click="editComment(comment)">
                    <v-list-item-title>Edit</v-list-item-title>
                  </v-list-item>
                  <v-list-item @click="deleteComment(comment.id)">
                    <v-list-item-title>Delete</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </v-card-title>
            <v-card-subtitle>Posted by {{ comment.username }}</v-card-subtitle>
          </v-card>
        </div>
      </v-card-text>

      <!-- Comment Input Field (Fixed at Bottom) -->
      <v-card-actions class="comment-input">
        <v-textarea v-model="newComment" append-icon="mdi-send" placeholder="Add a comment..." rows="1" @click:append="addComment" outlined></v-textarea>
      </v-card-actions>

      <!-- Closing Actions -->
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="close">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>


<script>
import axios from 'axios';
import { ref } from 'vue';
import { jwtDecode } from 'jwt-decode';

export default {
  props: {
    image: {
      type: Object,
      required: true
    }
  },
  setup() {
    const dialog = ref(false);
    const newComment = ref('');
    const comments = ref([]);
    const userRole = ref(null);
    const userId = ref(null);

    const token = localStorage.getItem('user-token');
    if (token) {
      const decoded = jwtDecode(token);
      userRole.value = decoded.role;
      userId.value = decoded.sub;
    }

    const open = () => {
      dialog.value = true;
      fetchComments();
    };

    const close = () => {
      dialog.value = false;
    };

    const fetchComments = () => {
      axios.get(`/api/v1/public/images/${this.image.id}/comments`, authHeaders)
        .then(response => {
          comments.value = response.data;
        })
        .catch(error => console.error('Error fetching comments:', error));
    };

    const canEditOrDelete = (comment) => {
      return userRole.value === 'ADMINISTRATOR' || userId.value === comment.userId;
    };

    const addComment = () => {
      if (!userRole.value) return; // Only authenticated users can add comments

      axios.post(`/api/v1/public/images/${this.image.id}/comments`, { text: newComment.value }, authHeaders)
        .then(() => {
          newComment.value = '';
          fetchComments();
        })
        .catch(error => console.error('Error adding comment:', error));
    };

    return { dialog, newComment, comments, open, close, fetchComments, addComment, canEditOrDelete, editComment, deleteComment };
  }
};
</script>


<style>
.comments-container {
  max-height: 300px;
  overflow-y: auto;
}

.comment-input {
  position: absolute;
  bottom: 0;
  width: 100%;
  background-color: white;
}

.v-dialog {
  overflow: hidden;
}
</style>
