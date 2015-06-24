package com.company.kyotoapi;

import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.factory.KyotoDbBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/*
kyoto database structure key
tablesnames=,,,,
listsnames=,,,,
tablesCounts=
listsCounts=
list-listname-inserted=
list-listname-removed=
table-tablename-colcounts=
table-tablename-inserted=
table-tablename-removed=
..
..
table-tablename-1-1
*/
/**
 * Created by suresh on 8/6/15.
 */
public class KyotoSystem {
    public String[] getPermTablesNames(){
        return permkyotoDb.get("tablesnames").split(",");
    }
    public String[] getPermListsNames(){
        return permkyotoDb.get("listsnames").split(",");
    }
    public String[] getTempListsNames(){
        return tempkyotoDb.get("listsnames").split(",");
    }
    public String[] getTempTablesNames(){
        return tempkyotoDb.get("tablesnames").split(",");
    }

    public String[] getTempTablesCounts(){
        return tempkyotoDb.get("tablescounts").split(",");

    }
    public String[] getPermTablesCounts(){
        return permkyotoDb.get("tablescounts").split(",");
    }

    public String[] getPermListsCounts(){
        return permkyotoDb.get("listscounts").split(",");
    }
    public String[] getTempListsCounts() {
        return tempkyotoDb.get("listscounts").split(",");
    }

    public void createPermTable(String tableName,long noOfColumns){

        for (String s:getPermTablesNames()){
            if(s.equals(tableName)){
                System.out.println("Table Already Exists!");
                return;
            }
        }
        permkyotoDb.increment("tablescounts",1l); //modifying tablescount for new table

        //modifying tables names list
        if(permkyotoDb.get("tablesnames")==null){
            permkyotoDb.set("tablesnames",tableName);
        }else{
            String temp = permkyotoDb.get("tablesnames");
            permkyotoDb.set("tablesnames",temp+","+tableName);
        }

        permkyotoDb.set("table-"+tableName+"-colcounts",noOfColumns);
        permkyotoDb.set("table-"+tableName+"-inserted",0l);
        permkyotoDb.set("table-"+tableName+"-removed",0l);
    }

    public long getPermTableCount(String tableName){
        return permkyotoDb.getLong("table-"+tableName+"-inserted")-permkyotoDb.getLong("table-"+tableName+"-removed");
    }
    public long getTempTableCount(String tableName){
        return tempkyotoDb.getLong("table-"+tableName+"-inserted")-tempkyotoDb.getLong("table-"+tableName+"-removed");
    }

