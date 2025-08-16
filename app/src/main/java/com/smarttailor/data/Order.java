package com.smarttailor.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "orders",
    foreignKeys = @ForeignKey(
        entity = Customer.class,
        parentColumns = "Id",
        childColumns = "customerId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("customerId")}
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long customerId;

    public long receievedDate;
    public Integer quantity;
    public Double price;
    public Double total;
    public Double advance;
    public Double remaining;
    public String collartypes;
    public String frontpocket;
    public String sidepocket;
    public String bottomtype;
    public Boolean doublestich;
    public Boolean shalwarpocket;
    public String status;
    public Long collectiondate;
}
