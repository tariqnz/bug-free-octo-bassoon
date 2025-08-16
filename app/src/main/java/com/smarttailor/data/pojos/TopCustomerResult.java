package com.smarttailor.data.pojos;

import androidx.room.ColumnInfo;

// This is a POJO (Plain Old Java Object), not an Entity.
// It's used to hold the aggregated results for the top customer query.
public class TopCustomerResult {
    // Fields must be public or have public getters for Room to access them.
    @ColumnInfo(name = "customerId")
    public long customerId;

    public String name;

    @ColumnInfo(name = "father name")
    public String fatherName;

    public String address;

    public String mobile;

    @ColumnInfo(name = "total")
    public double totalAmountSpent;

    @ColumnInfo(name = "count")
    public int orderCount;
}
