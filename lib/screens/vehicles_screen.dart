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

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onHorizontalDragEnd: (DragEndDetails details) {
        Navigator.push(context, MaterialPageRoute(builder: (context) {return ViolationScreen();}));
      },
      child: Scaffold(
        appBar: AppBar(
          leading: IconButton(icon: IconButton(onPressed: () {}, icon: Icon(Icons.menu)), onPressed: () {  },),
          title: Text('Vehicles'),
          actions: [
            IconButton(onPressed: () {}, icon: Icon(Icons.search)),
          ],
        ),
        body: Column(
          children: [
            DataTable(
              columnSpacing: 80,
              dataRowHeight: 50,
                columns: [
                  DataColumn(label: Text('TYPE')),
                  DataColumn(label: Text('VEH NO.')),
                  DataColumn(label: Text('EXPIRES')),
                  DataColumn(label: Text('NAME')),
                ],
                rows: [
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                  DataRow(cells: [
                    DataCell(Icon(Icons.directions_car)),
                    DataCell(Text('5557')),
                    DataCell(Text('31-Dec-18')),
                    DataCell(Text('Qwerty')),
                  ]),
                ]
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.all(20)
              ),
              child: Text('Register Car'),
                onPressed: () {
                  Navigator.push(context, MaterialPageRoute(builder: (context) {return CameraScreen();}));
                }
                ),
          ],
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.logout),
          onPressed: () async {
            await _auth.signOut();
            Navigator.push(context, MaterialPageRoute(builder: (context) { return LoginScreen();}));
          },
        ),
      ),
    );
  }
}
