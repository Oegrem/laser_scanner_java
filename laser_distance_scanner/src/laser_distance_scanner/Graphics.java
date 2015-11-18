package laser_distance_scanner;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import code_snippets.clusterPoint;
import code_snippets.dbscan;
import code_snippets.line_extraction;
import code_snippets.Line;
import data_processing.Cluster;
import data_processing.ClusterPoint;
import data_processing.Clustering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.plaf.synth.SynthSpinnerUI;

public class Graphics {
	
	// private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;

	// The window handle
	private long window;
	private int zoomFactor = 600; // 600 ca gesamtes zimmer sichtbar, sollte auf sichtbarkeit der maximalen 10 meter gesetzt werden
	
	private Distance_scanner scn;

	private boolean drawPoints = true;

	private boolean drawLines = true;

	private int toChange = 0;

	// private dbscan dbscn = new dbscan(10, 5); // DBSCAN clustering

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		scn = Distance_scanner.getDistanceScanner("sim1"); // Getting/Creating
															// Distance_scanner

		scn.start(); // starting Thread with connecting and starting Measurement

		// clusterList.addAll(scn.getClusterVector());

		// dbscn.cluster(SynchronListHandler.getPointVector());

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

				if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
					if (drawPoints) {
						drawPoints = false;
					} else {
						drawPoints = true;
					}
				}

				if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
					if (drawLines) {
						drawLines = false;
					} else {
						drawLines = true;
					}
				}

				if (key == GLFW_KEY_X && action == GLFW_RELEASE) {
					switch (toChange) {
					case 0:
						Clustering.threshold += 10;
						System.out.println(Integer.toString(Clustering.threshold));
						break;
					case 1:
						Clustering.minClusterSize++;
						System.out.println(Integer.toString(Clustering.minClusterSize));
						break;
					case 2:
						Distance_scanner.slomo+=1;
						System.out.println(""+Distance_scanner.slomo);
						break;
					}
				}

				if (key == GLFW_KEY_C && action == GLFW_RELEASE) {
					switch (toChange) {
					case 0:
						Clustering.threshold -= 10;
						System.out.println(Integer.toString(Clustering.threshold));
						break;
					case 1:
						Clustering.minClusterSize--;
						System.out.println(Integer.toString(Clustering.minClusterSize));
						break;
					case 2:
						Distance_scanner.slomo-=1;

						System.out.println(""+Distance_scanner.slomo);
						break;
					}
				}

				if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
					toChange++;
					if (toChange > 2) {
						toChange = 0;
					}
				}

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
			glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			glBegin(GL_POINTS);
			// rot

			for (Point p : SynchronListHandler.getPointVector()) {
				float x = ((float) p.x);
				float y = ((float) p.y);
				glVertex2f(x, y);
			}

			glEnd();
		} /*
			 * if(drawLines){ glColor3f(0.0f, 1.0f, 0.0f); glBegin(GL_LINES);
			 * 
			 * for (Line line : SynchronListHandler.getLineList()) {
			 * glVertex2f((float) line.getP1().getX() / 300, (float)
			 * line.getP1().getY() / 300);
			 * 
			 * glVertex2f((float) line.getP2().getX() / 300, (float)
			 * line.getP2().getY() / 300); } glEnd(); }
			 * 
			 * dbscn.cluster(SynchronListHandler.getPointVector()); // Drawing
			 * the clustered Points
			 * 
			 * 
			 * for(Vector<clusterPoint> cluster : dbscn.getClusters()){
			 * 
			 * glColor3f(0.0f,0.0f,1.0f); glBegin(GL_POINTS); for(Point p :
			 * cluster){ float x = ((float)p.x) / 300; float y = ((float)p.y) /
			 * 300; glVertex2f(x, y); }
			 * 
			 * } glEnd();
			 */

		// irgendwas anderes
		glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		glBegin(GL_POINTS);
		for (Cluster c : SynchronListHandler.getClusterVector()) {
			float x = ((float) c.getCenter().x);
			float y = ((float) c.getCenter().y);
			glVertex2f(x, y); // wenn m�glich noch rechteck mit den cluster
								// ecken zeichnen (c.getMinCorner() (min x und
								// min y) c.getMaxCorner() (max x und max y))
		}
		glEnd();

		glColor3f(1.0f, 0.0f, 1.0f);

		glBegin(GL_POINTS);
		for (ClusterPoint c : SynchronListHandler.getClusteredPoints()) {
			float x = ((float) c.x);
			float y = ((float) c.y);

			glVertex2f(x, y);
		}
		
		glEnd();

		if (drawLines) {
			glColor4f(0.0f, 0.0f, 1.0f, 0.3f); // last value is
												// opacity(transparenz): lower =
												// more opacity
			glBegin(GL_QUADS);
			for (Cluster c : SynchronListHandler.getClusterVector()) {
				glVertex2f(((float) c.getMinCorner().getX()), ((float) c.getMinCorner().getY()));
				glVertex2f(((float) c.getMaxCorner().getX()), ((float) c.getMinCorner().getY()));
				glVertex2f(((float) c.getMaxCorner().getX()), ((float) c.getMaxCorner().getY()));
				glVertex2f(((float) c.getMinCorner().getX()), ((float) c.getMaxCorner().getY()));
			}
			glEnd();
		}
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
		glOrtho(-3000, 3000, -3000, 3000, -1, 1); // To change the resolution of
		// coord.system; change -6 & +6 to range
		// (default: -1 & +1)
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
				float x = 80 * (float) Math.cos(i);
				float y = 80 * (float) Math.sin(i);
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
