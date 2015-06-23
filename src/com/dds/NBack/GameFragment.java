package com.dds.NBack;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    private Animation animationShow;
    private Animation animationHide;
    private boolean paused;
    private AlertDialog alertDialog;
    private Button btnPosition;
    private AnimatorSet positionAnimator;

    @Override
    public void onStart() {
        super.onStart();
        gameGrid = (GridLayout) getActivity().findViewById(R.id.gameGrid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        Fragment fragment = this;
        game = new Game((Observer) fragment);

        View btnStartPause = rootView.findViewById(R.id.btnStartPause);
        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.initializeParams(2, TimeLapse.SLOW);
                new Thread(game).start();
//                if (game.getState() == State.STOPPED) {
//                    if (previousPoint != null) {
//                        getViewAtPoint(previousPoint).setVisibility(View.INVISIBLE);
//                    }
//                    game.initializeParams(2, TimeLapse.SLOW);
//                    new Thread(game).start();
//                    ((Button) btnStartPause).setText("PAUSE");
//                } else if (game.getState() != State.PAUSED) {
//                    ((Button) btnStartPause).setText("RESUME");
//                    game.pause();
//                } else if (game.getState() == State.PAUSED) {
//                    ((Button) btnStartPause).setText("START");
//                    game.pause();
//                }
            }
        });

        rootView.findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //game.stop();
                game.pause();
            }
        });

        btnPosition = (Button) rootView.findViewById(R.id.btnPosition);
//        btnPosition.
        positionAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flash);
        positionAnimator.setTarget(btnPosition);

        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.match();
            }
        });

        result = (TextView) rootView.findViewById(R.id.tvResult);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animationShow = AnimationUtils.loadAnimation(getActivity(), R.anim.point_show);
        animationHide = AnimationUtils.loadAnimation(getActivity(), R.anim.point_hide);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
            matched = gameDto.getLastMatch();
            if (matched == Matched.MATCH) {
                flashButtonGreen();
            }
        } else if (gameDto.getState() == State.PAUSED) {
//            pause();
            Log.d(LOG_TAG,"PAUSED");
        } else if (gameDto.getState() == State.STOPPED) {
            Log.d(LOG_TAG,"STOPPED");
        }
        updateResult();
    }

    private void updateResult() {
        Matched matched = gameDto.getLastMatch();
        if (matched == Matched.MATCH) {
            result.setTextColor(getResources().getColor(R.color.green));
        } else if (matched == Matched.MISMATCH) {
            result.setTextColor(getResources().getColor(R.color.red));
            flashButtonGreen();
        } else {
            result.setTextColor(getResources().getColor(R.color.black));
        }
        result.setText(String.format("R: %s W: %s", gameDto.getMatched(), gameDto.getMismatched()));
    }

    private void showPoint(){
        getViewAtPoint(gameDto.getCurrentPoint()).startAnimation(animationShow);
        getViewAtPoint(gameDto.getCurrentPoint()).setVisibility(View.VISIBLE);
    }

    private void hidePoint(){
        getViewAtPoint(gameDto.getCurrentPoint()).startAnimation(animationHide);
        getViewAtPoint(gameDto.getCurrentPoint()).setVisibility(View.INVISIBLE);
    }

    private View getViewAtPoint(Point point) {
        return gameGrid.getChildAt(point.x * game.FIELD_SIZE + point.y);
    }

    private void pause() {
        paused = true;
        alertDialog = new AlertDialog.Builder(getActivity())
                //.setTitle("PAUSED!")
                .setMessage("PAUSED!")
                //.
                .create();
        alertDialog.show();
    }

    private void flashButtonGreen() {
        positionAnimator.start();
    }
}
