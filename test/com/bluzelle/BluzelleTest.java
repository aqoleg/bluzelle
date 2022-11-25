package com.bluzelle;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

// some mnemonic
class BluzelleTest {
    private static final String mnemonic = "some mnemonic";
    private static final String endpoint = "http://client.sentry.testnet.public.bluzelle.com:1317";
    private static final String chain_id = "bluzelleTestNetPublic-5";
    private GasInfo gasInfo = new GasInfo(10, 0, 0);
    private LeaseInfo leaseInfo = new LeaseInfo(0, 1, 0, 0);

    @Test
    void t() {
        String m = "bluzelle12n7xe37da3cz2herpvqgdqm80sqalw52n932qc";
        String o = "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9";
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "u", chain_id);
        Bluzelle bluzelle1 = Bluzelle.connect("mnemonic", endpoint, "u", chain_id);
        System.out.println(bluzelle.account().ubntAmount);
        bluzelle.transferTokensTo(bluzelle1.address, 100, gasInfo);
        System.out.println(bluzelle.account().ubntAmount);
        System.out.println(bluzelle1.account().ubntAmount);


        //sequence 240


    }


    @Test
    void test() {
        LeaseInfo leaseInfo = new LeaseInfo(0, 0, 1, 0);
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "u", chain_id);
        Response r = bluzelle.createMessage()
                .create("key", "1", gasInfo, leaseInfo)
                .create("key1", "12", gasInfo, leaseInfo)
                .create("key2", "134", gasInfo, leaseInfo)
                .create("key3", "156", gasInfo, leaseInfo)
                .read("key2", gasInfo, "r0")
                .read("key", gasInfo, "r1")
                .delete("key", gasInfo)
                .keys(gasInfo, "k")
                .create("kkk", "1", gasInfo, leaseInfo)
                .create("kkkk", "33", gasInfo, leaseInfo)
                .has("key", gasInfo, "h0")
                .keyValues(gasInfo, "v0")
                .rename("key1", "k", gasInfo)
                .renewLease("k", gasInfo, this.leaseInfo)
                .count(gasInfo, "c0")
                .getLease("key2", gasInfo, "l0")
                .getNShortestLeases(3, gasInfo, "n0")
                .send();
        bluzelle.deleteAll(gasInfo);
        System.out.println(r.gasUsed + ", " + r.height + ", " + r.txHash);
        System.out.println(r.getString("r0"));
        System.out.println(r.getString("r1"));
        System.out.println(r.getKeys("k"));
        System.out.println(r.getBoolean("h0"));
        System.out.println(r.getKeyValues("v0"));
        System.out.println(r.getInt("c0"));
        System.out.println(r.getInt("l0"));
        System.out.println(r.getLeases("n0"));
    }

    @Test
    void test1() {
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "uuid", chain_id);
        System.out.println("version");
        System.out.println(bluzelle.version());
        System.out.println();
        System.out.println("account");
        System.out.println(bluzelle.account());
        System.out.println();

        assertThrows(
                NullPointerException.class,
                () -> bluzelle.deleteAll(null)
        );
        assertThrows(
                NullPointerException.class,
                () -> bluzelle.create("key", null, gasInfo, null)
        );
        assertThrows(
                NullPointerException.class,
                () -> bluzelle.update(null, "value", gasInfo, leaseInfo)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> bluzelle.create("slash/", "v", gasInfo, leaseInfo)
        );

        assertThrows(
                ServerException.class,
                () -> bluzelle.delete("nonexistingkey", gasInfo)
        );
        assertThrows(
                ServerException.class,
                () -> bluzelle.create("key100500", "s", new GasInfo(0, 1, 0), null)
        );
        assertThrows(
                ServerException.class,
                () -> bluzelle.txGetLease("nonexistingkey", gasInfo)
        );
    }

    @Test
    void test2() {
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "9", chain_id);
        String s = " !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        String key = "key";
        bluzelle.create(key, s, gasInfo, leaseInfo);
        assertTrue(bluzelle.has(key));
        assertTrue(bluzelle.txHas(key, gasInfo));
        assertEquals(s, bluzelle.read(key, true));
        bluzelle.delete(key, gasInfo);

        assertEquals("[]", bluzelle.keys().toString());
        assertEquals("[]", bluzelle.txKeys(gasInfo).toString());
        assertEquals("{}", bluzelle.keyValues().toString());
        assertEquals("{}", bluzelle.txKeyValues(gasInfo).toString());
        assertEquals("{}", bluzelle.getNShortestLeases(10).toString());
        assertEquals("{}", bluzelle.txGetNShortestLeases(10, gasInfo).toString());
    }

    @Test
    void test3() {
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "1", chain_id);
        if (bluzelle.has("key Д\" =? k")) {
            bluzelle.delete("key Д\" =? k", gasInfo);
        }
        bluzelle.create("key Д\" =? k", "value нЬ", gasInfo, leaseInfo);
        assertEquals("value нЬ", bluzelle.read("key Д\" =? k", false));
        assertThrows(
                KeyNotFoundException.class,
                () -> bluzelle.read("nokey", true)
        );
        assertEquals("value нЬ", bluzelle.txRead("key Д\" =? k", gasInfo));
        bluzelle.delete("key Д\" =? k", gasInfo);
    }

    @Test
    void test4() {
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "--", chain_id);
        bluzelle.deleteAll(gasInfo);
        bluzelle.create("newkeytest1", "value 1221 1 1 1", gasInfo, null);
        bluzelle.create("newkeytest2", "33", gasInfo, null);
        bluzelle.create("newkeytest3", "value", gasInfo, null);
        bluzelle.create("newkeytest4", "new value test 4", gasInfo, null);
        assertThrows(
                KeyNotFoundException.class,
                () -> bluzelle.read("nonexistingkey", true)
        );
        assertEquals("value 1221 1 1 1", bluzelle.read("newkeytest1", false));
        assertEquals("value", bluzelle.read("newkeytest3", true));
        bluzelle.rename("newkeytest4", "Mkey", gasInfo);
        assertEquals("new value test 4", bluzelle.txRead("Mkey", gasInfo));
        bluzelle.deleteAll(gasInfo);
        assertEquals(0, bluzelle.count());
        bluzelle.create("key1", "key1", gasInfo, leaseInfo);
        bluzelle.create("key2", "34 44 null", gasInfo, leaseInfo);
        bluzelle.create("key3", "value3", gasInfo, leaseInfo);
        bluzelle.create("key4", "new value test 4", gasInfo, leaseInfo);
        assertEquals(4, bluzelle.count());
        HashMap<String, String> map = bluzelle.keyValues();
        assertEquals("key1", map.get("key1"));
        assertEquals("34 44 null", map.get("key2"));
        assertEquals("value3", map.get("key3"));
        assertEquals("new value test 4", map.get("key4"));
        assertTrue(bluzelle.has("key1"));
        bluzelle.rename("key1", "Mkey", gasInfo);
        assertFalse(bluzelle.has("key1"));
        bluzelle.update("key2", "rrrrrrrrr", gasInfo, leaseInfo);
        bluzelle.delete("key3", gasInfo);
        assertFalse(bluzelle.txHas("key3", gasInfo));
        assertTrue(bluzelle.txHas("key2", gasInfo));
        ArrayList<String> list = bluzelle.keys();
        assertTrue(list.contains("Mkey"));
        assertTrue(list.contains("key2"));
        assertTrue(list.contains("key4"));
        assertEquals(3, bluzelle.txCount(gasInfo));
        map.clear();
        map.put("key2", "booooo");
        map.put("key4", "aqoleg");
        bluzelle.multiUpdate(map, gasInfo);
        list = bluzelle.txKeys(gasInfo);
        assertTrue(list.contains("key4"));
        assertTrue(list.contains("key2"));
        map = bluzelle.txKeyValues(gasInfo);
        assertEquals("booooo", map.get("key2"));
        assertEquals("aqoleg", map.get("key4"));
        bluzelle.create("key", "lease600", gasInfo, new LeaseInfo(0, 0, 10, 0));
        int lease = bluzelle.getLease("key");
        assertTrue(lease <= 600 && lease > 530);
        bluzelle.renewLeaseAll(gasInfo, new LeaseInfo(0, 0, 1, 40));
        bluzelle.renewLease("key", gasInfo, new LeaseInfo(0, 0, 40, 0));
        lease = bluzelle.txGetLease("key", gasInfo);
        assertTrue(lease <= 2400 && lease > 2200);
        HashMap<String, Integer> leaseMap = bluzelle.getNShortestLeases(3);
        assertEquals(3, leaseMap.size());
        assertTrue(leaseMap.get("Mkey") < 100);
        assertTrue(leaseMap.get("key2") < 100);
        assertTrue(leaseMap.get("key4") < 100);
        leaseMap = bluzelle.txGetNShortestLeases(4, gasInfo);
        assertTrue(leaseMap.get("Mkey") < 100);
        assertTrue(leaseMap.get("key") < 2400);
        assertTrue(leaseMap.get("key2") < 100);
        assertTrue(leaseMap.get("key4") < 100);
        bluzelle.deleteAll(gasInfo);
    }
}