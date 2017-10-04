//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.util.StringUtils;


public abstract class EditorPageBase {

	/**
	 * This sub class implements a ModifyListener that makes the text content
	 * unreadable if the text content is not a JobParameter. 
	 */
	public class ChangeEchoCharWhenJobParam implements ModifyListener {
		public void modifyText(ModifyEvent modifyEv) {
			Text srcText = (Text) modifyEv.getSource();

			// make the text readable if it's a job parameter
			if (StringUtils.isJobParamVariable(srcText.getText())) {
				srcText.setEchoChar('\0');
			}
			else {
				srcText.setEchoChar('-');
			}
		}
	} // end of sub class ChangeEchoCharWhenJobParam
	

	private String                           name;
	private String                           title;
	private String                           description;
	private String                           helpID;

	protected MultiPageEditorBase            editor;
	protected int                            index = -1;
	protected Map<String, Widget>            propertyToWidgetMap;
	protected Map<Widget, ControlDecoration> widgetToValidationDecorationsMap;

	
	static String copyright() {
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	
	public EditorPageBase(String tabName, String title, String descriptionText, String helpID) {
		this.name = tabName;
		this.title = title;
		this.description = descriptionText;
		this.propertyToWidgetMap = new HashMap<String, Widget>();
		this.widgetToValidationDecorationsMap = new HashMap<Widget, ControlDecoration>();
		this.helpID = helpID;
	}

	public String getHelpID() {
		return this.helpID;
	}

	public String getName() {
		return this.name;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public Image getImage() {
		return null;
	}

	public void setEditor(MultiPageEditorBase editor) {
		this.editor = editor;
	}

	public abstract void createControls(IControlFactory controlFactory, Composite parent);

	protected Composite createGroup(IControlFactory controlFactory, Composite composite, String groupName, String groupDescription) {
		Composite infoGroup = controlFactory.createGroup(composite, groupName, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		infoGroup.setLayout(gl);

		if (groupDescription != null) {
			Label l1 = controlFactory.createLabel(infoGroup, SWT.NONE);
			l1.setText(groupDescription);
		}
		return infoGroup;
	}


	protected void selectButtonWithEvent(Button b, boolean selection) {
		b.setSelection(selection);
		Listener[] listeners = b.getListeners(SWT.Selection);
		for (Listener l : listeners) {
			Event e = new Event();
			e.type = SWT.Selection;
			e.widget = b;
			e.data = selection;
			l.handleEvent(e);
		}
	}

	protected void selectComboWithEvent(Combo c, int selection) {
		c.select(selection);
		Listener[] listeners = c.getListeners(SWT.Selection);
		for (Listener l : listeners) {
			Event e = new Event();
			e.type = SWT.Selection;
			e.widget = c;
			e.data = selection;
			l.handleEvent(e);
		}

	}

	protected void configureTextForProperty(final Text t, final String property) {
		String val = this.editor.getConfiguration().get(property);
		if (val == null) {
			val = ""; //$NON-NLS-1$
		}
		this.propertyToWidgetMap.put(property, t);
		
		t.setText(val);
		t.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// this listener is also called when calling Text.setText()
				// ignore this during initialization phase
				if (editor.allPagesCreated) {
					Widget w = e.widget;
					if (w instanceof Text) {
						editor.getConfiguration().put(property, ((Text) w).getText());
					}
				}
			}
		});

		final ControlDecoration validationDecoration = new ControlDecoration(t, SWT.LEFT | SWT.TOP);
		this.widgetToValidationDecorationsMap.put(t, validationDecoration);

		setToolTipText(t, property);
	}

