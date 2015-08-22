package com.hahattpro.meowdebughelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.File;

/**
 * Created by Thien on 7/28/2015.
 */
public class Mailer {

    Context context;

    public Mailer(Context context) {
        this.context = context;
    }



    public void SendMail(String to,String subject, String msg,@Nullable File file1){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

// The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);
        if (file1!=null) {
            Log.i("meow", "path = " + file1.getPath());
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file1));
        }
        context.startActivity(emailIntent);
    }
}
