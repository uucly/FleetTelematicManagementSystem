package de.master.masterthesis.db_connection;

import com.vividsolutions.jts.geom.Point;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-17T15:55:47.081+0100")
@StaticMetamodel(VehicleOrder.class)
public class VehicleOrder_ {
	public static volatile SingularAttribute<VehicleOrder, Integer> id;
	public static volatile SingularAttribute<VehicleOrder, Point> source;
	public static volatile SingularAttribute<VehicleOrder, Point> target;
	public static volatile SingularAttribute<VehicleOrder, Double> cost;
	public static volatile SingularAttribute<VehicleOrder, Integer> in_progress;
	public static volatile SingularAttribute<VehicleOrder, Double> oldCosts;
	public static volatile SingularAttribute<VehicleOrder, Double> waitCosts;
	public static volatile SingularAttribute<VehicleOrder, Double> newCosts;
	public static volatile SingularAttribute<VehicleOrder, Double> orderCosts;
	public static volatile SingularAttribute<VehicleOrder, Double> totalCosts;
	public static volatile SingularAttribute<VehicleOrder, Date> date;
	public static volatile SingularAttribute<VehicleOrder, VehicleDevice> device;
}
