<script setup lang="ts">
import { ref, shallowRef } from 'vue';
import sidebarItems from './vertical-sidebar/sidebarItem';
import NavGroup from './vertical-sidebar/NavGroup/index.vue';
import NavItem from './vertical-sidebar/NavItem/index.vue';
import Logo from './logo/Logo.vue';
// Icon Imports
import { Menu2Icon, BellRingingIcon } from 'vue-tabler-icons';
import NotificationDD from './vertical-header/NotificationDD.vue';
import ProfileDD from './vertical-header/ProfileDD.vue';
import NavCollapse from './vertical-sidebar/NavCollapse/NavCollapse.vue';
import Global from '../../Global';
import { useRouter } from 'vue-router';

const sidebarMenu = shallowRef(sidebarItems);
const sDrawer = ref(true);
const url = Global.url;
const nombre = localStorage.getItem('nombre') ?? '';
const url_image = Global.url_image;
const rol = localStorage.getItem('rol') ?? 'admin';
const imagen = localStorage.getItem('imagen') ?? '';
const correo = localStorage.getItem('correo') ?? 'jsimonvega@gmail.com';
        



const router = useRouter();

const cerrarSession = () => {
    localStorage.clear();
    router.push('/');
};
</script>

<template>
    <v-navigation-drawer left v-model="sDrawer" app class="leftSidebar bg-containerBg" elevation="10" width="270">
        <div class="pa-5 pl-4">
            <Logo />
        </div>
        <!-- ---------------------------------------------- -->
        <!---Navigation -->
        <!-- ---------------------------------------------- -->
        <perfect-scrollbar class="scrollnavbar bg-containerBg overflow-y-hidden">
            <v-list class="py-4 px-4 bg-containerBg">
                <!---Menu Loop -->
                <template v-for="(item, i) in sidebarMenu">
                    <!---Item Sub Header -->
                    <NavGroup :item="item" v-if="item.header" :key="item.title" />

                    <NavCollapse class="" :item="item" :level="0" v-else-if="item.children" />
                    <!---Single Item-->
                    <NavItem :item="item" v-else class="leftPadding" />
                    <!---End Single Item-->
                </template>
                <!-- <Moreoption/> -->
            </v-list>
           

            
        </perfect-scrollbar>
        <div class="py-3 px-2 userbottom bg-containerBg ">
            <div class="v-sheet v-theme--BLUE_THEME bg-lightprimary rounded-md pa-4 ExtraBox hide-menu">
                <div class="d-flex align-center justify-space-between">
                    <div class="v-avatar v-theme--BLUE_THEME v-avatar--density-default v-avatar--variant-flat" style="width: 50px; height: 50px;">
                        <img :src="url_image+imagen" width="50" alt="{{ nombre }}">
                        <!----><span class="v-avatar__underlay"></span>
                    </div>
                    <div>
                        <h6 class="text-h6 d-flex align-center font-weight-semibold">{{ nombre }}</h6>
                        <span class="text-subtitle-2 font-weight-medium text-grey100">{{ rol }}</span>
                    </div>
                    <div>
                        <button type="button" @click="cerrarSession"
                        class="v-btn v-btn--flat v-btn--icon v-theme--BLUE_THEME v-btn--density-default v-btn--size-small v-btn--variant-elevated 
                        bg-lightprimary">
                            <span class="v-btn__overlay"></span>
                            <span class="v-btn__underlay"></span>
                            <!---->
                            <span class="v-btn__content" data-no-activator="">
                                <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" aria-hidden="true" role="img" class="text-primary iconify iconify--solar" stroke-width="3" width="24" height="24" viewBox="0 0 24 24">
                                    <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5">
                                        <path d="M12 20a8 8 0 1 1 0-16"></path>
                                        <path stroke-linejoin="round" d="M10 12h10m0 0l-3-3m3 3l-3 3"></path>
                                    </g>
                                </svg>
                            </span><!----><!---->
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </v-navigation-drawer>
    <div class="container verticalLayout">
        <div class="maxWidth">
            <v-app-bar elevation="0" height="70" class="top-header">
                <div class="d-flex align-center justify-space-between w-100">
                    <div>
                        <v-btn class="hidden-lg-and-up text-muted" @click="sDrawer = !sDrawer" icon variant="flat" size="small">
                            <Menu2Icon size="20" stroke-width="1.5" />
                        </v-btn>
                        <!-- Notification -->
                        <NotificationDD />
                    </div>
                    <div>
                        
                        <ProfileDD />
                    </div>
                </div>
            </v-app-bar>
        </div>
    </div>
</template>
