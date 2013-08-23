package edu.ucla.ee.nesl.privacyfilter.filtermanager.algo;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.InferenceMethod;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.Inference;

public class InferenceSensorMapper {
	public static final int DISALLOW = 0;
	public static final int ALLOW = 1;

	public static class InferencePreference { // {{{
		private int action;
		private int priority; // user's specified priority, or -1 if they didn't set one

		public InferencePreference (int action, int priority) {
			this.action = action;
			this.priority = priority;
		}

		public int getAction() {
			return action;
		}

		public int getPriority() {
			return priority;
		}
	} // }}}

	public static HashMap<SensorType, Integer> generateSensorMap (HashMap<Inference, InferencePreference> inferencePreferences, ArrayList<SensorType> allSensorsAvailable, int sensorTolerance) {
		HashMap<SensorType, Integer> sensorMap = new HashMap<SensorType, Integer>();

		for (Inference inference : inferencePreferences.keySet()) {
			if (inferencePreferences.get(inference).getAction() == ALLOW && inferencePreferences.get(inference).getPriority() > 0) {
				for (InferenceMethod method : inference.getInferenceMethods()) {
					for (SensorType sensor : method.getSensorsRequired()) {
						sensorMap.put(sensor, ALLOW);
					}
				}
			}
		}

		for (Inference inference : inferencePreferences.keySet()) {
			if (inferencePreferences.get(inference).getAction() != ALLOW && inferencePreferences.get(inference).getPriority() > 0) {
				for (InferenceMethod method : inference.getInferenceMethods()) {
					for (SensorType sensor : method.getSensorsRequired()) {
						sensorMap.put(sensor, DISALLOW);
					}
				}
			}
		}
		
		return sensorMap;
	}

}
