import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:npgroups/npgroups.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _numberplate = 'Unknown';
  Npgroups? _npgroups;
  @override
  void initState() {

    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
   _npgroups = Npgroups(listenToNumplate);
   await _npgroups?.startListening();
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {

      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ575");
      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ4775");
      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ5775");
      _npgroups?.processNumberplate("MH02AQ5775");

    } on PlatformException {
      _numberplate = 'Error';
    }

    // If the widget was removed from the tfree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;


  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Numberplate Plugin Sample App'),
        ),
        body: Center(
          child: Text('Numberplate: $_numberplate\n'),
        ),
      ),
    );
  }


  listenToNumplate(String numplate){
   setState(() {
     _numberplate = numplate;
   });
  }
}
