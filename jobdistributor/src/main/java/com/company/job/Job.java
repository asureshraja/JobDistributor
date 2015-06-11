package com.company.job;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by suresh on 5/6/15.
 */
public class Job extends GeneralJob  implements Serializable{
    private int a=5;
    private int b=40;


    public Job(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Object doWork() {
        return a+b;
    }


}
