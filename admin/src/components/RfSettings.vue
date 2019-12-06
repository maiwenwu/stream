<template>
  <div style="padding : 1rem 0;">
    <el-container>
      <el-table :data="all_rf_data" height="700" border max-height="900">
        <el-table-column prop="boardId" label="Board ID" align="center" :formatter="getBoardId"></el-table-column>
        <el-table-column prop="tone" label="22KHz" align="center" :formatter="getTone"></el-table-column>
        <el-table-column prop="lnbPower" label="LNB Power" align="center" :formatter="getLnbPower"></el-table-column>
        <el-table-column
          prop="diseqc1_0"
          label="DiSEqC1.0"
          align="center"
          :formatter="getDiseqc1_0"
        ></el-table-column>
        <el-table-column
          prop="diseqc1_1"
          label="DiSEqC1.1"
          align="center"
          :formatter="getDiseqc1_1"
        ></el-table-column>
        <el-table-column label="Settings" align="center">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.$index, scope.row)" style="border:0;color: blue;">
              <i class="el-icon-setting"></i>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-container>
    <el-dialog :visible.sync="dialogFormVisible" :close-on-click-modal="false" :showClose="false">
      <el-form v-model="rf_data">
        <el-form-item label="Board ID" label-width="100px">
          <el-input v-model="rf_data.boardId" :disabled="true" autocomplete="off" :formatter="getBoardId"></el-input>
        </el-form-item>
        <el-form-item label="22KHz" label-width="100px">
          <el-select v-model="rf_data.tone" style="display:block;">
            <el-option v-for="(item,index) in tone" :key="index" :value="index" :label="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="LNB Power" label-width="100px">
          <el-select v-model="rf_data.lnbPower" style="display:block;">
            <el-option v-for="(item,index) in lnb_power" :key="index" :value="index" :label="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="DiSEqC1.0" label-width="100px">
          <el-select v-model="rf_data.diseqc1_0" style="display:block;">
            <el-option v-for="(item,index) in diseqc1_0" :key="index" :value="index" :label="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="DiSEqC1.1" label-width="100px">
          <el-select v-model="rf_data.diseqc1_1" style="display:block;">
            <el-option v-for="(item,index) in diseqc1_1" :key="index" :value="index" :label="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Close</el-button>
        <el-button type="primary" @click="save">Save</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      all_rf_data: [],
      rf_data: [],
      tone: ["On", "Off"],
      lnb_power: ["13V", "18V"],
      diseqc1_0: ["None", "LNB1", "LNB2", "LNB3", "LNB4"],
      diseqc1_1: [
        "None",
        "LNB1",
        "LNB2",
        "LNB3",
        "LNB4",
        "LNB5",
        "LNB6",
        "LNB7",
        "LNB8",
        "LNB9",
        "LNB10",
        "LNB11",
        "LNB12",
        "LNB13",
        "LNB14",
        "LNB15",
        "LNB16"
      ],
      dialogFormVisible: false
    };
  },
  mounted: function() {
    this.getRfSettings();
  },
  methods: {
    getRfSettings() {
      this.$ajax
        .post("http://localhost:8080/rf/getAllRf")
        .then(response => {
          console.log(response);
          this.all_rf_data = response.data;
        })
        .catch(err => {
          console.log(err);
        });
    },
    getBoardId(val) {
      if (val.boardId == 0) {
        return "1";
      } else if (val.boardId == 1) {
        return "2";
      } else if (val.boardId == 2) {
        return "3";
      } else if (val.boardId == 3) {
        return "4";
      } else if (val.boardId == 4) {
        return "5";
      }
    },
    getTone(val) {
      return this.tone[val.tone];
    },
    getLnbPower(val) {
      return this.lnb_power[val.lnbPower];
    },
    getDiseqc1_0(val) {
      return this.diseqc1_0[val.diseqc1_0];
    },
    getDiseqc1_1(val) {
      return this.diseqc1_1[val.diseqc1_1];
    },
    handleEdit(index, row) {
      this.rf_data = row;
      this.rf_data.boardId = row.boardId;
      this.rf_data.tone = row.tone;
      this.rf_data.lnbPower = row.lnbPower;
      this.rf_data.diseqc1_0 = row.diseqc1_0;
      this.rf_data.diseqc1_1 = row.diseqc1_1;
      this.dialogFormVisible = true;
    },
    save() {
      console.log(this.rf_data);
      this.checkStreamByBoardId();
    },
    checkStreamByBoardId() {
      this.$ajax
        .post(
          "http://localhost:8080/streaming/getStreamByBoardId?board_id=" +
            (this.rf_data.boardId - 1)
        )
        .then(response => {
          console.log(response);
          if (response.data.result) {
            this.checkProgramByBoardId();
          } else {
            this.$confirm(
              " There are some streams in the module, please delete first.",
              "",
              {
                confirmButtonText: "OK",
                cancelButtonText: "Close",
                type: "warning"
              }
            )
              .then(() => {})
              .catch(() => {});
          }
        })
        .catch(err => {
          console.log(err);
        });
    },
    checkProgramByBoardId() {
      this.$ajax
        .post(
          "http://localhost:8080/programs/checkProgramByBoardId?board_id=" +
            (this.rf_data.boardId - 1)
        )
        .then(response => {
          if (response.data) {
            this.saveRfInfo();
          } else {
            this.$confirm(
              "This action will delete all programs on this board.Do you want to continue？",
              "",
              {
                confirmButtonText: "YES",
                cancelButtonText: "NO",
                type: "warning"
              }
            )
              .then(() => {
                this.deleteAllProgramByBoardId();
              })
              .catch(() => {});
          }
        })
        .catch(err => {
          console.log(err);
        });
    },
    deleteAllProgramByBoardId() {
      this.$ajax
        .post(
          "http://localhost:8080/programs/deleteProgramByBoardId?board_id=" +
            (this.rf_data.boardId - 1)
        )
        .then(response => {
          console.log(response);
          this.saveRfInfo();
        })
        .catch(err => {
          console.log(err);
        });
    },
    saveRfInfo() {
      this.$ajax
        .post("http://localhost:8080/rf/saveRfInfo", this.rf_data)
        .then(response => {
          this.$confirm(response.data.msg, "", {
            confirmButtonText: "OK",
            type: "warning"
          })
            .then(() => {})
            .catch(() => {});
        })
        .catch(err => {
          console.log(err);
        });
    }
  }
};
</script>