package masterthesis.master.de.vehicledeviceclient;

import android.location.Location;

/**
 * Created by uucly on 11.03.2016.
 */
public class WfsUpdateGmlGenerator {

    public static String generate(Location location, int vehicleID) {
        return "<wfs:Transaction service=\"WFS\" version=\"1.0.0\" xmlns=\"http://www.opengis.net/wfs\" xmlns:vehicleDevice=\"www.geoserver.org/vehicleDevice\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:gml=\"http://www.opengis.net/gml\">\n" +
                "\t<wfs:Update typeName=\"vehicleDevice:vehicledevice\">\n" +
                "\t\t<wfs:Property>\n" +
                "\t\t\t<vehicleDevice:Name>currentposition</vehicleDevice:Name>\n" +
                "\t\t\t<vehicleDevice:Value>\n" +
                "\t\t\t\t<gml:Point srsName='http://www.opengis.net/gml/srs/epsg.xml#4326'>\n" +
                "\t\t\t\t\t<gml:coordinates>\n" +
                location.getLongitude() + "," + location.getLatitude() + "\n" +
                "\t\t\t\t\t</gml:coordinates>\n" +
                "\t\t\t\t</gml:Point>\n" +
                "\t\t\t</vehicleDevice:Value>\n" +
                "\t\t</wfs:Property>\n" +
                "\t\t<wfs:Property>\n" +
                "\t\t\t<vehicleDevice:Name>id</vehicleDevice:Name>\n" +
                "\t\t\t<vehicleDevice:Value>113</vehicleDevice:Value>\n" +
                "\t\t</wfs:Property>\n" +
                "\t\t<ogc:Filter>\n" +
                "\t\t\t<ogc:FeatureId fid=\"vehicledevice." + vehicleID + "\"/>\n" +
                "\t\t</ogc:Filter>\n" +
                "\t</wfs:Update>\n" +
                "</wfs:Transaction>";
    }

}
