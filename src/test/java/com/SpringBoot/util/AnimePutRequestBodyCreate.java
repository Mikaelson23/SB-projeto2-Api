package com.SpringBoot.util;

import com.SpringBoot.request.AnimePutRequestBody;

public class AnimePutRequestBodyCreate {
    public static AnimePutRequestBody createAnimeToBeUpdate(){
        return AnimePutRequestBody.builder()
                .name(AnimeCreator.createValidUpdatedAnime().getName())
                .id(AnimeCreator.createValidUpdatedAnime().getId())
                .build();
    }
}
