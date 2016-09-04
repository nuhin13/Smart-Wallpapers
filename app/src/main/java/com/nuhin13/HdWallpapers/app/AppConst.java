package com.nuhin13.HdWallpapers.app;


public class AppConst {
    // Number of columns of Grid View
    // by default 2 but user can configure this in settings activity
    public static final int NUM_OF_COLUMNS = 2;

    // Gridview image padding
    public static final int GRID_PADDING = 0; // in dp

    // Gallery directory name to save wallpapers
    public static final String SDCARD_DIR_NAME = "Smart Wallpapers";

    // Picasa/Google web album username
    public static final String PICASA_USER = "nuhin2323";

    // Public albums list url
    public static final String URL_PICASA_ALBUMS = "https://picasaweb.google.com/data/feed/api/user/101754985895842901747?kind=album&alt=json";

    // Picasa album photos url
    public static final String URL_ALBUM_PHOTOS = "https://picasaweb.google.com/data/feed/api/user/101754985895842901747/albumid/_ALBUM_ID_?alt=json";

    // Picasa recenlty added photos url
    public static final String URL_RECENTLY_ADDED = "https://picasaweb.google.com/data/feed/api/user/101754985895842901747?kind=photo&alt=json";
}
