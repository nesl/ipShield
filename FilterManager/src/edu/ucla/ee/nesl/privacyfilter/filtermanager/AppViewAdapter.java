package edu.ucla.ee.nesl.privacyfilter.filtermanager;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppFilterData;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppId;

public class AppViewAdapter extends ArrayAdapter<AppFilterData> {

	Context context;

	public AppViewAdapter (Context baseContext, List<AppFilterData> apps) {
		super(baseContext, R.layout.app_list_entry, R.id.app_list_entry_title, apps);
		this.context = baseContext;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		AppFilterData app = getItem(position);

		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = inflater.inflate(R.layout.app_list_entry, null);
		}

		view.setTag(new AppId(app.getApplicationInfo()));
		((ImageView) view.findViewById(R.id.app_list_entry_icon)).setImageDrawable(app.getIcon());
		((TextView) view.findViewById(R.id.app_list_entry_title)).setText(app.toString());

		String usageMessage = "";

		int numSensorsUsed = app.getSensorsUsed().size();
		String sensorPlural = "s";
		if (numSensorsUsed == 1) {
			sensorPlural = "";
		}

		int numInferences = app.getInferences().size();
		String inferencePlural = "s";
		if (numInferences == 1) {
			inferencePlural = "";
		}

		if (numInferences > 0) {
			usageMessage += Integer.toString(numInferences) + " inference" + inferencePlural + " via ";
		}
		if (numSensorsUsed > 0) {
			usageMessage += Integer.toString(numSensorsUsed) + " sensor" + sensorPlural;
		}
		((TextView) view.findViewById(R.id.app_list_entry_subtitle)).setText(usageMessage);
		//((TextView) view.findViewById(R.id.app_list_entry_status)).setText("BASE");

		return view;
	}
}
