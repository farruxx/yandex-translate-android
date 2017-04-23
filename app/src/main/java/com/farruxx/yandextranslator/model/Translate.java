package com.farruxx.yandextranslator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Farruxx on 23.04.2017.
 */
@DatabaseTable
public class Translate {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String originLanguage;

    @DatabaseField
    public String destLanguage;

    @DatabaseField
    public String dest;

    @DatabaseField
    public String origin;

    @DatabaseField
    public long timestamp;

    @DatabaseField
    public boolean favorite;

    public Translate() {
    }

    public Translate(String originLanguage, String destLanguage, String origin, String dest, long timestamp) {
        this.originLanguage = originLanguage;
        this.destLanguage = destLanguage;
        this.origin = origin;
        this.dest = dest;
        this.timestamp = timestamp;
    }

    public Translate withFavorites(boolean checked) {
        favorite = checked;
        return this;
    }
}
