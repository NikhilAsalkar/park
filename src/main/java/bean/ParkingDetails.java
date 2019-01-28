package bean;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class ParkingDetails {

        @JsonProperty("_id")
        private ObjectId id;

        public String color;
        public String registerNumber;
        public double level;
        public double slot;
        boolean isFilled;

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getRegisterNumber() {
            return registerNumber;
        }

        public void setRegisterNumber(String registerNumber) {
            this.registerNumber = registerNumber;
        }

        public double getLevel() {
            return level;
        }

        public void setLevel(double level) {
            this.level = level;
        }

        public double getSlot() {
            return slot;
        }

        public void setSlot(double slot) {
            this.slot = slot;
        }

    @Override
    public String toString() {
        return "ParkingDetails{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", registerNumber='" + registerNumber + '\'' +
                ", level=" + level +
                ", slot=" + slot +
                '}';
    }
}

