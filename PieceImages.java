package org.cis1200.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PieceImages {
    private static final Map<String, BufferedImage> images = new HashMap<>();

    static {
        //load all images for pieces
        loadImage("blackking",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/blackking.png");
        loadImage("blackqueen",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/blackqueen.png");
        loadImage("blackrook",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/blackrook.png");
        loadImage("blackbishop",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/blackbishop.png");
        loadImage("blackknight",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/blackknight.png");
        loadImage("blackpawn",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/blackpawn.png");

        loadImage("whiteking",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/whiteking.png");
        loadImage("whitequeen",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/whitequeen.png");
        loadImage("whiterook",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/whiterook.png");
        loadImage("whitebishop",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/whitebishop.png");
        loadImage("whiteknight",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/whiteknight.png");
        loadImage("whitepawn",
                "/Users/collin/Downloads/hw09_local_temp/src/main/resources/whitepawn.png");
    }

    private static void loadImage(String key, String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            images.put(key, image);
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
        }
    }

    public static Image getImage(Piece piece) {
        String color;
        if (piece.getColor() == 0) {
            color = "white";
        } else {
            color = "black";
        }

        String pieceType = piece.getClass().getSimpleName().toLowerCase();
        String key = color + pieceType;

        BufferedImage image = images.get(key);
        if (image == null) {
            System.err.println("Failed to find image for piece " + pieceType);
            return null;
        }
        return image;
    }



}
