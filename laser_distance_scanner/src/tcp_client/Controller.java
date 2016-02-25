package tcp_client;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

public class Controller implements Initializable {

	@FXML
	// fx:id="startButton"
	private Button startButton; // Value injected by FXMLLoader

	@FXML
	// fx:id="stopButton"
	private Button stopButton;

	@FXML
	// fx:id="drawPoints"
	private CheckBox drawPoints;

	@FXML
	// fx:id="drawClusters"
	private CheckBox drawClusters;

	@FXML
	// fx:id="slowMoSlider"
	private Slider slowMoSlider;

	@FXML
	// fx:id="pause"
	private ToggleButton pause;

	@FXML
	// fx:id="nextFrame"
	private Button nextFrame;

	@FXML
	// fx:id="lastFrame"
	private Button lastFrame;

	@FXML
	// fx:id="pointColor"
	private ColorPicker pointColor;

	@FXML
	// fx:id="clusterColor"
	private ColorPicker clusterColor;

	@FXML
	// fx:id="resetGraphics"
	private Button resetGraphics;

	@FXML
	// fx:id="slide"
	private Slider slide;

	@FXML
	// fx:id="ipField"
	private TextField ipField;

	@FXML
	// fx:id="portField"
	private TextField portField;

	@FXML
	// fx:id="log"
	private TextArea log;

	@FXML
	// fx:id="dataMode"
	private ChoiceBox<String> dataMode;

	@FXML
	// fx:id="simName"
	private TextField simName;

	@FXML
	// fx:id="recordButton"
	private ToggleButton recordButton;

	@FXML
	// fx:id="sliderLabel"
	private Label sliderLabel;

	private Thread t = null;

	@Override
	// This method is called by the FXMLLoader when initialization is
	// complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert stopButton != null : "fx:id=\"stopButton\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert drawPoints != null : "fx:id=\"drawPoints\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert drawClusters != null : "fx:id=\"drawCluster\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert pause != null : "fx:id=\"pause\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert slide != null : "fx:id=\"slide\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert nextFrame != null : "fx:id=\"nextFrame\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert lastFrame != null : "fx:id=\"lastFrame\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert pointColor != null : "fx:id=\"pointColor\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert clusterColor != null : "fx:id=\"clusterColor\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert ipField != null : "fx:id=\"ipField\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert portField != null : "fx:id=\"portField\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert log != null : "fx:id=\"log\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert dataMode != null : "fx:id=\"dataMode\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert simName != null : "fx:id=\"simName\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert recordButton != null : "fx:id=\"recordButton\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert slowMoSlider != null : "fx:id=\"slowMoSlider\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert sliderLabel != null : "fx:id=\"sliderLabel\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert resetGraphics != null : "fx:id=\"resetGraphics\" was not injected: check your FXML file 'RootWin.fxml'.";

		// initialize your logic here: all @FXML variables will have been
		// injected

		pointColor.setValue(new Color(1.0f, 0.0f, 0.0f, 1.0f));

		clusterColor.setValue(new Color(1.0f, 0.0f, 0.0f, 1.0f));

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				if (t == null) {

					int port = 9988;
					String ip = "127.0.0.1";

					if (!portField.getText().equals("")) {
						port = Integer.parseInt(portField.getText());
					}

					if (!ipField.getText().equals("")) {
						ip = ipField.getText();
					}

					ClientC.addLog("Connected to " + ip + ":"
							+ Integer.toString(port));

					log.setText(ClientC.logString);

					t = new ClientC(ip, port);
					t.start();
				}
				new Graphics().run();
			}
		});

		stopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 7;

				ClientC.addLog("Disconnected from Server");

				log.setText(ClientC.logString);

				Graphics.closeGraphics();

				System.exit(0);
			}
		});

		drawPoints.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Graphics.drawPoints = !Graphics.drawPoints;
				pointColor.setDisable(!Graphics.drawPoints);
			}
		});

		pointColor.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Graphics.pointColor = pointColor.getValue();
				ClientC.addLog("New Point Color:" + pointColor.getValue());
			}
		});

		drawClusters.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Graphics.drawClusters = !Graphics.drawClusters;
				clusterColor.setDisable(!Graphics.drawClusters);
			}
		});

		clusterColor.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Graphics.clusterColor = clusterColor.getValue();
				ClientC.addLog("New Cluster Color:" + clusterColor.getValue());
			}
		});

		resetGraphics.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Graphics.resetGraphics();
			}
		});

		slowMoSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				ClientC.data = newValue.floatValue();
				ClientC.request = 6;
			}
		});

		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (ClientC.isSimul) {
					if (pause.getText().equals("pause")) {
						pause.setText("play");

						ClientC.addLog("Paused Simulation");

						lastFrame.setDisable(false);
						nextFrame.setDisable(false);
						sliderLabel.setDisable(false);
						slide.setDisable(false);

					} else {
						pause.setText("pause");

						ClientC.addLog("Resumed Simulation");

						lastFrame.setDisable(true);
						nextFrame.setDisable(true);
						sliderLabel.setDisable(true);
						slide.setDisable(true);

					}
					ClientC.request = 3;
					ClientC.data = 1;
				}
			}
		});

		slide.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				ClientC.data = newValue.floatValue() * 10;
				ClientC.request = 4;
			}
		});

		nextFrame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 3;
				ClientC.data = 3;
			}
		});

		lastFrame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 3;
				ClientC.data = 2;
			}
		});

		dataMode.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue) {
						ClientC.request = 5;
						ClientC.data = newValue;

						if (newValue.equals("Sensor")) {
							recordButton.setDisable(false);
							simName.setEditable(true);
						}

						ClientC.addLog("New DataMode: " + newValue);
						log.setText(ClientC.logString);
					}
				});

		recordButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 8;
				if (simName.getText().equals("")) {
					DateFormat df = new SimpleDateFormat("dd_MM_HH:mm:ss");
					Calendar calobj = Calendar.getInstance();

					ClientC.data = df.format(calobj.getTime());

				} else {
					ClientC.data = simName.getText();
				}
			}
		});

	}

	public void getDataModes() {
		if (dataMode.getItems().size() <= 0) {
			dataMode.getItems().clear();
			dataMode.getItems().addAll(ClientC.modes);
			dataMode.getSelectionModel().select(0);
		}
	}

	public void refreshLog() {
		log.setText(ClientC.logString);
	}
}