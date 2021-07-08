import 'dart:io';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:image_picker/image_picker.dart';
import 'package:firebase_ml_vision/firebase_ml_vision.dart';
import 'package:society_app/screens/result_screen.dart';

class CameraScreen extends StatefulWidget {

  static String id = 'camera_screen';

  @override
  _CameraScreenState createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {

   static File? _image;
   String? resultText;

  final imagePicker = ImagePicker();


  Future getImage() async {
    final image = await imagePicker.getImage(source: ImageSource.camera);
    setState(() {
      _image = File(image!.path);
    });
  }

  Future getNumberPlate() async {
    FirebaseVisionImage mlImage = FirebaseVisionImage.fromFile(_image);
    TextRecognizer recognizeText = FirebaseVision.instance.textRecognizer();
    VisionText readText = await recognizeText.processImage(mlImage);

    for(TextBlock block in readText.blocks){
      for(TextLine line in block.lines) {
        for(TextElement word in line.elements) {
          resultText = word.text;
        }
      }
    }

    if(resultText == null){
      print('null');
    } else {
      Navigator.push(context, MaterialPageRoute(builder: (context) {return ResultScreen(_image, resultText);}));

    }

  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
          child: _image == null ? Text('No Image') : Image.file(_image!)
      ),
      floatingActionButton: _image == null ? FloatingActionButton(
        backgroundColor: Colors.blue,
        onPressed: () {
          getImage();

        },
        child: Icon(Icons.camera_alt),
      ) 
      : FloatingActionButton(
        onPressed: () {
          getNumberPlate();
        },
        backgroundColor: Colors.blue,
        child: Icon(Icons.check),
      )
    );
  }
}
