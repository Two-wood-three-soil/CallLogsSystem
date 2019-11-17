//package com.project.callloggengenmodule;
package com.project.callloggengenmodule;

import java.util.Calendar;
import java.util.Random;

public class test {


    public static void main(String[] args) {
        Random r = new Random();
        int year = r.nextInt(2);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        System.out.println(calendar);
    }
}
