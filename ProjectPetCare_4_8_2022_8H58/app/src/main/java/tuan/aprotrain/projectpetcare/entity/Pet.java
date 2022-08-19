package tuan.aprotrain.projectpetcare.entity;

public class Pet {
    private String petName;

    private String gender;
    private String kind;
    private String species;

    private float petHeight;
    private float petWeight;
    private String birthDate;
    private String color;
    private String intact;
    private String notes;

    private long petId;
    private String userId;

    public Pet() {
    }

    public Pet(long petId, String petName) {
        this.petId = petId;
        this.petName = petName;
    }

    public Pet(String petName, String gender, String breed, String species, float petHeight, float petWeight, String birthDate, String color, String intact, String notes, int petId, String userId) {
        this.petName = petName;
        this.gender = gender;
        this.kind = breed;
        this.species = species;
        this.petHeight = petHeight;
        this.petWeight = petWeight;
        this.birthDate = birthDate;
        this.color = color;
        this.intact = intact;
        this.notes = notes;
        this.petId = petId;
        this.userId = userId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public float getPetHeight() {
        return petHeight;
    }

    public void setPetHeight(float petHeight) {
        this.petHeight = petHeight;
    }

    public float getPetWeight() {
        return petWeight;
    }

    public void setPetWeight(float petWeight) {
        this.petWeight = petWeight;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIntact() {
        return intact;
    }

    public void setIntact(String intact) {
        this.intact = intact;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getPetId() {
        return petId;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
