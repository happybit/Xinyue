package in.xinyue.xinyue.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import in.xinyue.xinyue.database.GsonRequest;
import in.xinyue.xinyue.database.MySingleton;
import in.xinyue.xinyue.database.PostDatabaseHelper;
import in.xinyue.xinyue.database.PostReaderContract;
import in.xinyue.xinyue.json.PostJson;
import in.xinyue.xinyue.json.TermsJson;
import in.xinyue.xinyue.model.Category;
import in.xinyue.xinyue.model.RefreshLayout;
import in.xinyue.xinyue.ui.PostDetailActivity;

public class ContentFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int FIRST_PAGE_NUMBER = 1;
    private int mNextPage = 2;
    private Category mCategory;

    private ListView mListView;
    private SimpleCursorAdapter adapter;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private RefreshLayout refreshLayout;
    private View footerLayout;
    private TextView textMore;
    private ProgressBar progressBar;
    private View contentView;

    private boolean loadMoreFlag = false;

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_PAGE = "extra_page";

    private String[] projection = {PostReaderContract.PostTable._ID,
            PostReaderContract.PostTable.COLUMN_NAME_TITLE,
            PostReaderContract.PostTable.COLUMN_NAME_COVER};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContentFragment.
     */
    public static ContentFragment newInstance(Category category) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(args);
        return fragment;
    }

    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Set title bar
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Xinyue");
        }

        contentView = inflater.inflate(R.layout.fragment_content, container, false);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parseArgument();

        //swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_layout);
        refreshLayout = (RefreshLayout) contentView.findViewById(R.id.swipe_layout);
        footerLayout = getActivity().getLayoutInflater().inflate(R.layout.listview_footer, null);
        textMore = (TextView) footerLayout.findViewById(R.id.text_more);
        progressBar = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);

        textMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreFlag = true;
                loadMore();
            }
        });

        mListView = getListView();
        mListView.setDividerHeight(2);

        mListView.addFooterView(footerLayout);
        refreshLayout.setChildView(mListView);

        // prevent the progress bar covering by action bar.
        int topToPadding = 100;
        refreshLayout.setProgressViewOffset(false, 0, topToPadding);

        refreshLayout.setColorSchemeResources(R.color.AccentColor);

        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNext(FIRST_PAGE_NUMBER);
            }
        });

        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                loadMoreFlag = true;
                loadMore();
            }
        });

        Log.d("XinyueLog", "refresh layout refreshing status is " + refreshLayout.isRefreshing());
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
        Log.d("XinyueLog", "refresh layout refreshing status is " + refreshLayout.isRefreshing());
        fillData();
    }

    private void loadMore() {
        textMore.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loadNext(mNextPage);
    }

    private void parseArgument() {
        Bundle args = getArguments();
        mCategory = Category.valueOf(args.getString(EXTRA_CATEGORY));
    }

    // open the second activity if an entry is clicked
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), PostDetailActivity.class);
        Log.d("XinyueLog", String.valueOf(id));
        Uri postUri = Uri.parse(PostContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(PostContentProvider.CONTENT_ITEM_TYPE, postUri);

        startActivity(i);
    }

    private void fillData() {
        String[] from = new String[] {PostReaderContract.PostTable.COLUMN_NAME_TITLE, PostReaderContract.PostTable.COLUMN_NAME_COVER};
        int[] to = new int[] {R.id.title, R.id.cover};

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.post_row, null, from, to, 0);

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

        adapter.setViewBinder(savb);
        setListAdapter(adapter);

        Bundle args = new Bundle();
        args.putString(EXTRA_PAGE, String.valueOf(FIRST_PAGE_NUMBER));
        getLoaderManager().initLoader(getLoaderId(mCategory), args, this);
    }

    private void setCoverResource(View view, Cursor cursor) {
        String imageUri = cursor.getString(cursor.
                getColumnIndexOrThrow(PostReaderContract.PostTable.COLUMN_NAME_COVER));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.reminder)
                .showImageForEmptyUri(R.drawable.reminder)
                .showImageOnFail(R.drawable.reminder)
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
        args.putString(EXTRA_PAGE, String.valueOf(pageNumber));
        getLoaderManager().restartLoader(getLoaderId(mCategory), args, this);

        if (pageNumber>1) {
            mNextPage = pageNumber + 1;
        }
    }

    private int getLoaderId(Category category) {
        return category.ordinal();
    }


    // create a new loader after the initLoader() call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String pageNum = args.getString(EXTRA_PAGE);

        String selection = PostReaderContract.PostTable.COLUMN_NAME_CATEGORY + " LIKE LOWER(?)";
        String categoryMatchString = mCategory.getDisplayName();
        if (!mCategory.equals(Category.all)) {
            categoryMatchString = "; " + categoryMatchString;
        }
        String[] selectionArgs = new String[] {"%"+categoryMatchString+"%"};

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                PostContentProvider.CONTENT_URI, projection, selection, selectionArgs,
                PostReaderContract.PostTable.COLUMN_NAME_CREATED_DATE + " DESC") {
            @Override
            public Cursor loadInBackground() {
                Cursor c = super.loadInBackground();
                refreshLayout.setLoading(false);
                asyncQueryRequest(PostContentProvider.CONTENT_URI, mCategory.getDisplayName(), pageNum);
                return c;
            }
        };

        return cursorLoader;
    }

    private Uri getContentUri(String pageNumber) {
        Uri contentUri = PostContentProvider.CONTENT_URI.buildUpon().
                appendQueryParameter(EXTRA_CATEGORY, mCategory.getDisplayName()).build();
        contentUri = contentUri.buildUpon().
                appendQueryParameter(EXTRA_PAGE, pageNumber).build();
        return contentUri;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            refreshLayout.setRefreshing(true);
            loadNext(FIRST_PAGE_NUMBER);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        //refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
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
        Log.d("XinyueLog", "request url is: " + url);
        GsonRequest<PostJson[]> gsonRequest = new GsonRequest<>(url, PostJson[].class, null,
                new Response.Listener<PostJson[]>() {
                    @Override
                    public void onResponse(PostJson[] response) {
                        List<PostJson> posts = Arrays.asList(response);

                        if (posts.isEmpty()) {
                            refreshLayout.setRefreshing(false);

                            if (loadMoreFlag) {
                                textMore.setText("No more to load!");
                                textMore.setOnClickListener(null);
                                refreshLayout.setOnLoadListener(null);
                                textMore.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }

                            return;
                        }

                        for(int i=0; i<posts.size(); i++) {
                            PostJson post = posts.get(i);
                            ContentValues postValues = getPostValues(post);

                            Uri providerUri = getActivity().getContentResolver().
                                    insert(PostContentProvider.CONTENT_URI, postValues);

                            if (providerUri != null) {
                                Log.d("XinyueLog", "Insert " + providerUri.toString() +
                                        " into database!");
                            }
                        }

                        refreshLayout.setRefreshing(false);

                        if (loadMoreFlag) {
                            textMore.setText("more...");
                            textMore.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
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
                                    getAttachment_meta().getSizes().getMedium().getUrl();
                        } else {
                            cover = "drawable://" + R.drawable.reminder;
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
                            categorySb.append(category.getSlug() + "; ");
                        }

                        return categorySb.toString();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("XinyueLog", volleyError.toString());
                refreshLayout.setRefreshing(false);

                if (loadMoreFlag) {
                    if (mNextPage > 2) {
                        mNextPage--;
                    }
                    textMore.setText("Connect failed. Click to retry!");
                    textMore.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                //Toast.makeText(getActivity(), "Internet connection error.",
                //        Toast.LENGTH_SHORT).show();
            }
        });


        MySingleton.getInstance(getActivity()).addToRequestQueue(gsonRequest);

    }

}
