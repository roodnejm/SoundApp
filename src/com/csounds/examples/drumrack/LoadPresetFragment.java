package com.csounds.examples.drumrack;

import com.csounds.examples.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LoadPresetFragment extends ListFragment {
	   
	Integer[] imageId = {
		      R.drawable.drumrack,
		      R.drawable.dubstep

		 
		  };
	  String[] numbers_text = new String[] { "DrumRack", "DubStep Rack", "three", "four",  
	    "five", "six", "seven", "eight", "nine", "ten", "eleven",  
	    "twelve", "thirteen", "fourteen", "fifteen" };  
	  String[] numbers_digits = new String[] { "0", "1", "2", "3", "4", "5", "6",  
	    "7", "8", "9", "10", "11", "12", "13", "14" };  
	  //set preset from fragment to drummachineactivity
	  @Override  
	  public void onListItemClick(ListView l, View v, int position, long id) {  
		  Toast toast = Toast.makeText(getActivity(), getActivity() + " "+ numbers_digits[(int) id], 1);
		  toast.show();    
// pass the id of the selected preset to the activity
		  System.out.println("test" +(int)id);
				    ((DrumMachineActivity)getActivity()).setPreset((int)id);

	  }
	  
	  @Override  
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	    Bundle savedInstanceState) {  
	   ArrayAdapter<String> adapter = new ArrayAdapter<String>(  
	     inflater.getContext(), android.R.layout.simple_list_item_1,  
	     numbers_text);  
	   setListAdapter(adapter);  
	   View view = super.onCreateView(inflater, container, savedInstanceState); 
	   view.setBackgroundResource(com.csounds.examples.R.color.white);
	   return  view;
	  } 
	  
	  
	  
}