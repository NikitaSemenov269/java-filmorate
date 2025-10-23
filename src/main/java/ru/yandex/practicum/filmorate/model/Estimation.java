package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Estimation {
    private Long reviewId;
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long userId;
    private Boolean isLike;
}
