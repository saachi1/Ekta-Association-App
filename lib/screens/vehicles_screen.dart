import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'login_screen.dart';
import 'camera_screen.dart';
import 'violation_screen.dart';

class VehiclesScreen extends StatefulWidget {

  static String id = 'vehicles_screen';

  @override
  _VehiclesScreenState createState() => _VehiclesScreenState();
}

class _VehiclesScreenState extends State<VehiclesScreen> {
  
  final _auth = FirebaseAuth.instance;
  final columns = ['TYPE', 'VEH NO.', 'EXPIRES', 'NAME'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(icon: IconButton(onPressed: () {}, icon: Icon(Icons.menu)), onPressed: () {  },),
        title: Text('Vehicles'),
        actions: [
          IconButton(onPressed: () {}, icon: Icon(Icons.search)),
          IconButton(onPressed: () {Navigator.push(context, MaterialPageRoute(builder: (context) { return CameraScreen();}));}, icon: Icon(Icons.camera_alt)),
          IconButton(onPressed: () {Navigator.push(context, MaterialPageRoute(builder: (context) { return ViolationScreen();}));}, icon: Icon(Icons.error_outline))
        ],
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [

        ],
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.logout),
        onPressed: () async {
          await _auth.signOut();
          Navigator.push(context, MaterialPageRoute(builder: (context) { return LoginScreen();}));
        },
      ),
    );
  }
}
