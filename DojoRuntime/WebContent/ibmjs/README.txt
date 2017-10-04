*************************************************************************************************
IBM ONE UI DOJO EXTENSIONS (IDX) TOOLKIT V1.2 GA RELEASE --  RELEASE NOTES
*************************************************************************************************
 
OVERVIEW: 
---------

 
This is the GA release of Version 1.2 of the IBM One UI Dojo Extensions (IDX) Toolkit, formerly named the Convergence IDX Dojo Toolkit. This release serves as the inaugural release of the integrated toolkit that combines the heritage Convergence IDX modules with the heritage IBM One UI Toolkit modules. This toolkit is the official One UI toolkit to be used for new UI construction across IBM.  The IBM One UI Dojo Extensions (IDX) Toolkit provides IBM development teams with a faster path to development of Dojo applications that are aligned with the IBM One UI corporate standard. 

The inaugural IBM One UI release marks a significant IBM milestone toward greater consistency in look and feel and homogeneity in UI construction across the SWG portfolio. As part of the IBM Client Strategy, this toolkit supports the progressive strategic integration of Dojo UI toolkits across IBM into a single standardized toolkit, with widgets and themes supporting the IBM One UI standards.

The ZIP file contains all widgets from the two source toolkits, documentation, and a widget gallery/example application.

For existing IDX toolkit users, please note that although the common name of the toolkit has changed, the namespace has not changed; all IDX widgets continue to use the established "idx" prefix.  

 

CONTENTS:
---------

The Version 1.2 toolkit provides 154 modules. These modules include widgets, grid cell formatters, utilities, and a JSON data store.  Additionally, a custom CSS theme is provided  to achieve the One UI visual design signature. A second custom theme referred to as Vienna is included for InfoSphere products only, and serves as the interim design signature as InfoSphere transitions to the IBM One UI theme. The Vienna theme will be deprecated in a future release. 

The "Pre One UI" theme introduced in Version 1.1 has been replaced with the official "One UI" theme, please see more information below under New Content if you are currently using the Pre One UI theme. 

A complete list of included content can be found here: http://w3.ibm.com/connections/wikis/home?lang=en_US#/wiki/W3eec627be8de_4768_bb60_f063cb204266/page/Current%20IDX%20Assets  
 
You can view the heritage Convergence IDX demo gallery/example application online at: http://pokgsa.ibm.com/~bcaceres/public/imconverge/idx-1.2 and prior to the IBM One UI widgets being fully integrated into the IDX example application, you can view the heritage IBM One UI Toolkit widgets here: http://pokgsa.ibm.com/~bcaceres/public/imconverge/idx-1.2/ibmjs/idx/themes/oneui/widgetGallery.html. The two galleries are linked via the navigation at the top of the respective interfaces.
 

 

CLEARING HOUSE AND COO INFORMATION:

------------------------------------------------------------------

 

 

Please register your bundling dependency via our Clearing House entry: https://clearinghouse.raleigh.ibm.com/wps/myportal/Deliverable?ProductID=1332956393875. The Certificate of Originality for IDX can be found in the IID attached to the Clearing House record. Please complete the questions in the IID prior to submitting your request.

 
 
DOJO AND BROWSER SUPPORT:
--------------------------

It is our goal that the integrated toolkit will eventually support Dojo 1.7.2 and 1.6.1. For this release, the heritage IDX widgets support Dojo 1.7.2 and Dojo 1.6.1 with multiple themes, while the heritage One UI Toolkit modules only function with Dojo 1.7.2 and the One UI theme. 

 

IDX is aligned with the IBM Dojo Toolkit (IDT) browser-support matrix for browsers that are currently supported by their manufacturers. With the 1.2 release, we have completed additional testing on browsers that are over and above those supported by IDT.  

 
Please note that Dojo 1.5 and Dojo 1.6.0 are no longer supported (though support for Dojo 1.6.1 remains). Teams who are using older versions of IDX and Dojo 1.5 or Dojo 1.6 can still file support requests with the IDX team for the version of IDX they are using. 
 

IDX 1.2 will support the following browsers:
For Dojo 1.6.1:
- Internet Explorer 7, 8, 9 
- Firefox 10 Extended Support Release (ESR)*, and Firefox 3.6 (Since 3.6 is end-of-life, it has been sniff tested). 
- Chrome 18 for Windows, Macintosh and Linux*
- Safari 5.1 for Windows and Macintosh
- iOS 5.1 for iPad will be sniff tested
NOTE: One UI Toolkit widgets are not supported on Dojo 1.6.1 for this release.
 
For Dojo 1.7.2 (AMD and Synchronous modes):
- Internet Explorer 7, 8, 9 
- Firefox 10 Extended Support Release (ESR)*, and Firefox 3.6 (Since 3.6 is end-of-life, it has been sniff tested)
- Chrome 18 for Windows, Macintosh and Linux*
- iOS 5.1 for iPad will be sniff tested
- Safari 5.1 for Windows and Macintosh

 

