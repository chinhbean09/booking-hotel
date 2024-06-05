package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Room;
import com.chinhbean.bookinghotel.entities.RoomImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomImageResponse {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("room_id")
    private Long roomId;


    public static RoomImageResponse fromRoomImage(RoomImage roomImage) {

        return RoomImageResponse.builder()
                .id(roomImage.getId())
                .imageUrl(roomImage.getImageUrls())
                .roomId(roomImage.getRoom().getId())
                .build();
    }
}
