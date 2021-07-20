package in.getparking.npgroups;

import java.util.List;

public interface NumberPlateMatcherInterface {
    List<String> validNumberPlate(String numberPlate);

    String getRegexLog() ;
    void resetRegexLog();
}
