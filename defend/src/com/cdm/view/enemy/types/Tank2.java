package com.cdm.view.enemy.types;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.cdm.view.IRenderer;
import com.cdm.view.PolySprite;
import com.cdm.view.Position;
import com.cdm.view.enemy.Chain;
import com.cdm.view.enemy.GroundMovingEnemy;

public class Tank2 extends GroundMovingEnemy {


	public Position nextStep = null;
	public static final float SPEED = 0.35f;


	private static final Vector3 c0 = new Vector3(-1, -1, 0);
	private static final Vector3 c1 = new Vector3(1, -1, 0);
	private static final Vector3 c2 = new Vector3(1, 1, 0);
	private static final Vector3 c3 = new Vector3(-1, 1, 0);
	private static final Vector3 d0 = new Vector3(-0.5f, -0.5f, 0);
	private static final Vector3 d1 = new Vector3(0.5f, -0.5f, 0);
	private static final Vector3 d2 = new Vector3(0.5f, 0.5f, 0);
	private static final Vector3 d3 = new Vector3(-0.5f, 0.5f, 0);
	private static final Vector3 r1 = new Vector3(0.5f, 0.25f, 0);
	private static final Vector3 r2 = new Vector3(0.75f, 0.25f, 0);
	private static final Vector3 r3 = new Vector3(0.5f, -0.25f, 0);
	private static final Vector3 r4 = new Vector3(0.75f, -0.25f, 0);
	private static final Vector3 k0 = new Vector3(-0.9f, -1.5f, 0);
	private static final Vector3 k1 = new Vector3(0.8f, -1.5f, 0);
	private static final Vector3 k2 = new Vector3(0.8f, -1.1f, 0);
	private static final Vector3 k3 = new Vector3(-0.9f, -1.1f, 0);
	private static final Vector3 x0 = new Vector3(-0.9f, 1.5f, 0);
	private static final Vector3 x1 = new Vector3(0.8f, 1.5f, 0);
	private static final Vector3 x2 = new Vector3(0.8f, 1.1f, 0);
	private static final Vector3 x3 = new Vector3(-0.9f, 1.1f, 0);
	private static PolySprite sprite = null;
	private static final List<Vector3> lines = Arrays.asList(new Vector3[] {
			c0, c1, c1, c2, c2, c3, c3, c0, d0, d1, d1, d2, d2, d3, d3, d0, r1,
			r2, r3, r4, k0, k1, k1, k2, k2, k3, k3, k0, x0, x1, x1, x2, x2, x3,
			x3, x0 });

	private static final List<Vector3> poly = Arrays.asList(new Vector3[] { c0,
			c1, c2, c0, c2, c3,k0,k1,k2,k3,k2,k0,x0,x1,x2,x3,x2,x0 });
	private static final Color innerColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	private static final Color outerColor = new Color(0.9f, 0.0f, 0.0f, 1.0f);
	private final Chain chains = new Chain();

	public Tank2(Position pos) {
		super(pos);
		setSize(0.25f);
		setEnergy(20);
	}

	public void move(float time) {
		super.move(time);
		chains.move(time*SPEED/GroundMovingEnemy.SPEED);
	}

	@Override
	public void draw(IRenderer renderer) {
		
		if (sprite == null) {
			sprite = new PolySprite();
			sprite.makeNiceRectangle(1f, -1f, -1f, 2f, 2f, outerColor, new Color(
					0f, 0f, 0f, 0f));
			sprite.init();
		}
		
		renderer.drawPoly(getPosition(), poly, getAngle(), innerColor,
				getSize());
		getShakingLines().draw(renderer,getPosition(), lines, getAngle(), outerColor,
				getSize());

		chains.drawChain(renderer, getPosition(), getAngle(), outerColor,
				getSize());

		super.draw(renderer);
	}

	@Override
	public float getOriginalSpeed() {
		return SPEED;
	}

	@Override
	public int getMoney() {
		return 3;
	}

	@Override
	public int getPoints() {
		return 10;
	}

	@Override
	public int getBonus() {
		return 1;
	}
}
