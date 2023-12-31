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
        <v-list>
          <v-list-item v-for="comment in comments" :key="comment.id">
            <v-list-item-content>
              <v-list-item-title>{{ comment.text }}</v-list-item-title>
              <v-list-item-subtitle>Posted by {{ comment.username }}</v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-action>
              <!-- Comment Action Menu -->
              <v-menu offset-y>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon>mdi-dots-vertical</v-icon>
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
            </v-list-item-action>
          </v-list-item>
        </v-list>
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
import { jwtDecode } from 'jwt-decode';

export default {
  props: {
    image: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      dialog: false,
      newComment: '',
      comments: [] // Store fetched comments
    };
  },
  methods: {
    open() {
      this.dialog = true;
      this.fetchComments(); // Fetch comments when the modal is opened
    },
    close() {
      this.dialog = false;
    },
    fetchComments() {
      // Fetch comments from the backend
      // axios.get(`/api/v1/public/images/${this.image.id}/comments`)
      //   .then(response => {
      //     this.comments = response.data;
      //   });
    },
    addComment() {
      // Post the new comment to the backend
      // axios.post(`/api/v1/public/images/${this.image.id}/comments`, { text: this.newComment })
      //   .then(() => {
      //     this.newComment = '';
      //     this.fetchComments(); // Refresh comments
      //   });
    },
    editComment(comment) {
      // Handle comment editing
      // Show a dialog or another component to edit the comment
    },
    deleteComment(commentId) {
      // Delete the comment from the backend
      // axios.delete(`/api/v1/public/images/${this.image.id}/comments/${commentId}`)
      //   .then(() => {
      //     this.fetchComments(); // Refresh comments
      //   });
    }
  }
};
</script>

<style>
.comments-container {
  max-height: 300px; /* Adjust the height as needed */
  overflow-y: auto; /* Make the comment list scrollable */
}

.comment-input {
  position: absolute;
  bottom: 0;
  width: 100%;
  background-color: white;
}

.v-dialog {
  overflow: hidden; /* Prevent scrolling inside the entire dialog */
}
</style>
