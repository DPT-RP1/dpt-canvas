package com.sony.dpt.media;

import java.io.IOException;

/**
 * This represent a flippable image group, the original intent being
 * to abstract cbz/cbr files
 */
public interface ImagePack {

    byte[] page(int i) throws IOException;

    int pageCount();

    int currentPage();

    void close() throws IOException;
}
