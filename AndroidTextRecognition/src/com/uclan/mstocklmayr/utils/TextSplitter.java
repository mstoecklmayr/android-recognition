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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSplitter {
    private String input;
    private Map<String, String> result;
    private String phoneRegex = "^\\+?[0-9. ()-]{10,25}$";

    public TextSplitter(String text) {
        this.input = text;
        performTextAnalysis();
    }

    private void performTextAnalysis() {
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher;
        String[] parts = this.input.split("\\n");
        List<String> partList = new ArrayList<String>(Arrays.asList(parts));

        for (Iterator<String> iterator = partList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.isEmpty()) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }
        this.result = new HashMap<String, String>();

        //first line is most probably the namey
        this.result.put(ContactTypes.NAME.toString(),partList.get(0));

        for(int j=1; j<partList.size();j++){
            String part = partList.get(j);
            if(part.contains("@")){
                this.result.put(ContactTypes.PRIVATE_EMAIL.toString(), part);
                continue;
            }
            matcher = pattern.matcher(part);
            if(matcher.matches()){
                this.result.put(ContactTypes.PHONE.toString(),part);
                continue;
            }
            this.result.put(ContactTypes.OTHER.toString()+j,part);
        }
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }

    public static String[] splitName(boolean isFirstNameFirst, String name){
        String[] split = name.split(" ");
        if(!isFirstNameFirst){
            //check if name consists out of two parts
            if(split.length == 2){
                String help = split[0];
                split[0] = split[1];
                split[1] = help;
            }
        }

        return split;
    }
}
