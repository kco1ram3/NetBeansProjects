<%-- 
    Document   : index
    Created on : Nov 18, 2012, 2:57:24 PM
    Author     : puttipong
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Message Sender</title>
    </head>
    <body>
        <form name="form1" action="PostMessage" method="post">
            <table>
                <tr>
                    <td>Message: </td><td><input type="text" name="message" /></td><td><button type="submit">send</button></td>
                </tr>
            </table>
        </form>    
    </body>
</html>
