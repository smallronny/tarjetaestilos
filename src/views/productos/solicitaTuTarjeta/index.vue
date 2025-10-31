<script setup lang="ts">
import { ref, reactive, onMounted, watch  } from 'vue'
import { useRouter } from 'vue-router';
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';
import axios from 'axios';
import type { VForm } from "vuetify/components";
import { Icon } from '@iconify/vue';

import { useToast } from "vue-toastification";
import { format, parseISO } from 'date-fns';
import { shallowRef } from 'vue';
import { nextTick } from 'vue';
import tinymce from 'tinymce';


import Banners from '@/models/Banners';
import PageContent from '@/models/PageContent';
import Cta from '@/models/Cta'; 
import FrequentlyAskedQuestion from '@/models/FrequentlyAskedQuestion';
import Promotion from '@/models/Promotion';

interface Buttons {
  id: number
  title: string
  add_class: string
  link: string
}

const buttons = ref<any[]>([]);

const tab = ref("general") 
interface Item {
  id: number;
  name: string;
  image: string;
}
// Variables
const affiliates = ref<any[]>([]);
const editorValidTerm = ref(true);
const editorValid = ref(true);
const url = ref(Global.url);
const categorias = ref<any[]>([])
const url_logo = ref(Global.url_image);
const selected = ref<Item | null>(null);
const slug_menu = ref('solicita-tu-tarjeta');
const menu_id = ref(0);
const menuPromotion_id = ref(0);
const router = useRouter()
const toast = useToast()
const url_image = ref(Global.url_image);
const showmessage = ref(false)
const routeBanner = ref<string|null>(null);

const routePageContent = ref<string|null>(null);
const routeCta = ref<string|null>(null);
const routeFaq = ref<string|null>(null);
const routePromotion = ref<string|null>(null);
const departamentos = ref<any[]>([]);
const token = localStorage.getItem('token');

const registrosBanner = ref<any[]>([]);
const registrosCorporateData = ref<any[]>([]);
const registrosPageContent = ref<any[]>([]);
const registrosCta = ref<any[]>([]);
const registrosFaq = ref<any[]>([]);
const registrosPromotion = ref<any[]>([]);

const modelBanner = reactive(new Banners(null, null, null, null, null, null, null, null));
const modelNBanner = reactive(new Banners(null, null, null, null, null, null, null, null));

const modelPageContent = reactive(new PageContent(null, null, null, null, null, null, null));
const modelNPageContent = reactive(new PageContent(null, null, null, null, null, null, null));

const modelCta = reactive(new Cta(null, null, null, null, null, null, null));
const modelNCta = reactive(new Cta(null, null, null, null, null, null, null));

const modelFaq = reactive(new FrequentlyAskedQuestion(null, null, null, null, null, null));
const modelNFaq = reactive(new FrequentlyAskedQuestion(null, null, null, null, null, null));

const modelPromotion = reactive(new Promotion(null, null, null, null, null, null, null, null, null));
const modelNPromotion = reactive(new Promotion(null, null, null, null, null, null, null, null, null));

const totalElementsBanner = ref(0);
const numberOfElementsBanner = ref(0);
const totalPagesBanner = ref(0);

const totalElementsPromotion = ref(0);
const numberOfElementsPromotion = ref(0);
const totalPagesPromotion = ref(0);

const totalElementsPageContent = ref(0);
const numberOfElementsPageContent = ref(0);
const totalPagesPageContent = ref(0);

const totalElementsCta = ref(0);
const numberOfElementsCta = ref(0);
const totalPagesCta = ref(0);

const totalElementsFaq = ref(0);
const numberOfElementsFaq = ref(0);
const totalPagesFaq = ref(0);

const title_modal = ref('');
const title_modalPageContent = ref('');
const title_modalCta = ref('');
const title_modalFaq = ref('');
const title_modalPromotion = ref('');


const showModalBanner = ref(false);
const showModalPageContent = ref(false);
const showModalCta = ref(false);
const showModalFaq = ref(false);
const showModalPromotion = ref(false)


