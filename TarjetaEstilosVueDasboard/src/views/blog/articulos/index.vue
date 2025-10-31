<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios';

import UiChildCard from '@/components/shared/UiChildCard.vue';
import CardsMedia from "@/components/ui-components/cards/CardsMedia.vue";
import Blog from "@/models/Blog"; 
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';
import { VDateInput } from 'vuetify/labs/VDateInput'
import { format, parseISO } from 'date-fns';
import { DateTime } from "luxon";
import { shallowRef } from 'vue'
import { useDate } from 'vuetify'


import { useToast } from "vue-toastification";
import type { VForm } from "vuetify/components";
import swal from 'sweetalert';
import { Icon } from '@iconify/vue';

import { nextTick } from 'vue';
import tinymce from 'tinymce';
import { wait } from 'ckeditor5';

// Variables
const menu_id = ref(0);
const toast = useToast()
const router = useRouter()
const registros = ref<any[]>([])
const categorias = ref<any[]>([])
const url = ref(Global.url)
const url_image = ref(Global.url_image)

const blog = reactive(new Blog(null, null, null, null, null, null, null, null, null, null));
const blogN = reactive(new Blog(null, null, null, null, null, null, null, null, null, null));

const title_modal = ref('')
const showmessage = ref(false)
const showModal = ref(false)
let totalElements = 0;
let numberOfElements =0;
let page=1;
const size=20;
const token = localStorage.getItem('token');
let totalPages = 0;

const dateRange = ref([]);
const date = shallowRef<Date | null>(null);


const adapter = useDate();
const showEditor = ref(false);
const editorValid = ref(true);

const editorData = ref("");
const formAddBlog = ref<VForm | null>(null)
const valid = ref(false);
const editar = ref(false);
const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 

const imageCard = ref<string | null>(null)
const imageMain = ref<string | null>(null)
const fileCard = ref<File|null>(null);
const fileMain = ref<File|null>(null);
const file = ref<File|null>(null);

const logo = ref<string | null>(null);
const fileLogo = ref<File|null>(null);
const image = ref<string | null>(null);

const rules = {
  required: (value:any) => !!value || "Este campo es obligatorio",
  min3: (value:any) =>
    (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
};

const editorConfig = {
  extraPlugins: [ MyCustomUploadAdapterPlugin ]
};

// ---- UploadAdapter personalizado ----
function MyCustomUploadAdapterPlugin(editor:any) {
  editor.plugins.get("FileRepository").createUploadAdapter = (loader:any) => {
    return new MyUploadAdapter(loader);
  };
}



const validateEditor = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const text = blogN.description?.replace(/<[^>]*>?/gm, "").trim();
  editorValid.value = text ? text.length >= 3 : false;
  console.log('editorValid.value: ',editorValid.value);
  return editorValid.value;
};



class MyUploadAdapter {
  private loader: any;
  constructor(loader:any) {
    this.loader = loader;
  }

  // Empieza la subida
  upload() {
    return this.loader.file.then((file:any) => {
      const data = new FormData();
      data.append("upload", file);

      return fetch(Global.url+"cms/blog/upload-image", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: data
      })
        .then((res) => res.json())
        .then((res) => {
          return {
            default: res.url 
          };
        });
    });
  }

  // En caso de que CKEditor cancele la subida
  abort() {
    // aquí podrías cancelar el fetch si usas AbortController
  }
}

const onEditorReady = (editor: any) => {
  console.log('CKEditor está listo!', editor);
};



