<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router';

import UiChildCard from '@/components/shared/UiChildCard.vue';
import Department from '@/models/Department'; "@/models/Department"; 
import api from "@/interceptors/axiosInterceptor";
import { Icon } from '@iconify/vue';
import type { VForm } from "vuetify/components";
import { useToast } from "vue-toastification";
import Closable from '@/components/ui-components/alert/Closable.vue';
import swal from 'sweetalert';



// Variables
const toast = useToast()
const router = useRouter()
const registros = ref<any[]>([])
const showmessage = ref(false);
const totalElements = ref(0);
const numberOfElements = ref(0);
const page = ref(1);
const size = 3;
const totalPages = ref(0);
const token = localStorage.getItem('token');
const model = reactive(new Department(null, null, null, null));
const modelN = reactive(new Department(null, null, null, null));
const showModal = ref(false);
const title_modal = ref('');
const editar = ref(false);
const formAddCat = ref<VForm | null>(null);
const createdId = ref(0); 
const updatedId = ref(0);  
const deletedId  = ref(0);
const valid = ref(false);

const rules = {
  required: (value:any) => !!value || "Este campo es obligatorio",
  min3: (value:any) =>
    (value && value.length >= 3) || "Debe tener al menos 3 caracteres",
};

// Funciones
async function consultarRegistros() {
  registros.value = [];
  try {
    const response = await api.get(`cms/departments/${page.value}/${size}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (Array.isArray(response.data.content) && response.data.content.length > 0) {
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
    name: model.name,
    page: page,
    size: size,
  }
  try {
    const response = await api.post("cms/departments/search",datosEnviar, {
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
    const response = await api.get(`cms/departments/${pageNew}/${size}`, {
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

function modalAgregar(){
  showModal.value = true;
  title_modal.value = "Agregar Departamento";
  editar.value = false;
}

function modalEditar(mo:any){
  editar.value = true;
  showModal.value = true;
  title_modal.value = "Modificar Departamento";
  modelN.name = mo.name;
  modelN.id = mo.id;
  
}

function flassmessagesuccess(message:string,time: number){                
    toast.success(`${message}`, {
        timeout: time
    });
}

async function guardar(){
  console.log('guardar');
  
  const result = await formAddCat.value?.validate()
  if (result?.valid) {
    console.log("Formulario válido ✅", modelN);
    try {
      const response = await api.post("cms/department/create",modelN, {
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
          showModal.value = false; 
          Object.assign(modelN, new Department(null, null, null, null, null));
          
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
  
  const result = await formAddCat.value?.validate();
  if (result?.valid ) {
    console.log("Formulario válido ✅", modelN);
    try {
      const response = await api.put(`cms/department/update/${modelN.id}`,modelN, {
        headers: { 
          Authorization: `Bearer ${token}`,
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
          showModal.value = false; 
          Object.assign(modelN, new Department(null, null, null, null, null));
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
  showModal.value = false;
  Object.assign(modelN, new Department(null, null, null, null, null));  
}

async function fnDelete(mo: any) {
  console.log('modelEliminar:', mo);
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
    const response = await api.delete(`cms/department/delete/${mo.id}`, {
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
  } else {
    router.push('/login')
  }
})

</script>

<template>
  <v-row >
    <v-col cols="12">
      <UiChildCard title="Buscar departamentos" class="py-1">
        <v-form @submit.prevent="searchRegistros()">
          <v-row class="mt-1 mb-1" > 
            <v-col cols="12" sm="6" md="6" lg="4" >
              <v-text-field
                label="nombre"
                placeholder="nombre"
                v-model.trim="model.name"
              ></v-text-field>
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
  <v-row class="justify-space-between align-center px-3 py-3 bg-white rounded-b-lg mx-0">
    <v-col cols="12" sm="12" md="6" lg="6" class="d-flex align-items-stretch" >
      <div class="text-h5">Departamentos ({{ totalElements }})</div>
    </v-col>
    <v-col cols="12" sm="12" md="6" lg="6" class="d-flex align-items-stretch" >
      <v-row class="justify-space-between align-center bg-white rounded-b-lg " v-if="totalPages>1">
        <v-col cols="12" sm="12" md="6" lg="6" class="d-flex align-items-stretch" >
          <div>Registros por página: {{ numberOfElements }}</div>
        </v-col>    
        <v-col cols="12" sm="12" md="6" lg="6" class="d-flex align-items-stretch" >
          <v-pagination 
            v-model="page" 
            :length="totalPages" 
            total-visible="7" 
            @update:modelValue="paginateRegistros" 
          />
        </v-col>
      </v-row>      
    </v-col>
  </v-row>
  <v-row>
    <v-col 
      v-for="(item, index) in registros"
      :key="item.id || index"
      cols="12"
      sm="12"
      lg="4"
      class="d-flex align-items-stretch"
    >
      <UiChildCard :title="item.name" variant="elevated" elevation="none"
        :class="{
          'box-shadow-animate-create': item.id === createdId, 
          'box-shadow-animate': item.id === updatedId,
          'box-shadow-animate-deleted': item.id === deletedId
        }"
      >
        <v-card-actions class="px-0">
            <v-btn text="Editar" color="lightinfo" elevation="5" variant="elevated" class="no-hover-shadow " @click="modalEditar(item)">
              <template v-slot:prepend>
                <div class="d-flex align-center justify-center" >
                    <Icon icon="solar:gallery-edit-line-duotone" width="24" height="24" />
                </div>
              </template>
            </v-btn>

            <v-btn text="Eliminar" color="error" elevation="5" variant="elevated" class="no-hover-shadow " @click="fnDelete(item)">
              <template v-slot:prepend>
                <div class="d-flex align-center justify-center" >
                    <Icon icon="solar:trash-bin-trash-line-duotone" width="24" height="24" />
                </div>
              </template>        
            </v-btn>
        </v-card-actions>
      </UiChildCard>
    </v-col>
    <v-col cols="12" class="bg-amber" v-if="registros.length==0">
        <div class="p-3 bg-white">
          <Closable />
        </div>
    </v-col>
  </v-row>

  <v-dialog v-model="showModal" scrim="primary" :persistent="true" width="500px" max-width="100%" transition="dialog-bottom-transition">
    <v-card>
      <v-card-title class="v-card-title sticky top-0 bg-white z-10 py-4 d-flex align-center">
        {{ title_modal }}
        <v-spacer></v-spacer>
        <v-btn icon @click="closeModal()" class="close-btn" elevation="3" color="error">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>      
      <v-card-text class="flex-grow-1 overflow-y-auto">
        <v-form ref="formAddCat" v-model="valid" lazy-validation>
          <div class="v-row mt-1 mb-1">
            <div class="v-col-md-12 v-col-12">
              <v-text-field label="Nombre" placeholder="Nombre" v-model="modelN.name" 
                :rules="[rules.required, rules.min3]"
                required 
              ></v-text-field>
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
