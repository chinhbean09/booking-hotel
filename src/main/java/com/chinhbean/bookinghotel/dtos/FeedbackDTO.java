package com.chinhbean.bookinghotel.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    @NotNull(message = "Hotel ID cannot be null")
    @Min(value = 1, message = "Hotel ID must be greater than 0")
    private Long hotelId;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be greater than 1")
    @Max(value = 10, message = "Rating cannot be more than 10")
    private Double rating;

    @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
    private String comment;
}
