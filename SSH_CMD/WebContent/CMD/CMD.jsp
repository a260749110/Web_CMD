<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>CMD</title>
<script src="../js/jquery-1.12.0.min.js"></script>
</head>
<body>
	<div>
		<span>ip:</span><input type="text" id="host_" value="<%=(request.getSession().getAttribute("host")==null)?"":request.getSession().getAttribute("host")%>"> <span>port:</span><input
			type="text" id="port_" value="<%=(request.getSession().getAttribute("port")==null)?"":request.getSession().getAttribute("port")%>"> <span>user:</span><input type="text"
			id="user_" value="<%=(request.getSession().getAttribute("user")==null)?"":request.getSession().getAttribute("user")%>"> <span>passw:</span><input type="password"
			id="passw_" value="<%=(request.getSession().getAttribute("passw")==null)?"":request.getSession().getAttribute("passw")%>">
		<button id="connect_" onclick="connect()">链接</button>
	</div>
	<div>
		<textarea id="showView" rows="30" cols="100" readonly="readonly"></textarea>
	</div>
	<div>
		<span id ="title_"></span><input type="text" id="cmd_input" width="70%" onkeypress="keyPress()" onkeydown="keyPress()">
		<button onclick="cmd()">提交</button>
	</div>

</body>
<script type="text/javascript">
	var i = 0;
	var key;

	function keyPress(event)
	{
		var e = event || window.event || arguments.callee.caller.arguments[0]; 
		if(e && e.keyCode==13){
			cmd();
		}
		
		//if(e && e.keyCode==27)
	//		{
		//	alert("a");
		//	exit();
		//	}
		
	}
	function exit()
	{
		var cmdData = $("#cmd_input").val();
		$("#cmd_input").val("");
		 {
			$.post("CMD_EXIT.jsp", {
				'key' : key,
				'cmd' : cmdData
			}, function(d, s) {
			

			})
		}
	}
	function connect()
	{
	
		$.post("CMD_LAND.jsp", {
			'host' : $("#host_").val(),
			'port' : $("#port_").val(),
			'user' : $("#user_").val(),
			'passw' :$("#passw_").val()
		}, function(d, s) {
			
			data = eval("(" + d + ")");
			key = data.key;
		}
	);
	}
	setInterval(function() {
		//要执行的代码                    
		if (key.length == 0)
			return;
		$.post("CMD_Reader.jsp", {
			'key' : key
		}, function(d, s) {
			data = eval("(" + d + ")");

			$("#showView").html($("#showView").html() + data.msg);
			key = data.key;
			if (data.msg.length > 0) {
				var show = document.getElementById("showView");
				show.scrollTop = show.scrollHeight;
				$("#title_").html(data.title+":");
				
			}

		})
	}, 500);
	function cmd() {
		var cmdData = $("#cmd_input").val();
		$("#cmd_input").val("");
		if (cmdData.length > 0) {
			$.post("CMD_Send.jsp", {
				'key' : key,
				'cmd' : cmdData
			}, function(d, s) {
				data = eval("(" + d + ")");
				if (data.flag == false) {
					key = "";
				}

			})
		}
	}
</script>

</html>