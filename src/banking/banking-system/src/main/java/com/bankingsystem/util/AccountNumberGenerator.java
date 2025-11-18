
package com.bankingsystem.util;

import java.util.Random;

public class AccountNumberGenerator {
    public static String generate(String name){
        String i = name==null? "AC": name.toUpperCase().replaceAll("[^A-Z]","").substring(0, Math.min(3,name.length()));
        return i + (1000 + new Random().nextInt(9000));
    }
}
