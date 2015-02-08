/*
 * Copyright 2015 Michael Stöcklmayr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uclan.mstocklmayr.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
