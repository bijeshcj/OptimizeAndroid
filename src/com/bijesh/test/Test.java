package com.bijesh.test;

import com.bijesh.FileTypes;
import com.bijesh.SearchTypes;
import com.bijesh.Utility;
import com.bijesh.models.AndroidProject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bijesh on 4/10/14.
 */
public class Test {
    public static void main(String... str) throws IOException{
//        System.out.println(File.separator);
//        System.out.println(new Test().splitFileName("button_normal.9.png"));
//        checkForFileName(fileContent,"logo");
//          new Test().countFiles(new File("C:\\Bijesh\\Test\\MyAndroidApp"));
//         System.out.println("Number of java and xml files in the project are "+AndroidProject.INS.fileCount);
//        new Test().setAddition();

//         new Test().detectComments(getFileContent(new File("C:\\Users\\bijesh\\Desktop\\OptimizeAndroidPresentation\\TestFifa_6_May\\src\\com\\prokarma\\fifa\\webservice\\AsynchronousSender.java")));
//         new Test().detectSingleLineComments(fileContent);
//           new Test().finder(fileContent,"R.drawable.logo");
//        System.out.println(doesContains(fileContent,"R.drawable.logo"));
//        Utility.createBackupFolder(new File("C:\\Users\\bijesh\\Desktop\\OptimizeAndroidPresentation\\TestFifa_6_May"));
        new Test().getInnerBackupFolderName(new File("C:\\Users\\bijesh\\Desktop\\OptimizeAndroidPresentation\\TestFifa_6_May\\res\\drawable-ldpi\\lic1.png"));
//        Windows 7
//        new Test().printProperties();
    }

    private void printProperties(){
        System.out.println(System.getProperty("os.name"));
    }

    private String getInnerBackupFolderName(File resourceFile){
        String filePathStr = resourceFile.getParent().toString();
        System.out.println("$$ FilenameUtils.separatorsToWindows() "+resourceFile.getParent());
        String seperator = "";
        String folderName = "";
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            seperator = "\\\\";
        }else{
            System.out.println("Non Windows ");
        }
        String[] splits = filePathStr.split(seperator);
        if(splits != null){
           folderName = splits[splits.length-1];
        }
        System.out.println("$$$ Folder name is "+folderName);
        return folderName;
    }

    private static String getFileContent(File file) throws IOException{
        String contents = FileUtils.readFileToString(file);
        return contents;
    }

/*

 */

    static String fileContent = "this is a proper " +
            " string which contains the long string  " +
            "people say its a long string but do you really think so " +
            "you now need to concentrate in your iq and logic thinking power \n" +
            " thts how you can improve \n" +
            "/*this is the comment line \n" +
            " this is the second line \n" +
            " this is also a comment " +
            " this is also a comment " +
            "this is also a comment R.drawable.logo*/" +
            "rapheal nadal" +
            "" +
            "//single line comment "+
            " tempt \n this shoule not";

    private static boolean doesContains(String fileContent,String imageName){
        boolean retFlag;
        String commentedLines = detectComments(fileContent);
        int occurenceComments = getOccurrence(commentedLines, imageName);
        int occurenceFile = getOccurrence(fileContent, imageName);
        System.out.println("Number of occurence in commented Lines are "+occurenceComments);
        System.out.println("Number of occurence in entire are "+occurenceFile);
        retFlag = validateImage(occurenceComments,occurenceFile);
        return retFlag;
    }

    private static boolean validateImage(int occurrenceComments,int occurrenceFile){
        boolean retFlag = false;
        if(occurrenceComments == occurrenceFile){
            retFlag = false; // image not used in the file
        }else if(occurrenceFile > occurrenceComments){
            retFlag = true;
        }else if(occurrenceFile > 0){
            retFlag =  true;
        }
        return retFlag;
    }


    private static int getOccurrence(String fileContent, String imageName){
        Pattern pat = Pattern.compile(imageName);
        Matcher mat = pat.matcher(fileContent);
        int occurrence = 0;
        while(mat.find()){
            occurrence++;
        }
        System.out.println("Occurence of "+imageName+" is "+occurrence);
        return occurrence;
    }
    private static final String COMMENT_PATTERN = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
    private static String detectComments(String fileContents){
        StringBuilder sb = new StringBuilder();
        System.out.println("$$ original string "+fileContents);
        System.out.println("*********************************");
        Pattern pat = Pattern.compile(COMMENT_PATTERN);//   /*.*/
        Matcher mat = pat.matcher(fileContents);
        while(mat.find()){
             System.out.println("$$"+mat.group());
            sb.append(mat.group());
        }
        sb = detectSingleLineComments(fileContents,sb);
        System.out.println("Total Comments: "+sb.toString());
        return sb.toString();
    }
    private static final String SINGLE_LINE_PATTERN = "//([^\\r\\n])+";
    private static StringBuilder detectSingleLineComments(String fileContents,StringBuilder sb){
        Pattern pat = Pattern.compile(SINGLE_LINE_PATTERN);
        Matcher mat = pat.matcher(fileContents);
        while (mat.find()){
//            System.out.println(mat.group());
            sb.append(mat.group());
        }
        return sb;
    }



    private void setAddition(){
        Set<Short> shorts = new HashSet<Short>();
        for(short i=0;i<100;i++){
            shorts.add(i);
        }
        System.out.println(shorts);
        for(int i=0;i<shorts.size();i++)
            shorts.remove(i);

        System.out.println(shorts);
    }

    private void countFiles(File androidProjectPath){
        for(File file: androidProjectPath.listFiles()){
//           System.out.println(file.getName());
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

    private static void checkForFileName(String fileContent,String imageName){
//        System.out.println(fileContent.contains(imageName));
    }

    private String splitFileName(String imageName){
        return imageName.split("\\.")[0];
    }




}
