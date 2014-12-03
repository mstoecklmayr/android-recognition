package com.uclan.mstocklmayr.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by mike on 11/24/14.
 */
public class RandomId {
    private Map<String,Integer> idMap;

    Random r = new Random();
    int Low = 1000;
    int High = 7000;

    //generates a map with random ids for the input elements in the add contact activity
    public RandomId(){
        idMap = new HashMap<String, Integer>();
        for(int i = 1; i<50;i++){
            int random = r.nextInt(High-Low) + Low;
            idMap.put("OTHER"+i,random);
        }
    }

    public int getIdFromKey(String key){
        return this.idMap.get(key);
    }

    public Set<Map.Entry<String,Integer>> getEntries(){
        return this.idMap.entrySet();
    }
}
