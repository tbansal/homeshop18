<html>
<head>
<title>Rewards Basic example</title>
</head>
<body>
<p>Rewards Basic example</p>
<p><%= request.getAttribute("message") == null ? "" : request.getAttribute("message") %></p>

This is login jsp
<form action="j_security_check" method=post>
    <p><strong>Please Enter Your User Name: </strong>
    <input type="text" name="j_username" size="25">
    <p><p><strong>Please Enter Your Password: </strong>
    <input type="password" size="15" name="j_password">
    <p><p>
    <input type="submit" value="Submit">
    <input type="reset" value="Reset">
</form>
</body>
</html>