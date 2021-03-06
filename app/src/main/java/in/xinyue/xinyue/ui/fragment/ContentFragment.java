package in.xinyue.xinyue.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.XinyueApi;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.contentprovider.database.PostReaderContract;
import in.xinyue.xinyue.request.AsyncQuery;
import in.xinyue.xinyue.request.RequestCallback;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.ui.adapter.ListViewBinder;
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
    private AsyncQuery asyncQuery;

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
        asyncQuery = new AsyncQuery(getActivity(),
                category.getDisplayName(), createRequestCallback());
    }

    private RequestCallback createRequestCallback() {
        return new RequestCallback() {
            @Override
            public void onRetrievePosts() {
                Log.d(XinyueApi.XINYUE_LOG_TAG, "dismiss progress bar when posts retrieved.");
                dismissProgressBarIfPostsRetrieved();
            }

            @Override
            public void onVolleyError() {
                Log.d(XinyueApi.XINYUE_LOG_TAG, "on volley error");
                dismissProgressBarIfLoadFailed();
            }

            @Override
            public void onNoMorePosts() {
                Log.d(XinyueApi.XINYUE_LOG_TAG, "no more posts");
                dismissProgressBarIfNoMorePosts();
            }

            @Override
            public void onNoDataConnection() {
                Log.d(XinyueApi.XINYUE_LOG_TAG, "no data connection");
                dismissProgressBarAndMakeToastIfNoDataConnection();
            }
        };
    }

    private void parseCategoryFromArgs() {
        Bundle args = getArguments();
        category = Category.valueOf(args.getString(KEY_CATEGORY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentFragmentView = inflater.inflate(R.layout.fragment_content, container, false);
        initialization(contentFragmentView);
        return contentFragmentView;
    }

    private void initialization(View view) {
        initRefreshLayout(view);
        initActionBar();
        initListFooter();
        initAdapter();
        restartLoaderAndRequestPage(FIRST_PAGE_INDEX);
    }

    private void initActionBar() {
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getActivity().getString(R.string.app_name));
            supportActionBar.setElevation(0);
        }
    }

    private void initListFooter() {
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

    private void initRefreshLayout(View view) {
        refreshLayout = (RefreshLayout) view.findViewById(R.id.swipe_layout);
        setRefreshLayoutOffsetAndColorScheme();
        setListenersForRefreshLayout();
        displayRefreshProgressBar();
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

    private void loadPage(int pageIndex) {
        Log.d(XinyueApi.XINYUE_LOG_TAG, "nextPageIndex is:" + nextPageIndex);
        restartLoaderAndRequestPage(pageIndex);

        if (listFooter.isLoadingMore()) nextPageIndex = pageIndex + 1;
    }

    private void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    private void displayRefreshProgressBar() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
    }

    private void dismissRefreshProgressBar() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private void initAdapter() {
        String[] from = new String[] {PostReaderContract.PostTable.COLUMN_NAME_TITLE,
                PostReaderContract.PostTable.COLUMN_NAME_COVER};
        int[] to = new int[] {R.id.title, R.id.cover};
        cursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.post_row, null, from, to, 0);
        cursorAdapter.setViewBinder(new ListViewBinder());
        setListAdapter(cursorAdapter);
    }

    private void restartLoaderAndRequestPage(int pageIndex) {
        Bundle args = new Bundle();
        args.putInt(KEY_PAGE, pageIndex);
        getLoaderManager().restartLoader(0, args, this);
        refreshLayout.setLoading(false); // need to reset loading flag during each loading.
        asyncQuery.loadingPage(pageIndex);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = getListView();

        int DIVIDER_HEIGHT = 2;
        listView.setDividerHeight(DIVIDER_HEIGHT);

        listView.addFooterView(listFooter.getListFooterView());
        refreshLayout.setChildView(listView);
    }

    /* open the post detail if one entry is clicked. */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int INVALID_POST_ID = -1;
        super.onListItemClick(l, v, position, id);
        if (id == INVALID_POST_ID) return; // prevent crash when id is -1;
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
        final int pageIndex = args.getInt(KEY_PAGE);

        String[] postProjection = {PostReaderContract.PostTable._ID,
                PostReaderContract.PostTable.COLUMN_NAME_TITLE,
                PostReaderContract.PostTable.COLUMN_NAME_COVER};
        String selection = PostReaderContract.PostTable.COLUMN_NAME_CATEGORY + " LIKE LOWER(?)";
        String[] selectionArgs = getCategoryMatchString();
        String sortOrder = getSortOrder(pageIndex);
        Log.d(XinyueApi.XINYUE_LOG_TAG, "page index is " + pageIndex);
        Log.d(XinyueApi.XINYUE_LOG_TAG, "sort order is " + sortOrder);

        return new CursorLoader(getActivity(),
                PostContentProvider.CONTENT_URI,
                postProjection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @NonNull
    private String getSortOrder(int pageNum) {
        // display the post in decedent order and add 10 more posts per load.
        String totalDisplayedPostsAmount = getTotalDisplayedPostsAmount(pageNum);
        return PostReaderContract.PostTable.
                COLUMN_NAME_CREATED_DATE + " DESC" + totalDisplayedPostsAmount;
    }

    @NonNull
    private String getTotalDisplayedPostsAmount(int pageIndex) {
        // if it's just refreshing, no need to limit the amount to 10 since $pageindex is 1.
        return (refreshLayout.isRefreshing()) ? ("") : (" LIMIT " + pageIndex * POSTS_PER_LOAD);
    }

    private String[] getCategoryMatchString() {
        String categoryMatchString = category.getDisplayName();

        // every category is displayed as "all; earrings;".
        // Below code is trying to prevent the confusing of "rings" and "earrings".
        // For instance, "rings" will return "; rings".
        if (!category.equals(Category.all)) {
            categoryMatchString = "; " + categoryMatchString;
        }

        return new String[] {"%"+categoryMatchString+"%"};
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
        makeToastToIndicateError(R.string.data_connect_is_off);
        dismissProgressBarIfErrorEncountered();
    }

    private void makeToastToIndicateError(int resource) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(),
                    getActivity().getResources().getString(resource),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissProgressBarIfErrorEncountered() {
        dismissRefreshProgressBar();

        if (listFooter.isLoadingMore()) {
            keepNextPageIndexRemainTheSame();
            listFooter.displayLoadMoreTextViewWhenErrorEncountered();
        }
    }

    private void dismissProgressBarIfNoMorePosts() {
        dismissRefreshProgressBar();

        if (listFooter.isLoadingMore()) {
            keepNextPageIndexRemainTheSame();
            listFooter.displayNoMoreTextView();
            refreshLayout.setOnLoadListener(null);
        }
    }

    private void dismissProgressBarIfPostsRetrieved() {
        dismissRefreshProgressBar();
        if (listFooter.isLoadingMore()) listFooter.displayLoadMoreTextView();
    }

    private void dismissProgressBarIfLoadFailed() {
        makeToastToIndicateError(R.string.data_connect_is_failed);
        dismissProgressBarIfErrorEncountered();
    }

    private void keepNextPageIndexRemainTheSame() {
        if (nextPageIndex > 2) {
            nextPageIndex--;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            refreshList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshList() {
        listView.smoothScrollToPosition(0);
        displayRefreshProgressBar();
        loadPage(FIRST_PAGE_INDEX);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(XinyueApi.XINYUE_LOG_TAG, "category " + category.getDisplayName() + " is resuming.");
    }
}