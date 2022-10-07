package com.personal.g_photos;

import org.junit.jupiter.api.Test;

class AppStartGooglePhotosOrganizerTest {

	@Test
	void testWork() {

		final String inputFolderPathString = "D:\\tmp\\GooglePhotosOrganizer\\SmallSampleOrig";
		final String outputFolderPathString = "D:\\tmp\\GooglePhotosOrganizer\\SmallSample";

		AppStartGooglePhotosOrganizer.work(inputFolderPathString, outputFolderPathString);
	}
}
