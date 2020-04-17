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

    /**
     * return the next status of this item
     * @return next status item
     */
    public Item afterItem() {
        if(units.size() <= item + 1)
            return null;
        return new Item(this.units, item + 1, lookahead);
    }

    /** judge if the next item unit is the specific unit */
    public boolean ifItemNext(String s) {
        return units.get(item).equals(s);
    }
}