* We have performed extensive accessibility testing using Firefox 10 ESR as our accessibility browser. 

 
Please note the following:
- Where an IDX component is an extension of a Dojo dijit or dojox component, accessibility violations originating in the base component may not have been corrected in the IDX component. In all cases where accessibility or other defects have been traced to a Dojo dijit or dojox component, defects have been opened against the base Dojo component.
- Some examples in the sample application included in the IDX Toolkit are not fully accessible, due to current Dojo accessibility restrictions. 
- The Dojo Toolkit website (dojotoolkit.org) documents known accessibility violations for dijit and dojox components.  
 

 

 
================================================================================
CURRENT CHANGELOG: CHANGES FROM IDX VERSION 1.1 to VERSION 1.2 
================================================================================

The focus of development efforts for the 1.2 release have been:  

(1) Inclusion of the IBM One UI Toolkit widgets into the IDX source stream.
(2) Development of the IBM One UI theme. 
(3) Support for Dojo 1.7.2
(4) Support for Asynchronous Method Definition (AMD) module format. Legacy (Synchronous) mode is also still supported. More information on AMD is available on the Dojo website: http://dojotoolkit.org/blog/learn-more-about-amd

 
Key updates included in 1.2 GA Release, since the 1.2 Limited Release, are:

    Extensive quality verification and defect fixes, including accessibility defect fixes.

 

 
 
NEW CONTENT: 
------------
 

- 64 IBM One UI Toolkit widgets have been added. These include widgets that will optimize the efficient and consistent enablement of the IBM One UI design signature and interaction standards. See the IBM One UI site for more details:  http://w3.ibm.com/uxd/oneui

- One UI theme: The One UI theme replaces the "Pre One UI theme" provided in the 1.1 release.  In order to replace the "preoneui" theme with the "oneui" theme simply replace the the "preoneui" class attribute on your document's body tag with "oneui".  Further, if you have developed application-specific widgets with CSS files that use the "preoneui" CSS selector, simply search and replace these globally with "oneui."  We felt this change was neccessary to avoid the confusion that stemmed from introduction of the name "preoneui," but please inform the IDX team if you feel that your product team cannot contain this change.The One UI theme has been designed by the corporate IBM One UI team and augmented with elements of the previous "preoneui" theme to provide additional styling of base dojo widgets. When including the One UI theme for your build (ibmjs/idx/themes/oneui), note that it references icons from the ibmjs/idx/icons directory. This format mirrors the dijit directory structure where common image icons can be accessed from all themes. 

Please note that InfoSphere teams should continue using the Vienna theme for 2012 development activity, other portfolios should consult with their UX representatives to determine which theme is appropriate for their use. 

-GridX - a new grid widget with better rendering speed, well modularized architecture and optimized capability for dealing with huge data set.

There are two parts of gridx provided in IDX 1.2
1. idx.gridx - is a mirror of the base gridx available under Dojo OSS Foundation, it provides claro theme with a rich set of features mostly covered the existing dojox.grid.* component.
2. idx.oneui.gridx - is part of OneUI theme implementation following the IBM OneUI Grid Standard

Please note - there is a plan to move the base gridx(idx.gridx) to IBM Dojo Toolkit(IDT) 1.8+ releases and the detailed upgrading tips will be provided in the future.

- ConsoleLayout - A new widget, idx.templates.ConsoleLayout, provides a templated widget for InfoSphere consoles, populated dynamically from a JSON config/registry.

- OpenMenuTabContainer, A new widget, idx.layout.OpenMenuTabContainer, provides an Open Menu for a Dojo TabContainer, to open as well as create new tabs.

- Person Card: A new widget, idx.widget.PersonCard, provides a feature to render various properties of a person, including a photo, in a person card format. Properties can be fetched from the IBM Connections Profiles service specifying its service URL and query parameters.

- NavTree and NavTreeModel. These two widgets work together to provide functionality for implementing hierarchical web application navigation in a tree -- typically for use in the leading sidebar of BorderContainer layout.

 
DEPRECATED CONTENT:  
--------------------

- Pre One UI theme has been replaced with One UI theme, as described above.

 
 
KNOWN ISSUES:
-------------
Some minor defects are known to exist. Our RTC dashboard is here: https://csnext.ibm.com:8002/jazz/web/projects/IM%20Convergence#action=com.ibm.team.dashboard.viewDashboard

- Redundancy of idx.widget.HoverHelp and idx.oneui.HoverHelpTooltip. idx.widget.HoverHelp has extended functionality and Dojo 1.6 support for IDX heritage applications, while idx.oneui.HoverHelpTooltip supports only 1.7 but adheres to One UI standards. idx.oneui.HoverHelpTooltip is a replacement for dijit.Tooltip. 

- Redundancy of idx.form.TriStateCheckBox and idx.oneui.form.TriStateCheckBox. idx.form.TriStateCheckBox is a subclass of idx.oneui.form.TriStateCheckBox in order to maintain support for Dojo 1.6. 

