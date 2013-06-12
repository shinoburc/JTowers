package org.dyndns.dandydot.jtowers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class CardGenerator { 
    private static int[] num  = {0,1,2,3,4,5,6,7,8,9,10,11,12
            ,13,14,15,16,17,18,19,20,21,22,23,24,25
            ,26,27,28,29,30,31,32,33,34,35,36,37,38
            ,39,40,41,42,43,44,45,46,47,48,49,50,51 };
    private static final int num_max = 52;

    public static Iterator randomCardGenerate(){
        ArrayList<Card> list = new ArrayList<Card>();
        Random r = new Random();
        CardFactory cf = CardFactory.getInstance();
        int j, tmp;
        for (int i = 0; i < num_max; i++) {
            j = (Integer.MAX_VALUE & r.nextInt()) % num_max; /* ? */
            tmp = num[i];
            num[i] = num[j];
            num[j] = tmp;
        }

        //gen Cards
        for (int i = 0; i < num_max; i++) {
            list.add(cf.getCard((int)(num[i] / 13),(num[i] % 13) + 1) );
        }
        return list.iterator();
    }
}
