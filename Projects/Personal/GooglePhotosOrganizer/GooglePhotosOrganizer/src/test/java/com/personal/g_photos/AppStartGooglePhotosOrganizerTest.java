package com.personal.g_photos;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.io.folder_deleters.FactoryFolderDeleter;

class AppStartGooglePhotosOrganizerTest {

	@Test
	void testWork() {

		final String[] args;
		final int input = Integer.parseInt("11");
		if (input == 1) {
			args = new String[] {
					"D:\\tmp\\GooglePhotosOrganizer\\SmallSampleOrig",
					"D:\\tmp\\GooglePhotosOrganizer\\SmallSample",
					"-verbose"
			};

		} else if (input == 2) {
			args = new String[] {
					"D:\\tmp\\GooglePhotosOrganizer\\SmallSample2Orig",
					"D:\\tmp\\GooglePhotosOrganizer\\SmallSample2",
					"-verbose"
			};

		} else if (input == 11) {
			args = new String[] {
					"D:\\tmp\\GooglePhotosOrganizer\\IphoneSampleOrig",
					"D:\\tmp\\GooglePhotosOrganizer\\IphoneSample",
					"-verbose",
					// "-keep_live_photo_videos"
			};

		} else if (input == 101) {
			args = new String[] {
					"D:\\IVI_MISC\\Misc\\mnf\\steff\\GPhotos__new",
					"D:\\IVI_MISC\\Misc\\mnf\\steff\\GPhotos__new_org"
			};

		} else {
			throw new RuntimeException();
		}

		final boolean success = FactoryFolderDeleter.getInstance()
				.deleteFolder(args[1], true, true);
		Assertions.assertTrue(success);

		AppStartGooglePhotosOrganizer.work(args);
	}

	@Test
	void testParseInstantFromString() {

		final String dateString = "Nov 2, 2022, 4:54:41\u202fPM UTC";
		final Instant instant = AppStartGooglePhotosOrganizer.parseInstantFromString(dateString);
		Assertions.assertNotNull(instant);
	}
}
