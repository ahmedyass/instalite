<template>
  <v-container>
    <v-data-table
      :headers="headers"
      :items="users"
      :items-per-page="pagination.itemsPerPage"
      :page.sync="pagination.page"
      :server-items-length="pagination.itemCount"
      class="elevation-1"
    >
      <template v-slot:item.action="{ item }">
        <v-menu open-on-hover>
          <template v-slot:activator="{ on, props }">
            <v-btn icon small v-bind="props" v-on="on">
              <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item @click="openEditModal(item)">
              <v-list-item-title>Edit</v-list-item-title>
            </v-list-item>
            <v-list-item @click="confirmDelete(item)">
              <v-list-item-title>Delete</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>
    </v-data-table>

    <edit-user-modal v-if="selectedUser" :key="selectedUser.id" v-model="editDialog" :user="selectedUser" @updated="fetchUsers"></edit-user-modal>

    <v-dialog v-model="deleteDialog" max-width="500px">
      <v-card>
        <v-card-title class="headline">Are you sure?</v-card-title>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="deleteDialog = false">Cancel</v-btn>
          <v-btn color="red darken-1" text @click="deleteUser">Delete</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{ snackbar.text }}
      <v-btn color="white" text @click="snackbar.show = false">Close</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import axios from 'axios';
import EditUserModal from '@/components/EditUserModal.vue';

export default {
  components: { EditUserModal },
  data() {
    return {
      users: [],
      pagination: {
        page: 1,
        itemsPerPage: 10,
        itemCount: 0,
        pageCount: 0
      },
      headers: [
        { text: 'Username', value: 'username' },
        { text: 'Email', value: 'email' },
        { text: 'Role', value: 'role' },
        { text: 'Actions', value: 'action', sortable: false }
      ],
      selectedUser: null,
      editDialog: false,
      deleteDialog: false,
      snackbar: { show: false, color: '', text: '' }
    };
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers() {
      const token = localStorage.getItem('user-token');
      axios.get(`http://localhost:8080/api/v1/users?page=${this.pagination.page - 1}&size=${this.pagination.itemsPerPage}`, { headers: { 'Authorization': `Bearer ${token}` } })
        .then(response => {
          this.users = response.data.data;
          this.pagination.itemCount = response.data.itemCount;
          this.pagination.pageCount = response.data.pageCount;
        })
        .catch(error => {
          this.showSnackbar('Failed to fetch users', 'error');
        });
    },
    openEditModal(user) {
      this.selectedUser = user;
      this.editDialog = true;
    },
    confirmDelete(user) {
      this.selectedUser = user;
      this.deleteDialog = true;
    },
    deleteUser() {
      const token = localStorage.getItem('user-token');
      axios.delete(`http://localhost:8080/api/v1/users/${this.selectedUser.id}`, { headers: { 'Authorization': `Bearer ${token}` } })
        .then(() => {
          this.deleteDialog = false;
          this.showSnackbar('User deleted successfully', 'success');
          this.fetchUsers();
        })
        .catch(error => {
          this.showSnackbar('Failed to delete user', 'error');
        });
    },
    showSnackbar(text, color) {
      this.snackbar.text = text;
      this.snackbar.color = color;
      this.snackbar.show = true;
    }
  }
};
</script>
