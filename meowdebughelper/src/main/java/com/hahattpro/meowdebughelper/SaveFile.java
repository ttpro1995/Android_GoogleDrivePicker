package com.hahattpro.meowdebughelper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by haha on 5/6/2015.
 */


/**
 * Create new file.
 * to get that file, use getter
 * contructor take FILE_NAME (ex: my_note_about_cat.txt), what in that file (ex: there is a cat), context of activity (ex: MainActivity.this)
 * File will be save into Download Folder
 * Search for "SaveFile path" log tag to know where your file
 */
public class SaveFile {
    private File file;//contain file
    private String FILE_NAME;//name of file
    private Context context;// context from activity
    private String path;


    public SaveFile(String NAME, String file_body, Context appContext) {
        FILE_NAME =NAME;
    context = appContext;
    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" +FILE_NAME;
        try{
            file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(file_body);
            writer.close();
            out.close();
        }
        catch (FileNotFoundException e){

        }
        catch (IOException e){
            //do nothing
        }
        Log.i("SaveFile path","path : "+path);
    }

    public File getFile()
    {//getter
        return file;
    }

    void CleanUp()
    {//delete file on phone so no memory leak
        context.deleteFile(FILE_NAME);
    }

}

//TODO: create List File and readfile
