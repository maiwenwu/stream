<template>
  <div id="main">
    <!-- <el-container>
			<el-aside width="200px">Aside</el-aside>
			<el-container>
				<el-header>Header</el-header>
				<el-main>Main</el-main>
				<el-footer>Footer</el-footer>
			</el-container>
		</el-container>
    <router-view></router-view>-->

    <el-container class="home-container">
      <!-- hread -->
      <el-header class="home-header">
        <span class="home_title">admin</span>
        <div style="display: flex;align-items: center;margin-right: 7px">
          <el-badge style="margin-right: 30px">
            <i class="fa fa-bell-o" style="cursor: pointer"></i>
          </el-badge>
        </div>
      </el-header>

      <!-- aside -->
      <el-container>
        <el-aside width="200px" class="home-aside" style="overflow: visible;">
          <div style="display: flex;justify-content: flex-start;width:200px;text-align: left;">
            <el-menu style="background: #ececec;width: 200px;" router>
              <el-submenu v-for="(item,index) in list" :key="index" :index="index+''">
                <template slot="title">
                  <i :class="item.icon"></i>
                  <span>{{item.title}}</span>
                </template>
                <el-menu-item
                  v-for="children in item.children"
                  :index="children.path"
                  :key="children.path"
                  @click="getRouteName()"
                >{{children.name}}</el-menu-item>
              </el-submenu>
            </el-menu>
          </div>
        </el-aside>

        <el-main style="border: 1px solid rgba(0, 0, 0, 0.125);">
          <div class="card">
            <div class="header">
              <p style="margin-left:20px;font-weight: 800">{{route_name}}</p>
            </div>
            <router-view></router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      route_name: "",
      list: [
        {
          title: "Boards",
          icon: "el-icon-copy-document",
          children: [
            {
              path: "/rf_setting",
              name: "Rf Settings"
            },
            {
              path: "/search",
              name: "Search"
            }
          ]
        },
        {
          title: "Data",
          icon: "el-icon-location",
          children: [
            {
              path: "/manage_program",
              name: "Manage Program"
            }
          ]
        }
      ]
    };
  },
  mounted() {
    this.route_name = this.$route.name;
  },
  methods: {
    getRouteName() {
      console.log(this.$route.name);
      this.route_name = this.$route.name;
    }
  }
};
</script>

<style lang="less">
.header {
  height: 50px;
  border-bottom: none;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  background: rgba(230, 230, 230, 0.5);
  width: 100%;
  border-bottom: 1px solid rgba(0, 0, 0, 0.125);
}
.card {
  border: 1px solid rgba(0, 0, 0, 0.125);
}

.home-container {
  height: 100%;
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%;
}

.home-header {
  background-color: #20a0ff;
  color: #333;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: content-box;
  padding: 0px;
}

.home-aside {
  background-color: #ececec;
}

.home_title {
  color: #fff;
  font-size: 22px;
  display: inline;
  margin-left: 8px;
}

.el-submenu .el-menu-item {
  width: 180px;
  min-width: 175px;
}
</style>