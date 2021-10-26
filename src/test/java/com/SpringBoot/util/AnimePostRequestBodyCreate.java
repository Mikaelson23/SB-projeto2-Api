package com.SpringBoot.util;

import com.SpringBoot.request.AnimePostRequestBody;

public class AnimePostRequestBodyCreate {
    public static AnimePostRequestBody createAnimeToBeSaved(){
        return AnimePostRequestBody.builder().name(AnimeCreator.createAnimeToBeSaved().getName()).build();
    }
}
