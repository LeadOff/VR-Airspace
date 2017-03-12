package com.example.gregou.opengl;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {
    private Context mContext;
    private FloatBuffer mVertexBuffer = null;
    private ShortBuffer mTriangleBorderIndicesBuffer = null;
    private int mNumOfTriangleBorderIndices = 0;

    public static float mAngleX = 0.0f;
    public static float mAngleY = 0.0f;
    public float mAngleZ = 0.0f;
    private static float mPreviousX;
    private static float mPreviousY;
    private static final float TOUCH_SCALE_FACTOR = 0.6f;

    public MyRenderer(Context context) {
        mContext = context;
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //
        gl.glTranslatef(0.0f, 0.0f, 0.0f);
        //gl.glMultMatrixf(dynamicview.mR, 0);
        gl.glRotatef(mAngleX-dynamicview.x, 1, 0, 0);
        gl.glRotatef(mAngleY-dynamicview.y, 0, 1, 0);
        gl.glRotatef(mAngleZ-dynamicview.z, 0, 0, 1);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

        // Set line color to green     gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);

        // Draw all lines
        gl.glDrawElements(GL10.GL_LINES, mNumOfTriangleBorderIndices,
                GL10.GL_UNSIGNED_SHORT, mTriangleBorderIndicesBuffer);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        //gl.glDisable(GL10.GL_DEPTH_TEST);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Get all the buffers ready
        setAllBuffers();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        float aspect = (float)width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-aspect, aspect, -1.0f, 1.0f, 1.0f, 100.0f);
    }

    private void setAllBuffers(){

        float vertexlist[] = {                                                                      // Set vertex buffer
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 2.0f, 0.0f,
                -2.0f, -2.0f, -2.0f,
        };
        int j=vertexlist.length;int i;
        for (i=0;i<j;i++){
            vertexlist[i]=10*vertexlist[i];

        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexlist.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertexlist);
        mVertexBuffer.position(0);

        short trigborderindexlist[] = {                                                             // Set triangle border buffer with vertex indices
                4, 0,
                4, 1,
                4, 2,
                4, 3,
                0, 1,
                1, 3,
                3, 2,
                2, 0,
                0, 3,
                4, 5
                ,0,6
        };
        mNumOfTriangleBorderIndices = trigborderindexlist.length;
        ByteBuffer tbibb = ByteBuffer.allocateDirect(trigborderindexlist.length * 2);
        tbibb.order(ByteOrder.nativeOrder());
        mTriangleBorderIndicesBuffer = tbibb.asShortBuffer();
        mTriangleBorderIndicesBuffer.put(trigborderindexlist);
        mTriangleBorderIndicesBuffer.position(0);
    }

    public static boolean onTouchEvent(MotionEvent e) {
        float ex = e.getX();
        float ey = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = ex - mPreviousX;
                float dy = ey - mPreviousY;
                mAngleY = (mAngleY + (int)(dx * TOUCH_SCALE_FACTOR) + 360) % 360;
                mAngleX = (mAngleX + (int)(dy * TOUCH_SCALE_FACTOR) + 360) % 360;
                break;
        }
        mPreviousX = ex;
        mPreviousY = ey;
        return true;
    }

}






   /* @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
        //GLU.gluLookAt(gl, 0.0f, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        // You OpenGL|ES initialization code here
        // ......
    }
*/