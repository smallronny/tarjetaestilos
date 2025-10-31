<script setup lang="ts">
import { ref, reactive, onMounted, watch  } from 'vue'
import { useRouter } from 'vue-router';
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';

import type { VForm } from "vuetify/components";
import { Icon } from '@iconify/vue';

import { useToast } from "vue-toastification";

import { shallowRef } from 'vue';
import { nextTick } from 'vue';
import tinymce from 'tinymce';


import PageContent from '@/models/PageContent';

const tab = ref("general") 
interface Item {
  id: number;
  name: string;
  image: string;
}
// Variables

const selected = ref<Item | null>(null);
const slug_menu = ref('terminos-y-condiciones');
const menu_id = ref(0);
const router = useRouter()
const toast = useToast()
const url_image = ref(Global.url_image);
const showmessage = ref(false)
const routeBanner = ref<string|null>(null);
const routeCorporateData = ref<string|null>(null);
const routePageContent = ref<string|null>(null);
const routeCta = ref<string|null>(null);
const routeFaq = ref<string|null>(null);

const token = localStorage.getItem('token');

const registrosBanner = ref<any[]>([]);
const registrosCorporateData = ref<any[]>([]);
const registrosPageContent = ref<any[]>([]);
const registrosCta = ref<any[]>([]);
const registrosFaq = ref<any[]>([]);


const modelPageContent = reactive(new PageContent(null, null, null, null, null, null, null));
const modelNPageContent = reactive(new PageContent(null, null, null, null, null, null, null));


const totalElementsBanner = ref(0);
const numberOfElementsBanner = ref(0);
const totalPagesBanner = ref(0);

const totalElementsPageContent = ref(0);
const numberOfElementsPageContent = ref(0);
const totalPagesPageContent = ref(0);


const title_modal = ref('');
const title_modalPageContent = ref('');


const showModalPageContent = ref(false);


const showEditorPageContent = ref(false);

const editorSubTitleValid = ref(true);
const editorTitleValid = ref(true);

const editorValidPageContent = ref(true);
const editorValidTitlePageContent = ref(true);

const formAddPageContent = ref<VForm | null>(null);

const valid = ref(false);
const editarPageContent = ref(false);


const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 
const date = shallowRef<Date | null>(null);
const image = ref<string | null>(null);
const file = ref<File|null>(null);

const logo = ref<string | null>(null);
const fileLogo = ref<File|null>(null);

routePageContent.value = "page-content";

