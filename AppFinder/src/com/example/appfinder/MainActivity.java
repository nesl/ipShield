package com.example.appfinder;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.content.pm.*;

public class MainActivity extends Activity {

	private static final String nl = System.getProperty("line.separator");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView theContent = (TextView) findViewById(R.id.the_content);
		theContent.setScrollContainer(true);
		theContent.setMovementMethod(new ScrollingMovementMethod());

		theContent.append("LIST OF INSTALLED APPLICATIONS:" + nl + nl);

		List<ApplicationInfo> installedApps = getPackageManager().getInstalledApplications(0);

		for (int appIdx = 0; appIdx < installedApps.size(); appIdx++) {
			ApplicationInfo app = installedApps.get(appIdx);
			theContent.append(app.processName + nl + "\tRuns with UID: " + Integer.toString(app.uid));

			if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
				theContent.append(nl + "\tThis is a system process.");
			} else {
				theContent.append(nl + "\tThis is a user-installed process.");
			}

			theContent.append(nl + nl);
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
