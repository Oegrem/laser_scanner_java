package laser_distance_scanner;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import data_processing.Cluster;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graphics {
	
	// private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;

	// The window handle
	private long window;
	private int zoomFactor = 600; // 600 ca gesamtes zimmer sichtbar, sollte auf sichtbarkeit der maximalen 10 meter gesetzt werden
	
	private Distance_scanner scn;

	private CopyOnWriteArrayList<Cluster> clusterList = new CopyOnWriteArrayList<Cluster>();

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		scn = Distance_scanner.getDistanceScanner("sim1"); // Getting/Creating Distance_scanner

		scn.start(); // starting Thread with connecting and starting Measurement

		clusterList.addAll(scn.getClusterVector());
		
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

		int WIDTH = 700;
		int HEIGHT = 700;

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
			}
		});

		// Get the resolution of the primary monitor
		/*
		 * GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); //
		 * Center our window glfwSetWindowPos( window, (vidmode.getWidth() -
		 * WIDTH) / 2, (vidmode.getHeight() - HEIGHT) / 2 );
		 */
		glfwSetWindowPos(window, (900 - WIDTH) / 2, (900 - HEIGHT) / 2);
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private synchronized void drawSensorPixel() {
		glBegin(GL_POINTS);
		
		// rot
		glColor3f(1.0f, 0.0f, 0.0f);
		for (Point p : SynchronListHandler.getPointVector()) {
			float x = ((float)p.x) / zoomFactor;
			float y = ((float)p.y) / zoomFactor;
			glVertex2f(x, y);
		}
		
		// irgendwas anderes farbiges
		glColor3f(0.0f, 1.0f, 0.0f);
		for (Cluster c : clusterList) {
			float x = ((float)c.getCenter().x) / zoomFactor;
			float y = ((float)c.getCenter().y) / zoomFactor;
			glVertex2f(x, y);
			// wenn möglich noch rechteck mit den cluster ecken zeichnen (c.getMinCorner() (min x und min y) c.getMaxCorner() (max x und max y))
		}
		
		glEnd();
		glColor4f(0.0f, 0.0f, 1.0f, 0.3f); // last value is opacity (transparenz): lower = more opacity
		glBegin(GL_QUADS);
		for (Cluster c : clusterList) {
			glVertex2f(((float)c.getMinCorner().getX())/zoomFactor,((float)c.getMinCorner().getY())/zoomFactor);
			glVertex2f(((float)c.getMaxCorner().getX())/zoomFactor,((float)c.getMinCorner().getY())/zoomFactor);
			glVertex2f(((float)c.getMaxCorner().getX())/zoomFactor,((float)c.getMaxCorner().getY())/zoomFactor);
			glVertex2f(((float)c.getMinCorner().getX())/zoomFactor,((float)c.getMaxCorner().getY())/zoomFactor);
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

		glEnable(GL_BLEND); // enable blending (opacity)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-6, 6, -6, 6, -1, 1); // To change the resolution of coord.system; change -6 & +6 to range (default: -1 & +1)
		glMatrixMode(GL_MODELVIEW);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (glfwWindowShouldClose(window) == GL_FALSE) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer

			glPointSize(2);

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
