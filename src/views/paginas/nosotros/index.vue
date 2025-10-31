<script setup lang="ts">
import { ref, reactive, onMounted  } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios';
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';

import type { VForm } from "vuetify/components";
import { Icon } from '@iconify/vue';

import { useToast } from "vue-toastification";

import { shallowRef } from 'vue';
import { parseISO } from 'date-fns';
import { nextTick } from 'vue';
import tinymce from 'tinymce';


import Banners from '@/models/Banners';
import CorporateData from '@/models/CorporateData';
import PageContent from '@/models/PageContent';
import Cta from '@/models/Cta';
import FrequentlyAskedQuestion from '@/models/FrequentlyAskedQuestion'; 



const tab = ref("general") 
interface Item {
  id: number;
  name: string;
  image: string;
}
// Variables
const affiliates = ref<any[]>([])
const selected = ref<Item | null>(null);
const slug_menu = ref('acerca-de-nosotros');
const router = useRouter()
const toast = useToast()
const departamentos = ref<any[]>([]);
const url = ref(Global.url);
const url_image = ref(Global.url_image);
const url_logo = ref(Global.url_image);
const showmessage = ref(false)
const routeBanner = ref<string|null>(null);
const routeCorporateData = ref<string|null>(null);
const routePageContent = ref<string|null>(null);
const routeCta = ref<string|null>(null);
const routeFaq = ref<string|null>(null);

const categorias = ref<any[]>([])
const token = localStorage.getItem('token');

const registrosBanner = ref<any[]>([]);
const registrosCorporateData = ref<any[]>([]);
const registrosPageContent = ref<any[]>([]);
const registrosCta = ref<any[]>([]);
const registrosFaq = ref<any[]>([]);

const modelBanner = reactive(new Banners(null, null, null, null, null, null, null, null));
const modelNBanner = reactive(new Banners(null, null, null, null, null, null, null, null));

const modelCorporateData = reactive(new CorporateData(null, null, null, null, null, null, null, null));
const modelNCorporateData = reactive(new CorporateData(null, null, null, null, null, null, null, null));

const modelPageContent = reactive(new PageContent(null, null, null, null, null, null, null));
const modelNPageContent = reactive(new PageContent(null, null, null, null, null, null, null));

const modelCta = reactive(new Cta(null, null, null, null, null, null, null));
const modelNCta = reactive(new Cta(null, null, null, null, null, null, null));

const modelFaq = reactive(new FrequentlyAskedQuestion(null, null, null, null, null, null));
const modelNFaq = reactive(new FrequentlyAskedQuestion(null, null, null, null, null, null));


const totalElementsBanner = ref(0);
const numberOfElementsBanner = ref(0);
const totalPagesBanner = ref(0);

const totalElementsCorporateData = ref(0);
const numberOfElementsCorporateData = ref(0);
const totalPagesCorporateData = ref(0);

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
const title_modalCorporateData = ref('');
const title_modalPageContent = ref('');
const title_modalCta = ref('');
const title_modalFaq = ref('');


const showModalBanner = ref(false);
const showModalCorporateData = ref(false)
const showModalPageContent = ref(false)
const showModalCta = ref(false)
const showModalFaq = ref(false)


const showEditor = ref(false);
const showEditorCorporateData = ref(false);
const showEditorPageContent = ref(false);
const showEditorCta = ref(false);
const showEditorFaq = ref(false);

const editorSubTitleValid = ref(true);
const editorTitleValid = ref(true);

const editorValid = ref(true);
const editorValidPageContent = ref(true);
const editorValidCorporateData = ref(true);



const formAddBanner = ref<VForm | null>(null);
const formAddCorporateData = ref<VForm | null>(null);
const formAddPageContent = ref<VForm | null>(null);
const formAddCta = ref<VForm | null>(null);
const formAddFaq = ref<VForm | null>(null);

const valid = ref(false);
const editar = ref(false);
const editarCorporateData = ref(false);
const editarPageContent = ref(false);
const editarCta = ref(false);
const editarFaq = ref(false);


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
routeCorporateData.value = "corporate-data";
routePageContent.value = "page-content";
routeCta.value = "cta";
routeFaq.value = "faq";

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
      registrosBanner.value = response.data.content;
      totalPagesBanner.value = response.data.totalPages;
      totalElementsBanner.value = response.data.totalElements;
      numberOfElementsBanner.value = response.data.numberOfElements;
      modelBanner.menu_id = response.data.content[0].id;
      modelNBanner.menu_id = response.data.content[0].id;
      modelCorporateData.menu_id = response.data.content[0].id;
      modelNCorporateData.menu_id = response.data.content[0].id;
      modelPageContent.menu_id = response.data.content[0].id;
      modelNPageContent.menu_id = response.data.content[0].id;
      modelCta.menu_id = response.data.content[0].id;
      modelNCta.menu_id = response.data.content[0].id;
      modelFaq.menu_id = response.data.content[0].id;
      modelNFaq.menu_id = response.data.content[0].id;
      
      
      
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

