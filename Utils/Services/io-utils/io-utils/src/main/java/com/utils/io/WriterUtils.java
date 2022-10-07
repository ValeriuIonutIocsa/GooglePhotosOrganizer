package com.utils.io;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.io.ro_flag_clearers.FactoryReadOnlyFlagClearer;
import com.utils.log.Logger;

public final class WriterUtils {

	private WriterUtils() {
	}

	public static boolean byteArrayToFile(
			final byte[] byteArray,
			final String filePathString) {

		boolean success = false;
		try {
			final Path filePath = Paths.get(filePathString);
			Files.write(filePath, byteArray);
			success = true;

		} catch (final Exception exc) {
			Logger.printError("failed to write byte array to file:" +
					System.lineSeparator() + filePathString);
			Logger.printException(exc);
		}
		return success;
	}

	public static void stringToFile(
			final String string,
			final Charset charset,
			final String filePathString) throws Exception {

		FactoryFolderCreator.getInstance().createParentDirectories(filePathString, true);
		FactoryReadOnlyFlagClearer.getInstance().clearReadOnlyFlagFile(filePathString, true);
		try (PrintStream printStream = StreamUtils.openPrintStream(filePathString, false, charset)) {
			printStream.print(string);
		}
	}
}
