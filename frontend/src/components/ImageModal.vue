<template>
  <v-dialog v-model="dialog" max-width="800px">
    <v-card>
      <!-- Image Title -->
      <v-card-title>{{ image.title }}</v-card-title>
      <v-divider></v-divider>

      <!-- Image Display -->
      <v-img :src="image.url" aspect-ratio="1.7"></v-img>
      <v-divider></v-divider>
      <!-- Image Description -->
      <v-card-text>{{ image.description }}</v-card-text>
      <v-divider></v-divider>
      <!-- Comments List -->
      <v-card-text class="comments-container">
        <div v-for="comment in comments" :key="comment.id" class="my-2">
          <v-card class="comment-card" variant="flat">
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
                  <v-list-item @click="openEditCommentModal(comment)">
                    <template v-slot:prepend>
                      <v-icon>mdi-pencil-outline</v-icon>
                    </template>
                    <v-list-item-title>Edit</v-list-item-title>
                  </v-list-item>
                  <v-list-item @click="promptDeleteComment(comment.id)">
                    <template v-slot:prepend>
                      <v-icon>mdi-delete-outline</v-icon>
                    </template>
                    <v-list-item-title>Delete</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </v-card-title>
            <v-card-subtitle>Posted by {{ comment.username }} • {{ new Date(comment.timestamp).toLocaleString() }}</v-card-subtitle>
          </v-card>
        </div>
      </v-card-text>
      <v-divider></v-divider>

      <!-- Comment Input Field (Fixed at Bottom) -->
      <v-card-actions class="comment-input" v-if="isAuthenticated()">
        <v-textarea v-model="newComment" append-icon="mdi-send" placeholder="Add a comment..." rows="1" @click:append="addComment" outlined></v-textarea>
      </v-card-actions>

      <!-- Closing Actions -->
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="close">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-snackbar v-model="snackbar.show" :color="snackbar.color">
    {{ snackbar.text }}
    <v-btn color="white" text @click="snackbar.show = false">Close</v-btn>
  </v-snackbar>

  <!--  Edit comment modal-->
  <v-dialog v-model="editCommentDialog" max-width="500px">
    <v-card>
      <v-card-title>Edit Comment</v-card-title>
      <v-card-text>
        <v-textarea v-model="editedComment.text" label="Edit Comment" rows="2" outlined></v-textarea>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="editCommentDialog = false">Cancel</v-btn>
        <v-btn color="blue darken-1" text @click="editComment">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!--  Delete comment modal-->
  <v-dialog v-model="confirmDeleteDialog" max-width="290">
    <v-card>
      <v-card-title>Confirm Delete</v-card-title>
      <v-card-text>Are you sure you want to delete this comment?</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="green" text @click="confirmDeleteDialog = false">Cancel</v-btn>
        <v-btn color="red" text @click="deleteComment">Confirm</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from 'axios';
import {ref, watch} from 'vue';
import { jwtDecode } from 'jwt-decode';

export default {
  props: {
    image: {
      type: Object,
      required: true
    },
    imageType: {
      type: String,
      required: true,
      validator: value => ['public', 'private'].includes(value)
    }
  },
  methods: {
    isAuthenticated() {
      return !!localStorage.getItem('user-token');
    },
  },
  setup(props) {
    const dialog = ref(false);
    const newComment = ref('');
    const comments = ref([]);
    const userRole = ref(null);
    const userId = ref(null);
    const snackbar = ref({ show: false, text: '', color: '' });
    const confirmDeleteDialog = ref(false);
    const commentToDelete = ref(null);
    const editCommentDialog = ref(false);
    const editedComment = ref({ id: null, text: '' });

    const token = localStorage.getItem('user-token');
    if (token) {
      const decoded = jwtDecode(token);
      userRole.value = decoded.role;
      userId.value = decoded.sub;
    }

    const authHeaders = {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    };

    const open = () => {
      dialog.value = true;
      fetchComments();
    };

    const close = () => {
      dialog.value = false;
      comments.value = [];
    };

    const fetchComments = () => {
      axios.get(`http://localhost:8080/api/v1/${props.imageType}/images/${props.image.id}/comments`)
        .then(response => {
          comments.value = response.data.data.filter(comment => comment.imageId === props.image.id);
          console.log("Fetched comments:", comments.value);
        })
        .catch(error => {
          console.error('Error fetching comments:', error);
        });
    };


    watch(dialog, (newVal) => {
      if (newVal === true) {
        fetchComments();
      } else {
        comments.value = [];
      }
    });

    const canEditOrDelete = (comment) => {
      return userRole.value === 'ADMINISTRATOR' || userId.value === comment.userId;
    };

    const addComment = () => {
      if (!newComment.value.trim()) return;
      if (!userRole.value) return;

      axios.post(`http://localhost:8080/api/v1/${props.imageType}/images/${props.image.id}/comments`, newComment.value, {
        headers: {
          ...authHeaders.headers,
          'Content-Type': 'text/plain'
        }
      })
        .then(() => {
          newComment.value = '';
          fetchComments();
          snackbar.value = {show: true, text: 'Comment added successfully', color: 'success'};
        })
        .catch(error => {
          console.error('Error adding comment:', error);
          snackbar.value = {show: true, text: 'Failed to add comment', color: 'error'};
        });
    };

    const openEditCommentModal = (comment) => {
      editedComment.value = { id: comment.id, text: comment.text };
      editCommentDialog.value = true;
    };

    const editComment = () => {
      axios.put(`http://localhost:8080/api/v1/${props.imageType}/images/${props.image.id}/comments/${editedComment.value.id}`, editedComment.value.text, {
        headers: {
          ...authHeaders.headers,
          'Content-Type': 'text/plain'
        }
      })
        .then(() => {
          editCommentDialog.value = false;
          fetchComments();
          snackbar.value = { show: true, text: 'Comment updated successfully', color: 'success' };
        })
        .catch(error => {
          console.error('Error editing comment:', error);
          snackbar.value = { show: true, text: 'Failed to edit comment', color: 'error' };
        });
    };
    const promptDeleteComment = (commentId) => {
      commentToDelete.value = commentId;
      confirmDeleteDialog.value = true;
    };

    const deleteComment = () => {
      axios.delete(`http://localhost:8080/api/v1/${props.imageType}/images/${props.image.id}/comments/${commentToDelete.value}`, authHeaders)
        .then(() => {
          fetchComments();
          snackbar.value = {show: true, text: 'Comment deleted successfully', color: 'success'};
        })
        .catch(error => {
            console.error('Error deleting comment:', error);
            snackbar.value = {show: true, text: 'Failed to delete comment', color: 'error'};
          }
        );
      confirmDeleteDialog.value = false;
    };

    return {
      dialog,
      newComment,
      comments,
      open,
      close,
      fetchComments,
      addComment,
      canEditOrDelete,
      editComment,
      deleteComment,
      snackbar,
      confirmDeleteDialog,
      commentToDelete,
      promptDeleteComment,
      editCommentDialog,
      editedComment,
      openEditCommentModal
    };
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
