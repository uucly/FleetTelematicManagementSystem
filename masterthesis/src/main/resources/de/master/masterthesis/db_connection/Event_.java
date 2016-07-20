package de.master.masterthesis.db_connection;

import com.vividsolutions.jts.geom.Point;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-03-17T15:55:46.935+0100")
@StaticMetamodel(Event.class)
public class Event_ {
	public static volatile SingularAttribute<Event, Long> id;
	public static volatile SingularAttribute<Event, String> title;
	public static volatile SingularAttribute<Event, Date> date;
	public static volatile SingularAttribute<Event, Point> location;
}
