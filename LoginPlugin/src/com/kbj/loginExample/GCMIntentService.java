package com.kbj.loginExample; //Edit this to match the name of your application

import com.google.android.gcm.*;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;



public class GCMIntentService extends GCMBaseIntentService {

	public static final String ME = "GCMReceiver";

	public GCMIntentService() {
		super("GCMIntentService");
	}

	private static final String TAG = "GCMIntentService";
	
	@Override
	public void onRegistered(Context context, String regId) {

		Log.v(ME + ":onRegistered", "Registration ID arrived!");
		Log.v(ME + ":onRegistered", regId);

		JSONObject json = null;

		try {
			json = new JSONObject();
			json.put("event", "registered");
			json.put("regid", regId);
			Log.v(ME + ":onRegisterd", json.toString());
			LoginPluginWS.sendJavascript(json);

		} catch (JSONException e) {
			// No message to the user is sent, JSON failed
			Log.e(ME + ":onRegisterd", "JSON exception");
		}

	}

	@Override
	public void onUnregistered(Context context, String regId) {
		Log.d(TAG, "onUnregistered - regId: " + regId);
	}

	@Override
	protected void onMessage(final Context arg0, final Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("Registration", "Got a message!");
		Log.i("Registration", arg1.getStringExtra("message"));
		String message = arg1.getStringExtra("message");

		generateNotification(getApplicationContext(), message);
	}
	
	private static void generateNotification(Context context, String message) {
	    long when = System.currentTimeMillis();
	    NotificationManager notificationManager = (NotificationManager)
	            context.getSystemService(Context.NOTIFICATION_SERVICE);
	    Notification notification = new Notification(R.drawable.tbgicon, message, when);
	    Intent notificationIntent = new Intent(context, LoginPlugin.class);
	    notificationIntent.putExtra("message", message);
	    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	            Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    String title = "The Business Game";
	    PendingIntent intent =
	            PendingIntent.getActivity(context, 0, notificationIntent, 0);
	    notification.setLatestEventInfo(context, title, message, intent);
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    notification.defaults|= Notification.DEFAULT_LIGHTS;
	    notification.defaults|= Notification.DEFAULT_VIBRATE;
	    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
	    notificationManager.notify(0, notification);
	}


	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "onError - errorId: " + errorId);
	}
	
	

}
