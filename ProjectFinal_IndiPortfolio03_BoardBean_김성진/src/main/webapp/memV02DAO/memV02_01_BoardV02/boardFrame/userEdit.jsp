<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${empty sessionScope.loginUser}">
    <c:redirect url="UserController?action=loginForm"/>
  </c:when>
</c:choose>

<c:set var="user" value="${sessionScope.loginUser}" />
<c:set var="imgName" value="${user.profileImg}" />
<c:set var="imgPath" value="${empty imgName ? pageContext.request.contextPath.concat('/profiles/default.png') : pageContext.request.contextPath.concat('/profiles/').concat(imgName)}" />

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>회원정보 수정</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/memV02DAO/memV02_01_BoardV02/css/userEdit.css">
</head>
<body>
  <h2>회원정보 수정</h2>

  <div class="form-wrapper">
    <form action="${pageContext.request.contextPath}/UserController?action=updateProfile" method="post" enctype="multipart/form-data">
      <input type="hidden" name="deleteProfileImg" id="deleteProfileImg" value="false">

      <table>
        <tr>
          <td>프로필 이미지</td>
          <td>
            <img src="${imgPath}" width="120" height="120" alt="프로필" id="profilePreview">
            <br>
            <input type="file" name="newProfileImg" accept="image/*">
            <input type="button" id="delBtn" value="삭제">
          </td>
        </tr>
        <tr>
          <td>아이디</td>
          <td>${user.userId}</td>
        </tr>
        <tr>
          <td>이메일</td>
          <td><input type="email" name="userEmail" value="${user.userEmail}" required></td>
        </tr>
        <tr>
          <td>현재 비밀번호</td>
          <td><input type="password" name="currentPw" id="pw" required></td>
        </tr>
        <tr>
          <td>새 비밀번호</td>
          <td><input type="password" name="newPw" id="newPw"></td>
        </tr>
        <tr>
          <td>비밀번호 확인</td>
          <td>
            <input type="password" id="pwCheck">
            <span id="msg" style="color:red;"></span>
          </td>
        </tr>
      </table>
      <br>
      <input type="hidden" name="userId" value="${user.userId}">
      <input type="submit" value="정보 수정" onclick="return validatePassword();">
      <input type="button" value="취소" onclick="history.back();">
    </form>
  </div>

  <script>
    document.addEventListener("DOMContentLoaded", function () {
      document.getElementById("delBtn").addEventListener("click", function () {
        const img = document.getElementById("profilePreview");
        img.src = "${pageContext.request.contextPath}/profiles/default.png";
        document.getElementById("deleteProfileImg").value = "true";
      });

      window.validatePassword = function () {
        var newPw = document.getElementById("newPw").value;
        var pwCheck = document.getElementById("pwCheck").value;
        var msg = document.getElementById("msg");

        if (newPw || pwCheck) {
          if (newPw !== pwCheck) {
            msg.innerText = "비밀번호가 일치하지 않습니다.";
            return false;
          }
        }

        msg.innerText = "";
        return true;
      };
    });
  </script>

  <p class="pFooter">ⓒCopyright 김성진</p>
</body>
</html>
