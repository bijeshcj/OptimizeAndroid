package com.bijesh;

import com.bijesh.models.AndroidProject;

/**
 * Created by bijesh on 4/10/14.
 */
public class PersistImages {
      public PersistImages(){
      }
      protected void traverseImageFolder(){
          Utility.traverse(AndroidProject.INS.resFolderPath,SearchTypes.drawable);
      }
}
