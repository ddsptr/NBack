package com.dds.NBack;

import android.graphics.Point;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

class Game extends Observable {
    public static final int FIELD_SIZE = 3;
    public static final int GAME_CYCLES = 20;
    public static final int TIMELAPSE = 3 * 1000;

    private Point currentPoint;
    private int level;

    public Game(Observer observer, int level){
        this.level = level;
        addObserver(observer);
    }

    public Point getRandomPoint(){
        Random random = new Random();
        return new Point(random.nextInt(FIELD_SIZE), random.nextInt(FIELD_SIZE));
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void start(){
        for (int i = 0; i < GAME_CYCLES; i++) {
            currentPoint = getRandomPoint();
            setChanged();
            notifyObservers();
            try {
                Thread.sleep(TIMELAPSE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}