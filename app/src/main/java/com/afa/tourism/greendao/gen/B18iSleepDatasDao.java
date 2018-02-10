package com.afa.tourism.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.bozhilun.android.B18I.b18ibean.B18iSleepDatas;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "B18I_SLEEP_DATAS".
*/
public class B18iSleepDatasDao extends AbstractDao<B18iSleepDatas, Long> {

    public static final String TABLENAME = "B18I_SLEEP_DATAS";

    /**
     * Properties of entity B18iSleepDatas.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Ids = new Property(0, Long.class, "ids", true, "_id");
        public final static Property Id = new Property(1, int.class, "id", false, "ID");
        public final static Property Total = new Property(2, int.class, "total", false, "TOTAL");
        public final static Property Awake = new Property(3, int.class, "awake", false, "AWAKE");
        public final static Property Light = new Property(4, int.class, "light", false, "LIGHT");
        public final static Property Deep = new Property(5, int.class, "deep", false, "DEEP");
        public final static Property Awaketime = new Property(6, int.class, "awaketime", false, "AWAKETIME");
        public final static Property Detail = new Property(7, String.class, "detail", false, "DETAIL");
        public final static Property Date = new Property(8, String.class, "date", false, "DATE");
        public final static Property Flag = new Property(9, int.class, "flag", false, "FLAG");
        public final static Property Type = new Property(10, int.class, "type", false, "TYPE");
        public final static Property TimeStamp = new Property(11, long.class, "timeStamp", false, "TIME_STAMP");
    }


    public B18iSleepDatasDao(DaoConfig config) {
        super(config);
    }
    
    public B18iSleepDatasDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"B18I_SLEEP_DATAS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: ids
                "\"ID\" INTEGER NOT NULL ," + // 1: id
                "\"TOTAL\" INTEGER NOT NULL ," + // 2: total
                "\"AWAKE\" INTEGER NOT NULL ," + // 3: awake
                "\"LIGHT\" INTEGER NOT NULL ," + // 4: light
                "\"DEEP\" INTEGER NOT NULL ," + // 5: deep
                "\"AWAKETIME\" INTEGER NOT NULL ," + // 6: awaketime
                "\"DETAIL\" TEXT," + // 7: detail
                "\"DATE\" TEXT," + // 8: date
                "\"FLAG\" INTEGER NOT NULL ," + // 9: flag
                "\"TYPE\" INTEGER NOT NULL ," + // 10: type
                "\"TIME_STAMP\" INTEGER NOT NULL );"); // 11: timeStamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"B18I_SLEEP_DATAS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, B18iSleepDatas entity) {
        stmt.clearBindings();
 
        Long ids = entity.getIds();
        if (ids != null) {
            stmt.bindLong(1, ids);
        }
        stmt.bindLong(2, entity.getId());
        stmt.bindLong(3, entity.getTotal());
        stmt.bindLong(4, entity.getAwake());
        stmt.bindLong(5, entity.getLight());
        stmt.bindLong(6, entity.getDeep());
        stmt.bindLong(7, entity.getAwaketime());
 
        String detail = entity.getDetail();
        if (detail != null) {
            stmt.bindString(8, detail);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(9, date);
        }
        stmt.bindLong(10, entity.getFlag());
        stmt.bindLong(11, entity.getType());
        stmt.bindLong(12, entity.getTimeStamp());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, B18iSleepDatas entity) {
        stmt.clearBindings();
 
        Long ids = entity.getIds();
        if (ids != null) {
            stmt.bindLong(1, ids);
        }
        stmt.bindLong(2, entity.getId());
        stmt.bindLong(3, entity.getTotal());
        stmt.bindLong(4, entity.getAwake());
        stmt.bindLong(5, entity.getLight());
        stmt.bindLong(6, entity.getDeep());
        stmt.bindLong(7, entity.getAwaketime());
 
        String detail = entity.getDetail();
        if (detail != null) {
            stmt.bindString(8, detail);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(9, date);
        }
        stmt.bindLong(10, entity.getFlag());
        stmt.bindLong(11, entity.getType());
        stmt.bindLong(12, entity.getTimeStamp());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public B18iSleepDatas readEntity(Cursor cursor, int offset) {
        B18iSleepDatas entity = new B18iSleepDatas( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // ids
            cursor.getInt(offset + 1), // id
            cursor.getInt(offset + 2), // total
            cursor.getInt(offset + 3), // awake
            cursor.getInt(offset + 4), // light
            cursor.getInt(offset + 5), // deep
            cursor.getInt(offset + 6), // awaketime
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // detail
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // date
            cursor.getInt(offset + 9), // flag
            cursor.getInt(offset + 10), // type
            cursor.getLong(offset + 11) // timeStamp
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, B18iSleepDatas entity, int offset) {
        entity.setIds(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.getInt(offset + 1));
        entity.setTotal(cursor.getInt(offset + 2));
        entity.setAwake(cursor.getInt(offset + 3));
        entity.setLight(cursor.getInt(offset + 4));
        entity.setDeep(cursor.getInt(offset + 5));
        entity.setAwaketime(cursor.getInt(offset + 6));
        entity.setDetail(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDate(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFlag(cursor.getInt(offset + 9));
        entity.setType(cursor.getInt(offset + 10));
        entity.setTimeStamp(cursor.getLong(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(B18iSleepDatas entity, long rowId) {
        entity.setIds(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(B18iSleepDatas entity) {
        if(entity != null) {
            return entity.getIds();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(B18iSleepDatas entity) {
        return entity.getIds() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
