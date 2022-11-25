// bluzelle client
// usage:
//    connect
//       Bluzelle bluzelle = Bluzelle.connect(mnemonicString, endpointString, uuidString, chainIdString);
//    data
//       String version = bluzelle.version();
//       JsonObject account = bluzelle.account();
//    create
//       bluzelle.create(keyString, valueString, gasInfo, leaseInfo);
//    read
//       String value = bluzelle.read(keyString, isProve);
//       String value = bluzelle.txRead(keyString, gasInfo);
//       boolean has = bluzelle.has(keyString);
//       boolean has = bluzelle.txHas(keyString, gasInfo);
//       int count = bluzelle.count();
//       int count = bluzelle.txCount(gasInfo);
//       ArrayList<String> keys = bluzelle.crypto();
//       ArrayList<String> keys = bluzelle.txKeys(gasInfo);
//       HashMap<String, String> keyValues = bluzelle.keyValues();
//       HashMap<String, String> keyValues = bluzelle.txKeyValues(gasInfo);
//       int leaseSeconds = bluzelle.getLease(keyString);
//       int leaseSeconds = bluzelle.txGetLease(keyString, gasInfo);
//       HashMap<String, Integer> leases = bluzelle.getNShortestLeases(n);
//       HashMap<String, Integer> leases = bluzelle.txGetNShortestLeases(n, gasInfo);
//    update
//       bluzelle.update(keyString, valueString, gasInfo, leaseInfo);
//       bluzelle.rename(keyString, newKeyString, gasInfo);
//       bluzelle.multiUpdate(keyValuesHashMap, gasInfo);
//       bluzelle.renewLease(keyString, gasInfo, leaseInfo);
//       bluzelle.renewLeaseAll(gasInfo, leaseInfo);
//    delete
//       bluzelle.delete(keyString, gasInfo);
//       bluzelle.deleteAll(gasInfo);
package com.bluzelle;

import com.bluzelle.crypto.HdKeyPair;
import com.bluzelle.crypto.Mnemonic;
import com.bluzelle.json.JsonArray;
import com.bluzelle.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bluzelle.LeaseInfo.blockTimeSeconds;
import static com.bluzelle.Utils.*;

public class Bluzelle {
    public final String address;
    public final String endpoint;
    public final String chainId;
    public final String uuid;
    private final HdKeyPair keyPair;

    private Bluzelle(HdKeyPair keyPair, String address, String endpoint, String chainId, String uuid) {
        this.keyPair = keyPair;
        this.address = address;
        this.endpoint = endpoint;
        this.chainId = chainId;
        this.uuid = uuid;
    }

