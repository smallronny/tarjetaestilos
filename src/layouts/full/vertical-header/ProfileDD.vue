<template>    
    <v-menu v-model="menu"  :close-on-content-click="false">
        <template v-slot:activator="{ props }">
            <v-btn class="d-flex align-center gap-2 py-2" variant="text" v-bind="props" :density="null" >
                <v-avatar size="50">
                    <img :src="url_image+imagen" width="50" alt="user" />
                </v-avatar>
                <div class="ml-md-4 d-md-block d-none text-left">
                    <h6 class="text-h6 d-flex align-center text-black font-weight-semibold">{{ nombre }}</h6>
                    <span class="text-subtitle-2 font-weight-medium text-grey100">{{ rol }}</span>
                </div>
            </v-btn>
        </template>        
        <v-sheet
            rounded="xl"            
            elevation="10"
            class="mt-2 transition-fast-in-fast-out"
            style="overflow: hidden"
            >
            <!-- Encabezado del usuario -->
            <div class="px-8 pt-6 ">
                <div class="d-flex align-center justify-space-between">
                    <h6 class="text-h5 font-weight-semibold">User Profile</h6>  
                    <v-btn icon @click="menu = false" variant="text" class="ma-0 pa-0">
                        <Icon
                            icon="solar:close-circle-line-duotone"
                            width="22"
                            height="22"
                            class="text-grey200 opacity-50"
                        />
                    </v-btn>                    
                </div>                
                <div class="d-flex align-center mt-5 pb-6">
                    <v-avatar size="90">
                        <img :src="url_image + imagen" width="90" alt="user"  />
                    </v-avatar>
                    <div class="ml-4">
                        <h6 class="text-h6 d-flex align-center text-black font-weight-semibold">{{ nombre }}</h6>
                        <span class="text-subtitle-2 font-weight-medium text-grey100">{{ rol }}</span>
                        <div class="d-flex align-center mt-1 ">
                            <Icon icon="solar:letter-line-duotone" width="20" height="20" class="text-info" />
                            <span class="text-subtitle-1 text-grey100 font-weight-medium ml-2">{{ correo }}</span>
                        </div>
                    </div>
                </div>
                <v-divider></v-divider>
            </div>
            <!-- Opciones de menú -->
            <v-list class="py-4 px-8 custom-text-primary" lines="one" density="compact">
                <v-list-item value="item1" color="primary">
                <template v-slot:prepend>
                    <div
                    class="v-avatar rounded-lg bg-lightinfo d-flex align-center justify-center"
                    style="width: 40px; height: 40px"
                    >
                    <Icon
                        icon="solar:user-circle-line-duotone"
                        width="24"
                        height="24"
                        class="text-info"
                    />
                    </div>
                </template>
                <v-list-item-title class="pl-4 text-body-1">Mi Perfil</v-list-item-title>
                </v-list-item>

                <!-- Agrega más links aquí si deseas -->
            </v-list>

            

            <!-- Botón cerrar sesión -->
            <div class="pt-4 pb-4 px-5 text-center">
                <v-btn
                @click="cerrarSession"
                color="primary"
                variant="outlined"
                class="rounded-pill"
                block
                >
                Cerrar Sesión
                </v-btn>
            </div>
        </v-sheet>
    </v-menu>
</template>
<script >  
    import { UserIcon, MailIcon, ListCheckIcon } from 'vue-tabler-icons';
    import { Icon } from '@iconify/vue'
    import Global from '../../../Global';
    import { ref } from 'vue'
    
    export default {
        data(){
            return{
                nombre:String,
                rol:String,
                imagen:String,
                correo:String,
                isShow:Boolean=false,
                url: Global.url,
                url_image: Global.url_image,
                menu: false
            }
        },
        components: {
            Icon
        },
        created:function(){
            
        },
        mounted(){
            if(localStorage.getItem('token')){
                this.nombre=localStorage.getItem('nombre');
                this.imagen=localStorage.getItem('imagen');
                this.rol=localStorage.getItem('rol') ?? 'admin';
                this.correo=localStorage.getItem('correo') ?? 'jsimonvega@gmail.com';
            }else{
                this.$router.push("/");
            }
        },
        methods:{            
            cerrarSession(){
                localStorage.clear()
                this.$router.push("/");
            }
        },
        name: 'Menu-backend'
    }
</script>