package de.master.server.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import rest.VehicleOrderRest;

public class DBHandler{

	
	public void nativeQuery(final String query){
		new DBHelper<Void>() {
			
			@Override
			public Void execute(EntityManager manager) {
				System.out.println(query);
				manager.createNativeQuery(query).getResultList();
				return null;
			}
		}.doSomething();
	}
	
	public void nativeQueryUpdate(final String query){
		new DBHelper<Void>() {
			
			@Override
			public Void execute(EntityManager manager) {
				System.out.println(query);
				manager.createNativeQuery(query).executeUpdate();
				return null;
			}
		}.doSomething();
	}
	
	
	public VehicleOrderRest getVehicleOrder(final Integer index){
		return new DBHelper<VehicleOrderRest>() {
			
			@Override
			public VehicleOrderRest execute(EntityManager manager) {
				return parse(manager.find(VehicleOrder.class, index));
			}
		}.doSomething();
	}
	
	public VehicleOrderRest getVehicleOrder(final String query){
		return new DBHelper<VehicleOrderRest>() {
			
			@Override
			public VehicleOrderRest execute(EntityManager manager) {
				System.out.println(query);
				return parse(manager.createQuery(query, VehicleOrder.class).getResultList());
			}
		}.doSomething();
	}
	
	public Integer loadNextOrderID(final String query) {
		return new DBHelper<Integer>() {
			
			@Override
			public Integer execute(EntityManager manager) {
				System.out.println(query);
				List<Integer> ids = manager.createQuery(query, Integer.class).setMaxResults(1).getResultList();
				return (ids != null && !ids.isEmpty()) ? ids.get(0) : -1;
			}
		}.doSomething();
	}
	
		
	/* methods */
	private static VehicleOrderRest parse(List<VehicleOrder> orders){
		if(orders == null || orders.isEmpty() ){
			return VehicleOrderRest.EMPTY_ORDER;
		}
		
		VehicleOrder order = orders.get(0);
		VehicleOrderRest pojo = init(order);
		
		return pojo;
	}
	
	private static VehicleOrderRest parse(VehicleOrder order){
		if(order == null){
			return VehicleOrderRest.EMPTY_ORDER;
		}
		
		VehicleOrderRest pojo = init(order);
		
		return pojo;
	}
	
	private static VehicleOrderRest init(VehicleOrder order){
		VehicleOrderRest pojo = new VehicleOrderRest(order.getSource().toText(), order.getSource().toText(), order.getId());
		pojo.setOrderCosts(order.getOrderCosts());
		pojo.setSourceCosts(order.getSourceCosts());
		pojo.setTargetCosts(order.getTargetCosts());
		pojo.setOrderCosts(order.getOrderCosts());
		pojo.setWaitCosts(order.getWaitCosts());
		pojo.setDate(order.getDate());
		pojo.setTotalCosts(order.getTotalCosts());
		pojo.setStatus(order.getStatus() == null ? -1 : order.getStatus());
		pojo.setDeviceID(order.getDevice() == null ? -1 : order.getDevice().getDeviceID());
		return pojo;
	}
	
	/* class */
	private static abstract class DBHelper<T>{
		
		public T doSomething(){
			EntityManager manager = JPAUtil.createEntityManager();
			EntityTransaction transaction = manager.getTransaction();
			transaction.begin();
			T t = execute(manager);
			transaction.commit();
			manager.close();
			return t;
		}
		
		public abstract T execute(EntityManager manager);
	}
	
}
