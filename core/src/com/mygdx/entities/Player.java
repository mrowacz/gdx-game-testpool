package com.mygdx.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

/**
 * Created by mrowacz on 17.08.17.
 */
public class Player extends B2DSprite {
    private int numCrystals;
    private int totalCrystals;

    public Player(Body body) {
        super(body);
        Texture tex = MyGdxGame.res.getTexture("bunny");
        TextureRegion[] sprites = TextureRegion.split(tex, 32, 32)[0];

        setAnimation(sprites, 1/12f);
    }

    public void collecCrystal() { numCrystals++; };
    public int getNumCrystals() { return numCrystals; }
    public void setTotalCrystals(int i) { totalCrystals = i; }
    public int getTotalCrystals() { return totalCrystals; }

}