async function searchGetMenu() {
  let datosEnviar= {
      slug: 'educacion-financiera',      
  }
  try {
    const response = await api.post("cms/menu/search",datosEnviar, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      menu_id.value = response.data.content[0].id;
      
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function modalAgregar(){
  
  showModal.value = true;
  title_modal.value = "Agregar Blog";
  showEditor.value = true;
  editar.value = false;
  await nextTick(); 
  initEditorDesc();
}

async function modalEditar(blog:any){
  
  editar.value = true;
  showModal.value = true;
  title_modal.value = "Modificar Blog";
  showEditor.value = true;
  blogN.title = blog.title;
  blogN.summary = blog.summary;
  blogN.description = blog.description;
  
  date.value = blog.publicationDate ? parseISO(blog.publicationDate) : null;

  imageCard.value = url_image.value + blog.cardImage;
  imageMain.value = url_image.value + blog.mainImage;
  blogN.id = blog.id;
  const categoryIds = blog.blogCategories.map((item: any) => item.category.id);
  blogN.category_id = categoryIds
  await nextTick(); 
  initEditorDesc();
}




//funcion para obtener el archivo cargado de la imagen de card
async function onFileChangeCard(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    file.value = target.files[0] 
    const reader = new FileReader()
    reader.onload = e => {
      imageCard.value = e.target?.result as string
    }
    reader.readAsDataURL(file.value) 
    const formData = new FormData();
    formData.append("image", file.value as File); 
    formData.append("type", "card"); 
      
    try {
      const response = await api.post("cms/blog/image",formData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        if(response.data.status===201 && response.data.data){
          flassmessagesuccess(response.data.message,20);
          blogN.card_image = response.data.data;        
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
}

//funcion para obtener el archivo cargado de la imagen main
async function onFileChangeMain(event: Event){
  const targetLogo = event.target as HTMLInputElement
  if (targetLogo.files && targetLogo.files[0]) {
    fileLogo.value = targetLogo.files[0] // ✅ Aquí sí es un File (Blob)
    const readerLogo = new FileReader()
    readerLogo.onload = el => {
      imageMain.value = el.target?.result as string
    }
    readerLogo.readAsDataURL(fileLogo.value)
    const formData = new FormData();
    formData.append("image", file.value as File); 
    formData.append("type", "logo"); 
      
    try {
      const response = await api.post("cms/blog/image",formData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        if(response.data.status===201 && response.data.data){
          flassmessagesuccess(response.data.message,20);
          blogN.main_image = response.data.data;        
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
}

//formatear la fecha para enviar a guardar
function formatIso (date:any) {
  return adapter.toISO(date)
}

//formatear el label para visualizar el rango de fechas 
const formatDateRanges2 = (dates: any) => {
  console.log('length:',dates);
  if (!dates) return ""
  if (Array.isArray(dates) && dates.length > 1) {
    console.log('length:',dates.length);
    const start = DateTime.fromJSDate(dates[0]).toFormat("dd/MM/yyyy")
    const end = DateTime.fromJSDate(dates[(dates.length - 1)]).toFormat("dd/MM/yyyy")
    return `${start} - ${end}`
  }
  return DateTime.fromJSDate(dates).toFormat("dd/MM/yyyy")
}

const formatDate = (dates: any) => {
  console.log('date para formatear:',dates);
  const fecha = DateTime.fromJSDate(dates).toFormat("dd/MM/yyyy");
  return `${fecha}`
}


// Funciones
async function consultarRegistros() {
  registros.value = [];
  try {
    const response = await api.get(`cms/blog/${page}/${size}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registros.value = response.data.content;
      totalPages = response.data.totalPages;
      totalElements = response.data.totalElements;
      numberOfElements = response.data.numberOfElements
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistros() {
  const startDate = computed(() => dateRange.value?.[0] ?? null)
  const endDate   = computed(() => dateRange.value?.[(dateRange.value.length - 1)] ?? null)
  console.log('dateRange: ',dateRange);
  console.log('startDate:',startDate);
  console.log('endDate:',endDate);

  registros.value = [];
  let datosEnviar= {
    title: blog.title,
    description: blog.description,
    summary: blog.summary,
    startDate: startDate.value ? formatIso(startDate.value) : null,
    endDate: endDate.value ? formatIso(endDate.value) : null,
    category_id: blog.category_id,
    menu_id: menu_id.value,
    page: page,
    size: size,
  }
  try {
    const response = await api.post("cms/blog/search",datosEnviar, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registros.value = response.data.content;
      totalPages = response.data.totalPages;
      totalElements = response.data.totalElements;
      numberOfElements = response.data.numberOfElements
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
    const response = await api.get(`cms/blog/${pageNew}/${size}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registros.value = response.data.content;
      totalPages = response.data.totalPages;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);
    
  }
}

function getCategories(){
  axios
    .get(url.value + 'cms/categoryAll',{
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

function flassmessagesuccess(message:string,time: number){                
    toast.success(`${message}`, {
        timeout: time
    });
}

function getPublicationDate() {
  if (!date.value) return null;

  // Si `date.value` ya es un objeto Date
  const d = new Date(date.value);
  return d.toISOString(); // Ejemplo: 2025-02-11T05:00:00.000Z
  // o si tu backend espera formato corto:
  // return d.toISOString().split('T')[0]; // "2025-02-11"
}

async function guardar(){
  console.log('guardar');
  console.log('date:',date);  
  blogN.menu_id = menu_id.value;
  blogN.publication_date = getPublicationDate();
  const result = await formAddBlog.value?.validate()
  const ckValid = await validateEditor();
  console.log('ckValid:', ckValid);
  if (result?.valid && ckValid) {
    try {
      const response = await api.post("cms/blog/create",blogN, {
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
          Object.assign(blogN, new Blog(null, null, null, null, null, null, null, null, null, null));
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
  console.log('date:',date);
  blogN.menu_id = menu_id.value;
  const result = await formAddBlog.value?.validate()
  const ckValid = await validateEditor();
  console.log('ckValid:', ckValid);
  if (result?.valid && ckValid) {
    
    try {
      const response = await api.put(`cms/blog/update/${blogN.id}`,blogN, {
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
          Object.assign(blogN, new Blog(null, null, null, null, null, null, null, null, null, null));
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
  Object.assign(blogN, new Blog(null, null, null, null, null, null, null, null, null, null));
  imageCard.value = null;
  imageMain.value = null;
  fileCard.value = null;
  fileMain.value = null;
  date.value = null;
  tinymce.remove("#mieditorDesc");
  tinymce.remove("#mieditorTerm");
}

async function fnDelete(blog: any) {
  console.log('blogEliminar:', blog);

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
    const response = await api.delete(`cms/blog/delete/${blog.id}`, {
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

let editorInstanceDesc:any = null;

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
        .post("cms/blog/upload-image", formData, {
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
        blogN.description = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(blogN.description || '');
      });      
    }
  });
}

// Ciclo de vida
onMounted(async() => {
  if (localStorage.getItem('token')) {
    showModal.value = false;

    await searchGetMenu();


    getCategories();
    consultarRegistros();
    initEditorDesc();
  } else {
    router.push('/login')
  }
})



</script>

<template>
  <v-row >
    <v-col cols="12">
      <UiChildCard title="Buscar Noticia" class="py-1">
        <v-form @submit.prevent="searchRegistros()">
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-3 v-col-12">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="blog.title"
              ></v-text-field>
            </div>            
            <div class="v-col-md-3 v-col-12">
              <v-select chips multiple :items="categorias" label="Categorías" v-model="blog.category_id" />
            </div>
            <div class="v-col-md-3 v-col-12">
              <v-date-input v-model="dateRange" label="Rango de fechas" multiple="range" prepend-icon="mdi-calendar-range" clearable 
                :display-value="formatDateRanges2(dateRange)" :value="formatDateRanges2(dateRange)" placeholder="dd/mm/YYYY" variant="outlined"
              />                            
            </div>            
            <div class="v-col-md-1 v-col-3 d-flex">
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
          <div class="text-h5">Noticias ({{ totalElements }})</div>
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
    <v-col v-for="(item, index) in registros" :key="item.id || index" cols="12" sm="12" lg="4" class="d-flex align-items-stretch" >
      
        <CardsMedia :img="url_image+item.cardImage" :summary="item.summary" 
          :publicationDate="item.publicationDate"
          :title="item.title" :categories="item.blogCategories"
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
        <v-form ref="formAddBlog" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="blogN.title" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-textarea label="Resumen" placeholder="Escribe el resumen" v-model.trim="blogN.summary" outlined rows="1" auto-grow 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-textarea>
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-select chips multiple :items="categorias" label="Categorías" v-model="blogN.category_id" />
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-date-input v-model="date" label="Fecha de publicación" prepend-icon="mdi-calendar-range" variant="outlined"
                :rules="[rules.required]"
                required 
                :display-format="'dd/MM/yyyy'" placeholder="dd/mm/YYYY" 
              ></v-date-input>
            </div>
            <div class="v-col-md-12 v-col-12">
              <div >
                <label>Descripción</label>
                <textarea id="mieditorDesc" v-model="blogN.description"></textarea>          
                <!-- Mensaje de validación -->
                <small v-if="!editorValid" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-file-input accept="image/*" label="Subir imagen Card" prepend-icon="mdi-image" @change="onFileChangeCard" class="file-input-custom" 
                :rules="!editar ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-img v-if="imageCard" :src="imageCard" max-width="300" class="rounded-xl" />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-file-input  accept="image/*" label="Subir imagen Principal" prepend-icon="mdi-image" @change="onFileChangeMain" class="file-input-custom" 
                :rules="!editar ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-img v-if="imageMain" :src="imageMain" max-width="300" class="rounded-xl" />
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
