import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

import Home from '../components/Home.vue';
import News from '../components/News.vue';
import Stream from '../components/Stream.vue';
import Search from '../components/Search.vue';
import Main from '../components/Main.vue';
import Login from '../components/Login.vue';

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
	return originalPush.call(this, location).catch(err => err)
}

const routes = [

	{
		path: '/',
		name: 'main',
		meta: {
			requireAuth: true,
		},
		component: Main,
		children: [
			{
				path: 'home',
				name: 'home',
				component: Home,
			},
			{
				path: 'news',
				name: 'news',
				component: News,
			},
			{
				path: 'stream',
				name: 'stream',
				component: Stream,
			},
			{
				path: 'search',
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
