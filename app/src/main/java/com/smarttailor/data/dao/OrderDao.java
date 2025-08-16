package com.smarttailor.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.smarttailor.data.Order;
import com.smarttailor.data.pojos.OrderListResult;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);

    @Query("SELECT " +
           "c.Name as name, c.`Father Name` as fatherName, c.mobile as mobile, " +
           "o.id as id, o.status as status, o.remaining as remaining, o.collectiondate as collectionDate " +
           "FROM orders o INNER JOIN customers c ON o.customerId = c.Id " +
           "ORDER BY o.receievedDate DESC")
    LiveData<List<OrderListResult>> getOrderList();
}
