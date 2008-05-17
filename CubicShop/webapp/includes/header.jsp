<%@ page import="java.util.*" %>
<% Thread.sleep(700); %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
 
<html>
	<head>
		<title>CubicShop</title>
		<style type="text/css" media="all">
			@import "/cubicshop/cubicshop.css";
		</style>
		<script type="text/javascript" src="/cubicshop/js/cubicshop.js"></script>
	</head>
	<body>
 	 <div id="frontpage">
	  <div id="generalpos" style="background-color: #FFFFFF; width: 700px;"><div id="autat-bottom-margin"><div class="wrap1"><div class="wrap2"><div class="wrap3"><div id="generalstyle">
		<div id="boss">
		 <div id="autat-page-container">
		  <center>
		   <div id="centerWhite">				
			<div style="background-color: #FFFFFF; width: 664px; padding-top: 50px; padding-bottom: 10px;">
				<table width="70%">
				 <tr>
				  <td align="center">
				  	<a href="index.jsp" id="logoLink"><img id="homeLogo" alt="To Front Page" width="50" src="/cubicshop/images/cubicshop.gif" border="0" style="border: 0px ! important;"/></a>
				  </td>
				  <td align="center">
					<div id="header" onClick="document.getElementById('infoBox').innerHTML='We sell cubes';"><h1>CubicShop</h1></div>
				  </td>
				  <td align="center">
				  	<img id="logo" alt="Logo" onDblclick="document.getElementById('username').value='Test';document.loginForm.submit();" width="50" src="/cubicshop/images/cubicshop.gif" border="0" style="border: 0px ! important;"/>
				  </td>
				 </tr>
				</table>

<%
				if (request.getParameter("username") != null) {
				   session.setAttribute("loggedIn", request.getParameter("username"));
				}
%>
				<table width="460" bgcolor="#EEEEEE" style="margin-top: 20px; margin-bottom: 40px;" id="headerLinks">
					<tr>
						<td>
							<a href="/cubicshop/index.jsp">Welcome</a>
						</td>
						<td>
							<a href="/cubicshop/webshop.jsp">Webshop</a>
						</td>
						<td>
							<a href="/cubicshop/search.jsp">Search</a>
						</td>
						<td>
							<a href="/cubicshop/cart.jsp">Your cart</a>
						</td>
						<td>
							<a href="/cubicshop/about.jsp">About us</a>
						</td>
					</tr>
				</table>
				
				
<%!
/**
 * Converts a text string to camelCase notation with initial letter set to small.
 * See also the JUnit test case.
 * 
 * @param name The string to convert
 * @return The camelCase version.
 */
public String camel(String name) {

	StringBuffer b = new StringBuffer(name.length());
	StringTokenizer t = new StringTokenizer(name);
	if (!t.hasMoreTokens())
		return name;
	
	String firstToken = t.nextToken();

	if (!t.hasMoreTokens())
		return firstToken.substring(0, 1).toLowerCase() + firstToken.substring(1);

	b.append(firstToken.substring(0, 1).toLowerCase());
	b.append(firstToken.substring(1).toLowerCase());

	while (t.hasMoreTokens()) {
		String token = t.nextToken();
		b.append(token.substring(0, 1).toUpperCase());
		b.append(token.substring(1).toLowerCase());
	}
	return b.toString();
}
%>

<script type="text/javascript">
	function showBuyTooltip() {
		document.getElementById("buyTooltip").innerHTML = "Buy this item";
		document.getElementById("buyTooltip").style.visibility = "visible";
	 	document.getElementById("buyTooltip").style.display = "block";
	}
	function hideBuyTooltip() {
		document.getElementById("buyTooltip").innerHTML = "";
		document.getElementById("buyTooltip").style.visibility = "hidden";
	 	document.getElementById("buyTooltip").style.display = "none";
	}
</script>
