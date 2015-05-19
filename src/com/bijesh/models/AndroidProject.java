package com.bijesh.models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bijesh on 4/10/14.
 */
public enum AndroidProject {INS;
    public File androidProject;
    public File androidManifest;
    public File resFolderPath;
    public File srcFolderPath;
    public File layoutFolderPath;
    public File backupFolderPath;
    public int fileCount;
    public ArrayList<File> imageFilePath = new ArrayList<File>();
    public ArrayList<String> imageFileNames = new ArrayList<String>();
    public ArrayList<Integer> imagePositions = new ArrayList<Integer>();
    public ArrayList<File> imageDeleteFilePath = new ArrayList<File>();
    public int NOT_USED = 0;
}
