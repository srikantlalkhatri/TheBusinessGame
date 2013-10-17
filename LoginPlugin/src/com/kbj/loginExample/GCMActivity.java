package com.kbj.loginExample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class GCMActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String msg = this.getIntent().getStringExtra("message");
		if(msg != null){

	        new AlertDialog.Builder(this)
	        .setTitle("New Notification")
	        .setMessage(msg)
	        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	            }
	        }).create().show();
	    }  
		finish();
	}
}


