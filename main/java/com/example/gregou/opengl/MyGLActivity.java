package com.example.gregou.opengl;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

/** * Our OpenGL program's main activity */
public class MyGLActivity extends Activity  implements SensorEventListener {
    final float pi = (float) Math.PI;
    final float rad2deg = 180/pi;
    public static float x; //pitch
    public static float y; //roll
    public static float z; //azimuth
    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] inOrientMatrix = new float[16];
    float[] outOrientMatrix= new float[16];
    float orientation[] = new float[3];
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;//SensorManager.SENSOR_DELAY_GAME;//500 * 1000; // (int)500ms
    private static final int FROM_RADS_TO_DEGS = -57;
    private GLSurfaceView glView;   // Use GLSurfaceView
    private int orientationmode=1;      /*****************************************************************************************************/

    @Override           // Call back when the activity is started, to initialize the view
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new GLSurfaceView(this);           // Allocate a GLSurfaceView
        glView.setRenderer(new MyRenderer(this)); // Use a custom renderer MyGLRenderer
        this.setContentView(glView);                // This activity sets to GLSurfaceView
        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            if(orientationmode==1){mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);}//TYPE_MAGNETIC_FIELD
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }
    }

    @Override           // Call back when the activity is going into the background
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, MyGLRenderer.camera_rot_get()+"\r\n x "+x+"\r\n y "+y+"\r\n z "+z+' '+MyRenderer.mAngleX+' '+MyRenderer.mAngleY, Toast.LENGTH_LONG).show();
        glView.onPause();
    }


    @Override           // Call back after onPause()
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
    private float[] startFromSensorTransformation;
    private float[] phoneInWorldSpaceMatrix = new float[16];
    public void onSensorChanged(SensorEvent event) {
        if(orientationmode==1) {
            if (event.sensor == mRotationSensor) {
                if (event.values.length > 4) {
                    float[] truncatedRotationVector = new float[4];
                    System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                    update(truncatedRotationVector);
                } else {
                    update(event.values);
                }
            }
        }

    }
    private void update(float[] vectors) {
        if(orientationmode==1){
            vectors[3]=- vectors[3];
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
            int worldAxisX = SensorManager.AXIS_X;
            int worldAxisZ = SensorManager.AXIS_Z;
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);
             x = orientation[0] * FROM_RADS_TO_DEGS;//azimut//pitch//roll
             y = orientation[1] * FROM_RADS_TO_DEGS;
             z = orientation[2] * FROM_RADS_TO_DEGS;
        }
        if(orientationmode==2){
        x=vectors[1] ;
        y=vectors[0] ;
        z=vectors[2] ;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {    }
    @Override
    protected void onDestroy() {// TODO Auto-generated method stub
        super.onDestroy();
        mSensorManager.unregisterListener(this);// if(sersorrunning){//  }
    }
    public boolean onTouchEvent(MotionEvent event) {
        return MyRenderer.onTouchEvent(event);
    }






}








/*
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomag = event.values.clone();
                break;
        }
        if (gravity != null && geomag != null) {
            if (SensorManager.getRotationMatrix(inOrientMatrix, null, gravity, geomag)) {
                SensorManager.remapCoordinateSystem(inOrientMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, outOrientMatrix);
                SensorManager.getOrientation(outOrientMatrix, orientation);
                x = orientation[1] * rad2deg; //pitch
                y = orientation[0] * rad2deg; //azimuth
                z = orientation[2] * rad2deg; //roll
                glView.requestRender();

            }
        }
    }
*/
//MyGLRenderer.camera_rot_set(orientation);
//((TextView)findViewById(R.id.pitch)).setText("Pitch: "+pitch);
//((TextView)findViewById(R.id.roll)).setText("Roll: "+roll);



/**
 * Created by gregou on 06/03/2017.
 */