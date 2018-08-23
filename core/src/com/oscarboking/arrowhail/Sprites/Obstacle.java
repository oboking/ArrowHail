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

import java.util.Random;

public class Obstacle {
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
    private Sprite sprite = new Sprite(new Texture("arrow2.png"));
    private float posY;
    private float posX;

    //try1
    /** a hit body **/
    Body hitBody = null;
    Camera cam;
    Vector3 testPoint = new Vector3();
    private Boolean isHit = false;

    private float fallSpeed = 6f;

    public Obstacle(World world,Camera cam, float fallSpeed) {
        r = new Random();

        this.world = world;
        this.cam=cam;
        this.fallSpeed=fallSpeed;

        //rectangle
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set( new Vector2( x + width / 2, y + height / 2));

        float randomValue = (float)((-50) + (1000 - (-50)) * r.nextDouble())*(-1);

        shape = new PolygonShape();
        shape.setAsBox( width / 2, height / 2 );

        body = world.createBody(bodyDef);
        body.createFixture(shape, density);
        body.setTransform(randomValue,1160,0);

        sprite.setPosition(randomValue,2000);
        sprite.setScale(0.20f,0.20f);

        body.setUserData(sprite);

        body.setLinearVelocity(0,-400);

        posY = (int)(body.getPosition().y);
        posX = (int)(body.getPosition().x);
    }

    Vector3 touchPoint=new Vector3();

    public void render(SpriteBatch batch,float delta) {

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

    public float getFallSpeed(){
        return this.fallSpeed;
    }
    public Sprite getSprite(){
        return this.sprite;
    }

    public void destroy(){
        sprite=null;
    }

    public Boolean isHit(){
        return this.isHit;
    }

}
