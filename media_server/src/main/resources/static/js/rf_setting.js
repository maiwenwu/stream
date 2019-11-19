function RfMgr()
{
	var self = this;
}

$(function() {
	var rf_mgr = new RfMgr(); 
	rf_mgr.init();
});

RfMgr.prototype =
{
	constructor:RfMgr,
	init:function()
	{
		var self = this;
		self.common_mgr = new CommonMgr();
		self.tone = [ 'On', 'Off' ];
		self.lnb_power = [ '13V', '18V' ];
		self.diseqc1_0 = [ 'None', 'LNB1', 'LNB2', 'LNB3', 'LNB4' ];
		self.diseqc1_1 = [ 'None', 'LNB1', 'LNB2', 'LNB3', 'LNB4', 'LNB5', 'LNB6', 'LNB7', 'LNB8', 'LNB9', 'LNB10', 'LNB11', 'LNB12', 'LNB13', 'LNB14', 'LNB15', 'LNB16' ];

		self.RF_UPDATE = 1;
		self.rf_list ="";
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self = this;
		self.DialogInit();
	},
	init_data:function()
	{
		var self = this;
		self.getAllRf();
	},
	init_event:function()
	{
		var self = this;
		$('#close').click(function() {	
			$('#myModal').modal('hide'); //关闭modal
		})
		
		$('#save').click(function() {
			var board_id = $('#board_id').val()-1;
			self.checkStreamByBoardId(board_id);
		})
		
	},
	DialogInit:function()
	{
		var self = this;
	    options={
	        OkClick:function (handler) {
	            if(handler == self.RF_UPDATE)
	            {
	            	self.deleteAllProgramByBoardId();
	            }
	        },
	        CancelClick:function (handler) {
	        }
	    };
	   self.common_mgr.initDialog(options);
	},
	getAllRf:function() {
		var self = this;
		$.ajax({
			type : "post",
			url : "/rf/getAllRf?"+new Date().getTime(),
			dataType : "json",
			success : function(data) {
				if (data != null) {
					self.drawRfTable(data);
					self.rf_list = data;
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	},
	drawRfTable:function(data) {
		var self = this;
		var list = "";
		for (var i = 0; i < data.length; i++) {
			list += "<tr>";
			list += "<td>" + (data[i].boardId + 1) + "</td>";
			list += "<td>" + self.tone[data[i].tone] + "</td>";
			list += "<td>" + self.lnb_power[data[i].lnbPower] + "</td>";
			list += "<td>" + self.diseqc1_0[data[i].diseqc1_0] + "</td>";
			list += "<td>" + self.diseqc1_1[data[i].diseqc1_1] + "</td>";
			list += "<td><button class=\"create\" value=\""
					+ data[i].boardId
					+ "\"><i class=\"fa fa-cog\" title=\"Settings\" ></i></button></td>";
			list += "</tr>";
		}
		$(".rf_list").html(list);
		self.addClickFunction();
	},
	addClickFunction:function()
	{
		var self = this;
		$('.create').click(function() {	 
			var id = $(this).val();
			self.creatOptions(id);
			$('#myModal').modal('show') //显示modal
		})
	},
	creatOptions:function(board_id) {
		var self = this;
		$("#tone").empty();
		for (var i = 0; i < self.tone.length; i++) {
			$("#tone").append("<option value='" + i + "'>" + self.tone[i] + "</option>")
		}

		$("#lnb_power").empty();
		for (var i = 0; i < self.lnb_power.length; i++) {
			$("#lnb_power").append(
					"<option value='" + i + "'>" + self.lnb_power[i] + "</option>")
		}

		$("#diseqc1_0").empty();
		for (var i = 0; i < self.diseqc1_0.length; i++) {
			$("#diseqc1_0").append(
					"<option value='" + i + "'>" + self.diseqc1_0[i] + "</option>")
		}

		$("#diseqc1_1").empty();
		for (var i = 0; i < self.diseqc1_1.length; i++) {
			$("#diseqc1_1").append(
					"<option value='" + i + "'>" + self.diseqc1_1[i] + "</option>")
		}
		for(var i = 0; i < self.rf_list.length; i++)
		{
			if(self.rf_list[i].boardId == board_id)
			{
				var data = self.rf_list[i];
				$('#board_id').val(data.boardId*1+1);
				$('#tone').val(data.tone);
				$('#lnb_power').val(data.lnbPower);
				$('#diseqc1_0').val(data.diseqc1_0);
				$('#diseqc1_1').val(data.diseqc1_1);
			}
		}
	},
	checkStreamByBoardId:function(board_id) {
		var self = this;
		$.ajax({
			url : "/streaming/getStreamByBoardId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id
			},
			async : false,
			success : function(data) {//true--> 没有推流节目　　false--> 有推流节目
				if (data.result) {
					self.checkProgramByBoardId(board_id);
				} else {
					self.common_mgr.modalShow("","Info","There are some streams in the module, please delete first.",true,false);
				}
			}
		})
	},
	checkProgramByBoardId:function(board_id) {
		var self = this;
		$.ajax({
			url : "/programs/checkProgramByBoardId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id
			},
			async : false,
			success : function(data) {//true-->没有节目	false--> 有节目
				if (data) {
					self.saveRfInfo();
				} else {
					$('#cancel').html("No");
					$('#ok').html("Yes");
					self.common_mgr.modalShow(self.RF_UPDATE,"Warning","This action will delete all programs on this board.Do you want to continue？",true,true);
				}
			}
		})
	},
	saveRfInfo:function() {
		var self = this;
		var board_id = $('#board_id').val()-1;
		var tone = $('#tone').val();
		var lnbPower = $('#lnb_power').val();
		var diseqc1_0 = $('#diseqc1_0').val();
		var diseqc1_1 = $('#diseqc1_1').val();
		
		var data = {
				"boardId" : board_id,
				"tone" : tone,
				"lnbPower" : lnbPower,
				"diseqc1_0" : diseqc1_0,
				"diseqc1_1" : diseqc1_1
			}
		
		$.ajax({
			type : "post",
			url : "/rf/saveRfInfo?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			data : JSON.stringify(data),
			sync : false,
			success : function(data) {
				console.log(data);
				if (data.result == 1) {
					$('#myModal').modal('hide'); //关闭modal
					self.getAllRf();
					setTimeout(() => {
						self.common_mgr.modalShow("","Info",data.msg,true,false);
					}, 500);
					for(var i = 0;i < 6;i++)
					{
						self.doLockSignal(i);
						self.updateLockSignal(i);
					}
				}
			}
		})
	},
	deleteAllProgramByBoardId:function() {
		var self = this;
		var board_id = $('#board_id').val()-1;
		$.ajax({
			url : "/programs/deleteProgramByBoardId?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id,
			},
			async : false,
			success : function(data) {
				self.saveRfInfo();
			}
		})
	},
	doLockSignal:function(module_id) {
		var self = this;
		var board_id = $('#board_id').val()-1;
		$.ajax({
			url : "/search/lockSignal?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"board_id" : board_id ,
				"module_id" : module_id,
				"sat_id" : "",
				"tp_id" : "",
				"lnb_type" : ""
			},
			success : function(data) {
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
	},
	updateLockSignal:function(module_id) {
		var self = this;
		var board_id = $('#board_id').val()-1;
		var data = {
			"boardId" : board_id,
			"moduleId" : module_id,
			"satId" : 0,
			"tpId" : 0,
			"status" : 0
		}
		var lock_json = JSON.stringify(data);
		$.ajax({
			url : "/streaming/updateLockSignal?"+new Date().getTime(),
			type : "post",
			contentType : "application/json",
			dataType : "json",
			data : lock_json,
			success : function(data) {
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("textStatus:", textStatus);
			}
		})
	},
}





