package in.getparking.npgroups;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.CharMatcher;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    //Todo - move this to CalendarManager or replace calls to it with a method defined there.
    public static String toDateString(long dateLong, String format) {
        String dateString = "";
        Date date = new Date(dateLong);
        SimpleDateFormat df = new SimpleDateFormat(format);
        dateString = df.format(date);
        return dateString;
    }


    public static String xtractLast4Digits(String numberPlate) {
        Log.d(TAG, "xtractLast4Digits: " + numberPlate);
        StringBuffer buffer = new StringBuffer();
        String[] components = numberPlate.split("(?=\\d{1,4}$)", 2);
        if (components != null) {
            if (components.length > 1) {
                Log.d(TAG, "xtractLast4Digits: components[1] = " + components[1]);
                return components[1];

            }
        }
        return "";
    }


    public static String charToNearestNumberConverter(String vehicleDigits) {
        String zeroRegex = "[DOUVQ]";
        String oneRegex = "[IJLT]";
        String twoRegex = "[Z]";
        String fourRegex = "[AH]";
        String sixRegex = "[CG]";
        String eightRegex = "[SB]";
        String noneRegex = "[^0-9]";   //"[KMNPRWXY-/+&%$#@!.?,]";

        vehicleDigits = vehicleDigits.replaceAll(zeroRegex, "0");
        vehicleDigits = vehicleDigits.replaceAll(oneRegex, "1");
        vehicleDigits = vehicleDigits.replaceAll(twoRegex, "2");
        vehicleDigits = vehicleDigits.replaceAll(fourRegex, "4");
        vehicleDigits = vehicleDigits.replaceAll(sixRegex, "6");
        vehicleDigits = vehicleDigits.replaceAll(eightRegex, "8");
        vehicleDigits = vehicleDigits.replaceAll(noneRegex, " ");

        return vehicleDigits;
    }

    public static String formatNumberPlate(String numberPlate) {
        String numberPlate4 = "", numberPlate3 = "", numberPlate2 = "", numPlateStateCode = "";
        // First take the number plate passed and populate into the number plate fields
        String[] splits = numberPlate.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        Integer[] numerics = new Integer[splits.length];
        int index = 0;

        for (String split : splits) {
            try {
                numerics[index] = Integer.parseInt(split);
                ++index;
            } catch (NumberFormatException e) {
                numerics[index] = -1;
                ++index;
            }
            Log.d(TAG, "init: np split = " + split + ", numeric = " + numerics[index - 1]);
        }

        index = splits.length;


        while (--index >= 0) {
            if (numerics[index] == -1) continue;
            numberPlate4 = numerics[index].toString();
            while (--index >= 0) {
                if (numerics[index] != -1) continue;
                numberPlate3 = (splits[index]);
                break;
            }
            break;
        }

        while (--index >= 0) {
            if (numerics[index] == -1) continue;
            numberPlate2 = numerics[index].toString();
            while (--index >= 0) {
                if (numerics[index] != -1) continue;
                numPlateStateCode = splits[index];
                break;
            }
            break;
        }

        String formattedPlate = "";
        if (!numPlateStateCode.trim().isEmpty()) {
            if (!numberPlate2.trim().isEmpty()) {
                formattedPlate = numPlateStateCode + "-" + numberPlate2 + " ";
            } else {
                formattedPlate = numPlateStateCode + " ";
            }
        }
        if (!numberPlate3.trim().isEmpty()) {
            formattedPlate = formattedPlate + numberPlate3 + "-";
        }
        formattedPlate = formattedPlate + numberPlate4;


        return formattedPlate; //numPlateStateCode+"-"+numberPlate2 +'\n'+numberPlate3+"-"+numberPlate4;

    }

    public static String[] splitPlateIntoComponents(String numberPlate) {
        if (numberPlate == null)
            throw new IllegalArgumentException();

        String state = "", district = "", series = "", digits = "";

        numberPlate = numberPlate.replace("[^\\w]", ""); //strip out any spaces that have crept in

        if (numberPlate.length() > 4) {
            String[] splits = numberPlate.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            Integer[] numerics = new Integer[splits.length];
            int index = 0;

            for (String split : splits) {
                try {
                    numerics[index] = Integer.parseInt(split);
                    ++index;
                } catch (NumberFormatException e) {
                    numerics[index] = -1;
                    ++index;
                }
                Log.d(TAG, "init: np split = " + split + ", numeric = " + numerics[index - 1]);
            }

            index = splits.length;


            while (--index >= 0) {
                if (numerics[index] == -1) continue;
                digits = numerics[index].toString();
                while (--index >= 0) {
                    if (numerics[index] != -1) continue;
                    series = splits[index].toUpperCase();
                    break;
                }
                break;
            }

            while (--index >= 0) {
                if (numerics[index] == -1) continue;
                district = numerics[index].toString();
                while (--index >= 0) {
                    if (numerics[index] != -1) continue;
                    state = splits[index].toUpperCase();
                    break;
                }
                break;
            }

        }
        String[] components = {state, district, series, digits};
        return components;
    }
    public static double timeDiffSec(long captureStartTime, long currentTimeMillis) {
        return (currentTimeMillis - captureStartTime) * 0.001;
    }

    public static String getLastnCharacters(String inputString,
                                            int subStringLength) {
        int length = inputString.length();
        if (length <= subStringLength) {
            return inputString;
        }
        int startIndex = length - subStringLength;
        return inputString.substring(startIndex);
    }

    public static String getDigits(String numberPlate) {
        String[] components = numberPlate.split("(?=\\d{1,4}$)", 2);
        if (components != null) {
            if (components.length > 1) {
                return components[1];

            }
        }
        return "";
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static String getFormattedStringForLedpanel(String ledData, int lineNumber, int noOfRow) {
        if (ledData.length() > 10) {
            ledData = ledData.substring(0, 10);
        }
        String formatedStringForLed = "";
        String preFixString = "DMB"; // DMB for display as a Dumb device
        String screenNumber = "0";  // screen number of led panel
        int stringLength = ledData.length(); // length of displayable string
        String xCoOrdinate = String.valueOf((64 - (stringLength * 6)) / 2);
        /*
         thi is to calculate dynamic x-coordinate.
         64 is no of pixel in x axis of led panel,
         5 means one characer needs 5 pixel in x axis,
         2 means no of sides, start and end of led panel(2 sided)
         this formula calculate no of space have to give before the displayable sring
        */
        if (xCoOrdinate.length() == 1) { //xCoOrdinate string length must be 3 accoring to led string format
            xCoOrdinate = "00" + xCoOrdinate;
        } else if (xCoOrdinate.length() == 2) {
            xCoOrdinate = "0" + xCoOrdinate;
        }
        String yCoOrdinate = "";

        //lineNumber is row number
        if (noOfRow == 3) {
            if (lineNumber == 0) {
                yCoOrdinate = "001";
            } else if (lineNumber == 1) {
                yCoOrdinate = "012";
            } else if (lineNumber == 2) {
                yCoOrdinate = "023";
            }
        } else if (noOfRow == 2) {
            if (lineNumber == 0) {
                yCoOrdinate = "007";
            } else if (lineNumber == 1) {
                yCoOrdinate = "020";
            }
        }


        if (String.valueOf(stringLength).length() == 1) {
            formatedStringForLed = preFixString + screenNumber + lineNumber +
                    xCoOrdinate + yCoOrdinate + 0 + stringLength + ledData;
        } else {
            formatedStringForLed = preFixString + screenNumber + lineNumber +
                    xCoOrdinate + yCoOrdinate + stringLength + ledData;
        }
        // final String format is : DMB1000100310GETPARKING
        return formatedStringForLed;
    }


    public static void disableViewGroup(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableViewGroup((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    public static String extractNumberplateDigits(String vehicleNo) {

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
            } else if(theDigits.length() > 4){
                theDigits = theDigits.substring(theDigits.length() - 4);
            }
            return theDigits;
        } else {
            return "0000";
        }

    }





    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static int computeEditDistance(String a, String b) {

        // Handle the trivial cases
        if(StringUtils.isAllBlank(a))
            return b.length();

        if(StringUtils.isAllBlank(b))
            return a.length();

        if(a.equals(b))
            return 0;

        // Since we are ignoring the zeroth element in our matrix operations below, right shifting all chars by prefixing with a space character.
        char[] aChars = (' '+ a).toCharArray();
        char[] bChars = (' '+ b).toCharArray();

        int[][] distMatrix = new int[aChars.length][bChars.length];


        for (int i = 0; i < aChars.length; ++i) {
            distMatrix[i][0] = i;
        }

        for (int j = 0; j < bChars.length; ++j) {
            distMatrix[0][j] = j;
        }


        for (int i = 1; i < aChars.length; ++i)
            for (int j = 1; j < bChars.length; ++j) {
                int delta = aChars[i] == bChars[j] ? 0 : 1;
                distMatrix[i][j] = Math.min(distMatrix[i - 1][j - 1] + delta,
                        Math.min(distMatrix[i - 1][j] + 1, distMatrix[i][j - 1] + 1));
            }


        return distMatrix[aChars.length - 1][bChars.length - 1];
    }


}
