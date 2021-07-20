package in.getparking.npgroups;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static in.getparking.npgroups.PlateMatcherAlgo2.replaceNumbersWithSimilarAlphas;


public class ReversePlateMatchImproved implements NumberPlateMatcherInterface {

    private static final String TAG = ReversePlateMatchImproved.class.getSimpleName();

    private StringBuilder regexLog = new StringBuilder(200);

    private final Pattern districtstateNotOkPattern;

    /************* Ideal Case Pattern****************/

    public static final String state =

            "(?<!(?![oOl])[a-zA-Z])((?:(?:PA)|(?:RA)|(?:SA)|(?:RB)|(?:GC)|(?:GA)|(?:JG)|(?:RH)|(?:PH)|(?:KJ)|(?:HJ)|(?:AK)|(?:LK)" +

                    "|(?:PM)|(?:HM)|(?:NM)|(?:LM)|(?:ZM)|(?:LN)|(?:DO)|(?:RO)|(?:BP)|(?:JR)|(?:KS)|(?:NT)|(?:ST)|(?:RT)" +

                    "|(?:AU)|(?:KU)|(?:PU)|(?:BW)|(?:NA)|(?:HC)|(?:ND)|(?:DD)|(?:DL)|(?:LD)|(?:YP)))";    // INDIA States

    private final String district = PlateMatcherAlgo2.district;
//            "((?:[OASL0-9]{1,2}))";    // Integer Number 1// Non-greedy match on filler
//    // (?<=[A-Z])[0-9]{1,2}(?![0-9])

    //    private final String district_spl = "((?:[A-Z]{0,1}?))";
    private final String district_spl = PlateMatcherAlgo2.district_spl;
//            "((?:[CEPRTVY]{0,1}?))"; // Based on a internet result that restricts specials to these. Needs verification

    private final String series = PlateMatcherAlgo2.series;
//            "(?![0O])((?:[8062A-Z]{0,2}))";    // Word 1

    private final String seriesMandatory = PlateMatcherAlgo2.seriesMandatory;

    private final static String numberLike = "[ODASLG\\d]";
    public static final String digitsPattern = "((?![\\d]{5,})(?:"+numberLike+"{1,4}))";
    // Negative look ahead (?![ODASLG\d]{5,}) so that if there are more than 4 digits, then the first is discarded (in a reverse match)


    /********************************************/

    /************* State Not ok rest ok district****************/

    public static final String stateNotOk = "(?<!(?![oOl])[a-zA-Z])((?:[A-Z]{2}))";    // No Proper State Match but rest of them match

    /********************************************/

    /**********************District Pattern Issue**************/

    private final String districtNotOk = "((?:\\w{1,2}+))";

    /**********************************************************/

    private final String zeroRegex = "[DOUVQ]";

    private final String oneRegex = "[ilIJT]";

    private final String twoRegex = "[g]";

    private final String fourRegex = "[AEHLZ]";

    private final String fiveRegex = "[Ss]";

    private final String sixRegex = "[CG]";

    private final String sevenRegex = "[T]";

    private final String eightRegex = "[B%]";

    private final String noneRegex = "[^0-9]";   //"[KMNPRWXY-/+&%$#@!.?,]";

    private final String stateStartCharRegex = "[ABCGHJKMNOPRSTUWDL]";

    private final String stateEndCharRegex = "[PRSGAJKHLNZDBY]";

    private final Map<String, List<String>> stateFirstToSecondMap = new HashMap<>();

    private final Map<String, List<String>> stateSecondToFirstMap = new HashMap<>();

    private final Pattern idealPattern;

    private final Pattern idealPatternPerfect;

    private final Pattern stateNotOkPattern;

    private final Pattern districtNotOkPattern;

    private final Pattern stateMissingPattern;

    private final Pattern stateDistrictMissingPattern;

