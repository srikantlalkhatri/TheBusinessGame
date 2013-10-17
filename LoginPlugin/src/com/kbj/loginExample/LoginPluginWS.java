package com.kbj.loginExample;

import java.io.IOException;
import java.util.HashMap;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;

import org.apache.cordova.api.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class LoginPluginWS extends CordovaPlugin {

	public static final String TAG = "LoginPlugin";

	private String NAMESPACE = "http://ws.kbj.com";
	//private static final String URL = "http://10.0.2.2:8085/KBJwebService/services/Kbj_WebService?wsdl";
	private String URL = "http://5.1.113.121:8080/KBJwebService/services/Kbj_WebService?wsdl";
	private String SOAP_ACTION = "http://ws.kbj.com/authentication";
	private String METHOD_NAME = "authentication";
	
	private String SOAP_ACTION_logout = "http://ws.kbj.com/logout";
	private String METHOD_NAME_logout = "logout";
	
	String UN = null;
	String PW = null;  
  
	boolean executeStatus = false;
	String requestUserID = null; 
	String requestPassword = null;  
	String senderID = null;

	boolean loginStatus = false;    
	String regID = null;
	String userID = null;   
	String userName = null;
	String requestdeviceInfo = null;
	static String gcmRegId = null;
	String loginAction = "loginAction";
	JSONObject loginJsonResponse = null;
	static JSONObject gcmIntentJsonReturn = null;
	
	boolean logoutStatus = false;
	JSONObject logoutJsonResponse = null;
	public static final String ME = "LoginPlugin";
	Context contex;
	public static final String REGISTER = "register";
	public static final String UNREGISTER = "unregister";

	public static CordovaPlugin gwebView;
	private static String gECB;
	private static String gSenderID;

	ConnectionDetector cd;
	boolean isInternetPresent = false;
	boolean isEmail = false;

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) {
		try {
			System.out.println("LoginPlugin execute:");

			final PluginResult.Status status = PluginResult.Status.OK;
			

			System.out.println(args);

			if (loginAction.equals(action)) {

				JSONObject obj2 = (JSONObject) args.get(0);

				requestUserID = obj2.getString("username");
				requestPassword = obj2.getString("password");
				gcmRegId = obj2.getString("gcmregid");
				requestdeviceInfo = obj2.getString("deviceInfo");
				System.out.println("Login Credentials : " + args.toString());
				LOG.v("Login Credentials : ", args.toString());

				new LoginActionForWS() {
					@Override
					public void onPostExecute(Boolean result) {
						// boolean networkStatus = isConnectingToInternet();
						// if(networkStatus){
						if (result == true) {
							boolean flag = deviceHasGoogleAccount();
							if (flag) {
								// boolean networkStatus =
								// isConnectingToInternet();
								// if (networkStatus) {
								cordova.getActivity().runOnUiThread(
										new Runnable() {
											public void run() {
												Toast myToast = Toast
														.makeText(
																cordova.getActivity()
																		.getApplicationContext(),
																"Success",
																Toast.LENGTH_SHORT);
												myToast.show();

											}

										});
								try {
									loginJsonResponse = new JSONObject();
									loginJsonResponse.put("loginStatus",
											loginStatus);
									loginJsonResponse.put("userID", userID);
									loginJsonResponse.put("userName", userName);
									loginJsonResponse.put("regID", regID);
									System.out.println("loginJsonResponse : "
											+ loginJsonResponse);
									callbackContext
											.sendPluginResult(new PluginResult(
													status, loginJsonResponse));

								} catch (JSONException jsonException) {

									callbackContext
											.sendPluginResult(new PluginResult(
													PluginResult.Status.JSON_EXCEPTION,
													" Login Cre Json Fault "));

								}
							} else {
								Toast myToast = Toast.makeText(cordova
										.getActivity().getApplicationContext(),
										"Login Failed, please add gmail account\n"
												+ "in your device",
										Toast.LENGTH_SHORT);
								myToast.show();
							}
							/*
							 * } else { Toast myToast = Toast.makeText(cordova
							 * .getActivity().getApplicationContext(),
							 * "Login Failed: No internet connection",
							 * Toast.LENGTH_SHORT); myToast.show(); }
							 */
						}

						else {
							cordova.getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast myToast = Toast
											.makeText(
													cordova.getActivity()
															.getApplicationContext(),
													"Login Failed: invalid Username/Password",
													Toast.LENGTH_SHORT);
									myToast.show();
									callbackContext
											.sendPluginResult(new PluginResult(
													PluginResult.Status.JSON_EXCEPTION,
													" Login Failed - wrong cre "));

								}
							});

						}
						// }
						/*
						 * else { Toast myToast = Toast.makeText(cordova
						 * .getActivity() .getApplicationContext(),
						 * "Login Failed: No internet connection",
						 * Toast.LENGTH_SHORT); myToast.show();
						 * 
						 * }
						 */}

				}.execute(requestUserID, requestPassword, gcmRegId, requestdeviceInfo);

			} else if (action.equals("loadUrl")) {
				String url = null;
				JSONObject data = new JSONObject();
				System.out.println("load url data : " + args);
				JSONObject obj3 = (JSONObject) args.get(0);

				url = obj3.getString("url");
				data = obj3.getJSONObject("data");
				System.out.println("URL to load" + url);
				System.out.println("data : " + data);

				// System.out.println("json object:" + obj4);

				this.loadUrl(url, data);
			} else if (REGISTER.equals(action)) {

				Log.v(ME + ":execute", "data=" + args.toString());

				try {

					JSONObject jo = new JSONObject(args.toString().substring(1,
							args.toString().length() - 1));

					gwebView = this;

					Log.v(ME + ":execute", "jo=" + jo.toString());

					gECB = (String) jo.get("ecb");
					gSenderID = (String) jo.get("senderID");

					Log.v(ME + ":execute", "ECB=" + gECB + " senderID="
							+ gSenderID);

					new gcmRegisterForWS() {
						@Override
						public void onPostExecute(JSONObject result) {
							Log.v(ME + ":execute",
									"GCMRegistrar.register called " + result);
							callbackContext.sendPluginResult(new PluginResult(
									status, result));
						}
					}.execute(gSenderID);

					// result = new PluginResult(Status.OK);
				} catch (JSONException e) {
					Log.e(ME, "Got JSON Exception " + e.getMessage());
					callbackContext.sendPluginResult(new PluginResult(
							PluginResult.Status.JSON_EXCEPTION));
					// result = new PluginResult(Status.JSON_EXCEPTION);
				} 
			} else if (UNREGISTER.equals(action)) {

				GCMRegistrar.unregister(cordova.getActivity()
						.getApplicationContext());
				Log.v(ME + ":" + UNREGISTER, "GCMRegistrar.unregister called ");

			} else if ("logoutAction".equals(action)) {
				JSONObject logoutobj = (JSONObject) args.get(0);

				String currPlayerRegid  = logoutobj.getString("regID");
				System.out.println("currPlayerRegid :" +currPlayerRegid);
				new LogoutActionForWS() {

					protected void onPostExecute(Boolean result) {
						if (result == true) {
							cordova.getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast myToast = Toast.makeText(cordova
											.getActivity()
											.getApplicationContext(),
											"Successfully Logged out",
											Toast.LENGTH_SHORT);
									myToast.show();
								}
							});
							try {
								logoutJsonResponse = new JSONObject();
								logoutJsonResponse.put("logoutStatus",
										logoutStatus);
								callbackContext
								.sendPluginResult(new PluginResult(
										status,
										logoutJsonResponse));
							} catch (JSONException e) {
								System.out.println(e);
							}

						} else {
							System.out.println("Failed plugin");
						}
					};
				}.execute(currPlayerRegid);

			} else {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.INVALID_ACTION));
				// result = new PluginResult(Status.INVALID_ACTION);
				Log.e(ME, "Invalid action : " + action);
			}

			return true;
		} catch (JSONException e) {
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.JSON_EXCEPTION));
			return false;
		}

	}

	/************************* Checking gmail account in devices **************/

	private boolean deviceHasGoogleAccount() {
		AccountManager accMan = AccountManager.get(cordova.getActivity()
				.getApplicationContext());
		Account[] accArray = accMan.getAccountsByType("com.google");
		return accArray.length >= 1 ? true : false;
	}

	/*	*//************ Checking Internet connection ****************/
	/*
	 * 
	 * public boolean isConnectingToInternet() { Context context = null;
	 * ConnectivityManager connectivity = (ConnectivityManager) context
	 * .getSystemService(Context.CONNECTIVITY_SERVICE); if (connectivity !=
	 * null) { NetworkInfo[] info = connectivity.getAllNetworkInfo(); if (info
	 * != null) for (int i = 0; i < info.length; i++) if (info[i].getState() ==
	 * NetworkInfo.State.CONNECTED) { return true; }
	 * 
	 * } return false; }
	 */

	// @SuppressWarnings("deprecation")
	public static void sendJavascript(JSONObject _json) throws JSONException {
		gcmIntentJsonReturn = new JSONObject();

		// gcmIntentJsonReturn.put("gECB", gECB);
		// gcmIntentJsonReturn.put("gECBValue", _json);

		String _d = "javascript:" + gECB + "(" + _json.toString() + ")";
		Log.v(ME + ":sendJavascript", _d);

		if (gECB != null) {
			gcmIntentJsonReturn = _json;
			Log.v(ME + ":sendJavascript", gcmIntentJsonReturn.toString());
			// ((Plugin) gwebView).sendJavascript( _d );
		}
	}

	private class LoginActionForWS extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			String user_Name = params[0].toString();
			String user_Password = params[1].toString();
			String gcmid = params[2].toString();
			String deviceinfo = params[3].toString();
			System.out.println(user_Name + " " + user_Password);
			// Pass value for userName variable of the web service
			PropertyInfo unameProp = new PropertyInfo();
			unameProp.setName("userID");
			unameProp.setValue(user_Name);// set value for userName variable
			unameProp.setType(String.class);// Define the type of the variable
			request.addProperty(unameProp);// Pass properties to the variable

			// Pass value for Password variable of the web service
			PropertyInfo passwordProp = new PropertyInfo();
			passwordProp.setName("password");
			passwordProp.setValue(user_Password);
			passwordProp.setType(String.class);
			request.addProperty(passwordProp);

			// Pass value for gcm reg id variable of the web service
			PropertyInfo gcmRegIdProp = new PropertyInfo();
			gcmRegIdProp.setName("gcmRegId");
			gcmRegIdProp.setValue(gcmid);
			gcmRegIdProp.setType(String.class);
			request.addProperty(gcmRegIdProp);

			// Pass value for gcm reg id variable of the web service
						PropertyInfo deviceinfoProp = new PropertyInfo();
						deviceinfoProp.setName("deviceName");
						deviceinfoProp.setValue(deviceinfo);
						deviceinfoProp.setType(String.class);
						request.addProperty(deviceinfoProp);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			System.out.println(" soap envelope " + envelope);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		
				try {
					androidHttpTransport.call(SOAP_ACTION, envelope);
				/*} catch (IOException e) {
					System.out.println("[LoginPlugin: LoginActionForWS]IOException");
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					System.out.println("[LoginPlugin: LoginActionForWS]XmlPullParserException");
					e.printStackTrace();
				}*/
				SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
				
				
				/*Object resultsRequestSOAP = null;
				try {
					resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
				} catch (SoapFault e) {
					
					e.printStackTrace();
				}*/

				System.out.println("SoapPrimitive:" + resultsRequestSOAP);
				String str = resultsRequestSOAP.toString();
				String arr[] = str.split(",");
				System.out.println("Response Array:" + arr);

				loginStatus = Boolean.parseBoolean(arr[0].toString());
				System.out.println("return login status : " + loginStatus);
				regID = arr[1].toString();
				userID = arr[2].toString();
				userName = arr[3].toString();
				}catch(Exception ex){
					ex.printStackTrace();
				}

			

			return loginStatus;
		}

		/*
		 * @Override protected void onPostExecute(String status) {}
		 * 
		 * @Override protected void onPreExecute() {}
		 * 
		 * @Override protected void onProgressUpdate(Void... values) {}
		 */
	}

	/**
	 * Load the url into the webview.
	 * 
	 * @param url
	 * @param props
	 *            Properties that can be passed in to the DroidGap activity
	 *            (i.e. loadingDialog, wait, ...)
	 * @throws JSONException
	 */
	public void loadUrl(String url, JSONObject props) throws JSONException {
		LOG.d("App", "App.loadUrl(" + url + "," + props + ")");
		int wait = 0;
		boolean openExternal = false;
		boolean clearHistory = false;

		// If there are properties, then set them on the Activity
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (props != null) {
			JSONArray keys = props.names();
			for (int i = 0; i < keys.length(); i++) {
				String key = keys.getString(i);
				if (key.equals("wait")) {
					wait = props.getInt(key);
				} else if (key.equalsIgnoreCase("openexternal")) {
					openExternal = props.getBoolean(key);
					System.out.println("openExternal : " + openExternal);
				} else if (key.equalsIgnoreCase("clearhistory")) {
					clearHistory = props.getBoolean(key);
					System.out.println("clearhistory : " + clearHistory);
				} else {
					Object value = props.get(key);
					if (value == null) {

					} else if (value.getClass().equals(String.class)) {
						params.put(key, (String) value);
					} else if (value.getClass().equals(Boolean.class)) {
						params.put(key, (Boolean) value);
					} else if (value.getClass().equals(Integer.class)) {
						params.put(key, (Integer) value);
					}
				}
			}
		}

		// If wait property, then delay loading

		if (wait > 0) {
			try {
				synchronized (this) {
					this.wait(wait);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("parameter to next page : " + params);

		this.webView.showWebPage(url, openExternal, clearHistory, params);

	}

	/**
	 * Clear page history for the app.
	 */
	public void clearHistory() {
		this.webView.clearHistory();
	}

	private class gcmRegisterForWS extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject gcmStatus = new JSONObject();
			String senderID = params[0].toString();

			GCMRegistrar.register(
					cordova.getActivity().getApplicationContext(), senderID);

			if (gcmIntentJsonReturn == null) {
				while (gcmIntentJsonReturn == null) {
					if (gcmIntentJsonReturn != null) {
						System.out
								.println("[Login Plugin: gcmRegisterForWS()] gcmIntentJsonReturn"
										+ gcmIntentJsonReturn);
						gcmStatus = gcmIntentJsonReturn;
					}
				}
			} else {
				gcmStatus = gcmIntentJsonReturn;
			}

			System.out
					.println("[Login Plugin: gcmRegisterForWS()] gcmIntentJsonReturn"
							+ gcmIntentJsonReturn);
			gcmStatus = gcmIntentJsonReturn;
			System.out.println("Returned Json : " + gcmStatus);

			return gcmStatus;
		}

	}
	
	private class LogoutActionForWS extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
		
			System.out.println("[Invitation Plugin] Reg ID for Logout: "
					+ params);
			String regID = params[0].toString();
			System.out.println("Current player Reg ID : " + regID);
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_logout);

			PropertyInfo regIDProp = new PropertyInfo();
			regIDProp.setName("regID");
			regIDProp.setValue(regID);
			regIDProp.setType(String.class);
			request.addProperty(regIDProp);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);

			envelope.setOutputSoapObject(request);

			HttpTransportSE ht = new HttpTransportSE(URL);
			try {
				ht.call(SOAP_ACTION_logout, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				System.out.println("REsponse: " + response);
				logoutStatus = Boolean.parseBoolean(response.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return logoutStatus;
		}

	}

}

