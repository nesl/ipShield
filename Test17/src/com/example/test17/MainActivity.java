package com.example.test17;

import com.google.protobuf.InvalidProtocolBufferException;

import com.example.test17.FirewallConfigMessage.Action;
import com.example.test17.FirewallConfigMessage.FirewallConfig;
import com.example.test17.FirewallConfigMessage.Param;
import com.example.test17.FirewallConfigMessage.Rule;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.os.FirewallConfigManager;

public class MainActivity extends Activity {

	public final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FirewallConfigManager firewallManager = (FirewallConfigManager)getSystemService(Context.FIREWALLCONFIG_SERVICE);
		String config = getConfig();
		TextView t = new TextView(this);
		//t=(TextView)findViewById(R.id.configText);
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
        		Log.d(TAG, "Action = " + rule.getAction().getActionType() + ":params: const = " + rule.getAction().getParam().getConstantValue() + ": delay = " + rule.getAction().getParam().getDelay() + ": mean = " + rule.getAction().getParam().getPerturb().getMean());
        	}   
        }
	}
    
    // hard code the rule here.
    public String getConfig() {   	
    	byte[] bs;
		FirewallConfig.Builder configBuilder = FirewallConfig.newBuilder();
		
		Rule.Builder rule1 = Rule.newBuilder();
		rule1.setRuleName("Rule1");
		rule1.setSensorType(Sensor.TYPE_ACCELEROMETER);
		rule1.setPkgName("imoblife.androidsensorbox");
		rule1.setPkgUid(10043);
		Action.Builder action1 = Action.newBuilder();
		action1.setActionType(Action.ActionType.ACTION_PASSTHROUGH);
		Param.Builder param1 = Param.newBuilder();
		//param1.setConstantValue((float)1.5);
		action1.setParam(param1.build());
		rule1.setAction(action1.build());
		configBuilder.addRule(rule1.build());
		
		/*
		Rule.Builder rule2 = Rule.newBuilder();
		rule2.setRuleName("Rule2");
		rule2.setSensorType(2);
		rule2.setPkgName("com.twitter");
		rule2.setPkgUid(100035);
		Action.Builder action2 = Action.newBuilder();
		action2.setActionType(Action.ActionType.ACTION_SUPPRESS);
		rule2.setAction(action2.build());
		configBuilder.addRule(rule2.build());
		*/
		
		FirewallConfig firewallConfig = configBuilder.build();
		bs = firewallConfig.toByteArray();
		String str = Base64.encodeToString(bs, Base64.DEFAULT);
		Log.d("In getConfig", str);
		return str;
		
    }
	
}
