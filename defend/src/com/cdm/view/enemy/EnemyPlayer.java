package com.cdm.view.enemy;

import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import com.cdm.gui.effects.SoundFX;
import com.cdm.gui.effects.SoundFX.Type;
import com.cdm.view.Position;
import com.cdm.view.elements.EnemyUnits;
import com.cdm.view.elements.Level;
import com.cdm.view.elements.LevelFinishedListener;
import com.cdm.view.elements.paths.PathPos;

public class EnemyPlayer {
	private static final int MAX_TRIALS = 50;
	public final float WAITING_TIME = 3.0f;

	enum Mode {
		ATTACK, WAIT
	};

	private Mode mode = Mode.WAIT;
	private int waveNo = 0;
	private float enemyStrength = 3.0f;
	private Level level;
	private float timeToNextWave = WAITING_TIME;
	private float timeInWave = 0.0f;
	private SortedSet<EnemyDef> defs = new TreeSet<EnemyDef>();
	private boolean alreadySent = false;
	private Integer maxLevel = 11;
	private LevelFinishedListener levelFinishedListener;
	private Random random = new Random();

	public EnemyPlayer(LevelFinishedListener pFinishedListener) {
		levelFinishedListener = pFinishedListener;
	}

	public Integer getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
	}

	public Level getLevel() {
		return level;
	}

	public int getWaveNo() {
		return waveNo;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void addTime(float t) {
		if (mode.equals(Mode.ATTACK)) {
			if (level.hasEnemies() || !alreadySent) {
				timeToNextWave = WAITING_TIME;
				timeInWave += t;
				while (defs.size() > 0 && defs.first().time < timeInWave) {
					EnemyDef def = defs.first();
					defs.remove(def);
					alreadySent = true;

					List<PathPos> pp = level.getEnemyStartPosition();
					int nextInt = random.nextInt(pp.size());
					Position x = new Position(pp.get(nextInt),
							Position.LEVEL_REF);
					EnemyUnit e = EnemyUnits.create(def.type, x, waveNo,
							level.getLevelNo());
					level.add(e);

				}
			} else
				startWait();
		} else {
			if (timeToNextWave < 0) {
				startNewWave();
			} else {
				timeToNextWave -= t;
			}
		}
	}

	private void startWait() {
		waveNo += 1;
		if (waveNo >= maxLevel) {
			SoundFX.play(Type.LEVEL2);
			SoundFX.play(Type.WIN2);
			levelFinishedListener.levelFinished();
		}
		mode = Mode.WAIT;
		timeToNextWave = WAITING_TIME;
	}

	private void startNewWave() {
		timeInWave = 0.0f;
		mode = Mode.ATTACK;
		alreadySent = false;

		defs.clear();

		// strength-based randomized enemy creation
		enemyStrength += (4 *(float) level.getLevelNo() + 1.7f * (float) getWaveNo());
		System.out
				.println("Strength " + enemyStrength + " wave:" + getWaveNo());
		Float currentStrength = enemyStrength;
		Float lastTime = 0.0f;
		int trials = MAX_TRIALS; // don' run endlessly

		while (currentStrength > 0 && trials > 0) {
			EnemyType t = EnemyType.random();
			float strength = t.getStrength(getLevel().getLevelNo());

			if (strength < enemyStrength / 2 && strength < currentStrength) {
				System.out.println("Took " + t + " " + strength + " curr:"
						+ currentStrength);
				currentStrength -= strength;
				lastTime += (float) Math.random() * (5.0f - waveNo / 4) + 0.6f;
				defs.add(new EnemyDef(t, lastTime));
				trials = MAX_TRIALS;
			} else
				trials -= 1;
		}

	}

}
