/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.musicfile;

import java.nio.file.Path;
import java.util.List;

/**
 * This class is used for signal end of each producer of Music File.  This is 
 * used by the consumer to detect if it needs to continue to wait.
 * 
 * @author ravi
 */
public final class TerminatorMusicFile extends AbstractMusicFile{
    private static TerminatorMusicFile instance = new TerminatorMusicFile();
    
    private TerminatorMusicFile(){ 
        super(null); 
    }
    
    @Override
    public String getTitle() {
        return TERMINATOR_STRING;
    }

    @Override
    public String getAlbum() {
        return TERMINATOR_STRING;
    }

    @Override
    public String getArtist() {
        return TERMINATOR_STRING;
    }

    @Override
    public String getGenre() {
        return TERMINATOR_STRING;
    }

    @Override
    public String getYear() {
        return TERMINATOR_STRING;
    }

    @Override
    public List<String> getArtists() {
        return null;
    }

    @Override
    public Path getPath() {
        return null;
    }
    
    public static TerminatorMusicFile getInstance()
    {
        return instance;
    }
    
    /** 
     * Using the logic that there could be only one instance of Terminator 
     * 
     * @param obj
     * @return true if obj is same as instance
     */
    @Override
    public boolean equals(Object obj){
        return instance == obj;
    }
    
    @Override
    public String toString(){
        return TERMINATOR_STRING;
    }
    
   
}