- One UI "special" button placement not yet integrated into idx.form.buttons extensions.

- While extensive accessibility testing has been completed, covering color, contrast, keyboard navigation, and access via screen reader, and all known defects with the widgets have been resolved, the toolkit has not yet been certified for accessibility. 

- idx.oneui.gridx - is still a partial implementation of OneUI Grid Standard with following features in IDX 1.2:
- Sort
- Pagination
- QuickerFilter
- Drag & Drop
- 10 variations of OneUI Grid styles

- gridx(including both idx.gridx and idx.oneui.gridx) is still limited a11y supported in IDX 1.2(with keyboard and ARIA supported), more a11y enhancements & fixes will be provided in the future releases.

- gridx DOH tests come from community code and will not work as-is within the toolkit. When creating a build, you may see an error saying "Missing dependency in legacy module.	reference module: idx/gridx/tests/doh/common; dependency: dojo/robotx". This error can be ignored.  


BUG REPORTING:

--------------

 
Bug reports and feature and enhancement requests will be managed through the "IBM Dojo Toolkit Public" project area on CSNext. Bug reports or feature requests can be opened using either Rational Team Concert eclipse client or the web-based client.
 
Accessing the Eclipse client:
To connect RTC v2 or v3 eclipse client to the IBM Dojo Toolkit Public project area:
Create a repository connection to https://csnext.ibm.com:8002/jazz.
Connect to the Project Area: IBM Dojo Toolkit Public.
Note: you do not need to request to be a member of the csnext project.
 
Accessing the web-based client:
Go to the IBM Dojo Toolkit Public project area: https://csnext.ibm.com:8002/jazz/web/projects/IBM%20Dojo%20Toolkit%20Public 
 
To open a bug report against the IBM One UI Toolkit:
Create a new work item.
Select "Bug Report" as the type.
Select "IDX 1.2 - IBM One UI Dojo Extensions Toolkit" in the "Found in" field.
Select "IDX" in the "Filed Against" field.
Fill in as much information as you can to assist the team in diagnosis of the issue. Please leave the "Owned By" field empty.

 
DOCUMENTATION:
--------------
“jsdocs.” You can also click the “Help” link in the upper right corner of the example application to view this documentation.

IBM One UI Tookit widgets include sample tests in the idx/oneui/tests directory. Documentation for One UI widgets will be added to the same directory as the rest. 
Additional documentation including brand-specific guides will be available for the GA release. 
 

 
WIDGET COMPATIBILITY WITH ESTABLISHED PORTFOLIO STANDARDS:
-------------------------------------------------------------------------
It is important to note that as IDX expands to encompass more product teams across Information Management, Industry Solutions and IBM, some widgets may be added that are specific to one product family, and may not be approved for use in your product. 
 
 
InfoSphere: 

InfoSphere teams should continue to use the Vienna theme for their 2012 releases. Widgets in the idx.oneui prefix have not been themed with the Vienna design. For the heritage IDX widgets, in version 1.2, the following included widgets are not approved for use in InfoSphere products:
* idx.layout.AccordionTabContainer
* idx.layout.CollapsibleTabContainer
* idx.layout.ContentPane
* idx.layout.DockContainer
* idx.layout.ECMTitlePane
* idx.form.DropDownSelect
* idx.form.ComboLink 
* idx.widget.Banner (use AppFrame and AppMarquee instead)
* idx.widget.ECMBreadcrumb  
 
Please inquire with your UX representative or Kathy Setzer/Austin/IBMUS if you have any questions.
 
 
Enterprise Content Management (ECM): 
For the heritage IDX widgets, in version 1.2, the following widgets are not approved for use in ECM products:
* idx.app.AppFrame
* idx.app.TabsMenuLauncher
* idx.app.AppMarquee
* idx.app.LoginFrame (approved for some products, contact Brett for review)
 
Please inquire with your UX representative or Brett W Morris/Bethesda/IBM if you have any questions.
 
================================================================================
WHAT'S COMING 
================================================================================
 

In version 1.3 and future releases:

- Accessibility certification on Firefox 10 ESR.

- We will continue through 2012 to roll other Dojo UI toolkits into the single One UI IDX offering.

- Support for newer browsers, continuing our strategy to align with the official IBM Dojo Toolkit (IDT) support matrix.
- Bidirectional (BiDi) / RTL certification
- Breadcrumb widget that consolidates the currently disparate breadcrumb functionality contributed by the InfoSphere and ECM teams in order to make it easier to create header panes that feature breadcrumbs. 
- Enhanced grid functionality.
- Widget contributions from several other teams across IBM.

- gridx - following features are in plan for the future releases:
- One UI Adaptive Filter
- One UI Advanced Filter Dialogue
- All-in-one grid bar to support any free control combinations between pagination and filter bars
- Tree grid compatibility with other top prioritized modules
- Row Lock
- Details on Demand
- Numerous a11y enhancements and fixes(e.g. high contrast)