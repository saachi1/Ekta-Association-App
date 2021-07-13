import 'package:flutter/material.dart';
import 'package:society_app/screens/camera_screen.dart';

class CommonButton extends StatelessWidget {

  final Color colour;
  final String buttonText;


  CommonButton({required this.buttonText, required this.colour});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
        style: ElevatedButton.styleFrom(
          primary: colour,
          padding: EdgeInsets.fromLTRB(70, 20, 70, 20),
        ),
        child: Text(buttonText, style: TextStyle(color: Colors.white),),
        onPressed: () {
          Navigator.pushNamed(context, CameraScreen.id);
        }
    );
  }
}

