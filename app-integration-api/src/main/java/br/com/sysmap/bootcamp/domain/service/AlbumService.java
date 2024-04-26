package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApi spotifyApi;
    private final AlbumRepository albumRepository;
    private final UsersService usersService;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        return this.spotifyApi.getAlbums(search);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Album saveAlbum(Album album) {
        Users user =getUser();
        if (albumRepository.existsByIdSpotify(album.getIdSpotify())) {
            throw new IllegalArgumentException("Album with ID exists " + album.getIdSpotify() + " already exists");
        }

        album.setUsers(user);
        Album albumSaved = albumRepository.save(album);

        WalletDto walletDto = new WalletDto(user.getEmail(), albumSaved.getValue());
        this.template.convertAndSend(queue.getName(), walletDto);

        return albumSaved;
    }
    @Transactional(readOnly = true)
    public List<Album> getAllAlbumsfromCurrentUser(){
        Users user = getUser();
        return albumRepository.findAllByUsers(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAlbumById(Long id) {
        Album album = albumRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Album with ID " + id + " does not exist"));

        Users user = getUser();
        if (!album.getUsers().equals(user)) {
            throw new IllegalArgumentException("You are not allowed to remove this album");
        }
        albumRepository.delete(album);
    }

    Users getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return usersService.findByEmail(username);
    }


}