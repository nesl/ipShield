package edu.ucla.ee.nesl.privacyfilter.filtermanager;

// imports {{{

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Button;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppFilterData;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppId;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.Inference;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;

import com.google.protobuf.*;
import android.os.FirewallConfigManager;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.io.protobuf.FirewallConfigMessage;
import android.util.Base64;

// }}}

/**
 * A fragment representing a single App detail screen.
 * This fragment is either contained in a {@link AppListActivity}
 * in two-pane mode (on tablets) or a {@link AppDetailActivity}
 * on handsets.
 */
public class AppDetailFragment extends Fragment {
	// Android boilerplate {{{

	/**
	 * The fragment argument representing the app ID that this fragment
	 * represents.
	 */

	public static final String ARG_APP_STR = "app_id_string";
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AppDetailFragment() {
	}

	// }}}
		// Convenience {{{

		private static float str2float (CharSequence chs) {
			String str = new String((String) chs.toString());
			if (str.equals("")) {
				return 0f;
			} else {
				return Float.parseFloat(str);
			}
		}

		// }}}

	// SensorTypeRule {{{

	// represents a single stipulation about what to do with one sensor type re the
	// app in question
	// i.e., one "action" so to speak

	public class SensorTypeRule {
		private static final int MAX_CONSTANTS = 5;

		private final FirewallConfigMessage.Action.ActionType[] PROTOBUF_ACTIONS = {
			FirewallConfigMessage.Action.ActionType.ACTION_PASSTHROUGH,
			FirewallConfigMessage.Action.ActionType.ACTION_SUPPRESS,
			FirewallConfigMessage.Action.ActionType.ACTION_CONSTANT,
			FirewallConfigMessage.Action.ActionType.ACTION_DELAY,
			FirewallConfigMessage.Action.ActionType.ACTION_PERTURB,
		};

		private final FirewallConfigMessage.Perturb.DistributionType[] PROTOBUF_DISTRIBUTIONS = {
			FirewallConfigMessage.Perturb.DistributionType.GAUSSIAN,
			FirewallConfigMessage.Perturb.DistributionType.UNIFORM,
			FirewallConfigMessage.Perturb.DistributionType.EXPONENTIAL
		};

		private SensorType sensorType;

		// View members {{{

		private View ruleView;
		private Spinner ruleActionView;

		private ViewGroup[] constantViews;
		private TextView[] constantNameViews;
		private TextView[] constantUnitViews;
		private TextView[] constantValueViews;

		private ViewGroup delayView;
		private TextView delayDaysView;
		private TextView delayHoursView;
		private TextView delayMinutesView;
		private TextView delaySecondsView;
		private TextView delayMillisecondsView;

		private ViewGroup perturbView;
		private Spinner perturbDistributionView;
		private TextView perturbMeanView;
		private TextView perturbVarianceView;
		private TextView perturbMinView;
		private TextView perturbMaxView;
		private TextView perturbLambdaView;

		private ViewGroup timingView;
		private CheckBox[] daysOfWeekView;
		private TextView fromHourView;
		private TextView toHourView;
		private TextView fromMinuteView;
		private TextView toMinuteView;

		// }}}

		protected SensorTypeRule (SensorType sensorType) {
			this.sensorType = sensorType;
		}

		private void setupPerturbSpinner () { // {{{
			perturbDistributionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
					perturbMeanView.setVisibility(View.GONE);
					perturbVarianceView.setVisibility(View.GONE);
					perturbMinView.setVisibility(View.GONE);
					perturbMaxView.setVisibility(View.GONE);
					perturbLambdaView.setVisibility(View.GONE);

					switch (position) {
						case 0: // gaussian
							perturbMeanView.setVisibility(View.VISIBLE);
							perturbVarianceView.setVisibility(View.VISIBLE);
							break;
						case 1: // uniform
							perturbMinView.setVisibility(View.VISIBLE);
							perturbMaxView.setVisibility(View.VISIBLE);
							break;
						case 2: // exponential
							perturbLambdaView.setVisibility(View.VISIBLE);
							break;
					}
				}

