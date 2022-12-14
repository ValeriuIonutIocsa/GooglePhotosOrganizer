package com.personal.g_photos;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppStartGooglePhotosOrganizerTest {

	@Test
	void testWork() {

		final String inputFolderPathString;
		final String outputFolderPathString;
		final int input = Integer.parseInt("11");
		if (input == 1) {
			inputFolderPathString = "D:\\tmp\\GooglePhotosOrganizer\\SmallSampleOrig";
			outputFolderPathString = "D:\\tmp\\GooglePhotosOrganizer\\SmallSample";

		} else if (input == 11) {
			inputFolderPathString = "D:\\IVI_MISC\\Misc\\mnf\\steff\\GPhotos__new";
			outputFolderPathString = "D:\\IVI_MISC\\Misc\\mnf\\steff\\GPhotos__new_org";

		} else {
			throw new RuntimeException();
		}
		AppStartGooglePhotosOrganizer.work(inputFolderPathString, outputFolderPathString);
	}

	@Test
	void testParseInstantFromString() {

		final String dateString = "Nov 2, 2022, 4:54:41\u202fPM UTC";
		final Instant instant = AppStartGooglePhotosOrganizer.parseInstantFromString(dateString);
		Assertions.assertNotNull(instant);
	}
}
