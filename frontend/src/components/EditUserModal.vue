<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-title>Edit User</v-card-title>
      <v-card-text>
        <v-container>
          <v-form ref="form" v-model="valid">
            <v-text-field label="Username" v-model="editedUser.username" :rules="[rules.required]" required></v-text-field>
            <v-text-field label="Email" v-model="editedUser.email" :rules="[rules.required, rules.email]" required></v-text-field>
            <v-select label="Role" v-model="editedUser.role" :items="roles" :rules="[rules.required]" required></v-select>
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
import axios from 'axios';

export default {
  props: {
    value: Boolean,
    user: Object
  },
  data() {
    return {
      dialog: false,
      valid: false,
      editedUser: {},
      roles: ['USER', 'ADMINISTRATOR', 'PRIVILEGED_USER'],
      rules: {
        required: value => !!value || 'Required.',
        email: value => /.+@.+\..+/.test(value) || 'E-mail must be valid.'
      }
    };
  },
  watch: {
    value(val) {
      this.dialog = val;
      if (val) {
        this.editedUser = Object.assign({}, this.user);
      }
    }
  },
  methods: {
    cancel() {
      this.dialog = false;
    },
    save() {
      if (this.$refs.form.validate()) {
        const token = localStorage.getItem('user-token');
        axios.put(`http://localhost:8080/api/v1/users/${this.editedUser.id}`, this.editedUser, { headers: { 'Authorization': `Bearer ${token}` } })
          .then(() => {
            this.dialog = false;
            this.$emit('updated');
          })
          .catch(error => {
            console.error('Error updating user:', error);
          });
      }
    }
  }
};
</script>
