package org.dyndns.dandydot.jtowers;

public class JTowers {
    private static String version = "0.0.0";

    public static void main(String[] args) {
        CardTable cardTable = new JCardTable("JTowers " + version);
        cardTable.execute();
    }
}
