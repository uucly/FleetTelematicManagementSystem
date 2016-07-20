package masterthesis.master.de.vehicledeviceclient;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * Created by uucly on 16.03.2016.
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    private static final String TAG = "DateDeserializer";

    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        Log.i(TAG, jp.getText());
        return new Date();
    }
}
