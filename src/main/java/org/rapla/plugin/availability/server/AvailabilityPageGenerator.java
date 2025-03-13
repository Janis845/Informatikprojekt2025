package org.rapla.plugin.availability.server;

/*--------------------------------------------------------------------------*
| Copyright (C) 2014 Christopher Kohlhaas                                  |
|                                                                          |
| This program is free software; you can redistribute it and/or modify     |
| it under the terms of the GNU General Public License as published by the |
| Free Software Foundation. A copy of the license has been included with   |
| these distribution in the COPYING file, if not go to www.fsf.org         |
|                                                                          |
| As a special exception, you are granted the permissions to link this     |
| program with every library, which license fulfills the Open Source       |
| Definition as published by the Open Source Initiative (OSI).             |
*--------------------------------------------------------------------------*/

import org.jetbrains.annotations.NotNull;




import org.rapla.RaplaResources;
import org.rapla.components.util.IOUtil;
import org.rapla.components.util.ParseDateException;
import org.rapla.entities.EntityNotFoundException;
import org.rapla.entities.User;
import org.rapla.entities.configuration.CalendarModelConfiguration;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.configuration.RaplaMap;
import org.rapla.entities.domain.Allocatable;
import org.rapla.facade.CalendarNotFoundExeption;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.RaplaFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.framework.internal.AbstractRaplaLocale;
import org.rapla.logger.Logger;
import org.rapla.plugin.availability.AvailabilityPlugin;
import org.rapla.plugin.availability.AvailabilityResources;
import org.rapla.plugin.availability.AdminMenuEntry.AdminMenuEntryDialog;
import org.rapla.plugin.urlencryption.UrlEncryption;
import org.rapla.plugin.urlencryption.UrlEncryptionPlugin;
import org.rapla.server.extensionpoints.HTMLViewPage;
import org.rapla.storage.StorageOperator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.subst.Token.Type;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/******* USAGE: ************
* ReadOnly calendarview view.
* You will need the autoexport plugin to createInfoDialog a calendarview-view.
*
* Call:
* rapla/calendar?user=<username>&file=<export_name>
*
* Optional Parameters:
*
* &hide_nav: will hide the navigation bar.
* &day=<day>:  int-value of the day of month that should be displayed
* &month=<month>:  int-value of the month
* &year=<year>:  int-value of the year
* &today:  will set the view to the current day. Ignores day, month and year
*/
@Path("availability")
@Singleton
public class AvailabilityPageGenerator
{
   @Inject 
   Map<String, Provider<HTMLViewPage>> factoryMap;
   @Inject 
   public RaplaFacade facade;
   @Inject 
   Logger logger;
   @Inject 
   RaplaLocale raplaLocale;
   @Inject 
   RaplaResources i18n;
   @Inject 
   AvailabilityResources autoexportI18n;

   @Inject 
   public AvailabilityPageGenerator()
   {
   }

   public RaplaFacade getFacade() {
       return facade;
   }


   private String getTitle(String key, CalendarModelConfiguration conf)
   {
       String title = conf.getTitle();
       if (title == null || title.trim().length() == 0)
       {
           title = key;
       }
       return title;
   }

   class TitleComparator implements Comparator<String>
   {

       Map<String, CalendarModelConfiguration> base;

       public TitleComparator(Map<String, CalendarModelConfiguration> base)
       {
           this.base = base;
       }

       public int compare(String a, String b)
       {

           final String title1 = getTitle(a, base.get(a));
           final String title2 = getTitle(b, base.get(b));
           int result = title1.compareToIgnoreCase(title2);
           if (result != 0)
           {
               return result;
           }
           return a.compareToIgnoreCase(b);

       }
   }

   private boolean isEncrypted(CalendarModelConfiguration conf) {
       String encyrptionSelected = conf.getOptionMap().get(UrlEncryptionPlugin.URL_ENCRYPTION);
       return "true".equals(encyrptionSelected);
   }

   @NotNull
   protected String getBaseUrl(HttpServletRequest request) {
       return availabilityWebpage.getUrl(request, getBasePath()); // change: availabilityWebpage instead of AbstractHTMLCalendarPage, Goal: Page generator uses plugin files to generate the Webpage
   }

   @NotNull
   protected String getBasePath() {
       return "rapla/availability"; 
   }

