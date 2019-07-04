package ryley.horton.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ProjectileView pv;
    Button fire,target;
    ImageView eleUp,eleDown,hUp,hDown,mUp,mDown;

    private float finalX,peakY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pv = findViewById(R.id.projectileView);

        target = findViewById(R.id.target_button);
        fire = findViewById(R.id.fire_button);
        eleUp = findViewById(R.id.elevation_up);
        eleDown = findViewById(R.id.elevation_down);
        hUp = findViewById(R.id.height_up);
        hDown = findViewById(R.id.height_down);
        mUp = findViewById(R.id.muzzle_up);
        mDown = findViewById(R.id.muzzle_down);

        pv.setMainActivity(this);
        if(savedInstanceState == null){
            changeElevationText();
            changeHeightText();
            changeMuzzleVelocityText();
            changeFinalXAndY(0,0);

            fire.setVisibility(View.INVISIBLE);
        }

        pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.pauseSimulation();
            }
        });

        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.fireCannon();
                fire.setEnabled(false);
            }

        });

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.randomizeTarget();
                pv.invalidate();
                setFireVisibility();
            }
        });
        eleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.changeElevation(1);
                changeElevationText();
                pv.invalidate();
            }
        });
        eleDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.changeElevation(-1);
                changeElevationText();
                pv.invalidate();
            }
        });
        eleUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pv.changeElevation(10);
                changeElevationText();
                pv.invalidate();
                return true;
            }
        });
        eleDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pv.changeElevation(-10);
                changeElevationText();
                pv.invalidate();
                return true;
            }
        });
        hUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.changeTowerHeight(1f);
                changeHeightText();
                changeElevationText();
                pv.invalidate();
            }
        });
        hDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.changeTowerHeight(-1f);
                changeHeightText();
                changeElevationText();
                pv.invalidate();
            }
        });
        hUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pv.changeTowerHeight(10f);
                changeHeightText();
                changeElevationText();
                pv.invalidate();
                return true;
            }
        });
        hDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pv.changeTowerHeight(-10f);
                changeHeightText();
                changeElevationText();
                pv.invalidate();
                return true;
            }
        });
        mUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.changeMuzzleVelocity(1);
                changeMuzzleVelocityText();
            }
        });
        mDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.changeMuzzleVelocity(-1);
                changeMuzzleVelocityText();
            }
        });
        mUp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pv.changeMuzzleVelocity(10);
                changeMuzzleVelocityText();
                return true;
            }
        });
        mDown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pv.changeMuzzleVelocity(-10);
                changeMuzzleVelocityText();
                return true;
            }
        });
    }
    public void changeElevationText(){
        int ele = Math.round(pv.getElevation());
        TextView text = findViewById(R.id.elevation_text);
        text.setText("Elevation (deg): " + ele);
    }
    public void changeHeightText(){
        int h = Math.round(pv.getTowerHeight());
        TextView text = findViewById(R.id.height_text);
        text.setText("Cannon Height (m): " + h);
    }
    public void changeMuzzleVelocityText(){
        int v = Math.round(pv.getMuzzleVelocity());
        TextView text = findViewById(R.id.muzzle_info);
        text.setText("Muzzle Velocity (m/s): " + v);
    }
    public void changeFinalXAndY(float finalX,float peakY){
        TextView finalXText  = findViewById(R.id.final_x);
        TextView peakYText = findViewById(R.id.final_y);

        finalXText.setText("Final x (m): " + String.format("%.1f",finalX/100f));
        peakYText.setText("Peak y (m): " + String.format("%.1f",peakY/100f));

        this.finalX = finalX;
        this.peakY = peakY;
    }
    public void setFireVisibility(){
        fire.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.about_action){
            Toast.makeText(this,"Project Final, Spring 2019, Ryley J Horton", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.preferences){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pv.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        pv.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int targetLength = pref.getInt("target",1);
        pv.setTargetPosition(targetLength);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putDouble("elevation",pv.getElevation());
        outState.putDouble("muzzleVelocity",pv.getMuzzleVelocity());
        outState.putDouble("towerHeight",pv.getTowerHeight());
        outState.putBoolean("fired",pv.fired);
        outState.putDouble("x",pv.getX());
        outState.putDouble("y",pv.getY());
        outState.putDouble("vx",pv.getVX());
        outState.putDouble("vy",pv.getVY());
        outState.putDouble("peakY",peakY);
        outState.putDouble("finalX",finalX);


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        pv.fired = savedInstanceState.getBoolean("fired");

        int elevation = (int)(savedInstanceState.getDouble("elevation"));
        int muzzleVelocity = (int)(savedInstanceState.getDouble("muzzleVelocity"));
        int towerHeight = (int)(savedInstanceState.getDouble("towerHeight"));
        float x = (float)(savedInstanceState.getDouble("x"));
        float y = (float)(savedInstanceState.getDouble("y"));
        float vx = (float)(savedInstanceState.getDouble("vx"));
        float vy = (float)(savedInstanceState.getDouble("vy"));

        pv.reinitializeParameters(x,y,vx,vy,elevation,towerHeight,muzzleVelocity);
        finalX = (float)(savedInstanceState.getDouble("finalX"));
        peakY = (float)(savedInstanceState.getDouble("peakY"));

        super.onRestoreInstanceState(savedInstanceState);
    }
}
