package com.bluzelle;

import com.bluzelle.crypto.Ecc;
import com.bluzelle.crypto.HdKeyPair;
import com.bluzelle.json.JsonArray;
import com.bluzelle.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bluzelle.Response.*;
import static com.bluzelle.Utils.*;

public class Message {
    private final Bluzelle bluzelle;
    private final HdKeyPair keyPair;
    private final ArrayList<Integer> types = new ArrayList<>();
    private final ArrayList<String> tags = new ArrayList<>();
    private final JsonArray msg = new JsonArray();
    private int maxGas = 0;
    private int maxFee = 0;
    private int gasPrice = 0;

    Message(Bluzelle bluzelle, HdKeyPair keyPair) {
        this.bluzelle = bluzelle;
        this.keyPair = keyPair;
    }

    public Message transferTokensTo(String address, int amount, GasInfo gasInfo) {
        JsonObject ubnt = new JsonObject().put("amount", String.valueOf(amount)).put("denom", "ubnt");
        JsonObject data = new JsonObject();
        data.put("from_address", bluzelle.address);
        data.put("to_address", address);
        data.put("amount", new JsonArray().put(ubnt));

        msg.put(new JsonObject().put("type", "cosmos-sdk/MsgSend").put("value", data));

        maxGas += gasInfo.maxGas == 0 ? 200000 : gasInfo.maxGas;
        maxFee += gasInfo.maxFee;
        gasPrice = Math.max(gasPrice, gasInfo.gasPrice);
        return this;
    }

