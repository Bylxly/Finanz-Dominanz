package Server.Field.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColorGroup implements Serializable {
    private final String colorName;
    private final String colorCode;
    private final List<Street> streets;

    public ColorGroup(String colorName, String colorCode) {
        this.colorName = colorName;
        this.colorCode = colorCode;
        this.streets = new ArrayList<>();
    }

    public void addStreet(Street street) {
        if (!streets.contains(street)) {
            streets.add(street);
        }
    }

    public boolean isComplete() {
        for (Street street : streets) {
            if (!street.getOwner().equals(streets.get(0).getOwner())) {
                return false;
            }
        }
        return true;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public String getColorName() {
        return colorName;
    }

    public String getColorCode() {
        return colorCode;
    }
}
