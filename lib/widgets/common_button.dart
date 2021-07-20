import 'package:flutter/material.dart';
import 'package:society_app/screens/camera_screen.dart';

class CommonButton extends StatelessWidget {

  final Color colour;
  final String buttonText;
  final Function? onTap;


  CommonButton({required this.buttonText, required this.colour, this.onTap});

  @override
  Widget build(BuildContext context) {
    var h = MediaQuery.of(context).size.height;
    var w = MediaQuery.of(context).size.width;
    return ElevatedButton(
        style: ElevatedButton.styleFrom(
          primary: colour,
          padding: EdgeInsets.symmetric(vertical: h * 0.028 , horizontal: w *  0.16)
            // fromLTRB(70, 20, 70, 20),
        ),
        child: Text(buttonText, style: TextStyle(color: Colors.white),),
        onPressed: () {
          onTap != null? onTap!() :  Navigator.pushNamed(context, CameraScreen.id);
        }
    );
  }
}

