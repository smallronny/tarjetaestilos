// src/types/vue-shims.d.ts
import { ComponentCustomProperties } from 'vue'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $flashMessage: {
      show: (options: {
        type?: string
        title?: string
        message?: string
        time?: number
        beforeUnmount?: () => void
      }) => void
    }
  }
}

export {}