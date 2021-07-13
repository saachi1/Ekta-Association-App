
import 'package:camera/camera.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:society_app/screens/camera_screen.dart';
import 'vehicles_screen.dart';

enum MobileVerificationState {
  SHOW_MOBILE_FORM_STATE,
  SHOW_OTP_FORM_STATE,
}

class LoginScreen extends StatefulWidget {



  static String id = 'login_screen';

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {

  MobileVerificationState currentState = MobileVerificationState.SHOW_MOBILE_FORM_STATE;
  final phoneController = TextEditingController();
  final otpController = TextEditingController();

  FirebaseAuth _auth = FirebaseAuth.instance;

  late String verificationId;
  late bool showLoading = false;

  void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) async {

    setState(() {
      showLoading = true;
    });

    try {
      final authCredential = await _auth.signInWithCredential(phoneAuthCredential);

      setState(() {
        showLoading = false;
      });

      if (authCredential.user != null) {
        Navigator.push(context, MaterialPageRoute(builder: (context) => VehiclesScreen()));
      }
    } on FirebaseAuthException catch (e) {
      setState(() {
        showLoading = false;
      });

      _scaffoldKey.currentState!.showSnackBar(SnackBar(content: Text(e.message != null? '' : '')));
    }

  }

  getMobileFormWidget(context){
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        TextFormField(

          controller: phoneController,
          decoration: InputDecoration(
            hintText: 'Enter Phone Number',
          ),
        ),
        SizedBox(
          height: 16,
        ),
        ElevatedButton(onPressed: () {Navigator.push(context, MaterialPageRoute(builder: (context) {return VehiclesScreen();}));}, child: Text('Click Me')),
        FlatButton(
          onPressed: () async {

            setState(() {
              showLoading = true;
            });

            await _auth.verifyPhoneNumber(
                phoneNumber:  '+91' + phoneController.text ,
                verificationCompleted: (phoneAuthCredential) async {
                  setState(() {
                    showLoading = false;
                  });
                  // signInWithPhoneAuthCredential(phoneAuthCredential);

                },
                verificationFailed: (verificationFailed) async {
                  setState(() {
                    showLoading = false;
                  });
                  _scaffoldKey.currentState!.showSnackBar(SnackBar(content: Text(verificationFailed.message != null ? '': '')));

                },
                codeSent: (verificationId, resendingToken) async {
                  setState(() {
                    showLoading = false;
                    currentState = MobileVerificationState.SHOW_OTP_FORM_STATE;
                    this.verificationId = verificationId;

                  });
                },
                codeAutoRetrievalTimeout: (verificationId) async {

                }
            );
          },
          child: Text('SEND'),
          color: Colors.blue,
          textColor: Colors.white,
        )
      ],
    );
  }

  getOtpFormWidget(context){
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        TextField(
          controller: otpController,
          decoration: InputDecoration(
            hintText: 'Enter OTP',
          ),
        ),
        SizedBox(
          height: 16,
        ),
        FlatButton(
          onPressed: () async {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.credential(verificationId: verificationId, smsCode: otpController.text);

            signInWithPhoneAuthCredential(phoneAuthCredential);
          },
          child: Text('VERIFY'),
          color: Colors.blue,
          textColor: Colors.white,
        )
      ],
    );
  }

  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        key: _scaffoldKey,
        body: Container(
          padding: EdgeInsets.all(16),
          child: showLoading ? Center(child: CircularProgressIndicator(),) : currentState == MobileVerificationState.SHOW_MOBILE_FORM_STATE ?
          getMobileFormWidget(context):
          getOtpFormWidget(context),
        )
    );
  }
}

