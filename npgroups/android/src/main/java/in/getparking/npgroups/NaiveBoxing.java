package in.getparking.npgroups;

import android.util.Log;

import com.google.common.base.CharMatcher;
import com.google.common.collect.EvictingQueue;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;


//This class depends purely on tracking id provided to it.
//This class will spit out a result if it gets at-least 5 frames or 1 second is passed.
//This is a trade-off between speed and accuracy..For Better accuracy and reliable results use NumberplateBoxingAndVoting
//This is inspired from the same class mentioned above
public class NaiveBoxing {
    private static final String TAG = NaiveBoxing.class.getSimpleName();

    private List<NumberPlateObject> queue;
    private NumberplateExtracter listener;
    private StringLengthComparator comparator = new StringLengthComparator(10);

    //Cache of last x numberplates sent
    private EvictingQueue<String> recentNumberplatesSent = EvictingQueue.create(10);
    private long lastPostedTimeStamp = 0;
    private Disposable intervalDisposable;
    private boolean timerIsActive = false;
    private final Object mLock = new Object();

    private StringBuilder algoSysLog = new StringBuilder(200);
    private StringBuilder regexOutputReplay = new StringBuilder(200);

    public static final long MILLIS_DAY_ROUNDED = 100000000; // 100 million


    public NaiveBoxing(NumberplateExtracter listener) {
        this.listener = listener;
        queue = new ArrayList<>();
    }

    private Map<String, Long> recentNumplatesSentMap = new BoundedNumPlatesMap<>(10); // Set of recently sent Num Plate detections, to prevent us from sending duplicates

    /***
     * An extension of a LinkedHashMap that automatically deletes the oldest entry when MAX_SIZE is exceeded
     * @param <T>
     * @param <V>
     */
    private static class BoundedNumPlatesMap<T, V> extends LinkedHashMap<T, V> {

        private final int MAX_SIZE;

        @Override
        protected boolean removeEldestEntry(Entry<T, V> eldest) {
            return size() > MAX_SIZE;
        }

        public BoundedNumPlatesMap(int maxSize) {
            super();
            MAX_SIZE = maxSize;
        }

    } //End class BoundedNumPlatesMap


    public void group(NumberPlateObject numberPlateObject) {
        createANewMapEntry(numberPlateObject);
        //For a fast moving vehicle with different tracking id's
        if (!queue.isEmpty() && queue.size() >= 3) {
            //Create a new map entry
            timerIsActive = false;
            sendResult();
        } else {
            if (!timerIsActive) {
                timerIsActive = true;
                startTimer();
            }
            //Create a timer observable here
        }
    }

    private void createANewMapEntry(NumberPlateObject numberPlateObject) {
        synchronized (mLock) {
            queue.add(numberPlateObject);
            mLock.notifyAll();
        }
    }

