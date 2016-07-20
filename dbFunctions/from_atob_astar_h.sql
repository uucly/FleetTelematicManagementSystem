-- Function: public.pgr_fromatob_astar_h(character varying, double precision, double precision, double precision, double precision)

-- DROP FUNCTION public.pgr_fromatob_astar_h(character varying, double precision, double precision, double precision, double precision);

CREATE OR REPLACE FUNCTION public.pgr_fromatob_astar_h(
    IN tbl character varying,
    IN x1 double precision,
    IN y1 double precision,
    IN x2 double precision,
    IN y2 double precision,
    OUT seq integer,
    OUT gid integer,
    OUT name text,
    OUT km integer,
    OUT time_h double precision,
    OUT geom geometry)
  RETURNS SETOF record AS
$BODY$
DECLARE
        sql     text;
        rec     record;
        source	integer;
        target	integer;
        point	integer;
        km	integer;
        
BEGIN

	execute 'select source from ' || tbl|| ' order by st_distance(st_setsrid(st_makepoint(x1,y1), 4326), 
	st_setsrid(st_makepoint(' || x1 || ' , ' || y1 || '), 4326)) limit 1' into rec;

	source := rec.source;
	
	execute 'select target from ' || tbl|| ' order by st_distance(st_setsrid(st_makepoint(x2,y2), 4326), 
	st_setsrid(st_makepoint(' || x2 || ' , ' || y2 || '), 4326)) limit 1' into rec;

	target := rec.target;

        seq := 0;

	sql := 'SELECT seq as id, id1 AS node, id2 AS edge, b.osm_name, b.cost as time_h, b.km, b.geom_way
		FROM pgr_astar(
				''SELECT id,
				source::integer,
				target::integer,
				km*1/kmh::double precision AS cost,
				reverse_cost::double precision AS reverse_cost,
				x1,
				y1,
				x2,
				y2
				FROM ' || quote_ident(tbl) || ''','
				|| source || ',' || target || ', true, true)
				a LEFT JOIN ' || quote_ident(tbl) || ' b ON (a.id2 = b.id)';


        
        FOR rec IN EXECUTE sql
        LOOP
		
		-- Return record
                seq     := seq + 1;
                gid     := rec.id;
                name    := rec.osm_name;
                time_h  := rec.time_h;
                km      := rec.km;
                geom    := rec.geom_way;
                RETURN NEXT;
        END LOOP;
        RETURN;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100
  ROWS 1000;
ALTER FUNCTION public.pgr_fromatob_astar_h(character varying, double precision, double precision, double precision, double precision)
  OWNER TO postgres;
