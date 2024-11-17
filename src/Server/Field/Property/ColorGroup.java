package Server.Field.Property;

import java.util.ArrayList;
import java.util.List;

public class ColorGroup {
    private final String color;
    private final List<Street> streets;

    public ColorGroup(String color) {
        this.color = color;
        this.streets = new ArrayList<>();
    }

    public void addStreet(Street street) {
        if (!streets.contains(street)) {
            streets.add(street);
        }
    }

    public boolean isComplete() {
        for (Street street : streets) {
            if (!street.getHasHotel()) {
                return false;
            }
        }
        return true;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public String getColor() {
        return color;
    }
}
