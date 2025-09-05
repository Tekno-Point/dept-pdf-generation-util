package com.pdfGeneration.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JsonUtility {

    public JsonObject getJsonObject(String jsonReq){
        try {
            return JsonParser.parseString(jsonReq).getAsJsonObject();
        }catch (Exception ex){
            return new JsonObject();
        }
    }

    public boolean returnTrueOrFalse(String flag) {
        return flag.equalsIgnoreCase("Y");
    }

    public JsonObject getJsonObjectFromObject(JsonObject jsonReq) {
        try {
            return jsonReq;
        } catch (Exception ex) {
            return new JsonObject();
        }
    }

    public JsonArray getJsonArrayByKey(String keyName, JsonObject objName) {
        if (objName.has(keyName)) {
            Optional<JsonArray> value = Optional.of(objName.get(keyName).getAsJsonArray());
            return value.orElseThrow().getAsJsonArray();
        } else {
            return new JsonArray();
        }
    }


    public String getBooleanToString(String val){
        return val.equalsIgnoreCase("true") ? "Y" : val.equalsIgnoreCase("false") ?"N" : "";
    }
    public String getJsonKeyValue(String keyName, JsonObject objName){
        if(objName.has(keyName)){
            Optional<String> value ;
            if (objName.has(keyName) && !objName.get(keyName).isJsonNull()) {
                value =  Optional.of(objName.get(keyName).getAsString());
            } else {
                value =  Optional.empty();
            }
            return value.orElse("");

        }else{
            return "";
        }
    }

    public JsonObject getJsonObjectByKey(String keyName, JsonObject objName){
        if(objName.has(keyName)){
            Optional<JsonObject> value = Optional.of(objName.get(keyName).getAsJsonObject());
            return value.orElseGet(JsonObject::new);
        }else{
            return new JsonObject();
        }
    }

    public boolean getBooleanKeyValue(String keyName, JsonObject objName){
        if (objName.has(keyName)) {
            return objName.get(keyName).getAsBoolean();
        } else {
            return false;
        }
    }
    public Boolean getJsonKeyValueForBoolean(String keyName, JsonObject objName){
        if(objName.has(keyName)){
            Optional<Boolean> value = Optional.of(objName.get(keyName).getAsBoolean());
            return value.orElse(true);
        }else{
            return false;
        }
    }

    public boolean getYesOrNoFromString(String val){
        return !val.equalsIgnoreCase("NO");
    }

    public String getYesOrNoFromStringForMedical(String val) {
        return val.equalsIgnoreCase("NO") ? "NO" : "YES";
    }
}
