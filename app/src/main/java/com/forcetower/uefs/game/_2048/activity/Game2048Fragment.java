package com.forcetower.uefs.game._2048.activity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.GameFragment2048Binding;
import com.forcetower.uefs.game._2048.tools.InputListener;
import com.forcetower.uefs.game._2048.tools.KeyListener;
import com.forcetower.uefs.game._2048.tools.ScoreKeeper;
import com.forcetower.uefs.game._2048.view.Game;
import com.forcetower.uefs.game._2048.view.Tile;
import com.forcetower.uefs.view.UBaseActivity;

import timber.log.Timber;

/**
 * Created by João Paulo on 02/06/2018.
 */
public class Game2048Fragment extends Fragment implements KeyListener, Game.GameStateListener, View.OnTouchListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    static final int MIN_DISTANCE = 70;
    private float downX, downY, upX, upY;

    private Game mGame;

    private static final String SCORE = "savegame.score";
    private static final String UNDO_SCORE = "savegame.undoscore";
    private static final String CAN_UNDO = "savegame.canundo";
    private static final String UNDO_GRID = "savegame.undo";
    private static final String GAME_STATE = "savegame.gamestate";
    private static final String UNDO_GAME_STATE = "savegame.undogamestate";

    private GameFragment2048Binding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment_2048, container, false);
        binding.gamePad.setOnTouchListener(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScoreKeeper mScoreKeeper = new ScoreKeeper(requireActivity());
        mScoreKeeper.setViews(binding.tvScore, binding.tvHighscore);
        mGame = new Game(requireActivity());
        mGame.setup(binding.gameview);
        mGame.setScoreListener(mScoreKeeper);
        mGame.setGameStateListener(this);
        mGame.newGame();
        InputListener input = new InputListener();
        input.setView(binding.gameview);
        input.setGame(mGame);

        binding.tvTitle.setOnClickListener(v -> {
            if (!mGame.isEndlessMode()) {
                mGame.setEndlessMode();
                binding.tvTitle.setText(Html.fromHtml("&infin;"));
            }
        });

        binding.ibReset.setOnClickListener(v -> {
            mGame.newGame();
            binding.tvTitle.setText(Html.fromHtml("2048"));
        });

        binding.ibUndo.setOnClickListener(v -> {
            mGame.revertUndoState();
            if (mGame.getGameState() == Game.State.ENDLESS || mGame.getGameState() == Game.State.ENLESS_WON) {
                binding.tvTitle.setText(Html.fromHtml("&infin;"));
            } else {
                binding.tvTitle.setText(Html.fromHtml("2048"));
            }
        });

        binding.ibReset.setOnLongClickListener(v -> {
            Toast.makeText(getActivity(), getString(R.string.start_new_game), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void onPause() {
        if (mGame != null) save();
        super.onPause();
    }

    @Override
    public void onResume() {
        load();
        if (mGame.getGameState() == Game.State.ENDLESS || mGame.getGameState() == Game.State.ENLESS_WON) {
            binding.tvTitle.setText(Html.fromHtml("&infin;"));
        }
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mGame.move(Game.DIRECTION_DOWN);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            mGame.move(Game.DIRECTION_UP);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mGame.move(Game.DIRECTION_LEFT);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mGame.move(Game.DIRECTION_RIGHT);
            return true;
        }
        return false;
    }
    @Override
    public void onGameStateChanged(Game.State state) {
        Timber.d("Game state changed to: " + state);
        if (state == Game.State.WON || state == Game.State.ENLESS_WON) {
            binding.tvEndgameOverlay.setVisibility(View.VISIBLE);
            binding.tvEndgameOverlay.setText(R.string.you_win);
            UBaseActivity activity = (UBaseActivity) requireActivity();
            activity.unlockAchievements(getString(R.string.achievement_you_are_good_in_2048), activity.mPlayGamesInstance);
            activity.incrementAchievementProgress(getString(R.string.achievement_unes_2048_champion), 1, activity.mPlayGamesInstance);
        } else if (state == Game.State.LOST) {
            binding.tvEndgameOverlay.setVisibility(View.VISIBLE);
            binding.tvEndgameOverlay.setText(R.string.game_over);
            UBaseActivity activity = (UBaseActivity) requireActivity();
            activity.unlockAchievements(getString(R.string.achievement_you_tried_2048), activity.mPlayGamesInstance);
            activity.incrementAchievementProgress(getString(R.string.achievement_practice_makes_perfect), 1, activity.mPlayGamesInstance);
        } else {
            binding.tvEndgameOverlay.setVisibility(View.GONE);
        }
    }

    private void save() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        Tile[][] field = mGame.getGameGrid().getGrid();
        Tile[][] undoField = mGame.getGameGrid().getUndoGrid();
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] != null) {
                    editor.putInt(xx + " " + yy, field[xx][yy].getValue());
                } else {
                    editor.putInt(xx + " " + yy, 0);
                }

                if (undoField[xx][yy] != null) {
                    editor.putInt(UNDO_GRID + xx + " " + yy, undoField[xx][yy].getValue());
                } else {
                    editor.putInt(UNDO_GRID + xx + " " + yy, 0);
                }
            }
        }
        editor.putLong(SCORE, mGame.getScore());
        editor.putLong(UNDO_SCORE, mGame.getLastScore());
        editor.putBoolean(CAN_UNDO, mGame.isCanUndo());
        editor.putString(GAME_STATE, mGame.getGameState().name());
        editor.putString(UNDO_GAME_STATE, mGame.getLastGameState().name());
        editor.apply();
    }

    private void load() {
        //Stopping all animations
        binding.gameview.cancelAnimations();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        for (int xx = 0; xx < mGame.getGameGrid().getGrid().length; xx++) {
            for (int yy = 0; yy < mGame.getGameGrid().getGrid()[0].length; yy++) {
                int value = settings.getInt(xx + " " + yy, -1);
                if (value > 0) {
                    mGame.getGameGrid().getGrid()[xx][yy] = new Tile(xx, yy, value);
                } else if (value == 0) {
                    mGame.getGameGrid().getGrid()[xx][yy] = null;
                }

                int undoValue = settings.getInt(UNDO_GRID + xx + " " + yy, -1);
                if (undoValue > 0) {
                    mGame.getGameGrid().getUndoGrid()[xx][yy] = new Tile(xx, yy, undoValue);
                } else if (value == 0) {
                    mGame.getGameGrid().getUndoGrid()[xx][yy] = null;
                }
            }
        }

        mGame.setScore(settings.getLong(SCORE, 0));
        mGame.setLastScore(settings.getLong(UNDO_SCORE, 0));
        mGame.setCanUndo(settings.getBoolean(CAN_UNDO, mGame.isCanUndo()));
        try {
            mGame.updateGameState(Game.State.valueOf(settings.getString(GAME_STATE, Game.State.NORMAL.name())));
        } catch (IllegalArgumentException e) {
            mGame.updateGameState(Game.State.NORMAL);
        }
        try {
            mGame.setLastGameState(Game.State.valueOf(settings.getString(UNDO_GAME_STATE, Game.State.NORMAL.name())));
        } catch (IllegalArgumentException e) {
            mGame.setLastGameState(Game.State.NORMAL);
        }
        mGame.updateUI();
    }

    public void onLeftSwipe(){
        mGame.move(Game.DIRECTION_LEFT);
        Timber.d("Left Swipe");
    }

    public void onRightSwipe(){
        mGame.move(Game.DIRECTION_RIGHT);
        Timber.d("Right Swipe");
    }

    public void onDownSwipe(){
        mGame.move(Game.DIRECTION_DOWN);
        Timber.d("Down Swipe");
    }

    public void onUpSwipe(){
        mGame.move(Game.DIRECTION_UP);
        Timber.d("Up Swipe");
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if(Math.abs(deltaX) > Math.abs(deltaY)) {
                    if(Math.abs(deltaX) > MIN_DISTANCE){
                        // left or right
                        if(deltaX > 0) { this.onLeftSwipe(); return true; }
                        if(deltaX < 0) { this.onRightSwipe(); return true; }
                    }
                    else {
                        Timber.i("Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                        return false; // We don't consume the event
                    }
                }
                // swipe vertical?
                else
                {
                    if(Math.abs(deltaY) > MIN_DISTANCE){
                        // top or down
                        if(deltaY < 0) { this.onDownSwipe(); return true; }
                        if(deltaY > 0) { this.onUpSwipe(); return true; }
                    }
                    else {
                        Timber.i("Vertical Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                        return false; // We don't consume the event
                    }
                }

                return true;
            }
        }
        return false;
    }

}
