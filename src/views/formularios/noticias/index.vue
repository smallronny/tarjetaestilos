<script setup lang="ts">

import axios from "axios";
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


import Banners from '@/models/Banners';
import FormBlog from '@/models/FormBlog';


interface Buttons {
  id: number
  title: string
  add_class: string
  link: string
}



const tab = ref("general") 
interface Item {
  id: number;
  name: string;
  image: string;
}
// Variables


const slug_menu = ref('educacion-financiera');
const menu_id = ref(0);
const router = useRouter()
const toast = useToast()
const url_image = ref(Global.url_image);
const showmessage = ref(false)
const routeBanner = ref<string|null>(null);
const routePageContent = ref<string|null>(null);


const token = localStorage.getItem('token');

const registrosBanner = ref<any[]>([]);
const registrosPageContent = ref<any[]>([]);


const modelBanner = reactive(new Banners(null, null, null, null, null, null, null, null));
const modelNBanner = reactive(new Banners(null, null, null, null, null, null, null, null));

const modelPageContent = reactive(new FormBlog(null, null, null, null, null, null, null));
const modelNPageContent = reactive(new FormBlog(null, null, null, null, null, null, null));

const totalElementsBanner = ref(0);
const numberOfElementsBanner = ref(0);
const totalPagesBanner = ref(0);

const totalElementsPageContent = ref(0);
const numberOfElementsPageContent = ref(0);
const totalPagesPageContent = ref(0);


const title_modal = ref('');
const title_modalPageContent = ref('');


const showModalBanner = ref(false);
const showModalPageContent = ref(false);


const showEditor = ref(false);
const showEditorPageContent = ref(false);


const editorValidPageContent = ref(true);
const editorValidTitlePageContent = ref(true);


const formAddBanner = ref<VForm | null>(null);
const formAddPageContent = ref<VForm | null>(null);

const valid = ref(false);
const editar = ref(false);
const editarPageContent = ref(false);


const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 
const date = shallowRef<Date | null>(null);
const image = ref<string | null>(null);
const file = ref<File|null>(null);

const logo = ref<string | null>(null);
const fileLogo = ref<File|null>(null);

