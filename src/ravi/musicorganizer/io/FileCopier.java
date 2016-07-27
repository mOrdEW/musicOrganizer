/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingDeque;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ravi.musicorganizer.musicfile.MusicFile;

/**
 *  This class should maintain a list of files to be copied or symlinked and 
 * a separate thread will watch for the list and does the actual copying...
 * 
 * @author ravi
 */
public class FileCopier implements Runnable {

    public static final Logger LOGGER = Logger.getLogger(FileCopier.class.
            getName());
    
    public static final Pattern PATTERN = Pattern.compile("%\\w+?%");
    private BlockingDeque<MusicFile> musicFileList;
    private int poll = 0;
    public static final String FOLDER_DELIMITER = "/\\";
    
    static{
        try{
            FileHandler handler = new FileHandler("/tmp/consumer.txt");
            LOGGER.addHandler(handler);
        }
        catch(IOException ex){
            LOGGER.log(Level.WARNING, "Could not create custom handler", ex);
        }
        catch(SecurityException ex){
            LOGGER.log(Level.WARNING, "Could not create custom handler", ex);
        }
        
    }
    private final String fileFormat;
    private final Path target;
    
    public FileCopier(BlockingDeque<MusicFile> queue)
    {
        this(queue, 1);
    }
    
    public FileCopier(BlockingDeque<MusicFile> queue, int numberOfItems) {
        this(queue, numberOfItems, Paths.get("/tmp"), "");
        poll = numberOfItems;
    }
    
    public FileCopier(BlockingDeque<MusicFile> queue, int numberOfItems, 
            Path targetFolder, String targetFileFormat) {
        musicFileList = queue;
        poll = numberOfItems;
        target = targetFolder;
        fileFormat = targetFileFormat;
    }
    
    private Path generateTarget(MusicFile file){
        if(file == null) return null;
        Matcher matcher = PATTERN.matcher(fileFormat);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(buffer, getReplacement(file, 
                    matcher.group()));
        }
        matcher.appendTail(buffer);
        buffer.append(".mp3");
        
        return generateTargetPathFromString(buffer.toString());
            
    }
            
    
    @Override
    public void run() {
        while (poll > 0)
        {
            try {
                MusicFile file = musicFileList.takeFirst();
                if (file.equals(MusicFile.TERMINATOR)) poll--;
                else {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, 
                        " {0}\n{1}", new String[]{String.valueOf(poll), 
                         file.toString()});
                    Path targetPath = generateTarget(file);
                    if (Files.notExists(targetPath.getParent()))
                        Files.createDirectories(targetPath.getParent());
                    if (Files.notExists(targetPath))
                        Files.copy(file.getPath(), targetPath);
                    
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(FileCopier.class.getName())
                        .log(Level.SEVERE, null, ex);
                break;
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        "Could not create the file.", ex);
            }
        }
    }

    private String getReplacement(MusicFile file, String name) {
        String returnString = null;
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append("get");
        for(final String word:name.split("[\\s%]+?")){
            if(word.length() > 0) {
                methodBuilder.append(word.substring(0, 1).toUpperCase());
                methodBuilder.append(word.substring(1).toLowerCase());
            }
        }
        String methodName = methodBuilder.toString(); 
        
        try {
            Method getter = file.getClass().getMethod(methodName);
            
            returnString = getter.invoke(file).toString();
            if (returnString.length() == 0) 
                returnString = "Unknown-" + methodName.substring(3);
            
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (SecurityException ex) {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, 
                    ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, 
                    ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(FileCopier.class.getName()).log(Level.SEVERE, null, 
                    ex);
        }
        return returnString;
    }

    private Path generateTargetPathFromString(String toString) {
        Path path = target;
        StringTokenizer tokenizer = new StringTokenizer(toString, 
                FOLDER_DELIMITER);
        while (tokenizer.hasMoreTokens()){
            path = path.resolve(tokenizer.nextToken());
        }
        return path;
    }

}
