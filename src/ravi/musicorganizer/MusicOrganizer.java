/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.TagException;
import ravi.musicorganizer.io.FileCopier;
import ravi.musicorganizer.io.MusicFileRunnable;
import ravi.musicorganizer.musicfile.MusicFile;

/**
 *
 * @author ravi
 */
public class MusicOrganizer{
    private static void createOptions(Options opts) {
        opts.addOption("s", "src", true, "Source Directory");
        opts.addOption("t", "target", true, "Target Directory");
        opts.addOption("l", "symlink", false, "Try to create symlinks instead "
                + " of copying");
        opts.addOption("f", "format", true, "Format of the target file. Eg: "
                                    + "%genre%/%album%[%year%]/%title%");
        opts.addOption("n", "noOfFiles", true, "Number of files to be "
                + "transferred");
    }

    private Path sourcePath;
    private Path targetPath;
    private String targetFormat;
    
    private List<Path> filesSelected = new ArrayList<>();
    private BitSet alreadySelectedFiles;
    private boolean recurseFolders = false;
    
    private final BlockingDeque<MusicFile> deQueue 
            = new LinkedBlockingDeque<>();
    
    private int numberOfFiles = 100;
    private final Set<MusicFileRunnable> producerRegistry = new HashSet<>();
    
   public MusicOrganizer(Path source, Path target)
   {
       sourcePath = source;
       targetPath = target;
       
   }
   
   public MusicOrganizer(String source, String target)
   {
       this(Paths.get(source), Paths.get(target));
   }
   
   public MusicOrganizer(Path source, Path target, boolean recurse, String 
           format)
   {
       this(source, target);
       recurseFolders = recurse;
       targetFormat = format;
   }
   
   public MusicOrganizer(Path source, Path target, boolean recurse, String 
           format, int numberOfFiles) {
       this(source, target, recurse, format);
       this.numberOfFiles = numberOfFiles;
   }
   
   /**
    * Generates the total File list, initializes the Producer and Consumer
    * 
    * 
    */
   public void init()
   {
       collectMusicFiles(sourcePath);
       alreadySelectedFiles = new BitSet(filesSelected.size());
       FileCopier copier = new FileCopier(deQueue, Math.min(numberOfFiles, 
               filesSelected.size()), targetPath, targetFormat);
       Executor executor = Executors.newSingleThreadExecutor();
       executor.execute(copier);
       selectRandomFile(copier);
       
   }
        
    public void printID3(AbstractMP3Tag id, PrintStream writer)
    {
        if (id == null) return;
         writer.println("**********************************************");
         writer.println("Song Title\t:\t" + id.getSongTitle());
         writer.println("Album Title\t:\t" + id.getAlbumTitle());
         writer.println("Lead Artist\t:\t" + id.getLeadArtist());
         writer.println("Year Released\t:\t" + id.getYearReleased());
         writer.println("Tag Type\t:\t" + id.getClass().getSimpleName());
         writer.println("**********************************************");
    }
              
     /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.farng.mp3.TagException
     */
    public static void main(String[] args) throws IOException, TagException{
        Options opts = new Options();
        createOptions(opts);
        CommandLineParser parser;
        parser = new DefaultParser();
        try{
            CommandLine comLine = parser.parse(opts, args);
            Path srcPath = Paths.get("/media/sdb4/Music/KKNair/KKNairHindi/");
            Path targetPath = Paths.get("/tmp");
            String format = "%genre%/%album%[%year%]/%title%";
            int noOfFiles = 100;
        
        
            File srcDir = new File("/media/sdb4/Music/KKNair/KKNairHindi/");
            if (comLine.hasOption('s')){
                srcDir = new File(comLine.getOptionValue('s'));
                srcPath = Paths.get(comLine.getOptionValue('s'));
            }
        
            if (comLine.hasOption('t')) {
                targetPath = Paths.get(comLine.getOptionValue('t'));
            }

            if(comLine.hasOption('f')){
                format = comLine.getOptionValue('f');
            }

            if(comLine.hasOption('n')){
                noOfFiles = Integer.parseInt(comLine.getOptionValue('n'));
            }

            MusicOrganizer organizer = new MusicOrganizer(srcPath, targetPath, true,
                    format, noOfFiles);
            organizer.init();
        }
        catch (ParseException ex)
        {
            Logger.getLogger("MusicOrganizer").log(Level.SEVERE, ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("musicOrganizer", opts);
        }
        
    }
        

    /**
     * @return the sourcePath
     */
    public Path getSourcePath() {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the targetPath
     */
    public Path getTargetPath() {
        return targetPath;
    }

    /**
     * @param targetPath the targetPath to set
     */
    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    /**
     * @return the recurseFolders
     */
    public boolean isRecurseFolders() {
        return recurseFolders;
    }

    /**
     * @param recurseFolders the recurseFolders to set
     */
    public void setRecurseFolders(boolean recurseFolders) {
        this.recurseFolders = recurseFolders;
    }

    private void collectMusicFiles(Path sourcePath) {        
       DirectoryStream<Path> stream = null;
       try {
            stream = Files.newDirectoryStream(sourcePath);
            for(Path file : stream)
            {
                if(recurseFolders && Files.isDirectory(file))
                {
                    collectMusicFiles(file);
                }
                else if ((Files.probeContentType(file) != null) && 
                        Files.probeContentType(file).equals("audio/mpeg"))
                {
                    filesSelected.add(file);
                }
            }
       }
       catch (IOException ex)
       {
           Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
       }
       finally
       {
           if(stream != null)
           {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(MusicOrganizer.class.getName()).
                            log(Level.SEVERE, "When trying to close stream", ex);
                }
           }
       }
            
    }


    private void selectRandomFile(FileCopier copier) {
        int fileNumber = filesSelected.size();
        int filesRemaining = Math.min(fileNumber, numberOfFiles);
        Executor executor = Executors.newCachedThreadPool();
        //Executor executor = Executors.newSingleThreadExecutor();
        Random random = new Random();

        while (filesRemaining > 0)
        {
            int randomIndex = random.nextInt(fileNumber);
            
            if(!alreadySelectedFiles.get(randomIndex))
            {
                alreadySelectedFiles.set(randomIndex);
                filesRemaining--;
                Path randomFilePath = filesSelected.get(randomIndex);
                Logger.getLogger(getClass().getName()).log(Level.INFO, 
                        "File: {0}", randomFilePath);
                executor.execute(new MusicFileRunnable(randomFilePath, 
                        deQueue));
            }
        }
    }
    
    /** 
     * Registers a producer
     * @param producer The producer to be added.
     */
    public void registerProducer(MusicFileRunnable producer){
        producerRegistry.add(producer);
    }
    
    /**
     * Unregister a producer
     * @param producer The producer to be removed.
     */
     public void unregisterProducer(MusicFileRunnable producer) {
         producerRegistry.remove(producer);  
     }
     
     /**
      * Clears all producers
      */
     public void clearProducerRegistry(){
         producerRegistry.clear();
     }
             
}
