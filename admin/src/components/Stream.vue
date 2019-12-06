<template>
  <div style="padding : 0px 1rem;">
    <div class="card-body">
      <el-form label-position="right" label-width="150px" :model="output" :rules="rules">
        <el-form-item label="Board ID :" prop="boardId">
          <el-input v-model="output.boardId"></el-input>
        </el-form-item>
        <el-form-item label="Module ID :">
          <el-input v-model="output.moduleId"></el-input>
        </el-form-item>
        <el-form-item label="Type :">
          <el-input v-model="output.type"></el-input>
        </el-form-item>
        <el-form-item label="programId :">
          <el-input v-model="output.programId"></el-input>
        </el-form-item>
        <el-form-item label=" setPids :">
          <el-input v-model="output. setPids"></el-input>
        </el-form-item>
        <el-form-item label="audioPids :">
          <el-input v-model="output.audioPids"></el-input>
        </el-form-item>
        <el-form-item label="channel ID :">
          <el-input v-model="output.lableId"></el-input>
        </el-form-item>
        <el-form-item label="Gx protocol :">
          <el-input v-model="output.outMode"></el-input>
        </el-form-item>
        <el-form-item label="Gx ip :">
          <el-input v-model="output.outIp"></el-input>
        </el-form-item>
        <el-form-item label="hlsOutProtocol  :">
          <el-input v-model="output.hlsOutMode"></el-input>
        </el-form-item>
        <el-form-item label="hlsOutIp  :">
          <el-input v-model="output.hlsOutIp"></el-input>
        </el-form-item>
        <el-form-item label="hlsOutPort  :">
          <el-input v-model="output.hlsOutPort"></el-input>
        </el-form-item>
        <el-form-item label>
          <el-button type="primary" style="display:block;width:100%" @click="addStream">Add</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      output: {
        id: "",
        boardId: "",
        moduleId: "",
        type: "",
        programId: "",
        setPids: "",
        audioPids: "",
        hlsOutIp: "",
        hlsOutMode: "",
        hlsOutPort: "",
        lableId: "",
        outIp: "",
        outMode: "",
        subtitlePids: ""
      },
      rules: {
        boardId: [{ required: true, message: "必填:board id", trigger: "blur" }]
      }
    };
  },
  methods: {
    addStream() {
      this.$ajax
        .post("http://localhost:8080/streaming/addOutputConfig", this.output)
        .then(response => {
          console.log(response);
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