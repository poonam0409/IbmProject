//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.idocseglist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.idocseglist;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.wizards.SAPSystemSelectionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.ibm.is.sappack.gen.tools.sap.provider.SapIDocTypesTreeviewContentProvider;
import com.ibm.is.sappack.gen.tools.sap.provider.SapIDocTypesTreeviewLabelDecorator;
import com.ibm.is.sappack.gen.tools.sap.provider.SapIDocTypesTreeviewLabelProvider;
import com.ibm.is.sappack.gen.tools.sap.utilities.ExceptionHandler;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;
import com.sap.conn.jco.JCoException;


public class IDocBrowserWizardPage extends WizardPage {
	private   Group                               idocTypesGroup;
	private   Tree                                tree;
	private   TreeViewer                          treeViewer;
	private   Button                              extensionTypeButton;
	private   Button                              basicTypeButton;
	private   String                              idocTypeName;
	private   SAPSystemSelectionWizardPage        sapSystemSelectionPage;
	private   SapIDocTypesTreeviewContentProvider treeViewContentProvider;

	protected Text                                textIDocSearchTerm;
	protected Button                              buttonSearch;
	protected StackLayout                         stackLayout;
	protected Composite                           stackComposite;
	protected IDocReleaseSelectionWidget          idocRelease;


	/*
	private static final String IDOC_BROWSER_WIZARD_PAGE_EXTENDED_IDOC_TYPE = "IDocBrowserWizardPage.EXTENDED_IDOC_TYPE"; //$NON-NLS-1$
	private static final String IDOC_BROWSER_WIZARD_PAGE_SETTINGS_IDOC_SEARCH_TERM = "IDocBrowserWizardPage.textIDocSearchTerm"; //$NON-NLS-1$
	private static final String IDOC_BROWSER_WIZARD_PAGE_IDOC_TYPE_RELEASE = "IDocBrowserWizardPage.IdocTypeRelease"; //$NON-NLS-1$
*/

	
  	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}	

	public IDocBrowserWizardPage(SAPSystemSelectionWizardPage sapSystemSelectionPage, String idocTypName) {
		super(Messages.IDocBrowserWizardPage_0, Messages.IDocBrowserWizardPage_1, null);
		setDescription(Messages.IDocBrowserWizardPage_2);
		this.sapSystemSelectionPage = sapSystemSelectionPage;
		
		if (idocTypName == null || idocTypName.isEmpty()) {
			this.idocTypeName = null;
		}
		else {
			this.idocTypeName = idocTypName;
		}
	}

	public void createControl(final Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NULL);

		// create a layout for this wizard page
		GridLayout topGridLayout = new GridLayout();
		topGridLayout.numColumns = 1;
		topGridLayout.makeColumnsEqualWidth = false;
		mainComposite.setLayout(topGridLayout);

		Group group = new Group(mainComposite, SWT.NULL);
		group.setText(Messages.TableBrowserWizardPage_12);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;

		group.setLayout(gridLayout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.textIDocSearchTerm = new Text(group, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		this.textIDocSearchTerm.setLayoutData(gridData);

		this.textIDocSearchTerm.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					buttonSearch.notifyListeners(SWT.Selection, new Event());
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// Nothing to be done here
			}
		});

		if (this.idocTypeName != null) {
			this.textIDocSearchTerm.setText(this.idocTypeName);
		}
		this.textIDocSearchTerm.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();

			}

		});
		WidgetIDUtils.assignID(textIDocSearchTerm, WidgetIDConstants.MOD_TEXTIDOCSEARCHTERM);

		this.buttonSearch = new Button(group, SWT.PUSH);
		this.buttonSearch.setText(Messages.TableBrowserWizardPage_13);
		this.buttonSearch.forceFocus();
		WidgetIDUtils.assignID(buttonSearch, WidgetIDConstants.MOD_BUTTONSEARCH);

		this.buttonSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (Utilities.containsOnlyWildcardChar(textIDocSearchTerm.getText())) {
					MessageDialog.openInformation(getShell(), Messages.TitleError, Messages.BrowserWizardPage_1);
					textIDocSearchTerm.setFocus();
				} else {
					Cursor originalCursor = IDocBrowserWizardPage.this.getShell().getCursor();
					Cursor waitCursor = new Cursor(IDocBrowserWizardPage.this.getShell().getDisplay(), SWT.CURSOR_WAIT);

					IDocBrowserWizardPage.this.getShell().setCursor(waitCursor);
					IDocBrowserWizardPage.this.buttonSearch.setEnabled(false);

					SapSystem selectedSapSystem = IDocBrowserWizardPage.this.sapSystemSelectionPage.getSelectedSAPSystem();
					try {

						String searchTerm = getSearchTerm();
						if (Utilities.isEmpty(searchTerm)) {
							return;
						}
						IDocType[] foundIDocTypes = SapIDocTypesTreeviewContentProvider.createInitialTreeContent(selectedSapSystem,
						                                                                                         searchTerm,
						                                                                                         extensionTypeButton.getSelection(),
						                                                                                         getRelease());
						if (foundIDocTypes == null || foundIDocTypes.length == 0) {
							MessageDialog.openInformation(getShell(), Messages.IDocBrowserWizardPage_4, MessageFormat.format(Messages.IDocBrowserWizardPage_5, searchTerm));
						}
						else {
							IDocBrowserWizardPage.this.treeViewer.setInput(foundIDocTypes);
						}
						dialogChanged();
					}
					catch (JCoException jcoException) {
//						jcoException.printStackTrace();
						Activator.getLogger().log(Level.SEVERE, jcoException.getMessage(), jcoException);
						ExceptionHandler.handleJcoException(jcoException, selectedSapSystem, getShell());

					}
					finally {
						IDocBrowserWizardPage.this.buttonSearch.setEnabled(true);
						IDocBrowserWizardPage.this.getShell().setCursor(originalCursor);
					}
				}

			}
		});

		// basic type or extension type option
		Composite radioComposite = new Composite(group, SWT.NULL);
		radioComposite.setLayout(new GridLayout(1, false));
		this.basicTypeButton = new Button(radioComposite, SWT.RADIO);
		this.basicTypeButton.setText(Messages.IDocBrowserWizardPage_BasicType);
		this.basicTypeButton.setSelection(true);
		WidgetIDUtils.assignID(basicTypeButton, WidgetIDConstants.MOD_BASICTYPEBUTTON);

		this.extensionTypeButton = new Button(radioComposite, SWT.RADIO);
		this.extensionTypeButton.setText(Messages.IDocBrowserWizardPage_ExtensionType);
		WidgetIDUtils.assignID(extensionTypeButton, WidgetIDConstants.MOD_EXTENSIONTYPEBUTTON);

		new Label(group, SWT.NONE);

		this.idocRelease = new IDocReleaseSelectionWidget(group, true);

		/*
		Composite versionComposite = new Composite(group, SWT.NULL);
		versionComposite.setLayout(new GridLayout(2, false));
		Label l = new Label(versionComposite, SWT.NONE);
		l.setText("Release:");
		
		versionCombo = new Combo(versionComposite, SWT.NONE);
		versionCombo.add(""); //$NON-NLS-1$
		versionCombo.add("700"); //$NON-NLS-1$
		versionCombo.add("640"); //$NON-NLS-1$
		versionCombo.add("620"); //$NON-NLS-1$
		versionCombo.add("46D"); //$NON-NLS-1$
		versionCombo.add("46C"); //$NON-NLS-1$
		versionCombo.add("46B"); //$NON-NLS-1$
		versionCombo.add("46A"); //$NON-NLS-1$
		*/

		this.stackComposite = new Composite(mainComposite, SWT.NULL);
		this.stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.stackLayout = new StackLayout();
		this.stackComposite.setLayout(this.stackLayout);

		this.idocTypesGroup = createIDocTypesTree(this.stackComposite);
		this.stackLayout.topControl = this.idocTypesGroup;

	//	restoreWidgetValues();

		/*
		// if we are in replay mode we make all children read only
		if (((IMetadataImportWizard) getWizard()).isReplayMode()) {
			makeAllChildrenReadOnly(container);
		}
		*/

		dialogChanged();

		setControl(mainComposite);
	}

	protected String getRelease() {
		return this.idocRelease.getRelease();
	}

	protected String getSearchTerm() {
		String searchTerm = this.textIDocSearchTerm.getText();
		searchTerm = searchTerm.toUpperCase().replaceAll(Constants.INVALID_CHARS_FOR_TABLE_NAMES, Constants.EMPTY_STRING);
		searchTerm = searchTerm.replace(IDocSegmentListValidator.SEARCH_WILDCARD_COMMON, IDocSegmentListValidator.SEARCH_WILDCARD_SAP);
		this.textIDocSearchTerm.setText(searchTerm);

		return searchTerm;
	}

	private Group createIDocTypesTree(Composite parent) {
		Group group = new Group(parent, SWT.NULL);
		group.setText(Messages.TableBrowserWizardPage_6);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		group.setLayout(gridLayout);
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		this.tree = new Tree(group, SWT.BORDER | SWT.CHECK);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		this.tree.setLayoutData(data);
		WidgetIDUtils.assignID(tree, WidgetIDConstants.MOD_IDOCTREE);

		this.treeViewer = new TreeViewer(this.tree);

		this.treeViewContentProvider = new SapIDocTypesTreeviewContentProvider();
		this.treeViewer.setContentProvider(this.treeViewContentProvider);
		ILabelProvider lp = new SapIDocTypesTreeviewLabelProvider();
		ILabelDecorator ld = new SapIDocTypesTreeviewLabelDecorator();
		this.treeViewer.setLabelProvider(new DecoratingLabelProvider(lp, ld));

		this.tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				// whenever the check box for the IDoc type is checked
				// we take care of the segments of that type
				if (event.detail == SWT.CHECK) {

					// get the source of the event
					TreeItem item = (TreeItem) event.item;

					// get the expansion state of the tree item
					boolean isExpanded = item.getExpanded();

					// disable redrawing of the tree control in order to avoid screen flickering
					tree.setRedraw(false);

					// set a different cursor to express busy state of the UI
					Cursor originalCursor = getShell().getCursor();
					Cursor waitCursor = new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT);
					getShell().setCursor(waitCursor);

					// checking the tree items
					checkTreeItemsSelection(item);

					// restore the expansion state of the tree item
					item.setExpanded(isExpanded);

					// re-enabling redrawing of the tree control
					tree.setRedraw(true);

					// restore the original cursor
					getShell().setCursor(originalCursor);
					dialogChanged();
				}
			}

			public void checkTreeItemsSelection(TreeItem item) {
				boolean hasMandatorySegments = false;

				// find idoc type item
				TreeItem idocTypeItem = item;
				while (idocTypeItem.getParentItem() != null) {
					idocTypeItem = idocTypeItem.getParentItem();
				}

				// we have to expand the tree item to receive all its children
				setTreeItemExpanded(item);

				// initialize items stack
				Stack<TreeItem> itemsToVisit = new Stack<TreeItem>();
				TreeItem[] rootItems = idocTypeItem.getItems();
				itemsToVisit.addAll(Arrays.asList(rootItems));

				// taking care of the segments
				// depending on whether an IDoc type has been checked in the tree control
				// or an individual segment belonging to one particular IDoc type
				if (item == idocTypeItem) {

					// we do have a kind of ternary check box widget which works as follows:
					// default state: IDoc type not checked, none of its segments checked
					// first click: IDoc type half checked, only mandatory segments are checked
					// second click: IDoc type fully checked, all segments are checked
					// third click: go back to default
					// in order to implement this behaviour we have to track the status of the
					// corresponding check box widget
					if (idocTypeItem.getChecked()) {

						// item has been checked
						if (idocTypeItem.getGrayed()) {

							// check box is grayed which means mandatory segments have been
							// checked previously (or manual segment selection existed)
							// our only way to go from here is to select all segments (second click)
							idocTypeItem.setGrayed(false);
							while (!itemsToVisit.isEmpty()) {
								TreeItem currentItem = itemsToVisit.pop();

								// we have to expand the tree item to receive its children
								setTreeItemExpanded(currentItem);
								currentItem.setChecked(idocTypeItem.getChecked());
								TreeItem[] children = currentItem.getItems();
								itemsToVisit.addAll(Arrays.asList(children));
							}
						} else {

							// check box is white which means this is the initial check
							// our only way to go from here is to select mandatory segments only (first click)
							idocTypeItem.setGrayed(true);

							// as there might be the chance that an IDoc type has no
							// mandatory segments we have to handle this by skipping the selection
							// of mandatory segments (there are non, right?) and going straight
							// to full selection of all segments, thus mimicking a second click
							// for this purpose we make a copy of the tree item list for revisiting
							Stack<TreeItem> itemsToVisitAgain = new Stack<TreeItem>();

							while (!itemsToVisit.isEmpty()) {
								TreeItem currentItem = itemsToVisit.pop();

								// we have to expand the tree item to receive its children
								setTreeItemExpanded(currentItem);
								itemsToVisitAgain.push(currentItem);

								// sometimes(?) we stumble upon non-readable items
								// in the tree item list so we have to check first,
								// whether usable data (from the domain) is available
								// if not, we just skip that item
								Segment currentSegment = (Segment) currentItem.getData();
								if (currentSegment != null) {
									if (currentSegment.mandatory) {
										hasMandatorySegments = true;
										currentItem.setChecked(idocTypeItem.getChecked());
									}
								}

								TreeItem[] children = currentItem.getItems();
								itemsToVisit.addAll(Arrays.asList(children));
							}

							if (!hasMandatorySegments) {

								// we do not have any mandatory segments so we take
								// the copied tree item list and go through it once more for
								// full selection
								idocTypeItem.setGrayed(false);
								while (!itemsToVisitAgain.isEmpty()) {
									TreeItem currentItemAgain = itemsToVisitAgain.pop();

									// we have to expand the tree item to receive its children
									setTreeItemExpanded(currentItemAgain);
									currentItemAgain.setChecked(idocTypeItem.getChecked());
									TreeItem[] childrenAgain = currentItemAgain.getItems();
									itemsToVisitAgain.addAll(Arrays.asList(childrenAgain));
								}
							}

							// copied tree item list is no longer needed, so we dereference it
							itemsToVisitAgain = null;
						}
					} else {

						// item has been unchecked
						// before we can remove the check mark we have to figure the preconditions
						// meaning that we could come from either the first click or the second click
						if (idocTypeItem.getGrayed()) {

							// check box is grayed which means mandatory segments have been
							// checked previously (or manual segment selection existed)
							// our only way to go from here is to NOT remove the check mark from the box
							// and to select all segments (second click)
							idocTypeItem.setGrayed(false);
							idocTypeItem.setChecked(true);
							while (!itemsToVisit.isEmpty()) {
								TreeItem currentItem = itemsToVisit.pop();

								// we have to expand the tree item to receive its children
								setTreeItemExpanded(currentItem);
								currentItem.setChecked(idocTypeItem.getChecked());
								TreeItem[] children = currentItem.getItems();
								itemsToVisit.addAll(Arrays.asList(children));
							}
						} else {

							// check box is white which means that all segments have been checked
							// previously
							// our only way to go from here is to have the check mark removed from the box
							// and to deselect all segments (third click)
							while (!itemsToVisit.isEmpty()) {
								TreeItem currentItem = itemsToVisit.pop();

								// we have to expand the tree item to receive its children
								setTreeItemExpanded(currentItem);
								currentItem.setChecked(idocTypeItem.getChecked());
								TreeItem[] children = currentItem.getItems();
								itemsToVisit.addAll(Arrays.asList(children));
							}
						}
					}
				} else {

					// an individual segment has been checked from the tree
					// we have to make sure that the segment path up to the root of the tree
					// (the corresponding IDoc type) is completely checked
					TreeItem currentSegmentItem = item;
					while ((currentSegmentItem.getChecked()) && (currentSegmentItem.getParentItem() != null) && (currentSegmentItem.getParentItem().getData() instanceof Segment)) {
						currentSegmentItem = currentSegmentItem.getParentItem();
						currentSegmentItem.setChecked(true);
					}

					// in addition to that we have to make sure that sub segments get unchecked
					// whenever a parent segment is unchecked
					if (!item.getChecked()) {
						Stack<TreeItem> subSegmentItems = new Stack<TreeItem>();
						TreeItem[] children = item.getItems();
						subSegmentItems.addAll(Arrays.asList(children));

						while (!subSegmentItems.isEmpty()) {
							TreeItem subItem = subSegmentItems.pop();
							subItem.setChecked(false);
							children = subItem.getItems();
							subSegmentItems.addAll(Arrays.asList(children));
						}
					}

					// since checking and unchecking of individual segments from the tree
					// affects the parent IDoc type we have to make sure that the check box
					// of the IDoc type always is in sync with the checked segments
					// this means the state of the IDoc check box (checked / unchecked) as
					// well as the color of the check box (grayed / white)
					boolean allChecked = true;
					boolean noneChecked = true;

					while (!itemsToVisit.isEmpty()) {
						TreeItem currentItem = itemsToVisit.pop();
						if (currentItem.getChecked()) {
							noneChecked = false;
						} else {
							allChecked = false;
						}

						TreeItem[] children = currentItem.getItems();
						itemsToVisit.addAll(Arrays.asList(children));
					}

					if (allChecked) {
						idocTypeItem.setChecked(true);
						idocTypeItem.setGrayed(false);
					} else if (noneChecked) {
						idocTypeItem.setChecked(false);
						idocTypeItem.setGrayed(false);
					} else {
						idocTypeItem.setChecked(true);
						idocTypeItem.setGrayed(true);
					}
				}
			}

			public void setTreeItemExpanded(TreeItem item) {
				item.setExpanded(true);
				treeViewer.refresh();
			}
		});

		return group;
	}

	public void dialogChanged() {
		// ---- IDoc Type extension ----
		// ((IMetadataImportWizard) getWizard()).setAllowFinish(false);
		// -----------------------------

		// olix if ((this.idocTypesOption != null) &&
		// this.idocTypesOption.getSelection()) {
		boolean complete = true;

		Object treeViewerInput = this.treeViewer.getInput();
		TreeItem[] items = this.tree.getItems();
		int checkedIDocTypes = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked() && (items[i].getData() instanceof IDocType)) {
				checkedIDocTypes++;
			}
		}

		if (Utilities.containsAllowedChars(this.textIDocSearchTerm.getText())) {
			this.buttonSearch.setEnabled(true);
		} else {
			complete = false;
			this.buttonSearch.setEnabled(false);
		}

		if (treeViewerInput == null) {
			updateStatus(Messages.TableBrowserWizardPage_16);
			complete = false;
		} else if (checkedIDocTypes == 0) {
			updateStatus(Messages.TableBrowserWizardPage_17);
			complete = false;
		} else if (checkedIDocTypes > 1) {
			updateStatus(Messages.TableBrowserWizardPage_18);
			complete = false;
		}

		if (complete) {
			updateStatus(this.treeViewContentProvider.getLastErrorMessage());
		}

		return;
	}

	private void updateStatus(String message) {
		if (message == null) {
			setErrorMessage(null);
			setPageComplete(true);
			return;
		}

		setErrorMessage(message);
		setPageComplete(false);
	}

	public IDocType getSelectedIDocType() {
		TreeItem[] items = this.tree.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				IDocType idocType = (IDocType) items[i].getData();
				return idocType;
			}
		}

		return null;
	}

	// is never used !!	
	//	private List<String> getSelectedSegmentTypes() throws JCoException {
	//		List<Segment> segments = getSelectedSegmentsObjects();
	//		List<String> selectedSegments = new ArrayList<String>();
	//		for (Segment seg : segments) {
	//			selectedSegments.add(seg.getType());
	//		}
	//		return selectedSegments;
	//	}
	//

	public List<String> getSelectedSegments() throws JCoException {
		List<Segment> segments = getSelectedSegmentsObjects();
		List<String> selectedSegments = new ArrayList<String>();
		for (Segment seg : segments) {
			selectedSegments.add(seg.getType());
		}
		return selectedSegments;
		/*
		List<String> selectedSegments = new ArrayList<String>();
		TreeItem[] rootItems = this.tree.getItems();

		for (int i = 0; i < rootItems.length; i++) {
			if (rootItems[i].getChecked() && (rootItems[i].getItems()[0].getData() == null)) {

				// this root item is checked but has never been expanded so
				// segments are not loaded yet
				IDocType idocType = (IDocType) rootItems[i].getData();
				Iterator<Segment> segments = idocType.getSegments().iterator();
				while (segments.hasNext()) {
					Segment segment = segments.next();
					selectedSegments.add(segment.getDefinition());
				}
			}
			else {
				addSegments(selectedSegments, rootItems[i]);
			}
		}

		return selectedSegments;
		*/
	}

	private List<Segment> getSelectedSegmentsObjects() throws JCoException {
		List<Segment> selectedSegments = new ArrayList<Segment>();
		TreeItem[] rootItems = this.tree.getItems();

		for (int i = 0; i < rootItems.length; i++) {
			if (rootItems[i].getChecked() && (rootItems[i].getItems()[0].getData() == null)) {

				// this root item is checked but has never been expanded so
				// segments are not loaded yet
				IDocType idocType = (IDocType) rootItems[i].getData();
				Iterator<Segment> segments = idocType.getSegments().iterator();
				while (segments.hasNext()) {
					Segment segment = segments.next();
					selectedSegments.add(segment);
				}
			} else {
				addSegmentObjects(selectedSegments, rootItems[i]);
			}
		}

		return selectedSegments;
	}

	private void addSegmentObjects(List<Segment> segments, TreeItem item) {
		if (item.getData() instanceof Segment) {
			if (item.getChecked()) {
				Segment segment = (Segment) item.getData();
				segments.add(segment);
			}
		}

		TreeItem[] children = item.getItems();

		for (int i = 0; i < children.length; i++) {
			addSegmentObjects(segments, children[i]);
		}
	}

	/*
		private void addSegments(List<String> segments, TreeItem item) {
			if (item.getData() instanceof Segment) {
				if (item.getChecked()) {
					Segment segment = (Segment) item.getData();
					segments.add(segment.getDefinition());
				}
			}

			TreeItem[] children = item.getItems();

			for (int i = 0; i < children.length; i++) {
				addSegments(segments, children[i]);
			}
		}
	*/
