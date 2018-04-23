/*
 * Copyright 2018 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.utils.arquillian;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * For IT, it's the best practice to introduce entropy so that any potential corner cases could be spotted during IT.
 *
 * @author Bauke Scholtz
 */
public final class Entropy {

	private static final List<String> LETTERS_AND_DIGITS = IntStream
		.range(Character.MIN_VALUE, Byte.MAX_VALUE)
		.filter(Character::isLetterOrDigit)
		.mapToObj(i -> String.valueOf((char) i))
		.collect(Collectors.toList());

	private Entropy() {
		throw new AssertionError("This is a utility class.");
	}

	/**
	 * Returns random string. Current implementation just returns the {@link UUID}.
	 * @return Random string.
	 */
	public static String getRandomString() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Returns random string of fixed length.
	 * @param length The desired amount of characters.
	 * @return Random string of fixed length.
	 */
	public static String getRandomStringOfFixedLength(int length) {
		return IntStream.range(0, length).mapToObj(i -> getRandomListItem(LETTERS_AND_DIGITS)).collect(Collectors.joining());
	}

	/**
	 * Returns random number between given values, inclusive.
	 * @param min The minimum value.
	 * @param max The maximum value, inclusive.
	 * @return Random number between given values, inclusive.
	 */
	public static int getRandomNumberBetween(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	/**
	 * Returns random number of fixed length.
	 * @param length The desired amount of digits.
	 * @return Random number of fixed length.
	 */
	public static long getRandomNumberOfFixedLength(int length) {
		String zeroes = String.join("", Collections.nCopies(length - 1, "0"));
		return Long.parseLong("1" + zeroes) + ThreadLocalRandom.current().nextLong(Long.parseLong("9" + zeroes));
	}

	/**
	 * Returns random item from given list.
	 * @param list List to get random item from.
	 * @return Random item from given list.
	 * @param <E> Generic list element type.
	 */
	public static <E> E getRandomListItem(List<E> list) {
		return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
	}

	/**
	 * Returns random value from given enum.
	 * @param enumType Enum type to get random value from.
	 * @return Random value from given enum.
	 * @param <E> Generic enum type.
	 */
	public static <E extends Enum<E>> E getRandomEnumValue(Class<E> enumType) {
		E[] values = enumType.getEnumConstants();
		return values[ThreadLocalRandom.current().nextInt(0, values.length)];
	}

	/**
	 * Returns random local date between given years, inclusive.
	 * @param minYear The minimum year.
	 * @param maxYear The maximum year, inclusive.
	 * @return Random local date between given years, inclusive.
	 */
	public static LocalDate getRandomLocalDateBetweenYears(int minYear, int maxYear) {
		LocalDate minDate = LocalDate.of(minYear, Month.JANUARY, 1);
		LocalDate maxDate = LocalDate.of(maxYear, Month.DECEMBER, Month.DECEMBER.maxLength());
		return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(minDate.toEpochDay(), maxDate.toEpochDay()));
	}

	/**
	 * Returns random local date representing date of birth between given ages, inclusive.
	 * @param minAge The minimum age.
	 * @param maxAge The maximum age, inclusive.
	 * @return Random local date representing date of birth between given ages, inclusive.
	 */
	public static LocalDate getRandomDateOfBirthBetweenAges(int minAge, int maxAge) {
		LocalDate now = LocalDate.now();
		int year = getRandomNumberBetween(now.minusYears(maxAge - 1).getYear(), now.minusYears(minAge + 1).getYear());
		Month month = getRandomEnumValue(Month.class);
		int day = getRandomNumberBetween(1, YearMonth.of(year, month).lengthOfMonth());
		return LocalDate.of(year, month, day);
	}

}