    public void createTempTable(String tableName,long noOfColumns){

        for (String s:getPermTablesNames()){
            if(s.equals(tableName)){
                System.out.println("Table Already Exists!");
                return;
            }
        }
        tempkyotoDb.increment("tablescounts",1l); //modifying tablescount for new table

        //modifying tables names list
        if(tempkyotoDb.get("tablesnames")==null){
            tempkyotoDb.set("tablesnames",tableName);
        }else{
            String temp = tempkyotoDb.get("tablesnames");
            tempkyotoDb.set("tablesnames",temp+","+tableName);
        }

        tempkyotoDb.set("table-"+tableName+"-colcounts",noOfColumns);
        tempkyotoDb.set("table-"+tableName+"-inserted",0l);
        tempkyotoDb.set("table-"+tableName+"-removed",0l);
    }
    public boolean deletePermTable(String tableName){
        String tempchk = permkyotoDb.get("table-" + tableName + "-colcounts");
        if(tempchk !=null){
            if(permkyotoDb.get("tablesnames")==null){
                //do nothing
            }else{
                String temp = permkyotoDb.get("tablesnames");
                permkyotoDb.set("tablesnames",temp.replace(","+tableName,""));
            }
            permkyotoDb.increment("tablescounts", -1l);
            permkyotoDb.remove("table-" + tableName + "-colcounts");
            permkyotoDb.remove("table-" + tableName + "-inserted");
            permkyotoDb.remove("table-" + tableName + "-removed");
            return true;
        }else{
            return false;
        }
    }
    public boolean deleteTempTable(String tableName){
        String tempchk = tempkyotoDb.get("table-" + tableName + "-colcounts");
        if(tempchk !=null){
            if(tempkyotoDb.get("tablesnames")==null){
                //do nothing
            }else{
                String temp = tempkyotoDb.get("tablesnames");
                tempkyotoDb.set("tablesnames", temp.replace("," + tableName, ""));
            }
            tempkyotoDb.increment("tablescounts", -1l);
            tempkyotoDb.remove("table-" + tableName + "-colcounts");
            tempkyotoDb.remove("table-" + tableName + "-inserted");
            tempkyotoDb.remove("table-" + tableName + "-removed");
            return true;
        }else{
            return false;
        }
    }
    private boolean insertIntoPermTable(String tableName,long column,String value){
        String temp = permkyotoDb.get("table-" + tableName + "-colcounts");
        if(temp!=null){
            if(Long.parseLong(temp)<=column && Long.parseLong(temp)>0 && column>0){
                permkyotoDb.set("table-" + tableName + "-" + String.valueOf(permkyotoDb.getLong("table-" + tableName + "-inserted") + 1) + "-" + String.valueOf(column), value);

            }
            else{
                System.out.println("error with column");
            }
        }else{
            System.out.println("no table found");
        }
        return true;
    }
    private boolean insertIntoTempTable(String tableName,long column,String value){
        String temp = tempkyotoDb.get("table-" + tableName + "-colcounts");
        if(temp!=null){
            if(Long.parseLong(temp)<=column && Long.parseLong(temp)>0 && column>0){
                tempkyotoDb.set("table-" + tableName + "-" + String.valueOf(tempkyotoDb.getLong("table-" + tableName + "-inserted") + 1) + "-" + String.valueOf(column), value);

            }
            else{
                System.out.println("error with column");
                return false;
            }
        }else{
            System.out.println("no table found");
            return false;
        }
        return true;
    }
    private boolean updatePermTable(String tableName,long column,long rowId,String value){
        String temp = permkyotoDb.get("table-" + tableName + "-"+String.valueOf(rowId)+"-"+String.valueOf(column));
        if(temp==null){
            System.out.println("no such record found");
            return false;
        }else{
            temp = permkyotoDb.get("table-" + tableName + "-colcounts");
            if(temp!=null){
                if(Long.parseLong(temp)<=column && Long.parseLong(temp)>0 && column>0){
                    permkyotoDb.set("table-" + tableName + "-"+String.valueOf(rowId)+"-"+String.valueOf(column),value);

                }
                else{
                    System.out.println("error with column");
                    return false;
                }
            }else{
                System.out.println("no table found");
                return false;
            }
            return true;
        }

    }
    private boolean updateTempTable(String tableName,long column,long rowId,String value){
        String temp = tempkyotoDb.get("table-" + tableName + "-"+String.valueOf(rowId)+"-"+String.valueOf(column));
        if(temp==null){
            System.out.println("no such record found");
            return false;
        }else{
            temp = tempkyotoDb.get("table-" + tableName + "-colcounts");
            if(temp!=null){
                if(Long.parseLong(temp)<=column && Long.parseLong(temp)>0 && column>0){
                    tempkyotoDb.set("table-" + tableName + "-"+String.valueOf(rowId)+"-"+String.valueOf(column),value);

                }
                else{
                    System.out.println("error with column");
                    return false;
                }
            }else{
                System.out.println("no table found");
                return false;
            }
            return true;
        }

    }
    public boolean insertIntoTempTable(String tablename,String commaSeparatedRecord){
        long col=1l;
        for(String value:commaSeparatedRecord.split(",")){
            if(!insertIntoTempTable(tablename, col, value)){
                return false;
            }
            col = col +1;
        }
        tempkyotoDb.increment("table-"+tablename+"-inserted",1l);
        return true;
    }

