package com.smarttailor.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "staff")
public class Staff {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    @ColumnInfo(name = "father name")
    public String fatherName;

    @NonNull
    public String mobile;

    @NonNull
    public String password; // In a real app, this should be a securely hashed password.

    @ColumnInfo(name = "date_updated")
    public long dateUpdated;

    // Constructor
    public Staff() {}
}
