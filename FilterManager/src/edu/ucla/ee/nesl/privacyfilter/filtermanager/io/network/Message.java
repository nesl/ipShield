package edu.ucla.ee.nesl.privacyfilter.filtermanager.io.network;

import java.util.ArrayList;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppId;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;

// a Message represents a mapping between one application ID and a list
// of sensors we've seen being used by that application.
// each sensor should be identified by its sensorID in the SQLite database
// AS WELL AS its human-readable name
public class Message {
	protected AppId appId;
	protected ArrayList<SensorType> sensorsUsed;
	public Message (AppId newAppId, ArrayList<SensorType> newSensorTypesUsed) {
		this.appId = newAppId;
		if (newSensorTypesUsed != null) {
			this.sensorsUsed = newSensorTypesUsed;
		} else {
			this.sensorsUsed = new ArrayList<SensorType>();
		}
	}
	public Message (AppId newAppId) {
		this.appId = newAppId;
		this.sensorsUsed = new ArrayList<SensorType>();
	}
	
	public void addSensorTypeUsed (SensorType sensor) {
		sensorsUsed.add(sensor);
	}
	
	// export this Message as a JSON string
	public String toString () {
		JSONObject message = new JSONObject();
		try {
			message.put("appId", appId);
		
			JSONArray sensorsJson = new JSONArray();
		
			for (int sensorIdx = 0; sensorIdx < sensorsUsed.size(); sensorIdx++) {
				JSONObject sensorObj = new JSONObject();
				sensorObj.put("sensorId", sensorsUsed.get(sensorIdx).getDbId());
				sensorObj.put("sensorName", sensorsUsed.get(sensorIdx).getName());
			}
			message.put("sensorsUsed", sensorsJson.toString());
		} catch (JSONException e) {
			Log.e(getClass().toString(), "Tried to convert message to JSON, but a JSONException occurred... this shouldn't happen", e);
		}
		
		return message.toString();
	}
}
