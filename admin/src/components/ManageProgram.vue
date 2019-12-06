/* eslint-disable */
<template>
  <div style="padding : 0px 1rem;">
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
      height="700"
      v-loading="tableLoading"
      border
      max-height="900"
      @selection-change="selectAll"
    >
      <el-table-column type="selection" align="center"></el-table-column>
      <el-table-column type="index" :index="table_index" align="center"></el-table-column>
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
        layout="total, sizes, jumper, prev, pager, next"
        :total="totalCount"
      ></el-pagination>
    </div>
    <el-dialog title="收货地址" :visible.sync="dialogFormVisible">
      <el-form v-model="data">
        <el-form-item label="活动名称" label-width="80px">
          <el-input v-model="data.serviceName" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="活动区域" label-width="80px">
          <el-select v-model="data.tpId" placeholder="请选择活动区域" style="display:block;">
            <el-option label="区域一" value="shanghai"></el-option>
            <el-option label="区域二" value="beijing"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
      </div>
    </el-dialog>
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
      sateSelected: 93,
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
      all_id: [],
      dialogFormVisible: false
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
        .post(
          "http://localhost:8080/search/getSatInfoById?id=" +
            this.sateSelected +
            "&max_tp_id=0"
        )
        .then(response => {
          this.dispName = [];
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
      this.dialogFormVisible = true;
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
      this.getTpInfo();
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
    },
    table_index(index) {
      return (this.currentPage - 1) * this.pageSize + index + 1;
    }
  }
};
</script>

<style lang="less">
.table {
  width: 100%;
  max-width: 100%;
  background-color: transparent;
  border-collapse: collapse;
}
.col-form-label {
  white-space: nowrap;
  text-align: left;
  margin: 0 auto;
  padding-top: calc(0.375rem + 1px);
  padding-bottom: calc(0.375rem + 1px);
  line-height: 1.5;
  display: inline-block;
  padding-right: 20px;
}
.form-control {
  display: block;
  width: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  color: #495057;
  background-color: #fff;
  background-image: none;
  background-clip: padding-box;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
}
tr {
  margin-bottom: 1rem;
}
</style>