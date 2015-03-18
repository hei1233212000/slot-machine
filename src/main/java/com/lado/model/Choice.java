package com.lado.model;

import com.google.common.base.MoreObjects;

public class Choice {
	private Long id;
	private String name;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("name", name)
				.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static class Builder {
		private Long id;
		private String name;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Choice build() {
			Choice choice = new Choice();
			choice.id = id;
			choice.name = name;
			return choice;
		}
	}
}
