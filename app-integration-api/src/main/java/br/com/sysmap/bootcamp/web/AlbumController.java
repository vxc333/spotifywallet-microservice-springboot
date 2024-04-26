package br.com.sysmap.bootcamp.web;


import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@Tag(name="Albums", description = "Albums API")
public class AlbumController {

    private final AlbumService albumService;


    @GetMapping("/all")
    @Operation(summary = "Get all albums from Spotify service by Text parameter")
    public ResponseEntity<List<AlbumModel>> getAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search));
    }

    @GetMapping("/my-collection")
    @Operation(summary = "Get all albums from my collection")
    public ResponseEntity<List<Album>> getAllAlbumsFromMyCollection(){
      List<Album> albums = albumService.getAllAlbumsfromCurrentUser();
      return ResponseEntity.ok(albums);
    };

    @PostMapping("/sale")
    @Operation(summary = "Buy an album")
    public ResponseEntity<Album> saveAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(this.albumService.saveAlbum(album));
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Remove an album by ID")
    public ResponseEntity<String> removeAlbum(@PathVariable("id") Long id) {
        albumService.removeAlbumById(id);
        return ResponseEntity.ok("Removed album with id " + id);
    }

}