async function searchRegistrosCorporateDatas() {
  registrosCorporateData.value = [];  
  try {
    const response = await api.post(`cms/${routeCorporateData.value}/search`,modelCorporateData, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data.content);
    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
      console.log('content:',response.data.content);
      registrosCorporateData.value = response.data.content;
      totalPagesCorporateData.value = response.data.totalPages;
      totalElementsCorporateData.value = response.data.totalElements;
      numberOfElementsCorporateData.value = response.data.numberOfElements;
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function searchRegistrosPageContent() {
  registrosPageContent.value = [];  
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

async function searchRegistrosCta() {
  registrosCta.value = [];  
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



async function modalAgregarBanner(){ 
  await searchGetMenu();
  showModalBanner.value = true;
  title_modal.value = "Agregar Banner";
  showEditor.value = true;
  editar.value = false;
  await nextTick(); 
  initEditorTitle();
  initEditorSubTitle();
}

async function modalAgregarCorporateData(){ 
  await searchGetMenu();
  showModalCorporateData.value = true;
  title_modalCorporateData.value = "Agregar Información corporativa";
  showEditorCorporateData.value = true;
  editarCorporateData.value = false;
  await nextTick(); 
  await initEditorDescCorporateData();
  
  console.log('modelCorporateData:',modelNCorporateData);
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

async function modalAgregarCta(){ 
  await searchGetMenu();
  showModalCta.value = true;
  title_modalCta.value = "Agregar Cta";
  showEditorCta.value = true;
  editarCta.value = false;
  await nextTick(); 
  console.log('modelCta:',modelNCta);
}

async function modalAgregarFaq(){ 
  await searchGetMenu();
  showModalFaq.value = true;
  title_modalFaq.value = "Agregar preguntas";
  showEditorFaq.value = true;
  editarFaq.value = false;
  await nextTick(); 
  console.log('modelFaq:',modelNFaq);
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

async function modalEditarCorporateData(mo:any){
  await searchGetMenu();
  editarCorporateData.value = true;
  showModalCorporateData.value = true;
  title_modalCorporateData.value = "Modificar información corpórativa";
  showEditorCorporateData.value = true;
  modelNCorporateData.title = mo.title;
  modelNCorporateData.icon = mo.icon;
  modelNCorporateData.description = mo.description;
  selected.value = mo.affiliates.id;

  await nextTick();
  
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

  await nextTick();
  
}


async function modalEditarCta(mo:any){
  await searchGetMenu();
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
  await searchGetMenu();
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


let editorInstanceDesc:any = null;
let editorInstanceDescPageContent:any = null;
let editorInstanceDescCorporateData:any = null;

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
        .post("cms/corporate-data/image-upload", formData, {
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
        modelNCorporateData.description = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(modelNCorporateData.description || '');
      });      
    }
  });
}

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

async function initEditorDescCorporateData() {
  tinymce.init({
    license_key: 'gpl',
    selector: '#mieditorDescCorporateData',
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
        .post("cms/corporate-data/image-upload", formData, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          const data = response.data;

          console.log("data:", data);

          if (editorInstanceDescCorporateData) {
            editorInstanceDescCorporateData.insertContent(
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
      editorInstanceDescCorporateData = editorDesc;
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
        modelNCorporateData.description = editorDesc.getContent();
      });
      // cuando cargue, poner el contenido inicial
      editorDesc.on('init', () => {
        editorDesc.setContent(modelNCorporateData.description || '');
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

function closeModalCorporateData(){
  showModalCorporateData.value = false; showEditorCorporateData.value = false;
  Object.assign(modelNCorporateData, new CorporateData(null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  tinymce.remove("#mieditorDescCorporateData");
}

function closeModalPageContent(){
  showModalPageContent.value = false; showEditorPageContent.value = false;
  Object.assign(modelNPageContent, new PageContent(null, null, null, null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
  
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


const validateEditorDeskCorporateData = () => {
  // ejemplo: requerido y mínimo 3 caracteres sin etiquetas html
  const text = modelNCorporateData.description?.replace(/<[^>]*>?/gm, "").trim();
  editorValidCorporateData.value = text ? text.length >= 3 : false;
  console.log('editorValidCorporateData.value: ',editorValidCorporateData.value);
  return editorValidCorporateData.value;
};


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

async function guardarCorporateData(){
  console.log('guardar');      
  const result = await formAddCorporateData.value?.validate();
  console.log('modelNCorporateData:', modelNCorporateData);
  const ckValid = await validateEditorDeskCorporateData();
  
  if (result?.valid && ckValid ) {
    try {
      const response = await api.post(`cms/${routeCorporateData.value}/create`,modelNCorporateData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data);         
        if(response.data.status===201 && response.data.data){          
          registrosCorporateData.value.unshift(response.data.data);
          createdId.value = response.data.data.id;
          setTimeout(() => (createdId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalCorporateData.value = false; showEditorCorporateData.value = false;
          Object.assign(modelNCorporateData, new CorporateData(null, null, null, null, null, null, null, null));
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
  console.log('modelNShortcuts:', modelNPageContent);
  
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

async function guardarCta(){
  console.log('guardar');      
  const result = await formAddCta.value?.validate();
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

async function modificarCorporateData(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddCorporateData.value?.validate()
  if (result?.valid) {
    console.log("Formulario válido corporate data ✅", modelNCorporateData);
    try {
      const response = await api.put(`cms/${routeCorporateData.value}/update/${modelNCorporateData.id}`,modelNCorporateData, {
        headers: { 
          Authorization: `Bearer ${token}`
        },
      });
      console.log('content:',response.data.data);
      if (response.data.data) {
        console.log('content is array:',response.data.data); 
        const index = registrosCorporateData.value.findIndex(r => r.id === response.data.data.id);
        
        if(response.data.status===200 && response.data.data){
          updatedId.value = response.data.data.id;
          registrosCorporateData.value[index] = response.data.data;
          setTimeout(() => (updatedId.value = 0), 2000) 
          //console.log('ingreso');
          flassmessagesuccess(response.data.message,20);
          showModalCorporateData.value = false; showEditor.value = false;
          Object.assign(modelNCorporateData, new CorporateData(null, null, null, null, null, null, null, null));
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


async function modificarCta(){
  console.log('modiifcar');  
  console.log('date:',date); 
  const result = await formAddCta.value?.validate()
  
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
  const result = await formAddFaq.value?.validate()
  
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

async function fnDeleteCorporateData(mo: any) {
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
    const response = await api.delete(`cms/${routeCorporateData.value}/delete/${mo.id}`, {
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
          registrosCorporateData.value = registrosCorporateData.value.filter((r) => r.id !== mo.id);
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
      <v-tab value="corporateData" @click="searchRegistrosCorporateDatas">Información corporativa</v-tab>
      <v-tab value="cta" @click="searchRegistrosCta">Cta</v-tab>
      <v-tab value="faq" @click="searchRegistrosFaq">Preguntas Frequentes</v-tab> 
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

      <v-window-item value="corporateData">
        <v-card flat>
          <v-card-text>
            <v-form @submit.prevent="searchRegistrosCorporateDatas()">
              <v-row dense>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="modelCorporateData.title"
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
                  <v-btn text="Agregar" color="info" @click="modalAgregarCorporateData">
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
                v-for="(item, index) in registrosCorporateData"
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
                    <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditarCorporateData(item)"> 
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                        </div>
                      </template>
                    </v-btn>      

                    <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDeleteCorporateData(item)">
                      <template v-slot:prepend>
                        <div class="d-flex align-center justify-center" >
                          <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />            
                        </div>        
                      </template>        
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
              <v-col cols="12" class="bg-amber" v-if="registrosCorporateData.length==0">
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

  <v-dialog v-model="showModalCorporateData" scrim="primary" :persistent="true" max-width="1000px" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modalCorporateData }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModalCorporateData()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddCorporateData" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-6 v-col-12">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelNCorporateData.title" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>
            <div class="v-col-md-6 v-col-12">
              <v-text-field
                v-model.number="modelNCorporateData.icon"
                label="icono"
                placeholder="Escribe el icono"
                outlined
                :rules="[rules.required, rules.min3]"
                required
              />
            </div>
            <div class="v-col-md-12 v-col-12">
              <div >
                <label>Descripción</label>
                <textarea id="mieditorDescCorporateData" v-model="modelNCorporateData.description"></textarea>
                <!-- Mensaje de validación -->
                <small v-if="!editorValidCorporateData" class="text-error">
                  El contenido es requerido y debe tener al menos 3 caracteres
                </small>
              </div>
            </div>            
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="sticky bottom-0 bg-white z-10">
        <v-btn text="Cancelar" color="error" elevation="5" variant="elevated" @click="closeModalCorporateData()">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:close-circle-filled" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Guardar" color="success" elevation="5" variant="elevated" v-if="!editarCorporateData" @click="guardarCorporateData">
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" >
              <Icon icon="line-md:clipboard-plus-twotone" width="24" height="24" />
            </div>        
          </template>
        </v-btn>
        <v-btn text="Modificar" color="success" elevation="5" variant="elevated" v-if="editarCorporateData" @click="modificarCorporateData">
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
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </div>            
            <div class="v-col-md-6 v-col-12">
              <v-text-field
                v-model="modelNPageContent.image_position"
                label="Posición de imagen" placeholder="posición de imagen"
                color="primary" outlined
              ></v-text-field>
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
                :rules="!editarPageContent ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </div>
            <div class="v-col-md-3 v-col-12 d-flex align-center">
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
            </div>            
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

</template>
<style>
  .wrap-title {
    white-space: normal !important;
  }
</style>