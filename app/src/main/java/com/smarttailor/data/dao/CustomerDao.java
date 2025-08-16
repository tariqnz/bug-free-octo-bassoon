package com.smarttailor.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.smarttailor.data.Customer;
import com.smarttailor.data.pojos.TopCustomerResult;

import java.util.List;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Customer customer);

    @Update
    void update(Customer customer);

    @Query("SELECT * FROM customers ORDER BY Name ASC")
    LiveData<List<Customer>> getAllCustomers();

    @Query("SELECT " +
           "c.Id as customerId, c.Name as name, c.`Father Name` as fatherName, c.address as address, c.mobile as mobile, " +
           "SUM(o.total) as total, COUNT(o.id) as count " +
           "FROM customers c JOIN orders o ON c.Id = o.customerId " +
           "WHERE o.status = 'Completed' " + // Assuming 'Completed' is a valid status
           "GROUP BY c.Id " +
           "ORDER BY total DESC")
    LiveData<List<TopCustomerResult>> getTopCustomers();
}
