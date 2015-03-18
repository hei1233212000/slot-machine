package com.lado;

import com.lado.controller.LandingPageController;
import com.lado.util.Fxmls;
import io.datafx.controller.flow.Flow;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			primaryStage.setTitle("Slot Machine");
			new Flow(LandingPageController.class).startInStage(primaryStage);
		} catch (Exception e) {
			LOG.error("e: " + e, e);
			Fxmls.createStandardExceptionDialogAndShow("Startup fail", null, "Fail to launch the application", e);
		}
	}
}
