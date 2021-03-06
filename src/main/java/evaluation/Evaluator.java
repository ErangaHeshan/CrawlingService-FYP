package evaluation;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by roshanalwis on 9/2/17.
 */

public class Evaluator {
    private static ArrayList<String> getUnion(ArrayList<String> list1, ArrayList<String> list2) {
        /*
            Get union of two lists
         */

        ArrayList<String> list = new ArrayList<>();
        for(String str: list1) {
            if(!list.contains(str)) {
                list.add(str);
            }
        }

        for(String str: list2) {
            if(!list.contains(str)) {
                list.add(str);
            }
        }

        return list;
    }

    private static int count(ArrayList<String> list, String word) {
        /*
            Returns number of occurrences
         */

        int count = 0;
        for(String str: list) {
            if(str.equals(word)) {
                count++;
            }
        }

        return count;
    }

    private static int dotProduct(int[] arrayA, int[] arrayB){
        /*
            Calculate dot product between given two vectors
         */

        int result = 0;
        assert(arrayA.length == arrayB.length);

        for(int i=0; i<arrayA.length; i++){
            result += arrayA[i] * arrayB[i];
        }

        return result;
    }

    private static double modulus(int[] array){
        /*
            Measure modules of a vector
         */

        double result = 0;

        for(int i=0; i<array.length; i++){
            result += array[i] * array[i];
        }

        result = Math.sqrt(result);

        return result;
    }

    private static double cosineSimilarity(ArrayList<String> list1, ArrayList<String> list2) {
        /*
            Measure cosine similarity between two word lists
         */

        double result;

        ArrayList<String> commonList = getUnion(list1, list2);
        int[] vector1 = new int[commonList.size()];
        int[] vector2 = new int[commonList.size()];

        for(int i=0; i<commonList.size(); i++){
            vector1[i] = count(list1, commonList.get(i));
            vector2[i] = count(list2, commonList.get(i));
        }

        result = dotProduct(vector1, vector2) / (modulus(vector1) * modulus(vector2));

        return result;
    }

    private static int[] tagContentDiff(ArrayList<ArrayList<String>> oldVersion,
                                        ArrayList<ArrayList<String>> newVersion) {
        /*
            Compare new version with old version by iterating through content of new version
         */

        final int[] count = {0, 0};

        boolean found = false;
        double similarityValue = 0.0;

        for(int i=0; i<oldVersion.size(); i++){
            found = false;
            for (int j = 0; j < newVersion.size(); j++) {
                similarityValue = cosineSimilarity(oldVersion.get(i), newVersion.get(j));
                if(similarityValue > 0.9){
                    // Match count
                    count[0]++;
                    found = true;
                    break;
                }
            }

            if(!found){
                // Mismatch count
                count[1]++;
            }
        };

        return count;
    }

    public static HashMap<String, int[]> diff(HashMap<String, ArrayList<ArrayList<String>>> oldVersion,
                            HashMap<String, ArrayList<ArrayList<String>>> newVersion) {
        /*
            Measure difference between two versions based on the cosine similarity measure
         */

        // Get tags for each version
        Set<String> tagSet1 = oldVersion.keySet();
        Set<String> tagSet2 = newVersion.keySet();

        // Get common tags for both versions
        ArrayList<String> commonTags = getUnion(new ArrayList<String>(tagSet1), new ArrayList<String>(tagSet2));

        // Store difference measurements
        HashMap<String, int[]> diffInfo = new HashMap<>();

        final String[] tag = {null};
        IntStream.range(0, commonTags.size()).parallel().forEach((int i) -> {
            tag[0] = commonTags.get(i);
            diffInfo.put(tag[0], tagContentDiff(oldVersion.get(tag[0]), newVersion.get(tag[0])));
        });

        return diffInfo;
    }
}
