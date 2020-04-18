package parser;

import java.util.*;

public class Item {
    public List<String> units;
    public int item;
    public String lookahead;

    public Item(List<String> units, int item, String lookahead) {
        this.units = units;
        this.item = item;
        this.lookahead = lookahead;
    }

    /** return the next status of this item */
    public Item afterItem() {
        if(units.size() < item)
            return null;
        return new Item(this.units, item + 1, lookahead);
    }

    /** judge if the next item unit is the specific unit */
    public boolean ifItemNext(String s) {
        if(item == units.size())
            return false;
        return units.get(item).equals(s);
    }

    /** judge if the next item is non-terminal */
    public boolean ifNonTerminalNext(Set<String> set) {
        for(String s: set) {
            if(ifItemNext(s))
                return true;
        }
        return false;
    }

    /** return the next next units of the item */
    public String nextNextUnit() {
        if(units.size() == item + 1)
            return "$";
        else if(units.size() > item)
            return units.get(item + 1);
        else
            return null;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        int i;
        for(i = 0; i < units.size(); i++) {
            if(i == item)
                s.append("[.] ");
            s.append(units.get(i) + " ");
        }
        if(i == item)
            s.append("[.] ");
        s.append("\tlookahead: " + lookahead);
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item1 = (Item) o;

        if(units.size() != item1.units.size())
            return false;
        for(int i = 0; i < units.size(); i++) {
            if(!units.get(i).equals(item1.units.get(i)))
                return false;
        }

        return item == item1.item &&
                lookahead.equals(item1.lookahead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(units, item, lookahead);
    }
}
