/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author alex
 */
public class SaveSar {

    public SaveSar(File in, File out) throws IOException 
    {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            // magic number for Windows, 64Mb - 32Kb)
           int maxCount = (64 * 1024 * 1024) - (32 * 1024);
           long size = inChannel.size();
           long position = 0;
           while (position < size) {
              position += inChannel.transferTo(position, maxCount, outChannel);
           }
        } 
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
}
