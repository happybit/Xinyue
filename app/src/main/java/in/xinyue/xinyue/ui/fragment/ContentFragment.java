package in.xinyue.xinyue.ui.fragment;

import android.content.ContentValues;
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
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import in.xinyue.xinyue.view.RefreshLayout;
import in.xinyue.xinyue.ui.activity.PostDetailActivity;

public class ContentFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_CATEGORY = "category";
    public static final String KEY_PAGE = "page";

    private static final int FIRST_PAGE_INDEX = 1;

    private final DataAndWifiConnectionStatus connectionStatus = new DataAndWifiConnectionStatus();

    private int nextPageIndex = FIRST_PAGE_INDEX + 1;
    private Category category;

    private ListView listView;
    private RefreshLayout refreshLayout;
    private View listFooterView;
    private TextView loadMoreTextView;
    private ProgressBar loadMoreProgressBar;
    private View contentFragmentView;

    private SimpleCursorAdapter cursorAdapter;

    private boolean isLoadingMore = false;
    private boolean isRefreshing = false;

    private String[] postProjection = {PostReaderContract.PostTable._ID,
            PostReaderContract.PostTable.COLUMN_NAME_TITLE,
            PostReaderContract.PostTable.COLUMN_NAME_COVER};

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setActionBarTitleAndElevation();
        contentFragmentView = inflater.inflate(R.layout.fragment_content, container, false);
        parseArgument();

        setHasOptionsMenu(true);

        refreshLayout = (RefreshLayout) contentFragmentView.findViewById(R.id.swipe_layout);
        listFooterView = getActivity().getLayoutInflater().inflate(R.layout.listview_footer, null);
        loadMoreTextView = (TextView) listFooterView.findViewById(R.id.text_more);
        loadMoreProgressBar = (ProgressBar) listFooterView.findViewById(R.id.load_progress_bar);
        loadMoreProgressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        loadMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoadingMore = true;
                loadMore();
            }
        });

        // prevent the progress bar covering by action bar.
        int topToPadding = 100;
        refreshLayout.setProgressViewOffset(false, 0, topToPadding);

        refreshLayout.setColorSchemeResources(R.color.primary_material_dark);

        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                loadNext(FIRST_PAGE_INDEX);
            }
        });

        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                isLoadingMore = true;
                loadMore();
            }
        });

        Log.d(XinyueApi.XINYUE_LOG_TAG, category.getDisplayName()
                + " refresh layout refreshing status is " + refreshLayout.isRefreshing());
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
            Log.d(XinyueApi.XINYUE_LOG_TAG, "Category "
                    + category.getDisplayName() + " is refreshing.");
        }
        Log.d(XinyueApi.XINYUE_LOG_TAG, category.getDisplayName()
                + " refresh layout refreshing status is " + refreshLayout.isRefreshing());
        fillData();

        return contentFragmentView;
    }

    private void setActionBarTitleAndElevation() {
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getActivity().getString(R.string.app_name));
            supportActionBar.setElevation(0);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = getListView();
        listView.setDividerHeight(2);

        listView.addFooterView(listFooterView);
        refreshLayout.setChildView(listView);

    }

    private void loadMore() {
        loadMoreTextView.setVisibility(View.GONE);
        loadMoreProgressBar.setVisibility(View.VISIBLE);
        loadNext(nextPageIndex);
    }

    private void parseArgument() {
        Bundle args = getArguments();
        category = Category.valueOf(args.getString(KEY_CATEGORY));
    }

    // open the post detail if an entry is clicked.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), PostDetailActivity.class);
        Log.d(XinyueApi.XINYUE_LOG_TAG, String.valueOf(id));
        Uri postUri = Uri.parse(PostContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(PostContentProvider.CONTENT_ITEM_TYPE, postUri);

        startActivity(i);
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void fillData() {
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

        Bundle args = new Bundle();
        args.putString(KEY_PAGE, String.valueOf(FIRST_PAGE_INDEX));
        //getLoaderManager().initLoader(getLoaderId(category), args, this);
        //getLoaderManager().initLoader(0, args, this);
        getLoaderManager().restartLoader(0, args, this);
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
        //.displayer(new RoundedBitmapDisplayer(20)).build();

        ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        ImageView coverView = (ImageView) view.findViewById(R.id.cover);

        // ImageLoader.getInstance().displayImage(imageUri, coverView, options, animateFirstListener);
        //ImageLoader.getInstance().displayImage(imageUri, coverView, options);
        ImageAware imageAware = new ImageViewAware(coverView, false);
        ImageLoader.getInstance().displayImage(imageUri, imageAware, options);
    }

    private void loadNext(int pageNumber) {
        Bundle args = new Bundle();
        args.putString(KEY_PAGE, String.valueOf(pageNumber));
        //getLoaderManager().restartLoader(getLoaderId(category), args, this);
        getLoaderManager().restartLoader(0, args, this);
        Log.d(XinyueApi.XINYUE_LOG_TAG, "restart loader for category: " + category.getDisplayName());

        if (isLoadingMore) {
            nextPageIndex = pageNumber + 1;
        }
    }

    // create a new loader after the initLoader() call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String pageNum = args.getString(KEY_PAGE);

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

                            if (isLoadingMore) {
                                if (nextPageIndex > 2) {
                                    nextPageIndex--;
                                }
                                loadMoreTextView.setText(getActivity().getResources().
                                        getString(R.string.footer_fail_indication));
                                loadMoreTextView.setVisibility(View.VISIBLE);
                                loadMoreProgressBar.setVisibility(View.GONE);
                                isLoadingMore = false;
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
            listView.smoothScrollToPosition(0);
            refreshLayout.setRefreshing(true);
            isRefreshing = true;
            loadNext(FIRST_PAGE_INDEX);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
        //refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        cursorAdapter.swapCursor(null);
    }

    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
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

                            if (isLoadingMore) {
                                loadMoreTextView.setText(getActivity().getResources().
                                        getString(R.string.footer_no_more_indication));
                                loadMoreTextView.setOnClickListener(null);
                                refreshLayout.setOnLoadListener(null);
                                loadMoreTextView.setVisibility(View.VISIBLE);
                                loadMoreProgressBar.setVisibility(View.GONE);
                                isLoadingMore = false;
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

                        Log.d(XinyueApi.XINYUE_LOG_TAG, "isLoadingMore is " + isLoadingMore);
                        if (isLoadingMore) {
                            loadMoreTextView.setText(getActivity().getResources().
                                    getString(R.string.footer_has_more_indication));
                            loadMoreTextView.setVisibility(View.VISIBLE);
                            loadMoreProgressBar.setVisibility(View.GONE);
                            isLoadingMore = false;
                        }

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

                if (isLoadingMore) {
                    if (nextPageIndex > 2) {
                        nextPageIndex--;
                    }
                    loadMoreTextView.setText(getActivity().getResources().
                            getString(R.string.footer_fail_indication));
                    loadMoreTextView.setVisibility(View.VISIBLE);
                    loadMoreProgressBar.setVisibility(View.GONE);
                    isLoadingMore = false;
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