const rules = {
  required: (value:any) => !!value || "Este campo es obligatorio",
  min3: (value:any) =>
    (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
  numeric: (v: any) => /^(\d+(\.\d+)?)?$/.test(v) || 'Debe ser un número válido',
  range: (v: any) => (v >= 0 && v <= 100) || 'El descuento debe estar entre 0 y 100'
};


async function searchGetMenu() {  
  let datosEnviar= {
      slug: slug_menu.value,      
  }
  try {
    const response = await api.post("cms/menu/search",datosEnviar, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);      
      totalPagesBanner.value = response.data.totalPages;
      totalElementsBanner.value = response.data.totalElements;
      numberOfElementsBanner.value = response.data.numberOfElements;
      menu_id.value = response.data.content[0].id;
      
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistrosPageContent() {
  registrosPageContent.value = [];
  modelPageContent.menu_id = menu_id.value;
  try {
    const response = await api.post(`cms/${routePageContent.value}/search`,modelPageContent, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosPageContent.value = response.data.content;
      totalPagesPageContent.value = response.data.totalPages;
      totalElementsPageContent.value = response.data.totalElements;
      numberOfElementsPageContent.value = response.data.numberOfElements;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}


async function modalAgregarPageContent(){ 
  await searchGetMenu();
  showModalPageContent.value = true;
  title_modalPageContent.value = "Agregar contenido";
  showEditorPageContent.value = true;
  editarPageContent.value = false;
  await nextTick(); 
  initEditorDescPageContent();
  initEditorTitlePageContent();
  console.log('modelPageContent:',modelNPageContent);
}




async function modalEditarPageContent(mo:any){
  await searchGetMenu();
  editarPageContent.value = true;
  showModalPageContent.value = true;
  title_modalPageContent.value = "Modificar contenido";
  showEditorPageContent.value = true;
  modelNPageContent.title = mo.title;
  modelNPageContent.image_position = mo.image_position;  
  modelNPageContent.id = mo.id;
  modelNPageContent.menu_id = mo.menu.id;
  modelNPageContent.description = mo.description;

  image.value = url_image.value + mo.image;

  modelNPageContent.itemContents = mo.itemContents.map((item: any) => ({
    id: item.id,
    description: item.description,
    icon: item.icon
  }));

  await nextTick();
  initEditorDescPageContent();
}


let editorInstanceDesc:any = null;
let editorInstanceDescPageContent:any = null;

async function initEditorDescPageContent() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorDescPageContent',
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
        .post("cms/page-content/image-upload", formData, {
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
      editorInstanceDescPageContent = editorDesc;
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
        modelNPageContent.description = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(modelNPageContent.description || '');
      });      
    }
  });
}


async function initEditorTitlePageContent() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorTitlePageContent',
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
        .post("cms/page-content/image-upload", formData, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          const data = response.data;
          console.log("data:", data);
          if (editorInstanceTitle) {
            editorInstanceTitle.insertContent(
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
    setup: (editorTitle) => {
      editorInstanceDescPageContent = editorTitle;
      editorTitle.on('OpenWindow', (e) => {
        setTimeout(() => {
          const widthInput = document.querySelector('input[aria-label="Width"]');
          const heightInput = document.querySelector('input[aria-label="Height"]');    
          if (widthInput) widthInput.removeAttribute('readonly');
          if (heightInput) heightInput.removeAttribute('readonly');
        }, 100);
      });
      // sincronizar con Vue
      editorTitle.on('change keyup', () => {
        modelNPageContent.title = editorTitle.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorTitle.on('init', () => {
        editorTitle.setContent(modelNPageContent.title || '');
      });      
    }
  });
}


function closeModalPageContent(){
  showModalPageContent.value = false; showEditorPageContent.value = false;
  Object.assign(modelNPageContent, new PageContent(null, null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorDescPageContent");
  tinymce.remove("#mieditorTitlePageContent");
}



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
      const response = await api.post(`cms/${routeBanner.value}/image`,formData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        if(response.data.status===201 && response.data.data){
          flassmessagesuccess(response.data.message,20);
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
  
}

async function onFileChangePageContent(event: Event){
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
      
    try {
      const response = await api.post(`cms/${routePageContent.value}/image`,formData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        if(response.data.status===201 && response.data.data){
          flassmessagesuccess(response.data.message,20);
          modelNPageContent.image = response.data.data;        
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
  
}



function flassmessagesuccess(message:string,time: number){                
    toast.success(`${message}`, {
        timeout: time
    });
}

async function guardarPageContent(){
  console.log('guardar');      
  const result = await formAddPageContent.value?.validate();
  modelNPageContent.menu_id = menu_id.value;
  console.log('modelNPageContent:', modelNPageContent);
  
  if (result?.valid ) {
    try {
      const response = await api.post(`cms/${routePageContent.value}/create`,modelNPageContent, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosPageContent.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalPageContent.value = false; showEditorPageContent.value = false;
          tinymce.remove("#mieditorDescPageContent");
          tinymce.remove("#mieditorTitlePageContent");
          Object.assign(modelNPageContent, new PageContent(null, null, null, null, null, null, null));
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

interface ItemContent {
  id: number
  description: string
  icon: string
}

async function agregarItem() {  
  if (!modelNPageContent.itemContents) {
    modelNPageContent.itemContents = []
  }
  const newId = modelNPageContent.itemContents.length + 1
  const nuevoItem: ItemContent = {
    id: newId,
    description: '',
    icon: ''
  }
  modelNPageContent.itemContents.push(nuevoItem)
  console.log('modelNPagecontend.itemConten', modelNPageContent.itemContents)
}



async function modificarPageContent(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddPageContent.value?.validate()
  
  if (result?.valid) {
    console.log("Formulario válido atajo ✅", modelNPageContent);
    try {
      const response = await api.put(`cms/${routePageContent.value}/update/${modelNPageContent.id}`,modelNPageContent, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosPageContent.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosPageContent.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalPageContent.value = false; showEditorPageContent.value = false;
          Object.assign(modelNPageContent, new PageContent(null, null, null, null, null, null, null));
          image.value = null;
          file.value = null;
          date.value = null;
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

const panelAbierto = ref(null);
let editorInstanceTitle:any = null;
let editorInstanceSubTitle:any = null;



async function fnDeletePageContent(mo: any) {
  console.log('Eliminar:', mo);
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
    const response = await api.delete(`cms/${routePageContent.value}/delete/${mo.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "multipart/form-data"
      },
    });
    if (response.data.status === 200) {
      deletedId.value = mo.id;
      setTimeout(() => { 
        updatedId.value = 0; 
        setTimeout(() => { 
          registrosPageContent.value = registrosPageContent.value.filter((r) => r.id !== mo.id);
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

watch(panelAbierto, async (nuevoIndex, anterior) => {

  console.log('panelAbierto', panelAbierto);

  console.log('nuevo:', nuevoIndex)
  console.log('anterior:', anterior)

  const nuevos = nuevoIndex || []
  const anteriores = anterior || []
  if (nuevos.length > anteriores.length) {
    await nextTick(() => {
      const indices = Array.isArray(nuevoIndex) ? nuevoIndex : [nuevoIndex]

      indices.forEach((index) => {
        if (index == null) return // 👈 evita errores con null o undefined

        const item = modelNPageContent.itemContents[index]
        if (!item) return 

        const editorId = `mieditorDescItemContent_${index}`
        
        console.log('editorId:',editorId);
        // Evita inicializar dos veces el mismo editor
        if (!tinymce.get(editorId)) {
          tinymce.init({
          license_key: 'gpl',
          selector: `#${editorId}`,
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
                if (editorInstanceSubTitle) {
                  editorInstanceSubTitle.insertContent(
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
            editorTerm.on('change keyup', () => {
              // ✅ Guardar contenido en su propio objeto
              item.description = editorTerm.getContent();
            });

            editorTerm.on('init', () => {
              // ✅ Cargar contenido del propio item
              editorTerm.setContent(item.description || '');
            });
          }
        });
        }
      })
    })
  }

  // Si el panel se cerró
  if (nuevos.length < anteriores.length) {
    const cerrados = anteriores.filter(i => !nuevos.includes(i))
    cerrados.forEach(index => {
      const editorId = `mieditorDescItemContent_${index}`
      const instance = tinymce.get(editorId)
      if (instance) {
        console.log(`🔴 Cerrando editor ${editorId}`)
        tinymce.remove(instance)
      }
    })
  }
})

const imagePositions =  ref({
  itemContents: [
    { value: 'left', name: 'Izquierda'},
    { value: 'right', name: 'Derecha'},
    { value: 'arriba', name: 'Arriba'},
  ],
})
const eliminarItem = (index:any) => {
  modelNPageContent.itemContents.splice(index, 1)
}

onMounted(async () => {
  if (localStorage.getItem('token')) {
    await searchGetMenu();
    await searchRegistrosPageContent();
  } else {
    router.push('/')
  }

})

</script>

<template>
  <v-container>
    <!-- Barra de tabs -->
    <v-tabs v-model="tab" bg-color="primary" dark>
      <v-tab value="pageContent" @click="searchRegistrosPageContent">Contenido</v-tab>
      
    </v-tabs>

    <!-- Contenido sincronizado -->
    <v-window v-model="tab" class="mt-4">
      
      <v-window-item value="pageContent">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosPageContent()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelPageContent.title"
                    label="Buscar por título"
                    variant="outlined"
                    density="comfortable"
                    hide-details
                  />
                </v-col>
                <v-col cols="12" sm="4" class="d-flex justify-end">
                  <v-btn text="Buscar" type="submit" color="primary" class="me-1">
                    <template v-slot:prepend>
                      <div class="d-flex align-center justify-center" >
                        <Icon icon="solar:card-search-line-duotone" width="24" height="24" />
                      </div>
                    </template>
                  </v-btn>            
                  <v-btn text="Agregar" color="info" @click="modalAgregarPageContent">
                    <template v-slot:prepend>
                      <div class="d-flex align-center justify-center" >
                        <Icon icon="solar:camera-add-line-duotone" width="24" height="24" />
                      </div>
                    </template>
                  </v-btn>
                </v-col>
              </v-row>
            </v-form>

            <v-divider class="my-6"></v-divider>
            
            <v-row dense class="mt-4">
              <v-col
                v-for="(item, index) in registrosPageContent"
                :key="index"
                cols="12" sm="6" md="4"
              >
                <v-card class="h-100"
                :class="{
                  'box-shadow-animate-create': item.id === createdId, 
                  'box-shadow-animate': item.id === updatedId,
                  'box-shadow-animate-deleted': item.id === deletedId
                }"
                >
                  <v-img
                    :src="url_image+item.image"
                    height="160"
                    cover
                  />
                  <v-card-title v-html="item.title"></v-card-title>
                  <v-card-subtitle v-html="item.subtitle"></v-card-subtitle>
                  <v-card-actions>
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarPageContent(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeletePageContent(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosPageContent.length==0">
                <div class="p-3 bg-white">
                  <Closable />
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>

    </v-window>
  </v-container>


  <v-dialog v-model="showModalPageContent" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modalPageContent }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModalPageContent()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddPageContent" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-12 v-col-12">
              <div >
                <label>Titulo</label>
                <textarea id="mieditorTitlePageContent" v-model="modelNPageContent.title"></textarea>          
                <!-- Mensaje de validación -->
                <small v-if="!editorValidTitlePageContent" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>

            <div class="v-col-md-12 v-col-12">
              <div >
                <label>Descripción</label>
                <textarea id="mieditorDescPageContent" v-model="modelNPageContent.description"></textarea>          
                <!-- Mensaje de validación -->
                <small v-if="!editorValidPageContent" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-file-input accept="image/*" label="Subir imagen Card" prepend-icon="mdi-image" @change="onFileChangePageContent" class="file-input-custom" 
                
              />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center justify-end">
              <v-select
                v-model="modelNPageContent.image_position"
                label="Posición de imagen" placeholder="posición de imagen"
                :items="imagePositions.itemContents"
                item-title="name"
                item-value="value"
                color="primary" outlined
              ></v-select>
            </div>
            <v-col cols="12" sm="12" md="12" lg="3" class="d-flex align-center justify-end">
              <span class="me-2">Agregar Item</span> 
              <v-btn color="primary" @click="agregarItem" class="btn-fab">
                <Icon icon="icon-park-solid:add-one" width="28" height="28"  />
              </v-btn>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="12">
              <v-expansion-panels v-model="panelAbierto" variant="accordion" multiple>
                <v-expansion-panel
                  v-for="(item, index) in modelNPageContent.itemContents"
                  :key="index"
                >
                  <v-expansion-panel-title class="d-flex  align-center justify-space-between">
                    <span class="me-3">Item {{ item.id }}</span>
                    <v-btn                      
                        color="red"
                        size="small"
                        class="bg-error"
                        @click.stop="eliminarItem(index)"
                      >
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path stroke-dasharray="24" stroke-dashoffset="24" d="M12 20h5c0.5 0 1 -0.5 1 -1v-14M12 20h-5c-0.5 0 -1 -0.5 -1 -1v-14"><animate fill="freeze" attributeName="stroke-dashoffset" dur="0.4s" values="24;0"/></path><path stroke-dasharray="20" stroke-dashoffset="20" d="M4 5h16"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.4s" dur="0.2s" values="20;0"/></path><path stroke-dasharray="8" stroke-dashoffset="8" d="M10 4h4M10 9v7M14 9v7"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.6s" dur="0.2s" values="8;0"/></path></g></svg>
                    </v-btn>
                  </v-expansion-panel-title>

                  <v-expansion-panel-text>
                    <v-row>
                      <v-col cols="12" md="12">
                        <label>Descripción</label>
                        <textarea
                          :id="`mieditorDescItemContent_${index}`"
                          v-model="item.description"
                        ></textarea>
                      </v-col>

                      <v-col cols="12" md="12">
                        <v-text-field
                          v-model="item.icon"
                          :label="`Icono de ${item.id}`"
                        />
                      </v-col>
                    </v-row>
                  </v-expansion-panel-text>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-col>              
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="sticky bottom-0 bg-white z-10">
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModalPageContent()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editarPageContent" @click="guardarPageContent">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editarPageContent" @click="modificarPageContent">
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
<style>
  .wrap-title {
    white-space: normal !important;
  }
</style>