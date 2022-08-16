package com.byteteam.douyin.logic.factory;

import android.content.Context;

import com.byteteam.douyin.logic.database.MyDB;
import com.byteteam.douyin.logic.database.dao.AccessTokenDao;
import com.byteteam.douyin.logic.database.dao.ClientTokenDao;
import com.byteteam.douyin.logic.database.dao.RankItemDao;
import com.byteteam.douyin.logic.database.dao.RankListDao;

/**
 * @introduction： Dao工厂类
 * @author： 林锦焜
 * @time： 2022/8/7 18:11
 */
public class DaoFactory {

    public static MyDB providerAppDatabase(Context context) {
        return MyDB.get(context);
    }

    public static AccessTokenDao providerAccessTokenDao(Context context) {
        return providerAppDatabase(context).accessTokenDao();
    }

    public static ClientTokenDao providerClientTokenDao(Context context) {
        return providerAppDatabase(context).clientTokenDao();
    }

    public static RankItemDao providerRankItemDao(Context context) {
        return providerAppDatabase(context).rankItemDao();
    }

    public static RankListDao providerRankListDao(Context context) {
        return providerAppDatabase(context).rankListDao();
    }

}