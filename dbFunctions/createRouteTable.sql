-- Function: public.createroutetable(character varying, integer, integer, point, point)

-- DROP FUNCTION public.createroutetable(character varying, integer, integer, point, point);

CREATE OR REPLACE FUNCTION public.createroutetable(
    name character varying,
    vehicleid integer,
    orderid integer,
    source point,
    target point)
  RETURNS integer AS
$BODY$

DECLARE

BEGIN
	execute 'drop table if exists ' || name ||'_' || orderID; 
	execute 'create table '|| name || '_' || orderID || ' as SELECT * from pgr_fromAtoB_astar_h(''osm_2po_4pgr'',' || st_x(source::geometry) || ',' || st_y(	  source::geometry ) || ',' ||  st_x(target::geometry ) || ',' || st_y(target::geometry ) || ')';

	return 0;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.createroutetable(character varying, integer, integer, point, point)
  OWNER TO postgres;
