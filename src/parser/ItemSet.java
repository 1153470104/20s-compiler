package parser;

import java.util.*;

public class ItemSet {
    public Set<Item> itemSet = new HashSet<Item>();
    public Set<GotoItem> gotoItemSet = new HashSet<>();
    public Set<GotoItem> sourceItemSet = new HashSet<>();

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


    public void printSet() {
        for(Item i: itemSet) {
            System.out.println(i);
        }
    }

    public boolean setEquals(ItemSet otherSet) {
        if(otherSet.itemSet.size() != this.itemSet.size())
            return false;
        for(Item oi: otherSet.itemSet){
            if(!this.itemSet.contains(oi))
                return false;
        }
        return true;
    }

    public void combineSourceSet(ItemSet s) {
        this.sourceItemSet.addAll(s.sourceItemSet);
    }

    /** the inner class of itemSet */
    public static class GotoItem {
        public String gotoSet;
        public ItemSet itemSet;

        public GotoItem(String gotoSet, ItemSet itemSet) {
            this.gotoSet = gotoSet;
            this.itemSet = itemSet;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GotoItem)) return false;
            GotoItem gotoItem = (GotoItem) o;
            return gotoSet.equals(gotoItem.gotoSet) &&
                    itemSet.setEquals(gotoItem.itemSet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gotoSet, itemSet);
        }
    }
}
