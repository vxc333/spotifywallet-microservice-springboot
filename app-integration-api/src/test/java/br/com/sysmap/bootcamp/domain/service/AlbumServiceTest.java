package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AlbumServiceTest {

    @Mock
    private Queue queue;

    @Mock
    private RabbitTemplate template;

    @Mock
    private SpotifyApi spotifyApi;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAlbums() throws IOException, SpotifyWebApiException, ParseException {
        String search = "query";
        List<AlbumModel> expectedAlbums = new ArrayList<>();
        when(spotifyApi.getAlbums(search)).thenReturn(expectedAlbums);

        List<AlbumModel> result = albumService.getAlbums(search);

        assertSame(expectedAlbums, result);
    }

    @Test
    void testSaveAlbum() {
        Album album = Album.builder().build();
        Users user = Users.builder().build();
        when(usersService.findByEmail(anyString())).thenReturn(user);
        when(albumRepository.existsByIdSpotify(any())).thenReturn(false);
        when(albumRepository.save(album)).thenReturn(album);

        Album result = albumService.saveAlbum(album);

        assertNotNull(result);
        assertSame(album, result);
        verify(template, times(1)).convertAndSend(any(), any(WalletDto.class));
    }

    @Test
    void testGetAllAlbumsfromCurrentUser() {
        Users user = Users.builder().build();
        when(usersService.findByEmail(anyString())).thenReturn(user);
        List<Album> expectedAlbums = new ArrayList<>();
        when(albumRepository.findAllByUsers(user)).thenReturn(expectedAlbums);

        List<Album> result = albumService.getAllAlbumsfromCurrentUser();
        assertSame(expectedAlbums, result);
    }

    @Test
    void testRemoveAlbumById() {
        long id = 1L;
        Users user = Users.builder().build();
        Album album = Album.builder().id(id).build();
        album.setUsers(user);
        when(albumRepository.findById(id)).thenReturn(java.util.Optional.of(album));
        when(usersService.findByEmail(anyString())).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn("username");

        albumService.removeAlbumById(id);

        verify(albumRepository, times(1)).delete(album);
    }
}
