//package com.chinhbean.bookinghotel.responses;
//
//import com.chinhbean.bookinghotel.entities.RoomConvenience;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class ConvenienceRoomResponse {
//
//    @JsonProperty("id")
//    private Long id;
//
//    @JsonProperty("wardrobe")
//    private Boolean wardrobe;
//
//    @JsonProperty("air_conditioning")
//    private Boolean airConditioning;
//
//    @JsonProperty("tv")
//    private Boolean tv;
//
//    @JsonProperty("wifi")
//    private Boolean wifi;
//
//    @JsonProperty("toiletries")
//    private Boolean toiletries;
//
//    @JsonProperty("kitchen")
//    private Boolean kitchen;
//
//    public static ConvenienceRoomResponse fromConvenienceRoom(RoomConvenience roomConvenience) {
//        return ConvenienceRoomResponse.builder()
//                .id(roomConvenience.getId())
//                .wardrobe(roomConvenience.getWardrobe())
//                .airConditioning(roomConvenience.getAirConditioning())
//                .tv(roomConvenience.getTv())
//                .wifi(roomConvenience.getWifi())
//                .toiletries(roomConvenience.getToiletries())
//                .kitchen(roomConvenience.getKitchen())
//                .build();
//    }
//}
