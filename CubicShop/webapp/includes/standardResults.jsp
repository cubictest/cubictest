
	
<form name="cartForm" action="cart.jsp">
	<input type="hidden" name="cubicTitle" value="">
	
	<table border="1" width="380">
		<tr>
	   	<td>Name</td><td>Price</td><td align="right">Action</td>
		</tr>
		
		<tr>
			<td>
				<%= request.getParameter("query") %>
			</td>
			<td>
				$<%=  request.getParameter("query").length() %>
			</td>
			<td align="right">
				<input type="submit" 
					name="<%= "buy" + request.getParameter("query") %>"
					value="Buy"
					onClick="document.cartForm.cubicTitle.value='<%=request.getParameter("query") %>';">
			</td>
		</tr>
	</table>

</form>