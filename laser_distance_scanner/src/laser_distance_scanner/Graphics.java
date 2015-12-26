package laser_distance_scanner;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import code_snippets.clusterLineStrip;
import code_snippets.dbscan;
import data_processing.Cluster;
import data_processing.Clustering;
import data_processing.Processing;
import data_processing.Settings;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Point;
import java.util.ArrayList;

public class Graphics {

	// private GLFWErrorCallback errorCallback;
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

	private float xMove = 1;

	private float yMove = 1;

	private static int SCANNER_SIZE = 45;

	// The window handle
	private long window;

	private Distance_scanner scn;

	private boolean drawPoints = true;

	private boolean drawLines = true;

	private boolean drawDbscan = false;

	private int toChange = 0;

	private float zoom = 1;

	private boolean clusterColors = false;

	private ArrayList<DSButton> buttonList = new ArrayList<DSButton>();

	// private dbscan dbscn = new dbscan(10, 5); // DBSCAN clustering

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		scn = Distance_scanner.getDistanceScanner(); // Getting/Creating
														// Distance_scanner

		scn.start(); // starting Thread with connecting and starting Measurement

		// clusterList.addAll(scn.getClusterVector());

		// dbscn.cluster(SynchronListHandler.getPointVector());

		buttonList.add(new DSButton(new Point(1000, 1000),
				new Point(2000, 2000), 1, 255, 0, 0, 100));

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
							int id = DSButton.isButtonClicked(xpos, ypos,
									buttonList);
							if (id == -1) {
								xMove += (float) (xpos - xold) * 10;
								yMove += (float) (yold - ypos) * 10;
							} else {
								buttonAction(id);
							}
						}
						xold = xpos;
						yold = ypos;
					}
				});

		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action,
					int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect
																// this in our
																// rendering
																// loop
				if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
					if (Distance_scanner.playRecord) {
						Distance_scanner.playRecord = false;
						System.out.println("Record STOP");
					} else {
						Distance_scanner.playRecord = true;
						System.out.println("Record PLAY");
					}
				}

				if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
					if (!Distance_scanner.playRecord) {
						Distance_scanner.lastFrame = true;
					}
				}

				if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
					if (!Distance_scanner.playRecord) {
						Distance_scanner.nextFrame = true;
					}
				}

				if (key == GLFW_KEY_1 && action == GLFW_RELEASE) {
					if (drawPoints) {
						drawPoints = false;
						System.out.println("drawPoints OF");
					} else {
						drawPoints = true;
						System.out.println("drawPoints ON");
					}
				}

				if (key == GLFW_KEY_2 && action == GLFW_RELEASE) {
					if (drawDbscan) {
						drawDbscan = false;
						System.out.println("drawDbscan OF");
					} else {
						drawDbscan = true;
						System.out.println("drawDbscan ON");
					}
				}

				if (key == GLFW_KEY_3 && action == GLFW_RELEASE) {
					if (drawLines) {
						drawLines = false;
						System.out.println("drawLines OF");
					} else {
						drawLines = true;
						System.out.println("drawLines ON");
					}

				}

				if (key == GLFW_KEY_O && action == GLFW_RELEASE) {
					if (clusterColors) {
						clusterColors = false;
						System.out.println("clusterColors OFF");
					} else {
						clusterColors = true;
						System.out.println("clusterColors ON");
					}
				}

				if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
					if (drawLines) {
						drawLines = false;
						System.out.println("drawLines OF");
					} else {
						drawLines = true;
						System.out.println("drawLines ON");
					}
				}
				if (key == GLFW_KEY_G && action == GLFW_RELEASE) {
					if (Processing.isStraightening) {
						Processing.isStraightening = false;
						System.out.println("Glätten OF");
					} else {
						Processing.isStraightening = true;
						System.out.println("Glätten ON");
					}
				}

				if (key == GLFW_KEY_X && action == GLFW_RELEASE) {
					switch (toChange) {
					case 0:
						Settings.setClustering_threshold(Settings.getClustering_threshold() + 0.1);
						System.out
								.println("threshold: " + Settings.getClustering_threshold());
						break;
					case 1:
						break;
					case 2:
						Distance_scanner.slomo += 1;
						System.out.println("slomo: " + Distance_scanner.slomo);
						break;
					case 3:
						Settings.setClustering_search_range(Settings.getClustering_search_range() + 1);
						System.out.println("searchRange: "
								+ Settings.getClustering_search_range());
						break;
					case 4:
						System.out.println(dbscan.incRange(1));
						break;
					case 5:
						System.out.println(dbscan.incSize(1));
						break;
					}
				}

				if (key == GLFW_KEY_C && action == GLFW_RELEASE) {
					switch (toChange) {
					case 0:
						Settings.setClustering_threshold(Settings.getClustering_threshold() - 0.1);
						System.out
								.println("threshold: " + Settings.getClustering_threshold());
						break;
					case 1:
						break;
					case 2:
						if (Distance_scanner.slomo > 1) {
							Distance_scanner.slomo -= 1;
							System.out.println("slomo: "
									+ Distance_scanner.slomo);
						}
						break;
					case 3:
						Settings.setClustering_search_range(Settings.getClustering_search_range() - 1);
						System.out.println("searchRange: "
								+ Settings.getClustering_search_range());
						break;
					case 4:
						System.out.println(dbscan.incRange(-1));

						break;
					case 5:
						System.out.println(dbscan.incSize(-1));
						break;
					}
				}

				if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
					toChange++;
					if (toChange > 5) {
						toChange = 0;
					}
					switch (toChange) {
					case 0:
						System.out.println("threshold");
						break;
					case 1:
						break;
					case 2:
						System.out.println("slomo");
						break;
					case 3:
						System.out.println("searchRange");
						break;
					case 4:
						System.out.println("Range");
						break;
					case 5:
						System.out.println("Size");
						break;

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

	private void buttonAction(int id) {
		switch (id) {
		default:
			System.out.println(id);
			break;
		}
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

		}
		if (drawDbscan) {
			
			int rec = 0;
			
			for (clusterLineStrip cLS : SynchronListHandler.getClusterLines()) {
				if(cLS.recognised){
					rec++;
				setColor(cLS.getClusterId());
				
				glBegin(GL_LINE_STRIP);

				for (Point p : cLS.getLineStripPoints()) {
					glVertex2f((float) p.x, (float) p.y);
				}
				glEnd();
				}
			}
}
		if (drawLines) {

			// irgendwas anderes glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			glBegin(GL_POINTS);
			for (Cluster c : SynchronListHandler.getClusterVector()) {
				float x = ((float) c.getCenter().x);
				float y = ((float) c.getCenter().y);
				glVertex2f(x, y); // wenn möglich noch rechteck mit den cluster
									// // ecken zeichnen(c.getMinCorner() (min x
									// // und // min y) c.getMaxCorner() (max x
									// und max // y)) } glEnd();
			}
			glColor4f(0.0f, 0.0f, 1.0f, 0.8f); // last value is
												// opacity(transparenz):
												// lower =
												// // more opacity

			glBegin(GL_QUADS);
			for (Cluster c : SynchronListHandler.getClusterVector()) {
				if (clusterColors) {
					setColor(c.getID());
				}
				glVertex2f(((float) c.getMinCorner().getX()), ((float) c
						.getMinCorner().getY()));
				glVertex2f(((float) c.getMaxCorner().getX()), ((float) c
						.getMinCorner().getY()));
				glVertex2f(((float) c.getMaxCorner().getX()), ((float) c
						.getMaxCorner().getY()));
				glVertex2f(((float) c.getMinCorner().getX()), ((float) c
						.getMaxCorner().getY()));
			}
			glEnd();

		}
	}

	private void setColor(int i) {
		switch (i % 9) {
		case 0:
			glColor3f(0.5f, 0.5f, 1.0f);
			break;
		case 1:
			glColor3f(1.0f, 0.0f, 0.0f);
			break;
		case 2:
			glColor3f(0.0f, 1.0f, 0.0f);
			break;
		case 3:
			glColor3f(0.0f, 0.0f, 1.0f);
			break;
		case 4:
			glColor3f(1.0f, 1.0f, 0.0f);
			break;
		case 5:
			glColor3f(1.0f, 0.0f, 1.0f);
			break;
		case 6:
			glColor3f(0.0f, 1.0f, 1.0f);
			break;
		case 7:
			glColor3f(0.5f, 0.5f, 0.5f);
			break;
		case 8:
			glColor3f(1.0f, 0.5f, 0.5f);
			break;
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
		glOrtho(-3500, 3500, -3500, 3500, -1, 1); // To change the resolution of
		// coord.system; change -6 & +6 to range
		// (default: -1 & +1)
		glMatrixMode(GL_MODELVIEW);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (glfwWindowShouldClose(window) == GL_FALSE) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer

			glPointSize(2);

			glPushMatrix();

			glTranslatef(xMove, yMove, 1);

			glScalef(zoom, zoom, 1);

			drawSensorPixel();

			glPointSize(1);

			for (float g = 0; g < 10000; g += 500) {
				glColor4f(1 - (g / 10000), (g / 10000), 0.0f, 0.3f);
				glBegin(GL_POINTS);
				for (float i = 0; i < 360; i += 1) {

					float x = (SCANNER_SIZE + g) * (float) Math.cos(i);
					float y = (SCANNER_SIZE + g) * (float) Math.sin(i);

					glVertex2f(x, y);

				}
				glEnd();
			}

			glPopMatrix();

			for (DSButton dsb : buttonList) {
				// dsb.drawButton();
			}

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		scn.interrupt();
	}

}
