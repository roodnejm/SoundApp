package com.csounds.examples.tests;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.csounds.examples.R;
import com.csounds.examples.drumrack.LoadPresetFragment;

public class ILoaderPresetActivity extends ListActivity {
	String[] numbers_text = new String[] { "violin", "sin osc", "saw tooth", "four",  
			"five", "six", "seven", "eight", "nine", "ten", "eleven",  
			"twelve", "thirteen", "fourteen", "fifteen" };  
	String[] numbers_digits = new String[] { "0", "1", "2", "3", "4", "5", "6",  
			"7", "8", "9", "10", "11", "12", "13", "14" };  
	//set preset from fragment to drummachineactivity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(  
				this, android.R.layout.simple_list_item_1,  
				numbers_text);  
		setListAdapter(adapter); 
	}
	@Override  
	public void onListItemClick(ListView l, View v, int position, long id) {  
		Toast toast = Toast.makeText(this, this + " "+ numbers_digits[(int) id], 1);
		toast.show();    
		// pass the id of the selected preset to the activity
		
		(this).setPreset((int)id);

	}




	public void setPreset(int presetNbr) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("presetNbr", presetNbr);
		// TODO Add extras or a data URI to this intent as appropriate.
		setResult(Activity.RESULT_OK, resultIntent);
		finish();

	}
}

