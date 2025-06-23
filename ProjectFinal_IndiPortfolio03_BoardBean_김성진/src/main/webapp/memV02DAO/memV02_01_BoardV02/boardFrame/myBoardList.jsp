<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>내 게시글 관리</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/memV02DAO/memV02_01_BoardV02/css/boardTitleList.css"> 
</head>
<body>

<h2>** ${sessionScope.userName}님의 게시글 관리 **</h2>
<%@ include file="bodTop.jspf" %>

<table class="myTable">
  <thead>
    <tr>
      <th>번호</th>
      <th>제목</th>
      <th>작성일</th>
      <th>조회수</th>
      <th>IP</th>
      <th>수정</th>
      <th>삭제</th>
    </tr>
  </thead>
  <tbody>
    <c:choose>
      <c:when test="${not empty myPostList}">
        <c:forEach var="dto" items="${myPostList}">
          <tr>
            <td>${dto.bod_no}</td>
            <td>${dto.bod_subject}</td>
            <td>${dto.bod_logtime}</td>
            <td>${dto.bod_readCnt}</td>
            <td>${dto.bod_connIp}</td>
            <td>
              <a href="BoardController?action=editFormDirect&bod_no=${dto.bod_no}&bod_pwd=${dto.bod_pwd}">수정</a>
            </td>
            <td>
              <a href="BoardController?action=deleteDirect&bod_no=${dto.bod_no}"
                 onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a>
            </td>
          </tr>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <tr>
          <td colspan="7" style="text-align: center;">작성한 게시글이 없습니다.</td>
        </tr>
      </c:otherwise>
    </c:choose>
  </tbody>
</table>

<div class="pagination">
  <c:if test="${pu.startPage > 0}">
    <a href="BoardController?action=myList&nowPage=${pu.startPage - 1}&nowBlock=${nowBlock - 1}">≪</a>
  </c:if>

  <c:if test="${pu.endPage >= pu.startPage}">
    <c:forEach var="i" begin="${pu.startPage}" end="${pu.endPage}">
      <c:choose>
        <c:when test="${i == nowPage}">
          <a href="BoardController?action=myList&nowPage=${i}&nowBlock=${nowBlock}" class="active">${i + 1}</a>
        </c:when>
        <c:otherwise>
          <a href="BoardController?action=myList&nowPage=${i}&nowBlock=${nowBlock}">${i + 1}</a>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>

  <c:if test="${pu.endPage < pu.totalPage - 1}">
    <a href="BoardController?action=myList&nowPage=${pu.endPage + 1}&nowBlock=${nowBlock + 1}">≫</a>
  </c:if>
</div>

<%@ include file="bodBottom.jspf" %>
</body>
</html>
