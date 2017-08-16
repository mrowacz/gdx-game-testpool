package com.mygdx.handlers;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by mrowacz on 15.08.17.
 */
public class MyContactListener implements ContactListener {

    private int numFootContacts;

    // called when two fixtures start to collide
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot" )) {
            numFootContacts++;
        }
    }
    // called when two fixtures no longer collide
    public void endContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();
        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts--;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot" )) {
            numFootContacts--;
        }
    }

    public boolean isPlayerOnGround() { return numFootContacts > 0; }

    // collision detection
    // presolve
    // collision handling
    // postsolve

    public void preSolve(Contact c, Manifold m) {}
    public void postSolve(Contact c, ContactImpulse ci) {}
}
