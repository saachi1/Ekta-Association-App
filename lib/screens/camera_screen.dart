
import 'package:flutter/material.dart';

class CameraScreen extends StatelessWidget {

  static String id = 'camera_screen';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Camera Screen'),
      ),
      body: Center(
        child: Text('Camera Screen'),
      )
    );
  }
}
