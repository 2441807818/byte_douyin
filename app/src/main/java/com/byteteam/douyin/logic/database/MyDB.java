package com.byteteam.douyin.logic.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.byteteam.douyin.logic.database.converter.StringArrayConverters;
import com.byteteam.douyin.logic.database.dao.AccessTokenDao;
import com.byteteam.douyin.logic.database.dao.ClientTokenDao;
import com.byteteam.douyin.logic.database.dao.FollowItemDao;
import com.byteteam.douyin.logic.database.dao.MyFansDao;
import com.byteteam.douyin.logic.database.dao.RankItemDao;
import com.byteteam.douyin.logic.database.dao.RankListDao;
import com.byteteam.douyin.logic.database.dao.UserDao;
import com.byteteam.douyin.logic.database.dao.WorksDao;
import com.byteteam.douyin.logic.database.model.AccessToken;
import com.byteteam.douyin.logic.database.model.ClientToken;
import com.byteteam.douyin.logic.database.model.FollowItem;
import com.byteteam.douyin.logic.database.model.MyFans;
import com.byteteam.douyin.logic.database.model.RankItem;
import com.byteteam.douyin.logic.database.model.RankList;
import com.byteteam.douyin.logic.database.model.User;
import com.byteteam.douyin.logic.database.model.Works;

/**
 * @introduction： 数据库类
 * @author： 林锦焜
 * @time： 2022/8/7 18:10
 */
@Database(entities = {AccessToken.class, ClientToken.class, RankItem.class, RankList.class
        , User.class, FollowItem.class, Works.class, MyFans.class}, version = 10)
@TypeConverters({StringArrayConverters.class})
public abstract class MyDB extends RoomDatabase {

    public abstract AccessTokenDao accessTokenDao();

    public abstract ClientTokenDao clientTokenDao();

    public abstract RankItemDao rankItemDao();

    public abstract RankListDao rankListDao();

    public abstract UserDao userDao();

    public abstract WorksDao worksDao();

    public abstract FollowItemDao fansItemDao();

    public abstract MyFansDao myFansDao();


    private static volatile MyDB INSTANCE;

    public static MyDB get(Context context) {
        if (INSTANCE == null) {
            synchronized (MyDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyDB.class, "my-database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
