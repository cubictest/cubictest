<%@ include file="includes/header.jsp" %>

<h2>Welcome to our webshop!</h2>
<br>


<%
	if (request.getParameter("logout") != null) {
	   session.invalidate();
	   out.print("<b>You are now logged out</b><br><br>");
	}
	else if (session.getAttribute("loggedIn") != null) {
		if (session.getAttribute("loggedIn") != null && request.getParameter("logout") == null) {
		   out.print("<br><b>Welcome " + session.getAttribute("loggedIn") + "! ");
		}

	   out.print("You are now logged in.</b><br><br>");

%>
		<br>Change username?
		
<%
	}
	else {
%>
		Log in:
<%
	}
%>
	
	<form name="loginForm">
		<table>
			<tr>
				<td>
					<label for="username">Username</label>
				</td>
				<td>
					<input type="text" name="username" id="username">
				</td>
			</tr>
			<tr>
				<td>
					<label for="password">Password</label>
				</td>
				<td>
					<input type="password" name="password" id="password">
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<input type="submit" name="logIn" id="logIn" value="Log in">
					<input type="button" name="logOut" onClick="window.location='index.jsp?logout=true';" value="Log out">
				</td>
			</tr>
		</table>
	</form>

<%@ include file="includes/footer.jsp" %>
