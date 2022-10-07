package com.utils.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class PathUtilsTest {

	@TestFactory
	List<DynamicTest> testAppendFileNameSuffix() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (path with extension)",
					() -> testAppendFileNameSuffixCommon(
							"C:\\abc\\bcd\\file.txt", "_TMP",
							"C:\\abc\\bcd\\file_TMP.txt")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (path without extension)",
					() -> testAppendFileNameSuffixCommon(
							"C:\\abc\\bcd\\file", "_TMP",
							"C:\\abc\\bcd\\file_TMP")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (suffix with dot)",
					() -> testAppendFileNameSuffixCommon(
							"C:\\abc\\bcd\\file.txt", ".tmp",
							"C:\\abc\\bcd\\file.tmp.txt")));
		}
		return dynamicTestList;
	}

	private static void testAppendFileNameSuffixCommon(
			final String pathString,
			final String suffix,
			final String expectedResultPathString) {

		final String resultPathString = PathUtils.appendFileNameSuffix(pathString, suffix);
		Assertions.assertEquals(expectedResultPathString, resultPathString);
	}

	@TestFactory
	List<DynamicTest> testTryParseAbsolutePathString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (regular absolute path)",
					() -> testTryParseAbsolutePathStringCommon(
							"C:\\abc\\bcd\\file.txt", "C:\\abcd",
							"C:\\abc\\bcd\\file.txt")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (path with 2 dots)",
					() -> testTryParseAbsolutePathStringCommon(
							"C:\\abc\\..\\bcd\\file.txt", "D:\\tmp",
							"C:\\abc\\..\\bcd\\file.txt")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (relative path)",
					() -> testTryParseAbsolutePathStringCommon(
							".\\file.txt", "D:\\tmp",
							"D:\\tmp\\.\\file.txt")));
		}
		return dynamicTestList;
	}

	private static void testTryParseAbsolutePathStringCommon(
			final String pathString,
			final String rootPathString,
			final String expectedAbsolutePathString) {

		final String absolutePathString = PathUtils.tryParseAbsolutePathString(
				"test path", pathString, rootPathString);
		Assertions.assertEquals(expectedAbsolutePathString, absolutePathString);
	}

	@TestFactory
	List<DynamicTest> testComputeNormalizedPathString() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (regular normalized path)",
					() -> testComputeNormalizedPathStringCommon(
							"C:\\abc\\bcd\\file.txt",
							"C:\\abc\\bcd\\file.txt")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (path with 2 dots)",
					() -> testComputeNormalizedPathStringCommon(
							"C:\\abc\\..\\bcd\\file.txt",
							"C:\\bcd\\file.txt")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (relative path)",
					() -> testComputeNormalizedPathStringCommon(
							".\\file.txt",
							"C:\\IVI\\Conti\\Main\\Utils\\Services\\io-utils\\io-utils\\file.txt")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4 (relative path with 2 dots)",
					() -> testComputeNormalizedPathStringCommon(
							"..\\..\\test\\file.txt",
							"C:\\IVI\\Conti\\Main\\Utils\\Services\\test\\file.txt")));
		}
		return dynamicTestList;
	}

	private static void testComputeNormalizedPathStringCommon(
			final String pathString,
			final String expectedNormalizedPathString) {

		final String normalizedPathString = PathUtils.computeNormalizedPathString(pathString);
		Assertions.assertEquals(expectedNormalizedPathString, normalizedPathString);
	}

	@TestFactory
	List<DynamicTest> testComputeFileName() {

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		final List<Integer> testCaseList = Arrays.asList(0, 1, 2, 3, 4, 5);
		if (testCaseList.contains(1)) {
			dynamicTestList.add(DynamicTest.dynamicTest("1 (regular file path)",
					() -> testComputeFileNameCommon(
							"C:\\IVI\\Prog\\JavaGradle\\WeatherAnalyzer\\common_build.gradle",
							"common_build.gradle")));
		}
		if (testCaseList.contains(2)) {
			dynamicTestList.add(DynamicTest.dynamicTest("2 (regular folder path)",
					() -> testComputeFileNameCommon(
							"C:\\IVI\\Prog\\JavaGradle\\WeatherAnalyzer",
							"WeatherAnalyzer")));
		}
		if (testCaseList.contains(3)) {
			dynamicTestList.add(DynamicTest.dynamicTest("3 (folder path with dot)",
					() -> testComputeFileNameCommon(
							"C:\\IVI\\Prog\\JavaGradle\\WeatherAnalyzer\\.",
							".")));
		}
		if (testCaseList.contains(4)) {
			dynamicTestList.add(DynamicTest.dynamicTest("4 (folder path with two dots)",
					() -> testComputeFileNameCommon(
							"C:\\IVI\\Prog\\JavaGradle\\WeatherAnalyzer\\..",
							"..")));
		}
		if (testCaseList.contains(5)) {
			dynamicTestList.add(DynamicTest.dynamicTest("5 (null file path)",
					() -> testComputeFileNameCommon(
							null,
							null)));
		}
		return dynamicTestList;
	}

	private static void testComputeFileNameCommon(
			final String pathString,
			final String expectedFileName) {

		final String fileName = PathUtils.computeFileName(pathString);
		Assertions.assertEquals(expectedFileName, fileName);
	}
}
