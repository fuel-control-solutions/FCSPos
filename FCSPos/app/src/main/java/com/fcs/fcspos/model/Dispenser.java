package com.fcs.fcspos.model;

import android.print.PrinterId;

import java.util.ArrayList;

public class Dispenser {

    private String brand;
    private byte numberOfDigits;
    private byte decimalsInVolume;
    private ArrayList<Side> sides;


    public Dispenser(String brand, byte numberOfDigits, byte decimalsInVolume){
        this.brand = brand;
        this.numberOfDigits = numberOfDigits;
        this.decimalsInVolume = decimalsInVolume;
    }

    public void setSides(ArrayList<Side> sides) {
        this.sides = sides;
    }

    public ArrayList<Side> getSides() {
        return sides;
    }
}
