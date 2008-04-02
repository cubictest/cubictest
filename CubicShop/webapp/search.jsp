<%@ include file="includes/header.jsp" %>


<h2>Search</h2>
<br>
<b>Enter a cubic item (e.g. "moving box").</b><br><br>

<form name="searchForm" action="search.jsp">
	<table>
		<tr>
			<td>	
				<label for="search">Search</label>
			</td>
			<td>
				<select name="search" id="search" onchange="updateForm(this);">
					<option value="reviews" selected>
						Reviews
					</option>
					<option value="cubics">Cubics</option>
				</select>
			</td>
		</tr>
		<tr>		 	
			<td id="customCriteriaLabel">
			</td>
			<td id="customCriteria">
			</td>
		</tr>
		<tr>		 	
			<td>
				For
			</td>
			<td>
		 		<input type="text" name="query" id="query" />
			 	<input type="submit" name="go" value="Go" />
			</td>
		</tr>
	</table>
</form>

<% 
	if (request.getParameter("query") != null) {
%>
		<h2>Search result for <%= request.getParameter("search") %></h2><br>

		<%@ include file="includes/standardResults.jsp" %>
		
<%
	}
%>
		
<br>
<%@ include file="includes/footer.jsp" %>

