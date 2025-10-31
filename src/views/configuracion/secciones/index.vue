<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router';

import Section from '@/models/Section'; 

import UiChildCard from '@/components/shared/UiChildCard.vue';
import CardsMedia from "@/components/ui-components/cards/CardsMedia.vue";
import Global from '@/Global';
import api from "@/interceptors/axiosInterceptor";
import Closable from '@/components/ui-components/alert/Closable.vue';
import { format, parseISO } from 'date-fns';
import { DateTime } from "luxon";
import { shallowRef } from 'vue'
import { useDate } from 'vuetify'


import { useToast } from "vue-toastification";
import type { VForm } from "vuetify/components";
import swal from 'sweetalert';
import { Icon } from '@iconify/vue';

// Variables
const toast = useToast()
const router = useRouter()
const registros = ref<any[]>([])
const registrosType = ref<any[]>([])
const url_image = ref(Global.url_image)
const model = reactive(new Section(null, null, null, null, null, null));
const modelN = reactive(new Section(null, null, null, null, null, null));
const title_modal = ref('')
const showmessage = ref(false)
const showModal = ref(false)
let totalElements = 0;
let numberOfElements =0;
let page=1;
const size=20;
const token = localStorage.getItem('token');
let totalPages = 0;
const date = shallowRef<Date | null>(null);
const adapter = useDate();
const showEditor = ref(false);
const formAddFacility = ref<VForm | null>(null)
const valid = ref(false);
const editar = ref(false);
const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0); 

const image = ref<string | null>(null);
const file = ref<File|null>(null);
const route = ref<string|null>(null);
const routeType = ref<string|null>(null);
const pageTitle = ref<string|null>(null);

route.value = "section";
routeType.value = "section-type-all";
pageTitle.value = "Sección";


const rules = {
  required: (value:any) => !!value || "Este campo es obligatorio",
  min3: (value:any) =>
    (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
};



// Funciones

function modalAgregar(){
  showModal.value = true;
  title_modal.value = `Agregar ${pageTitle.value}`;
  showEditor.value = true;
  editar.value = false;
}

function modalEditar(mo:any){
  editar.value = true;
  showModal.value = true;
  title_modal.value = `Modificar ${pageTitle.value}`;
  showEditor.value = true;
  modelN.title = mo.title;
  modelN.id = mo.id;  
  modelN.section_type_id = mo.section_type_id;
}



async function consultarTipos() {
  registrosType.value = [];

  try {
    const response = await api.get(`cms/${routeType.value}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('content:',response.data);
    if (Array.isArray(response.data) && response.data.length > 0) {
      console.log('content:',response.data);
      registrosType.value = response.data.map((tp:any) => ({
        title: tp.title,
        value: tp.id
      }))
    } else {
      showmessage.value = true;
    }
  } catch (error: any) {
    console.error("Error en petición:", error);    
  }
}

async function consultarRegistros() {
  registros.value = [];

  try {
    const response = await api.get(`cms/${route.value}/${page}/${size}`, {
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
  registros.value = [];
  let datosEnviar= {
      title: model.title,
      section_type_id: model.section_type_id,
      page: page,
      size: size,
  }
  try {
    const response = await api.post(`cms/${route.value}/search`,datosEnviar, {
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
    const response = await api.get(`cms/${route.value}/${pageNew}/${size}`, {
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

function flassmessagesuccess(message:string,time: number){                
    toast.success(`${message}`, {
        timeout: time
    });
}

async function guardar(){
  console.log('guardar');
  console.log('date:',date);    
  const result = await formAddFacility.value?.validate();
  if (result?.valid) {    
    try {
      const response = await api.post(`cms/${route.value}/create`,modelN, {
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
          Object.assign(modelN, new Section(null, null, null, null, null, null));
          image.value = null;
          file.value = null;
          date.value = null;
          totalElements = totalElements+1;
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
  
  const result = await formAddFacility.value?.validate()
  
  if (result?.valid) {
    console.log("Formulario válido ✅", modelN);
    try {
      const response = await api.put(`cms/${route.value}/update/${modelN.id}`,modelN, {
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
          Object.assign(modelN, new Section(null, null, null, null, null, null));
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

function closeModal(){
  showModal.value = false; showEditor.value = false;
  Object.assign(modelN, new Section(null, null, null, null, null, null));
  image.value = null;
  file.value = null;
  date.value = null;
}

async function fnDelete(mo: any) {
  console.log('blogEliminar:', mo);

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
    const response = await api.delete(`cms/${route.value}/delete/${mo.id}`, {
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
          registros.value = registros.value.filter((r) => r.id !== mo.id);
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
    consultarTipos();
  } else {
    router.push('/')
  }
})

</script>

<template>
  <v-row >
    <v-col cols="12">
      <UiChildCard :title="`Buscar ${pageTitle}`" class="py-1">
        <v-form @submit.prevent="searchRegistros()">
          <v-row class="mt-1 mb-1" > 
            <v-col cols="12" sm="6" md="6" lg="4" >
              <v-text-field
                label="Titulo"
                placeholder="Titulo"
                v-model.trim="model.title"
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="6" md="6" lg="4" >
              <v-select :items="registrosType" label="Tipo" v-model="model.section_type_id" />
            </v-col>
            <v-col cols="12" sm="6" md="6" lg="4" >
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
            </v-col>          
          </v-row>
        </v-form>
      </UiChildCard>
    </v-col>
  </v-row>
  <v-row>
    <v-col cols="12" class="bg-amber" >
      <div class="d-flex justify-space-between align-center px-3 py-3 bg-white rounded-b-lg">
          <!-- Título -->
          <div class="text-h5">{{ pageTitle }} ({{ totalElements }})</div>
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
      
        <CardsMedia :img="url_image+item.sectionType.logo" fit="contain"
          :title="item.title" :label="item.sectionType.title"
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
        <v-form ref="formAddFacility" v-model="valid" lazy-validation>
          <v-row class="mt-1 mb-1" > 
            <v-col cols="12" sm="12" md="12" lg="6" >
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelN.title" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </v-col>            
            <v-col cols="12" sm="6" md="6" lg="6" >
              <v-select :items="registrosType" label="Tipo" v-model="modelN.section_type_id" />
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
