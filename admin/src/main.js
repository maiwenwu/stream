import Vue from 'vue'
import App from './App.vue'
import store from './store'
import router from './router/'
import ElementUI from 'element-ui'
import locale from 'element-ui/lib/locale/lang/en'
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios'

Vue.config.productionTip = false;
Vue.use(ElementUI,{locale});
Vue.prototype.$ajax = axios;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
