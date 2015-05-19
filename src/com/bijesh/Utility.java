package com.bijesh;

import com.bijesh.models.AndroidProject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by bijesh on 4/10/14.
 */
public class Utility {

    /**
     * Use this method to take a back up of file. This method will create a backup folder in the project root folder
     * and then will move the resource files and then delete those files from the project's resource folder.
     * @param file
     */
    protected static void fileBackupAndDelete(File file){

//        System.out.println("$$$ backup folder "+AndroidProject.INS.backupFolderPath);
//        System.out.println("$$$ resource file "+file);
        String folderName = getInnerBackupFolderName(file);
        File backUpInnerFolder = createInnerBackupFolder(AndroidProject.INS.backupFolderPath, folderName);
        try {
            FileUtils.copyFileToDirectory(file, backUpInnerFolder, true);
        }catch (IOException e){
            e.printStackTrace();
        }
        file.delete();
    }

    private static File createInnerBackupFolder(File backupFolderPath,String folderName){
        File backUpFolder = new File(backupFolderPath.toString()+File.separator+folderName);
        if(backUpFolder.mkdir()){
            System.out.println(folderName+" created successfully");
        }else{
            System.out.println(folderName+" already created");
        }
        return backUpFolder;
    }

    private static String getInnerBackupFolderName(File resourceFile){
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

    public static File createBackupFolder(File androidProject){
        File backUpFolder = new File(androidProject.toString()+File.separator+"unusedResourceBackups");
        if(backUpFolder.mkdir()){
            System.out.println("Backup folder created succdesfully");
        }else{
            System.out.println("Backup folder already exists");
        }
        return backUpFolder;
    }

    /**
     * Use this method to traverse through file system and then collect file info's
     *
     * @param path
     * @param searchTypes
     */
    protected static void traverse(File path,SearchTypes searchTypes){
        System.out.println("$$$ begin traverse");
        if(path != null){
           if(searchTypes.equals(SearchTypes.drawable)){
               System.out.println("$$$ path "+path);
               File[] files = path.listFiles();
               for(File file:files){
                   System.out.println("$$$ filePath "+file);
                   if(file.getName().startsWith(searchTypes.toString())){
                       if(file.isDirectory()){
                           File[] picFiles = file.listFiles();
                           for(File picFile:picFiles){
                               if(picFile.getName().endsWith(FileTypes.jpg.toString()) || picFile.getName().endsWith(FileTypes.png.toString())){
                                   System.out.println("$$$ file "+picFile.getName());
                                   AndroidProject.INS.imageFilePath.add(picFile);
                                   AndroidProject.INS.imageFileNames.add(splitFileName(picFile.getName()));

                               }
                           }
                       }
                   }
               }
//               System.out.println("$$$ imageList size "+AndroidProject.INS.imageFilePath.size());
//               System.out.println("$$$ imagenames  "+AndroidProject.INS.imageFileNames);
           }
        }
    }

    /**
     * this method will toggle between the folder which has to be searched
     * @param searchTypes
     */
    protected static void scanForImageFiles(SearchTypes searchTypes){
          switch (searchTypes){
              case project:
                  scan(AndroidProject.INS.androidProject);
                  break;
          }
    }

    /**
     * This method will scan all the files in the particular folder based on the file type
     * @param path
     */
    private static void scan(File path){
        System.out.println("Image Iteration started...");
        if(path != null){
            for(int iter=0;iter<AndroidProject.INS.imageFileNames.size();iter++){ //Image Iter
                int count = 0;
//                System.out.println("Searching for this image "+AndroidProject.INS.imageFileNames.get(iter));
                File[] files = path.listFiles();
                for(File file:files){                                    // File Iter
//                    System.out.println("$$$ iterating each file "+file+" count is "+count);
                    if(dontSearchThese(file.getName()))
                    {
//                        deliberate block
                        continue; // skipping this iteration
                    }else{
                        if(file.isDirectory()){
                             count = isPathDirectory(file.listFiles(),count,iter);
                        }else
                           count = detectFileAndStartScan(file,count,iter);
                    }
//                    System.out.println("##### count "+count);
                    if(count > 0){
//                        System.out.println("breaking the file loop as the image has been use in the project");
                        break;
                    }

                }
//                System.out.println("$$$ count "+count);
                if(count == AndroidProject.INS.NOT_USED){
                    AndroidProject.INS.imagePositions.add(iter);
                }
            }
        }
    }



    private static int isPathDirectory(File[] path,int count,int iter){
       for(File file:path){
           if(file.isDirectory()){
               count = isPathDirectory(file.listFiles(),count,iter);
               if(count > AndroidProject.INS.NOT_USED){
                   return count;
               }
           }else{
               count = detectFileAndStartScan(file,count,iter);
           }
       }
//        System.out.println("before isPathDirectory count "+count);
        return count;
    }

    private static int detectFileAndStartScan(File path,int count,int iter){
//        for(File file:path.listFiles()){
            if(path.isDirectory()){
//                System.out.println("is Directory: "+path.getAbsolutePath());
                detectFileAndStartScan(path,count,iter);
            }else if(path.isFile()){
//                System.out.println("is file: "+path.getAbsolutePath());
                if(path.getName().endsWith(FileTypes.java.toString()) || path.getName().endsWith(FileTypes.xml.toString())){
                    count = scanEngine(path,count,iter);

                }
            }
//        }
//        System.out.println("before detectFileAndStartScan count is "+count);
        return count;
    }

    private static int scanEngine(File file,int count,int iter){
        try{
            System.out.println("$$$ scanEngine "+file);
            String fileContent = FileUtils.readFileToString(file);
//                        System.out.println(fileContent);
            boolean isUsed = checkForFileName(getFileType(file),fileContent,AndroidProject.INS.imageFileNames.get(iter));
            if(isUsed){
//                System.out.println("$$$ is Used : "+AndroidProject.INS.imageFileNames.get(iter));
                count++;
            }else{
//                System.out.println("$$$ not used : "+AndroidProject.INS.imageFileNames.get(iter));
                return count;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
//        System.out.println("before scanEngine return count "+count);
        return count;
    }

    private static FileTypes getFileType(File file){
        String fileName = file.getName();
//        System.out.println("$$$ fileName "+fileName);
        if(fileName.endsWith(FileTypes.java.toString())){
            return FileTypes.java;
        }else if(fileName.endsWith(FileTypes.xml.toString())){
            return FileTypes.xml;
        }
        return FileTypes.not_bothered;
    }
    private static final String JAVA_SEARCH = "R.drawable.";
    private static final String XML_SEARCH = "@drawable/";
    private static boolean dontSearchThese(String name){
        boolean retFlag = false;
        if(name.startsWith(".")){
            return true;
        }
        for(NonSearchTypes type:NonSearchTypes.values()){
            if(name.equals(type.toString()))
                return true;
        }
        return retFlag;
    }

//    /**   NOTE: commented on 13/apr/2014
//     * This method will scan all the files in the particular folder based on the file type
//     * @param path
//     */
//    private static void scan(File path,FileTypes fileType){
//        if(path != null){
//            for(int i=0;i<AndroidProject.INS.imageFileNames.size();i++){ //Image Iter
//                int count = 0;
//                File[] files = path.listFiles();
//                for(File file:files){                                    // File Iter
//                    try{
//                        String fileContent = FileUtils.readFileToString(file);
////                        System.out.println(fileContent);
//                        boolean isUsed = checkForFileName(fileContent,AndroidProject.INS.imageFileNames.get(i));
//                        if(isUsed){
//                            System.out.println("$$$ is Used : "+AndroidProject.INS.imageFileNames.get(i));
//                             break;
//                       }else{
//                           System.out.println("$$$ not used : "+AndroidProject.INS.imageFileNames.get(i));
//                           count++;
//                       }
//                    }catch(IOException e){
//                        e.printStackTrace();
//                    }
//                }
//                System.out.println("$$$ count "+count);
//                if(count == files.length){
//                    AndroidProject.INS.imagePositions.add(i);
//                }
//            }
//        }
//    }
//    NOTE: commented on 11/apr/2014
//    private static void scan(File path,FileTypes fileType){
//         if(path != null){
////          scanning the xml files from the layout folder
//          if(fileType.equals(FileTypes.xml)){
//              File[] files = path.listFiles();
//              for(File file:files){
//                  System.out.println(file.getName());
//                  try{
//                   String fileContent = FileUtils.readFileToString(file);
//                   System.out.println(fileContent);
//                   for(int i=0;i<AndroidProject.INS.imageFileNames.size();i++){
//                       boolean isUsed = checkForFileName(fileContent,AndroidProject.INS.imageFileNames.get(i));
//                       if(isUsed){
////                           deliberate block
//                       }else{
//                           System.out.println("$$$ images not used  "+AndroidProject.INS.imageFileNames.get(i)+" position "+i);
//                           AndroidProject.INS.imagePositions.add(i);
//                       }
//                   }
//                  }catch (IOException e){
//                      e.printStackTrace();
//                  }
//              }
//          }
//         }else{
//             System.err.println("Fatal Error: Layout path is not initialized");
//         }
//    }

    private static boolean checkForFileName(FileTypes fileType,String fileContent,String imageName){
        boolean retFlag = false;
         switch (fileType){
             case java:
                 retFlag = doesContains(fileContent,imageName);//fileContent.contains(JAVA_SEARCH+imageName);
                 break;
             case xml:
                 retFlag = fileContent.contains(XML_SEARCH+imageName);
                 break;
         }
         return retFlag;
    }

    /**
     * This method will return the usage of the image file based on commented lines as well
     * @return boolean
     */
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

    private static final String COMMENT_PATTERN = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
    private static String detectComments(String fileContents){
        StringBuilder sb = new StringBuilder();
//        System.out.println("$$ original string "+fileContents);
//        System.out.println("*********************************");
        Pattern pat = Pattern.compile(COMMENT_PATTERN);//   /*.*/
        Matcher mat = pat.matcher(fileContents);
        while(mat.find()){
//             System.out.println("$$"+mat.group());
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

    /**
     * This method will get the name actual name of the image file without the file extension
     * @param imageName
     * @return
     */
    private static String splitFileName(String imageName){
        return imageName.split("\\.")[0];
    }

}
