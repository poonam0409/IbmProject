dojo.provide("cwapp.widgets.LoginPage");

dojo.require("idx.app.LoginFrame");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "LoginPage");

dojo.declare("cwapp.widgets.LoginPage", [ idx.app.LoginFrame ], {
	
	// nls support
	msg : {},

	// private members
	_connects : null,
	_form : null,
	
	// public members
	target : "",
	
	// public functions
	constructor : function() {
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "LoginPage"));
	
		this._connects = [];
		this.target = "/ibm/iis/cw/CWApp/j_security_check" + window.location.hash;
	},
	
	destroyRecursive : function(preserveDom) {
		dojo.forEach(this._connects, dojo.disconnect);
		this.inherited(arguments);
	},
	
	postCreate : function() {
		this._setLabels();
		var loginBox = dojo.query(".idxLoginBoxInner", this.domNode)[0];
		var formContent = loginBox.children[1];
		var errorTable = dojo.query("table", this.invalidLoginDialog.domNode)[0];

		dojo.attr(errorTable, "role", "presentation");
		
		this._form = dojo.create("form", {
			method: "POST",
			action: this.target
		});
		
		loginBox.removeChild(formContent);
		loginBox.appendChild(this._form);
		this._form.appendChild(formContent);
		
		this._connects.push(
			dojo.connect(this, "onSubmit", function() {
				this._form.submit();
			})
		);
		
		this._connects.push(
			dojo.connect(this, "onKeyPress", function(e) {
				if(e.keyCode == dojo.keys.ENTER){
					this._onSubmitClick();
				}
			})
		);
		
		this.loginUserName.textbox.name = "j_username";
		this.loginPassword.textbox.name = "j_password";
		this.loginUserName.focus();
	},
	
	showErrorMessage : function(msg) {
		var msgPlaceholder = dojo.query(".idxDialogIconText", this.invalidLoginDialog.domNode)[0];
		dojo.html.set(msgPlaceholder, this.msg[msg]);
		var originalMsg = this.invalidMessage;
		var hideErrorMsgHandle = dojo.connect(this.invalidLoginDialog, "hide", function() {
			dojo.disconnect(hideErrorMsgHandle);
			dojo.html.set(msgPlaceholder, originalMsg);
		});
		this.invalidLoginDialog.show();
	},

	// private functions
	_setLabel : function(queryString, key, domIndex) {
		var labelPlaceholder = dojo.query(queryString, this.domNode)[domIndex];
		dojo.html.set(labelPlaceholder, key);
	},
	
	_setLabels : function() {
		this._setLabel(".idxLoginTitle", this.msg.LOGIN_1, 0);
		this._setLabel(".idxLoginSubTitle", this.msg.LOGIN_2, 0);
		this._setLabel(".idxFieldLabel", this.msg.LOGIN_3, 0);
		this._setLabel(".idxFieldLabel", this.msg.LOGIN_4, 1);
		this._setLabel(".idxInactivityMessage", this.msg.LOGIN_5, 0);
		this._setLabel(".idxLoginCopyright", this.msg.LOGIN_6, 0);
		this._setLabel(".dijitButtonText", this.msg.LOGIN_7, 0);
		var userNameLabel = dojo.query(".idxFieldLabel", this.domNode)[0];
		dojo.html.set(userNameLabel, '<label for="loginFrameUserName">'	+ this.msg.LOGIN_3 +'</label>');	
		var passwordLabel = dojo.query(".idxFieldLabel", this.domNode)[1];
		dojo.html.set(passwordLabel, '<label for="loginFramePassword">'	+ this.msg.LOGIN_4 +'</label>');		
	}
});
