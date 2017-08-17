package com.mygdx.states;

import static com.mygdx.handlers.B2DVars.PPM;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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

    private World world;
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera b2dCam;
    private MyContactListener cl;

    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;

    private Player player;

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

        // set up box2d cam;
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, MyGdxGame.V_WIDTH / PPM, MyGdxGame.V_HEIGHT / PPM);
    }

    @Override
    public void handleInput() {
        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.isPlayerOnGround()) {
                player.getBody().applyForceToCenter(0, 200, true);
            }
        }

        if (MyInput.isPressed(MyInput.BUTTON2))
            System.out.println("hold x");
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);
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

        // draw box2d world
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
        fdef.filter.maskBits = -1;
        fdef.restitution = 0f;
        body.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(13 / PPM, 2/PPM, new Vector2(0, -13 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = -1;
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
}
