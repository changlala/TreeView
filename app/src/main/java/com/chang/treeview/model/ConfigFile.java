package com.chang.treeview.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 树的配置文件 json格式
 *
 */
public class ConfigFile {

    public static String FILE_PATH;

    public static String getFilePath() {
        return FILE_PATH;
    }

    public static void setFilePath(String filePath) {
        FILE_PATH = filePath;
    }

    public static Tree getTreeFrom(String fileName) throws Exception{
        Tree tree = null;

        File f = new File(FILE_PATH+"/"+fileName);
        if(f.exists()){
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try{
                fis = new FileInputStream(f);
                ois = new ObjectInputStream(fis);
                tree = (Tree)ois.readObject();
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
                throw e;
            } finally {
                try{
                    if(fis != null)
                        fis.close();
                    if(ois != null)
                        ois.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }
        return tree;
    }

    public static Boolean makeConfigFile(Tree tree, String fileName){
        //创建dir
        File dir = new File(FILE_PATH);
        if(!dir.exists())
            dir.mkdir();

        //创建file
        File f = new File(FILE_PATH+"/"+fileName);
        try{
            if(!f.exists()){
                f.createNewFile();
            }
        }catch (IOException | SecurityException e){
            e.printStackTrace();
            return false;
        }

        //写入tree
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try{
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(tree);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }finally {
            try{
                if(fos != null)
                    fos.close();
                if(oos != null)
                    oos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return true;

    }
}
