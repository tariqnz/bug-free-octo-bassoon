package com.example.bottomnav1.databaseTables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Update;
import java.util.List;

// These classes are used in queries but their definitions were not provided.
// I am assuming they exist in a way that is accessible to this DAO,
// and that the necessary Customer, OrderList, TopCustomers, and
// PendingOrdersList classes are defined elsewhere.

@Dao
public interface OrderDao {
    @Insert
    long insertOrder(Order order);

    @Update
    int updateOrder(Order order);

    @Query("SELECT * FROM 'orders'")
    List<Order> getOrder();

    @Query("SELECT * FROM 'orders' WHERE id = :id")
    Order getOrderById(long id);

    @Query("SELECT * FROM customers WHERE id = :id")
    Customer getCustomerDet(long id);

    @Query("DELETE FROM 'orders' WHERE id = :id")
    int deleteOrder(long id);

    // @Query("SELECT 'Customer'.Name, Customer.Father_Name, Customer.Mobile, 'Order'.status, 'Order'.Remaining, 'Order'.collectionDate FROM 'Order' LEFT JOIN Customer On 'Order'.customerId=Customer.Id")
    //List<OrderList> getOrders();
    //@RewriteQueriesToDropUnusedColumns
    @Query("SELECT C.Id AS id,C.Name, C.Father_Name, C.Mobile, O.status, O.Remaining, O.collectionDate FROM 'orders' AS O LEFT JOIN 'customers' AS C ON O.customerId = C.Id")
    List<OrderList> getOrders();

    @Query("SELECT customers.name, customers.father_name, COUNT(orders.Id) as count, SUM(orders.total) as total, customerId FROM 'orders' LEFT JOIN 'customers' On orders.customerId = customers.Id GROUP By customerId ORDER By COUNT(orders.Id) DESC LIMIT 5")
    List<TopCustomers> getTopCustomers();

    @Query("SELECT COUNT(id) FROM 'orders'")
        //before was (id)
    int getCountTotalOrders();

    @Query("SELECT COUNT(id) FROM 'orders' WHERE status = :delivered")
    int getTotalDeliveredOrders(String delivered);

    @Query("SELECT COUNT(id) FROM 'orders' WHERE status = :cancel")
    int getTotalCanceledOrders(String cancel);

    @Query("SELECT SUM(total) FROM 'orders' WHERE status != :cancel")
    int getTotalEarning(String cancel);

    //@Query("SELECT customers.name, customers.Father_Name, customers.Mobile, orders.id, orders.Status, orders.remaining, orders.collectionDate FROM 'orders' LEFT JOIN customers On orders.customerId = customers.Id WHERE status= :InProgress ORDER By orders.collectionDate ASC LIMIT 5")
    //List<OrderList> getPendingOrders(String InProgress);
    //create by Trae IDE
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT customers.Id AS id,customers.name, customers.father_name, customers.mobile, orders.id, orders.status, orders.remaining, orders.collectionDate FROM 'orders' LEFT JOIN customers On orders.customerId = customers.Id WHERE status= :InProgress ORDER By orders.collectionDate ASC LIMIT 5")
    List<PendingOrdersList> getPendingOrders(String InProgress);
    //List<OrderList> getPendingOrders(String InProgress);

    // Corrected method:
    @Query("UPDATE `orders` SET status = :newStatus WHERE id = :id")
    // Added backticks
    void updateStatus(long id, String newStatus); // Changed 'r' to 'newStatus' for clarity
}
