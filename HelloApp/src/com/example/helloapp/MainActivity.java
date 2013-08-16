package com.example.helloapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String EXTRA_MSG = "com.example.helloapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendMessage (View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText textBox = (EditText) findViewById(R.id.edit_message);
		String msg = textBox.getText().toString();
		intent.putExtra(EXTRA_MSG, msg);

		startActivity(intent);
	}
}
