package tcp_client;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class Controller implements Initializable {

	@FXML
	// fx:id="startButton"
	private Button startButton; // Value injected by FXMLLoader

	@FXML
	// fx:id="stopButton"
	private Button stopButton;

	@FXML
	// fx:id="parTest"
	private Button parTest;

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
	// fx:id="colorPicker"
	private ColorPicker colorPicker;

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

	@Override
	// This method is called by the FXMLLoader when initialization is
	// complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert stopButton != null : "fx:id=\"log\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert parTest != null : "fx:id=\"parTest\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert pause != null : "fx:id=\"pause\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert slide != null : "fx:id=\"slide\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert nextFrame != null : "fx:id=\"nextFrame\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert lastFrame != null : "fx:id=\"lastFrame\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert colorPicker != null : "fx:id=\"colorPicker\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert ipField != null : "fx:id=\"ipField\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert portField != null : "fx:id=\"portField\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert log != null : "fx:id=\"log\" was not injected: check your FXML file 'RootWin.fxml'.";

		// initialize your logic here: all @FXML variables will have been
		// injected

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				int port = 9988;
				String ip = "127.0.0.1";

				if (!portField.getText().equals("")) {
					port = Integer.parseInt(portField.getText());
				}

				if (!ipField.getText().equals("")) {
					ip = ipField.getText();
				}

				ClientC.logString += "Connected to " + ip + ":"
						+ Integer.toString(port) + "\n";

				log.setText(ClientC.logString);

				Thread t = new ClientC(ip, port);
				t.start();

				new Graphics().run();
			}
		});

		stopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 7;

				ClientC.logString += "Disconnected from Server\n";

				log.setText(ClientC.logString);
			}
		});

		parTest.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("ParTest pressed");

				log.setText(ClientC.logString);

				ClientC.request = 2;
			}
		});

		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (ClientC.isSimul) {
					if (pause.getText() == "pause") {
						pause.setText("play");

						ClientC.logString += "Paused Simulation\n";

						log.setText(ClientC.logString);

					} else {
						pause.setText("pause");

						ClientC.logString += "Resumed Simulation\n";

						log.setText(ClientC.logString);
					}
					ClientC.request = 3;
				}
			}
		});

		slide.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				ClientC.slider = newValue.floatValue() * 10;
				ClientC.request = 4;
			}
		});

		nextFrame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 5;
			}
		});

		lastFrame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.request = 6;
			}
		});

		colorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClientC.grColor = colorPicker.getValue();
			}
		});

	}
}