const showEditor = ref(false);
const showEditorPageContent = ref(false);
const showEditorCta = ref(false);
const showEditorFaq = ref(false);
const showEditorPromotion = ref(false);

const editorSubTitleValid = ref(true);
const editorTitleValid = ref(true);

const editorValidPageContent = ref(true);


const formAddBanner = ref<VForm | null>(null);
const formAddPageContent = ref<VForm | null>(null);
const formAddCta = ref<VForm | null>(null);
const formAddFaq = ref<VForm | null>(null);
const formAddPromotion = ref<VForm | null>(null);

const valid = ref(false);
const editar = ref(false);
const editarPageContent = ref(false);
const editarCta = ref(false);
const editarFaq = ref(false);
const editarPromotion = ref(false);


const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 
const date = shallowRef<Date | null>(null);
const image = ref<string | null>(null);
const imageCta = ref<string | null>(null);
const file = ref<File|null>(null);

const logo = ref<string | null>(null);
const fileLogo = ref<File|null>(null);

routeBanner.value = "banners";
routePageContent.value = "page-content";
routeCta.value = "cta";
routeFaq.value = "faq";
routePromotion.value = "promotion";

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

async function modalAgregarPageContent(){ 
  await searchGetMenu();
  showModalPageContent.value = true;
  title_modalPageContent.value = "Agregar contenido";
  showEditorPageContent.value = true;
  editarPageContent.value = false;
  await nextTick(); 
  initEditorDescPageContent();
  console.log('modelPageContent:',modelNPageContent);
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

function closeModal(){
  showModalBanner.value = false; showEditor.value = false;
  Object.assign(modelNBanner, new Banners(null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorTitle");
  tinymce.remove("#mieditorSubTitle");
}

function closeModalPageContent(){
  showModalPageContent.value = false; showEditorPageContent.value = false;
  Object.assign(modelNPageContent, new PageContent(null, null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorDescPageContent");
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

async function guardarBanner(){
  console.log('guardar');      
  const result = await formAddBanner.value?.validate();
  modelNBanner.menu_id = menu_id.value;
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
  // si buttons es null, inicialízalo primero
  
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
          showModalPageContent.value = false; showEditor.value = false;
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
        const editorId = `mieditorDescItemContent_${index}`

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

async function searchRegistrosCta() {
  registrosCta.value = [];  
  modelCta.menu_id = menu_id.value;
  try {
    const response = await api.post(`cms/${routeCta.value}/search`,modelCta, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosCta.value = response.data.content;
      totalPagesCta.value = response.data.totalPages;
      totalElementsCta.value = response.data.totalElements;
      numberOfElementsCta.value = response.data.numberOfElements;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistrosFaq() {
  registrosFaq.value = [];
  modelFaq.menu_id = menu_id.value;
  try {
    const response = await api.post(`cms/${routeFaq.value}/search`,modelFaq, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosFaq.value = response.data.content;
      totalPagesFaq.value = response.data.totalPages;
      totalElementsFaq.value = response.data.totalElements;
      numberOfElementsFaq.value = response.data.numberOfElements;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function modalAgregarCta(){ 
  showModalCta.value = true;
  title_modalCta.value = "Agregar Cta";
  showEditorCta.value = true;
  editarCta.value = false;
  await nextTick(); 
  console.log('modelCta:',modelNCta);
}

async function modalAgregarFaq(){ 
  showModalFaq.value = true;
  title_modalFaq.value = "Agregar preguntas";
  showEditorFaq.value = true;
  editarFaq.value = false;
  await nextTick(); 
  console.log('modelFaq:',modelNFaq);
}

async function modalEditarCta(mo:any){
  editarCta.value = true;
  showModalCta.value = true;
  title_modalCta.value = "Modificar cta";
  showEditorCta.value = true;
  modelNCta.url = mo.url;
  imageCta.value = url_image.value + mo.image;
  modelNCta.id = mo.id;
  modelNCta.menu_id = mo.menu.id;
  await nextTick();  
}

async function modalEditarFaq(mo:any){
  editarFaq.value = true;
  showModalFaq.value = true;
  title_modalFaq.value = "Modificar cta";
  showEditorFaq.value = true;
  modelNFaq.question = mo.question;
  modelNFaq.answer = mo.answer;
  modelNFaq.id = mo.id;
  modelNFaq.menu_id = mo.menu.id;
  await nextTick();  
}

function closeModalCta(){
  showModalCta.value = false; showEditorCta.value = false;
  Object.assign(modelNCta, new Cta(null, null, null, null, null, null, null));
  imageCta.value = null;
  file.value = null;
  date.value = null;
  
}

function closeModalFaq(){
  showModalFaq.value = false; showEditorFaq.value = false;
  Object.assign(modelNFaq, new FrequentlyAskedQuestion(null, null, null, null, null, null, null));  
  date.value = null;
}


async function onFileChangeCta(event: Event){
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    file.value = target.files[0] 
    const reader = new FileReader()
    reader.onload = e => {
      imageCta.value = e.target?.result as string
    }
    reader.readAsDataURL(file.value) 
    const formData = new FormData();
    formData.append("image", file.value as File); 
    formData.append("type", "card"); 
      
    try {
      const response = await api.post(`cms/${routeCta.value}/image`,formData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        if(response.data.status===201 && response.data.data){
          flassmessagesuccess(response.data.message,20);
          modelNCta.image = response.data.data;        
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
  
}



async function guardarCta(){
  console.log('guardar');      
  const result = await formAddCta.value?.validate();
  modelNCta.menu_id = menu_id.value;
  console.log('modelNCta:', modelNCta);
  
  if (result?.valid ) {
    try {
      const response = await api.post(`cms/${routeCta.value}/create`,modelNCta, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosCta.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalCta.value = false; showEditorCta.value = false;
          Object.assign(modelNCta, new Cta(null, null, null, null, null, null, null));
          imageCta.value = null;
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

async function guardarFaq(){
  console.log('guardar');      
  const result = await formAddFaq.value?.validate();
  modelNFaq.menu_id = menu_id.value;
  console.log('modelNFaq:', modelNFaq);
  
  if (result?.valid ) {
    try {
      const response = await api.post(`cms/${routeFaq.value}/create`,modelNFaq, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosFaq.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalFaq.value = false; showEditorFaq.value = false;
          Object.assign(modelNFaq, new FrequentlyAskedQuestion(null, null, null, null, null, null, null));
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


async function modificarCta(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddCta.value?.validate()
  
  modelNCta.menu_id = menu_id.value;

  if (result?.valid) {
    console.log("Formulario válido Cta ✅", modelNCta);
    try {
      const response = await api.put(`cms/${routeCta.value}/update/${modelNCta.id}`,modelNCta, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosCta.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosCta.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalCta.value = false; showEditor.value = false;
          Object.assign(modelNCta, new Cta(null, null, null, null, null, null, null));
          imageCta.value = null;
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



async function modificarFaq(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddFaq.value?.validate();
  modelNFaq.menu_id = menu_id.value;
  
  if (result?.valid) {
    console.log("Formulario válido Faq ✅", modelNFaq);
    try {
      const response = await api.put(`cms/${routeFaq.value}/update/${modelNFaq.id}`,modelNFaq, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosFaq.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosFaq.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalFaq.value = false; showEditor.value = false;
          Object.assign(modelNFaq, new FrequentlyAskedQuestion(null, null, null, null, null, null, null));
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

async function fnDeleteCta(mo: any) {
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
    const response = await api.delete(`cms/${routeCta.value}/delete/${mo.id}`, {
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
          registrosCta.value = registrosCta.value.filter((r) => r.id !== mo.id);
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

async function fnDeleteFaq(mo: any) {
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
    const response = await api.delete(`cms/${routeFaq.value}/delete/${mo.id}`, {
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
          registrosFaq.value = registrosFaq.value.filter((r) => r.id !== mo.id);
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
      
      menuPromotion_id.value = response.data.content[0].id;      
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistrosPromotions() {
  registrosPromotion.value = [];  
  modelPromotion.exclusive = true;
  modelPromotion.menu_id = menuPromotion_id.value;
  try {
    const response = await api.post(`cms/${routePromotion.value}/search`,modelPromotion, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosPromotion.value = response.data.content;
      totalPagesPromotion.value = response.data.totalPages;
      totalElementsPromotion.value = response.data.totalElements;
      numberOfElementsPromotion.value = response.data.numberOfElements;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function modalAgregarPromotion(){ 
  showModalPromotion.value = true;
  title_modalPromotion.value = "Agregar Promoción";
  showEditorPromotion.value = true;
  editarPromotion.value = false;
  await nextTick(); 
  initEditorDesc();
  initEditorTerm();
  console.log('modelPromotion:',modelNPromotion);
}


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
        modelNPromotion.description = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(modelNPromotion.description || '');
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
        modelNPromotion.terms_conditions = editorTerm.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorTerm.on('init', () => {
        editorTerm.setContent(modelNPromotion.terms_conditions || '');
      });      
    }
  });
}

async function modalEditarPromotion(mo:any){
  
  editarPromotion.value = true;
  showModalPromotion.value = true;
  title_modalPromotion.value = "Modificar Promoción";
  showEditorPromotion.value = true;
  modelNPromotion.title = mo.title;
  modelNPromotion.discount = mo.discount;
  modelNPromotion.description = mo.description;
  modelNPromotion.terms_conditions = mo.terms_conditions
  
  
  date.value = mo.publicationDate ? parseISO(mo.publicationDate) : null;

  image.value = url_image.value + mo.image;
  logo.value = url_logo.value + mo.logo;
  modelNPromotion.id = mo.id;
  const categoryIds = mo.promotionsCategories.map((item: any) => item.promotionCategory.id);
  modelNPromotion.categoryIds = categoryIds;
  modelNPromotion.department_id =mo.department.id;
  modelNPromotion.isnew = mo.isnew;
  modelNPromotion.outstanding = mo.outstanding;
  modelNPromotion.featured = mo.featured;
  modelNPromotion.promotion_link = mo.promotion_link;
  modelNPromotion.promotion_map = mo.promotion_map;
  modelNPromotion.menu_id = mo.menu.id;
  modelNPromotion.affiliates_id = mo.affiliates.id;
  modelNPromotion.home = true;
  selected.value = mo.affiliates.id;

  await nextTick();
  await initEditorDesc();
  await initEditorTerm();
}


async function fnDeletePromotion(mo: any) {
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
    const response = await api.delete(`cms/${routePromotion.value}/delete/${mo.id}`, {
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
          registrosPromotion.value = registrosPromotion.value.filter((r) => r.id !== mo.id);
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


function closeModalPromotion(){
  showModalPromotion.value = false; showEditorPromotion.value = false;
  Object.assign(modelNPromotion, new Promotion(null, null, null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorDesc");
  tinymce.remove("#mieditorTerm");
}

async function getCategories(){
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

async function consultarDepartamentos() {
  departamentos.value = [];

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


async function onFileChangePromotion(event: Event){
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
          modelNPromotion.image = response.data.data;        
        }
      } else {
        showmessage.value = true;
      }
    } catch (error: any) {
      console.error("Error en petición:", error);    
    }
  }
  
}


const validateEditorDesk = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const text = modelNPromotion.description?.replace(/<[^>]*>?/gm, "").trim();
  editorValid.value = text ? text.length >= 3 : false;
  console.log('editorValid.value: ',editorValid.value);
  return editorValid.value;
};


const validateEditorTerm = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const textTerm = modelNPromotion.terms_conditions?.replace(/<[^>]*>?/gm, "").trim();
  editorValidTerm.value = textTerm ? textTerm.length >= 3 : false;
  console.log('editorValid.value: ',editorValidTerm.value);
  return editorValidTerm.value;
};


async function guardarPromotion(){
  console.log('guardar');      
  const result = await formAddPromotion.value?.validate();
  console.log('modelN:', modelNPromotion);
  const ckValid = await validateEditorDesk();
  const ckValidTerm = await validateEditorTerm();
  modelNPromotion.menu_id = menuPromotion_id.value;
  modelNPromotion.affiliates_id = selected.value?.id ?? null;
  modelNPromotion.exclusive=true;
  if (result?.valid && ckValid && ckValidTerm ) {
    try {
      const response = await api.post(`cms/${routePromotion.value}/create`,modelNPromotion, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosPromotion.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalPromotion.value = false; showEditorPromotion.value = false;
          Object.assign(modelNPromotion, new Promotion(null, null, null, null, null, null, null, null, null, null, null,null,null));
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

async function modificarPromotion(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddPromotion.value?.validate();
  modelNPromotion.menu_id = menuPromotion_id.value;
  modelNPromotion.affiliates_id = selected.value?.id ?? modelNPromotion.affiliates_id;
  modelNPromotion.exclusive=true;
  if (result?.valid) {
    console.log("Formulario válido promocion ✅", modelNPromotion);
    try {
      const response = await api.put(`cms/${routePromotion.value}/update/${modelNPromotion.id}`,modelNPromotion, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosPromotion.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosPromotion.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalPromotion.value = false; showEditor.value = false;
          Object.assign(modelNPromotion, new Promotion(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
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

async function getAffiliates(){
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


onMounted(async () => {
  if (localStorage.getItem('token')) {
    await searchGetMenu();
    await searchGetMenuPromotion();
    await searchRegistrosBanners();
    await consultarDepartamentos();
    await getCategories();
    await getAffiliates();
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
      <v-tab value="cta" @click="searchRegistrosCta">Cta</v-tab>
      <v-tab value="faq" @click="searchRegistrosFaq">Preguntas Frequentes</v-tab> 
      <v-tab value="promotion" @click="searchRegistrosPromotions">Promociones</v-tab> 
      
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

      <v-window-item value="cta">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosCta()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelCta.url"
                    label="Buscar por url"
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
                  <v-btn text="Agregar" color="info" @click="modalAgregarCta()">
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
                v-for="(item, index) in registrosCta"
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
                  <v-card-title v-html="item.url"></v-card-title>
                  <v-card-actions>
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarCta(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeleteCta(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosCta.length==0">
                <div class="p-3 bg-white">
                  <Closable />
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>

      <v-window-item value="faq">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosFaq()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelFaq.question"
                    label="Buscar por pregunta"
                    variant="outlined"
                    density="comfortable"
                    hide-details
                  />
                </v-col>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelFaq.answer"
                    label="Buscar por respuesta"
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
                  <v-btn text="Agregar" color="info" @click="modalAgregarFaq()">
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
                v-for="(item, index) in registrosFaq"
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
                  <v-card-title v-html="item.question"></v-card-title>
                  <v-card-actions>
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarFaq(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeleteFaq(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosFaq.length==0">
                <div class="p-3 bg-white">
                  <Closable />
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-window-item>

      
      <v-window-item value="promotion">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosPromotions()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelPromotion.title"
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
                  <v-btn text="Agregar" color="info" @click="modalAgregarPromotion">
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
                v-for="(item, index) in registrosPromotion"
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
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarPromotion(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeletePromotion(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosPromotion.length==0">
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
                
              </div>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="12" >
              <div v-if="showEditor">     
                <label>Sub Título</label>                
                <textarea id="mieditorSubTitle" v-model="modelNBanner.subtitle"></textarea>
              </div>
            </v-col>
            
            <v-col cols="12" sm="12" md="12" lg="6" >
              <v-file-input accept="image/*" label="Subir imagen" prepend-icon="mdi-image" @change="onFileChange" class="file-input-custom" 
                
              />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" >
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" class="d-flex align-center">
                <span class="me-2">Agregar botón</span>    
                <v-btn color="primary" @click="agregarBoton" class="btn-fab">
                    <Icon icon="icon-park-solid:add-one" width="28" height="28"  />
                </v-btn>
            </v-col>   
            <v-col cols="12" sm="12" md="12" lg="12" class="d-flex align-center">
              <v-expansion-panels variant="accordion">
                <v-expansion-panel
                  v-for="button in modelNBanner.buttons"
                  :key="button.id"
                >
                  <v-expansion-panel-title>
                    Botón {{button.id}}
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
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelNPageContent.title" 
                
                 
              ></v-text-field>
            </div>            
            <div class="v-col-md-6 v-col-12">
              <v-select
                v-model="modelNPageContent.image_position"
                label="Posición de imagen" placeholder="posición de imagen"
                :items="imagePositions.itemContents"
                item-title="name"
                item-value="value"
                color="primary" outlined
              ></v-select>
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
            <v-col cols="12" sm="12" md="12" lg="6" class="d-flex align-center justify-end">
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
                  <v-expansion-panel-title>
                    Item {{ item.id }}
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

  
  <v-dialog v-model="showModalCta" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modalCta }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModalCta()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddCta" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Url" placeholder="Url" v-model="modelNCta.url" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>            
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-file-input accept="image/*" label="Subir imagen" prepend-icon="mdi-image" @change="onFileChangeCta" class="file-input-custom" 
                :rules="!editarCta ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-img v-if="imageCta" :src="imageCta" max-width="300" class="rounded-xl" />
            </div>          
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="sticky bottom-0 bg-white z-10">
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModalCta()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editarCta" @click="guardarCta">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editarCta" @click="modificarCta">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>


  <v-dialog v-model="showModalFaq" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modalFaq }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModalFaq()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddFaq" v-model="valid" lazy-validation>
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field label="Pregunta" placeholder="Pregunta" v-model="modelNFaq.question" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field label="Respuesta" placeholder="Respuesta" v-model="modelNFaq.answer" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions class="sticky bottom-0 bg-white z-10">
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModalFaq()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editarFaq" @click="guardarFaq">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editarFaq" @click="modificarFaq">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>


  
  <v-dialog v-model="showModalPromotion" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modalPromotion }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModalPromotion()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddPromotion" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelNPromotion.title" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-text-field
                v-model.number="modelNPromotion.discount"
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
              <v-select chips multiple :items="categorias" label="Categorías" v-model="modelNPromotion.categoryIds" />
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-select :items="departamentos" label="Departamentos" v-model="modelNPromotion.department_id" 
                :rules="[rules.required]"
                required 
              />
            </div>            
            <div class="v-col-md-4 v-col-12">
              <v-checkbox
                v-model="modelNPromotion.isnew"
                label="Nuevo"
                color="primary"
              ></v-checkbox>
            </div>
            <div class="v-col-md-4 v-col-12">
              <v-checkbox
                v-model="modelNPromotion.outstanding"
                label="Pendiente"
                color="primary"
              ></v-checkbox>
            </div>
            <div class="v-col-md-4 v-col-12">
              <v-checkbox
                v-model="modelNPromotion.featured"
                label="Destacado" 
                color="primary"  
              ></v-checkbox>
            </div>
            <div class="v-col-md-12 v-col-12">
              <v-text-field
                v-model="modelNPromotion.promotion_link"
                label="Url web" placeholder="Url web"
                color="primary" outlined
              ></v-text-field>
            </div>
            <div class="v-col-md-12 v-col-12">
              <v-text-field
                v-model="modelNPromotion.promotion_map"
                label="Mapa" placeholder="Mapa"
                color="primary" outlined
              ></v-text-field>
            </div>
            <div class="v-col-md-12 v-col-12">
              <div >
                <label>Descripción</label>
                <textarea id="mieditorDesc" v-model="modelNPromotion.description"></textarea>          
                <!-- Mensaje de validación -->
                <small v-if="!editorValid" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>
            <div class="v-col-md-12 v-col-12">
              <div>     
                <label>Términos y condiciones</label>                
                <textarea id="mieditorTerm" v-model="modelNPromotion.terms_conditions"></textarea>
                <!-- Mensaje de validación -->
                <small v-if="!editorValidTerm" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-file-input accept="image/*" label="Subir imagen Card" prepend-icon="mdi-image" @change="onFileChangePromotion" class="file-input-custom" 
                :rules="!editarPromotion ? [v => !!v || 'La imagen es obligatoria'] : []"
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
                label="Selecciona un afiliado"
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
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModalPromotion()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editarPromotion" @click="guardarPromotion">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editarPromotion" @click="modificarPromotion">
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