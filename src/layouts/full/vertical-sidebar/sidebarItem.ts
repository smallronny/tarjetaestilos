import {
    LayoutDashboardIcon,
    BorderAllIcon,
    AlertCircleIcon,
    CircleDotIcon,
    BoxMultiple1Icon,
    LoginIcon,
    MoodHappyIcon,
    ApertureIcon,
    UserPlusIcon
} from 'vue-tabler-icons';

export interface menu {
    header?: string;
    title?: string;
    icon?: any;
    to?: string;
    chip?: string;
    BgColor?: string;
    chipBgColor?: string;
    chipColor?: string;
    chipVariant?: string;
    chipIcon?: string;
    children?: menu[];
    disabled?: boolean;
    type?: string;
    subCaption?: string;
    external?: boolean;
}

const sidebarItem: menu[] = [
    { header: 'Contenidos' },
    {
        title: 'Paginas',
        icon: 'layers-minimalistic-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Home',
                to: '/pagina/home'
            },
            {
                title: 'Nosotros',
                to: '/pagina/nosotros'
            },
            {
                title: 'Contactanos',
                to: '/pagina/contactanos'
            },
            {
                title: 'Nuestras tiendas',
                to: '/pagina/nuestras-tiendas'
            },
            {
                title: 'Trabaja con nosotros',
                to: '/pagina/trabaja-con-nosotros'
            },            
            
        ]
    },

    {
        title: 'Productos',
        icon: 'ticket-sale-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Suma puntos',
                to: '/productos/suma-puntos'
            },
            {
                title: 'Solicita tu tarjeta',
                to: '/productos/solicita-tu-tarjeta'
            },
            {
                title: 'Simula tu crédito',
                to: '/productos/simula-tu-credito'
            }
        ]
    },

    {
        title: 'Establecimientos ',
        icon: 'garage-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Banner',
                to: '/establecimientos/banner'
            },
            {
                title: 'Promociones',
                to: '/establecimientos/promociones'
            },
            {
                title: 'Afiliados',
                to: '/establecimientos/afiliados'
            },
            {
                title: 'Categorías',
                to: '/establecimientos/categorias'
            },            
            {
                title: 'Redes Sociales',
                to: '/establecimientos/redesSociales'
            },
            {
                title: 'Departamentos',
                icon: 'buildings-linear',
                to: '/establecimientos/departamentos'
            },            
        ]
    },

    {
        title: 'Blog',
        icon: 'bill-list-linear',
        to: '/components/',
        children: [
            {
                title: 'Banner',
                icon: 'bill-list-linear',
                to: '/blog/banner'
            },
            {
                title: 'Artículos',
                icon: 'bill-list-linear',
                to: '/blog/articulos'             
            },
            {
                title: 'Categorías',
                icon: 'tuning-4-line-duotone',
                to: '/blog/categorias'
            },
            {
                title: 'Cta',
                icon: 'tuning-4-line-duotone',
                to: '/blog/cta'
            },
            {
                title: 'Faq',
                icon: 'tuning-4-line-duotone',
                to: '/blog/faq'
            }            
        ]
    },

    {
        title: 'Seguros',
        icon: 'key-square-2-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Odontológico',
                to: '/seguros/odontologico'
            },
            {
                title: 'SOAT',
                to: '/seguros/soat'
            },
            {
                title: 'Maxi Plus',
                to: '/seguros/maxi-plus'
            },
            {
                title: 'Onco Complemento',
                to: '/seguros/onco-complemento'
            },
            {
                title: 'Salud',
                to: '/seguros/salud'
            },
            {
                title: 'Accidentes',
                to: '/seguros/accidentes'
            }            
        ]
    },

    
    {
        title: 'Formularios',
        icon: 'file-text-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Noticias',
                to: '/formularios/noticias',
            }               
        ]
    },

    

    {
        title: 'Configuración',
        icon: 'settings-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Menú',
                to: '/configuracion/menu'
            },
            {
                title: 'Footer',
                to: '/configuracion/tipo-secciones'
            },
            {
                title: 'Secciones',
                to: '/configuracion/secciones'
            }      
        ]
    },

    {
        title: 'Legales',
        icon: 'shield-star-line-duotone',
        to: '/components/',
        children: [
            {
                title: 'Política de privacidad',
                to: '/legales/politicas-de-privacidad'
            },
            {
                title: 'Términos y condiciones',
                to: '/legales/terminos-y-condiciones'
            },
            {
                title: 'Políticas de cookies',
                to: '/legales/politicas-de-cookies'
            }      
        ]
    },

    
    
    
    
    
    
    

    
    
];

export default sidebarItem;
