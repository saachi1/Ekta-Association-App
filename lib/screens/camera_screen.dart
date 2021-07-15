import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:flutter/services.dart';
import 'package:npgroups/npgroups.dart';
import 'package:society_app/screens/result_screen.dart';
import 'package:society_app/widgets/common_drawer.dart';
import 'package:camera/camera.dart';
import 'dart:async';
import 'package:numeric_keyboard/numeric_keyboard.dart';


class CameraScreen extends StatefulWidget {

  final CameraDescription camera;

  static String id = 'camera_screen';


  CameraScreen({required this.camera});

  @override
  _CameraScreenState createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {

  List<String> currentPin = ["", "", "", ""];
  TextEditingController oneController = TextEditingController();
  TextEditingController twoController = TextEditingController();
  TextEditingController threeController = TextEditingController();
  TextEditingController fourController = TextEditingController();

  int pinIndex = 0;




  late CameraController _controller;
  late Future<void> _initializeControllerFuture;
  late Npgroups _npgroups;


  static const MethodChannel _channel = const MethodChannel('tflite');

  String? resultText;
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
    FirebaseVisionImage mlImage = FirebaseVisionImage.fromBytes(image.planes[0].bytes, null);
    TextRecognizer recognizeText = FirebaseVision.instance.textRecognizer();
    VisionText readText = await recognizeText.processImage(mlImage);

    for (TextBlock block in readText.blocks) {
      for (TextLine line in block.lines) {
        for (TextElement word in line.elements) {
          resultText = word.text;
          print(resultText);
          // _npgroups.processNumberplate(resultText!);
        }
      }
    }

    if (resultText == null) {
      print('null');
    } else {
      // detectedWordList.add(resultText);
    }
  }


  listenToNumplate(String numplate) {
    //Consume the numplate
  }

  dynamic captureImageStream() {
    _controller.startImageStream((CameraImage availableImage) {
      _controller.stopImageStream();
      getNumberPlate(availableImage);
    });
  }

  pinIndexSetup(String text) {
    if (pinIndex == 0)
      pinIndex = 1;
    else if (pinIndex < 4)
      pinIndex++;
    setPin(pinIndex, text);
    currentPin[pinIndex-1] = text;
    String strPin = '';
    currentPin.forEach((e) {
      strPin += e;
    });
    if(pinIndex == 4)
      print(strPin);
  }

  setPin(int n, String text) {
    switch (n) {
      case 1:
        oneController.text = text;
        break;
      case 2:
        twoController.text = text;
        break;
      case 3:
        threeController.text = text;
        break;
      case 4:
        fourController.text = text;
        break;
    }
  }

  clearPin() {
    if (pinIndex == 0)
      pinIndex = 0;
    else if (pinIndex == 4) {
      setPin(pinIndex, '');
      currentPin[pinIndex - 1] = '';
      pinIndex--;
    } else {
      setPin(pinIndex, '');
    }
  }


  @override
  Widget build(BuildContext context) {
    captureImageStream();
    return Scaffold(
      body: FutureBuilder<void>(
        future: _initializeControllerFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
              return Column(
                children: [
                  Stack(
                    children: [
                      AspectRatio(aspectRatio: _controller.value.aspectRatio - 0.42,
                          child: CameraPreview(_controller)),
                      Positioned(
                        bottom: 10,
                        left: 5,
                        child: Row(
                          children: [
                            VehicleButton(icon: Icons.motorcycle_outlined, func: () { captureImageStream();},),
                            SizedBox(width: 35),
                            VehicleButton(icon: Icons.directions_car, func: () {captureImageStream();},)
                          ],
                        ),
                      ),
                      Positioned(
                        top: 30,
                        left: 30,
                        child: Row(
                          children: [
                            CustomTextBox(tController: oneController,),
                            CustomTextBox(tController: twoController,),
                            CustomTextBox(tController: threeController),
                            CustomTextBox(tController: fourController,),
                          ],
                        ),
                      )

                    ],
                  ),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Container(
                        width: MediaQuery.of(context).size.width * .99,
                        child: Table(
                          children: [
                            TableRow(
                              children: [
                                buildButton('1', () {pinIndexSetup('1');}),
                                buildButton('2', () {pinIndexSetup('2');}),
                                buildButton('3', () {pinIndexSetup('3');})
                              ]
                            ),
                            TableRow(
                                children: [
                                  buildButton('4', () {pinIndexSetup('4');}),
                                  buildButton('5', () {pinIndexSetup('5');}),
                                  buildButton('6', () {pinIndexSetup('6');})
                                ]
                            ),
                            TableRow(
                                children: [
                                  buildButton('7', () {pinIndexSetup('7');}),
                                  buildButton('8', () {pinIndexSetup('8');}),
                                  buildButton('9', () {pinIndexSetup('9');})
                                ]
                            ),
                            TableRow(
                                children: [
                                  buildButton('✓', () {}),
                                  buildButton('0', () {pinIndexSetup('0');}),
                                  buildButton('⌫', () { clearPin();})
                                ]
                            ),
                          ],
                        ),
                      )
                    ],
                  )

                ],
              );

          } else {
            // Otherwise, display a loading indicator.
            return const Center(child: CircularProgressIndicator());
          }
        },
      ),
    );
  }

  }

class CustomTextBox extends StatelessWidget {

  final TextEditingController tController;

  CustomTextBox({required this.tController});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 7.0),
      width: 75,
      child: TextField(
        controller: tController,
        style: TextStyle(
          fontWeight: FontWeight.bold,
          fontSize: 21,
          color: Colors.black
        ),
        textAlign: TextAlign.center,
        readOnly: true,
        decoration: InputDecoration(
          contentPadding: EdgeInsets.all(16),
          filled: true,
          fillColor: Colors.grey
        ),
      ),
    );
  }
}

class VehicleButton extends StatelessWidget {

  final IconData icon;
  final func;


  VehicleButton({required this.icon, this.func});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: func,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 17, horizontal: 50),
        child: Icon(
        icon,
        color: Colors.white,
        size: 45,
    ),
      ),
      style: ElevatedButton.styleFrom(
        primary: Colors.blue, // This is what you need!
      ),
    );
  }
}

class buildButton extends StatelessWidget {

  final String buttonText;
  final VoidCallback function;

  buildButton(this.buttonText, this.function);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: MediaQuery.of(context).size.height * 0.1 * 0.85,
      color: Colors.blueAccent,
      child: FlatButton(
          onPressed: function,
          child: Text(
            buttonText,
            style: TextStyle(
                fontSize: 30.0,
                fontWeight: FontWeight.normal,
                color: Colors.white
            ),
          )
      ),
    );
  }
}

