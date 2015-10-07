package com.carvalho.leonardo.ribbitnew;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Leonardo on 06/10/2015.
 */
public class ToastGen
{
    private Context context;

    public ToastGen(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }




}
