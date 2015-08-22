package com.thaithien.apcs1351040.googledrivepicker;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.hahattpro.meowdebughelper.SaveFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity  {


    GoogleApiClient mGoogleApiClient = null;
    GoogleApiClient.ConnectionCallbacks callbacks;
    GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    int GOOGLE_DRIVE_LOGIN_REQUEST_CODE = 101;
    String GOOGLEDRIVE_LOG_TAG = "GOOGLE DRIVE";
    int ACCOUNT_PICKER_REQUEST_CODE = 102;
    int AFTER_UPLOAD_REQUEST_CODE = 103;
    Button button;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFile();
        button = (Button) findViewById(R.id.mButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAccountPicker();
            }
        });

        callbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnected");
                saveFiletoDrive(file);
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnectionSuspended");
            }
        };

        connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.hasResolution()) {
                    try {
                        //For first login when user choose account then ask for permission
                        //must call onActivityResult
                        Log.i(GOOGLEDRIVE_LOG_TAG,"onConnection failed has resolution");
                        connectionResult.startResolutionForResult(MainActivity.this, GOOGLE_DRIVE_LOGIN_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        // Unable to resolve, message user appropriately
                        Log.i(GOOGLEDRIVE_LOG_TAG, "something wrong");
                        e.printStackTrace();
                    }
                } else {
                    GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), MainActivity.this, 0).show();
                }
            }
        };
    }

    private void createFile(){
        SaveFile sv = new SaveFile("meowmeow.txt","Keep calm and meow on",this);
        file = sv.getFile();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_DRIVE_LOGIN_REQUEST_CODE)
            if (resultCode == RESULT_OK) {
                Log.i(GOOGLEDRIVE_LOG_TAG,"onConnection failed has resolution result");
                mGoogleApiClient.connect();
            }
        if (requestCode == ACCOUNT_PICKER_REQUEST_CODE)
        {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.i(GOOGLEDRIVE_LOG_TAG,"result account = "+accountName);
            LoginGoogleApi(accountName);
        }
        if (requestCode == AFTER_UPLOAD_REQUEST_CODE)
        {
            Log.i(GOOGLEDRIVE_LOG_TAG,"upload complete");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   if (mGoogleApiClient!=null)
     //   mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void LoginGoogleApi(String AccountName)
    {
        Log.i(GOOGLEDRIVE_LOG_TAG,"set account name " + AccountName);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .setAccountName(AccountName)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        //mGoogleApiClient.clearDefaultAccountAndReconnect();
        mGoogleApiClient.connect();
    }

    public void GoogleAccountPicker()
    {
        Log.i(GOOGLEDRIVE_LOG_TAG,"account picker");
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_PICKER_REQUEST_CODE);
    }

    public void saveFiletoDrive(final File mFile){
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        String TAG = "Drive new content";
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");

                        InputStream is;

                        try{
                        is = new FileInputStream(mFile);
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write inputstream to outputstream
                            org.apache.commons.io.IOUtils.copy(is, outputStream);
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle(file.getName()).build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(intentSender, AFTER_UPLOAD_REQUEST_CODE, null, 0, 0, 0);
                        }catch (Exception e){e.printStackTrace();}
                    }
                });
    }

}