    /**
     * create a field in the database
     *
     * @param key       name of the key to create
     * @param value     value to set the key
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException     if key == null or value == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty or contains '/', or lease is negative
     */
    public Message create(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        } else if (key.contains("/")) {
            throw new IllegalArgumentException("Key cannot contain a slash");
        }
        if (value == null) {
            throw new NullPointerException("null value");
        }
        int blocks = 0;
        if (leaseInfo != null) {
            blocks = leaseInfo.blocks;
            if (blocks < 0) {
                throw new IllegalArgumentException("Invalid lease time");
            }
        }

        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Value", value);
        data.put("Lease", String.valueOf(blocks));
        addMessage("crud/create", data, gasInfo);
        return this;
    }

    /**
     * retrieve the value of a key via a transaction
     *
     * @param key     the key to retrieve
     * @param gasInfo object containing gas parameters
     * @return String value of the key
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     */
    public Message read(String key, GasInfo gasInfo, String tag) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        types.add(typeRead);
        tags.add(tag);
        JsonObject data = new JsonObject().put("Key", key);
        addMessage("crud/read", data, gasInfo);
        return this;
    }

    /**
     * query to see if a key is in the database via a transaction
     *
     * @param key     the name of the key to query
     * @param gasInfo object containing gas parameters
     * @return value representing whether the key is in the database
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     */
    public Message has(String key, GasInfo gasInfo, String tag) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        types.add(typeHas);
        tags.add(tag);
        JsonObject data = new JsonObject().put("Key", key);
        addMessage("crud/has", data, gasInfo);
        return this;
    }

    /**
     * @param gasInfo object containing gas parameters
     * @return the number of keys in the current database/uuid via a transaction
     * @throws NullPointerException if gasInfo == null
     */
    public Message count(GasInfo gasInfo, String tag) {
        types.add(typeCount);
        tags.add(tag);
        addMessage("crud/count", new JsonObject(), gasInfo);
        return this;
    }

    /**
     * retrieve a list of all keys via a transaction
     *
     * @param gasInfo object containing gas parameters
     * @return ArrayList containing all keys
     * @throws NullPointerException if gasInfo == null
     */
    public Message keys(GasInfo gasInfo, String tag) {
        types.add(typeKeys);
        tags.add(tag);
        addMessage("crud/keys", new JsonObject(), gasInfo);
        return this;
    }

    /**
     * enumerate all keys and values in the current database/uuid via a transaction
     *
     * @param gasInfo object containing gas parameters
     * @return HashMap(key, value)
     * @throws NullPointerException if gasInfo == null
     */
    public Message keyValues(GasInfo gasInfo, String tag) {
        types.add(typeKeyValues);
        tags.add(tag);
        addMessage("crud/keyvalues", new JsonObject(), gasInfo);
        return this;
    }

    /**
     * retrieve the minimum time remaining on the lease for a key via a transaction
     *
     * @param key     the key to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return minimum length of time remaining for the key's lease, in seconds
     * @throws NullPointerException     if key == null
     * @throws IllegalArgumentException if key is empty
     */
    public Message getLease(String key, GasInfo gasInfo, String tag) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        types.add(typeLease);
        tags.add(tag);
        JsonObject data = new JsonObject().put("Key", key);
        addMessage("crud/getlease", data, gasInfo);
        return this;
    }

    /**
     * retrieve a list of the n keys in the database with the shortest leases via a transaction
     *
     * @param n       the number of keys to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return HashMap(key, lease seconds)
     * @throws NullPointerException     if gasInfo == null
     * @throws IllegalArgumentException if n < 0
     */
    public Message getNShortestLeases(int n, GasInfo gasInfo, String tag) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid value specified");
        }

        types.add(typeNShortestLeases);
        tags.add(tag);
        JsonObject data = new JsonObject().put("N", String.valueOf(n));
        addMessage("crud/getnshortestleases", data, gasInfo);
        return this;
    }

    /**
     * update a field in the database
     *
     * @param key       the name of the key to create
     * @param value     value to set the key
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo positive or negative amount of time to alter the lease by or null
     * @throws NullPointerException     if key == null or value == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     */
    public Message update(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (value == null) {
            throw new NullPointerException("null value");
        }

        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Value", value);
        data.put("Lease", leaseInfo == null ? "0" : String.valueOf(leaseInfo.blocks));
        addMessage("crud/update", data, gasInfo);
        return this;
    }

    /**
     * change the name of an existing key
     *
     * @param key     the name of the key to rename
     * @param newKey  the new name for the key
     * @param gasInfo object containing gas parameters
     * @throws NullPointerException     if key == null or newKey == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty or newKey is empty or newKey contains '/'
     */
    public Message rename(String key, String newKey, GasInfo gasInfo) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("New key cannot be empty");
        } else if (newKey.contains("/")) {
            throw new IllegalArgumentException("Key cannot contain a slash");
        }

        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("NewKey", newKey);
        addMessage("crud/rename", data, gasInfo);
        return this;
    }

    /**
     * update multiple fields in the database
     *
     * @param keyValues HashMap(key, value)
     * @param gasInfo   object containing gas parameters
     * @throws NullPointerException     if keyValues == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     */
    public Message multiUpdate(HashMap<String, String> keyValues, GasInfo gasInfo) {
        JsonArray jsonKeyValues = new JsonArray();
        String key;
        JsonObject jsonKeyValue;
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            key = entry.getKey();
            if (key.isEmpty()) {
                throw new IllegalArgumentException("Key cannot be empty");
            }
            jsonKeyValue = new JsonObject();
            jsonKeyValue.put("key", key);
            jsonKeyValue.put("value", entry.getValue());
            jsonKeyValues.put(jsonKeyValue);
        }

        JsonObject data = new JsonObject().put("KeyValues", jsonKeyValues);
        addMessage("crud/multiupdate", data, gasInfo);
        return this;
    }

    /**
     * update the minimum time remaining on the lease for a key
     *
     * @param key       the key to retrieve the lease information for
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty or lease is negative
     */
    public Message renewLease(String key, GasInfo gasInfo, LeaseInfo leaseInfo) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        int blocks = 0;
        if (leaseInfo != null) {
            blocks = leaseInfo.blocks;
            if (blocks < 0) {
                throw new IllegalArgumentException("Invalid lease time");
            }
        }

        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Lease", String.valueOf(blocks));
        addMessage("crud/renewlease", data, gasInfo);
        return this;
    }

    /**
     * update the minimum time remaining on the lease for all keys
     *
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException     if gasInfo == null
     * @throws IllegalArgumentException if lease is negative
     */
    public Message renewLeaseAll(GasInfo gasInfo, LeaseInfo leaseInfo) {
        int blocks = 0;
        if (leaseInfo != null) {
            blocks = leaseInfo.blocks;
            if (blocks < 0) {
                throw new IllegalArgumentException("Invalid lease time");
            }
        }

        JsonObject data = new JsonObject().put("Lease", String.valueOf(blocks));
        addMessage("crud/renewleaseall", data, gasInfo);
        return this;
    }

    /**
     * delete a field from the database
     *
     * @param key     the name of the key to delete
     * @param gasInfo object containing gas parameters
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     */
    public Message delete(String key, GasInfo gasInfo) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        JsonObject data = new JsonObject().put("Key", key);
        addMessage("crud/delete", data, gasInfo);
        return this;
    }

    /**
     * remove all keys in the current database/uuid
     *
     * @param gasInfo object containing gas parameters
     */
    public Message deleteAll(GasInfo gasInfo) {
        addMessage("crud/deleteall", new JsonObject(), gasInfo);
        return this;
    }

    public Response send() {
        JsonObject fee = new JsonObject();
        fee.put("gas", String.valueOf(maxGas));
        JsonObject feeAmount = new JsonObject();
        feeAmount.put("denom", "ubnt");
        feeAmount.put("amount", String.valueOf(maxFee == 0 ? maxGas * gasPrice : maxFee));
        fee.put("amount", new JsonArray().put(feeAmount));

        String memo = randomString();

        JsonObject tx = new JsonObject();
        tx.put("fee", fee);
        tx.put("memo", memo);
        tx.put("msg", msg);

        JsonObject out = new JsonObject();
        out.put("tx", tx);
        out.put("mode", "block");

        int i = 0;
        do {
            tx.put("signatures", new JsonArray().put(sign(msg, fee, memo)));
            //System.out.println("post:" + out.toString());

            String response = post(bluzelle.endpoint, out);
            //System.out.println("response:" + response);

            JsonObject responseData = JsonObject.parse(response);

            if (responseData.getInteger("code") == null) {
                return Response.parse(responseData, types, tags);
            }
            String errorMessage = extractMessage(responseData);
            if (!errorMessage.contains("signature verification failed")) {
                throw new ServerException(errorMessage);
            }
            if (++i == 20) {
                throw new ServerException(errorMessage);
            }

            if (i != 0) {
                System.out.println("!!!!");
                System.out.println("!!!!");
                System.out.println("!!!!");
            }

        } while (true);
    }

    private void addMessage(String path, JsonObject value, GasInfo gasInfo) {
        value.put("UUID", bluzelle.uuid);
        value.put("Owner", bluzelle.address);
        msg.put(new JsonObject().put("type", path).put("value", value));

        maxGas += gasInfo.maxGas == 0 ? 200000 : gasInfo.maxGas;
        maxFee += gasInfo.maxFee;
        gasPrice = Math.max(gasPrice, gasInfo.gasPrice);
    }

    private JsonObject sign(JsonArray msg, JsonObject fee, String memo) {
        String response = get(bluzelle.endpoint, "/auth/accounts/" + bluzelle.address);
        System.out.println(response);
        JsonObject account = JsonObject.parse(response).getObject("result").getObject("value");
        String sequence = account.getString("sequence");
        String accountNumber = account.getString("account_number");

        JsonObject payload = new JsonObject();
        payload.put("account_number", accountNumber);
        payload.put("chain_id", bluzelle.chainId);
        payload.put("fee", fee);
        payload.put("memo", memo);
        payload.put("msgs", msg);
        payload.put("sequence", sequence);

        byte[] hash = sha256hash(payload.toSanitizeString().getBytes());
        byte[] signature = Ecc.ecc.sign(hash, keyPair.d);

        JsonObject publicKey = new JsonObject();
        publicKey.put("type", "tendermint/PubKeySecp256k1");
        publicKey.put("value", base64encode(keyPair.publicKeyToByteArray()));

        JsonObject out = new JsonObject();
        out.put("pub_key", publicKey);
        out.put("signature", base64encode(signature));
        out.put("account_number", accountNumber);
        out.put("sequence", sequence);

        return out;
    }

    private String extractMessage(JsonObject data) {
        // data example:
        // {"height":"0",
        //  "txhash":"DC9D177340F74B6FA5DCD6E06A8CD47F9E208DE2F3045F932809BF92142DAD44",
        //  "codespace":"sdk",
        //  "code":4,
        //  "raw_log":"unauthorized: signature verification failed; verify correct account sequence and chain-id",
        //  "gas_wanted":"1200000",
        //  "gas_used":"45205"}
        String log = data.getString("raw_log");
        if (log == null) {
            return "";
        }
        int startPos = log.indexOf(": ");
        if (startPos < 0) {
            return log;
        }
        // "insufficient fee: insufficient fees; got: 10ubnt required: 2000000ubnt"
        if (log.substring(0, startPos).equals("insufficient fee")) {
            return log.substring(startPos + 2);
        }
        // "unauthorized: Key already exists: failed to execute message; message index: 0"
        int endPos = log.indexOf(":", startPos + 1);
        if (endPos < 0) {
            return log.substring(startPos + 2);
        }
        return log.substring(startPos + 2, log.indexOf(":", startPos + 1));
    }
}