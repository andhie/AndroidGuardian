package com.sentulasia.enl.model;

public class ScorePair implements Comparable<ScorePair> {

    String name;

    int score;

    public ScorePair(String name, int score) {
	this.name = name;
	this.score = score;
    }

    public String getName() {
	return name;
    }

    public int getScore() {
	return score;
    }

    @Override
    public int compareTo(ScorePair another) {
	return -Integer.valueOf(score).compareTo(another.score);
    }
}