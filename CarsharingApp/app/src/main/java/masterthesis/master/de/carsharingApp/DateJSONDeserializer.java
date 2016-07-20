package masterthesis.master.de.carsharingApp;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by uucly on 07.03.2016.
 */
public class DateJSONDeserializer extends JsonDeserializer<Date>{
    private static final String TAG = "DateJSONDeserializer";

    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        Log.i(TAG, jp.getText());
        return new Date();
    }
}
