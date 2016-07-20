-- Function: public.loadcostsoforder(integer, integer)

-- DROP FUNCTION public.loadcostsoforder(integer, integer);

CREATE OR REPLACE FUNCTION public.loadcostsoforder(
    IN auftragid integer,
    IN vehicleid integer,
    OUT oldcosts double precision,
    OUT waitcosts double precision)
  RETURNS SETOF record AS
$BODY$

DECLARE

currentVehiclePosition Point;
currentOrderSource Point;
currentOrderTarget Point;
currentOrderCosts double precision;
lastOrderTarget Point;
tempDeviceId int;

currentOrderStatus int;

BEGIN

	-- Hole aktuelle vehicle position
	SELECT currentposition::point FROM vehicledevice INTO currentVehiclePosition WHERE device_id = vehicleID;
		
	currentOrderStatus = getInProgress_fahrzeug(vehicleID, 1);
	------------------------------------ Altkosten------------------------------
	--Angefangene Aufträge: gerade auf weg zu source
	IF currentOrderStatus IS NOT NULL THEN

		SELECT source::point FROM vehicleorder INTO currentOrderSource WHERE vehicleorder.device_id = vehicleID AND in_progress = 1;
		SELECT target::point FROM vehicleorder INTO currentOrderTarget WHERE vehicleorder.device_id = vehicleID AND in_progress = 1;
		SELECT orderCosts FROM vehicleorder INTO currentOrderCosts WHERE vehicleorder.device_id = vehicleID AND in_progress = 1;

		oldCosts := costsFromAtoB(currentVehiclePosition,currentOrderSource) + currentOrderCosts;
	ELSE
		--Aufträge in Transport
		currentOrderStatus = getInProgress_fahrzeug(vehicleID, 2);
		SELECT target::point FROM vehicleorder INTO currentOrderTarget WHERE vehicleorder.device_id = vehicleID AND In_Progress = 2;
	
		oldCosts := costsFromAtoB(currentVehiclePosition,currentOrderTarget);
	END IF;

	----------------------Wartekosten----------------------------------
		
	--Aufträge in Warteschlange
	select sum(dump.waitcosts) from
	(
		(
		select id,in_progress, vehicleorder.waitcosts from vehicleorder 
		where in_progress = 3 
		and device_id=vehicleID 
		and (select date from vehicleorder where id = auftragID) > date
		) order by date
	) 
	as dump INTO waitCosts;
	
	IF waitCosts IS NULL THEN

		waitCosts := 0;

	END IF;

	return next;
	return;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.loadcostsoforder(integer, integer)
  OWNER TO postgres;
