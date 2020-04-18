package parser;

import java.util.*;

public class ItemSet {
    public Set<Item> itemSet = new HashSet<Item>();
    public Set<GotoItem> gotoItemSet = new HashSet<>();

    public ItemSet() {
    }

    public boolean containsList(List<String> list) {
        for(Item i: itemSet) {
            List<String> units = i.units;
            if(units.size() == list.size()) {
                for(int j = 0; j < list.size(); j++) {
                    if(!units.get(j).equals(list.get(j)))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    /** add an Item into the set */
    public void addItem(Item i) {
        itemSet.add(i);
    }

    /** add goto set */
    public void addGoto(String x, ItemSet s) {
        gotoItemSet.add(new GotoItem(x, s));
    }

    /** the inner class of itemSet */
    public static class GotoItem {
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
