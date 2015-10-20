package in.xinyue.xinyue.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.XinyueApi;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.contentprovider.PostsCursorLoader;
import in.xinyue.xinyue.contentprovider.database.PostReaderContract;
import in.xinyue.xinyue.request.AsyncQueryRequest;
import in.xinyue.xinyue.request.UILImageGetter;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.view.ListFooterLayout;
import in.xinyue.xinyue.view.RefreshLayout;
import in.xinyue.xinyue.ui.activity.PostDetailActivity;

public class ContentFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_CATEGORY = "category";
    public static final String KEY_PAGE = "page";

    private static final int FIRST_PAGE_INDEX = 1;
    private static final int POSTS_PER_LOAD = 10;

    private int nextPageIndex = FIRST_PAGE_INDEX + 1;
    private Category category;
    private ListView listView;
    private ListFooterLayout listFooter;
    private RefreshLayout refreshLayout;
    private SimpleCursorAdapter cursorAdapter;

    /**
     * Use this factory method to create a new instance of
     * content fragment using input category.
     */
    public static ContentFragment newInstance(Category category) {
        ContentFragment fragment = new ContentFragment();

        Bundle args = new Bundle();
        args.putString(KEY_CATEGORY, category.name());
        fragment.setArguments(args);

        return fragment;
    }

    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseCategoryFromArgs();
        setHasOptionsMenu(true);
    }

    private void parseCategoryFromArgs() {
        Bundle args = getArguments();
        category = Category.valueOf(args.getString(KEY_CATEGORY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentFragmentView = inflater.inflate(R.layout.fragment_content, container, false);
        initializeViewAndData(contentFragmentView);
        return contentFragmentView;
    }

    private void initializeViewAndData(View view) {
        initializeRefreshLayout(view);
        initializeActionBar();
        initializeListFooter();
        createAndSetListAdapter();
        restartLoader(String.valueOf(FIRST_PAGE_INDEX));
    }

    private void initializeActionBar() {
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getActivity().getString(R.string.app_name));
            supportActionBar.setElevation(0);
        }
    }

    private void initializeListFooter() {
        listFooter = new ListFooterLayout(getActivity());
        listFooter.inflateListFooter(R.layout.listview_footer);
        listFooter.initFooterTextView(R.id.text_more,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMore();
                    }
                });
        listFooter.initLoadMoreProgressBar(R.id.load_progress_bar);
    }

    private void initializeRefreshLayout(View view) {
        refreshLayout = (RefreshLayout) view.findViewById(R.id.swipe_layout);
        setRefreshLayoutOffsetAndColorScheme();
        setListenersForRefreshLayout();
        setRefreshing();
    }

    private void setRefreshLayoutOffsetAndColorScheme() {
        // set offset to prevent refresh progress bar overlapped by action bar.
        int refreshProgressBarOffset = 100;
        refreshLayout.setProgressViewOffset(false, 0, refreshProgressBarOffset);
        refreshLayout.setColorSchemeResources(R.color.primary_material_dark);
    }

    private void setListenersForRefreshLayout() {
        setOnRefreshListener();
        setOnLoadListener();
    }

    private void setOnLoadListener() {
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                loadMore();
            }
        });
    }

    private void loadMore() {
        listFooter.displayLoadMoreProgressBar();
        loadPage(nextPageIndex);
    }

    private void loadPage(int pageNumber) {
        Log.d(XinyueApi.XINYUE_LOG_TAG, "nextPageIndex is:" + nextPageIndex);
        restartLoader(String.valueOf(pageNumber));

        if (listFooter.isLoadingMore()) nextPageIndex = pageNumber + 1;
    }

    private void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //isRefreshing = true;
                setRefreshing();
                loadPage(FIRST_PAGE_INDEX);
            }
        });

    }

    private void setRefreshing() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
    }

    private void createAndSetListAdapter() {
        String[] from = new String[] {PostReaderContract.PostTable.COLUMN_NAME_TITLE,
                PostReaderContract.PostTable.COLUMN_NAME_COVER};
        int[] to = new int[] {R.id.title, R.id.cover};
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.post_row, null, from, to, 0);
        cursorAdapter.setViewBinder(new RowViewBinder());
        setListAdapter(cursorAdapter);
    }

    // view binder to set title and cover for each row.
    private class RowViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (isTitle(cursor, columnIndex)) {
                setTitle(view, cursor);
            } else if (isCover(cursor, columnIndex)) {
                setCover(view, cursor);
            }

            return false;
        }

        private boolean isTitle(Cursor cursor, int columnIndex) {
            return columnIndex == cursor.getColumnIndexOrThrow(PostReaderContract.
                    PostTable.COLUMN_NAME_TITLE);
        }

        private boolean setTitle(View view, Cursor cursor) {
            TextView titleView = (TextView) view.findViewById(R.id.title);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(PostReaderContract.
                    PostTable.COLUMN_NAME_TITLE));
            titleView.setText(title);
            return true;
        }

        private boolean isCover(Cursor cursor, int columnIndex) {
            return columnIndex == cursor.getColumnIndexOrThrow(PostReaderContract.
                    PostTable.COLUMN_NAME_COVER);
        }

        private boolean setCover(View view, Cursor cursor) {
            String imageUri = cursor.getString(cursor.
                    getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_COVER));
            ImageView coverView = (ImageView) view.findViewById(R.id.cover);
            ImageAware imageAware = new ImageViewAware(coverView, false);
            DisplayImageOptions options = UILImageGetter.getDisplayImageOptions();
            ImageLoader.getInstance().displayImage(imageUri, imageAware, options);
            return true;
        }
    }

    private void restartLoader(String value) {
        Bundle args = new Bundle();
        args.putString(KEY_PAGE, value);
        getLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = getListView();
        listView.setDividerHeight(2);
        listView.addFooterView(listFooter.getListFooterView());
        refreshLayout.setChildView(listView);
    }

    /* open the post detail if one entry is clicked. */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), PostDetailActivity.class);
        Uri postUri = Uri.parse(PostContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(PostContentProvider.CONTENT_ITEM_TYPE, postUri);
        startActivity(i);

        // new activity emerges and slides from right to left
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    // create a new loader after the initLoader() call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String pageNum = args.getString(KEY_PAGE);

        String[] postProjection = {PostReaderContract.PostTable._ID,
                PostReaderContract.PostTable.COLUMN_NAME_TITLE,
                PostReaderContract.PostTable.COLUMN_NAME_COVER};
        String selection = PostReaderContract.PostTable.COLUMN_NAME_CATEGORY + " LIKE LOWER(?)";
        String[] selectionArgs = getCategoryMatchString();
        String sortOrder = getSortOrder(pageNum);

        PostsCursorLoader loader = null;
        try {
            loader = new PostsCursorLoader(getActivity(),
                    PostContentProvider.CONTENT_URI,
                    postProjection,
                    selection,
                    selectionArgs,
                    sortOrder,
                    category.getDisplayName(),
                    pageNum);

            dismissProgressBarIfPostsRetrieved();
        } catch (AsyncQueryRequest.NoDataConnectionException e) {
            Log.d(XinyueApi.XINYUE_LOG_TAG, e.toString());
            dismissProgressBarAndMakeToastIfNoDataConnection();
        } catch (AsyncQueryRequest.VolleyErrorException e) {
            Log.d(XinyueApi.XINYUE_LOG_TAG, e.toString());
            dismissProgressBarIfLoadFailed();
        } catch (AsyncQueryRequest.NoMorePostsException e) {
            Log.d(XinyueApi.XINYUE_LOG_TAG, e.toString());
            dismissProgressBarWhenNoMorePosts();
        } finally {
            resetRefreshLayoutFlags();
        }

        return loader;
    }

    @NonNull
    private String getSortOrder(String pageNum) {
        // display the post in decedent order and add 10 more posts per load.
        String totalDisplayedPostsAmount = getTotalDisplayedPostsAmount(pageNum);
        return PostReaderContract.PostTable.
                COLUMN_NAME_CREATED_DATE + " DESC" + totalDisplayedPostsAmount;
    }

    @NonNull
    private String getTotalDisplayedPostsAmount(String pageNum) {
        // if it's just refreshing, no need to limit the amount to 10 since $pageindex is 1.
        int pageIndex = Integer.valueOf(pageNum);
        return (refreshLayout.isRefreshing()) ? ("") : (" LIMIT " + pageIndex * POSTS_PER_LOAD);
    }

    private String[] getCategoryMatchString() {
        String categoryMatchString = category.getDisplayName();

        // every category is displayed as "all; earrings;".
        // Below code is trying to prevent the confusing of "rings" and "earrings".
        if (!category.equals(Category.all)) {
            categoryMatchString = "; " + categoryMatchString;
        }

        return new String[] {"%"+categoryMatchString+"%"};
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            onClickRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickRefresh() {
        listView.smoothScrollToPosition(0);
        refreshLayout.setRefreshing(true);
        //isRefreshing = true;
        setRefreshing();
        loadPage(FIRST_PAGE_INDEX);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        cursorAdapter.swapCursor(null);
    }

    private void dismissProgressBarAndMakeToastIfNoDataConnection() {
        // UI operation like Toast cannot perform in background thread without Looper.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                makeToastToIndicateError();
                dismissProgressBarWhenErrorEncountered();
            }
        });
    }

    private void makeToastToIndicateError() {
        Toast.makeText(getActivity(),
                getActivity().getResources().getString(R.string.data_connect_is_off),
                Toast.LENGTH_SHORT).show();
    }

    private void dismissProgressBarWhenErrorEncountered() {
        refreshLayout.setRefreshing(false);

        if (listFooter.isLoadingMore()) {
            keepNextPageIndexRemainTheSame();
            listFooter.displayLoadMoreTextViewWhenErrorEncountered();
        }
    }

    public void dismissProgressBarWhenNoMorePosts() {
        refreshLayout.setRefreshing(false);

        if (listFooter.isLoadingMore()) {
            keepNextPageIndexRemainTheSame();
            listFooter.displayNoMoreTextView();
            refreshLayout.setOnLoadListener(null);
        }
    }

    public void dismissProgressBarIfPostsRetrieved() {
        refreshLayout.setRefreshing(false);
        if (listFooter.isLoadingMore()) listFooter.displayLoadMoreTextView();
    }

    private void dismissProgressBarIfLoadFailed() {
        refreshLayout.setRefreshing(false);

        if (listFooter.isLoadingMore()) {
            if (nextPageIndex > 2) {
                nextPageIndex--;
            }
            listFooter.displayLoadMoreTextViewWhenErrorEncountered();
        }

        if (getActivity() != null) {
            Toast.makeText(getActivity(), getActivity().getResources().
                            getString(R.string.data_connect_is_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void keepNextPageIndexRemainTheSame() {
        if (nextPageIndex > 2) {
            nextPageIndex--;
        }
    }

    public void resetRefreshLayoutFlags() {
        refreshLayout.setLoading(false);
        refreshLayout.setRefreshing(false);
    }



    public Category getCategory() {
        return category;
    }

}