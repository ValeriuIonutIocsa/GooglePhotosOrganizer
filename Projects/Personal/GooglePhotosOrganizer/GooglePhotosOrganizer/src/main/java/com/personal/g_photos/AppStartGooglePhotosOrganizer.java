package com.personal.g_photos;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

import org.json.JSONObject;

import com.utils.io.IoUtils;
import com.utils.io.ListFileUtils;
import com.utils.io.PathUtils;
import com.utils.io.ReaderUtils;
import com.utils.io.file_copiers.FactoryFileCopier;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.log.Logger;

final class AppStartGooglePhotosOrganizer {

	private AppStartGooglePhotosOrganizer() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();
		Logger.setDebugMode(true);

		if (args.length < 2) {

			Logger.printError("insufficient arguments");
			System.exit(-1);
		}

		final String inputFolderPathString = args[0];
		final String outputFolderPathString = args[1];
		work(inputFolderPathString, outputFolderPathString);

		Logger.printNewLine();
		Logger.printFinishMessage(start);
	}

	static void work(
			final String inputFolderPathString,
			final String outputFolderPathString) {

		Logger.printNewLine();
		Logger.printProgress("GooglePhotosOrganizer starting");

		if (!IoUtils.fileExists(inputFolderPathString)) {

			Logger.printError("input folder does not exist");
			System.exit(-2);
		}

		final boolean success =
				FactoryFolderCreator.getInstance().createDirectories(outputFolderPathString, true);
		if (!success) {
			System.exit(-3);
		}

		final List<String> filePathStringList =
				ListFileUtils.listFilesRecursively(inputFolderPathString);
		for (final String filePathString : filePathStringList) {

			if (!IoUtils.directoryExists(filePathString)) {

				final String jsonFilePathString = filePathString + ".json";
				if (IoUtils.fileExists(jsonFilePathString)) {

					processFile(filePathString, jsonFilePathString, outputFolderPathString);
				}
			}
		}
	}

	private static void processFile(
			final String filePathString,
			final String jsonFilePathString,
			final String outputFolderPathString) {

		try {
			Logger.printNewLine();
			Logger.printProgress("processing file:");
			Logger.printLine(filePathString);

			final String fileName = PathUtils.computeFileName(filePathString);
			final String outputFilePathString =
					Paths.get(outputFolderPathString, fileName).toString();

			final boolean success = copyFile(filePathString, outputFilePathString);
			if (success) {

				final Instant photoTakenTimeInstant = parsePhotoTakenTimeInstant(jsonFilePathString);
				if (photoTakenTimeInstant != null) {

					final FileTime fileTime = FileTime.from(photoTakenTimeInstant);
					final Path outputFilePath = Paths.get(outputFilePathString);
					Files.setLastModifiedTime(outputFilePath, fileTime);
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to process file:" + System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
	}

	private static boolean copyFile(
			final String filePathString,
			final String outputFilePathString) {

		boolean success;
		if (filePathString.endsWith(".mp4")) {

			success = false;
			try {
				Logger.printProgress("copying file:");
				Logger.printLine(filePathString);
				Logger.printLine("to:");
				Logger.printLine(outputFilePathString);

				success = FactoryFileDeleter.getInstance().deleteFile(outputFilePathString, true);
				if (success) {

					final ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command("ffmpeg", "-i", filePathString,
							"-vcodec", "copy", "-acodec", "copy", outputFilePathString);
					processBuilder.directory(new File(outputFilePathString).getParentFile());
					processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
					processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
					final Process process = processBuilder.start();
					final int exitCode = process.waitFor();
					success = exitCode == 0;
				}

			} catch (final Exception exc) {
				Logger.printError("failed to copy file " +
						System.lineSeparator() + filePathString +
						System.lineSeparator() + "to:" +
						System.lineSeparator() + outputFilePathString);
				Logger.printException(exc);
			}

		} else {
			success = FactoryFileCopier.getInstance()
					.copyFile(filePathString, outputFilePathString, true, true);
		}
		return success;
	}

	private static Instant parsePhotoTakenTimeInstant(
			final String jsonFilePathString) {

		Instant photoTakenTimeInstant = null;
		try {
			Logger.printProgress("parsing photo taken time from JSON file:");
			Logger.printLine(jsonFilePathString);

			final String jsonFileContent = ReaderUtils.fileToString(jsonFilePathString);
			final JSONObject jsonObject = new JSONObject(jsonFileContent);
			final JSONObject photoTakenTimeJsonObject = jsonObject.getJSONObject("photoTakenTime");
			final String formattedPhotoTakenTime = photoTakenTimeJsonObject.getString("formatted");

			photoTakenTimeInstant = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss a z").parse(
					formattedPhotoTakenTime).toInstant();

		} catch (final Exception exc) {
			Logger.printProgress("failed to parse photo taken time from JSON file:" +
					System.lineSeparator() + jsonFilePathString);
			Logger.printException(exc);
		}
		return photoTakenTimeInstant;
	}
}
