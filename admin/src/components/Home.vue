/* eslint-disable */
<template>
  <div>
    <!-- <span>Home</span>
    <el-button @click="getInfo()" type="primary" round>主要按钮</el-button>
    <br />
    <ul>
      <li v-for="(item,index) in data" :key="index">{{index + 1}} ----- {{item.serviceName}}</li>
    </ul>-->

    <el-container>
      <el-header
        style="padding: 0px;display:flex;justify-content:space-between;align-items: center"
      >
        <div style="display: inline">
          <el-input
            placeholder="Search Program name"
            clearable
            style="width: 210px;margin: 0px;padding: 0px;"
            prefix-icon="el-icon-search"
            v-model="keyWord"
            @change="keywordsChange"
          ></el-input>
          <el-button
            type="primary"
            style="margin-left: 5px"
            icon="el-icon-search"
            @click="searchPro"
          >搜索</el-button>
          <el-select
            v-model="sateSelected"
            placeholder="请选择"
            style="margin-left: 5px"
            @change="changeSate($event)"
          >
            <!-- <el-option value=0 label="ALL"></el-option> -->
            <el-option
              v-for="(item,index) in sate_info"
              :key="item.id"
              :label="index + 1 +  '-' + item.name"
              :value="item.id"
            ></el-option>
          </el-select>
          <el-select
            v-model="couponSelected"
            placeholder="请选择"
            style="margin-left: 5px"
            @change="changeTp($event)"
          >
            <el-option
              v-for="(item,index) in tp_info"
              :key="item.id"
              :label="dispName[index]"
              :value="item.id"
            ></el-option>
          </el-select>
          <el-select
            v-model="boardSelected"
            placeholder="请选择"
            style="margin-left: 5px"
            @change="changeBoard($event)"
          >
            <template v-for="(item,index) in boardList">
              <el-option v-if="index==0" :key="item.id" label="ALL" :value="item.id"></el-option>
              <el-option v-else :key="item.id" :label="item.name + '_' + index" :value="item.id"></el-option>
            </template>
          </el-select>
        </div>
      </el-header>
    </el-container>
    <el-table
      :data="data"
      height="750"
      v-loading="tableLoading"
      border
      stripe
      max-height="900"
      @selection-change="selectAll"
    >
      <el-table-column type="selection" align="center"></el-table-column>
      <el-table-column type="index" :index="index + 1" align="center"></el-table-column>
      <el-table-column prop="boardId" label="Board ID" align="center"></el-table-column>
      <el-table-column prop="serviceId" label="Service ID " align="center"></el-table-column>
      <el-table-column prop="serviceName" label="Service Name" align="center"></el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="display: flex;justify-content: space-between;margin: 8px 2px;">
      <el-button type="danger" size="mini" :disabled="all_id.length == 0" @click="doDeleteMore">批量删除</el-button>
      <el-pagination
        background
        :page-sizes="[10,20,50,100]"
        :page-size="10"
        :current-page="currentPage"
        @current-change="currentChange"
        @size-change="sizeChange"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      data: [],
      sate_info: [],
      tp_info: [],
      tableLoading: false,
      visible: false,
      index: 0,
      dispName: [],
      aStrUipol: ["H", "V", "L", "R"],
      couponSelected: 0,
      sateSelected: 1,
      boardSelected: 5,
      keyWord: "",
      totalCount: -1,
      currentPage: 1,
      pageSize: 10,
      boardList: [
        { id: 5, name: "All" },
        { id: 0, name: "Board" },
        { id: 1, name: "Board" },
        { id: 2, name: "Board" },
        { id: 3, name: "Board" },
        { id: 4, name: "Board" }
      ],
      all_id: []
    };
  },
  mounted: function() {
    this.getSateInfo();
    this.getTpInfo();
    this.getInfo();
  },
  methods: {
    getInfo() {
      this.tableLoading = true;
      this.$ajax
        .post(
          "http://localhost:8080/programs/selectAllPro?page=" +
            this.currentPage +
            "&size=" +
            this.pageSize +
            "&keyWord=" +
            this.keyWord +
            "&tp_id=" +
            this.couponSelected +
            "&sat_id=" +
            this.sateSelected +
            "&board_id=" +
            this.boardSelected
        )
        .then(response => {
          setTimeout(() => {
            this.tableLoading = false;
          }, 1000);
          this.data = response.data.list;
          this.totalCount = response.data.total;
          this.currentPage = response.data.pageNum;
          console.log(response);
        })
        .catch(err => console.log(err));
    },
    getTpInfo() {
      this.$ajax
        .post("http://localhost:8080/search/getSatInfoById?id=93&max_tp_id=0")
        .then(response => {
          this.tp_info = response.data.tp_info;
          var obj = new Object();
          obj.id = 0;
          this.tp_info.unshift(obj);
          this.dispName.push("ALL");
          for (var i = 0; i < this.tp_info.length; i++) {
            if (this.tp_info[i].id != 0) {
              var str =
                i +
                "-" +
                this.tp_info[i].freq +
                "/" +
                this.tp_info[i].symbolRate +
                "/" +
                this.aStrUipol[this.tp_info[i].polarization];
              this.dispName.push(str);
            }
          }
        })
        .catch(err => console.log(err));
    },
    getSateInfo() {
      this.$ajax
        .post("http://localhost:8080/search/getAllSatllite")
        .then(response => {
          this.sate_info = response.data;
        })
        .catch(err => console.log(err));
    },
    searchPro() {
      this.getInfo();
    },
    handleEdit(index, row) {
      console.log(index, row);
    },
    handleDelete(index, row) {
      console.log(index, row);
      this.$confirm(" 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.doDelete(row.id);
        })
        .catch(() => {});
    },
    doDelete(id) {
      var id_arr = [];
      id_arr.push(id);
      this.$ajax
        .post("http://localhost:8080/programs/deleteProgram", id_arr)
        .then(response => {
          this.getInfo();
          this.$message({
            message: "Successful!",
            type: "success"
          });
        })
        .catch(err => console.log(err));
    },
    doDeleteMore() {
      var id_arr = [];
      for (var i = 0; i < this.all_id.length; i++) {
        id_arr.push(this.all_id[i].id);
      }
      this.$ajax
        .post("http://localhost:8080/programs/deleteProgram", id_arr)
        .then(response => {
          console.log(response);
          this.getInfo();
          this.$message({
            message: "Successful!",
            type: "success"
          });
        })
        .catch(err => console.log(err));
    },
    changeTp(event) {
      this.getInfo();
    },
    changeSate(event) {
      this.getInfo();
    },
    changeBoard() {
      this.getInfo();
    },
    keywordsChange() {
      this.getInfo();
    },
    currentChange(currentChange) {
      this.currentPage = currentChange;
      this.getInfo();
    },
    sizeChange(sizeChange) {
      this.pageSize = sizeChange;
      this.getInfo();
    },
    selectAll(val) {
      console.log(val);
      this.all_id = val;
    }
  }
};
</script>