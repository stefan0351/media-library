<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ListIterator" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <title>MediaLib - <s:property value="pageTitle"/></title>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/style/style.css"/>
    <script language="JavaScript" src="<%=request.getContextPath()%>/js/overlib.js"></script>
    <script language="JavaScript" src="<%=request.getContextPath()%>/js/popup.js"></script>
    <script language="JavaScript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><s:property value="pageTitle"/></media:title>

<media:body>
    <media:sidebar>
        <tiles:useAttribute id="menuList" name="menus" classname="java.util.List" />
        <%
            List menus=(List) pageContext.getAttribute("menuList");
            ListIterator menuIterator=menus.listIterator(menus.size());
            while (menuIterator.hasPrevious())
            {
                Object menu=menuIterator.previous();
        %>
                <tiles:insertAttribute value="<%=menu%>" flush="true" />
        <%
            }
        %>
    </media:sidebar>
    <media:content>                               
        <tiles:importAttribute name="content" toName="contentJSP" ignore="true"/><%
        Object jspPage=pageContext.getAttribute("contentJSP");
        if (jspPage==null || application.getResource(jspPage.toString())==null) {%><ul class="errorList"><li>Page not found: <%=jspPage%></li></ul><%}
        else {%><tiles:insertAttribute name="content"/><%}
    %></media:content>
</media:body>

</body>
</html>
