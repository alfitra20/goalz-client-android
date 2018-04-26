package emse.mobisocial.goalz.ui.resource_library;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;

public class ImageExtractor {

    public static String extractImageUrl(String url) throws Exception {
        String contentType = new URL(url).openConnection().getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return url;
            } else if(contentType.contains("pdf/")) {
                return null;
            }
        }

        Document document = Jsoup.connect(url).get();

        String imageUrl = null;

        imageUrl = getImageFromSchema(document);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromOpenGraph(document);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromLinkRel(document);
        if (imageUrl != null) {
            return imageUrl;
        }

        return imageUrl;
    }

    private static String getImageFromLinkRel(Document document) {
        Element link = document.select("link[rel=image_src]").first();
        if (link != null) {
            return link.attr("abs:href");
        }
        return null;
    }

    private static String getImageFromOpenGraph(Document document) {
        Element image = document.select("meta[property=og:image]").first();
        if (image != null) {
            return image.attr("abs:content");
        }
        Element secureImage = document.select("meta[property=og:image:secure]").first();
        if (secureImage != null) {
            return secureImage.attr("abs:content");
        }
        return null;
    }

    private static String getImageFromSchema(Document document) {
        Element container =
                document.select("*[itemscope][itemtype=http://schema.org/ImageObject]").first();
        if (container == null) {
            return null;
        }

        Element image = container.select("img[itemprop=contentUrl]").first();
        if (image == null) {
            return null;
        }
        return image.absUrl("src");
    }
}
