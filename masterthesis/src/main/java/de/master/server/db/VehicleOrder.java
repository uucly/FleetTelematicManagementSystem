package de.master.server.db;

import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.function.Function;

/**
 * @author uucly on 24.02.2016.
 */

@Entity
public class VehicleOrder {

	@Id
	@GenericGenerator(name = "increment", strategy = "increment")
	@GeneratedValue(generator = "increment", strategy = GenerationType.IDENTITY)
	private Integer id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point source;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point target;

	private Integer status;

	private Double sourceCosts;

	private Double waitCosts;

	private Double targetCosts;

	private Double orderCosts;

	private Double totalCosts;

	private Date date;

	@ManyToOne
	@JoinColumn(name = "device_id")
	private VehicleDevice device;

	public VehicleOrder() {
	}

	public VehicleOrder(Point source, Point target) {
		this.source = source;
		this.target = target;
		this.date = new Date();
	}

	public VehicleOrder(Point source, Point target, VehicleDevice device) {
		this.source = source;
		this.target = target;
		this.device = device;
		this.date = new Date();
	}

	public Point getSource() {
		return source;
	}

	public Point getTarget() {
		return target;
	}

	public Date getDate() {
		return date;
	}

	public VehicleDevice getDevice() {
		return device;
	}

	public void setDevice(VehicleDevice device) {
		this.device = device;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public double getSourceCosts() {
		return sourceCosts;
	}

	public void setSourceCosts(double sourceCosts) {
		this.sourceCosts = sourceCosts;
	}

	public double getTargetCosts() {
		return targetCosts;
	}

	public void setTargetCosts(double targetCosts) {
		this.targetCosts = targetCosts;
	}

	public double getWaitCosts() {
		return waitCosts;
	}

	public void setWaitCosts(double waitCosts) {
		this.waitCosts = waitCosts;
	}

	public double getOrderCosts() {
		return orderCosts;
	}

	public void setOrderCosts(double orderCosts) {
		this.orderCosts = orderCosts;
	}

	public double getTotalCosts() {
		return totalCosts;
	}

	public void setTotalCosts(double totalCosts) {
		this.totalCosts = totalCosts;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "VehicleOrder [id=" + id + ", source=" + source + ", target=" + target + ", status="
				+ status + ", sourceCosts=" + sourceCosts + ", waitCosts=" + waitCosts + ", targetCosts=" + targetCosts
				+ ", orderCosts=" + orderCosts + ", totalCosts=" + totalCosts + ", date=" + date + ", device=" + device
				+ "]";
	}

}
