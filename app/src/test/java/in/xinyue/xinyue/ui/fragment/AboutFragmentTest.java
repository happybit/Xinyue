package in.xinyue.xinyue.ui.fragment;

import android.content.Intent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import in.xinyue.xinyue.BuildConfig;
import in.xinyue.xinyue.R;
import in.xinyue.xinyue.ui.DisplayCreditsActivity;
import in.xinyue.xinyue.ui.DisplayOriginActivity;
import in.xinyue.xinyue.ui.MainActivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by pzheng on 9/6/2015.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AboutFragmentTest {

    AboutFragment fragment;

    @Before
    public void setUp() {
        fragment = AboutFragment.newInstance();
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
    }

    @Test
    public void clickOrigin_shouldStartDisplayOriginActivity() {
        fragment.getView().findViewById(R.id.origin).performClick();

        Intent expectedIntent = new Intent(fragment.getActivity(), DisplayOriginActivity.class);
        assertThat(shadowOf(fragment.getActivity()).getNextStartedActivity()).isEqualTo(expectedIntent);
    }

    @Test
    public void clickCredits_shouldStartDisplayCreditsActivity() {
        fragment.getView().findViewById(R.id.credits).performClick();

        Intent expectedIntent = new Intent(fragment.getActivity(), DisplayCreditsActivity.class);
        assertThat(shadowOf(fragment.getActivity()).getNextStartedActivity()).isEqualTo(expectedIntent);
    }
}
