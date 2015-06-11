package com.company.job;

/**
 * Created by suresh on 11/6/15.
 */
public class StartSlave {
    public static void main(String[] args) {
        if(args.length==6){
            Slave s = new Slave(args[0],args[1],args[2],Integer.parseInt(args[3]),Integer.parseInt(args[4]),Integer.parseInt(args[5]));
            s.start();
        }
        else{
            System.out.println("following args are required in order: name-of-slave datafolder masterip port1 port2 port3 ");
        }

    }
}
