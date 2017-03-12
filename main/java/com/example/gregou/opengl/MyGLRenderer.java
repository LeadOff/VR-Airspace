package com.example.gregou.opengl;

/**
 * Created by gregou on 06/03/2017.
 */

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.widget.Toast;

/**
     *  OpenGL Custom renderer used with GLSurfaceView
     */
    public class MyGLRenderer implements GLSurfaceView.Renderer {

    Context context;   // Application's context
        Triangle triangle;     // ( NEW )
        Square quad;           // ( NEW )

        private Pyramid pyramid;    // (NEW)
        private Cube cube;          // (NEW)
        private static float[] camera_rot = new float[3];
        private static float anglePyramid = 0; // Rotational angle in degree for pyramid (NEW)
        private static float angleCube = 0;    // Rotational angle in degree for cube (NEW)
        private static float speedPyramid = 2.0f; // Rotational speed for pyramid (NEW)
        private static float speedCube = -1.5f;   // Rotational speed for cube (NEW)

        // Constructor
        public MyGLRenderer(Context context) {
            this.context = context;

            // Set up the data-array buffers for these shapes ( NEW )
            triangle = new Triangle();   // ( NEW )
            quad = new Square();         // ( NEW )
            pyramid = new Pyramid();   // (NEW)
            cube = new Cube();         // (NEW)
        }
       /* public static void camera_rot_set(float[] vectors) {

            camera_rot=vectors;

        }*/

        // Call back when the surface is first created or re-created
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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

        // Call back after onSurfaceCreated() or whenever the window's size changes
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            if (height == 0) height = 1;   // To prevent divide by zero
            float aspect = (float)width / height;// Set the viewport (display area) to cover the entire window
            gl.glViewport(0, 0, width, height);// Setup perspective projection, with aspect ratio matches viewport
            gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
            gl.glLoadIdentity();                 // Reset projection matrix
            GLU.gluPerspective(gl, 100, aspect, 0.5f, 200.f);// Use perspective projection
            gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
            gl.glLoadIdentity();                 // Reset
            // You OpenGL|ES display re-sizing code here
            // ......

        }

        // Call back to draw the current frame.
        @Override
        public void onDrawFrame(GL10 gl) {
            // Clear color and depth buffers using clear-value set earlier
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();
            GLU.gluLookAt(gl, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(MyGLActivity.x, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(MyGLActivity.y, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(MyGLActivity.z, 0.0f, 0.0f, 1.0f);
            //in case you have transformation, eg. the position of the object, you can do them here
            //gl.Translatef(0.0f, 0.0f, -DISTANCE NORTH);
            //gl.Translatef(0.0f, DISTANCE EAST, 0.0f);
            gl.glPushMatrix();
            //object draw

            //glRotatef(orbitDegrees, 0.f, 1.f, 0.f);/* orbit the Y axis */
           // glCallList(SCENE); /* draw the scene */
            // You OpenGL|ES rendering code here
            // ......
            //GLU.gluLookAt(gl, camera_rot[0], camera_rot[1], camera_rot[2],    -camera_rot[0], -camera_rot[1], -camera_rot[2],    0.0f, 1.0f, 0.0f);

            // ----- Render the Pyramid -----
            gl.glLoadIdentity();                 // Reset the model-view matrix
            gl.glTranslatef(-1.5f, 0.0f, -6.0f); // Translate left and into the screen
            gl.glRotatef(anglePyramid, 0.1f, 1.0f, -0.1f); // Rotate (NEW)
            pyramid.draw(gl);                              // Draw the pyramid (NEW)


            gl.glLoadIdentity();                 // Reset model-view matrix ( NEW )
            gl.glTranslatef(-1.5f, 2.0f, -6.0f); // Translate left and into the screen ( NEW )
            triangle.draw(gl);                   // Draw triangle ( NEW )

            // ----- Render the Color Cube -----
            gl.glLoadIdentity();                // Reset the model-view matrix
            gl.glTranslatef(1.5f, 0.0f, -6.0f); // Translate right and into the screen
            gl.glScalef(0.5f, 0.5f, 0.5f);      // Scale down (NEW)
            gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the axis (1,1,1) (NEW)
            cube.draw(gl);                      // Draw the cube (NEW)

            gl.glLoadIdentity();
            // Translate right, relative to the previous translation ( NEW )
            gl.glTranslatef(1.5f, 2.0f, -6.0f);
            //gl.glScalef(0.8f, 0.8f, 0.8f);
            quad.draw(gl);                       // Draw quad ( NEW )

            // Update the rotational angle after each refresh (NEW)
            anglePyramid += speedPyramid;   // (NEW)
            angleCube += speedCube;         // (NEW)

            //Toast.makeText(context, "This is my Toast message!",Toast.LENGTH_LONG).show();
        }

    public static String camera_rot_get() {

        return Float.toString(MyGLActivity.x)+' '+Float.toString(MyGLActivity.y)+' '+Float.toString(MyGLActivity.z);
    }
















}
