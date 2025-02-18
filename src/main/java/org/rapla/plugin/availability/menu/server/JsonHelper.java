package org.rapla.plugin.availability.menu.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class JsonHelper {
    private static final Gson gson = new Gson();

    public static List<Availabilities> convertJsonToAvailabilitiesList(String json) {
        Type listType = new TypeToken<List<Availabilities>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
