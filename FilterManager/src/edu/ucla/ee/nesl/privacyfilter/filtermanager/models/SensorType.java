package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import android.util.Log;

public class SensorType {
	private int dbId; // the sensors ID in our database
	private int androidId; // the sensor type constant according to android
	private String name;
	
	private static class AndroidSensorIdData {
		public String sensorName;
		public String[] valueNames;
		public String[] valueUnits;

		public AndroidSensorIdData (String newName, String[] newValueNames, String[] newValueUnits) {
			this.sensorName = newName;
			this.valueNames = newValueNames;
			this.valueUnits = newValueUnits;
		}
	}

	private static String[] namesForAndroidIds = {
		/* 0x00 */ "(Unused)",
		/* 0x01 */ "Accelerometer",
		/* 0x02 */ "Magnetic field",
		/* 0x03 */ "Orientation", // orientation type is deprecated
		/* 0x04 */ "Gyroscope",
		/* 0x05 */ "Light",
		/* 0x06 */ "Pressure",
		/* 0x07 */ "Temperature (deprecated)", // deprecated for TYPE_AMBIENT_TEMPERATURE
		/* 0x08 */ "Proximity",
		/* 0x09 */ "Gravity",
		/* 0x0a */ "Linear acceleration",
		/* 0x0b */ "Rotation vector",
		/* 0x0c */ "Relative humidity",
		/* 0x0d */ "Ambient temperature",
		/* 0x0e */ "Magnetic field (uncalibrated)",
		/* 0x0f */ "Game rotation vector",
		/* 0x10 */ "Gyroscope (uncalibrated)",
		/* 0x11 */ "Significant motion"
	};

	private static AndroidSensorIdData[] androidSensorIdData = {
		/* 0x00 */ new AndroidSensorIdData("(Unused sensor ID)", new String[]{}, new String[]{}),
		/* 0x01 */ new AndroidSensorIdData("Accelerometer", new String[]{"X", "Y", "Z"}, new String[]{"m/s\u00b2", "m/s\u00b2", "m/s\u00b2"}),
		/* 0x02 */ new AndroidSensorIdData("Magnetic field", new String[]{"X", "Y", "Z"}, new String[]{"\u03bcT", "\u03bcT", "\u03bcT"}),
		/* 0x03 */ new AndroidSensorIdData("Orientation", new String[]{"Aziumuth", "Pitch", "Roll"}, new String[]{"degrees", "degrees", "degrees"}), // orientation type is deprecated
		/* 0x04 */ new AndroidSensorIdData("Gyroscope", new String[]{"X", "Y", "Z"}, new String[]{"rad/s", "rad/s", "rad/s"}),
		/* 0x05 */ new AndroidSensorIdData("Light", new String[]{"Illuminance"}, new String[]{"lx"}),
		/* 0x06 */ new AndroidSensorIdData("Pressure", new String[]{"Pressure"}, new String[]{"millibars"}),
		/* 0x07 */ new AndroidSensorIdData("Temperature (deprecated)", new String[]{}, new String[]{}), // deprecated for TYPE_AMBIENT_TEMPERATURE
		/* 0x08 */ new AndroidSensorIdData("Proximity", new String[]{"Distance"}, new String[]{"cm"}),
		/* 0x09 */ new AndroidSensorIdData("Gravity", new String[]{"X", "Y", "Z"}, new String[]{"m/s\u00b2", "m/s\u00b2", "m/s\u00b2"}),
		/* 0x0a */ new AndroidSensorIdData("Linear acceleration", new String[]{"X", "Y", "Z"}, new String[]{"m/s\u00b2", "m/s\u00b2", "m/s\u00b2"}),
		/* 0x0b */ new AndroidSensorIdData("Rotation vector", new String[]{"X", "Y", "Z", "\u03b8", "Accuracy"}, new String[]{"unitless", "unitless", "unitless", "radians", "radians"}),
		/* 0x0c */ new AndroidSensorIdData("Relative humidity", new String[]{"Air humidity"}, new String[]{"%"}),
		/* 0x0d */ new AndroidSensorIdData("Ambient temperature", new String[]{"Room temperature"}, new String[]{"\u00b0C"}),
		/* 0x0e */ new AndroidSensorIdData("Magnetic field (uncalibrated)", new String[]{"Uncalib. X", "Uncalib. Y", "Uncalib. Z"}, new String[]{"\u03bcT", "\u03bcT", "\u03bcT"}),
		/* 0x0f */ new AndroidSensorIdData("Game rotation vector", new String[]{"X", "Y", "Z", "\u03b8", "Accuracy"}, new String[]{"unitless", "unitless", "unitless", "radians", "radians"}),
		/* 0x10 */ new AndroidSensorIdData("Gyroscope (uncalibrated)", new String[]{"Uncalib. X", "Uncalib. Y", "Uncalib. Z"}, new String[]{"rad/s", "rad/s", "rad/s"}),
		/* 0x11 */ new AndroidSensorIdData("Significant motion", new String[]{"Motion"}, new String[]{"unitless"})
	};

	public boolean equals (SensorType s) {
		// for now we are only concerning ourselves with sensors of which the android API is aware
		return (this.androidId == s.androidId);
	}

	public int hashCode () {
		return this.androidId;
	}

	public static SensorType defineFromDb (int dbId) {
		SensorType st = new SensorType();
		st.dbId = dbId;

		st.name = "DB not set up";

		return st;
	}

	public static SensorType defineFromAndroid (int baseAndroidId) {
		SensorType st = new SensorType();
		st.androidId = baseAndroidId;

		st.name = namesForAndroidIds[st.androidId];

		return st;
	}
	
	private SensorType () {
	}

	public String toString () {
		return getName();
	}
	
	public String getName () {
		return name;
	}
	
	public int getDbId () {
		return dbId;
	}
	
	public int getAndroidId () {
		return androidId;
	}

	public String[] getAndroidValueNames () {
		return androidSensorIdData[androidId].valueNames;
	}

	public String[] getAndroidValueUnits () {
		return androidSensorIdData[androidId].valueUnits;
	}

	public float getRecommendedDefaultValue () {
		return 1.0f; // FIXME test value for now
	}
}
