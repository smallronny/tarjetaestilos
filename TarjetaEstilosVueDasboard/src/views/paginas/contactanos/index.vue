<script setup lang="ts">
import { ref, reactive, onMounted  } from 'vue'
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
import Department from '@/models/Department';



const tab = ref("general") 
interface Item {
  id: number;
  name: string;
  image: string;
}
// Variables

const slug_menu = ref('contactanos');
const router = useRouter()
const toast = useToast()
const departaments = ref<any[]>([]);
const url_image = ref(Global.url_image);

const routeBanner = ref<string|null>(null);
const routeDepartment = ref<string|null>(null);

const token = localStorage.getItem('token');

const registrosBanner = ref<any[]>([]);
const registrosDepartment = ref<any[]>([]);

const modelBanner = reactive(new Banners(null, null, null, null, null, null, null, null));
const modelNBanner = reactive(new Banners(null, null, null, null, null, null, null, null));



const modelDepartment = reactive(new Department(null, null, null, null, null, null, null));
const modelNDepartment = reactive(new Department(null, null, null, null, null, null, null));



const totalElementsBanner = ref(0);
const numberOfElementsBanner = ref(0);
const totalPagesBanner = ref(0);


const totalElementsDepartment = ref(0);
const numberOfElementsDepartment = ref(0);
const totalPagesDepartment = ref(0);


const title_modal = ref('');
const title_modalDepartment = ref('');


const showModalBanner = ref(false);
const showModalDepartment = ref(false)


const showEditor = ref(false);
const showEditorDepartment = ref(false);

const editorSubTitleValid = ref(true);
const editorTitleValid = ref(true);

const editorValidDepartment = ref(true);



const formAddBanner = ref<VForm | null>(null);
const formAddDepartment = ref<VForm | null>(null);

const valid = ref(false);
const editar = ref(false);
const editarDepartment = ref(false);


const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 
const date = shallowRef<Date | null>(null);
const image = ref<string | null>(null);
const file = ref<File|null>(null);

const logo = ref<string | null>(null);
const fileLogo = ref<File|null>(null);
const showmessage = ref(false);

routeBanner.value = "banners";
routeDepartment.value = "department";

const rules = {
  required: (value:any) => !!value || "Este campo es obligatorio",
  min3: (value:any) =>
    (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
  numeric: (v: any) => /^(\d+(\.\d+)?)?$/.test(v) || 'Debe ser un número válido',
  range: (v: any) => (v >= 0 && v <= 100) || 'El descuento debe estar entre 0 y 100'
};

const validateEditorTitle = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const text = modelNBanner.title?.replace(/<[^>]*>?/gm, "").trim();
  editorTitleValid.value = text ? text.length >= 3 : false;
  console.log('editorValid.value: ',editorTitleValid.value);
  return editorTitleValid.value;
};

const validateEditorSubTitle = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const textTerm = modelNBanner.subtitle?.replace(/<[^>]*>?/gm, "").trim();
  editorSubTitleValid.value = textTerm ? textTerm.length >= 3 : false;
  console.log('editorValid.value: ',editorSubTitleValid.value);
  return editorSubTitleValid.value;
};


async function searchGetMenu() {
  registrosBanner.value = [];
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
      modelBanner.menu_id = response.data.content[0].id;
                  
      
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}



