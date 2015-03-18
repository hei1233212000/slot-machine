package com.lado.controller;

import com.google.common.base.Strings;
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

	private static final String ACTION_ADD_CHOICE = "addChoice";
	private static final String ACTION_DELETE_CHOICE = "deleteChoice";
	private static final String ACTION_IMPORT_CHOICES = "importChoices";
	private static final String ACTION_EXPORT_CHOICES = "exportChoices";
	private static final String ACTION_ABOUT = "about";
	private static final String ACTION_ROLLING = "rolling";

	@FXML
	private ListView<Choice> choices;

	@FXML
	@ActionTrigger(ACTION_ABOUT)
	private MenuItem about;

	@FXML
	@ActionTrigger(ACTION_ROLLING)
	private Button rolling;

	@FXML
	@ActionTrigger(ACTION_ADD_CHOICE)
	private TextField newChoiceNameField;

	@FXML
	@ActionTrigger(ACTION_ADD_CHOICE)
	private Button addChoice;

	@FXML
	@ActionTrigger(ACTION_IMPORT_CHOICES)
	private MenuItem importChoices;

	@FXML
	@ActionTrigger(ACTION_EXPORT_CHOICES)
	private MenuItem exportChoices;

	@FXML
	private Label result;

	@FXML
	private Label rollCount;

	private ChoiceService choiceService;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		// TODO: should use IoC framework
		choiceService = new ChoiceService();
		choices.setCellFactory(v -> new ListCell<Choice>() {
			@Override
			protected void updateItem(Choice choice, boolean empty) {
				super.updateItem(choice, empty);
				if (empty || choice == null) {
					setText(null);
					setGraphic(null);
				} else {
					setText(choice.getName());
				}
			}
		});
		refreshChoices();
	}

	@ActionMethod(ACTION_ADD_CHOICE)
	public void addChoice() {
		String newChoiceName = newChoiceNameField.getText();
		if (Strings.isNullOrEmpty(newChoiceName) || newChoiceName.trim().isEmpty()) {
			Fxmls.createStandardAlertAndShow(Alert.AlertType.WARNING, "Add choice", null, "Please enter Choice Name");
			return;
		}
		choiceService.add(newChoiceName);
		refreshChoices();
		newChoiceNameField.setText("");
	}

	@ActionMethod(ACTION_IMPORT_CHOICES)
	public void importChoices() {
		// TODO: implement this
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

	@ActionMethod(ACTION_EXPORT_CHOICES)
	public void exportChoices() {
		// get the current list of pojo
		// convert the list into file
		// export it
		// TODO: implement this
		Fxmls.createStandardAlertAndShow(Alert.AlertType.INFORMATION, "Export", null, "Coming soon...");
	}

	@ActionMethod(ACTION_ROLLING)
	public void rolling() {
		Choice choice = choiceService.randomPick();
		result.setText(choice.getName());
		rollCount.setText(Integer.toString(choiceService.getRollCount()));
	}

	@ActionMethod(ACTION_ABOUT)
	public void about() {
		String about = "If you have any enquiry, advice, bug report, etc, please report it to https://github.com/hei1233212000/slot-machine\nHave a nice day :)";
		Fxmls.createStandardAlertAndShow(Alert.AlertType.INFORMATION, "About", null, about);
	}

	private void refreshChoices() {
		choices.setItems(FXCollections.observableArrayList(choiceService.findAll()));
	}
}
