package com.forcetower.uefs.game.g2048.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.forcetower.uefs.game.g2048.view.Game;

public class ScoreKeeper extends ContextWrapper implements Game.ScoreListener {
    private TextView mScoreDisplay;
    private TextView mHighScoreDisplay;
    private static final String HIGH_SCORE = "score.highscore";
    private static final String PREFERENCES = "score";
    private final SharedPreferences mPreferences;
    private long mScore;
    private long mHighScore;

    public ScoreKeeper(Context context) {
        super(context);
        mPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setViews(TextView score, TextView highScore) {
        mScoreDisplay = score;
        mHighScoreDisplay = highScore;
        reset();
    }

    private void reset() {
        mHighScore = loadHighScore();
        if (mHighScoreDisplay != null) {
            String highScore = "" + mHighScore;
            mHighScoreDisplay.setText(highScore);
        }
        mScore = 0;
        if (mScoreDisplay != null) {
            String score = "" + mScore;
            mScoreDisplay.setText(score);
        }
    }

    private long loadHighScore() {
        if (mPreferences == null)
            return -1;
        return mPreferences.getLong(HIGH_SCORE, 0);
    }

    private void saveHighScore(long highScore) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(HIGH_SCORE, highScore);
        editor.apply();
    }

    void setScore(long score) {
        mScore = score;
        if (mScoreDisplay != null) {
            String scoreT = "" + mScore;
            mScoreDisplay.setText(scoreT);
        } if (mScore > mHighScore) {
            mHighScore = mScore;
            if (mHighScoreDisplay != null) {
                String highScore = "" + mHighScore;
                mHighScoreDisplay.setText(highScore);
            }
            saveHighScore(mHighScore);
        }
    }

    @Override
    public void onNewScore(long score) {
        setScore(score);
    }
}
