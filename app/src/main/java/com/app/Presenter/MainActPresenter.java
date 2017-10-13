package com.app.Presenter;

import com.app.View.MainActView;
import com.app.model.Products_Pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/*
 * Created by Yash on 13/10/17.
 */

public class MainActPresenter
{
    /**
     * ArrayList : products_arrayList
     */

    private ArrayList<Products_Pojo> products_arrayList;

    /**
     * View : MainActView
     */
    private MainActView mactView;

    /*
     * Instantiates a new MainActivity Presenter.
     *
     * @param mactView
     *     the mactView
     */

    public MainActPresenter(MainActView mactView)
    {
        this.mactView = mactView;
        products_arrayList = new ArrayList<>();
    }

    public void setAllData(JSONObject response,int pageIndex)
    {
        try
        {
            JSONArray productsJsonArray = response.getJSONArray("products");

            if (productsJsonArray.length() >= 1)
            {
                if(pageIndex == 0)
                {
                    products_arrayList.clear();// --> Clear method is used to clear the ArrayList
                }

                for (int i = 0; i < productsJsonArray.length(); i++)
                {
                    JSONObject products_jsonObject = productsJsonArray.getJSONObject(i);

                    Products_Pojo products_pojo = new Products_Pojo();

                    if(products_jsonObject.has("title"))
                    {
                        if (!(products_jsonObject.isNull("title")))
                        {
                            products_pojo.setTitle(products_jsonObject.getString("title"));
                        }
                    }
                    products_arrayList.add(products_pojo);
                }
                mactView.updateData(products_arrayList);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
