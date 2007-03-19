<%@ include file="includes/header.jsp" %>

<h2>Your shopping cart</h2>
<br>
<%
	if (session.getAttribute("loggedIn") != null) {

	   if (session.getAttribute("cartItems") == null) {
			//initializing cart
		   session.setAttribute("cartItems", new ArrayList());
		}
		List cartItems = (List) session.getAttribute("cartItems");
		
		Enumeration names = request.getParameterNames();
		String[] remove = new String[100];
		int i = 0;
		
		while(names.hasMoreElements()) {
		   String name = (String)names.nextElement(); 
		   if (name.startsWith("remove")) {
		      remove[i] = name.substring(6,7).toLowerCase() + name.substring(7);
		   }
		   i++;
		}

		for(int j = 0; j < remove.length; j++) {
	      if (remove[j] == null || remove[j].equals("")) break;
	      for (Iterator iter = cartItems.iterator(); iter.hasNext();) {
	         if (camel((String)iter.next()).equals(remove[j])) {
	            iter.remove();
	         }
	      }
	      cartItems.remove(remove[j]);
	   }

		if (request.getParameter("cubicTitle") != null && !cartItems.contains(request.getParameter("cubicTitle"))) {
			cartItems.add(request.getParameter("cubicTitle"));
			out.print("Added to cart: " + request.getParameter("cubicTitle") + "<br><br>");
		}

%>

		<h3>Cart contents:</h3><br>

<%
		if (cartItems == null || cartItems.size() == 0) {
		   out.print("<div>None</div><br><br>");
		}
		else {
		   out.print("<form name=\"removeForm\"><table>");
		   out.print("<tr><td width=\"300\">&nbsp;</td><td>Remove?</td></tr>");
			for (Iterator items = cartItems.iterator(); items.hasNext();) {
			   String item = items.next().toString();
			   out.println("<tr><td width=\"300\">" + item + "</td><td><input type=\"checkbox\" name=\"" + camel("remove " + item) + "\" value=\"on\"></td></tr>");
			}		   
		   out.print("<tr><td align=\"center\" colspan=\"2\"><br><input type=\"submit\" name=\"removeSelected\" value=\"Remove selected\"></td></tr>");
		   out.print("</table></form>");
		}
	}
	else {
%>
		<b><font color="red">You must log in before you can purchase.</font></b><br><br><br>
<% } %>
<%@ include file="includes/footer.jsp" %>
