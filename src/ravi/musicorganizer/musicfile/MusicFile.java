/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.musicfile;

import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author ravi
 */
public interface MusicFile {
    public static final String TERMINATOR_STRING = "*** T E R M I N A T O R ***";
    public static final MusicFile TERMINATOR = TerminatorMusicFile.getInstance();
    public static final String UNKNOWN = "Unknown";
    
    public String getTitle();
    public String getAlbum();
    public String getArtist();
    public List<String> getArtists();
    public String getGenre();
    public String getYear();
    public String getDecade();
    @Override
    public String toString();
    public Path getPath();
}
