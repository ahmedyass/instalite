<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-title>Edit User</v-card-title>
      <v-card-text>
        <v-container>
          <v-form ref="form" v-model="valid">
            <v-text-field
              label="Username"
              v-model="editedUser.username"
              :rules="[rules.required]"
              required
            ></v-text-field>
            <v-text-field
              label="Email"
              v-model="editedUser.email"
              :rules="[rules.required, rules.email]"
              required
            ></v-text-field>
            <v-select
              label="Role"
              v-model="editedUser.role"
              :items="roles"
              :rules="[rules.required]"
              required
            ></v-select>
          </v-form>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="red darken-1" text @click="cancel">Cancel</v-btn>
        <v-btn color="green darken-1" text @click="save">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  props: {
    value: Boolean,
    user: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props, { expose , emit }) {
    const dialog = ref(false);
    const valid = ref(false);
    const editedUser = ref({});
    const roles = ref(['USER', 'ADMINISTRATOR', 'PRIVILEGED_USER']);
    const rules = {
      required: value => !!value || 'Required.',
      email: value => /.+@.+\..+/.test(value) || 'E-mail must be valid.',
    };


    const save = () => {
      if (valid.value) {
        const token = localStorage.getItem('user-token');
        axios.put(`http://localhost:8080/api/v1/users/${editedUser.value.id}`, editedUser.value, {
          headers: { 'Authorization': `Bearer ${token}` }
        })
          .then(() => {
            dialog.value = false;
            emit('update-success'); // Emit an event indicating a successful update
          })
          .catch(error => {
            console.error('Error updating user:', error);
          });
      }
    };


    const open = (user) => {
      editedUser.value = { ...user };
      dialog.value = true;
    };

    expose({ open });
    const cancel = () => {
      dialog.value = false;
    };

    return {
      dialog,
      valid,
      editedUser,
      roles,
      rules,
      save,
      cancel
    };
  }
};
</script>
