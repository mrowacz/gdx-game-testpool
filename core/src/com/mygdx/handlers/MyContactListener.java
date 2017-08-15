package com.mygdx.handlers;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by mrowacz on 15.08.17.
 */
public class MyContactListener implements ContactListener {

    // called when two fixtures start to collide
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        System.out.println(
                fa.getUserData() + ", " + fb.getUserData()
        );
    }
    // called when two fixtures no longer collide
    public void endContact(Contact c) {
        System.out.println("End Contact");
    }

    // collision detection
    // presolve
    // collision handling
    // postsolve

    public void preSolve(Contact c, Manifold m) {}
    public void postSolve(Contact c, ContactImpulse ci) {}
}