routeBanner.value = "banners";
routePageContent.value = "form-blog";

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
      modelBanner.menu_id = response.data.content[0].id;
      modelNBanner.menu_id = response.data.content[0].id;
      modelPageContent.menu_id = response.data.content[0].id;
      modelNPageContent.menu_id = response.data.content[0].id; 
      menu_id.value = response.data.content[0].id;
      
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistrosBanners() {
  registrosBanner.value = [];
  modelBanner.menu_id = menu_id.value;
  modelBanner.blog_form=true;
  try {
    const response = await api.post(`cms/${routeBanner.value}/search`,modelBanner, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosBanner.value = response.data.content;
      totalPagesBanner.value = response.data.totalPages;
      totalElementsBanner.value = response.data.totalElements;
      numberOfElementsBanner.value = response.data.numberOfElements;
      
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


async function modalAgregarBanner(){   
  showModalBanner.value = true;
  title_modal.value = "Agregar Banner";
  showEditor.value = true;
  editar.value = false;
  await nextTick(); 
  initEditorTitle();
  initEditorSubTitle();
}

async function agregarBoton() {
  // si buttons es null, inicialízalo primero
  if (!modelNBanner.buttons) {
    modelNBanner.buttons = []
  }
  const newId = modelNBanner.buttons.length + 1
  const nuevoBoton: Buttons = {
    id: newId,
    title: '',
    add_class: '',
    link: ''
  }
  modelNBanner.buttons.push(nuevoBoton)
  console.log('modelN.buttons', modelNBanner.buttons)
}





async function modalEditarBanner(mo:any){
  await searchGetMenu();
  editar.value = true;
  showModalBanner.value = true;
  title_modal.value = "Modificar Banner";
  showEditor.value = true;
  modelNBanner.title = mo.title;
  modelNBanner.subtitle = mo.subtitle;
 
  image.value = url_image.value + mo.image;
 
  modelNBanner.id = mo.id;

  modelNBanner.buttons = mo.buttons.map((item: any) => ({
    id: item.id,
    link: item.link || "",
    add_class: item.add_class,
    title: item.title
  }));

  await nextTick();
  initEditorTitle();
  initEditorSubTitle();
}



function closeModal(){
  showModalBanner.value = false; showEditor.value = false;
  Object.assign(modelNBanner, new Banners(null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorTitle");
  tinymce.remove("#mieditorSubTitle");
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
          modelNBanner.image = response.data.data;        
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

async function guardarBanner(){
  console.log('guardar');      
  const result = await formAddBanner.value?.validate();
  modelNBanner.menu_id = menu_id.value;
  modelNBanner.blog_form = true;
  console.log('modelN:', modelNBanner);
  
  
  if (result?.valid ) {
    try {
      const response = await api.post(`cms/${routeBanner.value}/create`,modelNBanner, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosBanner.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalBanner.value = false; showEditor.value = false;
          tinymce.remove("#mieditorDescPageContent");
          tinymce.remove("#mieditorTitlePageContent");
          Object.assign(modelNBanner, new Banners(null, null, null, null, null, null, null, null));
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

async function modificarBanner(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddBanner.value?.validate()
  
  if (result?.valid) {
    console.log("Formulario válido ✅", modelNBanner);
    try {
      const response = await api.put(`cms/${routeBanner.value}/update/${modelNBanner.id}`,modelNBanner, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosBanner.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosBanner.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalBanner.value = false; showEditor.value = false;
          Object.assign(modelNBanner, new Banners(null, null, null, null, null, null, null, null));
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

async function initEditorTitle() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorTitle',
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
        .post(`cms/${routeBanner.value}/image-upload`, formData, {
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

    setup: (editorDesc) => {
      editorInstanceTitle = editorDesc;
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
        modelNBanner.title = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(modelNBanner.title || '');
      });      
    }
  });
}

async function initEditorSubTitle() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorSubTitle',
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
      editorInstanceSubTitle = editorTerm;
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
        modelNBanner.subtitle = editorTerm.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorTerm.on('init', () => {
        editorTerm.setContent(modelNBanner.subtitle || '');
      });      
    }
  });
}




async function fnDeleteBanner(mo: any) {
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
    const response = await api.delete(`cms/${routeBanner.value}/delete/${mo.id}`, {
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
          registrosBanner.value = registrosBanner.value.filter((r) => r.id !== mo.id);
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

const imagePositions =  ref({
  itemContents: [
    { value: 'left', name: 'Izquierda'},
    { value: 'right', name: 'Derecha'},
    { value: 'arriba', name: 'Arriba'},
  ],
})



const eliminarBoton = (index:any) => {
  modelNBanner.buttons.splice(index, 1)
}

const headers = [
  { title: 'ID', value: 'id' },
  { title: 'DNI', value: 'dni' },
  { title: 'Correo', value: 'email' },
  { title: 'Teléfono', value: 'phone' },  
]
const search = ref('')

async function exportarExcel() {
    try {
    const response = await axios.get("http://localhost:8081/tarjeta-estilos/cms/descargar-excel", {
        responseType: "blob", 
        headers: {
            Authorization: `Bearer ${token}`
        },
    });

    // Crear URL temporal para descargar
    const blob = new Blob([response.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });

    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "form_blog.xlsx");
    document.body.appendChild(link);
    link.click();
    link.remove();

    window.URL.revokeObjectURL(url);
    } catch (error) {
    console.error("Error al exportar Excel:", error);
    alert("Hubo un problema al generar el archivo Excel.");
    }
}

onMounted(async () => {
  if (localStorage.getItem('token')) {
    await searchGetMenu();
    await searchRegistrosBanners();    
  } else {
    router.push('/')
  }

})

</script>

<template>
  <v-container>
    <!-- Barra de tabs -->
    <v-tabs v-model="tab" bg-color="primary" dark>
      <v-tab value="banners">Banners</v-tab>
      <v-tab value="pageContent" @click="searchRegistrosPageContent">Contenido</v-tab>      
      
    </v-tabs>

    <!-- Contenido sincronizado -->
    <v-window v-model="tab" class="mt-4">
      <v-window-item value="banners">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosBanners()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelBanner.title"
                    label="Buscar por título"
                    variant="outlined"
                    density="comfortable"
                    hide-details
                  />
                </v-col>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelBanner.subtitle"
                    label="Buscar por subtitulo"
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
                  <v-btn text="Agregar" color="info" @click="modalAgregarBanner">
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
                v-for="(item, index) in registrosBanner"
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
                  <v-card-title class="wrap-title" v-html="item.title"></v-card-title>
                  <v-card-subtitle class="wrap-title" v-html="item.subtitle"></v-card-subtitle>
                  <v-card-actions>
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarBanner(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeleteBanner(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosBanner.length==0">
                <div class="p-3 bg-white">
                  <Closable />
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>

      <v-window-item value="pageContent">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosPageContent()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelPageContent.dni"
                    label="Buscar por dni"
                    variant="outlined"
                    density="comfortable"
                    hide-details
                  />
                </v-col>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelPageContent.email"
                    label="Buscar por email"
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
                  <v-btn text="" color="" class="me-1" @click="exportarExcel()">
                    <template v-slot:prepend>
                      <div class="d-flex align-center justify-center" >
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32"><path fill="#20744a" fill-rule="evenodd" d="M28.781 4.405h-10.13V2.018L2 4.588v22.527l16.651 2.868v-3.538h10.13A1.16 1.16 0 0 0 30 25.349V5.5a1.16 1.16 0 0 0-1.219-1.095m.16 21.126H18.617l-.017-1.889h2.487v-2.2h-2.506l-.012-1.3h2.518v-2.2H18.55l-.012-1.3h2.549v-2.2H18.53v-1.3h2.557v-2.2H18.53v-1.3h2.557v-2.2H18.53v-2h10.411Z"/><path fill="#20744a" d="M22.487 7.439h4.323v2.2h-4.323zm0 3.501h4.323v2.2h-4.323zm0 3.501h4.323v2.2h-4.323zm0 3.501h4.323v2.2h-4.323zm0 3.501h4.323v2.2h-4.323z"/><path fill="#fff" fill-rule="evenodd" d="m6.347 10.673l2.146-.123l1.349 3.709l1.594-3.862l2.146-.123l-2.606 5.266l2.606 5.279l-2.269-.153l-1.532-4.024l-1.533 3.871l-2.085-.184l2.422-4.663z"/></svg>
                      </div>
                    </template>
                  </v-btn>
                                    
                </v-col>
              </v-row>
            </v-form>

            <v-divider class="my-6"></v-divider>
            
            

            <v-data-table
                :headers="headers"
                :items="registrosPageContent"
                :search="search"
                :items-per-page="30"
                class="elevation-1"
            >                
            </v-data-table>

          </v-card-text>
        </v-card>
      </v-window-item>

    </v-window>
  </v-container>


  <v-dialog v-model="showModalBanner" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modal }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModal()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddBanner" v-model="valid" lazy-validation>
          <v-row class="mt-1 mb-1" > 
            <v-col cols="12" sm="12" md="12" lg="12" >
              <div v-if="showEditor">     
                <label>Título</label>                
                <textarea id="mieditorTitle" v-model="modelNBanner.title"></textarea>
                <!-- Mensaje de validación -->
                
              </div>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="12" >
              <div v-if="showEditor">     
                <label>Sub Título</label>                
                <textarea id="mieditorSubTitle" v-model="modelNBanner.subtitle"></textarea>
              </div>
            </v-col>
            
            <v-col cols="12" sm="12" md="12" lg="3" >
              <v-text-field label="Etiqueta Seguro" placeholder="Etiqueta seguro" v-model="modelNBanner.label"                 
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" >
              <v-file-input accept="image/*" label="Subir imagen" prepend-icon="mdi-image" @change="onFileChange" class="file-input-custom" />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" >
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" class="d-flex align-start">
                <span class="me-2 mt-3">Agregar botón</span>    
                <v-btn color="primary" @click="agregarBoton" class="btn-fab mt-3">
                    <Icon icon="icon-park-solid:add-one" width="28" height="28"  />
                </v-btn>
            </v-col>   
            <v-col cols="12" sm="12" md="12" lg="12" class="d-flex align-center">
              <v-expansion-panels variant="accordion">
                <v-expansion-panel
                  v-for="(button, index) in modelNBanner.buttons"
                  :key="index"
                >
                  <v-expansion-panel-title class="d-flex  align-center justify-space-between">
                    <span class="me-3">Botón {{ button.id }}</span>
                    <v-btn                      
                        color="red"
                        size="small"
                        class="bg-error"
                        @click.stop="eliminarBoton(index)"
                      >
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path stroke-dasharray="24" stroke-dashoffset="24" d="M12 20h5c0.5 0 1 -0.5 1 -1v-14M12 20h-5c-0.5 0 -1 -0.5 -1 -1v-14"><animate fill="freeze" attributeName="stroke-dashoffset" dur="0.4s" values="24;0"/></path><path stroke-dasharray="20" stroke-dashoffset="20" d="M4 5h16"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.4s" dur="0.2s" values="20;0"/></path><path stroke-dasharray="8" stroke-dashoffset="8" d="M10 4h4M10 9v7M14 9v7"><animate fill="freeze" attributeName="stroke-dashoffset" begin="0.6s" dur="0.2s" values="8;0"/></path></g></svg>
                    </v-btn>
                  </v-expansion-panel-title>

                  <v-expansion-panel-text>
                    <v-row>
                      <v-col cols="12" sm="12" md="12" lg="6" class="d-flex align-center">
                        <v-text-field
                          v-model="button.title"
                          :label="`Título`"
                          placeholder="Agregar Título"
                        />
                      </v-col>
                      <v-col cols="12" sm="12" md="12" lg="6" class="d-flex align-center">
                        <v-text-field
                          v-model="button.link"
                          :label="`Link de botón ${button.id}`"
                          placeholder="https://..."
                        />
                      </v-col>
                      <v-col cols="12" sm="12" md="12" lg="6" class="d-flex align-center">
                        <v-text-field
                          v-model="button.add_class"
                          :label="`Clase css `"
                          placeholder="Agregar clases"
                        />
                      </v-col>
                    </v-row>
                  </v-expansion-panel-text>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-col> 
          </v-row>
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
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editar" @click="guardarBanner">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editar" @click="modificarBanner">
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