/*
	private void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();

		if (settings != null) {
			settings.put(IDOC_BROWSER_WIZARD_PAGE_SETTINGS_IDOC_SEARCH_TERM, this.textIDocSearchTerm.getText());
			boolean isExtendedType = this.extensionTypeButton.getSelection();
			settings.put(IDOC_BROWSER_WIZARD_PAGE_EXTENDED_IDOC_TYPE, isExtendedType);
			settings.put(IDOC_BROWSER_WIZARD_PAGE_IDOC_TYPE_RELEASE, this.idocRelease.getRelease());
		}
	}
*/
	/*
	protected void restoreWidgetValues() {
		if (this.idocTypesGroup == null) {
			return;
		}

		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String searchTerm = settings.get(IDOC_BROWSER_WIZARD_PAGE_SETTINGS_IDOC_SEARCH_TERM);

			if (searchTerm != null) {
				this.textIDocSearchTerm.setText(searchTerm);
				textIDocSearchTerm.selectAll();
			}

			boolean isExtendedtype = settings.getBoolean(IDOC_BROWSER_WIZARD_PAGE_EXTENDED_IDOC_TYPE);
			this.extensionTypeButton.setSelection(isExtendedtype);
			this.basicTypeButton.setSelection(!isExtendedtype);

			String release = settings.get(IDOC_BROWSER_WIZARD_PAGE_IDOC_TYPE_RELEASE);
			if (release != null) {
				this.idocRelease.setRelease(release);
			}
		}
	}
*/
	/*
	public boolean nextPressed() {
		try {
			TreeItem[] rootItems = this.tree.getItems();

			for (int i = 0; i < rootItems.length; i++) {
				if (rootItems[i].getChecked()) {
					IDocType idocType = (IDocType) rootItems[i].getData();
					IDocSegmentCollector collector = new IDocSegmentCollector(idocType);

					getContainer().run(true, true, collector);

				}
			}

			/ * remember selected idoc segments * /
			List<String> selectedSegments = this.getSelectedSegments();

			for (int i = 0; i < selectedSegments.size(); i++) {
				String segmentName = selectedSegments.get(i);
				Segment segment = this.getSelectedIDocType().getSegment(segmentName);
				segment.setSelected(true);
			}

			this.getSharedObjectsMap().put(IDocPageGroup.IDOC_TYPE, getSelectedIDocType());
			this.getSharedObjectsMap().put(IDocPageGroup.INITIAL_SEGMENTS, getSelectedSegments());
			this.saveWidgetValues();
			return true;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);

			return false;
		} catch (JCoException e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
			return false;
		}
	}
*/
}
