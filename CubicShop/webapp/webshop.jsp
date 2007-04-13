<%@ include file="includes/header.jsp" %>

<h2>Webshop</h2>
<br>
<form name="cartForm" action="cart.jsp">
	<input type="hidden" name="cubicTitle" value="">
	
	<h4>Large boxes</h4>
	<table border="1" width="460" id="webshopTable">
		<tr>
	   	<td>Name</td><td>Price</td><td>Action</td>
		</tr>

		<tr>
			<td>
				<a href="products/shippingcrate.jsp">Shipping crate</a>
			</td>
			<td>
				$2500
			</td>
			<td>
				<input type="submit" 
					name="buyShippingCrate"
					value="Buy"
					onClick="document.cartForm.cubicTitle.value='Shipping crate';">
			</td>
		</tr>
		<tr>
			<td>
				<a href="products/enginecrate.jsp">Engine crate</a>
			</td>
			<td>
				$25
			</td>
			<td>
				<input type="submit" 
					name="buyEngineCrate"
					value="Buy"
					onClick="document.cartForm.cubicTitle.value='Engine crate';">
			</td>
		</tr>

	</table>
	
	
	<br/>
	<h4>Small boxes</h4>
	
	<table border="1" width="460" id="webshopTable">
		<tr>
	   	<td>Name</td><td>Price</td><td>Action</td>
		</tr>

		<tr>
			<td>
				<a href="products/loudspeakerbox.jsp">Loudspeaker box</a>
			</td>
			<td>
				$120
			</td>
			<td>
				<input type="submit" 
					name="buyLoudspeakerBox"
					value="Buy"
					onClick="document.cartForm.cubicTitle.value='Loudspeaker box';">
			</td>
		</tr>
		<tr>
			<td>
				<a href="products/3dshooterbox.jsp">3D Shooter Box</a>
			</td>
			<td>
				$29
			</td>
			<td>
				<input type="submit" 
					name="buy3dShooterBox"
					value="Buy"
					onClick="document.cartForm.cubicTitle.value='3D Shooter Box';">
			</td>
		</tr>

	</table>

</form>

<%@ include file="includes/footer.jsp" %>
