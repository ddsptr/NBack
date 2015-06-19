package com.dds.NBack;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by dds on 17.06.15.
 */
public class GameFragment extends Fragment implements Observer{
    private Game game;
    private GridLayout gameGrid;
    private Point previousPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GridLayout gameGrid = (GridLayout) getActivity().findViewById(R.id.gameGrid);

        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        Fragment fragment = this;

        Button buttonStart = ((Button) rootView.findViewById(R.id.btnStart));
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = new Game((Observer) fragment, 2);
                new Thread(game).start();
            }
        });

        Button buttonStop = ((Button) rootView.findViewById(R.id.btnStop));
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.stop();
            }
        });

        return rootView;
    }

    @Override
    public void update(Observable observable, Object data) {
        game = (Game) observable;
        hideLastPoint();
        showNextPoint();
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
