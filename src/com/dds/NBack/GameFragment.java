package com.dds.NBack;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by dds on 17.06.15.
 */
public class GameFragment extends Fragment implements Observer{
    private final static String LOG_TAG = GameFragment.class.getSimpleName();
    private Game game;
    private GameDto gameDto;
    private GridLayout gameGrid;
    private Point previousPoint;
    private TextView result;
    private Matched matched;

    @Override
    public void onStart() {
        super.onStart();
        gameGrid = (GridLayout) getActivity().findViewById(R.id.gameGrid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        Fragment fragment = this;

        rootView.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousPoint != null) {
                    getViewAtPoint(previousPoint).setVisibility(View.INVISIBLE);
                }
                game = new Game((Observer) fragment);
                game.initializeParams(2, TimeLapse.FAST);
                new Thread(game).start();
            }
        });

        rootView.findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.stop();
                if (previousPoint != null) {
                    getViewAtPoint(previousPoint).setVisibility(View.INVISIBLE);
                }
                previousPoint = null;
            }
        });

        rootView.findViewById(R.id.btnPosition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.match();
            }
        });

        result = (TextView) rootView.findViewById(R.id.tvResult);

        return rootView;
    }

    @Override
    public void update(Observable observable, Object data) {
        gameDto = (GameDto) data;
        if (gameDto.getState() == State.SHOWING_POINT) {
            Log.d(LOG_TAG,"SHOWING_POINT");
            showPoint();
        } else if (gameDto.getState() == State.HIDING_POINT) {
            Log.d(LOG_TAG,"HIDING_POINT");
            hidePoint();
        } else if (gameDto.getState() == State.MATCHING) {
            Log.d(LOG_TAG,"MATCHING");
            matched = game.peekLastMatch();
        } else if (gameDto.getState() == State.STOPPED) {
            Log.d(LOG_TAG,"STOPPED");
        }
        updateResult();
    }

    private void updateResult() {
        if (matched == Matched.MATCH) {
            result.setTextColor(getResources().getColor(R.color.green));
        } else if (matched == Matched.MISMATCH) {
            result.setTextColor(getResources().getColor(R.color.red));
        } else {
            result.setTextColor(getResources().getColor(R.color.black));
        }
        result.setText(String.format("R: %s W: %s", gameDto.getMatched(), gameDto.getMismatched()));
    }

    private void showPoint(){
        getViewAtPoint(gameDto.getCurrentPoint()).setVisibility(View.VISIBLE);
    }

    private void hidePoint(){
        getViewAtPoint(gameDto.getCurrentPoint()).setVisibility(View.INVISIBLE);
    }

    private View getViewAtPoint(Point point) {
        return gameGrid.getChildAt(point.x * game.FIELD_SIZE + point.y);
    }
}
