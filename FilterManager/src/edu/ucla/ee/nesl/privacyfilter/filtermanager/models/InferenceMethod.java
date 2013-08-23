package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import java.util.ArrayList;

import android.database.*;
import android.database.sqlite.*;

public class InferenceMethod {
	private int methodId; // the method's ID in the database
	private int inferenceId; // the inference which this method yields
	private ArrayList<SensorType> sensorsRequired = null; // the sensors an app would access to have to perform this particular inference method
	private double accuracy; // the "accuracy" of this method
	private String paperTitle; // title of the paper from which the method comes

	public InferenceMethod (int methodId) {
		this.methodId = methodId;
		this.sensorsRequired = new ArrayList<SensorType>();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(AppFilterData.INFERENCE_DB_FILE, null, SQLiteDatabase.OPEN_READONLY);

		Cursor metaDataCursor = db.query("Methods INNER JOIN Papers ON Methods.paperID = Papers.paperID", new String[]{"inferenceID", "accuracy", "Papers.title"}, "methodID = ?", new String[]{Integer.toString(this.methodId)}, null, null, null, "1");
		metaDataCursor.moveToFirst();
		this.inferenceId = metaDataCursor.getInt(0);
		this.accuracy = metaDataCursor.getDouble(1);
		this.paperTitle = metaDataCursor.getString(2);

		Cursor sensorIdCursor = db.query(true, "Requirements", new String[]{"sensorID"}, "methodID = ?", new String[]{Integer.toString(this.methodId)}, null, null, null, null);
		while (! sensorIdCursor.isLast ()) {
			sensorIdCursor.moveToNext();
			this.sensorsRequired.add(SensorType.defineFromDb(sensorIdCursor.getInt(0)));
		}

		db.close();
	}

	public int getInferenceId () {
		return inferenceId;
	}

	public ArrayList<SensorType> getSensorsRequired () {
		return sensorsRequired;
	}

	public double getAccuracy () {
		return accuracy;
	}

	public String getPaperTitle () {
		return paperTitle;
	}
}
