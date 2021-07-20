
import 'dart:typed_data';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:flutter/services.dart';
// import 'package:npgroups/npgroups.dart';
import 'package:society_app/screens/result_screen.dart';
import 'package:camera/camera.dart';
import 'dart:async';
import 'package:society_app/scanner_utils.dart';




dynamic _scanResults;
late CameraController _controller;
bool _isDetecting = false;
CameraLensDirection _direction = CameraLensDirection.back;
String _numberplate = 'unknown';
List<String> mylist = [];

final TextRecognizer _recognizer = FirebaseVision.instance.textRecognizer();
final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();



void _initializeCamera() async {

  final CameraDescription description =
  await ScannerUtils.getCamera(_direction);

  _controller =
      CameraController(description, ResolutionPreset.max, enableAudio: false);
  await _controller.initialize();

  _controller.startImageStream((CameraImage image) {
    if (_isDetecting) {
      return;
    }

    _isDetecting = true;
    ScannerUtils.detect(
      image: image,
      detectInImage: _recognizer.processImage,
      imageRotation: description.sensorOrientation,
    ).then(
          (dynamic results) {
        _scanResults = results;
        if (_scanResults != null) {
          for (TextBlock block in _scanResults.blocks) {
            mylist.add(block.text);
          }
        }
      },
    ).whenComplete(() => _isDetecting = false);
  });
  _numberplate = Regex(mylist);

}


String Regex(List<String> list){
  print(mylist);
  print('MYLIST');
  var numbers = ['0','1','2','3','4','5','6','7','8','9','0'];
  var ans = 'unknown';
  for (String string in list){
    if (string.length < 11 &&	string.length > 4){
      var newString1 = string.substring(string.length - 1);
      var newString2 = string.substring(string.length - 2);
      var newString3 = string.substring(string.length - 3);
      var newString4 = string.substring(string.length - 4);

      if (numbers.contains(newString1)) {
        if(numbers.contains(newString2)) {
          if(numbers.contains(newString3)) {
            if (numbers.contains(newString4)) {
              ans = string;
            }
          }
        }
      }
    }
  }
  return ans;
}



class CameraScreen extends StatefulWidget {


  static String id = 'camera_screen';


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




  // late Npgroups _npgroups;


  static const MethodChannel _channel = const MethodChannel('tflite');

  late int imageHeight;
  late int imageWidth;

  bool? get isPaused => null;


  @override
  void initState() {
    super.initState();
    _initializeCamera();
  }




  // Future<void> initPlatformState() async {
  //   print('START TIME NPGROUPS');
  //
  //   print(DateTime.now());
  //
  //   _npgroups = Npgroups(listenToNumplate);
  //   await _npgroups.startListening();
  //     for (String string in mylist) {
  //       try{
  //         _npgroups.processNumberplate(string);
  //       }
  //       on PlatformException {
  //         _numberplate = 'Error';
  //       }
  //     if (!mounted) return;
  //   }
  //       Navigator.push(context, MaterialPageRoute(builder: (context) => ResultScreen(resultText: _numberplate)));
  //
  //
  //
  //   print(DateTime.now());
  //   print('END TIME NPGROUPS');
  //
  // }

  @override
  void dispose() {
    _controller.dispose();
    _recognizer.close();
    super.dispose();
  }



  listenToNumplate(String numplate) {
    setState(() {
      _numberplate = numplate;
    });
  }

  pinIndexSetup(String text) {
    if(pinIndex == 0)
      pinIndex = 1;
    else if (pinIndex < 4)
      pinIndex++;
    setPin(pinIndex,text);
    currentPin[ pinIndex-1] = text;
    String strPin = '';
    currentPin.forEach((e) {
      strPin += e;
    });
    if(pinIndex == 4)
      print(strPin);

  }

  setPin(int n, String text) {
    switch(n) {
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

  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    var width = MediaQuery.of(context).size.width;
    return Scaffold(
      body: FutureBuilder<void>(
        future: _controller.initialize(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
              return Container(
                width: width,
                height: height,
                child:
                Column(
                  children: [
                    Stack(
                      children: [

                        Container(height: height * 0.65,  width: width,child: CameraPreview(_controller)),


                        Positioned(
                            bottom:  MediaQuery.of(context).size.height * 0.25,
                            right: MediaQuery.of(context).size.width * 0.04,
                            child: FloatingActionButton(onPressed: () {
                              _initializeCamera();
                            })),
                        Container(
                          child: Positioned(
                            bottom: MediaQuery.of(context).size.height * 0.026,
                            left: MediaQuery.of(context).size.width * 0.04,
                            child: Row(
                              children: [
                                VehicleButton(icon: Icons.motorcycle_outlined, func: () {},),
                                SizedBox(width: width * 0.08,),
                                VehicleButton(icon: Icons.directions_car, func: () {},)
                              ],
                            ),
                          ),
                        ),
                        Positioned(
                          top: MediaQuery.of(context).size.height * 0.042,
                          left: MediaQuery.of(context).size.width * 0.03,
                          child: Row(
                            children: [
                              CustomTextBox(textEditingController: oneController,),
                              CustomTextBox(textEditingController: twoController,),
                              CustomTextBox(textEditingController: threeController,),
                              CustomTextBox(textEditingController: fourController ,),
                            ],
                          ),
                        ),
                      ],
                    ),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Container(
                          height: height * 0.35,
                          width: width,
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
                                    buildButton('✓', () {
                                      //       Navigator.push(context, MaterialPageRoute(builder: (context) => ResultScreen(resultText: _numberplate)));

                                      // var code = oneController.text + twoController.text + threeController.text + fourController.text;
                                        // Navigator.push(context, PageTransition(type: PageTransitionType.rightToLeft, child: ResultScreen(resultText: code)));
                                    }),
                                    buildButton('0', () {pinIndexSetup('0');}),
                                    buildButton('⌫', () {
                                      if(pinIndex == 0)
                                        pinIndex = 0;
                                      else if (pinIndex == 4) {
                                        setPin(pinIndex, '');
                                        currentPin[pinIndex-1] = '';
                                        pinIndex--;
                                      } else {
                                        setPin(pinIndex, '');
                                        currentPin[pinIndex-1] = '';
                                        pinIndex--;
                                      }
                                    })
                                  ]
                              ),
                            ],
                          ),
                        )
                      ],
                    ),
                  ],
                ),
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

  final TextEditingController textEditingController;

  CustomTextBox({ required this.textEditingController});


  @override
  Widget build(BuildContext context) {
    var w = MediaQuery.of(context).size.width;
    var h = MediaQuery.of(context).size.height;
    return Container(
      width: w * 0.18,
      height: h * 0.1,
      margin: EdgeInsets.symmetric(horizontal: 7.0),
      child: TextField(
        controller: textEditingController,
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
          fillColor: Colors.white
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
    var h =  MediaQuery.of(context).size.height;
    var w = MediaQuery.of(context).size.width;
    return ConstrainedBox(
      constraints: BoxConstraints.tightFor(width: w * 0.4, height: h * 0.1),
      child: ElevatedButton(
        onPressed: func,
        child: Icon(
        icon,
        color: Colors.white,
        size: w * 0.1,
      ),
        style: ElevatedButton.styleFrom(
          primary: Colors.blue,
        ),
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
      height: MediaQuery.of(context).size.height * 0.0875,
      width: MediaQuery.of(context).size.width * 0.3,
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

