package com.bencorp.scrab;

import java.io.File;

/**
 * Created by hp-pc on 8/21/2018.
 */

public class ListBlueprint {
    private String fileName;

    //private String fileName;

    public ListBlueprint(File fileName){

        setFileName(fileName);
    }

    private void setFileName(File fileName){
        String[] name = fileName.getName().split("\\.");
        this.fileName = name[0];
    }
    public String getFileName(){
        return fileName;
    }

}
