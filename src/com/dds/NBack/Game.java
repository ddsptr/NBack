package com.dds.NBack;

import android.app.Fragment;
import android.graphics.Point;

import java.util.*;

class Game extends Observable implements Runnable{
    public static final int FIELD_SIZE = 3;
    public static final int GAME_CYCLES = 20;
    public static final int TIMELAPSE = (int)(0.5 * 1000);

    private Point currentPoint;
    private Point previousPoint;
    private int level;
    private Observer observer;
    private Queue<Point> moves = new LinkedList<>();
    private boolean stopFlag;

    public Game(Observer observer, int level){
        this.level = level;
        this.observer = observer;
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
        stopFlag = false;
        for (int i = 0; i < GAME_CYCLES; i++) {
            if (stopFlag){
                break;
            }
            previousPoint = currentPoint;
            currentPoint = getRandomPoint();
            moves.add(currentPoint);
            setChanged();
            ((Fragment) observer).getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyObservers();
                }
            });
            try {
                Thread.sleep(TIMELAPSE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        stopFlag = true;
    }

    public Point getPreviousPoint() {
        return previousPoint;
    }

    @Override
    public void run() {
        start();
    }
}