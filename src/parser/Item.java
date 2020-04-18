package parser;

import java.util.*;

public class Item {
    public List<String> units;
    public int item;
    public String lookahead;

    public Item(List<String> units, int item, String lookahead) {
        this.units = units;
        this.item = item;
    }

    /** return the next status of this item */
    public Item afterItem() {
        if(units.size() <= item)
            return null;
        return new Item(this.units, item + 1, lookahead);
    }

    /** judge if the next item unit is the specific unit */
    public boolean ifItemNext(String s) {
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
            return "end";
        else if(units.size() > item)
            return units.get(item + 1);
        else
            return null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int i;
        for(i = 0; i < units.size(); i++) {
            if(i == item)
                s.append("· ");
            s.append(units.get(i) + " ");
        }
        if(i == item)
            s.append("· ");
        s.append(lookahead);
        return s.toString();
    }
}
