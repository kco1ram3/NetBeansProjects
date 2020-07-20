<%-- 
    Document   : uploadFile
    Created on : Aug 24, 2013, 6:32:13 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form method="POST" action="UploadFile.do" enctype="multipart/form-data">
            <h3>ไฟล์นำเข้า:</h3>
            <input type="file" name="file" />
            <p>
                <input type="submit" value="บันทึกข้อมูล" />
            </p>
        </form>
        Latitude&nbsp;:&nbsp;${Latitude}
        Longitude&nbsp;:&nbsp;${Longitude}<br />
        <img src="${ImageUpload}" alt="" height="" width="" />
    </body>
</html>
