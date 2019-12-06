<template>
  <div style="padding : 0px 1rem;">
    <div class="card-body">
      <el-form label-position="right" label-width="150px">
        <el-form-item label="Board ID :">
          <el-select
            v-model="boardSelected"
            placeholder="请选择"
            style="display:block;"
            @change="changeBoard($event)"
          >
            <template v-for="(item,index) in boardList">
              <el-option v-if="index == 0" :key="item.id" label=" " :value="item.id"></el-option>
              <el-option v-else :key="item.id" :label="item.name + '_' + index" :value="item.id"></el-option>
            </template>
          </el-select>
        </el-form-item>
        <div v-if="this.boardSelected != 5">
          <el-form-item label="22KHz :">
            <el-input :disabled="true" :value="tone[rf_info.tone]"></el-input>
          </el-form-item>
          <el-form-item label="LNB Power :">
            <el-input :disabled="true" :value="lnb_power[rf_info.lnbPower]"></el-input>
          </el-form-item>
          <el-form-item label="DiSEqC1.0 :">
            <el-input :disabled="true" :value="diseqc1_0[rf_info.diseqc1_0]"></el-input>
          </el-form-item>
          <el-form-item label="DiSEqC1.1 :">
            <el-input :disabled="true" :value="diseqc1_1[rf_info.diseqc1_1]"></el-input>
          </el-form-item>
        </div>

        <el-form-item label="Satellite :">
          <el-select
            style="display:block;"
            v-model="sateSelected"
            @change="changeSate"
            :disabled="this.boardSelected == 5"
          >
            <el-option
              v-for="(item,index) in sate_info"
              :key="item.id"
              :value="item.id"
              :label="index + 1 +  '-' + item.name"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Transponder :">
          <el-select
            style="display:block;"
            v-model="tpSelected"
            :disabled="this.boardSelected == 5"
            @change="changeTp"
          >
            <el-option
              v-for="(item,index) in tp_info"
              :key="item.id"
              :value="item.id"
              :label="dispName[index]"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="LNB Type :">
          <el-select
            style="display:block;"
            v-model="lnbTypeSelect"
            :disabled="this.boardSelected == 5"
            @change="changeLnbType"
          >
            <el-option v-for="(item) in lnb_freq" :key="item" :value="item" :label="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Strength :">
          <el-progress :text-inside="true" :stroke-width="40" :percentage="strength"></el-progress>
        </el-form-item>
        <el-form-item label="Quality :">
          <el-progress :text-inside="true" :stroke-width="40" :percentage="quality"></el-progress>
        </el-form-item>
        <el-form-item label>
          <el-button
            type="primary"
            style="display:block;width:100%"
            :disabled="this.boardSelected == 5"
          >TP Search</el-button>
        </el-form-item>
        <el-form-item label>
          <el-button
            type="success"
            style="display:block;width:100%"
            :disabled="this.boardSelected == 5"
          >Satellite Search</el-button>
        </el-form-item>
        <el-form-item label>
          <el-button
            type="warning"
            style="display:block;width:100%"
            :disabled="this.boardSelected == 5"
          >Blind Search</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { setInterval } from "timers";
export default {
  data() {
    return {
      boardSelected: 5,
      sateSelected: 1,
      tpSelected: 1,
      lnbTypeSelect: "",
      rf_info: [],
      sate_info: [],
      tp_info: [],
      lnb_type: "",
      strength: 0,
      quality: 0,
      boardList: [
        { id: 5, name: "" },
        { id: 0, name: "Board" },
        { id: 1, name: "Board" },
        { id: 2, name: "Board" },
        { id: 3, name: "Board" },
        { id: 4, name: "Board" }
      ],
      lnb_freq: [
        "5150",
        "5700",
        "5750",
        "9750",
        "10600",
        "10750",
        "11250",
        "11300",
        "11475",
        "5150/5750",
        "Universal",
        "9750/10750",
        "9750/10700"
      ],
      tone: ["On", "Off"],
      lnb_power: ["13V", "18V"],
      diseqc1_0: ["None", "LNB1", "LNB2", "LNB3", "LNB4", "LNB5"],
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
      dispName: [],
      aStrUipol: ["H", "V", "L", "R"],
      startInterval: "",
      isLock: false,
      search_info: []
    };
  },
  mounted: function() {
    this.getSateInfo();
    this.getTpInfoBySateId();
    this.startInterval = setInterval(() => {
      //使用箭头函数，如果不适用箭头函数，那么this指向的是windows。
      if (this.isLock) {
        this.getSearchStatus();
      }
    }, 1000);
  },
  methods: {
    getRfInfo() {
      this.$ajax
        .post(
          "http://localhost:8080/rf/getRfByBoardId?board_id=" +
            this.boardSelected
        )
        .then(response => {
          this.rf_info = response.data;
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
    getTpInfoBySateId() {
      this.$ajax
        .post(
          "http://localhost:8080/search/getSatInfoById?id=" +
            this.sateSelected +
            "&max_tp_id=0"
        )
        .then(response => {
          this.dispName = [];
          this.lnbTypeSelect = response.data.lnb_type;
          this.tp_info = response.data.tp_info;
          this.tpSelected = this.tp_info[0].id;
          for (var i = 0; i < this.tp_info.length; i++) {
            var str =
              i +
              1 +
              "-" +
              this.tp_info[i].freq +
              "/" +
              this.tp_info[i].symbolRate +
              "/" +
              this.aStrUipol[this.tp_info[i].polarization];
            this.dispName.push(str);
          }
        })
        .catch(err => console.log(err));
    },
    changeSate() {
      this.getTpInfoBySateId();
      this.doLockSignal();
    },
    changeTp() {
      this.doLockSignal();
    },
    changeLnbType() {
      this.doLockSignal();
    },
    changeBoard(board_id) {
      if (board_id < 5) {
        this.getRfInfo();
        this.doLockSignal();
      } else {
        this.isLock = false;
      }
    },
    doLockSignal() {
      this.$ajax
        .post(
          "http://localhost:8080/search/lockSignal?board_id=" +
            this.boardSelected +
            "&module_id=0&sat_id=" +
            this.sateSelected +
            "&tp_id=" +
            this.tpSelected +
            "&lnb_type=" +
            this.lnbTypeSelect
        )
        .then(response => {
          if (response.data.data == "SUCCESS:") {
            this.isLock = true;
          }
        })
        .catch(err => console.log(err));
    },
    getSearchStatus() {
      this.$ajax
        .post(
          "http://localhost:8080/search/getSearchStatus?board_id=" +
            this.boardSelected +
            "&module_id=0"
        )
        .then(response => {
          this.search_info = JSON.parse(response.data.data);
          this.strength = this.search_info.streng;
          this.quality = this.search_info.quality;
        })
        .catch(err => console.log(err));
    }
  }
};
</script>

<style lang="less">
.card-body {
  padding: 1.25rem;
  width: 50%;
  margin-left: 20%;
}
</style>