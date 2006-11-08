<%@ include file="../includes/header.jsp" %>

<h2>Loudspeaker box</h2>

<br/>

<form name="cartForm" action="../cart.jsp">
	<input type="hidden" name="cubicTitle" value="">

<p>
	<center>
		<img src="../images/loudspeakerbox.jpg"/>
	</center>
</p>
<br>
	<a href="../webshop.jsp">Back</a> &nbsp; &nbsp; &nbsp; &nbsp;
	<input type="submit" 
		name="buy"
		value="Buy"
		onClick="document.cartForm.cubicTitle.value='Loudspeaker box';"
		onMouseOver="showBuyTooltip();" onMouseOut="hideBuyTooltip();">

<div id="buyTooltip" style="visibility: hidden; display: none;">
		

</form>
<%@ include file="../includes/footer.jsp" %>
