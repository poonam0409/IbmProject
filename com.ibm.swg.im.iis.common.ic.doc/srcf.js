////////////////////////////////////////////////////////////////////////////////
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
////////////////////////////////////////////////////////////////////////////////

// Assumes that IBM documentation plugins all begin with the string "com.ibm"

function ibm_id_form(micname)
{
   var mform_htm = "reader-comment-form.html";
   var mlist     = window.location.pathname.split("/")
   var mplugin   = mlist[1]


   for (x in mlist)
   {
     if (mlist[x].search(/com.ibm/i) >-1)
     {
        mplugin = mlist[x]
        break
     }
   }

   var mfile     = mlist[mlist.length-1]
   var mtitle    = document.title
   var mtest     = window.location.href
   var mreplace, mhref

   var browserlanguage = navigator.language ? navigator.language : navigator.browserLanguage
   var filelanguage = document.documentElement.lang 

   mreplace =  window.location.href.substr(window.location.href.indexOf(mplugin),window.location.href.length-mplugin.length)
   if (typeof plugin_form == 'undefined')
   {
     mhref = encodeURI(window.location.href.replace(mreplace, mplugin + "/" + mform_htm + "?" + mplugin + "+" + mfile + "+" + mtitle))
   }
   else
   {
      mhref = encodeURI(window.location.href.replace(mreplace, plugin_form + "/" + mform_htm + "?" + micname + "+" + mplugin + "+" + mfile + "+" + mtitle + "+" + filelanguage + "+" + browserlanguage))
   }
   window.location = mhref;
   
//   alert('browser: ' + browserlanguage);
//   alert('file: ' + filelanguage);

}
