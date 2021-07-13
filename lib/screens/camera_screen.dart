import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:flutter/services.dart';
import 'package:npgroups/npgroups.dart';
import 'package:society_app/widgets/common_drawer.dart';
import 'package:camera/camera.dart';
import 'dart:async';



class CameraScreen extends StatefulWidget {

  final CameraDescription camera;

  static String id = 'camera_screen';


  CameraScreen({required this.camera});

  @override
  _CameraScreenState createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {

  late CameraController _controller;
  late Future<void> _initializeControllerFuture;
  late Npgroups _npgroups;
  List imagePathList = [];

  List<String?> detectedWordList = [];

  static const MethodChannel _channel = const MethodChannel('tflite');

  String? resultText;
  late CameraImage photo;
  late int imageHeight;
  late int imageWidth;


  bool? get isPaused => null;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    _controller = CameraController(
      // Get a specific camera from the list of available cameras.
      widget.camera,
      // Define the resolution to use.
      ResolutionPreset.medium,
    );

    _initializeControllerFuture = _controller.initialize();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    _npgroups = Npgroups(listenToNumplate);
    await _npgroups.startListening();
  }

  @override
  void dispose() {
    // Dispose of the controller when the widget is disposed.
    _controller.dispose();
    super.dispose();
  }

  Future getNumberPlate(image) async {
    FirebaseVisionImage mlImage = FirebaseVisionImage.fromFile(image);
    TextRecognizer recognizeText = FirebaseVision.instance.textRecognizer();
    VisionText readText = await recognizeText.processImage(mlImage);

    for (TextBlock block in readText.blocks) {
      for (TextLine line in block.lines) {
        for (TextElement word in line.elements) {
          resultText = word.text;
          _npgroups.processNumberplate(resultText!);
        }
      }
    }

    if (resultText == null) {
      print('null');
    } else {
      detectedWordList.add(resultText);
    }
  }


  listenToNumplate(String numplate) {
    //Consume the numplate
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: CommonDrawer(),
      appBar: AppBar(
        title: Text(
            'Camera'
        ),
      ),
      body: FutureBuilder<void>(
        future: _initializeControllerFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
            // If the Future is complete, display the preview.
            return CameraPreview(_controller);
          } else {
            // Otherwise, display a loading indicator.
            return const Center(child: CircularProgressIndicator());
          }
        },
      ),
      floatingActionButton: FloatingActionButton(
        // Provide an onPressed callback.
        onPressed: () async {
          // Take the Picture in a try / catch block. If anything goes wrong,
          // catch the error.
          try {
            // Ensure that the camera is initialized.
            await _initializeControllerFuture;

            _controller.startImageStream((CameraImage img) {
              _controller.stopImageStream();
              if (!isPaused!) {
                runPoseNetOnFrame(bytesList: img.planes.map((plane) {
                  return plane.bytes;
                }).toList(),
                    imageHeight: img.height,
                    imageWidth: img.width,
                    numResults: 1).then((recognitions) {
                  setState(() {
                    photo = img;
                    imageHeight = img.height;
                    imageWidth = img.width;
                  });
                });
              }
            });
          } catch (e) {
            // If an error occurs, log the error to the console.
            print(e);
          }
        },
        child: const Icon(Icons.camera_alt),
      ),
    );
  }

  Future<List> runPoseNetOnFrame({required List<Uint8List> bytesList,
    int imageHeight = 1280,
    int imageWidth = 720,
    double imageMean = 127.5,
    double imageStd = 127.5,
    int rotation: 90, // Android only
    int numResults = 1,
    double threshold = 0.5,
    int nmsRadius = 20,
    bool asynch = true}) async {
    return await _channel.invokeMethod(
      'runPoseNetOnFrame',
      {
        "bytesList": bytesList,
        "imageHeight": imageHeight,
        "imageWidth": imageWidth,
        "imageMean": imageMean,
        "imageStd": imageStd,
        "rotation": rotation,
        "numResults": numResults,
        "threshold": threshold,
        "nmsRadius": nmsRadius,
        "asynch": asynch,
      },
    );
  }
}
