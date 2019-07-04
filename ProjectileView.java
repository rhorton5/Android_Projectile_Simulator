package ryley.horton.com;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class ProjectileView extends View implements MediaPlayer.OnCompletionListener{
    //MainActivity Parameters
    MainActivity mainActivity;

    //Meters to Centimeter Scaling
    private final static float SCALE = 100f;
    //Dimensions Specified
    private final static float originalXDimension = 110f * SCALE;//110 meters
    private final static float originalYDimension = 70f * SCALE;//70 meters
    private final static float aspectRatio = originalXDimension/originalYDimension;

    //Used View Size
    private float viewX = originalXDimension;
    private float viewY = originalYDimension;

    //Set Drawn Background
    private float towerHeight = 5f;
    private int elevation = 45;
    private int muzzleVelocity = 25;
    private float targetPosition;
    private float targetScale;
    private static final float grassTop = 5f * SCALE;

    //Paints
    private Paint towerColor = new Paint(),grassColor = new Paint(),muzzleColor = new Paint(),cannonballColor = new Paint();
    private Paint turretColor = new Paint();

    //Boolean for Fire
    boolean readyToFire;
    boolean fired;

    //Simulation Variables
    private TimeAnimator timeAnimator;
    private final static int MSEC = 50;
    long mLastTime;

    private float gravity = -9.8f;

    //Cannonball Itself

    private float yVelocity; //yPosition's velocity
    private float yPosition;

    private float xPosition;
    private float xVelocity; //xPosition's velocity

    //Result Variable
    private float peakY;
    private float finalX;

    private float targetLeft, targetRight;

    //Media Players
    private MediaPlayer cannonFireSound,awwwSound,crowdCheerSound;

    //Constructors
    public ProjectileView(Context context) {
        super(context);
        initializePaints();
        initializeStartingPosition();
    }

    public ProjectileView(Context context,AttributeSet attrs) {
        super(context, attrs);
        initializePaints();
        initializeStartingPosition();
    }

    public ProjectileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializePaints();
        initializeStartingPosition();
    }

    //Initialize Paints
    public void initializePaints(){
        towerColor.setColor(Color.LTGRAY);
        grassColor.setColor(Color.GREEN);
        muzzleColor.setColor(Color.BLACK);
        turretColor.setColor(Color.RED);
        cannonballColor.setColor(Color.BLACK);
    }
    //Draw Backgrounds
    private void drawBackGround(Canvas canvas){
        canvas.drawRGB(0,0,200);
        drawMuzzle(canvas);
        drawTurret(canvas);
        drawTower(canvas);
        drawGrass(canvas);
        drawTarget(canvas);
        drawBall(canvas);

    }
    private void drawTarget(Canvas canvas){
        if(readyToFire){
            /*
                stackoverflow.com/questions/34491569/how-to-change-the-size-of-bitmap
                Used parts of this to get the Bitmap resized.
             */
            Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.target);
            Bitmap resizedBM = Bitmap.createScaledBitmap(bm,(int)targetScale,(int)(2f * SCALE),false);

            canvas.drawBitmap(resizedBM,targetPosition,grassTop,turretColor);
            targetLeft = targetPosition;
            targetRight = targetLeft + (targetScale);
        }

    }
    private void drawGrass(Canvas canvas){ canvas.drawRect(-5f * SCALE,0,originalXDimension,grassTop,grassColor); }

    private void drawTower(Canvas canvas){ canvas.drawRect(0 * SCALE,grassTop,5f * SCALE, grassTop + (towerHeight * SCALE),towerColor); }

    private void drawTurret(Canvas canvas){ canvas.drawCircle((5f * SCALE)/2,grassTop + (towerHeight * SCALE),2.5f * SCALE,turretColor); }

    private void drawMuzzle(Canvas canvas){
        //Fix
        canvas.save();
        float xPosition = (3f * SCALE)/2, yPosition = grassTop + (towerHeight * SCALE);
        canvas.rotate(elevation,(3f *  SCALE)/2,grassTop + towerHeight * SCALE + ((5 * SCALE)/2));
        canvas.drawRect(xPosition,yPosition, xPosition + (4f * SCALE),yPosition + (2f * SCALE),muzzleColor);
        canvas.restore();
    }
    private void drawBall(Canvas canvas){
        if(fired){
            float xpos = xPosition, ypos = yPosition;
            canvas.drawCircle(xpos,ypos,1f*SCALE,cannonballColor);

            if(ypos <= grassTop){
                stopSimulation();
            }
        }
    }

    //Miscellaneous Time Animation
    private void stopSimulation(){
        Dialog dialog;
        if(timeAnimator != null){
            timeAnimator.pause();
            timeAnimator = null;
            fired = false;
        }
        if(ballLandsOnTarget()){
            dialog = successDialog();
            startCheerSound();
        }else{
            dialog = failureDialog();
            startAwwwSound();
        }

        finalX = xPosition;

        initializeStartingPosition();


        dialog.show();
        invalidate();
        setFinalXAndY();


    }
    private void initializeStartingPosition(){
        xPosition = (5f * SCALE)/2;
        yPosition = grassTop + (towerHeight * SCALE) + (1f * SCALE);

        xVelocity = 0f;
        yVelocity = 0f;
    }
    private Dialog successDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(R.string.success_dialog).setPositiveButton(R.string.success_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return dialog.create();
    }
    private Dialog failureDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(R.string.failure_dialog).setPositiveButton(R.string.failure_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return dialog.create();
    }
    private boolean ballLandsOnTarget(){
        return xPosition >= targetLeft && xPosition <= targetRight;
    }

    //Button Commands
    public void randomizeTarget(){
        int max = 95, min = 10; //in meters
        readyToFire = true;
        
        Random r = new Random();
        int startValue = r.nextInt(max-min)+min;
        targetPosition = startValue * SCALE;
    }

    //Sound Commands
    public void startFireCannonSound(){
        cannonFireSound = MediaPlayer.create(getContext(),R.raw.cannonfire);
        cannonFireSound.setVolume(0.5f,0.5f);
        cannonFireSound.setOnCompletionListener(this);
        cannonFireSound.start();
    }
    private void startAwwwSound(){
        awwwSound = MediaPlayer.create(getContext(),R.raw.aww);
        awwwSound.setVolume(0.5f,0.5f);
        awwwSound.setOnCompletionListener(this);
        awwwSound.start();
    }
    private void startCheerSound(){
        crowdCheerSound = MediaPlayer.create(getContext(),R.raw.crowdcheer);
        crowdCheerSound.setVolume(0.5f,0.5f);
        crowdCheerSound.setOnCompletionListener(this);
        crowdCheerSound.start();
    }
    private void manageMediaPlayers(boolean stopping){
        if(cannonFireSound != null){
            if(cannonFireSound.isPlaying() && stopping){
                cannonFireSound.pause();
            }else if(!cannonFireSound.isPlaying() & !stopping){
                cannonFireSound.start();
            }
        }
        if(awwwSound != null){
            if(awwwSound.isPlaying() && stopping){
                awwwSound.pause();
            }else if(!awwwSound.isPlaying() & !stopping){
                awwwSound.start();
            }
        }
        if(crowdCheerSound != null){
            if(crowdCheerSound.isPlaying() && stopping){
                crowdCheerSound.pause();
            }else if(!crowdCheerSound.isPlaying() & !stopping){
                crowdCheerSound.start();
            }
        }

    }
    //Simulation Methods
    public void fireCannon(){
        if(fired){ Toast.makeText(getContext(),"Ball is already fired, please wait.  :)",Toast.LENGTH_SHORT).show(); return; }
        fired = true;
        timeAnimator = new TimeAnimator();
        mLastTime = System.currentTimeMillis();

        startFireCannonSound();
        setInitialVelocities();

        timeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                long now = System.currentTimeMillis();
                if((now - mLastTime) < MSEC)
                    return;
                mLastTime = now;
                animateCannonball(deltaTime);
                invalidate();
                checkPeakY();
            }
        });
        timeAnimator.start();
    }

    public void setInitialVelocities(){
        if(xVelocity == 0f && yVelocity == 0f) {
            xVelocity = (float) (muzzleVelocity * Math.cos(Math.PI/180f*elevation));
            yVelocity = (float) (muzzleVelocity * Math.sin(Math.PI/180f*elevation));
        }
    }

    public void animateCannonball(float deltaTime){
        deltaTime = deltaTime/1000f;

        yVelocity = yVelocity  + (gravity * deltaTime);

        yPosition = yPosition + (yVelocity * deltaTime) * SCALE;
        xPosition = xPosition + (xVelocity * deltaTime) * SCALE;
    }

    //ImageView Commands
    public void changeElevation(int i){
        if(i + elevation > 90 || i + elevation < 0)
            return;
        else if(towerHeight <= 0f && elevation + i < 25)
            return;
        elevation += i;


    }
    public void checkPeakY(){
        if(peakY < yPosition)
            peakY = yPosition;
    }
    private void checkForZeroHeight(){
        if(towerHeight <= 0f){
            if(elevation < 25){
                elevation = 25;
            }
        }
    }
    public void changeTowerHeight(float i){
        if(i + towerHeight > 15f || i + towerHeight < 0f)
            return;
        towerHeight += i;
        if(!fired)
            yPosition = grassTop + (towerHeight * SCALE) + (1f * SCALE);
        checkForZeroHeight();
    }
    public void changeMuzzleVelocity(int i){
        if(i + muzzleVelocity < 10 || i + muzzleVelocity > 30)
            return;
        muzzleVelocity += i;
    }
    public void pauseSimulation(){
        if(timeAnimator != null){
            if(timeAnimator.isPaused())
                timeAnimator.start();
            else{
                timeAnimator.pause();
                mLastTime = 0;
            }

        }
    }

    //Get Methods
    public float getElevation(){ return elevation; }

    public float getTowerHeight(){ return towerHeight; }

    public float getMuzzleVelocity(){ return muzzleVelocity; }

    public float getX(){return xPosition;}

    public float getY(){return yPosition;}

    public float getVX(){return xVelocity;}

    public float getVY(){return yVelocity;}

    //Set Methods
    public void setTargetPosition(int value){
        targetScale = value * SCALE;
        invalidate();
    }
    public void reinitializeParameters(float xPosition, float yPosition, float xVelocity, float yVelocity, int elevation, int towerHeight, int muzzleVelocity){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.elevation = elevation;
        this.towerHeight = towerHeight;
        this.muzzleVelocity = muzzleVelocity;
    }

    //Helper Method (onDraw)
    private void scaleAndTranslateCanvas(Canvas canvas){
        float sx = (getWidth() / originalXDimension), sy = (getHeight() / originalYDimension);

        canvas.translate(5f,getHeight());//5 pixels, not five meters
        canvas.scale(sx,-sy);
    }

    //Overridden Methods
    @Override
    public void onCompletion(MediaPlayer mp){
        int id = mp.getAudioSessionId();
        if(cannonFireSound != null && id == cannonFireSound.getAudioSessionId()){
            cannonFireSound.release();
            cannonFireSound = null;
        }else if(awwwSound != null && id == awwwSound.getAudioSessionId()){
            awwwSound.release();
            awwwSound = null;
        }else if(crowdCheerSound != null && id == crowdCheerSound.getAudioSessionId()){
            crowdCheerSound.release();
            crowdCheerSound = null;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scaleAndTranslateCanvas(canvas);
        drawBackGround(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int cWidth = (int)(height * aspectRatio);
        int cHeight = (int)(width / aspectRatio);

        int fWidth, fHeight;
        if(cWidth > width){
            fWidth = cWidth;
            fHeight = height;
        }else{
            fWidth = width;
            fHeight = cHeight;
        }
        setMeasuredDimension(fWidth,fHeight);
    }

    public void onPause(){
        manageMediaPlayers(true);
        if(timeAnimator == null)
            return;
        if(timeAnimator.isRunning()){
            timeAnimator.pause();
        }
    }
    public void onResume(){ manageMediaPlayers(false); }

    public void setMainActivity(Activity activity){ mainActivity = (MainActivity)activity; }

    private void setFinalXAndY(){
        if(finalX == 0f)
            mainActivity.changeFinalXAndY(finalX ,peakY);
        else
            mainActivity.changeFinalXAndY(finalX-xPosition,peakY-grassTop);//Since the x and y values are not at (0,0)
        mainActivity.fire.setEnabled(true);
    }
}
