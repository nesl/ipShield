package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import android.database.*;
import android.database.sqlite.*;

import java.util.ArrayList; 

public class Inference {
	private int inferenceId; // the inference's ID in the database
	private String name;
	private String description;
	private ArrayList<InferenceMethod> methodsAvailable = null; // methods used to create this particular inference

	// methods is assumed to be a unique list (i.e. one obtained from our DB query which has the "DISTINCT" stipulation)
	public static ArrayList<Inference> getInferencesFromMethods (ArrayList<InferenceMethod> methods) { // {{{
		ArrayList<Inference> inferences = new ArrayList<Inference>();
		for (InferenceMethod method : methods) {
			Inference inference = new Inference(method.getInferenceId());

			Inference target = null;
			for (Inference cur : inferences) {
				if (inference.getInferenceId() == cur.getInferenceId()) {
					target = cur;
				}
			}

			if (target == null) {
				target = inference;
				inferences.add(target);
			}

			target.methodsAvailable.add(method);
		}

		return inferences;
	} // }}}

	private Inference (int inferenceId) { // {{{
		this.inferenceId = inferenceId;

		SQLiteDatabase db = SQLiteDatabase.openDatabase(AppFilterData.INFERENCE_DB_FILE, null, SQLiteDatabase.OPEN_READONLY);
		Cursor result = db.query("Inferences", new String[]{"name", "description"}, "inferenceID = ?", new String[]{Integer.toString(this.inferenceId)}, null, null, null, "1");
		result.moveToFirst();
		this.name = result.getString(0);
		this.description = result.getString(1);
		db.close();

		this.methodsAvailable = new ArrayList<InferenceMethod>();
	} // }}}

	public int getInferenceId () {
		return inferenceId;
	}
	public String getName () {
		return name;
	}
	public String getDescription () {
		return description;
	}

	public ArrayList<InferenceMethod> getInferenceMethods () {
		return methodsAvailable;
	}
}
