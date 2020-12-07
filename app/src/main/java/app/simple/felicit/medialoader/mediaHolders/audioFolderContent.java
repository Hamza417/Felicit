package app.simple.felicit.medialoader.mediaHolders;

import java.util.ArrayList;

public class audioFolderContent {

    private ArrayList<AudioContent> audioFiles;
    private String folderName;
    private String folderPath;
    private int bucket_id;

    public audioFolderContent(){
        audioFiles = new ArrayList<>();
    }

    public audioFolderContent(String folderName, String folderPath) {
        this.folderName = folderName;
        this.folderPath = folderPath;
        audioFiles = new ArrayList<>();
    }

    public ArrayList<AudioContent> getMusicFiles() {
        return audioFiles;
    }

    public void setMusicFiles(ArrayList<AudioContent> audioFiles) {
        this.audioFiles = audioFiles;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getNumberOfSongs() {
        return audioFiles.size();
    }

    public int getBucket_id() {
        return bucket_id;
    }

    public void setBucket_id(int bucket_id) {
        this.bucket_id = bucket_id;
    }
}
