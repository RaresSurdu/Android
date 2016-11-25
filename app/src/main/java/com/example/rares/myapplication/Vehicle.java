package com.example.rares.myapplication;

import java.io.Serializable;

/**
 * Created by Rares on 05.11.2016.
 */

public class Vehicle implements Serializable{

    private String make;
    private String model;
    private int capacity;

    public Vehicle(String make, String model, int capacity) {
        this.make = make;
        this.model = model;
        this.capacity = capacity;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return  make ;
    }
}
