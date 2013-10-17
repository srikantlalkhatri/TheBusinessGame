gApp = new Array();
//gApp.deviceready = false;
gApp.gcmregid = '';

usersData = {
	userID : '',
	regID : '',
	userName : ''
};

invitationIDs = {
	userID1 : '',
	userID2 : '',
	userID3 : ''
};

/*
 * window.onbeforeunload = function(e) { handleLogout();
 * 
 * if (gApp.gcmregid.length > 0) { // The same routines are called for
 * success/fail on the unregister. You // can make them unique if you like
 * window.LoginPlugin.unregister(GCM_Success, GCM_Fail); // close the GCM }
 *  };
 */

var deviceInfo;
var localStorageFirstTime;

//document.addEventListener('deviceready', function() {

	//gApp.DeviceReady = true;
function afterDeviceReadyRecievedEvent(e){
	// if (!$.localStorage.getItem('kbjUserCredential')) {
	// console.log('dynamic');
	// localStorageFirstTime = 0;

	var retGCMId;
	console.log("Device is ready.");
	
	
	console.log("device info " + device.platform + " " + device.version + " "
			+ device.uuid + " " + device.name);
	deviceInfo = device.platform + "-" + device.version + "-" + device.uuid
			+ "-" + device.name;

	gcmCredential = {
		senderID : "700476530079",
		ecb : "GCM_Event"
	};

	window.plugins.LoginPluginWS.register(gcmCredential, GCM_Success, GCM_Fail); 

	
	
	// } else {
	// console.log('from cache');
	// var lsvalue = $.localStorage.getItem("key");
	// console.log("ls storage value : " + lsvalue);
	// usersData.regID = lsvalue.regID;
	// usersData.userID = lsvalue.userID;
	// usersData.userName = lsvalue.userName;
	// gApp.gcmregid = lsvalue.gcmregid;
	// console.log(usersData.regID + " " + usersData.userID + " " +
	// usersData.userName +" "+gApp.gcmregid);
	// documentReady();
	// invitationPageStart();
	// }
}
//}, false);

function GCM_Event(e) {

	// $("#app-status-ul").append('<li>EVENT -> RECEIVED:' + e.event + '</li>');

	switch (e.event) {
	case 'registered':
		// the definition of the e variable is json return defined in
		// GCMReceiver.java
		// In my case on registered I have EVENT and REGID defined
		gApp.gcmregid = e.regid;
		console.log("Reg ID in JS : " + gApp.gcmregid);

		if (gApp.gcmregid.length > 0) {

			//documentReady();
			// $("#app-status-ul").append(
			// '<li>REGISTERED -> REGID:' + e.regid + "</li>");

			// ==============================================================================
			// ==============================================================================
			// ==============================================================================
			//
			// This is where you would code to send the REGID to your server for
			// this device
			//
			// ==============================================================================
			// ==============================================================================
			// ==============================================================================

		}

		break;

	case 'message':

		if (e.message !== null) {
			alert("MESSAGE" + e.message);
			// alert();
		} else {
			alert("Failed");
		}
		// the definition of the e variable is json return defined in
		// GCMIntentService.java
		// In my case on registered I have EVENT, MSG and MSGCNT defined

		// You will NOT receive any messages unless you build a HOST server
		// application to send
		// Messages to you, This is just here to show you how it might work

		// $("#app-status-ul")
		// .append('<li>MESSAGE -> MSG: ' + e.message + '</li>');
		//          
		// $("#app-status-ul").append(
		// '<li>MESSAGE -> MSGCNT: ' + e.msgcnt + '</li>');

		break;

	case 'error':

		// $("#app-status-ul").append('<li>ERROR -> MSG:' + e.msg + '</li>');

		break;

	default:
		// alert('An unknown GCM event has occurred');
		// $("#app-status-ul")
		// .append(
		// '<li>EVENT -> Unknown, an event was received and we do not know what
		// it is</li>');

	}
}

function GCM_Success(e) {

	console.log("[Login JS] Success method : " + e);
	// $("#app-status-ul")
	// .append(
	// '<li>GCM_Success -> We have successfully registered and called the GCM
	// plugin, waiting for GCM_Event:registered -> REGID back from
	// Google</li>');
	GCM_Event(e);

}

function GCM_Fail(e) {
	// $("#app-status-ul").append(
	// '<li>GCM_Fail -> GCM plugin failed to register</li>');
	//
	// $("#app-status-ul").append('<li>GCM_Fail -> ' + e.msg + '</li>');

} 


function documentReady() {

	$(document).ready(function() {
		console.log("Document is ready");
		
		// Listener for Login Page

		// $("#loginButton").on("tap", handleLogin);
		//$("#loginButton").on("click", handleLogin);
		$('#loginButton').click(handleLogin);
  
	});
}


/*
 * ***********************CHECKING INTERNET
 * CONNECTION*****************************
 */

function checkConnection() {
	var networkState = navigator.connection.type;

	var states = {};
	states[Connection.UNKNOWN] = 'Unknown connection';
	states[Connection.ETHERNET] = 'Ethernet connection';
	states[Connection.WIFI] = 'WiFi connection';
	states[Connection.CELL_2G] = 'Cell 2G connection';
	states[Connection.CELL_3G] = 'Cell 3G connection';
	states[Connection.CELL_4G] = 'Cell 4G connection';
	states[Connection.NONE] = 'No network connection';
	if (navigator.connection.type == "none") {
		alert(states[networkState]);
		navigator.app.exitApp();
	}
}

/* ***********************LOGIN***************************** */
 
function handleLogin(event) {

	console.log("handleLogin");
	var form = $("#loginForm");
	// disable the button so we can't resubmit while we wait
	$("#loginButton", form).attr("disabled", "disabled");
	var u = $("#username", form).val();
	var p = $("#password", form).val();
	console.log("click");
	if (u != '' && p != '') {  
		loginCre = {
			username : u,
			password : p,
			gcmregid : gApp.gcmregid, 
			deviceInfo : deviceInfo
		};
		window.plugins.LoginPluginWS.loginAction(loginCre, LOGIN_Success,
				LOGIN_Fail);

	} else { 

		alert("You must enter a username and password");
		
	}
  
}

var loginCredentials;
function LOGIN_Success(result) {
	console.log("LOGIN_Success : " + result);
	if (result.loginStatus == true) {
		
		alert("Login Success:" + result.loginStatus + " " + result.userID);

	} else {
		LOGIN_Fail(result);
	}

}

function LOGIN_Fail(e) {
	console.log("LOGIN_Fail : " + e);
	$("#loginButton").removeAttr("disabled");
	alert("Login Failed:" + e.loginStatus);

	$.mobile.navigate("#loginPage");

}

