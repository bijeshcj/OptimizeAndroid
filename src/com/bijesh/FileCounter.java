package com.bijesh;

import com.bijesh.models.AndroidProject;

import java.io.File;

/**
 * Created by Bijesh on 12-04-2014.
 */
public class FileCounter {

    public void countFiles(){
        for(File file: AndroidProject.INS.androidProject.listFiles()){
//            System.out.println(file.getName());
            if(file.getName().equals(SearchTypes.res.toString())){
//                System.out.println("Found res folder");
                detectFileType(file);
            }else if(file.getName().equals(SearchTypes.src.toString())){
//                System.out.println("Found src folder");
                detectFileType(file);
            }else if(file.getName().equals(SearchTypes.AndroidManifest.toString()+".xml")){
//                System.out.println("Found manifest file");
                AndroidProject.INS.fileCount++;
//                System.out.println("file count now "+AndroidProject.INS.fileCount);
            }
        }
    }

    private void detectFileType(File path){
        for(File file:path.listFiles()){
            if(file.isDirectory()){
//                System.out.println("is Directory: "+file.getAbsolutePath());
                detectFileType(file);
            }else if(file.isFile()){
//                System.out.println("is file: "+file.getAbsolutePath());
                if(file.getName().endsWith(FileTypes.java.toString()) || file.getName().endsWith(FileTypes.xml.toString())){
                    AndroidProject.INS.fileCount++;
//                    System.out.println("file count now "+AndroidProject.INS.fileCount);
                }
            }
        }
    }
}
