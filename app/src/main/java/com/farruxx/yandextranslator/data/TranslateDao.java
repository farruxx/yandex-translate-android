package com.farruxx.yandextranslator.data;

import com.farruxx.yandextranslator.model.Translate;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class TranslateDao extends BaseDaoImpl<Translate, Long> {
    public TranslateDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Translate.class);
    }

    public List<Translate> getAllTranslations() throws SQLException {
        return queryBuilder().orderBy("timestamp", false).query();
    }

    public void save(Translate translate) throws SQLException {
        QueryBuilder<Translate, Long> queryBuilder = queryBuilder();
        Where<Translate, Long> where = queryBuilder.where();
        where.eq("originLanguage", translate.originLanguage);
        where.and();
        where.eq("destLanguage", translate.destLanguage);
        where.and();
        where.eq("origin", translate.origin);
        Translate existsOne = queryBuilder.queryForFirst();
        if(existsOne != null){
            translate.id = existsOne.id;
            translate.favorite = translate.favorite || existsOne.favorite;
        }
        createOrUpdate(translate);


    }
    public boolean isInFavorites(String originLanguage, String translateLanguage, String origin) throws SQLException {
        boolean result = false;
        QueryBuilder<Translate, Long> queryBuilder = queryBuilder();
        Where<Translate, Long> where = queryBuilder.where();
        where.eq("originLanguage", originLanguage);
        where.and();
        where.eq("destLanguage", translateLanguage);
        where.and();
        where.eq("origin", origin);
        where.and();
        where.eq("favorite", true);
        Translate translate = queryBuilder.queryForFirst();
        if(translate != null){
            result = true;
        }
        return  result;
    }
    public void adjustFavorites(Translate translate) throws SQLException {
        UpdateBuilder<Translate, Long> updateBuilder = updateBuilder();
        Where<Translate, Long> where = updateBuilder.where();
        where.eq("originLanguage", translate.originLanguage);
        where.and();
        where.eq("destLanguage", translate.destLanguage);
        where.and();
        where.eq("origin", translate.origin);
        updateBuilder.updateColumnValue("favorite", translate.favorite);
        int count = updateBuilder.update();
        if(count == 0){
           save(translate);
        }

    }
}
