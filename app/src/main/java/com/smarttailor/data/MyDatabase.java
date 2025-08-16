package com.smarttailor.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.smarttailor.data.dao.CustomerDao;
import com.smarttailor.data.dao.MeasurementDao;
import com.smarttailor.data.dao.OrderDao;
import com.smarttailor.data.dao.StaffDao;

@Database(
    entities = {Customer.class, Measurement.class, Order.class, Staff.class},
    version = 1,
    exportSchema = false
)
public abstract class MyDatabase extends RoomDatabase {

    public abstract CustomerDao customerDao();
    public abstract MeasurementDao measurementDao();
    public abstract OrderDao orderDao();
    public abstract StaffDao staffDao();

    private static volatile MyDatabase INSTANCE;

    public static MyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyDatabase.class, "smart_tailor_database")
                            // In a real app, you'd want a proper migration strategy
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
