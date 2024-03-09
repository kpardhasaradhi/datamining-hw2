import java.util.*;
import java.io.*;

public class Apriori {

    public static void main(String args[]) {
        try {
            String fileName = args[0];
            int minimumSupportPercentage = Integer.parseInt(args[1]);
            List<Set<String>> database = Utils.readTransactions(fileName);

            float minSupport = (float) minimumSupportPercentage / 100;
            float minimumSupportValue = minSupport * database.size();
            System.out.print("Total database size = ");
            System.out.print(database.size());
            System.out.print(", Minimum support percentage = ");
            System.out.print(minimumSupportPercentage);
            System.out.print(", Calculated minimum support value = ");
            System.out.println(minimumSupportValue);

            Set<String> uniqueItems = Utils.getUniqueItems(database);

            int level = 1;

            List<Set<String>> candidates = new ArrayList<>();
            List<List<Set<String>>> eachLevelFrequentItems = new ArrayList<>();

            while (true) {
                System.out.println("****************** LEVEL ******************" );
                System.out.println("Level " + level + ": ");
                if (level == 1) {
                    candidates = Utils.generateFirstCandidate(uniqueItems);
                    System.out.println("Candidates : " + candidates);
                } else {
                    candidates = Utils.generateCandidates(eachLevelFrequentItems.get(level - 2), level);
                    System.out.println("Candidates : " + candidates);
                }

                if (candidates.isEmpty())
                    break;

                List<Set<String>> presentLevelFrequentItems = new ArrayList<>();
                for (Set<String> candidate : candidates) {
                    int candidateSupport = Utils.getCandidateSupport(candidate, database);
                    System.out.println(candidate + " => " + candidateSupport);
                    if (candidateSupport >= minimumSupportValue) {
                        presentLevelFrequentItems.add(candidate);
                    }
                }

                System.out.println("Present Level Frequent Items with (Minimum Support Value " + minimumSupportValue + ") = " + presentLevelFrequentItems);

                if (presentLevelFrequentItems.isEmpty())
                    break;

                eachLevelFrequentItems.add(level - 1, presentLevelFrequentItems);
                level++;
                candidates = null;
            }
            System.out.println("\n\nFinal frequent itemsets = " + eachLevelFrequentItems);
        }catch (IOException ioException){
            System.out.println(ioException.getStackTrace());
        }
    }
}

class Utils {
    public static List<Set<String>> readTransactions(String filePath) throws IOException {
        List<Set<String>> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(" ");
                Set<String> transaction = new HashSet<>();
                for (String element : elements) {
                    transaction.add(element);
                }
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public static int getCandidateSupport(Set<String> candidate, List<Set<String>> database) {
        int count = 0;
        for (Set<String> record : database) {
            if (record.containsAll(candidate)) {
                count++;
            } else {
                boolean areAllElementsContained = true;
                for (String element : candidate) {
                    if (!record.contains(element)) {
                        areAllElementsContained = false;
                        break;
                    }
                }
                if (areAllElementsContained) {
                    count++;
                }
            }
        }
        return count;
    }

    public static List<Set<String>> computeCombinations(Set<String> values, int size) {
        List<Set<String>> combinations = new ArrayList<>();
        computeCombinationsUtil(values, size, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void computeCombinationsUtil(Set<String> values, int size, List<String> presentCombination, List<Set<String>> combinations) {
        if (size == 0) {
            combinations.add(new HashSet<>(presentCombination));
            return;
        }
        if (values.isEmpty()) {
            return;
        }
        String[] valuesArray = values.toArray(new String[0]);
        for (int i = 0; i < valuesArray.length; i++) {
            String presentValue = valuesArray[i];
            List<String> remainingValues = new ArrayList<>(Arrays.asList(valuesArray).subList(i + 1, valuesArray.length));
            presentCombination.add(presentValue);
            computeCombinationsUtil(new HashSet<>(remainingValues), size - 1, presentCombination, combinations);
            presentCombination.remove(presentValue);
        }
    }

    public static List<Set<String>> generateCandidates(List<Set<String>> previousFrequentItemSet, int candidateSetSize) {
        Set<String> uniqueItems = getUniqueItems(previousFrequentItemSet);
        List<Set<String>> combinations = computeCombinations(uniqueItems, candidateSetSize);
        return combinations;
    }

    public static List<Set<String>> generateFirstCandidate(Set<String> uniqueItems) {
        List<Set<String>> firstCandidateList = new ArrayList<>();
        for (String uniqueItem : uniqueItems) {
            HashSet<String> oneElementSet = new HashSet<>();
            oneElementSet.add(uniqueItem);
            firstCandidateList.add(oneElementSet);
        }
        return firstCandidateList;
    }

    public static Set<String> getUniqueItems(List<Set<String>> database) {
        Set<String> uniqueItems = new HashSet<>();
        for (Set<String> transaction : database) {
            for (String item : transaction) {
                uniqueItems.add(item);
            }
        }
        return uniqueItems;
    }
}
