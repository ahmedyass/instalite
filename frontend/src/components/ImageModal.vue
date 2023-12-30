<template>
  <v-dialog v-model="dialog">
    <v-card>
      <v-card-title>{{ image.title }}</v-card-title>
      <v-divider></v-divider>
      <v-img :src="image.url" aspect-ratio="1.7"></v-img>
      <v-card-text>{{ image.description }}</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="close">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>

export default {
  props: {
    image: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      dialog: false
    };
  },
  methods: {
    open() {
      this.dialog = true;
    },
    close() {
      this.dialog = false;
    }
  }
};
</script>

