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
		//((TextView) view.findViewById(R.id.app_list_entry_subtitle)).setText("Sensors: Acl Gyr Loc Mic");
		//((TextView) view.findViewById(R.id.app_list_entry_status)).setText("BASE");

		return view;
	}
}
