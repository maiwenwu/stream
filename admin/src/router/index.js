import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

import ManageProgram from '../components/ManageProgram.vue';
import Stream from '../components/Stream.vue';
import Search from '../components/Search.vue';
import Main from '../components/Main.vue';
import Login from '../components/Login.vue';
import RFSettings from '../components/RfSettings.vue';

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
	return originalPush.call(this, location).catch(err => err)
}

const routes = [

	{
		path:'/',
		name:'login',
		meta: {
			requireAuth: true,
		},
		component: Login,
	},
	{
		path: '/main',
		name: 'main',
		meta: {
			requireAuth: true,
		},
		component: Main,
		children: [
			{
				path: '/manage_program',
				name: 'manage_program',
				component: ManageProgram,
			},
			{
				path: '/rf_setting',
				name: 'rf_setting',
				component: RFSettings,
			},
			{
				path: '/search',
				name: 'search',
				component: Search,
			}
		]
	},


]

const router = new VueRouter({
	routes
})

export default router
