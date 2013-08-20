package edu.ucla.ee.nesl;

import com.google.protobuf.InvalidProtocolBufferException;

import edu.ucla.ee.nesl.FirewallConfigMessages.Action;
import edu.ucla.ee.nesl.FirewallConfigMessages.DateTime;
import edu.ucla.ee.nesl.FirewallConfigMessages.FirewallConfig;
import edu.ucla.ee.nesl.FirewallConfigMessages.Param;
import edu.ucla.ee.nesl.FirewallConfigMessages.Perturb;
import edu.ucla.ee.nesl.FirewallConfigMessages.Rule;
import edu.ucla.ee.nesl.FirewallConfigMessages.SensorValue;
import edu.ucla.ee.nesl.FirewallConfigMessages.VectorValue;
import edu.ucla.ee.nesl.FirewallConfigMessages.Perturb.DistributionType;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.os.FirewallConfigManager;

public class MainActivity extends Activity {

	public final String TAG = "MainActivity";
	public enum weekday {
		SUNDAY(0), MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6);
		
		private int value;
		weekday(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FirewallConfigManager firewallManager = (FirewallConfigManager)getSystemService(Context.FIREWALLCONFIG_SERVICE);
		String config = getConfig();
		TextView t = new TextView(this);
		t=(TextView)findViewById(R.id.configText);
		t.setText(config);
		
		firewallManager.setFirewallConfig(getConfig());
		readConfig(config);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// read the rule back
	public void readConfig(String serializedRule) {
		FirewallConfig firewallConfig = null;
        try {
        	byte[] byteArr = Base64.decode(serializedRule, Base64.DEFAULT);
            //ByteString byteString = ByteString.copyFromUtf8(serializedRule);
            //firewallConfig = FirewallConfig.parseFrom(byteString);
            firewallConfig = FirewallConfig.parseFrom(byteArr);
        } catch (InvalidProtocolBufferException ex) {
            Log.e(TAG, "InvalidProtocolBufferException");
        }   
        
        if(firewallConfig != null) {
        	Log.d(TAG, "Writing the Firewall Config File");
        	for(Rule rule: firewallConfig.getRuleList()) {
        		Log.d(TAG, "ruleName = " + rule.getRuleName() + ": sensorType = " + rule.getSensorType() + ": pkgName = " + rule.getPkgName() + ": pkgUid = " + rule.getPkgUid());
        	}   
        }
	}
	
    public FirewallConfig testSuppression(String pkgName, int uid, int sensorType, String ruleName, 
    		int weekDay, int fromHr, int fromMin, int toHr, int toMin ) {
    	boolean toAddDate = false;
    	FirewallConfig.Builder configBuilder = FirewallConfig.newBuilder();
    	Rule.Builder rule = Rule.newBuilder();
    	rule.setRuleName(ruleName);
    	rule.setPkgName(pkgName);
    	rule.setSensorType(sensorType);
    	rule.setPkgUid(uid);
    	
    	Action.Builder action = Action.newBuilder();
    	action.setActionType(Action.ActionType.ACTION_SUPPRESS);
    	
    	DateTime.Builder dateTime = DateTime.newBuilder();
    	if(weekDay != -1) {
    		toAddDate = true;
    		dateTime.addDayOfWeek(weekDay);
    	}
    	if((fromHr != -1) && (toHr != -1)) {
    		toAddDate = true;
    		dateTime.setFromHr(fromHr);
    		if(fromMin != -1)
    			dateTime.setFromMin(fromMin);
    		
    		dateTime.setToHr(toHr);
    		if(toMin != -1)
    			dateTime.setToMin(toMin);
    	}
    	if(toAddDate) {
    		rule.setDateTime(dateTime.build());
    	}
    	rule.setAction(action.build());
		configBuilder.addRule(rule.build());
    	return configBuilder.build();
    }
    
    public FirewallConfig testPassThrough(String pkgName, int uid, int sensorType, String ruleName) {
    	
    	FirewallConfig.Builder configBuilder = FirewallConfig.newBuilder();
    	Rule.Builder rule = Rule.newBuilder();
    	rule.setRuleName(ruleName);
    	rule.setPkgName(pkgName);
    	rule.setSensorType(sensorType);
    	rule.setPkgUid(uid);
    	
    	Action.Builder action = Action.newBuilder();
    	action.setActionType(Action.ActionType.ACTION_PASSTHROUGH);
    	rule.setAction(action.build());
		configBuilder.addRule(rule.build());
    	return configBuilder.build();
    	
    }
    // hard code the rule here.
    public String getConfig() {   	
    	byte[] bs;
    	
		
	/*	
		rule1.setSensorType(Sensor.TYPE_LIGHT);
		rule1.setPkgName("imoblife.androidsensorbox");
		//rule1.setPkgName("com.authorwjf");
		rule1.setPkgUid(10043);
		//rule1.setPkgUid(10046);
		
		action1.setActionType(Action.ActionType.ACTION_SUPPRESS);
	
		// TestCase1: Testing the setting of vector values - pass
		VectorValue.Builder vectorValue1 = VectorValue.newBuilder();
		vectorValue1.setX(1);
		vectorValue1.setY(2);
		vectorValue1.setZ(5);
		SensorValue.Builder sensorValue1 = SensorValue.newBuilder();
		sensorValue1.setVecVal(vectorValue1.build());
	*/
		
	/*	
		//TestCase 2: Testing if setting default works for all axis - pass
		SensorValue.Builder sensorValue1 = SensorValue.newBuilder();
		sensorValue1.setDefaultVal(2);
	*/	
	
/*
	    //TestCase 3: Testing for scalar Valued sensors
		SensorValue.Builder sensorValue1 = SensorValue.newBuilder();
		sensorValue1.setScalarVal(54); // change sensor type to light above
		Perturb.Builder perturb1 = Perturb.newBuilder();
		perturb1.setDistType(DistributionType.GAUSSIAN);
		perturb1.setMean(1);
		perturb1.setVariance(3);
	     
		Param.Builder param1 = Param.newBuilder();
		//param1.setConstantValue(sensorValue1.build());
		param1.setPerturb(perturb1.build());
		action1.setParam(param1.build());
*/

		//SensorValue.Builder sensorValue = SensorValue.newBuilder();
		//SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		/*
		sensorValue.setScalarVal(lightSensor.getMaximumRange());
		Param.Builder param1 = Param.newBuilder();
		param1.setConstantValue(sensorValue.build());
		action1.setParam(param1.build());
		
		rule1.setAction(action1.build());
		configBuilder.addRule(rule1.build());
		
		Rule.Builder rule2 = Rule.newBuilder();
		rule2.setRuleName("Rule2");
		//rule2.setSensorType(Sensor.TYPE_ACCELEROMETER);
		//rule2.setSensorType(Sensor.TYPE_LIGHT);
		//rule2.setSensorType(Sensor.TYPE_ORIENTATION);
		//rule2.setSensorType(Sensor.TYPE_PROXIMITY);
		//rule2.setSensorType(Sensor.TYPE_GYROSCOPE);
		rule2.setSensorType(Sensor.TYPE_MAGNETIC_FIELD);
		rule2.setPkgName("imoblife.androidsensorbox");
		rule2.setPkgUid(10043);
		Action.Builder action2 = Action.newBuilder();
		action2.setActionType(Action.ActionType.ACTION_SUPPRESS);
		Param.Builder param2 = Param.newBuilder();
		//param2.setConstantValue((float)2);
		action2.setParam(param2.build());
		rule2.setAction(action2.build());
		configBuilder.addRule(rule2.build());

		Rule.Builder rule3 = Rule.newBuilder();
		rule3.setRuleName("Rule3");
		rule3.setSensorType(3);
		rule3.setPkgName("com.google");
		rule3.setPkgUid(100045);
		Action.Builder action3 = Action.newBuilder();
		action3.setActionType(Action.ActionType.ACTION_SUPPRESS);
		rule3.setAction(action3.build());
		configBuilder.addRule(rule3.build());*/
		
    	// Default values for time fields is set to -1
    	// Get the pkgName and UID from /data/system/package.xml on the phone
		FirewallConfig firewallConfig1 = testSuppression("imoblife.androidsensorbox", 10043, Sensor.TYPE_ACCELEROMETER, "Rule1", -1, -1, -1, -1, -1);
    	FirewallConfig firewallConfig2 = testPassThrough("imoblife.androidsensorbox", 10043, Sensor.TYPE_ACCELEROMETER, "Rule2");
		bs = firewallConfig2.toByteArray();
		String str = Base64.encodeToString(bs, Base64.DEFAULT);
		Log.d("In getConfig", str);
		return str;
		
    }
	
}
