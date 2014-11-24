package com.uclan.mstocklmayr.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mike on 11/23/14.
 */
public class TextSplitter {
    private String input;
    private Map<String, String> result;
    private String phoneRegex = "^\\+?[0-9. ()-]{10,25}$";

    public static String NAME = "NAME";
    public static String EMAIL = "EMAIL";
    public static String PHONE = "PHONE";
    public static String OTHER = "OTHER";

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
        this.result.put(NAME,partList.get(0));

        for(int j=1; j<partList.size();j++){
            String part = partList.get(j);
            //TODO what if there are more phone numbers or more emails?
            if(part.contains("@")){
                this.result.put(EMAIL, part);
                continue;
            }
            matcher = pattern.matcher(part);
            if(matcher.matches()){
                this.result.put(PHONE,part);
                continue;
            }
            this.result.put(OTHER+j,part);
        }
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}
