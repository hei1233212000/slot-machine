package com.lado.controller;

import com.lado.model.Choice;
import com.lado.service.ChoiceService;
import com.lado.util.Fxmls;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FXMLController("/fxml/landing-page.fxml")
public class LandingPageController {
	private static final Logger LOG = LoggerFactory.getLogger(LandingPageController.class);

	private static final String ACTION_IMPORT_FILE = "importFile";
	private static final String ACTION_EXPORT_FILE = "exportFile";
	private static final String ACTION_AOUT = "about";
	private static final String ACTION_ROLLING = "rolling";

	@FXML
	private ListView<String> choiceListView;

	@FXML
	@ActionTrigger(ACTION_IMPORT_FILE)
	private MenuItem importFile;

	@FXML
	@ActionTrigger(ACTION_EXPORT_FILE)
	private MenuItem exportFile;

	@FXML
	@ActionTrigger(ACTION_AOUT)
	private MenuItem about;

	@FXML
	@ActionTrigger(ACTION_ROLLING)
	private Button rollingButton;

	@FXML
	private Label result;

	@FXML
	private Label rollCount;

	private ChoiceService choiceService;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		// TODO: should use IoC framework
		choiceService = new ChoiceService();
		List<String> choiceNames = choiceService.findAll()
										.stream()
										.flatMap(c -> Stream.of(c.getName()))
										.collect(Collectors.toList());
		choiceListView.setItems(FXCollections.observableArrayList(choiceNames));
	}

	@ActionMethod(ACTION_ROLLING)
	public void rolling() {
		Choice choice = choiceService.randomPick();
		result.setText(choice.getName());
		rollCount.setText(Integer.toString(choiceService.getRollCount()));
	}

	@ActionMethod(ACTION_IMPORT_FILE)
	public void importFile() {
		Fxmls.createStandardAlertAndShow(Alert.AlertType.INFORMATION, "Import", null, "Coming soon...");
		if (1 == 1) return;
		try {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Open File");
			File file = chooser.showOpenDialog(new Stage());
			String fileContentType = Files.probeContentType(file.toPath());
			if (!"text/plain".equals(fileContentType)) Fxmls.createStandardAlertAndShow(Alert.AlertType.WARNING, "Import", null, "Only text file is allowed");
			// convert file into list of pojo
			// append the list of pojo into the current list
		} catch (Exception e) {
			LOG.error("e: " + e, e);
			Fxmls.createStandardExceptionDialogAndShow("Import", null, "Fail to import file", e);
		}
	}

	@ActionMethod(ACTION_EXPORT_FILE)
	public void exportFile() {
		// get the current list of pojo
		// convert the list into file
		// export it
		Fxmls.createStandardAlertAndShow(Alert.AlertType.INFORMATION, "Export", null, "Coming soon...");
	}

	@ActionMethod(ACTION_AOUT)
	public void about() {
		Fxmls.createStandardAlertAndShow(Alert.AlertType.INFORMATION, "About", null, "Coming soon...");
	}
}
