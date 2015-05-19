package com.bijesh;

import com.bijesh.models.AndroidProject;
import com.bijesh.test.FileAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by bijesh on 4/10/14.
 */
public class Optimize extends JFrame {

    private JFileChooser mJfcPath;
    private JPanel mMainPanel, mPanel, mClearPanel;
    private JTextField mJtfFilePath;
    private JButton mOpenButton;
    private JTextArea mJtaResults;
    private JScrollPane mJScrollPane;
    private JButton mBtnClearAll,mBtnClearBackup;

    public Optimize() {
        super("Optimize Android");
        setLookAndFeel();
        initUIComponents();

    }

    private void setLookAndFeel() {
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation(sd.width / 2 - 400 / 2, sd.height / 2 - 400 / 2);
//        md.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*Suggested Code*/
        try {

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
//                    System.out.println("CHOSEN THIS");
                    break;
                } else {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUIComponents() {

        mMainPanel = new JPanel(new BorderLayout());

        mPanel = new JPanel(new FlowLayout());
        mJtfFilePath = new JTextField(25);
        mOpenButton = new JButton("Open an Android Project's Manifest file");

        mPanel.add(mJtfFilePath);
        mPanel.add(mOpenButton);

        mJtaResults = new JTextArea();
        mJtaResults.setEditable(false);
        mJtaResults.setBackground(Color.DARK_GRAY);
        mJtaResults.setForeground(Color.ORANGE);

        mJScrollPane = new JScrollPane(mJtaResults);

        mClearPanel = new JPanel(new FlowLayout());
        mBtnClearAll = new JButton("Clear All Unused Images");
        mBtnClearBackup = new JButton("Clear and back up");
        mClearPanel.add(mBtnClearBackup);
        mClearPanel.add(mBtnClearAll);


        mOpenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mJfcPath = new JFileChooser();
                int option = mJfcPath.showOpenDialog(Optimize.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    AndroidProject.INS.androidProject = mJfcPath.getCurrentDirectory();
                    mJtfFilePath.setText(AndroidProject.INS.androidProject.getAbsolutePath());
                    process();
                } else {

                }

            }
        });

        mBtnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performFileAction(FileAction.DELETE);
            }
        });

        mBtnClearBackup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performFileAction(FileAction.BACKUPANDDELETE);
            }
        });


        mMainPanel.add(mPanel, BorderLayout.NORTH);
        mMainPanel.add(mJScrollPane, BorderLayout.CENTER);
        mMainPanel.add(mClearPanel, BorderLayout.SOUTH);
        this.add(mMainPanel);

    }

    private void performFileAction(FileAction fileAction) {
        if (AndroidProject.INS.imagePositions.size() > 0 && AndroidProject.INS.imageFilePath.size() > 0) {
            int count = 0;
            for (int i = 0; i < AndroidProject.INS.imagePositions.size(); i++) {
//            System.out.println(AndroidProject.INS.imageFilePath.get(AndroidProject.INS.imagePositions.get(i)));
//                mJtaResults.append(AndroidProject.INS.imageFilePath.get(AndroidProject.INS.imagePositions.get(i)) + "\n");
                File deleteFile = AndroidProject.INS.imageFilePath.get(AndroidProject.INS.imagePositions.get(i));
                if(fileAction.equals(FileAction.DELETE)){
                    deleteFile.delete();
                }else{
                    Utility.fileBackupAndDelete(deleteFile);
                }
                count++;
            }
            mJtaResults.append("\nDeleted " + count + " files...");
//            clearAndroidProject();
        } else {
            JOptionPane.showMessageDialog(null, "No files to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAndroidProject() {
        AndroidProject.INS.androidProject = null;
        AndroidProject.INS.androidManifest = null;
        AndroidProject.INS.resFolderPath = null;
        AndroidProject.INS.srcFolderPath = null;
        AndroidProject.INS.layoutFolderPath = null;
        AndroidProject.INS.backupFolderPath = null;
        AndroidProject.INS.fileCount = 0;
        AndroidProject.INS.imageFilePath = null;
        AndroidProject.INS.imageFileNames = null;
        AndroidProject.INS.imagePositions = null;
        AndroidProject.INS.imageDeleteFilePath = null;
        AndroidProject.INS.NOT_USED = 0;
    }

    /**
     * The method where the entire engine starts
     */
    private void process() {
        System.out.println("Scanning the project... please wait");
        initializeAllPath();
        persistImageNames();
//        initFileCount();
        filterLayouts();

        displayResults();
    }

    private void initFileCount() {
        FileCounter counter = new FileCounter();
        counter.countFiles();
//        System.out.println("Total java and xml files in the project: " + AndroidProject.INS.fileCount);
    }

    /**
     * Use this method to display results
     */
    private void displayResults() {

//        System.out.println("$$$ Results are " + AndroidProject.INS.imagePositions);
        mJtaResults.append("Files which would be deleted are :\n");
        mJtaResults.append("==========================\n\n");
        System.out.println("AndroidProject.INS.imagePositions.size() "+AndroidProject.INS.imagePositions.size());
        if (AndroidProject.INS.imagePositions != null && AndroidProject.INS.imagePositions.size() > 0) {
            System.out.println("in if block");
            for (int i = 0; i < AndroidProject.INS.imagePositions.size(); i++) {
//            System.out.println(AndroidProject.INS.imageFilePath.get(AndroidProject.INS.imagePositions.get(i)));
                mJtaResults.append(AndroidProject.INS.imageFilePath.get(AndroidProject.INS.imagePositions.get(i)) + "\n");
            }
        } else {
            System.out.println("in else part");
            mJtaResults.append("No unused images in this project...\n");
        }
    }

    /**
     * Use this method for checking whether any drawables are used in the layouts folder
     */
    private void filterLayouts() {
        new Filter().filterLayouts();
    }


    /**
     * This method will collect all the names of the images used in the drawable folder
     */
    private void persistImageNames() {
        PersistImages persist = new PersistImages();
        persist.traverseImageFolder();
    }

    /**
     * Use this method to initialize the path of  project's all folder (like res folder,src folder, layout folder)
     */
    private void initializeAllPath() {
        initializeManifest();
        initializeRes();
        initializeSrc();
        initializeLayout();
        intializeBackupFolder();
    }

    /**
     * intialize the backup folder this path will be uninitialized in clearAndroidProject()
     */
    private void intializeBackupFolder(){
        AndroidProject.INS.backupFolderPath = Utility.createBackupFolder(AndroidProject.INS.androidProject);
    }

    /**
     * initialize the layout folder
     * NOTE: in future it will keep scanning inside all the folder and find the layout folder rather than by hard code
     */
    private void initializeManifest() {
        AndroidProject.INS.androidManifest = new File(AndroidProject.INS.androidProject + "AndroidManifest.xml");
//        System.out.println("$$$ manifest file " + AndroidProject.INS.androidManifest);
    }

    /**
     * initialize the layout folder
     * NOTE: in future it will keep scanning inside all the folder and find the layout folder rather than by hard code
     */
    private void initializeLayout() {
        AndroidProject.INS.layoutFolderPath = new File(AndroidProject.INS.resFolderPath + File.separator + "layout");
//        System.out.println("$$$ layout Folder " + AndroidProject.INS.layoutFolderPath);
    }

    /**
     * initialize the src folder
     * NOTE: in future it will keep scanning inside all the folder and find the src folder rather than by hard code
     */
    private void initializeSrc() {
        AndroidProject.INS.srcFolderPath = new File(AndroidProject.INS.androidProject.getAbsolutePath() + File.separator + "src");
//        System.out.println("$$$ src Folder " + AndroidProject.INS.srcFolderPath);
    }

    /**
     * initialize the res folder
     * NOTE: in future it will keep scanning inside all the folder and find the res folder rather than by hard code
     */
    private void initializeRes() {
        AndroidProject.INS.resFolderPath = new File(AndroidProject.INS.androidProject.getAbsolutePath() + File.separator + "res");
//        System.out.println("$$$ res Folder " + AndroidProject.INS.resFolderPath);
    }


    public static void main(String... str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new Optimize();
                frame.setTitle("Optimize Android");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(new Dimension(600, 400));
                frame.setVisible(true);
            }
        });
    }
}
