package com.smarttailor.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.smarttailor.data.Measurement;

@Dao
public interface MeasurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Measurement measurement);

    @Query("SELECT * FROM measurements WHERE customerId = :customerId LIMIT 1")
    Measurement getMeasurementForCustomer(long customerId);
}
