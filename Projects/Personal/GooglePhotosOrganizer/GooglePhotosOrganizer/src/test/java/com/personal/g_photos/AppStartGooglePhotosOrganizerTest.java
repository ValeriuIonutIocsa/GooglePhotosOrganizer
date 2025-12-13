package com.personal.g_photos;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.io.folder_deleters.FactoryFolderDeleter;
import com.utils.test.TestInputUtils;

class AppStartGooglePhotosOrganizerTest {

	@Test
	void testWork() {

		final String[] args;
		final int input = TestInputUtils.parseTestInputNumber("102");
		if (input == 1) {
			args = new String[] {
					"D:\\IVI_PERS\\Tmp\\GooglePhotosOrganizer\\SmallSampleOrig",
					"D:\\IVI_PERS\\Tmp\\GooglePhotosOrganizer\\SmallSample",
					"-verbose"
			};

		} else if (input == 2) {
			args = new String[] {
					"D:\\IVI_PERS\\Tmp\\GooglePhotosOrganizer\\SmallSample2Orig",
					"D:\\IVI_PERS\\Tmp\\GooglePhotosOrganizer\\SmallSample2",
					"-verbose"
			};

		} else if (input == 11) {
			args = new String[] {
					"D:\\IVI_PERS\\Tmp\\GooglePhotosOrganizer\\IphoneSampleOrig",
					"D:\\IVI_PERS\\Tmp\\GooglePhotosOrganizer\\IphoneSample",
					"-verbose",
					"-keep_live_photo_videos"
			};

		} else if (input == 101) {
			args = new String[] {
					"D:\\IVI_MISC\\Misc\\mnf\\steff\\GPhotos__new",
					"D:\\IVI_MISC\\Misc\\mnf\\steff\\GPhotos__new_org"
			};

		} else if (input == 102) {
			args = new String[] {
					"D:\\IVI\\Misc\\mnf\\mama\\Takeout",
					"D:\\IVI\\Misc\\mnf\\mama\\Takeout_org"
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

		final String dateString = "13 Sept 2023, 13:31:27 UTC";
		final Instant instant = AppStartGooglePhotosOrganizer.parseInstantFromString(dateString);
		Assertions.assertNotNull(instant);
	}
}
