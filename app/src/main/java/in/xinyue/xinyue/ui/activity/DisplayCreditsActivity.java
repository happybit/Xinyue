package in.xinyue.xinyue.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.view.swipeback.SwipeBackActivity;


public class DisplayCreditsActivity extends SwipeBackActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_credits);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(12.0f);
        }

        setTitle(getResources().getString(R.string.about_credits));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
