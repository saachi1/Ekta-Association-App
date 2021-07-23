import 'dart:ffi';
import 'dart:io';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import 'camera_screen.dart';
import 'package:society_app/widgets/common_button.dart';
import 'vehicles_screen.dart';


class ResultScreen extends StatelessWidget {

  // File image;
  String resultText;
  // List<String> list;
  // Function? newPhoto;
  // ResultScreen(this.image);

  ResultScreen({required this.resultText});

  @override
  Widget build(BuildContext context) {
    var width = MediaQuery.of(context).size.width;
    var height = MediaQuery.of(context).size.width;
    return Scaffold(
      body: Container(

        child: Column(
          children: [

            SizedBox(height: height * 0.15,),
            // Image.file(image),
            SizedBox(height: height * 0.107,),
            Text(resultText, style: TextStyle(fontSize: 40),),
            SizedBox(height: height * 0.107,),

            Text('Is the numberplate correct?', style: TextStyle(fontSize: width * 0.06, color: Colors.grey)),
            SizedBox(height: height * 0.12,),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                CommonButton(buttonText: 'No', colour: Colors.red, onTap: () {Navigator.pop(context);},),
                SizedBox(width: width * 0.1,),
                CommonButton(buttonText: 'Yes', colour: Colors.green, onTap: () {Navigator.pushNamed(context, VehiclesScreen.id);},)
              ],
            ),
          ],
        ),
      ),
    );
  }
}