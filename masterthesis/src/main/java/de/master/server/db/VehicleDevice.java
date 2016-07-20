package de.master.server.db;

import com.vividsolutions.jts.geom.Point;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author uucly on 25.02.2016.
 */

@Entity
public class VehicleDevice {

    @Id
    @GenericGenerator(name="increment" , strategy="increment")
    @GeneratedValue(generator="increment", strategy=GenerationType.IDENTITY)
    private Long id;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point currentPosition;

    private Integer status;
    
    @OneToOne
	@JoinColumn(name = "order_id")
    private VehicleOrder order;
    
    public VehicleDevice() {
    }

    public VehicleDevice(Point currentPosition) {
        this.currentPosition = currentPosition;
    }
	
	public Long getDeviceID(){
		return this.id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public VehicleOrder getOrder() {
		return order;
	}

	public void setOrder(VehicleOrder order) {
		this.order = order;
	}

}
