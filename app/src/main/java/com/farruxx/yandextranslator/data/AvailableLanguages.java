package com.farruxx.yandextranslator.data;

import com.farruxx.yandextranslator.model.TranslateDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Farruxx on 13.04.2017.
 */
//Holds available languages at runtime
public class AvailableLanguages {

    private final HashMap<String, TranslateDirection> languageLabels;
    private final LinkedHashMap<String, ArrayList<String>> languageDirections;
    public boolean swap;

    //create and parse response
    public AvailableLanguages(JSONObject languagesResult) throws JSONException {
        JSONArray dirs = languagesResult.getJSONArray("dirs");
        JSONObject langs = languagesResult.getJSONObject("langs");
        languageLabels = new HashMap<>();
        languageDirections = new LinkedHashMap<>();

        Iterator<String> keys = langs.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            languageLabels.put(key, new TranslateDirection(key, langs.getString(key)));
        }

        for (int i = 0; i < dirs.length(); i++) {
            String item = dirs.getString(i);
            String[] parts = item.split("-");
            String key = parts[0];
            ArrayList<String> availableLanguages = languageDirections.get(key);
            if (availableLanguages == null) {
                availableLanguages = new ArrayList<>();
                availableLanguages.add(parts[1]);
                languageDirections.put(key, availableLanguages);
            } else {
                availableLanguages.add(parts[1]);
            }
        }
    }
    //get available dest languages depending orign languages
    public List<TranslateDirection> getAvailableDirections(String originDirection) {
        ArrayList<String> availableDirections = languageDirections.get(originDirection);
        ArrayList<TranslateDirection> result = new ArrayList<>();
        for (int i = 0; i < availableDirections.size(); i++) {
            String direction = availableDirections.get(i);
            result.add(languageLabels.get(direction));
        }
        Collections.sort(result, (o1, o2) -> o1.name.compareTo(o2.name));
        return result;
    }

    //get available origin languages
    public List<TranslateDirection> getOriginDirections() {
        Set<String> directions = languageDirections.keySet();
        ArrayList<TranslateDirection> result = new ArrayList<>();
        for (String direction : directions) {
            result.add(languageLabels.get(direction));
        }
        Collections.sort(result, (o1, o2) -> o1.name.compareTo(o2.name));
        return result;
    }

}