async function searchRegistrosBanners() {
  registrosBanner.value = [];
  
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



async function searchRegistrosDepartment() {
  registrosDepartment.value = [];  
  try {
    const response = await api.post(`cms/${routeDepartment.value}/search`,modelDepartment, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosDepartment.value = response.data.content;
      totalPagesDepartment.value = response.data.totalPages;
      totalElementsDepartment.value = response.data.totalElements;
      numberOfElementsDepartment.value = response.data.numberOfElements;
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



async function modalAgregarDepartment(){   
  showModalDepartment.value = true;
  title_modalDepartment.value = "Agregar departamento";
  showEditorDepartment.value = true;
  editarDepartment.value = false;
  await nextTick(); 
  
  console.log('modelDepartment:',modelNDepartment);
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
  await nextTick();
  initEditorTitle();
  initEditorSubTitle();
}



async function modalEditarDepartment(mo:any){
  await searchGetMenu();
  editarDepartment.value = true;
  showModalDepartment.value = true;
  title_modalDepartment.value = "Modificar contenido";
  showEditorDepartment.value = true;
  modelNDepartment.label_phone = mo.label_phone;
  modelNDepartment.phone = mo.phone;  
  modelNDepartment.id = mo.id;
  modelNDepartment.name = mo.name;

  image.value = url_image.value + mo.image;

  await nextTick();
  
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



function closeModalDepartment(){
  showModalDepartment.value = false; showEditorDepartment.value = false;
  Object.assign(modelNDepartment, new Department(null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  
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
  console.log('modelN:', modelNBanner);
  const ckValid = await validateEditorTitle();
  const ckValidTerm = await validateEditorSubTitle();
  if (result?.valid && ckValid && ckValidTerm ) {
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

function initEditorSubTitle() {
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




async function guardarDepartment(){
  console.log('guardar');      
  const result = await formAddDepartment.value?.validate();
  console.log('modelNShortcuts:', modelNDepartment);
  
  if (result?.valid ) {
    try {
      const response = await api.post(`cms/${routeDepartment.value}/create`,modelNDepartment, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosDepartment.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalDepartment.value = false; showEditorDepartment.value = false;
          Object.assign(modelNDepartment, new Department(null, null, null, null, null, null, null));
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


async function modificarDepartment(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddDepartment.value?.validate()
  
  if (result?.valid) {
    console.log("Formulario válido atajo ✅", modelNDepartment);
    try {
      const response = await api.put(`cms/${routeDepartment.value}/update/${modelNDepartment.id}`,modelNDepartment, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosDepartment.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosDepartment.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalDepartment.value = false; showEditor.value = false;
          Object.assign(modelNDepartment, new Department(null, null, null, null, null, null, null));
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


async function fnDeleteDepartment(mo: any) {
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
    const response = await api.delete(`cms/${routeDepartment.value}/delete/${mo.id}`, {
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
          registrosDepartment.value = registrosDepartment.value.filter((r) => r.id !== mo.id);
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
      <v-tab value="department" @click="searchRegistrosDepartment">Departamentos</v-tab>
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

      <v-window-item value="department">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosDepartment()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelDepartment.name"
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
                  <v-btn text="Agregar" color="info" @click="modalAgregarDepartment">
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
                v-for="(item, index) in registrosDepartment"
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
                  
                  <v-card-title v-html="item.name"></v-card-title>
                  <v-card-subtitle v-html="item.phone"></v-card-subtitle>
                  <v-card-actions>
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarDepartment(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeleteDepartment(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosDepartment.length==0">
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
                <small v-if="!editorTitleValid" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="12" >
              <div v-if="showEditor">     
                <label>Sub Título</label>                
                <textarea id="mieditorSubTitle" v-model="modelNBanner.subtitle"></textarea>
                <!-- Mensaje de validación -->
                <small v-if="!editorSubTitleValid" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </v-col>
            
            <v-col cols="12" sm="12" md="12" lg="6" >
              <v-file-input accept="image/*" label="Subir imagen" prepend-icon="mdi-image" @change="onFileChange" class="file-input-custom" 
                :rules="!editar ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="6" >
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
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


  <v-dialog v-model="showModalDepartment" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modalDepartment }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModalDepartment()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddDepartment" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Titulo" placeholder="Nombre" v-model="modelNDepartment.name" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>            
            <div class="v-col-md-6 v-col-12">
              <v-text-field
                v-model="modelNDepartment.label_phone"
                label="Etiqueta Teléfono" placeholder="Etiqueta de teléfono"
                color="primary" outlined
              ></v-text-field>
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-text-field
                v-model="modelNDepartment.phone"
                label="Teléfono" placeholder="Teléfono"
                color="primary" outlined
              ></v-text-field>
            </div>            
                     
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="sticky bottom-0 bg-white z-10">
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModalDepartment()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editarDepartment" @click="guardarDepartment">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editarDepartment" @click="modificarDepartment">
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