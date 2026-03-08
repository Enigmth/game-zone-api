package gamezone.domain.dto.users;

import jakarta.validation.constraints.NotBlank;

public record PatchUserProfileRequest(@NotBlank String preferredPosition) {}
