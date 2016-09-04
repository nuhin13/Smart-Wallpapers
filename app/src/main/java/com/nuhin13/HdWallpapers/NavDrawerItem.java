package com.nuhin13.HdWallpapers;

public class NavDrawerItem {

    private boolean showNotify;
    private String albumId, albumTitle, albumPhotoCount;
    // boolean flag to check for recent album
    private boolean isRecentAlbum = false;
    private boolean active;

    public NavDrawerItem() {
    }

    public NavDrawerItem(boolean showNotify, String albumId, String albumTitle, String albumPhotoCount) {
        this.showNotify = showNotify;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.albumPhotoCount = albumPhotoCount;
    }

    public NavDrawerItem(String albumId, String albumTitle, String albumPhotoCount,
                         boolean isRecentAlbum) {
        this.albumTitle = albumTitle;
        this.albumPhotoCount = albumPhotoCount;
        this.isRecentAlbum = isRecentAlbum;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getPhotoNo() {
        return albumPhotoCount;
    }

    public void setPhotoNo(String photoNo) {
        this.albumPhotoCount = photoNo;
    }

    public String getTitle() {
        return this.albumTitle;
    }

    public void setTitle(String title) {
        this.albumTitle = title;
    }
    public boolean isRecentAlbum() {
        return isRecentAlbum;
    }

    public void setRecentAlbum(boolean isRecentAlbum) {
        this.isRecentAlbum = isRecentAlbum;

    }


}