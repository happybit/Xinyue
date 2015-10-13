package in.xinyue.xinyue.ui.fragment;


import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import in.xinyue.xinyue.BuildConfig;
import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.ui.activity.MainActivity;

import static org.robolectric.Shadows.shadowOf;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ContentFragmentTest {

    private ContentFragment fragment;

    @Test
    @Ignore
    public void newInstance_fragmentShouldNotNullWhenCategoryIsAll() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
        Assert.assertNotNull(fragment);
    }

    @Test
    public void newInstance_argumentShouldIsAllWhenCategoryIsAll() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
        String category = fragment.getArguments().getString(ContentFragment.EXTRA_CATEGORY);
        Assert.assertEquals("all", category);
    }

    @Test
    public void onCreateView_viewShouldNotNullWhenCategoryIsAll() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
        assertThat(fragment.getView()).isNotNull();
    }

    @Test
    public void onCreateView_menuShouldNotNullWhenCategoryIsAll() {
        fragment = ContentFragment.newInstance(Category.all);
        FragmentActivity activity = Robolectric.setupActivity(MainActivity.class);
        activity.getSupportFragmentManager()
                .beginTransaction().add(R.id.frame, fragment, null).commit();
        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertThat(menu).isNotNull();
    }

    @Test
    public void onCreateView_actionBarTitleShouldBeXinyueWhenCategoryIsAll() {
        fragment = ContentFragment.newInstance(Category.all);
        FragmentActivity activity = Robolectric.setupActivity(MainActivity.class);
        final ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
        assertThat(supportActionBar.getTitle()).isEqualTo("Xinyue");
    }

    @Test
    public void onCreateView_actionBarElevationShouldBeCorrectWhenCategoryIsAll() {
        fragment = ContentFragment.newInstance(Category.all);
        FragmentActivity activity = Robolectric.setupActivity(MainActivity.class);
        final ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
        assertThat(supportActionBar.getElevation()).isEqualTo(0);
    }

}
