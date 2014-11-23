package com.uclan.mstocklmayr.utils;

import java.util.Map;
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
        String[] parts = this.input.split("\\\\r?\\\\n");

        //first line is most probably the name
        this.result.put(NAME,parts[0]);

        for(int i=1;i<parts.length;i++){
            if(i+1<parts.length){
                parts[i] = parts[i+1];
            }else{
             //set last element to null;
            }
        }

        for(int j=0; j<parts.length;j++){
            String part = parts[j];
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
