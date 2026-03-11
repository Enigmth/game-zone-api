package gamezone.domain.dto.users;

import com.google.gson.annotations.SerializedName;
import gamezone.domain.AbstractEntity;

public class User extends AbstractEntity {
    @SerializedName("email")
    String email;
    @SerializedName("password_hash")
    String passwordHash;
    @SerializedName("first_name")
    String firstName;
    @SerializedName("last_name")
    String lastName;
    @SerializedName("rating")
    int rating;
    @SerializedName("win_streak")
    int winStreak;
    @SerializedName("total_minutes_played")
    int totalMinutesPlayed;
    @SerializedName("preferred_position")
    String preferredPosition;
    @SerializedName("games_played")
    int gamesPlayed;
    @SerializedName("facilities_played_count")
    int facilitiesPlayedCount;
    @SerializedName("phone")
    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public int getTotalMinutesPlayed() {
        return totalMinutesPlayed;
    }

    public void setTotalMinutesPlayed(int totalMinutesPlayed) {
        this.totalMinutesPlayed = totalMinutesPlayed;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getFacilitiesPlayedCount() {
        return facilitiesPlayedCount;
    }

    public void setFacilitiesPlayedCount(int facilitiesPlayedCount) {
        this.facilitiesPlayedCount = facilitiesPlayedCount;
    }

    @Override
    public String validate() {
        return "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
