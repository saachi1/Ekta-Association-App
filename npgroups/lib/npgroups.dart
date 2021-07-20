import 'dart:async';

import 'package:flutter/services.dart';

class Npgroups {
  MethodChannel _channel = const MethodChannel('npgroups');
  Function(String numplate) listenToNumplate;
  Npgroups(this.listenToNumplate);

  processNumberplate(String numberPlate) {
    _channel.invokeMethod('processNumberplate', {"numberPlate": numberPlate});
  }

  Future<void> _methodCallHandler(MethodCall call) async {
    if (call.method == "onNumberplateExtraction") {
      print("RECEIVED THIS ON CALLBACK --> " + call.arguments);
      listenToNumplate(call.arguments);
    }
  }

  Future startListening() async {
    _channel.setMethodCallHandler(_methodCallHandler);
  }
}
