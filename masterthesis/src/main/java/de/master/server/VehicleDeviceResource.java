package de.master.server;

import java.util.Optional;

import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import de.master.server.db.DBHandler;
import de.master.server.restPojos.UpdateOrderRest;
import de.master.server.restPojos.UpdateTransportStatusRest;
import de.master.server.restPojos.VehicleDeviceRest;
import rest.VehicleOrderRest;

public class VehicleDeviceResource extends AbstractRestResource<JsonWebSerialDeserial> {

	private static final long serialVersionUID = 1L;

	public VehicleDeviceResource() {
		super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()));
	}

	@MethodMapping(value = "/updateOrder", httpMethod = HttpMethod.PUT)
	public Boolean updateOrder(@RequestBody UpdateOrderRest order) {
		String query = "select updateNew(" + order.getOrderID() + ")";
		new DBHandler().nativeQuery(query);
		return true;
	}

	@MethodMapping(value = "/status", httpMethod = HttpMethod.PUT)
	public Boolean updateOrderStatus(@RequestBody UpdateTransportStatusRest statusPojo) {
		String sqlQuery = "update VehicleOrder set status=" + statusPojo.getStatus().ordinal() + " where id=" + statusPojo.getId();
		new DBHandler().nativeQueryUpdate(sqlQuery);
		return true;
	}

	
	@MethodMapping(value = "/nextVehicleOrder/{vehicleDeviceId}")
	public VehicleOrderRest getNewVehicleOrder(int vehicleDeviceId) {

		finishCurrentOrder(vehicleDeviceId);
		int nextOrderId = updateAndChooseNewOrder(vehicleDeviceId);
		return new DBHandler().getVehicleOrder(nextOrderId);
	}
	
	@MethodMapping(value = "/getOrderInformation/{orderIndex}")
	public VehicleOrderRest getOrderInformation(final int index) {
		new DBHandler().nativeQuery("select updateNew(" + index + ")");
		return new DBHandler().getVehicleOrder(index);
	}
	
	@MethodMapping(value = "/getCurrentOrder/{vehicleIndex}")
	public VehicleOrderRest getCurrentOrder(final int vehicleIndex) {
		return new DBHandler().getVehicleOrder("select e from VehicleOrder e where status in (1,2) and device_id=" + vehicleIndex);
	}
	
	@MethodMapping(value = "/reinsert", httpMethod = HttpMethod.PUT)
	public Boolean reinsertOrders(@RequestBody VehicleDeviceRest device) {
		Optional<Integer> nextOrderId = loadOrderIdToReinsert(device.getId());
		
		if(nextOrderId.isPresent()){
			new DBHandler().nativeQuery("select res_reinsert(" + nextOrderId.get() + ");");
			return true;
		} 
		return false;
	}

	
	
	

	/* methods */
	private int updateAndChooseNewOrder(int vehicleDeviceId) {
		DBHandler dbHandler = new DBHandler();
		String filterNextOrderID = "select id from VehicleOrder where device_id=" + vehicleDeviceId + " and status=3 order by date asc";
		
		int nextOrderID = dbHandler.loadNextOrderID(filterNextOrderID);
		
		if(nextOrderID != -1){
			dbHandler.nativeQueryUpdate("update VehicleOrder set status=1 where id=" + nextOrderID);
			dbHandler.nativeQuery("select updateNew(" + nextOrderID + ")");
			return nextOrderID;
		} else {
			return -1;
		}
	}

	/**
	 * TODO: speichere aktuelle order id in tabelle vehicle device und suche
	 * nach der order id und nicht nach in progress=2
	 * 
	 * @param vehicleDeviceId
	 */
	private void finishCurrentOrder(int vehicleDeviceId) {
		String sqlQuery = "update VehicleOrder set status=4 where device_id=" + vehicleDeviceId
				+ " and status IN (1,2)";
		System.out.println(sqlQuery);
		new DBHandler().nativeQueryUpdate(sqlQuery);
	}
	
	
	private Optional<Integer> loadOrderIdToReinsert(int vehicleIndex) {
		Integer status = new DBHandler().loadNextOrderID("Select status from VehicleDevice where id=" + vehicleIndex);
		VehicleOrderRest orderIDToReinsert = new DBHandler().getVehicleOrder("Select e.order from VehicleDevice e where e.id=" + vehicleIndex);
		if(orderIDToReinsert == null){
			return Optional.empty();
		}
		
		if(status == 2){
			String getNextOrderQuery = "Select date from VehicleOrder where id=" + orderIDToReinsert.getOrderID();
			String query = "Select id from VehicleOrder "
					+ "where device_id=" + vehicleIndex 
					+ " and date>(" + getNextOrderQuery + ")";
			return Optional.of(new DBHandler().loadNextOrderID(query));
		} else {
			return Optional.of(orderIDToReinsert.getOrderID());
		}
	}

	
}
