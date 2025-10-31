<script setup lang="ts">
import { ref, reactive, onMounted, computed, onBeforeMount, onBeforeUnmount  } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios';
import moment from 'moment';

import UiChildCard from '@/components/shared/UiChildCard.vue';
import CardsMedia from "@/components/ui-components/cards/CardsMedia.vue";
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';
import SocialNetworks from '@/models/SocialNetworks'; 
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
const token = localStorage.getItem('token');
const model = reactive(new SocialNetworks(null, null, null, null, null, null));
const modelN = reactive(new SocialNetworks(null, null, null, null, null, null));


const totalElements = ref(0);
const numberOfElements = ref(0);
const page = ref(1);
const size = 21;
const totalPages = ref(0);
const title_modal = ref('')
const showModal = ref(false)

const adapter = useDate();
const showEditor = ref(false);


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
const routeAffiliates = ref<string|null>(null);
const pageTitle = ref<string|null>(null);


pageTitle.value = "Redes Sociales";
routeAffiliates.value = "socialNetworks";


const rules = {
    required: (value:any) => !!value || "Este campo es obligatorio",
    min3: (value:any) =>
      (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
    numeric: (v: any) => /^(\d+(\.\d+)?)?$/.test(v) || 'Debe ser un número válido',
    range: (v: any) => (v >= 0 && v <= 100) || 'El descuento debe estar entre 0 y 100'
};



// Funciones
async function consultarRegistros() {
  registros.value = [];
  try {
    const response = await api.get(`cms/${routeAffiliates.value}/${page.value}/${size}`, {
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
      page: page,
      size: size,
  }
  try {
    const response = await api.post(`cms/${routeAffiliates.value}/search`,datosEnviar, {
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
    const response = await api.get(`cms/${routeAffiliates.value}/${pageNew}/${size}`, {
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


async function modalAgregar(){
  
  showModal.value = true;
  title_modal.value = `Agregar ${pageTitle.value}`;
  showEditor.value = true;
  editar.value = false;
  await nextTick(); 
}
async function modalEditar(mo:any){
  
  editar.value = true;
  showModal.value = true;
  title_modal.value = `Modificar ${pageTitle.value}`;
  showEditor.value = true;
  modelN.title = mo.title;
  modelN.icono = mo.icono;
  
  date.value = mo.publicationDate ? parseISO(mo.publicationDate) : null;
  
  await nextTick();
  
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
  console.log('modelN:', modelN);
  if (result?.valid ) {
    
    try {
      const response = await api.post(`cms/${routeAffiliates.value}/create`,modelN, {
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
          Object.assign(modelN, new SocialNetworks(null, null, null, null, null, null));
          image.value = null;
          file.value = null;
          date.value = null;
          logo.value = null;
          fileLogo.value = null;
          totalElements.value = totalElements.value+1;
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
  const result = await formAdd.value?.validate();
  console.log('modelN:', modelN);
  if (result?.valid) {
    
    try {
      const response = await api.put(`cms/${routeAffiliates.value}/update/${modelN.id}`,modelN, {
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
          Object.assign(modelN, new SocialNetworks(null, null, null, null, null, null));
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
  Object.assign(modelN, new SocialNetworks(null, null, null, null, null, null));
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
      <UiChildCard :title="`Buscar ${pageTitle}`" class="py-1">
        <v-form @submit.prevent="searchRegistros()">
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-3 v-col-sm-6 v-col-12">
              <v-text-field
                label="Título"
                placeholder="Título"
                v-model.trim="model.title"
              ></v-text-field>
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
          <div class="text-h5">{{pageTitle}} ({{ totalElements }})</div>
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
      <CardsMedia :svgCode="item.icono" 
          :title="item.title" 
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
          <v-row class="mt-1 mb-1" > 
            <v-col cols="12" sm="12" md="12" lg="6" >
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelN.title" 
                :rules="[rules.required]"
                required 
              ></v-text-field>
            </v-col>            
            <v-col cols="12" sm="12" md="12" lg="6" >
              <v-textarea
                  label="SVG Code"
                  v-model="modelN.icono"
                  rows="8"
                  auto-grow
                  outlined
                  :rules="[rules.required, rules.min3]"
                  placeholder="<svg> ... </svg>"
                />
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
