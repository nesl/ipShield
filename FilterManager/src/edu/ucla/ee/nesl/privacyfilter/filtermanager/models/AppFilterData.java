package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

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

import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;

import com.google.protobuf.*;
import android.os.FirewallConfigManager;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.io.protobuf.FirewallConfigMessage;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.io.protobuf.SensorCountMessage;


public class AppFilterData {
	public static final String APP_TRACKER_FILE = "/data/sensor-counter";

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

	private Context context;
	private PackageManager pm;
	private ApplicationInfo pmAppInfo; // app info from some package manager object

	//private FirewallConfig fwConf;

	private ArrayList<SensorType> sensorsUsed = new ArrayList<SensorType>();

	public AppFilterData (Context baseContext, ApplicationInfo basePmAppInfo) {
		this.context = baseContext;
		this.pm = this.context.getPackageManager();
		this.pmAppInfo = basePmAppInfo;

		// TODO initialize this.fwConf??

		// detect sensors used
		byte[] rawSensorBytes = readBytes(APP_TRACKER_FILE);

		try {
			SensorCountMessage.SensorCounter allSensorData = SensorCountMessage.SensorCounter.parseFrom(rawSensorBytes);

			for (int entryIdx = 0; entryIdx < allSensorData.getAppEntryCount(); entryIdx++) {
				SensorCountMessage.AppEntry appEntry = allSensorData.getAppEntry(entryIdx);

				if (appEntry.getPkgName().equals(pmAppInfo.packageName)) { // we're in the right package
					for (int sensorIdx = 0; sensorIdx < appEntry.getSensorEntryCount(); sensorIdx++) {
						// index of the sensor in the sensorEntry array corresponds the sensor's android ID
						if (appEntry.getSensorEntry(sensorIdx).getCount() > 0) {
							this.sensorsUsed.add(SensorType.defineFromAndroid(sensorIdx));
						}
					}
				}
			}
		} catch (InvalidProtocolBufferException protoE) {
			Log.e(getClass().toString(), "Caught an exception: " + protoE.toString());
		}
	}

	public ApplicationInfo getApplicationInfo () {
		return pmAppInfo;
	}

	public String toString () {
		return (String) pmAppInfo.loadLabel(pm);
	}

	public Drawable getIcon () {
		return pm.getApplicationIcon(pmAppInfo);
	}

	public ArrayList<Inference> getInferences () {
		ArrayList<Inference> infs = new ArrayList<Inference>();
		infs.add(new Inference(0));
		infs.add(new Inference(1));
		infs.add(new Inference(2));
		infs.add(new Inference(3));

		return infs;
	}
	
	public ArrayList<SensorType> getSensorsUsed () {
		return sensorsUsed;
	}
}
