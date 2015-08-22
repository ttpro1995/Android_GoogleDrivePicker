package com.hahattpro.meowdebughelper;

import android.content.Context;

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
 * contructor take FILE_NAME, what in that file, context of activity
 *
 */
public class CreateFile {
    private File file;//contain file
    private String FILE_NAME;//name of file
    private Context context;// context from activity
    public CreateFile(String NAME, String file_body, Context appContext) {
        FILE_NAME =NAME;
    context = appContext;

        try{
            FileOutputStream out = context.openFileOutput(FILE_NAME,0);
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

        file = context.getFileStreamPath(FILE_NAME);//put into file
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
