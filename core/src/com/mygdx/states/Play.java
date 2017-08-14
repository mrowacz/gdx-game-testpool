package com.mygdx.states;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.handlers.GameStateManager;

/**
 * Created by mrowacz on 14.08.17.
 */
public class Play extends GameState {

    private BitmapFont font = new BitmapFont();
    public Play(GameStateManager gsm) {
        super(gsm);
    }
    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render()
    {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        font.draw(sb, "play state test", 100, 100);
        sb.end();
    }

    @Override
    public void dispose() {

    }
}
