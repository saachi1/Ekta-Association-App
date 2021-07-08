import 'dart:io';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:image_picker/image_picker.dart';

class CameraScreen extends StatefulWidget {

  static String id = 'camera_screen';

  @override
  _CameraScreenState createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {

   File? _image;

  final imagePicker = ImagePicker();


  Future getImage() async {
    final image = await imagePicker.getImage(source: ImageSource.camera);
    setState(() {
      _image = File(image!.path);
    });
  }

  void getNumberPlate() async {

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
        onPressed: () {  },
        backgroundColor: Colors.blue,
        child: Icon(Icons.check),
      )
    );
  }
}
