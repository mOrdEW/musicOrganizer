/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingDeque;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import ravi.musicorganizer.jaudiotagger.JAudioTaggerMusicFile;
import ravi.musicorganizer.musicfile.MusicFile;

/**
 *Creates MusicFile out of filePath in a separate thread..
 * 
 * @author ravi
 */
public class MusicFileRunnable extends Observable implements Runnable {
    
    private final Path filePath;
    private final BlockingDeque<MusicFile> deQueue; 
    
    public static final Logger LOGGER = Logger.getLogger(MusicFileRunnable.class.
            getName());
    
    
    static{
        try{
            FileHandler handler = new FileHandler("/tmp/producer.txt");
            LOGGER.addHandler(handler);
        }
        catch(IOException | SecurityException ex){
            LOGGER.log(Level.WARNING, "Could not create custom handler", ex);
        }
    }
            
    
    public MusicFileRunnable(Path file, BlockingDeque<MusicFile> deque, 
            Observer o)
    {
        super();
        filePath = file;
        deQueue = deque;
        addObserver(o);
    }

    @Override
    public void run() {
        try 
        {
            MusicFile file = new JAudioTaggerMusicFile(filePath);
            deQueue.addLast(file);
            LOGGER.log(Level.INFO, "Added..\n{0}", file);
        }
        catch(Exception io)
        {
            LOGGER.log(Level.SEVERE, "Could not parse :: " + filePath, io);
        }
        finally
        {
            setChanged();
            notifyObservers();
        }
    }
    
}
