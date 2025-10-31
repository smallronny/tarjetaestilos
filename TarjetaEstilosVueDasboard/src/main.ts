import { createApp } from 'vue';
import App from './App.vue';
import { router } from './router';
import vuetify from './plugins/vuetify';
import '@/scss/style.scss';
import PerfectScrollbar from 'vue3-perfect-scrollbar';
import VueApexCharts from 'vue3-apexcharts';
import VueTablerIcons from 'vue-tabler-icons';
import Toast from 'vue-toastification';
import { POSITION } from 'vue-toastification';
import "vue-toastification/dist/index.css";
import VueScrollTo from 'vue-scrollto';
import CKEditor from '@ckeditor/ckeditor5-vue';   

const options= {
  position: POSITION.TOP_RIGHT,
  timeout: 2000,
};




const app = createApp(App);

app.use(Toast, options);
app.use(router);
app.use(PerfectScrollbar);
app.use(VueTablerIcons);
app.use(VueScrollTo);
app.use(VueApexCharts);
app.use(CKEditor);   
app.use(vuetify).mount('#app');