    public boolean insertIntoPermTable(String tablename,String commaSeparatedRecord){
        long col=1l;
        for(String value:commaSeparatedRecord.split(",")){
            if(!insertIntoPermTable(tablename, col, value)){
                return false;
            }
            col = col +1;
        }
        permkyotoDb.increment("table-" + tablename + "-inserted", 1l);
        return true;
    }

    public boolean deleteTempTableRecord(String tablename,long rowId){
        String temp = tempkyotoDb.get("table-" + tablename + "-colcounts");
        if(temp==null){
            System.out.println("table not exists");
            return false;
        }
        for (int i = 1; i <=Long.parseLong(temp) ; i++) {
            tempkyotoDb.remove("table-" + tablename + "-" + String.valueOf(rowId)+"-"+i);
        }
        tempkyotoDb.increment("table-" + tablename + "-removed",1l);
        return true;
    }
    public boolean deletePermTableRecord(String tablename,long rowId){
        String temp = permkyotoDb.get("table-" + tablename + "-colcounts");
        if(temp==null){
            System.out.println("table not exists");
            return false;
        }
        for (int i = 1; i <=Long.parseLong(temp) ; i++) {
            permkyotoDb.remove("table-" + tablename + "-" + String.valueOf(rowId)+"-"+i);
        }
        permkyotoDb.increment("table-" + tablename + "-removed",1l);
        return true;
    }
    public ArrayList<String> getPermTableRecord(String tablename,long rowId){
        String temp = permkyotoDb.get("table-" + tablename + "-colcounts");
        if(temp==null){
            System.out.println("table not exists");
            return null;
        }
        ArrayList<String> recordToReturn = new ArrayList<String>();
        for (int i = 1; i <=Long.parseLong(temp) ; i++) {
            temp = permkyotoDb.get("table-" + tablename + "-" + String.valueOf(rowId) + "-" + String.valueOf(i));
            if (temp == null) {
                System.out.println("no such record found");
                return null;
            } else {
                recordToReturn.add(temp);
            }
        }
        return recordToReturn;
    }
    public ArrayList<String> getTempTableRecord(String tablename,long rowId){
        String temp = tempkyotoDb.get("table-" + tablename + "-colcounts");
        if(temp==null){
            System.out.println("table not exists");
            return null;
        }
        ArrayList<String> recordToReturn = new ArrayList<String>();
        for (int i = 1; i <=Long.parseLong(temp) ; i++) {
            temp = tempkyotoDb.get("table-" + tablename + "-" + String.valueOf(rowId) + "-" + String.valueOf(i));
            if (temp == null) {
                System.out.println("no such record found");
                return null;
            } else {
                recordToReturn.add(temp);
            }
        }
        return recordToReturn;
    }
    public String getPermTableRecord(String tablename,long rowId,long column){
        String temp = permkyotoDb.get("table-" + tablename + "-"+String.valueOf(rowId)+"-"+String.valueOf(column));
        if(temp==null) {
            System.out.println("no such record found");
            return null;
        }else{
            return temp;
        }
    }
    public String getTempTableRecord(String tablename,long rowId,long column){
        String temp = tempkyotoDb.get("table-" + tablename + "-"+String.valueOf(rowId)+"-"+String.valueOf(column));
        if(temp==null) {
            System.out.println("no such record found");
            return null;
        }else{
            return temp;
        }
    }

    public KyotoSystem(String dataFolder) {
        tempdatas = new File(dataFolder+"/TEMP.kch");
        permdatas = new File(dataFolder+"/PERM.kch");
        if(!tempdatas.exists()){
            try {
                tempdatas.getParentFile().mkdir();
                tempdatas.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!permdatas .exists()){
            try {
                permdatas.getParentFile().mkdir();
                permdatas.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tempkyotoDb  = new KyotoDbBuilder(tempdatas).build();

        permkyotoDb  = new KyotoDbBuilder(permdatas).build();
    }

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


}

