package com.example.bottomnav1.databaseTables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    private long Id;
    @ColumnInfo(name = "customerId")
    private long customerId;
    @ColumnInfo(name = "receivedDate")
    private long dated;
    @ColumnInfo(name = "quantity")
    private int quantity;
    @ColumnInfo(name = "price")
    private int price;
    @ColumnInfo(name = "total")
    private int total;
    @ColumnInfo(name = "advance")
    private int advance;
    @ColumnInfo(name = "remaining")
    private int remaining;
    @ColumnInfo(name = "collarTypes")
    private int collar;
    @ColumnInfo(name = "frontPocket")
    private boolean frontPocket;
    @ColumnInfo(name = "sidePocket")
    private int sidePocket;
    @ColumnInfo(name = "bottomType")
    private int bottom;
    @ColumnInfo(name = "doubleStich")
    private boolean doubleStich;
    @ColumnInfo(name = "shalwarPocket")
    private boolean shalwarPocket;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "collectionDate")
    private long collectionDate;

    public Order() {
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getDated() {
        return dated;
    }

    public void setDated(long dated) {
        this.dated = dated;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAdvance() {
        return advance;
    }

    public void setAdvance(int advance) {
        this.advance = advance;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getCollar() {
        return collar;
    }

    public void setCollar(int collar) {
        this.collar = collar;
    }

    public boolean isFrontPocket() {
        return frontPocket;
    }

    public void setFrontPocket(boolean frontPocket) {
        this.frontPocket = frontPocket;
    }

    public int getSidePocket() {
        return sidePocket;
    }

    public void setSidePocket(int sidePocket) {
        this.sidePocket = sidePocket;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public boolean isDoubleStich() {
        return doubleStich;
    }

    public void setDoubleStich(boolean doubleStich) {
        this.doubleStich = doubleStich;
    }

    public boolean isShalwarPocket() {
        return shalwarPocket;
    }

    public void setShalwarPocket(boolean shalwarPocket) {
        this.shalwarPocket = shalwarPocket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(long collectionDate) {
        this.collectionDate = collectionDate;
    }

    public int IsBottom() {
        return bottom;
    }
}
