<%@ include file="includes/header.jsp" %>

<h2>About us</h2>
<br>
We sell cubics. <a href="http://www.wikipedia.org">Wikipedia >>></a>
<br><br><br>
<form>
	Was this info helpful?
	<table>
		<tr>
			<td>
				<input type="radio" name="helpfullness" id="helpful" value="Helpful"><label for="helpful">Helpful</label><br>
				<input type="radio" name="helpfullness" id="notHelpful" value="Not helpful"><label for="notHelpful">Not helpful</label>
			</td>
		</tr>
	</table>
	<input type="submit" value="Submit">
</form>
<%
	if(request.getParameter("helpfullness") != null) {
		out.println("You voted: " + request.getParameter("helpfullness") + ".<br>");
		out.println("<b>Thank you for your feedback.</b>");
	}
%>
<br>
<%@ include file="includes/footer.jsp" %>
