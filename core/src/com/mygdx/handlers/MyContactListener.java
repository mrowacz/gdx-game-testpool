package com.mygdx.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.states.Play;

/**
 * Created by mrowacz on 15.08.17.
 */
public class MyContactListener implements ContactListener {

    private int numFootContacts;
    private Array<Body> bodiesToRemove;

    public MyContactListener() {
        super();
        bodiesToRemove = new Array<Body>();
    }

    // called when two fixtures start to collide
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (fa == null || fb == null) return;

        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot" )) {
            numFootContacts++;
        }

        if (fa.getUserData() != null && fa.getUserData().equals("crystal" )) {
            // remove crystal
            bodiesToRemove.add(fa.getBody());
        }

        if (fb.getUserData() != null && fb.getUserData().equals("crystal" )) {
            // remove crystal
            bodiesToRemove.add(fb.getBody());
        }
    }
    // called when two fixtures no longer collide
    public void endContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (fa == null || fb == null) return;

        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts--;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot" )) {
            numFootContacts--;
        }
    }

    public boolean isPlayerOnGround() { return numFootContacts > 0; }
    public Array<Body> getBodiestoRemove() { return bodiesToRemove; }

    // collision detection
    // presolve
    // collision handling
    // postsolve

    public void preSolve(Contact c, Manifold m) {}
    public void postSolve(Contact c, ContactImpulse ci) {}
}
