package in.xinyue.xinyue.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.ui.activity.DisplayCreditsActivity;
import in.xinyue.xinyue.ui.activity.DisplayOriginActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {
    public static final String TAG = "about";
    public static final String CONTACT_EMAIL = "zehuipan@163.com";
    public static final String WEIBO_URL = "http://weibo.cn/qr/userinfo?uid=2863983581";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.getMenu().clear();
        }

        // Set title bar
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getActivity().getString(R.string.drawerlayout_menu_about));
            supportActionBar.setElevation(12.0f);
        }

        TextView originTextView = (TextView) view.findViewById(R.id.origin);
        TextView mailTextView = (TextView) view.findViewById(R.id.mail);
        TextView weiboTextView = (TextView) view.findViewById(R.id.weibo);
        TextView creditsTextView = (TextView) view.findViewById(R.id.credits);

        setClickListenerForViews(originTextView, mailTextView, weiboTextView, creditsTextView);

        return view;
    }

    private void setClickListenerForViews(TextView originTextView, TextView mailTextView,
                                          TextView weiboTextView, TextView creditsTextView) {
        setClickListenerToStartActivity(originTextView, DisplayOriginActivity.class);
        setClickListenerToStartActivity(creditsTextView, DisplayCreditsActivity.class);
        setClickListenerToCreateChooser(mailTextView, Intent.ACTION_SENDTO,
                R.string.send_mail_indication, Uri.fromParts("mailto", CONTACT_EMAIL, null));
        setClickListenerToCreateChooser(weiboTextView, Intent.ACTION_VIEW,
                R.string.view_weibo_indication, Uri.parse(WEIBO_URL));
    }

    private void setClickListenerToCreateChooser(TextView textView, final String intentAction,
                                                 final int indicationStringID, final Uri intentUri) {
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(intentAction, intentUri);
                createChooserForIntent(intent, indicationStringID);
            }
        });
    }

    private void setClickListenerToStartActivity(TextView textView, final Class<?> cls) {
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNewActivity(cls);
            }
        });
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void createChooserForIntent(Intent intent, int indicationStringID) {
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent,
                    getActivity().getResources().getString(indicationStringID)));
        }
    }


}
