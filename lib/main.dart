import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:society_app/screens/splash_screen.dart';
import 'package:society_app/screens/vehicles_screen.dart';
import 'package:society_app/screens/camera_screen.dart';
import 'package:society_app/screens/login_screen.dart';
import 'package:society_app/screens/violation_screen.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:society_app/screens/vehicles_screen.dart';


void main() async  {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SplashScreen(),
      routes: {
        LoginScreen.id: (context) => LoginScreen(),
        ViolationScreen.id: (context) => ViolationScreen(),
        VehiclesScreen.id: (context) => VehiclesScreen(),
        CameraScreen.id: (context) => CameraScreen(),
      },
    );
  }
}








