<script setup lang="ts">
import { ref, reactive, onMounted, computed, onBeforeMount, onBeforeUnmount  } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios';
import moment from 'moment';

import UiChildCard from '@/components/shared/UiChildCard.vue';
import CardsMedia from "@/components/ui-components/cards/CardsMedia.vue";
import Category from "@/models/PromotionCategory"; 
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';
import Promotion from '@/models/Promotion';
import type { VForm } from "vuetify/components";
import { Icon } from '@iconify/vue';

import { useToast } from "vue-toastification";
import { useDate } from 'vuetify';
import { shallowRef } from 'vue';
import { format, parseISO } from 'date-fns';
import { DateTime } from "luxon";


import { nextTick } from 'vue';
import tinymce from 'tinymce';


// Variables
const router = useRouter()
const toast = useToast()
const registros = ref<any[]>([])
const url = ref(Global.url)
const url_image = ref(Global.url_image)
const url_logo = ref(Global.url_image)
const showmessage = ref(false)
const departamentos = ref<any[]>([])
const categorias = ref<any[]>([])
const affiliates = ref<any[]>([])
const token = localStorage.getItem('token');
const model = reactive(new Promotion(null, null, null, null, null, null, null, null, null));
const modelN = reactive(new Promotion(null, null, null, null, null, null, null, null, null));


const totalElements = ref(0);
const numberOfElements = ref(0);
const page = ref(1);
const size = 21;
const totalPages = ref(0);
const title_modal = ref('')
const showModal = ref(false)

const adapter = useDate();
const showEditor = ref(false);
const editorValid = ref(true);
const editorValidTerm = ref(true);


const formAdd = ref<VForm | null>(null)
const valid = ref(false);
const editar = ref(false);
const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 
const date = shallowRef<Date | null>(null);
const image = ref<string | null>(null);
const file = ref<File|null>(null);

const logo = ref<string | null>(null);
const fileLogo = ref<File|null>(null);




