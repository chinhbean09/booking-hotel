package com.chinhbean.bookinghotel.responses.feedback;

import com.chinhbean.bookinghotel.entities.Feedback;
import com.chinhbean.bookinghotel.responses.hotel.HotelResponse;
import com.chinhbean.bookinghotel.responses.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("hotel")
    private HotelResponse hotel;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

    public static FeedbackResponse fromFeedback(Feedback feedback) {
        UserResponse userResponse = UserResponse.fromUser(feedback.getUser());
        HotelResponse hotelResponse = HotelResponse.fromHotel(feedback.getHotel());

        return FeedbackResponse.builder()
                .id(feedback.getId())
                .user(userResponse)
                .hotel(hotelResponse)
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .build();
    }
}
