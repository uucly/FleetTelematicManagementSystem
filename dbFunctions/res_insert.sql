-- Function: public.res_insert(integer, point, point)

-- DROP FUNCTION public.res_insert(integer, point, point);

CREATE OR REPLACE FUNCTION public.res_insert(
    orderid integer,
    sourceneworder point,
    targetneworder point)
  RETURNS integer AS
$BODY$

DECLARE


BEGIN

	insert into vehicleorder (id,date,source,target) values (orderID, now(), sourceneworder::geometry, targetneworder::geometry);

	perform(res_calculate(orderID, sourceneworder, targetneworder));
	RETURN 1;
	--RETURN QUERY SELECT 	vehicleorder.id, vehicleorder.date, st_astext(vehicleorder.source), st_astext(vehicleorder.target), vehicleorder.device_id, vehicleorder.status, vehicleorder.sourceCosts, vehicleorder.waitcosts, vehicleorder.targetCosts, vehicleorder.ordercosts, vehicleorder.totalcosts
				--FROM vehicleorder WHERE vehicleorder.id IN (orderID);
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.res_insert(integer, point, point)
  OWNER TO postgres;
