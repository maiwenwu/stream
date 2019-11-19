function LoginMgr()
{
    var self = this;
    self.init();
}

$(function() {
	var login_mgr = new LoginMgr();
})

LoginMgr.prototype =
{
	constructor:LoginMgr,
	init:function()
	{
		var self = this;
		
		$("body").keydown(function(e){
            var ev = window.event || e;
            var code = ev.keyCode || ev.which;
            if (code == 13) {
            	$("#login").focus();
            }
        });
		
		self.common_mgr = new CommonMgr();
		
		self.init_view();
		self.init_data();
		self.init_event();
	},
	init_view:function()
	{
		var self = this;
        var strName = localStorage.getItem('username');
        var strPass = localStorage.getItem('password');
        var check_status = localStorage.getItem('checkbox');
        if(check_status == 1)
        {
        	 $("#remember-me").attr("checked","true")
        }
        $('#user_name').val(strName);
		$('#password').val(strPass);
        
	},
	init_data:function()
	{
		var self = this;
	},
	init_event:function()
	{
		var self = this;
		$('#login').click(function() {
			var username = $('#user_name').val();
			var password = $('#password').val();
			var data = {
					"userName" : username,
					"password" : password
			}
            if($('#remember-me').is(':checked')){
                localStorage.setItem('username',username);
                localStorage.setItem('password',password);
                localStorage.setItem('checkbox',1);
            }else{
                localStorage.removeItem('username');
                localStorage.removeItem('password');
                localStorage.removeItem('checkbox');
            }
            
			var user_info = JSON.stringify(data);
			$.ajax({
				url : "/user/loginUser?"+new Date().getTime(),
				type : "post",
				dataType : "json",
				contentType : "application/json",
				data : user_info,
				success : function(data) {
					console.log(data);
					if (data.result) {
						window.location.href = "/system_info";
					} else {
						self.common_mgr.modalShow("","Info","Login failed, please check username or password!",true,false);
					}
				}
			})
		})
	},
}