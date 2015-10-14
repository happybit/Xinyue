package in.xinyue.xinyue.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.support.v4.Shadows;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import in.xinyue.xinyue.BuildConfig;
import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.ui.activity.MainActivity;
import in.xinyue.xinyue.ui.activity.PostDetailActivity;

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
        String category = fragment.getArguments().getString(ContentFragment.KEY_CATEGORY);
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

    @Test
    public void onCreateView_listviewShouldNotNull() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);
        ListView listView = (ListView) fragment.getActivity().findViewById(android.R.id.list);
        ShadowListView shadowListView = shadowOf(listView);
        shadowListView.populateItems();
        assertThat(listView).isNotNull();
    }

    @Test
    public void onListItemClick_shouldOpenPostDetailActivity() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);

        ListView listView = (ListView) fragment.getActivity().findViewById(android.R.id.list);
        ShadowListView shadowListView = shadowOf(listView);
        shadowListView.populateItems();
        shadowListView.performItemClick(0);

        Intent expectedIntent = new Intent(fragment.getActivity(), PostDetailActivity.class);
        Uri postUri = Uri.parse(PostContentProvider.CONTENT_URI + "/" + "-1");
        expectedIntent.putExtra(PostContentProvider.CONTENT_ITEM_TYPE, postUri);

        assertThat(shadowOf(fragment.getActivity()).getNextStartedActivity()).isEqualTo(expectedIntent);
    }

    @Test
    public void onOptionRefreshItemSelected_shouldReturnTrue() {
        fragment = ContentFragment.newInstance(Category.all);
        SupportFragmentTestUtil.startVisibleFragment(fragment, MainActivity.class, R.id.frame);

        MenuItem menuItem = new RoboMenuItem(R.id.refresh);
        assertThat(fragment.onOptionsItemSelected(menuItem)).isTrue();
    }

}
