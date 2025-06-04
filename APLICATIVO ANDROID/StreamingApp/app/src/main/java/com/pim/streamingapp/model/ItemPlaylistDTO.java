package com.pim.streamingapp.model;

public class ItemPlaylistDTO {
    public int playlistID;
    public int conteudoID;

    public ItemPlaylistDTO(int playlistID, int conteudoID) {
        this.playlistID = playlistID;
        this.conteudoID = conteudoID;
    }
}
