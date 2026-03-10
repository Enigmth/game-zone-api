package gamezone.domain.dto.games;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateGameRequest {
    @NotBlank
    public String title;
    @NotBlank
    public String location;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate date;
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    public LocalTime time;
    @Positive
    public Integer durationMinutes;
    @NotNull @Min(2) @Max(30)
    public Integer players;
    @NotNull
    public Boolean isFree;
    @Min(0)
    public Integer priceCents;
    @NotNull
    public Boolean isPublic;

    public String sport;

    @jakarta.validation.constraints.Pattern(regexp = "#[0-9A-Fa-f]{6}", message = "teamAColor must be a hex color e.g. #FF5252")
    public String teamAColor;

    @jakarta.validation.constraints.Pattern(regexp = "#[0-9A-Fa-f]{6}", message = "teamBColor must be a hex color e.g. #34D67A")
    public String teamBColor;

    @AssertTrue(message = "priceCents must be > 0 when isFree=false")
    public boolean isPriceValid() {
        if (Boolean.TRUE.equals(isFree)) {
            return priceCents == null || priceCents == 0;
        }
        return priceCents != null && priceCents > 0;
    }
}
