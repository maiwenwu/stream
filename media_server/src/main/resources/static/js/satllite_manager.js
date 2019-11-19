function SatMgr()
{
    var self = this;
}

$(function() {
	var sat_mgr = new SatMgr();
	sat_mgr.init();
	$('#sat_modal').on("hide.bs.modal", function() {
		$(this).find('input').each(function () {
            $(this).val("")
       });
		$(this).find('select').each(function () {
            $(this).val("")
       });
	})
})

SatMgr.prototype =
{
	 constructor:SatMgr,
	 init:function(){
		var self = this ;
		
		self.common_mgr = new CommonMgr();
		
		self.sat_dir = ['East','West'];
		self.lnb_type = ['Universal','5150/5750','5750/5150','9750/10750','5150','5750','9750','10600','10700','10750','11300','11475'];
		self.band_type =["C","KU"];
		self.sat_list =[];
		self.fuc_state = 0;// 0->>add,1-->edit,2-->delete
		self.checkArray = new Array();
		self.HANDLER_DELETE_SATELLITE = 11;
		self.pageNumber = 1;
		self.check_sat = true;
		
		self.init_view();
		self.init_data();
		self.init_event();
	 },
	 initTableHeight:function(){
		var self = this;
		var height = window.screen.height * 0.45 ;
		$(".table-body").css({"height": height});
	},
	init_view:function()
	{
		var self = this;
		self.initTableHeight();
		self.DialogInit();
	},
	init_data:function()
	{
		var self = this;
		self.doPage();
	},
	init_event:function()
	{
		var self = this;
		$('#page_size').change(function() {
			self.pageNumber = 1;
			self.doPage();
		})
	
		$('#key_word').on('input', function() {
			self.pageNumber = 1;
			self.doPage();
		})
		
		$('#check_all').click(function(){ 
			var ch=$("#satllite_list input[type=checkbox]");
			if(this.checked)
			{
				 for(var i=0;i<ch.length;i++)
	             {
	                 if(ch[i].checked){
	                 }else{
	                     ch[i].checked = true;
	                     self.checkArray.push(ch[i].value);
	                 }
	             }
			}else{
				for (var i = 0; i < ch.length; i++) {
	                ch[i].checked = false;
	                self.checkArray.pop();
	            }
			}
		})
		
		$('#add_sat').click(function() {
			self.fuc_state = 0;
			self.create_add_sat_option(0);
			$('#sat_modal').modal('show');
		})
		
		$('#delete_sat').click(function (){
			self.fuc_state = 2;
			if(self.checkArray.length < 1)
			{
				self.common_mgr.modalShow("","Warning","Please select Satellites to delete",true,false);
				return;
			}else
			{
				// 此处删除数据并且重新加载数据
            	self.checkSat();
            	if (self.check_sat == 2) {
					setTimeout(() => {
						self.common_mgr.modalShow("","Info","There are some streams from the satellite in the module, please delete first.",true,false);
					}, 500);
				} else if (self.check_sat == 1) {
					setTimeout(() => {
						$('#cancel').html("No");
						$('#ok').html("Yes");
						self.common_mgr.modalShow(self.HANDLER_DELETE_SATELLITE,"Info","This action will delete all programs on this satellite. Do you want to continue?",true,true);
					}, 500);
				} else {
					$('#cancel').html("No");
					$('#ok').html("Yes");
					self.common_mgr.modalShow(self.HANDLER_DELETE_SATELLITE,"Warning","Do you want to delete the selected satellite?",true,true);
				}
			}
		}),
		
		$('#edit_sat').click(function (){
			self.fuc_state = 1;
			if(self.checkArray.length != 1)
			{
				self.common_mgr.modalShow("","Warning","Please select one Satellite to edit!",true,false);
				return;
			}
			self.create_add_sat_option(1);
			$('#sat_modal').modal('show');
		})
		
		$('#btn_cancel').click(function() {	
			$('#sat_modal').modal('hide');
		})
		
		$('#btn_ok').click(function(){
			var json_data = null;
			if(self.fuc_state == 0)
			{
				json_data =
				{
					"name":$('#sat_lnb_type').val() == 0 ? "C_"+$('#sat_name').val():$('#sat_name').val(),
					"dir":$('#sat_dir').val(),
					"angle":$('#sat_angle').val() * 10,
					"lnbType":$('#sat_lnb_type').val() == 0 ? self.lnb_type[1]:self.lnb_type[0],
				}
				var data = JSON.stringify(json_data);
				if ($('#sat_name').val() == "") {
					$('#sat_name').focus();
					self.common_mgr.modalShow("","Info","The satellite name cannot be null.",true,false);
				} else if ($('#sat_name').val().length > 25) {
					$('#sat_name').focus();
					self.common_mgr.modalShow("","Info","The satellite name length must be between 0 and 25.",true,false);
				} else if ($('#sat_angle').val() == "") {
					$('#sat_angle').focus();
					self.common_mgr.modalShow("","Info","The angle cannot be null.",true,false);
				} else if ($('#sat_angle').val() < 0 || $('#sat_angle').val() > 360) {
					self.common_mgr.modalShow("","Info","The angles range from 0 to 360",true,false);
				} 
				else {
					// 此处保存数据库
					self.save_sat_data(0,data);
					$('#sat_modal').modal('hide');
				}
				
			}else if(self.fuc_state == 1)
			{
				json_data =
				{
					"id":$('#sat_id').val(),
					"name":$('#sat_name').val(),
					"dir":$('#sat_dir').val(),
					"angle":$('#sat_angle').val() * 10,
					// "lnbType":self.lnb_type[$('#sat_lnb_type').val()]
				}
				var data = JSON.stringify(json_data);
				if ($('#sat_name').val() == "") {
					$('#sat_name').focus();
					self.common_mgr.modalShow("","Info","The satellite name cannot be null.",true,false);
				} else if ($('#sat_name').val().length > 27) {
					$('#sat_name').focus();
					self.common_mgr.modalShow("","Info","The satellite name length must be between 0 and 27.",true,false);
				} else if ($('#sat_angle').val() == "") {
					$('#sat_angle').focus();
					self.common_mgr.modalShow("","Info","The angle cannot be null.",true,false);
				} else if ($('#sat_angle').val() < 0 || $('#sat_angle').val() > 360) {
					self.common_mgr.modalShow("","Info","The angles range from 0 to 360",true,false);
				} else {
					// 此处保存数据库
					self.save_sat_data(1,data);
					$('#sat_modal').modal('hide');
				}
			}
			
		})
	},
	save_sat_data:function(type,json_data){
		var self =this;
		if(type == 0) // add sat
		{
			$.ajax({
				url : "/satllite/addSatllite?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				contentType : "application/json",
				data : json_data,
				async : false,
				success : function(data) {
					if (data.result > 0) {
						self.common_mgr.modalShow("","Info",data.msg,true,false);
					}
				}
			})
		}else if(type == 1) // edit sat
		{
			self.checkSat();
			$.ajax({
				url : "/satllite/updateSatllite?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				contentType : "application/json",
				data : json_data,
				async : false,
				success : function(data) {
					if (data.result > 0) {
						self.common_mgr.modalShow("","Info",data.msg,true,false);	
						$('#sat_modal').modal('hide');
					}
				}
			})
		}
		self.doPage();
	},
	
	deleteSatllite : function() {
		var self = this;
		$.ajax({
			url : "/satllite/deleteSatllite?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			success : function(data) {
				if (data.result > 0) {
					self.doPage();
					setTimeout(() => {
						self.common_mgr.modalShow("","Info",data.msg,true,false);
					}, 500);
				}
			}
		})
	}, 
	
	checkSat:function() {
		var self = this;
		$.ajax({
			url : "/satllite/checkSatToDelete?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			async : false,
			success : function(data) {
				self.check_sat = data.result;
			}
		})
	},
	
	create_add_sat_option:function(type){
		var self =this;
		$("#sat_dir").empty();
		for (var i = 0; i < self.sat_dir.length; i++) {
			$("#sat_dir").append("<option value='" + i + "'>" + self.sat_dir[i] + "</option>");
		}
		
		if(type == 0)
		{
			$("#lnb_show_type").css("display","");
			$("#sat_lnb_type").empty();
			for (var i = 0; i < self.band_type.length; i++) {
				$("#sat_lnb_type").append("<option value='" + i + "'>" + self.band_type[i] + "</option>");
			}
		}else if(type == 1)
		{
			$("#lnb_show_type").css("display","none");
		}
		if(type == 0) // add
		{
			$("#func_type").html("Band");
			$('#myModalLabel').html("Add Satellite");
		}else if(type == 1) // edit
		{
			$('#myModalLabel').html("Edit Satellite");
			var obj = null;
			for(var i= 0; i< self.sat_list.length;i++)
			{
				if(self.checkArray[0] == self.sat_list[i].id){
					obj = self.sat_list[i];
					break;
				}
			}
			$('#sat_id').val(obj.id);
			$('#sat_name').val(obj.name);
			$('#sat_dir').val(obj.dir);
			$('#sat_angle').val(Math.round(obj.angle *10)/100);
			
			var lnb_type_index;
			for(var i=0; i< self.lnb_type.length;i++)
			{
				if(obj.lnbType == self.lnb_type[i])
				{
					lnb_type_index = i;
					break;
				}
			}
			$('#sat_lnb_type').val(lnb_type_index);
		}
	},
	
	DialogInit:function()
	{
		var self =this;
	    options={
	        OkClick:function (handler) {
	            if (handler == self.HANDLER_DELETE_SATELLITE) {
	            	self.deleteSatllite();
	            	self.updateLockSignalBySateList();
				}
	        },
	        CancelClick:function (handler) {
	        }
	    };
	    self.common_mgr.initDialog(options);
	},
	
	updateLockSignalBySateList:function() {
		var self = this;
		$.ajax({
			url : "/search/updateLockSignalBySateList?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify(self.checkArray),
			success : function(data) {
			}
		})
	},
	
	doPage:function() {
		var self = this;
		var page_size = $('#page_size option:selected').val();
		var keyWord = $('#key_word').val() * 10;
		$('#check_all').prop('checked',false);
		self.checkArray.length=0;
		$.ajax({
			url : "/satllite/selectAll?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"page" : self.pageNumber,
				"size" : page_size,
				"keyWord" : keyWord
			},
			success : function(data) {
				self.sat_list = data.list;
				self.createSatllite(self.sat_list);
				$('#total').html(" " + data.total + " ");
				self.createPagination(data);
			}
		})
	},
	
	doPagination:function (obj) {
		var self = this;
		$('#check_all').prop('checked',false);
		self.checkArray.length=0;
		
		var page_size = $('#page_size option:selected').val();
		var keyWord = $('#key_word').val() * 10;
		$.ajax({
			url : "/satllite/selectAll?"+new Date().getTime(),
			type : "post",
			dataType : "json",
			data : {
				"page" : obj.value,
				"size" : page_size,
				"keyWord" : keyWord
			},
			success : function(data) {
				if (data != null) {
					self.pageNumber = data.pageNum;
					self.sat_list = data.list;
					self.createSatllite(self.sat_list);
					$('#total').html(" " + data.total + " ");
					self.createPagination(data);
				}
			}
		})
	},
	
	createSatllite:function(satllite_list) {
		var self =this;
		var page_size = $('#page_size option:selected').val();
		$('#satllite_list').empty();
		var list = '';
		var id = (self.pageNumber-1) * page_size;
		if (satllite_list != null && satllite_list.length > 0) {
			for (var i = 0; i < satllite_list.length; i++) {
				list += "<tr class=\"tr-link\">";
				list += "<td width=\"5%\"><input type=\"checkbox\" value=\""+satllite_list[i].id+"\" class=\"check_line\"/></td>"
				list += "<td width=\"15%\">" + (id + i + 1) + "</td>";
				list += "<td width=\"30%\">" + satllite_list[i].name + "</td>";
				if(satllite_list[i].dir == 0)
				{
					list += "<td width=\"20%\">East</td>";
				}else if(satllite_list[i].dir == 1)
				{
					list += "<td width=\"20%\">West</td>";
				}
				list += "<td width=\"20%\">" + Math.round(satllite_list[i].angle * 10)/100 + "</td>";
				list += "</tr>";
			}
			$('#satllite_list').append(list);
			$('#first').html(" " + (id + 1) + "");
			$('#last').html(" " + (id + satllite_list.length) + "");
		} else {
			$('#first').html(" " + (0) + "");
			$('#last').html(" " + (0) + "");
		}
		
		$('.check_line').click(function(e){
			e.stopPropagation();
			if(this.checked)
			{
				self.checkArray.push(this.value);
			}else{
				for(var i=0;i<self.checkArray.length;i++)
	            {
	                if(self.checkArray[i] == this.value)
	                {
	                    self.checkArray.splice(i,1);
	                }
	            }
			}
			var page_size = $('#page_size option:selected').val();
			if(self.checkArray.length == page_size){
				$('#check_all').prop('checked',true);
			}else{
				$('#check_all').prop('checked',false);
			}
		});
		
		 $('.tr-link').unbind('click').click(function () {
			 $('.tr-link').removeClass("tr-in");
             $(this).addClass("tr-in");
             var tr_in = $(this).find("input[type=checkbox]");
             if(tr_in.prop('checked'))
             {
            	tr_in.prop('checked',false);
				for(var i=0;i<self.checkArray.length;i++)
				{
				    if(self.checkArray[i] == tr_in.val())
				    {
				        self.checkArray.splice(i,1);
				    }
				}
             }else{
            	 tr_in.prop('checked',true);
            	 self.checkArray.push(tr_in.val());
             }
            var page_size = $('#page_size option:selected').val();
 			if(self.checkArray.length == page_size){
 				$('#check_all').prop('checked',true);
 			}else{
 				$('#check_all').prop('checked',false);
 			}
		 });
	},
	
	createPagination:function(data) {
		var self = this;
		$('.pagination').empty();
		var list = '';
		if (data.pageNum == 0) {
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
			list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Previous</button></li>";
			list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
					+ 1 + "\" >" + 1 + "</button></li>"
			list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
			list += "<li  class=\"page-item disabled\"><button class=\"page-link\"value=\"" + 1
					+ "\" >Last</button></li>";
		} else {
			if (data.isFirstPage && !data.isLastPage) {
				if (data.navigateLastPage > 2) {
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
					list += "<li class=\"page-item disabled\"><button class=\"page-link\">Previous</button></li>";
					list += "<li class=\"page-item active\"><button  class=\"page-link\" value=\""
							+ data.pageNum
							+ "\">"
							+ data.pageNum + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1) + "\">"
							+ (data.pageNum + 1) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 2) + "\">"
							+ (data.pageNum + 2) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1)
							+ "\">Next</button></li>"
					list += "<li ><button class=\"page-link\"value=\"" + data.pages
							+ "\">Last</button></li>";
				} else {
					list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
					list += "<li class=\"page-item disabled\"><button class=\"page-link\">Previous</button></li>";
					list += "<li class=\"page-item active\"><button  class=\"page-link\" value=\""
							+ data.pageNum
							+ "\">"
							+ data.pageNum + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1) + "\">"
							+ (data.pageNum + 1) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum + 1)
							+ "\">Next</button></li>"
					list += "<li ><button class=\"page-link\"value=\"" + data.pages
							+ "\">Last</button></li>";
				}
				
			}

			if (!data.isFirstPage && !data.isLastPage) {
				list += "<li ><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
						+ "\">Previous</button></li>";
				list += "<li ><button  class=\"page-link\" value=\""
						+ (data.pageNum - 1) + "\" >"
						+ (data.pageNum - 1) + "</button></li>"
				list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
						+ data.pageNum
						+ "\">"
						+ data.pageNum + "</button></li>"
				list += "<li ><button  class=\"page-link\" value=\""
						+ (data.pageNum + 1) + "\" >"
						+ (data.pageNum + 1) + "</button></li>"
				list += "<li ><button  class=\"page-link\" value=\""
						+ (data.pageNum + 1)
						+ "\">Next</button></li>"
				list += "<li ><button class=\"page-link\"value=\"" + data.pages
						+ "\"  >Last</button></li>";
			}

			if (data.isLastPage && !data.isFirstPage) {
				if (data.navigateLastPage > 2) {
					list += "<li ><button class=\"page-link\"value=\"1\" >First</button></li>";
					list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
							+ "\" >Previous</button></li>";
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 2) + "\">"
							+ (data.pageNum - 2) + "</button></li>"
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 1) + "\">"
							+ (data.pageNum - 1) + "</button></li>"
					list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
							+ (data.pageNum)
							+ "\">"
							+ (data.pageNum) + "</button></li>"
					list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + data.pages
							+ "\">Last</button></li>";
				} else {
					list += "<li ><button class=\"page-link\"value=\"1\" >First</button></li>";
					list += "<li ><button class=\"page-link\"value=\"" + (data.pageNum - 1)
							+ "\" >Previous</button></li>";
					list += "<li ><button  class=\"page-link\" value=\""
							+ (data.pageNum - 1) + "\">"
							+ (data.pageNum - 1) + "</button></li>"
					list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
							+ (data.pageNum)
							+ "\">"
							+ (data.pageNum) + "</button></li>"
					list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
					list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + data.pages
							+ "\">Last</button></li>";
				}
			}

			if (data.isFirstPage && data.isLastPage) {
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"1\">First</button></li>";
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + (data.pageNum - 1)
						+ "\" >Previous</button></li>";
				list += "<li class=\"page-item active\" ><button  class=\"page-link\" value=\""
						+ (data.pageNum) + "\" >" + (data.pageNum) + "</button></li>"
				list += "<li class=\"page-item disabled\"><button  class=\"page-link\" >Next</button></li>"
				list += "<li class=\"page-item disabled\"><button class=\"page-link\"value=\"" + data.pages
						+ "\" >Last</button></li>";
			}
		}
		
		$('.pagination').append(list);
		
		$('.page-link').click(function(){
			self.doPagination(this);
		})
	},
}
