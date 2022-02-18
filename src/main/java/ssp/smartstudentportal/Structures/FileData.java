package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class FileData implements Serializable {
    String fileExtension;
    String name;
    byte[] file;
    public FileData(String fileExtension, byte[] file){
        this.fileExtension = fileExtension;
        this.file = file;
    }
    public FileData(String fileExtension, byte[] file,String name){
        this.fileExtension = fileExtension;
        this.file = file;
        this.name=name;
    }
    public FileData(){};
    public String getFileExtension(){
        return fileExtension;
    }
    public byte[] getFile(){
        return file;
    }

    public String getName() {
        return this.name;
    }
}
