package laser_distance_scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.nio.DoubleBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graphics {

	// private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;

	// The window handle
	private long window;

	private int lastRead = 0;
	
	private boolean clearFlag = false;
	
	private Distance_scanner scn;
	// private Vector<Point> points;
	private CopyOnWriteArrayList<Point> pointList = new CopyOnWriteArrayList<Point>();

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		scn = new Distance_scanner(); // Creating new Thread
		// scn.disconnect();
		scn.connect();
		scn.start();

		pointList.addAll(scn.getPointVector());

		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWErrorCallback
			glfwTerminate();
			// errorCallback.release();
		}
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		// glfwSetErrorCallback(errorCallback =
		// GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
												// after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

		int WIDTH = 300;
		int HEIGHT = 300;

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Laser Distance Scanner", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect
																// this in our
																// rendering
																// loop
				if(key == GLFW_KEY_C && action == GLFW_RELEASE){
					clearFlag = true;
				}
			}
		});
		
		
		
		// Get the resolution of the primary monitor
		/*
		 * GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); //
		 * Center our window glfwSetWindowPos( window, (vidmode.getWidth() -
		 * WIDTH) / 2, (vidmode.getHeight() - HEIGHT) / 2 );
		 */
		glfwSetWindowPos(window, (500 - WIDTH) / 2, (500 - HEIGHT) / 2);
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private synchronized void drawSensorPixel() {
		glBegin(GL_POINTS);
		glColor3f(1.0f, 0.0f, 0.0f);
		for (Point p : pointList) {
			float x = ((float) p.x) / 100;
			float y = ((float) p.y) / 100;
			glVertex2f(x, y);
		}
		glEnd();
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-5, 5, -5, 5, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (glfwWindowShouldClose(window) == GL_FALSE) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glPointSize(2);
			
			if(scn.getReadTimes()!=lastRead){
				lastRead = scn.getReadTimes();
				if(clearFlag){
				pointList.clear();
				clearFlag = false;	
				}
				pointList.addAll(scn.getPointVector());
				
			}
			
			drawSensorPixel();

			glPushMatrix();

			

			glBegin(GL_POINTS);
			glColor3f(0.0f, 0.0f, 1.0f);
			for (int i = 0; i < 360; i += 2) {
				float x = 0.3f * (float) Math.cos(i);
				float y = 0.3f * (float) Math.sin(i);
				glVertex2f(x, y);
			}
			glEnd();

			glPopMatrix();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		scn.interrupt();
	}

}
