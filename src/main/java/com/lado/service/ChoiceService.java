package com.lado.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lado.model.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChoiceService {
	private static final Logger LOG = LoggerFactory.getLogger(ChoiceService.class);

	public static final List<String> DEFAULT_CHOICE_NAMES = Arrays
			.asList("Student Can", "Fortune Metropolis", "Tam's Yunnan Noodle", "Ar Duck", "Staff Can");

	private static ConcurrentMap<Long, Choice> db = Maps.newConcurrentMap();
	private static int rollCount = 0;

	public ChoiceService() throws IOException, URISyntaxException {
		// TODO: let the IoC do it
		init();
	}

	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		for (Choice choice : defaultChoices()) {
			db.put(choice.getId(), choice);
		}
	}

	public Choice randomPick() {
		List<Choice> choices = Lists.newArrayList(db.values());
		Random randomGenerator = new Random();
		Choice choice = choices.get(randomGenerator.nextInt(choices.size()));
		rollCount++;
		return choice;
	}

	public List<Choice> findAll() {
		return Lists.newArrayList(db.values());
	}

	public int getRollCount() {
		return rollCount;
	}

	private List<Choice> convert(File file) throws IOException {
		if (file == null || !file.exists() || !file.isFile())
			throw new IllegalArgumentException("invalid file input is detected: " + file);
		return Files.lines(file.toPath())
				.filter(l -> !l.isEmpty())
				.flatMap(l -> {
					String[] tokens = l.split(",");
					if (tokens.length != 2)
						throw new IllegalStateException("there should have exactly 2 token in line: " + l);
					return Stream
							.of(new Choice.Builder().id(Long.valueOf(tokens[0].trim())).name(tokens[1].trim()).build());
				})
				.collect(Collectors.toList());
	}

	private List<Choice> defaultChoices() {
		List<Choice> defaultChoices = Lists.newArrayList();
		for (int index = 0; index < DEFAULT_CHOICE_NAMES.size(); index++) {
			defaultChoices
					.add(new Choice.Builder().id((long) (index + 1)).name(DEFAULT_CHOICE_NAMES.get(index)).build());
		}
		return defaultChoices;
	}
}
