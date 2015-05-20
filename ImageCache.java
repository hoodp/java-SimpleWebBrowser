package edu.gvsu.cis371;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache for images so that they aren't repeatedly re-loaded from the server (especially while scrolling).
 */
public class ImageCache {

  private Map<MyURL, Image> imageCache = new HashMap<MyURL, Image>();

  /**
   * An interface that allows us to pass a method as a parameter.
   */
  // Passing the "loadImage" method as a parameter allows us to 
  // put the code for loading an image in the browser (where it belongs) 
  public static interface ImageLoader {
    Image loadImage(MyURL url);
  }

  /**
   * Get an image.  Returns the image from the cache (if present), or loads the image from the server, if necessary.
   *
   * @param url    The URL of the image to load.
   * @param loader The method that will load the image (if necessary)
   * @return The desired image, or {@code null} if the image was not available.
   */
  public Image getImage(MyURL url, ImageLoader loader) {
    if (imageCache.containsKey(url)) {
      return imageCache.get(url);
    } else {

      // The image isn't in the cache, so use the loader
      // method to fetch the image from the server.
      Image image = loader.loadImage(url);
      if (image != null) {
        imageCache.put(url, image);
      }
      return image;
    }
  }
}