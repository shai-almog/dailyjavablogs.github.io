package com.github.dailyjavablogs.ghpagesbuilder.data;

public interface Identifiable<T> {

	T getId();

	void setId(T id);
}
