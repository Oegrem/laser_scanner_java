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
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class Controller implements Initializable {

	@FXML // fx:id="startButton"
	private Button startButton; // Value injected by FXMLLoader

	@FXML // fx:id="parTest"
	private Button parTest;

	@FXML // fx:id="pause"
	private Button pause;

	@FXML // fx:id="slide"
	private Slider slide;

	@Override // This method is called by the FXMLLoader when initialization is
				// complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert parTest != null : "fx:id=\"parTest\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert pause != null : "fx:id=\"pause\" was not injected: check your FXML file 'RootWin.fxml'.";
		assert slide != null : "fx:id=\"slide\" was not injected: check your FXML file 'RootWin.fxml'.";

		// initialize your logic here: all @FXML variables will have been
		// injected

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new Graphics().run();
			}
		});

		parTest.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("ParTest pressed");
				ClientC.request = 2;
			}
		});

		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Pause pressed");
				ClientC.request = 3;
			}
		});

		slide.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ClientC.slider = newValue.floatValue()*10;
				ClientC.request = 4;
			}
		});

	}
}