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
import Affiliates from '@/models/Affiliates';
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
const registrosSelected = ref<any[]>([])
const url = ref(Global.url)
const url_image = ref(Global.url_image)
const url_logo = ref(Global.url_image)
const showmessage = ref(false)
const token = localStorage.getItem('token');
const model = reactive(new Affiliates(null, null, null, null, null, null));
const modelN = reactive(new Affiliates(null, null, null, null, null, null));


const totalElements = ref(0);
const numberOfElements = ref(0);
const page = ref(1);
const size = 21;
const totalPages = ref(0);
const title_modal = ref('')
const showModal = ref(false)

const adapter = useDate();
const showEditor = ref(false);
const networks = ref<any[]>([]);
const networksSelected = ref<any[]>([])


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

const syncNetworks = (ids: number[]) => {
  // Mantener redes seleccionadas con links (si ya existían)
  const updated = ids.map(id => {
    const existing = modelN.networks?.find(n => n.id === id)
    return existing || { id, link: '' }
  })
  modelN.networks = updated
}

pageTitle.value = "Afiliados";
routeAffiliates.value = "affiliates";



const rules = {
    required: (value:any) => !!value || "Este campo es obligatorio",
    min3: (value:any) =>
      (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
    numeric: (v: any) => /^(\d+(\.\d+)?)?$/.test(v) || 'Debe ser un número válido',
    range: (v: any) => (v >= 0 && v <= 100) || 'El descuento debe estar entre 0 y 100'
};


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
      const response = await api.post(`cms/${routeAffiliates.value}/image`,formData, {
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
  
  date.value = mo.publicationDate ? parseISO(mo.publicationDate) : null;

  image.value = url_image.value + mo.image;
  logo.value = url_logo.value + mo.logo;
  modelN.id = mo.id;
  const networksIds = mo.affiliatesNetworks.map((item: any) => item.socialNetworks.id);
  modelN.networksIds = networksIds;

  modelN.networks = mo.affiliatesNetworks.map((item: any) => ({
    id: item.socialNetworks.id,
    link: item.link || ""
  }));

  await nextTick();
  
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
          Object.assign(modelN, new Affiliates(null, null, null, null, null, null));
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
          Object.assign(modelN, new Affiliates(null, null, null, null, null, null));
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

function getNetworks(){
  axios
    .get(url.value + 'cms/socialNetworksAll',{
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    .then((response) => {
      networks.value = []
      if (response.status === 200) {
        if (Array.isArray(response.data) && response.data.length > 0) {
          networks.value = response.data.map(cat => ({
            title: cat.title,
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

function closeModal(){
  showModal.value = false; showEditor.value = false;
  Object.assign(modelN, new Affiliates(null, null, null, null, null, null));
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
    getNetworks();
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
          <v-row class="mt-1 mb-1" > 
            <v-col cols="12" sm="12" md="12" lg="6" class="d-flex align-center">
              <v-text-field label="Titulo" placeholder="Titulo" v-model="modelN.title" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" class="d-flex align-center">
              <v-file-input accept="image/*" label="Subir imagen Card" prepend-icon="mdi-image" @change="onFileChange" class="file-input-custom" 
                :rules="!editar ? [v => !!v || 'La imagen es obligatoria'] : []"
              />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="3" class="d-flex align-center">
              <v-img v-if="image" :src="image" max-width="300" class="rounded-xl" />
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="12" class="d-flex align-center">
              <v-select chips multiple :items="networks" label="Redes sociales" v-model="modelN.networksIds" @update:modelValue="syncNetworks"/>
            </v-col>
            <v-col cols="12" sm="12" md="12" lg="12" class="d-flex align-center">
              <v-expansion-panels variant="accordion">
                <v-expansion-panel
                  v-for="network in modelN.networks"
                  :key="network.id"
                >
                  <v-expansion-panel-title>
                    {{ networks.find(n => n.value === network.id)?.title }}
                  </v-expansion-panel-title>

                  <v-expansion-panel-text>
                    <v-text-field
                      v-model="network.link"
                      :label="`Link de ${networks.find(n => n.value === network.id)?.title}`"
                      placeholder="https://..."
                    />
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