   @GET
   //@Produces("text/html;charset=ISO-8859-1")
   @Path("{id}")
   @Produces({"text/html;UTF-8","text/calendar;UTF-8"})
   public void generatePage(@PathParam("id")  String path, @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ServletException
   {

	   try {
	        // System-Preferences laden (als Lesezugriff, da hier keine Änderung vorgenommen wird)
	        String storedUrls = facade.getSystemPreferences().getEntryAsString(AvailabilityPlugin.URLS, "");
	        List<String> urlList = new ArrayList<>();
	        if (!storedUrls.isEmpty()) {
	            urlList = new ArrayList<>(Arrays.asList(storedUrls.split(",")));
	        }
	        
	        String currentUrl = request.getRequestURL().toString();
	        System.out.println("Aktuelle URL: " + currentUrl);
	        System.out.println("Gespeicherte URLs: " + urlList);
	        
	        // Überprüfen, ob die aktuelle URL in den gespeicherten Preferences enthalten ist
	        if (!urlList.contains(currentUrl)) {
	            System.out.println("Die URL ist nicht in der Liste enthalten.");
	            String message = "404 Website not available";
	            write404(response, message);
	            return;
	        } else {
	            System.out.println("Die URL ist in der Liste enthalten.");
	        }
	    } catch (RaplaException e) {
	        e.printStackTrace();
	    }

	   StorageOperator operator = getFacade().getOperator();
	   Map<String, Object> threadContextMap = operator.getThreadContextMap();

       try
       {
           String username = "admin" ; //wird das benötigt?
           String filename = request.getParameter("file");

           CalendarSelectionModel model = null;
           User user;
           try
           {
               user = facade.getUser(username);
               
           }
           catch (EntityNotFoundException ex)
           {
               String message = "404 Calendar not available  " + username + "/" + filename;
               write404(response, message);
               logger.getChildLogger("html.404").warn("404 User not found " + username);
               return;
           }
           try
           {
               model = facade.newCalendarModel(user);
               model.load(filename);
           }
           catch (CalendarNotFoundExeption ex)
           {
               String message = "404 Calendar not available  " + user + "/" + filename;
               write404(response, message);
               return;
           }
           String allocatableId = request.getParameter("allocatable_id");
           if (allocatableId != null)
           {
               Collection<Allocatable> selectedAllocatables =model.getSelectedAllocatablesAsList();
               Allocatable foundAlloc = null;
               for (Allocatable alloc : selectedAllocatables)
               {
                   if (alloc.getId().equals(allocatableId))
                   {
                       foundAlloc = alloc;
                       break;
                   }
               }
               if (allocatableId.isEmpty() || foundAlloc != null)
               {
                   request.setAttribute("allocatable_id", allocatableId);
                   if (foundAlloc != null)
                   {
                       model.setSelectedObjects(Collections.singleton(foundAlloc));
                   }
               }
               else
               {
                   String message = "404 allocatable with id '" + allocatableId + "' not found for calendar " + user + "/" + filename;
                   write404(response, message);
                   return;
               }
           }

           final String viewId = "availabilityWebsite"; //change: model.getViewId()
           final Provider<HTMLViewPage> htmlViewPageProvider = factoryMap.get(viewId);

           if (htmlViewPageProvider != null)
           {
               HTMLViewPage currentView = htmlViewPageProvider.get();
               if (currentView != null)
               {
                   try
                   {
                       currentView.generatePage(request.getServletContext(), request, response, model);
                   }
                   catch (ServletException ex)
                   {
                       Throwable cause = ex.getCause();
                       if (cause instanceof ParseDateException)
                       {
                           write404(response, cause.getMessage() + " in calendar " + user + "/" + filename);
                       }
                       else
                       {
                           throw ex;
                       }
                   }
               }
               else
               {
                   write404(response,
                           "No view available for calendar " + user + "/" + filename + ". Rapla has currently no html support for the view with the id '"
                                   + viewId + "'.");
               }
           }
           else
           {
               writeError(response, "No view available for exportfile '" + filename + "'. Please install and select the plugin for " + viewId);
           }
       }
       catch (Exception ex)
       {
           writeStacktrace(response, ex);
           throw new ServletException(ex);
       } finally {
           threadContextMap.remove("internal_request");
       }

   }
   



   
   private void writeStacktrace(HttpServletResponse response, Exception ex) throws IOException
   {
       String charsetNonUtf = raplaLocale.getCharsetNonUtf();
       response.setContentType("text/html; charset=" + charsetNonUtf);
       java.io.PrintWriter out = response.getWriter();
       out.println(IOUtil.getStackTraceAsString(ex));
       out.close();
   }

   protected void write404(HttpServletResponse response, String message) throws IOException
   {
       response.setStatus(404);
       response.getWriter().print(message);
       logger.getChildLogger("html.404").warn(message);
       response.getWriter().close();
   }

   protected void writeUnsupported(HttpServletResponse response, String message) throws IOException
   {
       response.setStatus(415);
       response.getWriter().print(message);
       response.getWriter().close();
   }


   private void writeError(HttpServletResponse response, String message) throws IOException
   {
       response.setStatus(500);
       response.setContentType("text/html; charset=" + raplaLocale.getCharsetNonUtf());
       java.io.PrintWriter out = response.getWriter();
       out.println(message);
       out.close();
   }

}