    private void startTimer() {
        if (intervalDisposable != null && !intervalDisposable.isDisposed()) {
            intervalDisposable.dispose();
        }
        System.out.println("New Timer");
        intervalDisposable = Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            if (!queue.isEmpty()) {
                System.out.println("Timer Set Off");
                timerIsActive = false;
                sendResult();
            }
        });
    }

    private void sendResult() {

        if (intervalDisposable != null && !intervalDisposable.isDisposed()) {

            intervalDisposable.dispose();
        }
        timerIsActive = false;
        List<NumberPlateObject> candidates = new ArrayList<>(queue);
        synchronized (mLock) {
            queue.clear();
            mLock.notifyAll();
        }

        Collections.sort(candidates, comparator);

        //Get the topmost item out of it assuming that this would be the correct one since its nearer to camera
        //do a fuzzy search with previously sent plates .. if get an exact match then drop the result
        if (!recentNumberplatesSent.isEmpty()) {
            //Probably rank it up here too before comparing
            NumberPlateObject numberPlateObject = findTheMaxOccurrence(candidates);
            if (numberPlateObject != null) {
                algoSysLog.append("Max Occurrence Plate = ").append(numberPlateObject.getNumberPlate()).append(";");

                ExtractedResult result = FuzzySearch.extractOne(numberPlateObject.getNumberPlate(), recentNumberplatesSent);

                algoSysLog.append("Similarity score (recent plates) = ").append(result.getScore())
                        .append(", closest match = ").append(result.getString()).append(";");

                // If there is no similar string out there
                if (result.getScore() < 75) {
                    //     System.out.print("Tracking Id " + activeTrackingId + " To be posted Candidate : " + numberPlateObject.getNumberPlate() + " - FuzzyWuzzy " + result.toString());
                    //  if (Utils.timeDiffSec(lastPostedTimeStamp, System.currentTimeMillis()) > 1) {

                    postResult(candidates);
                    //  }

                } else if (result.getScore() >= 95) {
                    try {
                        Long timestamp = recentNumplatesSentMap.get(result.getString());

                        //If its more than 94 then check for time when it was posted.. if its under a threshold then post it
                        if (timestamp != null && Utils.timeDiffSec(timestamp, System.currentTimeMillis()) > (5 * 60)) {

                            algoSysLog.append("Ignore high similarity score since enough time has elapsed").append(";");
                            postResult(candidates);
                        }
                    } catch (Exception ignored) {

                    }

                } else {
                    try {
                        //check for last 4 digits not to be same
                        //Biasing towards digits being same or not
                        //Grey Zone Hope we don't land here
                        // this needs more heuristics

                        // Instead of checking for exact match in the digits, we check to see if at least 3 out of 4 digits match. If they don't
                        // match, we go ahead and post this result despite the high similarity score for the NumberPlate.
                        if (FuzzySearch.ratio(extractNumberplateDigits(result.getString()),
                                extractNumberplateDigits(numberPlateObject.getNumberPlate())) < 75) {
                            //TODO check this thing further or time based

                            algoSysLog.append("Ignore high similarity score since Plate Digits do not match").append(";");

                            postResult(candidates);
                        } else {
                            //If they are same then check the time before that they were posted
                            Long ts = recentNumplatesSentMap.get(result.getString());
                            if (ts != null && Utils.timeDiffSec(ts, System.currentTimeMillis()) > (5 * 60)) {

                                postResult(candidates);
                            }
                        }

                    } catch (Exception ignored) {

                    }
                }
            }
        } else {
            System.out.println("Iska Bhi Koi Maa Baap Nahi");
            postResult(candidates);
        }
    }

    private String extractNumberplateDigits(String vehicleNo) {
        //String vehicleNumber = vehicleNo.substring(vehicleNo.length() - 4);
        if (vehicleNo != null) {
            String vehicleNumber = "";
            String theRemainingChar = "";
            for (int x = vehicleNo.length() - 1; x >= 0; x--) {
                if ((String.valueOf(vehicleNo.charAt(x)).matches("\\d")) && vehicleNumber.length() <= 4) {
                    vehicleNumber = vehicleNo.charAt(x) + vehicleNumber;
                } else {
                    theRemainingChar = vehicleNo.substring(0, x + 1);
                    break;
                }
            }
            String theDigits = CharMatcher.digit().retainFrom(vehicleNumber); //Assuming that the last most character is a digit
            if (theDigits.length() < 4) {
                theDigits = StringUtils.leftPad(theDigits, 4, '0');
            } else if (theDigits.length() > 4) {
                theDigits = theDigits.substring(theDigits.length() - 4);
            }
            return theDigits;
        } else {
            return "0000";
        }
    }

    private void postResult(List<NumberPlateObject> candidates) {

        NumberPlateObject numberPlateObject = findTheMaxOccurrence(candidates);
        if (numberPlateObject != null) {
            Log.i(TAG, "postResult: winner is " + numberPlateObject.getNumberPlate());
            recentNumberplatesSent.add(numberPlateObject.getNumberPlate());
            recentNumplatesSentMap.put(numberPlateObject.getNumberPlate(), System.currentTimeMillis());
            lastPostedTimeStamp = System.currentTimeMillis();
            listener.onNumberplateExtraction(numberPlateObject.getNumberPlate());

            algoSysLog.append("PostResult: Winner = ").append(numberPlateObject.getNumberPlate()).append(";");
            signalNewBoxToLogMethod();
        }
    }

    private NumberPlateObject findTheMaxOccurrence(List<NumberPlateObject> candidates) {
        try {
            Map<String, Integer> voting = new HashMap<>();
            Map<String, NumberPlateObject> npBox = new HashMap<>();
            for (NumberPlateObject np : candidates) {
                if (voting.containsKey(np.getNumberPlate())) {
                    voting.put(np.getNumberPlate(), voting.get(np.getNumberPlate()) + 1);
                } else {
                    voting.put(np.getNumberPlate(), 1);
                }
                npBox.put(np.getNumberPlate(), np);
            }
            HashMap<String, Integer> sorted = sortByValue(voting);

            if (sorted.get(sorted.keySet().toArray()[0]) > 1) {
                return npBox.get(sorted.keySet().toArray()[0]);
            } else return null;
//TODO what if there is only one impression of any plate
        } catch (Exception e) {
            return null;
        }


    }

    // function to sort hashmap by values
    private static HashMap<String, Integer> sortByValue(Map<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    private void signalNewBoxToLogMethod() {
//        if (screenUpdateListener== null)
//            return;

        publishAndResetReplayLog();
        //  screenUpdateListener.displayTextOnScreen(algoSysLog.toString(), AnprActivity.NEW_BOX);
        algoSysLog = new StringBuilder(200);
    }

    public void publishAndResetReplayLog() {
        // screenUpdateListener.displayTextOnScreen(regexOutputReplay.toString(), AnprActivity.REPLAY_LOG);
        regexOutputReplay = new StringBuilder(200);
    }

}


class StringLengthComparator implements Comparator<NumberPlateObject> {

    private int referenceLength;

    StringLengthComparator(int reference) {
        super();
        this.referenceLength = reference;
    }

    public int compare(NumberPlateObject s1, NumberPlateObject s2) {
        int dist1 = Math.abs(s1.getNumberPlate().length() - referenceLength);
        int dist2 = Math.abs(s2.getNumberPlate().length() - referenceLength);
        return dist2 - dist1;
    }

}
