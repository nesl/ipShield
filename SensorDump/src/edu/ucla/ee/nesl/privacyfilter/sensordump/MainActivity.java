package edu.ucla.ee.nesl.privacyfilter.sensordump;

import android.os.Bundle;
import android.content.Context;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;

import java.util.ArrayList;

public class MainActivity extends Activity {

	private static final String newline = System.getProperty("line.separator");

	private static final int SENSORS_PER_PAGE = 3; // number of sensors to display at one time

	private class SensorDisplayer implements SensorEventListener { // {{{
		private Context context;
		private Sensor sensor;
		private View rootView;
		private TextView nameView;
		private TextView accuracyView;
		private TextView valuesView;
		
		public SensorDisplayer (Context baseContext, Sensor baseSensor, View baseRootView) {
			this.context = baseContext;
			this.sensor = baseSensor;
			this.rootView = baseRootView;
			this.nameView = (TextView) this.rootView.findViewById(R.id.sensor_name);
			this.accuracyView = (TextView) this.rootView.findViewById(R.id.sensor_accuracy);
			this.valuesView = (TextView) this.rootView.findViewById(R.id.sensor_values);

			nameView.setText(sensor.getVendor() + " " + sensor.getName() + newline + "(ver. " + Integer.toString(sensor.getVersion()) + ", resolution: " + Float.toString(sensor.getResolution()) + ")");
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			accuracyView.setText("Accuracy: " + Integer.toString(accuracy));
		}

		public void onSensorChanged(SensorEvent event) {
			StringBuilder values = new StringBuilder("Values: " + newline + "\t");
			for (int idx = 0; idx < event.values.length; idx++) {
				values.append(Float.toString(event.values[idx]));

				if (idx !=event.values.length - 1) {
					values.append(newline + "\t");
				}
			}

			valuesView.setText(values.toString());
		}

		public void removeView () {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
	} // }}}
	private class NavButtonListener implements OnClickListener {
		public void onClick(View button) {
			if (button.getId() == R.id.prevButton) {
				if (currentlyShowingFrom >= SENSORS_PER_PAGE) {
					currentlyShowingFrom -= SENSORS_PER_PAGE;
					updateDisplay();
				}
			} else if (button.getId() == R.id.nextButton) {
				if (currentlyShowingFrom < allSensors.size() - SENSORS_PER_PAGE) {
					currentlyShowingFrom += SENSORS_PER_PAGE;
					updateDisplay();
				}
			}
		}
	}

	private ViewGroup contentBodyView;
	private LayoutInflater inflater;
	private SensorManager sensorManager;

	private ArrayList<Sensor> allSensors;

	private int currentlyShowingFrom = 0;
	private ArrayList<Sensor> displayedSensors;
	private ArrayList<SensorDisplayer> sensorDisplayers; // parallel with displayedSensors

	// update the display, showing a page-worth of sensors, starting at the index given by currentlyShowingFrom
	private void updateDisplay() {
		// stop displaying the old page of sensors
		for (int sensorIdx = 0; sensorIdx < displayedSensors.size(); sensorIdx++) {
			sensorDisplayers.get(sensorIdx).removeView();
			sensorManager.unregisterListener(sensorDisplayers.get(sensorIdx), displayedSensors.get(sensorIdx));
		}

		// figure out which sensors to display now
		int showTo = Math.min(currentlyShowingFrom + SENSORS_PER_PAGE, allSensors.size());

		// start displaying the new page of sensors
		displayedSensors = new ArrayList<Sensor>(allSensors.subList(currentlyShowingFrom, showTo));
		sensorDisplayers = new ArrayList<SensorDisplayer>(); // parallel array
		for (int sensorIdx = 0; sensorIdx < displayedSensors.size(); sensorIdx++) {
			Sensor curSensor = displayedSensors.get(sensorIdx);
			View curSensorView = inflater.inflate(R.layout.sensor, null);
			SensorDisplayer sd = new SensorDisplayer(this, curSensor, curSensorView);
			sensorDisplayers.add(sensorIdx, sd);
			contentBodyView.addView(curSensorView);

			sensorManager.registerListener(sd, curSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	private void initializeData () {
		contentBodyView = (ViewGroup) findViewById(R.id.content);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		allSensors = new ArrayList<Sensor>(sensorManager.getSensorList(Sensor.TYPE_ALL));

		displayedSensors = new ArrayList<Sensor>();
		sensorDisplayers = new ArrayList<SensorDisplayer>(); // parallel with displayedSensors
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initializeData();

		findViewById(R.id.prevButton).setOnClickListener(new NavButtonListener());
		findViewById(R.id.nextButton).setOnClickListener(new NavButtonListener());
		
		updateDisplay();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}

