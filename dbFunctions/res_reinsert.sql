-- Function: public.res_reinsert(integer)

-- DROP FUNCTION public.res_reinsert(integer);

CREATE OR REPLACE FUNCTION public.res_reinsert(orderid integer)
  RETURNS integer AS
$BODY$

DECLARE

deviceID int;
orderIDsToReinsert int[];

currentOrder vehicleorder;

ordersOfCurrentVehicle int;

i int;

BEGIN

	select vehicleorder.device_id from vehicleorder where vehicleorder.id = orderid into deviceID;
	orderIDsToReinsert = array(select vehicleorder.id from vehicleorder where vehicleorder.device_id = deviceID and (select vehicleorder.date from vehicleorder where vehicleorder.id = orderid and vehicleorder.device_id = deviceID) <= vehicleorder.date order by vehicleorder.date);

	FOR i IN SELECT generate_subscripts(orderIDsToReinsert, 1) LOOP
		select * from vehicleorder where vehicleorder.id = orderIDsToReinsert[i] into currentOrder;

		IF currentOrder.id = (select order_id from vehicleDevice where vehicleDevice.id = deviceID) THEN
			update vehicledevice set status = 4 where vehicledevice.id = deviceID;
			update vehicledevice set order_id = null where vehicledevice.id = deviceID;
		END IF;
		delete from vehicleorder where vehicleorder.id = orderIDsToReinsert[i];

		perform(res_insert(currentOrder.id, currentOrder.source::point, currentOrder.target::point));  
		
	END LOOP;

	return 1;
	--RETURN QUERY SELECT 	vehicleorder.id, vehicleorder.date, st_astext(vehicleorder.source), st_astext(vehicleorder.target), vehicleorder.device_id, vehicleorder.status, vehicleorder.sourceCosts, vehicleorder.waitcosts, vehicleorder.targetCosts, vehicleorder.ordercosts, vehicleorder.totalcosts
	--			FROM vehicleorder WHERE vehicleorder.id IN (orderID);
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.res_reinsert(integer)
  OWNER TO postgres;
