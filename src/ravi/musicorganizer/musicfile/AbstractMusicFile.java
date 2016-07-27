/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ravi.musicorganizer.musicfile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import sun.util.logging.PlatformLogger;

/**
 * Just gives a default implementation of getArtists..
 * 
 * @author ravi
 */
public abstract class AbstractMusicFile implements MusicFile {
   public static final String DEFAULT_DELIMITERS = "& ,";
   private String delimiter = DEFAULT_DELIMITERS;
   
   private Path filePath;
   
   public AbstractMusicFile(Path file)
   {
       filePath = file;
   }
   
   public void addDelimiters(String delimiters){
  
       delimiter = delimiter + delimiters;
   }
   
   public void addDelimiter(char delimiterCharacter){
       delimiter = delimiter + delimiterCharacter;
   }
           

   public void setDelimiters(String delimiters)
   {
       delimiter = delimiters;
   }
   
    @Override
   public List<String> getArtists()
   {
      String artists = getArtist();
      return Arrays.asList(artists.split("[" + delimiter + "]"));
   }
   
   public String getDecade()
   {
       String returnString = "";
   
       try {
        int year = Integer.parseInt(getYear());
        int decade = year / 10 * 10;
        returnString = String.valueOf(decade);
       }
       catch (NumberFormatException ex) {
           Logger.getLogger(getClass().getName()).warning("Cannot calculate Decade..");
       }
       finally {
           return returnString;
       }
   }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("**********************************************\n");
        builder.append("Song Title\t:\t").append(getTitle())
                .append('\n');
        builder.append("Album Title\t:\t").append(getAlbum())
                .append('\n');
        builder.append("Lead Artist\t:\t").append(getArtist())
                .append('\n');
        builder.append("Year Released\t:\t").append(getYear())
                .append('\n');
        builder.append("Genre     \t:\t").append(getGenre())
                .append('\n');
        builder.append("**********************************************\n");
        return builder.toString();
    }
    
    public Path getPath()
    {
        return filePath;
    }
    
     @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MusicFile)
        {
            MusicFile musicFile = (MusicFile) obj;
            return (musicFile.getAlbum().equals(getAlbum()) && 
                    musicFile.getArtist().equals(getArtist()) && 
                    musicFile.getArtists().equals(getArtists()) && 
                    musicFile.getGenre().equals(getGenre()) && 
                    musicFile.getTitle().equals(getTitle()) && 
                    musicFile.getYear().equals(getYear()));
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (getAlbum() != null ? getAlbum().hashCode() : 0);
        hash = 43 * hash + (getArtist() != null ? getArtist().hashCode() : 0);
        hash = 43 * hash + (getArtists() != null ? getArtists().hashCode() : 0);
        hash = 43 * hash + (getGenre() != null ? getGenre().hashCode() : 0);
        hash = 43 * hash + (getTitle() != null ? getTitle().hashCode() : 0);
        hash = 43 * hash + (getYear() != null ? getYear().hashCode() : 0);
        return hash;
    }

    protected String escapeString(String input)
    {
        char[] array = input.trim().toCharArray();
        StringBuilder buffer = new StringBuilder(array.length);
        for (int i = 0; i < array.length; i++)
        {
            if ((array[i] > ' ') && (array[i] < 127))
                buffer.append(array[i]);
            else if (array[i] == ' ') buffer.append('_');
        }
        return buffer.toString();
    }
}
