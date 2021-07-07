import 'package:flutter/material.dart';
import 'package:society_app/screens/vehicles_screen.dart';

class ViolationScreen extends StatefulWidget {

  static String id = 'violation_screen';

  @override
  _ViolationScreenState createState() => _ViolationScreenState();
}

class _ViolationScreenState extends State<ViolationScreen> {


  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onHorizontalDragStart: (DragStartDetails details) {
        Navigator.push(context, MaterialPageRoute(builder: (context) {return VehiclesScreen();}));
      },
      child: Scaffold(
        appBar: AppBar(
          title: Text('Violation Screen'),
        ),
        body: Center(
          child: Text('Violation Screen'),
        ),
      ),
    );
  }
}
