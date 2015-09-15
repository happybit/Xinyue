package in.xinyue.xinyue.ui.fragment;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import in.xinyue.xinyue.BuildConfig;
import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.ui.activity.MainActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ContentFragmentTest {

    private ContentFragment fragment;

    @Test
    public void checkFragmentForCategoryAll_() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
    }
}
