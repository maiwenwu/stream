function ConfigurationMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var configuration_mgr = new ConfigurationMgr();
})

ConfigurationMgr.prototype =
{
	constructor:ConfigurationMgr,
	init:function()
	{
		var self = this;
		
		self.common_mgr = new CommonMgr();
		
		self.init_view();
		self.init_data();
		self.init_event();
		self.init_wait_modal();
	},
	init_wait_modal:function(){
		var self = this;
		$('#modal_wait').on('show.bs.modal', function(){
          var $this = $(this);
          var $modal_dialog = $this.find('.modal-dialog');
          // 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
          $this.css('display', 'block');
          $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) });
        });
		
		$(document.body).css({
			  "overflow-x":"auto",
			  "overflow-y":"auto"
			  });
	},
	init_view:function()
	{
		var self = this;
	},
	init_data:function()
	{
		var self = this;
	},
	init_event:function()
	{
		var self = this;
		
		$('#import').click(function () {
			 $("#file").trigger("click");
		})
		
		$('#upload').click(function () {
			 $("#uploadFile").trigger("click");
		})
		
		$('input[id=file]').change(function(e) {
			
			var filepath = $(this).val();
			var index1 = filepath.lastIndexOf("\\");
			var index2 = filepath.length;
			var filename = filepath.substring(index1 + 1, index2);
			$('#fileName').val(filename);
			var fileExtension = filename.split('.').pop().toLowerCase();
			if (fileExtension == "xls" || fileExtension == "xlsx") {
				$("#fileName").css({
					"color" : ""
				});
				self.uploadExcel();
			} else {
				self.common_mgr.modalShow("", "info", "The file format is wrong!", true, false);
				$("#fileName").css({
					"color" : "red"
				});
			}
			e.target.value = '';
		});
	},
	uploadExcel : function() {
		var self = this;
		$('#modal_wait').modal('toggle');
		var form = new FormData(document.getElementById("uploadExcelForm"));
		$.ajax({
	        url: "/configuration/importExcel?"+new Date().getTime(),
	        type: "post",
	        data: form,
	        processData: false,
	        contentType: false,
	        success: function(result) {
	        	console.log("result:",result);
	        	$('#modal_wait').modal('toggle');
	        	if (result == 1) {
	        		self.common_mgr.modalShow("1","Warning","Successful, please refresh the page!",false,false);
		        	self.execRestartProgram();
				} else {
					self.common_mgr.modalShow("","Warning","Import failed!",true,false);
				}
	        },
	    });
	},
	execRestartProgram:function (){
		var self = this;
		$.ajax({
			type : "get",
			url : "/os/execRestartProgram?"+new Date().getTime(),
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
			}
		})
	},
}


