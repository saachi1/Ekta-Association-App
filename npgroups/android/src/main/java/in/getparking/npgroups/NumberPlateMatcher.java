package in.getparking.npgroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberPlateMatcher implements NumberPlateMatcherInterface {
    private static final String TAG = NumberPlateMatcher.class.getSimpleName();

    private final Pattern districtstateNotOkPattern;

    private String start_str = "^"; // Start of the string

    private String nonGr = ".*?";    // Non-greedy match on filler

    /************* Ideal Case Pattern****************/

    private String state = "((?:(?:AP)|(?:AR)|(?:AS)|(?:BR)|(?:CG)|(?:GA)|(?:GJ)|(?:HR)|(?:HP)|(?:JK)|(?:JH)|(?:KA)|(?:KL)" +

            "|(?:MP)|(?:MH)|(?:MN)|(?:ML)|(?:MZ)|(?:NL)|(?:OD)|(?:OR)|(?:PB)|(?:RJ)|(?:SK)|(?:TN)|(?:TS)|(?:TR)" +

            "|(?:UA)|(?:UK)|(?:UP)|(?:WB)|(?:AN)|(?:CH)|(?:DN)|(?:DD)|(?:DL)|(?:LD)|(?:PY)))(?![a-z])";    // INDIA States

    private String district = "((?:[0-9]{1,2}))";    // Integer Number 1// Non-greedy match on filler
    // (?<=[A-Z])[0-9]{1,2}(?![0-9])

    private String district_spl = "((?:[A-Z]{0,1}))";

    private String series = "((?:[A-Z]{0,3}))";    // Word 1

    private String serialNo = "((?:\\d{1,4}))";    // Integer Number 2

    /********************************************/

    /************* State Not ok rest ok district****************/

    private String stateNotOk = "((?:[A-Z]{2}))";    // No Proper State Match but rest of them match

    /********************************************/

    /**********************District Pattern Issue**************/

    private String districtNotOk = "((?:\\w{1,2}))";

    /**********************************************************/

    private String zeroRegex = "[DOUVQ]";

    private String oneRegex = "[IJLT]";

    private String twoRegex = "[Z]";

    private String fourRegex = "[AHL]";

    private String sixRegex = "[CG]";

    private String eightRegex = "[SB]";

    private String noneRegex = "[^0-9]";   //"[KMNPRWXY-/+&%$#@!.?,]";

    private String stateStartCharRegex = "[ABCGHJKMNOPRSTUWDL]";

    private String stateEndCharRegex = "[PRSGAJKHLNZDBY]";

    private Map<String, List<String>> stateFirstToSecondMap = new HashMap<>();

    private Map<String, List<String>> stateSecondToFirstMap = new HashMap<>();

    private Pattern idealPattern;

    private Pattern stateNotOkPattern;

    private Pattern districtNotOkPattern;

    private Map<String, Integer> stateToDistrictMap = new HashMap<>();

    private Map<String, Double> scoreMap = new HashMap<>();

    private static Map<String, Double> sortByValue(final Map<String, Double> wordCounts) {

        return wordCounts.entrySet()

                .stream()

                .sorted((Map.Entry.<String, Double>comparingByValue().reversed()))

                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    public NumberPlateMatcher() {

        idealPattern = Pattern.compile(state + district + district_spl + series + serialNo);

        stateNotOkPattern = Pattern.compile(stateNotOk + district + district_spl + series + serialNo);

        districtNotOkPattern = Pattern.compile(state + districtNotOk + district_spl + series + serialNo);

        districtstateNotOkPattern = Pattern.compile(stateNotOk + districtNotOk + district_spl + series + serialNo);

        prepareStateMaps();

    }

    String[] states = new String[]{"AP", "AR", "AS", "BR", "CG", "GA", "GJ", "HR", "HP", "JK", "JH", "KA", "KL", "MP", "MH",
            "MN", "ML", "MZ", "NL", "OD", "OR", "PB", "RJ", "SK", "TN", "TS", "TR", "UA", "UK", "UP", "WB", "AN", "CH", "DN", "DD", "DL", "LD", "PY"};

    /***
     * Prepare a map that, given a character (alphabet) that is the first character of a state code, we can retrieve
     * all possible second characters that would result in a valid state code. So if first character is 'M', the permitted
     * second characters are 'H', 'L, 'N', 'P' and 'Z' for the state codes corresponding to Maharashtra, Meghalaya, Manipur,
     * Madhya Pradesh and Mizoram.
     * We also do a similar exercise where given the second character, we map it to all permitted first characters.
     */
    private void prepareStateMaps() {

        for (String state : states) {

            String firstChar = state.substring(0, 1);
            String secondChar = state.substring(1, 2);

            List<String> secondChars = stateFirstToSecondMap.get(firstChar);

            if (secondChars != null && !secondChars.contains(secondChar)) {
                secondChars.add(secondChar);
            }
            else if (secondChars == null) {
                secondChars = new ArrayList<>();
                secondChars.add(secondChar);
            }

            stateFirstToSecondMap.put(firstChar, secondChars);

            List<String> firstChars = stateSecondToFirstMap.get(secondChar);

            if (firstChars != null && !firstChars.contains(firstChar)) {
                firstChars.add(firstChar);
            }
            else if (firstChars == null) {
                firstChars = new ArrayList<>();
                firstChars.add(firstChar);
            }

            stateSecondToFirstMap.put(secondChar, firstChars);
        }
    }



    @Override
    public List<String> validNumberPlate(String numberPlate) {

        List<String> numberPlates = new ArrayList<>();

        String np = numberPlate.replaceAll("[^\\w]", "").toUpperCase().replace("IND","");

        Matcher m = idealPattern.matcher(np);

//        //System.out.println("Validating : " + numberPlate);

        if (m.find() && getValidString(m).length() > 6) {

            numberPlates.add(getValidString(m));

            scoreMap.put(m.group(1), scoreMap.get(m.group(1)) == null ? 1D : scoreMap.get(m.group(1)) + 1D);

            return numberPlates;

        }

        Matcher m1 = districtNotOkPattern.matcher(np);

        if (m1.find()) {

            scoreMap.put(m1.group(1), scoreMap.get(m1.group(1)) == null ? 1D : scoreMap.get(m1.group(1)) + 1D);

            return getCorrectPlate(getValidString(m1), true, m1, false);

        }

        Matcher m2 = districtstateNotOkPattern.matcher(np);

        if (m2.find()) {

            scoreMap.put(m2.group(1), scoreMap.get(m2.group(1)) == null ? 1D : scoreMap.get(m2.group(1)) + 1D);

            return getCorrectPlate(getValidString(m2), true, m2, true);

        }

        Matcher m3 = stateNotOkPattern.matcher(np);

        if (m3.find()) {

            return getCorrectPlate(getValidString(m3), false, m3, false);

        }

        return null;

    }

    private List<String> getCorrectPlate(String validString, boolean districtIssue, Matcher m, boolean stateIssue) {

        List<String> numberPlates = new ArrayList<>();

        String state = m.group(1);

        String district = m.group(2);

        String spl = m.group(3);

        String series = m.group(4);

        String serial = m.group(5);

        if (!validString.equalsIgnoreCase("INVALID") && districtIssue && !stateIssue) {

            district = getCorrectedDistrict(district);

            String np = state + district + spl + series + serial;

            numberPlates.add(np);

        } else if (!validString.equalsIgnoreCase("INVALID") && districtIssue && stateIssue) {

            district = getCorrectedDistrict(district);

            List<String> correctedStates = getCorrectedStates(state);

            for (String statestr : correctedStates) {

                numberPlates.add(statestr + district + spl + series + serial);

            }

        } else if (!validString.equalsIgnoreCase("INVALID") && !districtIssue && stateIssue) {

            List<String> correctedStates = getCorrectedStates(state);

            for (String statestr : correctedStates) {

                numberPlates.add(statestr + district + spl + series + serial);

            }

        }

        if (numberPlates.size() > 1) {

            return sortNumberPlatesByScore(numberPlates);

        } else

            return numberPlates;

    }

    private List<String> getCorrectedStates(String state) {

        String firstChar = state.substring(0, 1);

        String secondChar = state.substring(1, 2);

        boolean firstMatch = false;

        boolean secondMatch = false;

        if (firstChar.matches(stateStartCharRegex)) {

            firstMatch = true;

        }

        if (secondChar.matches(stateEndCharRegex)) {

            secondMatch = true;

        }

        if (firstMatch && secondMatch) {

            //Combination won't work

            //Get all combinations with first letter

            List<String> states = getStatesMatchingFirstChar(firstChar);

            states.addAll(getStatesMatchingSecondChar(secondChar));

            return states;

        }

        if (firstMatch && !secondMatch) {

            //Try substituting all states that match

            return getStatesMatchingFirstChar(firstChar);

        }

        if (!firstMatch && secondMatch) {

            //Try substituting all states that match

            List<String> states = getStatesMatchingSecondChar(secondChar);

            return states;

        }

        return new ArrayList<>();

    }

    private String getCorrectedDistrict(String district) {

        district = district.replaceAll(zeroRegex, "0");

        district = district.replaceAll(oneRegex, "1");

        district = district.replaceAll(twoRegex, "2");

        district = district.replaceAll(fourRegex, "4");

        district = district.replaceAll(sixRegex, "6");

        district = district.replaceAll(eightRegex, "8");

        district = district.replaceAll(noneRegex, " ");

        return district;

    }

    private List<String> sortNumberPlatesByScore(List<String> numberPlates) {

        Map<String, Double> numplateMap = new LinkedHashMap<>();

        for (String numberPlate : numberPlates) {

            numplateMap.put(numberPlate, scoreMap.get(numberPlate.substring(0, 2)) == null ? 0D :

                    scoreMap.get(numberPlate.substring(0, 2)));

        }

        Map<String, Double> sortedMap = sortByValue(numplateMap);

        return new ArrayList<>(sortedMap.keySet());

    }

    /**
     * Given the second character in a state code, fetch the permissible values for the first character
     * @param secondChar
     * @return
     */
    private List<String> getStatesMatchingSecondChar(String secondChar) {

        List<String> states = new ArrayList<>();
        List<String> firstChars = stateSecondToFirstMap.get(secondChar);

        for (String firstChar : firstChars) {
            states.add(firstChar + secondChar);
        }

        return states;
    }


    /**
     * Given the first character in a state code, fetch the permissible values for the second character
     * @param firstChar
     * @return
     */
    private List<String> getStatesMatchingFirstChar(String firstChar) {

        List<String> states = new ArrayList<>();
        List<String> secondChars = stateFirstToSecondMap.get(firstChar);

        for (String secondChar : secondChars) {
            states.add(firstChar + secondChar);
        }

        return states;
    }

    private String getValidString(Matcher m) {

        String state = m.group(1);
        String district = m.group(2);
        String spl = m.group(3);
        String series = m.group(4);
        String serial = m.group(5);

        //  //System.out.println("(" + state + ")(" + district + ")(" + spl + ")(" + series + ")(" + serial + ")");

        String np = state + district + spl + series + serial;

        return (np.length() < 5 ? "INVALID" : np);

    }

    @Override
    public String getRegexLog() {
        return "";
    }

    @Override
    public void resetRegexLog() {
        //Implement if you want to drill down on regex logic
    }
}