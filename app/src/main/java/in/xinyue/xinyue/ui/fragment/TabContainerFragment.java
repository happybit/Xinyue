package in.xinyue.xinyue.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.ui.adapter.FragmentAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabContainerFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int checkPosition;
    private FragmentAdapter mAdapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabContainerFragment.
     */
    public static TabContainerFragment newInstance() {
        TabContainerFragment fragment = new TabContainerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TabContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab, container, false);

        setupTablayout(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupTablayout(View view) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mAdapter = new FragmentAdapter(getChildFragmentManager(), getActivity());
        viewPager.setAdapter(mAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        //tabLayout.setupWithViewPager(viewPager);

        /*tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });*/

        // Work-around for TabLayout missing issue
        // https://code.google.com/p/android/issues/detail?id=180462
        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayout.setupWithViewPager(viewPager);
        } else {
            tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    tabLayout.setupWithViewPager(viewPager);

                    tabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }

    }

}
