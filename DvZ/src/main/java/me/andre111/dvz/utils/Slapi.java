package me.andre111.dvz.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Slapi
{
    public static void save(Object obj, String path) throws Exception
    {
    	//exist check
    	File f = (new File(path)).getParentFile();
    	if(!f.exists()) f.mkdirs();
    	
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
        oos.writeObject(obj);
        oos.flush();
        oos.close();
    }
    public static Object load(String path) throws Exception
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        Object result = ois.readObject();
        ois.close();
        return result;
    }
}