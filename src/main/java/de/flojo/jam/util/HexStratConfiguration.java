package de.flojo.jam.util;

import de.flojo.jam.networking.share.util.IAmJson;

public class HexStratConfiguration implements IAmJson {
    int defaultStartMoney = 45;
    String defaultIpAddress = "localhost";
    int defaultPort =1096;

    public int getDefaultPort() {
        return defaultPort;
    }

    public int getDefaultStartMoney() {
        return defaultStartMoney;
    }

    public String getDefaultIpAddress() {
        return defaultIpAddress;
    }
}
