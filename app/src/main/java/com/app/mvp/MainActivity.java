package com.app.mvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.Adapter.ProductsAdapter;
import com.app.Extras.ConstantData;
import com.app.Extras.MySingleton;
import com.app.Presenter.MainActPresenter;
import com.app.View.MainActView;
import com.app.model.Products_Pojo;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActView
{
    private ArrayList<Products_Pojo> products_arrayList;
    private RecyclerView products_rclv;
    private MainActPresenter mainActPresenter;
    private boolean isLoading = false;
    private RelativeLayout progressBar;
    private int pageIndex = 0;
    private LinearLayoutManager linearLayoutManager;
    private ProductsAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActPresenter = new MainActPresenter(this);

        /*
         *  Intialization of ProgressBar
         */

        progressBar = (RelativeLayout) findViewById(R.id.rltv_progressBar);

         /*
         *  Intialization of RecyclerView
         */

        products_rclv = (RecyclerView) findViewById(R.id.rclv_products);

         /*
         *  Intialization of ArrayList
         */

        products_arrayList = new ArrayList<>();

        String ProductsUrl = ConstantData.service_URL + "search?page="+pageIndex+"&pageSize=15";

        getProductData(ProductsUrl);

         /*
         *  Intialization of Push LoadMore OnScrollListener in Recylerview
         */

        products_rclv.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading)
                {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0)
                    {
                        isLoading = true;

                        pageIndex++;

                        String ProductsUrl = ConstantData.service_URL + "search?page="+pageIndex+"&pageSize=15";

                        getProductData(ProductsUrl);
                    }
                }
            }
        });
    }

    private void getProductData(String ProductsUrl)
    {
        progressBar.setVisibility(View.VISIBLE);

        final int pageNumber = pageIndex;

        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ProductsUrl, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                if(response.length() >= 1)
                {
                    mainActPresenter.setAllData(response,pageIndex);

                }
                else
                {
                    if(pageNumber != 0)
                    {
                        pageIndex = pageNumber - 1;
                    }
                }

                if(progressBar != null)
                {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                if(progressBar != null)
                {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        mJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(mJsonObjectRequest);

        /*
         *  Above MySingleton is used to Create a Instance Object only one time
         *  which is used to maintain Queue of Request Because Request If no Resonse came
         *  Request is made again to server within 30 second if 20 second exceeds
         *  it will show the timeout.
         */
    }

    @Override
    public void updateData(ArrayList<Products_Pojo> List)
    {
        products_arrayList = List;

        if(pageIndex == 0)
        {
            productsAdapter = new ProductsAdapter(MainActivity.this, products_arrayList);

            products_rclv.setAdapter(productsAdapter);

            linearLayoutManager = new LinearLayoutManager(MainActivity.this);
            products_rclv.setLayoutManager(linearLayoutManager);
        }
        else
        {
            isLoading = false;
            if(productsAdapter != null)
            {
                // notifyDataSetChanged is used to Notify The Adapter afer having Changes in RecyclerView
                productsAdapter.notifyDataSetChanged();
            }
        }
    }
}
