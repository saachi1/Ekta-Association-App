import 'package:society_app/screens/login_screen.dart';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';

class CommonDrawer extends StatelessWidget {

  final _auth = FirebaseAuth.instance;


  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: Column(
        children: [
          Container(
            width: 100,
            height: 100,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              image: DecorationImage(
                image:  AssetImage('assets/images/logo.png'),
                fit: BoxFit.fill
              ),
            ),
          ),
          ListTile(
            leading: Icon(Icons.logout),
            title: Text('Logout'),
            onTap: () async {
    await _auth.signOut();
    Navigator.push(context, MaterialPageRoute(builder: (context) { return LoginScreen();}));
    },
          )
        ],
      ),
    );
  }
}
