package com.SpringBoot.mapper;

import com.SpringBoot.domain.Anime;
import com.SpringBoot.request.AnimePostRequestBody;
import com.SpringBoot.request.AnimePutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);
    public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);
    public abstract Anime toAnime(AnimePutRequestBody animePostRequestBody);
}
