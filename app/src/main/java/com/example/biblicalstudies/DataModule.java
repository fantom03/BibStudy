package com.example.biblicalstudies;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DataModule implements Serializable {

    private final int SERIAL_ID = 254482;

    private String classNAME;
    private HashMap<String, ArrayList<LESSON> > classes;

    public DataModule(String classNAME, HashMap<String, ArrayList<LESSON>> classes) {
        this.classNAME = classNAME;
        this.classes = classes;
    }
}

final class LESSON implements Serializable{

    private final int SERIAL_ID = 254483;

    private String lessonNAME;
    private HashMap<String, ArrayList< Pair< ArrayList<AUD>, ArrayList<DOC> > > > data;

    public LESSON(String lessonNAME) {
        this.lessonNAME = lessonNAME;
        this.data = new HashMap<>();
    }

    public HashMap<String, ArrayList< Pair< ArrayList<AUD>, ArrayList<DOC> > > > getMAP(){
        return data;
    }

}

final class AUD implements Serializable{

    private final int SERIAL_ID = 254484;

    private String audTITLE;
    private String audTIME;
    private String audURL;
    private String audDESC;

    public AUD(String audTITLE, String audTIME, String audURL, String audDESC) {
        this.audTITLE = audTITLE;
        this.audTIME = audTIME;
        this.audURL = audURL;
        this.audDESC = audDESC;
    }

    public String getAudTITLE() {
        return audTITLE;
    }

    public String getAudTIME() {
        return audTIME;
    }

    public String getAudURL() {
        return audURL;
    }

    public String getAudDESC() {
        return audDESC;
    }
}

final class DOC implements Serializable{

    private final int SERIAL_ID = 254485;

    private String docTITLE;
    private String docURL;
    private String docDESC;

    public DOC(String docTITLE, String docURL, String docDESC) {
        this.docTITLE = docTITLE;
        this.docURL = docURL;
        this.docDESC = docDESC;
    }

    public String getDocTITLE() {
        return docTITLE;
    }

    public String getDocURL() {
        return docURL;
    }

    public String getDocDESC() {
        return docDESC;
    }
}
