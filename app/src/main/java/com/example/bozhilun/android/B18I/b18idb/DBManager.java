package com.example.bozhilun.android.B18I.b18idb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;

/**
 * @aboutContent: 声明一个数据库管理者单例
 * @author： 安
 * @crateTime: 2017/9/21 09:21
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class DBManager {

    private final static String dbName = "b18i_db";
    private static DBManager mInstance;
    private static DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }


    public static DaoMaster.DevOpenHelper getOpenHelper() {
        return openHelper;
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }


    //////**************************************************

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }



    public DaoSession getDaoSession() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

//    /**
//     * 插入一条记录
//     *
//     * @param user
//     */
//    public void insertUser(UserInforDatas user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        UserInforDatasDao userDao = daoSession.getUserInforDatasDao();
//        userDao.insert(user);
//    }


//    /**
//     * 插入用户集合
//     *
//     * @param users
//     */
//    public void insertUserList(List<UserInforDatas> users) {
//        if (users == null || users.isEmpty()) {
//            return;
//        }
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        UserInforDatasDao userDao = daoSession.getUserInforDatasDao();
//        userDao.insertInTx(users);
//    }
//
//
//    /**
//     * 删除一条记录
//     *
//     * @param user
//     */
//    public void deleteUser(UserInforDatas user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        UserInforDatasDao userDao = daoSession.getUserInforDatasDao();
//        userDao.delete(user);
//    }
//
//
//    /**
//     * 更新一条记录
//     *
//     * @param user
//     */
//    public void updateUser(UserInforDatas user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        UserInforDatasDao userDao = daoSession.getUserInforDatasDao();
//        userDao.update(user);
//    }
//
//
//    /**
//     * 查询用户列表
//     */
//    public List<UserInforDatas> queryUserList() {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        UserInforDatasDao userDao = daoSession.getUserInforDatasDao();
//        QueryBuilder<UserInforDatas> qb = userDao.queryBuilder();
//        List<UserInforDatas> list = qb.list();
//        return list;
//    }
//
//    /**
//     * 查询用户列表
//     */
//    public List<UserInforDatas> queryUserList(int age) {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        UserInforDatasDao userDao = daoSession.getUserInforDatasDao();
//        QueryBuilder<UserInforDatas> qb = userDao.queryBuilder();
//        qb.where(UserInforDatasDao.Properties.Age.gt(age)).orderAsc(UserInforDatasDao.Properties.Age);
//        List<UserInforDatas> list = qb.list();
//        return list;
//    }
}
