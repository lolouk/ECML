package com.ecml;

/**
 * Created by Jerome on 22/06/2015.
 */
public class ActivityParameters {

    private int number;
    private String activityType;
    private int duration;   //in milliseconds
    private String song;
    private int tempo;
    private int gameLevel;
    private boolean active;
    private boolean finished;
    private int countdown;

    public ActivityParameters(int number, String activityType, int duration, String song, int tempo, boolean active, boolean finished, int countdown) {
        this.number = number;
        this.activityType = activityType;
        this.duration = duration;
        this.song = song;
        this.tempo = tempo;
        this.gameLevel = 1;
        this.active = active;
        this.finished = finished;
        this.countdown = countdown;
    }

    public String getSong() {
        return song;
    }

    public int getDuration() {
        return duration;
    }

    public int getNumber() {
        return number;
    }

    public String getActivityType() {
        return activityType;
    }

    public int getTempo() {
        return tempo;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFinished() {
        return finished;
    }

    public void finish() {
        this.active = false;
        this.finished = true;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
}
