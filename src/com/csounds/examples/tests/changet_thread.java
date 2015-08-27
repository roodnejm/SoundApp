package com.csounds.examples.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.csounds.CsoundObj;
import com.csounds.examples.R;

import csnd6.Csound;
import csnd6.CsoundArgVList;
import csnd6.CsoundMYFLTArray;
import csnd6.SWIGTYPE_p_CSOUND_;
import csnd6.SWIGTYPE_p_p_float;
import csnd6.SWIGTYPE_p_void;
import csnd6.csnd;

public class changet_thread extends Thread {
    SWIGTYPE_p_void myvoid;
    SWIGTYPE_p_CSOUND_ mycsound = csnd.csoundCreate(myvoid);
    Csound csound = new Csound();
    CsoundArgVList args = new CsoundArgVList();
    CsoundMYFLTArray myfltarray = new CsoundMYFLTArray();

    boolean on = false;
    boolean pause = false;

    double myvalue;
	private String csd;
	private CsoundObj csoundObj;
	private float percentage;

    changet_thread(double someValue, String csd, CsoundObj csoundObj, float percentage) {
        myvalue = someValue;
        this.csd = csd;
        this.csoundObj = csoundObj;
        this.percentage = percentage;
        run();
    } //ends constructor

    public void run() {
        
    	
    
        try {
        	Csound csound = csoundObj.getCsound();
			
			System.out.println("freq " + percentage);
			 while (csound.PerformKsmps() == 0) {
		            // Update channel values
				 csound.SetChannel("freq", percentage/100);
		        }
        } catch (Exception e) {
        }

        //java.lang.System.err.println("Could not Perform...\n");
        //csound.Stop();
        //csound.Cleanup();
        csound.Reset();
        java.lang.System.exit(1);
    } //end run method


}//ends changet_Thread
