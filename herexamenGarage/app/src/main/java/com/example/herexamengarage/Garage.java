package com.example.herexamengarage;

import java.io.Serializable;

public class Garage implements Serializable {
    /* Private fields */
    private String Id;
    private String Name;
    private int Capacity;
    private int CurrentCount;
    private String Address;
    private String ImageUrl;

    /* Constructors */
    public Garage(){}

    public Garage(String name, int capacity, int currentCount, String address, String imageUrl) {
        Name = name;
        Capacity = capacity;
        CurrentCount = currentCount;
        Address = address;
        ImageUrl = imageUrl;
    }

    /* Getters and setters */
    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getCurrentCount() {
        return CurrentCount;
    }

    public void setCurrentCount(int currentCount) {
        CurrentCount = currentCount;
    }

    /* Check if the parking spot is full */
    public boolean isFull(){
        return getCurrentCount() >= getCapacity();
    }

    /* Get amount of free spots */
    public int getFreeSpots(){
        return getCapacity() - getCurrentCount();
    }
}
