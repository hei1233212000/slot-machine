package com.lado.view;

import javafx.scene.control.ListCell;

import com.lado.model.Choice;
import com.lado.service.ChoiceService;

public class ChoiceListCell extends ListCell<Choice> {
	private ChoiceService choiceService;

	public ChoiceListCell(ChoiceService choiceService) {
		this.choiceService = choiceService;
	}

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
}
