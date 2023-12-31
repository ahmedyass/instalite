<template>
  <v-container>
    <v-col cols="9" offset="1">
      <div>
        <v-row>
          <h1>Public images</h1>
          <v-divider class="border-opacity-50 pa-2"></v-divider>
          <div class="pa-2">
            <v-row>
              <v-col v-for="image in images" :key="image.id" cols="9">
                <v-sheet border rounded="lg" width="100%" variant="tonal">
                  <v-hover v-slot="{ isHovering, props }">
                    <v-img max-height="50rem" :src="image.url" width="100rem" v-bind="props" cover>
                      <v-overlay :model-value="isHovering" contained scrim="#030303" class="align-center justify-center">
                        <v-btn variant="flat" @click="showImageModal(image)">Show image</v-btn>
                      </v-overlay>
                    </v-img>
                  </v-hover>
                  <v-card-item>
                    <v-card-title class="d-flex justify-space-between">
                      <h3>{{ image.title }}</h3>
                      <ImageActions :imageId="image.id" :initialImage="image" image-type="public" />
                    </v-card-title>
                    <v-card-subtitle>username • {{ new Date(image.creationDate).toLocaleString() }}</v-card-subtitle>
                  </v-card-item>
                  <v-card-text>
                    <p>{{ image.description }}</p>
                  </v-card-text>
                </v-sheet>
              </v-col>
            </v-row>
          </div>
          <div class="mx-auto">
            <v-btn icon="mdi-refresh" variant="flat" v-if="!allLoaded" @click="loadMore"></v-btn>
          </div>
        </v-row>
      </div>
    </v-col>
    <image-modal ref="imageModal" :image="selectedImage"></image-modal>
  </v-container>
</template>

<script>
import axios from 'axios';
import ImageModal from '@/components/ImageModal.vue';
import ImageActions from '@/components/ImageActions.vue';

export default {
  components: {
    ImageModal,
    ImageActions
  },
  data() {
    return {
      images: [],
      page: 0,
      allLoaded: false,
      selectedImage: null
    };
  },
  mounted() {
    this.loadMore();
  },
  methods: {
    loadMore() {
      axios.get(`http://localhost:8080/api/v1/public/images?page=${this.page}&size=3`)
        .then(response => {
          const newImages = response.data.content;
          if (newImages.length > 0) {
            this.images.push(...newImages);
            this.page++;
          } else {
            this.allLoaded = true;
          }
        })
        .catch(error => console.error(error));
    },
    showImageModal(image) {
      this.selectedImage = image;
      this.$refs.imageModal.open();
    }
  }
};
</script>
