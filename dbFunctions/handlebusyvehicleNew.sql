-- Function: public.handlebusyvehiclenew(integer, integer, point, point)

-- DROP FUNCTION public.handlebusyvehiclenew(integer, integer, point, point);

CREATE OR REPLACE FUNCTION public.handlebusyvehiclenew(
    IN vehicleid integer,
    IN auftragid integer,
    IN sourceneworder point,
    IN targetneworder point,
    OUT waitcosts double precision,
    OUT sourcecosts double precision)
  RETURNS SETOF record AS
$BODY$

DECLARE

lastOrderTarget Point;
lastOrderID int;

BEGIN
	
	waitcosts = 0;
	sourceCosts = 0;

	--select * from loadCostsOfOrder(auftragID, vehicleID) into oldcosts, waitcosts;
	waitCosts := loadWaitCosts(auftragid, vehicleid);
	---------------------------Neukosten---------------------------------

	lastOrderID = getLastOrderID(vehicleid,auftragid);

	IF lastOrderID IS NULL THEN
		SELECT target::point FROM vehicleorder INTO lastOrderTarget WHERE vehicleorder.device_id = vehicleid AND status IN (2,1);
	ELSE
		SELECT target::point FROM vehicleorder INTO lastOrderTarget WHERE vehicleorder.id = lastOrderID;
	END IF;

	sourceCosts := costsFromAtoB(lastOrderTarget,sourceneworder);
	
	RETURN NEXT;
	RETURN;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.handlebusyvehiclenew(integer, integer, point, point)
  OWNER TO postgres;
