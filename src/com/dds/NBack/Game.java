package com.dds.NBack;

import android.app.Fragment;
import android.graphics.Point;
import android.util.Log;

import java.util.*;

class Game extends Observable implements Runnable{
    private final static String LOG_TAG = Game.class.getSimpleName();
    public static final int FIELD_SIZE = 3;
    public static final int GAME_CYCLES = 20;
    public static final int TIME_LAPSE_FAST = (int)(1 * 1000);
    public static final int TIME_LAPSE_MIDDLE = (int)(2 * 1000);
    public static final int TIME_LAPSE_SLOW = (int)(3 * 1000);

    private Point currentPoint;
    private Point previousPoint;
    private int level;
    private Observer observer;
    private Queue<Point> moves = new LinkedList<>();
    private boolean stopFlag;
    private boolean checkMatchFlag;
    private Matched lastMatch = Matched.EMPTY;
    private Point nBackPoint;
    private int matched;
    private int mismatched;
    private int timeLapse;
    private boolean initializedParams = false;
    private State state = State.STOPPED;

    public Game(Observer observer) {
        this.observer = observer;
        addObserver(observer);
    }

    public Point getRandomPoint() {
        Random random = new Random();
        return new Point(random.nextInt(FIELD_SIZE), random.nextInt(FIELD_SIZE));
    }

    public void start() {
        initialize();
        startGameLoop();
    }

    private void startGameLoop() {
        for (int i = 0; i < GAME_CYCLES; i++) {
            lastMatch = Matched.EMPTY;
            if (stopFlag){
                break;
            }
            previousPoint = currentPoint;
            currentPoint = getRandomPoint();
            state = State.SHOWING_POINT;
            Log.d(LOG_TAG, "SHOWING_POINT");
            notifyStateChanged();
            moves.add(currentPoint);
            if (moves.size() > level)
            {
                nBackPoint = moves.remove();
            }
            try {
                Thread.sleep(timeLapse);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            state = State.HIDING_POINT;
            Log.d(LOG_TAG, "HIDING_POINT");
            notifyStateChanged();
            if (checkMatchFlag) {
                checkMatch();
                state = State.MATCHING;
                Log.d(LOG_TAG, "MATCHING");
                notifyStateChanged();
                checkMatchFlag = false;
            }
        }
        previousPoint = currentPoint;
        state = State.STOPPED;
        Log.d(LOG_TAG, "STOPPED");
        notifyStateChanged();
    }

    public void initializeParams(int level, TimeLapse timeLapse){
        this.level = level;
        switch (timeLapse){
            case FAST:
                this.timeLapse = TIME_LAPSE_FAST;
                break;
            case MEDIUM:
                this.timeLapse = TIME_LAPSE_MIDDLE;
                break;
            case SLOW:
                this.timeLapse = TIME_LAPSE_SLOW;
                break;
            default:
                this.timeLapse = TIME_LAPSE_SLOW;
        }
        initializedParams = true;
    }

    private void initialize() {
        if (!initializedParams) {
            throw new IllegalStateException();
        }
        stopFlag = false;
        checkMatchFlag = false;
        nBackPoint = null;
        matched = 0;
        mismatched = 0;
    }

    private void notifyStateChanged() {
        GameDto gameDto = toDto();
        ((Fragment) observer).getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyObservers(gameDto);
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
        lastMatch = Matched.EMPTY;
        return localLastMatch;
    }

    @Override
    public void run() {
        start();
    }

    private void checkMatch() {
        if (nBackPoint.equals(currentPoint)) {
            lastMatch = Matched.MATCH;
            matched++;
        } else {
            lastMatch = Matched.MISMATCH;
            mismatched++;
        }
    }

    public int getMatched() {
        return matched;
    }

    public int getMismatched() {
        return mismatched;
    }

    public State getState() {
        return state;
    }

    private GameDto toDto(){
        GameDto gameDto = new GameDto(currentPoint, matched, mismatched, state, lastMatch);
        return gameDto;
    }
}

enum Matched {
    EMPTY,
    MATCH,
    MISMATCH
}

enum TimeLapse {
    FAST,
    MEDIUM,
    SLOW
}

enum State {
    SHOWING_POINT,
    HIDING_POINT,
    MATCHING,
    STOPPED
}