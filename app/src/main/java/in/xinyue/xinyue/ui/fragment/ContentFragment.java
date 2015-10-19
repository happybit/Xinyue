package in.xinyue.xinyue.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import in.xinyue.xinyue.R;
import in.xinyue.xinyue.api.XinyueApi;
import in.xinyue.xinyue.contentprovider.PostContentProvider;
import in.xinyue.xinyue.request.GsonRequest;
import in.xinyue.xinyue.request.MySingleton;
import in.xinyue.xinyue.contentprovider.database.PostReaderContract;
import in.xinyue.xinyue.request.json.PostJson;
import in.xinyue.xinyue.request.json.TermsJson;
import in.xinyue.xinyue.api.Category;
import in.xinyue.xinyue.request.DataAndWifiConnectionStatus;
import in.xinyue.xinyue.view.ListFooterLayout;
import in.xinyue.xinyue.view.RefreshLayout;
import in.xinyue.xinyue.ui.activity.PostDetailActivity;

public class ContentFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_CATEGORY = "category";
    public static final String KEY_PAGE = "page";

    private static final int FIRST_PAGE_INDEX = 1;

    private int nextPageIndex = FIRST_PAGE_INDEX + 1;
    private Category category;
    private ListView listView;
    private RefreshLayout refreshLayout;
    private boolean isRefreshing = false;
    private SimpleCursorAdapter cursorAdapter;
    private ListFooterLayout listFooter;

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
                isRefreshing = true;
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

        SimpleCursorAdapter.ViewBinder savb =
                new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (columnIndex == cursor.
                                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_TITLE)) {
                            TextView titleView = (TextView) view.findViewById(R.id.title);
                            String title = cursor.getString(columnIndex);
                            titleView.setText(title);
                            return true;
                        } else if (columnIndex == cursor.
                                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_COVER)) {
                            setCoverResource(view, cursor);
                            return true;
                        }

                        return false;
                    }
                };

        cursorAdapter.setViewBinder(savb);
        setListAdapter(cursorAdapter);
    }

    private void setCoverResource(View view, Cursor cursor) {
        String imageUri = cursor.getString(cursor.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_COVER));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.primary_material_light)
                .showImageForEmptyUri(R.drawable.fail_empty_image)
                .showImageOnFail(R.drawable.fail_empty_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();

        ImageView coverView = (ImageView) view.findViewById(R.id.cover);

        ImageAware imageAware = new ImageViewAware(coverView, false);
        ImageLoader.getInstance().displayImage(imageUri, imageAware, options);
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
        String categoryMatchString = category.getDisplayName();
        if (!category.equals(Category.all)) {
            categoryMatchString = "; " + categoryMatchString;
        }
        String[] selectionArgs = new String[] {"%"+categoryMatchString+"%"};

        String limitPostNumber = (isRefreshing) ?
                ("") : (" LIMIT " + String.valueOf(Integer.valueOf(pageNum)*10));

        Log.d(XinyueApi.XINYUE_LOG_TAG, "create loader for category: " + category.getDisplayName());
        return (new CursorLoader(getActivity(),
                PostContentProvider.CONTENT_URI, postProjection, selection, selectionArgs,
                PostReaderContract.PostTable.
                        COLUMN_NAME_CREATED_DATE + " DESC" + limitPostNumber) {
            @Override
            public Cursor loadInBackground() {
                Cursor c = super.loadInBackground();
                refreshLayout.setLoading(false);
                Log.d(XinyueApi.XINYUE_LOG_TAG, "load in background for category: " + category.getDisplayName());

                final DataAndWifiConnectionStatus connectionStatus = new DataAndWifiConnectionStatus();

                if (connectionStatus.isDataConnected(getActivity())
                        || connectionStatus.isWifiConnected(getActivity())) {
                    asyncQueryRequest(PostContentProvider.CONTENT_URI,
                            category.getDisplayName(), pageNum);
                } else {
                    // UI operation like Toast cannot perform in background thread without Looper.
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getResources().
                                            getString(R.string.data_connect_is_off),
                                    Toast.LENGTH_SHORT).show();

                            refreshLayout.setRefreshing(false);

                            if (listFooter.isLoadingMore()) {
                                if (nextPageIndex > 2) {
                                    nextPageIndex--;
                                }

                                listFooter.displayLoadMoreTextViewWhenErrorEncountered();
                            }
                        }
                    });

                }

                isRefreshing = false;

                return c;
            }
        });
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
        isRefreshing = true;
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

    public void asyncQueryRequest(Uri uri, String category, final String pageNumber) {
        String url = String.format(XinyueApi.LIST, category, pageNumber);
        Log.d(XinyueApi.XINYUE_LOG_TAG, "request url is: " + url);
        GsonRequest<PostJson[]> gsonRequest = new GsonRequest<>(url, PostJson[].class, null,
                new Response.Listener<PostJson[]>() {
                    @Override
                    public void onResponse(PostJson[] response) {
                        List<PostJson> posts = Arrays.asList(response);

                        if (posts.isEmpty()) {
                            refreshLayout.setRefreshing(false);
                            Log.d(XinyueApi.XINYUE_LOG_TAG, "Category " + ContentFragment.this.category.getDisplayName() + " refreshing disappear for empty response.");
                            if (listFooter.isLoadingMore()) {
                                listFooter.displayNoMoreTextView();
                                refreshLayout.setOnLoadListener(null);
                            }
                            return;
                        }

                        for(int i=0; i<posts.size(); i++) {
                            PostJson post = posts.get(i);
                            ContentValues postValues = getPostValues(post);

                            if (getActivity() == null) {
                                return;
                            }

                            Uri providerUri = getActivity().getContentResolver().
                                    insert(PostContentProvider.CONTENT_URI, postValues);

                            if (providerUri != null) {
                                Log.d(XinyueApi.XINYUE_LOG_TAG, "Insert " + providerUri.toString() +
                                        " into database!");
                            }
                        }

                        refreshLayout.setRefreshing(false);
                        Log.d(XinyueApi.XINYUE_LOG_TAG, "Category " + ContentFragment.this.category.getDisplayName() + " refreshing disappear for complete response.");

                        Log.d(XinyueApi.XINYUE_LOG_TAG, "isLoadingMore is " + listFooter.isLoadingMore());
                        if (listFooter.isLoadingMore()) listFooter.displayLoadMoreTextView();
                    }

                    private ContentValues getPostValues(PostJson post) {
                        String postId = String.valueOf(post.getID());
                        String title = post.getTitle();
                        String category = getCategory(post);
                        String createddate = post.getDate();
                        String link = post.getLink();

                        String cover;
                        if (post.getFeatured_image() != null) {
                            cover = post.getFeatured_image().
                                    getSource();
                        } else {
                            cover = "drawable://" + R.drawable.fail_empty_image;
                        }

                        String content = post.getContent();

                        ContentValues postValues = new ContentValues();
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_POST_ID, postId);
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_TITLE, title);
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_CATEGORY, category);
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_COVER, cover);
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE, createddate);
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_LINK, link);
                        postValues.put(PostReaderContract.PostTable.COLUMN_NAME_CONTENT, content);
                        return postValues;
                    }

                    private String getCategory(PostJson post) {
                        List<TermsJson.CategoryJson> categories = post.getTerms().getCategory();

                        StringBuilder categorySb = new StringBuilder();

                        for (TermsJson.CategoryJson category : categories) {
                            String slugName = category.getSlug() + "; ";
                            categorySb.append(slugName);
                        }

                        return categorySb.toString();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(XinyueApi.XINYUE_LOG_TAG, volleyError.toString());
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
        });


        MySingleton.getInstance(getActivity()).addToRequestQueue(gsonRequest);

    }

}
