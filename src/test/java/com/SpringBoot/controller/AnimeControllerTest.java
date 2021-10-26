package com.SpringBoot.controller;


import com.SpringBoot.domain.Anime;
import com.SpringBoot.util.AnimePutRequestBodyCreate;
import com.SpringBoot.util.AnimeCreator;
import com.SpringBoot.util.AnimePostRequestBodyCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.SpringBoot.request.AnimePostRequestBody;
import com.SpringBoot.request.AnimePutRequestBody;
import com.SpringBoot.service.AnimeService;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class AnimeControllerTest {
    @InjectMocks
    private AnimeController animeController;
    @Mock
    private AnimeService animeServiceMock;

    @BeforeEach
    void setUp(){
       PageImpl<Anime> anime = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(anime);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());

    }
    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void listReturnsListOfAnimesInsidePageObjectWhenSuccessful(){
        String nameExpected = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(nameExpected);
    }
    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void listAllReturnsListOfAnimesWhenSuccessful(){
        String nameExpected = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.listAll().getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(nameExpected);
    }
    @Test
    @DisplayName("find by id returns list of anime when successful")
    void findByIdReturnsListOfAnimesWhenSuccessful(){
        Long idExpected = AnimeCreator.createValidAnime().getId();

        Anime anime = animeController.findById(1).getBody();

        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(idExpected);
    }
    @Test
    @DisplayName("find by Name returns list of anime when successful")
    void findByNameReturnsListOfAnimesWhenSuccessful(){
        String nameExpected = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(nameExpected);
    }
    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByNameReturnsEmptyListOfAnimeWhenAnimeIsNotFound(){
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();

    }
    @Test
    @DisplayName("Save return anime when successful")
    void saveReturnAnimeWhenSuccessful(){
        Anime anime = animeController.save(AnimePostRequestBodyCreate.createAnimeToBeSaved())
                .getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }
    @Test
    @DisplayName("Replace list of anime when successful")
    void replaceUpdateAnimeWhenSuccessful(){

        Assertions.assertThatCode(() ->         animeController.replace(AnimePutRequestBodyCreate
                .createAnimeToBeUpdate())).doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreate.createAnimeToBeUpdate());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }
    @Test
    @DisplayName("Delete anime when successful")
    void DeleteAnimeWhenSuccessful(){

        Assertions.assertThatCode(() ->         animeController.delete(1)).doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.delete(1);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }
}
