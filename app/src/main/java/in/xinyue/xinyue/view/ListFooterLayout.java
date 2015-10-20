package in.xinyue.xinyue.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.xinyue.xinyue.R;

/**
 * Class for list footer layout which includes an indication text view and a progress bar.
 */
public class ListFooterLayout {

    private Context context;
    private View listFooterView;
    private TextView footerTextView;
    private ProgressBar loadMoreProgressBar;
    private boolean isLoadingMore;

    public ListFooterLayout(Context context) {
        this.context = context;
        resetLoadingStatus();
    }

    private void resetLoadingStatus() {
        isLoadingMore = false;
    }

    private void enableLoadingStatus() {
        isLoadingMore = true;
    }

    public void inflateListFooter(int resource) {
        listFooterView = LayoutInflater.from(context).inflate(resource, null);
    }

    public void initFooterTextView(int resource, View.OnClickListener listener) {
        footerTextView = (TextView) listFooterView.findViewById(resource);
        footerTextView.setOnClickListener(listener);
    }

    public void initLoadMoreProgressBar(int resource) {
        loadMoreProgressBar = (ProgressBar) listFooterView.findViewById(resource);
        loadMoreProgressBar.getIndeterminateDrawable()
                .setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY); // set color scheme
    }

    public View getListFooterView() {
        return listFooterView;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    /**
     * When click on "load more" text view or pull to load more,
     * dismiss "load more" text view and display "load more" progress bar;
     */
    public void displayLoadMoreProgressBar() {
        enableLoadingStatus();
        loadMoreProgressBar.setVisibility(View.VISIBLE);
        footerTextView.setVisibility(View.GONE);
    }

    /**
     * If new posts are retrieved successfully, dismiss "load more" progress bar
     * and display "load more" text view.;
     */
    public void displayLoadMoreTextView() {
        footerTextView.setText(context.getResources().
                getString(R.string.footer_has_more_indication));
        dismissProgressBarAndDisplayTextView();
    }

    /**
     * If no more posts, dismiss "load more" progress bar and display "load more" text view,
     * but replace "load more" string to "no more".
     */
    public void displayNoMoreTextView() {
        footerTextView.setText(context.getResources().
                getString(R.string.footer_no_more_indication));
        footerTextView.setOnClickListener(null);
        dismissProgressBarAndDisplayTextView();
    }

    /**
     * If error encountered during loading posts, dismiss "load more" progress bar
     * and display "load more" text view.
     */
    public void displayLoadMoreTextViewWhenErrorEncountered() {
        footerTextView.setText(context.getResources().
                getString(R.string.footer_fail_indication));
        dismissProgressBarAndDisplayTextView();
    }

    private void dismissProgressBarAndDisplayTextView() {
        footerTextView.setVisibility(View.VISIBLE);
        loadMoreProgressBar.setVisibility(View.GONE);
        resetLoadingStatus();
    }
}
