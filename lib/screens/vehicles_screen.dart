import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:society_app/widgets/common_drawer.dart';
import 'login_screen.dart';
import 'camera_screen.dart';
import 'violation_screen.dart';
import 'package:society_app/widgets/common_button.dart';
import 'package:page_transition/page_transition.dart';


class VehiclesScreen extends StatefulWidget {


  static String id = 'vehicles_screen';

  @override
  _VehiclesScreenState createState() => _VehiclesScreenState();
}

class _VehiclesScreenState extends State<VehiclesScreen> {


  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    var width = MediaQuery.of(context).size.width;

    return GestureDetector(
      onHorizontalDragEnd: (DragEndDetails details) {
        Navigator.push(context, PageTransition(type: PageTransitionType.rightToLeft, child: ViolationScreen()));
      },
      child: Scaffold(
        appBar: AppBar(
          title: Text('Vehicles'),
          actions: [
            IconButton(onPressed: () {}, icon: Icon(Icons.search)),
          ],
        ),
        body: Column(
          children: [
        Container(
          height: height * 0.12,
          width: width,
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
            Container(
              height: height * 0.35,
              width: width,
              child: DataTable(
                columnSpacing: width * 0.01,
                  dataRowHeight: height * 0.083,
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
            ),
            SizedBox(
              height: 80,
            ),
            Expanded(
              child: Container(
                width: width,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    CommonButton(buttonText: 'IN', colour: Colors.green,),
                    SizedBox(width: 40,),
                    CommonButton(buttonText: 'OUT', colour: Colors.red,),
                  ],
                ),
              ),
            )

          ],
        ),
        drawer: CommonDrawer(),
      ),
    );
  }
}


