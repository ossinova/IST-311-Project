package com.cdm.view.elements;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.cdm.gui.effects.SoundFX;
import com.cdm.gui.effects.SoundFX.Type;
import com.cdm.view.IRenderer;
import com.cdm.view.PolySprite;
import com.cdm.view.Position;
import com.cdm.view.elements.shots.RocketShot;
import com.cdm.view.enemy.EnemyUnit;

public class RocketThrower extends RotatingUnit implements Element {

	private List<Vector3> lines;
	private List<Vector3> poly;
	private PolySprite sprite;
	float shotFrequency = 5.0f;
	float lastShot = 0.0f;
	private float maxDist = 3.5f;
	private double startingRadius = 0.4f;
	Color innerColor = new Color(0, 0, 0.6f, 1.0f);
	Color outerColor = new Color(0.2f, 0.2f, 1.0f, 1.0f);

	public RocketThrower(Position p) {
		super(p);
		Vector3 c0 = new Vector3(-1, -1, 0);
		Vector3 c1 = new Vector3(1, -1, 0);
		Vector3 c2 = new Vector3(1, 1, 0);
		Vector3 c3 = new Vector3(-1, 1, 0);

		sprite = new PolySprite();
		lines = Arrays.asList(new Vector3[] { c0, c1, c1, c2, c2, c3, c3, c0 });
		poly = Arrays.asList(new Vector3[] { c0, c1, c2, c0, c2, c3 });

		Color xColor = new Color(1, 1, 1, 1);
		
		Vector3 z0=new Vector3(-1000,-1000,0);
		Vector3 z1=new Vector3(1000,-1000,0);
		Vector3 z2=new Vector3(1000,1000,0);
		Vector3 z3=new Vector3(-1000,1000,0);
		sprite.addVertex(c0, xColor);
		sprite.addVertex(c1, xColor);
		sprite.addVertex(c2, xColor);
		sprite.addVertex(c0, xColor);
		sprite.addVertex(c2, xColor);
		sprite.addVertex(c3, xColor);
		sprite.init();
	}

	@Override
	public void draw(IRenderer renderer) {
		renderer.drawPoly(getPosition(), poly, getAngle(), innerColor,
				getSize());
		renderer.drawLines(getPosition(), lines, getAngle(), outerColor,
				getSize());
		renderer.render(sprite, getPosition(), getSize(), getAngle());

	}

	@Override
	protected EnemyUnit getEnemy() {
		EnemyUnit u = getLevel().getNextEnemy(getPosition());
		if (u == null)
			return null;
		if (getPosition().distance(u.getPosition()) > maxDist)
			return null;
		return u;
	}

	@Override
	public void move(float time) {
		super.move(time);
		EnemyUnit enemy = getEnemy();
		lastShot += time;
		if (ableToShoot)
			shoot(enemy);
	}

	protected float getMaxDist() {
		return maxDist;
	}

	private void shoot(EnemyUnit enemy) {
		if (enemy != null) {

			if (lastShot > shotFrequency) {
				lastShot = 0.0f;
				Position startingPos = new Position(getPosition());
				float angle = getAngle();
				startingPos.x -= Math.cos(angle * MathTools.M_PI / 180.0f)
						* startingRadius;
				startingPos.y -= Math.sin(angle * MathTools.M_PI / 180.0f)
						* startingRadius;
				getLevel().addShot(
						new RocketShot(startingPos, anticipatePosition(enemy),
								getLevel()));
				SoundFX.play(Type.SHOT);

			}

		}
	}

	@Override
	public int getZLayer() {
		return 0;
	}
}
