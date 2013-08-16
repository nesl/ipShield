package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

public class Inference {
	private int id; // the inferences ID in the database
	private String name;

	public Inference (int newid) {
		this.id = newid;
	}

	public String toString () {
		return "Inference #" + Integer.toString(id);
	}
}
