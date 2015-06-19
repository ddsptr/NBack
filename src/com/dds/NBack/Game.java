package com.dds.NBack;

import android.app.Fragment;
import android.graphics.Point;

import java.util.*;

class Game extends Observable implements Runnable{
    public static final int FIELD_SIZE = 3;
    public static final int GAME_CYCLES = 20;
    public static final int TIME_LAPSE = (int)(0.5 * 1000);

    private Point currentPoint;
    private Point previousPoint;
    private int level;
    private Observer observer;
    private Queue<Point> moves = new LinkedList<>();
    private boolean stopFlag;
    private boolean checkMatchFlag;
    private Matched lastMatch = Matched.Empty;
    private Point nBackPoint;
    private int matched;
    private int mismatched;

    public Game(Observer observer, int level) {
        this.level = level;
        this.observer = observer;
        addObserver(observer);
    }

    public Point getRandomPoint() {
        Random random = new Random();
        return new Point(random.nextInt(FIELD_SIZE), random.nextInt(FIELD_SIZE));
    }

    public void start() {
        initializeNewGame();

        startGameLoop();
    }

    private void startGameLoop() {
        for (int i = 0; i < GAME_CYCLES; i++) {
            lastMatch = Matched.Empty;
            if (stopFlag){
                break;
            }
            previousPoint = currentPoint;
            currentPoint = getRandomPoint();
            moves.add(currentPoint);
            if (moves.size() > level)
            {
                nBackPoint = moves.peek();
            }
            notifyChanged();
            try {
                Thread.sleep(TIME_LAPSE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (checkMatchFlag) {
                checkMatch();
                checkMatchFlag = false;
                notifyChanged();
            }
        }
    }

    private void initializeNewGame() {
        stopFlag = false;
        checkMatchFlag = false;
        nBackPoint = null;
        matched = 0;
        mismatched = 0;
    }

    private void notifyChanged() {
        setChanged();
        ((Fragment) observer).getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyObservers();
            }
        });
    }

    public Point getPreviousPoint() {
        return previousPoint;
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void stop() {
        stopFlag = true;
    }

    public void match() {
        checkMatchFlag = true;
    }

    public Matched peekLastMatch() {
        Matched localLastMatch = lastMatch;
        lastMatch = Matched.Empty;
        return localLastMatch;
    }

    @Override
    public void run() {
        start();
    }

    private void checkMatch() {
        if (nBackPoint.equals(currentPoint)) {
            lastMatch = Matched.Match;
            matched++;
        } else {
            lastMatch = Matched.Mismatch;
            mismatched++;
        }
    }

    public int getMatched() {
        return matched;
    }

    public int getMismatched() {
        return mismatched;
    }
}

enum Matched {
    Empty,
    Match,
    Mismatch
}
