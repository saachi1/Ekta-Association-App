package in.getparking.npgroups;

import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** NpgroupsPlugin */
public class NpgroupsPlugin implements FlutterPlugin, MethodCallHandler,NumberplateExtracter {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private ReversePlateMatchImproved numberPlateMatcher;
  public static final int MIN_ACCEPTABLE_NUMPLATE_LENGTH = 8;
  private  Pattern statePattern;
  NaiveBoxing naiveBoxing;
  private static final String TAG = NpgroupsPlugin.class.getSimpleName();
  private final String state = "^((?:(?:AP)|(?:AR)|(?:AS)|(?:BR)|(?:CG)|(?:GA)|(?:GJ)|(?:HR)|(?:HP)|(?:JK)|(?:JH)|(?:KA)|(?:KL)" +

          "|(?:MP)|(?:MH)|(?:MN)|(?:ML)|(?:MZ)|(?:NL)|(?:OD)|(?:OR)|(?:PB)|(?:RJ)|(?:SK)|(?:TN)|(?:TS)|(?:TR)" +

          "|(?:UA)|(?:UK)|(?:UP)|(?:WB)|(?:AN)|(?:CH)|(?:DN)|(?:DD)|(?:DL)|(?:LD)|(?:PY)))(?!(?![oOl])[a-zA-Z])";
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    numberPlateMatcher = new ReversePlateMatchImproved();
    statePattern = Pattern.compile(state);
    naiveBoxing = new NaiveBoxing(this);
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "npgroups");
    channel.setMethodCallHandler(this);

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if(call.method.equals("processNumberplate")){
      String detectedText = call.argument("numberPlate");

      //Replace all special characters and IND before even sending to regex
      assert detectedText != null;
      String rawDetectionString = detectedText.trim().replaceAll("[^\\w]+", "-")
              .toUpperCase()
              .replace("IND", "");


      //Drop all strings less than six chars

      if (rawDetectionString.length() < 7) {
//            Log.d(TAG, "onSuccess: Detected string was below length threshold"+rawDetectionString);
        return;
      }


      List<String> numberPlates = numberPlateMatcher.validNumberPlate(rawDetectionString);
      //   Log.d(Constants.TPROF, "REGEX since capture " + Utils.timeDiffSec(timeStampLocal, System.currentTimeMillis()) + " FRAME NO - " + frameNo);
      if (numberPlates != null && !numberPlates.isEmpty() && !StringUtils.isAllBlank(numberPlates.get(0))
              && !numberPlates.get(0).equalsIgnoreCase("invalid") && numberPlates.get(0).length() >= MIN_ACCEPTABLE_NUMPLATE_LENGTH) {
        //TODO move this to regex code
        //To ensure we are sending plates with state codes only
        Matcher m = statePattern.matcher(numberPlates.get(0));
        if(m.find()) {
         // Log.d(TAG, "onSuccess: sending post regex plate " + numberPlates.get(0) + ", from raw string(" + rawDetectionString.replaceAll("[\n\r]", "") + ") to boxing algo");
          naiveBoxing.group(new NumberPlateObject(numberPlates.get(0)));
//            NumberplateBoxingAndVoting.get().addNumberplateToQueue(new NumberPlateObject(numberPlates.get(0),
//                    bitmapImage, frameId, fullImage, timeStampLocal, frameNo));
        }

      }
   //   System.out.println("MethodChannel Se Mila "+detectedText);
    }

    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onNumberplateExtraction(String fullnumberplate) {

    channel.invokeMethod("onNumberplateExtraction",fullnumberplate);
  }
}
