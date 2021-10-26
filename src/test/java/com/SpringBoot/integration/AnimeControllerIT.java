package com.SpringBoot.integration;


import com.SpringBoot.domain.Anime;
import com.SpringBoot.domain.UsuarioP;
import com.SpringBoot.repository.AnimeRepository;
import com.SpringBoot.repository.UsuarioRepository;
import com.SpringBoot.request.AnimePostRequestBody;
import com.SpringBoot.util.AnimeCreator;
import com.SpringBoot.util.AnimePostRequestBodyCreate;
import com.SpringBoot.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "TestRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateUser;

    @Autowired
    @Qualifier(value = "TestRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateAdmin;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final UsuarioP USER = UsuarioP.builder()
            .name("teste")
            .password("{bcrypt}$2a$10$UQ7udcClmm/0tK9cjm2CEujXND1mOENLH4pLSji/qZOikIeu9tfF6")
            .userName("teste")
            .authorities("ROLE_USER")
            .build();

    private static final UsuarioP ADMIN = UsuarioP.builder()
            .name("kell")
            .password("{bcrypt}$2a$10$UQ7udcClmm/0tK9cjm2CEujXND1mOENLH4pLSji/qZOikIeu9tfF6")
            .userName("kell")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "TestRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("teste","2233");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "TestRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("kell","2233");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(USER);
        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll returns list of anime when successful")
    void listAllReturnsListOfAnimesWhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(USER);

        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplateUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("find by id returns list of anime when successful")
    void findByIdReturnsListOfAnimesWhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(USER);

        Long idExpected = animeSaved.getId();

        Anime anime = testRestTemplateUser.getForObject("/animes/{id}", Anime.class, idExpected);

        Assertions.assertThat(anime)
                .isNotNull();

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(idExpected);
    }

    @Test
    @DisplayName("find by Name returns list of anime when successful")
    void findByNameReturnsListOfAnimesWhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(USER);

        String nameExpected = animeSaved.getName();

        String stringUrl = String.format("/animes/find?name=%s", nameExpected);

        List<Anime> animes = testRestTemplateUser.exchange(stringUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(nameExpected);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByNameReturnsEmptyListOfAnimeWhenAnimeIsNotFound() {
        usuarioRepository.save(USER);

        List<Anime> animes = testRestTemplateUser.exchange
                ("/animes/find?name=saintSeiya", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }
    
    @Test
    @DisplayName("Save return anime when successful")
    void saveReturnAnimeWhenSuccessful() {
        usuarioRepository.save(USER);

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreate.createAnimeToBeSaved();

        ResponseEntity<Anime> animeResponseEntity = testRestTemplateUser.postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();

        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("Replace list of anime when successful")
    void replaceUpdateAnimeWhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(USER);

        animeSaved.setName("new name");

        ResponseEntity<Void> animeResponseEntity = testRestTemplateUser.exchange
                ("/animes",HttpMethod.PUT,new HttpEntity<>(animeSaved), Void.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Delete anime when successful")
    void DeleteAnimeWhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(USER);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateUser.exchange
                ("/animes/admin/{id}",HttpMethod.DELETE,null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

    @Test
    @DisplayName("Delete anime when successful Role admin")
    void DeleteAnimeWhenSuccessfulRoleAdmin() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        usuarioRepository.save(ADMIN);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateAdmin.exchange
                ("/animes/admin/{id}",HttpMethod.DELETE,null, Void.class, animeSaved.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();

        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }
}