    public ReversePlateMatchImproved() {

        idealPattern = Pattern.compile(digitsPattern + series + district_spl + district + state);
        idealPatternPerfect = Pattern.compile(digitsPattern + seriesMandatory + district_spl + district + state);
        stateNotOkPattern = Pattern.compile(digitsPattern + series + district_spl + district + stateNotOk);
        districtNotOkPattern = Pattern.compile( digitsPattern + series + district_spl + districtNotOk + state);
        districtstateNotOkPattern = Pattern.compile(digitsPattern + series + district_spl + districtNotOk + stateNotOk);
        stateMissingPattern = Pattern.compile(digitsPattern + series + district );
        stateDistrictMissingPattern= Pattern.compile(digitsPattern + series);

        prepareStateMaps();
    }

    String[] states = new String[]{"AP", "AR", "AS", "BR", "CG", "GA", "GJ", "HR", "HP", "JK", "JH", "KA", "KL", "MP", "MH",
            "MN", "ML", "MZ", "NL", "OD", "OR", "PB", "RJ", "SK", "TN", "TS", "TR", "UA", "UK", "UP", "WB", "AN", "CH", "DN", "DD", "DL", "LD", "PY"};

//    String[] states = new String[]{"PA", "RA", "SA", "RB", "GC", "AG", "JG", "RH", "PH", "KJ", "HJ", "AK", "LK", "PM", "HM",
//            "NM", "LM", "ZM", "LN", "DO", "RO", "BP", "JR", "KS", "NT", "ST", "RT", "AU", "KU", "PU", "BW", "NA", "HC", "ND", "DD", "DL", "LD", "YP"};

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

        String np = numberPlate.replaceAll("[^\\w]", "")
                .toUpperCase()
                .replace("IND", "");

        regexLog.append(":#: Input:Clean=").append(numberPlate).append(":").append(np).append(";");

        np = new StringBuilder(np).reverse().toString();

        List<String> numberPlates = validNumberPlateRev(np);

