<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	 <div>
		<form method="POST" action='<c:url value="/extract/document"></c:url>' enctype="multipart/form-data">
			<!-- File to upload: <input type="file" name="file"><br />
		    <input type="submit" value="Upload"> Press here to upload the file! -->
		    <input type="submit" value="Load Documents">
		</form>
	</div> 
	
	<div>
		<form method="POST" action='<c:url value="/assesment/questions"></c:url>' enctype="multipart/form-data">
		    <input type="submit" value="Load Assesment">
		</form>
	</div> 
	${message}
	<hr>
	<form action='<c:url value="/search/document"></c:url>'>
		 Search : <input type="text" name="q">
		<input type="submit" value="Search">
	</form>
</body>
</html>
