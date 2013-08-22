package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

// imports {{{

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.graphics.drawable.Drawable;

import android.util.Base64;

import android.util.Log;

import android.database.*;
import android.database.sqlite.*;


import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.InferenceMethod;

import com.google.protobuf.*;
import android.os.FirewallConfigManager;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.io.protobuf.FirewallConfigMessage;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.io.protobuf.SensorCountMessage;

// }}}

public class AppFilterData {
	public static final String APP_TRACKER_FILE = "/data/sensor-counter";
	public static final String INFERENCE_DB_FILE = "/data/data/edu.ucla.ee.nesl.privacyfilter.filtermanager/databases/inference.db";

	// convenience {{{
	public static byte[] readBytes (String filePath) {
		byte[] buf;
		
		try {
			File file = new File(filePath);
			BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
			
			if (file.length() > Integer.MAX_VALUE) {
				Log.e("readBytes", "Tried to read a file (" + filePath + ") larger than I can handle.  Data read may be truncated.");
			}

			buf = new byte[(int) file.length()];
			inStream.read(buf, 0, (int) file.length());
			
			inStream.close();
		} catch (Exception e) {
			Log.e("readBytes", "Caught an exception: " + e.toString());
			buf = new byte[0]; // return empty data
		}

		return buf;
	}
	// }}}

	private Context context;
	private PackageManager pm;
	private ApplicationInfo pmAppInfo; // app info from some package manager object

	private ArrayList<SensorType> sensorsUsed;
	private ArrayList<InferenceMethod> inferenceMethods = null; // per the inference database, this should list all methods by which this app could obtain an inference
	private ArrayList<Inference> inferences = null; // per the inference database, this should list all inferences that this app could obtain

	private ArrayList<SensorType> detectSensorsUsed () { // {{{
		ArrayList<SensorType> detectedSensorsUsed = new ArrayList<SensorType>();

		byte[] rawSensorBytes = readBytes(APP_TRACKER_FILE);

		try {
			SensorCountMessage.SensorCounter allSensorData = SensorCountMessage.SensorCounter.parseFrom(rawSensorBytes);

			for (int entryIdx = 0; entryIdx < allSensorData.getAppEntryCount(); entryIdx++) {
				SensorCountMessage.AppEntry appEntry = allSensorData.getAppEntry(entryIdx);

				if (appEntry.getPkgName().equals(pmAppInfo.packageName)) { // we're in the right package
					for (int sensorIdx = 0; sensorIdx < appEntry.getSensorEntryCount(); sensorIdx++) {
						// index of the sensor in the sensorEntry array corresponds the sensor's android ID
						if (appEntry.getSensorEntry(sensorIdx).getCount() > 0) {
							detectedSensorsUsed.add(SensorType.defineFromAndroid(sensorIdx));
						}
					}
				}
			}
		} catch (InvalidProtocolBufferException protoE) {
			Log.e(getClass().toString(), "Caught an exception: " + protoE.toString());
		}

		return detectedSensorsUsed;
	} // }}}

	private void createSensorsAvailableTable (SQLiteDatabase db, ArrayList<Integer> dbSensorTypesAvailable) throws SQLException { // {{{
		db.execSQL("CREATE TEMPORARY TABLE SensorsAvailable (sensorID INT, FOREIGN KEY (sensorID) REFERENCES Sensors(sensorID));");

		if (dbSensorTypesAvailable.size() > 0) { // add sensor types the table
			String sensorsAvailSql = "INSERT INTO SensorsAvailable VALUES ";

			for (int dbSType : dbSensorTypesAvailable) {
				sensorsAvailSql += "(" + Integer.toString(dbSType) + "), ";
			}

			int finalDbSType = dbSensorTypesAvailable.get(dbSensorTypesAvailable.size() - 1);
			sensorsAvailSql += "(" + Integer.toString(finalDbSType) + ");";

			// not using Android's insert method because it does not support multiple rows
			db.execSQL(sensorsAvailSql);
		}
	} // }}}
	private ArrayList<InferenceMethod> queryForInferenceMethods  () { // {{{
		int[] methodIds;

		ArrayList<Integer> dbSensorTypesAvailable = new ArrayList<Integer>();
		for (SensorType st : getSensorsUsed()) {
			dbSensorTypesAvailable.add(st.getDbId());
		}
		SQLiteDatabase db = SQLiteDatabase.openDatabase(INFERENCE_DB_FILE, null, SQLiteDatabase.OPEN_READWRITE);
		db.beginTransaction();
		try {
			createSensorsAvailableTable(db, dbSensorTypesAvailable);

			Cursor iMethodsC = db.rawQuery("SELECT DISTINCT methodID FROM Requirements EXCEPT SELECT methodID FROM Requirements LEFT JOIN SensorsAvailable ON (Requirements.sensorID = SensorsAvailable.sensorID) WHERE SensorsAvailable.sensorID IS NULL;", null);
			methodIds = new int[iMethodsC.getCount()];
			for (int methIdx = 0; methIdx < methodIds.length; methIdx++) {
				iMethodsC.moveToPosition(methIdx);
				methodIds[methIdx] = iMethodsC.getInt(0);
			}

			db.execSQL("DROP TABLE SensorsAvailable;");
			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();
		}
		db.close();

		ArrayList<InferenceMethod> methods = new ArrayList<InferenceMethod>();
		for (int mId : methodIds) {
			methods.add(new InferenceMethod(mId));
		}

		return methods;
	} // }}}

	public AppFilterData (Context baseContext, ApplicationInfo basePmAppInfo) { // {{{
		this.context = baseContext;
		this.pm = this.context.getPackageManager();
		this.pmAppInfo = basePmAppInfo;

		sensorsUsed = detectSensorsUsed();
	} // }}}

	public String toString () { // {{{
		return (String) pmAppInfo.loadLabel(pm);
	} // }}}
	public ApplicationInfo getApplicationInfo () { // {{{
		return pmAppInfo;
	} // }}}
	public String getPackageName() { // {{{
		return pmAppInfo.packageName;
	} // }}}
	public Drawable getIcon () { // {{{
		return pm.getApplicationIcon(pmAppInfo);
	} // }}}

	public ArrayList<InferenceMethod> getInferenceMethods () { // {{{
		if (inferenceMethods == null) {
			inferenceMethods = queryForInferenceMethods();
		}

		return inferenceMethods;
	} // }}}
	public ArrayList<Inference> getInferences () { // {{{
		if (inferenceMethods == null) {
			inferences = Inference.getInferencesFromMethods(getInferenceMethods());
		}

		return inferences;
	} // }}}
	public ArrayList<SensorType> getSensorsUsed () { // {{{
		return sensorsUsed;
	} // }}}
}
