package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AlbumControllerTest {

    @InjectMocks
    private AlbumController albumController;

    @Mock
    private AlbumService albumService;

    public AlbumControllerTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAlbums() throws IOException, ParseException, SpotifyWebApiException {
        // Arrange
        String search = "search query";
        List<AlbumModel> expectedAlbums = new ArrayList<>();
        when(albumService.getAlbums(search)).thenReturn(expectedAlbums);

        // Act
        ResponseEntity<List<AlbumModel>> responseEntity = albumController.getAlbums(search);

        // Assert
        assertEquals(expectedAlbums, responseEntity.getBody());
    }

    @Test
    void testGetAllAlbumsFromMyCollection() {
        // Arrange
        List<Album> expectedAlbums = new ArrayList<>();
        when(albumService.getAllAlbumsfromCurrentUser()).thenReturn(expectedAlbums);

        // Act
        ResponseEntity<List<Album>> responseEntity = albumController.getAllAlbumsFromMyCollection();

        // Assert
        assertEquals(expectedAlbums, responseEntity.getBody());
    }

    @Test
    void testSaveAlbum() {
        // Arrange
        Album album = Album.builder().build();
        when(albumService.saveAlbum(any(Album.class))).thenReturn(album);

        // Act
        ResponseEntity<Album> responseEntity = albumController.saveAlbum(album);

        // Assert
        assertEquals(album, responseEntity.getBody());
    }

    @Test
    void testRemoveAlbum() {
        // Arrange
        Long id = 1L;

        // Act
        ResponseEntity<String> responseEntity = albumController.removeAlbum(id);

        // Assert
        assertEquals("Removed album with id " + id, responseEntity.getBody());
    }
}
