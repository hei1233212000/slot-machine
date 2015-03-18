package com.lado.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public final class Fxmls {
	private Fxmls() {}

	/**
	 * Create a standard dialog which would be shown after it is created automatically
	 */
	public static Optional<ButtonType> createStandardAlertAndShow(Alert.AlertType type, String title, String headerText, String contentText) {
		return createStandardAlert(type, title, headerText, contentText).showAndWait();
	}

	public static Optional<ButtonType> createStandardExceptionDialogAndShow(String title, String headerText,
			String contentText, Exception exception) {
		Alert alert = createStandardAlert(Alert.AlertType.ERROR, title, headerText, contentText);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(new Label("The exception stacktrace was:"), 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		return alert.showAndWait();
	}

	/**
	 * Create a standard dialog
	 */
	public static Alert createStandardAlert(Alert.AlertType type, String title, String headerText, String contentText) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		return alert;
	}
}