const rules = {
  required: (value:any) => !!value || "Este campo es obligatorio",
  min3: (value:any) =>
    (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
  numeric: (v: any) => /^(\d+(\.\d+)?)?$/.test(v) || 'Debe ser un número válido',
  range: (v: any) => (v >= 0 && v <= 100) || 'El descuento debe estar entre 0 y 100'
};


async function searchGetMenuPromotion() {
  let datosEnviar= {
      slug: 'establecimientos-afiliados',      
  }
  try {
    const response = await api.post("cms/menu/search",datosEnviar, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      model.menu_id = response.data.content[0].id;
      modelN.menu_id = response.data.content[0].id;
      searchRegistros();
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

const validateEditor = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const text = modelN.description?.replace(/<[^>]*>?/gm, "").trim();
  editorValid.value = text ? text.length >= 3 : false;
  console.log('editorValid.value: ',editorValid.value);
  return editorValid.value;
};

const validateEditorTerm = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const textTerm = modelN.terms_conditions?.replace(/<[^>]*>?/gm, "").trim();
  editorValidTerm.value = textTerm ? textTerm.length >= 3 : false;
  console.log('editorValid.value: ',editorValidTerm.value);
  return editorValidTerm.value;
};

interface Item {
  id: number;
  name: string;
  image: string;
}

const  itemsAffiliates: Item[] = [
  { id: 1, name: 'Manzana', image: 'https://cdn-icons-png.flaticon.com/512/415/415733.png' } ,
  { id: 2, name: 'Banana', image: 'https://cdn-icons-png.flaticon.com/512/415/415753.png' } ,
  
]

const selected = ref<Item | null>(null);



async function onFileChange(event: Event){
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    file.value = target.files[0] 
    const reader = new FileReader()
    reader.onload = e => {
      image.value = e.target?.result as string
    }
    reader.readAsDataURL(file.value) 
    const formData = new FormData();
    formData.append("image", file.value as File); 
    formData.append("type", "card"); 
      
    try {
      const response = await api.post("cms/promotion/image",formData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        if(response.data.status===201 && response.data.data){
          flassmessagesuccess(response.data.message,20);
          modelN.image = response.data.data;        
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
  
}


// Funciones
async function consultarRegistros() {
  registros.value = [];
  try {
    const response = await api.get(`cms/promotions/${page.value}/${size}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registros.value = response.data.content;
      totalPages.value = response.data.totalPages;
      totalElements.value = response.data.totalElements;
      numberOfElements.value = response.data.numberOfElements
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistros() {
  registros.value = [];
  let datosEnviar= {
      title: model.title,
      description: model.description,
      discount: model.discount,
      isnew: model.isnew,
      department_id: model.department_id,
      page: page.value,
      size: size,
  }
  try {
    const response = await api.post("cms/promotion/search",datosEnviar, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registros.value = response.data.content;
      totalPages.value = response.data.totalPages;
      totalElements.value = response.data.totalElements;
      numberOfElements.value = response.data.numberOfElements
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function paginateRegistros(pageNew:Number) {
  registros.value = [];

  try {
    const response = await api.get(`cms/promotions/${pageNew}/${size}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registros.value = response.data.content;
      totalPages.value = response.data.totalPages;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);
    
  }
}

async function consultarDepartamentos() {
  registros.value = [];

  try {
    const response = await api.get(`cms/departmentAll`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    departamentos.value = []
    if (Array.isArray(response.data) && response.data.length > 0) {
      departamentos.value = [
        { title: 'Seleccione', value: '' }, 
        ...response.data.map(dep => ({
          title: dep.name,
          value: dep.id
        }))
      ]
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

function getCategories(){
  axios
    .get(url.value + 'cms/promotion-category-all',{
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    .then((response) => {
      categorias.value = []
      if (response.status === 200) {
        if (Array.isArray(response.data) && response.data.length > 0) {
          categorias.value = response.data.map(cat => ({
            title: cat.name,
            value: cat.id
          }))
        } 
      } else {
        // error de status
        console.error("Error en la petición:", response.status)
        showmessage.value = true
      }
    })
    .catch((error) => {
      console.error(error)
    })
}

function getAffiliates(){
  axios
    .get(url.value + 'cms/affiliates-all',{
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    .then((response) => {
      affiliates.value = []
      if (response.status === 200) {
        if (Array.isArray(response.data) && response.data.length > 0) {
          affiliates.value = response.data.map(afi => ({
            name: afi.title,
            id: afi.id,
            image: url_image.value+afi.image
          }))
        } 
      } else {
        // error de status
        console.error("Error en la petición:", response.status)
        showmessage.value = true
      }
    })
    .catch((error) => {
      console.error(error)
    })
}


let editorInstanceDesc:any = null;
let editorInstanceTerm:any = null;

async function initEditorDesc() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorDesc',
    plugins: 'lists link image table code',
    toolbar:
      'undo redo | bold italic | alignleft aligncenter alignright | bullist numlist | link image | code',
    menubar: false,
    height: 300,
    image_dimensions: true,
    image_advtab: true,
    images_upload_handler: (blobInfo) => {
      const formData = new FormData();
      formData.append("file", blobInfo.blob(), blobInfo.filename());

      // Axios ya maneja la promesa
      return api
        .post("cms/promotion/image-upload", formData, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          const data = response.data;

          console.log("data:", data);

          if (editorInstanceDesc) {
            editorInstanceDesc.insertContent(
              `<img src="${Global.url}${data.location}" alt="${blobInfo.filename()}" width="${data.width}" height="${data.height}" />`
            );
          }

          // devolver URL a TinyMCE (si no quieres doble inserción, puedes solo devolver resolve(''))
          return '';
        })
        .catch((error) => {
          console.error("Error en subida de imagen:", error);
          return Promise.reject("Error al subir la imagen");
        });
    },

    setup: (editorDesc) => {
      editorInstanceDesc = editorDesc;
      editorDesc.on('OpenWindow', (e) => {
        setTimeout(() => {
          const widthInput = document.querySelector('input[aria-label="Width"]');
          const heightInput = document.querySelector('input[aria-label="Height"]');
          
          if (widthInput) widthInput.removeAttribute('readonly');
          if (heightInput) heightInput.removeAttribute('readonly');
        }, 100);
      });
      // sincronizar con Vue
      editorDesc.on('change keyup', () => {
        modelN.description = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(modelN.description || '');
      });      
    }
  });
}

function initEditorTerm() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorTerm',
    plugins: 'lists link image table code',
    toolbar:
      'undo redo | bold italic | alignleft aligncenter alignright | bullist numlist | link image | code',
    menubar: false,
    height: 300,
    images_upload_url: '/cms/promotion/image-upload', // tu endpoint en backend

    images_upload_handler: (blobInfo) => {
      const formData = new FormData();
      formData.append("file", blobInfo.blob(), blobInfo.filename());

      // Axios ya maneja la promesa
      return api
        .post("cms/promotion/image-upload", formData, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          const data = response.data;

          console.log("data:", data);

          if (editorInstanceTerm) {
            editorInstanceTerm.insertContent(
              `<img src="${Global.url}${data.location}" alt="${blobInfo.filename()}" width="${data.width}" height="${data.height}" />`
            );
          }

          // devolver URL a TinyMCE (si no quieres doble inserción, puedes solo devolver resolve(''))
          return '';
        })
        .catch((error) => {
          console.error("Error en subida de imagen:", error);
          return Promise.reject("Error al subir la imagen");
        });
    },
    setup: (editorTerm) => {
      editorInstanceTerm = editorTerm;
      editorTerm.on('OpenWindow', (e) => {
        setTimeout(() => {
          const widthInput = document.querySelector('input[aria-label="Width"]');
          const heightInput = document.querySelector('input[aria-label="Height"]');
          
          if (widthInput) widthInput.removeAttribute('readonly');
          if (heightInput) heightInput.removeAttribute('readonly');
        }, 100);
      });
      // sincronizar con Vue
      editorTerm.on('change keyup', () => {
        modelN.terms_conditions = editorTerm.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorTerm.on('init', () => {
        editorTerm.setContent(modelN.terms_conditions || '');
      });      
    }
  });
}

async function modalAgregar(){
  
  showModal.value = true;
  title_modal.value = "Agregar Promoción";
  showEditor.value = true;
  editar.value = false;
  await nextTick(); 
  initEditorDesc();
  initEditorTerm();
}
async function modalEditar(mo:any){
  
  editar.value = true;
  showModal.value = true;
  title_modal.value = "Modificar Promoción";
  showEditor.value = true;
  modelN.title = mo.title;
  modelN.discount = mo.discount;
  modelN.description = mo.description;
  modelN.terms_conditions = mo.terms_conditions
  
  
  date.value = mo.publicationDate ? parseISO(mo.publicationDate) : null;

  image.value = url_image.value + mo.image;
  logo.value = url_logo.value + mo.logo;
  modelN.id = mo.id;
  const categoryIds = mo.promotionsCategories.map((item: any) => item.promotionCategory.id);
  modelN.categoryIds = categoryIds;
  modelN.department_id =mo.department.id;
  modelN.isnew = mo.isnew;
  modelN.outstanding = mo.outstanding;
  modelN.featured = mo.featured;
  modelN.promotion_link = mo.promotion_link;
  modelN.promotion_map = mo.promotion_map;

  await nextTick();
  initEditorDesc();
  initEditorTerm();
}

function formatIso (date:any) {
  return adapter.toISO(date)
}

function flassmessagesuccess(message:string,time: number){                
    toast.success(`${message}`, {
        timeout: time
    });
}

async function guardar(){
  console.log('guardar');    
  
  const result = await formAdd.value?.validate()
  const ckValid = await validateEditor();
  const ckValidTerm = await validateEditorTerm();
  console.log('ckValid:', ckValid);
  console.log('ckValidTerm:', ckValidTerm);
  console.log('modelN:', modelN);
  modelN.affiliates_id = selected.value?.id ?? null;
  if (result?.valid && ckValid && ckValidTerm) {
    
    try {
      const response = await api.post("cms/promotion/create",modelN, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        
        if(response.data.status===201 && response.data.data){
          
          registros.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModal.value = false; showEditor.value = false;
          Object.assign(modelN, new Promotion(null, null, null, null, null, null, null, null, null, null, null,null,null));
          image.value = null;
          file.value = null;
          date.value = null;
          logo.value = null;
          fileLogo.value = null;
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  } else {
    console.log("Formulario inválido ❌");
  }  
}

async function modificar(){
  console.log('modiifcar');  
  const result = await formAdd.value?.validate()
  const ckValid = await validateEditor();
  const ckValidTerm = await validateEditorTerm();
  console.log('ckValid:', ckValid);
  console.log('ckValidTerm:', ckValidTerm);
  console.log('modelN:', modelN);
  modelN.affiliates_id = selected.value?.id ?? modelN.affiliates_id;
  if (result?.valid && ckValid && ckValidTerm) {
    
    try {
      const response = await api.put(`cms/promotion/update/${modelN.id}`,modelN, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registros.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registros.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModal.value = false; showEditor.value = false;
          Object.assign(modelN, new Promotion(null, null, null, null, null, null, null, null, null, null, null, null, null));
          image.value = null;
          file.value = null;
          date.value = null;
          logo.value = null;
          fileLogo.value = null;
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  } else {
    console.log("Formulario inválido ❌");
  }  
}

function closeModal(){
  showModal.value = false; showEditor.value = false;
  Object.assign(modelN, new Promotion(null, null, null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorDesc");
  tinymce.remove("#mieditorTerm");
}

async function fnDelete(blog: any) {
  console.log('Eliminar:', blog);

  const willDelete = await swal({
    title: "¿Está seguro de eliminar el registro?",
    text: "El registro no podrá ser recuperado!",
    icon: "warning",
    buttons: ["Cancelar", "Eliminar"],
    dangerMode: true,
  });

  if (!willDelete) {
    swal("El registro no fue eliminado.");
    return;
  }

  try {
    const response = await api.delete(`cms/promotion/delete/${blog.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "multipart/form-data"
      },
    });
    if (response.data.status === 200) {
      deletedId.value = blog.id;
      setTimeout(() => { 
        updatedId.value = 0; 
        setTimeout(() => { 
          registros.value = registros.value.filter((r) => r.id !== blog.id);
        }, 1000); 
      }, 5000);
      flassmessagesuccess(response.data.message, 5000);
      
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);
    swal("Ocurrió un error al eliminar!", { icon: "error" });
  }
}
// Ciclo de vida
onMounted(() => {
  if (localStorage.getItem('token')) {
    consultarRegistros();
    consultarDepartamentos();
    getCategories();
    initEditorDesc();
    initEditorTerm();
    getAffiliates();
    searchGetMenuPromotion();
  } else {
    router.push('/')
  }

})

onBeforeUnmount(() => {
  tinymce.remove("#mieditorDesc");
  tinymce.remove("#mieditorTerm");
});
</script>

<template>
  <v-row >
    <v-col cols="12">
      <UiChildCard title="Buscar Promociones" class="py-1">
        <v-form @submit.prevent="searchRegistros()">
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-3 v-col-sm-6 v-col-12">
              <v-text-field
                label="Título"
                placeholder="Título"
                v-model.trim="model.title"
              ></v-text-field>
            </div>
            <div class="v-col-md-3 v-col-sm-6 v-col-12">
              <v-text-field
                label="Descuento"
                placeholder="Descuento"
                v-model.trim="model.discount"
              ></v-text-field>
            </div>            
            <div class="v-col-md-3 v-col-sm-6 v-col-12">
              <v-select
                  :items="departamentos"
                  label="Departamento"
                  v-model="model.department_id"
                  item-title="title"
                  item-value="value"

                />
            </div>
            <div class="v-col-md-3 v-col-sm-6 v-col-12">
              <v-select chips multiple :items="categorias" label="Categorías" v-model="model.categoryIds" />
            </div>            
            <div class="v-col-md-3 v-col-sm-6 v-col-sm-6 v-col-lg-3 v-col-xl-1 v-col-12">
              <v-checkbox
                v-model="model.isnew"
                label="Nuevo"
                color="primary"
              ></v-checkbox>
            </div>
            <div class="v-col-md-3 v-col-sm-6 v-col-lg-2 v-col-12 d-flex">
              <v-btn text="Buscar" type="submit" color="primary" class="me-1">
                <template v-slot:prepend>
                  <div class="d-flex align-center justify-center" >
                    <Icon icon="solar:card-search-line-duotone" width="24" height="24" />
                  </div>
                </template>
              </v-btn>            
              <v-btn text="Agregar" color="info" @click="modalAgregar">
                <template v-slot:prepend>
                  <div class="d-flex align-center justify-center" >
                    <Icon icon="solar:camera-add-line-duotone" width="24" height="24" />
                  </div>
                </template>
              </v-btn>
            </div>        
          </div>
        </v-form>
      </UiChildCard>
    </v-col>
  </v-row>
  <v-row>
    <v-col cols="12" class="bg-amber" >
      <div class="d-flex justify-space-between align-center px-3 py-3 bg-white rounded-b-lg">
          <!-- Título -->
          <div class="text-h5">Promociones ({{ totalElements }})</div>
          <!-- Sección derecha -->
          <div class="d-flex align-center ga-2" v-if="totalPages>1">
            <div>Registros por página: {{ numberOfElements }}</div>
            <v-pagination 
              v-model="page"
              :length="totalPages"
              total-visible="7"
              @update:modelValue="paginateRegistros"
            />
          </div>
        </div>
    </v-col>   
    <v-col 
      v-for="(item, index) in registros"
      :key="item.id || index"
      cols="12"
      sm="12"
      lg="4"
      class="d-flex align-items-stretch"
    >
      <CardsMedia :img="url_image+item.image" :summary="item.discount" :description="item.description" 
          
          :title="item.title" :promotionsCategories="item.promotionsCategories"
          @edit="modalEditar(item)"
          @delete="fnDelete(item)"
          :class="{
            'box-shadow-animate-create': item.id === createdId, 
            'box-shadow-animate': item.id === updatedId,
            'box-shadow-animate-deleted': item.id === deletedId
          }"
        />
    </v-col>
    <v-col cols="12" class="bg-amber" v-if="registros.length==0">
        <div class="p-3 bg-white">
          <Closable />
        </div>
    </v-col>
  </v-row>

  <v-dialog v-model="showModal" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modal }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModal()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAdd" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelN.title" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-text-field
                v-model.number="modelN.discount"
                label="Descuento"
                placeholder="Escribe el descuento"
                outlined
                type="number"
                step="0.01" 
                min="0"
                max="100"
                :rules="[rules.required, rules.numeric, rules.range]"
                required
              />
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-select chips multiple :items="categorias" label="Categorías" v-model="modelN.categoryIds" />
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-select :items="departamentos" label="Departamentos" v-model="modelN.department_id" 
                :rules="[rules.required]"
                required 
              />
            </div>            
            <div class="v-col-md-4 v-col-12">
              <v-checkbox
                v-model="modelN.isnew"
                label="Nuevo"
                color="primary"
              ></v-checkbox>
            </div>
            <div class="v-col-md-4 v-col-12">
              <v-checkbox
                v-model="modelN.outstanding"
                label="Pendiente"
                color="primary"
              ></v-checkbox>
            </div>
            <div class="v-col-md-4 v-col-12">
              <v-checkbox
                v-model="modelN.featured"
                label="Destacado" 
                color="primary"  
              ></v-checkbox>
            </div>
            <div class="v-col-md-12 v-col-12">
              <v-text-field
                v-model="modelN.promotion_link"
                label="Url web" placeholder="Url web"
                color="primary" outlined
              ></v-text-field>
            </div>
            <div class="v-col-md-12 v-col-12">
              <v-text-field
                v-model="modelN.promotion_map"
                label="Mapa" placeholder="Mapa"
                color="primary" outlined
              ></v-text-field>
            </div>
            <div class="v-col-md-12 v-col-12">
              <div >
                <label>Descripción</label>
                <textarea id="mieditorDesc" v-model="modelN.description"></textarea>          
                <!-- Mensaje de validación -->
                <small v-if="!editorValid" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>
            <div class="v-col-md-12 v-col-12">
              <div v-if="showEditor">     
                <label>Términos y condiciones</label>                
                <textarea id="mieditorTerm" v-model="modelN.terms_conditions"></textarea>
                <!-- Mensaje de validación -->
                <small v-if="!editorValid" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-file-input accept="image/*" label="Subir imagen Card" prepend-icon="mdi-image" @change="onFileChange" class="file-input-custom" 
                :rules="!editar ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
            </div>            
            
            <div class="v-col-md-6 v-col-12 d-flex align-center">
              <v-select
                v-model="selected"
                :items="affiliates"
                item-value="id"
                label="Selecciona una afiliado"
                return-object
              >
                <!-- Slot item -->
                <template #item="{ item, props }">
                  <div v-bind="props" class="flex items-center px-3 py-2">
                    <img :src="item.raw.image" class="w-6 h-6 object-contain mr-2" width="24" alt="fruta" />
                    <span>{{ item.raw.name }}</span>
                  </div>
                </template>

                <!-- Slot selection -->
                <template #selection="{ item }">
                  <div class="flex items-center">
                    <img :src="item.raw.image ?? item.raw.image" class="w-6 h-6 object-contain mr-2" width="24" alt="fruta" />
                    <span>{{ item.raw.name ?? item.raw.name }}</span>
                  </div>
                </template>
              </v-select>
            </div>
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="sticky bottom-0 bg-white z-10">
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModal()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editar" @click="guardar">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editar" @click="modificar">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
