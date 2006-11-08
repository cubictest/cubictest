<%@ include file="../includes/header.jsp" %>

<h2>Engine crate</h2>

<br/>
<form name="cartForm" action="../cart.jsp">
	<input type="hidden" name="cubicTitle" value="">

<p>
	<center>
		<img src="../images/enginecrate.jpg"/>
	</center>
</p>
<br>
	<a href="../webshop.jsp">Back</a> &nbsp; &nbsp; &nbsp; &nbsp;
	<input type="submit" 
		name="buy"
		value="Buy"
		onClick="document.cartForm.cubicTitle.value='Engine crate';"
		onMouseOver="showBuyTooltip();" onMouseOut="hideBuyTooltip();">

<div id="buyTooltip" style="visibility: hidden; display: none;">
		

</form>
<%@ include file="../includes/footer.jsp" %>
