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

public final class Entropy {

	private Entropy() {
		throw new AssertionError("This is a utility class.");
	}

	public static String getRandomString() {
		return UUID.randomUUID().toString();
	}

	public static int getRandomNumberBetween(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static long getRandomNumberOfFixedLength(int length) {
		String zeroes = String.join("", Collections.nCopies(length - 1, "0"));
		return Long.parseLong("1" + zeroes) + ThreadLocalRandom.current().nextLong(Long.parseLong("9" + zeroes));
	}

	public static <T> T getRandomListItem(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
	}

	public static <E extends Enum<E>> E getRandomEnumValue(Class<E> enumType) {
		E[] values = enumType.getEnumConstants();
		return values[ThreadLocalRandom.current().nextInt(0, values.length)];
	}

	public static LocalDate getRandomLocalDateBetweenYears(int minYear, int maxYear) {
		LocalDate minDate = LocalDate.of(minYear, Month.JANUARY, 1);
		LocalDate maxDate = LocalDate.of(maxYear, Month.DECEMBER, Month.DECEMBER.maxLength());
		return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(minDate.toEpochDay(), maxDate.toEpochDay()));
	}

	public static LocalDate getRandomDateOfBirthBetweenAges(int minAge, int maxAge) {
		LocalDate now = LocalDate.now();
		int year = getRandomNumberBetween(now.minusYears(maxAge - 1).getYear(), now.minusYears(minAge + 1).getYear());
		Month month = getRandomEnumValue(Month.class);
		int day = getRandomNumberBetween(1, YearMonth.of(year, month).lengthOfMonth());
		return LocalDate.of(year, month, day);
	}

}