(function() {

	// Plugin for the loginPage

	var LoginPluginWS = function() {
	};

	LoginPluginWS.prototype.loginAction = function(loginCre, successCallBack,
			errorCallBack) {
		console.log("Login Plugin to authenticate");
		return cordova.exec(successCallBack, // Callback which will be called
												// when directory listing is
												// successful
		errorCallBack, // Callback which will be called when directory listing
						// encounters an error
		'LoginPluginWS', // Telling Cordova that we want to run
						// "DirectoryListing" Plugin
		'loginAction', // Telling the plugin, which action we want to perform
		[ loginCre ]); // Passing a list of arguments to the plugin,

	};
	
	//Logout plugin
	LoginPluginWS.prototype.logoutAction = function(logoutCre, successCallBack,
			errorCallBack) {
		console.log("LogoutPlugin to logout");
		return cordova.exec(successCallBack,
		errorCallBack,
		'LoginPluginWS',
		'logoutAction',
		[ logoutCre ]); 

	};
	
	
	LoginPluginWS.prototype.loadUrl = function(dataToNextPage, errorCallBack) {
		console.log("Login Plugin to load url.");
		return cordova.exec(successCallBack, errorCallBack, 'LoginPluginWS',
				'loadUrl', [ dataToNextPage ]);
	};
	
	
	/**
	 * Register device with GCM service
	 * @param senderId - GCM service identifier
	 * @param eventCallback - {String} - Name of global window function that will handle incoming events from GCM
	 * @param successCallback - {Function} - called on success on registering device
	 * @param failureCallback - {Function} - called on failure on registering device
	 */
	LoginPluginWS.prototype.register = function(gcmCredential, successCallback, failureCallback) {
				
	  if ( typeof gcmCredential.ecb != "string") {   // The eventCallback has to be a STRING name not the actual routine like success/fail routines
	    var e = new Array();
	    e.msg = 'eventCallback must be a STRING name of the routine';
	    e.rc = -1;
	    failureCallback( e );
	    return;
	  }

	  return Cordova.exec(successCallback,      //Callback which will be called when directory listing is successful
	              failureCallback,       //Callback which will be called when directory listing encounters an error
	              'LoginPluginWS',        //Telling Cordova that we want to run "DirectoryListing" Plugin
	              'register',             //Telling the plugin, which action we want to perform
	              [gcmCredential]);          //Passing a list of arguments to the plugin,
	                          // The ecb variable is the STRING name of your javascript routine to be used for callbacks
	                          // You can add more to validate that eventCallback is a string and not an object
	};


	/**
	 * Un-Register device with GCM service
	 * @param successCallback - {Function} - called on success on un-registering device
	 * @param failureCallback - {Function} - called on failure on un-registering device
	 */
	LoginPluginWS.prototype.unregister = function( successCallback, failureCallback ) {

	    return cordova.exec(successCallback, failureCallback, 'LoginPluginWS', 'unregister');        
	};
	
	LoginPluginWS.prototype.message = function(messageData, successCallback, failureCallback ) {
		if ( typeof gcmCredential.ecb != "string") {  
			var e = new Array();
		    e.msg = 'eventCallback must be a STRING name of the routine';
		    e.rc = -1;
		    failureCallback( e );
		    return;
		  }
	    return cordova.exec(successCallback, failureCallback, 'LoginPluginWS', 'message', [messageData]);          
	};
	
	
	function successCallBack(result) {
		console.log(result);
	}
	function errorCallBack(error) {
		console.log(error);
	}

	if (cordova.addPlugin) {
		cordova.addConstructor(function() {
			// Register the javascript plugin with Cordova
			cordova.addPlugin('LoginPluginWS', new LoginPluginWS());
		});
	} else {
		if (!window.plugins) {
			window.plugins = {};
		}
		window.plugins.LoginPluginWS = new LoginPluginWS();
	}

})();
