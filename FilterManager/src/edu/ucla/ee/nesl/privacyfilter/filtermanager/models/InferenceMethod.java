package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import android.database.*;
import android.database.sqlite.*;

public class InferenceMethod {
	private int methodId; // the method's ID in the database
	private int inferenceId; // the inference which this method yields

	public InferenceMethod (int methodId) {
		this.methodId = methodId;

		SQLiteDatabase db = SQLiteDatabase.openDatabase(AppFilterData.INFERENCE_DB_FILE, null, SQLiteDatabase.OPEN_READONLY);
		Cursor result = db.query("Methods", new String[]{"inferenceID"}, "methodID = ?", new String[]{Integer.toString(this.methodId)}, null, null, null, "1");
		result.moveToFirst();
		inferenceId = result.getInt(0);
		db.close();
	}

	public int getInferenceId () {
		return inferenceId;
	}
}
