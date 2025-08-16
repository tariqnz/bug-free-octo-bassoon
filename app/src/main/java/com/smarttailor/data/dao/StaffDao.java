package com.smarttailor.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.smarttailor.data.Staff;

@Dao
public interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Staff staff);

    @Query("SELECT * FROM staff WHERE mobile = :mobile LIMIT 1")
    Staff getStaffByMobile(String mobile);
}
