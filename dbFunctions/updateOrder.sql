-- Function: public.updateorder(integer)

-- DROP FUNCTION public.updateorder(integer);

CREATE OR REPLACE FUNCTION public.updateorder(auftragid integer)
  RETURNS integer AS
$BODY$

DECLARE

vehicleID int;
choosenVehicleID int;
currentVehiclePosition Point;
currentOrderSource Point;
currentOrderTarget Point;
currentOrderCosts double precision;
lastOrderTarget Point;
costs double precision;
reach double precision;

status int;

oldCosts double precision;
old_Costs double precision;

waitCosts double precision;

minCosts double precision;
minCostsVehicleID int;
minreach double precision;
minOldCosts double precision;
minWaitCosts double precision;

sourceOrder Point;
targetOrder Point;

order_costs double precision;
currentOrderStatus int;
i int;

rec record;

BEGIN

	select device_id from vehicleorder where id = auftragID into vehicleID;

	select source::point from vehicleorder where id=auftragID into sourceOrder;
	select target::point from vehicleorder where id=auftragID into targetOrder;
	
/*	update vehicleDevice set cost = 0 where vehicledevice.device_ID = vehicleID;
	update vehicleorder set cost = 0 where id = auftragID;
	update vehicleorder set oldcosts = 0 where id = auftragID;
	update vehicleorder set newCosts = 0 where id = auftragID;
	update vehicleorder set ordercosts = 0 where id = auftragID;
	update vehicleorder set totalcosts = 0 where id = auftragID;
	update vehicleorder set waitcosts = 0 where id = auftragID;
		
	update vehicleorder set device_id = vehicleID where id = auftragID;*/

	minreach = 0;
	minoldCosts = 0;
	minwaitcosts = 0;
			
	minCosts = 99999999;
	
	
	costs = 0;
		
	SELECT currentposition::point FROM vehicledevice INTO currentVehiclePosition WHERE device_id = vehicleID;

	select in_progress from vehicleorder into status where id = auftragID and in_progress IN (1,2);

	IF status = 1 THEN

		reach = costsFromAtoB(currentVehiclePosition,sourceOrder);
		oldCosts = 0;
		waitCosts = 0;
		currentOrderStatus = NULL;

		execute 'drop table if exists routeToSource_' || auftragid || '_' || vehicleID; 
		execute 'create table routeToSource_' || auftragid || '_' || vehicleID || ' as SELECT * from pgr_fromAtoB_astar_h(''osm_2po_4pgr'',' || st_x(currentVehiclePosition::geometry) || ',' || st_y(currentVehiclePosition::geometry ) || ',' 
		|| st_x(sourceOrder::geometry ) || ',' 
		|| st_y(sourceOrder::geometry ) || ')';
		--order_costs = costsFromAtoB(sourceOrder,targetOrder);
		select orderCosts from vehicleorder where id = auftragID into order_costs;
		
		costs = reach;
		update vehicleorder set orderCosts = order_costs where id = auftragID;
		update vehicleorder set totalcosts = costs + order_costs where id = auftragID;
		update vehicleorder set newCosts = reach where id = auftragID;
		update vehicleorder set cost = costs where id = auftragID;
		update vehicleorder set waitcosts = reach + order_costs where id = auftragID;
		
	ELSEIF status = 2 THEN

		reach = costsFromAtoB(currentVehiclePosition,targetOrder);
		oldCosts = 0;
		waitCosts = 0;
		currentOrderStatus = NULL;
		order_costs = 0;
		execute 'drop table if exists routeToSource_' || auftragid || '_' || vehicleID; 
		execute 'create table routeToSource_' || auftragid || '_' || vehicleID || ' as SELECT * from pgr_fromAtoB_astar_h(''osm_2po_4pgr'',' || st_x(currentVehiclePosition::geometry) || ',' || st_y(currentVehiclePosition::geometry ) || ',' 
		|| st_x(targetOrder::geometry ) || ',' 
		|| st_y(targetOrder::geometry ) || ')';

		costs = reach;
		update vehicleorder set totalcosts = costs where id = auftragID;
		update vehicleorder set newCosts = reach where id = auftragID;
		update vehicleorder set cost = costs where id = auftragID;
		update vehicleorder set waitcosts = reach where id = auftragID;
		
	ELSE
		select * from handleBusyVehicle(vehicleID,auftragID,sourceOrder,targetOrder) into oldCosts, waitCosts, reach;

		old_Costs = oldCosts;
		select orderCosts from vehicleorder where id = auftragID into order_costs;
		costs = old_Costs + waitcosts + reach;
		update vehicleDevice set cost = costs + order_costs where vehicledevice.device_ID = vehicleID;
		update vehicleorder set cost = costs where id = auftragID;
		update vehicleorder set oldcosts = old_Costs where id = auftragID;
		update vehicleorder set newCosts = reach where id = auftragID;
		update vehicleorder set ordercosts = order_costs where id = auftragID;
		update vehicleorder set totalcosts = costs + order_costs where id = auftragID;
		update vehicleorder set waitcosts = reach + order_costs where id = auftragID;	
	END IF;
	
/*	IF tempDeviceId IS NOT NULL THEN --vehicle ist frei und kann direkt zu source fahren

		reach = costsFromAtoB(currentVehiclePosition,sourceOrder);
		oldCosts = 0;
		waitcosts = 0;
		currentOrderStatus = NULL;

		execute 'drop table if exists routeToNext_' || auftragid || '_' || vehicleID; 
		execute 'create table routeToNext_' || auftragid || '_' || vehicleID || ' as SELECT * from pgr_fromAtoB_astar_h(''osm_2po_4pgr'',' || st_x(currentVehiclePosition::geometry) || ',' || st_y(currentVehiclePosition::geometry ) || ',' 
		|| st_x(sourceOrder::geometry ) || ',' 
		|| st_y(sourceOrder::geometry ) || ')';
	ELSE --vehicle ist nicht frei sonder hat bereits auftrag
		
		
			
	END IF;*/

	/*costs = oldCosts + waitcosts + reach;
		
			
	minCosts = costs;
	minreach = reach;
	minoldCosts = oldcosts;
	minwaitcosts = waitcosts;
			
	--order_costs = costsFromAtoB(sourceOrder,targetOrder);
	
	update vehicleDevice set cost = minCosts + order_costs where vehicledevice.device_ID = vehicleID;
	update vehicleorder set cost = minCosts where id = auftragID;
	update vehicleorder set oldcosts = minoldCosts where id = auftragID;
	update vehicleorder set newCosts = minreach where id = auftragID;
	update vehicleorder set ordercosts = order_costs where id = auftragID;
	update vehicleorder set totalcosts = mincosts + order_costs where id = auftragID;
	update vehicleorder set waitcosts = reach + order_costs where id = auftragID;	*/
	RETURN vehicleID;
	
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.updateorder(integer)
  OWNER TO postgres;
