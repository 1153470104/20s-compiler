package parser;

import java.util.*;

public class ItemSet {
    public Set<Item> itemSet = new HashSet<Item>();
    public Set<GotoItem> gotoItemSet = new HashSet<>();

    /** add an Item into the set */
    public void addItem(Item i) {
        itemSet.add(i);
    }

    /** add goto set */
    public void addGoto(String x, ItemSet s) {
        gotoItemSet.add(new GotoItem(x, s));
    }

    /** the inner class of itemSet */
    class GotoItem {
        public String gotoSet;
        public ItemSet itemSet;

        public GotoItem(String gotoSet, ItemSet itemSet) {
            this.gotoSet = gotoSet;
            this.itemSet = itemSet;
        }
    }

    public void printSet() {
        for(Item i: itemSet) {
            System.out.println(i);
        }
    }
}
