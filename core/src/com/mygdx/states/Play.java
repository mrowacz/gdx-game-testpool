package com.mygdx.states;

import static com.mygdx.handlers.B2DVars.PPM;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.entities.Crystal;
import com.mygdx.entities.HUD;
import com.mygdx.entities.Player;
import com.mygdx.game.MyGdxGame;
import com.mygdx.handlers.B2DVars;
import com.mygdx.handlers.GameStateManager;
import com.mygdx.handlers.MyContactListener;
import com.mygdx.handlers.MyInput;

/**
 * Created by mrowacz on 14.08.17.
 */
public class Play extends GameState {

    private boolean debug = true;

    private World world;
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera b2dCam;
    private MyContactListener cl;

    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;

    private Player player;
    private Array<Crystal> crystals;

    private HUD hud;

    public Play(GameStateManager gsm) {
        super(gsm);

        // set up box2d stuff
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // creating player body
        createPlayer();

        // createTiles();
        createTiles();

        // create crystals
        createCrystals();

        // set up box2d cam;
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, MyGdxGame.V_WIDTH / PPM, MyGdxGame.V_HEIGHT / PPM);

        // set up  hud
        hud = new HUD(player);
    }

    @Override
    public void handleInput() {
        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.isPlayerOnGround()) {
                player.getBody().applyForceToCenter(0, 250, true);
            }
        }

        // switch block color
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            switchBlocks();
        }

    }

    @Override
    public void update(float dt) {
        // check input
        handleInput();

        // update box
        world.step(dt, 6, 2);

        // move player
        player.update(dt);

        // remove crystals
        Array<Body> bodies = cl.getBodiestoRemove();
        for (int i = 0; i < bodies.size; i++) {
            Body b = bodies.get(i);
            crystals.removeValue((Crystal) b.getUserData(), true);
            world.destroyBody(b);
            player.collecCrystal();
        }
        bodies.clear();

        for (int i = 0; i < crystals.size; i++)
            crystals.get(i).update(dt);
    }

    @Override
    public void render()
    {
        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw tile map
        tmr.setView(cam);
        tmr.render();

        //draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        // draw crystals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        // draw hud
        sb.setProjectionMatrix(hudCam.combined );
        hud.render(sb);

        // draw box2d world
        if (debug)
            b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(160 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.linearVelocity.set(1, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(13 / PPM, 13 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        fdef.restitution = 0f;
        body.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(13 / PPM, 2/PPM, new Vector2(0, -13 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");

        // create player
        player = new Player(body);

        body.setUserData(player);
        
    }

    private void createTiles() {
        // load map
        tileMap = new TmxMapLoader().load("maps/level1.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);

        tileSize = Integer.parseInt(tileMap.getProperties().get("tilewidth").toString());
        TiledMapTileLayer layer;

        layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);

        layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);

        layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
        createLayer(layer, B2DVars.BIT_BLUE);
    }

    private void createLayer(TiledMapTileLayer layer, short bits) {

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (int row = 0; row < layer.getHeight(); row ++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                // get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                // check if cell exists
                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                // create a body + fixture from cell
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set (
                        (col + 0.5f) * tileSize / PPM,
                        (row + 0.5f) * tileSize / PPM
                );

                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-tileSize / 2 / PPM, -tileSize / 2 / PPM);
                v[1] = new Vector2(-tileSize / 2 / PPM, tileSize / 2 / PPM);
                v[2] = new Vector2(tileSize / 2 / PPM, tileSize / 2 / PPM);
                cs.createChain(v);
                fdef.friction = 0;
                fdef.shape = cs;
                fdef.filter.categoryBits = bits;
                fdef.filter.maskBits = B2DVars.BIT_PLAYER;
                fdef.isSensor = false;

                world.createBody(bdef).createFixture(fdef);
            }
        }
    }

    private void createCrystals()
    {
        crystals = new Array<Crystal>();
        MapLayer layer = tileMap.getLayers().get("crystals");

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {

            bdef.type = BodyDef.BodyType.StaticBody;
            float x = Float.parseFloat(mo.getProperties().get("x").toString()) / PPM;
            float y = Float.parseFloat(mo.getProperties().get("y").toString()) / PPM;
            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8f / PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("crystal");

            Crystal c = new Crystal(body);
            crystals.add(c);

            body.setUserData(c);
        }
    }

    private void switchBlocks() {
        Filter filter = player.getBody().getFixtureList().first().getFilterData();
        short bits = filter.maskBits;

        // switch to next color
        // red -> green -> blue -> red
        if ((bits & B2DVars.BIT_RED) != 0) {
            bits &= ~B2DVars.BIT_RED;
            bits |= B2DVars.BIT_GREEN;
        } else if ((bits & B2DVars.BIT_GREEN) != 0) {
            bits &= ~B2DVars.BIT_GREEN;
            bits |= B2DVars.BIT_BLUE;
        } else if ((bits & B2DVars.BIT_BLUE) != 0) {
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_RED;
        }

        // set new mask bits
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        // set ne mask bits for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~B2DVars.BIT_CRYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);
    }
}
