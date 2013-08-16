package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import android.util.Log;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

// This class is intended to hold just enough information to universally uniquely
// identify a package--i.e., FACEBOOK or YOUTUBE or "Developer Sally's Hot App"
// AND make that information serializable for use in bundling
// ...and potentially for later use in querying a cloud database

// AFAIK currently Android uses package names universally uniquely---as it should
// so this class is somewhat redundant.  It is largely here for modularity, so that
// we can change the way we identify apps later, in case, e.g.:
//	- an app changes its package name and we want to track it
//	- an app forks into multiple versions
//	- some other weird shit happens

// the SERIEALIZED version of an a AppId takes the form of the string generated
// from its generateUniqueString() method

public class AppId {
	private String packageName = "";
	
	// construct a AppId based just on an ApplicationInfo object
	public AppId(ApplicationInfo appInfo) {
		this.packageName = appInfo.packageName;
	}

	// construct a AppId based on one of our unique string identifiers
	public AppId(String uniqueString) {
		packageName = uniqueString;
	}

	public String generateUniqueString() {
		return packageName;
	}

	public ApplicationInfo getApplicationInfo(PackageManager pm, int flags) {
		ApplicationInfo toReturn = null;
		try {
			toReturn = pm.getApplicationInfo(packageName, flags);
		} catch (NameNotFoundException e) {
			Log.e(getClass().toString(), "Somebody tried to get the application info for a package that does not exist", e);
			e.printStackTrace();
		}
		
		return toReturn;
	}
}
