-- Function: public.res_calculate(integer, point, point)

-- DROP FUNCTION public.res_calculate(integer, point, point);

CREATE OR REPLACE FUNCTION public.res_calculate(
    auftragid integer,
    sourceneworder point,
    targetneworder point)
  RETURNS integer AS
$BODY$

DECLARE

vehicleID int[];
choosenVehicleID int;
currentVehiclePosition Point;
currentOrderSource Point;
currentOrderTarget Point;
currentOrderCosts double precision;

currentStatus int;
sourceCosts double precision;
currentTargetCosts double precision;
waitCosts double precision;
total_costs double precision;

minCosts double precision;
minCostsVehicleID int;
minSourceCosts double precision;
minWaitCosts double precision;
minOrderStatus int;

orderStatus int;

order_costs double precision;

i int;

currentDeviceOrder int;

choosenVehiclePosition Point;
lastOrderTarget Point;
rec record;

BEGIN

	update vehicleorder set targetCosts = 0 where id = auftragid;
	update vehicleorder set sourceCosts = 0 where id = auftragid;
	update vehicleorder set ordercosts = 0 where id = auftragid;
	update vehicleorder set totalcosts = 0 where id = auftragid;
	update vehicleorder set waitcosts = 0 where id = auftragid;
			
	minCosts = 99999999;
	vehicleID = array(SELECT id FROM vehicledevice);

	FOR i IN SELECT generate_subscripts(vehicleID, 1) LOOP
	
		SELECT currentposition::point FROM vehicledevice INTO currentVehiclePosition WHERE id = vehicleID[i];

		select order_id from vehicledevice into currentDeviceOrder where id = vehicleID[i];
		
		IF currentDeviceOrder IS NULL THEN --vehicle ist frei und kann direkt zu source fahren

			sourceCosts = costsFromAtoB(currentVehiclePosition,sourceneworder);
			waitcosts = sourceCosts;
			orderStatus = 1;
		ELSE --vehicle ist nicht frei sonder hat bereits auftrag
			select * from handleBusyVehiclenew(vehicleID[i],auftragID,  sourceneworder ,targetneworder) into waitCosts, sourceCosts;
			orderStatus = 3;
			waitCosts = (waitCosts + sourceCosts);
			
		END IF;

		--costs = waitcosts + sourceCosts;
		
		IF waitCosts < minCosts THEN
			minCosts = waitCosts;
			minSourceCosts = sourceCosts;
			minwaitcosts = waitcosts;
			minCostsVehicleID = vehicleID[i];
			minOrderStatus = orderStatus;
		END IF;
	END LOOP;

	currentTargetCosts = costsFromAtoB(sourceneworder,targetneworder);

	
	IF (minOrderStatus = 1) THEN
		select currentposition::point from vehicleDevice where id = minCostsVehicleID into choosenVehiclePosition;
		perform(createRouteTable('sourceRoute', minCostsVehicleID, auftragid, choosenVehiclePosition, sourceneworder));
		update vehicledevice set order_id = auftragid where id = minCostsVehicleID;
	ELSEIF minOrderStatus = 3 THEN
		select target::point from vehicleorder where id = getLastOrderID(minCostsVehicleID, auftragid) into lastOrderTarget;
		perform(createRouteTable('sourceRoute', minCostsVehicleID, auftragid, lastOrderTarget, sourceneworder));
	END IF;
	
	perform(createRouteTable('targetRoute',minCostsVehicleID, auftragid, sourceneworder, targetneworder));

	update vehicleorder set status = minOrderStatus where id = auftragid;
	
	update vehicleorder set targetCosts = currentTargetCosts where id = auftragid;
	update vehicleorder set sourceCosts = minSourceCosts where id = auftragid;
	update vehicleorder set ordercosts = currentTargetCosts + minSourceCosts where id = auftragid;
	update vehicleorder set totalcosts = minWaitCosts + currentTargetCosts where id = auftragid;
	update vehicleorder set waitcosts = minWaitCosts where id = auftragid;
		
	update vehicleorder set device_id = minCostsVehicleID where id = auftragid;
	
	
	RETURN minCostsVehicleID;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.res_calculate(integer, point, point)
  OWNER TO postgres;
