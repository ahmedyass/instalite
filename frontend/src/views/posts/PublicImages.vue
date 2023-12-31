<template>
  <v-container>
    <v-col cols="12" class="px-10">
      <div>
        <v-row>
          <h1>Public images</h1>
          <v-divider class="border-opacity-50 pa-2"></v-divider>
          <v-row>
            <v-col>
              <v-col v-for="image in images" :key="image.id">
                <v-sheet border rounded flat>
                  <v-hover v-slot="{ isHovering, props }">
                    <v-img :src="image.url" v-bind="props" max-height="50rem" cover>
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
                    <v-card-subtitle>{{ image.username }} • {{ new Date(image.creationDate).toLocaleString() }}</v-card-subtitle>
                  </v-card-item>
                  <v-card-text>
                    <p>{{ image.description }}</p>
                  </v-card-text>
                </v-sheet>
              </v-col>
              <v-col class="text-center">
                <v-btn icon="mdi-refresh" variant="flat" v-if="!allLoaded" @click="loadMore"></v-btn>
              </v-col>
            </v-col>
          </v-row>
        </v-row>
      </div>
    </v-col>
    <image-modal ref="imageModal" :image="selectedImage" imageType="public"></image-modal>
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
        .catch(error => {
          console.error('Error loading images:', error);
        });
    },
    showImageModal(image) {
      this.selectedImage = image;
      this.$refs.imageModal.open();
    }
  }
};
</script>

<style>
.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.image-fit {
  max-width: 100%;
  width: 100%;
  height: auto;
}


/* Add any additional custom styles as needed */
</style>
