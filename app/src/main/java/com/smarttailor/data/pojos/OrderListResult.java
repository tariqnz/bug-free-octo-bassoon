package com.smarttailor.data.pojos;

import androidx.room.ColumnInfo;

// This is a POJO (Plain Old Java Object), not an Entity.
// It's used to hold the results of a JOIN query for the order list.
public class OrderListResult {
    // Fields must be public or have public getters for Room to access them.
    public String name;

    @ColumnInfo(name = "father name")
    public String fatherName;

    public String mobile;

    @ColumnInfo(name = "id")
    public long orderId;

    public String status;

    public Double remaining;

    public Long collectionDate;
}
