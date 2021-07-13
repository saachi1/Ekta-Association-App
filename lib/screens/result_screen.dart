import 'dart:ffi';
import 'dart:io';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'camera_screen.dart';
import 'package:society_app/widgets/common_button.dart';
import 'vehicles_screen.dart';
import 'package:society_app/widgets/common_button.dart';


class ResultScreen extends StatelessWidget {

  // File? image;
  // String? resultText;
  // Function? newPhoto;
  // ResultScreen(this.image, this.resultText, this.newPhoto);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          SizedBox(height: 70,),
          // Image.file(image!),
          SizedBox(height: 50,),
          // Text(resultText!, style: TextStyle(fontSize: 40),),
          SizedBox(height: 50,),

          Text('Is the numberplate correct?', style: TextStyle(fontSize: 25, color: Colors.grey)),
          SizedBox(height: 50,),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Colors.red,
                    padding: EdgeInsets.fromLTRB(70, 20, 70, 20),
                  ),
                  child: Text('No', style: TextStyle(color: Colors.white),),
                  onPressed: () {
                    // newPhoto!();
                    Navigator.pushNamed(context, CameraScreen.id);

                  }
              ),
              SizedBox(width: 20,),
              ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Colors.green,
                    padding: EdgeInsets.fromLTRB(70, 20, 70, 20),
                  ),
                  child: Text('Yes', style: TextStyle(color: Colors.white),),
                  onPressed: () {
                  }
              )

            ],
          ),


        ],
      ),
    );
  }
}
// ElevatedButton(
// style: ElevatedButton.styleFrom(
// padding: EdgeInsets.fromLTRB(60, 20, 60, 20),
// ),
// onPressed: () {
// Navigator.push(context, MaterialPageRoute(builder: (context) {return VehiclesScreen();}));
//
// },
// child: Text('Confirm', style: TextStyle(fontSize: 30),)
// )