				public void onNothingSelected (AdapterView<?> parent) {
				}
			});
		} // }}}
		private void setupActionSpinner () { // {{{
			ruleActionView = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action);

			ruleActionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
					for (int constIdx = 0; constIdx < MAX_CONSTANTS; constIdx++) {
						constantViews[constIdx].setVisibility(View.GONE);
					}
					delayView.setVisibility(View.GONE);
					perturbView.setVisibility(View.GONE);
					timingView.setVisibility(View.GONE);

					switch (position) {
						case 0: // no action
							break;
						case 1: // suppress
							timingView.setVisibility(View.VISIBLE);
							break;
						case 2: // constant
							for (int constIdx = 0; constIdx < MAX_CONSTANTS && constIdx < sensorType.getAndroidValueNames().length; constIdx++) {
								constantViews[constIdx].setVisibility(View.VISIBLE);
							}
							timingView.setVisibility(View.VISIBLE);
							break;
						case 3: // delay
							delayView.setVisibility(View.VISIBLE);
							timingView.setVisibility(View.VISIBLE);
							break;
						case 4: // perturb
							perturbView.setVisibility(View.VISIBLE);
							timingView.setVisibility(View.VISIBLE);
							break;
					}
				}

				public void onNothingSelected (AdapterView<?> parent) {
				}
			});
		} // }}}

		protected void setView (View ruleView) {
			this.ruleView = ruleView;

			TextView name = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_name);

			name.setText(sensorType.getName() + ":");

			// set up this action's arguments {{{

			this.constantViews = new ViewGroup[MAX_CONSTANTS];
			this.constantNameViews = new TextView[MAX_CONSTANTS];
			this.constantUnitViews = new TextView[MAX_CONSTANTS];
			this.constantValueViews = new TextView[MAX_CONSTANTS];

			this.constantViews[0] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0);
			this.constantViews[1] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1);
			this.constantViews[2] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2);
			this.constantViews[3] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3);
			this.constantViews[4] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4);
			this.constantNameViews[0] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0_name);
			this.constantNameViews[1] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1_name);
			this.constantNameViews[2] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2_name);
			this.constantNameViews[3] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3_name);
			this.constantNameViews[4] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4_name);
			this.constantUnitViews[0] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0_unit);
			this.constantUnitViews[1] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1_unit);
			this.constantUnitViews[2] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2_unit);
			this.constantUnitViews[3] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3_unit);
			this.constantUnitViews[4] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4_unit);
			this.constantValueViews[0] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0_value);
			this.constantValueViews[1] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1_value);
			this.constantValueViews[2] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2_value);
			this.constantValueViews[3] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3_value);
			this.constantValueViews[4] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4_value);

			// set up constant names and units
			for (int constIdx = 0; constIdx < MAX_CONSTANTS && constIdx < sensorType.getAndroidValueNames().length; constIdx++) {
				constantNameViews[constIdx].setText(sensorType.getAndroidValueNames()[constIdx] + ":");
				constantUnitViews[constIdx].setText("(" + sensorType.getAndroidValueUnits()[constIdx] + ")");
			}

			this.delayView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay);
			this.delayDaysView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_days);
			this.delayHoursView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_hours);
			this.delayMinutesView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_minutes);
			this.delaySecondsView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_seconds);
			this.delayMillisecondsView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_milliseconds);

			this.perturbView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb);
			this.perturbDistributionView = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_distribution);
			this.perturbMeanView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_mean);
			this.perturbVarianceView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_variance);
			this.perturbMinView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_min);
			this.perturbMaxView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_max);
			this.perturbLambdaView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_lambda);

			timingView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing);
			daysOfWeekView = new CheckBox[7];
			daysOfWeekView[0] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_sunday);
			daysOfWeekView[1] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_monday);
			daysOfWeekView[2] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_tuesday);
			daysOfWeekView[3] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_wednesday);
			daysOfWeekView[4] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_thursday);
			daysOfWeekView[5] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_friday);
			daysOfWeekView[6] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_saturday);
			fromHourView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_fromhour);
			toHourView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_tohour);
			fromMinuteView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_fromminutes);
			toMinuteView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_tominutes);

			// }}}

			setupActionSpinner();
			setupPerturbSpinner();
		}

		protected FirewallConfigMessage.Rule genRule () { // {{{

			// build param {{{

			FirewallConfigMessage.Param.Builder paramBuilder = FirewallConfigMessage.Param.newBuilder();

			// set sensorvalue {{{

			FirewallConfigMessage.SensorValue.Builder svBuilder = FirewallConfigMessage.SensorValue.newBuilder();

			svBuilder.setScalarVal(str2float((constantValueViews[0].getText())));

			svBuilder.setVecVal(
				FirewallConfigMessage.VectorValue.newBuilder()
					.setX(str2float(constantValueViews[0].getText()))
					.setY(str2float(constantValueViews[1].getText()))
					.setZ(str2float(constantValueViews[2].getText()))
					.setTheta(str2float(constantValueViews[3].getText()))
					.setAccuracy(str2float(constantValueViews[4].getText()))
					.build()
			);

			// TODO timestamp is currently unused by the service

			svBuilder.setDefaultVal(sensorType.getRecommendedDefaultValue());

			paramBuilder.setConstantValue(svBuilder.build());

			// }}}
			// set delay {{{

			float delay = 0f;

			delay += str2float(delayMillisecondsView.getText()) / 1000;
			delay += str2float(delaySecondsView.getText());
			delay += str2float(delayMinutesView.getText()) * 60;
			delay += str2float(delayHoursView.getText()) * 3600;
			delay += str2float(delayDaysView.getText()) * 86400;

			paramBuilder.setDelay(delay);
			
			// }}}
			// set perturb data {{{

			FirewallConfigMessage.Perturb.Builder perturbBuilder = FirewallConfigMessage.Perturb.newBuilder();

			perturbBuilder.setDistType(PROTOBUF_DISTRIBUTIONS[perturbDistributionView.getSelectedItemPosition()]);
			perturbBuilder.setMean(str2float(perturbMeanView.getText()));
			perturbBuilder.setVariance(str2float(perturbVarianceView.getText()));
			perturbBuilder.setUnifMin(str2float(perturbMinView.getText()));
			perturbBuilder.setUnifMax(str2float(perturbMaxView.getText()));
			perturbBuilder.setExpParam(str2float(perturbLambdaView.getText()));

			paramBuilder.setPerturb(perturbBuilder.build());

			// }}}

			FirewallConfigMessage.Param param = paramBuilder.build();

			// }}}
			// build action {{{

			FirewallConfigMessage.Action.Builder actionBuilder = FirewallConfigMessage.Action.newBuilder();
			actionBuilder.setActionType(PROTOBUF_ACTIONS[ruleActionView.getSelectedItemPosition()]);

			actionBuilder.setParam(param);

			FirewallConfigMessage.Action action = actionBuilder.build();

			// }}}
			// build timing {{{

			FirewallConfigMessage.DateTime.Builder dtBuilder = FirewallConfigMessage.DateTime.newBuilder();
			for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
				if (daysOfWeekView[dayIdx].isChecked()) {
					dtBuilder.addDayOfWeek(dayIdx);
				}
			}

			dtBuilder.setFromHr(Integer.parseInt(fromHourView.getText().toString()));
			dtBuilder.setFromMin(Integer.parseInt(fromMinuteView.getText().toString()));
			dtBuilder.setToHr(Integer.parseInt(toHourView.getText().toString()));
			dtBuilder.setToMin(Integer.parseInt(toMinuteView.getText().toString()));

			FirewallConfigMessage.DateTime dt = dtBuilder.build();

			// }}}
			// build rule {{{

			FirewallConfigMessage.Rule.Builder ruleBuilder = FirewallConfigMessage.Rule.newBuilder();
			ruleBuilder.setRuleName("FILTERMANAGER_RULE" + Integer.toString((new java.util.Random()).nextInt(10000)));
			ruleBuilder.setSensorType(sensorType.getAndroidId());
			ruleBuilder.setPkgName(app.getApplicationInfo().packageName);
			ruleBuilder.setPkgUid(app.getApplicationInfo().uid);
			// TODO ignoring dateTime for now
			ruleBuilder.setAction(action);
			ruleBuilder.setDateTime(dt);

			return ruleBuilder.build();

			// }}}

		} // }}}
	}

	// }}}

	AppFilterData app; // the app whose data this fragment is open for editing

	ViewGroup sensorViews; // the view containing the sensors
	ArrayList<SensorTypeRule> rules = new ArrayList<SensorTypeRule>();

	// onCreate {{{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_APP_STR)) {
			AppId appId = new AppId(getArguments().getString(ARG_APP_STR));

			app = new AppFilterData(getActivity(), appId.getApplicationInfo(getActivity().getPackageManager(), 0));
		}
	}

	// }}}

	// onCreateView {{{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_app_detail, container, false);

		if (app == null) {
			Log.wtf(getClass().toString(), "Was instructed to show detail on an app but app was given as null");
		}

		sensorViews = (ViewGroup) rootView.findViewById(R.id.fragment_app_detail_sensors);
		SensorManager sensorMgr = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

		LayoutInflater sensorInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Set up a temporary header {{{
		((TextView) rootView.findViewById(R.id.fragment_app_detail_title)).setText(app.toString());
		((ImageView) rootView.findViewById(R.id.fragment_app_detail_icon)).setImageDrawable(app.getIcon());
		//((TextView) rootView.findViewById(R.id.fragment_app_detail_subtitle)).setText("Sensors: Acl Gyr Loc Mic");
		//((TextView) rootView.findViewById(R.id.fragment_app_detail_status)).setText("BASE");
		// }}}

//		// figure out which "types" of sensor are present on this device {{{
//		ArrayList<Sensor> sensors = new ArrayList<Sensor>(sensorMgr.getSensorList(Sensor.TYPE_ALL));
//		ArrayList<Integer> sensorTypes = new ArrayList<Integer>();
//		for (int sIdx = 0; sIdx < sensors.size(); sIdx++) {
//			int sType = sensors.get(sIdx).getType(); //SensorType.defineFromAndroid(sensors.get(sIdx).getType());
//
//			if (sensorTypes.contains(sType) == false) {
//				sensorTypes.add(sType);
//			}
//		}
//		// }}}
		
		ArrayList<SensorType> sensorTypes = app.getSensorsUsed();
		
		// for each sensor type...
		for (int sTypeIdx = 0; sTypeIdx < sensorTypes.size(); sTypeIdx++) {
			// create a rule for this sensor type
			SensorTypeRule rule = new SensorTypeRule(sensorTypes.get(sTypeIdx));
			
			//prepare a View for editing of the rule for this sensor
			ViewGroup ruleView = (ViewGroup) sensorInflater.inflate(R.layout.fragment_app_detail_sensor, sensorViews, false);

			rule.setView(ruleView);

			rules.add(rule);
			sensorViews.addView(ruleView);
		}

		((Button) rootView.findViewById(R.id.fragment_app_detail_apply_button)).setOnClickListener(new View.OnClickListener () {
			public void onClick (View v) {
				FirewallConfigMessage.FirewallConfig.Builder fwBuilder = FirewallConfigMessage.FirewallConfig.newBuilder();
				
				for (int sTypeIdx = 0; sTypeIdx < rules.size(); sTypeIdx++) {
					FirewallConfigMessage.Rule curRule = rules.get(sTypeIdx).genRule();
					fwBuilder.addRule(curRule);
				}

				FirewallConfigMessage.FirewallConfig fwConfig = fwBuilder.build();

				String serializedFirewallConfigProto = Base64.encodeToString(fwConfig.toByteArray(), Base64.DEFAULT);

				FirewallConfigManager fwMgr = (FirewallConfigManager) getActivity().getSystemService(Context.FIREWALLCONFIG_SERVICE);
				fwMgr.setFirewallConfig(serializedFirewallConfigProto);

				getActivity().finish();
			}
		});

		return rootView;
	}

	// }}}
}
