import 'package:flutter/material.dart';
import 'package:society_app/screens/splash_screen.dart';
import 'package:society_app/screens/vehicles_screen.dart';
import 'package:society_app/screens/camera_screen.dart';
import 'package:society_app/screens/login_screen.dart';
import 'package:society_app/screens/violation_screen.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:camera/camera.dart';


Future <void> main() async  {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  final cameras = await availableCameras();
  final firstCamera = cameras.first;

  runApp(MyApp(passedCamera:firstCamera));
}

class MyApp extends StatelessWidget {

  final CameraDescription? passedCamera;

  const MyApp({this.passedCamera});


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SplashScreen(),
      routes: {
        LoginScreen.id: (context) => LoginScreen(),
        ViolationScreen.id: (context) => ViolationScreen(),
        VehiclesScreen.id: (context) => VehiclesScreen(),
        CameraScreen.id: (context) => CameraScreen(camera:passedCamera!),
      },
    );
  }
}








