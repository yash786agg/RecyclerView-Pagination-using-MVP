package com.app.View;

import com.app.model.Products_Pojo;
import java.util.ArrayList;

/*
 * Created by Yash on 13/10/17.
 */

public interface MainActView
{
    /*
     * Update user info text mactView.
     * the String crop_name_List
     */
    void updateData(ArrayList<Products_Pojo> products_arrayList);
}
