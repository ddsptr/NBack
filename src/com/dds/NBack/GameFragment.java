package com.dds.NBack;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.Observable;
import java.util.Observer;
import java.util.zip.Inflater;

/**
 * Created by dds on 17.06.15.
 */
public class GameFragment extends Fragment implements Observer{
    private Game game;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        Button button = ((Button) rootView.findViewById(R.id.btnStart));
    }

    public void update(Observable observable, Object data) {
        game = (Game) observable;
        Point point = game.getCurrentPoint();
        GridLayout gameGrid = (GridLayout) getActivity().findViewById(R.id.gameGrid);
        View pointView = gameGrid.getChildAt(point.x * game.FIELD_SIZE + point.y);
        pointView.setBackgroundColor(getResources().getColor(R.color.black));
    }
}
