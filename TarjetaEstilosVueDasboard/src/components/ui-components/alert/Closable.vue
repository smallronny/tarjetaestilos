<script setup lang="ts">
import { ref } from 'vue';
const alert = ref(true);
</script>
<template>
   <div>
    <v-alert
      v-model="alert"
      border="start"
      variant="tonal"
      closable
      close-label="Close Alert"
      color="warning"
      title="Closable Alert"
    >
      No existen registros
    </v-alert>
    <div
      v-if="!alert"
    >
      <v-btn
        color="warning"
        @click="alert = true" flat>
        Reset
      </v-btn>
    </div>
  </div>
</template>
