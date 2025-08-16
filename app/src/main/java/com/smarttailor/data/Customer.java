package com.smarttailor.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    public long id;

    @ColumnInfo(name = "Name")
    public String name;

    @ColumnInfo(name = "Father Name")
    public String fatherName;

    public String address;

    @NonNull
    public String mobile;

    @ColumnInfo(name = "date_updated")
    public long dateUpdated;

    // Room requires a no-arg constructor
    public Customer() {}

    // A constructor for creating new instances
    public Customer(@NonNull String mobile, String name, String fatherName, String address) {
        this.mobile = mobile;
        this.name = name;
        this.fatherName = fatherName;
        this.address = address;
        this.dateUpdated = System.currentTimeMillis();
    }
}
