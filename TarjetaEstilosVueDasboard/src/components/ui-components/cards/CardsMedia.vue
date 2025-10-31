<script setup lang="ts">
  import { computed, ref } from "vue"
  import { formatDistanceToNow } from "date-fns";
  import { es } from "date-fns/locale";
  import { Icon } from '@iconify/vue';
  import type { PropType } from "vue";


  interface Category {
    id: number;
    name: string;
  }


  interface CategoryWrapper {
    category: Category;
  }

  interface PromotionCategory {
    id: number;
    name: string;
  }

  interface PromotionCategoryWrapper {
    promotionCategory: PromotionCategory;
  }

  interface Section {
    id: number;
    title: string;
  }

  interface SectionsWrapper {
    section: Section;
  }

  interface TipoMenu {
    id: number;
    title: string;
  }

  interface TipoMenuWrapper {
    sectionType: TipoMenu;
  }

  const props = defineProps({
      title: String,
      img: String,
      svgCode: String,
      slug: String,
      icons: Boolean,
      pagina: Boolean,
      productos: Boolean,
      seguros: Boolean,
      summary: String,
      publicationDate: String,
      description: String,
      sections: {
        type: Array as PropType<SectionsWrapper[]>,
        default: () => []
      },
      tipoMenus: {
        type: Array as PropType<TipoMenuWrapper[]>,
        default: () => []
      },
      categories: {
        type: Array as PropType<CategoryWrapper[]>,
        default: () => []
      },
      promotionsCategories: {
        type: Array as PropType<PromotionCategoryWrapper[]>,
        default: () => []
      },
      label: String,
      button_label: String,
      fit: {
        type: String as () => 'cover' | 'contain',
        default: 'cover',
        validator: (v: 'cover' | 'contain') => ['cover', 'contain'].includes(v),
      }
  });
  
  const emit = defineEmits<{
    (e: "edit", blog: any): void,
    (e: "delete", blog: any): void
  }>();

  

  const dateFormat = computed(() => {
    if (!props.publicationDate) return "";
    return formatDistanceToNow(new Date(props.publicationDate), {
      addSuffix: true,
      locale: es,
    });
  });
</script>
<template>
  <!-- ----------------------------------------------------------------------------- -->
  <!-- Media -->
  <!-- ----------------------------------------------------------------------------- -->

  <v-card >
    <v-img v-if="img"
      class="align-end text-white"
      height="200"
      :src='img'
      v-bind="{ [fit]: true }"
    >
      <v-card-title class="bg-primary opacity-90"  v-html="title">
      
      </v-card-title>
    </v-img>
    
    <v-card-title v-if="!img" class="bg-primary opacity-90 d-flex gap-2">
      <span>Título: </span> 
      <div v-if="svgCode" v-html="svgCode" class="ms-3"></div> 
      <span>{{ title }}</span> 
    </v-card-title>
    <v-card-subtitle class="pt-4" v-if="summary">Resumen: {{ summary }} </v-card-subtitle>
    <v-card-subtitle class="pt-4" v-if="label">Etiqueta: {{ label }} </v-card-subtitle>
    <v-row style="margin: 0;" class="align-center" v-if="icons && slug">
      <v-col cols="6" class="align-center d-flex py-0" v-if="slug">
        <small>slug: {{ slug }}</small>
      </v-col>
      <v-col cols="6" class="align-center d-flex py-0 justify-space-between" v-if="icons">
        <small>
          Página:
        </small>
        <Icon v-if="pagina" icon="line-md:confirm-circle" width="24" height="24" color="green" />
        <Icon v-else icon="line-md:close-circle" width="24" height="24" color="red" />
      </v-col>
    </v-row>
    <v-row style="margin: 0;" class="align-center" v-if="icons">      
      <v-col cols="6" class="align-center d-flex py-0 justify-space-between">
        <small>
          Productos:
        </small>
        <Icon v-if="productos" icon="line-md:confirm-circle" width="24" height="24" color="green" />
        <Icon v-else icon="line-md:close-circle" width="24" height="24" color="red" />
      </v-col>
      <v-col cols="6" class="align-center d-flex py-0 justify-space-between">
        <small>
          Seguros:
        </small>
        <Icon v-if="seguros" icon="line-md:confirm-circle" width="24" height="24" color="green" />
        <Icon v-else icon="line-md:close-circle" width="24" height="24" color="red" />
      </v-col>
    </v-row>
    
    <v-row style="margin: 0;">
      <v-col cols="6" v-if="sections.length > 0" class="py-0">
        <small class="">Secciones:</small>
        <v-list density="compact" class="py-0">
          <v-list-item
            v-for="item in sections"
            :key="item.section.id" min-height="21" class="py-0"
          >
            <v-list-item-title>
              <small>{{ item.section.title }}</small>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-col>
      <v-col cols="6" v-if="tipoMenus.length > 0" class="py-0">
        <small class="">Tipo menu:</small>
        <v-list density="compact" class="py-0">
          <v-list-item
            v-for="item in tipoMenus"
            :key="item.sectionType.id" min-height="21" class="py-0"
          >
            <v-list-item-title>
              <small>{{ item.sectionType.title }}</small>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-col>
    </v-row>
    

    <div class="d-flex gap-2 position-absolute" style="top: 3px; right: 5px;" v-if="categories.length>0">
      <v-chip
        v-for="(item, index) in categories"
        :key="index"
        class="bg-primary opacity-90"
      >
        {{ item.category.name }}
      </v-chip>
    </div>

    <div class="d-flex gap-2 position-absolute" style="top: 3px; right: 5px;" v-if="promotionsCategories.length>0">
      <v-chip
        v-for="(item, index) in promotionsCategories"
        :key="index"
        class="bg-primary opacity-90"
      >
        {{ item.promotionCategory.name }}
      </v-chip>
    </div>
    

    <v-card-text v-if="description && dateFormat">
      <div v-html="description"></div>
      <small>{{ dateFormat }}</small>
    </v-card-text>
    <v-card-text v-if="button_label">
      <small>{{ button_label }}</small>
    </v-card-text>
    <v-card-actions>
      <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="emit('edit', props)"> 
        <template v-slot:prepend>
          <div class="d-flex align-center justify-center" >
            <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
          </div>
        </template>
      </v-btn>      

      <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="emit('delete', props)">
        <template v-slot:prepend>
          <div class="d-flex align-center justify-center" >
            <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
          </div>        
        </template>        
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