        if(numberPlates == null || numberPlates.size() ==0)
            return null;
        String unreversed = new StringBuilder(numberPlates.get(0)).reverse().toString();
        regexLog.append("Returning: ").append(unreversed).append(";");
        return Arrays.asList(unreversed);
    }

    private List<String> validNumberPlateRev(String np) {

        List<String> numberPlates = new ArrayList<>();

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

                int end = stateMissingMatcher.end(0);
                if(np.length() - end >= 2)
                    numberPlates =  getCorrectPlateWithMissingState(np.substring(end, end + 2), stateMissingMatcher);
                else if(np.length() - end == 1){
                    String stateCandidate =  np.substring(end, end+1) + " ";
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

        String state = m.group(5);
        String district = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(4)), 2, '0');
        String spl = m.group(3);
        String series = getCorrectedSeries(m.group(2));
        String digits = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(1)), 4, '0');

        if(!districtIssue && district.length() == 1)
            district =  district + '0';

        regexLog.append("districtIssue:").append(districtIssue).append(", stateIssue:").append(stateIssue).append(", [ST:DST:SPL:SER:DGT]: ")
                .append(state).append(":").append(district).append(":").append(spl).append(":").append(series).append(":").append(digits).append(";");

        if (!validString.equalsIgnoreCase("INVALID") && districtIssue && !stateIssue) {

            String np =  digits + series + spl  + district + state;
            numberPlates.add(np);
            regexLog.append("CorrectDist=").append(district).append(";");

        } else if (!validString.equalsIgnoreCase("INVALID") && districtIssue && stateIssue) {

            List<String> correctedStates = getCorrectedStates(state);
            for (String statestr : correctedStates) {
                numberPlates.add(digits + series + spl  + district + statestr);
            }

            String correctedState = "Null";
            if(correctedStates.size() > 1)
                correctedState = correctedStates.get(0);

            regexLog.append("CorrectDist=").append(district).append(", CorrectState=").append(correctedState).append(";");

        } else if (!validString.equalsIgnoreCase("INVALID") && !districtIssue && stateIssue) {

            List<String> correctedStates = getCorrectedStates(state);
            for (String statestr : correctedStates) {
                numberPlates.add(digits + series + spl  + district + statestr);
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

        String district = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(3)), 2, '0');
        String series = getCorrectedSeries(m.group(2));
        String digits = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(1)), 4, '0');
        String state = "";

        regexLog.append("stateCandidate:").append(stateCandidate).append(", [DST:SER:DGT]: ")
                .append(district).append(":").append(series).append(":").append(digits).append(";");


        List<String> correctedStates = getCorrectedStates(stateCandidate);
        if(correctedStates != null && correctedStates.size() > 0)
            state = correctedStates.get(0);

        String np = digits + series + district + state ;
        numberPlates.add(np);

        regexLog.append("CorrectState=").append(state).append(";");

        return numberPlates;
    }


    private List<String> getCorrectPlateWithMissingStateDistrict(String stateCandidate, String distCandidate, Matcher m) {
        List<String> numberPlates = new ArrayList<>();

        String series = getCorrectedSeries(m.group(2));
        String digits = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(1)), 4, '0');
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

        String np = digits + series + district + state ;
        numberPlates.add(np);

        regexLog.append("CorrectDist=").append(district).append(", CorrectState=").append(state).append(";");

        return numberPlates;
    }



    private List<String> getCorrectedStates(String state) {

        state = new StringBuilder(state).reverse().toString();

        String firstChar = state.substring(0, 1);
        String secondChar = state.substring(1, 2);

        boolean firstMatch = false;
        boolean secondMatch = false;

        List<String> states = new ArrayList<>();

        if(state.equals("XX"))
            return Arrays.asList(new StringBuilder(mostFrequentState).reverse().toString());

        if(secondChar.matches("[O]")){ //todo - should this be firstChar instead of secondChar - I am a little confused
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
            states = Arrays.asList(mostFrequentState);
        }


        states = sortByObservedFrequency(states);
        return Arrays.asList(new StringBuilder(states.get(0)).reverse().toString());
    }


    Map<String, Integer> observedFrequency = new HashMap<>();
    {
        observedFrequency.put("MH", 107);
        observedFrequency.put("GJ", 5);
        observedFrequency.put("DL", 4);
        observedFrequency.put("KA", 3);
        observedFrequency.put("UP", 3);
        observedFrequency.put("KL", 2);
        observedFrequency.put("HR", 2);
        observedFrequency.put("WB", 1);
        observedFrequency.put("AP", 1);
        observedFrequency.put("MP", 1);
        observedFrequency.put("DD", 1);
        observedFrequency.put("DN", 1);
    }

    private String mostFrequentState = "MH";  //reverse of MH, todo at some point this should be data driveb


    private List<String> sortByObservedFrequency(List<String> states){
        Collections.sort(states, new Comparator<String>() {
            @Override
            public int compare(String state1, String state2) {
                return observedFrequency.getOrDefault(state2, 1) - observedFrequency.getOrDefault(state1, 1);

            }
        });
        return states;
    }


    private String replaceAlphasWithSimilarNums(String original) {

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


    private String getCorrectedSeries(String originalSeries){
        if(originalSeries.length() == 0)
            return originalSeries;

        originalSeries = replaceNumbersWithSimilarAlphas(originalSeries);
        // A series doesn't end with an O (by regulation, to prevent confusing it with a zero in the serial/digits, so we are likely looking at a D
        return originalSeries.replaceAll("^O", "D");
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

        String state = m.group(5);
        String district = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(4)), 2, '0');
        String spl = m.group(3);
        String series = getCorrectedSeries(m.group(2));
        String digits = StringUtils.rightPad(replaceAlphasWithSimilarNums(m.group(1)), 4, '0');

        //  //System.out.println("(" + state + ")(" + district + ")(" + spl + ")(" + series + ")(" + digits + ")");

        String np =  digits + series  + spl + district + state ;
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