    /**
     * creates and configures connection
     *
     * @param mnemonic mnemonic of the private key for account
     * @param endpoint hostname and port of rest server
     *                 if null or empty uses default "http://localhost:1317"
     * @param uuid     uuid
     *                 if null or empty uses uuid the same as address
     * @param chainId  chain id of account
     *                 if null or empty uses default "bluzelle"
     * @return instance of Bluzelle
     * @throws NullPointerException if mnemonic == null
     * @throws ConnectionException  if can not connect to the node
     */
    public static Bluzelle connect(String mnemonic, String endpoint, String uuid, String chainId) {
        HdKeyPair master = HdKeyPair.createMaster(Mnemonic.createSeed(mnemonic, "mnemonic"));
        HdKeyPair keyPair = master.generateChild("44'/118'/0'/0/0");
        String address = getAddress(keyPair);
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "http://localhost:1317";
        }
        if (uuid == null || uuid.isEmpty()) {
            uuid = address;
        }
        if (chainId == null || chainId.isEmpty()) {
            chainId = "bluzelle";
        }
        return new Bluzelle(keyPair, address, endpoint, chainId, uuid);
    }

    public static String createMnemonic(int length) {
        return Mnemonic.generateMnemonic(length);
    }

    public static String createMnemonic(byte[] entropy) {
        return Mnemonic.entropyToMnemonic(entropy);
    }

    /**
     * @return version of the service
     * @throws ConnectionException if can not connect to the node
     */
    public String version() {
        String response = get(endpoint, "/node_info");
        return JsonObject.parse(response).getObject("application_version").getString("version");
    }

    /**
     * @return AccountData with information about the currently active account
     * @throws ConnectionException if can not connect to the node
     */
    public AccountData account() {
        String response = get(endpoint, "/auth/accounts/" + address);
        return AccountData.parse(JsonObject.parse(response).getObject("result").getObject("value"));
    }

    public void transferTokensTo(String address, int amount, GasInfo gasInfo) {
        createMessage().transferTokensTo(address, amount, gasInfo).send();
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
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void create(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        createMessage().create(key, value, gasInfo, leaseInfo).send();
    }

    /**
     * retrieve the value of a key without consensus verification
     *
     * @param key   the key to retrieve
     * @param prove a proof of the value is required from the network
     * @return String value of the key
     * @throws NullPointerException     if key == null
     * @throws IllegalArgumentException if key is empty
     * @throws KeyNotFoundException     if key does not exist
     * @throws ConnectionException      if can not connect to the node
     */
    public String read(String key, boolean prove) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        String path = "/crud/" + (prove ? "pread/" : "read/") + uuid + "/" + urlEncode(key);
        String response = get(endpoint, path, key);
        return JsonObject.parse(response).getObject("result").getString("value");
    }

    /**
     * retrieve the value of a key via a transaction
     *
     * @param key     the key to retrieve
     * @param gasInfo object containing gas parameters
     * @return String value of the key
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public String txRead(String key, GasInfo gasInfo) {
        return createMessage().read(key, gasInfo, "").send().getString("");
    }

    /**
     * query to see if a key is in the database
     *
     * @param key the name of the key to query
     * @return value representing whether the key is in the database
     * @throws NullPointerException     if key == null
     * @throws IllegalArgumentException if key is empty
     * @throws KeyNotFoundException     if key does not exist
     * @throws ConnectionException      if can not connect to the node
     */
    public boolean has(String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        String response = get(endpoint, "/crud/has/" + uuid + "/" + urlEncode(key));
        return JsonObject.parse(response).getObject("result").getBoolean("has");
    }

    /**
     * query to see if a key is in the database via a transaction
     *
     * @param key     the name of the key to query
     * @param gasInfo object containing gas parameters
     * @return value representing whether the key is in the database
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public boolean txHas(String key, GasInfo gasInfo) {
        return createMessage().has(key, gasInfo, "").send().getBoolean("");
    }

    /**
     * @return the number of keys in the current database/uuid
     * @throws ConnectionException if can not connect to the node
     */
    public int count() {
        String response = get(endpoint, "/crud/count/" + uuid);
        return Integer.parseInt(JsonObject.parse(response).getObject("result").getString("count"));
    }

    /**
     * @param gasInfo object containing gas parameters
     * @return the number of keys in the current database/uuid via a transaction
     * @throws NullPointerException if gasInfo == null
     * @throws ConnectionException  if can not connect to the node
     * @throws ServerException      if server returns error
     */
    public int txCount(GasInfo gasInfo) {
        return createMessage().count(gasInfo, "").send().getInt("");
    }

    /**
     * retrieve a list of all keys
     *
     * @return ArrayList containing all keys
     * @throws ConnectionException if can not connect to the node
     */
    public ArrayList<String> keys() {
        String response = get(endpoint, "/crud/keys/" + uuid);
        JsonArray keys = JsonObject.parse(response).getObject("result").getArray("keys");

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

    /**
     * retrieve a list of all keys via a transaction
     *
     * @param gasInfo object containing gas parameters
     * @return ArrayList containing all keys
     * @throws NullPointerException if gasInfo == null
     * @throws ConnectionException  if can not connect to the node
     * @throws ServerException      if server returns error
     */
    public ArrayList<String> txKeys(GasInfo gasInfo) {
        return createMessage().keys(gasInfo, "").send().getKeys("");
    }

    /**
     * enumerate all keys and values in the current database/uuid
     *
     * @return HashMap(key, value)
     * @throws ConnectionException if can not connect to the node
     */
    public HashMap<String, String> keyValues() {
        String response = get(endpoint, "/crud/keyvalues/" + uuid);
        JsonArray keyValues = JsonObject.parse(response).getObject("result").getArray("keyvalues");

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

    /**
     * enumerate all keys and values in the current database/uuid via a transaction
     *
     * @param gasInfo object containing gas parameters
     * @return HashMap(key, value)
     * @throws NullPointerException if gasInfo == null
     * @throws ConnectionException  if can not connect to the node
     * @throws ServerException      if server returns error
     */
    public HashMap<String, String> txKeyValues(GasInfo gasInfo) {
        return createMessage().keyValues(gasInfo, "").send().getKeyValues("");
    }

    /**
     * retrieve the minimum time remaining on the lease for a key
     *
     * @param key the key to retrieve the lease information for
     * @return minimum length of time remaining for the key's lease, in seconds
     * @throws NullPointerException     if key == null
     * @throws IllegalArgumentException if key is empty
     * @throws KeyNotFoundException     if key does not exist
     * @throws ConnectionException      if can not connect to the node
     */
    public int getLease(String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        String response = get(endpoint, "/crud/getlease/" + uuid + "/" + urlEncode(key), key);
        return Integer.parseInt(JsonObject.parse(response).getObject("result").getString("lease")) * blockTimeSeconds;
    }

    /**
     * retrieve the minimum time remaining on the lease for a key via a transaction
     *
     * @param key     the key to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return minimum length of time remaining for the key's lease, in seconds
     * @throws NullPointerException     if key == null
     * @throws IllegalArgumentException if key is empty
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public int txGetLease(String key, GasInfo gasInfo) {
        return createMessage().getLease(key, gasInfo, "").send().getInt("");
    }

    /**
     * retrieve a list of the n keys in the database with the shortest leases
     *
     * @param n the number of keys to retrieve the lease information for
     * @return HashMap(key, lease seconds)
     * @throws IllegalArgumentException if n < 0
     * @throws ConnectionException      if can not connect to the node
     */
    public HashMap<String, Integer> getNShortestLeases(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid value specified");
        }

        String response = get(endpoint, "/crud/getnshortestleases/" + uuid + "/" + n);
        JsonArray keyLeases = JsonObject.parse(response).getObject("result").getArray("keyleases");

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

    /**
     * retrieve a list of the n keys in the database with the shortest leases via a transaction
     *
     * @param n       the number of keys to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return HashMap(key, lease seconds)
     * @throws NullPointerException     if gasInfo == null
     * @throws IllegalArgumentException if n < 0
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public HashMap<String, Integer> txGetNShortestLeases(int n, GasInfo gasInfo) {
        return createMessage().getNShortestLeases(n, gasInfo, "").send().getLeases("");
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
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void update(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        createMessage().update(key, value, gasInfo, leaseInfo).send();
    }

    /**
     * change the name of an existing key
     *
     * @param key     the name of the key to rename
     * @param newKey  the new name for the key
     * @param gasInfo object containing gas parameters
     * @throws NullPointerException     if key == null or newKey == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty or newKey is empty or newKey contains '/'
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void rename(String key, String newKey, GasInfo gasInfo) {
        createMessage().rename(key, newKey, gasInfo).send();
    }

    /**
     * update multiple fields in the database
     *
     * @param keyValues HashMap(key, value)
     * @param gasInfo   object containing gas parameters
     * @throws NullPointerException     if keyValues == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void multiUpdate(HashMap<String, String> keyValues, GasInfo gasInfo) {
        createMessage().multiUpdate(keyValues, gasInfo).send();
    }

    /**
     * update the minimum time remaining on the lease for a key
     *
     * @param key       the key to retrieve the lease information for
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty or lease is negative
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void renewLease(String key, GasInfo gasInfo, LeaseInfo leaseInfo) {
        createMessage().renewLease(key, gasInfo, leaseInfo).send();
    }

    /**
     * update the minimum time remaining on the lease for all keys
     *
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException     if gasInfo == null
     * @throws IllegalArgumentException if lease is negative
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void renewLeaseAll(GasInfo gasInfo, LeaseInfo leaseInfo) {
        createMessage().renewLeaseAll(gasInfo, leaseInfo).send();
    }

    /**
     * delete a field from the database
     *
     * @param key     the name of the key to delete
     * @param gasInfo object containing gas parameters
     * @throws NullPointerException     if key == null or gasInfo == null
     * @throws IllegalArgumentException if key is empty
     * @throws ConnectionException      if can not connect to the node
     * @throws ServerException          if server returns error
     */
    public void delete(String key, GasInfo gasInfo) {
        createMessage().delete(key, gasInfo).send();
    }

    /**
     * remove all keys in the current database/uuid
     *
     * @param gasInfo object containing gas parameters
     * @throws ConnectionException if can not connect to the node
     * @throws ServerException     if server returns error
     */
    public void deleteAll(GasInfo gasInfo) {
        createMessage().deleteAll(gasInfo).send();
    }

    public Message createMessage() {
        return new Message(this, keyPair);
    }
}