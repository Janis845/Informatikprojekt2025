Probieren:

package org.rapla.plugin.availability;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.rapla.RaplaResources;
import org.rapla.components.calendarview.html.AbstractHTMLView;
import org.rapla.components.util.DateTools.IncrementSize;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.AppointmentFormater;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.RaplaFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;
import org.rapla.plugin.abstractcalendar.server.AbstractHTMLCalendarPage;

public class AvailabilityWebpage extends AbstractHTMLCalendarPage {

    public AvailabilityWebpage(RaplaLocale raplaLocale, RaplaResources raplaResources, RaplaFacade facade,
                               Logger logger, AppointmentFormater appointmentFormater) {
        super(raplaLocale, raplaResources, facade, logger, appointmentFormater);
    }

    @Override
    protected AbstractHTMLView createCalendarView() throws RaplaException {
        return null;
    }

    @Override
    protected IncrementSize getIncrementSize() {
        return null;
    }

    @Override
    protected void configureView() throws RaplaException {}

    @Override
    protected void printPage(HttpServletRequest request, java.io.PrintWriter out, Date currentDate)
            throws ServletException, UnsupportedEncodingException {

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("  <title>Dozenten Verfügbarkeit</title>");
        out.println("  <meta HTTP-EQUIV='Content-Type' content='text/html; charset=UTF-8'>");
        out.println("  <style>");
        out.println("    body { font-family: Arial, sans-serif; margin: 50px; padding: 20px; }");
        out.println("    .container { max-width: 500px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 10px; background: #f9f9f9; }");
        out.println("    h2 { text-align: center; }");
        out.println("    label { font-weight: bold; display: block; margin-top: 10px; }");
        out.println("    input, select { width: 100%; padding: 8px; margin-top: 5px; border: 1px solid #ccc; border-radius: 5px; }");
        out.println("    button { width: 100%; padding: 10px; background: #28a745; color: white; border: none; border-radius: 5px; margin-top: 15px; }");
        out.println("    button:hover { background: #218838; }");
        out.println("    .remove-btn { background: red; margin-top: 5px; }");
        out.println("  </style>");
        out.println("  <script>");
        out.println("    function addTimeSlot() {");
        out.println("        let container = document.getElementById('timeSlots');");
        out.println("        let div = document.createElement('div');");
        out.println("        div.innerHTML = `<label>Zeitraum:</label>");
        out.println("        <input type='date' name='startDate[]' required> bis ");
        out.println("        <input type='date' name='endDate[]' required>");
        out.println("        <label>Uhrzeit:</label>");
        out.println("        <input type='time' name='startTime[]' required> - ");
        out.println("        <input type='time' name='endTime[]' required>");
        out.println("        <button type='button' class='remove-btn' onclick='this.parentNode.remove();'>Entfernen</button>`;");
        out.println("        container.appendChild(div);");
        out.println("    }");
        out.println("  </script>");
        out.println("</head>");
        out.println("<body>");

        out.println("  <div class='container'>");
        out.println("    <h2>Dozenten Verfügbarkeit</h2>");
        out.println("    <form action='/submitAvailability' method='post'>");
        out.println("      <label for='firstname'>Vorname:</label>");
        out.println("      <input type='text' id='firstname' name='firstname' required>");
        out.println("      <label for='lastname'>Nachname:</label>");
        out.println("      <input type='text' id='lastname' name='lastname' required>");
        out.println("      <div id='timeSlots'>");
        out.println("        <div>");
        out.println("          <label>Zeitraum:</label>");
        out.println("          <input type='date' name='startDate[]' required> bis ");
        out.println("          <input type='date' name='endDate[]' required>");
        out.println("          <label>Uhrzeit:</label>");
        out.println("          <input type='time' name='startTime[]' required> - ");
        out.println("          <input type='time' name='endTime[]' required>");
        out.println("        </div>");
        out.println("      </div>");
        out.println("      <button type='button' onclick='addTimeSlot()'>Weiteren Zeitraum hinzufügen</button>");
        out.println("      <button type='submit'>Verfügbarkeit senden</button>");
        out.println("    </form>");
        out.println("  </div>");

        out.println("</body>");
        out.println("</html>");
    }
}
