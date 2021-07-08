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
        Container(
        padding: EdgeInsets.all(20),
        color: Colors.blueAccent,
        child: Row(
          children: [
            SizedBox(width: 30,),

            Icon(Icons.motorcycle_rounded, color: Colors.white,size: 40,),
            SizedBox(width: 30,),

            Text('3', style: TextStyle(color: Colors.white, fontSize: 25),),
            SizedBox(width: 100,),

            Icon(Icons.directions_car, color: Colors.white, size: 40,),
            SizedBox(width: 30,),

            Text('5', style: TextStyle(color: Colors.white, fontSize: 25)),
          ],
        ),
      ),
            DataTable(
                columnSpacing: 50,
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

                ]
            ),
            SizedBox(
              height: 80,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                CommonButton(buttonText: 'IN', colour: Colors.green,),
                SizedBox(width: 50,),
                CommonButton(buttonText: 'OUT', colour: Colors.red,),
              ],
            )

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
          Navigator.push(context, MaterialPageRoute(builder: (context) {return CameraScreen();}));
        }
    );
  }
}

