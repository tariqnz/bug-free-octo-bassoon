package com.smarttailor.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "measurements",
    foreignKeys = @ForeignKey(
        entity = Customer.class,
        parentColumns = "Id",
        childColumns = "customerId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "customerId", unique = true)}
)
public class Measurement {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long customerId;

    public Double kameezLength;
    public Double armLenth;
    public Double teera;
    public Double neck;
    public Double chest;
    public Double waist;
    public Double ghera;
    public Double shoulder;
    public Double shalwarLenth;
    public Double paincha;
    public Double cafsize;
}
