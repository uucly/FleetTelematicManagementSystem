package de.master.masterthesis.db_connection;

import com.vividsolutions.jts.geom.Point;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-17T15:55:47.077+0100")
@StaticMetamodel(VehicleDevice.class)
public class VehicleDevice_ {
	public static volatile SingularAttribute<VehicleDevice, Long> device_id;
	public static volatile SingularAttribute<VehicleDevice, Point> currentPosition;
	public static volatile SingularAttribute<VehicleDevice, Double> cost;
}
