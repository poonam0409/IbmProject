dojo.provide("cwapp.tab_rdm.ReferenceDataLoadWizard");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.dialogs");

dojo.require("dijit.Dialog");
dojo.require("dijit.layout.StackContainer");

dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.layout.ButtonBar");

dojo.require("cwapp.main.Error");

dojo.require("cwapp.tab_config.SapSystemPasswordDialog");
dojo.require("cwapp.tab_rdm.ReferenceDataImportProgressDialog");

dojo.require("cwapp.tab_rdm.referenceDataLoadWizard.ParametersPage");
dojo.require("cwapp.tab_rdm.referenceDataLoadWizard.ReferenceDataTableSelectionPage");
dojo.require("cwapp.tab_rdm.referenceDataLoadWizard.ReferenceDataImportSummaryPage");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.ReferenceDataLoadWizard", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_rdm", "templates/ReferenceDataLoadWizard.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},

	// private members
	_wizardDialog : null,
	_sapSystemPasswordDialog : null,
	_wizardSteps : null,
	
	// wizard page settings
	_wizardPages : {
		selectedSapSystem : null,
		rollout : null,
		bo : null,
		selectedReferenceTablesStore : null,
	},
	
	// constants
	_con : {
		ID_PROPERTY : "name",
	},

	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}

		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp",	"CwApp"));
	},

	postMixInProperties : function() {
		this._createWizardDialog();
		this.inherited(arguments);
	},
	
	postCreate : function() {
		// inject reference to this wizard into the wizard pages
		this.parametersPage.setParentWizard(this);
		this.referenceDataTableSelectionPage.setParentWizard(this);
		this.referenceDataImportSummaryPage.setParentWizard(this);
		
		this._wizardSteps = [this.stepArrow1,
		                     this.stepArrow2,
		                     this.stepArrow3];
		
		// connect the various buttons to events
		dojo.connect(this.backButton, "onClick", this, "_previousPage");
		dojo.connect(this.nextButton, "onClick", this, "_nextPage");
		dojo.connect(this.finishButton, "onClick", this, "_finishWizard");
		dojo.connect(this.cancelButton, "onClick", this, "hide");
		dojo.connect(this._wizardDialog, "onCancel", this, "_cancel");
		
		this.inherited(arguments);
	},
	
	show : function() {

		// at this point we 'weave' our widget html template content into the private dialog instance
		this._wizardDialog.set("content", this.domNode);
		this._wizardDialog.show();

		dojo.style(this._wizardDialog.containerNode, "padding", "0px");

		this._updateStepArrows();
		
		// Workaround for dojo stack container sizing:
		// Any first page will not be sized correctly on startup. "Reselecting" it after a short delay fixes the content size.
		setTimeout(dojo.hitch(this, function() {
			this.stackContainer._transition(this.parametersPage, this.parametersPage);
		}), 100);
		
		this.inherited(arguments);
	},
	
	hide : function() {
		this.inherited(arguments);
		this._wizardDialog.hide();
		this._cancel();
	},
	
	_cancel : function() {
		this.destroyRecursive();
	},
	
	setSapSystem : function(sapSystem) {
		this._wizardPages.selectedSapSystem = sapSystem;
	},

	getSapSystem : function() {
		 return this._wizardPages.selectedSapSystem;
	},
	
	setRollout : function(rollout) {
		this._wizardPages.rollout = rollout;
	},

	getRollout : function() {
		 return this._wizardPages.rollout;
	},
	
	setBO : function(bo) {
		this._wizardPages.bo = bo;
	},

	getBO : function() {
		return this._wizardPages.bo;
	},
	
	getBOForDisplay : function() {
		if (this._wizardPages.bo == this.parametersPage._con.BO_WILDCARD) {
			return this.msg.WizardParameters_AllBOs;
		} else {
			return this._wizardPages.bo;
		}
	},

	setReferenceTableStore : function(referenceTableStore) {
		this._wizardPages.selectedReferenceTablesStore = referenceTableStore;
	},

	getReferenceTableStore : function() {
		return this._wizardPages.selectedReferenceTablesStore;
	},
	
	clearReferenceTableStore : function() {
		this._wizardPages.selectedReferenceTablesStore = null;
	},

	enableNextButton : function() {
		this.nextButton.set("disabled", false);
	},
	
	disableNextButton : function() {
		this.nextButton.set("disabled", true);
	},
	
	// private functions
	
	// create the private dialog which is used to host our template context
	_createWizardDialog : function() {
		this._wizardDialog = new dijit.Dialog({parseOnLoad : false});
		this._wizardDialog.set("title", this.msg.REFDATALOADWIZ_1);
		this._wizardDialog.startup();
	},
	
	// display the next wizard page
	_nextPage : function() {
		var currentPage = this.stackContainer.selectedChildWidget;
		var currentPageIndex = this.stackContainer.getIndexOfChild(currentPage);

		if (this.stackContainer.selectedChildWidget == this.parametersPage) {
			// We're on the ParametersPage
			// Try to get the SAP password
			var deferred = this._handleSapSystemPassword();
			deferred.then(dojo.hitch(this, function(success) {
				if (success) {
					// Go to the selection page
					this.stackContainer.forward();
					this._updateStepArrows();
					this._changeButtonStates();
					
					dojo.publish(Topics.REFERENCEDATALOADWIZARD_NEXT, [currentPageIndex]);
				}
			}), dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			}));
		} else {
			// We're not on the ParametersPage, continue
			this.stackContainer.forward();
			this._updateStepArrows();
			this._changeButtonStates();
			
			dojo.publish(Topics.REFERENCEDATALOADWIZARD_NEXT, [currentPageIndex, currentPage]);
		}
	},
	
	// display the previous wizard page
	_previousPage : function() {
		var currentPageIndex = this.stackContainer.getIndexOfChild(this.stackContainer.selectedChildWidget);

		this.stackContainer.back();
		this._updateStepArrows();
		this._changeButtonStates();
		
		dojo.publish(Topics.REFERENCEDATALOADWIZARD_BACK, [currentPageIndex]);
	},
	
	// finish (and end) the wizard
	_finishWizard : function() {
		this._wizardDialog.hide();
		
		var progressDialog = new cwapp.tab_rdm.ReferenceDataImportProgressDialog({_idProperty : this._con.ID_PROPERTY});
		progressDialog.setStartingPage(this);
		progressDialog.startup();
		progressDialog.start();
	},
	
	// enable / disable the wizard buttons depending on the number of available wizard pages
	// and the current page we're on
	_changeButtonStates : function() {
		var currentPageIndex = this.stackContainer.getIndexOfChild(this.stackContainer.selectedChildWidget);
		var numberOfChildren = this.stackContainer.getChildren().length;
		
		// we've reached the last page of the wizard
		// so we disable the 'next' button and enable the 'finish' button
		if (currentPageIndex == (numberOfChildren - 1)) {
			this.nextButton.set("disabled", true);
			this.finishButton.set("disabled", false);
		}
		else {
			this.nextButton.set("disabled", false);
			this.finishButton.set("disabled", true);
		}
		
		// we're at the first page of the wizard
		// so we disable the 'back button'
		if (currentPageIndex == 0) {
			this.backButton.set("disabled", true);
		}
		else {
			this.backButton.set("disabled", false);
		}
	},
	
	_updateStepArrows : function() {
		var currentPage = this.stackContainer.selectedChildWidget;
		var currentPageIndex = this.stackContainer.getIndexOfChild(currentPage);

		var steps = this._wizardSteps;
        dojo.forEach(steps, function(entry, i) {
            dojo.removeClass(entry, "completed");
            if (i < currentPageIndex) {
                dojo.addClass(entry, "completed");
            }
            dojo.removeClass(entry, "beforeSelected");
            if (i == currentPageIndex - 1) {
                dojo.addClass(entry, "beforeSelected");
            }
            dojo.removeClass(entry, "selected");
            if (i == currentPageIndex) {
                dojo.addClass(entry, "selected");
            }
            dojo.removeClass(entry, "lastTab");
            if (i == steps.length - 1) {
                dojo.addClass(entry, "lastTab");
            }
        });
	},
	
	// check if the SAP system selected on the ParametersPage has a password
	// and if not, prompt for one
	// this function is asynchronous therefore we return a dojo.Deferred
	_handleSapSystemPassword : function() {
		if (!this._sapSystemPasswordDialog) {
			this._sapSystemPasswordDialog = new cwapp.tab_config.SapSystemPasswordDialog();
		}
		return this._sapSystemPasswordDialog.getPassword(this._wizardPages.selectedSapSystem);	
	},

});
