<template>
  <v-container>
    <v-data-table
      :headers="headers"
      :items="users"
      :items-per-page="pagination.itemsPerPage"
      :page.sync="pagination.page"
      :server-items-length="pagination.itemCount"
      @update:page="fetchUsers"
      class="elevation-1"
    >
      <template v-slot:item.action="{ item }">
        <v-menu open-on-hover>
          <template v-slot:activator="{ on, props }">
            <v-btn icon small v-bind="props" v-on="on" variant="flat">
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

    <!-- Edit Modal -->
    <edit-user-modal ref="editUserModal" :user="selectedUser" @updated="fetchUsers"></edit-user-modal>

    <!-- Confirmation Dialog -->
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

    <!-- Snackbar -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{ snackbar.text }}
      <v-btn color="white" text @click="snackbar.show = false">Close</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import axios from 'axios';
import EditUserModal from '@/components/EditUserModal.vue';
import { ref, onMounted } from 'vue';

export default {
  components: { EditUserModal },
  setup() {
    const users = ref([]);
    const pagination = ref({
      page: 1,
      itemsPerPage: 10,
      itemCount: 0,
      pageCount: 0
    });
    const headers = ref([
      { text: 'Username', value: 'username' },
      { text: 'Email', value: 'email' },
      { text: 'Role', value: 'role' },
      { text: 'Actions', value: 'action', sortable: false }
    ]);
    const selectedUser = ref(null);
    const editUserModal = ref(null);
    const deleteDialog = ref(false);
    const snackbar = ref({ show: false, color: '', text: '' });

    const fetchUsers = () => {
      const token = localStorage.getItem('user-token');
      axios.get(`http://localhost:8080/api/v1/users?page=${pagination.value.page - 1}&size=${pagination.value.itemsPerPage}`, { headers: { 'Authorization': `Bearer ${token}` } })
        .then(response => {
          users.value = response.data.data;
          pagination.value.itemCount = response.data.itemCount;
          pagination.value.pageCount = response.data.pageCount;
        })
        .catch(error => {
          showSnackbar('Failed to fetch users', 'error');
        });
    };

    const openEditModal = (user) => {
      selectedUser.value = user;
      editUserModal.value.open(user);
    };

    const confirmDelete = (user) => {
      selectedUser.value = user;
      deleteDialog.value = true;
    };

    const deleteUser = () => {
      const token = localStorage.getItem('user-token');
      axios.delete(`http://localhost:8080/api/v1/users/${selectedUser.value.id}`, { headers: { 'Authorization': `Bearer ${token}` } })
        .then(() => {
          deleteDialog.value = false;
          showSnackbar('User deleted successfully', 'success');
          fetchUsers();
        })
        .catch(error => {
          showSnackbar('Failed to delete user', 'error');
        });
    };

    const showSnackbar = (text, color) => {
      snackbar.value.text = text;
      snackbar.value.color = color;
      snackbar.value.show = true;
    };

    onMounted(fetchUsers);

    return {
      users,
      pagination,
      headers,
      selectedUser,
      deleteDialog,
      snackbar,
      editUserModal,
      fetchUsers,
      openEditModal,
      confirmDelete,
      deleteUser,
      showSnackbar
    };
  }
};
</script>




