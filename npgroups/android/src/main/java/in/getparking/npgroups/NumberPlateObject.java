package in.getparking.npgroups;
import org.jetbrains.annotations.NotNull;


public class NumberPlateObject {
    public final String numberPlate;

//    private final Bitmap image;
//
//    public final long frameId;
//
//    public final Bitmap fullImage;
//
//    public final long startTime;
//    public final long frameNo;
//    public final String vehType;

    public NumberPlateObject(String numberPlate) {
        this.numberPlate = numberPlate;
//        this.image = image;
//        this.frameId = frameId;
//
//        this.fullImage = fullImage;
//        this.startTime = timeStampLocal;
//        this.frameNo = frameNo;
//        this.vehType = vehType;
    }

    public String getNumberPlate() {
        return numberPlate;
    }
//
//    public Bitmap getImage() {
//        return image;
//    }
//
//    public long getFrameId() {
//        return frameId;
//    }

    @NotNull
    @Override
    public String toString() {
        return numberPlate;
    }
}
