package com.bluzelle;

import com.bluzelle.json.JsonArray;
import com.bluzelle.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bluzelle.LeaseInfo.blockTimeSeconds;
import static com.bluzelle.Utils.hexToString;

public class Response {
    static final int typeRead = 0;
    static final int typeHas = 1;
    static final int typeCount = 2;
    static final int typeKeys = 3;
    static final int typeKeyValues = 4;
    static final int typeLease = 5;
    static final int typeNShortestLeases = 6;
    public final String txHash;
    public final int height;
    public final int gasUsed;
    private final HashMap<String, Object> values = new HashMap<>();

    private Response(String txHash, int height, int gasUsed) {
        this.txHash = txHash;
        this.height = height;
        this.gasUsed = gasUsed;
    }

    static Response parse(JsonObject input, ArrayList<Integer> types, ArrayList<String> tags) {
        // input example: {
        //  "height":"233785",
        //  "txhash":"40036D74943EBA43FDBF6A9D7264C91E5FDE93DA14745137E111C03393B424A8",
        //  "data":"7B...7D",
        //  "raw_log":"...",
        //  "logs":[{
        //   "msg_index":0,
        //   "log":"",
        //   "events":[{"type":"message","attributes":[{"key":"action","value":"create"}]}]
        //   },{...}],
        //  "gas_wanted":"1200000",
        //  "gas_used":"75528"}
        Response response = new Response(
                input.getString("txhash"),
                Integer.parseInt(input.getString("height")),
                Integer.parseInt(input.getString("gas_used"))
        );
        String data = input.getString("data");
        response.parse(data == null ? "" : hexToString(data), types, tags);
        return response;
    }

    public String getString(String tag) {
        return (String) values.get(tag);
    }

    public boolean getBoolean(String tag) {
        return (boolean) values.get(tag);
    }

    public int getInt(String tag) {
        return (int) values.get(tag);
    }

    public ArrayList<String> getKeys(String tag) {
        //noinspection unchecked
        return (ArrayList<String>) values.get(tag);
    }

    public HashMap<String, String> getKeyValues(String tag) {
        return (HashMap<String, String>) values.get(tag);
    }

    public HashMap<String, Integer> getLeases(String tag) {
        return (HashMap<String, Integer>) values.get(tag);
    }

    private void parse(String data, ArrayList<Integer> types, ArrayList<String> tags) {
        //System.out.println(data);
        int startPos = 0;
        int endPos;
        JsonObject value;
        Object object;
        int count = types.size();
        for (int i = 0; i < count; i++) {
            endPos = data.indexOf("}{", startPos);
            if (endPos == -1) {
                endPos = data.length();
            } else {
                endPos++;
            }
            value = JsonObject.parse(data.substring(startPos, endPos));
            switch (types.get(i)) {
                case typeRead:
                    object = value.getString("value");
                    break;
                case typeHas:
                    object = value.getBoolean("has");
                    break;
                case typeCount:
                    object = Integer.parseInt(value.getString("count"));
                    break;
                case typeKeys:
                    object = parseKeys(value);
                    break;
                case typeKeyValues:
                    object = parseKeyValues(value);
                    break;
                case typeLease:
                    object = Integer.parseInt(value.getString("lease")) * blockTimeSeconds;
                    break;
                case typeNShortestLeases:
                    object = parseLeases(value);
                    break;
                default:
                    object = null;
            }
            values.put(tags.get(i), object);
            startPos = endPos;
        }
    }

    private ArrayList<String> parseKeys(JsonObject value) {
        JsonArray keys = value.getArray("keys");
        ArrayList<String> list = new ArrayList<>();
        if (keys == null) {
            return list;
        }
        int length = keys.length();
        for (int i = 0; i < length; i++) {
            list.add(keys.getString(i));
        }
        return list;
    }

    private HashMap<String, String> parseKeyValues(JsonObject value) {
        JsonArray keyValues = value.getArray("keyvalues");
        HashMap<String, String> map = new HashMap<>();
        if (keyValues == null) {
            return map;
        }
        JsonObject object;
        int length = keyValues.length();
        for (int i = 0; i < length; i++) {
            object = keyValues.getObject(i);
            map.put(object.getString("key"), object.getString("value"));
        }
        return map;
    }

    private HashMap<String, Integer> parseLeases(JsonObject value) {
        JsonArray keyLeases = value.getArray("keyleases");
        HashMap<String, Integer> map = new HashMap<>();
        if (keyLeases == null) {
            return map;
        }
        int length = keyLeases.length();
        for (int i = 0; i < length; i++) {
            JsonObject object = keyLeases.getObject(i);
            map.put(object.getString("key"), Integer.parseInt(object.getString("lease")) * blockTimeSeconds);
        }
        return map;
    }
}