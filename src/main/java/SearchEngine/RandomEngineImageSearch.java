package SearchEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomEngineImageSearch {

    private static final int GOOGLE = 0;
    private static final int YANDEX = 1;
    private static final int AZURE = 2;

    public static String getRandomImage(String searchTerm) throws SearchEngineException {

        List<Integer> imageServices = Arrays.asList(YANDEX, GOOGLE, AZURE);
        Collections.shuffle(imageServices);

        String imageSrc = null;

        for (Integer imageService : imageServices) {
            if (imageService == YANDEX) {
                YandexImageSearch yandexSearch = new YandexImageSearch(searchTerm);
                imageSrc = yandexSearch.get();
            } else if (imageService == GOOGLE) {
                GoogleImageSearch googleSearch = new GoogleImageSearch(searchTerm);
                imageSrc = googleSearch.get();
            } else if (imageService == AZURE) {
                AzureImageSearch azureSearch = new AzureImageSearch(searchTerm);
                imageSrc = azureSearch.get();
            }
            if (imageSrc != null) {
                return imageSrc;
            }
        }

        throw new SearchEngineException("Не нашёл");
    }

}
