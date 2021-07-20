package in.getparking.npgroups;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlateMatcherAlgo2 implements NumberPlateMatcherInterface {
    private static final String TAG = NumberPlateMatcher.class.getSimpleName();

    private StringBuilder regexLog = new StringBuilder(200);

    private final Pattern districtstateNotOkPattern;

    /************* Ideal Case Pattern****************/

    public static final String state = "((?:(?:AP)|(?:AR)|(?:AS)|(?:BR)|(?:CG)|(?:GA)|(?:GJ)|(?:HR)|(?:HP)|(?:JK)|(?:JH)|(?:KA)|(?:KL)" +

            "|(?:MP)|(?:MH)|(?:MN)|(?:ML)|(?:MZ)|(?:NL)|(?:OD)|(?:OR)|(?:PB)|(?:RJ)|(?:SK)|(?:TN)|(?:TS)|(?:TR)" +

            "|(?:UA)|(?:UK)|(?:UP)|(?:WB)|(?:AN)|(?:CH)|(?:DN)|(?:DD)|(?:DL)|(?:LD)|(?:PY)))(?!(?![oOl])[a-zA-Z])";    // INDIA States

    public static final String district = "((?:(?!0[^OIDBASLTC0-9])[OIDBASLTC0-9]{1,2}))";    // Integer Number 1// Non-greedy match on filler
    // Negative Lookahead (?!0[^ODASL0-9]) is used so that we don't have district as only 0 (zero) i.e. we don't match MH-0-AB-1234

    // (?<=[A-Z])[0-9]{1,2}(?![0-9])

//    private final String district_spl = "((?:[A-Z]{0,1}?))";
    public static final String district_spl = "((?:[CEPRTVY]{0,1}?))"; // Based on a internet result that restricts specials to these. Needs verification

    public static final String series =   "((?:[806452A-Z]{0,2}))(?<![O0])";    // Negative look behind doesn't seem to change anything - maybe remove it
    public static final String seriesMandatory = "((?:[806452A-Z]{1,2}))(?<![O0])";    // We really want there to be a series code

    public static final String digitsPattern = "((?:[ODASLG\\d]{1,4}))";    // Integer Number 2

    private final String separators = "(?:[-._ ])?";

    /********************************************/

    /************* State Not ok rest ok district****************/

    public static final String stateNotOk = "((?:[A-Z]{2}))";    // No Proper State Match but rest of them match

    /********************************************/

    /**********************District Pattern Issue**************/

    private String districtNotOk = "((?:\\w{1,2}+))";

    /**********************************************************/

    private String zeroRegex = "[DOUVQ]";

    private String oneRegex = "[ilIJT]";

    private String twoRegex = "[g]";

    private String fourRegex = "[AEHLZ]";

    private String fiveRegex = "[Ss]";

    private String sixRegex = "[CG]";

    private String sevenRegex = "[T]";

    private String eightRegex = "[B%]";

    private String noneRegex = "[^0-9]";   //"[KMNPRWXY-/+&%$#@!.?,]";

    private String stateStartCharRegex = "[ABCGHJKMNOPRSTUWDL]";

    private String stateEndCharRegex = "[PRSGAJKHLNZDBY]";

    private Map<String, List<String>> stateFirstToSecondMap = new HashMap<>();

    private Map<String, List<String>> stateSecondToFirstMap = new HashMap<>();

    private Pattern idealPattern;
    private Pattern idealPatternPerfect;

    private Pattern stateNotOkPattern;

    private Pattern districtNotOkPattern;

    private Pattern stateMissingPattern;

    private Pattern stateDistrictMissingPattern;

    private Map<String, Integer> stateToDistrictMap = new HashMap<>();

    public PlateMatcherAlgo2() {

        idealPattern = Pattern.compile(state + district + district_spl + series + digitsPattern);
        idealPatternPerfect = Pattern.compile(state + district + district_spl + seriesMandatory + digitsPattern);
        stateNotOkPattern = Pattern.compile(stateNotOk + district + district_spl + series + digitsPattern);
        districtNotOkPattern = Pattern.compile(state + districtNotOk + district_spl + series + digitsPattern);
        districtstateNotOkPattern = Pattern.compile(stateNotOk + districtNotOk + district_spl + series + digitsPattern);
        stateMissingPattern = Pattern.compile(district + series + digitsPattern);
        stateDistrictMissingPattern= Pattern.compile(series + digitsPattern);

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

        String np = numberPlate.replaceAll("[^\\w]", "")
                .toUpperCase()
                .replace("IND","");

        regexLog.append(":#: Input:Clean=").append(numberPlate).append(":").append(np).append(";");

        Matcher m;

        if(np.length() >= 8) { // Long string so unlikely that the series code is missing, so we will go with mandating that there be a series code.
            m = idealPatternPerfect.matcher(np);

            if (m.find() && getValidString(m).length() > 6) {

                numberPlates.add(getValidString(m));
                regexLog.append("Ideal Match;Return: ").append(getValidString(m)).append(';');
                return numberPlates;
            }
        }

        m = idealPattern.matcher(np);
        int fullMatchLength = m.find()? m.group().length(): 0;


        Matcher stateNotOkMatcher = stateNotOkPattern.matcher(np);
        Matcher stateMissingMatcher = stateMissingPattern.matcher(np);
        Matcher districtNotOkMatcher = districtNotOkPattern.matcher(np);


        int stateNotOkLength = stateNotOkMatcher.find()? stateNotOkMatcher.group().length(): 0;
        int stateMissingLength = stateMissingMatcher.find()? stateMissingMatcher.group().length(): 0;
        int districtNotOkLength = districtNotOkMatcher.find()? districtNotOkMatcher.group().length(): 0;


        if (m.find() && fullMatchLength > Math.max(stateNotOkLength, Math.max(stateMissingLength, districtNotOkLength))) {
            numberPlates.add(getValidString(m));
            regexLog.append("Ideal Match;Return: ").append(getValidString(m)).append(';');
            return numberPlates;
        }

        if(districtNotOkLength > stateNotOkLength && stateNotOkLength > stateMissingLength) {
            regexLog.append("district not ok match;");
            return getCorrectPlate(getValidString(districtNotOkMatcher), true, districtNotOkMatcher, false);
        }

        if((stateNotOkLength+stateMissingLength) > 0) { // at least one of the two patterns matched

            if( stateNotOkLength >= stateMissingLength){ //Pick the pattern with a longer match
                regexLog.append("state not ok match;");
                return getCorrectPlate(getValidString(stateNotOkMatcher), false, stateNotOkMatcher, true);
            } else {  /*** stateMissingPattern : 01AB1234  ***/
                regexLog.append("state missing match;");

                int start = stateMissingMatcher.start(0);
                if(start >= 2)
                    numberPlates =  getCorrectPlateWithMissingState(np.substring(start -2, start), stateMissingMatcher);
                else if(start == 1){
                    String stateCandidate = " " + np.substring(0, 1);
                    numberPlates =  getCorrectPlateWithMissingState(stateCandidate, stateMissingMatcher);
                } else {
                    numberPlates =  getCorrectPlateWithMissingState("XX", stateMissingMatcher);

                }

                if(numberPlates != null & numberPlates.size() >0)
                    regexLog.append("Return: ").append(numberPlates.get(0)).append(";");
                if(numberPlates != null) return numberPlates;

            } // end else state missing pattern
        } // end if at least one of the two (damaged state code) patterns matched


        //todo - not required here since it is considered above, no?
        Matcher m1 = districtNotOkPattern.matcher(np);

        if (m1.find()) {
            regexLog.append("district not ok match;");
            return getCorrectPlate(getValidString(m1), true, m1, false);
        }


        /*** AB1234  ***/
        Matcher stateDistrictMissingMatcher = stateDistrictMissingPattern.matcher(np);

        if (stateDistrictMissingMatcher.find()
                && stateDistrictMissingMatcher.group().length() > 4) { // Too small a match length might mean that it is just some random noise
            regexLog.append("state & district missing match;");
            int start = stateDistrictMissingMatcher.start(0);
            String stateCandidate = "", distCandidate = "";
            if(start >= 2){
                distCandidate = np.substring(start-2, start);
            }

            if(start >= 4){
                stateCandidate = np.substring(start-4, start-2);
            } else if(start == 3){ //So we got only the H of MH or the L of KL. Pass the state as " H" and " L" respectively
                stateCandidate = " " + np.substring(0, 1);
            } else {
                stateCandidate = "XX";
            }


            numberPlates =  getCorrectPlateWithMissingStateDistrict(stateCandidate, distCandidate, stateDistrictMissingMatcher);
            if(numberPlates != null & numberPlates.size() >0)
                regexLog.append("Return: ").append(numberPlates.get(0)).append(";");
            if(numberPlates != null) return numberPlates;
        }


        Matcher m2 = districtstateNotOkPattern.matcher(np);

        if (m2.find()) {

            return getCorrectPlate(getValidString(m2), true, m2, true);
        }


        return null;
    }

    private List<String> getCorrectPlate(String validString, boolean districtIssue, Matcher m, boolean stateIssue) {

        List<String> numberPlates = new ArrayList<>();

        String state = m.group(1);
        String district = replaceAlphasWithSimilarNums(m.group(2));
        String spl = m.group(3);
        String series = getCorrectedSeries(m.group(4));
        String digits = replaceAlphasWithSimilarNums(m.group(5));

        if(!districtIssue && district.length() == 1)
            district = '0' + district;

        regexLog.append("districtIssue:").append(districtIssue).append(", stateIssue:").append(stateIssue).append(", [ST:DST:SPL:SER:DGT]: ")
                .append(state).append(":").append(district).append(":").append(spl).append(":").append(series).append(":").append(digits).append(";");

        if (!validString.equalsIgnoreCase("INVALID") && districtIssue && !stateIssue) {

            String np = state + district + spl + series + digits;
            numberPlates.add(np);
            regexLog.append("CorrectDist=").append(district).append(";");

        } else if (!validString.equalsIgnoreCase("INVALID") && districtIssue && stateIssue) {

            List<String> correctedStates = getCorrectedStates(state);
            for (String statestr : correctedStates) {
                numberPlates.add(statestr + district + spl + series + digits);
            }

            String correctedState = "Null";
            if(correctedStates.size() > 1)
                correctedState = correctedStates.get(0);

            regexLog.append("CorrectDist=").append(district).append(", CorrectState=").append(correctedState).append(";");

        } else if (!validString.equalsIgnoreCase("INVALID") && !districtIssue && stateIssue) {

            List<String> correctedStates = getCorrectedStates(state);
            for (String statestr : correctedStates) {
                numberPlates.add(statestr + district + spl + series + digits);
            }

            String correctedState = "Null";
            if(correctedStates.size() > 1)
                correctedState = correctedStates.get(0);

            regexLog.append("CorrectState=").append(correctedState).append(";");
        }

        if(numberPlates != null & numberPlates.size() >0)
            regexLog.append("Return: ").append(numberPlates.get(0)).append(";");
        return numberPlates;
    }


    private List<String> getCorrectPlateWithMissingState(String stateCandidate, Matcher m) {

        List<String> numberPlates = new ArrayList<>();

        String district = replaceAlphasWithSimilarNums(m.group(1));
        String series = getCorrectedSeries(m.group(2));
        String digits = replaceAlphasWithSimilarNums(m.group(3));
        String state = "";

        regexLog.append("stateCandidate:").append(stateCandidate).append(", [DST:SER:DGT]: ")
                .append(district).append(":").append(series).append(":").append(digits).append(";");


        List<String> correctedStates = getCorrectedStates(stateCandidate);
        if(correctedStates != null && correctedStates.size() > 0)
            state = correctedStates.get(0);

        String np = state + district + series + digits;
        numberPlates.add(np);

        regexLog.append("CorrectState=").append(state).append(";");

        return numberPlates;
    }


    private List<String> getCorrectPlateWithMissingStateDistrict(String stateCandidate, String distCandidate, Matcher m) {
        List<String> numberPlates = new ArrayList<>();

        String series = getCorrectedSeries(m.group(1));
        String digits = replaceAlphasWithSimilarNums(m.group(2));
        String state = "";
        String district = "";

        regexLog.append("stateCandidate:").append(stateCandidate).append(", districtCandidate:").append(distCandidate)
                .append(", [SER:DGT]: ")
                .append(series).append(":").append(digits).append(";");


        district = replaceAlphasWithSimilarNums(distCandidate);

        List<String> correctedStates = getCorrectedStates(stateCandidate);
        if(correctedStates != null && correctedStates.size() > 0)
            state = correctedStates.get(0);
        else
            return null;

        String np = state + district + series + digits;
        numberPlates.add(np);

        regexLog.append("CorrectDist=").append(district).append(", CorrectState=").append(state).append(";");

        return numberPlates;
    }


//todo - state correction can be based on a nearness graph of some sort instead of string pattern. Maybe trie??
    private List<String> getCorrectedStates(String state) {

        String firstChar = state.substring(0, 1);
        String secondChar = state.substring(1, 2);

        boolean firstMatch = false;
        boolean secondMatch = false;

        List<String> states = new ArrayList<>();

        if(state.equals("XX"))
            return Arrays.asList(mostFrequentState);

        if(secondChar.matches("[O]")){
        // There are no state codes ending in 'O' or 'I', so these are likely to be numbers that are part of the district code. We will ignore them
            secondChar= firstChar;
            firstChar = " ";
        }


        if (firstChar.matches(stateStartCharRegex)) {
            firstMatch = true;
        }

        if (secondChar.matches(stateEndCharRegex)) {
            secondMatch = true;
        }

        if (firstMatch && secondMatch) {   //Combination not working, look at other options

            //Get all combinations with first letter
            states = getStatesMatchingFirstChar(firstChar);

            //Get all combinations with second letter
            states.addAll(getStatesMatchingSecondChar(secondChar));
        }

        if (firstMatch && !secondMatch) {

            //Try substituting all states that match
            states = getStatesMatchingFirstChar(firstChar);
        }

        if (!firstMatch && secondMatch) {

            //Try substituting all states that match
            states = getStatesMatchingSecondChar(secondChar);
        }

        if(!firstMatch && !secondMatch){
            // Nothing matches, put in a placeholder for now
            states = Arrays.asList("XX");
        }


        states = sortByObservedFrequency(states);
        return states;
    }


    Map<String, Integer> observedFrequency = new HashMap<>();
    {
        observedFrequency.put("MH", 107);
        observedFrequency.put("GJ", 5);
        observedFrequency.put("DL", 4);
        observedFrequency.put("UP", 2);
        observedFrequency.put("AP", 1);
        observedFrequency.put("HR", 1);
        observedFrequency.put("DD", 1);
    }

    private String mostFrequentState = "MH";  //todo at some point this should be data driven


    private List<String> sortByObservedFrequency(List<String> states){
        Collections.sort(states, new Comparator<String>() {
            @Override
            public int compare(String state1, String state2) {
                return observedFrequency.getOrDefault(state2, 1) - observedFrequency.getOrDefault(state1, 1);

            }
        });
        return states;
    }


    private String replaceAlphasWithSimilarNums(@NonNull String original) {

        original = original.replaceAll(zeroRegex, "0");

        original = original.replaceAll(oneRegex, "1");

        original = original.replaceAll(twoRegex, "2");

        original = original.replaceAll(fourRegex, "4");

        original = original.replaceAll(fiveRegex, "5");

        original = original.replaceAll(sixRegex, "6");

        original = original.replaceAll(sevenRegex, "7");

        original = original.replaceAll(eightRegex, "8");

        original = original.replaceAll(noneRegex, " ");

        return original;

    }



    public static String replaceNumbersWithSimilarAlphas(@NonNull String original) {
        if(original.length() == 0)
            return original;

        original = original.replaceAll("8", "B");
        original = original.replaceAll("0", "D");
        original = original.replaceAll("6", "G");
        original = original.replaceAll("2", "Z");
        original = original.replaceAll("4", "A");
        original = original.replaceAll("5", "S");
        original = original.replaceAll("7", "T");

        return original;

    }


    private String getCorrectedSeries(String originalSeries){
        if(originalSeries.length() == 0)
            return originalSeries;

        originalSeries = replaceNumbersWithSimilarAlphas(originalSeries);

        // A series doesn't end with alphabet O (by regulation, to prevent confusing it with a zero in the serial/digit, so we are likely looking at a D
//        return originalSeries.replaceAll("O$", "D");
        return originalSeries.replaceAll("O", "D");
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
        String district = replaceAlphasWithSimilarNums(m.group(2));
        String spl = m.group(3);
        String series = getCorrectedSeries(m.group(4));
        String digits = replaceAlphasWithSimilarNums(m.group(5));

        //  //System.out.println("(" + state + ")(" + district + ")(" + spl + ")(" + series + ")(" + digits + ")");

        String np = state + district + spl + series + digits;
        regexLog.append("getValidString() [ST:DST:SPL:SER:DGT]: ")
                .append(state).append(":").append(district).append(":").append(spl).append(":").append(series).append(":").append(digits).append(";");

        return (np.length() < 5 ? "INVALID" : np);

    }

    public String getRegexLog() {
        return regexLog.toString();
    }

    public void resetRegexLog() {
        regexLog = new StringBuilder(200);
    }


}
