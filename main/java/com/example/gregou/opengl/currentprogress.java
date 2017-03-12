package com.example.gregou.opengl;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class currentprogress extends Activity implements SensorEventListener {
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
    Button[] buttons=new Button[5];
    private int inclinaison=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glView = new GLSurfaceView(this);           // Allocate a GLSurfaceView
        glView.setRenderer(new MynewRenderer(this)); // Use a custom renderer MyGLRenderer
        // Creating a new RelativeLayout
        RelativeLayout relativeLayout = new RelativeLayout(this);
        LinearLayout ll= new LinearLayout(this);
        // Defining the RelativeLayout layout parameters with Fill_Parent
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        // Creating a new Left Button
        buttons[0] = new Button(this);
        buttons[0].setText("Button1");

        // Creating a new Left Button with Margin
        buttons[1] = new Button(this);
        buttons[1].setText("Button2");

        // Creating a new Center Button
        buttons[2] = new Button(this);
        buttons[2].setText("Button3");

        // Creating a new Bottom Button
        buttons[3] = new Button(this);
        buttons[3].setText("Button4");

        // Add a Layout to the Buttons
        AddButtonLayout(buttons[0], RelativeLayout.ALIGN_PARENT_RIGHT);//CENTER_IN_PARENT //ALIGN_PARENT_BOTTOM //ALIGN_PARENT_LEFT
        AddButtonLayout(buttons[1], RelativeLayout.ALIGN_PARENT_RIGHT);
        AddButtonLayout(buttons[2], RelativeLayout.ALIGN_PARENT_RIGHT);
        AddButtonLayout(buttons[3], RelativeLayout.ALIGN_PARENT_BOTTOM);
        // Add a Layout to the Button with Margin
        //LayoutAddButton(button2, RelativeLayout.ALIGN_PARENT_LEFT, 30, 80, 0, 0);

        // Add the Buttons to the View
        relativeLayout.addView(glView);
        relativeLayout.addView(ll,llparams);

        ll.addView(buttons[0]);
        ll.addView(buttons[1]);
        ll.addView(buttons[2]);
        relativeLayout.addView(buttons[3]);

        // Setting the RelativeLayout as our content view
        setContentView(relativeLayout, relativeLayoutParams);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        /*try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            if(orientationmode==1){
                mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            }//TYPE_MAGNETIC_FIELD
            mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }*/
    }

    private void LayoutAddButton(Button button, int centerInParent, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        // Defining the layout parameters of the Button
        RelativeLayout.LayoutParams buttonLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Add Margin to the LayoutParameters
        buttonLayoutParameters.setMargins(marginLeft, marginTop, marginRight, marginBottom);

        // Add Rule to Layout
        buttonLayoutParameters.addRule(centerInParent);

        // Setting the parameters on the Button
        button.setLayoutParams(buttonLayoutParameters);
    }

    private void AddButtonLayout(Button button, int centerInParent) {
        // Just call the other AddButtonLayout Method with Margin 0
        LayoutAddButton(button, centerInParent, 0, 0, 0, 0);
    }

    @Override           // Call back when the activity is going into the background
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, MyGLRenderer.camera_rot_get()+"\r\n x "+x+"\r\n y "+y+"\r\n z "+z+' '+MyRenderer.mAngleX+' '+MyRenderer.mAngleY, Toast.LENGTH_LONG).show();
        glView.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override           // Call back after onPause()
    protected void onResume() {
        super.onResume();
        glView.onResume();
        Sensor gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    public static float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float[] data;
        if (type == Sensor.TYPE_ACCELEROMETER) {
            data = mGData;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            data = mMData;
        } else {
            // we should not be here.
            return;
        }
        for (int i = 0; i < 3; i++)
            data[i] = event.values[i];
        SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
// some test code which will be used/cleaned up before we ship this.
//        SensorManager.remapCoordinateSystem(mR,
//                SensorManager.AXIS_X, SensorManager.AXIS_Z, mR);
        SensorManager.remapCoordinateSystem(mR,SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mR);
        SensorManager.getOrientation(mR, mOrientation);
        float incl = SensorManager.getInclination(mI);
        final float rad2deg = (float) (180.0f / Math.PI);
        mCount = 0;
        x = (int) (mOrientation[0] * rad2deg);
        y = (int) (mOrientation[1] * rad2deg);
        z = (int) (mOrientation[2] * rad2deg);
        inclinaison = (int) (incl * rad2deg);
        buttons[0].setText("x "+x);
        buttons[1].setText("y "+y);
        buttons[2].setText("z "+z);
        buttons[3].setText("incl "+inclinaison);

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


            Log.d("Compass", "yaw: " + (int)(mOrientation[0]*rad2deg) +
                    "  pitch: " + (int)(mOrientation[1]*rad2deg) +
                    "  roll: " + (int)(mOrientation[2]*rad2deg) +
                    "  incl: " + (int)(incl*rad2deg)
            );




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
*/

/*
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
            //azimut//pitch//roll
            x = orientation[0] * FROM_RADS_TO_DEGS;
            y = orientation[1] * FROM_RADS_TO_DEGS;
            z = orientation[2] * FROM_RADS_TO_DEGS;
            buttons[0].setText("x "+x);
            buttons[1].setText("y "+y);
            buttons[2].setText("z "+z);
        }
        if(orientationmode==2){
            x=vectors[1] ;
            y=vectors[0] ;
            z=vectors[2] ;
        }
    }






    */