package org.rapla.plugin.availability.server;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.rapla.RaplaResources;
import org.rapla.components.calendarview.html.AbstractHTMLView;
import org.rapla.components.util.DateTools;
import org.rapla.components.util.DateTools.IncrementSize;
import org.rapla.entities.domain.AppointmentFormater;
import org.rapla.facade.RaplaFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;
import org.rapla.plugin.abstractcalendar.server.AbstractHTMLCalendarPage;

public class availabilityWebpage extends AbstractHTMLCalendarPage {

    public availabilityWebpage(RaplaLocale raplaLocale, RaplaResources raplaResources, RaplaFacade facade,
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
    protected void configureView() throws RaplaException {
    }

    @Override
    protected void printPage(HttpServletRequest request, PrintWriter out, Date currentDate)
            throws ServletException, UnsupportedEncodingException {

    	out.println("<!DOCTYPE html>");
    	out.println("<html>");
    	out.println("<head>");
    	out.println("  <title>Dozenten Verfügbarkeit</title>");
    	out.println("  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
    	out.println("  <style>");
    	out.println("    body { font-family: Arial, sans-serif; margin: 50px; padding: 20px; }");
    	out.println("    .container { max-width: 400px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 10px; background: #f9f9f9; }");
    	out.println("    h2 { text-align: center; }");
    	out.println("    label { font-weight: bold; display: block; margin-top: 10px; }");
    	out.println("    input, select { width: 100%; padding: 8px; margin-top: 5px; border: 1px solid #ccc; border-radius: 5px; }");
    	out.println("    .buttons { display: flex; gap: 10px; margin-top: 15px; }");
    	out.println("    button { flex: 1; padding: 10px; border: none; border-radius: 5px; cursor: pointer; }");
    	out.println("    .add-btn { background: #007bff; color: white; }");
    	out.println("    .submit-btn { background: #28a745; color: white; }");
    	out.println("    ul { list-style: none; padding: 0; }");
    	out.println("    li { background: #e9ecef; padding: 8px; margin: 5px 0; border-radius: 5px; display: flex; justify-content: space-between; align-items: center; }");
    	out.println("  </style>");
    	out.println("</head>");
    	out.println("<body>");

    	out.println("  <div class='container'>");
    	out.println("    <h2>Dozenten Verfügbarkeit</h2>");
    	out.println("    <label for='date'>Datum:</label>");
    	out.println("    <input type='date' id='date' name='date' required>");
    	out.println("    <label for='raplaID' style='display:none;'>RaplaID:</label>");
    	out.println("    <input type='hidden' id='raplaID' name='raplaID' required>");
    	out.println("    <script>");
    	out.println("      const urlParts = window.location.href.split('/');");
    	out.println("      document.getElementById('raplaID').value = urlParts[urlParts.length - 1];");
    	out.println("    </script>");
    	out.println("    <label for='starttime'>Startzeit:</label>");
    	out.println("    <input type='time' id='starttime' name='starttime' required>");
    	out.println("    <label for='endtime'>Endzeit:</label>");
    	out.println("    <input type='time' id='endtime' name='endtime' required>");
    	out.println("    <label for='recurrence'>Wiederholung:</label>");
    	out.println("    <select id='recurrence' name='recurrence'>");
    	out.println("      <option value='once'>Einmal</option>");
    	out.println("      <option value='weekly'>Wöchentlich</option>");
    	out.println("    </select>");
    	out.println("    <label for='weeks'>Anzahl der Wochen:</label>");
    	out.println("    <input type='number' id='weeks' name='weeks' min='1' value='1' style='display:none;' required>");

    	out.println("    <div class='buttons'>");
    	out.println("      <button type='button' class='add-btn' onclick='addAvailability()'>+</button>");
    	out.println("      <button type='button' class='submit-btn'>Absenden</button>");
    	out.println("    </div>");

    	out.println("    <h3>Geplante Verfügbarkeiten:</h3>");
    	out.println("    <ul id='availability-list'></ul>");
    	out.println("  </div>");

    	out.println("  <script>");
    	out.println("    let availabilities = [];");

    	out.println("    document.getElementById('recurrence').addEventListener('change', function() {");
    	out.println("      let weeksInput = document.getElementById('weeks');");
    	out.println("      weeksInput.style.display = (this.value === 'weekly') ? 'block' : 'none';");
    	out.println("    });");

    	out.println("    function addAvailability() {");
    	out.println("      let date = document.getElementById('date').value;");
    	out.println("      let raplaID = document.getElementById('raplaID').value;");
    	out.println("      let starttime = document.getElementById('starttime').value;");
    	out.println("      let endtime = document.getElementById('endtime').value;");
    	out.println("      let recurrence = document.getElementById('recurrence').value;");
    	out.println("      let weeks = parseInt(document.getElementById('weeks').value) || 1;");

    	out.println("      if (!date || !starttime || !endtime || !raplaID) {");
    	out.println("        alert('Bitte alle Felder ausfüllen!');");
    	out.println("        return;");
    	out.println("      }");

    	out.println("      console.log('Neue Verfügbarkeit hinzugefügt:', { date, starttime, endtime, recurrence, weeks, raplaID });");

    	out.println("      if (recurrence === 'once') {");
    	out.println("        availabilities.push({ date, starttime, endtime, raplaID });");
    	out.println("      } else {");
    	out.println("        for (let i = 0; i < weeks; i++) {");
    	out.println("          let nextDate = new Date(date);");
    	out.println("          nextDate.setDate(nextDate.getDate() + (7 * i));");
    	out.println("          availabilities.push({ date: nextDate.toISOString().split('T')[0], starttime, endtime, raplaID });");
    	out.println("        }");
    	out.println("      }");

    	out.println("      updateAvailabilityList();");
    	out.println("    }");

    	out.println("    function updateAvailabilityList() {");
    	out.println("      let list = document.getElementById('availability-list');");
    	out.println("      list.innerHTML = '';");
    	out.println("      availabilities.forEach((entry, index) => {");
    	out.println("        let li = document.createElement('li');");
    	out.println("        li.innerHTML = `${entry.date} von ${entry.starttime} bis ${entry.endtime} <button class='delete-btn' onclick='removeAvailability(${index})'>X</button>`;");
    	out.println("        list.appendChild(li);");
    	out.println("      });");
    	out.println("    }");

    	out.println("    function removeAvailability(index) {");
    	out.println("      console.log('Eintrag entfernt:', availabilities[index]);");
    	out.println("      availabilities.splice(index, 1);");
    	out.println("      updateAvailabilityList();");
    	out.println("    }");

    	out.println("    document.querySelector('.submit-btn').addEventListener('click', function() {");
    	out.println("      console.log('Submit-Button gedrückt');");
    	out.println("      if (availabilities.length === 0) { alert('Keine Verfügbarkeiten zum Absenden!'); return; }");
    	out.println("      console.log('Sende Daten an Server:', JSON.stringify(availabilities));");

    	out.println("      fetch('/rapla/availability', {");
    	out.println("        method: 'POST',");
    	out.println("        headers: { 'Content-Type': 'application/json' },");
    	out.println("        body: JSON.stringify(availabilities)");
    	out.println("      }).then(response => response.text()).then(data => {");
    	out.println("        console.log('Server Antwort:', data);");
    	out.println("        alert('Verfügbarkeiten gespeichert!');");
    	out.println("        availabilities = [];");
    	out.println("        updateAvailabilityList();");
    	out.println("      }).catch(error => {");
    	out.println("        console.error('Fehler beim Absenden:', error);");
    	out.println("        alert('Fehler beim Absenden: ' + error);");
    	out.println("      });");
    	out.println("    });");
    	out.println("  </script>");
    	out.println("</body>");
    	out.println("</html>");

    }
}
