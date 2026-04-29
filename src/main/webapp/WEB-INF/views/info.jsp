<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 1. 20.
  Time: 오후 4:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>잡모아</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css" />
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
    <!-- sweetalert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

    <script>
        $(document).ready(function(){
            let icon = '${icon}';
            // console.log(icon);
            let title = '${title}';
            // console.log(title);
            let message = '${message}';
            // console.log(message);
            let url = '${url}';
            // console.log(url);

            if(icon == 'success'){
                alertDefaultSuccess(title, message).then((r) => {
                    // console.log(r);
                    location.href = url;
                });
            }
            else if(icon == 'error'){
                alertDefaultError(title, message).then(()=>{
                    location.href = url;
                });
            }
            else if(icon == 'info'){
                alertDefaultInfo(title, message).then(()=>{
                    location.href = url;
                });
            }
            else if(icon == 'question'){
                alertDefaultQuestion(title, message).then(()=>{
                    location.href = url;
                });
            }
            else if(icon == 'back'){
                alertDefaultInfo(title, message).then(()=>{
                    window.history.back();
                });
            }
            else if(icon == 'warning'){
                alertDefaultWarning(title, message).then(()=>{
                    location.href = url;
                });
            }
            else{
                alertDefaultWarning('잘못된 접근', '메인 페이지로 돌아갑니다.').then(()=>{
                    location.href = 'login.do';
                });
            }
        })
    </script>

</head>
<body>
</body>
</html>