import 'package:flutter/material.dart';
import 'package:society_app/screens/camera_screen.dart';
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
          ],
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            Navigator.push(context, MaterialPageRoute(builder: (context) {return CameraScreen();}));
          },
          child: Icon(Icons.report_problem,),
          backgroundColor: Colors.red,
        ),
      ),
    );
  }
}

