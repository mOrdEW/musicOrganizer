/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.jid3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import ravi.musicorganizer.musicfile.AbstractMusicFile;

/**
 *
 * @author ravi
 */
public class JavaID3MP3File extends AbstractMusicFile {
    
    private AbstractMP3Tag tag;
    
    public JavaID3MP3File(File file) throws IOException
    {
        this(file.toPath());
    }
    
    public JavaID3MP3File(Path musicFile) throws IOException, NullPointerException
    {
        super(musicFile);
        MP3File mp3File = null;
        try {
            mp3File = new MP3File(musicFile.toFile());
        } catch (TagException ex) {
            Logger.getLogger(JavaID3MP3File.class.getName()).log(Level.SEVERE, 
                    null, ex);
        }
        init(mp3File);
    }
    
    private void init(MP3File file)
    {
         if (file.hasID3v2Tag())
                tag = file.getID3v2Tag();
            else if (file.hasID3v1Tag())
                tag = file.getID3v1Tag();
            else if (file.hasFilenameTag())
                tag = file.getFilenameTag();
            else
            {
                Logger.getLogger(JavaID3MP3File.class.getName()).log(Level.SEVERE, 
                    "No known Tag Present..");
                return;
            }
    }

    @Override
    public String getTitle() {
        String returnString = UNKNOWN;
        try {
            returnString = escapeString(tag.getSongTitle());
        } catch(NullPointerException ex){
            Logger.getLogger(getClass().getName()).warning("No tag found.");
        } finally {
            return returnString;
        }
    }

    @Override
    public String getAlbum() {
        String returnString = UNKNOWN;
        try {
            returnString = escapeString(tag.getAlbumTitle());
        } catch(NullPointerException ex){
            Logger.getLogger(getClass().getName()).warning("No tag found.");
        } finally {
            return returnString;
        }
    }

    @Override
    public String getArtist() {
        String returnString = UNKNOWN;
        try {
            returnString = escapeString(tag.getLeadArtist());
        } catch(NullPointerException ex){
            Logger.getLogger(getClass().getName()).warning("No tag found.");
        } finally {
            return returnString;
        }
    }

    @Override
    public String getGenre() {
        String returnString = UNKNOWN;
        try {
            returnString = escapeString(tag.getSongGenre());
        } catch(NullPointerException ex){
            Logger.getLogger(getClass().getName()).warning("No tag found.");
        } finally {
            return returnString;
        }
    }

    @Override
    public String getYear() {
        String returnString = UNKNOWN;
        try {
            returnString = escapeString(tag.getYearReleased());
        } catch(NullPointerException ex){
            Logger.getLogger(getClass().getName()).warning("No tag found.");
        } finally {
            return returnString;
        }
    }
}
