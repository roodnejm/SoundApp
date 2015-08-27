package com.csounds.examples.drumrack;

import com.csounds.examples.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class DrumMachineActivity extends FragmentActivity {
	
	@SuppressWarnings("unused")
	private static final String TAG = DrumMachineActivity.class.getSimpleName();
	
	private DrumMachine model;
	/* onCreate()
	 * initialize model, UI, fragment(s) 
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        model = new DrumMachine(this);
        
        LinearLayout drumMachineLayout 
       	= (LinearLayout) LayoutInflater.from(this).inflate(R.layout.drum_machine, null);
        setContentView(drumMachineLayout);
        
        ButtonGridView grid = (ButtonGridView) drumMachineLayout.findViewById(R.id.button_grid);
        grid.setDrumMachineModel(model);
        
        LcdScreenView lcd = (LcdScreenView) drumMachineLayout.findViewById(R.id.lcd_screen);
        new DrumMachineLcdUpdater(lcd.getModel());
        
        Button loadPresetButton = (Button)findViewById(R.id.loadpresetbutton);
        loadPresetButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
           	 FragmentManager fm = getSupportFragmentManager();
             FragmentTransaction ft = fm.beginTransaction();
             LoadPresetFragment f = (LoadPresetFragment) fm.findFragmentByTag("tag");

             if(f == null) {  // not added
                 f = new LoadPresetFragment();
                 ft.add(android.R.id.content, f, "tag");
                 ft.addToBackStack("loadfragment");
                 ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

             } else {  // already added

                 ft.remove(f);
                 ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
             }

             ft.commit();

        }
        });
    }
    
    /* setPreset() 
     * @parameters int presetNbr
     */
    public void setPreset(int presetNbr) {
    	if(presetNbr == 0)
    	model.initPreset(this,0);
    	if(presetNbr == 1)
    	model.initPreset(this,1);
    }
    
    

}