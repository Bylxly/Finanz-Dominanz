package Server.Field.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert eine Farbgruppe im Spiel.
 * Eine Farbgruppe besteht aus mehreren Straßen, die derselben Farbe zugeordnet sind.
 */
public class ColorGroup implements Serializable {
    private final String colorName; // Der Name der Farbe.
    private final String colorCode; // Der Farbcode für die Konsolenausgabe.
    private final List<Street> streets; // Die Liste der Straßen in dieser Farbgruppe.

    /**
     * Konstruktor für eine Farbgruppe.
     *
     * @param colorName Der Name der Farbe.
     * @param colorCode Der Farbcode für die Konsolenausgabe.
     */
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

    /**
     * Überprüft, ob die Farbgruppe vollständig ist.
     * Eine Farbgruppe ist vollständig, wenn alle Straßen derselben Farbgruppe demselben Spieler gehören.
     *
     * @return true, wenn die Farbgruppe vollständig ist, sonst false.
     */
    public boolean isComplete() {
        for (Street street : streets) {
            if (street.getOwner() == null) {
                return false;
            }
            else if (!street.getOwner().equals(streets.get(0).getOwner())) {
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
