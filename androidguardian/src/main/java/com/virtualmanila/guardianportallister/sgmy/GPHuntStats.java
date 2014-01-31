package com.virtualmanila.guardianportallister.sgmy;

import java.util.HashMap;
import java.util.Map;

public class GPHuntStats {

	private Map<String, Integer> score;

	public GPHuntStats() {
		score = new HashMap<String, Integer>();
	}

	private void addScore(String namex, int pts) {
		String[] names = namex.split(",");
		for (String name : names) {
			name = name.toUpperCase().trim();
			Integer currScore = score.get(name.trim());
			if (currScore == null) {
				currScore = 0;
			}
			currScore = currScore + pts;
			score.put(name, currScore);
		}
	}
}
