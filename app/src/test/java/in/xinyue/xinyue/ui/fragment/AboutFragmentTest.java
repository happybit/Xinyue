package in.xinyue.xinyue.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import org.junit.Before;
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


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AboutFragmentTest {

    private AboutFragment fragment;

    @Before
    public void setUp() {
        fragment = AboutFragment.newInstance();
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
    }

    private void checkRelationBetweenViewAndNextActivity(int viewID, Class<?> cls) {
        View view = fragment.getView();

        if (view == null) {
            return;
        }
        
        fragment.getView().findViewById(viewID).performClick();
        Intent expectedIntent = new Intent(fragment.getActivity(), cls);
        assertThat(shadowOf(fragment.getActivity()).getNextStartedActivity()).isEqualTo(expectedIntent);
    }

    @Test
    public void clickOrigin_shouldStartDisplayOriginActivity() {
        checkRelationBetweenViewAndNextActivity(R.id.origin, DisplayOriginActivity.class);
    }

    @Test
    public void clickCredits_shouldStartDisplayCreditsActivity() {
        checkRelationBetweenViewAndNextActivity(R.id.credits, DisplayCreditsActivity.class);
    }


}
