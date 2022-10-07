package com.utils.string.regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RegexUtilsTest {

	@Test
	void testMatchesPattern() {

		final String string;
		final String patternString;
		final boolean caseSensitive;
		final boolean expectedMatches;
		final int input = Integer.parseInt("1");
		if (input == 1) {

			string = "SOMEthing";
			patternString = "SO(?i)me(?-i)thing";
			caseSensitive = true;
			expectedMatches = true;

		} else if (input == 2) {

			string = "SOMEthing";
			patternString = "something";
			caseSensitive = false;
			expectedMatches = true;

		} else if (input == 3) {

			string = "SOMEthing";
			patternString = "something";
			caseSensitive = true;
			expectedMatches = false;

		} else {
			throw new RuntimeException();
		}

		final boolean matches = RegexUtils.matchesPattern(string, patternString, caseSensitive);
		Assertions.assertEquals(expectedMatches, matches);
	}
}
