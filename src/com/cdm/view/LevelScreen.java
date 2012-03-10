package com.cdm.view;

import java.util.Set;

import org.lwjgl.Sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cdm.SString;
import com.cdm.Settings;
import com.cdm.gui.Button;
import com.cdm.gui.IButtonPressed;
import com.cdm.gui.IUnitTypeSelected;
import com.cdm.gui.UnitTypeButton;
import com.cdm.gui.WidgetContainer;
import com.cdm.view.Position.RefSystem;
import com.cdm.view.elements.Elements;
import com.cdm.view.elements.Level;
import com.cdm.view.elements.Unit;
import com.cdm.view.elements.Unit.UnitType;

public class LevelScreen extends Screen implements IUnitTypeSelected,
		IButtonPressed {
	public SpriteBatch spriteBatch = new SpriteBatch();
	public static TextureRegion bg;
	private Renderer renderer = new Renderer();
	private Level level = new Level();
	private WidgetContainer gui = new WidgetContainer();
	private Unit dragElement = null;

	public LevelScreen() {
		bg = load("res/bg_stars.png", 64, 64);
		UnitTypeButton tb = new UnitTypeButton(40, 400, 30,
				Unit.UnitType.CANNON);
		tb.setListener(this);
		gui.add(tb);

		Button sizeButton = new Button(200, 400, 30);
		sizeButton.setButtonName(SString.SIZE_BUTTON);
		sizeButton.setPressedListener(this);
		gui.add(sizeButton);
	}

	private Long oldMicros = 0L;

	@Override
	public void render(float delta) {
		// System.out.println(delta);
		Long millis = System.currentTimeMillis();
		Long micro = System.nanoTime() / 1000 + millis * 1000;
		if (oldMicros > 0) {
			delta = (micro - oldMicros) * 0.000001f;
			// System.out.println(delta);
			// System.out.println(millis-oldMillis);
		}
		oldMicros = micro;
		delta += mywait(delta);

		if (false) {
			System.out.print("FPS:");
			System.out.println(1.0f / delta);
		}
		spriteBatch.begin();
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++)
				draw(bg, x * 64, y * 64);

		spriteBatch.end();
		drawLineBased(delta);

	}

	private void drawLineBased(float delta) {
		if (delta > 0) {
			level.move(delta);
		}
		OrthographicCamera cam;
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		GLCommon gl = Gdx.gl;

		long startPhysics = System.nanoTime();

		cam.position.set(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2, 0);
		cam.update();
		cam.apply(Gdx.gl10);

		Gdx.gl10.glScalef(1, -1, 0);
		Gdx.gl10.glTranslatef(0, -Gdx.graphics.getHeight(), 0);

		level.draw(renderer);
		gui.draw(renderer);

		if (dragElement != null) {
			dragElement.draw(renderer);
		}
	}

	static final int CIRCLE_VERTICES = 10;

	private float mywait(float delta) {
		if (false)
			return 0.0f;
		try {
			Integer ms = (int) (delta * 1000);
			// ~ 50 fps
			// TODO: sleep shorter, if rendering does need more time
			Long millis0 = System.currentTimeMillis();
			Long micros0 = System.nanoTime() / 1000;

			int wait = 15 - ms;
			if (wait > 5) {
				Thread.sleep(wait);
				// System.out.println(wait);
			}
			Long millis1 = System.currentTimeMillis();
			Long micros1 = System.nanoTime() / 1000;
			return ((millis1 - millis0) * 1000 + (micros1 - micros0)) / 1000000.0f;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void draw(TextureRegion region, int x, int y) {
		int width = region.getRegionWidth();
		if (width < 0)
			width = -width;
		spriteBatch.draw(region, x, y, width, -region.getRegionHeight());
	}

	public static TextureRegion load(String name, int width, int height) {
		Texture texture = new Texture(Gdx.files.internal(name));
		TextureRegion region = new TextureRegion(texture, 0, 0, width, height);
		region.flip(false, true);
		return region;
	}

	public boolean touchDown(int x, int y, int pointer, int button) {
		if (gui.opaque(x, y)) {
			System.out.println("MYYY TOUCHDOWN");
			gui.touchDown(x, y, pointer, button);
			return true;
		}
		System.out.println("touchDown");
		System.out.println(x);
		System.out.println(y);
		return false;
	}

	public boolean touchUp(int x, int y, int pointer, int button) {
		if (gui.opaque(x, y)) {
			System.out.println("MYYY TOUCHDOWN");
			gui.touchUp(x, y, pointer, button);
			return true;
		}
		System.out.println("touchUp");
		stopDragging();
		level.stopHover();
		return false;
	}

	private void stopDragging() {
		if (dragElement != null) {
			level.add(dragElement);
		}
		dragElement = null;
	}

	public boolean touchDragged(int x, int y, int pointer) {
		Position pos = new Position(x, y, RefSystem.Screen);
		if (dragElement != null)
			dragElement.setPosition(pos);
		System.out.println("touchDrag");
		level.hover(pos);
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		// System.out.println("touchmoved");
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		System.out.println("scroll");
		return false;
	}

	@Override
	public void unitTypeSelected(UnitType type, Position screenPos) {
		System.out.println("Unit Type selected");
		dragElement = Elements.getElementBy(type, screenPos);
	}

	@Override
	public void buttonPressed(SString buttonName) {
		if (buttonName.equals(SString.SIZE_BUTTON)) {
			Settings.CELL_WIDTH = 16;
		}
	}

}
