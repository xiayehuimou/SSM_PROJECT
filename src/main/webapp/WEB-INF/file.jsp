<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>简单文件上传示例</title>
</head>
<body>
<form method="post" action="/ssm_demo/upload" enctype="multipart/form-data">
    <input type="file" name="multiFiles"/><br/>
    <input type="file" name="multiFiles"/><br/>
    <input type="file" name="multiFiles"/><br/>
    <input type="submit" value="上传"/>
</form>
<div>
    <h3>code:${code},message:${message}</h3>
</div>
</body>
</html>