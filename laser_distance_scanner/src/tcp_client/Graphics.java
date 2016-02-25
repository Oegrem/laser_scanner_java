package tcp_client;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import data_processing.Clustering;
import data_processing.Processing;
import data_processing.SimpleCluster;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.scene.paint.Color;
import laser_distance_scanner.SynchronListHandler;

public class Graphics {

	// private GLFWErrorCallback errorCallback;
	@SuppressWarnings("unused")
	private GLFWKeyCallback keyCallback;

	@SuppressWarnings("unused")
	private GLFWScrollCallback scrollCallBack;

	@SuppressWarnings("unused")
	private GLFWMouseButtonCallback mouseButtonCallBack;

	@SuppressWarnings("unused")
	private GLFWCursorPosCallback cursorPosCallBack;

	private double xold = 0;

	private double yold = 0;

	private boolean leftButtonPressed = false;

	private static float xMove = 1;

	private static float yMove = 1;

	private static float zoom = 1;

	public static boolean drawPoints = true;

	public static Color pointColor = new Color(1, 0, 0, 1);

	public static boolean drawClusters = true;

	public static Color clusterColor = new Color(1, 0, 0, 1);

	private static boolean showGraphics = true;

	// The window handle
	private long window;

	public static CopyOnWriteArrayList<Point> cp = new CopyOnWriteArrayList<Point>();

	public static CopyOnWriteArrayList<SimpleCluster> sP = new CopyOnWriteArrayList<SimpleCluster>();

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);

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
		window = glfwCreateWindow(WIDTH, HEIGHT, "Laser Distance Scanner",
				NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.

		glfwSetScrollCallback(window,
				scrollCallBack = new GLFWScrollCallback() {

					@Override
					public void invoke(long window, double xoffset,
							double yoffset) {
						zoom += yoffset / 5;
					}
				});

		glfwSetMouseButtonCallback(window,
				mouseButtonCallBack = new GLFWMouseButtonCallback() {

					@Override
					public void invoke(long window, int button, int action,
							int mods) {
						if (button == GLFW_MOUSE_BUTTON_LEFT
								&& action == GLFW_PRESS) {
							leftButtonPressed = true;
						}

						if (button == GLFW_MOUSE_BUTTON_LEFT
								&& action == GLFW_RELEASE) {
							leftButtonPressed = false;
						}
					}

				});

		glfwSetCursorPosCallback(window,
				cursorPosCallBack = new GLFWCursorPosCallback() {
					@Override
					public void invoke(long window, double xpos, double ypos) {
						if (leftButtonPressed) {

							xMove += (float) (xpos - xold) * 10;
							yMove += (float) (yold - ypos) * 10;

						}
						xold = xpos;
						yold = ypos;
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

		if (drawPoints) {

			float r, g, b, a;
			r = (float) pointColor.getRed();
			g = (float) pointColor.getGreen();
			b = (float) pointColor.getBlue();
			a = (float) clusterColor.getOpacity();

			glColor4f(r, g, b, a);
			glBegin(GL_POINTS);

			try {
				for (int i = 0; i < cp.size(); i++) {

					Point p = cp.get(i);

					glVertex2f((float) p.x, (float) p.y);

				}

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Point Array Out of Bounds");
			}

			glEnd();

		}
		if (drawClusters) {

			float r, g, b, a;
			r = (float) clusterColor.getRed();
			g = (float) clusterColor.getGreen();
			b = (float) clusterColor.getBlue();
			a = (float) clusterColor.getOpacity();

			glColor4f(r, g, b, a);

			for (SimpleCluster sC : sP) {
				try {
					Point mid = new Point(0, 0);
					int z = 0;
					for (int i = sC.getFirstElement(); i <= sC.getLastElement()
							&& i < cp.size(); i++) {
						mid.x += cp.get(i).x;
						mid.y += cp.get(i).y;
						z++;
					}
					if (z == 0)
						continue;
					mid.x /= z;
					mid.y /= z;

					glBegin(GL_POLYGON);

					glVertex2f(cp.get(sC.getFirstElement()).x,
							cp.get(sC.getFirstElement()).y);
					glVertex2f(mid.x, mid.y);
					glVertex2f(cp.get(sC.getLastElement()).x,
							cp.get(sC.getLastElement()).y);

					glVertex2f(mid.x * (float) 1.5, mid.y * (float) 1.5);
					glEnd();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}

	}

	public static void resetGraphics() {
		xMove = 1;
		yMove = 1;
		zoom = 1;
		drawPoints = true;
		pointColor = new Color(1, 0, 0, 1);
		drawClusters = true;
		clusterColor = new Color(1, 0, 0, 1);
	}

	public static void closeGraphics() {
		showGraphics = false;
		return;
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

		glLineWidth(3);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-3500, 3500, -3500, 3500, -1, 1); // To change the resolution of
		// coord.system; change -6 & +6 to range
		// (default: -1 & +1)
		glMatrixMode(GL_MODELVIEW);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (glfwWindowShouldClose(window) == GL_FALSE && showGraphics) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer

			glPointSize(2);

			glPushMatrix();

			glTranslatef(xMove, yMove, 1);

			glScalef(zoom, zoom, 1);

			drawSensorPixel();

			glBegin(GL_POINTS);
			glColor3f(0.0f, 0.0f, 1.0f);
			for (int i = 0; i < 360; i += 2) {
				float x = 80 * (float) Math.cos(i);
				float y = 80 * (float) Math.sin(i);
				glVertex2f(x, y);
				float xM = 10000 * (float) Math.cos(i);
				float yM = 10000 * (float) Math.sin(i);
				glVertex2f(xM, yM);
			}
			glEnd();

			glPopMatrix();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
		return;
	}

}
