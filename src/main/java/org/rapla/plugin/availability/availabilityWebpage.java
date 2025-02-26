package org.rapla.plugin.availability;

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
        out.println("    .add-btn:hover { background: #0056b3; }");
        out.println("    .submit-btn { background: #28a745; color: white; }");
        out.println("    .submit-btn:hover { background: #218838; }");
        out.println("    ul { list-style: none; padding: 0; }");
        out.println("    li { background: #e9ecef; padding: 8px; margin: 5px 0; border-radius: 5px; display: flex; justify-content: space-between; align-items: center; }");
        out.println("    .delete-btn { background: red; color: white; border: none; padding: 5px; cursor: pointer; border-radius: 3px; }");
        out.println("  </style>");
        out.println("</head>");
        out.println("<body>");

        // Formular für neue Verfügbarkeiten
        out.println("  <div class='container'>");
        out.println("    <h2>Dozenten Verfügbarkeit</h2>");
        out.println("    <label for='date'>Datum:</label>");
        out.println("    <input type='date' id='date' name='date' required>");
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

        // Buttons für Hinzufügen und Absenden
        out.println("    <div class='buttons'>");
        out.println("      <button type='button' class='add-btn' onclick='addAvailability()'>+</button>");
        out.println("      <button type='button' class='submit-btn' onclick='submitAvailabilities()'>Absenden</button>");
        out.println("    </div>");

        // Anzeige der hinzugefügten Verfügbarkeiten
        out.println("    <h3>Geplante Verfügbarkeiten:</h3>");
        out.println("    <ul id='availability-list'></ul>");
        out.println("  </div>");

        // JavaScript für die Verarbeitung
        out.println("  <script>");
        out.println("    let availabilities = [];"); // Zwischenspeicher für Verfügbarkeiten

        out.println("    document.getElementById('recurrence').addEventListener('change', function() {");
        out.println("      let weeksInput = document.getElementById('weeks');");
        out.println("      if (this.value === 'weekly') {");
        out.println("        weeksInput.style.display = 'block';");
        out.println("      } else {");
        out.println("        weeksInput.style.display = 'none';");
        out.println("      }");
        out.println("    });");

        out.println("    function addAvailability() {");
        out.println("      let date = document.getElementById('date').value;");
        out.println("      let starttime = document.getElementById('starttime').value;");
        out.println("      let endtime = document.getElementById('endtime').value;");
        out.println("      let recurrence = document.getElementById('recurrence').value;");
        out.println("      let weeks = parseInt(document.getElementById('weeks').value) || 1;");

        out.println("      if (!date || !starttime || !endtime) {");
        out.println("        alert('Bitte alle Felder ausfüllen!');");
        out.println("        return;");
        out.println("      }");

        out.println("      if (recurrence === 'once') {");
        out.println("        let entry = { date, starttime, endtime };");
        out.println("        availabilities.push(entry);");
        out.println("      } else if (recurrence === 'weekly') {");
        out.println("        for (let i = 0; i < weeks; i++) { // Fügt die angegebenen Wochen hinzu");
        out.println("          let nextDate = new Date(date);");
        out.println("          nextDate.setDate(nextDate.getDate() + (7 * i)); // Wöchentlich");
        out.println("          let entry = { date: nextDate.toISOString().split('T')[0], starttime, endtime };");
        out.println("          availabilities.push(entry);");
        out.println("        }");
        out.println("      }");

        out.println("      updateAvailabilityList();");
        out.println("    }");

        out.println("    function updateAvailabilityList() {");
        out.println("      let list = document.getElementById('availability-list');");
        out.println("      list.innerHTML = '';"); // Liste leeren
        out.println("      availabilities.forEach((entry, index) => {");
        out.println("        let li = document.createElement('li');");
        out.println("        li.innerHTML = `${entry.date} von ${entry.starttime} bis ${entry.endtime} <button class='delete-btn' onclick='removeAvailability(${index})'>X</button>`;");
        out.println("        list.appendChild(li);");
        out.println("      });");
        out.println("    }");

        out.println("    function removeAvailability(index) {");
        out.println("      availabilities.splice(index, 1);"); // Eintrag aus Array löschen
        out.println("      updateAvailabilityList();");
        out.println("    }");

        out.println("    function submitAvailabilities() {");
        out.println("      if (availabilities.length === 0) { alert('Keine Verfügbarkeiten zum Absenden!'); return; }");
        out.println("      fetch('', {"); // URL leer lassen, da Request an dieselbe Seite geht
        out.println("        method: 'POST',");
        out.println("        headers: { 'Content-Type': 'application/json' },");
        out.println("        body: JSON.stringify(availabilities)");
        out.println("      }).then(response => response.text()).then(data => {");
        out.println("        alert('Verfügbarkeiten gespeichert!');");
        out.println("        availabilities = [];"); // Liste nach dem Absenden leeren
        out.println("        updateAvailabilityList();");
        out.println("      }).catch(error => alert('Fehler beim Absenden: ' + error));");
        out.println("    }");

        out.println("  </script>");

        out.println("</body>");
        out.println("</html>");
    }
}
