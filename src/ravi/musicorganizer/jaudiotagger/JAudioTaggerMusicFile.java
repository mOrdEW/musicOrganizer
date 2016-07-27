/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.jaudiotagger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import ravi.musicorganizer.musicfile.AbstractMusicFile;

/**
 *
 * @author rknair
 */
public class JAudioTaggerMusicFile extends AbstractMusicFile {
    
    private Tag tag;
    
    public JAudioTaggerMusicFile(Path musicFile){
        super(musicFile);
        try {
            AudioFile f = AudioFileIO.read(musicFile.toFile());
            tag = f.getTag();
        } catch (CannotReadException ex) {
            Logger.getLogger(JAudioTaggerMusicFile.class.getName())
                    .log(Level.SEVERE, musicFile.toString(), ex);
        } catch (IOException ex) {
            Logger.getLogger(JAudioTaggerMusicFile.class.getName())
                    .log(Level.SEVERE, musicFile.toString(), ex);
        } catch (TagException ex) {
            Logger.getLogger(JAudioTaggerMusicFile.class.getName())
                    .log(Level.SEVERE, musicFile.toString(), ex);
        } catch (ReadOnlyFileException ex) {
            Logger.getLogger(JAudioTaggerMusicFile.class.getName())
                    .log(Level.SEVERE, musicFile.toString(), ex);
        } catch (InvalidAudioFrameException ex) {
            Logger.getLogger(JAudioTaggerMusicFile.class.getName())
                    .log(Level.SEVERE, musicFile.toString(), ex);
        }
        
    }

    @Override
    public String getTitle() {
        String returnString = UNKNOWN;
        try {
            returnString = escapeString(tag.getFirst(FieldKey.TITLE));
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
            returnString = escapeString(tag.getFirst(FieldKey.ALBUM));
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
            returnString = escapeString(tag.getFirst(FieldKey.ARTIST));
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
            returnString = escapeString(tag.getFirst(FieldKey.GENRE));
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
            returnString = escapeString(tag.getFirst(FieldKey.YEAR));
        } catch(NullPointerException ex){
            Logger.getLogger(getClass().getName()).warning("No tag found.");
        } finally {
            return returnString;
        }
    }
    
}
