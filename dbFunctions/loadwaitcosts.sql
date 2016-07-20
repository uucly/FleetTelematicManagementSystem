-- Function: public.loadwaitcosts(integer, integer)

-- DROP FUNCTION public.loadwaitcosts(integer, integer);

CREATE OR REPLACE FUNCTION public.loadwaitcosts(
    auftragid integer,
    vehicleid integer)
  RETURNS double precision AS
$BODY$

DECLARE

currentVehiclePosition Point;
currentOrderSource Point;
currentOrderTarget Point;
currentTargetCosts double precision;
lastOrderTarget Point;

orderCostsOfCurrentOrder double precision;
orderCostsOfWaitingOrder double precision;

currentOrderStatus int;

BEGIN

	-- Hole aktuelle vehicle position
	SELECT currentposition::point FROM vehicledevice INTO currentVehiclePosition WHERE id = vehicleID;
		
	--currentOrderStatus = getInProgress_fahrzeug(vehicleID, 1);
	SELECT status FROM vehicledevice WHERE id = vehicleID INTO currentOrderStatus;
	------------------------------------ Altkosten------------------------------
	--Angefangene Aufträge: gerade auf weg zu source
	IF currentOrderStatus = 1 OR (select order_id from vehicledevice where id = vehicleID) IS NOT NULL THEN

		SELECT source::point FROM vehicleorder INTO currentOrderSource WHERE vehicleorder.device_id = vehicleID AND status = 1;
		SELECT targetCosts FROM vehicleorder INTO currentTargetCosts WHERE vehicleorder.device_id = vehicleID AND status = 1;

		orderCostsOfCurrentOrder = costsFromAtoB(currentVehiclePosition,currentOrderSource) + currentTargetCosts;
	ELSEIF currentOrderStatus = 2 THEN
		--Aufträge in Transport
		SELECT target::point FROM vehicleorder INTO currentOrderTarget WHERE vehicleorder.device_id = vehicleID AND status = 2;
	
		orderCostsOfCurrentOrder = costsFromAtoB(currentVehiclePosition,currentOrderTarget);
	END IF;

	----------------------Wartekosten----------------------------------
		
	--Aufträge in Warteschlange
	select sum(dump.orderCosts) from
	(
		(
		select id,status, vehicleorder.orderCosts from vehicleorder
		where status = 3 
		and device_id=vehicleID 
		and (select date from vehicleorder where id = auftragID) > date
		) order by date
	) 
	as dump INTO orderCostsOfWaitingOrder;
	
	IF orderCostsOfWaitingOrder IS NULL THEN

		orderCostsOfWaitingOrder = 0;

	END IF;

	return orderCostsOfCurrentOrder + orderCostsOfWaitingOrder;
	
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.loadwaitcosts(integer, integer)
  OWNER TO postgres;
