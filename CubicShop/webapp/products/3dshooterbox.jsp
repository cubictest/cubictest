<%@ include file="../includes/header.jsp" %>

<h2>3D Shooter Box</h2>

<br/>

<form name="cartForm" action="../cart.jsp">
	<input type="hidden" name="cubicTitle" value="">
	
<p>
	<center>
		Price: $29, Weight: 5000 lbs
		<br><br>
	</center>
</p>
<p>
	<center>
		<img src="../images/3dshootercrate.jpg"/>
	</center>
</p>
<br>
	<a href="../webshop.jsp">Back</a> &nbsp; &nbsp; &nbsp; &nbsp;
	<input type="submit" 
		name="buy"
		value="Buy"
		onClick="document.cartForm.cubicTitle.value='3D Shooter Box';"
		onMouseOver="showBuyTooltip();" onMouseOut="hideBuyTooltip();">

<div id="buyTooltip" style="visibility: hidden; display: none;">

</div>

</form>


<%@ include file="../includes/footer.jsp" %>

