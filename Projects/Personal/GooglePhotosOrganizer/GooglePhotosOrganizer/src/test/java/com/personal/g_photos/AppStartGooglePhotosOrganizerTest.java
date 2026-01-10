package com.personal.g_photos;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.utils.io.folder_deleters.FactoryFolderDeleter;
import com.utils.test.DynamicTestOption;
import com.utils.test.DynamicTestOptions;
import com.utils.test.DynamicTestSuite;
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

	@TestFactory
	List<DynamicTest> testParseInstantFromString() {

		final DynamicTestOptions<String> instantDynamicTestOptions =
				new DynamicTestOptions<>("instant string", 1);

		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(1, "format 1",
				"Dec 21, 2025, 7:23:45 PM UTC"));

		instantDynamicTestOptions.getDynamicTestOptionList().add(new DynamicTestOption<>(11, "format 2",
				"13 Sept 2023, 13:31:27 UTC"));

		final DynamicTestSuite dynamicTestSuite = new DynamicTestSuite(DynamicTestSuite.Mode.ALL,
				() -> testParseInstantFromStringCommon(instantDynamicTestOptions), instantDynamicTestOptions);

		return dynamicTestSuite.createDynamicTestList();
	}

	private static void testParseInstantFromStringCommon(
			final DynamicTestOptions<String> instantStringOptions) {

		final String instantString = instantStringOptions.computeValue();
		final Instant instant = AppStartGooglePhotosOrganizer.parseInstantFromString(instantString);
		Assertions.assertNotNull(instant);
	}
}
