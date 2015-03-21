package com.lado.controller;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.lado.model.Choice;
import com.lado.service.ChoiceService;
import com.lado.util.Fxmls;
import com.lado.view.ChoiceListCell;
import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FXMLController("/fxml/landing-page.fxml")
public class LandingPageController {
	private static final Logger LOG = LoggerFactory.getLogger(LandingPageController.class);

	private static final String ACTION_ADD_CHOICE = "addChoice";
	private static final String ACTION_DELETE_CHOICE = "deleteChoice";
	private static final String ACTION_IMPORT_CHOICES = "importChoices";
	private static final String ACTION_EXPORT_CHOICES = "exportChoices";
	private static final String ACTION_ABOUT = "about";
	private static final String ACTION_ROLLING = "rolling";

    @FXMLViewFlowContext
    private ViewFlowContext viewFlowContext;

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
	@ActionTrigger(ACTION_DELETE_CHOICE)
	private Button deleteChoice;

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
		choices.setEditable(true);
		choices.setCellFactory(listView -> new ChoiceListCell(choiceService));
		refreshChoices();
	}

	@ActionMethod(ACTION_ADD_CHOICE)
	public void addChoice() {
		String newChoiceName = newChoiceNameField.getText();
		if (Strings.isNullOrEmpty(newChoiceName) || newChoiceName.trim().isEmpty()) {
			Fxmls.createStandardAlertAndShow(Alert.AlertType.WARNING, "Add choice", null, "Please enter Choice Name");
			return;
		}
        // TODO: add handling for InvalidChoiceException
		choiceService.add(newChoiceName);
		refreshChoices();
		newChoiceNameField.setText("");
	}

	@ActionMethod(ACTION_DELETE_CHOICE)
	public void deleteChoice() {
		Choice selectedChoice = choices.getSelectionModel().getSelectedItem();
		if (selectedChoice == null) {
			Fxmls.createStandardAlertAndShow(Alert.AlertType.WARNING, "Delete choice", null, "Please select a choice");
			return;
		}
		choiceService.delete(selectedChoice.getId());
		refreshChoices();
	}

	@ActionMethod(ACTION_IMPORT_CHOICES)
	public void importChoices() {
        try {
            FileChooser fileChooser = generateFileChooserForImportAndExport("Import Choices");
            File file = fileChooser
                    .showOpenDialog(viewFlowContext.getCurrentViewContext().getRootNode().getScene().getWindow());
            if (file == null) return;
            List<Choice> importedChoices = choiceService.convert(file);
            List<String> choiceNames = importedChoices.stream().map(Choice::getName).collect(Collectors.toList());
            // create a dialog to show the imported choices and let user decide to ADD or REPLACE
            Alert importChoicesAlert =
                    Fxmls.createStandardAlert(Alert.AlertType.INFORMATION, "Import Choices",
                            "Import the following choices", null);
            // create TextArea to store the choices
            TextArea choicesArea =
                    new TextArea(choiceNames.stream().collect(Collectors.joining(System.lineSeparator())));
            choicesArea.setEditable(false);
            choicesArea.setWrapText(true);
            // configure the TextArea layout
            choicesArea.setMaxWidth(Double.MAX_VALUE);
            choicesArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(choicesArea, Priority.ALWAYS);
            GridPane.setHgrow(choicesArea, Priority.ALWAYS);
            // put the choices TextArea into the GridPane
            GridPane choicesPane = new GridPane();
            choicesPane.setMaxWidth(Double.MAX_VALUE);
            choicesPane.add(choicesArea, 0, 0);
            // Set choicesPane into the dialog pane
            importChoicesAlert.getDialogPane().setContent(choicesPane);

            // create ADD and REPLACE button
            ButtonType addButton = new ButtonType("Add");
            ButtonType replaceButton = new ButtonType("Replace");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            importChoicesAlert.getButtonTypes().setAll(addButton, replaceButton, cancelButton);

            // show the alert
            Optional<ButtonType> result = importChoicesAlert.showAndWait();
            // TODO: add handling for InvalidChoiceException
            if (result.get() == addButton) {
                choiceService.add(importedChoices);
            } else if (result.get() == replaceButton) {
                choiceService.replace(importedChoices);
            }
            refreshChoices();
        } catch (Exception e) {
            LOG.error("e: " + e, e);
            Fxmls.createStandardExceptionDialogAndShow("Import", null, "Fail to import choices", e);
        }
    }

	@ActionMethod(ACTION_EXPORT_CHOICES)
	public void exportChoices() {
        try {
            FileChooser fileChooser = generateFileChooserForImportAndExport("Export Choices");
            File file = fileChooser.showSaveDialog(
                    viewFlowContext.getCurrentViewContext().getRootNode().getScene().getWindow());
            if (file == null) return;
            Files.write(choiceService.findAll()
                            .stream()
                            .map(Choice::getName)
                            .collect(Collectors.joining(System.lineSeparator()))
                    , file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.error("e: " + e, e);
            Fxmls.createStandardExceptionDialogAndShow("Export Choice Fail", null, "Fail to export choices", e);
        }
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

    private FileChooser generateFileChooserForImportAndExport(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        //Set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        return fileChooser;
    }
}
