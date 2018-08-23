package com.oscarboking.arrowhail.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.oscarboking.arrowhail.GdxGame;

import java.util.Random;

public class PowerUp {
    public int x,y;
    public Vector3 position;
    public World world;

    private Body body;
    private BodyDef bodyDef;
    private PolygonShape shape;

    private float density = 1f;
    private final float width = 1f;
    private final float height = 1f;

    Random r;

    //try2
    private Sprite sprite;
    private float posY;
    private float posX;

    //try1
    /** a hit body **/
    Body hitBody = null;
    Camera cam;
    Vector3 testPoint = new Vector3();
    private Boolean isHit = false;

    private float fallSpeed = 6f;

    private String type; //either "x2", "slow-mo", "shield"
    private Boolean hasBeenHandled = false;

    public PowerUp(World world,Camera cam) {
        r = new Random();

        this.world = world;
        this.cam=cam;
        this.fallSpeed=fallSpeed;

        //rectangle
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set( new Vector2( x + width / 2, y + height / 2));

        float randomValue = (float)((-500) + (500 - (-500)) * r.nextDouble())*(-1);

        shape = new PolygonShape();
        shape.setAsBox( width / 2, height / 2 );

        body = world.createBody(bodyDef);
        body.createFixture(shape, density);
        body.setTransform(randomValue,1160,0);

        int decideType = r.nextInt(4);

        if(decideType==0){//shield
            sprite = new Sprite(new Texture("shield.png"));
            type="shield";
        }else if (decideType==1){//slow mo
            sprite = new Sprite(new Texture("slow_mo.png"));
            type="slow-mo";
        }else if (decideType==2){//x2
            sprite = new Sprite(new Texture("x2.png"));
            type="x2";
        }else if (decideType==3){//x2
            sprite = new Sprite(new Texture("pause.png"));
            type="pause";
        }

        sprite.setPosition(randomValue,2000);
        sprite.setScale(0.10f,0.10f);

        body.setUserData(sprite);

        body.setLinearVelocity(0,-400);

        posY = (int)(body.getPosition().y);
        posX = (int)(body.getPosition().x);
    }

    Vector3 touchPoint=new Vector3();

    public void render(SpriteBatch batch, float delta) {

        float rotation = (float)Math.toDegrees(body.getAngle());

        posY-=fallSpeed;

        sprite.setPosition((posX+width)*(-1)/2,(posY));
        sprite.setRotation(rotation);
        sprite.draw(batch);
        ((Sprite) body.getUserData()).draw(batch);

        if(Gdx.input.isTouched())
        {
            cam.unproject(touchPoint.set(Gdx.input.getX(),Gdx.input.getY(),0));
            if(sprite.getBoundingRectangle().contains(touchPoint.x,touchPoint.y))
            {
                isHit=true;
            }
        }
    }

    public void setFallSpeed(float newFallSpeed){
        this.fallSpeed=newFallSpeed;
    }

    public String getType(){
        return this.type;
    }

    public Sprite getSprite(){
        return this.sprite;
    }


    public Boolean isHit(){
        return this.isHit;
    }

    public void destroy(){
        sprite=null;
    }

    public Boolean getHasBeenHandled() {
        return hasBeenHandled;
    }

    public void setHasBeenHandled(Boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }
}
