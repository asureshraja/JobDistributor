package com.company.datatypes;

import fm.last.commons.kyoto.DbType;
import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.factory.KyotoDbBuilder;
import fm.last.commons.kyoto.factory.Mode;
import kyotocabinet.DB;

import java.io.File;
import java.util.EnumSet;

/**
 * Created by suresh on 8/6/15.
 */
public class KyotoSystem {
    private File tempdatas;
    private File permdatas;

    public KyotoDb getPermkyotoDb() {
        return permkyotoDb;
    }

    public KyotoDb getTempkyotoDb() {
        return tempkyotoDb;
    }


    private KyotoDb tempkyotoDb;
    private KyotoDb permkyotoDb;
    public void init(String dataFolder){
        tempdatas = new File(dataFolder+"/TEMP.kch");
        permdatas = new File(dataFolder+"/PERM.kch");
        tempkyotoDb  = new KyotoDbBuilder(tempdatas).build();
        permkyotoDb  = new KyotoDbBuilder(permdatas).build();
    }

}
