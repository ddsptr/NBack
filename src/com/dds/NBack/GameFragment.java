package com.dds.NBack;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by dds on 17.06.15.
 */
public class GameFragment extends Fragment implements Observer{
    private Game game;
    private GridLayout gameGrid;
    private Point previousPoint;
    private TextView result;

    @Override
    public void onStart() {
        super.onStart();
        gameGrid = (GridLayout) getActivity().findViewById(R.id.gameGrid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        Fragment fragment = this;

        ((Button) rootView.findViewById(R.id.btnStart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = new Game((Observer) fragment, 2);
                new Thread(game).start();
            }
        });

        ((Button) rootView.findViewById(R.id.btnStop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.stop();
            }
        });

        ((Button) rootView.findViewById(R.id.btnPosition)).setOnClickListener(new View.OnClickListener() {
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
        game = (Game) observable;
        hideLastPoint();
        showNextPoint();
        updateResult();
    }

    private void updateResult() {
        result.setText(String.format("R: %s W: %s", game.getMatched(), game.getMismatched()));
    }

    private void showNextPoint(){
        Point point = game.getCurrentPoint();
        View pointView = gameGrid.getChildAt(point.x * game.FIELD_SIZE + point.y);
        pointView.setVisibility(View.VISIBLE);
    }

    private void hideLastPoint(){
        previousPoint = game.getPreviousPoint();
        if (previousPoint != null) {
            View previousPointView = gameGrid.getChildAt(previousPoint.x * game.FIELD_SIZE + previousPoint.y);
            previousPointView.setVisibility(View.INVISIBLE);
        }
    }
}
