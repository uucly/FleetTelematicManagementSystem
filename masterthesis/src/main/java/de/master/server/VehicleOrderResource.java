package de.master.server;

import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import de.master.server.db.DBHandler;
import rest.VehicleOrderRest;

public class VehicleOrderResource extends AbstractRestResource<JsonWebSerialDeserial>{

	private static final long serialVersionUID = 1L;

	public VehicleOrderResource() {
		super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()));
	}

	@MethodMapping(value = "/order/{vehicleOrderIndex}")
	public VehicleOrderRest getVehicleOrder(int index) {
		new DBHandler().nativeQuery("select updateNew(" + index + ")");
		return new DBHandler().getVehicleOrder(index);
	}
	
	@MethodMapping(value = "/newVehicleDevice", httpMethod = HttpMethod.POST)
	public VehicleOrderRest getNewVehicle(@RequestBody VehicleOrderRest orderPojo) {
		String sourceText = orderPojo.getSource();
		String targetText = orderPojo.getTarget();
		
		String sqlQuery = "select res_insert(" + orderPojo.getOrderID() + "," + sourceText + "," + targetText + ")";
		new DBHandler().nativeQuery(sqlQuery);
		return new DBHandler().getVehicleOrder(orderPojo.getOrderID());
	}
}
