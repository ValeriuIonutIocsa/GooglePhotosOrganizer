package com.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.utils.annotations.ApiMethod;
import com.utils.log.Logger;
import com.utils.string.StrUtils;

public final class IoUtils {

	private IoUtils() {
	}

	/**
	 * @param pathString
	 *            the input path
	 * @return false if the path is null, blank or if the file does not exist, true otherwise
	 */
	@ApiMethod
	public static boolean fileExists(
			final String pathString) {
		return StringUtils.isNotBlank(pathString) && new File(pathString).exists();
	}

	/**
	 * @param pathString
	 *            the input path
	 * @return false if the path is null, blank or if the file is not a directory, true otherwise
	 */
	@ApiMethod
	public static boolean directoryExists(
			final String pathString) {
		return StringUtils.isNotBlank(pathString) && new File(pathString).isDirectory();
	}

	@ApiMethod
	public static long fileSize(
			final String filePathString) {

		long size = -1;
		try {
			final Path filePath = Paths.get(filePathString);
			size = Files.size(filePath);

		} catch (final Exception ignored) {
		}
		return size;
	}

	@ApiMethod
	public static long fileLastModifiedTime(
			final String filePathString) {

		long lastModifiedTime = -1;
		try {
			final Path filePath = Paths.get(filePathString);
			lastModifiedTime = Files.getLastModifiedTime(filePath).toMillis();

		} catch (final Exception ignored) {
		}
		return lastModifiedTime;
	}

	@ApiMethod
	public static byte[] fileMd5HashCode(
			final String filePathString) {

		byte[] md5HashCode = null;
		try {
			final byte[] fileBytes = ReaderUtils.fileToByteArray(filePathString);
			final MessageDigest messageDigestMd5 = MessageDigest.getInstance("MD5");
			md5HashCode = messageDigestMd5.digest(fileBytes);

		} catch (final Exception ignored) {
		}
		return md5HashCode;
	}

	@ApiMethod
	public static int computeLineCount(
			final String filePathString) {

		int lineCount = 0;
		try (BufferedReader bufferedReader = ReaderUtils.openBufferedReader(filePathString)) {

			while (bufferedReader.readLine() != null) {
				lineCount++;
			}

		} catch (final Exception exc) {
			Logger.printError("failed to compute line count of file:" +
					System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
		return lineCount;
	}

	@ApiMethod
	public static File createTemporaryFile(
			final InputStream inputStream) throws IOException {

		final File tempFile = File.createTempFile(StrUtils.createDateTimeString(), ".tmp");
		tempFile.deleteOnExit();
		try (OutputStream outputStream = Files.newOutputStream(tempFile.toPath())) {
			IOUtils.copy(inputStream, outputStream);
		}
		return tempFile;
	}

	@ApiMethod
	public static boolean openFileWithDefaultApp(
			final String filePathString) {

		boolean success = false;
		try {
			final ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("cmd", "/c", "start", filePathString);
			processBuilder.inheritIO();
			final Process process = processBuilder.start();
			final int exitCode = process.waitFor();
			success = exitCode == 0;

		} catch (final Exception exc) {
			Logger.printError("failed to open file with default app:" +
					System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
		return success;
	}
}
