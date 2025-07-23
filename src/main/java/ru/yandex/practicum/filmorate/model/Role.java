package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Role {
    // ПЕРЕДЕЛАТЬ
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Integer id;
    private boolean isFriend;
}
