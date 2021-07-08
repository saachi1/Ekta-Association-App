import 'dart:io';

import 'package:flutter/material.dart';
import 'camera_screen.dart';
import 'package:image_picker/image_picker.dart';
import 'package:society_app/widgets/common_button.dart';
import 'vehicles_screen.dart';


class ResultScreen extends StatelessWidget {

  File? image;
  String? resultText;
  ResultScreen(this.image, this.resultText);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          SizedBox(height: 70,),
          Image.file(image!),
          SizedBox(height: 50,),
          Text(resultText!, style: TextStyle(fontSize: 40),),
          SizedBox(height: 50,),

          Text('Is the numberplate correct?', style: TextStyle(fontSize: 25, color: Colors.grey)),
          SizedBox(height: 50,),
          ElevatedButton(
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.fromLTRB(60, 20, 60, 20),
              ),
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) {return VehiclesScreen();}));

              },
              child: Text('Confirm', style: TextStyle(fontSize: 30),)
          )
        ],
      ),
    );
  }
}
