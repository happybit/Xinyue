package in.xinyue.xinyue.model;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.ui.fragment.ContentFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 7;
    private String tabTitles[];
    //private String tabTitles[] = new String[] {"All"};
    private Context context;

    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.tabTitles = context.getResources().getStringArray(R.array.categories);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("XinyueLog", "Position is " + position);
        Log.d("XinyueLog", "Category is " + Category.values()[position]);
        return ContentFragment.newInstance(Category.values()[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