	protected void configureComboForProperty(Combo c, final String property) {
		int selection = editor.getConfiguration().getInt(property);

		c.select(selection);

		Listener l = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (editor.allPagesCreated) {
					Widget w = event.widget;
					if (w instanceof Combo) {
						editor.getConfiguration().put(property, ((Combo) w).getSelectionIndex());
					}
				}
			}
		};
		c.addListener(SWT.Selection, l);
		c.addListener(SWT.KeyDown, l);
	}

	protected void configureComboForPropertyStr(Combo c, final String property) {
		String val = editor.getConfiguration().get(property);
		if (val == null) {

			val = ""; //$NON-NLS-1$
		}
		//		editor.getPropertiesMap().registerKey(property, defaultValue);

		c.setText(val);

		Listener l = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (editor.allPagesCreated) {
					Widget w = event.widget;
					if (w instanceof Combo) {
						editor.getConfiguration().put(property, ((Combo) w).getText());
					}
				}
			}
		};
		c.addListener(SWT.Selection, l);
		c.addListener(SWT.KeyDown, l);
	}

	protected void configureCheckboxForProperty(final Button b, final String property) {
		boolean select = editor.getConfiguration().getBoolean(property);
		b.setSelection(select);

		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (editor.allPagesCreated) {
					editor.getConfiguration().put(property, b.getSelection());
				}
			}

		});

		setToolTipText(b, property);
	}

	protected void configureRadioButtonsForProperty(final Button[] buttons, final String property) {
		int ix = editor.getConfiguration().getInt(property);

		SelectionListener l = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (editor.allPagesCreated) {
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].getSelection()) {
							editor.getConfiguration().put(property, String.valueOf(i));
							break;
						}
					}
				}
			}
		};

		//	editor.getPropertiesMap().registerKey(property, String.valueOf(defaultTrue));

		for (int i = 0; i < buttons.length; i++) {
			boolean select = (i == ix);
			final Button b = buttons[i];
			b.setSelection(select);
			b.addSelectionListener(l);
		}

	}

	/// helper function
	protected void createKeyValueTextGroup(IControlFactory controlFactory, Composite composite, String groupName, String groupDescription, String[] keys, String[] values, Integer[] styles,
			String[] mapKeys) {

		Composite infoGroup = controlFactory.createGroup(composite, groupName, SWT.NONE);

		GridLayout gl = new GridLayout(2, false);
		infoGroup.setLayout(gl);

		if (groupDescription != null) {
			Label l1 = controlFactory.createLabel(infoGroup, SWT.NONE);
			l1.setText(groupDescription);
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			l1.setLayoutData(gd);
		}

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = values[i];
			final String mapKey = mapKeys[i];
			int style = styles[i];

			Label l = controlFactory.createLabel(infoGroup, SWT.NONE);
			l.setText(key);

			final Text t = controlFactory.createText(infoGroup, SWT.BORDER | style);
			this.configureTextForProperty(t, mapKey);
			if (value != null) {
				t.setText(value);
			}
			t.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}
	}

	protected void sendEvent(EditorEvent event) {
		editor.sendEvent(event);
	}

	public void update(EditorEvent event) {
		// do nothing by default
	}

	protected String getReadablePropertyNameForTextLabel(String prop) {
		PropertyInfo pi = this.editor.getConfiguration().getPropertyCollection().getPropertyInfo(prop);
		if (pi == null) {
			return "UNKNOWN_PROPERTY"; //$NON-NLS-1$
		}
		return pi.getReadableName() + ":"; //$NON-NLS-1$
	}

	/*
	protected Text createLabelAndTextField(IControlFactory controlFactory, Composite parent, String propertyName, int textStyle, Object optionalTextLayoutData) {
		return this.createLabelAndTextField(controlFactory, parent, propertyName, textStyle, "", optionalTextLayoutData);
	}
	*/

	protected Text createLabelAndTextField(IControlFactory controlFactory, Composite parent, String propertyName, int textStyle, Object optionalTextLayoutData) {
		Label l = controlFactory.createLabel(parent, SWT.NONE);
		String s = getReadablePropertyNameForTextLabel(propertyName);
		l.setText(s);

		Text t = controlFactory.createText(parent, textStyle);
		if (optionalTextLayoutData != null) {
			t.setLayoutData(optionalTextLayoutData);
		}
		this.configureTextForProperty(t, propertyName);
		return t;
	}

	protected void handleException(Throwable t) {
		Utils.showUnexpectedException(null, t);
	}

	private void setToolTipText(Control ctrl, String property) {
		PropertyInfoCollection coll = editor.getConfiguration().getPropertyCollection();
		if (coll != null) {
			PropertyInfo pi = coll.getPropertyInfo(property);
			if (pi != null) {
				ctrl.setToolTipText(pi.getDescription());
			}
		}
		
	}
	
}
