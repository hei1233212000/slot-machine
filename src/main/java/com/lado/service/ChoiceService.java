package com.lado.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.lado.exception.InvalidChoiceException;
import com.lado.model.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ChoiceService {
	private static final Logger LOG = LoggerFactory.getLogger(ChoiceService.class);

	public static final List<String> DEFAULT_CHOICE_NAMES = Arrays
			.asList("Student Can", "Fortune Metropolis", "Tam's Yunnan Noodle", "Ar Duck", "Staff Can");

	private static long choiceId = 1;
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

	/**
	 * The result would be ordered by name
	 */
	public List<Choice> findAll() {
		return Lists.newArrayList(db.values())
				.stream()
				.sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
				.collect(Collectors.toList());
	}

	public int getRollCount() {
		return rollCount;
	}

    public Choice add(String choiceName) {
        if (Strings.isNullOrEmpty(choiceName)) throw new IllegalArgumentException("choiceName cannot be null or empty");
        List<Choice> choices = add(Arrays.asList(new Choice.Builder().name(choiceName).build()));
        return choices.isEmpty() ? null : choices.get(0);
    }

    /**
     * It would first remove ALL the choices in the DB and then insert new records based on <code>choices</code>
     */
    public List<Choice> replace(List<Choice> choices) {
        if (choices == null) throw new IllegalArgumentException("choices cannot be null");
        db.clear();
        return add(choices);
    }

    public List<Choice> add(List<Choice> choices) {
        if (choices == null) throw new IllegalArgumentException("choices cannot be null");
        boolean invalidChoiceIsDetected = choices.stream().allMatch(c -> Strings.isNullOrEmpty(c.getName()));
        if (invalidChoiceIsDetected) throw new InvalidChoiceException("Choice name could NOT be empty");
        // persist
        choices.stream().forEach(c -> {
            c.setId(findNextChoiceId());
            db.put(c.getId(), c);
        });
        return choices;
    }

	public Choice delete(long id) {
		LOG.info("Choice with id[{}] is going to be deleted", id);
		Choice removedChoice = db.remove(id);
		LOG.info("{} is removed", removedChoice);
		return removedChoice;
	}

	public List<Choice> convert(File file) throws IOException {
		if (file == null || !file.exists() || !file.isFile())
			throw new IllegalArgumentException("invalid file input is detected: " + file);
		return Files.readLines(file, StandardCharsets.UTF_8)
                .stream()
				.filter(l -> !l.isEmpty())
				.map(l -> new Choice.Builder().name(l.trim()).build())
				.collect(Collectors.toList());
	}

	private List<Choice> defaultChoices() {
		List<Choice> defaultChoices = Lists.newArrayList();
        defaultChoices.addAll(
                DEFAULT_CHOICE_NAMES.stream()
                .map(choiceName -> new Choice.Builder().id(findNextChoiceId()).name(choiceName).build())
                .collect(Collectors.toList()));
		return defaultChoices;
	}

	private long findNextChoiceId() {
		return choiceId++;
	}
}
