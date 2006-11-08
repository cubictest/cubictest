<%@ include file="../includes/header.jsp" %>

<h2>Shipping crate</h2>

<br/>

<form name="cartForm" action="../cart.jsp">
	<input type="hidden" name="cubicTitle" value="">

<p>
	<center>
		<img src="../images/shippingcrate.jpg"/>
	</center>
	<br>

	<a href="../webshop.jsp">Back</a> &nbsp; &nbsp; &nbsp; &nbsp;
	<input type="submit" 
		name="buy"
		value="Buy"
		onClick="document.cartForm.cubicTitle.value='Shipping crate';"
		onMouseOver="showBuyTooltip();" onMouseOut="hideBuyTooltip();">

<div id="buyTooltip" style="visibility: hidden; display: none;">
		
</p>
</form>

<%@ include file="../includes/footer.jsp" %>
