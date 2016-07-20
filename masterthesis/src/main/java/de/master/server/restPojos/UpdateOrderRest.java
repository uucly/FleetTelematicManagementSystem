package de.master.server.restPojos;

public class UpdateOrderRest {
	
	private final Integer orderID;

	public UpdateOrderRest(Integer orderID) {
		this.orderID = orderID;
	}

	public Integer getOrderID() {
		return orderID;
	}
	
	
}
