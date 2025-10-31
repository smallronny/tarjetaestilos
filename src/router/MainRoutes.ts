const MainRoutes = {
    path: '/main',
    meta: {
        requiresAuth: true
    },
    redirect: '/main',
    component: () => import('@/layouts/full/FullLayout.vue'),
    children: [

        //BLOG
        {
            name: 'Blog',
            path: '/blog/articulos',
            component: () => import('@/views/blog/articulos/index.vue')
        },
        {
            name: 'Categorias',
            path: '/blog/categorias',
            component: () => import('@/views/blog/categoria/index.vue')
        },
        {
            name: 'BannerBlog',
            path: '/blog/banner',
            component: () => import('@/views/blog/banner/index.vue')
        },
        {
            name: 'BannerCta',
            path: '/blog/cta',
            component: () => import('@/views/blog/cta/index.vue')
        },
        {
            name: 'BannerFaq',
            path: '/blog/faq',
            component: () => import('@/views/blog/faq/index.vue')
        },        


        //paginas
        {
            name: 'Home',
            path: '/pagina/home',
            component: () => import('@/views/paginas/home/index.vue')
        },
        
        {
            name: 'Contactanos',
            path: '/pagina/contactanos',
            component: () => import('@/views/paginas/contactanos/index.vue')
        },
        {
            name: 'NuestrasTiendas',
            path: '/pagina/nuestras-tiendas',
            component: () => import('@/views/paginas/nuestrasTiendas/index.vue')
        },
        {
            name: 'Trabaja con nosotros',
            path: '/pagina/trabaja-con-nosotros',
            component: () => import('@/views/paginas/trabajaConNosotros/index.vue')
        },
        {
            name: 'Nosotros',
            path: '/pagina/nosotros',
            component: () => import('@/views/paginas/nosotros/index.vue')
        },

        //PRODUCTOS
        {
            name: 'SumaPuntos',
            path: '/productos/suma-puntos',
            component: () => import('@/views/productos/sumaPuntos/index.vue')
        },
        
        {
            name: 'SolicitaTuTarjeta',
            path: '/productos/solicita-tu-tarjeta',
            component: () => import('@/views/productos/solicitaTuTarjeta/index.vue')
        },
        {
            name: 'SimulaTuCredito',
            path: '/productos/simula-tu-credito',
            component: () => import('@/views/productos/simulaTuCredito/index.vue')
        },

        //Establecimientos
        {
            name: 'Banner',
            path: '/establecimientos/banner',
            component: () => import('@/views/establecimientos/banner/index.vue')
        },
        {
            name: 'Afiliados',
            path: '/establecimientos/afiliados',
            component: () => import('@/views/establecimientos/afiliados/index.vue')
        },
        {
            name: 'RedesSociales',
            path: '/establecimientos/redesSociales',
            component: () => import('@/views/establecimientos/redesSociales/index.vue')
        },
        {
            name: 'Promociones',
            path: '/establecimientos/promociones',
            component: () => import('@/views/establecimientos/promotions/index.vue')
        },
        {
            name: 'CategoriasPromociones',
            path: '/establecimientos/categorias',
            component: () => import('@/views/establecimientos/promotionCategory/index.vue')
        },
        {
            name: 'Departamentos',
            path: '/establecimientos/departamentos',
            component: () => import('@/views/establecimientos/departments/index.vue')
        },

        //SEGUROS
        {
            name: 'Odontológico',
            path: '/seguros/odontologico',
            component: () => import('@/views/seguros/odontologico/index.vue')
        },
        {
            name: 'Accidentes',
            path: '/seguros/accidentes',
            component: () => import('@/views/seguros/accidentes/index.vue')
        },
        {
            name: 'MaxiPlus',
            path: '/seguros/maxi-plus',
            component: () => import('@/views/seguros/maxiPlus/index.vue')
        },
        {
            name: 'OncoComplemento',
            path: '/seguros/onco-complemento',
            component: () => import('@/views/seguros/oncoComplemento/index.vue')
        },
        {
            name: 'Salud',
            path: '/seguros/salud',
            component: () => import('@/views/seguros/salud/index.vue')
        },
        {
            name: 'Soat',
            path: '/seguros/soat'
            ,
            component: () => import('@/views/seguros/soat/index.vue')
        },

        //FORMULARIOS
        {
            name: 'Formulario noticias',
            path: '/formularios/noticias',
            component: () => import('@/views/formularios/noticias/index.vue')
        },

        //CONFIGURACION
        {
            name: 'Tipo de secciones',
            path: '/configuracion/tipo-secciones',
            component: () => import('@/views/configuracion/tipoSeccion/index.vue')
        },
        {
            name: 'Secciones',
            path: '/configuracion/secciones',
            component: () => import('@/views/configuracion/secciones/index.vue')
        },
        
        {
            name: 'Menú',
            path: '/configuracion/menu',
            component: () => import('@/views/configuracion/menu/index.vue')
        },

        //LEGALES
        {
            name: 'Políticas de privacidad',
            path: '/legales/politicas-de-privacidad',
            component: () => import('@/views/legales/politicaPrivacidad/index.vue')
        },
        {
            name: 'Términos y condiciones',
            path: '/legales/terminos-y-condiciones',
            component: () => import('@/views/legales/terminosCondiciones/index.vue')
        },
        {
            name: 'Políticas de cookies',
            path: '/legales/politicas-de-cookies',
            component: () => import('@/views/legales/politicasCookies/index.vue')
        },
        {
            name: 'Libro de reclamaciones',
            path: '/legales/libro-de-reclamaciones',
            component: () => import('@/views/legales/libroReclamaciones/index.vue')
        },



        


        //extras
        {
            name: 'Dashboard',
            path: '/Dashboard',
            component: () => import('@/views/dashboard/index.vue')
        },
        {
            name: 'Alert',
            path: '/ui/alerts',
            component: () => import('@/views/ui-components/Alerts.vue')
        },
        {
            name: 'Buttons',
            path: '/ui/buttons',
            component: () => import('@/views/ui-components/Buttons.vue')
        },
        {
            name: 'Cards',
            path: '/ui/cards',
            component: () => import('@/views/ui-components/Cards.vue')
        },
        {
            name: 'Tables',
            path: '/ui/tables',
            component: () => import('@/views/ui-components/Tables.vue')
        },
        {
            name: 'Icons',
            path: '/icons',
            component: () => import('@/views/pages/Icons.vue')
        },
        {
            name: 'Starter',
            path: '/sample-page',
            component: () => import('@/views/pages/SamplePage.vue')
        },
       
    ]
};

export default MainRoutes;
