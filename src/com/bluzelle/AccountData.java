package com.bluzelle;

import com.bluzelle.json.JsonObject;

public class AccountData {
    public final String publicKey;
    public final int accountNumber;
    public final int sequence;
    public final long ubntAmount;

    private AccountData(String publicKey, int accountNumber, int sequence, long ubntAmount) {
        this.publicKey = publicKey;
        this.accountNumber = accountNumber;
        this.sequence = sequence;
        this.ubntAmount = ubntAmount;
    }

    public static AccountData parse(JsonObject object) {
        return new AccountData(
                object.getObject("public_key").getString("value"),
                Integer.parseInt(object.getString("account_number")),
                Integer.parseInt(object.getString("sequence")),
                Long.parseLong(object.getArray("coins").getObject(0).getString("amount"))
        );
    }
}