-- Function: public.costsfromatob(point, point)

-- DROP FUNCTION public.costsfromatob(point, point);

CREATE OR REPLACE FUNCTION public.costsfromatob(
    source point,
    target point)
  RETURNS double precision AS
$BODY$

DECLARE

resultCosts double precision;

BEGIN

	SELECT sum(time_h) FROM pgr_fromAtoB_astar_h('osm_2po_4pgr', st_x(source::geometry),st_y(source::geometry), st_x(target::geometry),st_y(target::geometry)) INTO resultCosts;

	IF resultCosts IS NULL THEN
		RETURN 0;
	ELSE
		RETURN resultCosts;
	END IF;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.costsfromatob(point, point)
  OWNER TO postgres;
