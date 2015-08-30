package in.xinyue.xinyue.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

import in.xinyue.xinyue.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    public static final String TAG = "settings";

    private OnFragmentInteractionListener mListener;

    private ListPreference languageSetting;
    private static final String LANGUAGE_SETTING_KEY = "pref_key_language";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        initView();
        setListener();
    }

    private void setListener() {
        languageSetting.setOnPreferenceChangeListener(this);
    }

    private void initView() {
        languageSetting = (ListPreference) findPreference(LANGUAGE_SETTING_KEY);

        if (languageSetting.getValue() == null) {
            languageSetting.setValue(Locale.getDefault().getLanguage());
        }
        //languageSetting.getSharedPreferences().edit().putBoolean(LANGUAGE_SETTING_KEY, false).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();

        // Set title bar
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getActivity().getString(R.string.action_settings));
            supportActionBar.setElevation(12.0f);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case LANGUAGE_SETTING_KEY:
                if (newValue.toString().equals("en")) {
                    changeLanguage(Locale.ENGLISH);
                } else {
                    changeLanguage(Locale.CHINESE);
                }
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
                break;
        }
        return true;
    }

    private void changeLanguage(Locale locale) {
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
        getActivity().sendBroadcast(new Intent("language"));
    }

}
